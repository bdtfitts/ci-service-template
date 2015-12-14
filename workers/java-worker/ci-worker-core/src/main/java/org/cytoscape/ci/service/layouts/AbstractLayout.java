package org.cytoscape.ci.service.layouts;

import java.io.IOException;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.metadata.MetaDataCollection;

/**
 * AbstractLayout
 * 
 * @author Braxton Fitts
 * @author Ziran Zhang
 *
 * This class contains basic helper methods that will be
 * useful for creating a custom layout algorithm.
 */
public abstract class AbstractLayout implements LayoutAlgorithm {

	// CxReader used to retrieve network information;
	protected CxReader cxNetworkReader;

	// CxWriter used to write cartesianLayout aspect
	protected CxWriter cxLayoutWriter;

	// Boolean denoting whether layout application failed due to missing
	// information
	protected boolean failedDueToInput = false;

	public AbstractLayout(CxReader cxNetworkReader, CxWriter cxLayoutWriter) {
		this.cxNetworkReader = cxNetworkReader;
		this.cxLayoutWriter = cxLayoutWriter;
	}

	/*
	 * Start the cartesianLayout aspect and retrieve necessary information from
	 * input network
	 */
	protected void startLayout() throws IOException {
		cxLayoutWriter.startAspectFragment(CartesianLayoutElement.ASPECT_NAME);
		parseInput();
	}

	/* End the cartesianLayout aspect */
	protected void finishLayout() throws IOException {
		cxLayoutWriter.endAspectFragment();
	}

	/* Overloaded method for apply() since CxReader and Writer are fields */
	public boolean apply(MetaDataCollection postLayoutMetadata)
			throws IOException {
		return apply(postLayoutMetadata, cxNetworkReader, cxLayoutWriter);
	}

	/* Apply layout by writing CX stream containing cartesianLayout aspect */
	@Override
	abstract public boolean apply(MetaDataCollection postLayoutMetadata,
			CxReader cxNetworkReader, CxWriter cxLayoutWriter)
			throws IOException;

	/* Extract necessary information from the input network through the CxReader */
	abstract protected void parseInput() throws IOException;

}
