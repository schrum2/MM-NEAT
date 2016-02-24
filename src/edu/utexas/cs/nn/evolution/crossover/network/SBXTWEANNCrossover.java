/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.crossover.network;

import edu.utexas.cs.nn.evolution.crossover.real.SBX;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.Gene;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class SBXTWEANNCrossover extends TWEANNCrossover {

    private final SBX sbx;
    private final double bound;

    public SBXTWEANNCrossover() {
        this(Parameters.parameters.doubleParameter("crossExcessRate"));
    }

    public SBXTWEANNCrossover(double crossExcessRate) {
        super(crossExcessRate);
        // Bounds are known, but index positions can grow indefinitely
        this.sbx = new SBX(null, null);
        this.bound = Parameters.parameters.doubleParameter("weightBound");
    }

    /*
     * If crossing links, uses SBX to determine new weight values
     */
    @Override
    public <G extends Gene> void crossIndex(G leftGene, G rightGene, ArrayList<G> crossedLeft, ArrayList<G> crossedRight) {
        if (leftGene instanceof LinkGene) {
            LinkGene left = (LinkGene) leftGene;
            LinkGene right = (LinkGene) rightGene;

            Pair<Double, Double> p = sbx.newIndexContents(left.weight, right.weight, bound, -bound);
            System.out.println(left.weight + "," + right.weight + "->" + p.t1 + "," + p.t2);
            left.weight = p.t1;
            right.weight = p.t2;

            crossedLeft.add((G) left);
            crossedRight.add((G) right);
        } else {
            super.crossIndex(leftGene, rightGene, crossedLeft, crossedRight);
        }
    }
}
