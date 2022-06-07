package edu.southwestern.experiment.post;

import java.io.*;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.FileUtilities;

/**
 * General evolution experiments are meant to save the best genome in each
 * objective to a directory "bestObjectives". This experiment loads those genomes
 * and evaluates them.
 *
 * @author Jacob Schrum
 * @param <T> Type of evolved phenotype
 */
public class ObjectiveBestNetworksExperiment<T> implements Experiment {

	private ArrayList<Genotype<T>> genotypes;

	/**
	 * Load best performer in each objective (previously saved),
	 * or load entire past lineage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		if (Parameters.parameters.booleanParameter("watchLastBest")) {
			genotypes = new ArrayList<Genotype<T>>();
			for(int i = 0; i < MMNEAT.task.numObjectives(); i++) {
				int lastGen = Parameters.parameters.integerParameter("lastSavedGeneration");
				String file = FileUtilities.getSaveDirectory() + "/bestObjectives/gen" + lastGen + "_bestIn"+i;
				genotypes.add((Genotype<T>) PopulationUtil.extractGenotype(file));
			}
		} else {
			String dir = FileUtilities.getSaveDirectory() + "/bestObjectives";
			genotypes = PopulationUtil.load(dir);
		}
		
		// check
		// check if the first genome is a TWEANNGenotype
		if(genotypes.get(0) instanceof TWEANNGenotype) {
		
			// base directory where the sub-directory will be made.
			String base = Parameters.parameters.stringParameter("base");
			
			// make new sub-dir "GraphVizNetworks"
			File f = new File(base + "/" + Parameters.parameters.stringParameter("saveTo") +"/GraphVizNetworks");
			if (!f.exists()) f.mkdirs();
			
			// loop through all genotypes and create a file for each one containing toGraphViz method result
			String[] inputs = ((NetworkTask) MMNEAT.task).sensorLabels(); // get input labels
			String[] outputs = ((NetworkTask) MMNEAT.task).outputLabels(); // get output labels
			for(int i = 0; i < genotypes.size(); i++) {
				// use ((NetworkTask) MMNEAT.task)
				String nnString = ((TWEANNGenotype) MMNEAT.task).toGraphViz(inputs, outputs);
				//String nnString = TWEANNGenotype.toGraphViz(inputs, outputs);
				File newNeuralNetowrkFile = new File(base + "/" + Parameters.parameters.stringParameter("saveTo") + "/GraphVizNetworks/" + i + " nueralnetwork.txt");
				
				// write to new file the output of toGraphViz method
				try {
					FileWriter fw = new FileWriter(newNeuralNetowrkFile);
					fw.write(nnString);
					fw.close();
				} catch(Exception e) {
					System.out.println(e);
				}
				
			}

		}
	}

	/**
	 * Evaluate each individual. Only works for Loner Tasks
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		for (int i = 0; i < genotypes.size(); i++) {
			System.out.println("Best in Objective " + i + ": " + genotypes.get(i).getId());
			Score s = ((LonerTask) MMNEAT.task).evaluateOne(genotypes.get(i));
			System.out.println(s);
		}
		((LonerTask) MMNEAT.task).finalCleanup(); // domain cleanup if necessary
	}

	/**
	 * method not used
	 */
	@Override
	public boolean shouldStop() {
		// Will never be called
		return true;
	}
}
