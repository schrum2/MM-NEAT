package edu.utexas.cs.nn.evolution.mulambda;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.log.FitnessLog;
import edu.utexas.cs.nn.log.PlotLog;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.multitask.DangerousAreaModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.scent.VariableDirectionKStepDeathScentBlock;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MuLambda<T> implements SinglePopulationGenerationalEA<T> {

    public static final int MLTYPE_PLUS = 0;
    public static final int MLTYPE_COMMA = 1;
    private int mltype;
    public int mu;
    public int lambda;
    public SinglePopulationTask<T> task;
    public int generation;
    protected FitnessLog<T> parentLog;
    protected FitnessLog<T> childLog;
    protected PlotLog modeLog;
    protected boolean writeOutput;
    private final int MAX_MODE_OF_LOG_INTEREST = 5;
    public boolean evaluatingParents = false;

    public MuLambda(int mltype, SinglePopulationTask<T> task, int mu, int lambda, boolean io) {
        this.mltype = mltype;
        this.task = task;
        this.mu = mu;
        this.lambda = lambda;
        this.generation = Parameters.parameters.integerParameter("lastSavedGeneration");
        writeOutput = Parameters.parameters.booleanParameter("io");

        if (writeOutput && io) {
            parentLog = new FitnessLog<T>("parents");
            childLog = new FitnessLog<T>("child");
            if (task instanceof MsPacManTask
                    && (MMNEAT.modesToTrack > 1
                    || TWEANN.preferenceNeuron()
                    || Parameters.parameters.booleanParameter("ensembleModeMutation"))) {
                ArrayList<String> labels = new ArrayList<String>();
                labels.add("Max Modes of Best");
                labels.add("Min Modes of Best");
                labels.add("Avg Modes of Best");
                labels.add("Max Modes of Worst");
                labels.add("Min Modes of Worst");
                labels.add("Avg Modes of Worst");
                int i;
                for (i = 1; i <= MAX_MODE_OF_LOG_INTEREST; i++) {
                    labels.add(i + " Mode Best");
                }
                labels.add("More Than " + i + " Modes Best");
                modeLog = new PlotLog("ModeUsage", labels);
            }
        }
    }

    public Task getTask() {
        return task;
    }

    public int currentGeneration() {
        return generation;
    }

    /**
     * The initial parent population is of size mu, and is randomly generated
     * based on an example genotype.
     *
     * @param example = used to generate random genotypes
     * @return = the initial parent population
     */
    public ArrayList<Genotype<T>> initialPopulation(Genotype<T> example) {
        return initialPopulation(example, mu);
    }

    public static <T> ArrayList<Genotype<T>> initialPopulation(Genotype<T> example, int mu) {
        ArrayList<Genotype<T>> parents = new ArrayList<Genotype<T>>(mu);
        if (MMNEAT.seedExample) {
            for (int i = 0; i < mu; i++) {
                // Exact copies of seed network
                parents.add(example.copy());
            }
        } else {
            for (int i = 0; i < mu; i++) {
                parents.add(example.newInstance());
            }
        }
        return parents;
    }

    public void logParentInfo(ArrayList<Score<T>> parentScores) {
        if (writeOutput) {
            parentLog.log(parentScores, generation);
            Genotype example = parentScores.get(0).individual;
            if (example instanceof TWEANNGenotype) {
                ArrayList<TWEANNGenotype> tweanns = new ArrayList<TWEANNGenotype>(parentScores.size());
                for (Score<T> g : parentScores) {
                    tweanns.add((TWEANNGenotype) g.individual);
                }
                EvolutionaryHistory.logTWEANNData(tweanns, generation);
                if (modeLog != null) {
                    modeLogging(parentScores);
                }
            }
        }
    }

    private void modeLogging(ArrayList<Score<T>> parentScores) {
        // Find out how many modes the best and worst nets had
        double[] modeBests = new double[MAX_MODE_OF_LOG_INTEREST + 1];
        double max = 0;
        double min = Double.MAX_VALUE;
        int minMaxModes = 0;
        int minMinModes = Integer.MAX_VALUE;
        int maxMaxModes = 0;
        int maxMinModes = Integer.MAX_VALUE;
        ArrayList<Double> modeCountsOfBest = new ArrayList<Double>();
        ArrayList<Double> modeCountsOfWorst = new ArrayList<Double>();
        for (Score<T> g : parentScores) {
            double score = g.otherStats[0]; // Game score should always be first "other" stat
            int modes = ((TWEANNGenotype) g.individual).numModes;
            if (modes <= MAX_MODE_OF_LOG_INTEREST) {
                modeBests[modes - 1] = Math.max(modeBests[modes - 1], score);
            } else {
                modeBests[MAX_MODE_OF_LOG_INTEREST] = Math.max(modeBests[MAX_MODE_OF_LOG_INTEREST], score);
            }
            if (score == max) {
                modeCountsOfBest.add((double) modes);
                maxMaxModes = Math.max(maxMaxModes, modes);
                maxMinModes = Math.min(maxMinModes, modes);
            } else if (score > max) {
                modeCountsOfBest = new ArrayList<Double>();
                modeCountsOfBest.add((double) modes);
                maxMaxModes = modes;
                maxMinModes = modes;
            }
            max = Math.max(score, max);

            if (score == min) {
                modeCountsOfWorst.add((double) modes);
                minMaxModes = Math.max(minMaxModes, modes);
                minMinModes = Math.min(minMinModes, modes);
            } else if (score < min) {
                modeCountsOfWorst = new ArrayList<Double>();
                modeCountsOfWorst.add((double) modes);
                minMaxModes = modes;
                minMinModes = modes;
            }
            min = Math.min(score, min);
        }
        //System.out.println("modeCountsOfBest:"+modeCountsOfBest);
        double maxAvgModes = StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(modeCountsOfBest));
        double minAvgModes = StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(modeCountsOfWorst));
        //System.out.println("avgModes:"+avgModes);
        // Log the data
        ArrayList<Double> logValues = new ArrayList<Double>(8);
        logValues.add((double) maxMaxModes);
        logValues.add((double) maxMinModes);
        logValues.add(maxAvgModes);
        logValues.add((double) minMaxModes);
        logValues.add((double) minMinModes);
        logValues.add(minAvgModes);
        // <= because the last slot is for that many modes or more
        for (int i = 0; i <= MAX_MODE_OF_LOG_INTEREST; i++) {
            logValues.add(modeBests[i]);
        }
        modeLog.log(generation, logValues);
    }

    public ArrayList<Score<T>> processChildren(ArrayList<Score<T>> parentScores) {
        //System.out.println("processChildren");
        ArrayList<Genotype<T>> children = 
                performDeltaCoding(generation) ? 
                PopulationUtil.getBestAndDeltaCode(parentScores): 
                generateChildren(lambda, parentScores);
        //System.out.println("Eval children");
        ArrayList<Score<T>> childrenScores = task.evaluateAll(children);
        //System.out.println("Done children");
        if (writeOutput) {
            childLog.log(childrenScores, generation);
        }
        return childrenScores;
    }

    public static boolean performDeltaCoding(int generation) {
        boolean periodicDeltaCoding = Parameters.parameters.booleanParameter("periodicDeltaCoding");
        boolean result = 
                (periodicDeltaCoding
                && generation != 0
                && generation % Parameters.parameters.integerParameter("deltaCodingFrequency") == 0);
        if(result) {
            System.out.println("Delta code children");
        }
        return result;
    }

    public ArrayList<Genotype<T>> selectAndAdvance(ArrayList<Score<T>> parentScores, ArrayList<Score<T>> childrenScores) {
        ArrayList<Score<T>> population = prepareSourcePopulation(parentScores, childrenScores);
        ArrayList<Genotype<T>> newParents = selection(mu, population);
        EvolutionaryHistory.logMutationData("---Gen " + generation + " Over-----------------");
        EvolutionaryHistory.logLineageData("---Gen " + generation + " Over-----------------");
        generation++;
        EvolutionaryHistory.frozenPreferenceVsPolicyStatusUpdate(newParents, generation);
        CommonConstants.trialsByGenerationUpdate(generation);
        if (Parameters.parameters.booleanParameter("scalePillsByGen")) { // For pacman
            Parameters.parameters.setDouble("preEatenPillPercentage", 1.0 - ((generation * 1.0) / Parameters.parameters.integerParameter("maxGens")));
        }
        if (Parameters.parameters.booleanParameter("incrementallyDecreasingEdibleTime")) {
            MMNEAT.setEdibleTimeBasedOnGeneration(generation);
        }
        if (Parameters.parameters.booleanParameter("incrementallyDecreasingLairTime")) {
            MMNEAT.setLairTimeBasedOnGeneration(generation);
        }
        VariableDirectionKStepDeathScentBlock.updateScentMaps(); // For pacman 
        DangerousAreaModeSelector.updateScentMaps(); // For pacman
        return newParents;
    }

    public ArrayList<Score<T>> prepareSourcePopulation(ArrayList<Score<T>> parentScores, ArrayList<Score<T>> childrenScores) {
        return prepareSourcePopulation(parentScores, childrenScores, mltype);
    }

    public static <T> ArrayList<Score<T>> prepareSourcePopulation(ArrayList<Score<T>> parentScores, ArrayList<Score<T>> childrenScores, int mltype) {
        ArrayList<Score<T>> population = null;
        switch (mltype) {
            case MLTYPE_PLUS:
                population = parentScores;
                population.addAll(childrenScores);
                break;
            case MLTYPE_COMMA:
                population = childrenScores;
                break;
        }
        return population;
    }

    /**
     * Given the current parent population, return the next parent population
     *
     * @param parents = current parent population
     * @return = next parent population
     */
    public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> parents) {
        evaluatingParents = true;
        long start = System.currentTimeMillis();
        System.out.println("Eval parents: ");// + start);
        ArrayList<Score<T>> parentScores = task.evaluateAll(parents);
        long end = System.currentTimeMillis();
        System.out.println("Done parents: " + TimeUnit.MILLISECONDS.toMinutes(end - start) + " minutes");

        // Get some info about modes, if doing mode mutation
        if (TWEANN.preferenceNeuron()) {
            EvolutionaryHistory.maxModes = 0;
            EvolutionaryHistory.minModes = Integer.MAX_VALUE;

            if (parentScores.get(0).individual instanceof TWEANNGenotype) {
                for (Score<T> g : parentScores) {
                    TWEANNGenotype tg = (TWEANNGenotype) g.individual;
                    EvolutionaryHistory.maxModes = Math.max(tg.numModes, EvolutionaryHistory.maxModes);
                    EvolutionaryHistory.minModes = Math.min(tg.numModes, EvolutionaryHistory.minModes);
                }
            }
        }

        evaluatingParents = false;
        start = System.currentTimeMillis();
        System.out.println("Eval children: "); // + start);
        ArrayList<Score<T>> childrenScores = processChildren(parentScores);
        end = System.currentTimeMillis();
        System.out.println("Done children: " + TimeUnit.MILLISECONDS.toMinutes(end - start) + " minutes");

        // Parent logging occurs after child evals to decrease odds of logs getting out of sync.
        // This way, all logs are updated at once, along with the generation param being advanced.
        logParentInfo(parentScores);
        if (writeOutput) {
            ArrayList<Score<T>> combined = new ArrayList<Score<T>>(mu + lambda);
            combined.addAll(parentScores);
            combined.addAll(childrenScores);
            MMNEAT.logPerformanceInformation(combined, generation);
        }
        return selectAndAdvance(parentScores, childrenScores);
    }

    public void close(ArrayList<Genotype<T>> population) {
        ArrayList<Score<T>> parentScores = task.evaluateAll(population);
        logParentInfo(parentScores);
        if (writeOutput) {
            this.parentLog.close();
            this.childLog.close();
            if (modeLog != null) {
                this.modeLog.close();
            }
        }
    }

    /**
     * Because each parent is evaluated once, and each child is evaluated once
     *
     * @return number of evaluations per generation
     */
    public int evaluationsPerGeneration() {
        return mu + lambda;
    }

    public abstract ArrayList<Genotype<T>> generateChildren(int numChildren, ArrayList<Score<T>> parentScores);

    public abstract ArrayList<Genotype<T>> selection(int numParents, ArrayList<Score<T>> scores);
}
