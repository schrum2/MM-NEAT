package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.datastructures.Vertex;

public class WaterLavaSecondaryCreationFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	WaterLavaSecondaryCreationFitness testInstance;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
		MinecraftServer.launchServer();
		MinecraftClient.getMinecraftClient();
		CommonConstants.watch = true; // Displays debugging info
	}

	@Before
	public void setUp() throws Exception {
		testInstance = new WaterLavaSecondaryCreationFitness();
		CommonConstants.watch = true; // Displays debugging info
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		Thread.sleep(waitTime);
		
		MinecraftClient.terminateClientScriptProcess();
		MinecraftServer.terminateServer();
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

	@Test
	public void testInteractionSameOrientationOfNorth() {
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 100); // Larger buffer is important

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,7,-35,BlockType.FLOWING_WATER, Orientation.NORTH));
		testBlockSet.add(new Block(-24,7,-35,BlockType.FLOWING_LAVA, Orientation.NORTH));
		
		
		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
	
		assertEquals(0.5, testInstance.fitnessScore(testCorner,testBlockSet),0.5);
	}
	//have a case where they are not touching with a fitness of zero
	//have one where they interact
}
