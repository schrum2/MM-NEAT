package edu.southwestern.log;

import edu.southwestern.parameters.Parameters;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Logging class for summary statistics about a group of scores.
 *
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 */
public abstract class StatisticsLog<T> extends MMNEATLog {

	public ArrayList<ArrayList<Double>> allMins, allMaxes, allAverages;
	public double[] overallMaxes;
	public double[] overallMins;
	boolean draw = Parameters.parameters.booleanParameter("watchFitness");

	/**
	 * Constructor for a statistics log. Uses inherited MMNEATLog
	 * 
	 * @param infix
	 *            name of file
	 * @param labels
	 *            labels for plot files
	 */
	public StatisticsLog(String infix, ArrayList<String> labels) {
		super(infix);// super constructor
		if (draw) {// used to facilitate drawing output to plot file
			allMins = new ArrayList<ArrayList<Double>>();
			allMaxes = new ArrayList<ArrayList<Double>>();
			allAverages = new ArrayList<ArrayList<Double>>();
		}
		if (labels != null) {// used to retrieve plot labels
			makePlotFile(labels, false);
			makePlotFile(labels, true);
		}
	}

	/**
	 * Makes a plot file of all max, min and average from each generation
	 * 
	 * @param labels
	 *            labels of plot file
	 * @param makePDF
	 *            whether or not to also make a pdf
	 */
	public void makePlotFile(ArrayList<String> labels, boolean makePDF) {
		File plotFile = null;
		if (!makePDF) {
			plotFile = new File(directory + prefix + "_log.plot");
		} else {
			plotFile = new File(directory + prefix + "_logPDF.plot");
		}
		if (!plotFile.exists()) {
			try {
				PrintStream plotStream = new PrintStream(new FileOutputStream(plotFile));
				if (makePDF) {
					plotStream.println("set terminal pdf color");
				}
				plotStream.println("set style data lines");
				plotStream.println("set xlabel \"Generation\"");
				plotStream.println();

				for (int i = 0; i < labels.size(); i++) {
					int start = 2 + (i * 4);
					if (makePDF) {
						plotStream.println("set output \"" + prefix + "-" + labels.get(i) + ".pdf\"");
					}
					plotStream.println("set title \"" + prefix + " " + labels.get(i) + "\"");
					plotStream.println("plot \\");
					plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + start + " t \"MIN\", \\");
					plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 1) + " t \"AVG\", \\");
					plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 2) + " t \"MAX\"");
					plotStream.println();
					if (!makePDF) {
						plotStream.println("pause -1");
						plotStream.println();
					}
				}

				plotStream.close();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}

	}

	/**
	 * initializes arrayLists of max min and averages if they are to be saved
	 * 
	 * @param objectives
	 */
	public void initLongTermScores(int objectives) {
		for (int i = 0; i < objectives; i++) {
			allMins.add(new ArrayList<Double>());
			allMaxes.add(new ArrayList<Double>());
			allAverages.add(new ArrayList<Double>());
		}
		// creates arrays containing max range for values
		overallMaxes = new double[objectives];
		Arrays.fill(overallMaxes, -Double.MAX_VALUE);
		overallMins = new double[objectives];
		Arrays.fill(overallMins, Double.MAX_VALUE);
	}

	public abstract void log(ArrayList<T> scores, int generation);

	/**
	 * logs average scores from each generation
	 * 
	 * @param scores
	 *            2D array containing scores from generation
	 * @param generation
	 *            generation in question
	 */
	public void logAverages(double[][] scores, int generation) {
		int categories = scores[0].length;
		if (draw && allMins.isEmpty()) {
			initLongTermScores(categories);
		}

		double[] averages = new double[categories];
		double[] mins = new double[categories];
		double[] maxes = new double[categories];
		double[] ss = new double[categories];
		double[] variances = new double[categories];
		double[] stdevs = new double[categories];

		for (int j = 0; j < categories; j++) {
			mins[j] = Double.MAX_VALUE;
			maxes[j] = -Double.MAX_VALUE;
		}

		for (int i = 0; i < scores.length; i++) {
			double[] values = scores[i];
			for (int j = 0; j < categories; j++) {
				double oldAverage = averages[j];
				averages[j] += (values[j] - averages[j]) / (i + 1);
				mins[j] = Math.min(mins[j], values[j]);
				maxes[j] = Math.max(maxes[j], values[j]);
				ss[j] += (values[j] - averages[j]) * (values[j] - oldAverage);
			}
		}

		stream.print(generation + "\t");
		for (int j = 0; j < categories; j++) {
			variances[j] = ss[j] / (scores.length - 1);
			stdevs[j] = Math.sqrt(variances[j]);
			stream.print(mins[j] + "\t" + averages[j] + "\t" + maxes[j] + "\t" + stdevs[j] + "\t");

			if (draw) {
				allMins.get(j).add(mins[j]);
				allMaxes.get(j).add(maxes[j]);
				allAverages.get(j).add(averages[j]);
				overallMaxes[j] = Math.max(overallMaxes[j], maxes[j]);
				overallMins[j] = Math.min(overallMins[j], mins[j]);
			}
		}
		stream.println();
	}
}
