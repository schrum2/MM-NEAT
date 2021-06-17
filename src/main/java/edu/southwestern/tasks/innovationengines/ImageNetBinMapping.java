package edu.southwestern.tasks.innovationengines;

import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;
import edu.southwestern.util.graphics.ImageNetClassification;

/**
 * Return names for all ImageNet classes
 * @author Jacob Schrum
 */
public class ImageNetBinMapping<T extends Network> implements BinLabels {
	/**
	 * All 1000 ImageNet labels
	 */
	@Override
	public List<String> binLabels() {
		return ImageNetClassification.getImageNetLabels();
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		return multi[0]; // archive is 1D
	}
	
	@Override
	public String[] dimensions() {
		return new String[] {"Image Category"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {1000};
	}
	
}
