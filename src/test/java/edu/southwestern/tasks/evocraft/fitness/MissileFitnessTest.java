package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.*;

import java.util.ArrayList;

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

public class MissileFitnessTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.CannonBlockSet", "minecraftTargetDistancefromShapeY:-10", "minecraftTargetDistancefromShapeX:0", "minecraftTargetDistancefromShapeZ:0"});
		if(!MinecraftServer.serverIsRunner()) {
			MinecraftServer.launchServer();
			MinecraftClient.getMinecraftClient();
		}
		CommonConstants.watch = false; // TOO MUCH DEBUGGING INFO // Displays debugging info
	}

	@Before
	public void setUp() throws Exception {
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
	//commented out to deal with freezing issue
	@Test
	public void testNoTNTFitnessScore() {	//TODO: freezing
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.CannonBlockSet", "minecraftTargetDistancefromShapeY:0", "minecraftTargetDistancefromShapeX:-10", "minecraftTargetDistancefromShapeZ:0"});
		MissileFitness testInstance = new MissileFitness();
		System.out.println("\n TEST: MissileFitnessTest: testNoTNTFitnessScore");

		System.out.println("GO");
		System.out.println("X:" + Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeX") + " Y:" + Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeY") + " Z:" + Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeZ"));
		System.out.println("1");
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		System.out.println("2");
		
//		System.out.println("trying to sleep");
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("after sleep");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
//		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!
		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,30,-35,BlockType.OBSIDIAN, Orientation.WEST));
		testBlockSet.add(new Block(-24,30,-35,BlockType.REDSTONE_BLOCK, Orientation.EAST));
		
		int min = (ranges.x()) * (ranges.z()) * (ranges.y());	
		System.out.println("This is Ranges.x:" + ranges.x());
		assertEquals(-min ,testInstance.fitnessScore(testCorner,testBlockSet),0.0);
		System.out.println("\n");
	}
	@Test
	public void testdropTNTFitnessScore() {	
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.CannonBlockSet", "minecraftTargetDistancefromShapeY:0", "minecraftTargetDistancefromShapeX:0", "minecraftTargetDistancefromShapeZ:0"});
		MissileFitness testInstance = new MissileFitness();
		
		System.out.println("\n TEST: MissileFitnessTest: testdropTNTFitnessScore");

		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!
		ArrayList<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,30,-35,BlockType.TNT, Orientation.WEST));
		testBlockSet.add(new Block(-24,30,-35,BlockType.REDSTONE_BLOCK, Orientation.EAST));
		
		int min = (ranges.x()) * (ranges.z()) * (ranges.y());	
		//System.out.println("This is Ranges.x:" + ranges.x());
		assertTrue(-min < testInstance.fitnessScore(testCorner, testBlockSet));
		System.out.println("\n");
	}
//	@Test
//	public void testshootTNTFitnessScore() {	
//		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","watch:false","minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.CannonBlockSet", "minecraftTargetDistancefromShapeY:0", "minecraftTargetDistancefromShapeX:0", "minecraftTargetDistancefromShapeZ:0"});
//		MissileFitness testInstance = new MissileFitness();
//		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
//		
//		System.out.println("Unit test: testNoTNTFitnessScore");
//
//		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,30,-35);
//		MinecraftClient.getMinecraftClient().clearSpaceForShapes(testCorner, ranges, 1, 50); // Larger buffer is important, but too large and it crashes!
//		ArrayList<Block> testBlockSet = new ArrayList<>();
//		//testBlockSet.add(new Block(-25,30,-35,BlockType.TNT, Orientation.WEST));
//		testBlockSet.add(new Block(-24,29,-35,BlockType.OBSIDIAN, Orientation.EAST));
//		testBlockSet.add(new Block(-23,30,-34,BlockType.OBSIDIAN, Orientation.EAST));
//		testBlockSet.add(new Block(-23,31,-34,BlockType.OBSIDIAN, Orientation.EAST));
//		//testBlockSet.add(new Block(-24,30,-35,BlockType.OBSIDIAN, Orientation.EAST));
//		//testBlockSet.add(new Block(-24,30,-35,BlockType.OBSIDIAN, Orientation.EAST));
//		
//		testBlockSet.add(new Block(-24,30,-35,BlockType.REDSTONE_BLOCK, Orientation.EAST));
//		testBlockSet.add(new Block(-24,31,-35,BlockType.TNT, Orientation.EAST));
//		testBlockSet.add(new Block(-24,32,-35,BlockType.TNT, Orientation.EAST));
//		testBlockSet.add(new Block(-24,33,-35,BlockType.TNT, Orientation.EAST));
//		
//		int min = (ranges.x()) * (ranges.z()) * (ranges.y());	
//		//System.out.println("This is Ranges.x:" + ranges.x());
//		assertTrue(-min < testInstance.fitnessScore(testCorner, testBlockSet));
//	}
}
