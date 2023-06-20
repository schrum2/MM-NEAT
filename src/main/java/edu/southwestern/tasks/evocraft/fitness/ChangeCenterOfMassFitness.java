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
		if(Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) return (minNumberOfShapeReadings() + 1) * overestimatedDistanceToEdge();
		else return overestimatedDistanceToEdge();
	}
	
	/**
	 * About the distance from the center of the area the shape is generated in to
	 * the edge of the space the shape is generated in.
	 * 
	 * @return Overestimate of distance from center to edge
	 */
	public double overestimatedDistanceToEdge() {	//the (max between given x,y,z ranges plus the spaceBetweenMinecraftShapes) divided by 2
		double oneDir = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes") + Math.max(Math.max(Parameters.parameters.integerParameter("minecraftXRange"), Parameters.parameters.integerParameter("minecraftYRange")),Parameters.parameters.integerParameter("minecraftZRange"));
		return oneDir / 2;
	}

	@Override
	public Double earlyEvaluationTerminationResult(MinecraftCoordinates corner, List<Block> originalBlocks,
			ArrayList<Pair<Long, List<Block>>> history, List<Block> newShapeBlockList) {

		//if using fast fitness return null, prevents early termination
		if(Parameters.parameters.booleanParameter("minecraftRewardFastFlyingMachines")) return null;

		return fitnessResultForFlyingMachine(originalBlocks, history, newShapeBlockList);
	}

	/**
	 * calculates the fitness for flying machines
	 * Uses the change in center of mass and block lists to determine if the shape has flown away. 
	 * If it has it awards max fitness with penalties for leftover blocks
	 * Otherwise it returns null
	 * @param originalBlocks the list of original blocks
	 * @param history a list of time stamps paired with a list of the blocks present at that time
	 * @param newShapeBlockList the most recently recorded list of blocks in the evaluation area (last entry in history)
	 * @return fitness after checking for departed blocks or null if shape has not flown away
	 */
	private Double fitnessResultForFlyingMachine(List<Block> originalBlocks, ArrayList<Pair<Long, List<Block>>> history,
			List<Block> newShapeBlockList) {
		
		//check if there has been any movement, if not, calculate final score now
		if(!shapeMovedForMostOfHistory(history)) {
			if(CommonConstants.watch) System.out.println("shape not moving, calculate final score early");
			MinecraftCoordinates shapeCorner = MinecraftUtilClass.minCoordinates(originalBlocks);
			return calculateFinalScore(history, shapeCorner, originalBlocks);
		}

		// Shape was not empty before, but it is now, so it must have flown away. Award max fitness
		if(newShapeBlockList.isEmpty()) { // If list is empty now (but was not before) then shape has flown completely away
			if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": Shape empty now: max fitness!");
			// TODO: This is a problem if the whole shape explodes!

			return maxFitness();
		}
		
		List<Block> previousBlocks = history.get(history.size() - 2).t2; // the block list for the second to last 
		Vertex lastCenterOfMass = MinecraftUtilClass.getCenterOfMass(previousBlocks);
		Vertex nextCenterOfMass = MinecraftUtilClass.getCenterOfMass(newShapeBlockList);
		
		// Only consider the shape to not be moving if the center of mass is the same AND the entire block list is the same
		if(Parameters.parameters.booleanParameter("minecraftEndEvalNoMovement") && lastCenterOfMass.equals(nextCenterOfMass) && previousBlocks.equals(newShapeBlockList)) {
			// This means that it hasn't moved, so move on to the next.
			// BUT What if it moves back and forth and returned to its original position?
			if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": No movement.");
			
			// check if it's missing any blocks from the original
			// TODO: Could this also have a problem when most of the shape explodes?
			Double result = ifSufficientBlocksDepartedThenMaximumFitnessWithPenalty(originalBlocks.size(), newShapeBlockList);
			if(result != null) {
				//if(CommonConstants.watch) System.out.println("return result:"+result);
				return result;
			}		
		}
