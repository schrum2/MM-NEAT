/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.ghosts;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;

/**
 *
 * @author Jacob Schrum
 * @param <T> type of phenotype
 */
public class SharedNNGhosts<T extends Network> extends Organism<T> {

    public SharedNNGhostsController controller;
    
    public SharedNNGhosts(Genotype<T> genotype) {
        super(genotype);
        Network net = (Network) this.getGenotype().getPhenotype();
        this.controller = new SharedNNCheckEachDirectionGhostsController(net);
    }

}
