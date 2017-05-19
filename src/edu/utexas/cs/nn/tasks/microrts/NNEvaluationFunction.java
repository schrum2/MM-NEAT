package edu.utexas.cs.nn.tasks.microrts;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;

public abstract class NNEvaluationFunction<T extends Network> extends EvaluationFunction{

	protected Network nn;

	public NNEvaluationFunction(){
	}
	
	public void setNetwork(Genotype<T> g) {
		nn = g.getPhenotype();
	}

	/**
	 * converts GameState into Array containing information useable to the network
	 * @param gs gamestate to be turned into array
	 * @return Array of coded info
	 */
	public abstract double[] gameStateToArray(GameState gs);

	/**
	 * 
	 * @return labels of sensors given to nn
	 */
	public abstract String[] sensorLabels();
	
	/**
	 * maximum possible thing returned by the evaluation function
	 */
	@Override
	public float upperBound(GameState gs) {
		return 1;
	}
}
