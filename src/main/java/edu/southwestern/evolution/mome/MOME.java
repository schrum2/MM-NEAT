package edu.southwestern.evolution.mome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import edu.southwestern.util.stats.StatisticsUtilities;
import jdk.nashorn.internal.ir.TryNode;

/**
 * TODO: Explain a bit more, and also cite the paper whose algorithm we are implementing using ACM style
 * This class implements MOME instead of MAP Elites using the paper from https://arxiv.org/pdf/2202.03057.pdf
 * It creates an archive where bins can house multiple individuals, it keeps track of generations
 * and does statistical data logging in files.
 * 
 * Just like MAPElites but with pareto fronts
 * 
 * Thomas Pierrot, Guillaume Richard, Karim Beguir, and Antoine Cully. 
 * 2022. Multi-objective quality diversity optimization. 
 * In Proceedings of the Genetic and Evolutionary Computation Conference (GECCO '22). 
 * Association for Computing Machinery, New York, NY, USA, 139–147. 
 * https://doi.org/10.1145/3512290.3528823
 * @author lewisj
 *
 * @param <T>
 */
public class MOME<T> implements SteadyStateEA<T>{

	protected MOMEArchive<T> archive;
								
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
	
	//logging variables 
	public boolean io;
	private boolean archiveFileCreated = false;	//track if the archive file is made
	
	// Not a MOMELog
	private MMNEATLog archiveLog = null; // Log general archive information. Does not use matrix plot, logged every generation
	
	//MOMELogs
	private MMNEATLog binPopulationSizeLog = null; // contains sizes of subpops in each bin, logged every generation
	private MMNEATLog hypervolumeLog = null;	//contains the size of the hypervolume for each bin's subpop, logged every generation
	private MMNEATLog[] maxFitnessLogs = null; //creates a log for each objective that contains the max fitness for each bin, logged every generation
	private MMNEATLog[] minFitnessLogs = null; //creates a log for each objective that contains the min fitness for each bin, logged every generation
	private MMNEATLog[] rangeFitnessLogs = null; //creates a log for each objective that contains the range from min to max for each bin, logged every generation

	//CONSTRUCTORS
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
		this.discardedIndividualCount = 0;
		this.individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
		this.individualCreationAttemptsCount = Parameters.parameters.integerParameter("lastSavedGeneration");
		//TODO: the above might cause issues
		
		//everything below here in this constructor deals with logging
		if(io) {	//logging
			String infix = "MOMEArchive";
			int numberOfObjectives = MMNEAT.task.numObjectives();
			int numberOfBinLabels = archive.getBinMapping().binLabels().size();
			
			// TODO: Contains MMNEATLogs for now, but later will be only those logs that are MOMELogs. Specifically, they have a column for each archive bin
			ArrayList<MMNEATLog> momeLogs = new ArrayList<>();
			
			// Logging in RAW mode so that can append to log file on experiment resume
			archiveLog = new MMNEATLog(infix, false, false, false, true);

			// These are MOMELogs
			binPopulationSizeLog = new MMNEATLog(infix+"_BinPopulation", false, false, false, true);
			momeLogs.add(binPopulationSizeLog);
			
			hypervolumeLog = new MMNEATLog(infix+"_Hypervolume", false, false, false, true);
			momeLogs.add(hypervolumeLog);
			
			maxFitnessLogs = new MMNEATLog[numberOfObjectives];
			minFitnessLogs = new MMNEATLog[numberOfObjectives];
			rangeFitnessLogs = new MMNEATLog[numberOfObjectives];
			
			String infixMin = infix + "_Min_Objective_";
			String infixMax = infix + "_Max_Objective_";
			String infixRange = infix + "_Range_";
			for (int i = 0; i < numberOfObjectives; i++) {
				minFitnessLogs[i] = new MMNEATLog(infixMin+MMNEAT.getFitnessFunctionName(i), false, false, false, true);
				maxFitnessLogs[i] = new MMNEATLog(infixMax+MMNEAT.getFitnessFunctionName(i), false, false, false, true);
				rangeFitnessLogs[i] = new MMNEATLog(infixRange+MMNEAT.getFitnessFunctionName(i), false, false, false, true);
				
				momeLogs.add(minFitnessLogs[i]);
				momeLogs.add(maxFitnessLogs[i]);
				momeLogs.add(rangeFitnessLogs[i]);
			}

			// Create gnuplot file for archive log
			String experimentPrefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber");					
			int yrange = Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration;
			System.out.println("yrange = " + yrange + ", from maxGens = "+Parameters.parameters.integerParameter("maxGens")+"/"+individualsPerGeneration+"=individualsPerGeneration");
			
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

		System.out.println("Initial population evaluated");
		
		CommonConstants.netio = backupNetIO;
	
		 // Add evaluated population to archive, if add is true
		evaluatedPopulation.parallelStream().forEach( (s) -> {
			archive.add(s); // Fill the archive with random starting individuals, only when this flag is true
		});

		System.out.println("Initial population binned");

		//initializing a variable
		this.mating = Parameters.parameters.booleanParameter("mating");
	}

