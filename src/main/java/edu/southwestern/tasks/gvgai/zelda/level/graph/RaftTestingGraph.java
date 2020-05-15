package edu.southwestern.tasks.gvgai.zelda.level.graph;

import java.util.LinkedList;
import java.util.List;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.util.datastructures.Graph;
/**
 * Constructs the graph used in the HumanSubjectStudy2019
 * 
 * 
 * Original ZeldaGraphGrammar paper: Jake Gutierrez and Jacob Schrum. Generative Adversarial 
 * Network Rooms in Generative Graph Grammar Dungeons for The Legend of Zelda. 
 * In IEEE Congress on Evolutionary Computation. 2020
 * https://people.southwestern.edu/~schrum2/SCOPE/gutierrez.cec2020.pdf
 * @author
 *
 */
public class RaftTestingGraph implements ZeldaDungeonGraphBackBone {

	@Override
	/**
	 * Sets the back bone used in the HumanSubjectStudy2019
	 * @return graph the set of rules used in the study
	 */
	public Graph<ZeldaGrammar> getInitialGraphBackBone(){
		List<ZeldaGrammar> initialList = new LinkedList<>();
		initialList.add(ZeldaGrammar.START_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.RAFT_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.PUZZLE_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.TREASURE);
		Graph<ZeldaGrammar> graph = new Graph<>(initialList);
		return graph;
	}



}
