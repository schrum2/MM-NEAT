package edu.southwestern.tasks.loderunner.astar;

import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.search.Heuristic;

public class LodeRunnerEnchancedEnemies {
	
	public static Heuristic<LodeRunnerAction, LodeRunnerState> moveCloserToPlayer = new Heuristic<LodeRunnerAction,LodeRunnerState>(){

		@Override
		public double h(LodeRunnerState s) {
			
			return 0;
		}
		
	};

}
