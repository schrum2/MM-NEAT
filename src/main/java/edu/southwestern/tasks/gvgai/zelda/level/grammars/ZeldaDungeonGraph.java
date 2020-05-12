package edu.southwestern.tasks.gvgai.zelda.level.grammars;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.util.datastructures.Graph;
/**
 * 
 * @author Ben Capps
 *
 */
public interface ZeldaDungeonGraph {
	/**
	 * returns a backbone for a dungeon
	 * @return ZeldaGraphGrammar the backbone for the dungeon
	 */
	public Graph<ZeldaGrammar> getGraph();
}
