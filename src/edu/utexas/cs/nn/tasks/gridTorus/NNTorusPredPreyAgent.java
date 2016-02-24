package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;

public class NNTorusPredPreyAgent<T extends Network> extends Organism<T> {

    public NNTorusPredPreyController controller;

    public NNTorusPredPreyAgent(Genotype<T> genotype) {
        super(genotype);
        Network net = (Network) this.getGenotype().getPhenotype();
        controller = new NNTorusPredPreyController(net);
    }

    public NNTorusPredPreyController getController() {
        return controller;
    }
}
