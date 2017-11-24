package org.deeplearning4j.examples.mariolevels;

import java.io.IOException;

import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

/**
 * This is a test of a model generated in Keras 2. DL4J fails to load it currently.
 * Once version 0.9.2 of DL4J gets put on Maven central, I should be able to successfully
 * load this model.
 * 
 * @author Jacob
 *
 */
public class TestMarioModel {
	public static void main(String[] args) throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {		
		MultiLayerNetwork network = KerasModelImport.importKerasSequentialModelAndWeights("data/kerasImport/generator.json","data/kerasImport/generator.h5");
		System.out.println(network.summary());
	}
}
