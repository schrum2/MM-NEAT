package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;

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
		fail("Not yet implemented");
	}

	@Test
	public void testMaxFitness() {
		fail("Not yet implemented");
	}
}