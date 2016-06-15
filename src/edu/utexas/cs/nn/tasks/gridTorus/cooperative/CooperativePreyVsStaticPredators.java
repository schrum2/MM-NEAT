package edu.utexas.cs.nn.tasks.gridTorus.cooperative;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPreyVsStaticPredatorsTask;
import edu.utexas.cs.nn.tasks.gridTorus.TorusPredPreyTask;

/**
 * Defines a cooperative evolved team of prey where each individual prey
 * has its own genotype and is evolved individually, all against static predators
 * @author rollinsa
 *
 */
public class CooperativePreyVsStaticPredators<T extends Network> extends CooperativeTorusPredPreyTask<T> {
	
	public CooperativePreyVsStaticPredators(){
		super();
	}

	@Override
	/**
	 * an integer array holding the fitness objective scores for each population
	 * @return an array of ints of the fitness objective scores for each team member
	 */
	public int[] objectivesPerPopulation() {
		int[] result = new int[Parameters.parameters.integerParameter("torusPreys")];
		for(int i = 0; i < result.length; i++) {
			result[i] = task.objectives.get(i).size();
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
		int[] result = new int[Parameters.parameters.integerParameter("torusPreys")];
		result[0] = scores;
		return result;
	}

	@Override
	/**
	 * an int designating the number of populations to be evolved
	 * @return number of population being evolved as an int
	 */
	public int numberOfPopulations() {
		return Parameters.parameters.integerParameter("torusPreys");
	}

	@Override
	/**
	 * gets and returns the task instance (for evolved prey)
	 * @return task, torusPredPreyTask instance
	 */
	public TorusPredPreyTask<T> getLonerTaskInstance() {
		if(task == null) {
			task = new TorusEvolvedPreyVsStaticPredatorsTask<T>();
			while(task.objectives.size() < numberOfPopulations()) {
				task.addAllObjectives(task.objectives.size());
			}
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
		task.evolved = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPreys")];
		TorusPredPreyTask.getEvolvedControllers(task.evolved, team, false);
		return task.evolved; 
	}

	@Override
	/**
	 * gets the pred agents
	 * @param team
	 * @return pred agents
	 */
	public TorusPredPreyController[] getPredAgents(Genotype<T>[] team) {
		return getLonerTaskInstance().getPredAgents(team[0]);
	}

}
