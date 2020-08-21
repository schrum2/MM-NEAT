package edu.southwestern.tasks.loderunner.astar;

import edu.southwestern.util.datastructures.Pair;

public class LodeRunnerEnhancedEnemiesUtil {
	

	public static double getManhattanDistance(Pair<Integer, Integer> enemy, Pair<Integer, Integer> player) {
		double manDist = 0;
		double yDist = Math.abs(enemy.t2 - player.t2);
		double xDist = Math.abs(enemy.t1 - player.t1);
		manDist = yDist+xDist;
		return manDist;
	}
	
	public static double findMin(double left, double right, double up, double down) {
		double min = Double.MAX_VALUE;
		if(left < min)
			min = left;
		if(right < min)
			min = right;
		if(up < min)
			min = up;
		if(down < min)
			min = down;
		return min;
	}
	

}
