package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

public class NegativeSpaceCountFitnessTest {
	
	NegativeSpaceCountFitness ff;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6"});
		
		// Uncomment out everything with client to see in Minecraft world. However, does not Maven build when not commented out!
		//MinecraftServer.launchServer();
		//MinecraftClient.getMinecraftClient();
	}
	
	@Before
	public void setUp() throws Exception {
		ff = new NegativeSpaceCountFitness();
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
		
		ArrayList<Block> blockSet1 = new ArrayList<>();
		ArrayList<Block> blockSet2 = new ArrayList<>();
		ArrayList<Block> blockSet3 = new ArrayList<>();
		blockSet1.add(new Block(10,15,10,BlockType.AIR, Orientation.WEST));
		assertEquals(-1,ff.fitnessScore(corner,blockSet1),0); // Test when nothing
		
		blockSet1.add(new Block(0,5,0,BlockType.GLOWSTONE, Orientation.WEST));
		assertEquals(0,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(1,5,0,BlockType.GLOWSTONE, Orientation.WEST));
		assertEquals(0,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(1,5,1,BlockType.GLOWSTONE, Orientation.WEST));
		blockSet1.add(new Block(0,5,1,BlockType.AIR, Orientation.WEST));
		assertEquals(1,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(0,6,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(0,6,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(1,6,0,BlockType.AIR, Orientation.WEST));
		assertEquals(1,ff.fitnessScore(corner,blockSet1),0); // Air above blocks does not effect the negative space
		blockSet1.add(new Block(1,6,1,BlockType.CRAFTING_TABLE, Orientation.WEST));
		assertEquals(4,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(1,7,1,BlockType.CRAFTING_TABLE, Orientation.WEST));
		blockSet1.add(new Block(0,7,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(1,7,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(0,7,1,BlockType.AIR, Orientation.WEST));
		assertEquals(7,ff.fitnessScore(corner,blockSet1),0);
		blockSet1.add(new Block(3,7,2,BlockType.WOOL, Orientation.WEST)); // Adding in air blocks
		blockSet1.add(new Block(3,7,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(3,7,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(3,6,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(3,6,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(3,6,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(3,5,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(3,5,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(3,5,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,7,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,7,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,7,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,6,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,6,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,6,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,5,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,5,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(2,5,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(1,7,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(1,6,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(1,5,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(0,7,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(0,6,2,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(0,5,2,BlockType.AIR, Orientation.WEST));

		assertEquals(30,ff.fitnessScore(corner,blockSet1),0);
		
		//MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1); // Spawns in just for verification
		
		MinecraftCoordinates corner2 = new MinecraftCoordinates(15,5,0); //Initializes another corner for testing
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		// Makes a cube
		for(int x=0;x<ranges.x();x++) {
			for(int y=0;y<ranges.y();y++) {
				for(int z=0;z<ranges.z();z++) {
					if((x+y+z)%2==1) blockSet2.add(new Block(15+x,5+y,0+z,BlockType.LAPIS_BLOCK, Orientation.WEST));
					else blockSet2.add(new Block(15+x,5+y,0+z,BlockType.AIR, Orientation.WEST));
				}
			}
		}
		assertEquals(500,ff.fitnessScore(corner2,blockSet2),0);
		//MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2); // Spawns in just for verification
		
		MinecraftCoordinates corner3 = new MinecraftCoordinates(26,5,0); //Initializes another corner for testing
		
		// Generates a 10x10x10 block of air
		for(int x=0;x<ranges.x();x++) {
			for(int y=0;y<ranges.y();y++) {
				for(int z=0;z<ranges.z();z++) {
					blockSet3.add(new Block(25+x,5+y,0+z,BlockType.AIR, Orientation.WEST)); // Has some display issues when adding to minecraft world
				}
			}
		}
		
		// Generates a 4x2x3 block in the air blocks created
		for(int x=0;x<4;x++) {
			for(int y=0;y<2;y++) {
				for(int z=0;z<3;z++) {
					blockSet3.add(new Block(26+x,5+y,0+z,BlockType.GOLD_ORE, Orientation.WEST));
				}
			}
		}
		assertEquals(0,ff.fitnessScore(corner3,blockSet3),0);
		
		blockSet3.add(new Block(32,5,0,BlockType.DIAMOND_ORE, Orientation.WEST));// Add one extra block to double check
		assertEquals(17,ff.fitnessScore(corner3,blockSet3),0); 
		//MinecraftClient.getMinecraftClient().spawnBlocks(blockSet3); // Spawns in just for verification
		
		
		//Clears space, comment out if need to do testing to see blocks be generated
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, ranges, 2, 0);
	}
	
	@Test
	public void testMaximumFitness() {
		assertEquals(998,ff.maxFitness(),0);
	}

}
