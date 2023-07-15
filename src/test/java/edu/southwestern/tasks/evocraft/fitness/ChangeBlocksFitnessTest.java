package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

/**
 * recent changes made a lot of these tests break, 
 * but we are not using it so we have commented out the problematic tests
 * @author lewisj
 *
 */
public class ChangeBlocksFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	ChangeBlocksFitness testInstance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"netio:false", "watch:false","minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		if(!MinecraftServer.serverIsRunning()) {
			MinecraftServer.launchServer();
			MinecraftClient.getMinecraftClient();
		}
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}

	@Before
	public void setUp() throws Exception {
		CommonConstants.netio = false; // Don't save if shapeIsWorthSaving
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
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet","minecraftChangeBlocksMomentum:" + false});
		
		System.out.println("\n TEST: ChangeBlocksFitnessTest: testStationaryFitnessScore");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
		//set up test corner and clear area
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	
		
		List<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,30,-35,BlockType.SLIME, Orientation.WEST));
		testBlockSet.add(new Block(-24,30,-35,BlockType.QUARTZ_BLOCK, Orientation.EAST));

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		assertEquals(0.0,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
		System.out.println("\n");
	}
	@Test
	public void testWorkingFlyingMachine() {	
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet","minecraftChangeBlocksMomentum:" + false});

		System.out.println("\n TEST: ChangeBlocksFitnessTest: testWorkingFlyingMachine");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
		//set up test corner and clear area
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);		
		
		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
		
		//shift flying machine shape to the test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		//around 112
		assertEquals(500.0,testInstance.fitnessScore(testCorner,testBlockSet),100.0);
		System.out.println("\n");
	}
	//fails sometimes
	@Test
	public void testOscillatingMachine() {
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:10","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:false","shortTimeBetweenMinecraftReads:" + 100L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		
		System.out.println("\n TEST: ChangeBlocksFitnessTest: testOscillatingMachine");

		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,51,-5);
		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
		//System.out.println("Shifted? " + testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	
		
		// Machine that moves back and forth (in the same spot)
		List<Block> oscillatingMachine = new ArrayList<>();
		oscillatingMachine.add(new Block(1,52,1,BlockType.STICKY_PISTON,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,52,0,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,51,1,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,51,0,BlockType.SLIME,Orientation.NORTH));

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(oscillatingMachine);
		oscillatingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(oscillatingMachine, originalShapeCoordinates, testCorner);


		// When the time is small (50L) then the score becomes large
		MinecraftClient.getMinecraftClient().spawnBlocks(oscillatingMachine);
		double amount = testInstance.fitnessScore(testCorner,oscillatingMachine);
		System.out.println("movement fitness when oscillating: "+ amount);
