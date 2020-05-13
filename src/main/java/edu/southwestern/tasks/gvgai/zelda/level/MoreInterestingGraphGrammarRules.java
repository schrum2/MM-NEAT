package edu.southwestern.tasks.gvgai.zelda.level;

public class MoreInterestingGraphGrammarRules extends ZeldaHumanSubjectStudy2019GraphGrammar {
	public MoreInterestingGraphGrammarRules() {
		super();
		GraphRule<ZeldaGrammar> rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
//		rule.grammar().setStart(ZeldaGrammar.START);
//		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
//		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
//		rule.grammar().addNodeBetween(ZeldaGrammar.BOMB_S);
//		rule.grammar().addNodeBetween(ZeldaGrammar.SOFT_LOCK_S);
//		graphRules.add(rule);
		
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().addNodeToStart(ZeldaGrammar.KEY);
		graphRules.add(rule);

		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.KEY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.LOCK);
		graphRules.add(rule);

		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.RAFT);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.RAFT);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.RAFT);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.RAFT_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.RAFT);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		graphRules.add(rule);
	
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.RAFT);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);


		//initialList.add(ZeldaGrammar.RAFT_S);
		
	}
	
}
