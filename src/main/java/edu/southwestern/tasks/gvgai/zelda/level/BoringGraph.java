package edu.southwestern.tasks.gvgai.zelda.level;

public class BoringGraph extends ZeldaHumanSubjectStudy2019GraphGrammar{
	public BoringGraph() {
		super();
		graphRules.clear();
		
		GraphRule<ZeldaGrammar> rule; /*=  new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);*/
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.TREASURE);
//		rule.grammar().setStart(ZeldaGrammar.START);
//		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
//		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);

		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		//rule.grammar().addNodeBetween(ZeldaGrammar.BOMB_S);
		rule.grammar().addNodeBetween(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		/*
		 * initialList.add(ZeldaGrammar.START_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.TREASURE);
		 */
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.SOFT_LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.SOFT_LOCK);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		
		
		
	}
}
