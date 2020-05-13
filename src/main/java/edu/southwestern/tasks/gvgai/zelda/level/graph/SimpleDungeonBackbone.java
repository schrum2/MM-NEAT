package edu.southwestern.tasks.gvgai.zelda.level.graph;

import java.util.LinkedList;
import java.util.List;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.util.datastructures.Graph;

/**
 * creates a simple dungeon backbone
 * 
 *
 */
public class SimpleDungeonBackbone implements ZeldaDungeonGraphBackBone {

	@Override
	/**
	 * Sets the rules used in the GraphDungeon
	 * @return graph the set of rules used in GraphDungeon
	 */
	public Graph<ZeldaGrammar> getInitialGraphBackBone() {
		List<ZeldaGrammar> initialList = new LinkedList<>();
		initialList.add(ZeldaGrammar.START_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.TREASURE);
		Graph<ZeldaGrammar> graph = new Graph<>(initialList);

		
		return graph;
	}
	

}
