package edu.southwestern.log;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.scores.Score;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.Plot;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Log specifically used for logging fitness information or other generational
 * score information associated with individual genotypes.
 *
 * @author Jacob Schrum
 * @param <T> phenotype being evolved
 */
public class FitnessLog<T> extends StatisticsLog<Score<T>> {

	private static int fitnessPanels = 0;
	DrawingPanel[] panels = null;

	public FitnessLog(String prefix) {
		this(prefix, 0);
	}	
	
	public FitnessLog(String prefix, int pop) {
		super(prefix, MMNEAT.fitnessPlusMetaheuristics(pop));
	}
	
	public void initPanels(int objectives){
		initPanels(objectives, 0);
	}

	public void initPanels(int objectives, int pop) {
		panels = new DrawingPanel[objectives];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new DrawingPanel(Plot.BROWSE_DIM, Plot.BROWSE_DIM, MMNEAT.fitnessFunctions.get(pop).get(i));
			panels[i].setLocation((fitnessPanels++) * (Plot.EDGE + Plot.BROWSE_DIM),
					Plot.TOP + TWEANN.NETWORK_VIEW_DIM);
		}
	}

        @Override
	public void log(ArrayList<Score<T>> scores, int generation) {
		this.logScores(scores, generation);
		this.logStats(scores, generation);
	}

	private void logScores(ArrayList<Score<T>> scores, int generation) {
		try {
			PrintStream gen = new PrintStream(new FileOutputStream(new File(directory + prefix + "_gen" + generation + ".txt")));
			PrintStream extra = null;
			if (scores.get(0).otherStats.length > 0) {
				extra = new PrintStream(new FileOutputStream(new File(directory + prefix + "_other_scores_gen" + generation + ".txt")));
			}

			for (int i = 0; i < scores.size(); i++) {
				Score<T> s = scores.get(i);
				// Actual fitness scores
				gen.print(i + "\t");
				gen.print(s.individual.getId() + "\t");
				for (int j = 0; j < s.scores.length; j++) {
					gen.print(s.scores[j] + "\t");
				}
				gen.println();
				// Scores for other things worth tracking, but not part of fitness
				if (s.otherStats.length > 0) {
					extra.print(i + "\t");
					extra.print(s.individual.getId() + "\t");
					for (int j = 0; j < s.otherStats.length; j++) {
						extra.print(s.otherStats[j] + "\t");
					}
					extra.println();
				}
			}
			gen.close();
			if (extra != null) {
				extra.close();
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Could not log scores to file");
			System.exit(1);
		}
	}

	/**
	 * Given the whole population, log several summary statistics.
	 * 
	 * @param scores
	 *            Population of scores from this generation
	 * @param generation
	 *            Particular generation being logged
	 */
	private void logStats(ArrayList<Score<T>> scores, int generation) {
		double[][] nextStage = new double[scores.size()][];
		for (int i = 0; i < scores.size(); i++) {
			Score<T> s = scores.get(i);
			double[] combined = new double[s.scores.length + s.otherStats.length];
			System.arraycopy(s.scores, 0, combined, 0, s.scores.length);
			System.arraycopy(s.otherStats, 0, combined, s.scores.length, s.otherStats.length);
			nextStage[i] = combined;
		}
		logAverages(nextStage, generation);

		if (draw) {
			int objectives = nextStage[0].length;
			if (panels == null) {
				initPanels(objectives);
			}
			for (int i = 0; i < objectives; i++) {
				panels[i].clear();
				GraphicsUtil.linePlot(panels[i], overallMins[i], overallMaxes[i], allMaxes.get(i), Color.magenta);
				GraphicsUtil.linePlot(panels[i], overallMins[i], overallMaxes[i], allAverages.get(i), Color.blue);
				GraphicsUtil.linePlot(panels[i], overallMins[i], overallMaxes[i], allMins.get(i), Color.green);
			}
		}
	}
}
