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
	MinecraftCoordinates cornerEx;
	
	List<Block> simplePiston1Redstone;
	List<Block> simplePiston2Redstone;
	List<Block> simplePiston2Redstone2;
	List<Block> exampleFlyingMachine;
	List<Block> pistonPushingBlocks; 
	
	
	BlockMovementFitness ff;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {});
		
		
		//MinecraftServer.launchServer();
		// Launches the client script before the parallel code to assure that only one client script exists
		MinecraftClient.getMinecraftClient();
	}
	
	@Before
	public void setUp() throws Exception {
		ff = new BlockMovementFitness();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// The default is about 10 seconds (10 000 milliseconds)
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		Thread.sleep(waitTime);
		
		MinecraftClient.terminateClientScriptProcess();
		MinecraftServer.terminateServer();
	}

	@Test
	public void testMaxFitness() {
		int xrange = Parameters.parameters.integerParameter("minecraftXRange");
		int yrange = Parameters.parameters.integerParameter("minecraftYRange");
		int zrange = Parameters.parameters.integerParameter("minecraftZRange");
		
		assertEquals(xrange*yrange*zrange, ff.maxFitness(),0.0);
	}

	@Test
	public void testFitnessFromBlocks() {
		
		corner = new MinecraftCoordinates(-6,5,-38);
		
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
		
		// Test to check if other blocks that are affected by pistons get called PISTON_EXTENSION
		pistonPushingBlocks = new ArrayList<>();
		pistonPushingBlocks.add(new Block(-4,7,-37,BlockType.PISTON,Orientation.EAST));
		pistonPushingBlocks.add(new Block(-3,7,-37,BlockType.SLIME,Orientation.WEST));
		pistonPushingBlocks.add(new Block(-3,8,-37,BlockType.REDSTONE_BLOCK,Orientation.WEST));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(pistonPushingBlocks);
		
		assertEquals(4.0, ff.fitnessFromBlocks(corner,pistonPushingBlocks), 0.0);
		
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, ranges, 1, 0);
		
		// Two pistons and two redstone blocks activating only ONE of the pistons
		// This represents the parts of the flying machine that push the machine
		// to the east
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
		cornerEx = new MinecraftCoordinates(0,5,-4);
		exampleFlyingMachine = new ArrayList<>();
		// Bottom layer
		exampleFlyingMachine.add(new Block(1,5,1,BlockType.PISTON,Orientation.NORTH));
		exampleFlyingMachine.add(new Block(1,5,0,BlockType.SLIME,Orientation.NORTH));
		exampleFlyingMachine.add(new Block(1,5,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		exampleFlyingMachine.add(new Block(1,5,-2,BlockType.PISTON,Orientation.NORTH));
		exampleFlyingMachine.add(new Block(1,5,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		exampleFlyingMachine.add(new Block(1,6,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		exampleFlyingMachine.add(new Block(1,6,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		exampleFlyingMachine.add(new Block(1,6,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(exampleFlyingMachine);
		
		assertEquals(11.0, ff.fitnessFromBlocks(cornerEx, exampleFlyingMachine), 0.0);
		
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerEx, ranges, 1, 0);
	}

	@Test
	public void testFitnessScoreMinecraftCoordinates() {

		// Create a shape involving some pistons that you expect to move several blocks

		
		fail("Not yet implemented");
	}

}
