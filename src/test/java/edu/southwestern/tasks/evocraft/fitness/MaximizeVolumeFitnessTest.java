package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.experiment.post.MinecraftBlockCompareExperiment;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class MaximizeVolumeFitnessTest {

	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	MaximizeVolumeFitness testInstance;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet","minecraftAccumulateChangeInCenterOfMass:false"});
		if(!MinecraftServer.serverIsRunner()) {
			MinecraftServer.launchServer();
			MinecraftClient.getMinecraftClient();
		}
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}
	
	@Before
	public void setUp() throws Exception {
		testInstance = new MaximizeVolumeFitness();
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MinecraftServerTestTracker.decrementServerTestCount();
		
		// Only terminate the server if no other tests will need it
		if(MinecraftServerTestTracker.checkServerTestCount() == 0) {
			long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
			Thread.sleep(waitTime);
			
			MinecraftClient.terminateClientScriptProcess();
			MinecraftServer.terminateServer();
		}
		CommonConstants.watch = false; // Displays debugging info
	}
	
	//passed, score is the total volume of the shape
	@Test
	public void testNoInteraction() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,7,-35,BlockType.SLIME, Orientation.WEST));
		testBlockSet.add(new Block(-20,7,-35,BlockType.QUARTZ_BLOCK, Orientation.EAST));
	
		assertEquals(6.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}
	
	//currently passes with a score of 125
	@Test
	public void testWorkingFlyingMachine() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!
		
		//create shape
		String blockString = "[QUARTZ_BLOCK at (-1433,37,-1432) oriented NORTH, TNT at (-1433,37,-1431) oriented UP, QUARTZ_BLOCK at (-1433,37,-1429) oriented NORTH, REDSTONE_BLOCK at (-1433,38,-1433) oriented NORTH, OBSERVER at (-1433,38,-1431) oriented DOWN, QUARTZ_BLOCK at (-1433,39,-1433) oriented UP, OBSERVER at (-1433,39,-1431) oriented NORTH, TNT at (-1433,40,-1431) oriented WEST, QUARTZ_BLOCK at (-1433,40,-1430) oriented NORTH, OBSERVER at (-1433,41,-1433) oriented NORTH, QUARTZ_BLOCK at (-1432,37,-1432) oriented NORTH, QUARTZ_BLOCK at (-1432,37,-1430) oriented UP, PISTON at (-1432,39,-1429) oriented NORTH, STICKY_PISTON at (-1432,40,-1433) oriented NORTH, QUARTZ_BLOCK at (-1432,40,-1432) oriented NORTH, QUARTZ_BLOCK at (-1432,40,-1431) oriented NORTH, OBSERVER at (-1432,41,-1433) oriented EAST, QUARTZ_BLOCK at (-1432,41,-1430) oriented EAST, QUARTZ_BLOCK at (-1431,37,-1431) oriented NORTH, QUARTZ_BLOCK at (-1431,37,-1429) oriented NORTH, STICKY_PISTON at (-1431,38,-1432) oriented NORTH, QUARTZ_BLOCK at (-1431,39,-1433) oriented NORTH, QUARTZ_BLOCK at (-1431,41,-1432) oriented NORTH, QUARTZ_BLOCK at (-1430,38,-1430) oriented WEST, TNT at (-1430,39,-1432) oriented NORTH, QUARTZ_BLOCK at (-1430,40,-1433) oriented DOWN, QUARTZ_BLOCK at (-1430,41,-1433) oriented EAST, REDSTONE_BLOCK at (-1430,41,-1432) oriented WEST, QUARTZ_BLOCK at (-1429,37,-1430) oriented UP, QUARTZ_BLOCK at (-1429,37,-1429) oriented EAST, TNT at (-1429,39,-1432) oriented NORTH, QUARTZ_BLOCK at (-1429,41,-1433) oriented NORTH, TNT at (-1429,41,-1432) oriented SOUTH, SLIME at (-1429,41,-1429) oriented NORTH]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(blockString);

		//shift flying machine shape to the test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		assertEquals(125.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}

	//This one has south and some other direction, 
	//one machine goes up and down as well which causes a large amount of variance, 952, 1008, 1008, 952
	//should probably identify the second direction, is a slower more interesting machine
	@Test
	public void testTwoWorkingFlyingMachineSomethingSouth() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!

		//currently from minecraft 2022, MEOriginalVectorPistonOrientation19
		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
		//currently from minecraft 2022, MEOriginalVectorPistonOrientation0
		String unidentifiedDirectionFlyingMachineString = "[STICKY_PISTON at (-160,68,-258) oriented EAST, REDSTONE_BLOCK at (-160,69,-258) oriented SOUTH, STICKY_PISTON at (-160,70,-258) oriented EAST, PISTON at (-159,68,-260) oriented WEST, SLIME at (-159,68,-259) oriented DOWN, REDSTONE_BLOCK at (-159,69,-260) oriented NORTH, SLIME at (-159,69,-259) oriented UP, STICKY_PISTON at (-159,70,-260) oriented SOUTH, SLIME at (-159,70,-259) oriented DOWN, QUARTZ_BLOCK at (-158,68,-260) oriented DOWN, REDSTONE_BLOCK at (-158,69,-260) oriented DOWN, REDSTONE_BLOCK at (-158,69,-259) oriented DOWN, STICKY_PISTON at (-158,70,-260) oriented EAST, STICKY_PISTON at (-158,70,-258) oriented WEST]";
		
		List<Block> southFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
		List<Block> unidentifiedDirectionFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(unidentifiedDirectionFlyingMachineString);

		//overall test block set
		List<Block> testBlockSet = new ArrayList<Block>();

		//south flying machine shifted to test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(southFlyingMachine);
		southFlyingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(southFlyingMachine, originalShapeCoordinates, testCorner);

		//augment testcorner by 8 to ensure they do not interfere with each other
		MinecraftCoordinates augmentedCoordinates = new MinecraftCoordinates(testCorner);
		augmentedCoordinates.t1 = augmentedCoordinates.t1 -8;			//moving only on the x axis

		originalShapeCoordinates = MinecraftUtilClass.minCoordinates(unidentifiedDirectionFlyingMachine);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(unidentifiedDirectionFlyingMachine, originalShapeCoordinates, augmentedCoordinates);

		testBlockSet.addAll(southFlyingMachine);

		assertEquals(1008.0,testInstance.fitnessScore(testCorner,testBlockSet),60.0);
	}

	//passed
	@Test
	public void testTwoWorkingFlyingMachineWestSouth() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!

		//currently from minecraft 2022, MEOriginalVectorPistonOrientation19
		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
		//currently from minecraft 2022 experiments, MEOriginalVectorPistonOrientation21
		String westFlyingMachineString = "[REDSTONE_BLOCK at (-500,102,501) oriented UP, STICKY_PISTON at (-500,102,502) oriented EAST, QUARTZ_BLOCK at (-499,100,500) oriented SOUTH, STICKY_PISTON at (-499,101,501) oriented EAST, SLIME at (-499,102,500) oriented DOWN, SLIME at (-499,102,501) oriented NORTH, SLIME at (-499,102,502) oriented WEST, SLIME at (-498,100,500) oriented NORTH, QUARTZ_BLOCK at (-498,100,501) oriented EAST, SLIME at (-498,101,500) oriented DOWN, REDSTONE_BLOCK at (-498,102,500) oriented UP, STICKY_PISTON at (-498,102,501) oriented WEST]";
		List<Block> southFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
		List<Block> westFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(westFlyingMachineString);

		//overall test block set
		List<Block> testBlockSet = new ArrayList<Block>();
		//add blocks using file here


		//shifting south flying machine coordinates to test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(southFlyingMachine);
		southFlyingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(southFlyingMachine, originalShapeCoordinates, testCorner);

		//augment testcorner by 4 to give the machines space
		MinecraftCoordinates augmentedCoordinates = new MinecraftCoordinates(testCorner);
		augmentedCoordinates.t1 = augmentedCoordinates.t1 -4;			//moving only on the x axis

		//shift west flying machine to test corner augmented on x axis by 4
		originalShapeCoordinates = MinecraftUtilClass.minCoordinates(westFlyingMachine);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(westFlyingMachine, originalShapeCoordinates, augmentedCoordinates);

		testBlockSet.addAll(southFlyingMachine);

		assertEquals(392.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}

	//passed, x offset is east west, did make them at different points n/s?
	@Test
	public void testTwoWorkingFlyingMachineNorthSouth() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!

		//currently from minecraft 2022, MEOriginalVectorPistonOrientation19
		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
		//currently from minecraft 2022 experiments, MEOriginalVectorPistonOrientation14
		String northFlyingMachineString = "[QUARTZ_BLOCK at (-500,100,500) oriented NORTH, SLIME at (-500,101,500) oriented WEST, REDSTONE_BLOCK at (-500,101,501) oriented WEST, SLIME at (-500,102,500) oriented SOUTH, STICKY_PISTON at (-500,102,501) oriented NORTH, SLIME at (-499,101,500) oriented UP, STICKY_PISTON at (-499,101,501) oriented NORTH, REDSTONE_BLOCK at (-499,102,500) oriented DOWN, SLIME at (-499,102,501) oriented EAST, STICKY_PISTON at (-499,102,502) oriented EAST, SLIME at (-498,101,500) oriented WEST, STICKY_PISTON at (-498,102,500) oriented SOUTH, SLIME at (-498,102,501) oriented DOWN, REDSTONE_BLOCK at (-498,102,502) oriented DOWN]";
		
		List<Block> southFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
		List<Block> northFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(northFlyingMachineString);

		//overall test block set
		List<Block> testBlockSet = new ArrayList<Block>();

		//shifting south flying machine coordinates to test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(southFlyingMachine);
		southFlyingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(southFlyingMachine, originalShapeCoordinates, testCorner);

		//augment testcorner by 4 to give the machines space
		MinecraftCoordinates augmentedCoordinates = new MinecraftCoordinates(testCorner);
		augmentedCoordinates.t1 = augmentedCoordinates.t1 -4;			//moving only on the x axis

		//shift west flying machine to test corner augmented on x axis by 4
		originalShapeCoordinates = MinecraftUtilClass.minCoordinates(northFlyingMachine);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(northFlyingMachine, originalShapeCoordinates, augmentedCoordinates);

		testBlockSet.addAll(southFlyingMachine);

		assertEquals(476.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}


}
