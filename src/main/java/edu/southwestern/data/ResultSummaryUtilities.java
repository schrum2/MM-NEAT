package edu.southwestern.data;

import edu.southwestern.log.PerformanceLog;
import edu.southwestern.log.TWEANNLog;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.GroupTask;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.MultiplePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.stats.Statistic;
import edu.southwestern.util.stats.StatisticsUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import jmetal.qualityIndicator.Hypervolume;

/**
 * After several runs of an experiment have been executed, this class can be
 * used to process all the data across runs and combine it into a useful summary
 * form. These methods are launched by executing MM-NEAT with "process" as the
 * first command line parameter.
 *
 * @author Jacob Schrum
 */
public class ResultSummaryUtilities {

	/**
	 * An experiment for postProcess
	 * Takes the average of the scores of all experiments that were run 
	 * If there are multiple populations, takes the average of scores for each population
	 * across all of the experiments
	 * @param dirPrefix, Directory Prefix
	 * @param filePrefix, File Prefix
	 * @param runs, number of runs to process
	 * @param generations, number of generations
	 * @param logSuffix, log suffix
	 * @param genFileMiddle, generation file middle name
	 * @param outputDir, output directory
	 * @param popNum, current population
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void processExperiment(String dirPrefix, String filePrefix, int runs, int generations,
			String logSuffix, String genFileMiddle, String outputDir, int popNum)
					throws FileNotFoundException, NoSuchMethodException {
		//HyperVolumeProcessing only happens with population zero because it would not work correctly with various
		//fitnesses for each population (coevolution). 
		if(popNum == 0 && Parameters.parameters.booleanParameter("processHV")){
			hypervolumeProcessing(dirPrefix, runs, filePrefix, genFileMiddle, generations, outputDir);
		}
		// Average objective scores
		System.out.println("Average scores: " + outputDir + "/" + filePrefix + "AVG" + logSuffix);
		int num = averageConditionResults(dirPrefix, filePrefix, logSuffix, runs, outputDir);
		plotAverageFitnessesFile(filePrefix, genFileMiddle + "Scores", logSuffix, num, runs, outputDir, StatisticsUtilities.tValue(runs), popNum);
		// TWEANN Info
		String loadFrom = Parameters.parameters.stringParameter("loadFrom");
		if ((loadFrom == null || loadFrom.equals("")) && !(MMNEAT.task instanceof MultiplePopulationTask)) {
			if (Parameters.parameters.booleanParameter("logTWEANNData")) {
				System.out.println("TWEANN Info: " + outputDir + "/" + filePrefix + "AVG" + logSuffix);
				int inputNum = averageConditionResults(dirPrefix, filePrefix, "_TWEANNData_log.txt", runs, outputDir);
				plotInfoFile(filePrefix, "_TWEANNData_log", inputNum, outputDir, TWEANNLog.getLabels());
			}
			if (Parameters.parameters.booleanParameter("logPerformance")) {
				System.out.println("Performance Info: " + outputDir + "/" + filePrefix + "AVG" + logSuffix);
				int inputNum = averageConditionResults(dirPrefix, filePrefix, "_Performance_log.txt", runs, outputDir);
				plotInfoFile(filePrefix, "_Performance_log", inputNum, outputDir, PerformanceLog.getLabels());
			}
		}
	}

	/**
	 * Calculates the hypervolumes and averages them, calls the methods to plot them as well
	 * @param dirPrefix, Directory Prefix
	 * @param runs, number of runs to process
	 * @param filePrefix, File Prefix
	 * @param genFileMiddle, generation file middle name
	 * @param generations, number of generations
	 * @param outputDir, output directory
	 * @throws FileNotFoundException
	 */
	public static void hypervolumeProcessing(String dirPrefix, int runs, String filePrefix, String genFileMiddle,
			int generations, String outputDir) throws FileNotFoundException {
		System.out.println("Calculate hypervolumes: " + dirPrefix + "X/" + filePrefix + "X" + genFileMiddle + "HV.txt");
		hypervolumesByGenerationForAllRuns(dirPrefix, runs, filePrefix, genFileMiddle, "txt", generations);
		// Average hypervolumes
		System.out.println("Average hypervolumes: " + outputDir + "/" + filePrefix + "AVG" + genFileMiddle + "HV.txt");
		averageConditionResults(dirPrefix, filePrefix, genFileMiddle + "HV.txt", runs, outputDir);
		plotHypervolumesFile(filePrefix, genFileMiddle + "HV", runs, outputDir);
	}

