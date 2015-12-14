package org.cytoscape.ci.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.aspects.readers.CyVisualPropertiesFragmentReader;
import org.cxio.aspects.readers.EdgesFragmentReader;
import org.cxio.aspects.readers.NodesFragmentReader;
import org.cxio.aspects.writers.CartesianLayoutFragmentWriter;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectFragmentReader;
import org.cxio.core.interfaces.AspectFragmentWriter;
import org.cxio.metadata.MetaDataCollection;
import org.cytoscape.ci.service.layouts.AbstractLayout;
import org.cytoscape.ci.service.layouts.GridLayout;
import org.cytoscape.ci.service.layouts.LayoutAlgorithm;
import org.cytoscape.ci.service.layouts.StackedNodeLayout;

/**
 * LayoutService
 * 
 * @author: Braxton Fitts
 * @author: Ziran Zhang
 *
 * This class provides the service of creating a CX stream that
 * contains the cartesianLayout aspect for a network defined using CX
 * format after applying a requested graph layout algorithm. Currently
 * available layout algorithms are: 
 * 1. Grid Layout
 * 2. Stacked Node Layout
 */
public class LayoutService {

	private static final String GRID_LAYOUT = "grid";
	private static final String STACKED_LAYOUT = "stacked";

	/* Calls the apply method of the passed-in LayoutAlgorithm object */
	private static boolean applyLayout(LayoutAlgorithm layout,
			MetaDataCollection postLayoutMetadata, CxReader cxNetworkReader,
			CxWriter cxLayoutWriter) throws IOException {
		if (layout instanceof AbstractLayout) {
			return ((AbstractLayout) layout).apply(postLayoutMetadata);
		} else
			return layout.apply(postLayoutMetadata, cxNetworkReader,
					cxLayoutWriter);
	}

	/*
	 * Perform the service. This method starts and finishes the CX stream that
	 * will represent the cartesianLayout for the passed-in network
	 */
	public static OutputStream run(String cxInput, String algorithm) {

		ByteArrayOutputStream cartesianLayout = new ByteArrayOutputStream();

		// Create necessary AspectFragmentReaders and AspectFragmentWriter
		NodesFragmentReader nodesFragmentReader = NodesFragmentReader
				.createInstance();
		EdgesFragmentReader edgesFragmentReader = EdgesFragmentReader
				.createInstance();
		CartesianLayoutFragmentWriter cartesianFragmentWriter = CartesianLayoutFragmentWriter
				.createInstance();

		// Prepare AspectFragmentWriter for bundling into CxReader object
		HashSet<AspectFragmentWriter> writer = new HashSet<AspectFragmentWriter>(
				1);
		writer.add(cartesianFragmentWriter);

		// Prepare AspectFragmentReaders for bundling into CxReader object
		HashSet<AspectFragmentReader> readers = new HashSet<AspectFragmentReader>(
				2);
		readers.add(nodesFragmentReader);
		readers.add(edgesFragmentReader);

		try {
			CxReader cxReader;
			CxWriter cxWriter = CxWriter.createInstance(cartesianLayout, false,
					writer);

			// Create the premetadata
			MetaDataCollection preLayoutMetadata = new MetaDataCollection();
			preLayoutMetadata.setVersion(CartesianLayoutElement.ASPECT_NAME,
					"1.0");
			preLayoutMetadata.setConsistencyGroup(
					CartesianLayoutElement.ASPECT_NAME, (long) 1);
			cxWriter.addPreMetaData(preLayoutMetadata);

			// Create the postmetadata that will be populated by the
			// LayoutAlgorithm
			MetaDataCollection postLayoutMetadata = new MetaDataCollection();

			// System.out.println("Applying layout...");
			if (algorithm == null) {
				algorithm = "null";
			}
			switch (algorithm) {
			case GRID_LAYOUT: {
				// System.out.println("Grid layout");
				cxReader = CxReader.createInstance(cxInput, readers);
				cxWriter.start();
				try {
					boolean failedDueToInput = applyLayout(new GridLayout(
							cxReader, cxWriter), postLayoutMetadata, cxReader,
							cxWriter);
					if (failedDueToInput) {
						cxWriter.end(false,
								"Input CX did not contain necessary aspects");
					} else {
						cxWriter.end(true, "Applied GridLayout to network");
					}
				} catch (IOException e) {
					cxWriter.end(false,
							"Error when writing to file. " + e.getMessage());
				}
				break;
			}
			case STACKED_LAYOUT: {
				// System.out.println("Stacked Node layout");
				CyVisualPropertiesFragmentReader vizPropFragmentReader = CyVisualPropertiesFragmentReader
						.createInstance();
				readers.add(vizPropFragmentReader);
				cxReader = CxReader.createInstance(cxInput, readers);
				cxWriter.start();
				try {
					boolean failedDueToInput = applyLayout(
							new StackedNodeLayout(cxReader, cxWriter),
							postLayoutMetadata, cxReader, cxWriter);
					if (failedDueToInput) {
						cxWriter.end(false,
								"Input CX did not contain necessary aspects");
					} else {
						cxWriter.end(true,
								"Applied StackdNodeLayout to network");
					}
				} catch (IOException e) {
					cxWriter.end(false,
							"Error when writing to file. " + e.getMessage());
				}
				break;
			}
			default: {
				// System.out.println("incompatible layout");
				cxWriter.start();
				cxWriter.end(true, "No layout in accordance to given algorithm");
				break;
			}
			}
			return cartesianLayout;
		} catch (IOException e) {
			throw new RuntimeException(
					"Unable to create CX Readers and/or Writers");
		}
	}
}
