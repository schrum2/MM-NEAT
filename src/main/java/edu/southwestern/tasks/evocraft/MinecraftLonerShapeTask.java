package edu.southwestern.tasks.evocraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBinLabels;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
/**
 * MAPElites only works with LonerTasks because it is a steady-state algorithm.
 * MinecraftShapeTask is parallelized for evolving populations, but this
 * version of the task cannot take advantage of that (though there may be a work-around
 * using multiple emitters). However, some of the code from MinecraftShapeTask is
 * re-used.
 * 
 * @author schrum2
 *
 * @param <T>
 */
public class MinecraftLonerShapeTask<T> extends NoisyLonerTask<T> implements NetworkTask, BoundedTask {

	private MinecraftShapeTask<T> internalMinecraftShapeTask;
	private static boolean spawnShapesInWorld=false;
	private static ArrayList<MinecraftCoordinates> parallelShapeCorners;
	private BlockingQueue<MinecraftCoordinates> coordinateQueue;
	// Each diamond block refers to one shape, and the int is the associated 1D archive index
	private static Set<ControlBlocks> blocksToMonitor = new HashSet<>();
	private static Thread interactionThread;
	private static double highestFitness;
	private static Set<Pair<MinecraftCoordinates,Integer>> championCoords = new HashSet<>();
	private boolean running = true; // for loop that allows interacting with the archive
	private boolean interactiveLoopFinished = false;
	
