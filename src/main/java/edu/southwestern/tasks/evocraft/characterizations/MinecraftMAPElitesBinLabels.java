package edu.southwestern.tasks.evocraft.characterizations;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;

/**
 * For Minecraft, all binning schemes make use of a collection of Minecraft fitness function scores
 * 
 * @author schrum2
 *
 */
public abstract class MinecraftMAPElitesBinLabels extends BaseBinLabels implements Iterable<int[]> {

	public MinecraftMAPElitesBinLabels() {
		// if populating the world with the archive, clear it all here
		// call List<String> binLabels() and dimensionSizes()
		
		// Putting clearing here because bin labels and other aspects need to be initialized first in the constructor before clearing can occur
		List<String> binSize = binLabels();
		// Gets ranges for all coordinates
		MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"),Parameters.parameters.integerParameter("minecraftYRange"),Parameters.parameters.integerParameter("minecraftZRange"));
		// Clears the area
		if(!Parameters.parameters.booleanParameter("minecraftSkipInitialClear")) {
			MinecraftClient.getMinecraftClient().clearSpaceForShapes(new MinecraftCoordinates(0,MinecraftClient.GROUND_LEVEL+1,0), ranges, binSize.size(), Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));	
		}else {
			System.out.println("Initial clear skipped, clearing smaller area for shapes that were generated, then placing fences");
		}
	}
	
	/**
	 * Collection of fitness functions that calculate scores to based the
	 * behavior characterization on.
	 * @return List of fitness functions for Minecraft
	 */
	public abstract List<MinecraftFitnessFunction> properties();
	
	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		// Assumes the keys for objects in the map are derived from the fitness function names, which dimensions() also uses.
		// Also assumes every fitness function returns a double which is stored as a Double, and has to be cast to double before being cast to int.
		return Arrays.stream(dimensions()).parallel().mapToInt(name -> (int) ((double) keys.get(name))).toArray();
	}

	@Override
	public String[] dimensions() {
		// Names of the fitness functions are the dimensions
		return properties().parallelStream().map(ff -> ff.getClass().getSimpleName()).toArray(String[]::new);
	}
	
	/**
	 * It is easy to generate an array of fitness scores from the list of fitness functions, which are the
	 * properties(). This method takes such an array and packages them in a HashMap since this representation
	 * is required elsewhere.
	 * 
	 * @param fitnessScores calculated fitness scores for each function in the properties/dimensions, and in the same order
	 * @return HashMap containing these scores with the names of the fitness functions as keys
	 */
	public HashMap<String,Object> behaviorMapFromScores(double[] fitnessScores) {
		HashMap<String,Object> behaviorMap = new HashMap<String,Object>();
		String[] functionNames = dimensions();
		for(int i = 0; i < functionNames.length; i++) {
			behaviorMap.put(functionNames[i], fitnessScores[i]);
		}
		return behaviorMap;
	}
	
	@Override
	public Iterator<int[]> iterator() {
		return new MultidimensionalIndexIterator();
	}
	
	/**
	 * Can iterate through all of the multidimensional indices in row-major order.
	 * Will visit every 1D index in the archive.
	 * 
	 * @author schrum2
	 *
	 */
	private class MultidimensionalIndexIterator implements Iterator<int[]> {

		private int[] current;
		private int[] sizes;
		private boolean finished;
		
		public MultidimensionalIndexIterator() {
			finished = false;
			sizes = dimensionSizes();
			// All 0s to start
			current = new int[sizes.length];
		}
		
		@Override
		public boolean hasNext() {
			return !finished;
		}

		@Override
		public int[] next() {
			// Result is already known
			int[] result = Arrays.copyOf(current, current.length);
			// Pre-calculate next in line
			int i = current.length;
			do {
				i--;
				current[i] = (current[i] + 1) % sizes[i];				
			} while(i > 0 && current[i] == 0);
			if(i == 0 && current[i] == 0) finished = true;
			return result;
		}
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:79 randomSeed:79 minecraftXRange:4 minecraftYRange:4 minecraftZRange:4 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true displayDiagonally:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true numImprovementEmitters:5 numOptimizingEmitters:0 CMAMESigma:0.5 resampleBadCMAMEGenomes:false base:minecraftaccumulate log:MinecraftAccumulate-CMAMEVectorCountNegative saveTo:CMAMEVectorCountNegative mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels lambda:10".split(" "));
	}
}
