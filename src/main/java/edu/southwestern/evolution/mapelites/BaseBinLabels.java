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
		// Value is saved if previously computed, so that we don't need to do it again
		if(keys.containsKey("dim1D")) return (int) keys.get("dim1D");
		return oneDimensionalIndex(multiDimensionalIndices(keys)); // Else compute
	}
}
