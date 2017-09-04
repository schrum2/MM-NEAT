package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.SimpleBlueprintGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.BlueprintTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Exactly the same as CooperativeGhostPillSubtaskSelectorMsPacManTask, except
 * that the teams to evaluate are selected based on blueprints, which are
 * themselves evolved, and also receive a fitness score.
 *
 * @author Jacob Schrum
 */
public class CooperativeBlueprintSubtaskMsPacManTask<T extends Network>
		extends CooperativeSubtaskSelectorMsPacManTask<T>implements BlueprintTask {

	public static final int SELECTOR_POP_INDEX = 0;
	/**
	 * Holds each previous set of scores so that child blueprints will have
	 * access to parent networks *
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList<ArrayList<Score>> previousScores;
	/**
	 * When a parent network score is accessed in previousScores, remember where
	 * it is *
	 */
	private HashMap<Long, Integer> parentLocations;
	protected int numberBlueprintParentReferences;
	protected int totalBlueprintReferences = 0;
	/**
	 * Can be different if mu != lambda *
	 */
	protected int previousTotalBlueprintReferences = 0;
	protected int numberFullChildBlueprints;
	protected int numberFullParentBlueprints;

        @Override
	public int getNumberUnevaluatedReferences() {
		return unevaluatedIndividuals;
	}

        @Override
	public int getNumberBlueprintParentReferences() {
		return numberBlueprintParentReferences;
	}

        @Override
	public int getTotalBlueprintReferences() {
		return totalBlueprintReferences;
	}

        @Override
	public int getNumberFullParentBlueprints() {
		return numberFullParentBlueprints;
	}

        @Override
	public int getNumberFullChildBlueprints() {
		return numberFullChildBlueprints;
	}

        @Override
	public int getPreviousTotalBlueprintReferences() {
		return previousTotalBlueprintReferences;
	}

        @Override
	public int getPreviousNumberUnevaluatedReferences() {
		return previousUnevaluatedIndividuals;
	}

	public CooperativeBlueprintSubtaskMsPacManTask() {
		super();
	}

	/**
	 * Overriding the avaluateAll method to create a join order based on
	 * blueprints rather than being randomized.
	 *
	 * @param populations
	 *            different populations of genotypes, each the same size.
	 *            Populations are: 0=Selectors,1=Ghosts,2=Pills,3=Blueprints.
	 * @return score for each member of each population
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<ArrayList<Score>> evaluateAllPopulations(ArrayList<ArrayList<Genotype>> populations) {
		// Re-initialize each eval cycle
		parentLocations = new HashMap<Long, Integer>();
		numberBlueprintParentReferences = 0;
		previousTotalBlueprintReferences = totalBlueprintReferences;
		totalBlueprintReferences = 0;
		numberFullChildBlueprints = 0;
		numberFullParentBlueprints = 0;

		int blueprintPopIndex = populations.size() - 1;
		ArrayList<Genotype> blueprints = populations.get(blueprintPopIndex);
		int pops = populations.size();
		int popSize = populations.get(0).size();

		// Add empty arrays of necessary size to teamOrder
		ArrayList<ArrayList<Integer>> teamOrder = new ArrayList<ArrayList<Integer>>(pops);
		for (int p = 0; p < pops; p++) {
			ArrayList<Integer> order = new ArrayList<Integer>(popSize);
			teamOrder.add(order);
		}
		// For each blueprint, fill slots in each column of team order to define
		// team
		for (int i = 0; i < blueprints.size(); i++) {
			SimpleBlueprintGenotype blueprint = (SimpleBlueprintGenotype) blueprints.get(i);
			// System.out.println(blueprint);
			ArrayList<Long> ids = blueprint.getPhenotype();
			boolean fullParent = true;
			boolean fullChild = true;
			for (int p = 0; p < pops; p++) {
				int index = -1;
				if (p == blueprintPopIndex) {
					index = i;
				} else {
					Long id = ids.get(p);
					// Find id in sub-population
					index = PopulationUtil.indexOfGenotypeWithId(populations.get(p), id);
					/**
					 * If index == -1 at this point, it means the network the
					 * blueprint wants is not in a child population, and instead
					 * it will need to be searched for in the parent population
					 * (previousScores) at the next phase
					 */
					if (index == -1) {
						fullChild = false;
						numberBlueprintParentReferences++;
						// System.out.println("Will look for network " + id + "
						// of pop " + p + " in parent population");
					} else {
						fullParent = false;
					}
					totalBlueprintReferences++;
				}
				teamOrder.get(p).add(index);
			}
			if (fullParent) {
				numberFullParentBlueprints++;
			} else if (fullChild) {
				numberFullChildBlueprints++;
			}
		}

		return evaluateAllPopulations(populations, teamOrder);
	}

	/**
	 * Modified so that team excludes the blueprint, which was responsible for
	 * making the team.
	 *
	 * @param populations
	 *            all populations, with blueprint population last
	 * @param joinOrder
	 *            order to eval teams
	 * @param index
	 *            index in join order groups
	 * @return team to evaluate
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Genotype[] getTeam(ArrayList<ArrayList<Genotype>> populations, List<ArrayList<Integer>> joinOrder, int index) {
		int pops = populations.size() - 1; // -1 so blueprint population ignored
		Genotype[] team = new Genotype[pops];
		for (int p = 0; p < pops; p++) {
			int popSlot = joinOrder.get(p).get(index);
			if (popSlot == -1) { // Network is in a parent population
				// Get the blueprint index
				int bpIndex = joinOrder.get(pops).get(index);
				// Get the blueprint
				SimpleBlueprintGenotype bp = (SimpleBlueprintGenotype) populations.get(pops).get(bpIndex);
				team[p] = getParentScore(bp, joinOrder, index, p).individual;
			} else { // Network was in a child population
				team[p] = populations.get(p).get(popSlot);
			}
		}
		return team;
	}

	/**
	 * Scores will only contain results for the networks, but the blueprint
	 * needs to get its score as well. Each blueprint slot should be null before
	 * a score is assigned. Network slots may have scores, and the new score is
	 * always the max between the current and previous.
	 *
	 * @param rawSums
	 *            Array that builds up to hold sum of scores across all evals
	 * @param teamOrder
	 *            eval order for teams
	 * @param order
	 *            index in team order groups
	 * @param scores
	 *            scores for the networks from evaluated team
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void addScores(Score[][] rawSums, List<ArrayList<Integer>> teamOrder, int order,
			ArrayList<Score> scores) {
		int blueprintPop = rawSums.length - 1;
		for (int p = 0; p < scores.size(); p++) {
			if (p == SELECTOR_POP_INDEX) {
                                // index of blueprint
				int orderIndex = teamOrder.get(blueprintPop).get(order); 
				// Each blueprint should only get one set of noisy evals
				assert(rawSums[blueprintPop][orderIndex] == null);
				// Blue print gets this score too
				Score score = scores.get(p); // score from selector
				rawSums[blueprintPop][orderIndex] = score.copy();
			}
			int orderIndex = teamOrder.get(p).get(order);
			Score score = scores.get(p);
			if (orderIndex == -1) { // Add to a parent pop score
				long parentId = score.individual.getId();
				int parentIndex = parentLocations.get(parentId);
				Score parentScore = previousScores.get(p).get(parentIndex);
				previousScores.get(p).set(parentIndex, score.maxScores(parentScore));
				// System.out.println("Updated parent score " +
				// parentScore.individual.getId() + " in pop " + p);
			} else {
				rawSums[p][orderIndex] = score.maxScores(rawSums[p][orderIndex]);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected ArrayList<ArrayList<Score>> wrapUpScores(Score[][] rawSums, ArrayList<ArrayList<Genotype>> populations,
			List<ArrayList<Integer>> teamOrder) {
		/**
		 * When evolving with blueprints, the scores, which also contain the
		 * evaluated genotypes, need to be saved a bit longer. This method is
		 * called twice per generation: once for the parent population, and once
		 * for the child population. The child population will have blueprints
		 * containing members of the parent population, so these scores are
		 * needed for two reasons: 1) parent genotypes need to be retrieved from
		 * the Score instances so they can be evaluated in child blueprints, and
		 * 2) parent scores need to be updated as a result (which works since
		 * this variable is just a reference to the scores array).
		 */
		this.previousScores = super.wrapUpScores(rawSums, populations, teamOrder);
		return previousScores;
	}

	/**
	 * Selector net, ghost task net, pill task net, and blueprint
	 *
	 * @return
	 */
	@Override
	public int numberOfPopulations() {
		return super.numberOfPopulations() + 1;
	}

	@Override
	public int[] objectivesPerPopulation() {
		int[] upper = super.objectivesPerPopulation();
		int[] result = new int[upper.length + 1];
		System.arraycopy(upper, 0, result, 0, upper.length);
		result[result.length - 1] = 1;
		return result;
	}

	@Override
	public int[] otherStatsPerPopulation() {
		int[] upper = super.objectivesPerPopulation();
		int[] result = new int[upper.length + 1];
		System.arraycopy(upper, 0, result, 0, upper.length);
		result[result.length - 1] = task.otherScores.size();
		return result;
	}

	/**
	 * Look up a parent population network Score given a blueprint from the
	 * child population that specifies its index.
	 *
	 * @param bp
	 *            child blueprint
	 * @param joinOrder
	 *            join order for child populations
	 * @param index
	 *            join order column index
	 * @param popIndex
	 *            subpop index
	 * @return Score of parent network (contains genotype)
	 */
	@SuppressWarnings("rawtypes")
	private Score getParentScore(SimpleBlueprintGenotype bp, List<ArrayList<Integer>> joinOrder, int index, int popIndex) {
		int popSize = previousScores.get(popIndex).size();
		// Get desired network id
		long netId = bp.getPhenotype().get(popIndex);
		// Found before?
		if (parentLocations.containsKey(netId)) {
			return previousScores.get(popIndex).get(parentLocations.get(netId));
		}
		// Search the previous scores
		for (int q = 0; q < popSize; q++) {
			Score s = previousScores.get(popIndex).get(q);
			if (s.individual.getId() == netId) {
				// Remember how to find this parent later when assigning scores
				parentLocations.put(netId, q);
				return s;
			}
		}
		System.out.println("The parent network should have been found!");
		System.exit(1);
		return null;
	}
}
