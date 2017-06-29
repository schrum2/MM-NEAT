package edu.utexas.cs.nn.evolution.halloffame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.SinglePopulationCoevolutionTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class HallOfFame<T> {
	
	private Set<Pair<Integer, List<Genotype<T>>>> hall; // Stores the Champions
	private int pastGens; // How many Generations in the past to select from
	private int numChamps; // How many Champions to fight against
	
	private int currentGen = -1; // Current Generation being evolved
	private List<Genotype<T>> champs; // Used for storing Champions to fight against
	
	public HallOfFame(){
		hall = new HashSet<Pair<Integer, List<Genotype<T>>>>();
		pastGens = Parameters.parameters.integerParameter("hallOfFamePastGens");
		numChamps = Parameters.parameters.integerParameter("hallOfFameNumChamps");
		champs = new ArrayList<Genotype<T>>();
	}
	
	/**
	 * Evaluates a given Agent against a specified
	 * portion of the Hall Of Fame Champions
	 * 
	 * @param challenger Genotype of the Agent being evaluated
	 * @return List<Pair<double[], double[]>> representing the Fitness Scores of the Agent
	 */
	@SuppressWarnings("unchecked")
	public Pair<double[], double[]> eval(Genotype<T> challenger){
		SinglePopulationCoevolutionTask<T> match = (SinglePopulationCoevolutionTask<T>) MMNEAT.task;
		
		ArrayList<Genotype<T>> genes = new ArrayList<Genotype<T>>();
		genes.add(challenger);
		
		// Changes the Hall of Fame Challenger list once a Generation; champs stay the same otherwise
		if(currentGen != MMNEAT.ea.currentGeneration()){
			currentGen = MMNEAT.ea.currentGeneration();
			
			if(Parameters.parameters.booleanParameter("hallOfFameSingleRandomChamp")){
				champs = getSingleRandomChamp();
			}else if(Parameters.parameters.booleanParameter("hallOfFameXRandomChamps")){
				if(Parameters.parameters.booleanParameter("hallOfFameYPastGens")){
					genes.addAll(getXRandomPastYGenChamps());
				}else{
					champs = getXRandomChamps();
				}
			}else if(Parameters.parameters.booleanParameter("hallOfFameYPastGens")){
				champs = getPastYGenChamps();
			}
		}
		
		genes.addAll(champs);
		
		double[][] fitness = new double[genes.size()][];
		double[][] other = new double[genes.size()][];
		
		for(int i = 0; i < genes.size(); i++){
			Pair<double[], double[]> scores = match.evaluateGroup(genes).get(0);
			fitness[i] = scores.t1;
			other[i] = scores.t2;
		}
		
		return NoisyLonerTask.averageResults(fitness, other);
	}
	
	/**
	 * Adds a given List of Champions into the Hall Of Fame
	 * 
	 * @param generation Generation of the Champions being put in the Hall Of Fame
	 * @param newChamps List of Genotypes from the Champions being saved
	 */
	@SuppressWarnings("unchecked")
	public void addChampions(int generation, List<Genotype<T>> newChamps){
		if(Parameters.parameters.booleanParameter("hallOfFamePareto")){
			SinglePopulationCoevolutionTask<T> match = (SinglePopulationCoevolutionTask<T>) MMNEAT.task;
			
			ArrayList<Genotype<T>> genes = new ArrayList<Genotype<T>>();
			genes.addAll(newChamps);
			genes.addAll(getAllChamps());
			
			// TODO: Set up a way to do multiple matches
			Pair<double[], double[]> scores = match.evaluateGroup(genes).get(0);
			
			// TODO: Get Scores here and add Champs appropriately
		}else{
			hall.add(new Pair<Integer, List<Genotype<T>>>(generation, newChamps));
		}
	}
	
	/**
	 * Returns a random Champion from the Hall of Fame
	 * 
	 * @return List containing the Genotype from a single Random Champion in the Hall of Fame
	 */
	public List<Genotype<T>> getSingleRandomChamp(){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds all Lists of Genotypes from the Hall
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			possChamp.addAll(p.t2);
		}
		
		List<Genotype<T>> singleChamp = new ArrayList<Genotype<T>>();
		singleChamp.add(RandomNumbers.randomElement(possChamp));
		
		return singleChamp;
	}
	
	/**
	 * Returns the List of Champions from the previous Generation
	 * 
	 * @return List of the Genotypes from the Champions from the previous Generation
	 */
	public List<Genotype<T>> getPreviousChamps(){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous generation
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			if(p.t1 == MMNEAT.ea.currentGeneration()-1) possChamp.addAll(p.t2);
		}
		
		return possChamp;
	}
	
	/**
	 * Returns all Champions in the Hall Of Fame
	 * 
	 * @return List of the Genotypes from all Champions in the Hall Of Fame
	 */
	public List<Genotype<T>> getAllChamps(){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds all Lists of Genotypes from the Hall
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			possChamp.addAll(p.t2);
		}
		
		return possChamp;
	}
	
	/**
	 * Default getPastYGenChamps;
	 * 
	 * Returns all Champions from the past X Generations,
	 * where X is the Parameter hallOfFamePastGens;
	 * 
	 * @return List of Genotypes from the Champions from the past X Generations
	 */
	public List<Genotype<T>> getPastYGenChamps(){
		return getPastYGenChamps(pastGens);
	}
	
	/**
	 * Returns all Champions from the past X Generations,
	 * where X is specified by the User;
	 * 
	 * @param numGens Number of Generations in the past to get Champions from
	 * @return List of Genotypes from the Champions from the past X Generations
	 */
	public List<Genotype<T>> getPastYGenChamps(int numGens){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous X generations
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			if(p.t1 >= (MMNEAT.ea.currentGeneration()-numGens)) possChamp.addAll(p.t2);
		}
		
		return possChamp;
	}
	
	/**
	 * Default getXRandomChamps;
	 * 
	 * Returns a List of Random Champions with a size of X,
	 * where X is the Parameter hallOfFameNumChamps;
	 * 
	 * Currently allows for duplicates
	 * 
	 * @return
	 */
	public List<Genotype<T>> getXRandomChamps(){
		return getXRandomChamps(numChamps);
	}
	
	/**
	 * Returns a List of Random Champions with a size of X,
	 * where X is specified by the User;
	 * currently allows for duplicates
	 * 
	 * @param numChamps Number of Random Champions to return
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomChamps(int numChamps){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds all Lists of Genotypes from the Hall
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			possChamp.addAll(p.t2);
		}
		
		List<Genotype<T>> randomChamps = new ArrayList<Genotype<T>>();
		
		for(int i = 0; i < numChamps; i++){
			randomChamps.add(RandomNumbers.randomElement(possChamp));
		}
		
		return randomChamps;
	}
	
	/**
	 * Default getXRandomPastYChamps;
	 * 
	 * Returns a List of Random Champions from the past X Generations with a size of Y,
	 * where X and Y are the Parameters hallOfFamePastGens and hallOfFameNumChamps;
	 * 
	 * Currently allows for duplicates
	 * 
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomPastYGenChamps(){
		return getXRandomPastYGenChamps(pastGens, numChamps);
	}
	
	/**
	 * Returns a List of Random Champions from the past X Generations with a size of Y,
	 * where X and Y are specified by the User;
	 * 
	 * Currently allows for duplicates
	 * 
	 * @param numGens Number of Generations in the past to get Champions from
	 * @param numChamps Number of Random Champions to return
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomPastYGenChamps(int numGens, int numChamps){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous X generations
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			if(p.t1 >= (MMNEAT.ea.currentGeneration() - numGens)) possChamp.addAll(p.t2);
		}
		
		List<Genotype<T>> randomChamps = new ArrayList<Genotype<T>>();
		
		for(int i = 0; i < numChamps; i++){
			randomChamps.add(RandomNumbers.randomElement(possChamp));
		}
		
		return randomChamps;
	}
	
}