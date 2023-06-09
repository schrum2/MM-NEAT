package edu.southwestern.tasks.evocraft.genotype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.mutation.AddBlockMutation;
import edu.southwestern.tasks.evocraft.mutation.ChangeBlockOrientationMutation;
import edu.southwestern.tasks.evocraft.mutation.ChangeBlockTypeMutation;
import edu.southwestern.tasks.evocraft.mutation.RemoveBlockMutation;
import edu.southwestern.tasks.evocraft.mutation.SwapBlocksMutation;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
/**
 * New way to represent a shape as Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>
 * This allows us to define mutation operators that are more appropriate for Minecraft and make it easier to search the space.
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
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}
	/**
	 * Creates a randomized initial shape
	 */
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
						BlockType type = randomBlockType();
						Orientation orientation = randomBlockOrientation();
						Block newBlock = new Block(coordinates, type, orientation);
						blocks.put(coordinates, newBlock);
					}
				}
			}
		}
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}

	public static Orientation randomBlockOrientation() {
		Orientation orientation = RandomNumbers.randomElement(Orientation.values());
		return orientation;
	}

	public static BlockType randomBlockType() {
		BlockType type = RandomNumbers.randomElement(MMNEAT.blockSet.getPossibleBlocks());
		return type;
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
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId());
		sb.append(" ");
		new AddBlockMutation().go(this, sb);
		new RemoveBlockMutation().go(this, sb);
		new ChangeBlockTypeMutation().go(this, sb);
		new SwapBlocksMutation().go(this, sb);
		new AddBlockMutation().go(this, sb);
		new ChangeBlockOrientationMutation().go(this, sb);
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}

	@Override
	public Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> crossover(
			Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> g) {

		MinecraftShapeGenotype other = (MinecraftShapeGenotype) g;
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					boolean flip = RandomNumbers.coinFlip();
					if(flip) {
						MinecraftCoordinates coordinates = new MinecraftCoordinates(xi, yi, zi);
						Block saveBlock1 = this.getBlockAtLocation(coordinates);
						Block saveBlock2 = other.getBlockAtLocation(coordinates);

						this.placeBlock(saveBlock2);
						other.placeBlock(saveBlock1);
					}
				}
			}
		}

		return other;
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
	 * adds a new block
	 * @param coordinates new block coordinates
	 * @param type new block type
	 * @param orientation new block orientation
	 */
	public void addBlock(MinecraftCoordinates coordinates, BlockType type, Orientation orientation) {
		Block newBlock = new Block(coordinates, type, orientation);
		Block previousBlock = blocks.put(coordinates, newBlock);
		assert previousBlock == null : "There was already a block here: " + previousBlock + " at " + coordinates + ":" + emptySpace + ":" + blocks;
		boolean emptyRemoved = emptySpace.remove(coordinates);
		assert emptyRemoved : "Space was not in empty list "+ coordinates + ":" + emptySpace + ":" + blocks;
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}
	/**
	 * remove block from coordinates
	 * @param coordinates the coordinates of the block you want removed
	 */
	public void removeBlock(MinecraftCoordinates coordinates) {
		Block removed = blocks.remove(coordinates);
		assert removed != null;
		boolean added = emptySpace.add(coordinates);
		assert added;
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}

	/**
	 * swap blocks at two coordinates (throw IllegalStatException if either is not in blocks HashMap)
	 * @param coordinates1 block 1
	 * @param coordinates2 block 2
	 */
	public void swapBlocks(MinecraftCoordinates coordinates1, MinecraftCoordinates coordinates2) {
		if(!blocks.containsKey(coordinates1)) throw new IllegalStateException("Block 1 was not present in the blocks hash map");
		else if(!blocks.containsKey(coordinates2)) throw new IllegalStateException("Block 2 was not present in the blocks hash map");
		else {
			Block block1 = blocks.remove(coordinates1);
			Block block2 = blocks.remove(coordinates2);

			Block newBlock1 = new Block(coordinates2, BlockType.values()[block1.type()], Orientation.values()[block1.orientation()]);
			Block newBlock2 = new Block(coordinates1, BlockType.values()[block2.type()], Orientation.values()[block2.orientation()]);

			blocks.put(coordinates2, newBlock1);
			blocks.put(coordinates1, newBlock2);
		}
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}

	/**
	 * Changes a desired blocks type
	 * @param coordinates desired block location
	 * @param type desired type
	 */
	public void changeBlockType(MinecraftCoordinates coordinates, BlockType type) {
		if(!blocks.containsKey(coordinates)) throw new IllegalStateException("Block was not present in the blocks hash map");
		Block oldBlock = blocks.remove(coordinates);	
		blocks.put(coordinates, new Block(coordinates, type, Orientation.values()[oldBlock.orientation()]));
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}


	// Change type
	//	public void changeBlockType(MinecraftCoordinates coordinates, BlockType type) {
	//		if(!blocks.containsKey(coordinates)) throw new IllegalStateException("Block was not present in the blocks hash map");
	//		Block oldBlock = blocks.get(coordinates);
	//		int[] otherTypes = IntStream.range(0, MMNEAT.blockSet.getPossibleBlocks().length).filter(i -> i != oldBlock.type()).toArray();
	//		int newTypeIndex = RandomNumbers.randomElement(otherTypes);
	//		
	//		
	//		List<BlockType> types = Arrays.asList(MMNEAT.blockSet.getPossibleBlocks());
	//		
	//		//oldBlock.type()
	//		
	//		//types.remove()
	//	}
	/**
	 * change orientation (exception if not present)		
	 * @param coordinates desired block location
	 * @param orientation desired orientation
	 */

	public void changeBlockOrientation(MinecraftCoordinates coordinates, Orientation orientation) {
		if(!blocks.containsKey(coordinates)) throw new IllegalStateException("Block was not present in the blocks hash map");
		Block oldBlock = blocks.remove(coordinates);	
		blocks.put(coordinates, new Block(coordinates, BlockType.values()[oldBlock.type()], orientation));
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}

	/**
	 * placeBlock puts a block into the shape. It replaces what is already there. If there is a block at the location, 
	 * change the type and orientation to match the new one. 
	 * @param block
	 */
	public void placeBlock(Block block) {
		if(BlockType.values()[block.type()] != BlockType.AIR) {
			if(!blocks.containsKey(block.blockPosition())) {
				//blocks.replace(block.blockPosition(), blocks.get(block.blockPosition()), block);
				blocks.put(block.blockPosition(), block);
			}else {
				blocks.put(block.blockPosition(), block);
				emptySpace.remove(block.blockPosition());
			}
		}else {
			emptySpace.add(block.blockPosition());
		}
		assert !MinecraftUtilClass.containsBlockType(new ArrayList<>(blocks.values()), BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + emptySpace;
	}
	/**
	 * returns a Block from blocks if it exists, or creates an AIR block at those coordinates if the location is in the empty spaces.
	 * @param coord position
	 * @return the block at coord
	 */
	public Block getBlockAtLocation(MinecraftCoordinates coord) {
		Block checkBlock = blocks.get(coord);
		if(checkBlock != null) {
			return checkBlock;
		} else {
			return new Block(coord, BlockType.AIR, Orientation.NORTH);
		}	
	}

	@Override
	public long getId() {
		return id;
	}

	public static void main(String[] args) {
		IntStream stream = IntStream.range(0, 10).filter(i -> i != 3);

		System.out.println(Arrays.toString(stream.toArray()));
	}
}
