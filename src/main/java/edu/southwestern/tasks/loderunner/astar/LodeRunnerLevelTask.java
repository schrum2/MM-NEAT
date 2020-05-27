package edu.southwestern.tasks.loderunner.astar;

import ch.idsia.ai.agents.Agent;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;

public class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> {
	private Agent agent;
	
	public LodeRunnerLevelTask() {
		try {//tries to create an agent from the command line parameter
			agent = (Agent) ClassCreation.createObject("lodeRunnerLevelAgent");
		} catch (NoSuchMethodException e) { //if it can't then it prints the stack trace
			e.printStackTrace();
		}
	}

	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// TODO Auto-generated method stub
		return null;
	}

}