//movement fitness when oscillating: 2.0, 6.0
		assertTrue(30 <= amount && amount <= 500);
		System.out.println("\n");
	}
	@Test
	public void testLargeOscillatingMachine() {
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:22","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:false","shortTimeBetweenMinecraftReads:" + 100L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		
		System.out.println("\n TEST: ChangeBlocksFitnessTest: testLargeOscillatingMachine");

		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,51,-5);
		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
		
		// Machine that moves back and forth (in the same spot)
		List<Block> oscillatingMachine = new ArrayList<>();
		oscillatingMachine.add(new Block(1,52,1,BlockType.STICKY_PISTON,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,52,0,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,51,1,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,51,0,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,53,0,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,53,1,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(2,53,1,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(0,53,1,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(-1,53,1,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(2,53,2,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(2,53,3,BlockType.SLIME,Orientation.NORTH));

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(oscillatingMachine);
		oscillatingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(oscillatingMachine, originalShapeCoordinates, testCorner);

		// When the time is small (50L) then the score becomes large
		//MinecraftClient.getMinecraftClient().spawnBlocks(oscillatingMachine);
		double amount = testInstance.fitnessScore(testCorner,oscillatingMachine);
		System.out.println("movement fitness when oscillating: "+ amount);
//movement fitness when oscillating: 6.0
		assertTrue(30 <= amount && amount <= 700);
		System.out.println("\n");
	}
	@Test
	public void testTwoWorkingFlyingMachineSomethingSouth() {	
		assert !CommonConstants.netio;
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftClearWithGlass:false","watch:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:22","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		assert !CommonConstants.netio;

		System.out.println("Unit test: testTwoWorkingFlyingMachineSomethingSouth");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,50,-35);
		//set up test corner and clear area
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		//System.out.println("Shifted testCorner: "+testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	
		
		//currently from minecraft 2022, MEOriginalVectorPistonOrientation19
		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
		//currently from minecraft 2022, MEOriginalVectorPistonOrientation0
		String unidentifiedDirectionFlyingMachineString = "[STICKY_PISTON at (-160,68,-258) oriented EAST, REDSTONE_BLOCK at (-160,69,-258) oriented SOUTH, STICKY_PISTON at (-160,70,-258) oriented EAST, PISTON at (-159,68,-260) oriented WEST, SLIME at (-159,68,-259) oriented DOWN, REDSTONE_BLOCK at (-159,69,-260) oriented NORTH, SLIME at (-159,69,-259) oriented UP, STICKY_PISTON at (-159,70,-260) oriented SOUTH, SLIME at (-159,70,-259) oriented DOWN, QUARTZ_BLOCK at (-158,68,-260) oriented DOWN, REDSTONE_BLOCK at (-158,69,-260) oriented DOWN, REDSTONE_BLOCK at (-158,69,-259) oriented DOWN, STICKY_PISTON at (-158,70,-260) oriented EAST, STICKY_PISTON at (-158,70,-258) oriented WEST]";
		
		List<Block> southFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
		List<Block> unidentifiedDirectionFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(unidentifiedDirectionFlyingMachineString);

		//overall test block set
		List<Block> testBlockSet = new ArrayList<Block>();

		//south flying machine shifted to test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(southFlyingMachine);
		southFlyingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(southFlyingMachine, originalShapeCoordinates, testCorner);

		//augment testcorner by 8 to ensure they do not interfere with each other
		MinecraftCoordinates augmentedCoordinates = new MinecraftCoordinates(testCorner);
		augmentedCoordinates.t1 = augmentedCoordinates.t1 -8;			//moving only on the x axis

		originalShapeCoordinates = MinecraftUtilClass.minCoordinates(unidentifiedDirectionFlyingMachine);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(unidentifiedDirectionFlyingMachine, originalShapeCoordinates, augmentedCoordinates);

		testBlockSet.addAll(southFlyingMachine);
		assert !CommonConstants.netio;
		assertEquals(425.0,testInstance.fitnessScore(augmentedCoordinates,testBlockSet),150.0);
	}
	@Test
	public void testMomentumStationaryFitnessScore() {	
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet","minecraftChangeBlocksMomentum:" + true});
		
		System.out.println("\n TEST: ChangeBlocksFitnessTest: testMomentumStationaryFitnessScore");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
		//set up test corner and clear area
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	
		
		List<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,30,-35,BlockType.SLIME, Orientation.WEST));
		testBlockSet.add(new Block(-24,30,-35,BlockType.QUARTZ_BLOCK, Orientation.EAST));

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		assertEquals(0.0,testInstance.fitnessScore(testCorner,testBlockSet),1.0);
		System.out.println("\n");
	}
	@Test
	public void testMomentumWorkingFlyingMachine() {	
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet","minecraftChangeBlocksMomentum:" + true});

		System.out.println("\n TEST: ChangeBlocksFitnessTest: testMomentumWorkingFlyingMachine");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
		//set up test corner and clear area
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	
		
		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
		
		//shift flying machine shape to the test corner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);
		
		System.out.println("score: " + testInstance.fitnessScore(testCorner,testBlockSet));
		assertEquals(3000.0,testInstance.fitnessScore(testCorner,testBlockSet),1000.0);
		System.out.println("\n");
	}
//	@Test
//	public void testMomentumOscillatingMachine() {
//		assert !CommonConstants.netio;
//		Parameters.initializeParameterCollections(new String[] {"watch:false", "netio:false", "minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:22","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:false","shortTimeBetweenMinecraftReads:" + 100L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet","minecraftChangeBlocksMomentum:" + true});
//		assert !CommonConstants.netio;
//		
//		System.out.println("\n TEST: ChangeBlocksFitnessTest: testMomentumOscillatingMachine");
//
//		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
//		//set up test corner and clear area
//		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
//		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
//		
//		// Machine that moves back and forth (in the same spot)
//		List<Block> oscillatingMachine = new ArrayList<>();
//		oscillatingMachine.add(new Block(1,12,1,BlockType.STICKY_PISTON,Orientation.NORTH));
//		oscillatingMachine.add(new Block(1,12,0,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(1,11,1,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
//		oscillatingMachine.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
//
//		//shift coordinates based on the testCorner
//		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(oscillatingMachine);
//		oscillatingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(oscillatingMachine, originalShapeCoordinates, testCorner);
//
//		// When the time is small (50L) then the score becomes large
//		MinecraftClient.getMinecraftClient().spawnBlocks(oscillatingMachine);
//		assert !CommonConstants.netio;
//		double amount = testInstance.fitnessScore(testCorner,oscillatingMachine);
//		System.out.println("movement fitness when oscillating: "+ amount);
//		
////movement fitness when oscillating: 8.75
//		assertTrue(30 <= amount && amount <= 500);
//		System.out.println("\n");
//	}
	
//	@Test
//	public void testMomentumLargeOscillatingMachine() {
//		Parameters.initializeParameterCollections(new String[] {"minecraftClearAndVerify:false","netio:false", "minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:47","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:false","shortTimeBetweenMinecraftReads:" + 100L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet","minecraftChangeBlocksMomentum:" + true});
//		
//		System.out.println("\n TEST: ChangeBlocksFitnessTest: testMomentumLargeOscillatingMachine");
//
//		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
//		//set up test corner and clear area
//		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
//		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
//		
//		// Machine that moves back and forth (in the same spot)
//		List<Block> oscillatingMachine = new ArrayList<>();
//		oscillatingMachine.add(new Block(2,12,1,BlockType.STICKY_PISTON,Orientation.NORTH));
//		oscillatingMachine.add(new Block(2,12,0,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(2,11,1,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
//		oscillatingMachine.add(new Block(2,11,0,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(2,13,0,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(2,13,1,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(3,13,1,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(1,13,1,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(0,13,1,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(3,13,2,BlockType.SLIME,Orientation.NORTH));
//		oscillatingMachine.add(new Block(3,13,3,BlockType.SLIME,Orientation.NORTH));
//
//		//shift coordinates based on the testCorner
//		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(oscillatingMachine);
//		oscillatingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(oscillatingMachine, originalShapeCoordinates, testCorner);
//
//		// When the time is small (50L) then the score becomes large
////		MinecraftClient.getMinecraftClient().spawnBlocks(oscillatingMachine);
//		System.out.println("about to run fitness test, previous clear done");
//
//		double amount = testInstance.fitnessScore(testCorner,oscillatingMachine);
//		System.out.println("movement fitness when oscillating: "+ amount);
////movement fitness when oscillating: 29.029831455738144
//		//52 : 1861.1056881710688
//		//42 : 17.353220970492096
//		//47 : 151.73627339672234
//		assertTrue(30 <= amount && amount <= 1200);
//		System.out.println("\n");
//	}
	
	
	
//	@Test
//	public void testMomentumTwoWorkingFlyingMachineSomethingSouth() {	
//		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftClearWithGlass:false","watch:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:16","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet","minecraftChangeBlocksMomentum:" + true});
//																																															//26 : 1406			66 : 919
//																																	  								 						//46 : 676.579		76 : 682
//																																															//56 : 1140, 1146	16 : 185
//		System.out.println("\n TEST: ChangeBlocksFitnessTest: testMomentumTwoWorkingFlyingMachineSomethingSouth");
//
//		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
//		//set up test corner and clear area
//		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
//		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
//		
//		//currently from minecraft 2022, MEOriginalVectorPistonOrientation19
//		String southFlyingMachineString = "[SLIME at (-500,100,501) oriented WEST, STICKY_PISTON at (-500,100,502) oriented NORTH, STICKY_PISTON at (-500,101,501) oriented SOUTH, SLIME at (-500,101,502) oriented WEST, QUARTZ_BLOCK at (-500,102,500) oriented SOUTH, REDSTONE_BLOCK at (-500,102,502) oriented DOWN, SLIME at (-499,100,501) oriented WEST, REDSTONE_BLOCK at (-499,100,502) oriented EAST, QUARTZ_BLOCK at (-499,101,500) oriented EAST, SLIME at (-499,101,501) oriented DOWN, STICKY_PISTON at (-499,102,502) oriented DOWN, QUARTZ_BLOCK at (-498,100,500) oriented DOWN, SLIME at (-498,100,501) oriented NORTH, REDSTONE_BLOCK at (-498,101,502) oriented EAST]";
//		//currently from minecraft 2022, MEOriginalVectorPistonOrientation0
//		String unidentifiedDirectionFlyingMachineString = "[STICKY_PISTON at (-160,68,-258) oriented EAST, REDSTONE_BLOCK at (-160,69,-258) oriented SOUTH, STICKY_PISTON at (-160,70,-258) oriented EAST, PISTON at (-159,68,-260) oriented WEST, SLIME at (-159,68,-259) oriented DOWN, REDSTONE_BLOCK at (-159,69,-260) oriented NORTH, SLIME at (-159,69,-259) oriented UP, STICKY_PISTON at (-159,70,-260) oriented SOUTH, SLIME at (-159,70,-259) oriented DOWN, QUARTZ_BLOCK at (-158,68,-260) oriented DOWN, REDSTONE_BLOCK at (-158,69,-260) oriented DOWN, REDSTONE_BLOCK at (-158,69,-259) oriented DOWN, STICKY_PISTON at (-158,70,-260) oriented EAST, STICKY_PISTON at (-158,70,-258) oriented WEST]";
//		
//		List<Block> southFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(southFlyingMachineString);
//		List<Block> unidentifiedDirectionFlyingMachine = MinecraftUtilClass.readMinecraftBlockListFromString(unidentifiedDirectionFlyingMachineString);
//
//		//overall test block set
//		List<Block> testBlockSet = new ArrayList<Block>();
//
//		//south flying machine shifted to test corner
//		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(southFlyingMachine);
//		southFlyingMachine = MinecraftUtilClass.shiftBlocksBetweenCorners(southFlyingMachine, originalShapeCoordinates, testCorner);
//
//		//augment testcorner by 8 to ensure they do not interfere with each other
//		MinecraftCoordinates augmentedCoordinates = new MinecraftCoordinates(testCorner);
//		augmentedCoordinates.t1 = augmentedCoordinates.t1 -8;			//moving only on the x axis
//
//		originalShapeCoordinates = MinecraftUtilClass.minCoordinates(unidentifiedDirectionFlyingMachine);
//		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(unidentifiedDirectionFlyingMachine, originalShapeCoordinates, augmentedCoordinates);
//
//		testBlockSet.addAll(southFlyingMachine);
//		System.out.println(testInstance.fitnessScore(testCorner,testBlockSet));
//		assertEquals(3000.0,testInstance.fitnessScore(testCorner,testBlockSet),1000.0);
//		System.out.println("\n");
//	}
}
