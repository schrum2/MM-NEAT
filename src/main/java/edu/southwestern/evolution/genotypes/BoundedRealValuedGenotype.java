package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.mutation.real.PerturbMutation;
import edu.southwestern.evolution.mutation.real.PolynomialMutation;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.datastructures.ArrayUtil;

import java.util.ArrayList;

/**
 * Genotype that is a fixed-length sequence of real numbers, all of which are
 * confined to specified ranges unique to each position in the genotype.
 * 
 * @author Jacob Schrum
 */
public class BoundedRealValuedGenotype extends RealValuedGenotype {

	double[] lower; // Lowest allowable value for each gene position
	double[] upper; // Highest allowable value for each gene position
	boolean polynomialMutation; // Whether or not polynomial mutation should be used

	/**
	 * Creates evolvable genotype where genes are restricted to the default MMNEAT upper and lower bounds
	 * Populates the genotype with values between the default MMNEAT upper and lower bounds
	 */
	public BoundedRealValuedGenotype() {
		// May need to change this if other tasks start using the real-coded genotype
		this(MMNEAT.getLowerBounds(), MMNEAT.getUpperBounds());
	}

	/**
	 * Creates evolvable genotype where genes are restricted to certain bounds
	 * Populates the genotype with values between certain bounds
	 * @param lower Array of lower bounds for each gene position
	 * @param upper Array of upper bounds for each gene position
	 */
	public BoundedRealValuedGenotype(double[] lower, double[] upper) {
		this(RandomNumbers.randomBoundedArray(lower, upper), lower, upper);
	}

	/**
	 * Creates evolvable genotype where genes are restricted to certain bounds
	 * @param genes Array of doubles corresponding to starting gene values
	 * @param lower Array of lower bounds for each gene position
	 * @param upper Array of upper bounds for each gene position
	 */
	public BoundedRealValuedGenotype(double[] genes, double[] lower, double[] upper) {
		super(genes);
		// Specialized mutation operator slightly more complicated than simple perturbation
		polynomialMutation = Parameters.parameters.booleanParameter("polynomialMutation");

		this.lower = lower;
		this.upper = upper;
		bound();
	}

	/**
	 * Creates evolvable genotype where genes are restricted to certain bounds
	 * @param genes ArrayList of doubles corresponding to starting gene values
	 * @param lower Array of lower bounds for each gene position
	 * @param upper Array of upper bounds for each gene position
	 */
	public BoundedRealValuedGenotype(ArrayList<Double> genes, double[] lower, double[] upper) {
		this(ArrayUtil.doubleArrayFromList(genes), lower, upper);
	}

	/**
	 * Creates evolvable genotype where genes are restricted to certain bounds
	 * @param genotype RealValuedGenotype to provide starting the genotype
	 * @param lower Array of lower bounds for each gene position
	 * @param upper Array of upper bounds for each gene position
	 */
	@SuppressWarnings("unused")
	private BoundedRealValuedGenotype(RealValuedGenotype genotype, double[] lower, double[] upper) {
		this(genotype.genes, lower, upper);
	}

	/**
	 * Returns a copy of genotype
	 */
	@Override
	public Genotype<ArrayList<Double>> copy() {
		double[] array = new double[genes.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = genes.get(i);
		}
		return new BoundedRealValuedGenotype(array, lower, upper);
	}

	/**
	 * Finds and returns the range between the upper and lower bounds of each gene
	 * @return An array of doubles corresponding to the range of each gene
	 */
	public final double[] getRange() {
		double[] magnitudes = new double[lower.length];
		for (int i = 0; i < magnitudes.length; i++) {
			magnitudes[i] = upper[i] - lower[i];
		}
		return magnitudes;
	}

	/**
	 * Mutates the genotype
	 */
	@Override
	public void mutate() {
		if (polynomialMutation) { // Specialized mutation operator slightly more complicated than simple perturbation
			new PolynomialMutation().mutate(this);
		} else { // Default
			new PerturbMutation(getRange()).mutate(this);
		}
		bound();
	}

	/**
	 * Push gene values that are out of bounds back 
	 * to the particular bound they crossed.
	 */
	public final void bound() {
		for (int i = 0; i < genes.size(); i++) {
			double x = genes.get(i);
			if (x < lower[i]) {
				x = lower[i];
			} else if (x > upper[i]) {
				x = upper[i];
			}
			genes.set(i, x);
		}
	}

	/**
	 * Gets a new instance of a BoundedRealValuedGenotype with the same bounds
	 * @return Returns a new instance of a BoundedRealValuedGenotype with the same bounds
	 */
	@Override
	public Genotype<ArrayList<Double>> newInstance() {
		return new BoundedRealValuedGenotype(lower, upper);
	}

	/**
	 * Returns an array of doubles corresponding to the lower bounds of each gene
	 */
	public double[] lowerBounds() {
		return lower;
	}

	/**
	 * Returns an array of doubles corresponding to the upper bounds of each gene
	 */
	public double[] upperBounds() {
		return upper;
	}
}
