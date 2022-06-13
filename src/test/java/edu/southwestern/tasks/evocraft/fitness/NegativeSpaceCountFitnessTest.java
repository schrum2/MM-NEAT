package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fossgalaxy.object.annotations.Parameter;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class NegativeSpaceCountFitnessTest {
	
	List<Block> blockSet1;
	List<Block> blockSet2;
	
	NegativeSpaceCountFitness ff;
	
	// Uncomment when it works! Might need to set a specific value of minecraftMandatoryWaitTime here
	//@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6"});
		MinecraftServer.launchServer();
		MinecraftClient.getMinecraftClient();
	}
	
	@Before
	public void setUp() throws Exception {
		ff = new NegativeSpaceCountFitness();
	}
	
	// Uncomment when it works!
	//@AfterClass
	public static void tearDownAfterClass() throws Exception {
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		Thread.sleep(waitTime);
		
		MinecraftClient.terminateClientScriptProcess();
		MinecraftServer.terminateServer();
	}
	
	
	// Uncomment when it works!
	//@Test
	@Test
	public void testFitnessFromBlocks() {
		MinecraftCoordinates corner = new MinecraftCoordinates(0,5,0); //Initializes corner for testing
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, MinecraftUtilClass.getRanges(), 2, 100);
		
		blockSet1 = new ArrayList<>();
		blockSet2 = new ArrayList<>();
		blockSet1.add(new Block(10,15,10,BlockType.AIR, Orientation.WEST));
		assertEquals(-1,ff.fitnessFromBlocks(corner,blockSet1),0); // Test when nothing
		
		blockSet1.add(new Block(0,5,0,BlockType.GLOWSTONE, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(1,5,0,BlockType.GLOWSTONE, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(1,5,1,BlockType.GLOWSTONE, Orientation.WEST));
		blockSet1.add(new Block(0,5,1,BlockType.AIR, Orientation.WEST));
		assertEquals(1,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(0,6,1,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(0,6,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(1,6,0,BlockType.AIR, Orientation.WEST));
		assertEquals(1,ff.fitnessFromBlocks(corner,blockSet1),0); // Air above blocks does not effect the negative space
		blockSet1.add(new Block(1,6,1,BlockType.CRAFTING_TABLE, Orientation.WEST));
		assertEquals(4,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(1,7,1,BlockType.CRAFTING_TABLE, Orientation.WEST));
		blockSet1.add(new Block(0,7,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(1,7,0,BlockType.AIR, Orientation.WEST));
		blockSet1.add(new Block(0,7,1,BlockType.AIR, Orientation.WEST));
		assertEquals(7,ff.fitnessFromBlocks(corner,blockSet1),0);
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

		assertEquals(30,ff.fitnessFromBlocks(corner,blockSet1),0);
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1); // Spawns in just for verification
		
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
		assertEquals(500,ff.fitnessFromBlocks(corner2,blockSet2),0);
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2); // Spawns in just for verification
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, ranges, 2, 0);
	}
	
	@Test
	public void testMaximumFitness() {
		assertEquals(998,ff.maxFitness(),0);
	}

}
