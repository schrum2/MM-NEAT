package edu.southwestern.networks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NormalizedMemoryNodeGene;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.networks.hyperneat.HyperNEATVisualizationUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.CombinatoricUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.Plot;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * My version of a Topology and Weight Evolving Neural Network. Nodes are stored
 * in a linear order that must obey the following constraints: 1) All input
 * nodes are at the far left 2) All output nodes are at the far right 3)
 * Feedforward links only go from left to right 4) Links from right to left are
 * always recurrent
 *
 * This is a bit more restrictive than the standard NEAT networks. The benefit
 * of it is that signals can propagate from input to output in a single
 * activation, unlike standard NEAT networks that only have activations travel
 * one link at a time.
 *
 * @author Jacob Schrum
 */
public class TWEANN implements Network {

	// Variables used for watching the behavior of an active network in a
	// graphical display
	public static int NETWORK_VIEW_DIM;
	public static final int NODE_DIM = 6;
	public static final int DISPLAY_BORDER = 25;
	public static final int LINK_CURVE_OFFSET = 7;
	public static DrawingPanel panel = null;
	public static DrawingPanel inputPanel = null;
	public static DrawingPanel preferenceNeuronPanel = null;
	public static List<DrawingPanel> subsPanel = null;
	public static ArrayList<Double>[] preferenceActivationHistory = null;

	// subclass for a synaptic link between nodes
	public class Link {

		public final Node target;
		public final double weight;
		public final long innovation;
		public final boolean recurrent;
		public final boolean frozen;
		public final int moduleSource;

		@Override
		public String toString() {
			String result = "";
			result += "(" + innovation + ":" + weight + ":" + target.innovation + ":"
					+ (recurrent ? "recurrent" : "forward") + ")";
			return result;
		}

		/**
		 * Make new link
		 *
		 * @param target
		 *            Node that link leads to
		 * @param weight
		 *            synaptic weight
		 * @param innovation
		 *            innovation number of link
		 * @param recurrent
		 *            whether link is recurrent
		 * @param frozen
		 *            whether link can be changed by mutation
		 */
		public Link(Node target, double weight, long innovation, boolean recurrent, boolean frozen, int moduleSource) {
			this.target = target;
			this.weight = weight;
			this.innovation = innovation;
			this.recurrent = recurrent;
			this.frozen = frozen;
			this.moduleSource = moduleSource;
		}

		protected void transmit(double signal) {			
			assert target != null : "Link target is null? " + innovation + " with weight " + weight;
			assert!Double.isNaN(target.sum) : "target.sum is NaN before transmit";
			assert!Double.isNaN(signal) : "signal is NaN before transmit";
			assert!Double.isNaN(weight) : "weight is NaN before transmit";
			assert!Double.isNaN(signal * weight) : "signal * weight is NaN before transmit: " + signal + "*" + weight;
			//if(target.innovation == 9) System.out.print(" to "+target.innovation+ ":" + target.sum + " += receiving " + signal + "*"+weight+"; ");
            target.sum += (signal * weight);
            //if(target.innovation == 9) System.out.println("new sum:" + target.sum);
			assert!Double.isNaN(target.sum) : "target.sum is NaN after transmit: " + signal + "*" + weight;
		}
	}

	// subclass for a single neuron
	public class Node {

		// Three types of neurons
		public static final int NTYPE_INPUT = 0;
		public static final int NTYPE_HIDDEN = 1;
		public static final int NTYPE_OUTPUT = 2;
		// Networks never change after being created (only the genotypes evolve)
		public final int ntype;
		public final int ftype;
		public final long innovation;
		public final boolean frozen;
		public final double bias;
		// Outgoing links
		public List<Link> outputs;
		protected double sum;
		protected double activation;
		// Used when displaying the network graphically
		public int displayX = 0;
		public int displayY = 0;

		public boolean isLinkRecurrent(long targetInnovation) {
			for (Link l : outputs) {
				if (l.target.innovation == targetInnovation) {
					return l.recurrent;
				}
			}
			throw new IllegalArgumentException("The targetInnovation (" + targetInnovation + ") was not found in " + outputs);
		}

		@Override
		public String toString() {
			String result = "";
			result += innovation + ":";
			result += ntypeName(ntype) + ":";
			result += ActivationFunctions.activationName(ftype) + ":";
			result += "bias = " + bias + ":";
			result += "Sum = " + sum + ":";
			//result += outputs;
			return result;
		}

		/**
		 * For textual display of network
		 *
		 * @param ntype
		 *            One of the specified neuron types
		 * @return String label for type of neuron
		 */
		private String ntypeName(int ntype) {
			switch (ntype) {
			case NTYPE_INPUT:
				return "Input";
			case NTYPE_HIDDEN:
				return "Hidden";
			case NTYPE_OUTPUT:
				return "Output";
			}
			// Should never reach
			System.out.println(ntype + " is not a valid type of neuron");
			System.exit(1);
			return "ERROR";
		}

		public Node(int ftype, int ntype, long innovation) {
			this(ftype, ntype, innovation, 0.0);
		}
		
		/**
		 * New node with no targets, not frozen by default
		 *
		 * @param ftype
		 *            = type of activation function
		 * @param ntype
		 *            = type of node: input, hidden, output
		 * @param innovation
		 *            = unique innovation number for node
                 * @param bias
                 *            = bias offset added to neuron sum before activation function.
                 *              (primarily needed by substrate networks from CPPNs)
		 */
		public Node(int ftype, int ntype, long innovation, double bias) {
			this(ftype, ntype, innovation, false, bias);
		}

		/**
		 * New node with no targets
		 *
		 * @param ftype
		 *            = type of activation function
		 * @param ntype
		 *            = type of node: input, hidden, output
		 * @param innovation
		 *            = unique innovation number for node
		 * @param frozen
		 *            = true if new link mutations cannot target this node
                 * @param bias
                 *            = bias offset added to neuron sum before activation function.
                 *              (primarily needed by substrate networks from CPPNs)
		 */
		public Node(int ftype, int ntype, long innovation, boolean frozen, double bias) {
			this.innovation = innovation;
			this.ftype = ftype;
			this.ntype = ntype;
			this.frozen = frozen;
			this.bias = bias;
			outputs = new LinkedList<Link>();
			flush();
		}

