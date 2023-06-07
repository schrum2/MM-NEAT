package edu.southwestern.tasks.evocraft.genotype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.Pair;

public class MinecraftShapeGenotype implements Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>>{

	private long id = EvolutionaryHistory.nextGenotypeId();
	// Stores parent IDs for tacking lineage. Not serialized.
	transient List<Long> parents = new LinkedList<Long>();
	
	private HashMap<MinecraftCoordinates, Block> blocks;
	private HashSet<MinecraftCoordinates> emptySpace;
	
	@Override
	public void addParent(long id) {
		parents.add(id);
	}

	@Override
	public List<Long> getParentIDs() {
		return parents;
	}
	
	private MinecraftShapeGenotype(HashMap<MinecraftCoordinates, Block> blocks, HashSet<MinecraftCoordinates> emptySpace) {
		this.blocks = blocks;
		this.emptySpace = emptySpace;
	}
	
	public MinecraftShapeGenotype() {
		this.blocks = new HashMap<MinecraftCoordinates, Block>();
		this.emptySpace = new HashSet<MinecraftCoordinates>();
		
		// TODO
		// Randomly initialize: loop through each index in the minecraft coordinate ranges (x,y,z),
		// Use RandomNumbers class to flip a coin, either put that coordinate in the emptySpace,
		// or get random block from block set with random orientation and put in the blocks map
		
	}
	
	@Override
	public Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> copy() {
		
		// Create new map and set and copy each item from original map and set, put into a genotype instance and return
		
		return null; // use private constructor
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> crossover(
			Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>> getPhenotype() {
		// TODO Auto-generated method stub
		return null; // put the map and set in a pair and return
	}

	@Override
	public Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> newInstance() {
		// TODO Auto-generated method stub
		return null; // call public constructor and return
	}

	@Override
	public long getId() {
		return id;
	}
}
