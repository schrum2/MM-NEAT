package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.MultiplePopulationGenerationalEA;
import edu.southwestern.evolution.mutation.tweann.*;
import edu.southwestern.evolution.nsga2.bd.characterizations.GeneralNetworkCharacterization;
import edu.southwestern.evolution.nsga2.bd.localcompetition.TWEANNModulesNicheDefinition;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.random.RandomGenerator;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;

import java.io.Serializable;
import java.util.*;

/**
 * Genotype for a Topology and Weight Evolving Neural Network. Standard genotype
 * used by NEAT.
 *
 * @author Jacob Schrum
 */
public class TWEANNGenotype implements NetworkGenotype<TWEANN>, Serializable {

	private static final long serialVersionUID = 1L;
	
	// If this is true, then plain Node and Link genes are used instead 
	// of full genes with extra fields. 
	public static boolean smallerGenotypes = false;

	/**
	 * Common features of both node and link genes
	 *
	 * @author Jacob Schrum
	 */
	public static abstract class Gene implements Serializable {
		private static final long serialVersionUID = 1L;
		public long innovation; // unique number for each gene

		protected Gene() {
			// For serialization only
		}
		
		private Gene(long innovation) {
			this.innovation = innovation;
		}

		// These methods are overridden and filled out
		// in the link and node gene classes that are
		// fully featured. They are left blank here
		// to allow for reduced memory versions of the genes.

		public void freeze() {
		}

		public void melt() {
		}

		public boolean isFrozen() {
			return false;
		}

		public Gene copy() {
			try {
				return (Gene) this.clone();
			} catch (CloneNotSupportedException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
			return null;
		}
	}

	/**
	 * Single neuron in a neural network
	 *
	 * @author Jacob Schrum
	 */
	public static class NodeGene extends Gene {

		private static final long serialVersionUID = 1L;
		public int ntype;
		public int ftype;
		protected double bias;

		/**
		 * New node gene
		 *
		 * @param ftype type of activation function
		 * @param ntype type of node (input, hidden, output)
		 * @param innovation unique innovation number for node
		 * @param frozen false if node can accept new inputs
		 * @param bias bias offset to sum of this node before activation
		 */
		private NodeGene(int ftype, int ntype, double bias, long innovation) {
			super(innovation);
			this.ftype = ftype;
			this.ntype = ntype;
			this.bias = bias;
		}

		// These methods are overridden and filled out
		// in the link and node gene classes that are
		// fully featured. They are left blank here
		// to allow for reduced memory versions of the genes.

		public boolean fromCombiningCrossover() {
			return false;
		}

		public void setFromCombiningCrossover() {
		}

		public double getBias() {
			return bias;
		}

		/**
		 * Nodes are equal if they have the same innovation number
		 *
		 * @param o another node gene
		 * @return Whether both are equal (NodeGenes with same innovation number)
		 */
		@Override
		public boolean equals(Object o) {
			NodeGene other = (NodeGene) o; // instanceof check is skipped for efficiency
			return innovation == other.innovation;
		}

		/**
		 * Clones given node
		 *
		 * @return Copy of the NodeGene
		 */
		@Override
		public NodeGene clone() {
			return newNodeGene(ftype, ntype, innovation, isFrozen(), getBias(), this instanceof NormalizedMemoryNodeGene, getMemoryGamma(), getMemoryBeta());
		}

		/**
		 * only used in normalized node memory genes
		 * @return
		 */
		public double getMemoryBeta() {
			return 0;
		}

		/**
		 * only used in normalized node memory genes
		 * @return
		 */
		public double getMemoryGamma() {
			return 0;
		}

		/**
		 * returns a string of node's data
		 *
		 * @return String representation of Node
		 */
		@Override
		public String toString() {
			return "(inno=" + innovation + 
					",ftype=" + ActivationFunctions.activationName(ftype) + 
					",ntype=" + ntype + 
					",frozen=" + isFrozen() + 
					",bias=" + getBias() +  ")";
		}
	}

	/**
	 * Node with all possible fields. Increased memory footprint.
	 *
	 * @author Jacob Schrum
	 */
	public static class FullNodeGene extends NodeGene {

		private static final long serialVersionUID = 1L;
		protected boolean fromCombiningCrossover = false;
		protected boolean frozen;

		/**
		 * New node gene
		 *
		 * @param ftype type of activation function
		 * @param ntype type of node (input, hidden, output)
		 * @param innovation unique innovation number for node
		 * @param frozen false if node can accept new inputs
		 * @param bias bias offset to sum of this node before activation
		 */
		protected FullNodeGene(int ftype, int ntype, long innovation, boolean frozen, double bias) {
			super(ftype, ntype, bias, innovation);
			this.frozen = frozen;
			this.bias = bias;
		}

		/**
		 * Prevent future modifications to the gene.
		 */
		@Override
		public void freeze() {
			frozen = true;
		}

		/**
		 * Re-enable modifications after a freeze.
		 */
		@Override
		public void melt() {
			frozen = false;
		}

		/**
		 * Whether frozen.
		 */
		@Override
		public boolean isFrozen() {
			return frozen;
		}

		@Override
		public boolean fromCombiningCrossover() {
			return fromCombiningCrossover;
		}

		@Override
		public void setFromCombiningCrossover() {
			fromCombiningCrossover = true;
		}

		public String toString() {
			return "Full"+super.toString();
		}
	}

	/**
	 * Used for "batch" normalization.
	 * [This type of genotype has not been fully utilized]
	 * 
	 * @author Devon Fulcher
	 */
	public static class NormalizedMemoryNodeGene extends FullNodeGene{

		private static final long serialVersionUID = 1L;
		private double gamma;
		private double beta;

		protected NormalizedMemoryNodeGene(int ftype, int ntype, long innovation, boolean frozen, double bias, double gamma, double beta) {
			super(ftype, ntype, innovation, frozen, bias);
			this.gamma = gamma;
			this.beta = beta;
		}

		public String toString() {
			return "Norm"+super.toString();
		}

		@Override
		public double getMemoryBeta() {
			return beta;
		}

		@Override
		public double getMemoryGamma() {
			return gamma;
		}
	}

	/**
	 * Single link between neurons in a neural network
	 *
	 * @author Jacob Schrum
	 */
	public static class LinkGene extends Gene {

		private static final long serialVersionUID = 1L;
		public long sourceInnovation;
		public long targetInnovation;
		public double weight;

		/**
		 * New link gene in which it needs to be specified whether or not it is
		 * active
		 *
		 * @param sourceInnovation innovation of node of origin 
		 * @param targetInnovation Innovation number of node that the link points to
		 * @param weight Synaptic weight
		 * @param innovation Innovation number of link gene
		 * @param active Whether link is expressed in phenotype
		 * @param recurrent Whether the link is considered recurrent
		 * @param frozen Whether the link is immune to modifications by mutation
		 */
		private LinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation) {
			super(innovation);
			this.sourceInnovation = sourceInnovation;
			this.targetInnovation = targetInnovation;
			this.weight = weight;
		}

		/**
		 * Small LinkGenes do not track their module source
		 * @return Always -1
		 */
		public int getModuleSource() {
			return -1; 
		}

		// These methods are overridden and filled out
		// in the link gene class that is
		// fully featured. They are left blank here
		// to allow for reduced memory versions of the genes.

		public boolean isActive() { // Genes are active by default
			return true;
		}

		public void setActive(boolean newValue) { // can't change active setting
		}

		public boolean isRecurrent() {
			return false;
		}

		/**
		 * Clones given link gene
		 *
		 * @return Copy of gene
		 */
		@Override
		public LinkGene clone() {
			return newLinkGene(sourceInnovation, targetInnovation, weight, innovation, isActive(), isRecurrent(), isFrozen());
		}

		/**
		 * Returns String of link gene data
		 *
		 * @return String representation of Link Gene
		 */
		@Override
		public String toString() {
			return "(inno=" + this.innovation + ",source=" + this.sourceInnovation + ",target=" + this.targetInnovation
					+ ",weight=" + this.weight + ",active=" + this.isActive() + ",recurrent=" + this.isRecurrent() + ",frozen="
					+ this.isFrozen() + ")";
			// A shorter output option: Sometimes useful for troubleshooting
			// return "(" + this.innovation + ":" + this.sourceInnovation + "->"
			// + this.targetInnovation + ")";
		}
	}

	/**
	 * A LinkGene with all possible extended features.
	 * Fills up memory faster, and also takes more space when
	 * serialized and saved to disk.
	 * 
	 * @author Jacob Schrum
	 *
	 */
	public static class FullLinkGene extends LinkGene {

		private static final long serialVersionUID = 1L;
		protected boolean active;
		protected boolean recurrent;
		protected boolean frozen;
		protected int moduleSource;

		/**
		 * New link gene in which it needs to be specified whether or not it is
		 * active
		 *
		 * @param sourceInnovation innovation of node of origin 
		 * @param targetInnovation Innovation number of node that the link points to
		 * @param weight Synaptic weight
		 * @param innovation Innovation number of link gene
		 * @param active Whether link is expressed in phenotype
		 * @param recurrent Whether the link is considered recurrent
		 * @param frozen Whether the link is immune to modifications by mutation
		 */
		private FullLinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean active, boolean recurrent, boolean frozen, int moduleSource) {
			super(sourceInnovation, targetInnovation, weight, innovation);
			this.active = active;
			this.recurrent = recurrent;
			this.frozen = frozen;
			this.moduleSource = moduleSource;
			// When constructing substrate network, each link must have a particular module as its source.
			assert !(HyperNEATCPPNGenotype.constructingNetwork && moduleSource == -1) : sourceInnovation + " -> " + targetInnovation + " with weight " + weight + " innovation " + innovation + " active "+ active;
		}

		@Override
		public void freeze() {
			frozen = true;
		}

		@Override
		public void melt() {
			frozen = false;
		}

		@Override
		public boolean isFrozen() {
			return frozen;
		}

