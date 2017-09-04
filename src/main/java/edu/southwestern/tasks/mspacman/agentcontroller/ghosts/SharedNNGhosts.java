package edu.southwestern.tasks.mspacman.agentcontroller.ghosts;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;

/**
 *
 * @author Jacob Schrum
 * @param <T>
 *            type of phenotype
 */
public class SharedNNGhosts<T extends Network> extends Organism<T> {

	public SharedNNGhostsController controller;

	public SharedNNGhosts(Genotype<T> genotype) {
		super(genotype);
		Network net = (Network) this.getGenotype().getPhenotype();
		this.controller = new SharedNNCheckEachDirectionGhostsController(net);
	}

}
