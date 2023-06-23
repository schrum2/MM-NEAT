package edu.southwestern.evolution.mome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
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
//	private String fileNameInfix = "MOMEArchive";
//	private static String fileNameFullName;
//	private static String fileNameInfixMax = "MOMEArchive_Max_Objective_";
//	private static String fileNameInfixMin = "MOMEArchive_Min_Objective_";
	private static String maxPrefix = "_Max_Objective_";
	private static String minPrefix = "_Min_Objective_";
	
	//log files only
	private MMNEATLog archiveLog = null; // Archive elite scores
//	private MMNEATLog randomLog = null; //general random test logging for now
	private MMNEATLog binPopulationSizeLog = null; //general random test logging for now
	private MMNEATLog[] maxFitnessLogs = null; //creates a log for each objective that contains the max fitness for each bin, logged every generation
	private MMNEATLog[] minFitnessLogs = null; //creates a log for each objective that contains the min fitness for each bin, logged every generation
//	private MMNEATLog[] gnuLogs = null; //creates a log for each objective that contains ????? for each bin, logged every generation
	private MMNEATLog[] gnuMaxLogs = null; //creates a log for each objective that contains ????? for each bin, logged every generation
	private MMNEATLog[] gnuMinLogs = null; //creates a log for each objective that contains ????? for each bin, logged every generation

	

	public MOME() {
		this(Parameters.parameters.stringParameter("archiveSubDirectoryName"), Parameters.parameters.booleanParameter("io"), Parameters.parameters.booleanParameter("netio"), true);
	}
	/**
	 * MMNEAT.usingDiversityBinningScheme = true;
		this.task = (LonerTask<T>) MMNEAT.task;
		this.io = ioOption; // write logs
		this.archive = new Archive<>(netioOption, archiveSubDirectoryName);
	 */
	
	/**
	 * 
	 * @param archiveSubDirectoryName
	 * @param ioOption
	 * @param netioOption
	 * @param createLogs
	 */
	
	@SuppressWarnings("unchecked")
	public MOME(/*TODO*/String archiveSubDirectoryName, boolean ioOption, boolean netioOption, boolean createLogs) {
		MMNEAT.usingDiversityBinningScheme = true;
		this.task = (LonerTask<T>) MMNEAT.task;
		this.archive = new MOMEArchive<>(netioOption, archiveSubDirectoryName);	//set up archive

		this.io = ioOption; // write logs
		//TODO: figure out how we get this number below
		this.mating = Parameters.parameters.booleanParameter("mating");
		this.crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
//		this.populationChangeCheck = false;
		this.addedIndividualCount = 0;
//		this.setDiscardedIndividualCount(0);		//broke this, need to investigate later
		this.individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
		this.individualCreationAttemptsCount = Parameters.parameters.integerParameter("lastSavedGeneration");
		//TODO: the above might cause issues
		
		
//////TODO: logging below //////////////////////////////////////////////////////////////////////////////
		if(io && createLogs) {
			//logging
			String infix = "MOMEArchive";
			int numberOfObjectivesToLog = MMNEAT.task.numObjectives();
			int numberOfBinLabels = archive.getBinMapping().binLabels().size();
			
			// Logging in RAW mode so that can append to log file on experiment resume
			archiveLog = new MMNEATLog(infix, false, false, false, true);
//		randomLog = new MMNEATLog("random", false, false, false, true);
			binPopulationSizeLog = new MMNEATLog("BinPopulation", false, false, false, true);
			maxFitnessLogs = new MMNEATLog[numberOfObjectivesToLog];
			minFitnessLogs = new MMNEATLog[numberOfObjectivesToLog];
//			gnuLogs = new MMNEATLog[2*numberOfObjectivesToLog];
			gnuMaxLogs = new MMNEATLog[numberOfObjectivesToLog];
			gnuMinLogs = new MMNEATLog[numberOfObjectivesToLog];
//remove			System.out.println("MOME creation infix: "+ infix);		// delete later


			String infixMin = infix + "_Min_Objective_";
			String infixMax = infix + "_Max_Objective_";
//remove			System.out.println("MOME creation-- Max: " + infixMax + " min: " + infixMin);		// remove later

// here is where to rename the gnu plots
			for (int i = 0; i < numberOfObjectivesToLog; i++) {
//				gnuLogs[i] = new MMNEATLog(infix + i, false, false, false, true);
				minFitnessLogs[i] = new MMNEATLog(infixMin + i, false, false, false, true);
				maxFitnessLogs[i] = new MMNEATLog(infixMax +i, false, false, false, true);
				gnuMaxLogs[i] = new MMNEATLog(infixMax +i, false, false, false, true);
				gnuMinLogs[i] = new MMNEATLog(infixMin +i, false, false, false, true);
			}
//			for (int i = numberOfObjectivesToLog; i < gnuLogs.length; i++) {
//				gnuLogs[i] = new MMNEATLog(infix + i, false, false, false, true);
//			}

			// Create gnuplot file for archive log
			String experimentPrefix = Parameters.parameters.stringParameter("log")
					+ Parameters.parameters.integerParameter("runNumber");
			
//remove later			System.out.println("MOME creation-- experiment prefix: " + experimentPrefix);		// remove later

//			String directory = FileUtilities.getSaveDirectory();
//			directory += (directory.equals("") ? "" : "/");
//			fileNameFullName = directory + Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber") + "_" + "MOMEArchive";
					
//			individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
			int yrange = Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration;
			setUpLogging(numberOfBinLabels, infix, experimentPrefix, yrange, individualsPerGeneration);
//			setUpLogging(numberOfBinLabels, experimentPrefix, yrange, individualsPerGeneration);
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
			String popString = "";
			int[] populationSizesForBins = archive.populationSizeForEveryBin();
			for (int i = 0; i < populationSizesForBins.length; i++) {
				popString = popString + populationSizesForBins[i] + "\t";
//				System.out.println("popSizes only:" + populationSizesForBins[i]);

			}
			//System.out.println("popSizes MOME:" + popString);
			binPopulationSizeLog.log(popString);
			//String populationSizeString = "";

			
			//LOGGING OBJECTIVES MAX AND MIN
			double[][] maxScoresBinXObjective = archive.maxScorebyBinXObjective(); //maxScores[bin][objective]
			double[][] minScoresBinXObjective = archive.minScorebyBinXObjective(); //minScores[bin][objective]
	//initialize log with info labels
			
			//loop through objectives to log max and min for each objectives log
			for (int i = 0; i < numberOfObjectives; i++) {
				
				//MAX FITNESS SCORES LOG
				Double[] maxScoresForOneObjective = ArrayUtils.toObject(ArrayUtil.column(maxScoresBinXObjective, i));
				maxFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(maxScoresForOneObjective, " \t").replaceAll("-Infinity", "X"));
				
				//MIN FITNESS SCORES LOG
				Double[] minScoresForOneObjective = ArrayUtils.toObject(ArrayUtil.column(minScoresBinXObjective, i));
				minFitnessLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(minScoresForOneObjective, "\t").replaceAll("-Infinity", "X"));
				
				//POPULATION LOG --- can probably move down
				//populationSizeString = populationSizeString + populationSizesForBins[i] + "\t";
				//System.out.println("popSizearray:" + populationSizesForBins[i]);

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
	public static void setUpLogging(int numberOfBinLabels, String infix, String experimentPrefix, int yrange, int individualsPerGeneration) {
//	public static void setUpLogging(int numberOfBinLabels, String experimentPrefix, int yrange, int individualsPerGeneration) {
		//this is for logging, copied all the parameters but probably don't need it all
		
		String prefix = experimentPrefix + "_" + infix;
		System.out.println("A-setup logging-- prefix: " + prefix);		// remove later

//		String fillPrefix = experimentPrefix + "_" + "Fill";
//		String fillDiscardedPrefix = experimentPrefix + "_" + "FillWithDiscarded";
//		String fillPercentagePrefix = experimentPrefix + "_" + "FillPercentage";
//		String qdPrefix = experimentPrefix + "_" + "QD";
//		String maxPrefix = experimentPrefix + "_" + "Maximum_Objective_";
//		String minPrefix = experimentPrefix + "_" + "Minimum_Objective_";
//		String lossPrefix = experimentPrefix + "_" + "ReconstructionLoss";
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
//remove		System.out.println("B-setup logging-- directory 1: " + directory);		// remove later

		directory += (directory.equals("") ? "" : "/");
//remove		System.out.println("C-setup logging-- directory 2: " + directory);		// remove later

//
		String fullName = directory + prefix + "_log.plt";
//remove		System.out.println("D-setup logging-- fullName: " + fullName);		// remove later

//		String fullFillName = directory + fillPrefix + "_log.plt";
//		String fullFillDiscardedName = directory + fillDiscardedPrefix + "_log.plt";
//		String fullFillPercentageName = directory + fillPercentagePrefix + "_log.plt";
//		String fullQDName = directory + qdPrefix + "_log.plt";
//		String maxFitnessName = directory + maxPrefix + "_log.plt";
//		String reconstructionLossName = directory + lossPrefix + "_log.plt";
		
//		//	private String fileNameInfix = "MOMEArchive";
//		private String fileNameFullName;
//		private String fileNameInfixMax = "MOMEArchive_Max_Objective_";
//		private String fileNameInfixMin = "MOMEArchive_Min_Objective_";
//		//log files only

		
		individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");

		
		for (int i = 0; i < MMNEAT.task.numObjectives(); i++) {
			//private MMNEATLog[] maxFitnessLogs
			//private MMNEATLog[] minFitnessLogs 
			//private MMNEATLog[] gnuLogs = n
			
			
			String fullNameMaxHARDCODED = directory + prefix + maxPrefix + i;
			String fullNameMinHARDCODED = directory + prefix + minPrefix + i;
//remove			System.out.println("H-setup logging-- fullNameMaxHARDCODED: " + fullNameMaxHARDCODED + " fullNameMinHARDCODED: " + fullNameMinHARDCODED);		//: remove later

			
			String fullPDFNameMax = directory + prefix + maxPrefix + i + "_pdf_log.plt";
//			String fullPDFNameMax = fileNameFullName + fileNameInfixMax + i;
//			String fullPDFNameMin = fileNameFullName + fileNameInfixMin + i;
			String fullPDFNameMin = directory + prefix + minPrefix + i + "_pdf_log.plt";
//			String fullPDFNameMax = 
			
//remove			System.out.println("I-setup logging-- fullPDFNameMax: " + fullPDFNameMax + " fullPDFNameMin: " + fullPDFNameMin);		//: remove later

			File pdfPlotMax = new File(fullPDFNameMax);
			File pdfPlotMin = new File(fullPDFNameMin);
			PrintStream ps;
			//I separated the max and min because they each have their own set.			
			
			try {
				//max pdf plots
				ps = new PrintStream(pdfPlotMax);
				ps.println("set term pdf enhanced");
				ps.println("unset key");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set yrange [0:"+ yrange +"]");
				ps.println("set xrange [0:"+ numberOfBinLabels + "]");
				ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
				ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
				
//remove				System.out.println("J-setup logging-- " + "set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");		//: remove later

//				ps.println("set output \"" + fileNameFullName.substring(fileNameFullName.lastIndexOf('/')+1, fileNameFullName.lastIndexOf('.')) + ".pdf\"");
				// The :1 is for skipping the "generation" number logged in the file
//				ps.println("plot \"" + fileNameFullName.substring(fileNameFullName.lastIndexOf('/')+1, fileNameFullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");
				ps.println("plot \"" + "Testing-TESTING11_MOMEArchive_Min_Objective_" + i + "_log" + ".txt\" matrix every ::1 with image");

				ps.close();
				
				//Testing-TESTING11_MOMEArchive_Min_Objective_0_log
				

				//I'm not sure what the below is
//				ps.println("set title \"" + experimentPrefix + " Maximum individual fitness score");
//				ps.println("set output \"" + maxFitnessName.substring(maxFitnessName.lastIndexOf('/')+1, maxFitnessName.lastIndexOf('.')) + ".pdf\"");
//				ps.println("plot \"" + name + ".txt\" u 1:4 w linespoints t \"Maximum Fitness Score\", \\");
//				ps.println("     \"" + name + ".txt\" u 1:8 w linespoints t \"Restricted Maximum Fitness Score\"");
//				
				
				
				//min pdf plots
				ps = new PrintStream(pdfPlotMin);
				ps.println("set term pdf enhanced");
				ps.println("unset key");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set yrange [0:"+ yrange +"]");
				ps.println("set xrange [0:"+ numberOfBinLabels + "]");
				ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
				ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
//				// The :1 is for skipping the "generation" number logged in the file
//				ps.println("plot \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");
//				ps.println("set output \"" + fileNameFullName.substring(fileNameFullName.lastIndexOf('/')+1, fileNameFullName.lastIndexOf('.')) + ".pdf\"");
				// The :1 is for skipping the "generation" number logged in the file
//				ps.println("plot \"" + fullPDFNameMin + ".txt\" matrix every ::1 with image");
				
				ps.println("plot \"" + fullNameMinHARDCODED + ".txt\" matrix every ::1 with image");

				ps.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
//		File pdfPlot = new File(fullPDFName);
//		File plot = new File(fileNameFullName); // for archive log plot file
		File plot = new File(fullName); // for archive log plot file
//		File fillPlot = new File(fullFillName);
		
		// Write to file
				try {
					// Archive PDF plot
//					individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
//					PrintStream ps = new PrintStream(pdfPlot);
//					ps.println("set term pdf enhanced");
//					ps.println("unset key");
//					// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
//					ps.println("set yrange [0:"+ yrange +"]");
//					ps.println("set xrange [0:"+ numberOfBinLabels + "]");
//					ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
//					ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
//					// The :1 is for skipping the "generation" number logged in the file
//					ps.println("plot \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");
//					ps.close();
					
					// Archive plot: In default GNU Plot window
					PrintStream ps = new PrintStream(plot);
					ps.println("unset key");
					// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
					ps.println("set yrange [0:"+ yrange +"]");
					ps.println("set xrange [0:"+ numberOfBinLabels + "]");
					ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
					ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
					// The :1 is for skipping the "generation" number logged in the file
					ps.println("plot \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");
//					ps.println("plot \"" + fileNameFullName.substring(fileNameFullName.lastIndexOf('/')+1, fileNameFullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");

//remove					System.out.println("J-setup logging -- " + "plot \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image"); //: delete later
					System.out.println("K-setup logging -- " + "set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
					ps.close();

					
				} catch (FileNotFoundException e) {
					System.out.println("Could not create plot file: " + plot.getName());
					e.printStackTrace();
					System.exit(1);
				}
		/**
		 * String prefix = experimentPrefix + "_" + infix;
		String fillPrefix = experimentPrefix + "_" + "Fill";
		String fillDiscardedPrefix = experimentPrefix + "_" + "FillWithDiscarded";
		String fillPercentagePrefix = experimentPrefix + "_" + "FillPercentage";
		String qdPrefix = experimentPrefix + "_" + "QD";
		String maxPrefix = experimentPrefix + "_" + "Maximum";
		String lossPrefix = experimentPrefix + "_" + "ReconstructionLoss";
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		String fullPDFName = directory + prefix + "_pdf_log.plt";
		String fullName = directory + prefix + "_log.plt";
		String fullFillName = directory + fillPrefix + "_log.plt";
		String fullFillDiscardedName = directory + fillDiscardedPrefix + "_log.plt";
		String fullFillPercentageName = directory + fillPercentagePrefix + "_log.plt";
		String fullQDName = directory + qdPrefix + "_log.plt";
		String maxFitnessName = directory + maxPrefix + "_log.plt";
		String reconstructionLossName = directory + lossPrefix + "_log.plt";
		File pdfPlot = new File(fullPDFName);
		File plot = new File(fullName); // for archive log plot file
		File fillPlot = new File(fullFillName);
		// Write to file
		try {
			// Archive PDF plot
			individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
			PrintStream ps = new PrintStream(pdfPlot);
			ps.println("set term pdf enhanced");
			ps.println("unset key");
			// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
			ps.println("set yrange [0:"+ yrange +"]");
			ps.println("set xrange [0:"+ archiveSize + "]");
			ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
			ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
			// The :1 is for skipping the "generation" number logged in the file
			ps.println("plot \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");
			ps.close();
			
			// Archive plot: In default GNU Plot window
			ps = new PrintStream(plot);
			ps.println("unset key");
			// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
			ps.println("set yrange [0:"+ yrange +"]");
			ps.println("set xrange [0:"+ archiveSize + "]");
			ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
			//ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
			// The :1 is for skipping the "generation" number logged in the file
			ps.println("plot \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");
			ps.close();
			
			
			// Fill percentage plot
			ps = new PrintStream(fillPlot);
			ps.println("set term pdf enhanced");
			//ps.println("unset key");
			ps.println("set key bottom right");
			// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
			ps.println("set xrange [0:"+ yrange +"]");
			ps.println("set title \"" + experimentPrefix + " Archive Filled Bins\"");
			ps.println("set output \"" + fullFillDiscardedName.substring(fullFillDiscardedName.lastIndexOf('/')+1, fullFillDiscardedName.lastIndexOf('.')) + ".pdf\"");
			String name = fullFillName.substring(fullFillName.lastIndexOf('/')+1, fullFillName.lastIndexOf('.'));
			ps.println("plot \"" + name + ".txt\" u 1:2 w linespoints t \"Total\", \\");
			ps.println("     \"" + name + ".txt\" u 1:5 w linespoints t \"Discarded\"" + (cppnDirLogging ? ", \\" : ""));
			if(cppnDirLogging) { // Print CPPN and direct counts on same plot
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:2 w linespoints t \"CPPNs\", \\");
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:3 w linespoints t \"Vectors\"");
			}
			
			ps.println("set title \"" + experimentPrefix + " Archive Filled Bins Percentage\"");
			ps.println("set output \"" + fullFillPercentageName.substring(fullFillPercentageName.lastIndexOf('/')+1, fullFillPercentageName.lastIndexOf('.')) + ".pdf\"");
			ps.println("plot \"" + name + ".txt\" u 1:($2 / "+numLabels+") w linespoints t \"Total\"" + (cppnDirLogging ? ", \\" : ""));
			if(cppnDirLogging) { // Print CPPN and direct counts on same plot
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:2 w linespoints t \"CPPNs\", \\");
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:3 w linespoints t \"Vectors\"");
			}
			
			ps.println("set title \"" + experimentPrefix + " Archive Filled Bins\"");
			ps.println("set output \"" + fullFillName.substring(fullFillName.lastIndexOf('/')+1, fullFillName.lastIndexOf('.')) + ".pdf\"");
			ps.println("plot \"" + name + ".txt\" u 1:2 w linespoints t \"Total\", \\");
			ps.println("     \"" + name + ".txt\" u 1:6 w linespoints t \"Restricted\"" + (cppnDirLogging ? ", \\" : ""));
			if(cppnDirLogging) { // Print CPPN and direct counts on same plot
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:2 w linespoints t \"CPPNs\", \\");
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:3 w linespoints t \"Vectors\"");
			}
			
			ps.println("set title \"" + experimentPrefix + " Archive QD Scores\"");
			ps.println("set output \"" + fullQDName.substring(fullQDName.lastIndexOf('/')+1, fullQDName.lastIndexOf('.')) + ".pdf\"");
			ps.println("plot \"" + name + ".txt\" u 1:3 w linespoints t \"QD Score\", \\");
			ps.println("     \"" + name + ".txt\" u 1:7 w linespoints t \"Restricted QD Score\"");
			
			ps.println("set title \"" + experimentPrefix + " Maximum individual fitness score");
			ps.println("set output \"" + maxFitnessName.substring(maxFitnessName.lastIndexOf('/')+1, maxFitnessName.lastIndexOf('.')) + ".pdf\"");
			ps.println("plot \"" + name + ".txt\" u 1:4 w linespoints t \"Maximum Fitness Score\", \\");
			ps.println("     \"" + name + ".txt\" u 1:8 w linespoints t \"Restricted Maximum Fitness Score\"");
			
			if(Parameters.parameters.booleanParameter("dynamicAutoencoderIntervals")) {
				ps.println("set title \"" + experimentPrefix + " Reconstruction Loss Range");
				ps.println("set output \"" + reconstructionLossName.substring(reconstructionLossName.lastIndexOf('/')+1, reconstructionLossName.lastIndexOf('.')) + ".pdf\"");
				ps.println("plot \"" + name.replace("_Fill_", "_autoencoderLossRange_") + ".txt\" u 1:2 w linespoints t \"Min Loss\", \\");
				ps.println("     \"" + name.replace("_Fill_", "_autoencoderLossRange_") + ".txt\" u 1:3 w linespoints t \"Max Loss\"");
			}
			
			ps.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Could not create plot file: " + plot.getName());
			e.printStackTrace();
			System.exit(1);
		}
		 */

	}
	
	/**
	 * TODO: I'm not too sure about this method.
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
