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

public class StackedNodeLayout extends AbstractLayout {

	//TODO make these values come from service parameters
	private static final double X_POS = 0d;
	private static final double Y_START_POS = 0d;
	
	private List<NodesElement> nodesToLayout;
	private Map<String, CyVisualPropertiesElement> idToVizPropsMap;
	private Double defaultHeight;
	
	public StackedNodeLayout(CxReader cxNodeReader, CxWriter cxLayoutWriter) {
		super(cxNodeReader, cxLayoutWriter);

	}
	@Override
	public void apply() throws IOException {
		startLayout();
		double y = Y_START_POS;
		for (final NodesElement node : nodesToLayout) {
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
			
			y += actualHeight * 2;
		}
		finishLayout();

	}
	@Override
	protected void parseInput() throws IOException{
		nodesToLayout = new ArrayList<NodesElement>();
		idToVizPropsMap = new HashMap<String, CyVisualPropertiesElement>();
		SortedMap<String, List<AspectElement>> aspectsMap = CxReader.parseAsMap(cxNodeReader);
		for (AspectElement element : aspectsMap.get(NodesElement.ASPECT_NAME)) {
			nodesToLayout.add((NodesElement)element);
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