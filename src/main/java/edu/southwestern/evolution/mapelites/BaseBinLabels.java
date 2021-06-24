package edu.southwestern.evolution.mapelites;

import java.util.HashMap;

/**
 * The rule for mapping from a HashMap to a 1D index via the multidimensional index is the same for all BinLabel schemes
 * @author schrum2
 *
 */
public abstract class BaseBinLabels implements BinLabels {

	@Override
	public int oneDimensionalIndex(HashMap<String, Object> keys) {
		return oneDimensionalIndex(multiDimensionalIndices(keys));
	}
}