	public MinecraftLonerShapeTask() 	{
		/**
		 * Default shape generation location is shifted away a bit so that the archive can populate the world starting around (0,5,0) 
		 */

		internalMinecraftShapeTask = new MinecraftShapeTask<T>() {
			public int getStartingX() { return Parameters.parameters.integerParameter("startX") - Parameters.parameters.integerParameter("minecraftXRange") - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER); }
			public int getStartingY() { return Parameters.parameters.integerParameter("startY");}
			public int getStartingZ() { return Parameters.parameters.integerParameter("startZ") - Parameters.parameters.integerParameter("minecraftZRange") - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength")*2, MinecraftClient.BUFFER); }
		};

		// Creates a new blocking queue to use with parallelism
		coordinateQueue = new ArrayBlockingQueue<>(Parameters.parameters.integerParameter("threads"));
		
		highestFitness=0;
		championCoords = new HashSet<Pair<MinecraftCoordinates,Integer>>();
		
		// Generates the corners for all of the shapes and then adds them into the blocking queue
		parallelShapeCorners = MinecraftShapeTask.getShapeCorners(Parameters.parameters.integerParameter("threads"),internalMinecraftShapeTask.getStartingX(),internalMinecraftShapeTask.getStartingY(),internalMinecraftShapeTask.getStartingZ(),MinecraftUtilClass.getRanges());
		for(MinecraftCoordinates corner : parallelShapeCorners) {
			try {
				coordinateQueue.put(corner);
			} catch (InterruptedException e) {
				System.out.println("Error with queue");
				e.printStackTrace();
				System.exit(1);
			}
		}

		if(Parameters.parameters.booleanParameter("interactWithMapElitesInWorld")) {
			blocksToMonitor = Collections.synchronizedSet(new HashSet<ControlBlocks>());
			interactionThread = new Thread() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					// Loop as long as evolution is running
					while(running) {
						ControlBlocks[] currentElements = new ControlBlocks[blocksToMonitor.size()];
						currentElements = blocksToMonitor.toArray(currentElements);

						// t1 is the diamond blocks, t2 is the emerald, and t3 is the 1D index
						for(ControlBlocks cb : currentElements) {
							// Initial check, reads in blocks once and compares from there
							ArrayList<Block> interactiveBlocks = MinecraftClient.getMinecraftClient().readCube(cb.getDiamond(),cb.getEmerald());
							ArrayList<Block> obsidianBlock = MinecraftClient.getMinecraftClient().readCube(cb.getObsidian());
							if(interactiveBlocks.get(0).type!=BlockType.DIAMOND_BLOCK || 
							   interactiveBlocks.get(interactiveBlocks.size()-1).type!=BlockType.EMERALD_BLOCK||
							   obsidianBlock.get(0).type!=BlockType.OBSIDIAN) { // If either diamond, emerald, or obsidian is different
								synchronized(blocksToMonitor) {
									
									// Recheck and verify interactiveBlocks and obsidianBlock
									interactiveBlocks = MinecraftClient.getMinecraftClient().readCube(cb.getDiamond(),cb.getEmerald());
									obsidianBlock = MinecraftClient.getMinecraftClient().readCube(cb.getObsidian());
									
									// Verify that it is actually missing
									if(interactiveBlocks.get(0).type!=BlockType.DIAMOND_BLOCK) {
										//System.out.println("Regenerate the elite by diamond block "+cb.getDiamond());
										// Gets score and uses it to place to clear and replace the shape
										Score<T> s = MMNEAT.getArchive().getElite(cb.getOneD());
										placeArchiveInWorld(s.individual, s.MAPElitesBehaviorMap(), MinecraftUtilClass.getRanges(),true);
									} else if(interactiveBlocks.get(interactiveBlocks.size()-1).type!=BlockType.EMERALD_BLOCK) {
										//System.out.println("Discard the elite by emerald block "+cb.getEmerald());
										// Uses score to clear the correct area
										Score<T> s = MMNEAT.getArchive().getElite(cb.getOneD());
										Pair<MinecraftCoordinates,MinecraftCoordinates> corners = configureStartPosition(MinecraftUtilClass.getRanges(), s.MAPElitesBehaviorMap());
										clearBlocksForShape(MinecraftUtilClass.getRanges(),corners.t1);
										
										//Removes from the archive, and then the set
										((MAPElites<T>) MMNEAT.ea).getArchive().removeElite(cb.getOneD());
										blocksToMonitor.remove(cb);
										
										// When emerald clears out champion, new champion needs to be found, gets the champ(s) in archive
										Set<Score<T>> champs = MMNEAT.getArchive().getChampions();
										List<Block> champions = new ArrayList<>();
										
										// For all champions, spawn a new gold block
										for(Score<T> champion : champs) {
											Pair<MinecraftCoordinates,MinecraftCoordinates> goldCorner = configureStartPosition(MinecraftUtilClass.getRanges(), champion.MAPElitesBehaviorMap());
											MinecraftCoordinates goldBlock = goldCorner.t2.add(new MinecraftCoordinates(-1, MinecraftUtilClass.getRanges().y(),-1));
											champions.add(new Block(goldBlock,BlockType.GOLD_BLOCK, Orientation.WEST));
											MinecraftClient.getMinecraftClient().spawnBlocks(champions);
											
											// Updates global vaiables
											MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
											int index1D = minecraftBinLabels.oneDimensionalIndex( champion.MAPElitesBehaviorMap());
											championCoords.add(new Pair<>(goldBlock,index1D));
											highestFitness = (double) champion.MAPElitesBehaviorMap().get("binScore");
										}
									} else if(obsidianBlock.get(0).type!=BlockType.OBSIDIAN) {
										//System.out.println("Spawn offspring from the elite by obsidian block "+cb.getObsidian());
										//System.out.println("1d:"+cb.getOneD()+"    Archive "+ Arrays.toString(MMNEAT.getArchive().getArchive().stream().map(s -> s == null ? "X" : ((Score) s).behaviorIndexScore() ).toArray()));
										
										((MAPElites<T>) MMNEAT.ea).newIndividual(cb.getOneD()); // Spawns new individual

										List<Block> interactive = new ArrayList<>(); //Spawning obsidian again
										interactive.add(new Block(cb.getObsidian(),BlockType.OBSIDIAN, Orientation.WEST));
										MinecraftClient.getMinecraftClient().spawnBlocks(interactive); 
									}
								}
							}
						}	
						// Sleep timer to not hog resources
						try {
							Thread.sleep(Parameters.parameters.integerParameter("interactiveSleepTimer"));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					// Might keep interactive loop active even after experiment completion so that blocks still work
					if(!Parameters.parameters.booleanParameter("interactWithMinecraftForever")) {
						interactiveLoopFinished = true;
					}
				}
			};
			interactionThread.start();
		} else {
			// Just say the thread is finished since it never starts in this case
			interactiveLoopFinished = true;
		}
	}
	
