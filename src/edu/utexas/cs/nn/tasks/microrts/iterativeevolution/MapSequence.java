package edu.utexas.cs.nn.tasks.microrts.iterativeevolution;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * @author alicequint
 *
 * extended by classes that exists to define
 * the maps and speed of sequence for iterative evolution
 */
public interface MapSequence {

	/**
	 * gives appropriate map to task
	 * @param generation
	 * 				which generation is being evolved at that time
	 * @return
	 * 			path to the map, from MMNEAT/data/MicroRTS/maps
	 */
	public String getAppropriateMap(int generation);
}