	/**
	 * Plots a file with given information 
	 * @param filePrefix, File Prefix
	 * @param middle, the middle of the file name
	 * @param num, number of columns in file
	 * @param outputDir, output directory
	 * @param labels, labels
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	private static void plotInfoFile(String filePrefix, String middle, int num, String outputDir, 
			ArrayList<String> labels) throws FileNotFoundException, NoSuchMethodException {
		plotInfoFile(filePrefix, middle, num, outputDir, labels, false);
		plotInfoFile(filePrefix, middle, num, outputDir, labels, true);
	}

	/**
	 * Plots a file with given information 
	 * @param filePrefix, File Prefix
	 * @param middle, the middle of the file name
	 * @param num, number of columns in file
	 * @param outputDir, output directory
	 * @param labels, labels
	 * @param makePDF, true if making a pdf, false if not 
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	private static void plotInfoFile(String filePrefix, String middle, int num, String outputDir,
			ArrayList<String> labels, boolean makePDF) throws FileNotFoundException, NoSuchMethodException {
		String plotFile = "";
		if(makePDF){
			plotFile = outputDir + "/" + filePrefix + "AVG" + middle + "PDF.plt";
		}else{
			plotFile = outputDir + "/" + filePrefix + "AVG" + middle + ".plt";
		}
		PrintStream out = new PrintStream(new FileOutputStream(plotFile));
		String condition = Parameters.parameters.stringParameter("saveTo");
		if(makePDF){
			out.println("set terminal pdf color");
		}
		out.println("set style data lines");
		out.println("set key left");

		String file = filePrefix + "AVG" + middle + ".txt";

		for (int i = 1; i < num; i += 4) {
			if(makePDF){
				out.println("set output \"Average " + labels.get(((i - 1) / 4)) + " for " + condition + " by Generation.pdf\"");
			}
			out.println("set title \"Average " + labels.get(((i - 1) / 4)) + " for " + condition + " by Generation\"");
			out.println("plot \\");
			out.println("\"" + file + "\" u 1:" + ((2 * i) + 1) + " t \"MIN\", \\");
			out.println("\"" + file + "\" u 1:" + ((2 * i) + 3) + " t \"AVG\", \\");
			out.println("\"" + file + "\" u 1:" + ((2 * i) + 5) + " t \"MAX\"");
			out.println("");
			if(!makePDF){
				out.println("pause -1");
				out.println("");
			}
		}
		out.close();
	}

	/**
	 * Finds the average file columns of each file for the number of runs to process
	 * @param dirPrefix, Directory Prefix
	 * @param filePrefix, File Prefix
	 * @param fileSuffix, File Suffix
	 * @param runs, number of runs to process
	 * @param outputDir, Output Directory
	 * @return the average condition results across the averages for each of the file columns
	 * @throws FileNotFoundException
	 */
	public static int averageConditionResults(String dirPrefix, String filePrefix, String fileSuffix, int runs,
			String outputDir) throws FileNotFoundException {
		String[] files = new String[runs];
		for (int i = 0; i < runs; i++) {
			files[i] = dirPrefix + i + "/" + filePrefix + i + fileSuffix;
		}
		int num = averageFileColumns(files, outputDir + "/" + filePrefix + "AVG" + fileSuffix);
		return num;
	}

