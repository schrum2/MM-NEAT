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
	protected boolean coevolution;
	
	private int howManyEvals = 0;
	
	public NNEvaluationFunction(){
	}
	
	public void setNetwork(Genotype<T> g) {
		howManyEvals = 0;
		nn = g.getPhenotype();
	}
	
	/**
	 *  creates the array to be given to the NN
	 */
	protected abstract double[] gameStateToArray(GameState gs, int playerToEvaluate);
	
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
		howManyEvals++;
		
		// Score from max perspective
		double[] inputs1 = gameStateToArray(gs, maxplayer);
		double[] outputs1 = nn.process(inputs1);
		float score1 = (float) outputs1[0];
		// Score from min perspective
		double[] inputs2 = gameStateToArray(gs, minplayer);
		double[] outputs2 = nn.process(inputs2);
		float score2 = (float) outputs2[0];
		
		return score1 - score2;
	}
	
	/**
	 *  for FF
	 * @return # of times a game state has been evaluated
	 */
	public int getNumEvals(){
		return howManyEvals;
	}

	public abstract int getNumInputSubstrates();

	public void setCoevolution(boolean b) {
		coevolution = b;
	}
}
