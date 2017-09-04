/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.evolution.genotypes;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.MLP;
import edu.southwestern.networks.SRN;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class SRNGenotype extends MLPGenotype {

	protected int numInputs;

	public SRNGenotype() {
		this(MMNEAT.networkInputs, Parameters.parameters.integerParameter("hiddenMLPNeurons"), MMNEAT.networkOutputs);
	}

	public SRNGenotype(int numberOfInputs, int numberOfHidden, int numberOfOutputs) {
		this(new SRN(numberOfInputs, numberOfHidden, numberOfOutputs));
	}

	public SRNGenotype(SRN srn) {
		SRN srnCopy = srn.copy();
		firstConnectionLayer = srnCopy.firstConnectionLayer;
		secondConnectionLayer = srnCopy.secondConnectionLayer;
		numInputs = srn.numInputs();
	}

	public SRNGenotype(MLPGenotype genotype, int actualInputs) {
		this(new SRN(genotype, actualInputs));
	}

	@Override
	public MLP getPhenotype() {
		return new SRN(this.firstConnectionLayer, this.secondConnectionLayer, this.numInputs);
	}

	@Override
	public Genotype<MLP> newInstance() {
		return new SRNGenotype(
				new SRN(numInputs, this.secondConnectionLayer.length, this.secondConnectionLayer[0].length));
	}

	@Override
	public Genotype<MLP> copy() {
		return new SRNGenotype((SRN) this.getPhenotype().copy());
	}

	@Override
	public Genotype<MLP> crossover(Genotype<MLP> g) {
		MLPGenotype result = (MLPGenotype) super.crossover(g);
		return new SRNGenotype(result, this.numInputs);
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "hiddenMLPNeurons:5" });
		MMNEAT.networkInputs = 2;
		MMNEAT.networkOutputs = 2;

		SRNGenotype srn = new SRNGenotype();

		MLP test = srn.getPhenotype();

		double[] inputs = new double[] { 5, -3 };
		int iterations = 10;

		System.out.println("SRN changes");
		for (int i = 0; i < iterations; i++) {
			System.out.println(Arrays.toString(test.process(inputs)));
		}

		System.out.println("Unless flushed every time");
		for (int i = 0; i < iterations; i++) {
			test.flush();
			System.out.println(Arrays.toString(test.process(inputs)));
		}
	}
}