		/**
		 * An input is added to the sum in case it holds recurrent activation
		 *
		 * @param input
		 *            sensor input
		 */
		protected void load(double input) {
			assert!Double.isNaN(sum) : "sum is NaN before load";
			assert!Double.isNaN(input) : "input is NaN in load";
			sum += input;
			assert!Double.isNaN(sum) : "sum is NaN after loading " + input;
		}

		public double output() {
			return activation;
		}
		
		/**
		 * Should only be used for testing purposes
		 * @param a new activation
		 */
		public void artificiallySetActivation(double a) {
			activation = a;
		}

		/**
		 * Used when network enters new environment and should no longer
		 * remember anything.
		 */
		protected final void flush() {
			sum = bias;
			activation = 0.0;
		}

		private void activate() {
			activation = ActivationFunctions.activation(ftype, sum);
		}

		protected void activateAndTransmit() {
            activate();
            // reset sum to original bias after activation 
			sum = bias;

			for (Link l : outputs) {
				l.transmit(activation);
			}
		}
		
		/**
		 * Current value of internal sum variable
		 * @return
		 */
		public double getSum() {
			return sum;
		}
		
		/**
		 * Current value of internal activation variable
		 * @return
		 */
		public double getActivation() {
			return activation;
		}

		/**
		 * Creates connection from this Node to target Node via a new Link.
		 * The module source for the link is -1 because this feature only makes
		 * sense for HyperNEAT networks.
		 *
		 * @param target
		 *            Node to link to
		 * @param weight
		 *            synaptic weight of link between nodes
		 * @param innovation
		 *            Innovation number of new Link
		 * @param recurrent
		 *            whether or not link is recurrent
		 * @param frozen
		 *            whether or not link can be changed
		 */
		protected void connect(Node target, double weight, long innovation, boolean recurrent, boolean frozen) {
			connect(target, weight, innovation, recurrent, frozen, -1);
		}
		
		/**
		 * Same as above, but specifies the module that defined the link
		 * (meaning that a CPPN module defined this substrate network link)
		 * 
		 * @param target
		 * @param weight
		 * @param innovation
		 * @param recurrent
		 * @param frozen
		 * @param linkModuleSource
		 */
		protected void connect(Node target, double weight, long innovation, boolean recurrent, boolean frozen, int linkModuleSource) {
			Link l = new Link(target, weight, innovation, recurrent, frozen, linkModuleSource);
			outputs.add(l);
		}

		/**
		 * Returns true if the node is connected to another node with a given
		 * innovation number.
		 *
		 * @param innovation
		 *            innovation number of target to search for
		 * @return whether the node is connected
		 */
		protected boolean isConnectedTo(long innovation) {
			for (Link l : outputs) {
				if (l.target.innovation == innovation) {
					return true;
				}
			}
			return false;
		}
	}

	public long getId() { return id; };
	
	private long id = -1;
	protected int numIn;
	protected int numOut;
	private int numModes;
	
	private int neuronsPerModule;
	private boolean standardMultitask;
	private int presetMode;
	// Only used with Hierarchical Multitask Networks
	private boolean[] viableModes;
	// HMT associates each module with a multitask mode
	public int[] moduleAssociations;
	public ArrayList<Node> nodes;
	public int[] moduleUsage;
	private double[] preferenceFatigue;
	public int chosenModule = 0;
	public boolean canDraw = true;
	public final int archetypeIndex;
	public final int outputStart;
	public static boolean finalPassOnOutputActivation = Parameters.parameters.booleanParameter("finalPassOnOutputActivation");

	/**
	 * Whether or not networks being used can/do have preference neurons
	 *
	 * @return true if preference neurons should be included
	 */
	public static boolean preferenceNeuron() {
		double mmpRate = Parameters.parameters.doubleParameter("mmpRate");
		double mmrRate = Parameters.parameters.doubleParameter("mmrRate");
		double mmdRate = Parameters.parameters.doubleParameter("mmdRate");
		double fullMMRate = Parameters.parameters.doubleParameter("fullMMRate");
		int startingModes = Parameters.parameters.integerParameter("startingModes");
		boolean multitaskCombiningCrossover = Parameters.parameters.booleanParameter("multitaskCombiningCrossover");
		return (!multitaskCombiningCrossover || startingModes > 1 || mmpRate > 0 || mmrRate > 0 || mmdRate > 0
				|| fullMMRate > 0);
	}

	/**
	 * Sets all modules as viable. This is the default. Only Hierarchical
	 * Multitask Networks would change these settings.
	 */
	private void allViable() {
		viableModes = new boolean[numModes];
		Arrays.fill(viableModes, true);
	}