	/**
	 * Private class to keep track of interactive blocks
	 * maintains coordinates of interactive block's for a single shape using oneD / oneDimIndex / index in 1D archive 
	 * overrides equals for convenience
	 * 
	 * @commenter lewisj
	 * 
	 * diamond block: clears space and respawns shape
	 * emerald block: removes shape from archive
	 * obsidian block: creates new shapes based on the chosen shape
	 * gold block: indicates the highest fitness shapes denoted as champions (not a control block)
	 * 
	 */
	private static class ControlBlocks {

		private MinecraftCoordinates diamondBlock;
		private MinecraftCoordinates emeraldBlock;
		private MinecraftCoordinates obsidianBlock;
		private int oneDimIndex;
		
		// all control blocks information related to one shape based on oneD / index in 1D archive
		private ControlBlocks(MinecraftCoordinates diamond,MinecraftCoordinates emerald,MinecraftCoordinates obsidian,int oneD) {
			diamondBlock  = new MinecraftCoordinates(diamond);
			emeraldBlock  = new MinecraftCoordinates(emerald);
			obsidianBlock = new MinecraftCoordinates(obsidian);
			oneDimIndex   = oneD;
		}
		
		/**
		 * Gets MinecraftCoordinate of the diamond block
		 * @return the diamond block
		 */
		public MinecraftCoordinates getDiamond() {
			return diamondBlock;
		}

		/**
		 * Gets MinecraftCoordinate of the emerald block
		 * @return the emerald block
		 */
		public MinecraftCoordinates getEmerald() {
			return emeraldBlock;
		}
		
		/**
		 * Gets MinecraftCoordinate of the obsidian block
		 * @return the obsidian block
		 */
		public MinecraftCoordinates getObsidian() {
			return obsidianBlock;
		}
		
