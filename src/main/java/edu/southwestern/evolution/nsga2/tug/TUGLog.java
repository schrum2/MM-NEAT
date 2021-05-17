package edu.southwestern.evolution.nsga2.tug;

import edu.southwestern.log.StatisticsLog;
import edu.southwestern.MMNEAT.MMNEAT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * logs information about TUG:
 * Targeting Unachieved Goals
 * 
 * @author Jacob Schrum
 */
public class TUGLog extends StatisticsLog<double[]> {
	// phenotype unknown here
	@SuppressWarnings("rawtypes")
	private final TUGNSGA2 ea;

	@SuppressWarnings("rawtypes")
	/**
	 * Constructs a TUGLog object which logs information
	 * about Targeting Unachieved Goals
	 * @param _prefix, string of the log prefix
	 * @param ea, TUGNSGA2 instance
	 */
	public TUGLog(String _prefix, TUGNSGA2 ea) {
		super(_prefix, null);
		this.ea = ea;
		if (lastLoadedEntry != null) {
			boolean[] usage = new boolean[ea.getTask().numObjectives()];
			double[] rwas = new double[ea.getTask().numObjectives()];
			double[] loadedGoals = new double[ea.getTask().numObjectives()];
			double[] lastDeltas = new double[ea.getTask().numObjectives()];
			boolean[] initialClimb = new boolean[ea.getTask().numObjectives()];
			Scanner s = new Scanner(lastLoadedEntry);
			s.next(); // drop generation
			for (int i = 0; i < loadedGoals.length; i++) {
				usage[i] = (s.nextInt() == 1); // get use bit
				s.next(); // drop average
				s.next(); // drop max
				rwas[i] = s.nextDouble(); // get RWA
				loadedGoals[i] = s.nextDouble(); // get goal
				lastDeltas[i] = s.nextDouble(); // get last goal delta
				initialClimb[i] = (s.nextInt() == 1); // get use bit
			}
			ea.loadTugState(usage, rwas, loadedGoals, lastDeltas, initialClimb);
			s.close();
		}
		// Cannot use the default plot file setup because there are extra things to plot
		File plotFile = new File(directory + prefix + "_log.plt");
		if (!plotFile.exists()) {
			ArrayList<String> labels = MMNEAT.fitnessPlusMetaheuristics(0); // population 0: loner task only!
			try {
				PrintStream plotStream = new PrintStream(new FileOutputStream(plotFile));
				plotStream.println("set style data lines");
				plotStream.println("set xlabel \"Generation\"");
				plotStream.println();

				// Plot objective scores and goals
				for (int i = 0; i < labels.size(); i++) {
					int start = 2 + (i * 7);
					plotStream.println("set title \"" + prefix + " " + labels.get(i) + "\"");
					plotStream.println("plot \\");
					plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 1) + " t \"Performance\", \\");
					plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 2) + " t \"Goal Target\", \\");
					plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 3) + " t \"RWA\", \\");
					plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 4) + " t \"Goal\"");
					plotStream.println();
					plotStream.println("pause -1");
					plotStream.println();
				}

				// // Plot behavior of goals
				// for (int i = 0; i < labels.size(); i++) {
				// int start = 2 + (i * 6);
				// plotStream.println("set title \"" + prefix + " " +
				// labels.get(i) + " Goal Behavior\"");
				// plotStream.println("plot \\");
				// plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" +
				// (start + 0) + " t \"Goal Usage\", \\");
				// plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" +
				// (start + 5) + " t \"Goal Delta\"");
				// plotStream.println();
				// plotStream.println("pause -1");
				// plotStream.println();
				// }

				plotStream.close();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Override
	/**
	 * logs information about TUG:
	 * Targeting Unachieved Goals
	 * @param stats, an array of doubles of the stats
	 * @param generation, an int designating generation number
	 */
	public void log(ArrayList<double[]> stats, int generation) {
		double[] performance = stats.get(0);
		double[] targets = stats.get(1);
		int objectives = targets.length;

		stream.print(generation + "\t");
		for (int j = 0; j < objectives; j++) {
			stream.print((ea.useObjective[j] ? 1 : 0) + "\t" + performance[j] + "\t" + targets[j] + "\t"
					+ ea.recencyWeightedAverages[j] + "\t" + ea.goals[j] + "\t" + ea.lastTUGDelta[j] + "\t"
					+ (ea.initialClimb[j] ? 1 : 0) + "\t");
		}
		stream.println();
	}
}
