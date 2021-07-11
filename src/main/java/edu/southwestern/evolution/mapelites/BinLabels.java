package edu.southwestern.evolution.mapelites;

import java.util.HashMap;
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
	
	/**
	 * To allow a variety of different binning schemes to be easily used,
	 * there needs to be support for calculating the 1D index based on a variety
	 * of different possible key values associated with different behavior characteristics.
	 * Each BinLabels scheme can take the HashMap given here, extract only the relevant
	 * values, discretize those values into archive indices, and then return the
	 * corresponding 1D index.
	 * 
	 * @param keys Dictionary associating labels with aspects of phenotype's behavior
	 * @return 1D index in the bin labels array
	 */
	public int oneDimensionalIndex(HashMap<String,Object> keys);
	
	/**
	 * If a HashMap is provided, then the components are raw parts of a behavior
	 * characteristic. They can be converted to multi-dimensional indices identifying
	 * a specific bin in the archive. This is an intermediate step in computing
	 * the 1D index in the list of bin labels.
	 * 
	 * @param keys Dictionary associating labels with aspects of phenotype's behavior
	 * @return Multi-dimensional index in the archive
	 */
	public int[] multiDimensionalIndices(HashMap<String,Object> keys);
	
	/**
	 * Gets the names of each bin dimension
	 * @return Array with a string name for each dimension
	 */
	public String[] dimensions();
	
	
	/**
	 * Gets the size of each archive dimension
	 * @returnArray with an int for each dimension's size
	 */
	public int[] dimensionSizes();
	
}
