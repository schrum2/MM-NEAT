package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.scent;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import java.util.*;
import pacman.Executor;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionKStepDeathScentBlock extends VariableDirectionBlock {

	public static int stepCount;
	public final boolean max;
	public static HashMap<Integer, HashMap<Integer, Integer>> scentMaps = null;
	// public static int[] maxDeathScents = new int[Constants.NUM_MAZES];
	// public static int[] sumDeathScents = new int[Constants.NUM_MAZES];
	public static int[] topSumDeathScents = new int[Constants.NUM_MAZES];

	public VariableDirectionKStepDeathScentBlock(int dir) {
		this(dir, true);
	}

	public VariableDirectionKStepDeathScentBlock(int dir, boolean max) {
		this(dir, Parameters.parameters.integerParameter("smallStepSimDepth"), max);
	}

	public VariableDirectionKStepDeathScentBlock(int dir, int k, boolean max) {
		super(dir);
		stepCount = k;
		this.max = max;
		// This needs to be updated every generation
		scentMaps = new HashMap<>();
		updateScentMaps();
	}

	/**
	 * Pulls scent map data from file. Needs to be called every new generation
	 * so that new data is accessible. The updates cannot simply be executed as
	 * deaths occur, since then individuals evaluated later in a generation
	 * would unfairly benefit from the death data of that same generation.
	 */
	public static void updateScentMaps() {
		if (scentMaps != null) {
			scentMaps = Executor.deaths.deathCount();
			// maxDeathScents = new int[Constants.NUM_MAZES];
			// sumDeathScents = new int[Constants.NUM_MAZES];
			topSumDeathScents = new int[Constants.NUM_MAZES];
			for (int i = 0; i < topSumDeathScents.length; i++) {
				if (scentMaps.containsKey(i)) {
					Collection<Integer> deaths = scentMaps.get(i).values();
					List<Integer> deathCounts = Collections.list(Collections.enumeration(deaths));
					// maxDeathScents[i] = StatisticsUtilities.maximum(deaths);
					// sumDeathScents[i] = StatisticsUtilities.sum(deaths);
					Collections.sort(deathCounts, Collections.reverseOrder()); // Descending
																				// order
					Iterator<Integer> itr = deathCounts.iterator();
					int numChecked = 0;
					// Put sum of top death counts in array
					while (itr.hasNext() && numChecked < stepCount) {
						topSumDeathScents[i] += itr.next();
						numChecked++;
					}
				}
			}
			System.out.println("Updating scent maps from file: " + Arrays.toString(topSumDeathScents));
		}
	}

	public double wallValue() {
		return 0;
	}

	@Override
	public double getValue(GameFacade gf) {
		final int currentLocation = gf.getPacmanCurrentNodeIndex();
		final int[] neighbors = gf.neighbors(currentLocation);
		final int next = neighbors[dir];
		assert next != -1 : "The next direction is not viable!";
		ArrayList<Integer> visited = new ArrayList<Integer>();
		visited.add(currentLocation);
		HashMap<Integer, Integer> mazeScentMap = scentMaps.get(gf.getMazeIndex());
		if (mazeScentMap == null) {
			return 0; // Agents have never died in this maze before
		}
		double count = countScentAlongPath(gf, visited, mazeScentMap, currentLocation, next, stepCount, max);
		// double maxCount = maxDeathScents[gf.getMazeIndex()] * stepCount; //
		// Maximum deaths at each location checked
		// double maxCount = sumDeathScents[gf.getMazeIndex()];
		double maxCount = topSumDeathScents[gf.getMazeIndex()];
		// System.out.println(dir + ":death scent:" + count + ":max:"+maxCount +
		// ":=" + (count/maxCount));
		return maxCount == 0 ? 0 : count / maxCount; // Normalized
	}

	public static int countScentAlongPath(GameFacade gf, ArrayList<Integer> visited,
			HashMap<Integer, Integer> previousDeathsAtLocations, int sourceLocation, int currentLocation,
			int remainingSteps, boolean max) {
		assert visited.contains(sourceLocation) : "Must have visited source location";
		if (remainingSteps == 0) {
			return 0;
		} else {
			final int[] neighbors = gf.neighbors(currentLocation);
			boolean foundReverse = false;
			for (int i = 0; i < neighbors.length && !foundReverse; i++) {
				if (neighbors[i] == sourceLocation) {
					neighbors[i] = -1; // Don't go backwards
					foundReverse = true;
				}
			}
			assert foundReverse : "Should always find the reverse direction";
			int aggregateCount = max ? -1 : Integer.MAX_VALUE;
			visited.add(currentLocation);
			for (int i = 0; i < neighbors.length; i++) {
				if (neighbors[i] != -1) {
					int count = countScentAlongPath(gf, visited, previousDeathsAtLocations, currentLocation,
							neighbors[i], remainingSteps - 1, max);
					aggregateCount = max ? Math.max(aggregateCount, count) : Math.min(aggregateCount, count);
				}
			}
			assert visited.get(
					visited.size() - 1) == currentLocation : "Should be popping the currentLocation off end of list";
			visited.remove(visited.size() - 1); // Should remove currentLocation
			boolean alreadyCounted = visited.contains(currentLocation);
			int targetsToCount = alreadyCounted ? 0
					: (previousDeathsAtLocations.containsKey(currentLocation)
							? previousDeathsAtLocations.get(currentLocation) : 0);
			// if(CommonConstants.watch && targetsToCount > 0){
			// gf.addPoints(Color.red, new int[]{currentLocation});
			// }
			return aggregateCount + targetsToCount;
		}
	}

	@Override
	public String getLabel() {
		return (max ? "Max" : "Min") + " Sum Death Scent Count " + stepCount + " Steps Ahead";
	}
}
