package edu.utexas.cs.nn.evolution.nsga2;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.mulambda.MuPlusLambda;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Better;
import edu.utexas.cs.nn.scores.MultiObjectiveScore;
import edu.utexas.cs.nn.scores.ObjectiveComparator;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Implementation of Deb's NSGA2 multiobjective EA.
 */
public class NSGA2<T> extends MuPlusLambda<T> {
    
    protected boolean mating;
    protected double crossoverRate;
    
    public NSGA2() {
        this(Parameters.parameters.booleanParameter("io"));
    }
    
    public NSGA2(boolean io) {
        this((SinglePopulationTask<T>) MMNEAT.task, Parameters.parameters.integerParameter("mu"), io);
    }
    
    public NSGA2(SinglePopulationTask<T> task, int mu, boolean io) {
        super(task, mu, mu, io);
        mating = Parameters.parameters.booleanParameter("mating");
        crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
    }
    
    @Override
    public ArrayList<Genotype<T>> generateChildren(int numChildren, ArrayList<Score<T>> parentScores) {
        NSGA2Score<T>[] scoresArray = getNSGA2Scores(parentScores);
        return generateNSGA2Children(numChildren, scoresArray, currentGeneration(), mating, crossoverRate);
    }
    
    public static <T> ArrayList<Genotype<T>> generateNSGA2Children(int numChildren, NSGA2Score<T>[] scoresArray, int generation, boolean mating, double crossoverRate) {
        assignCrowdingDistance(scoresArray);
        fastNonDominatedSort(scoresArray);
        
        ArrayList<Genotype<T>> offspring = new ArrayList<Genotype<T>>(numChildren);
        Better<NSGA2Score> judge;
        if (generation == 0) {
            judge = new Domination();
        } else {
            judge = new ParentComparator();
        }
        
        for (int i = 0; i < numChildren; i++) {
            int e1 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);
            int e2 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);
            
            NSGA2Score better = judge.better(scoresArray[e1], scoresArray[e2]);
            Genotype<T> source = better.individual;
            long parentId1 = source.getId();
            long parentId2 = -1;
            Genotype<T> e = source.copy();

            // This restriction on mutation and crossover only makes sense when using
            // pacman coevolution with a fitness/population for each individual level
            if (!CommonConstants.requireFitnessDifferenceForChange
                    || better.scores[0] > 0) { // If neither net has reached a given level, the scores of 0 will prevent evolution
//                System.out.println("Progressing with mutation on: " + better);

                if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
                    e1 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);
                    e2 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);
                    
                    Genotype<T> otherSource = judge.better(scoresArray[e1], scoresArray[e2]).individual;
                    parentId2 = otherSource.getId();
                    Genotype<T> otherOffspring;
                    
                    if (CommonConstants.cullCrossovers) {
                        ArrayList<Score<T>> litter = new ArrayList<Score<T>>(CommonConstants.litterSize);
                        // Fill litter
                        while (litter.size() < CommonConstants.litterSize) {
                            // Try crossover
                            Genotype<T> candidate1 = e.copy(); // Will be a candidate once crossover modifies it
                            Genotype<T> other = otherSource.copy();
                            //System.out.println(i + ":Litter Crossover");
                            Genotype<T> candidate2 = candidate1.crossover(other);
                            // Evaluate and add to litter
                            Pair<double[], double[]> score = ((NoisyLonerTask<T>) MMNEAT.task).oneEval(candidate1, 0);
                            MultiObjectiveScore<T> s = new MultiObjectiveScore<T>(candidate1, score.t1, null, score.t2);
                            litter.add(s);
                            
                            if (litter.size() < CommonConstants.litterSize) {
                                score = ((NoisyLonerTask<T>) MMNEAT.task).oneEval(candidate2, 0);
                                s = new MultiObjectiveScore<T>(candidate2, score.t1, null, score.t2);
                                litter.add(s);
                            }
                        }
                        // Cull litter
                        ArrayList<Genotype<T>> keepers = staticSelection(2, staticNSGA2Scores(litter));
                        // Best two of litter
                        e = keepers.get(0);
                        otherOffspring = keepers.get(1);
                    } else {
                        Genotype<T> other = otherSource.copy();
                        otherOffspring = e.crossover(other);
                    }
                    i++;
                    /*
                     * The offspring e will be added no matter what. Because i
                     * is increased and then checked, otherOffspring will NOT
                     * always be added.
                     */
                    if (i < numChildren) {
                        otherOffspring.mutate();
                        offspring.add(otherOffspring);
                        EvolutionaryHistory.logLineageData(parentId1 + " X " + parentId2 + " -> " + otherOffspring.getId());
                    }
                }
                
                e.mutate();
            }
