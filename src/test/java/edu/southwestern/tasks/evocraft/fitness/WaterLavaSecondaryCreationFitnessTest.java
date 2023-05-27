package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftServer;

public class WaterLavaSecondaryCreationFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	WaterLavaSecondaryCreationFitness testInstance;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet","minecraftAccumulateChangeInCenterOfMass:false"});
		if(!MinecraftServer.serverIsRunner()) {
			MinecraftServer.launchServer();
			MinecraftClient.getMinecraftClient();
		}
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}

	@Before
	public void setUp() throws Exception {
		testInstance = new WaterLavaSecondaryCreationFitness();
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MinecraftServerTestTracker.numberOfUnitTestsThatMakeAServer--;
		
		// Only terminate the server if no other tests will need it
		if(MinecraftServerTestTracker.numberOfUnitTestsThatMakeAServer == 0) {
			long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
			Thread.sleep(waitTime);
			
			MinecraftClient.terminateClientScriptProcess();
			MinecraftServer.terminateServer();
		}
		CommonConstants.watch = false; // Displays debugging info
	}

	//passed
	@Test
	public void testNoInteractionDifferentOrientations() {
		// TODO: Tests go here!
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 100); // Larger buffer is important

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,7,-35,BlockType.FLOWING_WATER, Orientation.WEST));
		testBlockSet.add(new Block(-20,7,-35,BlockType.FLOWING_LAVA, Orientation.EAST));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
	
		assertEquals(0.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}
	
	//passed
	@Test
	public void testNoInteractionOrientationNorthForBoth() {
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 100); // Larger buffer is important

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,7,-35,BlockType.FLOWING_WATER, Orientation.NORTH));
		testBlockSet.add(new Block(-20,7,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));
		
		
		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
	
		assertEquals(0.0, testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}

	//passed
	//this test makes one block of obsidian - fitness of 1
	@Test
	public void testInteractionSameOrientationOfNorth() {
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 100); // Larger buffer is important

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,7,-35,BlockType.FLOWING_WATER, Orientation.NORTH));
		testBlockSet.add(new Block(-24,7,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));
		
		
		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
	
		assertEquals(1.0, testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}
	
	//passed
	//testing for creation of two blocks of obsidian - fitness of 2
	@Test
	public void testInteractionThreeBlocksSameOrientationOfNorth() {
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 100); // Larger buffer is important

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,7,-35,BlockType.FLOWING_WATER, Orientation.NORTH));
		testBlockSet.add(new Block(-24,7,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));
		testBlockSet.add(new Block(-26,7,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));

		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
	
		assertEquals(2.0, testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}
	
	//creates 1 cobblestone - fitness 1
//	@Test
//	public void testInteractionTwoBlocksSameOrientationOfNorthCobblestoneCreation() {
//		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
//		
//		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
//		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 100); // Larger buffer is important
//
//		ArrayList<Block> testBlockSet = new ArrayList<>();
//		testBlockSet.add(new Block(-25,27,-35,BlockType.FLOWING_WATER, Orientation.NORTH));
//		testBlockSet.add(new Block(-22,27,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));
//		//testBlockSet.add(new Block(-26,7,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));
//
//		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
//	
//		assertEquals(1.0, testInstance.fitnessScore(testCorner,testBlockSet),0.0);
//	}
	
	//uses a quartz block base to create a platform for lava and water to mix
	@Test
	public void testInteractionQuartzBaseSameOrientationOfNorthCobblestoneCreation() {
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,27,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 100); // Larger buffer is important

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,27,-35,BlockType.FLOWING_WATER, Orientation.NORTH));
		testBlockSet.add(new Block(-21,27,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));
		
		//setting the base coordinates of the quartz platform
		int xMinCoordinate = -27;
		int xMaxCoordinate = -17;
		int zMinCoordinate = -38;
		int zMaxCoordinate = -32;
		int yBaseCoodinate = 25;

		//creates the base platform
		for(int xIndex = xMinCoordinate; xIndex <= xMaxCoordinate; xIndex++) {
			for(int zIndex = zMinCoordinate; zIndex <= zMaxCoordinate; zIndex++) {
				testBlockSet.add(new Block(xIndex,yBaseCoodinate,zIndex,BlockType.QUARTZ_BLOCK, Orientation.NORTH));
			}
		}
		//creates the edges of the platform
		for(int xIndex = xMinCoordinate; xIndex <= xMaxCoordinate; xIndex++) {
			testBlockSet.add(new Block(xIndex,yBaseCoodinate+1,zMinCoordinate,BlockType.QUARTZ_BLOCK, Orientation.NORTH));
			testBlockSet.add(new Block(xIndex,yBaseCoodinate+1,zMaxCoordinate,BlockType.QUARTZ_BLOCK, Orientation.NORTH));
		}
		for(int zIndex = zMinCoordinate+1; zIndex < zMaxCoordinate; zIndex++) {
			testBlockSet.add(new Block(xMinCoordinate,yBaseCoodinate+1,zIndex,BlockType.QUARTZ_BLOCK, Orientation.NORTH));
			testBlockSet.add(new Block(xMaxCoordinate,yBaseCoodinate+1,zIndex,BlockType.QUARTZ_BLOCK, Orientation.NORTH));
		}
		
		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
	
		//fillCube does not work because of the fitnessScore call
		assertEquals(13.0, testInstance.fitnessScore(testCorner,testBlockSet),2.0); // Seems like a lot of wiggle room ... too much?
	}

	
	//finish up and redo all unit tests
}
