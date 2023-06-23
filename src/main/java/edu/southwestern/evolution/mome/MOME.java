package edu.southwestern.evolution.mome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;

/**
 * TODO: Explain a bit more, and also cite the paper whose algorithm we are implementing using ACM style
 * 
 * @author lewisj
 *
 * @param <T>
 */
public class MOME<T> implements SteadyStateEA<T>{

	protected MOMEArchive<T> archive;
								// TODO: Yes, iterations tracks the number of individuals generated. This needs to be incremented somewhere
	//protected int iterations;	//might want to rename? what is it, just the number of individuals created so far?
	protected LonerTask<T> task; ///seems to be for cleanup, not sure what else
	
	//below deals with new individual creation
	private static final int NUM_CODE_EMPTY = -1;	//initialization number for a non existent parent
	private boolean mating;	//determines if using mating functionality
	private double crossoverRate;
	
	//tracking variables
	private int addedIndividualCount;
	private int discardedIndividualCount;	//don't know if we will need this
//	private boolean populationChangeCheck;	//this keeps track of what happened to the most recent individual created (if it was added or not)
						//false means the individual was not added and so the population hasn't changed
						//true means an individual was added and the population changed
	private int individualsPerGeneration;
	private int individualCreationAttemptsCount;	//this is analogous to iterations and helps to keep track of how many actual times new individual is called
	
	//logging variables (might be sorted into tracking or other grouping
	public boolean io;
	private boolean archiveFileCreated = false;	//track if the archive file is made
	
	// Not a MOMELog
	private MMNEATLog archiveLog = null; // Log general archive information. Does not use matrix plot, logged every generation
	
	// TODO: Convert these to MOMELogs
	private MMNEATLog binPopulationSizeLog = null; // contains sizes of subpops in each bin, logged every generation
	private MMNEATLog[] maxFitnessLogs = null; //creates a log for each objective that contains the max fitness for each bin, logged every generation
	private MMNEATLog[] minFitnessLogs = null; //creates a log for each objective that contains the min fitness for each bin, logged every generation
	private MMNEATLog[] rangeFitnessLogs = null; //creates a log for each objective that contains the range from min to max for each bin, logged every generation

	public MOME() {
		this(Parameters.parameters.stringParameter("archiveSubDirectoryName"), Parameters.parameters.booleanParameter("io"), Parameters.parameters.booleanParameter("netio"));
	}

