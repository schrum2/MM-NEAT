package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.experiment.post.MinecraftBlockCompareExperiment;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class MaximizeVolumeFitnessTest {

	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	MaximizeVolumeFitness testInstance;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet","minecraftAccumulateChangeInCenterOfMass:false"});
		if(!MinecraftServer.serverIsRunner()) {
			MinecraftServer.launchServer();
			MinecraftClient.getMinecraftClient();
		}
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}
	
	@Before
	public void setUp() throws Exception {
		testInstance = new MaximizeVolumeFitness();
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MinecraftServerTestTracker.decrementServerTestCount();
		
		// Only terminate the server if no other tests will need it
		if(MinecraftServerTestTracker.checkServerTestCount() == 0) {
			long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
			Thread.sleep(waitTime);
			
			MinecraftClient.terminateClientScriptProcess();
			MinecraftServer.terminateServer();
		}
		CommonConstants.watch = false; // Displays debugging info
	}
	
	@Test
	public void testNoInteraction() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftBlockListTextFile", "minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		
		String shapeOneFileName = Parameters.parameters.stringParameter("minecraftBlockListTextFile");
		File shapeOneTextFile = new File(shapeOneFileName);
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!

		ArrayList<Block> testBlockSet = new ArrayList<>();
		//add blocks using file here
		//List<Block> List1 = MinecraftBlockCompareExperiment.shiftBlocks(shapeOneTextFile,  MinecraftClient.POST_EVALUATION_SHAPE_CORNER);
		List<Block> listOne = MinecraftUtilClass.loadMAPElitesOutputFile(shapeOneTextFile);
		//MinecraftUtilClass.shiftBlocksBetweenCorners
		//move blocks over
		//add again
		//combine lists
		//
	
		assertEquals(0.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}
	
	@Test
	public void testWorkingFlyingMachine() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,7,-35,BlockType.SLIME, Orientation.WEST));
		testBlockSet.add(new Block(-20,7,-35,BlockType.QUARTZ_BLOCK, Orientation.EAST));
	
		assertEquals(0.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
