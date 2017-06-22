package edu.utexas.cs.nn.evolution.halloffame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class HallOfFame<T> {
	
	public Set<Pair<Integer, List<Genotype<T>>>> hall;
	
	public HallOfFame(){
		hall = new HashSet<Pair<Integer, List<Genotype<T>>>>();
	}
	
	/**
	 * Adds a given List of Champions into the Hall Of Fame
	 * 
	 * @param generation Generation of the Champions being put in the Hall Of Fame
	 * @param champs List of Genotypes from the Champions being saved
	 */
	public void addChampions(int generation, List<Genotype<T>> champs){
		hall.add(new Pair<Integer, List<Genotype<T>>>(generation, champs));
	}
	
	/**
	 * Returns a random Champion from the Hall of Fame
	 * 
	 * @return List containing the Genotype from a single Random Champion in the Hall of Fame
	 */
	public List<Genotype<T>> getRandomChamp(){
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
	 * Returns all Champions from the past X Generations,
	 * where X is specified by the User
	 * 
	 * @param numGens Number of Generations in the past to get Champions from
	 * @return List of Genotypes from the Champions from the past X Generations
	 */
	public List<Genotype<T>> getPastXGenChamps(int numGens){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous X generations
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			if(p.t1 >= (MMNEAT.ea.currentGeneration()-numGens)) possChamp.addAll(p.t2);
		}
		
		return possChamp;
	}
	
	/**
	 * Returns a List of Random Champions with a size of X,
	 * where X is specified by the User
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
	 * Returns a List of Random Champions from the past X Generations with a size of Y,
	 * where X and Y are specified by the User
	 * 
	 * @param numGens Number of Generations in the past to get Champions from
	 * @param numChamps Number of Random Champions to return
	 * @return List of Genotypes from the Random Champions
	 */
	public List<Genotype<T>> getXRandomPastYGenChamps(int numGens, int numChamps){
		List<Genotype<T>> possChamp = new ArrayList<Genotype<T>>();
		
		// Adds the List of Genotypes from the previous X generations
		for(Pair<Integer, List<Genotype<T>>> p : hall){
			if(p.t1 >= (MMNEAT.ea.currentGeneration()-numGens)) possChamp.addAll(p.t2);
		}
		
		List<Genotype<T>> randomChamps = new ArrayList<Genotype<T>>();
		
		for(int i = 0; i < numChamps; i++){
			randomChamps.add(RandomNumbers.randomElement(possChamp));
		}
		
		return randomChamps;
	}
	
}