	/**
	 * 
	 * @param archiveSubDirectoryName Name of the archive directory within the experiment directory
	 * @param ioOption Whether to write to logs and do other IO
	 * @param netioOption Whether to save xml genome files
	 */
	@SuppressWarnings("unchecked")
	public MOME(String archiveSubDirectoryName, boolean ioOption, boolean netioOption) {
		MMNEAT.usingDiversityBinningScheme = true;
		this.task = (LonerTask<T>) MMNEAT.task;
		this.archive = new MOMEArchive<>(netioOption, archiveSubDirectoryName);	//set up archive

		this.io = ioOption; // write logs
		this.mating = Parameters.parameters.booleanParameter("mating");
		this.crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
//		this.populationChangeCheck = false;
		this.addedIndividualCount = 0;
//		this.setDiscardedIndividualCount(0);		//broke this, need to investigate later
		this.individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
		this.individualCreationAttemptsCount = Parameters.parameters.integerParameter("lastSavedGeneration");
		//TODO: the above might cause issues
		
		
//////TODO: logging below //////////////////////////////////////////////////////////////////////////////
		if(io) {
			//logging
			String infix = "MOMEArchive";
			int numberOfObjectivesToLog = MMNEAT.task.numObjectives();
			int numberOfBinLabels = archive.getBinMapping().binLabels().size();
			// TODO: Contains MMNEATLogs for now, but later will be only those logs that are MOMELogs. Specifically, they have a column for each archive bin
			ArrayList<MMNEATLog> momeLogs = new ArrayList<>();
			
			// Logging in RAW mode so that can append to log file on experiment resume
			archiveLog = new MMNEATLog(infix, false, false, false, true);

			// These are MOMELogs
			binPopulationSizeLog = new MMNEATLog(infix+"_BinPopulation", false, false, false, true);
			momeLogs.add(binPopulationSizeLog);
			
			maxFitnessLogs = new MMNEATLog[numberOfObjectivesToLog];
			minFitnessLogs = new MMNEATLog[numberOfObjectivesToLog];
			rangeFitnessLogs = new MMNEATLog[numberOfObjectivesToLog];
			
			String infixMin = infix + "_Min_Objective_";
			String infixMax = infix + "_Max_Objective_";
			String infixRange = infix + "_Range_";
			for (int i = 0; i < numberOfObjectivesToLog; i++) {
				minFitnessLogs[i] = new MMNEATLog(infixMin+i, false, false, false, true);
				maxFitnessLogs[i] = new MMNEATLog(infixMax+i, false, false, false, true);
				rangeFitnessLogs[i] = new MMNEATLog(infixRange+i, false, false, false, true);
				
				momeLogs.add(minFitnessLogs[i]);
				momeLogs.add(maxFitnessLogs[i]);
				momeLogs.add(rangeFitnessLogs[i]);
			}

			// Create gnuplot file for archive log
			String experimentPrefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber");					
			int yrange = Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration;
			
			
			
			setUpLogging(numberOfBinLabels, infix, experimentPrefix, yrange, individualsPerGeneration, momeLogs);
		}
	}
	
	
	@Override
	public void initialize(Genotype<T> example) {
		// Do not allow Minecraft to contain archive when using MOME
		Parameters.parameters.setBoolean("minecraftContainsWholeMAPElitesArchive", false);

		ArrayList<Genotype<T>> startingPopulation; // Will be new or from saved archive

		System.out.println("Fill up initial archive");		
		// Start from scratch
		int startSize = Parameters.parameters.integerParameter("mu");
		startingPopulation = PopulationUtil.initialPopulation(example, startSize);			
		
		assert startingPopulation.size() == 0 || !(startingPopulation.get(0) instanceof BoundedRealValuedGenotype) || ((BoundedRealValuedGenotype) startingPopulation.get(0)).isBounded() : "Initial individual not bounded: "+startingPopulation.get(0);
		
		//add initial population to the archive
		Vector<Score<T>> evaluatedPopulation = new Vector<Score<T>>(startingPopulation.size());
		//not sure if we need netio stuff at all
		boolean backupNetIO = CommonConstants.netio;
		CommonConstants.netio = false; // Some tasks require archive comparison to do this, but it does not exist yet.
		Stream<Genotype<T>> evaluateStream = Parameters.parameters.booleanParameter("parallelMAPElitesInitialize") ? 
												startingPopulation.parallelStream() :
												startingPopulation.stream();
		if(Parameters.parameters.booleanParameter("parallelMAPElitesInitialize"))										
			System.out.println("Evaluate archive in parallel");
		
		// Evaluate initial population and add to evaluated population
		evaluateStream.forEach( (g) -> {
			Score<T> s = task.evaluate(g);
			evaluatedPopulation.add(s);
			//System.out.println("single evaluation done");
		});

		
		CommonConstants.netio = backupNetIO;
	
		 // Add evaluated population to archive, if add is true
		evaluatedPopulation.parallelStream().forEach( (s) -> {
			archive.add(s); // Fill the archive with random starting individuals, only when this flag is true
		});
		
		//initializing a variable
		this.mating = Parameters.parameters.booleanParameter("mating");
	}

	@Override
	public void newIndividual() {
		//System.out.println("new individual, current count:" + addedIndividualCount);

		//get random individual for parent 1
		Genotype<T> parentGenotype1 = archive.getRandomIndividual().individual;
		long parentId1 = parentGenotype1.getId();
		long parentId2 = NUM_CODE_EMPTY;	//initialize second parent in case it's needed
		
		//creating the first child
		Genotype<T> childGenotype1 = parentGenotype1.copy(); // Copy with different Id (will be further modified below)
		childGenotype1.addParent(parentId1);

		// Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
			//get a random individual for parent 2
			Genotype<T> parentGenotype2 = archive.getRandomIndividual().individual;
			parentId2 = parentGenotype2.getId();	// Parent Id comes from original genome
			
			//create second child
			Genotype<T> childGenotype2 = parentGenotype2.copy(); // Copy with different Id (further modified below)
			
			// Replace child2 with a crossover result
			childGenotype2 = childGenotype1.crossover(childGenotype2);
			childGenotype2.mutate(); // Probabilistic mutation of child
			
			//add parent ids to both children
			childGenotype2.addParent(parentId2);
			childGenotype2.addParent(parentId1);
			childGenotype1.addParent(parentId2);
			
			//evolutionary history log data
			EvolutionaryHistory.logLineageData(parentId1,parentId2,childGenotype2);

			// Evaluate and add child to archive
			Score<T> s2 = task.evaluate(childGenotype2);
			
			//try and add new individual then check if successful and population has changed
			afterIndividualCreationProcesses(archive.add(s2));			//this method will deal with anything that needs to be done after an individual is made
		}
		
