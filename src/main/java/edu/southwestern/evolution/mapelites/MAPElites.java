package edu.southwestern.evolution.mapelites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import autoencoder.python.AutoEncoderProcess;
import autoencoder.python.TrainAutoEncoderProcess;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBinLabels;
import edu.southwestern.tasks.innovationengines.PictureTargetTask;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.tasks.loderunner.LodeRunnerLevelTask;
import edu.southwestern.util.MultiobjectiveUtil;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;


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
	private MMNEATLog autoencoderLossRange = null;
	protected MMNEATLog[] emitterIndividualsLogs = null;
	private MMNEATLog[] otherStatsLogs = null;	//logs the other stats 
	private MMNEATLog otherStatsFillLog = null;
	private MMNEATLog otherHypervolumeLog = null;
	// separate log and plot file for each index in otherStats
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
		return archive.getBinMapping();
	}
	
	public MAPElites() {
		this(Parameters.parameters.stringParameter("archiveSubDirectoryName"), Parameters.parameters.booleanParameter("io"), Parameters.parameters.booleanParameter("netio"), true);
	}
	
	/**
	 * TODO: JavaDoc
	 * @param archiveSubDirectoryName
	 * @param ioOption
	 * @param netioOption
	 * @param createLogs
	 */
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
			
			//logging other stats
			int numberOfOtherStats = MMNEAT.getNumberOtherStatsForPopulation(0);
			ArrayList<MMNEATLog> otherStatsLogsList = null;
			if(numberOfOtherStats > 0) {
				otherStatsLogsList = new ArrayList<>();
				otherStatsLogs = new MMNEATLog[numberOfOtherStats];			
				//map elites can only have one fitness function, so everything beyond that is an other stat
				for (int i = 0; i < numberOfOtherStats; i++) {
					otherStatsLogs[i] = new MMNEATLog(infix+"_otherStat_"+i+"_" +MMNEAT.getFitnessFunctionName(i+1), false, false, false, true);
					otherStatsLogsList.add(otherStatsLogs[i]);
				}
				otherStatsFillLog = new MMNEATLog(infix+"_otherStatsFillLog", false, false, false, true);
				if(Parameters.parameters.booleanParameter("mapElitesLogsOtherScoreHypervolume")) {
					otherHypervolumeLog = new MMNEATLog(infix+"_otherStatsHypervolumeLog", false, false, false, true);
				}
			}
			
			// Can't check MMNEAT.genotype since MMNEAT.ea is initialized before MMNEAT.genotype
			boolean cppnDirLogging = Parameters.parameters.classParameter("genotype").equals(CPPNOrDirectToGANGenotype.class) ||
									 Parameters.parameters.classParameter("genotype").equals(CPPNOrBlockVectorGenotype.class);
			if(cppnDirLogging) {
				cppnThenDirectLog = new MMNEATLog("cppnToDirect", false, false, false, true);
				cppnVsDirectFitnessLog = new MMNEATLog("cppnVsDirectFitness", false, false, false, true);
			}
			// Create gnuplot file for archive log
			String experimentPrefix = Parameters.parameters.stringParameter("log")
					+ Parameters.parameters.integerParameter("runNumber");
			individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
			int yrange = Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration;
			setUpLogging(numLabels, infix, experimentPrefix, yrange, cppnDirLogging, individualsPerGeneration, otherStatsLogsList);
		}
		this.mating = Parameters.parameters.booleanParameter("mating");
		this.crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		this.iterations = Parameters.parameters.integerParameter("lastSavedGeneration");
		this.iterationsWithoutEliteCounter = 0;
		this.iterationsWithoutElite = 0; // Not accurate on resume		
	}

	/**
	 * TODO: JavaDoc
	 * This seems to be all the set up necessary for the GNU Plot related files
	 * @param numLabels
	 * @param infix
	 * @param experimentPrefix
	 * @param yrange
	 * @param cppnDirLogging
	 * @param individualsPerGeneration
	 * @param archiveSize
	 */
	public static void setUpLogging(int numLabels, String infix, String experimentPrefix, int yrange, boolean cppnDirLogging, int individualsPerGeneration, ArrayList<MMNEATLog> otherStatsLogsList) {
		
		String prefix = experimentPrefix + "_" + infix;
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
			ps.println("set xrange [1:"+ numLabels + "]");
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
			ps.println("set xrange [1:"+ numLabels + "]");
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

			//creating other stat logs
			//separate log and plot file for each index in otherStats
			int numberOfOtherStats = MMNEAT.getNumberOtherStatsForPopulation(0);
			
//			int otherStatIndex = 1;
			if (otherStatsLogsList != null) {
				for (MMNEATLog log : otherStatsLogsList) {
					String textLogFilename = log.getLogTextFilename();
					String plotFilename = textLogFilename.replace(".txt", ".plt");
					String plotPDFFilename = plotFilename.replace(".plt", "_PDF.plt");
					String logTitle = textLogFilename.replace(".txt", "");
					String pdfFilename = textLogFilename.replace(".txt", ".pdf");

					File plotFile = new File(directory + plotFilename);
					File plotPDFFile = new File(directory + plotPDFFilename);

					//log and plot each other stat
					// The PDF version
					ps = new PrintStream(plotPDFFile);
					ps.println("set term pdf enhanced");
					ps.println("unset key");
					// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
					ps.println("set yrange [0:"+ yrange +"]");
					ps.println("set xrange [1:"+ numLabels + "]");
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
					ps.println("set xrange [1:"+ numLabels + "]");
					ps.println("set title \"" + logTitle.replace("_", " ") + "\"");
					// The :1 is for skipping the "generation" number logged in the file
					ps.println("plot \"" + textLogFilename + "\" matrix every ::1 with image");
					// ps.println("pause -1"); // Not needed when only one item is plotted?
					ps.close();

				}
				
				if(Parameters.parameters.booleanParameter("mapElitesLogsOtherScoreHypervolume")) {
					String textHVLogFilename = experimentPrefix + "_" + infix + "_otherStatsHypervolumeLog_log.txt";
					String plotHVFilename = textHVLogFilename.replace(".txt", ".plt");

					File plotFileHV = new File(directory + plotHVFilename);
					ps = new PrintStream(plotFileHV);
					//ps.println("set term pdf enhanced");
					ps.println("set key bottom right");
					// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
					ps.println("set xrange [0:"+ yrange +"]");
					ps.println("set title \"" + experimentPrefix + " Hypervolume\"");
					//ps.println("set output \"" + experimentPrefix + "_otherStatsHypervolumeLog_log.pdf\"");
					ps.println("plot \"" + textHVLogFilename + "\" u 1:2 w linespoints t \"Hypervolume\"");
					ps.close();
				}
				
				//////////////////////////////////////
				
				String textLogFilename = experimentPrefix + "_" + infix + "_otherStatsFillLog_log.txt";
				String plotFilename = textLogFilename.replace(".txt", ".plt");
				
				File plotFile = new File(directory + plotFilename);
				
				ps = new PrintStream(plotFile);
				ps.println("set term pdf enhanced");
				//ps.println("unset key");
				ps.println("set key bottom right");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set xrange [0:"+ yrange +"]");
						
				// Why is this magic number 2?
				int index = 2;
				for (int i = 0; i < numberOfOtherStats; i++) {
					ps.println("set title \"" + experimentPrefix + " " + MMNEAT.getFitnessFunctionName(i+1) + " Max Fitness\"");
					ps.println("set output \"" + experimentPrefix + "_otherStatsFillLog_"+ MMNEAT.getFitnessFunctionName(i+1) + "_MaxFitness.pdf\"");
					ps.println("plot \"" + textLogFilename + "\" u 1:"+ (i+index) +" w linespoints t \"Max Fitness\"");

					index++;
		
					ps.println("set title \"" + experimentPrefix + " " + MMNEAT.getFitnessFunctionName(i+1) + " QD Score\"");
					ps.println("set output \"" + experimentPrefix + "_otherStatsFillLog_"+ MMNEAT.getFitnessFunctionName(i+1) + "_QDScore.pdf\"");
					ps.println("plot \"" + textLogFilename + "\" u 1:"+ (i+index) +" w linespoints t \"QD Score\"");
//					index++;
				}
			}
			

			ps.close();

		} catch (FileNotFoundException e) {
			System.out.println("Could not create plot file: " + plot.getName());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * TODO: JavaDoc
	 * @param bins
	 * @throws FileNotFoundException
	 */
	private void setupArchiveVisualizer(BinLabels bins) throws FileNotFoundException {
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		directory += (directory.equals("") ? "" : "/");
		String prefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber") + "_MAPElites";
		String fullName = directory + prefix + "_log.plt";
		PythonUtil.setPythonProgram();
		PythonUtil.checkPython();
		
		// Archive generator
		String[] dimensionNames = bins.dimensions();
		int[] dimensionSizes = bins.dimensionSizes();
		String archiveBatchName = directory + "GenerateArchiveImage.bat";
		String restrictedArchiveBatchName = directory + "GenerateRestrictedArchiveImage.bat";
		String archiveAnimationBatchName = directory + "GenerateArchiveAnimation.bat";
		
		if (dimensionNames.length == 3 || dimensionNames.length == 2) {
			PrintStream restricted = new PrintStream(new File(restrictedArchiveBatchName));
			PrintStream ps = new PrintStream(new File(archiveBatchName));
			if (dimensionNames.length == 3) { // add min/max batch params
				ps.println("REM python 3DMAPElitesArchivePlotter.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <third dimension name> <third dimension size> <row amount> <max value> <min value>\r\n"
						+ "REM The min and max values are not required, and instead will be calculated automatically"); // add description
			} else {
				ps.println("REM python 2DMAPElitesArchivePlotter.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <max value> <min value>\r\n"
						+ "REM The min and max values are not required, and instead will be calculated automatically");
			}
			// Print file that puts bounds around all archive points
			writeScriptLauncher(directory, prefix, fullName, dimensionNames, dimensionSizes, ps, " %1 %2 %3 %4 %5 %6 %7 %8");
			// Print file that puts bounds around restricted range
			writeScriptLauncher(directory, prefix, fullName, dimensionNames, dimensionSizes, restricted, " max min "+bins.lowerRestrictedBounds()+" "+bins.upperRestrictedBounds()+" Restricted");
			
			restricted.close();
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
			ps.print(" \""+prefix+"\"");
			for (int i = 0; i < dimensionNames.length; i++) {
				ps.print(" \""+dimensionNames[i]+"\" "+dimensionSizes[i]);
			}
			if (dimensionNames.length == 3) { // add min/max batch params
				ps.print(" 2 %1 %2 %3"); // add row param if 3
			} else {
				ps.print(" %1 %2 %3 %4");
			}
			ps.close();
		}
	}

	/**
	 * TODO: JavaDoc
	 * I think this creates the .bat file?
	 * @param directory
	 * @param prefix
	 * @param fullName
	 * @param dimensionNames
	 * @param dimensionSizes
	 * @param ps
	 * @param finalLine
	 */
	private void writeScriptLauncher(String directory, String prefix, String fullName, String[] dimensionNames,
			int[] dimensionSizes, PrintStream ps, String finalLine) {
		ps.println("cd ..");
		ps.println("cd ..");
		ps.print(PythonUtil.PYTHON_EXECUTABLE + " "+dimensionNames.length+"DMAPElitesArchivePlotter.py "+directory+fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt");
		ps.print(" \""+prefix+"\"");
		for (int i = 0; i < dimensionNames.length; i++) {
			ps.print(" \""+dimensionNames[i]+"\" "+dimensionSizes[i]);
		}
		if (dimensionNames.length == 3) { // add min/max batch params
			ps.print(" 2"); // add row param if 3
		} 
		// add min/max batch params and bounds for binning red box (designed for 3D. Excess parameters for 2D)
		ps.print(finalLine);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(Genotype<T> example) {	
		
		if(MMNEAT.task.numObjectives() > 1) {
			throw new IllegalStateException("MAP Elites is not equipped to handle multiple fitness functions: "+MMNEAT.fitnessFunctions.get(0)+
						". If you want multiple fitness functions, consider using MOME or NSGA2.");
		} else if(MMNEAT.task.numObjectives() == 0) {
			throw new IllegalStateException("MAP Elites still needs an objective/fitness to work.");
		}
		
		if (this instanceof CMAME && MMNEAT.genotype instanceof RealValuedGenotype) {
			emitterMeanLog = new MMNEATLog("EmitterMeans", false, false, false, true);
		}
		saveImageArchives = MMNEAT.task instanceof PictureTargetTask;
		ArrayList<Genotype<T>> startingPopulation; // Will be new or from saved archive
		
		// Do not discard individuals outside restricted range during initialization, since we
		// may end up with an empty archive in this case.
		boolean originalSetting = Parameters.parameters.booleanParameter("discardFromBinOutsideRestrictedRange");
		if(Parameters.parameters.booleanParameter("turnOffRestrictionsDuringInit")) {
			System.out.println("Do not discard any elites during initialization");
			Parameters.parameters.setBoolean("discardFromBinOutsideRestrictedRange", false);
		}	
		
		// logLock generally means we are doing a post experiment evaluation rather than attempting
		// to resume an experiment, so loading results is ok if logLock is true
		if(!Parameters.parameters.booleanParameter("logLock") && iterations > 0) {
			
			System.out.println("It is not safe to resume MAP-Elites runs that crash.");
			System.out.println("Because logging is periodic, the state of the archive may not reflect the state of the logs.");
			System.exit(1);
			
			startingPopulation = null;
			
//			startingPopulation = new ArrayList<>();
//			//int numLabels = archive.getBinMapping().binLabels().size();
//			// Loading from saved archive
//			String archiveDir = archive.getArchiveDirectory();
//			List<String> binLabels = archive.getBinMapping().binLabels();
//			Serialization.debug = false; // Don't print stack trace for missing files
//			// Load each elite from xml file into archive
//			for(int i = 0; i < binLabels.size(); i++) {
//				String binPrefix = archiveDir + "/" + binLabels.get(i) + "-";
//				Genotype<T> elite = (Genotype<T>) Serialization.load(binPrefix + "elite"); // Load genotype
//				//double binScore = Double.NEGATIVE_INFINITY; // The one bin score
//				if(elite != null) { // File actually exists
//					startingPopulation.add(elite);
//				}
//			}
//			Serialization.debug = true; // Restore stack traces
		} else {
			System.out.println("Fill up initial archive");		
			// Start from scratch
			int startSize = Parameters.parameters.integerParameter("mu");
			startingPopulation = PopulationUtil.initialPopulation(example, startSize);			
			
			assert startingPopulation.size() == 0 || !(startingPopulation.get(0) instanceof BoundedRealValuedGenotype) || ((BoundedRealValuedGenotype) startingPopulation.get(0)).isBounded() : "Initial individual not bounded: "+startingPopulation.get(0);
		}
		
		// Next code executes for fresh starts and resumes/loads
		// Even for resume, re-evaluate the loaded genotypes
		Vector<Score<T>> evaluatedPopulation = new Vector<>(startingPopulation.size());
		
		boolean backupNetIO = CommonConstants.netio;
		CommonConstants.netio = false; // Some tasks require archive comparison to do this, but it does not exist yet.
		Stream<Genotype<T>> evaluateStream = Parameters.parameters.booleanParameter("parallelMAPElitesInitialize") ? 
												startingPopulation.parallelStream() :
												startingPopulation.stream();
		if(Parameters.parameters.booleanParameter("parallelMAPElitesInitialize"))
			System.out.println("Evaluate archive in parallel");
		// Evaluate initial population
		evaluateStream.forEach( (g) -> {
			Score<T> s = task.evaluate(g);
			evaluatedPopulation.add(s);
		});
		CommonConstants.netio = backupNetIO;	
		
		if(Parameters.parameters.booleanParameter("dynamicAutoencoderIntervals")) {					
			autoencoderLossRange = new MMNEATLog("autoencoderLossRange", false, false, false, true);
		}

		// Special code if image auto-encoder is used
		if(Parameters.parameters.booleanParameter("trainInitialAutoEncoder") && saveImageArchives && Parameters.parameters.booleanParameter("trainingAutoEncoder")) {
			System.out.println("Train initial auto-encoder");
			((PictureTargetTask) MMNEAT.task).saveAllArchiveImages("starting", AutoEncoderProcess.SIDE_LENGTH, AutoEncoderProcess.SIDE_LENGTH, evaluatedPopulation);
			String experimentDir = FileUtilities.getSaveDirectory()+File.separator+"snapshots";
			Parameters.parameters.setString("mostRecentAutoEncoder", experimentDir+File.separator+ "starting.pth");
			String outputAutoEncoderFile = Parameters.parameters.stringParameter("mostRecentAutoEncoder");
			String trainingDataDirectory = experimentDir+File.separator+"starting";

			// This adds the population to the archive after training the auto-encoder
			trainImageAutoEncoderAndSetLossBounds(outputAutoEncoderFile, trainingDataDirectory, evaluatedPopulation);
			System.out.println("Initial occupancy: "+ this.archive.getNumberOfOccupiedBins());
		} else {
			MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"),Parameters.parameters.integerParameter("minecraftYRange"),Parameters.parameters.integerParameter("minecraftZRange"));
			boolean minecraftInit = archive.getBinMapping() instanceof MinecraftMAPElitesBinLabels && Parameters.parameters.booleanParameter("minecraftContainsWholeMAPElitesArchive");
			if(minecraftInit) { //then clear world
				// Initializes the population size and ranges for clearing
				int pop_size = Parameters.parameters.integerParameter("mu");
				MinecraftClient.getMinecraftClient().clearSpaceForShapes(new MinecraftCoordinates(0,MinecraftClient.GROUND_LEVEL+1,0), ranges, pop_size, Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));
				// Place fences around all areas where a shape from the archive could be placed
				System.out.println("Area cleared, placing fences...");
				MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
				int dim1D = 0;
				for(int[] multiIndices : minecraftBinLabels) {
					//System.out.println(Arrays.toString(multiIndices));
					Pair<MinecraftCoordinates, MinecraftCoordinates> corners = MinecraftLonerShapeTask.configureStartPosition(ranges, multiIndices, dim1D++);
					// Only place on ground
					if(corners.t1.y() == MinecraftClient.GROUND_LEVEL+1) {
						//System.out.println("YES GROUND");
						MinecraftLonerShapeTask.placeFencesAroundArchive(ranges,corners.t2);
					}
				}

				System.out.println("Fences placed");	
				MinecraftLonerShapeTask.spawnShapesInWorldTrue();
			}

			// Add initial population to archive, if add is true
			evaluatedPopulation.parallelStream().forEach( (s) -> {
				boolean result = archive.add(s); // Fill the archive with random starting individuals, only when this flag is true

				// Minecraft shapes have to be re-generated and added to the world
				synchronized(archive) {
					if(minecraftInit && result) {
						//System.out.println("Put "+s.individual.getId()+":"+s.MAPElitesBehaviorMap());
						int index1D = (int) s.MAPElitesBehaviorMap().get("dim1D");
						double scoreOfCurrentElite = s.behaviorIndexScore();
						MinecraftLonerShapeTask.clearAndSpawnShape(s.individual, s.MAPElitesBehaviorMap(), ranges, index1D, scoreOfCurrentElite);
					}
				}
			});
			//long endTime = System.currentTimeMillis();
			//System.out.println("TIME TAKEN:" + (endTime - startTime));
				
		}
			
		if(Parameters.parameters.booleanParameter("turnOffRestrictionsDuringInit")) {
			System.out.println("Return to discarding elites in restricted range");
			Parameters.parameters.setBoolean("discardFromBinOutsideRestrictedRange", originalSetting);
		}
	}

	/**
	 * Write one line of data to each of the active log files, but only periodically,
	 * when number of iterations divisible by individualsPerGeneration. 
	 */
	@SuppressWarnings("unchecked")
	protected void log() {
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
		if(io && iterations % individualsPerGeneration == 0) {
			int numCPPN = 0;
			int numDirect = 0;	//cppn related variable
			// When all iterations were logged, the file got too large
			//log.log(iterations + "\t" + iterationsWithoutElite + "\t" + StringUtils.join(ArrayUtils.toObject(archive.getEliteScores()), "\t"));
			// Just log every "generation" instead
			Float[] elite = ArrayUtils.toObject(archive.getEliteScores());
			final int pseudoGeneration = iterations/individualsPerGeneration;
			archiveLog.log(pseudoGeneration + "\t" + StringUtils.join(elite, "\t").replaceAll("-Infinity", "X"));
			// Small amount added to fitness scores to tweak the QD calculation
			double offsetSoThatOccupiedBinsWithMinFitnessAreBetterThanEmptyBins = Parameters.parameters.doubleParameter("mapElitesQDBaseOffset");
			//log otherStats
			int numberOfOtherStats = MMNEAT.getNumberOtherStatsForPopulation(0);
			if(numberOfOtherStats > 0) {
				String otherStatsFillString = pseudoGeneration + "\t";
				for (int i = 0; i < numberOfOtherStats; i++) {
					Float[] otherStats = ArrayUtils.toObject(archive.getOtherStatsScores(i));
					otherStatsLogs[i].log(pseudoGeneration + "\t" + StringUtils.join(otherStats, "\t").replaceAll("-Infinity", "X"));
					Float maximumFitness = StatisticsUtilities.maximum(otherStats);

					// It is assumed that each other stat used with MAP Elites is a component from a weighted sum.
					// However, some component fitnesses have negative minimum values. For proper QD calculation,
					// the score range has to be shifted up. In addition, the offsetSoThatOccupiedBinsWithMinFitnessAreBetterThanEmptyBins
					// is used so that even a minimal score is worth more than an empty bin. We add 1 to the index since we
					// skip over the actual fitness function (just one) and only get other stats.
					double minFitness = MMNEAT.fitnessFunctionMinScore(1 + i);
					// minFitness is subtracted since this base value will be added to the actual fitness.
					// for example, a negative min leads to a positive offset. Either way, want to zero out the min, except for offset.
					final double qdScore = calculateQDScore(otherStats, offsetSoThatOccupiedBinsWithMinFitnessAreBetterThanEmptyBins - minFitness);
					otherStatsFillString = otherStatsFillString + maximumFitness + "\t" + qdScore +"\t";
				}
				otherStatsFillLog.log(otherStatsFillString);
				if(Parameters.parameters.booleanParameter("mapElitesLogsOtherScoreHypervolume")) {
					Pair<Double, List<Score<T>>> volumeAndFront = archive.getHypervolumeAndParetoFrontAcrossOtherStats();
					otherHypervolumeLog.log(pseudoGeneration + "\t" + volumeAndFront.t1);				
					MultiobjectiveUtil.logParetoFrontGenotypesAndScorePlot("PseudoGen"+pseudoGeneration+"_ParetoFront", volumeAndFront.t2, null);
				}
			}
			
			Float maximumFitness = StatisticsUtilities.maximum(elite);
			// Exclude negative infinity to find out how many bins are filled
			final int numFilledBins = elite.length - ArrayUtil.countOccurrences(Float.NEGATIVE_INFINITY, elite);
			// Get the QD Score for this elite
			final double qdScore = calculateQDScore(elite);
			
			BinLabels labels = MMNEAT.getArchiveBinLabelsClass();
			Stream<Score<T>> stream = archive.archive.parallelStream().filter(s -> s != null && !labels.isOutsideRestrictedRange(s.MAPElitesBinIndex()));
			double[] restrictedScores = stream.mapToDouble(s -> s.behaviorIndexScore()).toArray();
			final int restrictedFilled = restrictedScores.length;
			final double restrictedQD = calculateQDScore(ArrayUtil.doubleArrayToFloatArray(restrictedScores));
			final double restrictedMaxFitness = restrictedScores.length == 0 ? 0 : StatisticsUtilities.maximum(restrictedScores);
			
			fillLog.log(pseudoGeneration + "\t" + numFilledBins   + "\t" + qdScore    + "\t" + maximumFitness + "\t" + iterationsWithoutEliteCounter + 
					                       "\t" + restrictedFilled+ "\t" +restrictedQD+ "\t" +restrictedMaxFitness);
			if(cppnThenDirectLog!=null) {
				Integer[] eliteProper = new Integer[elite.length];
				int i = 0;
				Vector<Score<T>> population = archive.archive;
				for(Score<T> p : population) {
					if(p == null || p.individual == null) eliteProper[i] = NUM_CODE_EMPTY; //if bin is empty
					else if( (p.individual instanceof CPPNOrDirectToGANGenotype && ((CPPNOrDirectToGANGenotype) p.individual).getFirstForm()) || 
							 (p.individual instanceof CPPNOrBlockVectorGenotype && ((CPPNOrBlockVectorGenotype) p.individual).getFirstForm()) ) {
						numCPPN++;
						eliteProper[i] = NUM_CODE_CPPN; //number for CPPN
					} else { // Assume first form is false
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
			
			if (emitterMeanLog != null) { 
				boolean backupNetIO = CommonConstants.netio;
				CommonConstants.netio = false; // Don't want to touch the archive when evaluating means
				
				BinLabels dimensionSlices = MMNEAT.getArchiveBinLabelsClass();
				String newLine = "" + pseudoGeneration;
				for (double[] mean : ((CMAME)this).getEmitterMeans()) { 
					
					Score<T> s = task.evaluate((Genotype<T>) new RealValuedGenotype(mean));
					int[] binCoords = dimensionSlices.multiDimensionalIndices(s.MAPElitesBehaviorMap());	
					newLine += "\t";
					for (int i = 0; i < binCoords.length; i++) {
						if (i != 0) {
							newLine += " ";
						}
						newLine += binCoords[i];
					}
				}
				emitterMeanLog.log(newLine);
				
				CommonConstants.netio = backupNetIO;
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
	public static double calculateQDScore(Float[] elites) {
		return calculateQDScore(elites,Parameters.parameters.doubleParameter("mapElitesQDBaseOffset")); 
	}
	
	/**
	 * Like the above, but allows for a specified offset to the min score.
	 * @param elite Array of elite scores from each bin, where empty bins have a score of negative infinity
	 * @param base 
	 * @return
	 */
	public static double calculateQDScore(Float[] elite, double base) {
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
		newIndividual(index);
	}

	/**
	 * Generate a new individual (possibly two) that have a specific individual
	 * as one of the parents. Individual is designated by its index in the 1D archive.
	 * 
	 * @param parentIndex
	 */
	@SuppressWarnings("rawtypes")
	public void newIndividual(int parentIndex) {
		assert archive.getElite(parentIndex) != null : parentIndex + " in " + archive;
		assert archive.getArchive().stream().filter(s -> s != null).count() == archive.getNumberOfOccupiedBins() : archive.getNumberOfOccupiedBins()+" supposedly occupied, but "+
				"Archive "+ Arrays.toString(archive.getArchive().stream().map(s -> s == null ? "X" : ((Score) s).behaviorIndexScore() ).toArray());
		
		Genotype<T> parent1 = archive.getElite(parentIndex).individual;
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
			child2.addParent(parentId2);
			child2.addParent(parentId1);
			child1.addParent(parentId2);
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
		//System.out.println("====================================================");
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void fileUpdates(boolean newEliteProduced) {
		if(saveImageArchives && iterations % Parameters.parameters.integerParameter("imageArchiveSaveFrequency") == 0) {
			System.out.println("Save whole archive at iteration "+iterations);
			((PictureTargetTask) MMNEAT.task).saveAllArchiveImages("iteration"+iterations, AutoEncoderProcess.SIDE_LENGTH, AutoEncoderProcess.SIDE_LENGTH, archive.getArchive());
			
			if(Parameters.parameters.booleanParameter("deleteOldArchives") && iterations != 0) {
				String snapshot = FileUtilities.getSaveDirectory() + File.separator + "snapshots";
				String toDelete = snapshot + File.separator + Parameters.parameters.stringParameter("latestIterationSaved");
				File dir = new File(toDelete);
				boolean result = FileUtilities.deleteDirectory(dir);
				System.out.println("Deleted "+toDelete+": " + result);
				
				if(Parameters.parameters.booleanParameter("trainingAutoEncoder")) {
					toDelete = toDelete + ".pth";
					File pth = new File(toDelete);
					pth.delete();
					System.out.println("Deleted "+toDelete);
				}
			}
			Parameters.parameters.setString("latestIterationSaved", "iteration" + iterations);
			// If we are using the autoencoder (only use if "trainingAutoEncoder" == true), re-train it here
			if(Parameters.parameters.booleanParameter("trainingAutoEncoder")) {
				String experimentDir = FileUtilities.getSaveDirectory()+File.separator+"snapshots";
				Parameters.parameters.setString("mostRecentAutoEncoder", experimentDir+File.separator+ "iteration" + iterations + ".pth");
				String outputAutoEncoderFile = Parameters.parameters.stringParameter("mostRecentAutoEncoder");
				String trainingDataDirectory = experimentDir+File.separator+"iteration" + iterations;
				
				int oldOccupied = this.archive.getNumberOfOccupiedBins();
				trainImageAutoEncoderAndSetLossBounds(outputAutoEncoderFile, trainingDataDirectory, archive.getArchive());
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
	 * Trains autoencoder using specified directory for source input and specified output file (pth extension).
	 * Also, a collection of preexisting images is (optionally) used to determine bounds on the possible loss values.
	 * 
	 * @param outputAutoEncoderFile Full path for pth file to save
	 * @param trainingDataDirectory Directory full of 28 by 28 images to train autoencoder on
	 * @param previousImages Collection of Scores for CPPNs that generate images to calculate loss for after training
	 */
	private void trainImageAutoEncoderAndSetLossBounds(String outputAutoEncoderFile, String trainingDataDirectory, Vector<Score<T>> previousImages) {
		if(AutoEncoderProcess.currentProcess != null) {
			// Stop autoencoder inference when it is time to train a new one
			AutoEncoderProcess.terminateAutoEncoderProcess(); 
		}
		TrainAutoEncoderProcess training = new TrainAutoEncoderProcess(trainingDataDirectory, outputAutoEncoderFile);
		training.start();
		// Initialize process for newly trained autoencoder
		AutoEncoderProcess.getAutoEncoderProcess(); // (sort of optional to initialize here)
		AutoEncoderProcess.neverInitialized = false;
		// Now we need to dump the archive and replace it with a new one after re-evaluating all old contents.
		if(Parameters.parameters.booleanParameter("dynamicAutoencoderIntervals")) {					
			double minLoss = 1.0;
			double maxLoss = 0.0;
			// TODO: This can be made parallel with a stream, but the local vars for min and max need some special handling
			for(Score<T> s : previousImages) {
				if(s != null) { // Ignore empty cells
					Network cppn = (Network) s.individual.getPhenotype();
					BufferedImage image = PicbreederTask.imageFromCPPN(cppn, PictureTargetTask.imageWidth, PictureTargetTask.imageHeight, ArrayUtil.doubleOnes(cppn.numInputs()));
					double loss = AutoEncoderProcess.getReconstructionLoss(image);
					minLoss = Math.min(loss, minLoss);
					maxLoss = Math.max(loss, maxLoss);
				}
			}
			Parameters.parameters.setDouble("minAutoencoderLoss", minLoss);
			Parameters.parameters.setDouble("maxAutoencoderLoss", maxLoss);	
			if(autoencoderLossRange != null) {
				final int pseudoGeneration = iterations/individualsPerGeneration;
				autoencoderLossRange.log(pseudoGeneration + "\t" + minLoss + "\t" + maxLoss);
			}
			System.out.println("Loss ranges from "+minLoss+" to "+maxLoss);
		}		
		// Will bin differently because autoencoder has changed, as have expected loss bounds. Images get re-evaluated
		this.archive = new Archive<T>(previousImages, this.archive.getBinMapping(), this.archive.getArchiveDirectory(), CommonConstants.netio); 
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
