package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

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
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
