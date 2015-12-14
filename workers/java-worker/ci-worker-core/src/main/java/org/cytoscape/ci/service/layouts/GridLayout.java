package org.cytoscape.ci.service.layouts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.aspects.datamodels.NodesElement;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.metadata.MetaDataCollection;

/**
 * GridLayout
 * 
 * @author Braxton Fitts
 * @author Ziran Zhang
 *
 * This class will create a CX cartesianLayout aspect corresponding to
 * the grid layout algorithm being applied to a input network.
 */
public class GridLayout extends AbstractLayout {

	// TODO make these values modifiable from service parameters
	private static final double NODE_VERTICAL_SPACING = 80d;
	private static final double NODE_HORIZONTAL_SPACING = 100d;
	private ArrayList<NodesElement> nodesToLayOut;

	public GridLayout(CxReader cxNetworkReader, CxWriter cxLayoutWriter) {
		super(cxNetworkReader, cxLayoutWriter);

	}

	/* Write the cartesianLayout aspect with coordinates as a grid layout */
	@Override
	public boolean apply(MetaDataCollection postLayoutMetadata,
			CxReader cxNetworkReader, CxWriter cxLayoutWriter)
			throws IOException {
		startLayout();
		if (failedDueToInput) {
			finishLayout();
			postLayoutMetadata.setElementCount(
					CartesianLayoutElement.ASPECT_NAME, 0L);
			cxLayoutWriter.addPostMetaData(postLayoutMetadata);
			return failedDueToInput;
		}
		double currX = 0.0d;
		double currY = 0.0d;
		double initialX = 0.0d;

		// Yes, our size and starting points need to be different
		final int nodeCount = nodesToLayOut.size();
		final int columns = (int) Math.sqrt(nodeCount);

		int count = 0;

		// Set visual property.
		// TODO: We need batch apply method for Visual Property values for
		// performance.
		for (final NodesElement node : nodesToLayOut) {
			CartesianLayoutElement nodeLayoutElement = new CartesianLayoutElement(
					node.getId(), currX, currY);
			cxLayoutWriter.writeAspectElement(nodeLayoutElement);

			count++;

			if (count == columns) {
				count = 0;
				currX = initialX;
				currY += NODE_VERTICAL_SPACING;
			} else {
				currX += NODE_HORIZONTAL_SPACING;
			}
		}
		finishLayout();

		postLayoutMetadata.setElementCount(CartesianLayoutElement.ASPECT_NAME,
				(long) nodesToLayOut.size());
		cxLayoutWriter.addPostMetaData(postLayoutMetadata);
		return failedDueToInput;
	}

	/*
	 * Extract information from the CxReader. GridLayout only requires the
	 * NodeElements of the input
	 */
	@Override
	protected void parseInput() throws IOException {
		nodesToLayOut = new ArrayList<NodesElement>();
		SortedMap<String, List<AspectElement>> aspectsMap = CxReader
				.parseAsMap(cxNetworkReader);
		// Check if input CX had necessary aspects
		if (!aspectsMap.containsKey(NodesElement.ASPECT_NAME)) {
			failedDueToInput = true;
			return;
		}
		for (AspectElement element : aspectsMap.get(NodesElement.ASPECT_NAME)) {
			nodesToLayOut.add((NodesElement) element);
		}
	}

}
