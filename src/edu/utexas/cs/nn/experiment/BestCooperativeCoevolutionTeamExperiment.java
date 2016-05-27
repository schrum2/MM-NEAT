package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.CooperativeTask;
import edu.utexas.cs.nn.util.file.FileUtilities;
import java.util.ArrayList;
import wox.serial.Easy;

/**
 * Load saved results from coevolution experiment and evaluate every possible
 * team combination to get their scores.
 *
 * @author Jacob Schrum
 */
public class BestCooperativeCoevolutionTeamExperiment implements Experiment {

	private CooperativeTask task;
	private Genotype[] team;

	public void init() {
		task = (CooperativeTask) MMNEAT.task;
		int numMembers = task.numberOfPopulations();
		team = new Genotype[numMembers];
		String teamDir = FileUtilities.getSaveDirectory() + "/bestTeam";
		for (int i = 0; i < numMembers; i++) {
			team[i] = (Genotype) Easy.load(teamDir + "/teamMember" + i + ".xml");
		}
	}

	public void run() {
		DrawingPanel[] panels = task.drawNetworks(team);
		ArrayList<Score> result = task.evaluate(team);
		task.disposePanels(panels);
		for (Score s : result) {
			System.out.println(s);
		}
	}

	public boolean shouldStop() {
		// Will never be called
		return true;
	}
}
