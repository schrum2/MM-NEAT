package edu.southwestern.tasks.innovationengines;

import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;
import edu.southwestern.util.graphics.ImageNetClassification;

/**
 * Return names for all ImageNet classes
 * @author Jacob Schrum
 */
public class ImageNetBinMapping<T extends Network> implements BinLabels<T> {
	/**
	 * All 1000 ImageNet labels
	 */
	@Override
	public List<String> binLabels() {
		return ImageNetClassification.getImageNetLabels();
	}

}
