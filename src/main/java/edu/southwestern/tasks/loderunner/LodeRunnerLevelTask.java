package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

public abstract class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> {
	
	private static final int numFitnessFunctions = 1;

	public LodeRunnerLevelTask() {
		MMNEAT.registerFitnessFunction("DistanceToFarthestGold");
	}

	@Override
	public int numObjectives() {
		return 1; //only one fitness function right now 
	}
	
	/**
	 * Different level generators use the genotype to generate a level in different ways
	 * @param individual Genotype 
	 * @return List of lists of integers corresponding to tile types
	 */
	public abstract List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<T> individual);

	@Override
	public double getTimeStamp() {
		return 0; //not used 
	}

	@SuppressWarnings("unused")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// TODO Auto-generated method stub
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions);
		List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual);
		LodeRunnerState start = new LodeRunnerState(level);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		double simpleAStarDistance = -1;
		try { 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, 100000);
			if(actionSequence == null) {
				fitnesses.add(-1.0);
			} else {
				simpleAStarDistance = 1.0*actionSequence.size();
				fitnesses.add(simpleAStarDistance);
			}
		} catch(IllegalStateException e) {
			fitnesses.add(-1.0);
			System.out.println("failed search");
			e.printStackTrace();
		}
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		
 		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), new double[0]);
	}

}
