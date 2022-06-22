package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class NumPistonsFitnessTest {

	List<Block> blockSet1;
	NumPistonsFitness ff; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6"});
		
		// Uncomment out everything with client to see in Minecraft world. However, does not Maven build when not commented out!
		//MinecraftServer.launchServer();
		//MinecraftClient.getMinecraftClient();
	}
	
	@Before
	public void setUp() throws Exception {
		ff = new NumPistonsFitness();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		//Thread.sleep(waitTime);
		
		//MinecraftClient.terminateClientScriptProcess();
		//MinecraftServer.terminateServer();
	}
	
	@Test
	public void testFitnessFromBlocks() {
		MinecraftCoordinates corner = new MinecraftCoordinates(0,5,0); //Initializes corner for testing
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, MinecraftUtilClass.getRanges(), 2, 100);
		
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(1,5,1,BlockType.AIR, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0); // Test when nothing
		blockSet1.add(new Block(2,5,1,BlockType.EMERALD_BLOCK, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,5,1,BlockType.PISTON, Orientation.WEST));
		assertEquals(1,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(4,5,1,BlockType.STICKY_PISTON, Orientation.WEST));
		assertEquals(2,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(2,6,1,BlockType.DIAMOND_BLOCK, Orientation.WEST));
		assertEquals(2,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,6,1,BlockType.PISTON, Orientation.WEST));
		assertEquals(3,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(4,6,1,BlockType.STICKY_PISTON, Orientation.WEST));
		assertEquals(4,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(5,6,1,BlockType.STICKY_PISTON, Orientation.WEST));
		assertEquals(5,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(6,6,1,BlockType.STICKY_PISTON, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(2,6,2,BlockType.GOLD_BLOCK, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(2,6,3,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(2,6,4,BlockType.QUARTZ_BLOCK, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(2,6,5,BlockType.CHEST, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(2,6,6,BlockType.GRASS, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,6,6,BlockType.AIR, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(4,6,6,BlockType.AIR, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(5,6,6,BlockType.AIR, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(6,6,6,BlockType.AIR, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(7,6,6,BlockType.AIR, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(8,6,6,BlockType.AIR, Orientation.WEST));
		assertEquals(6,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,7,1,BlockType.PISTON, Orientation.WEST));
		assertEquals(7,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,7,2,BlockType.PISTON, Orientation.WEST));
		assertEquals(8,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,7,3,BlockType.PISTON, Orientation.WEST));
		assertEquals(9,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(3,7,4,BlockType.PISTON, Orientation.WEST));
		assertEquals(10,ff.fitnessFromBlocks(corner,blockSet1),0);
	}

	@Test
	public void testMaxFitness() {
		assertEquals(1000,ff.maxFitness(),0);
	}
}