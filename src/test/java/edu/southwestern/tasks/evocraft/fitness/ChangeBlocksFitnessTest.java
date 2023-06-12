package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class ChangeBlocksFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	ChangeBlocksFitness testInstance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
//		if(!MinecraftServer.serverIsRunner()) {
//			MinecraftServer.launchServer();
//			MinecraftClient.getMinecraftClient();
//		}
//		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}

	@Before
	public void setUp() throws Exception {
		testInstance = new ChangeBlocksFitness();
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
	public void testStationaryFitnessScore() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet","minecraftChangeBlocksMomentum:" + false});
		
		System.out.println("Unit test: testStationaryFitnessScore");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!

		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,30,-35,BlockType.SLIME, Orientation.WEST));
		testBlockSet.add(new Block(-24,30,-35,BlockType.QUARTZ_BLOCK, Orientation.EAST));
				
		assertEquals(0.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}
	@Test
	public void testWorkingFlyingMachine() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet","minecraftChangeBlocksMomentum:" + false});

		System.out.println("Unit test: testWorkingFlyingMachine");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!
		
		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
		//shift flying machine shape to the test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		assertEquals(125.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
	}

}
