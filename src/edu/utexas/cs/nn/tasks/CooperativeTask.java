package edu.utexas.cs.nn.tasks;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.evolution.mulambda.CooperativeCoevolutionMuLambda;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.log.MONELog;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.mspacman.CooperativeMsPacManTask;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import wox.serial.Easy;

/**
 * Task involving multiple individuals combined into a single team or organism
 * that is evaluated.
 *
 * @author He_Deceives
 */
public abstract class CooperativeTask implements MultiplePopulationTask {

    /**
     * Used by blueprint evolution *
     */
    protected int unevaluatedIndividuals = 0;
    protected int previousUnevaluatedIndividuals = 0;
    /**
     * Number of times each component has to be evaluated in a team
     */
    protected int teams;
    /**
     * Gets randomly shuffled. Contains indices of where to pick component team
     * members from for each team to be evaluated. This is an instance variable
     * so that it doesn't have to be recreated each time, merely reshuffled.
     */
    private ArrayList<ArrayList<Integer>> joinOrder = null;
    public MONELog teamLog;
    private final boolean bestTeamScore;

    public CooperativeTask() {
        this.teams = Parameters.parameters.integerParameter("teams");
        if (Parameters.parameters.booleanParameter("io") && Parameters.parameters.booleanParameter("teamLog")) {
            this.teamLog = new MONELog("Teams", true);
        }
        TWEANN.NETWORK_VIEW_DIM = 800 / 5;
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
     * @param populations different populations of genotypes, each the same size
     * @return score for each member of each population
     */
    public ArrayList<ArrayList<Score>> evaluateAllPopulations(ArrayList<ArrayList<Genotype>> populations) {
        int pops = populations.size();
        int popSize = populations.get(0).size();
        // initialize the join order if not previously done
        if (joinOrder == null) {
            joinOrder = new ArrayList<ArrayList<Integer>>(pops);
            for (int p = 0; p < pops; p++) {
                ArrayList<Integer> order = new ArrayList<Integer>(teams * popSize);
                for (int t = 0; t < teams; t++) {
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
     * Performs all cooperative evaluations on the collection of populations
     * using the designated join orders. Each vector in the joinOrder
     * corresponds to a population in populations. Each value in the sub-vectors
     * of joinOrder is the index of a specific individual in the corresponding
     * populations sub-vector.
     *
     * In other words, joinOrder contains parallel vectors of members to choose
     * from the parallel population arrays of populations.
     *
     * @param populations all populations (all are the same size)
     * @param teamOrder how to join the population members into teams to
     * evaluate
     * @return score for each individual of each population
     */
    public ArrayList<ArrayList<Score>> evaluateAllPopulations(ArrayList<ArrayList<Genotype>> populations, List<ArrayList<Integer>> teamOrder) {
        int pops = populations.size();
        int popSize = populations.get(0).size();

        // initialize score table (nulls)
        Score[][] rawScores = new Score[pops][popSize];
        // Evaluate all
        int totalEvals = teamOrder.get(0).size();
        int maxPacManScore = 0;
        Score bestScoreSet = null;
        Genotype[] bestPacManTeam = null;
        boolean trackBestPacManScore = this instanceof CooperativeMsPacManTask
                && MMNEAT.ea instanceof CooperativeCoevolutionMuLambda
                && ((CooperativeCoevolutionMuLambda) MMNEAT.ea).evaluatingParents;
        for (int i = 0; i < totalEvals; i++) {
            // Create team
            Genotype[] team = getTeam(populations, teamOrder, i);
            // Visualize (conditional)
            DrawingPanel[] panels = drawNetworks(team);
            // Evaluate
            ArrayList<Score> scores = evaluate(team);
            // Track the best ms pacman team in each generation
            if (trackBestPacManScore) {
                Score firstScoreSet = scores.get(0);
                int gameScore = (int) firstScoreSet.otherStats[0]; // Game Score is always first
                if (gameScore >= maxPacManScore) {
                    bestPacManTeam = team;
                    maxPacManScore = gameScore;
                    bestScoreSet = firstScoreSet;
                }
            }
            // Show/track performance (conditional)
            trackingAndLogging(team, scores);
            disposePanels(panels);
            // Distribute scores appropriately
            addScores(rawScores, teamOrder, i, scores);
        }
        if (bestPacManTeam != null) {
            // Save best pacman team
            String teamDir = FileUtilities.getSaveDirectory() + "/bestTeam";
            File bestDir = new File(teamDir);
            // Delete old contents/team
            if (bestDir.exists()) {
                FileUtilities.deleteDirectoryContents(bestDir);
            } else {
                bestDir.mkdir();
            }
            for (int i = 0; i < bestPacManTeam.length; i++) {
                Easy.save(bestPacManTeam[i], teamDir + "/teamMember" + i + ".xml");
            }
            System.out.println("Saved best team with score of " + maxPacManScore);
            FileUtilities.simpleFileWrite(teamDir + "/score.txt", bestScoreSet.toString());
        }
        // re-package scores properly
        return wrapUpScores(rawScores, populations, teamOrder);
    }

    /**
     * One genotype for each member of the team, and one score for each member
     * as well
     *
     * @param team vector of the genotypes of the teammates
     * @return vector of scores to assign to each teammate
     */
    public abstract ArrayList<Score> evaluate(Genotype[] team);

    /*
     * Default objective mins of 0.
     */
    public double[] minScores() {
        return new double[this.numObjectives()];
    }

    /**
     * Get the indexth team to evaluate according to the joinOrder.
     *
     * @param populations all subpops
     * @param joinOrder specifies indices in each population to join to make
     * teams
     * @param index in joinOrder
     * @return team to evaluate
     */
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
     * @param team team that was just evaluated
     * @param scores scores of evaluated team
     */
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
     * @param team Genotypes of currently evaluated team
     * @return drawing panels showing the drawn networks
     */
    public static DrawingPanel[] drawNetworks(Genotype[] team) {
        DrawingPanel[] panels = null;
        if (CommonConstants.showNetworks) {
            panels = new DrawingPanel[team.length];
            for (int p = 0; p < team.length; p++) {
                if (team[p] instanceof TWEANNGenotype) {
                    panels[p] = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Evolving Network " + p);
                    panels[p].setLocation(LonerTask.NETWORK_WINDOW_OFFSET, p * (TWEANN.NETWORK_VIEW_DIM + 20));
                    ((TWEANNGenotype) team[p]).getPhenotype().draw(panels[p]);
                }
            }
        }
        if (CommonConstants.monitorInputs) {
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
     * @param rawScores scores from all evals performed
     * @param populations all subpopulations of genotypes
     * @param teamOrder eval order of populations members
     * @return ArrayList with data from rawScores, but nulls are now zero, and
     * each Score has the appropriate Genotype.
     */
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
                Score score = rawScores[p][i] == null
                        ? new Score(null, new double[objectivesPerPopulation()[p]], null, new double[otherStatsPerPopulation()[p]], 0)
                        : rawScores[p][i];
                double[] allScores = score.scores;
                double[] allOtherStats = score.otherStats;
                popScores.add(
                        new Score(populations.get(p).get(i), // The genotype
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
     * ... no punishment for being roped into a crappy team.
     *
     * @param rawScores accumulated max scores from evals so far
     * @param teamOrder order of evaluation
     * @param order index in teamOrder
     * @param scores new scores to be added to rawScores
     */
    protected void addScores(Score[][] rawScores, List<ArrayList<Integer>> teamOrder, int order, ArrayList<Score> scores) {
        for (int p = 0; p < scores.size(); p++) {
            int orderIndex = teamOrder.get(p).get(order);
            Score score = scores.get(p);
            rawScores[p][orderIndex] = (bestTeamScore ? score.maxScores(rawScores[p][orderIndex]) : score.incrementalAverage(rawScores[p][orderIndex]));
        }
    }
}
