package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.MultiObjectiveScore;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.*;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.facades.PacManControllerFacade;
import edu.utexas.cs.nn.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.objectives.SpecificGhostScore;
import edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment.FitnessToModeMap;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.OneGhostAndPillsMonitorInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.OneGhostMonitorInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import pacman.controllers.NewPacManController;

/**
 * @author Jacob Schrum
 */
public abstract class CooperativeNonHierarchicalMultiNetMsPacManTask<T extends Network> extends CooperativeMsPacManTask<T> {

    protected int members;
    protected boolean ensemble;
    protected boolean checkEachDir;
    public FitnessToModeMap fitnessMap;
    public MsPacManControllerInputOutputMediator[] inputMediators;

    public CooperativeNonHierarchicalMultiNetMsPacManTask(int numMembers, boolean ensemble, String fitnessMapKey) {
        this(numMembers, ensemble, fitnessMapKey, null);
    }

    public CooperativeNonHierarchicalMultiNetMsPacManTask(int numMembers, boolean ensemble, String fitnessMapKey, MsPacManControllerInputOutputMediator[] mediators) {
        this(numMembers, ensemble, fitnessMapKey, false, mediators);
    }

    public CooperativeNonHierarchicalMultiNetMsPacManTask(int numMembers, boolean ensemble, String fitnessMapKey, boolean ghostMonitors, MsPacManControllerInputOutputMediator[] mediators) {
        super();
        this.members = numMembers;
        // Copy of same mediator for each mode
        try {
            fitnessMap = (FitnessToModeMap) ClassCreation.createObject(fitnessMapKey); // plain fitness map, or a multitask scheme
            if (members == -1) { // Not ensemble
                MsPacManModeSelector temp = (MsPacManModeSelector) fitnessMap;
                members = temp.numModes();
                MMNEAT.modesToTrack = temp.numModes();
            }
            if (mediators == null) {
                this.inputMediators = new MsPacManControllerInputOutputMediator[members];
                //System.out.println("Initializing sub-network input mediators");
                for (int i = 0; i < members; i++) {
                    if (ghostMonitors) {
                        inputMediators[i] = Parameters.parameters.booleanParameter("ghostMonitorsSensePills") ? new OneGhostAndPillsMonitorInputOutputMediator(i) : new OneGhostMonitorInputOutputMediator(i);
                    } else {
                        inputMediators[i] = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
                    }
                }
            } else {
                this.inputMediators = mediators;
            }
            checkEachDir = inputMediators[0] instanceof VariableDirectionBlockLoadedInputOutputMediator;
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            System.out.println("Could not load pacman input-output mediator or fitness map");
            System.exit(1);
        }
        this.ensemble = ensemble;
        if (ghostMonitors) {
            for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
                task.addObjective(new SpecificGhostScore<T>(i), task.otherScores, false);
            }
        }
    }

    @Override
    public ArrayList<Score> evaluate(Genotype[] team) {

        Genotype<T>[] subNets = new Genotype[team.length];
        for (int i = 0; i < team.length; i++) {
            subNets[i] = team[i];
        }

        if (task.printFitness) {
            System.out.print("IDs");
            for (int i = 0; i < team.length; i++) {
                System.out.print(":" + team[i].getId());
            }
            System.out.println();
        }

        NNPacManController multiController;
        if (ensemble) {
            multiController = new EnsembleMsPacManController(subNets, inputMediators);
        } else if (checkEachDir) {
            multiController = new CheckEachDirectionMultinetworkMsPacManController(team, inputMediators, (MsPacManModeSelector) fitnessMap);
        } else {
            multiController = new MultinetworkSelectorMsPacManController(subNets, inputMediators, (MsPacManModeSelector) fitnessMap);
        }
        // Evaluate
        Score<T> taskScores = evaluate(multiController);
        int[] fitnessPreferences = fitnessMap.associatedFitnessScores();

        ArrayList<Score> result = new ArrayList<Score>(team.length);
        for (int i = 0; i < team.length; i++) {
            Score<T> s = new Score<T>(team[i],
                    task.fitnessArray(fitnessPreferences[i], taskScores),
                    null,
                    Arrays.copyOf(taskScores.otherStats, taskScores.otherStats.length));
            result.add(s);
        }
        return result;
    }

    /**
     * Unfortunately, this function required the duplication of lots of code
     * from NoisyLonerTask
     *
     * @param mspacman
     * @return
     */
    public Score<T> evaluate(NNPacManController multiController) {
        PacManControllerFacade mspacman = new PacManControllerFacade((NewPacManController) (multiController));

        task.prep();
        double[][] objectiveScores = new double[CommonConstants.trials][task.numObjectives()];
        double[][] otherScores = new double[CommonConstants.trials][task.numOtherScores()];
        double evalTimeSum = 0;

        Organism<T> organism = new NNMsPacMan<T>(multiController);
        for (int i = 0; i < CommonConstants.trials; i++) {
            long before = System.currentTimeMillis();
            GameFacade game = task.agentEval(mspacman, i);
            //System.out.println("Game Score:"+ game.getScore());
            double[] fitnesses = new double[this.numObjectives()];
            double[] scores = new double[task.numOtherScores()];
            for (int j = 0; j < task.objectives.size(); j++) {
                fitnesses[j] = task.objectives.get(j).score(game, organism);
            }
            //System.out.println("fitnesses:" + Arrays.toString(fitnesses));
            for (int j = 0; j < task.otherScores.size(); j++) {
                scores[j] = task.otherScores.get(j).score(game, organism);
            }
            //System.out.println("scores:" + Arrays.toString(scores));
            Pair<double[], double[]> result = new Pair<double[], double[]>(fitnesses, scores);


            if (task.printFitness) {
                System.out.println(Arrays.toString(result.t1) + Arrays.toString(result.t2));
                //task.scoreSummary(objectiveScores, otherScores, fitnesses, scores);
            }
            long after = System.currentTimeMillis();
            evalTimeSum += (after - before);
            objectiveScores[i] = result.t1; // fitness scores
            otherScores[i] = result.t2; // other scores
        }
        double averageEvalTime = evalTimeSum / CommonConstants.trials;
        double[] fitness = new double[this.numObjectives()];
        for (int i = 0; i < fitness.length; i++) {
            if (MMNEAT.aggregationOverrides.get(i) == null) {
                fitness[i] = task.stat.stat(ArrayUtil.column(objectiveScores, i));
            } else {
                fitness[i] = MMNEAT.aggregationOverrides.get(i).stat(ArrayUtil.column(objectiveScores, i));
            }
        }
        double[] other = new double[task.numOtherScores()];
        for (int i = 0; i < other.length; i++) {
            if (MMNEAT.aggregationOverrides.get(fitness.length + i) == null) {
                other[i] = task.stat.stat(ArrayUtil.column(otherScores, i));
            } else {
                other[i] = MMNEAT.aggregationOverrides.get(fitness.length + i).stat(ArrayUtil.column(otherScores, i));
            }
        }
        if (task.printFitness) {
            System.out.println("Team: ");
            System.out.println("\t" + task.scoreSummary(objectiveScores, otherScores, fitness, other));
//            System.out.println("\tFitness scores:");
//            int globalFitnessFunctionIndex = 0;
//            for (int i = 0; i < fitness.length; i++) {
//
//                Statistic fitnessStat = MONE.aggregationOverrides.get(globalFitnessFunctionIndex);
//                String fitnessFunctionName = MONE.fitnessFunctions.get(globalFitnessFunctionIndex) + (fitnessStat == null ? "" : "[" + fitnessStat.getClass().getSimpleName() + "]");
//                globalFitnessFunctionIndex++;
//
//                System.out.println("\t" + fitnessFunctionName + ":\t" + Arrays.toString(ArrayUtil.column(objectiveScores, i)) + ":" + fitness[i]);
//            }
//            System.out.println("\tOther scores:");
//            for (int i = 0; i < other.length; i++) {
//
//                Statistic fitnessStat = MONE.aggregationOverrides.get(globalFitnessFunctionIndex);
//                String otherScoreName = MONE.fitnessFunctions.get(globalFitnessFunctionIndex) + (fitnessStat == null ? "" : "[" + fitnessStat.getClass().getSimpleName() + "]");
//                globalFitnessFunctionIndex++;
//
//                System.out.println("\t" + otherScoreName + ":\t" + Arrays.toString(ArrayUtil.column(otherScores, i)) + ":" + other[i]);
//            }
        }
        task.cleanup();
        Score<T> s = new MultiObjectiveScore<T>(null, fitness, task.getBehaviorVector(), other);
        s.averageEvalTime = averageEvalTime;
        return s;
    }

    /**
     * One population for each mode in the multitask scheme
     *
     * @return
     */
    public int numberOfPopulations() {
        return members;
    }

    /**
     * WARNING! This over estimates the number of fitness values for each
     * evolving component, but this function is really only used by blueprints,
     * so maybe this is not a problem? Be careful if using for anything new.
     *
     * @return
     */
    public int[] objectivesPerPopulation() {
        int[] result = new int[members];
        for (int i = 0; i < result.length; i++) {
            result[i] = task.objectives.size();
        }
        return result;
    }

    /**
     * WARNING! This over estimates the number of fitness values for each
     * evolving component, but this function is really only used by blueprints,
     * so maybe this is not a problem? Be careful if using for anything new.
     *
     * @return
     */
    public int[] otherStatsPerPopulation() {
        int[] result = new int[members];
        for (int i = 0; i < result.length; i++) {
            result[i] = task.otherScores.size();
        }
        return result;
    }
}
