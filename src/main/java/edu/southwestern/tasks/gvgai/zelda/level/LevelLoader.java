package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.List;

/**
 * 
 * @author jakeG
 *
 */
public interface LevelLoader {
	/**
	 * Get the specified levels as a list of 2D list of ints
	 * @return List of 2D levels represented as a 2D list
	 */
	public List<List<List<Integer>>> getLevels();
}
