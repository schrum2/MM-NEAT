package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.GroupTask;
import edu.utexas.cs.nn.util.file.FileUtilities;
import java.util.ArrayList;
import wox.serial.Easy;

/**
 * Actually only works for coevolved Ms. Pac-Man experiments.
 * 
 * Load saved results from coevolution experiment and evaluate every possible
 * team combination to get their scores.
 *
 * @author Jacob Schrum
 */
public class BestCooperativeMsPacManTeamExperiment implements Experiment {

	private GroupTask task;
	@SuppressWarnings("rawtypes")
	private Genotype[] team;

        @SuppressWarnings("rawtypes")
		@Override
	public void init() {
		task = (GroupTask) MMNEAT.task;
		int numMembers = task.numberOfPopulations();
		team = new Genotype[numMembers];
		String teamDir = FileUtilities.getSaveDirectory() + "/bestTeam";
		for (int i = 0; i < numMembers; i++) {
			team[i] = (Genotype) Easy.load(teamDir + "/teamMember" + i + ".xml");
		}
	}

        @SuppressWarnings("rawtypes")
		@Override
	public void run() {
		DrawingPanel[] panels = GroupTask.drawNetworks(team);
		ArrayList<Score> result = task.evaluate(team);
		GroupTask.disposePanels(panels);
		for (Score s : result) {
			System.out.println(s);
		}
	}

        @Override
	public boolean shouldStop() {
		// Will never be called
		return true;
	}
}