		@Override
		public int getModuleSource() {
			return moduleSource;
		}

		@Override
		public boolean isActive() {
			return active;
		}

		@Override
		public void setActive(boolean newValue) {
			active = newValue;
		}

		public String toString() {
			return "Full" + super.toString();
		}
	}    

	// Genes are created through these method access points so that an easy
	// distinction between different types of genes (with different memory
	// footprints) can be made.


	public static final LinkGene newLinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean recurrent) {
		return newLinkGene(sourceInnovation, targetInnovation, weight, innovation, true, recurrent, false);
	}

	public static final LinkGene newLinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean recurrent, boolean frozen) {
		return newLinkGene(sourceInnovation, targetInnovation, weight, innovation, true, recurrent, frozen);
	}

	public static final LinkGene newLinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean active, boolean recurrent, boolean frozen) {
		return newLinkGene(sourceInnovation, targetInnovation, weight, innovation, active, recurrent, frozen, -1);
	}

	public static final LinkGene newLinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean active, boolean recurrent, boolean frozen, int moduleSource) {
		return smallerGenotypes
				? new LinkGene(sourceInnovation, targetInnovation, weight, innovation)
						: new FullLinkGene(sourceInnovation, targetInnovation, weight, innovation, active, recurrent, frozen, moduleSource);
	}

	public static final NodeGene newNodeGene(int ftype, int ntype, long innovation) {
		return newNodeGene(ftype, ntype, innovation, false, 0.0, false, 0, 0);
	}

	public static final NodeGene newNodeGene(int ftype, int ntype, long innovation, boolean frozen, double bias, boolean normalizedNodes) {
		double hardCodedGamma = 1;
		double hardCodedBeta = 0;
		return newNodeGene(ftype, ntype, innovation, frozen, bias, normalizedNodes, hardCodedGamma, hardCodedBeta);
	}
	
	public static final NodeGene newNodeGene(int ftype, int ntype, long innovation, boolean frozen, double bias, boolean normalizedNodes, double memGamma, double memBeta) {
		if(CommonConstants.inputsUseID && ntype == TWEANN.Node.NTYPE_INPUT) {
			ftype = ActivationFunctions.FTYPE_ID; // Force input nodes to use ID activation function
		}
		if(normalizedNodes && ntype != TWEANN.Node.NTYPE_INPUT) { //this could be a parameter option later
			return new NormalizedMemoryNodeGene(ftype, ntype, innovation, frozen, bias, memGamma, memBeta);
		} else if (smallerGenotypes) {
			return new NodeGene(ftype, ntype, bias, innovation);
		} else {
			return new FullNodeGene(ftype, ntype, innovation, frozen, bias);
		}
	}

	/**
	 * If there is a forward link from node A to node B, then node A must appear
	 * before node A in the list nodes. Additionally, all input nodes appear
	 * first, and all output nodes appear last.
	 */
	public ArrayList<NodeGene> nodes;
	/**
	 * Links can be in any order, and still function correctly
	 */
	public ArrayList<LinkGene> links;
	public int numIn;
	public int numOut;
	public int numModules;
	public int neuronsPerModule;
	public boolean standardMultitask;
	protected int[] moduleUsage;
	private long id = EvolutionaryHistory.nextGenotypeId();
	public int archetypeIndex;

	// Added to allow tracking of who parents are for score history purposes
	protected List<Long> parents = new LinkedList<Long>();

	@Override
	public void addParent(long id) {
		parents.add(id);
	}

	@Override
	public List<Long> getParentIDs() {
		return parents;
	}

	/**
	 * Copy constructor
	 *
	 * @param copy
	 */
	public TWEANNGenotype(TWEANNGenotype copy) {
		this(copy.nodes, copy.links, copy.neuronsPerModule, copy.standardMultitask, copy.archetypeIndex);
	}

	/**
	 * Construct new genotype from component node and link lists, along with
	 * important parameters
	 *
	 * @param nodes List of node genes in genotype (must obey order rules)
	 * @param links List of link genes in genotype
	 * @param neuronsPerModule Number of policy neurons per output module
	 * @param standardMultitask Whether this is a multitask network
	 * @param archetypeIndex Index of archetype to compare against for
	 * mutation/crossover alignments
	 */
	public TWEANNGenotype(ArrayList<NodeGene> nodes, ArrayList<LinkGene> links, int neuronsPerModule,
			boolean standardMultitask, int archetypeIndex) {

		assert neuronsPerModule > 0 : "Cannot have 0 neurons in a module!";

		this.archetypeIndex = archetypeIndex;
		this.nodes = nodes;
		this.links = links;
		this.neuronsPerModule = neuronsPerModule;
		this.standardMultitask = standardMultitask;

		numIn = 0;
		numOut = 0;
		for (NodeGene ng : nodes) {
			switch (ng.ntype) {
			case TWEANN.Node.NTYPE_INPUT:
				numIn++;
				break;
			case TWEANN.Node.NTYPE_OUTPUT:
				numOut++;
				break;
			default:
			}
		}
		this.numModules = numModules();
		this.moduleUsage = new int[numModules];
	}

	/**
	 * Derives the number of output modules in the network.
	 *
	 * @return Number of output modules.
	 */
	@Override
	public final int numModules() {
		return (int) Math.max(1,
				numOut / (neuronsPerModule + (standardMultitask ? 0 : 1)));
	}

	/**
	 * Number of recurrent or non-recurrent links in network
	 *
	 * @param recurrent Whether only recurrent links are being counted (vs only
	 * non-recurrent)
	 * @return Number of links counted.
	 */
	public double numLinks(boolean recurrent) {
		int count = 0;
		for (LinkGene l : links) {
			if (l.isRecurrent() == recurrent) {
				count++;
			}
		}
		return count;
	}

	/**
	 * New genotype encoded based on a TWEANN phenotype
	 *
	 * @param tweann The network to make a genotype for
	 */
	public TWEANNGenotype(TWEANN tweann) {
		archetypeIndex = tweann.archetypeIndex;
		numIn = tweann.numInputs();
		numOut = tweann.numOutputs();
		numModules = tweann.numModules();
		neuronsPerModule = tweann.neuronsPerModule();
		standardMultitask = tweann.isStandardMultitask();
		moduleUsage = tweann.moduleUsage;
		nodes = new ArrayList<NodeGene>(tweann.nodes.size());
		links = new ArrayList<LinkGene>(tweann.nodes.size());

		for (int i = 0; i < tweann.nodes.size(); i++) {
			TWEANN.Node n = tweann.nodes.get(i);
			NodeGene ng = newNodeGene(n.ftype, n.ntype, n.innovation, n.frozen, n.bias, false, 0, 0);
			nodes.add(ng);
			LinkedList<LinkGene> temp = new LinkedList<LinkGene>();
			for (TWEANN.Link l : n.outputs) {
				LinkGene lg = newLinkGene(n.innovation, l.target.innovation, l.weight, l.innovation, true, n.isLinkRecurrent(l.target.innovation), l.frozen, l.moduleSource);
				temp.add(lg);
			}
			for (int k = 0; k < temp.size(); k++) {
				links.add(temp.get(k));
			}
		}
	}

	/**
	 * New TWEANN Genotype, used by ClassCreation to get first example of run.
	 * Assume only one population by default, hence archetype index of 0.
	 */
	public TWEANNGenotype() {
		this(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
	}

	/**
	 * New starting genotype with a given number of input and output neurons
	 *
	 * @param numIn actual number of input sensors
	 * @param numOut number of actuators (in multitask case, #actuations times
	 * 				 num tasks. For module mutation, # of policy neurons per module)
	 * @param archetypeIndex which archetype to reference for crossover
	 */
	public TWEANNGenotype(int numIn, int numOut, int archetypeIndex) {
		this(numIn, numOut, CommonConstants.fs, CommonConstants.ftype, CommonConstants.multitaskModules, archetypeIndex);
	}

	/**
	 * New starting genotype with a given number of input and output neurons
	 *
	 * @param numIn actual number of input sensors
	 * @param numOut number of actuators (in multitask case, #actuations times
	 * 				 num tasks. For module mutation, # of policy neurons per module)
	 * @param featureSelective whether initial network is sparsely connected
	 * @param ftype activation function to use on neurons
	 * @param numModules number of MULTITASK modules (does not apply for
	 * 					 multiple modules with preference neurons)
	 * @param archetypeIndex which archetype to reference for crossover
	 */
	public TWEANNGenotype(int numIn, int numOut, boolean featureSelective, int ftype, int numModules, int archetypeIndex) {
		this(new TWEANN(numIn, numOut, featureSelective, ftype, numModules, archetypeIndex));
	}

	public void calculateNumModules() {
		int oldModules = numModules;
		int count = 0;
		// Need to recalculate outputs as well
		for (NodeGene n : nodes) {
			if (n.ntype == TWEANN.Node.NTYPE_OUTPUT) {
				count++;
			}
		}
		this.numOut = count;
		this.numModules = numModules();
		if (numModules != oldModules) {
			moduleUsage = Arrays.copyOf(moduleUsage, numModules);
		}
		assert (moduleUsage != null) : "How did moduleUsage become null? numModules = " + numModules;
	}

	public double lastModulesDistance() {
		return twoModulesDistance(numModules - 2, numModules - 1);
	}

	/**
	 * Given two modules in the network, use the GeneralNetworkCharacterization
	 * to determine the distance between their behaviors.
	 *
	 * @param m1 module index 1
	 * @param m2 module index 2
	 * @return module distance, or max double value if only one module exists
	 */
	public double twoModulesDistance(int m1, int m2) {
		if (numModules == 1) {
			return Double.MAX_VALUE;
		} else {
			ArrayList<double[]> syllabus;
			if (Parameters.parameters.booleanParameter("rememberObservations")) {
				syllabus = GeneralNetworkCharacterization.newPastExperiencesSyllabus(CommonConstants.syllabusSize);
			} else {
				syllabus = GeneralNetworkCharacterization.newRandomSyllabus(CommonConstants.syllabusSize);
			}
			ArrayList<Double> last = new ArrayList<Double>();
			ArrayList<Double> prev = new ArrayList<Double>();
			TWEANN t = this.getPhenotype();
			for (double[] inputs : syllabus) {
				t.process(inputs);
				double[] outPrev = t.moduleOutput(m1);
				double[] outLast = t.moduleOutput(m2);
				prev.addAll(ArrayUtil.doubleVectorFromArray(outPrev));
				last.addAll(ArrayUtil.doubleVectorFromArray(outLast));
			}
			return CartesianGeometricUtilities.euclideanDistance(prev, last);
		}
	}

	/**
	 * Mutates the existing weights, links, and nodes of a TWEANN
	 */
	@Override
	public void mutate() {
		// System.out.println("Mutate:" + this.id);
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId());
		sb.append(" ");
		// Melting/Freezing
		new MeltThenFreezePolicyMutation().go(this, sb);
		new MeltThenFreezePreferenceMutation().go(this, sb);
		new MeltThenFreezeAlternateMutation().go(this, sb);
		// Delete
		new DeleteLinkMutation().go(this, sb);
		//new DeleteModeMutation().go(this, sb); // Disabled until fixed; currently not supported
		if (CommonConstants.allowMultipleFunctions) { // Can turn a TWEANN into a CPPN
			new ActivationFunctionMutation().go(this, sb);
		}
		// Forms of mode mutation
		if (this.numModules < CommonConstants.maxModes
				// Only allow mode mutation when number of modes is same for all
				&& (!CommonConstants.onlyModeMutationWhenModesSame
						|| EvolutionaryHistory.minModes == EvolutionaryHistory.maxModes)
				&& // Make sure modes are different
				(CommonConstants.distanceForNewMode == -1
				|| CommonConstants.distanceForNewMode < lastModulesDistance())
				&& // If using niche restriction
				(!CommonConstants.nicheRestrictionOnModeMutation
						|| // Only allow new modes if niche with more or equal modes is doing well
						this.numModules <= TWEANNModulesNicheDefinition.bestHighModeNiche())) {
			// System.out.println("In Mode Mutation Block");
			new MMP().go(this, sb);
			new MMR().go(this, sb);
			new MMD().go(this, sb);
			new FullyConnectedModuleMutation().go(this, sb);
		}
		// Standard NEAT mutations
		int chance = 0;
		do {
			new SpliceNeuronMutation().go(this, sb);
			new NewLinkMutation().go(this, sb);
			chance++;
		} while (CommonConstants.mutationChancePerMode && chance < this.numModules);

		if (CommonConstants.polynomialWeightMutation) {
			new PolynomialWeightMutation().go(this, sb);
		} else if (CommonConstants.perLinkMutateRate > 0) {
			new AllWeightMutation().go(this, sb);
		} else {
			new WeightPurturbationMutation().go(this, sb);
		}

		EvolutionaryHistory.logMutationData(sb.toString());
	}

	/**
	 * Mutation to add a new fully connected output mode.
	 *
	 * @return Count of the number of links added as a result of the mutation
	 */
	public int fullyConnectedModeMutation() {
		int linksAdded = 0;
		int neuronsToAdd = neuronsPerModule + (TWEANN.preferenceNeuron() ? 1 : 0);
		for (int i = 0; i < neuronsToAdd; i++) {
			addRandomFullyConnectedOutputNode(ActivationFunctions.newNodeFunction());
			linksAdded += numIn;
		}
		numModules++;
		return linksAdded;
	}

	/**
	 * Mutation to add new output mode fully connected to inputs
	 *
	 * @param ftype activation function on each output neuron
	 */
	private void addRandomFullyConnectedOutputNode(int ftype) {
		ArrayList<Long> linkInnovations = new ArrayList<Long>(numIn);
		ArrayList<Double> weights = new ArrayList<Double>(numIn);
		for (int i = 0; i < numIn; i++) {
			linkInnovations.add(EvolutionaryHistory.nextInnovation());
			weights.add(RandomNumbers.fullSmallRand());
		}
		long innovation = EvolutionaryHistory.nextInnovation();
		addFullyConnectedOutputNode(ftype, innovation, weights, linkInnovations);
	}

	/**
	 * Adds new output mode to network
	 *
	 * @param randomSources true if new mode is connected to random sources.
	 * 						false if mode is connected to old mode.
	 * @param numLinks number of links going in to each new output neuron
	 * 				   return the number of links actually added
	 * @return Number of new links created to connect to new module
	 */
	public int moduleMutation(boolean randomSources, int numLinks) {
		assert !(!randomSources && numLinks > 1) : "MM(P) can only add one link per module!";

		int ftype = CommonConstants.mmpActivationId ? ActivationFunctions.FTYPE_ID
				: ActivationFunctions.newNodeFunction();
		int numLinksActuallyAdded = 0; // Add up since duplicate links won't be
		// added
		for (int i = 0; i < neuronsPerModule; i++) {
			double[] weights = new double[numLinks];
			long[] linkInnovations = new long[numLinks];
			long[] sourceInnovations = new long[numLinks];
			for (int j = 0; j < numLinks; j++) {
				sourceInnovations[j] = randomSources ? getRandomNonOutputNodeInnovationNumber()
						: nodes.get(nodes.size() - (neuronsPerModule + 1)).innovation;
				linkInnovations[j] = EvolutionaryHistory.nextInnovation();
				weights[j] = 2 * RandomNumbers.fullSmallRand();
			}
			numLinksActuallyAdded += addOutputNode(ftype, sourceInnovations, weights, linkInnovations);
		}
		// Preference neuron only has one input regardless of numLinks.
		// Preference neurons need to be easily alterable.
		if (TWEANN.preferenceNeuron()) {
			addRandomPreferenceNeuron(1);
		}

		numModules++;
		return numLinksActuallyAdded;
	}

	/**
	 * Delete a random link. Doesn't care about making the network disconnected.
	 *
	 * @return The link deleted, or null if there are no more links to be deleted
	 */
	public LinkGene deleteLinkMutation() {
		if(links.size()>0)
			return deleteLink(RandomNumbers.randomGenerator.nextInt(links.size()));
		else
			return null;
	}

	/**
	 * Deletes a link. Doesn't care if the network becomes disconnected.
	 *
	 * @param index Index of link in "links" to delete
	 * @return The link deleted
	 */
	public LinkGene deleteLink(int index) {
		return links.remove(index);
	}

	/**
	 * Deletes a random module. Doesn't care if the network becomes disconnected
	 */
	public void deleteRandomModeMutation() {
		if (numModules > 1) {
			deleteMode(RandomNumbers.randomGenerator.nextInt(numModules));
		}
	}

	/**
	 * Deletes least-used mutated module
	 */
	public void deleteLeastUsedModeMutation() {
		if (numModules > 1) {
			deleteMode(StatisticsUtilities.argmin(moduleUsage));
		}
	}

	/**
	 * Remove specified output mode from network.
	 *
	 * @param modeNum mode to delete
	 */
	private void deleteMode(int modeNum) {
		/*
		 * Changes in the way the network archetype is stored make mode deletion
		 * very complicated. To do it correctly, all innovation numbers for the
		 * output nodes would need to be shifted over. However, even worse, the
		 * target innovation number of the links need to be shifted. However,
		 * the final nail in the coffin is that these shifted links need to be
		 * checked against existing links connecting the shifted innovation
		 * numbers, so that the innovation number of the links can be
		 * reassigned. It's a mess, so the code dies on any mode deletion
		 * attempt.
		 */
		System.out.println("Can't do module deletion");
		System.exit(1);

		// System.out.println("Delete mode: " + modeNum);
		int outputStart = outputStartIndex();
		int actualNeuronsPerMode = neuronsPerModule + (TWEANN.preferenceNeuron() ? 1 : 0);
		for (int i = actualNeuronsPerMode - 1; i >= 0; i--) {
			deleteOutputNeuron(outputStart + (modeNum * actualNeuronsPerMode) + i);
		}
		numModules--;
	}

	/**
	 * Delete single output neuron from network. Links into and out of the node
	 * are deleted as well.
	 *
	 * @param nodeNum index in "nodes" of neuron to delete (must be an output node)
	 */
	private void deleteOutputNeuron(int nodeNum) {
		/*
		 * Can't be used for the same reasons deleteMode can't be used (see
		 * above). Therefore, code immediately fails if this is attempted.
		 */
		System.out.println("Can't do output neuron deletion");
		System.exit(1);

		long nodeInnovation = nodes.get(nodeNum).innovation;
		Iterator<LinkGene> itr = links.iterator();
		while (itr.hasNext()) {
			LinkGene lg = itr.next();
			if (lg.sourceInnovation == nodeInnovation || lg.targetInnovation == nodeInnovation) {
				itr.remove();
			}
		}
		nodes.remove(nodeNum);
		numOut--;
	}

	/**
	 * Mutation involving perturbation of a single link weight
	 */
	public void weightMutation() {
		perturbLink(randomAlterableLink(), MMNEAT.weightPerturber.randomOutput());
	}

	/**
	 * Mutation involving a chance of each weight being perturbed
	 *
	 * @param rand Random number generator to use
	 * @param rate Chance for each individual link mutation
	 */
	public void allWeightMutation(RandomGenerator rand, double rate) {
		for (LinkGene l : links) {
			if (!l.isFrozen() && RandomNumbers.randomGenerator.nextDouble() < rate) {
				perturbLink(l, rand.randomOutput());
			}
		}
	}

	/**
	 * Returns false if all links are inactive and/or frozen
	 *
	 * @return whether an alterable link exists
	 */
	public boolean existsAlterableLink() {
		for (LinkGene lg : links) {
			if (!lg.isFrozen() && lg.isActive()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return random alterable link index in "links". Link is alterable if it is
	 * not frozen. Must also be active
	 *
	 * @return index of random alterable link
	 */
	public LinkGene randomAlterableLink() {
		assert existsAlterableLink() : "There are no alterable links";
		ArrayList<LinkGene> indicies = new ArrayList<LinkGene>(links.size());
		for (LinkGene lg : links) {
			if (!lg.isFrozen() && lg.isActive()) {
				indicies.add(lg);
			}
		}
		if (indicies.isEmpty()) {
			// There is a small risk of this with mode deletion. Need to prevent
			System.out.println("No links to choose from. All are frozen!");
			System.exit(1);
		}
		return indicies.get(RandomNumbers.randomGenerator.nextInt(indicies.size()));
	}

	/**
	 * Perturb a given linkIndex by delta
	 *
	 * @param linkIndex index of link in links. Cannot be frozen
	 * @param delta amount to change weight by
	 */
	public void perturbLink(int linkIndex, double delta) {
		LinkGene lg = links.get(linkIndex);
		perturbLink(lg, delta);
	}

	public void perturbLink(LinkGene lg, double delta) {
		assert (!lg.isFrozen()) : "Cannot perturb frozen link!";
		lg.weight += delta;
	}

	/**
	 * Get the weight of the link at the index in the link list
	 *
	 * @param linkIndex index in "links"
	 * @return weight of link
	 */
	public double linkWeight(int linkIndex) {
		return links.get(linkIndex).weight;
	}

	/**
	 * Assign a given weight to a specified link gene
	 *
	 * @param lg a link gene
	 * @param w new synaptic weight
	 */
	public void setWeight(LinkGene lg, double w) {
		assert (!lg.isFrozen()) : "Cannot set frozen link!";
		lg.weight = w;
	}

	@Override
	public void setModuleUsage(int[] usage) {
		moduleUsage = usage;
	}

	@Override
	public int[] getModuleUsage() {
		return moduleUsage;
	}

	/**
	 * Returns LinkGene for newNode between nodes with the given linkInnovations
	 * numbers
	 *
	 * @param sourceInnovation linkInnovations of nodeInnovation node
	 * @param targetInnovation linkInnovations of sourceInnovation node
	 * @return null on failure, LinkGene otherwise
	 */
	public LinkGene getLinkBetween(long sourceInnovation, long targetInnovation) {
		for (LinkGene l : links) {
			if (l.sourceInnovation == sourceInnovation && l.targetInnovation == targetInnovation) {
				return l;
			}
		}
		return null;
	}

	/**
	 * default method that mutates links. Uses random link source and random
	 * synaptic weight.
	 */
	public void linkMutation() {
		linkMutation(getRandomLinkSourceNodeInnovationNumber(), RandomNumbers.fullSmallRand());
	}

	/**
	 * adds a new mutated link to TWEANN genotype from the node with "source"
	 * innovation number to a random target node.
	 *
	 * @param source the starting node innovation number
	 * @param weight the weight of the added link
	 */
	public void linkMutation(long source, double weight) {
		long target = getRandomAlterableConnectedNodeInnovationNumber(source, CommonConstants.connectToInputs);
		long link = EvolutionaryHistory.nextInnovation();
		addLink(source, target, weight, link);
	}

	/**
	 * Get random node innovation number
	 *
	 * @return any node innovation number in network
	 */
	private long getRandomLinkSourceNodeInnovationNumber() {
		assert nodes.size() != 0 : "Network somehow has no nodes";
		return nodes.get(RandomNumbers.randomGenerator.nextInt(nodes.size() + (CommonConstants.recurrency ? 0 : -1))).innovation;
	}

	/**
	 * Get random node innovation of node that is not an output node
	 *
	 * @return any node innovation that is not an output node
	 */
	private long getRandomNonOutputNodeInnovationNumber() {
		return nodes.get(RandomNumbers.randomGenerator.nextInt(outputStartIndex())).innovation;
	}

	/**
	 * Get innovation number of random node, restricted to nodes that are not
	 * frozen and have outgoing links. Output nodes are also included, even if
	 * they do not have outgoing links, since they are "connected" in the sense
	 * that they control network output. Input neurons can optionally be
	 * excluded as well.
	 *
	 * @param includeInputs true if input neurons can potentially be selected
	 * @return innovation number of chosen random node (under restrictions)
	 */
	private long getRandomAlterableConnectedNodeInnovationNumber(long source, boolean includeInputs) {
		int sourceIndex = indexOfNodeInnovation(source);
		assert sourceIndex != -1 : "Could not find source innovation: " + sourceIndex + "\n" + this;
		// Use of set prevents duplicates, insuring fair random choice
		HashSet<Long> sourceInnovationNumbers = new HashSet<Long>();
		for (LinkGene l : links) {
			int potentialTargetIndex = indexOfNodeInnovation(l.sourceInnovation);
			if ((CommonConstants.recurrency || sourceIndex < potentialTargetIndex) // recurrent links allowed?
					&& (includeInputs || nodes.get(potentialTargetIndex).ntype != TWEANN.Node.NTYPE_INPUT)) { // links to inputs allowed?
				sourceInnovationNumbers.add(l.sourceInnovation);

			}
		}
		// Then add the outputs
		for (int i = 0; i < numOut; i++) {
			int potentialTargetIndex = outputStartIndex() + i;
			if (CommonConstants.recurrency || sourceIndex < potentialTargetIndex) {
				sourceInnovationNumbers.add(nodes.get(potentialTargetIndex).innovation);
			}
		}
		// Exclude frozen nodes
		for (NodeGene n : nodes) {
			if (n.isFrozen()) {
				sourceInnovationNumbers.remove(n.innovation);
			}
		}
		if (sourceInnovationNumbers.isEmpty()) {
			if (Parameters.parameters.booleanParameter("prefFreezeUnalterable")) {
				new MeltThenFreezePreferenceMutation().mutate(this);
				// try again
				return getRandomAlterableConnectedNodeInnovationNumber(source, includeInputs);
			} else if (Parameters.parameters.booleanParameter("policyFreezeUnalterable")) {
				new MeltThenFreezePolicyMutation().mutate(this);
				// try again
				return getRandomAlterableConnectedNodeInnovationNumber(source, includeInputs);
			} else {
				// Small possibility with module deletion: fix
				System.out.println("No nodes are both connected and alterable!");
				System.out.println("There should be unfrozen outputs");
				System.out.println("Outputs: " + numOut);
				for (NodeGene ng : nodes) {
					if (ng.ntype == TWEANN.Node.NTYPE_OUTPUT) {
						System.out.print(ng + ", ");
					}
				}
				System.out.println();
				new NullPointerException().printStackTrace();
				System.exit(1);
			}
		}
		Long[] options = new Long[sourceInnovationNumbers.size()];
		return sourceInnovationNumbers.toArray(options)[RandomNumbers.randomGenerator.nextInt(sourceInnovationNumbers.size())];
	}

	/**
	 * Add a new new link between existing nodes
	 *
	 * @param sourceInnovation linkInnovations number of nodeInnovation node
	 * @param targetInnovation linkInnovations number of sourceInnovation node
	 * @param weight weight of new link
	 * @param innovation innovation number of new new link
	 */
	public void addLink(long sourceInnovation, long targetInnovation, double weight, long innovation) {
		if (getLinkBetween(sourceInnovation, targetInnovation) == null) {
			int target = indexOfNodeInnovation(targetInnovation);
			int source = indexOfNodeInnovation(sourceInnovation);
			assert target != -1 : "Target node innovation does not exist: " + target + "\n" + this;
			assert source != -1 : "Source node innovation does not exist: " + source + "\n" + this;
			// System.out.println(nodeInnovation + "->" + sourceInnovation);
			LinkGene lg = newLinkGene(sourceInnovation, targetInnovation, weight, innovation, target <= source);
			links.add(lg);
		}
	}

	/**
	 * Splices a mutation
	 */
	public void spliceMutation() {
		spliceMutation(ActivationFunctions.newNodeFunction());
	}

	/**
	 * splices a mutation according to activation function
	 *
	 * @param ftype activation function of genotype
	 */
	private void spliceMutation(int ftype) {
		LinkGene lg = randomAlterableLink();
		long source = lg.sourceInnovation;
		long target = lg.targetInnovation;
		long newNode = EvolutionaryHistory.nextInnovation();
		double weight1 = CommonConstants.minimizeSpliceImpact ? RandomNumbers.randomSign() * 0.00001
				: RandomNumbers.fullSmallRand();
		double weight2 = CommonConstants.minimizeSpliceImpact ? RandomNumbers.randomSign() * 0.00001
				: RandomNumbers.fullSmallRand();
		long toLink = EvolutionaryHistory.nextInnovation();
		long fromLink = EvolutionaryHistory.nextInnovation();
		spliceNode(ftype, newNode, source, target, weight1, weight2, toLink, fromLink);
	}

	/**
	 * Modifies archetype!
	 *
	 * Splice a new node between two connected nodes along the newNode
	 *
	 * @param ftype activation function type of new node
	 * @param newNodeInnovation linkInnovations number of new node
	 * @param sourceInnovation linkInnovations of nodeInnovation node for splice
	 * @param targetInnovation linkInnovations of sourceInnovation node for splice
	 * @param weight1 Weight on link entering newly spliced neuron
	 * @param weight2 Weight on link exiting newly spliced neuron
	 * @param toLinkInnovation new linkInnovations number for newNode between
	 * 						   nodeInnovation and new node
	 * @param fromLinkInnovation new linkInnovations number for newNode
	 * 						     between new node and sourceInnovation
	 */
	public void spliceNode(int ftype, long newNodeInnovation, long sourceInnovation, long targetInnovation,
			double weight1, double weight2, long toLinkInnovation, long fromLinkInnovation) {
		NodeGene ng = newNodeGene(ftype, TWEANN.Node.NTYPE_HIDDEN, newNodeInnovation);
		LinkGene lg = getLinkBetween(sourceInnovation, targetInnovation);
		lg.setActive(CommonConstants.minimizeSpliceImpact);
		nodes.add(Math.min(outputStartIndex(), Math.max(numIn, indexOfNodeInnovation(sourceInnovation) + 1)), ng);
		int index = EvolutionaryHistory.indexOfArchetypeInnovation(archetypeIndex, sourceInnovation);
		int pos = Math.min(EvolutionaryHistory.firstArchetypeOutputIndex(archetypeIndex), Math.max(numIn, index + 1));
		EvolutionaryHistory.archetypeAdd(archetypeIndex, pos, ng.clone(), numModules == 1, "splice " + sourceInnovation + "->" + targetInnovation);
		LinkGene toNew = newLinkGene(sourceInnovation, newNodeInnovation, weight1, toLinkInnovation, indexOfNodeInnovation(newNodeInnovation) <= indexOfNodeInnovation(sourceInnovation));
		LinkGene fromNew = newLinkGene(newNodeInnovation, targetInnovation, weight2, fromLinkInnovation, indexOfNodeInnovation(targetInnovation) <= indexOfNodeInnovation(newNodeInnovation));
		links.add(toNew);
		links.add(fromNew);
	}

	/**
	 * Create a String describing the mathematical function defined by each output node.
	 * Only works for non-recurrent networks.
	 * @return List of strings describing function for each output neuron
	 */
	public List<String> getFunction() {
		ArrayList<String> result = new ArrayList<>(this.numOut);
		for(int i = this.outputStartIndex(); i < nodes.size(); i++) {
			result.add(getFunction(nodes.get(i)));
		}
		return result;
	}

	/**
	 * Recursively compute the function associated with a particular neuron (no recurrent connections allowed!)
	 * @param n A NodeGene for a neuron
	 * @return String representation of function
	 */
	public String getFunction(NodeGene n) {
		String result = ActivationFunctions.activationName(n.ftype) + "(";
		boolean first = true;
		for(LinkGene lg : links) {
			if(lg.targetInnovation == n.innovation && lg.isActive()) {
				if(!first) {
					result += " + ";
				}
				result += lg.weight + "*" + getFunction(getNodeWithInnovation(lg.sourceInnovation));
				first = false;
			}
		}
		if(first) {
			// No incoming links found: Input node
			result += "Input"+n.innovation;
		}
		result += ")";
		return result;
	}

	/**
	 * Return the NodeGene with the given innovation number
	 * @param innovation Gene innovation number
	 * @return NodeGene with matching number
	 */
	private NodeGene getNodeWithInnovation(long innovation) {
		for(NodeGene ng : nodes) {
			if(ng.innovation == innovation) {
				return ng;
			}
		}
		throw new IllegalArgumentException("Node innovation not found: " + innovation);
	}

	/**
	 * Modifies archetype
	 *
	 * Should always be called for archetype as well
	 */
	private void addFullyConnectedOutputNode(int ftype, long newNodeInnovation, ArrayList<Double> weights,
			ArrayList<Long> linkInnovations) {
		NodeGene ng = newNodeGene(ftype, TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
		nodes.add(ng);
		numOut++;
		EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "full output");
		for (int i = 0; i < numIn; i++) {
			LinkGene toNew = newLinkGene(nodes.get(i).innovation, newNodeInnovation, weights.get(i), linkInnovations.get(i), false);
			links.add(toNew);
		}
	}

	/**
	 * Weakens all modules in the specified portion of the Genotype
	 *
	 * @param portion location in genotype of portion ti be weakened
	 */
	public void weakenAllModules(double portion) {
		for (int i = 0; i < numModules; i++) {
			weakenModulePreference(i, portion);
		}
	}

	/**
	 * Decrease the weights going into a given preference neuron so that its
	 * module will keep the same behavior, but be less likely to be chosen.
	 *
	 * @param module module to weaken
	 * @param portion should be between 0 and 1: Fraction to reduce weight by
	 */
	public void weakenModulePreference(int module, double portion) {
		System.out.println("Weaken module " + module + " by " + portion);
		// Identify preference neuron
		int outputStart = outputStartIndex();
		int preferenceLoc = outputStart + (module * (neuronsPerModule + 1)) + neuronsPerModule;
		NodeGene preferenceNode = nodes.get(preferenceLoc);
		long preferenceInnovation = preferenceNode.innovation;
		for (LinkGene lg : links) {
			// Get all links that feed preference neuron
			if (lg.targetInnovation == preferenceInnovation) {
				lg.weight *= portion; // decrease magnitude
			}
		}
	}

	/**
	 * Modifies archetype
	 *
	 * Adds a new output neuron with a given activation function, with incoming
	 * links from the specified sources. Returns number of links added to
	 * network, which may be less than planned since duplicates in the list
	 * sourceInnovations are ignored.
	 *
	 * This default version of the method always adds the new output neuron at
	 * the end of the list of output neurons.
	 *
	 * @param ftype Activation function type
	 * @param sourceInnovations list of neurons that will link to the new one
	 * @param weights weights for the synapses linking the sourceInnovations
	 * 					to this new node
	 * @param linkInnovations innovation numbers for each of the new synapses
	 * @return number of links actually added
	 */
	private int addOutputNode(int ftype, long[] sourceInnovations, double[] weights, long[] linkInnovations) {
		return addOutputNode(ftype, sourceInnovations, weights, linkInnovations, nodes.size(),true,-1);
	}

	/**
	 * Same as above, but can potentially add the new output neuron in between other output neurons.
	 * @param ftype
	 * @param sourceInnovations
	 * @param weights
	 * @param linkInnovations
	 * @param indexToAdd
	 * @return
	 */
	private int addOutputNode(int ftype, long[] sourceInnovations, double[] weights, long[] linkInnovations, int indexToAdd, boolean addAtEnd, int archetypeIndexToAdd) {
		long newNodeInnovation = -(numIn + numOut) - 1;
		NodeGene ng = newNodeGene(ftype, TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
		HashSet<Long> addedLinks = new HashSet<Long>();
		for (int i = 0; i < weights.length; i++) {
			if (!addedLinks.contains(sourceInnovations[i])) {
				addedLinks.add(sourceInnovations[i]);
				LinkGene toNew = newLinkGene(sourceInnovations[i], newNodeInnovation, weights[i], linkInnovations[i], false);
				links.add(toNew);
			}
		}
		nodes.add(indexToAdd, ng);
		numOut++;
		if(addAtEnd) {
			assert archetypeIndexToAdd == -1 : "If adding at the end of the archetype, then this value should be -1 to indicate that: " + archetypeIndexToAdd;
			EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "new output"); // Adds at end (for Module Mutation)
		} else {
			assert archetypeIndexToAdd != -1 : "archetypeIndexToAdd should not be -1 in this case";
			EvolutionaryHistory.archetypeAdd(archetypeIndex, archetypeIndexToAdd, ng.clone(), false, "new output"); // adds at index (for Cascade Expansion)
		}
		return addedLinks.size();
	}

	/**
	 * adds output neurons to cppn for new substrate connections and biases when a cascade expansion occurs
	 * @param initialNumSubstratePairs number of substrate pairs before addition of new substrate
	 * @param numInitialHiddenSubstrates number of hidden substrates in HyperNEAT network
	 * @param numNewHiddenSubstrates number of new substrates that need new outputs in cppn
	 * @param ftypes activation functions of new cppn outputs starting with the outputs for the connections between the new substrates
	 * 		and their leo outputs if applicable then ending with the biases for these new substrates
	 * @return the number of output neurons that were added to the cppn
	 */
	public int addMSSNeuronsToCPPN(int initialNumSubstratePairs, int numInitialHiddenSubstrates, int numOutputsInPhenotype, int numNewHiddenSubstrates, int[] ftypes) {
		int numInitialCPPNOutputsForSubstratePairs = initialNumSubstratePairs * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair;
		//numNewCPPNOutputsForSubstratePairs is dependent on the number of new hidden substrates and the connections of these new substrates 
		int numNewCPPNOutputsForSubstratePairs = (numNewHiddenSubstrates + numNewHiddenSubstrates * numOutputsInPhenotype) * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair;
		int numAdditionalNodes = numNewCPPNOutputsForSubstratePairs + numNewHiddenSubstrates;
		int numAdditionalLinks = Parameters.parameters.integerParameter("numIncomingLinksForCascadeExpansion");
		long[] linkInnovations = new long[numAdditionalLinks];
		long[] sourceInnovations = new long[numAdditionalLinks];
		double[] weights = new double[numAdditionalLinks];
		int position = this.outputStartIndex() + numInitialCPPNOutputsForSubstratePairs;
		int archetypeAddIndex = EvolutionaryHistory.archetypes[archetypeIndex].size() - nodes.size() + position;
		//adding new outputs for substrate pairs
		for (int i = 0; i < numNewCPPNOutputsForSubstratePairs; i++) {
			for (int j = 0; j < numAdditionalLinks; j++) {
				sourceInnovations[j] = getRandomNonOutputNodeInnovationNumber();
				linkInnovations[j] = EvolutionaryHistory.nextInnovation();
				weights[j] = RandomNumbers.fullSmallRand();
			}
			addOutputNode(ftypes[i], sourceInnovations, weights, linkInnovations, position + i, false, archetypeAddIndex + i);
		}
		//new substrate biases will be inserted after the old hidden substrate biases and before the output substrate biases
		position += numNewCPPNOutputsForSubstratePairs + numInitialHiddenSubstrates;
		archetypeAddIndex = EvolutionaryHistory.archetypes[archetypeIndex].size() - nodes.size() + position;
		//adding new outputs for hidden biases
		for (int i = 0; i < numNewHiddenSubstrates; i++) {
			for (int j = 0; j < numAdditionalLinks; j++) {
				sourceInnovations[j] = getRandomNonOutputNodeInnovationNumber();
				linkInnovations[j] = EvolutionaryHistory.nextInnovation();
				weights[j] = RandomNumbers.fullSmallRand();
			}
			addOutputNode(ftypes[numNewCPPNOutputsForSubstratePairs + i], sourceInnovations, weights, linkInnovations, position + i, false, archetypeAddIndex + i);
		}
		this.neuronsPerModule += numAdditionalNodes;
		return numAdditionalNodes;
	}

	/**
	 * Return the index of a given innovation number within the list of node
	 * genes
	 *
	 * @param innovation Innovation number to search for
	 * @return Index in list where gene is located
	 */
	private int indexOfNodeInnovation(long innovation) {
		return indexOfGeneInnovation(innovation, nodes);
	}

	/**
	 * Return the index of a given innovation number within the list of link
	 * genes
	 *
	 * @param innovation Innovation number to search for
	 * @return Index in list where gene is located
	 */
	@SuppressWarnings("unused")
	private int indexOfLinkInnovation(long innovation) {
		return indexOfGeneInnovation(innovation, links);
	}

	private int indexOfGeneInnovation(long innovation, ArrayList<? extends Gene> genes) {
		for (int i = 0; i < genes.size(); i++) {
			if (genes.get(i).innovation == innovation) {
				return i;
			}
		}
		System.out.println("innovation " + innovation + " not found in net " + this.getId());
		return -1;
	}

	/**
	 * allows for a static method to call the crossover function for the TWEANN
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Genotype<TWEANN> crossover(Genotype<TWEANN> g) {
		return MMNEAT.crossoverOperator.crossover(this, g);
	}

	/**
	 * Generate and return phenotype TWEANN from genotype
	 *
	 * @return executable TWEANN
	 */
	@Override
	public TWEANN getPhenotype() {
		TWEANN result = new TWEANN(this);
		// This is the point where old parent module usage is finally erased
		this.moduleUsage = result.moduleUsage;
		return result;
	}

	/**
	 * Copies the TWEANNGenotype via the trick of generating a TWEANN, then
	 * using it to generate a new Genotype
	 *
	 * @return copy of genotype
	 */
	@Override
	public Genotype<TWEANN> copy() {
		int[] temp = moduleUsage;
		TWEANNGenotype result = new TWEANNGenotype(this.getPhenotype());
		// Module usage is erased by getPhenotype(), so it is restored here
		moduleUsage = temp;
		result.moduleUsage = new int[temp.length];
		System.arraycopy(this.moduleUsage, 0, result.moduleUsage, 0, moduleUsage.length);
		return result;
	}

	/**
	 * Get fresh new instance of genotype, in order to start evolution
	 *
	 * @return new genotype for starting population
	 */
	@Override
	public Genotype<TWEANN> newInstance() {
		TWEANNGenotype result;
		if (MMNEAT.ea instanceof MultiplePopulationGenerationalEA) {
			// Networks from different sub-populations could have differing
			// numbers of inputs and outputs
			result = new TWEANNGenotype(this.numIn, this.neuronsPerModule, this.archetypeIndex);
		} else {
			result = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, this.archetypeIndex);
		}
		result.moduleUsage = new int[result.numModules];
		return result;
	}

	/**
	 * Compares two genotypes to see if the same, but not necessarily the same
	 * reference. Gene nodes must have the same innovation number in the same
	 * order and must have the same link innovation numbers but not necessarily
	 * in the same order.
	 *
	 * @param m first TWEANNGenotype to be compared
	 * @param o second TWEANNGenotype to be compared
	 * @return true if structure is same, false if not
	 */
	public static boolean sameStructure(TWEANNGenotype m, TWEANNGenotype o) {

		// array lists of genotypes
		ArrayList<TWEANNGenotype.NodeGene> mGeno = m.nodes;
		ArrayList<TWEANNGenotype.NodeGene> oGeno = o.nodes;
		ArrayList<TWEANNGenotype.LinkGene> FakemLink = m.links;
		ArrayList<TWEANNGenotype.LinkGene> FakeoLink = o.links;
		ArrayList<TWEANNGenotype.LinkGene> mLink = new ArrayList<>();
		ArrayList<TWEANNGenotype.LinkGene> oLink = new ArrayList<>();

		// makes sure the only nodes included from link genotypes are those that
		// are active
		for (int i = 0; i < FakemLink.size(); i++) {
			if (FakemLink.get(i).isActive()) {
				mLink.add(FakemLink.get(i));
			}
		}
		for (int i = 0; i < FakeoLink.size(); i++) {
			if (FakeoLink.get(i).isActive()) {
				oLink.add(FakeoLink.get(i));
			}
		}

		if (mGeno.size() == oGeno.size() && mLink.size() == oLink.size()) {
			int nodeSize = mGeno.size();
			int linkSize = mLink.size();
			// gets the innovation numbers of the links as a long array
			long[] mlink = new long[linkSize];
			for (int i = 0; i < linkSize; i++) {
				mlink[i] = mLink.get(i).innovation;
			}
			long[] olink = new long[linkSize];
			for (int i = 0; i < linkSize; i++) {
				olink[i] = oLink.get(i).innovation;
			}
			// checks that link node innovation numbers are the same, but not
			// necessarily in order
			if (ArrayUtil.setEquality(olink, mlink))
				; else {
					return false;
				}
			// checks that gene node innovations are the same and are in the
			// right order
			for (int i = 0; i < nodeSize; i++) {
				if (mGeno.get(i).innovation != oGeno.get(i).innovation) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	/**
	 * A generic toString method
	 *
	 * @return String with ID, number of modules, and list of node and link genes
	 */
	@Override
	public String toString() {
		String result = id + " (modules:" + numModules + ")" + "\n" + this.nodes + "\n" + this.links;
		return result;
	}

	/**
	 * Checks to see if the input neurons are actually connected.
	 *
	 * @return Array for each input: true if connected, false otherwise.
	 */
	public boolean[] inputUsageProfile() {
		boolean[] result = new boolean[numIn];
		for (int i = 0; i < numIn; i++) {
			long inputInnovation = nodes.get(i).innovation;
			for (LinkGene l : links) {
				if (l.sourceInnovation == inputInnovation) {
					result[i] = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * randomly duplicates a module in the network
	 */
	public void moduleDuplication() {
		int module = RandomNumbers.randomGenerator.nextInt(this.numModules);
		duplicateModule(module);
	}

	/**
	 * Duplicate each individual neuron of an output module
	 *
	 * @param module module to duplicate
	 */
	private void duplicateModule(int module) {
		// One-module network is missing first preference neuron
		if (numModules == 1 && numOut == neuronsPerModule && TWEANN.preferenceNeuron()) {
			addRandomPreferenceNeuron(1);
		}
		int outputStart = outputStartIndex();
		// Duplicate the policy neurons
		int moduleStart = outputStart + (module * (neuronsPerModule + (TWEANN.preferenceNeuron() ? 1 : 0)));
		for (int i = 0; i < neuronsPerModule; i++) {
			duplicateOutputNeuron(moduleStart + i); // Add policy neurons
		}
		if (TWEANN.preferenceNeuron()) {
			addRandomPreferenceNeuron(1);
		}
		// Increase num modules
		this.numModules++;
	}

	/**
	 * Modifies archetype!
	 *
	 * Adds a new preference neuron randomly in network
	 *
	 * @param numInputs number of inputs from network in which to randomly
	 * 					place neuron
	 */
	public void addRandomPreferenceNeuron(int numInputs) {
		// Randomize the preference neuron
		double[] weights = new double[numInputs];
		long[] linkInnovations = new long[numInputs];
		long[] sourceInnovations = new long[numInputs];
		for (int j = 0; j < numInputs; j++) {
			sourceInnovations[j] = getRandomNonOutputNodeInnovationNumber();
			linkInnovations[j] = EvolutionaryHistory.nextInnovation();
			weights[j] = RandomNumbers.fullSmallRand();
		}
		addOutputNode(ActivationFunctions.newNodeFunction(), sourceInnovations, weights, linkInnovations);
	}

	/**
	 * Modifies archetype!
	 *
	 * Take a network without preference neurons (unimodal or multitask) and
	 * insert a preference neuron after a mode. In order to keep the innovation
	 * numbers sorted in decreasing negative order, the innovation numbers of
	 * the policy neurons need to be shifted.
	 *
	 * @param moduleIndex index of mode to give preference neuron
	 */
	public void insertPreferenceNeuron(int moduleIndex) {
		int outputStart = outputStartIndex();
		// Assume other modes before modeIndex have NOT had preference neurons added yet
		int desiredPreferenceLoc = outputStart + (moduleIndex * neuronsPerModule) + neuronsPerModule;
		assert desiredPreferenceLoc <= nodes.size() : "Desired location too high! desiredPreferenceLoc:"
				+ desiredPreferenceLoc + ",nodes.size()=" + nodes.size() + ",neuronsPerModule=" + neuronsPerModule
				+ ",outputStart=" + outputStart;
		// Node that will link into the new preference neuron
		long randomSourceInnovation = nodes.get(RandomNumbers.randomGenerator.nextInt(outputStart)).innovation;
		// Last mode?
		long newNodeInnovation;
		if (desiredPreferenceLoc == nodes.size()) {
			// Then just add the preference neuron ... easy
			newNodeInnovation = -(numIn + numOut) - 1;
			// Create the output node
			NodeGene ng = newNodeGene(ActivationFunctions.newNodeFunction(), TWEANN.Node.NTYPE_OUTPUT,
					newNodeInnovation);
			nodes.add(ng);
			EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "insert end preference");
		} else {
			NodeGene current = nodes.get(desiredPreferenceLoc);
			// Get the innovation num of node currently in that position
			newNodeInnovation = current.innovation;
			NodeGene newPref = newNodeGene(ActivationFunctions.newNodeFunction(), TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
			// Put preference neuron after mode and before next mode
			nodes.add(desiredPreferenceLoc, newPref);
			// Shift all subsequent innovation numbers
			for (int i = desiredPreferenceLoc + 1; i < nodes.size(); i++) {
				nodes.get(i).innovation--;
			}
			// Shift corresponding link targets
			for (LinkGene l : links) {
				if (l.sourceInnovation <= newNodeInnovation) {
					l.sourceInnovation--;
				}
				if (l.targetInnovation <= newNodeInnovation) {
					l.targetInnovation--;
				}
			}
			// The last node is actually the one with the new innovation, so it
			// is added to archetype
			EvolutionaryHistory.archetypeAdd(archetypeIndex, nodes.get(nodes.size() - 1).clone(),
					"insert middle preference");
		}
		// Add one random link to new preference neuron
		this.addLink(randomSourceInnovation, newNodeInnovation, RandomNumbers.fullSmallRand(), EvolutionaryHistory.nextInnovation());
		numOut++;
		// EvolutionaryHistory.archetypeOut[archetypeIndex]++;
	}

	/**
	 * Modifies archetype!
	 *
	 * Duplicates a single output neuron at index neuronIndex in nodes.
	 * Duplication means copying all links that go into the node, including the
	 * weights.
	 *
	 * @param neuronIndex index in nodes of neuron to duplicate
	 */
	private void duplicateOutputNeuron(int neuronIndex) {
		NodeGene n = nodes.get(neuronIndex);
		assert (n.ntype == TWEANN.Node.NTYPE_OUTPUT) : "Node to duplicate not an output node";
		// Slots are already reserved for future output nodes
		// Should this convention change?
		long newNodeInnovation = -(numIn + numOut) - 1;
		// Create the output node
		NodeGene ng = newNodeGene(ActivationFunctions.newNodeFunction(), TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
		// Copy all links from old node
		for (NodeGene p : nodes) {
			LinkGene lg = getLinkBetween(p.innovation, n.innovation);
			if (lg != null && lg.isActive()) {
				// Copy newNode if it exists
				LinkGene duplicate;
				if (p.innovation == n.innovation) {
					duplicate = newLinkGene(ng.innovation, ng.innovation, lg.weight, EvolutionaryHistory.nextInnovation(), false);
				} else {
					duplicate = newLinkGene(p.innovation, ng.innovation, lg.weight, EvolutionaryHistory.nextInnovation(), false);
				}
				links.add(duplicate);
			}
		}
		nodes.add(ng);
		numOut++;
		EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "duplicate output");
	}

	/**
	 * Freezes all preferences neurons along with all components that influence
	 * them.
	 */
	public void freezePreferenceNeurons() {
		assert TWEANN.preferenceNeuron() : "Cannot freeze preference neurons if there are none";
		// System.out.println("\tFreeze preference neurons in " + this.getId());
		int outputStart = this.outputStartIndex();
		for (int i = 0; i < numModules; i++) {
			int neuronIndex = outputStart + neuronsPerModule + (i * (neuronsPerModule + 1));
			long innovation = nodes.get(neuronIndex).innovation;
			freezeInfluences(innovation);
		}
	}

	/**
	 * Freeze policy output neurons, excluding the preference neurons from being
	 * frozen.
	 */
	public void freezePolicyNeurons() {
		assert TWEANN.preferenceNeuron() : "Cannot freeze policy neurons if there are no preference neurons";
		int outputStart = this.outputStartIndex();
		for (int i = 0; i < numModules; i++) {
			for (int j = 0; j < neuronsPerModule; j++) {
				int neuronIndex = outputStart + (i * (neuronsPerModule + 1)) + j;
				long innovation = nodes.get(neuronIndex).innovation;
				freezeInfluences(innovation);
			}
		}
	}

	/**
	 * If policy affecting components are currently frozen, then they are melted
	 * and the preference affecting components are frozen. If the preference
	 * affecting components are currently frozen, then they are melted and the
	 * policy affecting components are frozen.
	 *
	 * @return true if policy was frozen, false if preference was frozen
	 */
	public boolean alternateFrozenPreferencePolicy() {
		int outputStart = this.outputStartIndex();
		// Check preference neurons first so that in fresh networks with
		// nothing frozen, the preference neurons will be frozen first
		int firstPreference = outputStart + this.neuronsPerModule;
		// If first preference neuron is frozen, assume all are
		if (nodes.get(firstPreference).isFrozen()) {
			this.meltNetwork(); // melt preference
			this.freezePolicyNeurons();
			return true;
		} else {
			// Otherwise assume policy neurons are frozen
			this.meltNetwork(); // melt policy
			this.freezePreferenceNeurons();
			return false;
		}
	}

	/**
	 * Freeze node with nodeInnovation number as well as all links and nodes
	 * that affect the given node. In other words, the behavior of the specified
	 * node is frozen by recursively freezing all components that affect the
	 * given node.
	 *
	 * @param nodeInnovation Node to freeze
	 */
	public void freezeInfluences(long nodeInnovation) {
		HashSet<Long> otherOutputs = new HashSet<Long>();
		for (int i = nodes.size() - numOut; i < nodes.size(); i++) {
			NodeGene ng = nodes.get(i);
			if (ng.innovation != nodeInnovation) {
				// Don't allow recurrent connected to cascade across other
				// outputs
				// to freeze the whole network
				otherOutputs.add(ng.innovation);
			}
		}
		freezeInfluences(nodeInnovation, otherOutputs);
	}

	/**
	 * Freezes all innovation nodes that have not yet been visited
	 *
	 * @param nodeInnovation the innovation number to be frozen
	 * @param visited a hash set of innovation numbers that have been visited
	 */
	private void freezeInfluences(long nodeInnovation, HashSet<Long> visited) {
		int nodesIndex = indexOfNodeInnovation(nodeInnovation);
		assert nodesIndex != -1 : "Node to freeze (" + nodeInnovation + ") not in nodes list";
		nodes.get(nodesIndex).freeze();
		visited.add(nodeInnovation);
		for (LinkGene l : links) {
			if (l.targetInnovation == nodeInnovation) {
				l.freeze();
				if (!visited.contains(l.sourceInnovation)) {
					freezeInfluences(l.sourceInnovation, visited);
				}
			}
		}
	}

	/**
	 * Freeze all components that influence a particular module, both policy and
	 * preference.
	 *
	 * @param m module to freeze, 0-indexed
	 */
	public void freezeModule(int m) {
		int outputStart = this.outputStartIndex();
		int neuronsInModule = neuronsPerModule + (TWEANN.preferenceNeuron() ? 1 : 0);
		for (int j = 0; j < neuronsInModule; j++) {
			int neuronIndex = outputStart + (m * neuronsInModule) + j;
			long innovation = nodes.get(neuronIndex).innovation;
			freezeInfluences(innovation);
		}
	}

	/**
	 * Freeze whole network so components cannot be altered by mutation. Should
	 * only be used before adding a new module. The new module will be
	 * alterable.
	 */
	public void freezeNetwork() {
		for (NodeGene ng : nodes) {
			ng.freeze();
		}
		for (LinkGene lg : links) {
			lg.freeze();
		}
	}

	/**
	 * Undoes a freeze. Used during crossover, since crossing frozen networks
	 * may leave only frozen genes.
	 */
	public void meltNetwork() {
		for (NodeGene ng : nodes) {
			ng.melt();
		}
		for (LinkGene lg : links) {
			lg.melt();
		}
	}

	/**
	 * Returns the percentage of the time that the most-used module is used
	 *
	 * @return
	 */
	public double maxModuleUsage() {
		double[] dist = StatisticsUtilities.distribution(moduleUsage);
		if (dist.length == 0) {
			return 0;
		} else {
			return StatisticsUtilities.maximum(dist);
		}
	}

	/**
	 * Returns the percentage of the time that the least-used mode is used
	 *
	 * @return
	 */
	public double minModuleUsage() {
		double[] dist = StatisticsUtilities.distribution(moduleUsage);
		if (dist.length == 0) {
			return 0;
		} else {
			return StatisticsUtilities.minimum(dist);
		}
	}

	public double wastedModuleUsage(int maxModules) {
		double waste = 0;
		double[] dist = StatisticsUtilities.distribution(moduleUsage);
		for (int i = 0; i < dist.length; i++) {
			waste += Math.max(0, dist[i] - (1 / maxModules));
		}
		return waste / maxModules;
	}

	@Override
	public long getId() {
		return id;
	}

	/**
	 * NOT REALLY USED
	 * 
	 * This function gives a measure of compatibility between two
	 * TWEANNGenotypes by computing a linear combination of 3 characterizing
	 * variables of their compatibilty. The 3 variables represent PERCENT
	 * DISJOINT GENES, PERCENT EXCESS GENES, MUTATIONAL DIFFERENCE WITHIN
	 * MATCHING GENES. So the formula for compatibility is:
	 * disjoint_coeff*pdg+excess_coeff*peg+mutdiff_coeff*mdmg. The 3
	 * coefficients are global system parameters
	 *
	 * @param g genotype
	 * @return measure of compatability
	 */
	public double compatibility(TWEANNGenotype g) {

		// Innovation numbers
		long p1innov;
		long p2innov;

		// Intermediate value
		double mut_diff;

		// Set up the counters
		double num_disjoint = 0.0;
		double num_excess = 0.0;
		double mut_diff_total = 0.0;
		double num_matching = 0.0; // Used to normalize mutation_num differences

		LinkGene _gene1;
		LinkGene _gene2;

		double max_genome_size; // Size of larger Genome

		// Get the length of the longest Genome for percentage computations
		int size1 = this.links.size();
		int size2 = g.links.size();
		max_genome_size = Math.max(size1, size2);
		// Now move through the Genes of each potential parent
		// until both Genomes end
		int j;
		int j1 = 0;
		int j2 = 0;

		for (j = 0; j < max_genome_size; j++) {

			if (j1 >= size1) {
				num_excess += 1.0;
				j2++;
			} else if (j2 >= size2) {
				num_excess += 1.0;
				j1++;
			} else {
				_gene1 = links.get(j1);
				_gene2 = g.links.get(j2);

				// Extract current linkInnovations numbers
				p1innov = _gene1.innovation;
				p2innov = _gene2.innovation;

				if (p1innov == p2innov) {
					num_matching += 1.0;
					mut_diff = Math.abs(_gene1.weight - _gene2.weight);
					mut_diff_total += mut_diff;
					j1++;
					j2++;
				} else if (p1innov < p2innov) {
					j1++;
					num_disjoint += 1.0;
				} else if (p2innov < p1innov) {
					j2++;
					num_disjoint += 1.0;
				}
			}
		}
		/**
		 * Return the compatibility number using compatibility formula Note that
		 * mut_diff_total/num_matching gives the AVERAGE difference between
		 * mutation_nums for any two matching Genes in the Genome. Look at
		 * disjointedness and excess in the absolute (ignoring size)
		 */

		return ((num_disjoint / max_genome_size) + (num_excess / max_genome_size)
				+ 0.4 * (mut_diff_total / num_matching));
	}

	/**
	 * Sorts link genes by innovation number
	 *
	 * @param linkedGene ArrayList of link genes to be sorted
	 */
	public static void sortLinkGenesByInnovationNumber(ArrayList<LinkGene> linkedGene) {
		Collections.sort(linkedGene, new Comparator<LinkGene>() {
			@Override
			public int compare(LinkGene o1, LinkGene o2) {// anonymous class
				return (int) Math.signum(o1.innovation - o2.innovation);
			}
		});
	}

	/**
	 * Sort the nodes so that there are no backward facing links, meaning no recurrency. The result will
	 * be a feed-forward network, but the method will crash if there are connectivity loops. Input and
	 * output nodes will remain unchanged ... only the hidden neurons are shuffled around.
	 * This is essentially an implementation of Kahn's algorithm for topological sort.
	 * @param TWEANNGenotype
	 */
	public static void sortNodeGenesByLinkConnectivity(TWEANNGenotype tg) {
		// First remove links from output layer to hidden layer (why do these exist?)
		Iterator<LinkGene> itr = tg.links.iterator();
		while(itr.hasNext()) {
			LinkGene lg = itr.next();
			if(tg.getNodeWithInnovation(lg.sourceInnovation).ntype == TWEANN.Node.NTYPE_OUTPUT && 
					tg.getNodeWithInnovation(lg.targetInnovation).ntype == TWEANN.Node.NTYPE_HIDDEN) {
				//System.out.println("Removing link from output to hidden: "+lg);
				itr.remove(); // No more cycle
			}
		}
		// Key = target innovation number, value = set of innovation numbers for all source nodes with a link to this target
		HashMap<Long, Set<Long>> incoming = new HashMap<>();
		for(NodeGene n : tg.nodes) {
			// Insert empty set for each target
			incoming.put(n.innovation, new HashSet<>());
		}
		for(LinkGene lg : tg.links) {
			// Add innovation for each source neuron with a link to the target
			incoming.get(lg.targetInnovation).add(lg.sourceInnovation);
		}
		
		//System.out.println("DEBUG 596: "+incoming.get(596L));

		ArrayList<NodeGene> newNodes = new ArrayList<>(tg.nodes.size());
		LinkedList<NodeGene> noIncoming = new LinkedList<>();
		for(int i = 0; i < tg.numIn; i++) {
			// Start with the input neurons
			noIncoming.add(tg.nodes.get(i));
		}
		// Start Kahn's algorithm
		while(!noIncoming.isEmpty()) {
			NodeGene first = noIncoming.remove(0); // First node with no incoming
			//System.out.println("Consider: " + first);
			// Don't add output neurons. They must be added at the end
			//if(first.ntype != TWEANN.Node.NTYPE_OUTPUT) {
			//System.out.println("\tAdd newNodes: " + first);
			newNodes.add(first); // Node appears at end of new node list
			//}
			for(LinkGene lg : tg.links) { // Bit inefficient
				if(lg.sourceInnovation == first.innovation) { // A link from the node aded to the list
					//System.out.println("\tRemove link: " + lg);
					incoming.get(lg.targetInnovation).remove(lg.sourceInnovation); // Remove that link
					if(incoming.get(lg.targetInnovation).isEmpty()) { // No more incoming links
						// Add to noIncoming list
						for(NodeGene ng : tg.nodes) {
							if(ng.innovation == lg.targetInnovation && ng.ntype != TWEANN.Node.NTYPE_OUTPUT) {
								//System.out.println("\tAdd noIncoming: " + ng);
								noIncoming.add(ng);
								break;
							}
						}
					}
				}
			}
		}
		// Add output nodes
		for(int i = 0; i < tg.numOut; i++) {
			newNodes.add(tg.nodes.get(tg.outputStartIndex() + i));
		}
		
		// Loops through to add back any nodes that went missing after topological sort
		// they are added after all the hidden neurons and before the output neurons
//		for(int i = 0; i < tg.nodes.size(); i++) {
//			if(getNodeGene(tg.nodes.get(i).innovation,newNodes) == null) {
//				newNodes.add(newNodes.size() - tg.numOut, tg.nodes.get(i));
//			}
//		}
		
		
		// Replace nodes with sorted nodes
		tg.nodes = newNodes;

		// Loop through tg.links and remove any link whose source or target innovation number
		// does not have a corresponding node in tg.nodes
		itr = tg.links.iterator();
		while(itr.hasNext()) {
			LinkGene lg = itr.next();
			NodeGene ngSource = getNodeGene(lg.sourceInnovation, tg.nodes);
			NodeGene ngTarget = getNodeGene(lg.targetInnovation, tg.nodes);
			if(ngSource == null || ngTarget == null) {
				itr.remove(); // No more cycle
			}
		}
		
	}
	
	/**
	 * Return given NodeGene in nodes list if it exists, or null otherwise
	 * @param innovation Innovation number
	 * @return NodeGene or null if absent
	 */
	public static NodeGene getNodeGene(long innovation, List<NodeGene> nodes) {
		//this.nodes;
		for(int i = 0; i < nodes.size(); i++) {
			if(innovation == nodes.get(i).innovation) {
				return nodes.get(i);
			}
		}
		return null;
	}
	

	/**
	 * Indicates whether there is any path from one node gene to another.
	 * ASSUMES NO LOOPS IN GENOME STRUCTURE!
	 * @param from Starting node gene innovation
	 * @param to Target node gene innovation
	 * @param links Links of whole network
	 * @return Whether there is a sequence of links that eventually get to "to" from "from"
	 */
	@SuppressWarnings("unused")
	private static boolean existsPath(long from, long to, List<LinkGene> links) {
		for(LinkGene lg : links) {
			if(from == lg.sourceInnovation) {
				return to == lg.targetInnovation || existsPath(lg.targetInnovation, to, links);
			}
		}
		return false; // No outgoing link genes
	}

	/**
	 * finds the biggest innovation number in a TWEANN genotype
	 *
	 * @return long corresponding to biggest innovation number
	 */
	public long biggestInnovation() {
		long max = Integer.MIN_VALUE; // Max innovation could actually be negative.
		for (NodeGene ng : nodes) {
			if (ng.innovation > max) {
				max = ng.innovation;
			}
		}
		for (LinkGene lg : links) {
			if (lg.innovation > max) {
				max = lg.innovation;
			}
		}
		return max;
	}

	/**
	 * Return position of first output neuron
	 *
	 * @return
	 */
	public int outputStartIndex() {
		return nodes.size() - numOut;
	}
	
	/**
	 * Returns a string of the contents of the GraphViz file that
	 * would draw a network
	 * 
	 * @return String of the contents in GraphViz file
	 */
	public String toGraphViz(String[] inputs, String[] outputs) {
		StringBuilder string = new StringBuilder();
		string.append("digraph {\nnode[fontsize=9 height=0.2 shape=circle width=0.2]");
		
		HashMap<Long,String> innovationToLabel = new HashMap<>();
		
		for(int i=0; i < nodes.size(); i++) {
			if(i >= nodes.size() - numOut) {
				assert nodes.get(i).ntype == TWEANN.Node.NTYPE_OUTPUT;
				string.append("\n" + "\"" + outputs[i - (nodes.size() - numOut)]+ "\"");
				string.append("[fillcolor=lightblue style=filled]");
				innovationToLabel.put(nodes.get(i).innovation,outputs[i - (nodes.size() - numOut)]);
			}
			else if(i < numIn) {
				assert nodes.get(i).ntype == TWEANN.Node.NTYPE_INPUT;
				string.append("\n" + "\"" + inputs[i] + "\"");
				string.append("[fillcolor=lightgray shape=box style=filled]");
				innovationToLabel.put(nodes.get(i).innovation,inputs[i]);
			}
			else {
				assert i >= numIn;
				assert i < nodes.size() - numOut;
				assert nodes.get(i).ntype == TWEANN.Node.NTYPE_HIDDEN;
				string.append("\n" + nodes.get(i).innovation);
				string.append("[fillcolor=white style=filled]");
			}
		}
	
		for(LinkGene lg : links) {
			if(innovationToLabel.containsKey(lg.sourceInnovation))
				string.append("\n" + "\""  + innovationToLabel.get(lg.sourceInnovation)+ "\"");
			else
				string.append("\n"+ lg.sourceInnovation);
			
			if(innovationToLabel.containsKey(lg.targetInnovation))
				string.append("->" + "\"" + innovationToLabel.get(lg.targetInnovation)+ "\"");
			else
				string.append("->"+ lg.targetInnovation);
			
			String color;
			if(lg.weight < 0) {
				color = "red";
			} else {
				assert lg.weight > 0;
				color = "green";
			}
			
			string.append("[color="+color+" penwidth="+Math.abs(lg.weight)+" style=solid]");
		}
		
		string.append("}");
		
		return string.toString();
	}

	/**
	 * Equals method that compares memory addresses of two TWEANNGenotypes
	 *
	 * @param o An object that should be a TWEANNGenotype
	 * @return returns true if same TWEANNGenotype, false if not
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TWEANNGenotype)) {
			return false;
		}
		TWEANNGenotype other = (TWEANNGenotype) o;
		return id == other.id;
	}
}
