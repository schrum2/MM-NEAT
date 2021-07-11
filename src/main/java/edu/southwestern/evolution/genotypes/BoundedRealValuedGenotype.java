package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.mapelites.mutations.MAPElitesLineMutation;
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
	
	boolean polynomialMutation; // Whether or not polynomial mutation should be used

	/**
	 * Creates evolvable genotype where genes are restricted to the default MMNEAT upper and lower bounds
	 * Populates the genotype with values between the default MMNEAT upper and lower bounds
	 */
	public BoundedRealValuedGenotype() {
		this(RandomNumbers.randomBoundedArray(MMNEAT.getLowerBounds(), MMNEAT.getUpperBounds()));
	}

	/**
	 * Creates evolvable genotype where genes are restricted to certain bounds
	 * @param genes Array of doubles corresponding to starting gene values
	 */
	public BoundedRealValuedGenotype(double[] genes) {
		super(genes);
		// Specialized mutation operator slightly more complicated than simple perturbation
		polynomialMutation = Parameters.parameters.booleanParameter("polynomialMutation");
		bound();
	}

	/**
	 * Creates evolvable genotype where genes are restricted to certain bounds
	 * @param genes ArrayList of doubles corresponding to starting gene values
	 */
	public BoundedRealValuedGenotype(ArrayList<Double> genes) {
		this(ArrayUtil.doubleArrayFromList(genes));
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
		return new BoundedRealValuedGenotype(array);
	}

	/**
	 * Finds and returns the range between the upper and lower bounds of each gene
	 * @return An array of doubles corresponding to the range of each gene
	 */
	public final double[] getRange() {
		double[] magnitudes = new double[MMNEAT.lower.length];
		for (int i = 0; i < magnitudes.length; i++) {
			magnitudes[i] = MMNEAT.upper[i] - MMNEAT.lower[i];
		}
		return magnitudes;
	}

	/**
	 * Mutates the genotype
	 */
	@Override
	public void mutate() {
		MAPElitesLineMutation lineMutation = null;
		if(Parameters.parameters.doubleParameter("meLineMutationRate") > 0.0 && (lineMutation = new MAPElitesLineMutation()).perform()) {		
			lineMutation.mutate(this);
		} else {
			if (polynomialMutation) { // Specialized mutation operator slightly more complicated than simple perturbation
				new PolynomialMutation().mutate(this);
			} else { // Default
				new PerturbMutation(getRange()).mutate(this);
			}
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
			if (x < MMNEAT.getLowerBounds()[i]) {
				x = MMNEAT.getLowerBounds()[i];
			} else if (x > MMNEAT.getUpperBounds()[i]) {
				x = MMNEAT.getUpperBounds()[i];
			}
			genes.set(i, x);
		}
	}
	
	/**
	 * Check if the genotype already is bounded by the appropriate range.
	 * @return true if all variables are appropriately bounded
	 */
	public final boolean isBounded() {
		for (int i = 0; i < genes.size(); i++) {
			double x = genes.get(i);
			if (x < MMNEAT.getLowerBounds()[i]) {
				return false;
			} else if (x > MMNEAT.getUpperBounds()[i]) {
				return false;
			}
		}
		return true;
	}

	public static final boolean isBounded(double[] genes) {
		for (int i = 0; i < genes.length; i++) {
			double x = genes[i];
			if (x < MMNEAT.getLowerBounds()[i]) {
				return false;
			} else if (x > MMNEAT.getUpperBounds()[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets a new instance of a BoundedRealValuedGenotype with the same bounds
	 * @return Returns a new instance of a BoundedRealValuedGenotype with the same bounds
	 */
	@Override
	public Genotype<ArrayList<Double>> newInstance() {
		return new BoundedRealValuedGenotype();
	}

	/**
	 * Returns an array of doubles corresponding to the lower bounds of each gene
	 */
	public double[] lowerBounds() {
		return MMNEAT.lower;
	}

	/**
	 * Returns an array of doubles corresponding to the upper bounds of each gene
	 */
	public double[] upperBounds() {
		return MMNEAT.upper;
	}
}
