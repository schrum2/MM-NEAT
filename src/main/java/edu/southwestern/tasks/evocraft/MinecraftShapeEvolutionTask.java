package edu.southwestern.tasks.evocraft;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.tasks.evocraft.blocks.MachineBlockSet;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.TypeCountFitness;


public class MinecraftShapeEvolutionTask<T> implements SinglePopulationTask<T>, NetworkTask {

	private ArrayList<MinecraftFitnessFunction> fitnessFunctions;
	private BlockSet blockSet;
	
	public MinecraftShapeEvolutionTask() {
		fitnessFunctions = new ArrayList<MinecraftFitnessFunction>();

		// TODO: Control via parameters later
		fitnessFunctions.add(new TypeCountFitness());
		// TODO: Command line parameter
		blockSet = new MachineBlockSet();
	}
	
	/**
	 * returns the sensorLabels
	 * @return the sensorLabels
	 */
	@Override
	public String[] sensorLabels() {
		if(Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane")) {
			return new String[] { "X", "Y", "Z", "R", "R-XY", "R-YZ", "R-XZ", "bias" };
		} else {
			return new String[] { "X", "Y", "Z", "R", "bias" };
		}
	}

	/**
	 * returns the outputLabels
	 * @return the outputLabels
	 */
	@Override
	public String[] outputLabels() {
		// Presence output and an output for each block type
		String[] labels = new String[1 + blockSet.getPossibleBlocks().length];
		labels[0] = "Presence";
		for(int i = 1; i < labels.length; i++) {
			labels[i] = blockSet.getPossibleBlocks()[i-1].name();
		}
		return labels;
	}

	@Override
	public int numObjectives() {
		return fitnessFunctions.size();
	}

	@Override
	public double[] minScores() {
		double[] scores = new double[fitnessFunctions.size()];
		for(int i = 0; i < scores.length; i++) {
			scores[i] = fitnessFunctions.get(i).minFitness();
		}
		return scores;
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	@Override
	public void finalCleanup() {
		// Nothing
	}

	@Override
	public void postConstructionInitialization() {
		// Nothing
	}

	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		MinecraftClient client = MinecraftClient.getMinecraftClient();
		ArrayList<Score<T>> scores = new ArrayList<Score<T>>();
		
		for(Genotype<T> g : population) {
			
			
		}
		
		// TODO Auto-generated method stub
		return null;
	}

}
