package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;

/**
 * 
 * @author alicequint
 *
 * @param NN
 */
public abstract class NNEvaluationFunction<T extends Network> extends EvaluationFunction{

	protected Network nn;
	protected PhysicalGameState pgs;
	
	public NNEvaluationFunction(){
	}
	
	public void setNetwork(Genotype<T> g) {
		nn = g.getPhenotype();
	}
	
	/**
	 *  creates the array to be given to the NN
	 */
	protected abstract double[] gameStateToArray(GameState gs);
	
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
	
	public void givePhysicalGameState(PhysicalGameState pgs) {
		this.pgs = pgs;
	}
	
	/**
	 * @param maxplayer - player to be evaluated
	 * @param minplayer - opponent
	 * @param gs - specified state of the game
	 * @return number from -1 to 1 depending on if and how hard evaluated player is winning/losing
	 */
	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		double[] inputs = gameStateToArray(gs);
		double[] outputs = nn.process(inputs);
		float score = (float) outputs[0];
		return score;
	}
}
