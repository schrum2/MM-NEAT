package edu.southwestern.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.gridTorus.GroupTorusPredPreyTask;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Task involving multiple individuals taken from separate populations.
 * Each population could contribute a different member of a team,
 * or a different component of a single structure. The different
 * populations may even by in competition.
 *
 * @author Jacob Schrum
 */
public abstract class GroupTask implements MultiplePopulationTask {

	/**
	 * Used by blueprint evolution 
	 */
	protected int unevaluatedIndividuals = 0;
	protected int previousUnevaluatedIndividuals = 0;
	/**
	 * Number of times each component has to be evaluated in a team.
         * A "team" might actually consist of different groups of competing
         * organisms.
	 */
	protected int teams;
	/**
	 * Gets randomly shuffled. Contains indices of where to pick component team
	 * members from for each team to be evaluated. This is an instance variable
	 * so that it doesn't have to be recreated each time, merely reshuffled.
	 */
	private ArrayList<ArrayList<Integer>> joinOrder = null;
        
        // Logging team data
	public MMNEATLog teamLog;
	private final boolean bestTeamScore;

	public GroupTask() {
		this.teams = Parameters.parameters.integerParameter("teams");
		if (Parameters.parameters.booleanParameter("io") && Parameters.parameters.booleanParameter("teamLog")) {
			this.teamLog = new MMNEATLog("Teams", true);
		}
		TWEANN.NETWORK_VIEW_DIM = 800 / 5; // Why these magic numbers? Why not 160?
		this.bestTeamScore = Parameters.parameters.booleanParameter("bestTeamScore");
	}

	/**
	 * Need to mix and match members from the different populations into teams
	 * that are evaluated. The score of an individual in each team is its
	 * average from all the teams it participates in (in the objectives it cares
	 * about)
	 *
	 * Assumes each sub-population has the same size.
	 *
	 * @param populations
	 *            different populations of genotypes, each the same size
	 * @return score for each member of each population
	 */
	@SuppressWarnings("rawtypes") // because populations can use different genotypes
	@Override
	public ArrayList<ArrayList<Score>> evaluateAllPopulations(ArrayList<ArrayList<Genotype>> populations) {
		int pops = populations.size();
		int popSize = populations.get(0).size();
		// initialize the join order if not previously done
		if (joinOrder == null) {
			joinOrder = new ArrayList<ArrayList<Integer>>(pops);
			for (int p = 0; p < pops; p++) {
				ArrayList<Integer> order = new ArrayList<Integer>(teams * popSize);
				for (int t = 0; t < teams; t++) {
                                        // Each index is repeated "teams" number of times
					for (int i = 0; i < popSize; i++) {
						order.add(i);
					}
				}
				joinOrder.add(order);
			}
		}

		// Shuffling happens whether the order is new or not
		for (int i = 0; i < pops; i++) {
			Collections.shuffle(joinOrder.get(i), RandomNumbers.randomGenerator);
		}

		return evaluateAllPopulations(populations, joinOrder);
	}



