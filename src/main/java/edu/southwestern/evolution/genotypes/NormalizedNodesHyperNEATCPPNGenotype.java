package edu.southwestern.evolution.genotypes;

import java.util.List;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.util.datastructures.Pair;

public class NormalizedNodesHyperNEATCPPNGenotype extends HyperNEATCPPNGenotype{

	private static final long serialVersionUID = 2981137241389828468L;
	RealValuedGenotype gammaValues;
	RealValuedGenotype betaValues;
	int numInputNodes;

	public static RealValuedGenotype initializeGamma(int length) {
		double[] initialValues = new double[length];
		for(int i = 0; i < length; i++) {
			initialValues[i] = 1;
		}
		return new RealValuedGenotype(initialValues);
	}

	public static RealValuedGenotype initializeBeta(int length) {
		return new RealValuedGenotype(new double[length]);
	}

	public NormalizedNodesHyperNEATCPPNGenotype() {
		super();
		Pair<Integer, Integer> numInputAndNormalizedNodes = numInputAndNormalizedNodes();
		int numNormalizedNodes = numInputAndNormalizedNodes.t2;
		gammaValues = initializeGamma(numNormalizedNodes);
		betaValues = initializeBeta(numNormalizedNodes);
		numInputNodes = numInputAndNormalizedNodes.t1;
	}

	public NormalizedNodesHyperNEATCPPNGenotype(HyperNEATCPPNGenotype hngt) {
		super(hngt.archetypeIndex, hngt.links, hngt.nodes, hngt.neuronsPerModule);
		Pair<Integer, Integer> numInputAndNormalizedNodes = numInputAndNormalizedNodes();
		this.numInputNodes = numInputAndNormalizedNodes.t1;
		int numNormalizedNodes = numInputAndNormalizedNodes.t2;
		gammaValues = initializeGamma(numNormalizedNodes);
		betaValues = initializeBeta(numNormalizedNodes);
	}

	private Pair<Integer, Integer> numInputAndNormalizedNodes() {
		List<Substrate> substrates = getSubstrateInformation((HyperNEATTask) MMNEAT.task);
		int numInputNodes = 0;
		int numNormalizedNodes = 0;
		for (Substrate substrate: substrates) {
			Pair<Integer, Integer> subSize = substrate.getSize();
			if(substrate.getStype() == Substrate.INPUT_SUBSTRATE) {
				numInputNodes += subSize.t1.intValue() * subSize.t2.intValue();
			} else {
				numNormalizedNodes += subSize.t1.intValue() * subSize.t2.intValue();
			}
		}
		return new Pair<Integer, Integer>(numInputNodes, numNormalizedNodes);
	}
	
	@Override
	public NodeGene newSubstrateNodeGene(Substrate sub, double bias) {
		double gammaValue = 1;
		double betaValue = 0;
		if(sub.getStype() != Substrate.INPUT_SUBSTRATE) {
			gammaValue = gammaValues.getPhenotype().get(innovationID - numInputNodes);
			betaValue = betaValues.getPhenotype().get(innovationID - numInputNodes);
		}
		return newNodeGene(sub.getFtype(), sub.getStype(), innovationID++, false, bias, normalizedNodeMemory, gammaValue, betaValue);
	}

	@Override
	public Genotype<TWEANN> copy() {
		HyperNEATCPPNGenotype initialCopy = (HyperNEATCPPNGenotype) super.copy(); 
		RealValuedGenotype gammaValuesCopy = (RealValuedGenotype) gammaValues.copy();
		RealValuedGenotype betaValuesCopy = (RealValuedGenotype) betaValues.copy();
		NormalizedNodesHyperNEATCPPNGenotype result = new NormalizedNodesHyperNEATCPPNGenotype(initialCopy);
		result.gammaValues = gammaValuesCopy;
		result.betaValues = betaValuesCopy;
		return result;
	}

	@Override
	public Genotype<TWEANN> newInstance() {
		HyperNEATCPPNGenotype initialInstance = (HyperNEATCPPNGenotype) super.newInstance();
		RealValuedGenotype gammaValuesInstance = (RealValuedGenotype) gammaValues.newInstance();
		RealValuedGenotype betaValuesInstance = (RealValuedGenotype) betaValues.newInstance();
		NormalizedNodesHyperNEATCPPNGenotype result = new NormalizedNodesHyperNEATCPPNGenotype(initialInstance);
		result.gammaValues = gammaValuesInstance;
		result.betaValues = betaValuesInstance;
		return result;
	}

	@Override
	public void mutate() {
		super.mutate();
		gammaValues.mutate();
		betaValues.mutate();
	}

	@Override
	public Genotype<TWEANN> crossover(Genotype<TWEANN> other) {
		Genotype<TWEANN> result = super.crossover(other);
		RealValuedGenotype crossedGammaValues = (RealValuedGenotype) gammaValues.crossover(((NormalizedNodesHyperNEATCPPNGenotype) other).gammaValues);
		RealValuedGenotype crossedBetaValues = (RealValuedGenotype) betaValues.crossover(((NormalizedNodesHyperNEATCPPNGenotype) other).betaValues);
		((NormalizedNodesHyperNEATCPPNGenotype) result).gammaValues = crossedGammaValues;
		((NormalizedNodesHyperNEATCPPNGenotype) result).betaValues = crossedBetaValues;
		return result;
	}
}