	@Override
	public void newIndividual() {
//		System.out.println("new individual, current count:" + addedIndividualCount);

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
//			System.out.println("before individual creation call second child");

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
//		System.out.println("before individual creation call first child");

		afterIndividualCreationProcesses(archive.add(s1));			//this will call anything we need to do after making a new individual
//		System.out.println("after individual creation call first child");

		assert archive.checkLargestSubpopNotGreaterThanMaxLimit() : "largest subpop is greater than max allowed-END OF NEW INDIVIDUAL";
		
//		System.out.println("end new individual");

	}
	
	/**
	 * holds commands made after an individual is created
	 * synchronized for logging purposes and tracking add and discard counts
	 * @param individualAddStatus	if an individual was added or not
	 */
	//@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void afterIndividualCreationProcesses(boolean individualAddStatus) {
//		System.out.println("in afterIndividualCreation total in archive: " +archive.totalNumberOfIndividualsInArchive());
		individualCreationAttemptsCount++;
		log();			////////////////
		if(individualAddStatus) {	//the individual was added and the population changed
			addedIndividualCount++;
		} else {
			discardedIndividualCount++;
		}
//		System.out.println("outside afterIndividualCreationProcess");

	}
	
//GETTERS/SETTERS
	/**
	 * the current generation is the number of attempts to create individuals 
	 * divided by the number of individuals in a generation
	 * 
	 * This is required to end the experiment! Otherwise it never ends
	 */
	@Override
	public int currentIteration() {
		return individualCreationAttemptsCount/individualsPerGeneration;
	}

	/**
	 * gets an ArrayList of the populations genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> getPopulation() {
		//System.out.println("in get population");
		return archive.getPopulation();
	}

	// This and MAP Elites probably should have a common interface that specifies the need for this method
	/**
	 * a method that mirrors MAP Elites
	 * same as getWholeArchiveScores in MOMEArchive, but returns ArrayList instead of Vector
	 * @return every score for the archive in an ArrayList
	 */
	public ArrayList<Score<T>> getArchive() {
//		System.out.println("in getArchive");

		 ArrayList<Score<T>> result = new ArrayList<>(archive.totalNumberOfIndividualsInArchive());
		 Vector<Score<T>> archiveScores = archive.getWholeArchiveScores();
		 int i = 0;
		 for (Score<T> score : archiveScores) {
			result.add(score);
		}
		return result;
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
	
	public int getAddedIndividualCount() {
		return addedIndividualCount;
	}

//LOGGING RELATED METHODS / LOGGING METHODS
	/**
	 * Write one line of data to each of the active log files, but only periodically,
	 * when number of iterations divisible by individualsPerGeneration. 
	 */
	protected void log() {
//		System.out.println("in log method");
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
		

		//if an individual was added and the population count is even with the steadyStateIndividualsPerGeneration
		//this is all periodic logging to text files
		if(io && (individualCreationAttemptsCount%individualsPerGeneration == 0)) {
//			System.out.println("in log method if statement");

			// Synchronizing on the archive should stop any calculations/modifications that result in log irregularities.
			// However, such course locking could slow down the simulation.
			synchronized(archive) {
//				System.out.println("in log method synchronized archive");


				assert archive.checkLargestSubpopNotGreaterThanMaxLimit() : "largest subpop is greater than max allowed-INSIDE LOG\n"+archive.archiveDebug();

				final int pseudoGeneration = individualCreationAttemptsCount/individualsPerGeneration;
				
				//print statements for the beginning of logging
				//System.out.println("individuals per generation:"+ individualsPerGeneration + " parameter:" + Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration"));
				//System.out.println("generation:"+pseudoGeneration+ " addedIndividualCount:" +addedIndividualCount + " individualCreationAttemptsCount:" + individualCreationAttemptsCount + " discarded individuals:" + discardedIndividualCount);
				System.out.println("generation:"+pseudoGeneration);
//				System.out.println("max bin size:" + archive.maxSubPopulationSizeInWholeArchive() + " max allowed: " + Parameters.parameters.integerParameter("maximumMOMESubPopulationSize"));

				int numberOfObjectives = MMNEAT.task.numObjectives();

//				System.out.println("LOGGING POPULATION INFORMATION");

				//LOGGING POPULATION INFORMATION
				assert archive.checkLargestSubpopNotGreaterThanMaxLimit() : "largest subpop is greater than max allowed -LOGGING POP INFO";
				int[] populationSizesForBins = archive.populationSizeForEveryBin();
				assert archive.checkIfNotGreaterThanMaxSubpop(populationSizesForBins) : "this array of subpop sizes contains one that is bigger than the max allowed";
				String popString = Arrays.toString(populationSizesForBins).replace(", ", "\t");
				popString = pseudoGeneration + "\t" + popString.substring(1, popString.length() - 1); // Remove opening and closing [ ] brackets
				binPopulationSizeLog.log(popString);

//				System.out.println("LOGGING HYPERVOLUME INFORMATION");
				//LOGGING HYPERVOLUME INFORMATION
				double[] hypervolumeForBins = archive.hyperVolumeOfAllBins();
				String hypervolumeString = Arrays.toString(hypervolumeForBins).replace(", ", "\t");
				hypervolumeString = pseudoGeneration + "\t" + hypervolumeString.substring(1, hypervolumeString.length() - 1); // Remove opening and closing [ ] brackets

//				System.out.println("LOGGING OBJECTIVES MAX AND MIN INFORMATION");
				//LOGGING OBJECTIVES MAX AND MIN INFORMATION
				double[][] maxScoresBinXObjective = archive.maxScorebyBinXObjective(); //maxScores[bin][objective]
//				System.out.println("LOGGING OBJECTIVES MAX AND MIN INFORMATION MIDDLE");

				double[][] minScoresBinXObjective = archive.minScorebyBinXObjective(); //minScores[bin][objective]
				//initialize log with info labels

//				System.out.println("before forLoop");

				
				//loop through objectives to log max and min for each objectives log
				for (int i = 0; i < numberOfObjectives; i++) {

					//MAX FITNESS SCORES LOG
					double[] maxColumn = ArrayUtil.column(maxScoresBinXObjective, i);
					Double[] maxScoresForOneObjective = ArrayUtils.toObject(maxColumn);
					maxFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(maxScoresForOneObjective, " \t").replaceAll("-Infinity", "X"));

					//MIN FITNESS SCORES LOG
					double[] minColumn = ArrayUtil.column(minScoresBinXObjective, i);
					Double[] minScoresForOneObjective = ArrayUtils.toObject(minColumn);
					minFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(minScoresForOneObjective, "\t").replaceAll("Infinity", "X"));

					//RANGE FITNESS SCORES LOG
					// If a bin is empty, its min and max are negative infinity, but the range should be 0.
					// With floating point, negative infinity minus itself is not 0. So, instead, we convert negative infinity to 0,
					// which will lead to a result of 0 - 0 = 0.
					Arrays.parallelSetAll(maxColumn, j -> (Double.isInfinite(maxColumn[j]) && maxColumn[j] < 0) ? 0.0 : maxColumn[j]);
					Arrays.parallelSetAll(minColumn, j -> (Double.isInfinite(minColumn[j]) && minColumn[j] < 0) ? 0.0 : minColumn[j]);
					Double[] scoreRangesForOneObjective = ArrayUtils.toObject(ArrayUtil.zipSubtract(maxColumn, minColumn));
					rangeFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(scoreRangesForOneObjective, "\t"));
				}

//				System.out.println("in log method after forLoop");


				//////////////ARCHIVE LOGGING / GENERAL PLOT LOGGING
				/**
				 * when logging for the archive make sure when you add a variable here
				 * you also add it to setUpLogging to keep consistency
				 */
				//PSEUDOGENERATION, NUMBER OF OCCUPIED BINS, PERCENTAGE OF BINS FILLED
				double percentageOfBinsFilled = (archive.getNumberOfOccupiedBins()*1.0)/archive.getBinMapping().binLabels().size();
				String printString = pseudoGeneration+"\t"+archive.getNumberOfOccupiedBins()+"\t"+ percentageOfBinsFilled +"\t";

//				System.out.println("ARCHIVE LOGGGING AFTER GEN/BINS");

				//TOTAL NUMBER OF INDIVIDUALS CURRENTLY IN THE ARCHIVE, NUMBER OF ADDED AND DISCARCARDED INDIVIDUALS
				printString = printString + archive.totalNumberOfIndividualsInArchive()+"\t" + addedIndividualCount + "\t" + discardedIndividualCount + "\t";

		//total hypervolume is the hypervolume in whole archive pareto front
		//moqd is what I have right now
				
				//HYPERVOLUME, INDIVIDUAL BIN MAX,MIN,MEAN, HYPERVOLUME OF COMBINED PARETO FRONT, MOQDSCORE
				double maxHyperVolumeBin = StatisticsUtilities.maximum(hypervolumeForBins);
				double minHyperVolumeBin = StatisticsUtilities.minimum(ArrayUtil.replace(hypervolumeForBins, 0, Double.POSITIVE_INFINITY)); //replace 0 with pos infinity so it gets actual scores
				double meanHyperVolumeOfBins = StatisticsUtilities.average(hypervolumeForBins);
				double totalHypervolumeCombinedParetoFrontOfWholeArchive = archive.combinedParetoFrontWholeArchiveHypervolume();
				//log separately
				double totalHypervolumeMOQDScore = archive.totalHypervolumeMOQDScore();
				
//				System.out.println("max:"+maxHyperVolumeBin+" min:"+minHyperVolumeBin+" mean:"+meanHyperVolumeOfBins+" totalCombinedParetoFront:???"+" MOQDScore:"+ totalHypervolumeMOQDScore);
				//add the max and min hypervolume before the max and min of other things
				printString = printString + maxHyperVolumeBin + "\t" + minHyperVolumeBin + "\t" + meanHyperVolumeOfBins + "\t" + totalHypervolumeCombinedParetoFrontOfWholeArchive + "\t" + totalHypervolumeMOQDScore + "\t";

//				System.out.println("ARCHIVE LOGGGING BEFORE MAX/MIN FITNESS");
				
				//MAX/MIN FITNESS PER OBJECTIVE FOR WHOLE ARCHIVE
				//adding max fitness scores to print
				//since all bins are put together it simply gets the max fitness score from the array
				double[] maxFitnessScoresArray = archive.maxFitnessInWholeArchiveXObjective();
				for (int i = 0; i < maxFitnessScoresArray.length; i++) {
					printString = printString + maxFitnessScoresArray[i] + "\t";
				}
				//adding min fitness scores to print, must be done after max to keep the order of max then min
				double[] minFitnessScoresArray = archive.minFitnessInWholeArchiveXObjective();
				for (int i = 0; i < minFitnessScoresArray.length; i++) {
					printString = printString + minFitnessScoresArray[i] + "\t";
				}

				//			System.out.println(printString);

				archiveLog.log(printString);
//				System.out.println("end of synchronize archive log");

			}