	/**
	 * Creates new random TWEANN
	 *
	 * @param numIn
	 *            Number of input sensors
	 * @param numOut
	 *            For Multitask, the actual number of outputs. Otherwise the
	 *            number of policy neurons per mode.
	 * @param featureSelective
	 *            Outputs are only sparsely connected to start if this is true,
	 *            fully connected otherwise
	 * @param ftype
	 *            Type of activation function on neurons
	 * @param numModes
	 *            The number of modes IF the network is multitask. Otherwise
	 *            this should be 1, even if starting with multiple modes with
	 *            preference neurons
	 * @param archetypeIndex
	 *            = archetype to align with for crossover
	 */
	public TWEANN(int numIn, int numOut, boolean featureSelective, int ftype, int numModes, int archetypeIndex) {
		NETWORK_VIEW_DIM = (Parameters.parameters.booleanParameter("hyperNEAT")) ? Parameters.parameters.integerParameter("hyperNEATNetworkDisplaySize") : 500;
		this.archetypeIndex = archetypeIndex;
		this.numIn = numIn;
		this.moduleUsage = new int[numModes];
		this.preferenceFatigue = new double[numModes];

		int startingPrefModes = Parameters.parameters.integerParameter("startingModes");
		// numModes only applies for Multitask
		if (numModes == 1) {
			int startingModes = startingPrefModes;
			// Either a new mode-mutation network or a unimodal network
			this.neuronsPerModule = numOut;
			this.standardMultitask = false;
			// Put preference neuron in place now if it will be needed later
			if (preferenceNeuron()) {
				numOut++;
			}
			this.numModes = startingModes;
			numOut *= startingModes;
		} else {
			// A Multitask network
			this.neuronsPerModule = numOut / numModes;
			this.numModes = numModes;
			this.standardMultitask = true;
			if (numOut % numModes != 0 || numOut % neuronsPerModule != 0) {
				System.out.println("Multitask network initialized wrong: ");
				System.out.println("numOut = " + numOut);
				System.out.println("numModes = " + numModes);
				System.out.println("neuronsPerMode = " + neuronsPerModule);
				throw new IllegalArgumentException("Multitask network initialized wrong: numOut = " + numOut
						+ ", numModes = " + numModes + ",neuronsPerMode = " + neuronsPerModule);
			}
		}
		this.numOut = numOut;

		nodes = new ArrayList<Node>(numIn + numOut);

		long innovation = -1;
		for (int i = 0; i < numIn; i++) {
			Node n = new Node(CommonConstants.inputsUseID ? ActivationFunctions.FTYPE_ID : ftype, Node.NTYPE_INPUT, innovation--);
			nodes.add(n);
		}

		for (int i = 0; i < numOut; i++) {
			Node n = new Node(ftype, Node.NTYPE_OUTPUT, innovation--);
			nodes.add(n);
		}

		long linkInnovationBound = innovation - 1;

		int linksPer = Parameters.parameters.integerParameter("fsLinksPerOut");
		for (int j = 0; j < numOut; j++) {
			int[] inputSources;
			if (preferenceNeuron() && j % (neuronsPerModule + 1) == neuronsPerModule) {
				// Preference neurons alway start with only one link
				inputSources = RandomNumbers.randomDistinct(1, numIn);
			} else if (featureSelective) {
				// Select linksPer inputs for each output
				inputSources = RandomNumbers.randomDistinct(linksPer, numIn);
			} else {
				// Every input connects to each output
				inputSources = new int[numIn];
				for (int i = 0; i < numIn; i++) {
					inputSources[i] = i;
				}
			}

			for (int i = 0; i < inputSources.length; i++) {
				Node out = nodes.get(numIn + j);
				// Innovation choice assures that similar links match up, even
				// with FS on
				nodes.get(inputSources[i]).connect(out, RandomNumbers.fullSmallRand(),
						linkInnovationBound - (j * numIn) - inputSources[i], false, false);
			}
		}
		outputStart = nodes.size() - numOut;

		// In a new network, each Multitask mode has one network module.
		// This is really only needed if hierarchicalMultitask is true.
		moduleAssociations = new int[this.numModes];
		for (int i = 0; i < this.numModes; i++) {
			// Modulus splits modules up evenly among modes
			moduleAssociations[i] = i % startingPrefModes;
			// System.out.println(Arrays.toString(modeAssociations));
		}
		allViable();
	}

	/**
	 * Create TWEANN based on TWEANNGenotype, which can encode an arbitrary
	 * network.
	 *
	 * @param g
	 *            The genotype
	 */
	public TWEANN(TWEANNGenotype g) {
		NETWORK_VIEW_DIM = (Parameters.parameters != null && Parameters.parameters.booleanParameter("hyperNEAT")) ? Parameters.parameters.integerParameter("hyperNEATNetworkDisplaySize") : 500;
		this.archetypeIndex = g.archetypeIndex;
		this.id = g.getId();
		this.nodes = new ArrayList<Node>(g.nodes.size());

		int countIn = 0;
		int countOut = 0;

		int section = Node.NTYPE_INPUT;
		for (int i = 0; i < g.nodes.size(); i++) {
			TWEANNGenotype.NodeGene ng = g.nodes.get(i);
			Node n = ng instanceof NormalizedMemoryNodeGene ? 
					new NormalizedMemoryNode(this, ng.ftype, ng.ntype, ng.innovation, ng.isFrozen(), ng.getBias(), ng.getMemoryGamma(), ng.getMemoryBeta()): 
					new Node(ng.ftype, ng.ntype, ng.innovation, ng.isFrozen(), ng.getBias());
			switch (ng.ntype) {
			case Node.NTYPE_INPUT:
				assert(section == Node.NTYPE_INPUT) : "Genome encoded false network: inputs: \n" + g;
				countIn++;
				break;
			case Node.NTYPE_HIDDEN:
				if (section != Node.NTYPE_HIDDEN) {
					assert(section == Node.NTYPE_INPUT) : "Genome encoded false network: hidden\n" + g;
					section = Node.NTYPE_HIDDEN;
				}
				break;
			case Node.NTYPE_OUTPUT:
				if (section != Node.NTYPE_OUTPUT) {
					assert(section == Node.NTYPE_HIDDEN
							|| section == Node.NTYPE_INPUT) : "Genome encoded false network: output\n" + g;
					section = Node.NTYPE_OUTPUT;
				}
				countOut++;
				break;
			}
			nodes.add(n);
		}

		this.numIn = countIn;
		this.numOut = countOut;
		this.numModes = g.numModules;
		this.neuronsPerModule = g.neuronsPerModule;
		this.standardMultitask = g.standardMultitask;
		moduleAssociations = new int[this.numModes];
		for (int i = 0; i < this.numModes; i++) {
			moduleAssociations[i] = i;
		}
		assert(numModes == 1
				|| numOut == (neuronsPerModule + (standardMultitask ? 0 : 1))
				* numModes) : "multitask:" + standardMultitask + "\n" + "Wrong number of outputs (" + numOut
		+ ") for the number of modes (" + numModes + ")";

		this.moduleUsage = new int[numModes];
		this.preferenceFatigue = new double[numModes];

		for (LinkGene lg : g.links) {
			if (lg.isActive()) {
				Node source = getNode(lg.sourceInnovation);
				Node target = getNode(lg.targetInnovation);
				assert(target != null) : "No target: " + lg + "\nNet:" + g.getId();
				assert(source != null) : "How could the source be null?";
				source.connect(target, lg.weight, lg.innovation, lg.isRecurrent(), lg.isFrozen(), lg.getModuleSource());
			}
		}
		outputStart = nodes.size() - numOut;
		allViable();
	}

	// Getters
	@Override
	public int[] getModuleUsage() {
		return moduleUsage;
	}

	@Override
	public int numInputs() {
		return numIn;
	}

	@Override
	public int numOutputs() {
		return numOut;
	}

	@Override
	public int effectiveNumOutputs() {
		return this.neuronsPerModule;
	}

	@Override
	public int numModules() {
		return numModes;
	}

