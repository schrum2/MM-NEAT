package edu.southwestern.tasks.gvgai.zelda.level.graph;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.util.datastructures.Graph;
/**
 * 
 * @author Ben Capps
 *
 */
public interface ZeldaDungeonGraphBackBone {
	/**
	 * returns a backbone for a dungeon
	 * @return ZeldaGraphGrammar the backbone for the dungeon
	 */
	public Graph<ZeldaGrammar> getInitialGraphBackBone();
	

}
