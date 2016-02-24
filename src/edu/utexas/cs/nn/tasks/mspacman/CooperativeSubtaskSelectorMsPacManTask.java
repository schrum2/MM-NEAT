package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment.FitnessToModeMap;
import edu.utexas.cs.nn.util.ClassCreation;
import java.util.ArrayList;

/**
 * Hierarchical approach in which one selector network population evolves on top
 * of several subnetwork populations. The selector doesn't know what the
 * subnetworks are doing, but picks one to control pacman at each time step.
 *
 * @author Jacob Schrum
 */
public class CooperativeSubtaskSelectorMsPacManTask<T extends Network> extends CooperativeMsPacManTask<T> {

    /**
     * Had to set up a weird method of passing the subnetworks to the pacman
     * controller via these static fields
     */
    public static Genotype[] subNetworks = null;
    private final int[] fitnessPreferences;

    public CooperativeSubtaskSelectorMsPacManTask() {
        super();
        Parameters.parameters.setBoolean("evolveNetworkSelector", true);
        FitnessToModeMap fitnessMap = null;
        try {
            fitnessMap = (FitnessToModeMap) ClassCreation.createObject("pacmanFitnessModeMap");
        } catch (NoSuchMethodException ex) {
            System.out.println("FitnessToModeMap failed loading");
            System.exit(1);
        }
        fitnessPreferences = fitnessMap.associatedFitnessScores();
    }

    @Override
    public ArrayList<Score> evaluate(Genotype[] team) {
        // Not sure if this will ever work for non-TWEANN networks, but I wanted to leave that option open
        Genotype<T> selectorNetwork = team[0];
        subNetworks = new Genotype[MMNEAT.modesToTrack];
        for (int i = 0; i < MMNEAT.modesToTrack; i++) {
            subNetworks[i] = team[i + 1];
        }

        if (task.printFitness) {
            System.out.print("IDs");
            for (int i = 0; i < team.length; i++) {
                System.out.print(":" + team[i].getId());
            }
            System.out.println();
        }

        // Evaluate
        Score<T> taskScores = task.evaluate(selectorNetwork);
        // Redistribute scores to each genotype
        Score[] scores = new Score[MMNEAT.modesToTrack];
        for (int i = 0; i < MMNEAT.modesToTrack; i++) {
            scores[i] = new Score<T>(subNetworks[i],
                    task.fitnessArray(fitnessPreferences[i], taskScores),
                    null, new double[]{});
        }
        subNetworks = null;

        ArrayList<Score> genotypeScores = new ArrayList<Score>(MMNEAT.modesToTrack + 2);
        genotypeScores.add(taskScores);
        for (int i = 0; i < MMNEAT.modesToTrack; i++) {
            genotypeScores.add(scores[i]);
        }

        return genotypeScores;
    }

    /**
     * Selector net, each subtask net
     *
     * @return
     */
    public int numberOfPopulations() {
        return MMNEAT.modesToTrack + 1;
    }

    /**
     * WARNING! This under estimates the number of fitness values for each
     * evolving component, but this function is really only used by blueprints,
     * so maybe this is not a problem? Be careful if using for anything new.
     *
     * @return
     */
    public int[] objectivesPerPopulation() {
        int[] result = new int[numberOfPopulations()];
        result[0] = task.objectives.size();
        for (int i = 1; i < result.length; i++) {
            result[i] = 1;
        }
        return result;
    }

    public int[] otherStatsPerPopulation() {
        int[] result = new int[numberOfPopulations()];
        result[0] = task.otherScores.size();
        for (int i = 1; i < result.length; i++) {
            result[i] = 0;
        }
        return result;
    }
}
