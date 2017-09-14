package edu.southwestern.tasks.innovationengines;

import edu.southwestern.evolution.mapelites.BinMapping;
import edu.southwestern.networks.Network;
import edu.southwestern.scores.Score;
import edu.southwestern.util.graphics.ImageNetClassification;

/**
 * Given scores for all Image Net classes, return the bin name with the highest score
 * @author Jacob Schrum
 */
public class ImageNetBinMapping<T extends Network> implements BinMapping<T> {
	/**
	 * Bin is the label with the highest score
	 */
	@Override
	public String binForScore(Score<T> s) {
		return ImageNetClassification.bestLabel(s.behaviorVector);
	}

}