//            else {
//                System.out.println("No mutation on " + better);
//            }

            offspring.add(e);
            if (parentId2 == -1) {
                EvolutionaryHistory.logLineageData(parentId1 + " -> " + e.getId());
            } else {
                EvolutionaryHistory.logLineageData(parentId1 + " X " + parentId2 + " -> " + e.getId());
            }
        }
        return offspring;
    }
    
    public NSGA2Score<T>[] getNSGA2Scores(ArrayList<Score<T>> scores) {
        return staticNSGA2Scores(scores);
    }
    
    public static <T> NSGA2Score<T>[] staticNSGA2Scores(ArrayList<Score<T>> scores) {
        NSGA2Score<T>[] scoresArray = new NSGA2Score[scores.size()];
        for (int i = 0; i < scores.size(); i++) {
            scoresArray[i] = new NSGA2Score<T>(scores.get(i));
        }
        return scoresArray;
    }
    
    @Override
    public ArrayList<Genotype<T>> selection(int numParents, ArrayList<Score<T>> scores) {
        return staticSelection(numParents, staticNSGA2Scores(scores));
    }
    
    public static <T> ArrayList<Genotype<T>> staticSelection(int numParents, NSGA2Score<T>[] scoresArray) {
        //NSGA2Score<T>[] scoresArray = staticNSGA2Scores(scores);
        assignCrowdingDistance(scoresArray);
        ArrayList<ArrayList<NSGA2Score<T>>> fronts = fastNonDominatedSort(scoresArray);
        
        ArrayList<Genotype<T>> newParents = new ArrayList<Genotype<T>>(numParents);
        int numAdded = 0;
        int currentFront = 0;
        
        while (numAdded < numParents) {
            ArrayList<NSGA2Score<T>> front = fronts.get(currentFront);
            if (front.size() <= (numParents - numAdded)) {
                for (int i = 0; i < front.size(); i++) {
                    newParents.add(front.get(i).individual);
                    numAdded++;
                }
            } else {
                NSGA2Score<T>[] lastFront = front.toArray(new NSGA2Score[front.size()]);
                Arrays.sort(lastFront, new CrowdingDistanceComparator());
                int index = lastFront.length - 1;
                while (numAdded < numParents) {
                    newParents.add(lastFront[index--].individual);
                    numAdded++;
                }
            }
            currentFront++;
        }
        return newParents;
    }
    
    private static void assignCrowdingDistance(NSGA2Score[] scores) {
        // reset distances
        for (int i = 0; i < scores.length; i++) {
            scores[i].setCrowdingDistance(0);
        }
        
        int numObjectives = scores[0].numObjectives();
        
        for (int j = 0; j < numObjectives; j++) {
            if (scores[0].useObjective(j)) {
                Arrays.sort(scores, new ObjectiveComparator(j));
                
                scores[0].setCrowdingDistance(Float.POSITIVE_INFINITY);
                scores[scores.length - 1].setCrowdingDistance(Float.POSITIVE_INFINITY);
                
                double min = scores[0].objectiveScore(j);
                double max = scores[scores.length - 1].objectiveScore(j);

                // Just leave all crowding distances at 0 if all objective scores are the same
                if (max - min > 0) {
                    for (int k = 1; k < scores.length - 1; k++) {
                        scores[k].crowdingDistance += Math.abs(scores[k + 1].objectiveScore(j) - scores[k - 1].objectiveScore(j)) / (max - min);
                    }
                }
            }
        }
    }
    
    private static <T> ArrayList<ArrayList<NSGA2Score<T>>> fastNonDominatedSort(NSGA2Score<T>[] scores) {
        
        for (int i = 0; i < scores.length; i++) {
            assert scores[i] != null : "Score is null! " + i;
            scores[i].reset();
        }
        
        for (int i = 0; i < scores.length; i++) {
            NSGA2Score<T> p = scores[i];
            for (int j = 0; j < scores.length; j++) {
                if (i != j) {
                    NSGA2Score<T> q = scores[j];
                    if (p.isBetter(q)) {
                        p.addDominatedIndividual(q);
                        q.increaseNumDominators();
                    }
                }
            }
        }
        
        int numAssigned = 0;
        int currentFront = 0;
        ArrayList<ArrayList<NSGA2Score<T>>> frontSet = new ArrayList<ArrayList<NSGA2Score<T>>>(scores.length);
        
        while (numAssigned < scores.length) {
            // Although this sizing scheme will waste space, it will assure that no resize is ever needed
            frontSet.add(new ArrayList<NSGA2Score<T>>(scores.length - numAssigned));
            
            for (int i = 0; i < scores.length; i++) {
                if (!scores[i].isAssigned && scores[i].numDominators == 0) {
                    frontSet.get(currentFront).add(scores[i]);
                    scores[i].assign(currentFront);
                    numAssigned++;
                    //System.out.println("Front " + currentFront + ": " + scores[i]);
                }
            }
            
            for (int i = 0; i < scores.length; i++) {
                if (scores[i].isAssigned && !scores[i].processed) {
                    scores[i].process();
                }
            }
            
            currentFront++;
        }

//        System.out.print("Front sizes:");
//        for(int i = 0; i < frontSet.size(); i++){
//            System.out.print(frontSet.get(i).size() + " ");
//        }
//        System.out.println();

        return frontSet;
    }
    
    public static <T> ArrayList<NSGA2Score<T>> getParetoFront(NSGA2Score<T>[] scores) {
        return fastNonDominatedSort(scores).get(0);
    }
    
    public static <T> ArrayList<ArrayList<NSGA2Score<T>>> getParetoLayers(NSGA2Score<T>[] scores) {
        return fastNonDominatedSort(scores);
    }
    
    public static void main(String[] args) {
        args = new String[]{"runNumber:0", "trials:1", "mu:5", "io:false", "netio:false", "mating:true", "task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask", "ea:edu.utexas.cs.nn.evolution.nsga2.NSGA2", "pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.FullTaskMediator"};
        Parameters.initializeParameterCollections(args);
        MMNEAT.loadClasses();
        
        NSGA2 ea = (NSGA2) MMNEAT.ea;
        ArrayList<Score> scores = new ArrayList<Score>();
        // layer 0
        ArrayList<Long> layer0 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 5})), new double[]{1, 5}, null));
        layer0.add(scores.get(0).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{4, 4})), new double[]{4, 4}, null));
        layer0.add(scores.get(1).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{5, 1})), new double[]{5, 1}, null));
        layer0.add(scores.get(2).individual.getId());
        // layer 1
        ArrayList<Long> layer1 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 4})), new double[]{1, 4}, null));
        layer1.add(scores.get(3).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{3, 3})), new double[]{3, 3}, null));
        layer1.add(scores.get(4).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{4, 1})), new double[]{4, 1}, null));
        layer1.add(scores.get(5).individual.getId());
        // layer 2
        ArrayList<Long> layer2 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 3})), new double[]{1, 3}, null));
        layer2.add(scores.get(6).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{2, 2})), new double[]{2, 2}, null));
        layer2.add(scores.get(7).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{3, 1})), new double[]{3, 1}, null));
        layer2.add(scores.get(8).individual.getId());
        // layer 3
        ArrayList<Long> layer3 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 1})), new double[]{1, 1}, null));
        layer3.add(scores.get(9).individual.getId());
        
        Collections.shuffle(scores, RandomNumbers.randomGenerator);
        ArrayList<Genotype> result0 = ea.selection(3, scores);
        System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(PopulationUtil.addListGenotypeType(result0)), layer0) ? "PASSED 0" : "FAILED 0 " + layer0 + " AND " + result0);
        ArrayList<Genotype> result1 = ea.selection(6, scores);
        layer1.addAll(layer0);
        System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(PopulationUtil.addListGenotypeType(result1)), layer1) ? "PASSED 1" : "FAILED 1 " + layer1 + " AND " + result1);
        ArrayList<Genotype> result2 = ea.selection(9, scores);
        layer2.addAll(layer1);
        System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(PopulationUtil.addListGenotypeType(result2)), layer2) ? "PASSED 2" : "FAILED 2 " + layer2 + " AND " + result2);
    }
}