		/**
		 * Gets one dimensional index of the shape
		 * return int of the 1D index
		 */
		public Integer getOneD() {
			return oneDimIndex;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(diamondBlock, emeraldBlock, obsidianBlock, oneDimIndex);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ControlBlocks other = (ControlBlocks) obj;
			return Objects.equals(diamondBlock, other.diamondBlock) && Objects.equals(emeraldBlock, other.emeraldBlock)
					&& Objects.equals(obsidianBlock, other.obsidianBlock) && oneDimIndex == other.oneDimIndex;
		}
		
	}
	
	
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) {
		//System.out.println("    Archive "+ Arrays.toString(MMNEAT.getArchive().getArchive().stream().map(s -> s == null ? "X" : ((Score) s).behaviorIndexScore() ).toArray()));
		
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		// Corner to clear and then place is taken from the queue. If the queue is empty, it waits until something is added in
		MinecraftCoordinates corner=null;
		try {
			corner = coordinateQueue.take();
		} catch (InterruptedException e) {
			System.out.println("Error with queue");
			e.printStackTrace();
			System.exit(1);
		}
		// Clears specified space for new shape
		clearBlocksForShape(ranges, corner);
		
		MinecraftCoordinates middle = corner.add(MinecraftUtilClass.emptySpaceOffsets());
		// Evaluates the shape at the middle of the space defined by the corner, and then adds the corner back to the queue
		Score<T> score = internalMinecraftShapeTask.evaluateOneShape(individual, middle);
		try {
			if(Parameters.parameters.integerParameter("minecraftXMovementBetweenEvals") != 0 && Parameters.parameters.integerParameter("minecraftMaxXShift") != 0) {
				int shiftValue = (corner.x() + Parameters.parameters.integerParameter("minecraftXMovementBetweenEvals")) % Parameters.parameters.integerParameter("minecraftMaxXShift");
				coordinateQueue.put(new MinecraftCoordinates(shiftValue, corner.y(), corner.z()));
			}else {
				coordinateQueue.put(new MinecraftCoordinates(corner));
			}
		} catch (InterruptedException e) {
			System.out.println("Error with queue");
			e.printStackTrace();
			System.exit(1);
		}

		if(score.usesMAPElitesMapSpecification()) {
			// Minimum over HashMap values
			for(HashMap.Entry<String,Object> entry : score.MAPElitesBehaviorMap().entrySet()) {
				if(behaviorCharacteristics.containsKey(entry.getKey()) && entry.getValue() instanceof Double) {
					double previous = ((Double) behaviorCharacteristics.get(entry.getKey())).doubleValue();
					double current = ((Double) entry.getValue()).doubleValue();
					//double avg = previous + (current - previous) / (num + 1); // Incremental average calculation 
					//behaviorCharacteristics.put(entry.getKey(), avg);
					double min = Math.min(previous,current); // Minimum: has to really succeed as flying machine, at least twice
					behaviorCharacteristics.put(entry.getKey(), min);
				} else { // Overwrite, fresh start
					assert num == 0 || !(entry.getValue() instanceof Double) : ""+behaviorCharacteristics;
					behaviorCharacteristics.put(entry.getKey(), entry.getValue());
				}
			}
		}
		// Checks command line param on whether or not to generate shapes in archive
		if(Parameters.parameters.booleanParameter("minecraftContainsWholeMAPElitesArchive")) { // || CommonConstants.netio) { // Why netio?
			// Places the shapes in the world based on their position
			placeArchiveInWorld(individual, behaviorCharacteristics, ranges);	
		}
		// This result will be ignored when using MAP Elites	
		return new Pair<>(score.scores, score.otherStats);
	}

	/**
	 * Generates the starting coordinates for spawning in the archived shapes. Then also checks if 
	 * the shapes should be added based on their score
	 * 
	 * @param individual specified genome of shape
	 * @param behaviorCharacteristics dictionary of values used for placing at the right index
	 * @param ranges specified range of each shape
	 */
	public static <T> void placeArchiveInWorld(Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics, MinecraftCoordinates ranges) {
		placeArchiveInWorld(individual, behaviorCharacteristics, ranges, false); // By default, do not force placement of new shape
	}

	/**
	 * generates coordinates for new archived shapes and updates the highest fitness score and gold blocks if necessary
	 * also forcibly clears and replaces a shape
	 * -Joanna
	 * @param <T>
	 * @param individual specified genome of shape
	 * @param behaviorCharacteristics dictionary of values used for placing at the right index
	 * @param ranges specified range of each shape
	 * @param forcePlacement forces placement on the shape by clearing and recreating shape
	 */
	@SuppressWarnings("unchecked")
	public static <T> void placeArchiveInWorld(Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics, MinecraftCoordinates ranges, boolean forcePlacement) {
		MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
		// Don't try to place shapes that have no place to go
		if(!minecraftBinLabels.discard(behaviorCharacteristics)) {
			int index1D = minecraftBinLabels.oneDimensionalIndex(behaviorCharacteristics);
			// Gets the bin scores to compare them
			double scoreOfCurrentElite = (double) behaviorCharacteristics.get("binScore");
			assert index1D >= 0 : individual.getId() + ":" + behaviorCharacteristics;
			//System.out.println(index1D+"          "+((MAPElites<T>) MMNEAT.ea).getArchive().getBinMapping().binLabels().size());
			assert index1D < ((MAPElites<T>) MMNEAT.ea).getArchive().getBinMapping().binLabels().size() : individual.getId() + ":" + behaviorCharacteristics;
			double scoreOfPreviousElite = 0;
			scoreOfPreviousElite = ((MAPElites<T>) MMNEAT.ea).getArchive().getBinScore(index1D);

			// If the new shape is better than the previous, it gets replaced
			if(forcePlacement || scoreOfCurrentElite > scoreOfPreviousElite) {
				clearAndSpawnShape(individual, behaviorCharacteristics, ranges, index1D, scoreOfCurrentElite);
				//System.out.println("Current:"+scoreOfCurrentElite+"  Highest:"+highestFitness);
			}
			
			// If the shape has a fitness greater than or equal to the previous champion's, then place a gold block near it
			if(Parameters.parameters.booleanParameter("minecraftContainsWholeMAPElitesArchive") && scoreOfCurrentElite>=highestFitness) {
				synchronized(championCoords) {
					// Sets up coordinates for the new block, and the array list for spawning
					Pair<MinecraftCoordinates,MinecraftCoordinates> corners = configureStartPosition(ranges, behaviorCharacteristics);
					MinecraftCoordinates goldBlock = corners.t2.add(new MinecraftCoordinates(-1, ranges.y(),-1));
					List<Block> champions = new ArrayList<>();

					// If the shape has the same fitness, add it to the set and spawn all of them again
					if(scoreOfCurrentElite==highestFitness) {
						championCoords.add(new Pair<>(goldBlock,index1D)); // Add to global set

						// Sets up the for each loop, loops through all champions and adds gold block at correct place
						Pair<MinecraftCoordinates,Integer>[] currentElements = new Pair[championCoords.size()];
						currentElements = championCoords.toArray(currentElements);
						for(Pair<MinecraftCoordinates,Integer> pair : currentElements) {
							champions.add(new Block(pair.t1,BlockType.GOLD_BLOCK, Orientation.WEST));	//orientation west is arbitrary
						}
					}

					// If the shape has a greater fitness, clears all gold blocks and adds a new one
					else if(scoreOfCurrentElite>highestFitness) {
						// For loop replaces all gold blocks in overworld with air
						Pair<MinecraftCoordinates,Integer>[] currentElements = new Pair[championCoords.size()];
						currentElements = championCoords.toArray(currentElements);
						for(Pair<MinecraftCoordinates,Integer> pair : currentElements) {
							champions.add(new Block(pair.t1,BlockType.AIR, Orientation.WEST));	//orientation west is arbitrary
						}
						// Clears the global set and adds the new pair in
						championCoords.clear();
						championCoords.add(new Pair<>(goldBlock,index1D));

						// Spawns blocks then clears the list, as there are issues when there are two blocks spawned at the same place
						MinecraftClient.getMinecraftClient().spawnBlocks(champions);
						champions.clear();
						champions.add(new Block(goldBlock,BlockType.GOLD_BLOCK, Orientation.WEST)); // Adds new gold block	//orientation west is arbitrary
					}
					MinecraftClient.getMinecraftClient().spawnBlocks(champions); // Spawns shapes in the game
					//System.out.println("Current:"+scoreOfCurrentElite+"  Highest:"+highestFitness); // For debugging
					if(spawnShapesInWorld) highestFitness = scoreOfCurrentElite; // increases the best fitness if needed
				}
			}
		}
	}

	/**
	 * Clear a space for the shape and then generate it. Also, save block list to archive.
	 * 
	 * @param <T>
	 * @param individual specified genome of shape
	 * @param behaviorCharacteristics dictionary of values used for placing at the right index
	 * @param ranges bounds of generated shape
	 * @param index1D index in 1D archive
	 * @param scoreOfCurrentElite quality score
	 */
	@SuppressWarnings("unchecked")
	public static <T> void clearAndSpawnShape(Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics,
			MinecraftCoordinates ranges, int index1D, double scoreOfCurrentElite) {
		
		List<Block> blocks = null;
		Pair<MinecraftCoordinates,MinecraftCoordinates> corners = configureStartPosition(ranges, behaviorCharacteristics);
		synchronized(blocksToMonitor) {

			// Clears old shape if there was one
			Pair<MinecraftCoordinates,MinecraftCoordinates> cleared = clearBlocksForShape(ranges, corners.t1);
			assert cleared.t1.equals(corners.t1) : "Cleared space does not start at right location: "+cleared.t1+" vs "+corners.t1;
			// Could do more checking here

			// Generates the new shape
			blocks = MMNEAT.shapeGenerator.generateShape(individual, corners.t2, MMNEAT.blockSet);

			// Spawning shapes is disabled during initialization
			if(spawnShapesInWorld) {
				MinecraftClient.getMinecraftClient().spawnBlocks(blocks); // Spawns shapes
				if(Parameters.parameters.booleanParameter("interactWithMapElitesInWorld")) {
					List<Block> interactive = new ArrayList<>(); //Spawning control blocks
					MinecraftCoordinates diamondBlock = corners.t2.sub(new MinecraftCoordinates(1,1,1));
					interactive.add(new Block(diamondBlock,BlockType.DIAMOND_BLOCK, Orientation.WEST));
					MinecraftCoordinates emeraldBlock = corners.t2.add(new MinecraftCoordinates(MinecraftUtilClass.getRanges().x(),-1,-1));
					interactive.add(new Block(emeraldBlock,BlockType.EMERALD_BLOCK, Orientation.WEST));
					MinecraftCoordinates obsidianBlock = corners.t2.sub(new MinecraftCoordinates(1,1,-MinecraftUtilClass.getRanges().z()));
					interactive.add(new Block(obsidianBlock,BlockType.OBSIDIAN, Orientation.WEST));
					blocksToMonitor.add(new ControlBlocks(diamondBlock,emeraldBlock,obsidianBlock,index1D)); // Adds to the set that loops to check them
					MinecraftClient.getMinecraftClient().spawnBlocks(interactive);
				}
			}
		}

		saveBlockListToMAPElitesArchive(individual.getId(), index1D, scoreOfCurrentElite, blocks);
	}

	/**
	 * TODO: deals with saving files / should this also be generalized and moved?
	 * Save a test list of the blocks in the generated shape to the archive directory for MAP Elites
	 * 
	 * @param <T>
	 * @param genomeId ID of genome being written
	 * @param dim1D location of genome in 1D archive / index in 1D archive
	 * @param scoreOfCurrentElite genome's score
	 * @param blocks blocks of the shape generated by genome
	 */
	public static <T> void saveBlockListToMAPElitesArchive(long genomeId, int dim1D, double scoreOfCurrentElite, List<Block> blocks) {
		if(CommonConstants.netio) {
			MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
			@SuppressWarnings("unchecked")
			Archive<T> archive = MMNEAT.getArchive();
			String fileName = String.format("%7.5f", scoreOfCurrentElite) + "_" + genomeId + ".txt";
			String binPath = archive.getArchiveDirectory() + File.separator + minecraftBinLabels.binLabels().get(dim1D);
			MinecraftUtilClass.writeBlockListFile(blocks, binPath, fileName);
		}
	}

	/**
	 * Figure out the corner in the world to place the shape at in the representation of the archive.
	 * Both the corner of the large space that the shape reserves, and the actual corner of where it will be generated.
	 * 
	 * @param ranges x/y/z sizes taken up by each shape
	 * @param behaviorCharacteristics characteristics of the shape that help determine its location in the archive
	 * @return Pair of the corner of the space to clear followed by the corner within that space to place the shape
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> configureStartPosition(MinecraftCoordinates ranges, HashMap<String,Object> behaviorCharacteristics) {
		MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
		int[] multiDimIndex = minecraftBinLabels.multiDimensionalIndices(behaviorCharacteristics);
		int dim1D = minecraftBinLabels.oneDimensionalIndex(behaviorCharacteristics);
		return configureStartPosition(ranges, multiDimIndex, dim1D);
	}		

	/**
	 * Figure out the corner in the world to place the shape at in the representation of the archive.
	 * Both the corner of the large space that the shape reserves, and the actual corner of where it will be generated.

	 * @param ranges x/y/z sizes taken up by each shape
	 * @param multiDimIndex Multi-dimensional location in archive
	 * @param dim1D one-dimensional location in archive
	 * @return Pair of the corner of the space to clear followed by the corner within that space to place the shape
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> configureStartPosition(MinecraftCoordinates ranges, int[] multiDimIndex, int dim1D) {
		MinecraftCoordinates startPosition;
		MinecraftCoordinates offset;
		int spaceBetween = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		// Location in multi-dimensional archive
		if(multiDimIndex.length==1 || multiDimIndex.length > 3 || Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")) {
			// Derive 1D location from multi-dimensional location
			startPosition = new MinecraftCoordinates(dim1D*(spaceBetween+ranges.x()),MinecraftClient.GROUND_LEVEL+1,0);				
		} else if(multiDimIndex.length==2){
			// Ground level fixed, but expand second coordinate in z dimension
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*(spaceBetween+ranges.x()),MinecraftClient.GROUND_LEVEL+1,multiDimIndex[1]*(spaceBetween+ranges.z()));
		} else if(multiDimIndex.length==3) {
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*(spaceBetween+ranges.x()),MinecraftClient.GROUND_LEVEL+1+multiDimIndex[1]*(spaceBetween+ranges.y()),multiDimIndex[2]*(spaceBetween+ranges.z()));
		} else {
			throw new IllegalArgumentException("This should be impossible to reach: "+Arrays.toString(multiDimIndex));
		}
		offset = MinecraftUtilClass.emptySpaceOffsets();
		return new Pair<MinecraftCoordinates,MinecraftCoordinates>(startPosition, startPosition.add(offset));
	}

	/**
	 * Clears the area for a specified shape 
	 * 
	 * @param ranges specified range of each shape
	 * @param startPosition Starting indices of a specific shape
	 * @return start and end coordinates of area that was cleared
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> clearBlocksForShape(MinecraftCoordinates ranges, MinecraftCoordinates startPosition) {
		MinecraftCoordinates clearEnd = startPosition.add(MinecraftUtilClass.reservedSpace());
		// Sub 1 to not delete interactive blocks
		clearEnd = clearEnd.sub(new MinecraftCoordinates(1));
		if(MinecraftClient.clientRunning()) {
			MinecraftClient.getMinecraftClient().clearCube(startPosition, clearEnd);
		} 
		return new Pair<MinecraftCoordinates,MinecraftCoordinates>(startPosition, clearEnd);
	}

	/**
	 * Places fences around all specified shapes
	 * 
	 * @param ranges Size of blocks, used to generate fences
	 * @param fencePlacePosition Position where fences are added in from
	 */
	public static void placeFencesAroundArchive(MinecraftCoordinates ranges, MinecraftCoordinates fencePlacePosition) {
		List<Block> fences = new ArrayList<>();
		// Places first fences based on starting point
		fencePlacePosition=fencePlacePosition.sub(new MinecraftCoordinates(1,fencePlacePosition.y()-MinecraftClient.GROUND_LEVEL,1));

		// Places all fences in the x direction
		for(int i =0;i<=ranges.x()+1;i++) {
			fences.add(new Block(fencePlacePosition.x()+i,fencePlacePosition.y(),fencePlacePosition.z(),BlockType.DARK_OAK_FENCE, Orientation.WEST));	//orientation and block type arbitrary
			fences.add(new Block(fencePlacePosition.x()+i,fencePlacePosition.y(),fencePlacePosition.z()+ranges.z()+1,BlockType.DARK_OAK_FENCE, Orientation.WEST));	//orientation and block type arbitrary

		}
		// Places all fences in the z direction
		for(int i =0;i<=ranges.z();i++) {
			fences.add(new Block(fencePlacePosition.x(),fencePlacePosition.y(),fencePlacePosition.z()+i,BlockType.DARK_OAK_FENCE, Orientation.WEST));	//orientation and block type arbitrary
			fences.add(new Block(fencePlacePosition.x()+ranges.x()+1,fencePlacePosition.y(),fencePlacePosition.z()+i,BlockType.DARK_OAK_FENCE, Orientation.WEST));	//orientation and block type arbitrary
		}
		MinecraftClient.getMinecraftClient().spawnBlocks(fences); // Spawns them in game
	}
	/**
	 * Sets spawnShapesInWorld to true
	 */
	public static void spawnShapesInWorldTrue() {
		spawnShapesInWorld=true;
	}

	/**
	 * Sets spawnShapesInWorld to false
	 */
	public static void spawnShapesInWorldFalse() {
		spawnShapesInWorld=false;
	}

	@Override
	public int numObjectives() {
		return internalMinecraftShapeTask.numObjectives();
	}

	@Override
	public double getTimeStamp() {
		return 0;
	}

	@Override
	public void postConstructionInitialization() {
		internalMinecraftShapeTask.postConstructionInitialization();
	}

	@Override
	public String[] sensorLabels() {
		return internalMinecraftShapeTask.sensorLabels();
	}

	@Override
	public String[] outputLabels() {
		return internalMinecraftShapeTask.outputLabels();
	}
	
	@Override
	public double[] minScores() {
		return this.internalMinecraftShapeTask.minScores();
	}

	@Override
	public void finalCleanup() {
		if(!Parameters.parameters.booleanParameter("interactWithMinecraftForever")) {
			running = false; // Stop the interactive loop
		}
		
		if(Parameters.parameters.booleanParameter("minecraftChangeCenterOfMassFitness") && MMNEAT.usingDiversityBinningScheme && CommonConstants.netio) {
			System.out.println("Write block lists for all flying elites to finalFlyingMachines");
			
			//final flying machines directory is created
			String flyingDir = FileUtilities.getSaveDirectory() + "/finalFlyingMachines";
			File dir = new File(flyingDir);
			// Create dir
			if (!dir.exists()) {
				dir.mkdir();
			}
			@SuppressWarnings("unchecked")
			Archive<T> archive = MMNEAT.getArchive();
			Vector<Score<T>> archiveVector = archive.getArchive();
			MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
			for(int i = 0; i < archiveVector.size(); i++) {
				Score<T> score = archiveVector.get(i);
				//if there is a fitness score related to this bin (ie. there exists a shape)
				if(score != null) {
					double fitness = score.behaviorIndexScore();
					//TODO: this deals with saving shapes
					if(this.internalMinecraftShapeTask.certainFlying(fitness)) {
						@SuppressWarnings("unchecked")
						List<Block> blocks = MMNEAT.shapeGenerator.generateShape(score.individual, MinecraftClient.POST_EVALUATION_SHAPE_CORNER, MMNEAT.blockSet);
						String label = minecraftBinLabels.binLabels().get(i);
						MinecraftUtilClass.writeBlockListFile(blocks, flyingDir + File.separator + label+"ID"+score.individual.getId(), "FITNESS"+fitness+".txt");			
					}
				}
			}			
		}
		
		while(!interactiveLoopFinished) {
			// Let interactive loop finish
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		internalMinecraftShapeTask.finalCleanup();
	}

	
	public static void main(String[] args) {
		int seed = 14;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:10", "maxGens:100000",
					"base:minecraft", "log:Minecraft-MAPElitesWHDSimple", "saveTo:MAPElitesWHDSimple",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false", "displayDiagonally:true",
					"io:true", "netio:true",
					"interactWithMapElitesInWorld:true",
					//"io:false", "netio:false", 
					"mating:true", "fs:false",
					//"startX:-10", "startY:5", "startZ:10",
					"minecraftClearDimension:130", "minecraftClearSleepTimer:200",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet",
					//"minecraftTypeCountFitness:true",
					//"minecraftFakeTestFitness:true",
					//"minecraftDiversityBlockFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true", 
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100", 
					//"extraSpaceBetweenMinecraftShapes:0",
					"minecraftSkipInitialClear:false",
					"spaceBetweenMinecraftShapes:5","parallelMAPElitesInitialize:false",
					"minecraftXRange:2","minecraftYRange:2","minecraftZRange:2",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:false", "netChangeActivationRate:0.3", "cleanFrequency:-1",
					"recurrency:false", "saveAllChampions:true", "cleanOldNetworks:false",
					"includeFullSigmoidFunction:true", "includeFullGaussFunction:true", "includeCosineFunction:true", 
					"includeGaussFunction:false", "includeIdFunction:true", "includeTriangleWaveFunction:false", 
					"includeSquareWaveFunction:false", "includeFullSawtoothFunction:false", "includeSigmoidFunction:false", 
					"includeAbsValFunction:false", "includeSawtoothFunction:false"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double[] getUpperBounds() {
		return internalMinecraftShapeTask.getUpperBounds();
	}

	@Override
	public double[] getLowerBounds() {
		return internalMinecraftShapeTask.getLowerBounds();
	}
}
