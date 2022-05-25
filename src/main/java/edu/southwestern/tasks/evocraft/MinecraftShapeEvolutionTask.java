package edu.southwestern.tasks.evocraft;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.TypeCountFitness;


public class MinecraftShapeEvolutionTask<T> implements SinglePopulationTask<T>, NetworkTask {

	private ArrayList<MinecraftFitnessFunction> fitnessFunctions;
	
	public MinecraftShapeEvolutionTask() {
		fitnessFunctions = new ArrayList<MinecraftFitnessFunction>();

		// TODO: Control via parameters later
		fitnessFunctions.add(new TypeCountFitness());
		
	}
	
	@Override
	public String[] sensorLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] outputLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numObjectives() {
		return fitnessFunctions.size();
	}

	@Override
	public double[] minScores() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void finalCleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postConstructionInitialization() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		// TODO Auto-generated method stub
		return null;
	}

}
