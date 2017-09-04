/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.metaheuristics.AntiMaxModuleUsageFitness;
import edu.southwestern.networks.Network;

/**
 *
 * @author Jacob Schrum
 */
public class AntiMaxModeUsage<T extends Network> extends MsPacManObjective<T> {

	private final AntiMaxModuleUsageFitness meta;

	public AntiMaxModeUsage() {
		this.meta = new AntiMaxModuleUsageFitness();
	}

	@Override
	public double minScore() {
		return meta.minScore();
	}

	public double fitness(Organism<T> individual) {
		return meta.getScore((TWEANNGenotype) individual.getGenotype());
	}
}
