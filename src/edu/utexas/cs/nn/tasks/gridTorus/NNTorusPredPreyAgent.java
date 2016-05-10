package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;

public class NNTorusPredPreyAgent<T extends Network> extends Organism<T> {

    public NNTorusPredPreyController controller; //This is the initializing for the brain that prey use? -Gab

    public NNTorusPredPreyAgent(Genotype<T> genotype) {
        super(genotype); //Super from Organism -Gab
        Network net = (Network) this.getGenotype().getPhenotype();
        controller = new NNTorusPredPreyController(net); //These statements make sense in how to get to the finished controller set up -Gab
    }

    public NNTorusPredPreyController getController() {
        return controller; //Because the file is PredPreyAgent, is it correct to assume this controller is available for all agents? Prey and Predator? Do they both evolve, or is it just Prey and the naming was just to be vague and consistent with other files? -Gab
    }
}