	/**
	 * Performs all group evaluations on the collection of populations
	 * using the designated join orders. Each list in the joinOrder
	 * corresponds to a population in populations. Each value in the sub-lists
	 * of joinOrder is the index of a specific individual in the corresponding
	 * populations sub-list.
	 *
	 * In other words, joinOrder contains parallel lists of members to choose
	 * from the parallel population arrays of populations.
	 *
	 * @param populations
	 *            all populations (all are the same size)
	 * @param teamOrder
	 *            how to join the population members into teams to evaluate
	 * @return score for each individual of each population
	 */
	@SuppressWarnings("rawtypes") // because population types may be mixed
	public ArrayList<ArrayList<Score>> evaluateAllPopulations(ArrayList<ArrayList<Genotype>> populations, List<ArrayList<Integer>> teamOrder) {
		int pops = populations.size();
                // Each is of same size
		int popSize = populations.get(0).size();

		// initialize score table (nulls)
		Score[][] rawScores = new Score[pops][popSize];
		// Evaluate all
		int totalEvals = teamOrder.get(0).size();

		// General tracking of best in each objective in each population
		double[][] bestObjectives =  new double[pops][];
		Genotype[][] bestGenotypes = new Genotype[pops][];
		Score[][] bestScores = new Score[pops][];

		// Go through each population
		for(int j = 0; j < pops; j ++){
			bestObjectives[j] = new double[objectivesPerPopulation()[j]];
			bestGenotypes[j] = new Genotype[bestObjectives[j].length];
			bestScores[j] = new Score[bestObjectives[j].length]; 
		}

		for (int i = 0; i < totalEvals; i++) {
			// Create team
			Genotype[] team = getTeam(populations, teamOrder, i);
			// Visualize (conditional)
			DrawingPanel[] panels = drawNetworks(team);
			// Evaluate
			ArrayList<Score> scores = evaluate(team);
			// Show/track performance (conditional)
			trackingAndLogging(team, scores);
			disposePanels(panels);
			// Distribute scores appropriately
			addScores(rawScores, teamOrder, i, scores);

			// Best in each objective in this population
			// Go through this population
			for (int k = 0; k < pops; k++) {
				//go through each objective for this population for this agent
				for (int l = 0; l < bestObjectives[k].length; l++) {
					//get the score of each objective for this agent
					double objectiveScore = scores.get(k).scores[l];
					// j == 0 saves first member of the population as the tentative best until a better individual is found
					if (i == 0 || objectiveScore >= bestObjectives[k][l]) {
						// update best individual in objective j
						bestGenotypes[k][l] = scores.get(k).individual;
						bestObjectives[k][l] = objectiveScore;
						bestScores[k][l] = scores.get(k);
					}
				}
			}
		}
		System.out.println(totalEvals + " evaluations conducted");
		
		if (CommonConstants.netio) {
			// Go through each population (for saving best objectives and genotypes of each population)
			for(int i = 0; i < pops; i++){
				//save the best in each objective for this population (will happen for each population)
				int currentGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
				String filePrefix = "gen" + currentGen + "_";
				// Save best in each objective
				String bestDir = FileUtilities.getSaveDirectory() + "/pop" + i + "_bestObjectives";
				File dir = new File(bestDir);
				// Delete old contents/team
				if (dir.exists() && !Parameters.parameters.booleanParameter("saveAllChampions")) {
					FileUtilities.deleteDirectoryContents(dir);
				} else {
					dir.mkdir();
				}
				// save all of the best objectives for this population
				for (int j = 0; j < bestObjectives[i].length; j++) {
					Serialization.save(bestGenotypes[i][j], bestDir + "/" + filePrefix + "bestIn" + j);
					FileUtilities.simpleFileWrite(bestDir + "/" + filePrefix + "score" + j + ".txt", bestScores[i][j].toString());
				}
			}
		}
		// re-package scores properly
		return wrapUpScores(rawScores, populations, teamOrder);
	}

	/**
	 * One genotype for each member of the team, and one score for each member
	 * as well
	 *
	 * @param team
	 *            vector of the genotypes of the teammates
	 * @return vector of scores to assign to each teammate
	 */
	@SuppressWarnings("rawtypes")  // because each population can have a different genotype
	public abstract ArrayList<Score> evaluate(Genotype[] team);

	/**
	 * Get the index-th team to evaluate according to the joinOrder.
	 *
	 * @param populations
	 *            all subpops
	 * @param joinOrder
	 *            specifies indices in each population to join to make teams
	 * @param index
	 *            in joinOrder
	 * @return team to evaluate
	 */
	@SuppressWarnings("rawtypes")  // because each population can have a different type
	protected Genotype[] getTeam(ArrayList<ArrayList<Genotype>> populations, List<ArrayList<Integer>> joinOrder, int index) {
		int pops = populations.size();
		Genotype[] team = new Genotype[pops];
		for (int p = 0; p < pops; p++) {
			team[p] = populations.get(p).get(joinOrder.get(p).get(index));
		}
		return team;
	}

	/**
	 * Print, display and/or write to file information about the most recent
	 * team's performance in its evaluation.
	 *
	 * @param team
	 *            team that was just evaluated
	 * @param scores
	 *            scores of evaluated team
	 */
	@SuppressWarnings("rawtypes")  // because each population can have a different type
	protected void trackingAndLogging(Genotype[] team, ArrayList<Score> scores) {
		// Team tracking
		long[] ids = new long[team.length];
		for (int q = 0; q < team.length; q++) {
			ids[q] = team[q].getId();
		}
		if (CommonConstants.showNetworks) {
			System.out.println(Arrays.toString(ids) + ": scores: " + scores);
		}
		if (teamLog != null) {
			teamLog.log(Arrays.toString(ids) + ": scores: " + scores);
		}

	}

	/**
	 * Clean up drawing panels
	 *
	 * @param panels
	 */
	public static void disposePanels(DrawingPanel[] panels) {
		if (panels != null) {
			for (int p = 0; p < panels.length; p++) {
				if (panels[p] != null) {
					panels[p].dispose();
					panels[p] = null;
				}
			}
		}
	}

