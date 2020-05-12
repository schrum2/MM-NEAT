package edu.southwestern.tasks.gvgai.zelda.level.grammars;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGraphGrammar;

public interface ZeldaDungeonGrammar {
	/**
	 * returns a backbone for a dungeon
	 * @return ZeldaGraphGrammar the backbone for the dungeon
	 */
	public ZeldaGraphGrammar getGrammar();
}
