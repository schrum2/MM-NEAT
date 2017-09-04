package edu.utexas.cs.nn.evolution.nsga2.bd.characterizations;

import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.HighestBitBehaviorVector;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;

/**
 * a general network behavior characterization if highest bit behavior vector
 * applicable
 * 
 * @author Jacob Schrum
 */
public class GeneralHighestBitNetworkCharacterization<T extends Network> extends GeneralNetworkCharacterization<T> {

	/**
	 * gets behavior vector for highest bit behavior
	 */
	@Override
	public BehaviorVector getBehaviorVector(Score<T> score) {
		return new HighestBitBehaviorVector(getBehaviorVector(score.individual.getPhenotype(), this.syllabus),
				MMNEAT.networkOutputs);
	}
}
