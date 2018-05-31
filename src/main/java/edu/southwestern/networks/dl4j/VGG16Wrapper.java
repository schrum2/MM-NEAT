package edu.southwestern.networks.dl4j;

import org.deeplearning4j.zoo.model.VGG16;

/**
 * Class that can wrap VGG16 and be a command line parameter.
 * @author Jacob Schrum
 */
public class VGG16Wrapper extends ZooModelImageNetWrapper {

	public VGG16Wrapper() {
		super(new VGG16());
	}

}
