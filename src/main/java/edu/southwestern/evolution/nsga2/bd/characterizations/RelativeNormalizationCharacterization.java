package edu.southwestern.evolution.nsga2.bd.characterizations;

import java.util.ArrayList;
import edu.southwestern.evolution.nsga2.bd.BDNSGA2;
import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.southwestern.networks.Network;
import edu.southwestern.scores.Score;

public class RelativeNormalizationCharacterization<T extends Network> extends GeneralNetworkCharacterization<Network> {
	
	/**
	 * Runs each vector in the syllabus through each network in the population. Characterizes
	 * each network based on its output of these syllabus vectors where each individual output is
	 * normalized across the set of outputs from every neural network.
	 * @author Devon Fulcher
	 * @param population the population of neural networks to be characterized
	 * @return characterization of each network 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<BehaviorVector> getAllBehaviorVectors(ArrayList<Score<T>> population, BehaviorCharacterization characterization) {
		//the syllabus is the set of vectors that will be input into each network within a generation
		@SuppressWarnings("unchecked")
		ArrayList<double[]> syllabus = characterization.getSyllabus();
		//where the result will be stored
		ArrayList<BehaviorVector> behaviorVectors = new ArrayList<BehaviorVector>(syllabus.size() * population.size());
		//a list of each network's list of outputs
		ArrayList<double[]> eachNetworksOutputs = new ArrayList<double[]>();
		//best scores per output for a given input vector across all networks in a generation
		ArrayList<Double> bestOutputScores = new ArrayList<Double>();
		//the number of outputs of all networks within this population
		int numOutputs = population.get(0).individual.getPhenotype().numOutputs();
		
		for (int i = 0; i < syllabus.size(); i++) {
			for (int j = 0; j < population.size(); j++) {
				//runs the ith input vector in the syllabus through the jth network and records the output
				double[] outputs = population.get(j).individual.getPhenotype().process(syllabus.get(i));
				//records the highest output among the set of networks for each output. for normalization later
				for (int k = 0; k < numOutputs; k++) {
					if(bestOutputScores.get(k) == null || bestOutputScores.get(k) < outputs[k]) {
						bestOutputScores.set(k, outputs[k]);
					}
				}
				eachNetworksOutputs.add(outputs);
			}
			
			for (int j = 0; j < population.size(); j++) {
				for (int k = 0; k < numOutputs; k++) {
					//normalize each vector with respect to the best output scores
					eachNetworksOutputs.get(j)[k] = eachNetworksOutputs.get(j)[k] / bestOutputScores.get(k);
				}
				//store each behavior vector in its correct location in behaviorVectors
				behaviorVectors.set(j * population.size() + i, new RealBehaviorVector(eachNetworksOutputs.get(j)));
			}
		}
		return behaviorVectors;
	}
}
