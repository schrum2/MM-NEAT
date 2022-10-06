package edu.southwestern.tasks.innovationengines;

import java.util.HashMap;
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
	
	@Override
	public int oneDimensionalIndex(HashMap<String, Object> keys) {
		throw new UnsupportedOperationException("One image can exist in all bins, so selecting one index with a HashMap is not supported");
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		throw new UnsupportedOperationException("One image can exist in all bins, so selecting one index with a HashMap is not supported");
	}

	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		return false;
	}

	@Override
	public boolean isOutsideRestrictedRange(int[] multi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String lowerRestrictedBounds() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String upperRestrictedBounds() {
		// TODO Auto-generated method stub
		return "";
	}

}
