package org.cytoscape.ci.service.layouts;

import java.io.IOException;

import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.metadata.MetaDataCollection;

/**
 * LayoutAlgorithm
 * 
 * @author: Braxton Fitts
 * @author: Ziran Zhang
 *
 * A LayoutAlgorithm is an object that will write the
 * cartesianLayout aspect of a CX stream that corresponds to a
 * placement of nodes from an input network in CX format according to
 * some graph layout algorithm. The LayoutAlgorithm object is also
 * responsible for writing any postmetadata that may come during the
 * creation of the cartesianLayout aspect. A LayoutAlgorithm object is
 * expected to be able to get the information about an input network
 * through a CxReader that contains the necessary AspectFragmentReaders
 * for the algorithm, and a CxWriter that contains the necessary
 * CartesianLayoutFragmentWriter.
 */
public interface LayoutAlgorithm {

	/*
	 * This method creates the cartesianLayout aspect to be written by the
	 * passed-in CxWriter for the network to be read by the passed-in CxReader.
	 * Returns a boolean expressing whether the layout failed due to the input
	 * not containing necessary aspects.
	 */
	public boolean apply(MetaDataCollection postLayoutMetadata,
			CxReader cxNetworkReader, CxWriter cxCartesianLayoutWriter)
			throws IOException;
}
