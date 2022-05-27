package edu.southwestern.tasks.evocraft;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.tasks.evocraft.blocks.MachineBlockSet;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;
import edu.southwestern.tasks.evocraft.fitness.TypeCountFitness;
import edu.southwestern.tasks.evocraft.shapegeneration.ShapeGenerator;
import edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator;


public class MinecraftShapeTask<T> implements SinglePopulationTask<T>, NetworkTask {
	
	private ArrayList<MinecraftFitnessFunction> fitnessFunctions;
	private BlockSet blockSet;
	private ShapeGenerator<T> shapeGenerator;
	private ArrayList<MinecraftCoordinates> corners;
	
	public MinecraftShapeTask() {
		MinecraftServer.launchServer();
		
		fitnessFunctions = new ArrayList<MinecraftFitnessFunction>();

		if(Parameters.parameters.booleanParameter("minecraftTypeCountFitness")) {
			fitnessFunctions.add(new TypeCountFitness());
		}
		
		if(Parameters.parameters.booleanParameter("minecraftOccupiedCountFitness")) {
			fitnessFunctions.add(new OccupiedCountFitness());
		}

		
		// TODO: Command line parameter
		blockSet = new MachineBlockSet();
		// TODO: Command line parameter: this one only works for Network phenotypes
		shapeGenerator = new ThreeDimensionalVolumeGenerator();
		
		for(MinecraftFitnessFunction ff : fitnessFunctions) {
			MMNEAT.registerFitnessFunction(ff.getClass().getSimpleName());
		}
	}
	
	/**
	 * returns the sensorLabels
	 * @return the sensorLabels
	 */
	@Override
	public String[] sensorLabels() {
		if(Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane")) {
			return new String[] { "X", "Y", "Z", "R", "R-XY", "R-YZ", "R-XZ", "bias" };
		} else {
			return new String[] { "X", "Y", "Z", "R", "bias" };
		}
	}

	/**
	 * returns the outputLabels
	 * @return the outputLabels
	 */
	@Override
	public String[] outputLabels() {
		// Presence output and an output for each block type
		String[] labels = new String[1 + blockSet.getPossibleBlocks().length];
		labels[0] = "Presence";
		for(int i = 1; i < labels.length; i++) {
			labels[i] = blockSet.getPossibleBlocks()[i-1].name();
		}
		return labels;
	}

	@Override
	public int numObjectives() {
		return fitnessFunctions.size();
	}

	@Override
	public double[] minScores() {
		double[] scores = new double[fitnessFunctions.size()];
		for(int i = 0; i < scores.length; i++) {
			scores[i] = fitnessFunctions.get(i).minFitness();
		}
		return scores;
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	@Override
	public void finalCleanup() {
		// Close Minecraft server after all evolution is done
		MinecraftServer.terminateServer();
	}

	@Override
	public void postConstructionInitialization() {
		MMNEAT.setNNInputParameters(sensorLabels().length, outputLabels().length);
	}

	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		MinecraftClient client = MinecraftClient.getMinecraftClient();
		
		int startingX = 0;
		int startingZ = 0;
		int count = 0;
		MinecraftCoordinates ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
		
		// Avoid recalculating the same corners every time
		if(corners == null) {
			corners = new ArrayList<>(population.size());
			for(int i = 0; i < population.size(); i++) {
				MinecraftCoordinates corner = new MinecraftCoordinates(startingX + count*(ranges.x() + MinecraftClient.SPACE_BETWEEN), MinecraftClient.GROUND_LEVEL+1, startingZ);
				corners.add(corner);
				count++;
			}
		}

		// Must clear the space where shapes are placed
		client.clearSpaceForShapes(new MinecraftCoordinates(startingX,MinecraftClient.GROUND_LEVEL+1,startingZ), ranges, population.size());
		
		// Generate and evaluate shapes in parallel
		IntStream stream = IntStream.range(0, corners.size());
		ArrayList<Score<T>> scores = stream.parallel().mapToObj( i -> {
			MinecraftCoordinates corner = corners.get(i);
			Genotype<T> genome = population.get(i);
			List<Block> blocks = shapeGenerator.generateShape(genome, corner, blockSet);
			client.spawnBlocks(blocks);
			double[] fitnessScores = new double[fitnessFunctions.size()];
			int scoreIndex = 0;
			for(MinecraftFitnessFunction ff : fitnessFunctions) {
				fitnessScores[scoreIndex++] = ff.fitnessScore(corner);
			}
			return new Score<T>(genome, fitnessScores);
		}).collect(Collectors.toCollection(ArrayList::new));
		
		return scores;
	}

	public static void main(String[] args) {
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:10", "maxGens:100",
					"base:minecraft", "log:Minecraft-TypeCount", "saveTo:TypeCount",
					"io:true", "netio:true", "mating:true", "fs:false", 
					"minecraftTypeCountFitness:true",
					//"minecraftOccupiedCountFitness:true",
					"task:edu.southwestern.tasks.evocraft.MinecraftShapeTask", "allowMultipleFunctions:true",
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
