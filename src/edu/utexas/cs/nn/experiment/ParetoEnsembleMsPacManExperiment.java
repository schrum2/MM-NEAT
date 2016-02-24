package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.mspacman.CooperativeEnsembleMsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.EnsembleMsPacManController;
import edu.utexas.cs.nn.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.PopulationUtil;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Designed to take one pre-evolved population and evaluate its Pareto front as
 * an ensemble
 *
 * @author Jacob Schrum
 */
public final class ParetoEnsembleMsPacManExperiment<T extends Network> implements Experiment {

    protected ArrayList<Genotype<T>> front;
    protected MsPacManModeSelector ms;
    private final MsPacManControllerInputOutputMediator[] inputMediators;

    public ParetoEnsembleMsPacManExperiment() throws NoSuchMethodException {
        this(FileUtilities.getSaveDirectory() + "/gen" + Parameters.parameters.integerParameter("lastSavedGeneration"),
                FileUtilities.getSaveDirectory() + "/" + Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber") + "_parents_gen" + Parameters.parameters.integerParameter("lastSavedGeneration") + ".txt");
    }

    public ParetoEnsembleMsPacManExperiment(String populationDir, String scoreFile) throws NoSuchMethodException {
        System.out.println("Loading population: " + populationDir);
        front = PopulationUtil.load(populationDir);
        System.out.println("Loading scores: " + scoreFile);
        NSGA2Score<T>[] scores = null;
        try {
            scores = PopulationUtil.loadScores(scoreFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        int topLayers = Parameters.parameters.integerParameter("layersToView");
        PopulationUtil.pruneDownToTopParetoLayers(front, scores, topLayers);
        Parameters.parameters.setInteger("numCoevolutionSubpops", front.size());
        System.out.println("Front/ensemble size = " + front.size());

        this.inputMediators = new MsPacManControllerInputOutputMediator[front.size()];
        for (int i = 0; i < inputMediators.length; i++) {
            inputMediators[i] = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
        }
        MMNEAT.setupMsPacmanParameters();
    }

    public void init() {
        // All work already done in constructor ... should move that here?
    }

    public void run() {

        CooperativeEnsembleMsPacManTask pmTask = new CooperativeEnsembleMsPacManTask(front.size());
        Genotype<T>[] genotypes = new Genotype[front.size()];
        for (int i = 0; i < genotypes.length; i++) {
            genotypes[i] = front.get(i);
        }
        EnsembleMsPacManController controller = new EnsembleMsPacManController(genotypes, inputMediators);
        Score s = null;
        for (int i = 0; i < CommonConstants.trials; i++) {
            Score next = pmTask.evaluate(controller);
            System.out.println("T" + i + ": " + next);
            s = s == null ? next : s.incrementalAverage(next);
        }
        System.out.println("Eval:\n" + s);
    }

    /*
     * Never called
     */
    public boolean shouldStop() {
        return true;
    }
}