//		if(CommonConstants.watch) System.out.println("return null");
		return null;
	}
	
	/**
	 * This checks that a shape is actually moving by going through its history.
	 * returns false if the shape has the same center of mass consistently and same block list.
	 * returns true if it reads more movement (3/4 of readings) than not
	 * measures the center of mass and block list, counts the number of movement reads and no movement reads, compares movement reads when returning false
	 * it does not compare movement reads when returning true, it may not need to compare the number of blocks
	 * @param history the list of time stamps and related block list readings at those times
	 * @return true for moving, false for not moving
	 */
	private boolean shapeMovedForMostOfHistory(ArrayList<Pair<Long, List<Block>>> history) {
		int noMovementCount = 0;
		int yesMovementCount = 0;
		
		if(CommonConstants.watch) System.out.println("history size: " + history.size());
		if(CommonConstants.watch) System.out.println("oscillating? " + oscillatingOrFlying(history));

		Vertex initialCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(0).t2);
		// First two entries are always duplicates of each other, but the first has orientation information
		for(int i = 2; i < history.size(); i++) {
			//if(CommonConstants.watch) System.out.println("noMovementCount: " + noMovementCount + " yesMovementCount: " + yesMovementCount + " i: " + i + " actual number of loops: " + (i-2));
			//if(CommonConstants.watch) System.out.println(" i: " + i + " actual number of loops: " + (i-1));

			//check for a change in the center of mass
			Vertex lastCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i-1).t2);
			Vertex nextCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i).t2);			
			// Only consider the shape to not be moving if the center of mass is the same AND the entire block list is the same
			// if(CommonConstants.watch) System.out.println( "mass: last==next:" + lastCenterOfMass.equals(nextCenterOfMass) + " previous history vs i history:"+ history.get(i-1).t2.equals(history.get(i).t2));

			if(lastCenterOfMass.equals(nextCenterOfMass) && history.get(i-1).t2.equals(history.get(i).t2)) {		//this is no movement & blocks are equal
				noMovementCount++;
			} else {
				yesMovementCount++;
			}
		}
		
		// If 3/4 of all moments in the evaluation history do not involve movement, then the shape is not moving
		if(noMovementCount > 0.75*(history.size()-2)) {
			return false;
		}
		
		Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass);
		if(CommonConstants.watch) System.out.println("farthest center of mass : " + farthestCenterOfMass);
		if(CommonConstants.watch) System.out.println("noMovementCount: " + noMovementCount + " yesMovementCount: " + yesMovementCount);

		Double result = checkCreditForDepartedBlocks(history.get(0).t2.size(), initialCenterOfMass, farthestCenterOfMass, history.get(history.size()-2).t2);
		if(CommonConstants.watch) System.out.println("result : " + result);
		if(result == null) {
			return false;
		}
		return true;
	}
	
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
	
	//placeholder for now
	//just checks if something is oscillating but if checked to soon just says it isn't
	public boolean oscillatingOrFlying(ArrayList<Pair<Long, List<Block>>> history) {
		//seting up relevant vertices
		if(history.size() < 4) {
			return false;
		}
		Vertex initialCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(0).t2);		
		Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass);
		
		Double result = checkCreditForDepartedBlocks(history.get(0).t2.size(), initialCenterOfMass, farthestCenterOfMass, history.get(history.size()-2).t2);
		if(result != null) {
			return false;
		}

		/**
		 *check if I need all 3
		Vertex initialCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(0).t2);
		//		Vertex initialCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(0).t2);
		Vertex lastCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(history.size()-3).t2);
		Vertex nextCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(history.size()-2).t2);

		Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass, lastCenterOfMass);

		if(CommonConstants.watch) System.out.println("farthest center of mass : " + farthestCenterOfMass);
		if(farthestCenterOfMass.equals(initialCenterOfMass)) {
			if(CommonConstants.watch) System.out.println("first case farthest == initial : " + farthestCenterOfMass + " " + initialCenterOfMass);
		}
		if(lastCenterOfMass.equals(initialCenterOfMass)) {
			if(CommonConstants.watch) System.out.println("farthest == initial : " + farthestCenterOfMass + " " + initialCenterOfMass);
		} 
		AND IN SHAPE HISTORY CHECK
		Vertex REMOVElastCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i-1).t2);
			Vertex REMOVEnextCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i).t2);
			lastCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i-1).t2);
			nextCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i).t2);

			// Only consider the shape to not be moving if the center of mass is the same AND the entire block list is the same
			if((REMOVElastCenterOfMass.equals(REMOVEnextCenterOfMass) && history.get(i-1).t2.equals(history.get(i).t2))) {		//this is no movement
				if(CommonConstants.watch) System.out.println("TESTING FOR RETURN FALSE / center of mass i-1 == i, blocks i-1 == i");
				//return true;
				//return false;
				 * 
	* if(Parameters.parameters.booleanParameter("minecraftEndEvalNoMovement") && lastCenterOfMass.equals(nextCenterOfMass) && previousBlocks.equals(newShapeBlockList)) {
if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": No movement.");
			// Compute farthest center of mass from history
			Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass, lastCenterOfMass);
			Double result = checkCreditForDepartedBlocks(originalBlocks.size(), initialCenterOfMass, farthestCenterOfMass, newShapeBlockList);
			if(result != null) {
				return result;
			}
		 */
		return true;
	}

	@Override
	public double calculateFinalScore(ArrayList<Pair<Long,List<Block>>> history, MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {
		if(CommonConstants.watch) System.out.println("XX	CALC FINAL SCORE	");

		Vertex initialCenterOfMass = MinecraftUtilClass.getCenterOfMass(originalBlocks);
		Vertex lastCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(0).t2);
		double totalChangeDistance = 0;

		for(int i = 1; i < history.size(); i++) {
			Vertex nextCenterOfMass = MinecraftUtilClass.getCenterOfMass(history.get(i).t2);
			
			//if evaluating and rewarding fast flying machines
			if(Parameters.parameters.booleanParameter("minecraftRewardFastFlyingMachines")) {
				Double fitnessResult = fitnessResultForFlyingMachine(originalBlocks, history, history.get(i).t2);
				if(fitnessResult != null) { // means machine was flying
					totalChangeDistance += fitnessResult; // accumulate large fitness for each reading where the machine is a confirmed flying machine
				} else { // emphasize getting away from starting point as fast as possible
					totalChangeDistance += initialCenterOfMass.distance(nextCenterOfMass); 
				}
			} else { // accumulate distance moved
				totalChangeDistance += lastCenterOfMass.distance(nextCenterOfMass);
			}
			lastCenterOfMass = nextCenterOfMass;
		}

		// It is possible that blocks flew away, but some remaining component kept oscillating until the end. This is still a flying machine though.
		Double result = ifSufficientBlocksDepartedThenMaximumFitnessWithPenalty(originalBlocks.size(), history.get(history.size() - 1).t2);
		if(result != null) return result;	//if there are enough departed blocks to count as flying, return the resulting score
		
		// Machine did not fly away
		double fitness = totalChangeDistance;		
		double changeInPosition = lastCenterOfMass.distance(initialCenterOfMass);
		assert !Double.isNaN(changeInPosition) : "Before: " + originalBlocks;
		if(!Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) {
			fitness = changeInPosition;		
		}
		if(CommonConstants.watch) System.out.println("RETURN result final score fitness: " + fitness);
		return fitness;		//fitness is either the total distance in accumulated change or just the distance from start to 
	}	

	/**
	 * method that makes sure you are taking the farthestCenterOfMass from history
	 * @param history record of the shape
	 * @param initialCenterOfMass initial center of mass
	 * @param lastCenterOfMass center of mass at the last point
	 * @return center of ,ass that was the farthest away from the initial
	 */
	public Vertex getFarthestCenterOfMass(ArrayList<Pair<Long,List<Block>>> history, Vertex initialCenterOfMass) {
		Vertex farthestCenterOfMass = initialCenterOfMass;
		double farthestDistance = 0;
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
	 * @return fitness after punishment for remaining blocks, or null if shape is deemed to not have flown away (not enough blocks leave)
	 */
	private Double ifSufficientBlocksDepartedThenMaximumFitnessWithPenalty(int initialBlockCount, List<Block> newShapeBlockList) {
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
			//MMNEAT.main("runNumber:5 randomSeed:5 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftRewardFastFlyingMachines:true minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftNorthSouthOnly:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:10 maxGens:60000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:10 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" ")); 

			MMNEAT.main("runNumber:90 randomSeed:98 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftRewardFastFlyingMachines:true minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftNorthSouthOnly:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" ")); 
			//MMNEAT.main("minecraftEvaluate minecraftBlockListTextFile:BROKEN watch:true netio:false spaceBetweenMinecraftShapes:10 minecraftChangeCenterOfMassFitness:true minecraftAccumulateChangeInCenterOfMass:true".split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
