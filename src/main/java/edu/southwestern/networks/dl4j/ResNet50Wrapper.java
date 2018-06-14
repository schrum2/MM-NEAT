package edu.southwestern.networks.dl4j;

import org.deeplearning4j.zoo.model.ResNet50;

import edu.southwestern.util.graphics.ImageNetClassification;

/**
 * Class that can wrap ResNet50 and be a command line parameter.
 * @author Jacob Schrum
 */
public class ResNet50Wrapper extends ZooModelImageNetWrapper {

	public ResNet50Wrapper() {
		super(ResNet50.builder().numClasses(ImageNetClassification.NUM_IMAGE_NET_CLASSES).build());
	}

}
