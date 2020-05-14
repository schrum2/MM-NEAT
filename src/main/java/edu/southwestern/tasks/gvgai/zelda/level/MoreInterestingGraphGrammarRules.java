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
	
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.RAFT_S);
//		rule.grammar().setStart(ZeldaGrammar.RAFT);
//		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S);
//		rule.grammar().setStart(ZeldaGrammar.ENEMY);
//		graphRules.add(rule);
//
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		graphRules.add(rule);
//		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		graphRules.add(rule);
//		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.PUZZLE_S);
		rule.grammar().setStart(ZeldaGrammar.PUZZLE);
		graphRules.add(rule);
		
		
		
		
		
		
		
		
		
		
		
		
		
		//DEFAULT RULES
		
		//START TO ANYTHING!!
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().addNodeToStart(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.PUZZLE_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.PUZZLE);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.RAFT);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		graphRules.add(rule);
		
		
		//EVERY POSSIBLE KEY COMBO
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.PUZZLE_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.PUZZLE);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.RAFT);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		graphRules.add(rule);
		
		//EVERY ENEMY COMBO!!
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.PUZZLE_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.PUZZLE);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.RAFT);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		
		
		
		
	
		//NOW FOR LOCKS!
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.PUZZLE_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.PUZZLE);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.RAFT_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.RAFT);
		graphRules.add(rule);
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		graphRules.add(rule);
		
		
		
		//NOW FOR PUZZLES!
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.PUZZLE_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.PUZZLE);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.PUZZLE_S, ZeldaGrammar.KEY_S);
//		rule.grammar().setStart(ZeldaGrammar.PUZZLE);
//		rule.grammar().setEnd(ZeldaGrammar.KEY);
//		graphRules.add(rule);
		
		
//		System.out.println("testing:");
//		for(GraphRule<ZeldaGrammar> r: graphRules) {
//			System.out.println("Start:"+r.getSymbolStart());
//			System.out.println("End:"+r.getSymbolEnd());
//
//		}
		//initialList.add(ZeldaGrammar.RAFT_S);
		
	}
	
}
