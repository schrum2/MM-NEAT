package edu.southwestern.tasks.gvgai.zelda.level.graph;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.util.datastructures.Graph;
/**
 * The back bone is the set of non-terminal symbols
 * Inputed into the graph grammar
 * @author
 *
 */
public interface ZeldaDungeonGraphBackBone {
	/**
	 * returns a backbone for a dungeon
	 * @return ZeldaGraphGrammar the backbone for the dungeon
	 */
	public Graph<ZeldaGrammar> getInitialGraphBackBone();
	

}
