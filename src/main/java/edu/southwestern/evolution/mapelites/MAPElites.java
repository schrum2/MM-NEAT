package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import autoencoder.python.AutoEncoderProcess;
import autoencoder.python.TrainAutoEncoderProcess;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.generalmappings.MultiDimensionalRealValuedSlicedBinLabels;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.innovationengines.PictureTargetTask;
import edu.southwestern.tasks.loderunner.LodeRunnerLevelTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;
import wox.serial.Easy;

/**
 * My version of Multi-dimensional Archive of Phenotypic Elites (MAP-Elites), the quality diversity (QD)
 * algorithms that illuminates a search space. This is an unusual implementation, but it gets the job done.
 * 
 * MAP Elites article: https://arxiv.org/abs/1504.04909
 * 
 * @author schrum2
 *
 * @param <T> phenotype
 */
public class MAPElites<T> implements SteadyStateEA<T> {
	private static final int NUM_CODE_EMPTY = -1;
	private static final int NUM_CODE_DIRECT = 2;
	private static final int NUM_CODE_CPPN = 1;
	public boolean io;
	private MMNEATLog archiveLog = null; // Archive elite scores
	private MMNEATLog fillLog = null; // Archive fill amount
	private MMNEATLog emitterMeanLog = null;
	private MMNEATLog cppnThenDirectLog = null;
	private MMNEATLog cppnVsDirectFitnessLog = null;
	protected MMNEATLog[] emitterIndividualsLogs = null;
	protected LonerTask<T> task;
	protected Archive<T> archive;
	private boolean mating;
	private double crossoverRate;
	protected int iterations;
	private int iterationsWithoutEliteCounter;
	private int iterationsWithoutElite;
	private int individualsPerGeneration;
	private boolean archiveFileCreated = false;
	private boolean saveImageArchives;

	public BinLabels getBinLabelsClass() {
		return archive.getBinLabelsClass();
	}
	
	public MAPElites() {
		this(Parameters.parameters.stringParameter("archiveSubDirectoryName"), Parameters.parameters.booleanParameter("io"), Parameters.parameters.booleanParameter("netio"), true);
	}
	
	@SuppressWarnings("unchecked")
	public MAPElites(String archiveSubDirectoryName, boolean ioOption, boolean netioOption, boolean createLogs) {
		MMNEAT.usingDiversityBinningScheme = true;
		this.task = (LonerTask<T>) MMNEAT.task;
		this.io = ioOption; // write logs
		this.archive = new Archive<>(netioOption, archiveSubDirectoryName);
		if(io && createLogs) {
			int numLabels = archive.getBinMapping().binLabels().size();
			String infix = "MAPElites";
			// Logging in RAW mode so that can append to log file on experiment resume
			archiveLog = new MMNEATLog(infix, false, false, false, true); 
			fillLog = new MMNEATLog("Fill", false, false, false, true);
			// Can't check MMNEAT.genotype since MMNEAT.ea is initialized before MMNEAT.genotype
			boolean cppnDirLogging = Parameters.parameters.classParameter("genotype").equals(CPPNOrDirectToGANGenotype.class);
			if(cppnDirLogging) {
				cppnThenDirectLog = new MMNEATLog("cppnToDirect", false, false, false, true);
				cppnVsDirectFitnessLog = new MMNEATLog("cppnVsDirectFitness", false, false, false, true);
			}
			// Create gnuplot file for archive log
			String experimentPrefix = Parameters.parameters.stringParameter("log")
					+ Parameters.parameters.integerParameter("runNumber");
			individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
			int yrange = Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration;
			setUpLogging(numLabels, infix, experimentPrefix, yrange, cppnDirLogging, individualsPerGeneration, archive.getBinMapping().binLabels().size());
			if (this instanceof CMAME) {
				emitterMeanLog = new MMNEATLog("EmitterMeans", false, false, false, true);
			}
		}
		this.mating = Parameters.parameters.booleanParameter("mating");
		this.crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		this.iterations = Parameters.parameters.integerParameter("lastSavedGeneration");
		this.iterationsWithoutEliteCounter = 0;
		this.iterationsWithoutElite = 0; // Not accurate on resume		
	}

