package org.cytoscape.ci.worker.layout;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.ci.service.LayoutService;
import org.cytoscape.ci.worker.BaseWorker;
import org.cytoscape.ci.worker.layout.LayoutInput;

/**
 * LayoutWorker
 * @author Braxton Fitts
 * 
 * This class is the worker used to bridge the CI-Layouts service code
 * to the Cytoscape CI.
 *
 */
public class LayoutWorker extends BaseWorker {
	
	@Override
	public String run(final String rawData) throws Exception {
		System.out.println("Layout worker called!");
		@SuppressWarnings("unchecked")
		
		final LayoutInput inputParams = mapper.readValue(rawData, LayoutInput.class);

		String inputNetwork = inputParams.network;
		String algorithm = inputParams.algorithm;

		//System.out.println(String.format("Applying layout %s to network %s", algorithm, inputNetwork));

		OutputStream cartesianLayout = LayoutService.run(inputNetwork, algorithm);

		String cartesianOutput = cartesianLayout.toString();

		// Return message as a serialized JSON
		return String.format("{\"result\" :  \"%s\"}", cartesianOutput);
	}
}