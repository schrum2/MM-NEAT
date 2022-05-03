package edu.southwestern.evolution.mapelites.generalmappings;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.zelda.ZeldaGANDungeonTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

public class LevelTraversalPathBinLabelsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception { // latent vector size is 80
		Parameters.parameters = null;
		MMNEAT.clearClasses();
		String[] params = new String[]{
				"runNumber:1",
				"randomSeed:1",
				"zeldaCPPN2GANSparseKeys:true",
				"zeldaALlowPuzzleDoorUglyHack:false",
				"zeldaCPPNtoGANAllowsRaft:true",
				"zeldaCPPNtoGANAllowsPuzzleDoors:true",
				"zeldaDungeonBackTrackRoomFitness:true",
				"zeldaDungeonDistinctRoomFitness:true",
				"zeldaDungeonDistanceFitness:false",
				"zeldaDungeonFewRoomFitness:false",
				"zeldaDungeonTraversedRoomFitness:true",
				"zeldaPercentDungeonTraversedRoomFitness:true",
				"zeldaDungeonRandomFitness:false",
				"watch:false",
				"trials:1",
				"mu:100",
				"makeZeldaLevelsPlayable:false",
				"zeldaGANLevelWidthChunks:4",
				"zeldaGANLevelHeightChunks:4",
				"zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth",
				"maxGens:100000",
				"io:false",
				"netio:false",
				"GANInputSize:10",
				"fs:false",
				"task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask",
				"cleanOldNetworks:false",
				"zeldaGANUsesOriginalEncoding:false",
				"cleanFrequency:-1",
				"saveAllChampions:true",
				"genotype:edu.southwestern.evolution.genotypes.RealValuedGenotype",
				"ea:edu.southwestern.evolution.mapelites.MAPElites",
				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
				"mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LevelTraversalPathBinLabels",
				"steadyStateIndividualsPerGeneration:100"
				};
		Parameters.initializeParameterCollections(params);
		MMNEAT.loadClasses();
	}
	
	LevelTraversalPathBinLabels binLabels;
	int levelDimensions;
	
	@Before
	public void setUp() throws Exception {
		binLabels = new LevelTraversalPathBinLabels();
		levelDimensions = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks");
	}
	
	/*
	 * Huge Helper method to take in a genotype, simulate
	 * how an MMNEAT process would create a behavior map
	 * from a level, and return the map that would be given
	 * to the binning scheme
	 */
	private HashMap<String, Object> genotypeIntoBehaviorMap(ArrayList<Double> geno) {
		System.out.println("[TEST] CREATE DUNGEON");
		Dungeon dungeon = ZeldaGANDungeonTask.getZeldaDungeonFromDirectArrayList(geno, 17, levelDimensions, levelDimensions);
		
		
		System.out.println("[TEST] START SEARCH");
		Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
		ZeldaState startState = new ZeldaState(5, 5, 0, dungeon);
		ArrayList<GridAction> actionSequence = ((AStarSearch<GridAction, ZeldaState>) search).search(startState, true, Parameters.parameters.integerParameter("aStarSearchBudget"));

		HashSet<ZeldaState> solutionPath = new HashSet<>();
		ZeldaState currentState = startState;
		solutionPath.add(currentState);
		
		HashSet<Pair<Integer,Integer>> exitedRoomCoordinates = new HashSet<>();
		Pair<Integer, Integer> prevRoom = null;
		for(GridAction a : actionSequence) {
			//System.out.println("[TEST] ACTION \""+a.toString()+"\"");
			currentState = (ZeldaState) currentState.getSuccessor(a);
			solutionPath.add(currentState);
			Pair<Integer,Integer> newRoom = new Pair<>(currentState.dX,currentState.dY);
			if(prevRoom!=null&&!prevRoom.equals(newRoom)){ //only ever true when leaving/entering a room
				exitedRoomCoordinates.add(prevRoom); //add the exited room
			}
			prevRoom = newRoom;
		}

		HashSet<Pair<Integer,Integer>> visitedRoomCoordinates = new HashSet<>();
		//sets a pair of coordinates for each room found 
		for(ZeldaState zs: solutionPath) {							
			// Set does not allow duplicates: one Pair per room
			visitedRoomCoordinates.add(new Pair<>(zs.dX,zs.dY)); 
		}
		
		HashSet<ZeldaState> mostRecentVisited = ((AStarSearch<GridAction, ZeldaState>) search).getVisited();
		
		System.out.println("[TEST] LABEL OUTPUT");
		HashMap<String, Object> behaviorMap = new HashMap<String, Object>();
		behaviorMap.put("Level Path", mostRecentVisited);
		
//		// view dungeon graphically to confirm
//		DungeonUtil.viewDungeon(dungeon, mostRecentVisited, solutionPath);
//		//DrawingPanel childPanel = GraphicsUtil.drawImage(image, "output", image.getWidth(), image.getHeight());
//		//childPanel.setLocation(200, 0);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		//childPanel.dispose();
		
		return behaviorMap;
	}
	
	/*
	 * Helper to convert behavior map results into
	 * an output bit string we can check in the test
	 * cases 
	 */
	private String bitStringFromBehaviorMap(HashMap<String, Object> behaviorMap) {
		int index = binLabels.multiDimensionalIndices(behaviorMap)[0];
		String bitStr = Integer.toBinaryString(index);
		// pad out bitStr to the right size
		while (bitStr.length() < 16) {
			bitStr = "0" + bitStr;
		}
		return bitStr;
	}
	
	@Test
	public void testBinLabels() {
		@SuppressWarnings("unused")
		// Make labels
		List<String> labels = binLabels.binLabels();
		// Determine genotype length
		int genoLength = (GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables())*16;
		System.out.println("[TEST] GENOME LENGTH: "+genoLength);
		
		
		
		// First test genotype
		ArrayList<Double> geno = new ArrayList<>(genoLength);
		for (int i = 0; i < genoLength; i++) {
			geno.add(((double)i)/genoLength);
		}
		// get map
		HashMap<String, Object> behaviorMap1 = genotypeIntoBehaviorMap(geno);
		String bitStr1 = bitStringFromBehaviorMap(behaviorMap1);
		
		System.out.println("dungeon layout:\n "+bitStr1.substring(0, 4));
		System.out.println(" "+bitStr1.substring(4, 8));
		System.out.println(" "+bitStr1.substring(8, 12));
		System.out.println(" "+bitStr1.substring(12, 16));
		
		// Get bit string from the behavior map, and make sure it found the right rooms
		assertEquals(bitStr1, "0110"
							+ "0010"
							+ "0011"
							+ "0001");

		
		
		// Second test genotype
		geno = new ArrayList<>(genoLength); // Clear genotype
		for (int i = 0; i < genoLength; i++) {
			geno.add(((double)i)/2*genoLength);
		}
		// get map
		HashMap<String, Object> behaviorMap2 = genotypeIntoBehaviorMap(geno);
		String bitStr2 = bitStringFromBehaviorMap(behaviorMap2);
		
		// Get bit string from the behavior map, and make sure it found the right rooms
		assertEquals(bitStr2, "0111"
							+ "0011"
							+ "0011"
							+ "0001");
		
		
		
		// Second test genotype
		geno = new ArrayList<>(genoLength); // Clear genotype
		for (int i = 0; i < genoLength; i++) {
			geno.add(((double)i)/3*genoLength);
		}
		// get map
		HashMap<String, Object> behaviorMap3 = genotypeIntoBehaviorMap(geno);
		String bitStr3 = bitStringFromBehaviorMap(behaviorMap3);
		
		// Get bit string from the behavior map, and make sure it found the right rooms
		assertEquals(bitStr3, "0110"
							+ "0111"
							+ "0010"
							+ "0011");
		
	}

}