	/**
	 * Given several files with columns of data in the same format,
	 * write one output file whose columns contain the corresponding averages
	 * of those values across the input files, and their sample variances.
	 * 
	 * 
	 * @param files Array of individual input file names
	 * @param output File name of new output file
	 * @return Number of columns in original input file (each should have same number)
	 * @throws java.io.FileNotFoundException if any of the files are not found
	 * 
	 */
	public static int averageFileColumns(String[] files, String output) throws FileNotFoundException {
		int result = 0;
		Scanner[] fs = new Scanner[files.length];
		// Create Scanner for each input file
		for (int i = 0; i < fs.length; i++) {
			fs[i] = new Scanner(new File(files[i]));
		}
		// Output file has a PrintStream
		PrintStream out = new PrintStream(new FileOutputStream(output));
		// Assumes all input files have same number of rows as first
		while (fs[0].hasNextLine()) {
			// Each scanner will hold one line from one of the files
			Scanner[] lines = new Scanner[fs.length];
			for (int i = 0; i < fs.length; i++) {
				String line = fs[i].nextLine();
				lines[i] = new Scanner(line);
			}

			String lineOut = "";
			result = 0;
			// Assume all input files have same number of columns as first
			while (lines[0].hasNext()) {
				double average = 0;
				double ss = 0;
				for (int i = 0; i < lines.length; i++) {
					double x = lines[i].nextDouble();
					double oldAverage = average;
					// incremental average update
					average += ((x - average) / (i + 1.0));
					// incremental sum of squares update
					ss += ((x - average) * (x - oldAverage));
				}
				// sample variance = s^2 = SS/(N-1)
				lineOut += average + "\t" + (ss / (lines.length - 1.0)) + "\t";
				result++;
			}
			out.println(lineOut);
		}
		out.close();
		return result;
	}

	/**
	 * Calls hypervolumesByGeneration for each run, which calls hypervolumeForGeneration for each generation, 
	 * which will print out the hypervolumes for each generation
	 * @param dirPrefix, directory prefix
	 * @param runs, number of process runs
	 * @param filePrefix, file prefix
	 * @param fileMiddle, middle of the file name
	 * @param fileExtension, extension for the file
	 * @param generations, number of generations
	 * @throws FileNotFoundException
	 */
	public static void hypervolumesByGenerationForAllRuns(String dirPrefix, int runs, String filePrefix,
			String fileMiddle, String fileExtension, int generations) throws FileNotFoundException {
		for (int i = 0; i < runs; i++) {
			hypervolumesByGeneration(dirPrefix, i, filePrefix, fileMiddle, fileExtension, generations,
					dirPrefix + i + "/" + filePrefix);
		}
	}

