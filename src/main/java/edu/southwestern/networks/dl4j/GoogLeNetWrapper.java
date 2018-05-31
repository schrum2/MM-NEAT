package edu.southwestern.networks.dl4j;

import org.deeplearning4j.zoo.model.GoogLeNet;

/**
 * Class that can wrap GoogLeNet and be a command line parameter.
 * @author Jacob Schrum
 */
public class GoogLeNetWrapper extends ZooModelImageNetWrapper {

	public GoogLeNetWrapper() {
		super(new GoogLeNet());
	}

}