		childGenotype1.mutate(); // Was potentially modified by crossover
		
		//evolutionary history log data
		if (parentId2 == NUM_CODE_EMPTY) {
			EvolutionaryHistory.logLineageData(parentId1,childGenotype1);
		} else {
			EvolutionaryHistory.logLineageData(parentId1,parentId2,childGenotype1);
		}
		
		// Evaluate and add child 1 to archive
		Score<T> s1 = task.evaluate(childGenotype1);
		
		// Try and add newest individual and update population change variable on result
		afterIndividualCreationProcesses(archive.add(s1));			//this will call anything we need to do after making a new individual
	}

	@Override
	public int currentIteration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void finalCleanup() {
		// TODO Auto-generated method stub
		task.finalCleanup();
	}

	/**
	 * gets an ArrayList of the populations genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> getPopulation() {
		//System.out.println("in get population");

		 ArrayList<Genotype<T>> result = new ArrayList<Genotype<T>>(archive.archive.size());

		 archive.archive.forEach( (coords, subpop) -> {	////goes through the archive
			 for(Score<T> s : subpop) {		//goes through the scores of the subpop
				 result.add(s.individual);
			 }
		 });
		 
		return result;
	}
	
	/**
	 * within file update method
	 * 	// Log to file
		log();
		Parameters.parameters.setInteger("lastSavedGeneration", iterations);
		// Track total iterations
		iterations++;
		// Track how long we have gone without producing a new elite individual
		if(newEliteProduced) {
			iterationsWithoutElite = 0;
		} else {
			iterationsWithoutEliteCounter++;
			iterationsWithoutElite++;
		}
		System.out.println(iterations + "\t" + iterationsWithoutElite + "\t");
	 */
	/**
	 * holds commands made after an individual is created
	 * @param individualAddStatus	if an individual was added or not
	 */
	//@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void afterIndividualCreationProcesses(boolean individualAddStatus) {
		//System.out.println("in afterIndividualCreation: " +archive.totalNumberOfIndividualsInArchive());
		individualCreationAttemptsCount++;
		log();			////////////////TODO: call logging class potentially
		if(individualAddStatus) {	//the individual was added and the population changed
			addedIndividualCount++;
		} 
	}
	

	@Override
	public boolean populationChanged() {
//		return populationChangeCheck; TODO:
		return false;
	}

	/**
	 * retrieves bin labels, maybe I should see what this really is?
	 * @return
	 */
	public BinLabels getBinLabelsClass() {
		//System.out.println("get bin labels class return this: " + archive.getBinMapping());
		return archive.getBinMapping();
	}	
	
	public int getDiscardedIndividualCount() {
		return discardedIndividualCount;
	}

	public void setDiscardedIndividualCount() {
		this.discardedIndividualCount = discardedIndividualCount++;
	}
