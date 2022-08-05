package edu.southwestern.evolution.genotypes;

import java.util.List;

import edu.southwestern.networks.NetworkPlusParameters;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.util.datastructures.Pair;

/**
 * A general class for combining a TWEANNGenotype with some other miscellaneous
 * evolvable genotype. However, this differs from the standard CombinedGenotype (which is stored
 * internally here), in that the created phenotypes are considered to be Networks, which makes
 * it easier to apply this new genotype in tasks that already expect some kind of Network phenotype.
 * 
 * @author schrum2
 *
 * @param <E> The non-TWEANN portion of the phenotype
 */
public class TWEANNPlusParametersGenotype<E> implements Genotype<NetworkPlusParameters<TWEANN,E>> {
	
	private CombinedGenotype<TWEANN,E> combined;
	
	public TWEANNGenotype getTWEANNGenotype() {
		return (TWEANNGenotype) combined.t1;
	}
	
	public TWEANNPlusParametersGenotype(TWEANNGenotype tg, Genotype<E> otherGenotype) {
		combined = new CombinedGenotype<TWEANN,E>(tg, otherGenotype);
	}
	
	@Override
	public void addParent(long id) {
		combined.addParent(id);
	}

	@Override
	public List<Long> getParentIDs() {
		return combined.getParentIDs();
	}

	@Override
	public Genotype<NetworkPlusParameters<TWEANN,E>> copy() {
		CombinedGenotype<TWEANN,E> cc = (CombinedGenotype<TWEANN, E>) combined.copy();
		return new TWEANNPlusParametersGenotype<E>((TWEANNGenotype) cc.t1, cc.t2);
	}

	@Override
	public void mutate() {
		combined.mutate();
	}


	@Override
	public Genotype<NetworkPlusParameters<TWEANN,E>> crossover(Genotype<NetworkPlusParameters<TWEANN,E>> g) {
		CombinedGenotype<TWEANN,E> child = (CombinedGenotype<TWEANN, E>) combined.crossover(((TWEANNPlusParametersGenotype<E>) g).combined);
		return new TWEANNPlusParametersGenotype<E>((TWEANNGenotype) child.t1, child.t2);
	}

	@Override
	public NetworkPlusParameters<TWEANN,E> getPhenotype() {
		Pair<TWEANN,E> pair = combined.getPhenotype();
		return new NetworkPlusParameters<TWEANN,E>(pair.t1, pair.t2);
	}

	@Override
	public Genotype<NetworkPlusParameters<TWEANN,E>> newInstance() {
		CombinedGenotype<TWEANN,E> instance = (CombinedGenotype<TWEANN, E>) combined.newInstance();
		return new TWEANNPlusParametersGenotype<E>((TWEANNGenotype) instance.t1, instance.t2);
	}

	@Override
	public long getId() {
		return combined.getId();
	}

}