	public static void setUpLogging(int numLabels, String infix, String experimentPrefix, int yrange, boolean cppnDirLogging, int individualsPerGeneration, int archiveSize) {
		
		String prefix = experimentPrefix + "_" + infix;
		String fillPrefix = experimentPrefix + "_" + "Fill";
		String fillDiscardedPrefix = experimentPrefix + "_" + "FillWithDiscarded";
		String fillPercentagePrefix = experimentPrefix + "_" + "FillPercentage";
		String qdPrefix = experimentPrefix + "_" + "QD";
		String maxPrefix = experimentPrefix + "_" + "Maximum";
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		String fullPDFName = directory + prefix + "_pdf_log.plt";
		String fullName = directory + prefix + "_log.plt";
		String fullFillName = directory + fillPrefix + "_log.plt";
		String fullFillDiscardedName = directory + fillDiscardedPrefix + "_log.plt";
		String fullFillPercentageName = directory + fillPercentagePrefix + "_log.plt";
		String fullQDName = directory + qdPrefix + "_log.plt";
		String maxFitnessName = directory + maxPrefix + "_log.plt";
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
			
			// Archive plot
			ps = new PrintStream(plot);
			ps.println("unset key");
			// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
			ps.println("set yrange [0:"+ yrange +"]");
			ps.println("set xrange [0:"+ archiveSize + "]");
			ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
			ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
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
			ps.println("plot \"" + name + ".txt\" u 1:2 w linespoints t \"Total\"" + (cppnDirLogging ? ", \\" : ""));
			if(cppnDirLogging) { // Print CPPN and direct counts on same plot
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:2 w linespoints t \"CPPNs\", \\");
				ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:3 w linespoints t \"Vectors\"");
			}
			
			ps.println("set title \"" + experimentPrefix + " Archive QD Scores\"");
			ps.println("set output \"" + fullQDName.substring(fullQDName.lastIndexOf('/')+1, fullQDName.lastIndexOf('.')) + ".pdf\"");
			ps.println("plot \"" + name + ".txt\" u 1:3 w linespoints t \"QD Score\"");
			
			ps.println("set title \"" + experimentPrefix + " Maximum individual fitness score");
			ps.println("set output \"" + maxFitnessName.substring(maxFitnessName.lastIndexOf('/')+1, maxFitnessName.lastIndexOf('.')) + ".pdf\"");
			ps.println("plot \"" + name + ".txt\" u 1:4 w linespoints t \"Maximum fitness Score\"");
			
			ps.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Could not create plot file: " + plot.getName());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void checkPython() { // Checks that python is present and functional
		PythonUtil.setPythonProgram();
		if(PythonUtil.PYTHON_EXECUTABLE.equals("")) {
			throw new RuntimeException("Before launching this program, you need to place the path to your "+
					   "Python executable in my_python_path.txt within the main MM-NEAT directory." + PythonUtil.PYTHON_EXECUTABLE);			
		} else if(!(new File(PythonUtil.PYTHON_EXECUTABLE).exists())) {
			throw new RuntimeException("Before launching this program, you need to place the path to your "+
									   "Python executable in my_python_path.txt within the main MM-NEAT directory. The current contents of this file are incorrect: "+
									   PythonUtil.PYTHON_EXECUTABLE);
		}
	}

	private void setupArchiveVisualizer(BinLabels bins) throws FileNotFoundException {
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		String prefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber") + "_MAPElites";
		String fullName = directory + prefix + "_log.plt";
		checkPython();
		
		// Archive generator
		String[] dimensionNames = bins.dimensions();
		int[] dimensionSizes = bins.dimensionSizes();
		String archiveBatchName = directory + "GenerateArchiveImage.bat";
		String archiveAnimationBatchName = directory + "GenerateArchiveAnimation.bat";
		
		if (dimensionNames.length == 3 || dimensionNames.length == 2) {
			PrintStream ps = new PrintStream(new File(archiveBatchName));
			if (dimensionNames.length == 3) { // add min/max batch params
				ps.println("REM python 3DMAPElitesArchivePlotter.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <third dimension name> <third dimension size> <row amount> <max value> <min value>\r\n"
						+ "REM The min and max values are not required, and instead will be calculated automatically"); // add description
			} else {
				ps.println("REM python 2DMAPElitesArchivePlotter.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <max value> <min value>\r\n"
						+ "REM The min and max values are not required, and instead will be calculated automatically");
			}
			ps.println("cd ..");
			ps.println("cd ..");
			ps.print(PythonUtil.PYTHON_EXECUTABLE + " "+dimensionNames.length+"DMAPElitesArchivePlotter.py "+directory+fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt");
			for (int i = 0; i < dimensionNames.length; i++) {
				ps.print(" \""+dimensionNames[i]+"\" "+dimensionSizes[i]);
			}
			if (dimensionNames.length == 3) { // add min/max batch params
				ps.print(" 2 %1 %2"); // add row param if 3
			} else {
				ps.print(" %1 %2");
			}
			ps.close();
			
			ps = new PrintStream(new File(archiveAnimationBatchName));
			if (dimensionNames.length == 3) { // add min/max batch params
				ps.println("REM python 3DMAPElitesArchivePlotAnimator.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <third dimension name> <third dimension size> <row amount> <max value> <min value>\r\n"
						+ "REM The min and max values are not required, and instead will be calculated automatically"); // add description
			} else {
				ps.println("REM python 2DMAPElitesArchivePlottAnimator.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <max value> <min value>\r\n"
						+ "REM The min and max values are not required, and instead will be calculated automatically");
			}
			ps.println("cd ..");
			ps.println("cd ..");
			ps.print(PythonUtil.PYTHON_EXECUTABLE + " "+dimensionNames.length+"DMAPElitesArchivePlotAnimator.py "+directory+fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt");
			for (int i = 0; i < dimensionNames.length; i++) {
				ps.print(" \""+dimensionNames[i]+"\" "+dimensionSizes[i]);
			}
			if (dimensionNames.length == 3) { // add min/max batch params
				ps.print(" 2 1 %1 %2"); // add row param if 3
			} else {
				ps.print(" 1 %1 %2");
			}
			ps.close();
		}
	}
	
	/**
	 * Get the archive
	 * @return
	 */
	public Archive<T> getArchive() {
		return archive;
	}
	
	/**
	 * Fill the archive with a set number of random initial genotypes,
	 * according to where they best fit.
	 * @param example Starting genotype used to derive new instances
	 */
	@Override
	public void initialize(Genotype<T> example) {		
		saveImageArchives = MMNEAT.task instanceof PictureTargetTask;
		if(iterations > 0) {
			int numLabels = archive.getBinMapping().binLabels().size();
			// Loading from saved archive
			String archiveDir = archive.getArchiveDirectory();
			List<String> binLabels = archive.getBinMapping().binLabels();
			// Load each elite from xml file into archive
			for(int i = 0; i < binLabels.size(); i++) {
				String binDir = archiveDir + "/" + binLabels.get(i) + "/";
				@SuppressWarnings("unchecked")
				Genotype<T> elite = (Genotype<T>) Easy.load(binDir + "elite.xml"); // Load genotype
				// Load behavior scores
				ArrayList<Double> scores = new ArrayList<Double>(numLabels); 
				try {
					Scanner scoresFile = new Scanner(new File(binDir + "scores.txt"));
					while(scoresFile.hasNextDouble()) {
						scores.add(scoresFile.nextDouble());
					}
					scoresFile.close();
				} catch (FileNotFoundException e) {
					System.out.println("Could not read " + binDir + "scores.txt");
					e.printStackTrace();
					System.exit(1);
				}
				// Package in a score
				Score<T> score = new Score<T>(elite, new double[0], scores);
				archive.archive.set(i, score); // Directly set the bin contents
			}
		} else {
			System.out.println("Fill up initial archive");
			// Start from scratch
			int startSize = Parameters.parameters.integerParameter("mu");
			ArrayList<Genotype<T>> startingPopulation = PopulationUtil.initialPopulation(example, startSize);
			startingPopulation.parallelStream().forEach( (g) -> {
				Score<T> s = task.evaluate(g);
				archive.add(s); // Fill the archive with random starting individuals
			});
				
		}
	}

	/**
	 * Write one line of data to each of the active log files, but only periodically,
	 * when number of iterations divisible by individualsPerGeneration. 
	 */
	private void log() {
		if (!archiveFileCreated) {
			try {
				setupArchiveVisualizer(archive.getBinMapping());
			} catch (FileNotFoundException e) {
				System.out.println("Could not create archive visualization file.");
				e.printStackTrace();
				System.exit(1);
			}
			archiveFileCreated = true;
		}
		if(io && iterations % individualsPerGeneration == 0) {
			int numCPPN = 0;
			int numDirect = 0;
			// When all iterations were logged, the file got too large
			//log.log(iterations + "\t" + iterationsWithoutElite + "\t" + StringUtils.join(ArrayUtils.toObject(archive.getEliteScores()), "\t"));
			// Just log every "generation" instead
			Float[] elite = ArrayUtils.toObject(archive.getEliteScores());
			final int pseudoGeneration = iterations/individualsPerGeneration;
			archiveLog.log(pseudoGeneration + "\t" + StringUtils.join(elite, "\t").replaceAll("-Infinity", "X"));
			Float maximumFitness = StatisticsUtilities.maximum(elite);
			// Exclude negative infinity to find out how many bins are filled
			final int numFilledBins = elite.length - ArrayUtil.countOccurrences(Float.NEGATIVE_INFINITY, elite);
			// Get the QD Score for this elite
			final double qdScore = calculateQDScore(elite);
			fillLog.log(pseudoGeneration + "\t" + numFilledBins + "\t" + qdScore + "\t" + maximumFitness + "\t" + iterationsWithoutEliteCounter);
			if(cppnThenDirectLog!=null) {
				Integer[] eliteProper = new Integer[elite.length];
				int i = 0;
				Vector<Score<T>> population = archive.archive;
				for(Score<T> p : population) {
					if(p == null || p.individual == null) eliteProper[i] = NUM_CODE_EMPTY; //if bin is empty
					else if(((CPPNOrDirectToGANGenotype) p.individual).getFirstForm()) {
						numCPPN++;
						eliteProper[i] = NUM_CODE_CPPN; //number for CPPN
					} else { // Assume first form is false
						assert !((CPPNOrDirectToGANGenotype) p.individual).getFirstForm();
						numDirect++;
						eliteProper[i] = NUM_CODE_DIRECT; //number for Direct
					}
					i++;
				}
				//in archive class, archive variable (vector)
				cppnThenDirectLog.log(pseudoGeneration+"\t"+numCPPN+"\t"+numDirect);
				cppnVsDirectFitnessLog.log(pseudoGeneration +"\t"+ StringUtils.join(eliteProper, "\t"));
			}			
			// Special code for Lode Runner
			if(MMNEAT.task instanceof LodeRunnerLevelTask) {
				int numBeatenLevels = 0;
				for(Float x : elite) {
					// If A* fitness is used, then unbeatable levels have a score of -1 and thus won't be counted here.
					// If A*/Connectivity combo is used, then a connectivity percentage in (0,1) means the level is not beatable.
					// Score will only be greater than 1 if there is an actual A* path.
					if(x >= 1.0) {
						numBeatenLevels++;
					}
				}
				((LodeRunnerLevelTask<?>)MMNEAT.task).beatable.log(pseudoGeneration + "\t" + numBeatenLevels + "\t" + ((1.0*numBeatenLevels)/(1.0*numFilledBins)));
			}
			
			if (emitterMeanLog != null && MMNEAT.getArchiveBinLabelsClass() instanceof MultiDimensionalRealValuedSlicedBinLabels) { // TODO
				MultiDimensionalRealValuedSlicedBinLabels dimensionSlices = (MultiDimensionalRealValuedSlicedBinLabels) MMNEAT.getArchiveBinLabelsClass();
				String newLine = "" + pseudoGeneration;
				for (double[] mean : ((CMAME)this).getEmitterMeans()) {
					int[] binCoords = dimensionSlices.discretize(dimensionSlices.behaviorCharacterization(mean));	
					newLine += "\t";
					for (int i = 0; i < binCoords.length; i++) {
						if (i != 0) {
							newLine += " ";
						}
						newLine += binCoords[i];
					}
				}
				emitterMeanLog.log(newLine);
			}
		}
	}

	/**
	 * Calculates a QD score for an elite by summing the valid values (non negative
	 * infinity). Each value is offset by the parameter "mapElitesQDBaseOffset" 
	 * before being added to the sum.
	 * @param elite An elite represented by an Array of floats representing each value
	 * @return returns a double representing the QD score with offset values
	 */
	public static double calculateQDScore(Float[] elite) {
		double base = Parameters.parameters.doubleParameter("mapElitesQDBaseOffset");
		double sum = 0.0;
		for (float x : elite) {
			if (x != Float.NEGATIVE_INFINITY) {
				sum += base + x;
			}
		}
		return sum;
	}

	/**
	 * Create one (maybe two) new individuals by randomly
	 * sampling from the elites in random bins. The reason
	 * that two individuals may be added is if crossover occurs.
	 * In this case, both children can potentially be added 
	 * to the archive, and both trigger logging to file. This
	 * actually counts as 2 iterations.
	 */
	@Override
	public void newIndividual() {
		int index = archive.randomOccupiedBinIndex();
		Genotype<T> parent1 = archive.getElite(index).individual;
		long parentId1 = parent1.getId(); // Parent Id comes from original genome
		long parentId2 = NUM_CODE_EMPTY;
		Genotype<T> child1 = parent1.copy(); // Copy with different Id (will be further modified below)
		child1.addParent(parentId1);
		
		// Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
			int otherIndex = archive.randomOccupiedBinIndex(); // From a different bin
			Genotype<T> parent2 = archive.getElite(otherIndex).individual;
			parentId2 = parent2.getId(); // Parent Id comes from original genome
			Genotype<T> child2 = parent2.copy(); // Copy with different Id (further modified below)
			
			// Replace child2 with a crossover result, and modify child1 in the process (two new children)
			child2 = child1.crossover(child2);
			child2.mutate(); // Probabilistic mutation of child
			child2.addParent(parent2.getId());
			child2.addParent(parent1.getId());
			child1.addParent(parent2.getId());
			EvolutionaryHistory.logLineageData(parentId1,parentId2,child2);
			// Evaluate and add child to archive
			Score<T> s2 = task.evaluate(child2);
			// Indicate whether elite was added
			boolean child2WasElite = archive.add(s2);
			fileUpdates(child2WasElite); // Log for each individual produced
		}
		
		child1.mutate(); // Was potentially modified by crossover
		if (parentId2 == NUM_CODE_EMPTY) {
			EvolutionaryHistory.logLineageData(parentId1,child1);
		} else {
			EvolutionaryHistory.logLineageData(parentId1,parentId2,child1);
		}
		// Evaluate and add child to archive
		Score<T> s1 = task.evaluate(child1);
		// Indicate whether elite was added
		boolean child1WasElite = archive.add(s1);
		fileUpdates(child1WasElite); // Log for each individual produced
	}
	
	/**
	 * Log data and update other data tracking variables.
	 * @param newEliteProduced Whether the latest individual was good enough to
	 * 							fill/replace a bin.
	 */
	public void fileUpdates(boolean newEliteProduced) {
		if(saveImageArchives && iterations % Parameters.parameters.integerParameter("imageArchiveSaveFrequency") == 0) {
			System.out.println("Save whole archive at iteration "+iterations);
			// 28 is a magic number, and should be either a constant of a command line parameter.
			// Fix later ... this is the standard image size for training our simple image autoencoder (based on MNIST)
			((PictureTargetTask<?>) MMNEAT.task).saveAllArchiveImages("iteration"+iterations, 28, 28);

			// If we are using the autoencoder (only use if "trainingAutoEncoder" == true), re-train it here
			if(Parameters.parameters.booleanParameter("trainingAutoEncoder")) {
				if(AutoEncoderProcess.currentProcess != null) {
					// Stop autoencoder inference when it is time to train a new one
					AutoEncoderProcess.terminateAutoEncoderProcess(); 
				}
				String experimentDir = FileUtilities.getSaveDirectory()+File.separator+"snapshots";
				Parameters.parameters.setString("mostRecentAutoEncoder", experimentDir+File.separator+ "iteration" + iterations + ".pth");
				TrainAutoEncoderProcess training = new TrainAutoEncoderProcess(experimentDir+File.separator+"iteration" + iterations, Parameters.parameters.stringParameter("mostRecentAutoEncoder"));
				training.start();
				// Initialize process for newly trained autoencoder
				AutoEncoderProcess.getAutoEncoderProcess(); // (sort of optional to initialize here)
				AutoEncoderProcess.neverInitialized = false;
				// Now we need to dump the archive and replace it with a new one after re-evaluating all old contents.
				int oldOccupied = this.archive.getNumberOfOccupiedBins();
				this.archive = new Archive<T>(this.archive);
				int newOccupied = this.archive.getNumberOfOccupiedBins();
				System.out.println("Archive reorganized based on new AutoEncoder: Occupancy "+oldOccupied+" to "+newOccupied);
			} 
			
		}
		// Log to file
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
		
	}
	
	/**
	 * Number of times new individuals have been 
	 * generated to add to archive.
	 */
	@Override
	public int currentIteration() {
		return iterations;
	}

	@Override
	public void finalCleanup() {
		task.finalCleanup();
	}

	/**
	 * Take members from archive and place them in an ArrayList
	 */
	@Override
	public ArrayList<Genotype<T>> getPopulation() {
		ArrayList<Genotype<T>> result = new ArrayList<Genotype<T>>(archive.archive.size());
		for(Score<T> s : archive.archive) {
			if(s != null) { // Not all bins are filled
				result.add(s.individual);
			}
		}
		return result;
	}

	/**
	 * If iterationsWithoutElite is 0, then the last new individual
	 * was inserted into the population.
	 */
	@Override
	public boolean populationChanged() {
		return iterationsWithoutElite == 0;
	}
}
