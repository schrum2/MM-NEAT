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
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class NegativeSpaceCountFitnessTest {
	MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"), 
			Parameters.parameters.integerParameter("minecraftYRange"),
			Parameters.parameters.integerParameter("minecraftZRange"));
	
	List<Block> blockSet1;
	List<Block> blockSet2;
	
	NegativeSpaceCountFitness ff;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6"});
		MinecraftServer.launchServer();
		MinecraftClient.getMinecraftClient();
	}
	
	@Before
	public void setUp() throws Exception {
		ff = new NegativeSpaceCountFitness();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		Thread.sleep(waitTime);
		
		MinecraftClient.terminateClientScriptProcess();
		MinecraftServer.terminateServer();
	}
	
	@Test
	public void testFitnessFromBlocks() {
		MinecraftCoordinates corner = new MinecraftCoordinates(0,5,0); //Initializes corner for testing
		
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(0,5,0,BlockType.GLOWSTONE, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(1,5,0,BlockType.GLOWSTONE, Orientation.WEST));
		assertEquals(0,ff.fitnessFromBlocks(corner,blockSet1),0);
		blockSet1.add(new Block(1,5,1,BlockType.GLOWSTONE, Orientation.WEST));
		blockSet1.add(new Block(0,5,1,BlockType.AIR, Orientation.WEST));
		
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		
		
		assertEquals(1,ff.fitnessFromBlocks(corner,blockSet1),0);
		
	}
	
	@Test
	public void testMaximumFitness() {
		assertEquals(998,ff.maxFitness(),0);
	}

}
