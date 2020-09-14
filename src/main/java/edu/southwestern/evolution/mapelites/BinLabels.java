package edu.southwestern.evolution.mapelites;

import java.util.List;

/**
 * Bin labals for MAP Elites
 * @author Jacob Schrum
 *
 */
public interface BinLabels {
	/**
	 * String names for each bin, laid out in 1D
	 * @return List of String names
	 */
	public List<String> binLabels();
	/**
	 * MAP Elites typically has a multidimensional archive. Given an array with each of the multiple
	 * indices, this method reduces the array to a single 1D index using row-major order look up.
	 * @param multi Each dimension of the archive in sequence for a particular bin
	 * @return Where the multiple indices map in row-major order to a 1D structure.
	 */
	public int oneDimensionalIndex(int[] multi);
}
