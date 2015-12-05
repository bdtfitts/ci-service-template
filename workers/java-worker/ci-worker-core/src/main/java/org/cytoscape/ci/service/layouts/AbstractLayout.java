package org.cytoscape.ci.service.layouts;

import java.io.IOException;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.metadata.MetaDataCollection;

public abstract class AbstractLayout implements LayoutAlgorithm {

	protected CxReader cxNodeReader;
	protected CxWriter cxLayoutWriter;
	public AbstractLayout(CxReader cxNodeReader, CxWriter cxLayoutWriter) {
		this.cxNodeReader = cxNodeReader;
		this.cxLayoutWriter = cxLayoutWriter;
	}
	
	protected void startLayout() throws IOException {
		cxLayoutWriter.startAspectFragment(CartesianLayoutElement.ASPECT_NAME);
		parseInput();
	}

	protected void finishLayout() throws IOException {
		cxLayoutWriter.endAspectFragment();
	}

	@Override
	abstract public void apply(MetaDataCollection postLayoutMetadata) throws IOException;

	abstract protected void parseInput() throws IOException;

}
