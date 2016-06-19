package edu.utexas.cs.nn.tasks.gridTorus.cooperative;

import edu.utexas.cs.nn.tasks.gridTorus.GroupTorusPredPreyTask;
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
 * @param <T> phenotype of each population
 *
 */
public class CooperativePredatorsVsStaticPreyTask<T extends Network> extends GroupTorusPredPreyTask<T> {

	public CooperativePredatorsVsStaticPreyTask(){
		super();
	}

	@Override
	/**
	 * gets and returns the task instance (for evolved predators)
	 * @return task, torusPredPreyTask instance
	 */
	public TorusPredPreyTask<T> getLonerTaskInstance() {
		if(task == null) {
			task = new TorusEvolvedPredatorsVsStaticPreyTask<T>();
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
		return getLonerTaskInstance().getPreyAgents(team[0]);
	}

	@Override
	/**
	 * gets the pred agents
	 * @param team
	 * @return pred agents
	 */
	public TorusPredPreyController[] getPredAgents(Genotype<T>[] team) {
		task.evolved = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPredators")];
		TorusPredPreyTask.getEvolvedControllers(task.evolved, team, true);
		return task.evolved; 
	}

}
