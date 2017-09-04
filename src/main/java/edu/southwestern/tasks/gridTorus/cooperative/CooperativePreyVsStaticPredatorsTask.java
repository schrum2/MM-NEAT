package edu.utexas.cs.nn.tasks.gridTorus.cooperative;

import edu.utexas.cs.nn.tasks.gridTorus.GroupTorusPredPreyTask;
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
public class CooperativePreyVsStaticPredatorsTask<T extends Network> extends GroupTorusPredPreyTask<T> {
	
	public CooperativePreyVsStaticPredatorsTask(){
		super();
	}

	@Override
	/**
	 * gets and returns the task instance (for evolved prey)
	 * @return task, torusPredPreyTask instance
	 */
	public TorusPredPreyTask<T> getLonerTaskInstance() {
		if(task == null) {
			task = new TorusEvolvedPreyVsStaticPredatorsTask<T>();
			while(task.objectives.size() < Parameters.parameters.integerParameter("torusPreys")) {
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
