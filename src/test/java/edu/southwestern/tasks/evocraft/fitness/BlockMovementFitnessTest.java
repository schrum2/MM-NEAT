package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftServer;

public class BlockMovementFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"), 
															Parameters.parameters.integerParameter("minecraftYRange"),
															Parameters.parameters.integerParameter("minecraftZRange"));		
	MinecraftCoordinates corner;
	
	List<Block> stagnantFlyingMachine;
	
	List<Block> movingFlyingMachine1Block;
	
	List<Block> movingFlyingMachineManyBlocks;
	
	List<Block> simplePiston1Redstone;
	
	List<Block> simplePiston2Redstone;
	
	List<Block> simplePiston2Redstone2;
	
	List<Block> exampleFlyingMachine;
	
	
	BlockMovementFitness ff;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {});
		
		
		MinecraftServer.launchServer();
		// Launches the client script before the parallel code to assure that only one client script exists
		MinecraftClient.getMinecraftClient();
	}
	
	@Before
	public void setUp() throws Exception {
		ff = new BlockMovementFitness();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MinecraftClient.terminateClientScriptProcess();
		MinecraftServer.terminateServer();
	}

	@Test
	public void testMaxFitness() {
		fail("Not yet implemented");
	}

	@Test
	public void testFitnessFromBlocks() {
		
		corner = new MinecraftCoordinates(-6,7,-38);
		
		// One piston with one redstone block activating it
		simplePiston1Redstone = new ArrayList<>();
		simplePiston1Redstone.add(new Block(-5,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		simplePiston1Redstone.add(new Block(-4,7,-35,BlockType.PISTON, Orientation.EAST));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(simplePiston1Redstone);
		
		assertEquals(1.0, ff.fitnessFromBlocks(corner,simplePiston1Redstone), 0.0);
		
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, ranges, 1, 0);
		
		
		// Two pistons with one redstone block activating one of the two
		simplePiston2Redstone = new ArrayList<>();
		simplePiston2Redstone.add(new Block(-5,7,-37,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		simplePiston2Redstone.add(new Block(-4,7,-37,BlockType.PISTON, Orientation.EAST));
		simplePiston2Redstone.add(new Block(-3,7,-37,BlockType.PISTON, Orientation.EAST));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(simplePiston2Redstone);
		
		assertEquals(2.0, ff.fitnessFromBlocks(corner,simplePiston2Redstone), 0.0);
		
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, ranges, 1, 0);
		
		// Two pistons and two redstone blocks activating only ONE of the pistons
		simplePiston2Redstone2 = new ArrayList<>();
		simplePiston2Redstone2.add(new Block(-5,7,-37,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		simplePiston2Redstone2.add(new Block(-4,7,-37,BlockType.PISTON, Orientation.EAST));
		simplePiston2Redstone2.add(new Block(-3,7,-37,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		simplePiston2Redstone2.add(new Block(-2,7,-37,BlockType.PISTON, Orientation.EAST));

		MinecraftClient.getMinecraftClient().spawnBlocks(simplePiston2Redstone2);

		assertEquals(1.0, ff.fitnessFromBlocks(corner,simplePiston2Redstone2), 0.0);

		MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, ranges, 1, 0);
		
		
		// Based off the example flying machine from Evocraft
		// Moves slightly upon spawning in, seems to be considering anything moved
		// by the piston is part of the piston? Also does not include new air blocks
		exampleFlyingMachine = new ArrayList<>();
		// Bottom layer
		exampleFlyingMachine.add(new Block(-5,7,-37,BlockType.PISTON,Orientation.EAST));
		exampleFlyingMachine.add(new Block(-4,7,-37,BlockType.SLIME,Orientation.WEST));
		exampleFlyingMachine.add(new Block(-3,7,-37,BlockType.PISTON,Orientation.WEST));
		exampleFlyingMachine.add(new Block(-2,7,-37,BlockType.STICKY_PISTON,Orientation.EAST));
		exampleFlyingMachine.add(new Block(-1,7,-37,BlockType.SLIME,Orientation.WEST));
		// Top layer
		exampleFlyingMachine.add(new Block(-4,8,-37,BlockType.REDSTONE_BLOCK,Orientation.WEST));
		exampleFlyingMachine.add(new Block(-1,8,-37,BlockType.REDSTONE_BLOCK,Orientation.WEST));
		// Activate
		exampleFlyingMachine.add(new Block(-3,8,-37,BlockType.QUARTZ_BLOCK,Orientation.WEST));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(exampleFlyingMachine);
		
		// Keeps getting a fitness score of 11
		assertEquals(10.0, ff.fitnessFromBlocks(corner, exampleFlyingMachine), 0.0);
		
//		cornerSFM = new MinecraftCoordinates(-6,7,-30);
//		
//		stagnantFlyingMachine = new ArrayList<>();
//		// List for an actual working flying machine but is stagnant because the observer isn't activated
//		stagnantFlyingMachine.add(new Block(-5,7,-28,BlockType.OBSERVER, Orientation.WEST));
//		stagnantFlyingMachine.add(new Block(-4,7,-28,BlockType.STICKY_PISTON, Orientation.EAST));
//		stagnantFlyingMachine.add(new Block(-3,7,-28,BlockType.SLIME, Orientation.WEST));
//		stagnantFlyingMachine.add(new Block(-2,7,-28,BlockType.SLIME, Orientation.WEST));
//		stagnantFlyingMachine.add(new Block(-5,7,-29,BlockType.SLIME, Orientation.WEST));
//		stagnantFlyingMachine.add(new Block(-4,7,-29,BlockType.SLIME, Orientation.WEST));
//		stagnantFlyingMachine.add(new Block(-3,7,-29,BlockType.STICKY_PISTON, Orientation.WEST));
//		stagnantFlyingMachine.add(new Block(-2,7,-29,BlockType.OBSERVER, Orientation.EAST));
//		
//		
//		MinecraftClient.getMinecraftClient().spawnBlocks(stagnantFlyingMachine);
//		
//
//		assertEquals(0.0, ff.fitnessFromBlocks(cornerSFM,stagnantFlyingMachine), 0.0);
//		
//		cornerMFM1B = new MinecraftCoordinates(-6,7,-34);
//		
//		movingFlyingMachine1Block = new ArrayList<>();
//		// List for moving flying machine, simply add a block by an observer so it moves
//		movingFlyingMachine1Block.add(new Block(-5,7,-32,BlockType.OBSERVER, Orientation.WEST));
//		movingFlyingMachine1Block.add(new Block(-4,7,-32,BlockType.STICKY_PISTON, Orientation.EAST));
//		movingFlyingMachine1Block.add(new Block(-3,7,-32,BlockType.SLIME, Orientation.WEST));
//		movingFlyingMachine1Block.add(new Block(-2,7,-32,BlockType.SLIME, Orientation.WEST));
//		movingFlyingMachine1Block.add(new Block(-5,7,-33,BlockType.SLIME, Orientation.WEST));
//		movingFlyingMachine1Block.add(new Block(-4,7,-33,BlockType.SLIME, Orientation.WEST));
//		movingFlyingMachine1Block.add(new Block(-3,7,-33,BlockType.STICKY_PISTON, Orientation.WEST));
//		movingFlyingMachine1Block.add(new Block(-2,7,-33,BlockType.OBSERVER, Orientation.EAST));
//		movingFlyingMachine1Block.add(new Block(-1,10,-33,BlockType.SAND,Orientation.EAST));
//		
//		MinecraftClient.getMinecraftClient().spawnBlocks(movingFlyingMachine1Block);
//		
//		// Seems like this one moves one block from the starting point
//		assertEquals(9.0,ff.fitnessFromBlocks(cornerMFM1B, movingFlyingMachine1Block),0.0);
		
		
		
	}

	@Test
	public void testFitnessScoreMinecraftCoordinates() {

		// Create a shape involving some pistons that you expect to move several blocks

		
		fail("Not yet implemented");
	}

}
