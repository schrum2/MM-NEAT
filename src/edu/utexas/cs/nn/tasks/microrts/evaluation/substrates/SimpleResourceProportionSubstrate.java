package edu.utexas.cs.nn.tasks.microrts.evaluation.substrates;

import micro.rts.GameState;

/**
 * Tracks proportion of given player's resources with respect to the other player.
 * 
 * @author schrum2
 *
 */
public class SimpleResourceProportionSubstrate extends MicroRTSSubstrateInputs {

	private boolean trackMyResources; // as opposed to opponent resources

	public SimpleResourceProportionSubstrate(boolean trackMyResources) {
		this.trackMyResources = trackMyResources;
	}

	@Override
	public double[][] getInputs(GameState gs, int evaluatedPlayer) {
		// % 2 works because there are 2 players. Would not work otherwise
		double trackCount = gs.getPlayer(trackMyResources ? evaluatedPlayer : (evaluatedPlayer + 1) % 2).getResources();
		double otherCount = gs.getPlayer(!trackMyResources ? evaluatedPlayer : (evaluatedPlayer + 1) % 2).getResources();
		// The +1.0 is to prevent division by 0 error
		// Difference and max are not an actual proportion, but this calculation makes the result of a sign consistent with who has more resources
		return new double[][]{ {(trackCount - otherCount) / (Math.max(trackCount,otherCount) + 1.0)} };
	}
}
