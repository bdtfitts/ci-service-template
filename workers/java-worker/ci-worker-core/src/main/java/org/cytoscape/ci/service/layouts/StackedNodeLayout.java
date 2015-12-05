package org.cytoscape.ci.service.layouts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.aspects.datamodels.CyVisualPropertiesElement;
import org.cxio.aspects.datamodels.NodesElement;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.metadata.MetaDataCollection;

public class StackedNodeLayout extends AbstractLayout {

	//TODO make these values come from service parameters
	private static final double X_POS = 0d;
	private static final double Y_START_POS = 0d;
	
	private List<NodesElement> nodesToLayOut;
	private Map<Long, CyVisualPropertiesElement> idToVizPropsMap;
	private Double defaultHeight;
	
	public StackedNodeLayout(CxReader cxNodeReader, CxWriter cxLayoutWriter) {
		super(cxNodeReader, cxLayoutWriter);

	}
	@Override
	public void apply(MetaDataCollection postLayoutMetadata) throws IOException {
		startLayout();
		double y = Y_START_POS;
		for (final NodesElement node : nodesToLayOut) {
			CartesianLayoutElement nodeLayoutElement = new CartesianLayoutElement(node.getId(), X_POS, y);
			CyVisualPropertiesElement vizPropEle = idToVizPropsMap.get(node.getId());
			Double actualHeight = defaultHeight;
			if (vizPropEle != null) {
				String height = vizPropEle.getProperties().get("NODE_HEIGHT");
				if (height != null) {
					actualHeight = Double.parseDouble(vizPropEle.getProperties().get("NODE_HEIGHT"));
				}
			}
			cxLayoutWriter.writeAspectElement(nodeLayoutElement);
			
			//TODO Retrieve node height from visualProperties aspect for spacing
			y += actualHeight * 2;
		}
		finishLayout();
		
		postLayoutMetadata.setElementCount(CartesianLayoutElement.ASPECT_NAME, (long) nodesToLayOut.size());
		cxLayoutWriter.addPostMetaData(postLayoutMetadata);

	}
	@Override
	protected void parseInput() throws IOException{
		nodesToLayOut = new ArrayList<NodesElement>();
		idToVizPropsMap = new HashMap<Long, CyVisualPropertiesElement>();
		SortedMap<String, List<AspectElement>> aspectsMap = CxReader.parseAsMap(cxNodeReader);
		for (AspectElement element : aspectsMap.get(NodesElement.ASPECT_NAME)) {
			nodesToLayOut.add((NodesElement)element);
			idToVizPropsMap.put(((NodesElement)element).getId(), null);
		}
		for (AspectElement element : aspectsMap.get(CyVisualPropertiesElement.ASPECT_NAME)) {
			CyVisualPropertiesElement vizPropEle = (CyVisualPropertiesElement)element;
			if (vizPropEle.getPropertiesOf().equals("nodes")) {
				idToVizPropsMap.put(vizPropEle.getAppliesTo().get(0), vizPropEle);
			}
			else if (vizPropEle.getPropertiesOf().equals("nodes:default")) {
				String height = vizPropEle.getProperties().get("NODE_HEIGHT");
				if (height != null) {
					defaultHeight = Double.parseDouble(height);
				}
			}
		}
	}

}
