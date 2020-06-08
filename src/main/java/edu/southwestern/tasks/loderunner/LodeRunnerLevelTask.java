package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

/**
 * 
 * @author kdste
 *
 * @param <T>
 */
public abstract class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> {
	
	private static final int numFitnessFunctions = 1; //we are only using a single fitness function right now 

	/**
	 * Registers all fitness functions that are used, rn only one is used for lode runner 
	 */
	public LodeRunnerLevelTask() {
		MMNEAT.registerFitnessFunction("DistanceToFarthestGold");
	}

	/**
	 * @return The number of fitness functions 
	 */
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

	/**
	 * Does one evaluation with the A* algorithm to see if the level is beatable 
	 * @param Genotype<T> 
	 * @param Integer 
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		List<Double> latentVector = (List<Double>) individual.getPhenotype(); //creates a double array for the spawn to be placed in GAN levels 
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions);
		List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual);
		List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level);
		Random rand = new Random(Double.doubleToLongBits(doubleArray[0]));
		LodeRunnerGANUtil.setSpawn(level, emptySpaces, rand);
		LodeRunnerState start = new LodeRunnerState(level);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		double simpleAStarDistance = -1;
		try { 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, Parameters.parameters.integerParameter( "aStarSearchBudget"));
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
