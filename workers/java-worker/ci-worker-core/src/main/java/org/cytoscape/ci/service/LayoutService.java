package org.cytoscape.ci.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

import org.cxio.aspects.readers.CyVisualPropertiesFragmentReader;
import org.cxio.aspects.readers.EdgesFragmentReader;
import org.cxio.aspects.readers.NodesFragmentReader;
import org.cxio.aspects.writers.CartesianLayoutFragmentWriter;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectFragmentReader;
import org.cxio.core.interfaces.AspectFragmentWriter;
import org.cytoscape.ci.service.layouts.GridLayout;
import org.cytoscape.ci.service.layouts.LayoutAlgorithm;
import org.cytoscape.ci.service.layouts.StackedNodeLayout;

public class LayoutService {
	
	private static final String GRID_LAYOUT = "grid";
	private static final String STACKED_LAYOUT = "stacked";

	private static void applyLayout(LayoutAlgorithm layout) throws IOException {
		layout.apply();
	}
	public static OutputStream run(InputStream cxInput, String algorithm) {

		ByteArrayOutputStream cartesianLayout = new ByteArrayOutputStream();
		
		//Create necessary AspectFragmentReaders and AspectFragmentWriter
		NodesFragmentReader nodesFragmentReader = NodesFragmentReader.createInstance();
		EdgesFragmentReader edgesFragmentReader = EdgesFragmentReader.createInstance();
		CartesianLayoutFragmentWriter cartesianFragmentWriter = CartesianLayoutFragmentWriter.createInstance();

		//Prepare AspectFragmentWriter for bundling into CxReader object
		HashSet<AspectFragmentWriter> writer = new HashSet<AspectFragmentWriter>(1);
		writer.add(cartesianFragmentWriter);

		//Prepare AspectFragmentReaders for bundling into CxReader object
		HashSet<AspectFragmentReader> readers = new HashSet<AspectFragmentReader>(2);
		readers.add(nodesFragmentReader);
		readers.add(edgesFragmentReader);

		try {
			CxReader cxReader;
			CxWriter cxWriter = CxWriter.createInstance(cartesianLayout, true, writer);
			System.out.println("Applying layout...");
			if (algorithm == null) {
				algorithm = "null";
			}
			switch (algorithm) {
				case GRID_LAYOUT: {
					System.out.println("Grid layout");
					cxReader = CxReader.createInstance(cxInput, readers);
					cxWriter.start();
					try {
						applyLayout(new GridLayout(cxReader, cxWriter));
						cxWriter.end(true, "Applied GridLayout to network");
					} catch (IOException e) {
						cxWriter.end(false, "Error when writing to file. " + e.getMessage());
					}
					break;
				}
				case STACKED_LAYOUT: {
					System.out.println("Stacked Node layout");
					CyVisualPropertiesFragmentReader vizPropFragmentReader = CyVisualPropertiesFragmentReader.createInstance();
					readers.add(vizPropFragmentReader);
					cxReader = CxReader.createInstance(cxInput, readers);
					cxWriter.start();
					try {
						applyLayout(new StackedNodeLayout(cxReader, cxWriter));
						cxWriter.end(true, "Applied StackedNodeLayout to network");
					} catch (IOException e) {
						cxWriter.end(false, "Error when writing to file. " + e.getMessage());
					}
					break;
				}
				default: {
					System.out.println("incompatible layout");
					cxWriter.start();
					cxWriter.end(true, "Unable to create layout in accordance to given algorithm");
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to create CX Readers and/or Writers");
		} finally {
			return cartesianLayout;
		}
	}
}