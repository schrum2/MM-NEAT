package edu.southwestern.evolution.halloffame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.SinglePopulationCoevolutionTask;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.random.RandomNumbers;

public class HallOfFame<T> {
	
	private Set<Triple<Integer, Genotype<T>, Score<T>>> hall; // Stores the Champions
	private int pastGens; // How many Generations in the past to select from
	private int numChamps; // How many Champions to fight against
	
	private int currentGen = -1; // Current Generation being evolved
	private List<Genotype<T>> champs; // Used for storing Champions to fight against
	
	public HallOfFame(){
		hall = new HashSet<Triple<Integer, Genotype<T>, Score<T>>>();
		pastGens = Parameters.parameters.integerParameter("hallOfFamePastGens");
		numChamps = Parameters.parameters.integerParameter("hallOfFameNumChamps");
		champs = new ArrayList<Genotype<T>>();
		System.out.println("Initialize Hall of Fame");
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
		
		// Changes the Hall of Fame Challenger list once a Generation; Champions stay the same otherwise
		if(currentGen != MMNEAT.ea.currentGeneration()){
			currentGen = MMNEAT.ea.currentGeneration();
			
			if(Parameters.parameters.booleanParameter("hallOfFameSingleRandomChamp")){
				champs = getSingleRandomChamp();
			}else if(Parameters.parameters.booleanParameter("hallOfFameXrandChamps")){
				if(Parameters.parameters.booleanParameter("hallOfFameYPastGens")){
					genes.addAll(getXRandomomPastYGenChamps());
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
	 * @param newChampsList List of Genotypes from the Champions being saved
	 */
	public void addChampions(int generation, List<Pair<Genotype<T>, Score<T>>> newChampsList){
		if(Parameters.parameters.booleanParameter("hallOfFamePareto")){
			
			// Cycles through every Champion currently in the Hall Of Fame
			Iterator<Triple<Integer, Genotype<T>, Score<T>>> itr = hall.iterator();
			while(itr.hasNext()){
				Triple<Integer, Genotype<T>, Score<T>> oldChamp = itr.next();
				// Cycles through all the new Champions
				Iterator<Pair<Genotype<T>, Score<T>>> itr2 = newChampsList.iterator();
				while(itr2.hasNext()){
					Pair<Genotype<T>, Score<T>> newChamp = itr2.next();
					if(newChamp.t2.isAtLeastAsGood(oldChamp.t3)) itr.remove(); // Removes the old Champion if the new Champion is better
					if(oldChamp.t3.isBetter(newChamp.t2)) itr2.remove(); // Removes the new Champion if the old Champion is better
				}
			}
			
			// Adds the new surviving Champions to the Hall Of Fame
			for(Pair<Genotype<T>, Score<T>> champion : newChampsList){
				hall.add(new Triple<Integer, Genotype<T>, Score<T>>(generation, champion.t1, champion.t2));
			}
		}else{
			for(Pair<Genotype<T>, Score<T>> champion : newChampsList){
				hall.add(new Triple<Integer, Genotype<T>, Score<T>>(generation, champion.t1, champion.t2));
			}
		}
		System.out.println("Hall of Fame now contains " + hall.size() + " champions");
	}
	
	/**
	 * Returns a random Champion from the Hall of Fame
	 * 
	 * @return List containing the Genotype from a single Random Champion in the Hall of Fame
	 */
	public List<Genotype<T>> getSingleRandomChamp(){
		List<Genotype<T>> champions = new ArrayList<Genotype<T>>();
		
		// Adds all Lists of Genotypes from the Hall
		for(Triple<Integer, Genotype<T>, Score<T>> tr : hall){
			champions.add(tr.t2);
		}
		
		List<Genotype<T>> singleChamp = new ArrayList<Genotype<T>>();
		singleChamp.add(RandomNumbers.randomElement(champions));
		
		return singleChamp;
	}
	
	/**
	 * Returns the List of Champions from the previous Generation
	 * 
	 * @return List of the Genotypes from the Champions from the previous Generation
	 */
	public List<Genotype<T>> getPreviousChamps(){
		List<Genotype<T>> champions = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous generation
		for(Triple<Integer, Genotype<T>, Score<T>> tr : hall){
			if(tr.t1 == MMNEAT.ea.currentGeneration()-1) champions.add(tr.t2);
		}
		
		return champions;
	}
	
	/**
	 * Returns all Champions in the Hall Of Fame
	 * 
	 * @return List of the Genotypes from all Champions in the Hall Of Fame
	 */
	public List<Genotype<T>> getAllChamps(){
		List<Genotype<T>> champions = new ArrayList<Genotype<T>>();
		
		// Adds all Lists of Genotypes from the Hall
		for(Triple<Integer, Genotype<T>, Score<T>> tr : hall){
			champions.add(tr.t2);
		}
		
		return champions;
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
		List<Genotype<T>> champions = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous X generations
		for(Triple<Integer, Genotype<T>, Score<T>> tr : hall){
			if(tr.t1 >= (MMNEAT.ea.currentGeneration()-numGens)) champions.add(tr.t2);
		}
		
		return champions;
	}
	
	/**
	 * Default getXRandomChamps;
	 * 
	 * Returns a List of Random Champions with a size of X,
	 * where X is the Parameter hallOfFameNumChamps
	 * 
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomChamps(){
		return getXRandomChamps(numChamps);
	}
	
	/**
	 * Returns a List of Random Champions with a size of X,
	 * where X is specified by the User
	 * 
	 * @param numChamps Number of Random Champions to return
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomChamps(int numChamps){
		List<Genotype<T>> champions = new ArrayList<Genotype<T>>();
		
		// Adds all Lists of Genotypes from the Hall
		for(Triple<Integer, Genotype<T>, Score<T>> tr : hall){
			champions.add(tr.t2);
		}
		
		// Only runs if there are more possible Champions than are required
		if(champions.size() > numChamps){
			Set<Genotype<T>> randChamps = new HashSet<Genotype<T>>();

			while(randChamps.size() < numChamps){
				randChamps.add(RandomNumbers.randomElement(champions));
			}

			champions.clear();
			champions.addAll(randChamps);
		}
		
		return champions;
	}
	
	/**
	 * Default getXRandomPastYGenChamps;
	 * 
	 * Returns a List of Random Champions from the past X Generations with a size of Y,
	 * where X and Y are the Parameters hallOfFamePastGens and hallOfFameNumChamps
	 * 
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomomPastYGenChamps(){
		return getXRandomPastYGenChamps(pastGens, numChamps);
	}
	
	/**
	 * Returns a List of Random Champions from the past X Generations with a size of Y,
	 * where X and Y are specified by the User;
	 * 
	 * @param numGens Number of Generations in the past to get Champions from
	 * @param numChamps Number of Random Champions to return
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomPastYGenChamps(int numGens, int numChamps){
		List<Genotype<T>> champions = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous X generations
		for(Triple<Integer, Genotype<T>, Score<T>> tr : hall){
			if(tr.t1 >= (MMNEAT.ea.currentGeneration() - numGens)) champions.add(tr.t2);
		}
		
		// Only runs if there are more possible Champions than are required
		if(champions.size() > numChamps){
			Set<Genotype<T>> randChamps = new HashSet<Genotype<T>>();
			
			while(randChamps.size() < numChamps){
				randChamps.add(RandomNumbers.randomElement(champions));
			}

			champions.clear();
			champions.addAll(randChamps);
		}

		return champions;
	}
	
}