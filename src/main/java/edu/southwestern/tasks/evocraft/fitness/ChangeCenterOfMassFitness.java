package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Vertex;
/**
 * Calculates the changes in the center of mass of
 * a given structure. If the structure is a flying machine
 * then it will have a positive non-zero fitness score (which is
 * dependent on the mandatory wait time parameter). Otherwise, the
 * structure is stagnant, meaning it has a fitness of 0.
 * @author Melanie Richey
 *
 */
public class ChangeCenterOfMassFitness extends TimedEvaluationMinecraftFitnessFunction {
	// Assume that the remaining block penalty will not be greater than this (should actually be much less)
	public static final double FLYING_PENALTY_BUFFER = 5;
	// At least this many blocks must depart to count as flying
	private static final int SUFFICIENT_DEPARTED_BLOCKS = 6;
	
	// Flying machines that leave blocks behind get a small fitness penalty proportional to the number of remaining blocks,
	// but scaled down to 10% of that.
	private static final double REMAINING_BLOCK_PUNISHMENT_SCALE = 0.1;
		
	@Override
	public double maxFitness() {
		// Probably overshoots a bit
		if(Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) return ((Parameters.parameters.longParameter("minecraftMandatoryWaitTime")/Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads")) + 1) * overestimatedDistanceToEdge();
		else return overestimatedDistanceToEdge();
	}
	
	/**
	 * About the distance from the center of the area the shape is generated in to
	 * the edge of the space the shape is generated in.
	 * 
	 * @return Overestimate of distance from center to edge
	 */
	public double overestimatedDistanceToEdge() {
		double oneDir = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes") + Math.max(Math.max(Parameters.parameters.integerParameter("minecraftXRange"), Parameters.parameters.integerParameter("minecraftYRange")),Parameters.parameters.integerParameter("minecraftZRange"));
		return oneDir / 2;
	}

	@Override
	public Double earlyEvaluationTerminationResult(MinecraftCoordinates corner, List<Block> originalBlocks,
			ArrayList<Pair<Long, List<Block>>> history, List<Block> newShapeBlockList) {

		//if using fast fitness return null
		if(Parameters.parameters.booleanParameter("minecraftRewardFastFlyingMachines")) return null;
		
		return fitnessResultForFlyingMachine(originalBlocks, history, newShapeBlockList);
	}

	private Double fitnessResultForFlyingMachine(List<Block> originalBlocks, ArrayList<Pair<Long, List<Block>>> history,
			List<Block> newShapeBlockList) {
		// Shape was not empty before, but it is now, so it must have flown away. Award max fitness
		if(newShapeBlockList.isEmpty()) { // If list is empty now (but was not before) then shape has flown completely away
			if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": Shape empty now: max fitness!");
			return maxFitness();
		}
		
		List<Block> previousBlocks = history.get(history.size() - 2).t2;
		Vertex initialCenterOfMass = MinecraftUtilClass.getCenterOfMass(originalBlocks);
		Vertex lastCenterOfMass = MinecraftUtilClass.getCenterOfMass(previousBlocks);
		Vertex nextCenterOfMass = MinecraftUtilClass.getCenterOfMass(newShapeBlockList);
		// Only consider the shape to not be moving if the center of mass is the same AND the entire block list is the same
		if(Parameters.parameters.booleanParameter("minecraftEndEvalNoMovement") && lastCenterOfMass.equals(nextCenterOfMass) && previousBlocks.equals(newShapeBlockList)) {
			// This means that it hasn't moved, so move on to the next.
			// BUT What if it moves back and forth and returned to its original position?
			if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": No movement.");
			// Compute farthest center of mass from history
			Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass, lastCenterOfMass);
			Double result = checkCreditForDepartedBlocks(originalBlocks.size(), initialCenterOfMass, farthestCenterOfMass, newShapeBlockList);
			if(result != null) {
				return result;
			}
		}
		
