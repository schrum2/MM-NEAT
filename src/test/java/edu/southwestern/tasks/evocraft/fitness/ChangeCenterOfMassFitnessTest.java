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
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.datastructures.Vertex;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class ChangeCenterOfMassFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	
	List<Block> blockSet1;
	List<Block> blockSet2;
	List<Block> oscillatingMachine;

	ChangeCenterOfMassFitness ff;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"watch:true","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		MinecraftServer.launchServer();
		MinecraftClient.getMinecraftClient();
		CommonConstants.watch = true; // Displays debugging info
	}

	@Before
	public void setUp() throws Exception {
		ff = new ChangeCenterOfMassFitness();
		CommonConstants.watch = true; // Displays debugging info
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		Thread.sleep(waitTime);
		
		MinecraftClient.terminateClientScriptProcess();
		MinecraftServer.terminateServer();
		CommonConstants.watch = false; // Displays debugging info
	}

	// Passes
	@Test
	public void testStagnantStructureQuickly() {
		ChangeCenterOfMassFitness.resetPreviousResults();
		MinecraftCoordinates cornerBS1 = new MinecraftCoordinates(-26,7,-35);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS1, ranges, 1, 100); // Larger buffer is important

		// Small list of blocks that don't move
		// Should have a fitness of 0
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(-25,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		blockSet1.add(new Block(-24,7,-35,BlockType.PISTON, Orientation.EAST));
		
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
	
		assertEquals(0.0, ff.fitnessScore(cornerBS1),0.0);
		Triple<Vertex, Vertex, Double> beforeAndAfter = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS1); // Prevent lock
		assertEquals(0.0, beforeAndAfter.t3, 0.0);
		
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS1, ranges, 1, 100); // Larger buffer is important
		
	}
	
	@Test
	public void testSimpleCases() {
		ChangeCenterOfMassFitness.resetPreviousResults();
		Parameters.initializeParameterCollections("minecraftXRange:4 minecraftYRange:4 minecraftZRange:4 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:false netio:false interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:5 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels".split(" "));
		

		
		MinecraftCoordinates cornerBS1 = new MinecraftCoordinates(0,5,0);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS1, ranges, 1, 100); // Larger buffer is important
		
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(0,5,0,BlockType.REDSTONE_BLOCK,Orientation.SOUTH));
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		double fitness = ff.fitnessScore(cornerBS1);
		System.out.println("fitness = "+fitness);
		assertTrue(fitness == 0.0);
		Triple<Vertex, Vertex, Double> beforeAndAfter = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS1); // Prevent lock
		assertTrue(beforeAndAfter.t3 == 0.0);
		
		blockSet1.add(new Block(1,5,0,BlockType.PISTON,Orientation.SOUTH));
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		double fitness2 = ff.fitnessScore(cornerBS1);
		System.out.println("fitness = "+fitness2);
		assertTrue(fitness < 0.1);
		Triple<Vertex, Vertex, Double> beforeAndAfter2 = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS1); // Prevent lock
		assertTrue(beforeAndAfter2.t3 < 0.1);
	}
	
	@Test
	public void testBigSmallMove() {
		ChangeCenterOfMassFitness.resetPreviousResults();
		Parameters.initializeParameterCollections("minecraftXRange:4 minecraftYRange:4 minecraftZRange:4 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:false netio:false interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:5 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels".split(" "));
		

		
		MinecraftCoordinates cornerBS1 = new MinecraftCoordinates(28,33,28);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS1, ranges, 1, 100); // Larger buffer is important

		blockSet1 = new ArrayList<>();
		
		blockSet1.add(new Block(29,35,29,BlockType.REDSTONE_BLOCK,Orientation.SOUTH)); 
		blockSet1.add(new Block(29,35,31,BlockType.PISTON,Orientation.DOWN)); 
		blockSet1.add(new Block(29,36,30,BlockType.STICKY_PISTON,Orientation.EAST)); 
		blockSet1.add(new Block(29,36,32,BlockType.OBSERVER,Orientation.UP)); 
		blockSet1.add(new Block(29,37,32,BlockType.OBSERVER,Orientation.WEST)); 
		blockSet1.add(new Block(30,34,32,BlockType.PISTON,Orientation.SOUTH)); 
		blockSet1.add(new Block(30,35,31,BlockType.SLIME,Orientation.EAST)); 
		blockSet1.add(new Block(30,36,30,BlockType.QUARTZ_BLOCK,Orientation.NORTH)); 
		blockSet1.add(new Block(30,37,29,BlockType.STICKY_PISTON,Orientation.UP)); 
		blockSet1.add(new Block(30,37,30,BlockType.OBSERVER,Orientation.SOUTH)); 
		blockSet1.add(new Block(30,37,32,BlockType.PISTON,Orientation.DOWN)); 
		blockSet1.add(new Block(31,34,30,BlockType.OBSERVER,Orientation.EAST)); 
		blockSet1.add(new Block(31,34,31,BlockType.STICKY_PISTON,Orientation.UP)); 
		blockSet1.add(new Block(31,34,32,BlockType.SLIME,Orientation.NORTH)); 
		blockSet1.add(new Block(31,35,29,BlockType.QUARTZ_BLOCK,Orientation.DOWN)); 
		blockSet1.add(new Block(31,35,30,BlockType.QUARTZ_BLOCK,Orientation.WEST)); 
		blockSet1.add(new Block(31,35,31,BlockType.SLIME,Orientation.NORTH)); 
		blockSet1.add(new Block(31,36,31,BlockType.STICKY_PISTON,Orientation.DOWN)); 
		blockSet1.add(new Block(31,37,32,BlockType.SLIME,Orientation.WEST)); 
		blockSet1.add(new Block(32,35,29,BlockType.PISTON,Orientation.EAST)); 
		blockSet1.add(new Block(32,35,30,BlockType.QUARTZ_BLOCK,Orientation.WEST)); 
		blockSet1.add(new Block(32,35,31,BlockType.REDSTONE_BLOCK,Orientation.WEST)); 
		blockSet1.add(new Block(32,35,32,BlockType.QUARTZ_BLOCK,Orientation.NORTH)); 
		blockSet1.add(new Block(32,36,32,BlockType.REDSTONE_BLOCK,Orientation.DOWN)); 
		blockSet1.add(new Block(32,37,29,BlockType.PISTON,Orientation.WEST)); 
		blockSet1.add(new Block(32,37,30,BlockType.STICKY_PISTON,Orientation.NORTH)); 
		blockSet1.add(new Block(32,37,32,BlockType.REDSTONE_BLOCK,Orientation.DOWN));		
		
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		double fitness = ff.fitnessScore(cornerBS1);
		System.out.println("fitness = "+fitness);
		assertTrue(fitness < 0.4);
		Triple<Vertex, Vertex, Double> beforeAndAfter = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS1); // Prevent lock
		assertTrue(beforeAndAfter.t3 < 0.4);
		
		
	}
	
	// Passes
	@Test
	public void testChangeInTotalDistance() throws InterruptedException {
		ChangeCenterOfMassFitness.resetPreviousResults();

		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
		
		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,11,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,11,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,12,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,12,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,12,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		System.out.println("shortTimeBetweenMinecraftReads = " + Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads"));
		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);

		// Since it is moving out completely, and all the ranges are the same value (10)
		// That means the max fitness is 10 + 6 / 2 = 8
		// However, the movement speed of the flying machine depends on the speed of the independently
		// executing Minecraft server, which is subject to variation. The main point is that the ship
		// flies for a bit, but the exact amount is hard to pin down. Thus, we only assert that the amount
		// is 6.0 or more
		//System.out.println("Fitness for the blockSet 2: "+ ff.fitnessScore(cornerBS2));
		assertEquals(ff.maxFitness(), ff.fitnessScore(cornerBS2),0.0);
		Triple<Vertex, Vertex, Double> beforeAndAfter = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS2); // Prevent lock
		assertEquals(ff.maxFitness(), beforeAndAfter.t3, 0.0);
		
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
	}

		
	// Passes
	@Test
	public void testChangeInPositionWithRemainingBlocks() {
		ChangeCenterOfMassFitness.resetPreviousResults();

		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:false","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
		
		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,11,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,11,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,12,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,12,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,12,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));

		// Left-over garbage: 3 blocks
		blockSet2.add(new Block(2,10,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(2,10,0,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(2,10,1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));

		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);

		// Three blocks remaining, so -0.3
		assertEquals(ff.maxFitness()-0.3, ff.fitnessScore(cornerBS2), 0.0);
		Triple<Vertex, Vertex, Double> beforeAndAfter = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS2); // Prevent lock
		assertEquals(ff.maxFitness()-0.3, beforeAndAfter.t3, 0.0);
		
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
	}
	
	
	// Passes
	@Test
	public void testChangeInPosition() {
		ChangeCenterOfMassFitness.resetPreviousResults();

		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:false","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
		
		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,11,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,11,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,12,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,12,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,12,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));


		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);
		// Machine flies away completely
		//System.out.println("Second flying machine fitness: " + ff.fitnessScore(cornerBS2));
		assertEquals(ff.maxFitness(), ff.fitnessScore(cornerBS2), 0.0);
		Triple<Vertex, Vertex, Double> beforeAndAfter = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS2); // Prevent lock
		assertEquals(ff.maxFitness(), beforeAndAfter.t3, 0.0);
		
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
	}
	
	// Passes
	// This test seems to pass reliably in isolation, but not always when run as part of the test suite, so it is disabled.
	@Test
	public void testOscillatingMachine() {
		ChangeCenterOfMassFitness.resetPreviousResults();

		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:false","shortTimeBetweenMinecraftReads:" + 100L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);

		// Machine that moves back and forth (in the same spot)
		oscillatingMachine = new ArrayList<>();
		oscillatingMachine.add(new Block(1,12,1,BlockType.STICKY_PISTON,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,12,0,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,11,1,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));

		// When the time is small (50L) then the score becomes large
		MinecraftClient.getMinecraftClient().spawnBlocks(oscillatingMachine);
		double amount = ff.fitnessScore(cornerBS2);
		Triple<Vertex, Vertex, Double> beforeAndAfter = ChangeCenterOfMassFitness.getPreviouslyComputedResult(cornerBS2); // Prevent lock
		System.out.println("beforeAndAfter.t3 = " + beforeAndAfter.t3);
		assertTrue(30 <= beforeAndAfter.t3);
		
		System.out.println("movement fitness when oscillating: "+ amount);
		assertTrue(30 <= amount);
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
	}

	// Passes
	@Test
	public void testGetCenterOfMass() {
		ChangeCenterOfMassFitness.resetPreviousResults();

		// Small list of blocks
		blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(-5,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		blockSet1.add(new Block(-4,7,-35,BlockType.PISTON, Orientation.EAST));
		
		
		assertEquals(new Vertex(-4.5,7.0,-35.0), ChangeCenterOfMassFitness.getCenterOfMass(blockSet1));
		
		
		// List of flying machine blocks
		blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,5,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,5,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,6,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,6,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,6,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		assertEquals(new Vertex(1.0,5.375,-1.375), ChangeCenterOfMassFitness.getCenterOfMass(blockSet2));
	}

}
