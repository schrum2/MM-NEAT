package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class ChangeCenterOfMassFitnessTest {
	
	List<Block> blockSet1;
	List<Block> blockSet2;

	ChangeCenterOfMassFitness ff;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ff = new ChangeCenterOfMassFitness();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testMinFitness() {
		fail("Not yet implemented");
	}

	@Test
	public void testMaxFitness() {
		fail("Not yet implemented");
	}

	@Test
	public void testFitnessScoreMinecraftCoordinates() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCenterOfMass() {
		// Small list of blocks
		// This should have a center of mass of (-5 + (-4) / 2, 7+7/2 , -35 - 35 /2 ) = (-9/2, 7, -35) = (-4, 7, -35)
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(-5,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		blockSet1.add(new Block(-4,7,-35,BlockType.PISTON, Orientation.EAST));
		
		assertEquals(new MinecraftCoordinates(-4,7,-35), ff.getCenterOfMass(blockSet1));
		
		// List of flying machine blocks
		// COM = (1, 25 + 18 / 8, ) = (1, 5, -1)
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
		
		assertEquals(new MinecraftCoordinates(1,5,-1), ff.getCenterOfMass(blockSet2));
	}

}
