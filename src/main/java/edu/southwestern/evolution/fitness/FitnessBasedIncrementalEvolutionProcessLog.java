package edu.southwestern.evolution.fitness;

import edu.southwestern.log.StatisticsLog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Jacob Schrum
 */
public class FitnessBasedIncrementalEvolutionProcessLog extends StatisticsLog<Double> {

	public FitnessBasedIncrementalEvolutionProcessLog(String _prefix, FitnessBasedIncrementalEvolutionProcess<?> process) {
		super(_prefix, null);
		// Restore state from log
		if (lastLoadedEntry != null) {
			Scanner s = new Scanner(lastLoadedEntry);
			s.next(); // drop generation
			double performance = s.nextDouble();
			double goal = s.nextDouble();
			double rwa = s.nextDouble();
			process.loadState(performance, goal, rwa);
			s.close();
		}
		// Cannot use the default plot file setup because there are extra things
		// to plot
		File plotFile = new File(directory + prefix + "_log.plot");
		if (!plotFile.exists()) {
			try {
				PrintStream plotStream = new PrintStream(new FileOutputStream(plotFile));
				plotStream.println("set style data lines");
				plotStream.println("set xlabel \"Generation\"");
				plotStream.println();

				// Plot objective scores and goals
				int start = 2;
				plotStream.println("set title \"" + prefix + "\"");
				plotStream.println("plot \\");
				plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 1) + " t \"Performance\", \\");
				plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 2) + " t \"Goal\", \\");
				plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (start + 3) + " t \"RWA\"");
				plotStream.println();
				plotStream.println("pause -1");
				plotStream.println();

				plotStream.close();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Override
	public void log(ArrayList<Double> scores, int generation) {
		stream.print(generation + "\t");
		for (int j = 0; j < scores.size(); j++) {
			stream.print(scores.get(j) + "\t");
		}
		stream.println();
	}
}
