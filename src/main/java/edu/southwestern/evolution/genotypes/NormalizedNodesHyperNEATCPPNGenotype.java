package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.util.datastructures.Pair;

public class NormalizedNodesHyperNEATCPPNGenotype extends HyperNEATCPPNGenotype{
	RealValuedGenotype gammaValues;
	RealValuedGenotype betaValues;
	
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
		int numNormalizedNodes = numNormalizedNodes();
		gammaValues = initializeGamma(numNormalizedNodes);
		betaValues = initializeBeta(numNormalizedNodes);
	}
	
	public NormalizedNodesHyperNEATCPPNGenotype(HyperNEATCPPNGenotype hngt) {
		this(hngt.archetypeIndex, hngt.links, hngt.nodes, hngt.neuronsPerModule);
	}
	
	public NormalizedNodesHyperNEATCPPNGenotype(int archetypeIndex, ArrayList<LinkGene> links, ArrayList<NodeGene> genes, int outputNeurons) {
		super(archetypeIndex, links, genes, outputNeurons);
		int numNormalizedNodes = numNormalizedNodes();
		gammaValues = initializeGamma(numNormalizedNodes);
		betaValues = initializeBeta(numNormalizedNodes);
	}
	
	private int numNormalizedNodes() {
		List<Substrate> substrates = getSubstrateInformation((HyperNEATTask) MMNEAT.task);
		int numNormalizedNodes = 0;
		for (Substrate substrate: substrates) {
			if(substrate.getStype() != Substrate.INPUT_SUBSTRATE) {
				Pair<Integer, Integer> subSize = substrate.getSize();
				numNormalizedNodes += subSize.t1.intValue() * subSize.t2.intValue();
			}
		}
		return numNormalizedNodes;
	}
	
	@Override
	public NodeGene newSubstrateNodeGene(Substrate sub, double bias) {
		System.out.println("gamma: " + gammaValues.getPhenotype());
		System.out.println("beta: " + betaValues.getPhenotype());
		System.out.println("innovationID" + innovationID);
		double gammaValue = gammaValues.getPhenotype().get(innovationID);
		double betaValue = betaValues.getPhenotype().get(innovationID);
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
