package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.evolution.mapelites.emitters.Emitter;
import edu.southwestern.evolution.mapelites.emitters.ImprovementEmitter;
import edu.southwestern.evolution.mapelites.emitters.OptimizingEmitter;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;

/**
 * Implementation of CMA-ME as an extension
 * of MAP Elites. Implementation is partially
 * detailed in the original CMA-ME paper:
 * 
 * Matthew C. Fontaine, Julian Togelius, Stefanos Nikolaidis, and Amy K. Hoover, "Covariance Matrix Adaptation for the Rapid Illumination of Behavior Space"
 * in GECCO 2020. https://arxiv.org/pdf/1912.02400.pdf
 * 
 * @author Maxx Batterton
 *
 */
public class CMAME extends MAPElites<ArrayList<Double>> {
	
	private Emitter[] emitters; // array holding all emitters
	public static final boolean PRINT_DEBUG = false; // prints out debug text if true (applies to both this class and emitter classes)
	//public static final double FAILURE_VALUE = Double.MAX_VALUE;
	public int totalEmitters;
	private int emitterCounter = 0;
	private String[] logFiles;
	
	/**
	 * Initializes CMA-ME, first by doing everything 
	 * MAP-Elites does, then adding emitters based on 
	 * parameters. Also creates log files for each 
	 * emitter so that valid parents can be tracked.
	 */
	public void initialize(Genotype<ArrayList<Double>> example) {
		super.initialize(example);
		int dimension = MMNEAT.getLowerBounds().length;
		int numImprovementEmitters = Parameters.parameters.integerParameter("numImprovementEmitters");
		int numOptimizingEmitters = Parameters.parameters.integerParameter("numOptimizingEmitters");
		totalEmitters = numImprovementEmitters+numOptimizingEmitters;
		emitters = new Emitter[totalEmitters];
		if (io) logFiles = new String[totalEmitters];
		int place = 0; // remember position in emitter array
		for (int i = 0; i < numImprovementEmitters; i++) {
			Emitter e = new ImprovementEmitter(dimension, archive, i+1); // create improvement emitters
			emitters[i] = e;
			if (logFiles != null) logFiles[i] = e.individualLog.getFile().getName(); // add emitter log for later
			place++;
		}
		for (int i = 0; i < numOptimizingEmitters; i++) {
			Emitter e = new OptimizingEmitter(dimension, archive, i+1); // create optimizing emitters
			emitters[place+i] = e;
			if (logFiles != null) logFiles[place+i] = e.individualLog.getFile().getName(); // add emitter valid parent logs for later
		}
		
		if (logFiles != null) { // Creation of plot file for plotting valid parent logs of each emitter
			String experimentPrefix = Parameters.parameters.stringParameter("log")
					+ Parameters.parameters.integerParameter("runNumber");
			String udPrefix = experimentPrefix + "_" + "UpdateDistribution";
			String directory = FileUtilities.getSaveDirectory(); // retrieves file directory
			directory += (directory.equals("") ? "" : "/");
			String udName = directory + udPrefix + "_log.plt";
			
			File plot = new File(udName);
			try {
				PrintStream ps = new PrintStream(plot);
				ps.println("set term pdf enhanced");
				ps.println("set yrange [0:"+ (Parameters.parameters.integerParameter("lambda")) +"]"); // lambda will be maximum possible value, a perfect update
				ps.println("set xrange [0:"+ (Parameters.parameters.integerParameter("maxGens")) + "]"); // scale vertical to maximum possible
				ps.println("set title \"" + experimentPrefix + " Number of Valid Parents\"");
				ps.println("set output \"" + udName.substring(udName.lastIndexOf('/')+1, udName.lastIndexOf('.')) + ".pdf\"");
				for (int i = 0; i < totalEmitters; i++) { // add line to plot each emitter
					String shortName = logFiles[i].replace(experimentPrefix+"_", "").replace("_log.txt", "");
					ps.println((i == 0 ? "plot \"" : "     \"") + logFiles[i] + "\" u 1:2 w linespoints t \""+shortName+"\"" + (i < totalEmitters-1 ? ", \\" : ""));
				}
				ps.close();
			} catch (FileNotFoundException e) {
				System.out.println("Could not create plot file: " + plot.getName());
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		
	}
	
	/**
	 * Create new individuals based on set
	 * population size of Emitters, and update
	 * distribution of Emitters with fitnesses 
	 * afterwards
	 */
	public void newIndividual() {
		incrementEmitterCounter(); // increment emitter counter
		Emitter thisEmitter = emitters[emitterCounter]; // pick the lowest one
		// rawIndividual may not be in bounds of BoundedRealValuedGenotype
		double[] rawIndividual = thisEmitter.sampleSingle(); // sample an individual from current emitter
		// individual will be bounded in each variable index
		Genotype<ArrayList<Double>> individual = MMNEAT.genotype instanceof BoundedRealValuedGenotype ? new BoundedRealValuedGenotype(rawIndividual) : new RealValuedGenotype(rawIndividual);
		
		// Get bounded values from genotype to get passed to emitters down below.
		// If resampleBadCMAMEGenomes is true, then the initial values will never exceed bounds, and thus rawIndividual will not change
		rawIndividual = ArrayUtil.doubleArrayFromList(individual.getPhenotype());
		
		Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
		assert individualScore.usesMAPElitesBinSpecification() || individualScore.usesMAPElitesMapSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		
		double individualBinScore = individualScore.behaviorIndexScore(); // extract new bin score
		Score<ArrayList<Double>> currentOccupant = archive.getElite(individualScore.MAPElitesBinIndex());
		// This will be a fitness score that is interpreted such that larger values are better
		double currentBinScore = currentOccupant == null ? Float.NEGATIVE_INFINITY : currentOccupant.behaviorIndexScore(); // extract current bin score

		thisEmitter.addFitness(rawIndividual, individualBinScore, currentBinScore, archive); // potentially add new fitness
		
		boolean replacedBool = archive.add(individualScore); // attempt to add individual to archive
		
		if (PRINT_DEBUG) {System.out.println("Emitter: \""+thisEmitter.emitterName+"\"\tSolutions: "+thisEmitter.solutionCount+"\t\tAmount of Parents: "+thisEmitter.additionCounter);}
		fileUpdates(replacedBool); // log failure or success
	}	

	/**
	 * Switches to the next emitter until all are processed, then
	 * loops back to the first emitter.
	 */
	private void incrementEmitterCounter() {
		emitterCounter = (emitterCounter + 1) % totalEmitters;
	}
	
	/**
	 * Update emitter log with valid parents.
	 * 
	 * @param mLog The log file to log into
	 * @param validParents The number of valid parents to be logged
	 */
	public void updateEmitterLog(MMNEATLog mLog, int validParents) {
		mLog.log(iterations + "\t" + validParents);
	}
	
	/**
	 * Get emitter means
	 * 
	 * @return
	 */
	public List<double[]> getEmitterMeans() {
		ArrayList<double[]> means = new ArrayList<>();
		for (Emitter e : emitters) {
			means.add(e.getMean());
		}
		return means;
	}
	
//	@Override
//	protected void log() {
//		
//	}
	
	// Test CMA-ME
	public static void main(String[] args) throws NoSuchMethodException, IOException {
		System.out.println("Testing CMA-ME");
		//MMNEAT.main("runNumber:0 randomSeed:0 base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-CMAME1Improvement saveTo:CMAME1Improvement marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:50 lambda:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 numImprovementEmitters:1 numOptimizingEmitters:0".split(" "));
		MMNEAT.main("runNumber:7 randomSeed:7 base:mariolevelsdecorateleniency log:MarioLevelsDecorateLeniency-CMAME5Improvement saveTo:CMAME5Improvement marioGANLevelChunks:2 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:37 lambda:100 maxGens:20000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 numImprovementEmitters:5 numOptimizingEmitters:0 CMAMESigma:0.5 marioStatBasedMEBinIntervals:200".split(" "));
		//runSeveralCMAME();
	}
	
	private static final String FOLDER = "mapelitesfunctionoptimizationseveral"; // output for multiple runs
	private static File severalLog = new File(FOLDER+"/Several_Log.txt"); // log file that takes data from each run
	private static File severalLogPlot = new File(FOLDER+"/Several_Log.txt.plt"); // plot file for above log
	
	
	/**
	 * Method for running multiple CMAME in sequence
	 * with variable lambda, and recording the final
	 * results of each lambda run, to be able to see
	 * what lambda works the best.
	 */
	@SuppressWarnings("unused")
	private static void runSeveralCMAME() throws NoSuchMethodException, IOException {
		new File(FOLDER+"/").mkdir();
		severalLog.createNewFile();
		PrintStream printStream = new PrintStream(severalLogPlot);
		
		printStream.println("set term pdf enhanced");
		printStream.println("set key bottom right");
		// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
		printStream.println("set xrange [0:200]");
		printStream.println("set title \"Final Bins Filled for variable lambda\"");
		printStream.println("set output \"Several_CMA-ME_Bins_Lambda.pdf\"");
		printStream.println("plot \"Several_Log.txt\" u 1:2 w linespoints t \"Final Bins Filled\"");
		
		printStream.println("set title \"Final QD Scores for variable lambda\"");
		printStream.println("plot \"Several_Log.txt\" u 1:3 w linespoints t \"Final Bins Filled\"");
		
		printStream.close();
		
		printStream = new PrintStream(new FileOutputStream(severalLog, false));
		
		for (int run = 2; run <= 200; run+=10) { // range of runs
			runSingleCMAME(run);
			Scanner currentFile = new Scanner(new File(FOLDER+"/CMAMELambda"+run+"/mapelitesfunctionoptimizationSeveral-CMAMELambda"+run+"_Fill_log.txt"));
			String line = "";
			while (currentFile.hasNextLine()) line = currentFile.nextLine();
			printStream.println(run + "\t" + line.split("\t")[1] + "\t" + line.split("\t")[2]);
			currentFile.close();
		}
		printStream.close();
	}
	
	// run a single CMA-ME with provided lambda. Generations is specified here
	private static void runSingleCMAME(int lambda) throws FileNotFoundException, NoSuchMethodException {
		int emitterCount = 3;
		int gens = 50000;
		MMNEAT.main(("runNumber:"+lambda+" randomSeed:"+lambda+" io:true numImprovementEmitters:"+emitterCount+" numOptimizingEmitters:0 lambda:"+lambda+" base:mapelitesfunctionoptimizationseveral log:mapelitesfunctionoptimizationSeveral-CMAMELambda saveTo:CMAMELambda netio:false maxGens:"+gens+" ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:1000 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRangeBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		//MMNEAT.main(("runNumber:"+lambda+" randomSeed:"+lambda+" io:true numImprovementEmitters:"+emitterCount+" numOptimizingEmitters:0 lambda:"+lambda+" base:mapelitesfunctionoptimizationseveral log:mapelitesfunctionoptimizationSeveral-CMAMELambda saveTo:CMAMELambda netio:false maxGens:"+gens+" ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		//MMNEAT.main(("runNumber:"+lambda+" randomSeed:"+lambda+" numImprovementEmitters:"+emitterCount+" numOptimizingEmitters:0 base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-CMAME1Improvement saveTo:CMAME1Improvement marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 lambda:"+lambda+" maxGens:"+gens+" io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000").split(" "));
	}
	
	
}