		return null;
	}
	
	@Override
	public double calculateFinalScore(ArrayList<Pair<Long,List<Block>>> history, MinecraftCoordinates corner, List<Block> originalBlocks) {
		Vertex initialCenterOfMass = MinecraftUtilClass.getCenterOfMass(originalBlocks);
		Vertex lastCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(0).t2);
		double totalChangeDistance = 0;
		for(int i = 1; i < history.size(); i++) {
			Vertex nextCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i).t2);
			//if evaluating and rewarding fast flying machines
			if(Parameters.parameters.booleanParameter("minecraftRewardFastFlyingMachines")) {
				//if the fitnessResult is not null, use it for calculating totalChangeDistance
				Double fitnessResult = fitnessResultForFlyingMachine(originalBlocks, history, history.get(i).t2);
				if(fitnessResult != null) { totalChangeDistance += fitnessResult; 
				//else calculate based on initial center of mass and the next center of mass to add to total change distance
				} else { totalChangeDistance += initialCenterOfMass.distance(nextCenterOfMass); }
			} else {
				totalChangeDistance += lastCenterOfMass.distance(nextCenterOfMass);
			}
			lastCenterOfMass = nextCenterOfMass;
		}
		
		// It is possible that blocks flew away, but some remaining component kept oscillating until the end. This is still a flying machine though.
		Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass, lastCenterOfMass);
		Double result = checkCreditForDepartedBlocks(originalBlocks.size(), initialCenterOfMass, farthestCenterOfMass, history.get(history.size() - 1).t2);
		if(result != null) return result;
		
		// Machine did not fly away
		double fitness = totalChangeDistance;		
		double changeInPosition = lastCenterOfMass.distance(initialCenterOfMass);
		assert !Double.isNaN(changeInPosition) : "Before: " + originalBlocks;
		if(!Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) {
			fitness = changeInPosition;		
		}
		return fitness;
	}	

	/**
	 * method that makes sure you are taking the farthestCenterOfMass from history
	 * @param history record of the shape
	 * @param initialCenterOfMass initial center of mass
	 * @param lastCenterOfMass center of mass at the last point
	 * @return center of ,ass that was the farthest away from the initial
	 */
	public Vertex getFarthestCenterOfMass(ArrayList<Pair<Long,List<Block>>> history, Vertex initialCenterOfMass,
			Vertex lastCenterOfMass) {
		Vertex farthestCenterOfMass = lastCenterOfMass; // Assume last location was farthest
		double farthestDistance = lastCenterOfMass.distance(initialCenterOfMass);
		for(Pair<Long,List<Block>> blocks : history) {
			Vertex v = MinecraftUtilClass.getCenterOfMass(blocks.t2);
			double distance = v.distance(initialCenterOfMass);
			if(distance > farthestDistance) {
				farthestDistance = distance;
				farthestCenterOfMass = v;
			}
		}
		return farthestCenterOfMass;
	}
	/**
	 * method that recognizes and punishes flying machines with leftover blocks
	 * @param initialBlockCount block count at origin
	 * @param initialCenterOfMass initial center of mass
	 * @param lastCenterOfMass center of mass at the last point
	 * @param newShapeBlockList a list of blocks after a recent update
	 * @return fitness after punishment for remaining blocks, or null if shape is deemed to not have flown away
	 */
	private Double checkCreditForDepartedBlocks(int initialBlockCount, Vertex initialCenterOfMass, Vertex lastCenterOfMass, List<Block> newShapeBlockList) {
		int remainingBlockCount = newShapeBlockList.size(); // Could be larger than initial due to extensions
		int departedBlockCount = initialBlockCount - remainingBlockCount; // Could be negative due to extensions
		Double result = null;
		// It should be hard to archive credit for flying, so make sure that the number of departed blocks is sufficiently high
		if(departedBlockCount > SUFFICIENT_DEPARTED_BLOCKS) {
			if(CommonConstants.watch) System.out.println("Enough have departed. departedBlockCount is "+departedBlockCount+ " from initialBlockCount of "+initialBlockCount);					
			// Ship flew so far away that we award max fitness, but penalize remaining blocks
			System.out.println(remainingBlockCount +" remaining blocks: max = " + maxFitness());
			result = maxFitness() - remainingBlockCount*REMAINING_BLOCK_PUNISHMENT_SCALE;
		}
		return result;
	}


	@Override
	public double minFitness() {
		return 0;
	}

	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:90 randomSeed:98 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftRewardFastFlyingMachines:true minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftNorthSouthOnly:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
