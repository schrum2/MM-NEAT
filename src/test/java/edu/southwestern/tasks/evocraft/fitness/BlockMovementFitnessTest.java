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
	
	List<Block> stagnantFlyingMachine;
	MinecraftCoordinates cornerSFM;
	
	List<Block> movingFlyingMachine1Block;
	MinecraftCoordinates cornerMFM1B;
	
	List<Block> movingFlyingMachineManyBlocks;
	MinecraftCoordinates cornerMFMMB;
	
	List<Block> simplePiston1Redstone;
	MinecraftCoordinates cornerSP1R;
	
	List<Block> simplePiston2Redstone;
	MinecraftCoordinates cornerSP2R;
	
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
		
		cornerSP1R = new MinecraftCoordinates(-6,7,-36);
		
		simplePiston1Redstone = new ArrayList<>();
		simplePiston1Redstone.add(new Block(-5,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		simplePiston1Redstone.add(new Block(-4,7,-35,BlockType.PISTON, Orientation.EAST));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(simplePiston1Redstone);
		
		assertEquals(1.0, ff.fitnessFromBlocks(cornerSP1R,simplePiston1Redstone), 0.0);
		
		
		cornerSP2R = new MinecraftCoordinates(-6,7,-38);
		
		simplePiston2Redstone = new ArrayList<>();
		simplePiston2Redstone.add(new Block(-5,7,-37,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		simplePiston2Redstone.add(new Block(-4,7,-37,BlockType.PISTON, Orientation.EAST));
		simplePiston2Redstone.add(new Block(-3,7,-37,BlockType.PISTON, Orientation.EAST));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(simplePiston2Redstone);
		
		assertEquals(1.0, ff.fitnessFromBlocks(cornerSP2R,simplePiston2Redstone), 0.0);
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
