package edu.utexas.cs.nn.evolution.selectiveBreeding;

import java.util.ArrayList;

import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.util.PopulationUtil;

/**
 * 
 * @author gillespl
 *
 * @param <T>
 */
public class SelectiveBreedingEA<T> implements SinglePopulationGenerationalEA<T> {

	private SinglePopulationTask<T> task;
	private int parentPop;
	private int generation;
	
	public SelectiveBreedingEA(SinglePopulationTask<T> task, int parentPop) {
		this.task = task;
		this.parentPop = parentPop;
		this.generation = Parameters.parameters.integerParameter("lastSavedGeneration");
	}
	@Override
	public int currentGeneration() {
		return generation;
	}

	@Override
	public int evaluationsPerGeneration() {
		return parentPop;
	}

	@Override
	public Task getTask() {
		return task;
	}

	@Override
	public ArrayList<Genotype<T>> initialPopulation(Genotype<T> example) {
		ArrayList<Genotype<T>> initPop = PopulationUtil.initialPopulation(example, parentPop);
		return initPop;
	}

	@Override
	public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> population) {
		int size = population.size();
		ArrayList<Genotype<T>> children = new ArrayList<Genotype<T>>();
		ArrayList<Score<T>> scores = task.evaluateAll(population);
		for(int i = 0; i < scores.size(); i++) {
			if(scores.get(i).scores[0] < 1.0) {//not sure if able to assume only one score in array
				scores.remove(i); 
			}
		}
		
		
		return null;
	}

	@Override
	public void close(ArrayList<Genotype<T>> population) {
		// TODO Auto-generated method stub
		
	}

}
