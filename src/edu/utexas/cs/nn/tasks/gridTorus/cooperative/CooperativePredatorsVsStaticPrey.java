package edu.utexas.cs.nn.tasks.gridTorus.cooperative;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.TorusPredPreyTask;

/**
 * Defines a cooperative evolved team of predators where each individual predator
 * has its own genotype and is evolved individually, all against static prey
 * @author rollinsa
 *
 */
public class CooperativePredatorsVsStaticPrey<T extends Network> extends CooperativeTorusPredPreyTask<T> {

	public CooperativePredatorsVsStaticPrey(){
		super();
	}


	@Override
	/**
	 * an integer array holding the fitness objective scores for each population
	 * @return an array of ints of the fitness objective scores for each team member
	 */
	public int[] objectivesPerPopulation() {
		int objectives = task.numObjectives();
		int[] result = new int[Parameters.parameters.integerParameter("torusPredators")];
		for(int i = 0; i < result.length; i++) {
			result[i] = objectives;
		}
		return result;
	}

	@Override
	/**
	 * an integer array holding the other scores for each population, as fitness scores which do 
	 * not have an effect on the actual evolution/selection process but are tracked for some other
	 * data/book-keeping purposes
	 * @return an array of ints of the other scores for each team member
	 */
	public int[] otherStatsPerPopulation() {
		int scores = task.numOtherScores();
		int[] result = new int[Parameters.parameters.integerParameter("torusPredators")];
		for(int i = 0; i < result.length; i++) {
			result[i] = scores;
		}
		return result;
	}

	@Override
	/**
	 * an int designating the number of populations to be evolved
	 * @return number of population being evolved as an int
	 */
	public int numberOfPopulations() {
		return Parameters.parameters.integerParameter("torusPredators");
	}

	@Override
	/**
	 * gets and returns the task instance (for evolved predators)
	 * @return task, torusPredPreyTask instance
	 */
	public TorusPredPreyTask<T> getLonerTaskInstance() {
		if(task == null) {
			task = new TorusEvolvedPredatorsVsStaticPreyTask<T>();
		}
		return task;
	}

	@Override
	/**
	 * gets the prey agents
	 * @param team
	 * @return prey agents
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T>[] team) {
		return getLonerTaskInstance().getPreyAgents(team[0]);
	}

	@Override
	/**
	 * gets the pred agents
	 * @param team
	 * @return pred agents
	 */
	public TorusPredPreyController[] getPredAgents(Genotype<T>[] team) {
		TorusPredPreyController[] evolved = getLonerTaskInstance().evolved;
		TorusPredPreyTask.getEvolvedControllers(evolved, team, true);
		return evolved;
	}

}
