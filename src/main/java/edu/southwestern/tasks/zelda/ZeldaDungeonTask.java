package edu.southwestern.tasks.zelda;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.MultiObjectiveScore;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.tasks.gvgai.zelda.study.DungeonNovelty;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;

public abstract class ZeldaDungeonTask<T> extends LonerTask<T> {

	private int numObjectives;
	
	public ZeldaDungeonTask() {
		// Objective functions
		numObjectives = 0;
		if(Parameters.parameters.booleanParameter("zeldaDungeonDistanceFitness")) {
			MMNEAT.registerFitnessFunction("DistanceToTriforce"); //distance to end
			numObjectives++;
		}
		if(Parameters.parameters.booleanParameter("zeldaDungeonFewRoomFitness")) {
			MMNEAT.registerFitnessFunction("NegativeRooms"); // Fewer rooms means more interesting shapes
			numObjectives++;
		}
		if(Parameters.parameters.booleanParameter("zeldaPercentDungeonTraversedRoomFitness")) {
			MMNEAT.registerFitnessFunction("PercentRoomsTraversed"); // Avoid superfluous rooms
			numObjectives++;
		}
		if(Parameters.parameters.booleanParameter("zeldaDungeonTraversedRoomFitness")) {
			MMNEAT.registerFitnessFunction("NumRoomsTraversed"); // Visit as many rooms as possible
			numObjectives++;
		}
		
		if(Parameters.parameters.booleanParameter("zeldaDungeonRandomFitness")) {
			MMNEAT.registerFitnessFunction("RandomFitness"); //assigns a random fitness to the room
			numObjectives++;
		}
		if(Parameters.parameters.booleanParameter("zeldaDungeonBackTrackRoomFitness")) {
			MMNEAT.registerFitnessFunction("NumBackTrackRooms"); //backtrack as many rooms as possible
			numObjectives++;
		}
		if(Parameters.parameters.booleanParameter("zeldaDungeonDistinctRoomFitness")) {
			MMNEAT.registerFitnessFunction("NumDistinctRooms"); //Make as many rooms as possible diverse
			numObjectives++;
		}
		//zeldaDungeonDiverseRoomFitness
		// Additional information tracked about each dungeon
		MMNEAT.registerFitnessFunction("NumRooms",false);
		MMNEAT.registerFitnessFunction("NumRoomsTraversed",false);
		MMNEAT.registerFitnessFunction("NumRoomsReachable",false);
		MMNEAT.registerFitnessFunction("NumSearchStatesVisited",false);
		MMNEAT.registerFitnessFunction("NumBackTrackRooms",false);
		MMNEAT.registerFitnessFunction("NumDistinctRooms",false);


		// More?
	}

	@Override
	/**
	 * returns the number of objectives
	 * @return numObjectives - the number of objectives
	 */
	public int numObjectives() {
		return numObjectives;  
	}
	/**
	 * returns the number of "other scores"
	 * @return 4, the other scores that do not affect selection
	 */
	public int numOtherScores() {
		return 4;
	}

	@Override
	/**
	 * returns the time stamp
	 * This method is not used
	 * @return 0 the time stamp that is not in use
	 */
	public double getTimeStamp() {
		return 0; // Not used
	}

