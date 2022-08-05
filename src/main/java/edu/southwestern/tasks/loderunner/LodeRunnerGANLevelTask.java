package edu.southwestern.tasks.loderunner;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * @author kdste
 *
 */
public class LodeRunnerGANLevelTask extends LodeRunnerLevelTask<List<Double>> implements BoundedTask {
	
	private static double[] upper;
	private static double[] lower;

	public static void main(String[] args) {
//		try {
//			//without MAPElites
//			//MMNEAT.main(new String[]{"runNumber:10", "randomSeed:0", "base:loderunnerlevels", "log:LodeRunnerLevels-Direct", "saveTo:Direct", "LodeRunnerAllGround100LevelsEpoch200000_10_7.pth", "watch:true", "GANInputSize:10", "trials:1", "mu:100", "maxGens:100000", "io:true", "netio:true", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "mating:true", "fs:false", "task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask", "cleanFrequency:-1", "saveAllChampions:true", "cleanOldNetworks:false", "logTWEANNData:false", "logMutationAndLineage:false watch:false", "steadyStateIndividualsPerGeneration:100", "aStarSearchBudget:100000"});
//			//with MAPElites
//			//MMNEAT.main(new String[] {"runNumber:12", "randomSeed:1", "base:loderunnerlevelsMAPElites", "mapElitesBinLabels:edu.southwestern.tasks.loderunner.LodeRunnerMAPElitesPercentConnectedNumGoldAndEnemiesBinLabels", "log:LodeRunnerLevels-Direct", "saveTo:Direct", "LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth", "ea:edu.southwestern.evolution.mapelites.MAPElites", "experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment","watch:false", "GANInputSize:10", "trials:1", "mu:100", "maxGens:100000", "io:true", "netio:true", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "mating:true", "fs:false", "task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask", "cleanFrequency:-1", "saveAllChampions:true", "cleanOldNetworks:false", "logTWEANNData:false", "logMutationAndLineage:false", "steadyStateIndividualsPerGeneration:100", "aStarSearchBudget:100000"});
//			//KL Divergence
//			MMNEAT.main(("klDivBinDimension:100 klDivMaxValue:2.0 runNumber:0 randomSeed:0 base:loderunnermapelites log:LodeRunnerMAPElites-KLDivergence saveTo:KLDivergence LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth watch:false GANInputSize:10 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment allowWeirdLodeRunnerActions:false lodeRunnerMaximizeEnemies:false lodeRunnerTSPBudget:0 mapElitesKLDivLevel1:data\\\\VGLC\\\\Level1.txt mapElitesKLDivLevel2:data\\\\VGLC\\\\Level2.txt").split(" "));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}

		
		// Similar to LodeRunnerGAN-MAPElitesGroundTreasureEnemiesCAFit-On100Levels.bat batch file
		Parameters.initializeParameterCollections("runNumber:0 randomSeed:0 LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch20000_10_7.pth watch:false GANInputSize:10 trials:1 mu:0 maxGens:0 io:false netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.loderunner.mapelites.LodeRunnerMAPElitesPercentGroundNumGoldAndEnemiesBinLabels allowWeirdLodeRunnerActions:false lodeRunnerMaximizeEnemies:false lodeRunnerTSPBudget:0 lodeRunnerAllowsAStarConnectivityCombo:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment ".split(" "));
		MMNEAT.loadClasses();
		GANProcess.terminateGANProcess(); // Kill the Python GAN process that is spawned
		System.out.println("READY"); // Tell Python program we are ready to receive;
		// Loop until Python program sends exit string
		String input = "";
		Scanner consoleFromPython = new Scanner(System.in);
		while(true) {
			input = consoleFromPython.nextLine();
			if(input.equals("exit")) break;
			List<List<List<Integer>>> levels = JsonReader.JsonToInt("["+input+"]"); // Wrap in extra array to match type
			List<List<Integer>> level = levels.get(0); // There is only one
			// Not how random seeds were originally generated, but should still be deterministic
			double psuedoRandomSeed = level.hashCode(); //getRandomSeedForSpawnPoint(individual); //creates the seed to be passed into the Random instance 
			HashMap<String,Object> behaviorCharacteristics = new HashMap<String,Object>();
			Genotype<List<Double>> individual = new Genotype<List<Double>>() {

				@Override
				public void addParent(long id) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public List<Long> getParentIDs() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Genotype<List<Double>> copy() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void mutate() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public Genotype<List<Double>> crossover(Genotype<List<Double>> g) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public List<Double> getPhenotype() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Genotype<List<Double>> newInstance() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public long getId() {
					// TODO Auto-generated method stub
					return 0;
				} // This is a dummy genotype that just fills a parameter slot

			};
			((LodeRunnerGANLevelTask) MMNEAT.task).evaluateOneLevel(level, psuedoRandomSeed, individual, behaviorCharacteristics);
			int[] archiveDimensions = MMNEAT.getArchiveBinLabelsClass().multiDimensionalIndices(behaviorCharacteristics);
			// Print to Python
			System.out.println(Arrays.toString(archiveDimensions)); // MAP Elites archive indices
			// The output order from this is unpredictable. Capture each of these lines using a dictionary in Python
			for(Entry<String,Object> p : behaviorCharacteristics.entrySet()) { 
				System.out.println(p.getKey() + " = " + p.getValue());
			}
			System.out.println("MAP DONE"); // You can check for this string in Python to know when the HashMap is done
		}
		consoleFromPython.close();
	}
	
	/**
	 * Random seed is simply the first latent variable
	 */
	public double getRandomSeedForSpawnPoint(Genotype<List<Double>> individual) {
		List<Double> latentVector = (List<Double>) individual.getPhenotype(); //creates a double array for the spawn to be placed in GAN levels 
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		double firstLatentVariable = doubleArray[0];
		return firstLatentVariable;
	}

	/**
	 * Gets the phenotype vector from the genotype and then returns a level by calling the static method below 
	 * @param List<Double> from the Genotype of the level 
	 * @return List<List<Integer>> that represents a level 
	 */
	@Override
	public List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<List<Double>> individual) {
		List<Double> latentVector = individual.getPhenotype();
		return getLodeRunnerLevelListRepresentationFromGenotypeStatic(latentVector);
	}

	/**
	 * Passes in a latent vector and returns a level from that latent vector 
	 * @param latentVector 
	 * @return A single level, List<List<Integer>>
	 */
	public static List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotypeStatic(List<Double> latentVector) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = LodeRunnerGANUtil.generateOneLevelListRepresentationFromGAN(doubleArray);
 		return level;
	}

	public static double[] getStaticUpperBounds() {
		if(upper == null) upper = ArrayUtil.doubleOnes(GANProcess.latentVectorLength());
		return upper;
	}

	public static double[] getStaticLowerBounds() {
		if(lower == null) lower = ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength());
		return lower;
	}

	@Override
	public double[] getUpperBounds() {
		return getStaticUpperBounds();
	}

	@Override
	public double[] getLowerBounds() {
		return getStaticLowerBounds();
	}

}