	/**
	 * Calls hypervolumeForGeneration for each generation, which will print out the hypervolumes
	 * @param dirPrefix, directory prefix
	 * @param run, number of process runs
	 * @param filePrefix, file prefix
	 * @param fileMiddle, middle of the file name
	 * @param fileExtension, extension for the file
	 * @param generations, number of generations
	 * @param outputPrefix, output prefix
	 * @throws FileNotFoundException
	 */
	public static void hypervolumesByGeneration(String dirPrefix, int run, String filePrefix, String fileMiddle,
			String fileExtension, int generations, String outputPrefix) throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream(outputPrefix + run + fileMiddle + "HV.txt"));
		for (int i = 0; i < generations; i++) {
			Pair<Double, Integer> hypervolumeAndFrontSize = hypervolumeForGeneration(dirPrefix, run, filePrefix,
					fileMiddle, i, fileExtension);
			// out.println(i + "\t" + hypervolume);
			out.println(i + "\t" + hypervolumeAndFrontSize.t1 + "\t" + hypervolumeAndFrontSize.t2);
		}
		out.close();
	}

	/**
	 * Actually returns both the hypervolume (as a Double) and the size of the
	 * Pareto front (the Integer)
	 * 
	 * @param dirPrefix
	 *            Dir of the specific method
	 * @param run
	 *            The number of the run
	 * @param filePrefix
	 *            Prefix for all data files
	 * @param fileMiddle
	 *            Unique file name part
	 * @param generation
	 *            Which generation to read
	 * @param fileExtension
	 *            How to end file
	 * @return pair with hypervolume and the size of the Pareto front
	 */
	public static Pair<Double, Integer> hypervolumeForGeneration(String dirPrefix, int run, String filePrefix,
			String fileMiddle, int generation, String fileExtension) {
		Hypervolume qualityIndicator = new Hypervolume();
		// Read the front from the files
		String file = dirPrefix + run + "/" + filePrefix + run + fileMiddle + generation + "." + fileExtension;
		double[][] fileData = qualityIndicator.utils_.readFront(file);
		double[][] solutions;
		// First drop solution number
		double[][] step1 = dropColumn(fileData, 0);
		// Then drop id numbers
		solutions = dropColumn(step1, 0);
		Task task = MMNEAT.task;
		int numObjectives = -1;
		if(task instanceof LonerTask) {
			numObjectives = task.numObjectives();
		} else if(task instanceof GroupTask) {
			//TODO: eventually should be generalized across all populations
			numObjectives = ((GroupTask) task).objectivesPerPopulation()[0];
		}
		while (solutions[0].length > numObjectives) {
			// Remove extra meta-heuristic objectives
			solutions = dropColumn(solutions, solutions[0].length - 1);
		}

		// Adjust for possibly negative min scores
		double[] mins = null;
		if(task instanceof LonerTask) {
			mins = MMNEAT.task.minScores();
		} else if(task instanceof GroupTask){
			//TODO: this is unclean and potentially problematic because minScores does not really make sense
			//with coevolution, and just the first population's objectives' minScores are returned from
			//the cooperativeTorusPredPreyTask minScore override.
			mins = ((GroupTask) task).minScores();
		}
		for (int i = 0; i < solutions.length; i++) {
			for (int j = 0; j < solutions[0].length; j++) {
				solutions[i][j] -= mins[j];
			}
		}

		int noNondominatedPoints = qualityIndicator.filterNondominatedSet(solutions, solutions.length, solutions[0].length);
		// Obtain hypervolume
		double value = qualityIndicator.calculateHypervolume(solutions, solutions.length, solutions[0].length);
		// return value;
		return new Pair<Double, Integer>(value, noNondominatedPoints);
	}

	/**
	 * Remove a given column from a 2D array of data.
	 * 
	 * TODO: Should this be moved to an existing Util class?
	 * 
	 * @param data 2D array of doubles
	 * @param col index of column to remove from data
	 * @return modified data array
	 */
	public static double[][] dropColumn(double[][] data, int col) {
		double[][] result = new double[data.length][data[0].length - 1];
		for (int i = 0; i < data.length; i++) {
			int k = 0;
			for (int j = 0; j < data[0].length; j++) {
				if (j != col) { // column to skip
					result[i][k++] = data[i][j];
				}
			}
		}
		return result;
	}

	/**
	 * plots the hypervolumes file which was calculated before
	 * @param filePrefix, file prefix
	 * @param fileSuffix, file suffix
	 * @param runs, number of process runs
	 * @param outputDir, output directory
	 * @throws FileNotFoundException
	 */
	private static void plotHypervolumesFile(String filePrefix, String fileSuffix, int runs, String outputDir) throws FileNotFoundException {
		plotHypervolumesFile(filePrefix, fileSuffix, runs, outputDir, false);
		plotHypervolumesFile(filePrefix, fileSuffix, runs, outputDir, true);
	}

	/**
	 * plots the hypervolumes file which was calculated before
	 * @param filePrefix, file prefix
	 * @param fileSuffix, file suffix
	 * @param runs, number of process runs
	 * @param outputDir, output directory
	 * @param makePDF, true if making a PDF, false if not
	 * @throws FileNotFoundException
	 */
	private static void plotHypervolumesFile(String filePrefix, String fileSuffix, int runs, String outputDir, boolean makePDF)
			throws FileNotFoundException {
		String plotFile = "";
		if(!makePDF){
			plotFile = outputDir + "/" + filePrefix + "All" + fileSuffix + ".plt";
		}else{
			plotFile = outputDir + "/" + filePrefix + "All" + fileSuffix + "PDF.plt";
		}
		PrintStream out = new PrintStream(new FileOutputStream(plotFile));
		String condition = Parameters.parameters.stringParameter("saveTo");
		if (makePDF) {
			out.println("set terminal pdf color");
		}
		out.println("set style data lines");
		out.println("set key left");
		if (makePDF) {
			out.println("set output \"Hypervolumes for " + condition + " by Generation.pdf\"");
		}
		out.println("set title \"Hypervolumes for " + condition + " by Generation\"");
		out.println("plot \\");
		for (int i = 0; i < runs; i++) {
			String file = condition + i + "/" + filePrefix + i + fileSuffix + ".txt";
			out.println("\"" + file + "\" u 1:2 t \"" + condition + i + "\"" + (i + 1 < runs ? ", \\" : ""));
		}
		out.println("");
		if (!makePDF) {
			out.println("pause -1");
			out.println("");
		}
		if (makePDF) {
			out.println("set output \"Pareto Front Size for " + condition + " by Generation.pdf\"");
		}
		out.println("set title \"Pareto Front Size for " + condition + " by Generation\"");
		out.println("plot \\");
		for (int i = 0; i < runs; i++) {
			String file = condition + i + "/" + filePrefix + i + fileSuffix + ".txt";
			out.println("\"" + file + "\" u 1:3 t \"" + condition + i + "\"" + (i + 1 < runs ? ", \\" : ""));
		}
		if (!makePDF) {
			out.println("");
			out.println("pause -1");
		}
		out.close();

		if (makePDF) {
			plotFile = outputDir + "/" + filePrefix + "AVG" + fileSuffix + "PDF.plt";
		}else{
			plotFile = outputDir + "/" + filePrefix + "AVG" + fileSuffix + ".plt";
		}
		out = new PrintStream(new FileOutputStream(plotFile));
		String file = filePrefix + "AVG" + fileSuffix + ".txt";
		if (makePDF) {
			out.println("set terminal pdf color");
		}
		out.println("set style data lines");
		out.println("set key left");
		if (makePDF) {
			out.println("set output \"Average Hypervolume for " + condition + " by Generation.pdf\"");
		}
		out.println("set title \"Average Hypervolume for " + condition + " by Generation\"");
		out.println("plot \\");
		out.println("\"" + file + "\" u 1:3 t \"" + condition + "\"");
		if (!makePDF) {
			out.println("");
			out.println("pause -1");
		}

		out.println("set yrange [0:]");
		if (makePDF) {
			out.println("set output \"Average Pareto Front Size for " + condition + " by Generation.pdf\"");
		}
		out.println("set title \"Average Pareto Front Size for " + condition + " by Generation\"");
		out.println("plot \\");
		out.println("\"" + file + "\" u 1:5 t \"" + condition + "\"");
		if (!makePDF) {
			out.println("");
			out.println("pause -1");
		}

		out.close();
	}

	/**
	 * plots the average fitnesses across all of the runs in a plot file
	 * @param filePrefix, file prefix
	 * @param middle, middle of the file name
	 * @param fileSuffix, file suffix
	 * @param num, number of columns in file
	 * @param runs, number of process runs
	 * @param outputDir, output directory
	 * @param t, critical t-value for Student's t-test
	 * @param popNum, number of current population
	 * @throws FileNotFoundException
	 */
	private static void plotAverageFitnessesFile(String filePrefix, String middle, String fileSuffix, int num,
			int runs, String outputDir, double t, int popNum) throws FileNotFoundException {
		plotAverageFitnessesFile(filePrefix, middle, fileSuffix, num, runs, outputDir, t, false, popNum);
		plotAverageFitnessesFile(filePrefix, middle, fileSuffix, num, runs, outputDir, t, true, popNum);
	}

	/**
	 * plots the average fitnesses across all of the runs in a plot file
	 * @param filePrefix, file prefix
	 * @param middle, middle of the file name
	 * @param fileSuffix, file suffix
	 * @param num, number of columns in file
	 * @param runs, number of process runs
	 * @param outputDir, output directory
	 * @param t, critical t-value for Student's t-test
	 * @param makePDF, true if making a PDF, false if not
	 * @param popNum, number of current population
	 * @throws FileNotFoundException
	 */
	private static void plotAverageFitnessesFile(String filePrefix, String middle, String fileSuffix, int num,
			int runs, String outputDir, double t, boolean makePDF, int popNum) throws FileNotFoundException {
		String plotFile = "";
		if(!makePDF){
			plotFile = outputDir + "/" + filePrefix + "AVG" + middle + ".plt";
		}else{
			plotFile = outputDir + "/" + filePrefix + "AVG" + middle + "PDF.plt";
		}
		PrintStream out = new PrintStream(new FileOutputStream(plotFile));
		String condition = Parameters.parameters.stringParameter("saveTo");
		if (makePDF) {
			out.println("set terminal pdf color");
		}
		out.println("set style data lines");
		out.println("set key left");
		// For confidence intervals
		out.println("set style fill transparent solid 0.2 noborder");

		String file = filePrefix + "AVG" + fileSuffix;
		for (int i = 1; i < num; i += 4) {
			Statistic stat = MMNEAT.aggregationOverrides.get((i - 1) / 4);
			String fitnessFunctionName = MMNEAT.fitnessFunctions.get(popNum).get((i - 1) / 4)
					+ (stat == null ? "" : "[" + stat.getClass().getSimpleName() + "]");
			if (makePDF) {
				out.println("set output \"Average '" + fitnessFunctionName + "' for " + condition + " by Generation.pdf\"");
			}
			out.println("set title \"Average '" + fitnessFunctionName + "' for " + condition + " by Generation\"");
			out.println("plot \\");

			int min = ((2 * i) + 1);
			// Note: Might need gnuplot's "every" command to space out plot frequency
			out.println("\"" + file + "\" u 1:" + min + " notitle lt 1 lw 2, \\");
			// Calculates standard error (SE) from variance (s^2): SE = sqrt(s^2 / N)
			out.println("\"" + file + "\" u 1:($"+ min +" - "+t+"*sqrt($"+ (min + 1) +"/"+runs+")):($"+ min +" + "+t+"*sqrt($"+ (min + 1) +"/"+runs+")) notitle with filledcurves lt 1 lw 2, \\");
			out.println("\"" + file + "\" u 1:"+min+":"+min+":"+min+" t \"MIN\" with errorbars lt 1 lw 2, \\");                        

			int avg = ((2 * i) + 3);
			// Note: Might need gnuplot's "every" command to space out plot frequency
			out.println("\"" + file + "\" u 1:" + avg + " notitle lt 2 lw 2, \\");
			// Calculates standard error (SE) from variance (s^2): SE = sqrt(s^2 / N)
			out.println("\"" + file + "\" u 1:($"+ avg +" - "+t+"*sqrt($"+ (avg + 1) +"/"+runs+")):($"+ avg +" + "+t+"*sqrt($"+ (avg + 1) +"/"+runs+")) notitle with filledcurves lt 2 lw 2, \\");
			out.println("\"" + file + "\" u 1:"+avg+":"+avg+":"+avg+" t \"AVG\" with errorbars lt 2 lw 2, \\");                        

			int max = ((2 * i) + 5);
			// Note: Might need gnuplot's "every" command to space out plot frequency
			out.println("\"" + file + "\" u 1:" + max + " notitle lt 3 lw 2, \\");
			// Calculates standard error (SE) from variance (s^2): SE = sqrt(s^2 / N)
			out.println("\"" + file + "\" u 1:($"+ max +" - "+t+"*sqrt($"+ (max + 1) +"/"+runs+")):($"+ max +" + "+t+"*sqrt($"+ (max + 1) +"/"+runs+")) notitle with filledcurves lt 3 lw 2, \\");
			out.println("\"" + file + "\" u 1:"+max+":"+max+":"+max+" t \"MAX\" with errorbars lt 3 lw 2");                        	

			out.println("");
			if (!makePDF) {
				out.println("pause -1");
				out.println("");
			}
		}
		out.close();
	}
}