	public int neuronsPerModule() {
		return neuronsPerModule;
	}

	@Override
	public boolean isMultitask() {
		return standardMultitask;
	}

	// Can this be removed? It was only included to contrast with heirarchical multitask, which is no longer a thing
	public boolean isStandardMultitask() {
		return standardMultitask;
	}

	/**
	 * Tell network to use the designated mode on the next process. 
	 * @param mode to use
	 */
	@Override
	public void chooseMode(int mode) {
		presetMode = mode;
	}

	@Override
	public int lastModule() {
		return chosenModule;
	}

	/**
	 * Take array of inputs to network and process them to produce an output
	 * vector.
	 *
	 * @param inputs
	 *            sensor readings for the network
	 * @return network output (single module)
	 */
	@Override
	public double[] process(double[] inputs) {
		assert(inputs.length == numIn) : "Input mismatch! numIn = " + numIn + "\n" + "inputs.length = " + inputs.length + "\n" + Arrays.toString(inputs);
		assert(numIn <= nodes.size()) : "Input mismatch! numIn = " + numIn + "\n" + "nodes.size() = " + nodes.size() + "\n" + nodes;

		try {
			// Load inputs
			for (int i = 0; i < numIn; i++) {
				assert !Double.isNaN(inputs[i]) : "Input " + i + " is NaN!" + Arrays.toString(inputs);
				nodes.get(i).load(inputs[i]);
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("CRASH! inputs = " + Arrays.toString(inputs));
			throw e;
		}

		// Activate nodes in forward order
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).activateAndTransmit();
		}
		
		// Option: if there is remaining activation in the output neurons, 
		// then process it. Used when importing CPPNs from original Picbreeder
		if(finalPassOnOutputActivation) {
			for(int i = this.outputStart; i < nodes.size(); i++) {
				// This means input arrived after last reset
				if(nodes.get(i).sum != nodes.get(i).bias) {
					nodes.get(i).activateAndTransmit();
				}
			}
		}
		
		// All outputs

		double[] preferences = new double[numModes];
		if (numModes == 1) {
			preferences[0] = 1.0;
		} else if (standardMultitask) { // But NOT Hierarchical Multitask
			preferences[presetMode] = 1.0;
		} else {
			for (int m = 0; m < numModes; m++) {
				Node out = nodes.get(outputStart + (m * (neuronsPerModule + 1)) + neuronsPerModule);
				// Inviable modes have minimal preference
				preferences[m] = viableModes[m] ? out.output() : -Double.MAX_VALUE;
			}
		}
		// subtract fatigue
		for (int i = 0; i < preferenceFatigue.length; i++) {
			preferences[i] -= preferenceFatigue[i];
		}

		if (CommonConstants.disabledMode >= 0) {
			// Can never be selected
			preferences[CommonConstants.disabledMode] = -2; 
		}

		if (canDraw && preferenceNeuronPanel != null && numModes > 1 && !standardMultitask) {
			refreshPreferencePlot(preferenceNeuronPanel, preferences);
		}

		// determine winner
		chosenModule = CommonConstants.softmaxModeSelection 
				? StatisticsUtilities.softmax(preferences, CommonConstants.softmaxTemperature)
						: StatisticsUtilities.argmax(preferences);

				this.moduleUsage[chosenModule]++;

				// add new fatigue
				preferenceFatigue[chosenModule] += CommonConstants.preferenceNeuronFatigueUnit;
				// decay fatigue
				for (int i = 0; i < preferenceFatigue.length; i++) {
					if (i != chosenModule) { // don't decay chosen mode
						preferenceFatigue[i] *= CommonConstants.preferenceNeuronDecay;
					}
				}

		double[] outputs = new double[neuronsPerModule];
		if (CommonConstants.weightedAverageModeAggregation) {
			// Calculate weighted average across all modes
			for (int i = 0; i < outputs.length; i++) {
				for (int j = 0; j < numModes; j++) {
					int modeStart = outputStart + (j * (neuronsPerModule + 1));
					outputs[i] += preferences[j] * nodes.get(modeStart + i).output();
				}
				outputs[i] /= numModes;
			}
		} else {
			outputs = moduleOutput(chosenModule);
		}

