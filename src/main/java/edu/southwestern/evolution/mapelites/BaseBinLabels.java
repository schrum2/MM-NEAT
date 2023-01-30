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
	
	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		// By default do not discard anything, but bin representations can be more efficient if
		// irrelevant bins are not stored.
		return false;
	}
	
	@Override
	public boolean isOutsideRestrictedRange(int[] multi) {
		// By default, there is no restricted range
		return false;
	}
	
	/**
	 * Assume no restricted bounds exist, but this can be overridden
	 * @return String listing the restricted lower bounds in each dimension, separated by spaces
	 */
	@Override
	public String lowerRestrictedBounds() {
		return "";
	}
	
	/**
	 * Assume no restricted bounds exist, but this can be overridden
	 * @return String listing the restricted upper bounds in each dimension, separated by spaces
	 */
	@Override
	public String upperRestrictedBounds() {
		return "";
	}
}
