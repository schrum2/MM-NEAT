package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.util.datastructures.Vertex;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class ChangeCenterOfMassFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"), 
															Parameters.parameters.integerParameter("minecraftYRange"),
															Parameters.parameters.integerParameter("minecraftZRange"));
	
	List<Block> blockSet1;
	List<Block> blockSet2;

	ChangeCenterOfMassFitness ff;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6"});
		MinecraftServer.launchServer();
		MinecraftClient.getMinecraftClient();
	}

	@Before
	public void setUp() throws Exception {
		ff = new ChangeCenterOfMassFitness();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		Thread.sleep(waitTime);
		
		MinecraftClient.terminateClientScriptProcess();
		MinecraftServer.terminateServer();
	}

	@Test
	public void testFitnessScoreMinecraftCoordinates() {
		// Small list of blocks that don't move
		// Should have a fitness of 0
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(-5,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		blockSet1.add(new Block(-4,7,-35,BlockType.PISTON, Orientation.EAST));
		
		MinecraftCoordinates cornerBS1 = new MinecraftCoordinates(-6,7,-35);
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
	
		assertEquals(0.0, ff.fitnessScore(cornerBS1),0.0);
		
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS1, ranges, 1, 100); // Larger buffer is important
		
		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,5,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,5,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,6,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,6,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,6,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,5,-5);
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);
		
		// Since it is moving out completely, and all the ranges are the same value (10)
		// That means the max fitness is 10 + 6 / 2 = 8
		// However, the movement speed of the flying machine depends on the speed of the independently
		// executing Minecraft server, which is subject to variation. The main point is that the ship
		// flies for a bit, but the exact amount is hard to pin down. Thus, we only assert that the amount
		// is 6.0 or more
		assertTrue(6.0 <= ff.fitnessScore(cornerBS2));
		
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 0);
		
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);
		
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime") + 25000l;
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			System.out.println("Thread was interrupted");
			e.printStackTrace();
			System.exit(1);
		}
		
		assertEquals(ff.maxFitness(),ff.fitnessScore(cornerBS2),0.0);
		
	}

	@Test
	public void testGetCenterOfMass() {
		// Small list of blocks
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(-5,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		blockSet1.add(new Block(-4,7,-35,BlockType.PISTON, Orientation.EAST));
		
		
		assertEquals(new Vertex(-4.5,7.0,-35.0), ChangeCenterOfMassFitness.getCenterOfMass(blockSet1));
		
		
		// List of flying machine blocks
		blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,5,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,5,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,6,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,6,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,6,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		assertEquals(new Vertex(1.0,5.375,-1.375), ChangeCenterOfMassFitness.getCenterOfMass(blockSet2));
	}

}