		if (canDraw) {
			if(!HyperNEATCPPNGenotype.constructingNetwork && CommonConstants.hyperNEAT && CommonConstants.monitorSubstrates) {
				animateSubstrate();
			}
			if (panel != null && Parameters.parameters.booleanParameter("animateNetwork")) {
				draw(panel);
			}
			if (inputPanel != null) {
				assert inputs.length == numIn : "Too many inputs: " + numIn + ":" + Arrays.toString(inputs);
				refreshActivation(inputPanel, inputs, outputs, preferences, standardMultitask, preferenceFatigue);
			}
		}		
		return outputs;
	}

	/**
	 * After processing, the neurons retain their activations. Therefore, the
	 * output values can be accessed for any mode, not just the chosen one.
	 *
	 * @param mode
	 *            mode outputs to access
	 * @return outputs of specific mode
	 */
	@Override
	public double[] moduleOutput(int mode) {
		int selectedModeStart = outputStart + (mode * (neuronsPerModule + (standardMultitask ? 0 : 1)));
		double[] outputs = new double[neuronsPerModule];
		for (int i = 0; i < neuronsPerModule; i++) {
			Node out = nodes.get(selectedModeStart + i);
			outputs[i] = out.output();
		}
		return outputs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void flush() {
		// System.out.println("Flush: " + id);
		for (Node n : nodes) {
			n.flush();
		}
		if (canDraw && preferenceNeuronPanel != null && !standardMultitask && numModes > 1) {
			preferenceActivationHistory = new ArrayList[numModes];
			for (int i = 0; i < preferenceActivationHistory.length; i++) {
				preferenceActivationHistory[i] = new ArrayList<Double>();
			}
		}
		this.preferenceFatigue = new double[numModes];
		if (inputPanel != null) {
			refreshActivation(inputPanel, new double[numIn], new double[neuronsPerModule], new double[numModes],
					standardMultitask, new double[numModes]);
		}
	}

	private Node getNode(long targetInnovation) {
		Node targetNode = null;
		for (Node n : nodes) {
			if (n.innovation == targetInnovation) {
				targetNode = n;
				break;
			}
		}
		assert targetNode != null : "No node with innovation " + targetInnovation + " existed";
		return targetNode;
	}

	@Override
	public String toString() {
		String result = "";
		result += numIn + " Inputs\n";
		result += numOut + " Outputs\n";
		result += numModes + " Modes\n";
		result += "Forward\n";
		for (int i = 0; i < nodes.size(); i++) {
			result += nodes.get(i) + "\n";
		}
		return result;
	}

	public void draw(DrawingPanel panel) {
		draw(panel, false);
	}

	public void draw(DrawingPanel panel, boolean showInnovationNumbers) {
		draw(panel, showInnovationNumbers, false);
	}

	private void refreshPreferencePlot(DrawingPanel preferenceNeuronPanel, double[] preferences) {
		// Update preference activation history
		preferenceNeuronPanel.clear();
		for (int i = 0; i < preferenceActivationHistory.length; i++) {
			preferenceActivationHistory[i].add(preferences[i]);
			GraphicsUtil.linePlot(preferenceNeuronPanel, -1, 1, preferenceActivationHistory[i],
					CombinatoricUtilities.colorFromInt(i));
		}
	}

	// Only used within substrate networks to visualize substrate activations
	private List<Substrate> substrateInformation = null;
	public void passSubstrateInformation(List<Substrate> substrateInformation) {
		this.substrateInformation = substrateInformation;
	}
	
	/**
	 * Creates and updates visuals of substrates used by h-neat tetris task 
	 */
	public void animateSubstrate() {
//			for(Substrate s : substrateInformation) { 
//				System.out.println(s);
//			}
			HyperNEATVisualizationUtil.drawSubstrates(nodes, substrateInformation);
			//tweannGenotype has getLinkBetween
			//just need to find a way to get neuron innovation numbers
	}

	private static void refreshActivation(DrawingPanel inputPanel, double[] inputs, double[] outputs, double[] preferences, boolean multitask, double[] preferenceFatigue) {
		NetworkTask task = (NetworkTask) MMNEAT.task;
		String[] labels = task.sensorLabels();
		assert labels.length == inputs.length : "Need correspondence between inputs and labels: " + Arrays.toString(labels) + Arrays.toString(inputs);

		Graphics2D g = inputPanel.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, (int) (Offspring.inputOffset * Plot.OFFSET) - (Plot.OFFSET / 2), inputPanel.getFrame().getWidth(), ((inputs.length + outputs.length + preferences.length + 2 + 3) / 2) * Plot.OFFSET);
		g.setFont(g.getFont().deriveFont(1));
		int i;
		for (i = 0; i < inputs.length; i++) {
			int y = (int) ((Offspring.inputOffset + (i * 0.5)) * Plot.OFFSET);
			int x = inputPanel.getFrame().getWidth() / 2;
			int w = (int) ((x - Plot.OFFSET) * Math.abs(inputs[i]));
			if (inputs[i] < 0) {
				x -= w;
				g.setColor(Color.blue);
			} else {
				g.setColor(Color.red);
			}
			g.fillRect(x, y - (Plot.OFFSET / 2), w, Plot.OFFSET / 2);
			g.setColor(Color.black);
			g.drawString(i + "", 1, y);
			g.drawString(labels[i], Plot.OFFSET, y);
		}

		g.setColor(Color.ORANGE);
		g.drawString("OUTPUTS", Plot.OFFSET, (int) ((Offspring.inputOffset + (i * 0.5)) * Plot.OFFSET));
		i++;

		labels = task.outputLabels();
		for (int j = 0; j < outputs.length; j++, i++) {
			int y = (int) ((Offspring.inputOffset + (i * 0.5)) * Plot.OFFSET);
			int x = inputPanel.getFrame().getWidth() / 2;
			int w = (int) ((x - Plot.OFFSET) * Math.abs(outputs[j]));
			if (outputs[j] < 0) {
				x -= w;
				g.setColor(Color.blue);
			} else {
				g.setColor(Color.red);
			}
			g.fillRect(x, y - (Plot.OFFSET / 2), w, Plot.OFFSET / 2);
			g.setColor(Color.black);
			g.drawString(labels[j], Plot.OFFSET, y);
		}

		g.setColor(Color.ORANGE);
		g.drawString("MODE PREFERENCES", Plot.OFFSET, (int) ((Offspring.inputOffset + (i * 0.5)) * Plot.OFFSET));
		i++;

		for (int j = 0; j < preferences.length; j++, i++) {
			int y = (int) ((Offspring.inputOffset + (i * 0.5)) * Plot.OFFSET);
			int x = inputPanel.getFrame().getWidth() / 2;
			int w = (int) ((x - Plot.OFFSET) * Math.abs(preferences[j]));
			if (preferences[j] < 0) {
				x -= w;
			}
			g.setColor(CombinatoricUtilities.colorFromInt(j + 1));
			g.fillRect(x, y - (Plot.OFFSET / 2), w, Plot.OFFSET / 2);
			// show fatigue
			g.setColor(Color.RED);
			int remove = (int) ((x - Plot.OFFSET) * preferenceFatigue[j]);
			if (preferences[j] > 0) {
				x = (x + w) - remove;
			}
			g.fillRect(x, y - (Plot.OFFSET / 2), remove, Plot.OFFSET / 2);
			// show text
			g.setColor(Color.black);
			g.drawString("Mode " + j, Plot.OFFSET, y);
		}

		i++;
		int y = (int) ((Offspring.inputOffset + (i * 0.5)) * Plot.OFFSET);
		g.drawString("Time Stamp: " + MMNEAT.task.getTimeStamp(), Plot.OFFSET, y);
	}

	//used in drawing class. Transient to avoid unnecessary saving to xml files
	transient private ArrayList<ArrayList<Node>> layers = null;


	/**
	 * Draws the network as a drawing panel to screen. Useful for troubleshooting 
	 * networks
	 * 
	 * @param panel drawingPanel to be used to draw network
	 * @param showInnovationNumbers shows innovation #s of node
	 * @param showWeights shows weights of links 
	 */
	public void draw(DrawingPanel panel, boolean showInnovationNumbers, boolean showWeights) {
		TWEANN.panel = panel;//instantiates private instance of panel inside of TWEANN object
		if (layers == null) createLayers();
		Graphics2D g = prepPanel(panel, Color.BLACK);//this part actually draws network
		drawNetwork(g, panel.getFrame().getHeight(), panel.getFrame().getWidth(), showInnovationNumbers, showWeights);
	}
	
	/**
	 * Draw network on the given Graphics instance
	 * @param g
	 * @param showInnovationNumbers
	 * @param showWeights
	 */
	public void drawNetwork(Graphics2D g, int frameHeight, int frameWidth, boolean showInnovationNumbers, boolean showWeights) {
		drawAllNodes(g, frameHeight, frameWidth, showInnovationNumbers);//puts nodes onto drawingPanel
		drawAllLinks(g, showInnovationNumbers, showWeights);
		// Only draw module assications if there are multiple modules
		if(moduleAssociations.length > 1) {
			addModuleAssociations(g, moduleAssociations);
		}
	}

	/**
	 * Get image of network without drawing anything to a DrawingPanel
	 * @param width Width of image
	 * @param height Height of image
	 * @param showInnovationNumbers Whether to show innovation numbers
	 * @param showWeights Whether to show weight values
	 * @return The image
	 */
	public BufferedImage getNetworkImage(int width, int height, boolean showInnovationNumbers, boolean showWeights) {
		if (layers == null) createLayers();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Set a white background to draw on
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				// For some reason, setting every individual pixel seems necessary
				image.setRGB(i, j, Color.WHITE.getRGB());
			}
		}
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.BLACK); // For lines, etc
		// Use of +46 here is bad magic number that undoes another magic number associated with bottom border of DrawingPanel
		drawNetwork(g, height+46, width, showInnovationNumbers, showWeights);
		return image;
	}

	/**
	 * Creates layers of network in order of input, hidden, output
	 */
	private void createLayers() {
		layers = new ArrayList<ArrayList<Node>>();
		layers.add(getNodesToDraw(0, numIn, Node.NTYPE_INPUT));//manually loads nodes from TWEANN into layers
		ArrayList<Node> hidden = getNodesToDraw(numIn, nodes.size() - numOut, Node.NTYPE_HIDDEN);
		ArrayList<ArrayList<Node>> hiddenLayers = sortHiddenLayers(hidden);
		for (int i = 0; i < hiddenLayers.size(); i++) {
			layers.add(hiddenLayers.get(i));
		}
		layers.add(getNodesToDraw(nodes.size() - numOut, nodes.size(), Node.NTYPE_OUTPUT));
	}

	/**
	 * gets the graphics from the drawing panel 
	 * 
	 * @param panel drawingPanel
	 * @param c color of links
	 * @return graphics object from drawingPanel
	 */
	private Graphics2D prepPanel(DrawingPanel panel, Color c) {
		Graphics2D g = panel.getGraphics();
		g.setColor(c);
		g.drawString("Network ID: " + id, 5, 10);//network id. #s 5 & 10 set this in top left corner of panel
		return g;
	}

	/**
	 *sizes the module rectangle based upon its share of usage
	 *in the network 
	 * @param g graphics object
	 * @param moduleAssociations values of associations with module
	 */
	private void addModuleAssociations(Graphics2D g, int[] moduleAssociations){
		for (int i = 0; i < moduleAssociations.length; i++) {
			g.setColor(CombinatoricUtilities.colorFromInt(i + 1));
			g.fillRect(100 + (i * 2 * NODE_DIM), 2, 2 * NODE_DIM, 2 * NODE_DIM);
			g.setColor(CombinatoricUtilities.colorFromInt(moduleAssociations[i] + 1));
			g.fillRect(100 + (i * 2 * NODE_DIM), 2 + 2 * NODE_DIM, 2 * NODE_DIM, 2 * NODE_DIM);
		}
	}

	/**
	 * Sets link color depending on nature of neuron
	 * @param g graphics object
	 * @param frozen if link is frozen
	 * @param recurrent if link is recurrent
	 */
	private void setLinkColor(Graphics2D g, boolean recurrent, boolean frozen) {
		if (frozen) {
			g.setColor(Color.CYAN);
		} else if (!recurrent) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(Color.GREEN);
		}
	}
	
	/**
	 * Draw the network links on the graphics instance
	 * @param g Graphics
	 * @param showInnovationNumbers
	 * @param showWeights
	 */
	public void drawAllLinks(Graphics2D g, boolean showInnovationNumbers, boolean showWeights) {
		for(ArrayList<Node> layer : layers) {
			for(Node display : layer) {
				for (Link disLink : display.outputs) {
					Node target = disLink.target;
					checkLinkTarget(target);
					if (showInnovationNumbers)drawLinkInnovationNumbersAndWeights(g, display, target, disLink, showWeights);
					setLinkColor(g, 
							disLink.recurrent || // Explicitly labeling a link as recurrent is possible, but isn't really done
							(archetypeIndex != -1 && // Substrate networks do not have archetypes so order cannot be checked
							// But we can check if the target node precedes the source in the archetype
							EvolutionaryHistory.indexOfArchetypeInnovation(archetypeIndex, disLink.target.innovation) < EvolutionaryHistory.indexOfArchetypeInnovation(archetypeIndex, display.innovation)), 
							disLink.frozen);
					drawLink(g, display, target, disLink, disLink.recurrent ? -1 : 1);
				}
			}
		}
	}
	/**
	 * draws link between two nodes
	 * 
	 * @param g graphics object
	 * @param display source node of link
	 * @param target target node of link
	 * @param disLink the link
	 * @param mult whether or not link is recurrent
	 */
	private void drawLink(Graphics2D g, Node display, Node target, Link disLink, int mult) {
		if (display.displayY == target.displayY) {
			drawLink(g, disLink.weight, display.displayX + (NODE_DIM / 2),
					display.displayY + (NODE_DIM / 2),
					display.displayX + (NODE_DIM / 2) + (mult * LINK_CURVE_OFFSET),
					display.displayY + (NODE_DIM / 2) - (mult * LINK_CURVE_OFFSET));
			drawLink(g, disLink.weight, display.displayX + (NODE_DIM / 2) + (mult * LINK_CURVE_OFFSET),
					display.displayY + (NODE_DIM / 2) - (mult * LINK_CURVE_OFFSET),
					target.displayX + (NODE_DIM / 2), target.displayY + (NODE_DIM / 2));
		} else {
			drawLink(g, disLink.weight, display.displayX + (NODE_DIM / 2),
					display.displayY + (NODE_DIM / 2), target.displayX + (NODE_DIM / 2),
					target.displayY + (NODE_DIM / 2));
		}
	}

	/**
	 * draws link between two nodes
	 * @param g graphics object
	 * @param weight weight of link
	 * @param x1 x-coord of source node
	 * @param y1 y-coord of source node
	 * @param x2 x-coord of target node
	 * @param y2 y-coord of target node
	 */
	private void drawLink(Graphics2D g, double weight, int x1, int y1, int x2, int y2) {
		final int MAX_LINES = NODE_DIM;
		int lines = Math.max(1, (int) (Math.abs(Math.tanh(weight)) * MAX_LINES));
		int xOffset = lines / 2;
		if (weight > 0) {
			for (int i = 0; i < lines; i++) {
				g.drawLine(x1 - xOffset + i, y1, x2 - xOffset + i, y2);
			}
		} else {
			double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
			final int SEGMENT_LEN = 10;
			double xSegmentLength = (Math.abs(x2 - x1) * SEGMENT_LEN) / length;
			double ySegmentLength = (Math.abs(y2 - y1) * SEGMENT_LEN) / length;
			int segments = (int) Math.floor(length / SEGMENT_LEN);
			double xSign = Math.signum(x2 - x1);
			double ySign = Math.signum(y2 - y1);
			for (int i = 0; i < segments; i++) {
				for (int j = 0; j < lines; j++) {
					g.drawLine(x1 - xOffset + j + (int) (xSign * i * xSegmentLength),
							y1 + (int) (ySign * i * ySegmentLength),
							x1 - xOffset + j + (int) (xSign * (i + 0.5) * xSegmentLength),
							y1 + (int) (ySign * (i + 0.5) * ySegmentLength));
				}
			}
		}
	}

	/**
	 * checks to make sure link target node exists
	 * @param target target node of link
	 */
	private void checkLinkTarget(Node target) { 
		if (target == null) {
			System.out.println("Null link target?");
			System.out.println("id:" + this.id);
			System.out.println("numIn:" + this.numIn);
			System.out.println("numOut:" + this.numOut);
			System.out.println("neuronsPerMode:" + this.neuronsPerModule);
			System.out.println("numModes:" + this.numModes);
			System.out.println("multitask:" + this.standardMultitask);
			System.out.println("nodes.size():" + nodes.size());
			for (Node node : nodes) {
				System.out.println(node == null ? "null" : node.innovation + ":" + node.ntype);
			}
			System.out.println("Done");
		}
	}

	/**
	 * hard copies nodes of TWEANN to draw network
	 * @param start starting index of node array
	 * @param end ending index of node array
	 * @param ntype node type
	 * @return array list of ntype of node
	 */
	protected ArrayList<Node> getNodesToDraw(int start, int end, int ntype) {
		ArrayList<Node> result = new ArrayList<Node>(end-start);
		for (int i = start; i < end; i++) {
			Node inNode = nodes.get(i);
			if (inNode.ntype == ntype) {
				result.add(inNode);
			} else {
				throw new IllegalArgumentException("node array is out of order!");
			}
		}
		return result;
	}

	/**
	 * sorts hidden layers of nodes
	 * 
	 * @param hidden array list of all hidden nodes
	 * @return array list containing each separate layer
	 */
	protected ArrayList<ArrayList<Node>> sortHiddenLayers(ArrayList<Node> hidden) {
		ArrayList<ArrayList<Node>> hiddenLayers = new ArrayList<ArrayList<Node>>();
		hiddenLayers.add(hidden);
		int hiddenLayer = 0;
		int loop = 0;
		while (true) {//true b/c check of nodes performed above
			loop++;
			ArrayList<Node> currentHiddenLayer = hiddenLayers.get(hiddenLayer);
			ArrayList<Node> nextHiddenLayer = new ArrayList<Node>();
			for (int c = 0; c < currentHiddenLayer.size(); c++) {
				int transitionIndex = currentHiddenLayer.size();
				for (int n = c + 1; n < currentHiddenLayer.size(); n++) {
					if (currentHiddenLayer.get(c).isConnectedTo(currentHiddenLayer.get(n).innovation)) {
						transitionIndex = n;
						break;
					}
				}
				int moving = currentHiddenLayer.size() - transitionIndex;
				for (int t = 0; t < moving; t++) {
					nextHiddenLayer.add(currentHiddenLayer.remove(transitionIndex));
				}
			}
			if (nextHiddenLayer.isEmpty()) {
				break;
			} else {
				hiddenLayers.add(nextHiddenLayer);
				hiddenLayer++;
			}
			// It is assumed that no network should be this big,
			// but this might not be true if HyperNEAT networks are drawn.
			if (loop > 10000) {
				System.out.println("Can't escape loop in network draw!");
				throw new IllegalArgumentException("HyperNEAT created a network with too many connections!");
			}
		}

		return hiddenLayers;
	}

	/**
	 * draws a border around the given node corresponding to activation #
	 * @param g graphics object
	 * @param c color of border
	 * @param x x-coord of node 
	 * @param y y-coord of node
	 * @param activation activation # of function used
	 * @param thickness thickness of border
	 */
	private void drawBorder(Graphics2D g, Color c, int x, int y, double activation, int thickness) { 
		Color component = g.getColor();
		g.setColor(c);
		g.fillRect(x - thickness, y - thickness, (int) ((1 + activation) * NODE_DIM) + thickness*2, (int) ((1 + activation) * NODE_DIM) + thickness*2);
		g.setColor(component);
	}

	/**
	 * Draws innovation numbers and weights
	 * @param g graphics object
	 * @param display source node
	 * @param target target node
	 * @param disLink link in question
	 * @param showWeights whether or not to show weights
	 */
	private void drawLinkInnovationNumbersAndWeights(Graphics2D g, Node display, Node target, Link disLink, boolean showWeights) {
		int x = (display.displayX + target.displayX) / 2;
		int y = (display.displayY + target.displayY) / 2;
		g.setColor(Color.MAGENTA);
		g.drawString("" + disLink.innovation, x, y);
		if (showWeights) {
			g.setColor(Color.DARK_GRAY);
			double weight = Math.ceil(disLink.weight * 100) / 100;
			g.drawString("" + weight, x, y + 10);
		}
	}

	/**
	 * draws node innovation numbers
	 * @param g graphics object
	 * @param display node
	 * @param x x-coord of node
	 * @param y y-coord of node
	 */
	private void drawNodeInnovationNumbers(Graphics2D g, Node display, int x, int y) {
		AffineTransform t = g.getTransform();
		g.setColor(Color.BLACK);
		int sign = display.ntype == Node.NTYPE_INPUT ? -1 : 1;
		g.rotate(sign * Math.PI / 4.0, x, y);
		g.drawString("" + display.innovation, x + NODE_DIM + (NODE_DIM / 2),
				y + (NODE_DIM / 2) + (sign * NODE_DIM));
		g.setTransform(t);
	}

	/**
	 * places all nodes on graphics
	 * @param g graphics object
	 * @param showInnovationNumbers whether or not to show innovation numbers
	 */
	protected void drawAllNodes(Graphics2D g, int frameHeight, int frameWidth, boolean showInnovationNumbers) {
		int height = frameHeight - 46;//46 is padding for panel
		int width = frameWidth - 6;//6 is padding for panel
		double verticalSpacing = ((height - (2.0 * DISPLAY_BORDER)) / (layers.size() - 1.0));
		for (int l = 0; l < layers.size(); l++) {
			ArrayList<Node> layer = layers.get(l);
			for (int n = 0; n < layer.size(); n++) {
				drawNode(g, verticalSpacing, width, height, layer, n, l, showInnovationNumbers);
			}
		}
	}

	/**
	 * Draws node
	 * @param g graphics object
	 * @param verticalSpacing spacing of node
	 * @param width width of node
	 * @param height height of node
	 * @param layer layer of nodes to write from 
	 * @param n number of node
	 * @param l  number of node layer
	 * @param showInnovationNumbers whether to show innovation numbers
	 */
	private void drawNode(Graphics2D g, double verticalSpacing, int width, int height, ArrayList<Node> layer, int n, int l, boolean showInnovationNumbers) {
		double horizontalSpacing = ((width - (2.0 * DISPLAY_BORDER)) / layer.size());
		int x = (int) (DISPLAY_BORDER + (n * horizontalSpacing) + (horizontalSpacing / 2.0));
		int y = (int) ((height - DISPLAY_BORDER) - (l * verticalSpacing));
		Node display = layer.get(n);
		display.displayX = x;
		display.displayY = y;
		g.setColor(Color.white);
		g.fillRect(x, y, 2 * NODE_DIM, 2 * NODE_DIM); // erase previous activation
		double activation = display.activation;
		eraseModeIndicator(g, x, y, Color.white);
		if (display.ntype == Node.NTYPE_OUTPUT) {
			drawOutputNode(g, n, x, y, activation);
		} else if (display.ntype == Node.NTYPE_HIDDEN) {
			drawHiddenNode(g, x, y, activation);
		} else {
			drawInputNode(g, n, x, y, activation);
		}
		checkNode(g, display);
		if (showInnovationNumbers) drawNodeInnovationNumbers(g, display, x, y);
	}

	/**
	 * draws output nodes
	 * @param g graphics object
	 * @param n number of node
	 * @param x x-coord of node
	 * @param y y-coord of node
	 * @param activation activation number of node
	 */
	private void drawOutputNode(Graphics2D g, int n, int x, int y, double activation) {
		if (n / (neuronsPerModule + (standardMultitask ? 0.0 : 1.0)) == chosenModule) {
			// The white mode indicator is invisible, and erases the current indicator.
			// In other words, don't actually show a mode indicator if there is only one mode.
			eraseModeIndicator(g, x, 0, numModes == 1 ? Color.white : CombinatoricUtilities.colorFromInt(chosenModule));
		}
		if (standardMultitask || n % (neuronsPerModule + 1) == neuronsPerModule) {
			g.setColor(Color.GRAY);//gray means multitask and multimodule is on
		} else {
			g.setColor(Color.ORANGE);//orange means standard single module and task
		}
		g.fillRect(x, y, (int) ((1 + activation) * NODE_DIM), (int) ((1 + activation) * NODE_DIM));
	}

	/**
	 * draws hidden nodes
	 * @param g graphics object
	 * @param x x-coord of node
	 * @param y y-coord of node
	 * @param activation activation of node
	 */
	private void drawHiddenNode(Graphics2D g, int x, int y, double activation){
		g.setColor(Color.RED);
		g.fillRect(x, y, (int) ((1 + activation) * NODE_DIM), (int) ((1 + activation) * NODE_DIM));
	}
	
	/**
	 * draws input nodes
	 * @param g graphics object
	 * @param n number of node
	 * @param x x-coord of node
	 * @param y y-coord of node
	 * @param activation activation of node
	 */
	private void drawInputNode(Graphics2D g, int n, int x, int y, double activation) {
		g.setColor(Color.BLACK);
		g.drawString(n + "", x, y + 15);
		g.setColor(Color.BLUE);
		g.fillRect(x, y, (int) ((1 + activation) * NODE_DIM), (int) ((1 + activation) * NODE_DIM));
	}
	
	/**
	 * adds border to node if frozen and corresponding to activation function
	 * @param g graphic object
	 * @param display node in question
	 */
	private void checkNode(Graphics2D g, Node display)	 {
		double activation = display.activation;
		if(Parameters.parameters.booleanParameter("allowMultipleFunctions") && display.frozen) { // TODO: Is this case even reachable?
			drawBorder(g, Color.CYAN, display.displayX, display.displayY, activation, 4);
			drawBorder(g, CombinatoricUtilities.colorFromInt(display.ftype), display.displayX, display.displayY, activation, 4);
		} else if (display.frozen) {
			drawBorder(g, Color.CYAN, display.displayX, display.displayY, activation, 2);
		} else if (Parameters.parameters.booleanParameter("allowMultipleFunctions")) { // TODO: Just move this to where the node is drawn in the first place?
			drawBorder(g, CombinatoricUtilities.colorFromInt(display.ftype), display.displayX, display.displayY, activation, 2);
		}
	}

	/**
	 * erases mode indicator
	 * @param g graphic object
	 * @param x x-coord of mode
	 * @param y y-coord of mode
	 * @param c color to erase mode with
	 */
	private void eraseModeIndicator(Graphics2D g, int x, int y, Color c) {
		g.setColor(c);
		g.fillRect(x, y, 2 * NODE_DIM, 2 * NODE_DIM);
	}
}