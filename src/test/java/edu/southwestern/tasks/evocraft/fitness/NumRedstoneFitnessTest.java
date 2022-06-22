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

	List<Block> blockSet1;
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
	public void testFitnessFromBlocks() {
		MinecraftCoordinates corner = new MinecraftCoordinates(0,5,0); //Initializes corner for testing

		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(1,5,1,BlockType.AIR, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0); // Test when nothing
		blockSet1.add(new Block(2,5,1,BlockType.EMERALD_BLOCK, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,5,1,BlockType.PISTON, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0);
	}

	@Test
	public void testMaxFitness() {
		assertEquals(1000,ff.maxFitness(),0);
	}

}
