package edu.utexas.cs.nn.tasks.gridTorus.cooperative;

import java.util.ArrayList;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.CooperativeTask;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyAgent;
import edu.utexas.cs.nn.tasks.gridTorus.TorusPredPreyTask;

/**
 * Defines a cooperative torusPredPreyTask for either a group of predators being
 * evolved against a group of static prey or a group of prey being evolved against a
 * group of static predators. Each agent of the team is evolved individually, with
 * its own genotype (a team of evolving populations).
 * @author rollinsa
 * @param <T> phenotype of all evolving populations
 *
 */
public abstract class CooperativeTorusPredPreyTask<T extends Network> extends CooperativeTask implements NetworkTask {

	public TorusPredPreyTask<T> task;

	/**
	 * construct a cooperative predPrey task based off of the torusPredPreyTask
	 * type that is being evolved
	 */
	public CooperativeTorusPredPreyTask() {
		task = getLonerTaskInstance();
	}

	/**
	 * gets and returns the task instance (for either evolved predators or evolved prey)
	 * @return task, torusPredPreyTask instance
	 */
	public abstract TorusPredPreyTask<T> getLonerTaskInstance();

	/**
	 * an int designating the number of populations to be evolved
	 */
        @Override
	public abstract int numberOfPopulations();

	/**
	 * an integer array holding the fitness objectives for each population
	 */
        @Override
	public abstract int[] objectivesPerPopulation();

	/**
	 * an integer array holding the other scores for each population (fitness scores
	 * that are not actually being used in the evaluation and evolution of the agent(s))
	 */
        @Override
	public abstract int[] otherStatsPerPopulation();

	/**
	 * gets and returns the time stamp of this task
	 * @return time stamp as a double
	 */
        @Override
	public double getTimeStamp() {
		return task.getTimeStamp();
	}

	/**
	 * nothing needs to be done here
	 */
        @Override
	public void finalCleanup() {
	}

	/**
	 * gets and returns the sensor labels for this task
	 * @return sensor labels in an array of strings
	 */
        @Override
	public String[] sensorLabels() {
		//TODO: Can't just use the lonerTask version of this method, it crashes
		//array of evolved agents is not defined
		return task.sensorLabels();
	}

	/**
	 * gets and returns the output labels for this task
	 * @return output labels in an array of strings
	 */
        @Override
	public String[] outputLabels() {
		return task.outputLabels();
	}

	@Override
	/**
	 * One genotype for each member of the team, and one score for each member
	 * as well
	 *
	 * @param team, list of the genotypes of the teammates
	 * @return list of scores, behaviors, and genotype for each member of the team
	 */
	public ArrayList<Score> evaluate(Genotype[] team) {

		ArrayList<Score> scores = new ArrayList<Score>();

		TorusPredPreyController[] predAgents = getPredAgents(team);
		TorusPredPreyController[] preyAgents = getPreyAgents(team);

		TorusPredPreyGame game = getLonerTaskInstance().runEval(predAgents, preyAgents);

		for(int i = 0; i < numberOfPopulations(); i++){
			//each score : Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector, double[] otherStats)

			double[] fitnesses = new double[objectivesPerPopulation()[i]];
			double[] otherStats = new double[otherStatsPerPopulation()[i]];

			// Fitness function requires an organism, so make this genotype into an organism
			// this erases information stored about module usage, so was saved in
			// order to be reset after the creation of this organism
			Organism<T> organism = new NNTorusPredPreyAgent<T>(team[i], !task.preyEvolve);
			for (int j = 0; j < fitnesses.length; j++) {
				fitnesses[j] = task.objectives.get(j).score(game, organism);
			}
			for (int j = 0; j < otherStats.length; j++) {
				otherStats[j] = task.otherScores.get(j).score(game,organism);		
			}
			scores.add(new Score(team[i], fitnesses, null, otherStats));
		}
		return scores;
	}

	/**
	 * gets the prey agents
	 * @param team
	 * @return prey agents
	 */
	public abstract TorusPredPreyController[] getPreyAgents(Genotype<T>[] team);

	/**
	 * gets the pred agents
	 * @param team
	 * @return pred agents
	 */
	public abstract TorusPredPreyController[] getPredAgents(Genotype<T>[] team);

}
