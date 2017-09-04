package edu.southwestern.evolution.crossover.network;

import edu.southwestern.evolution.crossover.real.SBX;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.Gene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;

import java.util.ArrayList;

/**
 * Performs standard TWEANN crossover, but uses a special crossover mechanism to
 * cross the weights of links that align during crossover.
 * 
 * Specifically, the real-valued weight links that align are crossed using SBX,
 * which is Simulated Binary Crossover, a mechanism for crossing real numbers
 * that supposedly preserves some of the benefits from standard binary crossover
 * of bit strings that represent integer values.
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
	@SuppressWarnings("unchecked")
	@Override
	public <G extends Gene> void crossIndex(G leftGene, G rightGene, ArrayList<G> crossedLeft,
			ArrayList<G> crossedRight) {
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
