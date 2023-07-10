package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class NumRedstoneFitnessTest {

	NumRedstoneFitness ff; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6"});
	}
	
	@Before
	public void setUp() throws Exception {
		ff = new NumRedstoneFitness();
	}
	
	@Test
	public void testfitnessScore() {
		MinecraftCoordinates corner = new MinecraftCoordinates(0,5,0); //Initializes corner for testing
		System.out.println("\n TEST: NumRedstoneFitnessTest: testfitnessScore");

		List<Block> blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(1,5,1,BlockType.AIR, Orientation.WEST));
		assertEquals(0,ff.fitnessScore(corner,blockSet1),0); // Test when nothing
		blockSet1.add(new Block(2,5,1,BlockType.EMERALD_BLOCK, Orientation.WEST));
		assertEquals(0,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(3,5,1,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		assertEquals(1,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(4,5,1,BlockType.GOLD_BLOCK, Orientation.WEST));
		assertEquals(1,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(5,5,1,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		assertEquals(2,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(2,6,1,BlockType.AIR, Orientation.WEST));
		assertEquals(2,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(3,6,1,BlockType.AIR, Orientation.WEST));
		assertEquals(2,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(4,6,1,BlockType.AIR, Orientation.WEST));
		assertEquals(2,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(5,6,1,BlockType.PISTON, Orientation.WEST));
		assertEquals(2,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(6,6,1,BlockType.STICKY_PISTON, Orientation.WEST));
		assertEquals(2,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(7,6,1,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		assertEquals(3,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(4,6,2,BlockType.AIR, Orientation.WEST));
		assertEquals(3,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(5,6,2,BlockType.PISTON, Orientation.WEST));
		assertEquals(3,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(6,6,2,BlockType.STICKY_PISTON, Orientation.WEST));
		assertEquals(3,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(7,6,2,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		assertEquals(4,ff.fitnessScore(corner,blockSet1),0);
		System.out.println("\n");
	}

	@Test
	public void testMaxFitness() {
		System.out.println("\n TEST: NumRedstoneFitnessTest: testMaximumFitness");

		assertEquals(1000,ff.maxFitness(),0);
		System.out.println("\n");
	}

}