//TODO: will probably move most of this out to logging class ////////////////////////////////////////////
	/**
	 * Write one line of data to each of the active log files, but only periodically,
	 * when number of iterations divisible by individualsPerGeneration. 
	 */
	protected void log() {
		//System.out.println("in log method");
		if (!archiveFileCreated) {
			try {
				if(Parameters.parameters.booleanParameter("io")) setupArchiveVisualizer(archive.getBinMapping());
			} catch (FileNotFoundException e) {
				System.out.println("Could not create archive visualization file.");
				e.printStackTrace();
				System.exit(1);
			}
			archiveFileCreated = true;
		}

		//System.out.println("individuals per generation:"+ individualsPerGeneration + " parameter:" + Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration"));

		//if an individual was added and the population count is even with the steadyStateIndividualsPerGeneration
		//this is all periodic logging to text files
		if(io && (individualCreationAttemptsCount%individualsPerGeneration == 0)) {
			final int pseudoGeneration = individualCreationAttemptsCount/individualsPerGeneration;

			System.out.println("generation:"+pseudoGeneration+ " addedIndividualCount:" +addedIndividualCount + " individualCreationAttemptsCount:" + individualCreationAttemptsCount);
			
			int numberOfObjectives = MMNEAT.task.numObjectives();
			
			//LOGGING POPULATION INFORMATION
			int[] populationSizesForBins = archive.populationSizeForEveryBin();
			String popString = Arrays.toString(populationSizesForBins).replace(", ", "\t");
			popString = popString.substring(1, popString.length() - 1); // Remove opening and closing [ ] brackets
			binPopulationSizeLog.log(popString);
			
			//LOGGING OBJECTIVES MAX AND MIN
			double[][] maxScoresBinXObjective = archive.maxScorebyBinXObjective(); //maxScores[bin][objective]
			double[][] minScoresBinXObjective = archive.minScorebyBinXObjective(); //minScores[bin][objective]
			//initialize log with info labels
			
			//loop through objectives to log max and min for each objectives log
			for (int i = 0; i < numberOfObjectives; i++) {
				
				//MAX FITNESS SCORES LOG
				double[] maxColumn = ArrayUtil.column(maxScoresBinXObjective, i);
				Double[] maxScoresForOneObjective = ArrayUtils.toObject(maxColumn);
				maxFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(maxScoresForOneObjective, " \t").replaceAll("-Infinity", "X"));
				
				//MIN FITNESS SCORES LOG
				double[] minColumn = ArrayUtil.column(minScoresBinXObjective, i);
				Double[] minScoresForOneObjective = ArrayUtils.toObject(minColumn);
				minFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(minScoresForOneObjective, "\t").replaceAll("-Infinity", "X"));
				
				//RANGE FITNESS SCORES LOG
				// If a bin is empty, its min and max are negative infinity, but the range should be 0.
				// With floating point, negative infinity minus itself is not 0. So, instead, we convert negative infinity to 0,
				// which will lead to a result of 0 - 0 = 0.
				Arrays.parallelSetAll(maxColumn, j -> (Double.isInfinite(maxColumn[j]) && maxColumn[j] < 0) ? 0.0 : maxColumn[j]);
				Arrays.parallelSetAll(minColumn, j -> (Double.isInfinite(minColumn[j]) && minColumn[j] < 0) ? 0.0 : minColumn[j]);
				Double[] scoreRangesForOneObjective = ArrayUtils.toObject(ArrayUtil.zipSubtract(maxColumn, minColumn));
				rangeFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(scoreRangesForOneObjective, "\t"));
			}
//
			
			//below is for archive logging archive
//			////////////BELOW WORKS
			double[] maxFitnessScoresArray = archive.maxFitnessInWholeArchiveXObjective();
			String printString = pseudoGeneration+"\t"+archive.getNumberOfOccupiedBins()+"\t"+archive.totalNumberOfIndividualsInArchive()+"\t";
			
			//adding max fitness scores to print
			//since all bins are put together it simply gets the match fitness score from the array
			for (int i = 0; i < maxFitnessScoresArray.length; i++) {
				printString = printString + maxFitnessScoresArray[i] + "\t";
			}
			//adding min fitness scores to print
			double[] minFitnessScoresArray = archive.maxFitnessInWholeArchiveXObjective();
			for (int i = 0; i < minFitnessScoresArray.length; i++) {
				printString = printString + minFitnessScoresArray[i] + "\t";
			}
			////////////////ABOVE WORKS
			
			System.out.println(printString);

			archiveLog.log(printString);
		}
	}
	
	//stubs of things from MAPElites
	private void writeScriptLauncher(String directory, String prefix, String fullName, String[] dimensionNames,
			int[] dimensionSizes, PrintStream ps, String finalLine) {
		//might need later
	}
	