	public abstract Dungeon getZeldaDungeonFromGenotype(Genotype<T> individual); //gets the dungeon from the genotype

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * 
	 * Determines the score for the dungeon, saves the file
	 * @param individual the genotype containing the dungeon
	 * @return score - the MultiObjectiveScore<T>(individual, scores, behaviorVector, other) of the dungeon
	 */
	public Score<T> evaluate(Genotype<T> individual) {
		// Defines the floor space (excluding walls)
		
		// TODO: Move/rename to util class. Mention ZELDA_FLOOR_SPACE
		//final int ZELDA_FLOOR_SPACE_ROWS = 7; // Number of rows to look through
		//final int ZELDA_FLOOR_SPACE_COLUMNS = 12; // Number of columns to look through

		ArrayList<Double> behaviorVector = null; // Filled in later
		Dungeon dungeon = getZeldaDungeonFromGenotype(individual);
		int distanceToTriforce = -100; // Very bad fitness if level is not beatable 
		int numRooms = 0; //number of rooms
		int searchStatesVisited = 0; //number of search states visited
		int numRoomsTraversed = 0; //the number of rooms traversed
		int numBackTrackRooms = 0; //the number of rooms traversed twice
		int numDistinctRooms = 0;
		int waterTileCount = 0; //the number of water tiles
		int wallTileCount = 0; //the number of wall tiles
		int numRoomsReachable = 0; //the number of reachable rooms
		int[] mapElitesBinIndices = null;
		double mapElitesBinScore = Double.NEGATIVE_INFINITY;
		HashSet<ZeldaState> solutionPath = null; 
		HashSet<ZeldaState> mostRecentVisited = null;
		
		if(dungeon != null) {
			try {
				// Determine which rooms actually connect with doors (but ignores blockage from inner walls
				dungeon.markReachableRooms();
				// Upper left corner of floor area (ignore surrounding walls)
				final Point START = new Point(2, 2);
				// Count occurrence of water and wall tiles in the dungeons for MAP Elites binning
				//ArrayList<ArrayList<Integer>> compareRooms = new ArrayList<ArrayList<Integer>>();
				HashSet<ArrayList<ArrayList<Integer>>> k = new HashSet<ArrayList<ArrayList<Integer>>>();
				for(Node room: dungeon.getLevels().values()) {
					if(room.reachable) { // Only include reachable rooms in feature calculation
						numRoomsReachable++;
						for(int x = START.x; x < START.x+ZeldaLevelUtil.ZELDA_FLOOR_SPACE_ROWS; x++) {
							for(int y = START.y; y < START.y+ZeldaLevelUtil.ZELDA_FLOOR_SPACE_COLUMNS; y++) {
								Tile tile = room.level.rougeTiles[y][x];
								if(tile.equals(Tile.WALL)) { //if it's a wall tile, increase wallTileCount
									wallTileCount++;
								} else if(tile.equals(Tile.WATER)) { //if it's a water tile, increase waterTileCount
									waterTileCount++;
								} 
							}
						}
					}
				}
				numDistinctRooms = ZeldaLevelUtil.countDiscreteRooms(dungeon, numRoomsReachable, START, k);
				//System.out.println("Waiting for ENTER: ");
				//MiscUtil.waitForReadStringAndEnterKeyPress();
				numRooms = dungeon.getLevels().size();
				// A* should already have been run during creation to assure beat-ability, but it is run again here to get the action sequence.
				ArrayList<GridAction> actionSequence; 
				//actionSequence = DungeonUtil.makeDungeonPlayable(dungeon);
				Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
				ZeldaState startState = new ZeldaState(5, 5, 0, dungeon);
				try {
					actionSequence = ((AStarSearch<GridAction, ZeldaState>) search).search(startState, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
					if(actionSequence != null) {
						distanceToTriforce = actionSequence.size();
						// Get states in the solution to plot a path
						solutionPath = new HashSet<>();
						//backTrackSolutionPath = new HashSet<>();
						ZeldaState currentState = startState;
						solutionPath.add(currentState);
						
						HashSet<Pair<Integer,Integer>> exitedRoomCoordinates = new HashSet<>();
						Pair<Integer, Integer> prevRoom = null;
						for(GridAction a : actionSequence) {
							currentState = (ZeldaState) currentState.getSuccessor(a);
							solutionPath.add(currentState);
							Pair<Integer,Integer> newRoom = new Pair<>(currentState.dX,currentState.dY);
							if(prevRoom!=null&&!prevRoom.equals(newRoom)){ //only ever true when leaving/entering a room
								exitedRoomCoordinates.add(prevRoom); //add the exited room
								if(exitedRoomCoordinates.contains(newRoom)) { //check if the room you just entered has already been visited
									numBackTrackRooms++;
								}
							}
							
							prevRoom = newRoom;
							
						}

						HashSet<Pair<Integer,Integer>> visitedRoomCoordinates = new HashSet<>();
						//sets a pair of coordinates for each room found 
						for(ZeldaState zs: solutionPath) {							
							// Set does not allow duplicates: one Pair per room
							visitedRoomCoordinates.add(new Pair<>(zs.dX,zs.dY)); 
							

							
						}
						numRoomsTraversed = visitedRoomCoordinates.size(); //sets the number of rooms traversed
						
						

//						System.out.println("numRoomsTraversed: "+numRoomsTraversed);
//						System.out.println("numBackTrackTraversed: "+numBackTrackRooms);
//						System.out.println("number of exited rooms "+exitedRoomCoordinates.size());

						//MiscUtil.waitForReadStringAndEnterKeyPress();
						
					}
				}catch(IllegalStateException e) {
					throw e; // Pass on exception, but the finally assures we save states when things go wrong.
				} finally {
					mostRecentVisited = ((AStarSearch<GridAction, ZeldaState>) search).getVisited();
					searchStatesVisited = mostRecentVisited.size();
				}

				if(CommonConstants.watch) {
					//prints data about the dungeon to the console
					System.out.println("Distance to Triforce: "+distanceToTriforce);
					System.out.println("Number of rooms: "+numRooms);
					System.out.println("Number of reachable rooms: "+numRoomsReachable);
					System.out.println("Number of rooms traversed: "+numRoomsTraversed);
					System.out.println("Number of states visited: "+searchStatesVisited);
					System.out.println("Number of backtracked rooms: "+numBackTrackRooms);
					System.out.println("Number of distinct rooms: "+numDistinctRooms);


					// View whole dungeon layout
					BufferedImage image = DungeonUtil.viewDungeon(dungeon, mostRecentVisited, solutionPath);
					String saveDir = FileUtilities.getSaveDirectory(); //save directory
					int currentGen = MMNEAT.ea instanceof GenerationalEA ? ((GenerationalEA) MMNEAT.ea).currentGeneration() : -1;
					//saves image
					if(Parameters.parameters.booleanParameter("io")) GraphicsUtil.saveImage(image, saveDir + File.separator + (currentGen == 0 ? "initial" : "gen"+ currentGen) + File.separator + "Dungeon"+individual.getId()+".png");
					//prompts user to play or continue
					System.out.println("Enter 'P' to play, or just press Enter to continue");
					String input = MiscUtil.waitForReadStringAndEnterKeyPress();
					System.out.println("Entered \""+input+"\"");
					//if the user entered P or p, then run
					if(input.toLowerCase().equals("p")) {
						new Thread() {
							@Override
							public void run() {
								// Repeat dungeon generation to remove visited marks
								Dungeon dungeon = getZeldaDungeonFromGenotype(individual);
								RougelikeApp.startDungeon(dungeon);
							}
						}.start();
						System.out.println("Press enter");
						MiscUtil.waitForReadStringAndEnterKeyPress();
					}
				}

			} catch(IllegalStateException e) {
				// Sometimes this exception occurs from A*. Not sure why, but we can take this to mean the level has a problem and deserves bad fitness.
				System.out.println("A* failure");
			} finally {

				// Need to assign MAP Elites bins no matter what
				
				// Could conceivably also be used for behavioral diversity instead of map elites, but this would be a weird behavior vector from a BD perspective
				if(MMNEAT.ea instanceof MAPElites) {
					// Hard coding bin score to be the percentage of reachable rooms traversed. May want to change this later.
					mapElitesBinScore = (numRoomsTraversed*1.0)/numRoomsReachable;
					
					if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof ZeldaMAPElitesWallWaterRoomsBinLabels) {					
						double wallTilePercentage = (wallTileCount*1.0)/(numRoomsReachable*ZeldaLevelUtil.ZELDA_FLOOR_SPACE_ROWS*ZeldaLevelUtil.ZELDA_FLOOR_SPACE_COLUMNS);
						double waterTilePercentage = (waterTileCount*1.0)/(numRoomsReachable*ZeldaLevelUtil.ZELDA_FLOOR_SPACE_ROWS*ZeldaLevelUtil.ZELDA_FLOOR_SPACE_COLUMNS);

						int wallTileIndex = (int)(wallTilePercentage*ZeldaMAPElitesWallWaterRoomsBinLabels.TILE_GROUPS); // [0,10), [10,20), [20,30), ... , [80,90), [90,100] <-- Assume 100% of one tile type is impossible
						int waterTileIndex = (int)(waterTilePercentage*ZeldaMAPElitesWallWaterRoomsBinLabels.TILE_GROUPS); // [0,10), [10,20), [20,30), ... , [80,90), [90,100] <-- Assume 100% of one tile type is impossible

						mapElitesBinIndices = new int[] {wallTileIndex,waterTileIndex,numRoomsReachable};
						System.out.println("["+wallTileIndex+"]["+waterTileIndex+"]["+numRoomsReachable+"] = "+mapElitesBinScore+" ("+numRoomsTraversed+" rooms)");
					} else if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof ZeldaMAPElitesDistinctAndBackTrackRoomsBinLabels) { //alternate binning scheme
						mapElitesBinIndices = new int[] {numDistinctRooms,numBackTrackRooms,numRoomsReachable};
						System.out.println("["+numDistinctRooms+"]["+numBackTrackRooms+"]["+numRoomsReachable+"] = "+mapElitesBinScore+" ("+numRoomsTraversed+" rooms)");
					}else if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof ZeldaMAPElitesNoveltyAndBackTrackRoomBinLabels) { //alternate binning scheme
						double dungeonNovelty = DungeonNovelty.averageDungeonNovelty(dungeon);
						int noveltyIndex =(int)((dungeonNovelty*ZeldaMAPElitesNoveltyAndBackTrackRoomBinLabels.NUM_NOVELTY_BINS)/ZeldaMAPElitesNoveltyAndBackTrackRoomBinLabels.MAX_EXPECTED_NOVELTY); //.7 is the guessed max novelty, magic number
						noveltyIndex = Math.min(noveltyIndex, ZeldaMAPElitesNoveltyAndBackTrackRoomBinLabels.NUM_NOVELTY_BINS - 1);
						mapElitesBinIndices = new int[] {noveltyIndex, numBackTrackRooms, numRoomsReachable};
						System.out.println("["+noveltyIndex+"]["+numBackTrackRooms+"]["+numRoomsReachable+"] = "+mapElitesBinScore+" ("+numRoomsTraversed+" rooms)");						
					} else {
						throw new RuntimeException("A Valid Binning Scheme For Zelda Was Not Specified");
					}

					// Saving map elites bin images
					if(CommonConstants.netio) {
						System.out.println("Save archive images");
						Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
						List<String> binLabels = archive.getBinMapping().binLabels();

						// Index in flattened bin array
						Score<T> elite = archive.getElite(mapElitesBinIndices);
						// If the bin is empty, or the candidate is better than the elite for that bin's score
						if(elite == null || mapElitesBinScore > elite.behaviorIndexScore()) {
							// CHANGE!
							BufferedImage imagePath = DungeonUtil.imageOfDungeon(dungeon, mostRecentVisited, solutionPath);
							BufferedImage imagePlain = DungeonUtil.imageOfDungeon(dungeon, null, null);
							//sets the fileName, binPath, and fullName
							String fileName = String.format("%7.5f", mapElitesBinScore) +"-"+ individual.getId() + ".png";
							if(individual instanceof CPPNOrDirectToGANGenotype) {
								CPPNOrDirectToGANGenotype temp = (CPPNOrDirectToGANGenotype) individual;
								if(temp.getFirstForm()) fileName = "CPPN-" + fileName;
								else fileName = "Direct-" + fileName;
							}
							String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(archive.getBinMapping().oneDimensionalIndex(mapElitesBinIndices));
							String fullName = binPath + "-" + fileName;
							System.out.println(fullName);
							GraphicsUtil.saveImage(imagePlain, fullName);	
							fileName = String.format("%7.5f", mapElitesBinScore) +"-"+ individual.getId() + "-solution.png";
							fullName = binPath + "-" + fileName;
							System.out.println(fullName);
							GraphicsUtil.saveImage(imagePath, fullName);	
						}
					}
				}
			}
		}

		ArrayList<Double> fitness = new ArrayList<Double>(5);
		//if the score is enough to affect selection (not an "other score," then add them to fitness
		if(Parameters.parameters.booleanParameter("zeldaDungeonDistanceFitness")) 
			fitness.add(new Double(distanceToTriforce));
		if(Parameters.parameters.booleanParameter("zeldaDungeonFewRoomFitness")) 
			fitness.add(new Double(-numRooms));
		if(Parameters.parameters.booleanParameter("zeldaPercentDungeonTraversedRoomFitness")) 
			fitness.add(new Double(numRooms == 0 ? 0 : (numRoomsTraversed*1.0)/numRooms));
		if(Parameters.parameters.booleanParameter("zeldaDungeonTraversedRoomFitness")) 
			fitness.add(new Double(numRoomsTraversed));
		if(Parameters.parameters.booleanParameter("zeldaDungeonRandomFitness")) 
			fitness.add(new Double(RandomNumbers.fullSmallRand()));
		if(Parameters.parameters.booleanParameter("zeldaDungeonBackTrackRoomFitness")) 
			fitness.add(new Double(numBackTrackRooms));
		if(Parameters.parameters.booleanParameter("zeldaDungeonDistinctRoomFitness")) 
			fitness.add(new Double(numDistinctRooms));
		
		
		double[] scores = new double[fitness.size()];
		//stores the scores from the fitness at the index
		for(int i = 0; i < scores.length; i++) {
			scores[i] = fitness.get(i);
		}

		double[] other = new double[] {numRooms, numRoomsTraversed, numRoomsReachable, searchStatesVisited};
		return mapElitesBinIndices == null ? 
				new MultiObjectiveScore<T>(individual, scores, behaviorVector, other) : // Need whole behavior vector
				new MultiObjectiveScore<T>(individual, scores, mapElitesBinIndices, mapElitesBinScore, other); // Just need score for one MAP Elites bin
	}
	
	/**
	 * Main method: for quick testing
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException{
		MMNEAT.main("runNumber:0 randomSeed:0 zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false zeldaDungeonBackTrackRoomFitness:true watch:true trials:1 mu:10 makeZeldaLevelsPlayable:false base:zeldagan log:ZeldaGAN-FitnessTemp saveTo:FitnessTemp zeldaGANLevelWidthChunks:10 zeldaGANLevelHeightChunks:10 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:5000000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype".split(" "));	
	}
}