	/**
	 * Draw the TWEANN members of the team currently being evaluated.
	 *
	 * @param team
	 *            Genotypes of currently evaluated team
	 * @return drawing panels showing the drawn networks
	 */
	@SuppressWarnings("rawtypes")
	public static DrawingPanel[] drawNetworks(Genotype[] team) {
		DrawingPanel[] panels = null;
		if (CommonConstants.showNetworks) {
			panels = new DrawingPanel[team.length];
			for (int p = 0; p < team.length; p++) {
				if (team[p] instanceof TWEANNGenotype) {
					panels[p] = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Evolved Network " + p);
					panels[p].setLocation(CommonTaskUtil.NETWORK_WINDOW_OFFSET, p * (TWEANN.NETWORK_VIEW_DIM + 20));
					((TWEANNGenotype) team[p]).getPhenotype().draw(panels[p]);
				}
			}
		}
		if (CommonConstants.monitorInputs && !(MMNEAT.task instanceof GroupTorusPredPreyTask)) {
			Offspring.fillInputs((TWEANNGenotype) team[0]);
		}
		return panels;
	}

	/**
	 * Wrap up the data in rawScores into ArrayLists so that they are in a form
	 * expected by the Evolutionary Algorithm. At this phase, null Scores
	 * (individuals that were never evaluated ... only happens with blueprints)
	 * get transformed into zero values (Which means this only works if all
	 * fitness functions are non-negative ... a problem?).
	 *
	 * @param rawScores
	 *            scores from all evals performed
	 * @param populations
	 *            all subpopulations of genotypes
	 * @param teamOrder
	 *            eval order of populations members
	 * @return ArrayList with data from rawScores, but nulls are now zero, and
	 *         each Score has the appropriate Genotype.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" }) // because each population can have a different type
	protected ArrayList<ArrayList<Score>> wrapUpScores(Score[][] rawScores, ArrayList<ArrayList<Genotype>> populations, List<ArrayList<Integer>> teamOrder) {
		// Tracking data that matters when using blueprints
		previousUnevaluatedIndividuals = unevaluatedIndividuals;
		unevaluatedIndividuals = 0;

		int pops = populations.size();
		int popSize = populations.get(0).size();
		// Put sums into proper score objects
		ArrayList<ArrayList<Score>> finalScores = new ArrayList<ArrayList<Score>>(pops);
		for (int p = 0; p < pops; p++) {
			ArrayList<Score> popScores = new ArrayList<Score>(popSize);
			for (int i = 0; i < popSize; i++) {
				if (rawScores[p][i] == null) {
					unevaluatedIndividuals++;
				}
				Score score = rawScores[p][i] == null ? new Score(null, new double[objectivesPerPopulation()[p]], null, new double[otherStatsPerPopulation()[p]], 0) : rawScores[p][i];
				double[] allScores = score.scores;
				double[] allOtherStats = score.otherStats;
				popScores.add(new Score(
						populations.get(p).get(i), // The genotype
						allScores, // average of raw scores from each team eval
						null, // Ignore Behavioral Diversity implementation for now
						allOtherStats, // average of other stats
						score.evals)); // number of evals
			}
			finalScores.add(popScores);
		}
		return finalScores;
	}

	/**
	 * Scores for each team member are the max across evals, because team
	 * members should only be rated based on the best team they contribute to
	 * ... no punishment for being roped into a crappy team, unless the
	 * bestTeamScore option is false. In this case, scores are averaged.
	 *
	 * @param rawScores
	 *            accumulated scores from evals so far
	 * @param teamOrder
	 *            order of evaluation
	 * @param order
	 *            index in teamOrder
	 * @param scores
	 *            new scores to be added to rawScores
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) // because each population can have a different type
	protected void addScores(Score[][] rawScores, List<ArrayList<Integer>> teamOrder, int order, ArrayList<Score> scores) {
		for (int p = 0; p < scores.size(); p++) {
			int orderIndex = teamOrder.get(p).get(order);
			Score score = scores.get(p);
			rawScores[p][orderIndex] = (bestTeamScore ? score.maxScores(rawScores[p][orderIndex]) : score.incrementalAverage(rawScores[p][orderIndex]));
		}
	}
        
        /**
	 * This is required by the Task interface, but is replaced by
         * objectivesPerPopulation() for tasks with multiple populations.
	 */
        @Override
	public int numObjectives() {
                // I would like to make this final, but some cooperative Ms Pac-Man tasks actually use
                // this method because they are both cooperative AND implement the SinglePopulationTask
                // interface, which is pretty weird, but makes sense given how the "teams" are constructed.
		throw new UnsupportedOperationException("Each population has its own number of objectives");
	}

        /**
         * Does not make sense for the same reason. Each population has
         * several min scores.
         * This is why it cannot be overridden again.
         */
	@Override
	public double[] minScores() {
		throw new UnsupportedOperationException("Does not make sense in the context of a cooperative task with multiple populations");
	}


}
