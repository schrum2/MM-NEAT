package edu.southwestern.networks.dl4j;

import org.deeplearning4j.zoo.model.ResNet50;

/**
 * Class that can wrap ResNet50 and be a command line parameter.
 * @author Jacob Schrum
 */
public class ResNet50Wrapper extends ZooModelImageNetWrapper {

	public ResNet50Wrapper() {
		super(new ResNet50());
	}

}
