package edu.southwestern.networks.dl4j;

import org.deeplearning4j.zoo.model.VGG19;

/**
 * Class that can wrap VGG19 and be a command line parameter.
 * @author Jacob Schrum
 */
public class VGG19Wrapper extends ZooModelImageNetWrapper {

	public VGG19Wrapper() {
		super(new VGG19());
	}

}
