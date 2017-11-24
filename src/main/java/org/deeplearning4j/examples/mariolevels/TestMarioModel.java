package org.deeplearning4j.examples.mariolevels;

import java.io.IOException;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.factory.Nd4j;

public class TestMarioModel {
	public static void main(String[] args) throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {		
		MultiLayerNetwork network = KerasModelImport.importKerasSequentialModelAndWeights("data/kerasImport/generator.json","data/kerasImport/generator.h5");
		System.out.println(network.summary());
	}
}