//TODO: definitely remove all of this
	public static void setUpLogging(int numberOfBinLabels, String infix, String experimentPrefix, int yrange, int individualsPerGeneration, ArrayList<MMNEATLog> momeLogs) {
		//this is for logging, copied all the parameters but probably don't need it all
		
		String prefix = experimentPrefix + "_" + infix;
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		
		
		String fullName = directory + prefix + "_log.plt";
		// TODO: The .plt file for the log of general archive information will be very different than the numerous MOMELogs below
		
		
		// All MOMELogs can be plotted in the same way, regardless of whether they correspond to individual objectives or not
		for(MMNEATLog log :  momeLogs) {
			String textLogFilename = log.getLogTextFilename();
			String plotFilename = textLogFilename.replace(".txt", ".plt");
			String plotPDFFilename = plotFilename.replace(".plt", "_PDF.plt");
			String logTitle = textLogFilename.replace(".txt", "");
			String pdfFilename = textLogFilename.replace(".txt", ".pdf");
			
			File plotFile = new File(directory + plotFilename);
			File plotPDFFile = new File(directory + plotPDFFilename);
			
			try {
				// The PDF version
				PrintStream ps = new PrintStream(plotPDFFile);
				ps.println("set term pdf enhanced");
				ps.println("unset key");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set yrange [0:"+ yrange +"]");
				ps.println("set xrange [0:"+ numberOfBinLabels + "]");
				ps.println("set title \"" + logTitle + "\"");
				ps.println("set output \"" + pdfFilename + "\"");				
				// The :1 is for skipping the "generation" number logged in the file
				ps.println("plot \"" + textLogFilename + "\" matrix every ::1 with image");
				ps.close();

				// Non-PDF version
				ps = new PrintStream(plotFile);
				ps.println("unset key");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set yrange [0:"+ yrange +"]");
				ps.println("set xrange [0:"+ numberOfBinLabels + "]");
				ps.println("set title \"" + logTitle + "\"");
				// The :1 is for skipping the "generation" number logged in the file
				ps.println("plot \"" + textLogFilename + "\" matrix every ::1 with image");
				ps.println("pause -1");
				ps.close();
		
			} catch (FileNotFoundException e) {
				System.out.println("Error creating MOME log files");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
	}
	
	/**
	 * TODO: The archive visualizer for MAP Elites created extra Python scripts that produce
	 * 		 the 2D final archive visualizations using matplot lib. Accomplishing a similar
	 * 	 	 feat using MOME will require a lot of thought and effort, but should happen
	 * 		 after more basic logging. 
	 *
	 * @param bins
	 * @throws FileNotFoundException
	 */
	private void setupArchiveVisualizer(BinLabels bins) throws FileNotFoundException {
		System.out.println("in setupArchiveVisualizer");

		//this might be the barebones logging?
		//its to set up the visualizer bat files? Maybe open text files for editting?
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		String prefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber") + "_MOMEElites";
		String fullName = directory + prefix + "_log.plt";
		System.out.println("archive vizualizer full name: " + fullName);
		//fullname = mometest/MEObserverVectorPistonOrientation7/MOMETest-MEObserverVectorPistonOrientation7_MOMEElites_log.plt


		PythonUtil.setPythonProgram();
		PythonUtil.checkPython();
	}
	

	public static void main (String[] args) {
		try {
			// This is a GECCO set of parameters with some minor changes for MOME
			//had to have a server running first
			//there is an issue with bin labels - MMNEAT.java method getArchiveBinLabelsClass & getArchive
			//Attempted to get archive bin label class without using MAP Elites or a psuedo-archive
			MMNEAT.main("runNumber:98 randomSeed:2 minecraftMaximizeVolumeFitness:true trackPseudoArchive:false minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:mometest log:MOMETest-currentlyTesting saveTo:currentlyTesting mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));

			//MMNEAT.main("runNumber:7 randomSeed:2 minecraftMaximizeVolumeFitness:true trackPseudoArchive:false minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:mometest log:MOMETest-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));
			//MMNEAT.main("runNumber:5 randomSeed:1 minecraftMaximizeVolumeFitness:true minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:5 maxGens:1 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:5 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:mometest log:MOMETest-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
//archive contains 125 bins
