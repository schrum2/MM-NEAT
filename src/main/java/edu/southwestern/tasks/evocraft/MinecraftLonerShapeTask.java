package edu.southwestern.tasks.evocraft;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.Pair;

/**
 * MAPElites only works with LonerTasks because it is a steady-state algorithm.
 * MinecraftShapeTask is parallelized for evolving populations, but this
 * version of the task cannot take advantage of that (though there may be a work-around
 * using multiple emitters). However, some of the code from MinecraftShapeTask is
 * re-used.
 * 
 * @author schrum2
 *
 * @param <T>
 */
public class MinecraftLonerShapeTask<T> extends NoisyLonerTask<T> implements NetworkTask {

	private MinecraftShapeTask<T> internalMinecraftShapeTask;
	
	public MinecraftLonerShapeTask() {
		internalMinecraftShapeTask = new MinecraftShapeTask<T>();
	}
	
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) {
		int startingX = internalMinecraftShapeTask.getStartingX();
		int startingZ = internalMinecraftShapeTask.getStartingZ();
		MinecraftCoordinates ranges = internalMinecraftShapeTask.getRanges();
		// Clears space for one shape
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(new MinecraftCoordinates(startingX,MinecraftClient.GROUND_LEVEL+1,startingZ), ranges, 1, Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));
		// List of 1 corner
		ArrayList<MinecraftCoordinates> corner = MinecraftShapeTask.getShapeCorners(1, startingX, startingZ, ranges);
		
		Score<T> score = internalMinecraftShapeTask.evaluateOneShape(individual, corner.get(0));
		// Copy over one HashMap to another (is there an easier way?)
		for(HashMap.Entry<String,Object> entry : score.MAPElitesBehaviorMap().entrySet()) {
			behaviorCharacteristics.put(entry.getKey(), entry.getValue());
		}
		// This result will be ignored when using MAP Elites
		return new Pair<>(score.scores, score.otherStats);
	}
	
	@Override
	public int numObjectives() {
		return internalMinecraftShapeTask.numObjectives();
	}

	@Override
	public double getTimeStamp() {
		return 0;
	}

	@Override
	public void postConstructionInitialization() {
		internalMinecraftShapeTask.postConstructionInitialization();
	}

	@Override
	public String[] sensorLabels() {
		return internalMinecraftShapeTask.sensorLabels();
	}

	@Override
	public String[] outputLabels() {
		return internalMinecraftShapeTask.outputLabels();
	}

    @Override
	public void finalCleanup() {
    	internalMinecraftShapeTask.finalCleanup();
	}
    
	public static void main(String[] args) {
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-MAPElitesWHD", "saveTo:MAPElitesWHD",
					"io:true", "netio:true", 
					"launchMinecraftServerFromJava:false",
					//"io:false", "netio:false", 
					"mating:true", "fs:false", 
					"minecraftTypeCountFitness:true", // Need some kind of fitness function?
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true",
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:false", "netChangeActivationRate:0.3", "cleanFrequency:-1",
					"recurrency:false", "saveAllChampions:true", "cleanOldNetworks:false",
					"includeFullSigmoidFunction:true", "includeFullGaussFunction:true", "includeCosineFunction:true", 
					"includeGaussFunction:false", "includeIdFunction:true", "includeTriangleWaveFunction:false", 
					"includeSquareWaveFunction:false", "includeFullSawtoothFunction:false", "includeSigmoidFunction:false", 
					"includeAbsValFunction:false", "includeSawtoothFunction:false"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
