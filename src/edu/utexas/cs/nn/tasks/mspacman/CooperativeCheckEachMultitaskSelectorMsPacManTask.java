package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 * Two networks are coevolved: One is a multitask network, the other is a preference network.
 * The preference network determines which multitask mode is used at each time step. 
 * Additionally, the networks are "check-each" networks, as far as sensor setup.
 *
 * @author Jacob Schrum
 */
public class CooperativeCheckEachMultitaskSelectorMsPacManTask<T extends Network> extends CooperativeMsPacManTask<T> {

    public static final int MULTITASK_INDEX = 0;
    public static final int PREFERENCE_INDEX = 1;
    /**
     * Pass multitask network via static field
     */
    public static Genotype multitaskGenotype = null;

    public CooperativeCheckEachMultitaskSelectorMsPacManTask() {
        super();
    }

    @Override
    public ArrayList<Score> evaluate(Genotype[] team) {
        multitaskGenotype = team[MULTITASK_INDEX];
        Genotype<T> preferenceGenotype = team[PREFERENCE_INDEX];
        
        if (task.printFitness) {
            System.out.print("IDs");
            for (int i = 0; i < team.length; i++) {
                System.out.print(":" + team[i].getId());
            }
            System.out.println();
        }

        // Evaluate
        Score<T> taskScores = task.evaluate(preferenceGenotype);
        // Multitask and preference nets get the same scores
        ArrayList<Score> genotypeScores = new ArrayList<Score>(numberOfPopulations());
        for (int i = 0; i < numberOfPopulations(); i++) {
            double[] scoresCopy = new double[taskScores.scores.length];
            System.arraycopy(taskScores.scores, 0, scoresCopy, 0, taskScores.scores.length);
            double[] otherStatsCopy = new double[taskScores.otherStats.length];
            System.arraycopy(taskScores.otherStats, 0, otherStatsCopy, 0, taskScores.otherStats.length);
            genotypeScores.add(new Score<T>(team[i], scoresCopy, null, otherStatsCopy));
        }
        multitaskGenotype = null;
        
        return genotypeScores;
    }

    /**
     * Multitask net and preference net
     * @return
     */
    public int numberOfPopulations() {
        return 2;
    }

    public int[] objectivesPerPopulation() {
        int[] result = new int[numberOfPopulations()];
        for (int i = 0; i < result.length; i++) {
            result[i] = task.objectives.size();
        }
        return result;
    }

    public int[] otherStatsPerPopulation() {
        int[] result = new int[numberOfPopulations()];
        for (int i = 0; i < result.length; i++) {
            result[i] = task.otherScores.size();
        }
        return result;
    }
}
