package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.emitters.*;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.file.FileUtilities;

public class CMAME extends MAPElites<ArrayList<Double>> {
	
	private Emitter[] emitters; // array holding all emitters
	public static final boolean PRINT_DEBUG = false; // prints out debug text if true (applies to both this class and emitter classes)
	public static final double FAILURE_VALUE = Double.MAX_VALUE; // For rastrigin, MAX_VALUE greatly outperforms 0.0, both of which were tested. It is possible this may be better as 0 for another task
	public int totalEmitters;
	private int emitterCounter = 0;
	private String[] logFiles;
	
	public void initialize(Genotype<ArrayList<Double>> example) {
		super.initialize(example);
		int dimension = MMNEAT.getLowerBounds().length;
		int numImprovementEmitters = Parameters.parameters.integerParameter("numImprovementEmitters");
		int numOptimizingEmitters = Parameters.parameters.integerParameter("numOptimizingEmitters");
		totalEmitters = numImprovementEmitters+numOptimizingEmitters;
		emitters = new Emitter[totalEmitters];
		if (((CMAME) MMNEAT.ea).io) logFiles = new String[totalEmitters];
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
			if (logFiles != null) logFiles[place+i] = e.individualLog.getFile().getName(); // add emitter log for later
		}
		
		if (logFiles != null) {
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
				ps.println("set xrange [0:"+ (Parameters.parameters.integerParameter("maxGens")) + "]"); // 
				ps.println("set title \"" + experimentPrefix + " Number of Valid Parents when Distribution is Updated\"");
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
		incrementEmitterCounter();
		Emitter thisEmitter = emitters[emitterCounter]; // pick the lowest one
		double[] rawIndividual = thisEmitter.sampleSingle();
		Genotype<ArrayList<Double>> individual = new BoundedRealValuedGenotype(rawIndividual);
		
		Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
		assert individualScore.usesMAPElitesBinSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		
		double individualBinScore = individualScore.behaviorIndexScore(); // extract new bin score
		Score<ArrayList<Double>> currentOccupant = archive.getElite(individualScore.MAPElitesBinIndex());
		// This will be a fitness score that is interpreted such that larger values are better
		double currentBinScore = currentOccupant == null ? Double.NEGATIVE_INFINITY : currentOccupant.behaviorIndexScore(); // extract current bin score

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
	
	public void updateEmitterLog(MMNEATLog mLog, int validParents) {
		mLog.log(iterations + "\t" + validParents);
	}
	
	
	
	
	
	// Test CMA-ME
	public static void main(String[] args) throws NoSuchMethodException, IOException {
		System.out.println("Testing CMA-ME");
		//int runNum = 9999;
		// Rastrigin test: 50 bin 		20 solution vector 		50000 gens
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true numImprovementEmitters:1 numOptimizingEmitters:0 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-CMAMERastrigin1ImprovementFunctionOptimization saveTo:CMAMERastrigin1ImprovementFunctionOptimization netio:false maxGens:50000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:50 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		
		// Rastrigin 500 bin 20 vector 50000 gens
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true numImprovementEmitters:3 numOptimizingEmitters:0 lambda:100 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-CMAMETesting saveTo:CMAMETesting netio:false maxGens:10000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		MMNEAT.main(("runNumber:"+100+" randomSeed:"+100+" io:true lambda:"+100+" base:mapelitesfunctionoptimizationseveral log:mapelitesfunctionoptimizationSeveral-MELambda saveTo:MELambda netio:false maxGens:"+50000+" ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true mu:50 lambda:50 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-METesting saveTo:METesting netio:false maxGens:10000 ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		
		//runSeveralCMAME();
	}
	
	private static final String FOLDER = "mapelitesfunctionoptimizationseveral";
	private static File severalLog = new File(FOLDER+"/Several_Log.txt");
	private static File severalLogPlot = new File(FOLDER+"/Several_Log.txt.plt");
	
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
		
		for (int run = 10; run <= 200; run+=10) {
			runSingleCMAME(run);
			Scanner currentFile = new Scanner(new File(FOLDER+"/CMAMELambda"+run+"/mapelitesfunctionoptimizationSeveral-CMAMELambda"+run+"_Fill_log.txt"));
			String line = "";
			while (currentFile.hasNextLine()) line = currentFile.nextLine();
			printStream.println(run + "\t" + line.split("\t")[1] + "\t" + line.split("\t")[2]);
		}
		printStream.close();
	}
	
	private static void runSingleCMAME(int lambda) throws FileNotFoundException, NoSuchMethodException {
		int emitterCount = 1;
		int gens = 50000;
		MMNEAT.main(("runNumber:"+lambda+" randomSeed:"+lambda+" io:true numImprovementEmitters:"+emitterCount+" numOptimizingEmitters:0 lambda:"+lambda+" base:mapelitesfunctionoptimizationseveral log:mapelitesfunctionoptimizationSeveral-CMAMELambda saveTo:CMAMELambda netio:false maxGens:"+gens+" ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		//MMNEAT.main(("runNumber:"+lambda+" randomSeed:"+lambda+" io:true numImprovementEmitters:"+emitterCount+" numOptimizingEmitters:0 lambda:"+lambda+" base:mapelitesfunctionoptimizationseveral log:mapelitesfunctionoptimizationSeveral-CMAMELambda saveTo:CMAMELambda netio:false maxGens:"+gens+" ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		//MMNEAT.main(("runNumber:"+lambda+" randomSeed:"+lambda+" numImprovementEmitters:"+emitterCount+" numOptimizingEmitters:0 base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-CMAME1Improvement saveTo:CMAME1Improvement marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 lambda:"+lambda+" maxGens:"+gens+" io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000").split(" "));
	}
	
	
}
