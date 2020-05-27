package edu.southwestern.tasks.loderunner.astar;

import java.util.ArrayList;

import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.State;

public class LodeRunnerState extends State<LodeRunnerState.LodeRunnerAction>{
	public LodeRunnerState() {
		
	}
	
	public static class LodeRunnerAction implements Action{
		
	}
	
	public static Heuristic<LodeRunnerAction,LodeRunnerState> collectGold = new Heuristic<LodeRunnerAction,LodeRunnerState>(){

		@Override
		public double h(LodeRunnerState s) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	};

	@Override
	public State<LodeRunnerAction> getSuccessor(LodeRunnerAction a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<LodeRunnerAction> getLegalActions(State<LodeRunnerAction> s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGoal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double stepCost(State<LodeRunnerAction> s, LodeRunnerAction a) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
