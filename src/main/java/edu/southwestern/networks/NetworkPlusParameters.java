package edu.southwestern.networks;

import edu.southwestern.util.datastructures.Pair;

/**
 * There are various cases where we want to evolve a neural network combined with
 * some basic configuration parameters. This can be handled by a CombinedGenotype, but
 * that Genotype creates a problem, because it maps to a phenotype that is a Pair,
 * and a Pair does not implement the Network interface. However, this class does
 * implement the Network interface, while still being a Pair.
 * 
 * @author schrum2
 *
 * @param <T> Some Network phenotype
 * @param <E> Any other arbitrary data type
 */
public class NetworkPlusParameters<T extends Network,E> extends Pair<T,E> implements Network {

	public NetworkPlusParameters(T pos1, E pos2) {
		super(pos1, pos2);
	}

	@Override
	public int numInputs() {
		return this.t1.numInputs();
	}

	@Override
	public int numOutputs() {
		return this.t1.numOutputs();
	}

	@Override
	public int effectiveNumOutputs() {
		return this.t1.effectiveNumOutputs();
	}

	@Override
	public double[] process(double[] inputs) {
		return this.t1.process(inputs);
	}

	@Override
	public void flush() {
		this.t1.flush();
	}

	@Override
	public boolean isMultitask() {
		return this.t1.isMultitask();
	}

	@Override
	public void chooseMode(int mode) {
		this.t1.chooseMode(mode);
	}

	@Override
	public int lastModule() {
		return this.t1.lastModule();
	}

	@Override
	public double[] moduleOutput(int mode) {
		return this.t1.moduleOutput(mode);
	}

	@Override
	public int numModules() {
		return this.t1.numModules();
	}

	@Override
	public int[] getModuleUsage() {
		return this.t1.getModuleUsage();
	}

}
