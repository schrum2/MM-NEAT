package edu.southwestern.tasks.evocraft.genotype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
/**
 * 
 * @author raffertyt
 *
 */

//why minecraft coordinates listed in both place
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

		// Randomly initialize: loop through each index in the minecraft coordinate ranges (x,y,z),
		// Use RandomNumbers class to flip a coin, either put that coordinate in the emptySpace,
		// or get random block from block set with random orientation and put in the blocks map
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					MinecraftCoordinates coordinates = new MinecraftCoordinates(xi, yi, zi);
					boolean empty = RandomNumbers.coinFlip();					
					if(empty) emptySpace.add(coordinates);
					else  {
						BlockType type = RandomNumbers.randomElement(MMNEAT.blockSet.getPossibleBlocks());
						Orientation orientation = RandomNumbers.randomElement(Orientation.values());
						Block newBlock = new Block(coordinates, type, orientation);
						blocks.put(coordinates, newBlock);
					}
				}
			}
		}
	}

	@Override
	public Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> copy() {

		// Create new map and set and copy each item from original map and set, put into a genotype instance and return

		HashMap<MinecraftCoordinates, Block> copyBlocks;
		HashSet<MinecraftCoordinates> copyEmptySpace;
		copyBlocks = blocks;
		copyEmptySpace = emptySpace;
		return new MinecraftShapeGenotype(copyBlocks, copyEmptySpace); // use private constructor
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
		Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>> newPair = new Pair<>(blocks, emptySpace);
		return newPair; // put the map and set in a pair and return
	}

	@Override
	public Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> newInstance() {
		MinecraftShapeGenotype newMinecraftShapeGenotype = new MinecraftShapeGenotype();
		return newMinecraftShapeGenotype; // call public constructor and return
	}
	
	/**
	 * 
	 * @param coordinates
	 * @param type
	 * @param orientation
	 */
	public void addBlock(MinecraftCoordinates coordinates, BlockType type, Orientation orientation) {
		Block newBlock = new Block(coordinates, type, orientation);
		Block previousBlock = blocks.put(coordinates, newBlock);
		assert previousBlock == null : "There was already a block here: " + previousBlock + " at " + coordinates + ":" + emptySpace + ":" + blocks;
		boolean emptyRemoved = emptySpace.remove(coordinates);
		assert emptyRemoved : "Space was not in empty list "+ coordinates + ":" + emptySpace + ":" + blocks;
	}
	
	// remove block from coordinates
	public void removeBlock(MinecraftCoordinates coordinates) {
		emptySpace.remove(coordinates);
	}
	// swap blocks at two coordinates (throw IllegalStatException if either is not in blocks HashMap)
	public void swapBlocks(MinecraftCoordinates coordinates1, BlockType type1, Orientation orientation1, MinecraftCoordinates coordinates2, BlockType type2, Orientation orientation2) {
		if(blocks.containsValue(new Block(coordinates1, type1, orientation1))) throw new IllegalStateException("Block 1 was not present in the blocks hash map");
		if(blocks.containsValue(new Block(coordinates2, type2, orientation2))) throw new IllegalStateException("Block 2 was not present in the blocks hash map");
		
	}
	// change rotation (exception if not present)
	
	// change orientation (exception if not present)
	
	@Override
	public long getId() {
		return id;
	}
}
