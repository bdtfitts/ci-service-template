package org.cytoscape.ci.worker.layout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.ci.service.LayoutService;
import org.cytoscape.ci.worker.BaseWorker;
import org.cytoscape.ci.worker.layout.LayoutInput;
import org.cytoscape.ci.worker.layout.LayoutResult;

/**
 * Sample Worker 1: HelloWorker
 * 
 *  - Simply pass through given message from the service.
 *
 */
public class LayoutWorker extends BaseWorker {
	
	@Override
	public String run(final String rawData) throws Exception {
		System.out.println("Layout worker called!");
		@SuppressWarnings("unchecked")
		
		final LayoutInput inputParams = mapper.readValue(rawData, LayoutInput.class);
		final LayoutResult result = new LayoutResult();
		String inputNetwork = inputParams.network;
		String algorithm = inputParams.algorithm;
		System.out.println(String.format("Applying layout %s to network %s", algorithm, inputNetwork));
		byte[] inputNetworkAsBytes = inputNetwork.getBytes();
		InputStream inputNetworkAsStream = new ByteArrayInputStream(inputNetworkAsBytes);
		ByteArrayOutputStream cartesianLayout = LayoutService.run(inputNetworkAsStream, algorithm);
		String cartesianOutput = cartesianLayout.toString();
		result.result = cartesianOutput;

		// Sleep to emulate long running task...
		Thread.sleep(5000);
		
		// Return message as a serialized JSON
		return mapper.writeValueAsString(result);
	}
}