/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.nsga2.tug;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.log.MONELog;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Jacob Schrum
 */
public class CompareNSGA2ToTUG<T> extends TUGNSGA2<T> {

    public static final boolean GHOSTS_ONLY = true;
    MONELog compareLog;

    public CompareNSGA2ToTUG() {
        super();
        compareLog = new MONELog("CompareNSGA2ToTUG", true);
    }

    @Override
    public ArrayList<Genotype<T>> selection(int numParents, ArrayList<Score<T>> scores) {
        long seed = RandomNumbers.randomGenerator.nextLong();
        RandomNumbers.randomGenerator = new Random(seed);
        ArrayList<Genotype<T>> tugResult = super.selection(numParents, scores);

        // Temporary testing for how NSGA2 with just first objective behaves
        // Have to act as if ghost objective is only score, before crowding distance, etc. calculated
        if (GHOSTS_ONLY) {
            for (Score<T> s : scores) {
                s.dropLastScore(); // drops pill score
            }
        }
        NSGA2Score<T>[] staticScores = staticNSGA2Scores(scores);
        // Reset seed for other selection method
        RandomNumbers.randomGenerator = new Random(seed);
        ArrayList<Genotype<T>> nsga2Result = staticSelection(numParents, staticScores);

        logResultDifferences(tugResult,nsga2Result,GHOSTS_ONLY,staticScores,"Combined");
        
        return tugResult;
    }

    @Override
    public ArrayList<Genotype<T>> generateChildren(int numChildren, ArrayList<Score<T>> parentScores) {
        long seed = RandomNumbers.randomGenerator.nextLong();
        RandomNumbers.randomGenerator = new Random(seed);
        ArrayList<Genotype<T>> tugResult = super.generateChildren(numChildren, parentScores);

        // The generated children will have different ids, but the parent ids should
        // be the same in each case. The two groups are separated by this line.
        EvolutionaryHistory.logLineageData("---Line between comparisons----------------");
        
        ArrayList<Score<T>> copyScores = new ArrayList<Score<T>>();
        for (Score<T> s : parentScores) {
            double[] copyScoreArray = new double[s.scores.length];
            System.arraycopy(s.scores, 0, copyScoreArray, 0, copyScoreArray.length);
            double[] copyOtherArray = new double[s.otherStats.length];
            System.arraycopy(s.otherStats, 0, copyOtherArray, 0, copyOtherArray.length);
            copyScores.add(new Score<T>(s.individual, copyScoreArray, null, copyOtherArray));
        }

        // Temporary testing for how NSGA2 with just first objective behaves
        // Have to act as if ghost objective is only score, before crowding distance, etc. calculated
        if (GHOSTS_ONLY) {
            for (Score<T> s : copyScores) {
                s.dropLastScore(); // drops pill score
            }
        }
        NSGA2Score<T>[] scoresArray = staticNSGA2Scores(copyScores);
        // Reset seed for other selection method
        RandomNumbers.randomGenerator = new Random(seed);
        ArrayList<Genotype<T>> nsga2Result = generateNSGA2Children(numChildren, scoresArray, currentGeneration(), mating, crossoverRate);

        // Just realized, this comparison simply doesn't work for child generation
        // because each child has a newly generated id number. What matters here
        // is which parents are chosen to reproduce and mate
        //logResultDifferences(tugResult,nsga2Result,GHOSTS_ONLY,scoresArray,"Child");
        
        return tugResult;
    }

    private void logResultDifferences(ArrayList<Genotype<T>> tugResult, ArrayList<Genotype<T>> nsga2Result, boolean ghostOnly, NSGA2Score<T>[] staticScores, String stage) {
        Pair<ArrayList<Genotype<T>>, ArrayList<Genotype<T>>> result = PopulationUtil.populationDifferences(nsga2Result, tugResult);
        compareLog.log("--Generation " + this.generation + " "+stage+" Selection-----------------");
        compareLog.log("  In " + (ghostOnly ? "Ghost Selection" : "NSGA2") + " but not TUG:");
        for (Genotype<T> g : result.t1) {
            compareLog.log("\t" + g.getId() + ": " + PopulationUtil.scoreWithId(g.getId(), staticScores));
        }
        compareLog.log("  In TUG but not " + (ghostOnly ? "Ghost Selection" : "NSGA2") + ":");
        for (Genotype<T> g : result.t2) {
            compareLog.log("\t" + g.getId() + ": " + PopulationUtil.scoreWithId(g.getId(), staticScores));
        }
    }
}