//			System.out.println("end log method if statement");

		}

	}
	
	//stubs of things from MAPElites
	private void writeScriptLauncher(String directory, String prefix, String fullName, String[] dimensionNames,
			int[] dimensionSizes, PrintStream ps, String finalLine) {
		//might need later
	}
	
	/**
	 * This sets up all logging files necessary for the experiment.
	 * Creates: archive log, subpop size log, hypervolume log, max and min logs for every objective
	 * momeLogs is an array for all logs that use the same setup.
	 * this is all the GNU plot stuff
	 * @param numberOfBinLabels	the number of bins being used that will need to be logged
	 * @param infix	the intermediate string of all file names
	 * @param experimentPrefix	the prefix string used for all logs
	 * @param yrange
	 * @param individualsPerGeneration the number of individuals that counts as a generation
	 * @param momeLogs the collection of logs that use the same setup
	 */
	public static void setUpLogging(int numberOfBinLabels, String infix, String experimentPrefix, int yrange, int individualsPerGeneration, ArrayList<MMNEATLog> momeLogs) {
		System.out.println("setupLogging");
		String prefix = experimentPrefix + "_" + infix;
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		
		String fullName = directory + prefix + "_log.plt";
		File archivePlotFile = new File(fullName);

		PrintStream ps;
		
		//tracks placement based on things
		int columnIndex = 1;	//keeps track of where the item is in the archive log
		HashMap<String, Integer> logIndex = new HashMap<>(); //tracks the column placement of data in the archive log


		logIndex.put("pseudoGeneration", columnIndex++); logIndex.put("occupiedBins", columnIndex++); logIndex.put("percentageBins", columnIndex++);
		logIndex.put("totalIndividuals", columnIndex++); logIndex.put("addedIndividuals", columnIndex++); logIndex.put("discardedIndividuals", columnIndex++);
		logIndex.put("hypervolumeMax", columnIndex++); logIndex.put("hypervolumeMin", columnIndex++); logIndex.put("hypervolumeMean", columnIndex++); 
		logIndex.put("hypervolumeTotalCombinedPareto", columnIndex++); logIndex.put("hypervolumeTotalMOQDScore", columnIndex++);
		logIndex.put("maxFitnessByObjectiveStart", columnIndex++);
//		System.out.println("hypervolumeMax test:"+logIndex.get("hypervolumeMax")+ " Inside logging thehypervolume, before fitness");
//		System.out.println("Max fitness starts:"+logIndex.get("maxFitnessByObjectiveStart")+ " Inside logging the max fitness scores");

		
		try {
			ps = new PrintStream(archivePlotFile);
			ps.println("set term pdf enhanced");
			ps.println("set key bottom right");
//			ps.println("set xrange [0:"+ yrange +"]");
			ps.println("set xrange [0:"+ (yrange + 20) +"]");

			//occupied bins plot
			ps.println("set title \"" + experimentPrefix + " Archive Number of Occupied Bins\"");
			ps.println("set output \""+ prefix + "_OccupiedBins_log.pdf\"");
			ps.println("plot \"" + prefix + "_log.txt\" u 1:" + logIndex.get("occupiedBins") + " w linespoints t \"Number Of Occupied Bins\"");
			
			//percentage bins plot
			ps.println("set title \"" + experimentPrefix + " Percentage Of Bins Occupied\"");
			ps.println("set output \""+ prefix + "_OccupiedBinsPercentage_log.pdf\"");
			ps.println("plot \"" + prefix + "_log.txt\" u 1:" + logIndex.get("percentageBins") + " w linespoints t \"Percentage of Occupied Bins\"");
//			ps.println("plot \"" + prefix + "_log.txt\" u 1:" + 3 + " w linespoints t \"Percentage of Occupied Bins\"");

			//Total Individuals in Archive
			ps.println("set title \"" + experimentPrefix + " Total Individuals in Archive\"");
			ps.println("set output \""+ prefix + "_TotalIndividualsInArchive_log.pdf\"");
			ps.println("plot \"" + prefix + "_log.txt\" u 1:" + logIndex.get("totalIndividuals") + " w linespoints t \"Total Individuals\"");
			
			//Added/Discarded number of individuals
			ps.println("set title \"" + experimentPrefix + " Total Individuals Added And Discarded\"");
			ps.println("set output \""+ prefix + "_AddedAndDiscardedIndividualsInArchive_log.pdf\"");
			ps.println("plot \"" + prefix + "_log.txt\" u 1:" + logIndex.get("addedIndividuals") + " w linespoints t \"Added Individuals\", \\");
			ps.println("     \"" + prefix + "_log.txt\" u 1:" + logIndex.get("discardedIndividuals") + " w linespoints t \"Discarded Individuals\"");
	
			//HYPERVOLUME, INDIVIDUAL BIN MAX,MIN,MEAN, HYPERVOLUME OF COMBINED PARETO FRONT, MOQDSCORE
			//hypervolume data bin		//in gnuplot it should be global hypervolume --- combinedParetoFrontWholeArchiveHypervolume
			ps.println("set title \"" + experimentPrefix + " Max/Min/Mean/Total Hypervolume in the Whole Archive\"");
			ps.println("set output \""+ prefix + "_HypervolumeWholeArchive_log.pdf\"");
			ps.println("plot \"" + prefix + "_log.txt\" u 1:" + logIndex.get("hypervolumeMax") + " w linespoints t \"Max Hypervolume\", \\");
			ps.println("     \"" + prefix + "_log.txt\" u 1:" + logIndex.get("hypervolumeMin") + " w linespoints t \"Min Hypervolume\", \\");
			ps.println("     \"" + prefix + "_log.txt\" u 1:" + logIndex.get("hypervolumeMean") + " w linespoints t \"Mean Hypervolume\", \\");
			ps.println("     \"" + prefix + "_log.txt\" u 1:" + logIndex.get("hypervolumeTotalCombinedPareto") + " w linespoints t \"Global Hypervolume\"");
		
			//hypervolumeTotalMOQDScore
			ps.println("set title \"" + experimentPrefix + " MOQDScore\"");
			ps.println("set output \""+ prefix + "_HypervolumeMOQDScore_log.pdf\"");
			ps.println("plot \"" + prefix + "_log.txt\" u 1:" + logIndex.get("hypervolumeTotalMOQDScore") + " w linespoints t \"MOQDScore\"");
			
//			System.out.println("Max fitness starts:"+logIndex.get("maxFitnessByObjectiveStart"));
			//doing multiple max/min logs per objective
			for (int i = 0; i < MMNEAT.task.numObjectives(); i++) {
				ps.println("set title \"" + experimentPrefix + " Max/Min in objective " + MMNEAT.getFitnessFunctionName(i) + "\"");
				ps.println("set output \""+ prefix + "_MaxMinInEachObjective_" + MMNEAT.getFitnessFunctionName(i) + "_log.pdf\"");
				ps.println("plot \"" + prefix + "_log.txt\" u 1:" + (i+logIndex.get("maxFitnessByObjectiveStart")) + " w linespoints t \"Max Fitness\", \\");
				ps.println("     \"" + prefix + "_log.txt\" u 1:" + (i+logIndex.get("maxFitnessByObjectiveStart")+MMNEAT.task.numObjectives()) + " w linespoints t \"Min Fitness\"");
			}
			ps.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
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
				ps = new PrintStream(plotPDFFile);
				ps.println("set term pdf enhanced");
				ps.println("unset key");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set yrange [0:"+ yrange +"]");
				ps.println("set xrange [1:"+ numberOfBinLabels + "]");
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
				ps.println("set xrange [1:"+ numberOfBinLabels + "]");
				ps.println("set title \"" + logTitle + "\"");
				// The :1 is for skipping the "generation" number logged in the file
				ps.println("plot \"" + textLogFilename + "\" matrix every ::1 with image");
				// ps.println("pause -1"); // Not needed when only one item is plotted?
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


		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		String prefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber") + "_MOMEElites";
		String fullName = directory + prefix + "_log.plt";
		System.out.println("archive vizualizer full name: " + fullName);
		//fullname = mometest/MEObserverVectorPistonOrientation7/MOMETest-MEObserverVectorPistonOrientation7_MOMEElites_log.plt


		PythonUtil.setPythonProgram();
		PythonUtil.checkPython();
	}
	
	@Override
	public void finalCleanup() {
		//setup finalCleanup logging
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		
		//this makes the directory folder for pareto fronts
		String saveDirectoryParetoFronts = directory + "/ParetoFronts";
		File directoryParetoFile = new File(saveDirectoryParetoFronts);
		if(!directoryParetoFile.exists()) {
			directoryParetoFile.mkdir();
		}
		System.out.println("pareto front directory name: "+saveDirectoryParetoFronts);

		
		int numberOfObjectives = MMNEAT.task.numObjectives();
//		int numberOfBinLabels = archive.getBinMapping().binLabels().size();
		int numberOfOccupiedBins = archive.getNumberOfOccupiedBins();
		
		//logging aggregate file
		File paretoFrontAggregateOutput = new File(saveDirectoryParetoFronts + "/AggregateFront.txt");
		System.out.println("aggregat name: " + saveDirectoryParetoFronts + "/AggregateFront.txt");

		
		PrintStream ps;
		try {
			ps = new PrintStream(paretoFrontAggregateOutput);

			///AGGREGATE LOGGING
			
			Vector<Score<T>> archiveFinalParetoFront = archive.getCombinedParetoFrontWholeArchive();
			//go through score for row
			//column is objectives
			for (Score<T> score : archiveFinalParetoFront) {
				String scoreString = "";
				for (int i = 0; i < numberOfObjectives; i++) {
					scoreString = scoreString + score.scores[i] + "\t";
				}
				ps.println(scoreString);
			}
			ps.close();
			
//			
//			System.out.println("about to make aggregate .plt");
//			String logTitle = saveDirectoryParetoFronts+"/AggregateFront.txt";
//			System.out.println("logTitle: " + logTitle);
//			String plotFilename = saveDirectoryParetoFronts+"/AggregateFront.plt";
//			System.out.println("plotFilename: " + plotFilename);
//			
//			String plotPDFFilename = plotFilename.replace(".plt", "_PDF.plt");
//			File plotFile = new File(plotFilename);
//			int yrange = 300;
//			int xrange = 300;
//			
//			try {
////				// The PDF version
////				ps = new PrintStream(plotPDFFile);
////				ps.println("set term pdf enhanced");
////				ps.println("unset key");
////				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
////				ps.println("set yrange [0:"+ yrange +"]");
////				ps.println("set xrange [1:"+ numberOfBinLabels + "]");
////				ps.println("set title \"" + logTitle + "\"");
////				ps.println("set output \"" + pdfFilename + "\"");				
////				// The :1 is for skipping the "generation" number logged in the file
////				ps.println("plot \"" + textLogFilename + "\" matrix every ::1 with image");
////				ps.close();
////				double[] maxPerObjective = archive.maxFitnessInWholeArchiveXObjective();
////				double yrange = maxPerObjective[0];
////				double xrange = maxPerObjective[1];
//				
//				// Non-PDF version
//				ps = new PrintStream(plotFile);
//				ps.println("unset key");
//				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
//				ps.println("set yrange [0:"+ (yrange+10) +"]");
//				ps.println("set xrange [1:"+ (xrange+10) + "]");
//				ps.println("set title \"" + logTitle + "\"");
//				// The :1 is for skipping the "generation" number logged in the file
//				ps.println("plot \"" + logTitle + "\" matrix every ::1 with image");
//				// ps.println("pause -1"); // Not needed when only one item is plotted?
//				ps.close();
//		
//			} catch (FileNotFoundException e) {
//				System.out.println("Error creating plt log file");
//				e.printStackTrace();
//				System.exit(1);
//			}
			
			
			
			
			
			int iLogs = 0;	//anytime a log is created, increment and check that it's not out of bounds
			
			//LOGGING FOR BINS
			
			//goes though all occupied bins I think?
			for (Vector<Integer> key : archive.archive.keySet()) {
				if(key.size() > 0) {			//for a bin that has individuals

					//SET UP BIN LOG FILE
					String binLabel = archive.getBinLabel(key);

					File paretoFrontSingleBinFile = new File(saveDirectoryParetoFronts + "/"+ binLabel+ ".txt");

					ps = new PrintStream(paretoFrontSingleBinFile);
					
					Vector<Score<T>> scoresForBin = archive.getScoresForBin(key);

					//GET A SINGLE ROW LOGGED
					//for each score in the bin, log that scores data on one row
					for (Score<T> score : scoresForBin) {
						//log this score in the bin for each objective, or create string
						String scoreString = "";
						for (int i = 0; i < numberOfObjectives; i++) {
							//this is objective i ----- adds objective column score to that scores row
							scoreString = scoreString + score.scores[i] + "\t";
							//string + score for objective i + tab
						}
						ps.println(scoreString);
					}
					ps.close();
				}else {
					System.out.println("this is an empty bin, I don't think this happens though?");
				}
				iLogs++;
				if(iLogs > numberOfOccupiedBins) {
					System.out.println("i logs greater than the number of logs " + iLogs + " number of occupied bins:" + numberOfOccupiedBins);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Logging of AggregateFront.txt failed");
			e.printStackTrace();
		}
				
		task.finalCleanup();
	}
	

	public static void main (String[] args) {
		try {
			// This is a GECCO set of parameters with some minor changes for MOME
			//had to have a server running first
			//there is an issue with bin labels - MMNEAT.java method getArchiveBinLabelsClass & getArchive
			//Attempted to get archive bin label class without using MAP Elites or a psuedo-archive
			MMNEAT.main("runNumber:99 randomSeed:2 minecraftMaximizeVolumeFitness:true trackPseudoArchive:false minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:mometest log:MOMETest-currentlyTesting saveTo:currentlyTesting mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));
//above is quick check
			//MMNEAT.main("runNumber:7 randomSeed:2 minecraftMaximizeVolumeFitness:true trackPseudoArchive:false minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:mometest log:MOMETest-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));
			//MMNEAT.main("runNumber:5 randomSeed:1 minecraftMaximizeVolumeFitness:true minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:5 maxGens:1 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:5 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:mometest log:MOMETest-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
//archive contains 125 bins
