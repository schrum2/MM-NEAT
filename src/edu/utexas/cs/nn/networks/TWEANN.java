package edu.utexas.cs.nn.networks;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.graphics.Plot;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.CombinatoricUtilities;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * My version of a Topology and Weight Evolving Neural Network.
 * Nodes are stored in a linear order that must obey the following constraints:
 * 1) All input nodes are at the far left
 * 2) All output nodes are at the far right
 * 3) Feedforward links only go from left to right
 * 4) Links from right to left are always recurrent
 * 
 * This is a bit more restrictive than the standard NEAT networks.
 * The benefit of it is that signals can propagate from input to output
 * in a single activation, unlike standard NEAT networks that only have
 * activations travel one link at a time.
 *
 * @author Jacob Schrum
 */
public class TWEANN implements Network {

	// Variables used for watching the behavior of an active network in a graphical display
    public static int NETWORK_VIEW_DIM = 500;
    public static final int NODE_DIM = 6;
    public static final int DISPLAY_BORDER = 25;
    public static final int LINK_CURVE_OFFSET = 7;
    public static DrawingPanel panel = null;
    public static DrawingPanel inputPanel = null;
    public static DrawingPanel preferenceNeuronPanel = null;
    public static ArrayList<Double>[] preferenceActivationHistory = null;

    // subclass for a synaptic link between nodes
    public class Link {

        public final Node target;
        public final double weight;
        public final long innovation;
        public final boolean recurrent;
        public final boolean frozen;

        @Override
        public String toString() {
            String result = "";
            result += "(" + innovation + ":" + weight + ":" + target.innovation + ":" + (recurrent ? "recurrent" : "forward") + ")";
            return result;
        }

        /**
         * Make new link
         *
         * @param target Node that link leads to
         * @param weight synaptic weight
         * @param innovation innovation number of link
         * @param recurrent whether link is recurrent
         * @param frozen whether link can be changed by mutation
         */
        public Link(Node target, double weight, long innovation, boolean recurrent, boolean frozen) {
            this.target = target;
            this.weight = weight;
            this.innovation = innovation;
            this.recurrent = recurrent;
            this.frozen = frozen;
        }

        protected void transmit(double signal) {
            target.sum += (signal * weight);
        }
    }

    // subclass for a single neuron
    public class Node {

    	// TODO: Generalize handling of activation functions to make CPPNs possible 
        
        // Three types of neurons
        public static final int NTYPE_INPUT = 0;
        public static final int NTYPE_HIDDEN = 1;
        public static final int NTYPE_OUTPUT = 2;
        // Networks never change after being created (only the genotypes evolve)
        public final int ntype;
        public final int ftype;
        public final long innovation;
        public final boolean frozen;
        // Outgoing links
        public List<Link> outputs;
        protected double sum;
        protected double activation;
        // Used when displaying the network graphically
        public int displayX = 0;
        public int displayY = 0;

        public boolean isLinkRecurrnt(long targetInnovation) {
            for (Link l : outputs) {
                if (l.target.innovation == targetInnovation) {
                    return l.recurrent;
                }
            }
            // Should never reach
            System.out.println("The targetInnovation (" + targetInnovation + ") was not found in " + outputs);
            System.exit(1);
            return false;
        }

        @Override
        public String toString() {
            String result = "";
            result += innovation + ":";
            result += ntypeName(ntype) + ":";
            result += ftypeName(ftype) + ":";
            result += "Sum = " + sum + ":";
            result += outputs;
            return result;
        }

        /**
         * For textual display of network
         * @param ntype One of the specified neuron types
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

        private String ftypeName(int ftype) {
            switch (ftype) {
            case ActivationFunctions.FTYPE_HLPIECEWISE:
                return "halfLinear";
                case ActivationFunctions.FTYPE_SIGMOID:
                    return "Sigmoid";
                case ActivationFunctions.FTYPE_TANH:
                    return "TanH";
                case ActivationFunctions.FTYPE_ID:
                    return "ID";
                case ActivationFunctions.FTYPE_FULLAPPROX:
                    return "FullApprox";
                case ActivationFunctions.FTYPE_APPROX:
                    return "SigmoidApprox";
                case ActivationFunctions.FTYPE_GAUSS:
                	return "Gaussian";
                case ActivationFunctions.FTYPE_SINE:
                	return "Sine";
                case ActivationFunctions.FTYPE_ABSVAL:
                	return "AbsoluteValue";
                				
            }
            // Should never reach
            System.out.println(ftype + " is not a valid type of activation function");
            System.exit(1);
            return "ERROR";
        }

        /**
         * New node with no targets, not frozen by default
         *
         * @param ftype = type of activation function
         * @param ntype = type of node: input, hidden, output
         * @param innovation = unique innovation number for node
         */
        public Node(int ftype, int ntype, long innovation) {
            this(ftype, ntype, innovation, false);
        }

        /**
         * New node with no targets
         *
         * @param ftype = type of activation function
         * @param ntype = type of node: input, hidden, output
         * @param innovation = unique innovation number for node
         * @param frozen = true if new link mutations cannot target this node
         */
        public Node(int ftype, int ntype, long innovation, boolean frozen) {
            this.innovation = innovation;
            this.ftype = ftype;
            this.ntype = ntype;
            this.frozen = frozen;
            outputs = new LinkedList<Link>();
            flush();
        }

        /**
         * An input is added to the sum in case it holds recurrent activation
         *
         * @param input = sensor input
         */
        protected void load(double input) {
            sum += input;
        }

        public double output() {
            return activation;
        }

        /**
         * Used when network enters new environment and should no longer
         * remember anything.
         */
        protected final void flush() {
            sum = 0.0;
            activation = 0.0;
        }

        private void activate() {
            switch (ftype) {
            case ActivationFunctions.FTYPE_HLPIECEWISE:
                activation = ActivationFunctions.halfLinear(sum);
                break;
                case ActivationFunctions.FTYPE_SIGMOID:
                    activation = ActivationFunctions.sigmoid(sum);
                    break;
                case ActivationFunctions.FTYPE_TANH:
                    activation = ActivationFunctions.tanh(sum);
                    break;
                case ActivationFunctions.FTYPE_ID:
                    activation = sum;
                    break;
                case ActivationFunctions.FTYPE_APPROX:
                    activation = ActivationFunctions.quickSigmoid(sum);
                    break;
                case ActivationFunctions.FTYPE_FULLAPPROX:
                    activation = ActivationFunctions.fullQuickSigmoid(sum);
                    break;
                case ActivationFunctions.FTYPE_GAUSS:
                	activation = ActivationFunctions.gaussian(sum);
                	break;
                case ActivationFunctions.FTYPE_SINE:
                	activation = ActivationFunctions.sine(sum);
                	break;
                case ActivationFunctions.FTYPE_ABSVAL:
                	activation = ActivationFunctions.absVal(sum);
                	break;
            }
        }

        protected void activateAndTransmit() {
            activate();
            // Clear the sum after activation
            sum = 0.0;

            for (Link l : outputs) {
                l.transmit(activation);
            }
        }

        /**
         * Creates connection from this Node to target Node via a new Link.
         * 
         * @param target Node to link to 
         * @param weight synaptic weight of link between nodes 
         * @param innovation Innovation number of new Link 
         * @param recurrent whether or not link is recurrent 
         * @param frozen whether or not link can be changed
         */
        protected void connect(Node target, double weight, long innovation, boolean recurrent, boolean frozen) {
            Link l = new Link(target, weight, innovation, recurrent, frozen);
            outputs.add(l);
        }

        /**
         * Returns true if the node is connected to another node with a given
         * innovation number.
         *
         * @param innovation innovation number of target to search for
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
    
    private long id = -1;
    private int numIn;
    private int numOut;
    private int numModes;
    private int neuronsPerMode;
    private boolean standardMultitask;
    private boolean hierarchicalMultitask;
    private int presetMode;
    // Only used with Hierarchical Multitask Networks
    private boolean[] viableModes;
    // HMT associates each module with a multitask mode
    public int[] modeAssociations;
    public ArrayList<Node> nodes;
    public int[] modeUsage;
    private double[] preferenceFatigue;
    public int chosenMode = 0;
    public boolean canDraw = true;
    public final int archetypeIndex;
    public final int outputStart;

    /**
     * Whether or not networks being used can/do have preference neurons
     * @return true if preference neurons should be included
     */
    public static boolean preferenceNeuron() {
        if (CommonConstants.ensembleModeMutation) {
            return false;
        }
        double mmpRate = Parameters.parameters.doubleParameter("mmpRate");
        double mmrRate = Parameters.parameters.doubleParameter("mmrRate");
        double mmdRate = Parameters.parameters.doubleParameter("mmdRate");
        double fullMMRate = Parameters.parameters.doubleParameter("fullMMRate");
        int startingModes = Parameters.parameters.integerParameter("startingModes");
        boolean multitaskCombiningCrossover = Parameters.parameters.booleanParameter("multitaskCombiningCrossover");
        return (!multitaskCombiningCrossover || startingModes > 1 || mmpRate > 0 || mmrRate > 0 || mmdRate > 0 || fullMMRate > 0 || CommonConstants.hierarchicalMultitask);
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
     * @param numIn Number of input sensors
     * @param numOut For Multitask, the actual number of outputs. Otherwise the
     * number of policy neurons per mode.
     * @param featureSelective Outputs are only sparsely connected to start if
     * this is true, fully connected otherwise
     * @param ftype Type of activation function on neurons
     * @param numModes The number of modes IF the network is multitask.
     * Otherwise this should be 1, even if starting with multiple modes with
     * preference neurons
     * @param archetypeIndex = archetype to align with for crossover
     */
    public TWEANN(int numIn, int numOut, boolean featureSelective, int ftype, int numModes, int archetypeIndex) {
        this.archetypeIndex = archetypeIndex;
        this.numIn = numIn;
        this.modeUsage = new int[numModes];
        this.preferenceFatigue = new double[numModes];
        this.hierarchicalMultitask = CommonConstants.hierarchicalMultitask;

        int startingPrefModes = Parameters.parameters.integerParameter("startingModes");
        // numModes only applies for Multitask
        if (numModes == 1 || hierarchicalMultitask) {
            int startingModes = startingPrefModes;
            if (hierarchicalMultitask) {
                // Can start with multiple preference neuron modules per multitask mode
                startingModes *= numModes;
            }
            // Either a new mode-mutation network or a unimodal network
            this.neuronsPerMode = numOut;
            this.standardMultitask = false;
            // Put preference neuron in place now if it will be needed later
            if (preferenceNeuron()) {
                numOut++;
            }
            this.numModes = startingModes;
            numOut *= startingModes;
        } else {
            // A Multitask network
            this.neuronsPerMode = numOut / numModes;
            this.numModes = numModes;
            this.standardMultitask = true;
            if (numOut % numModes != 0 || numOut % neuronsPerMode != 0) {
                System.out.println("Multitask network initialized wrong: ");
                System.out.println("numOut = " + numOut);
                System.out.println("numModes = " + numModes);
                System.out.println("neuronsPerMode = " + neuronsPerMode);
                throw new IllegalArgumentException("Multitask network initialized wrong: numOut = " + numOut + ", numModes = " + numModes + ",neuronsPerMode = " + neuronsPerMode);
            }
        }
        this.numOut = numOut;

        nodes = new ArrayList<Node>(numIn + numOut);

        long innovation = -1;
        for (int i = 0; i < numIn; i++) {
            Node n = new Node(ftype, Node.NTYPE_INPUT, innovation--);
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
            if (preferenceNeuron() && j % (neuronsPerMode + 1) == neuronsPerMode) {
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
                // Innovation choice assures that similar links match up, even with FS on
                nodes.get(inputSources[i]).connect(out, RandomNumbers.fullSmallRand(), linkInnovationBound - (j * numIn) - inputSources[i], false, false);
            }
        }
        outputStart = nodes.size() - numOut;

        // In a new network, each Multitask mode has one network module.
        // This is really only needed if hierarchicalMultitask is true.
        modeAssociations = new int[this.numModes];
        for (int i = 0; i < this.numModes; i++) {
            // Modulus splits modules up evenly among modes
            modeAssociations[i] = i % startingPrefModes;
            //System.out.println(Arrays.toString(modeAssociations));
        }
        allViable();
    }

    /**
     * Create TWEANN based on TWEANNGenotype, which can encode an arbitrary
     * network.
     * 
     * @param g The genotype
     */
    public TWEANN(TWEANNGenotype g) {
        this.archetypeIndex = g.archetypeIndex;
        this.id = g.getId();
        this.nodes = new ArrayList<Node>(g.nodes.size());

        int countIn = 0;
        int countOut = 0;

        int section = Node.NTYPE_INPUT;
        for (int i = 0; i < g.nodes.size(); i++) {
            TWEANNGenotype.NodeGene ng = g.nodes.get(i);
            Node n = new Node(ng.ftype, ng.ntype, ng.innovation, ng.frozen);
            switch (ng.ntype) {
                case Node.NTYPE_INPUT:
                    assert (section == Node.NTYPE_INPUT) : "Genome encoded false network: inputs: \n" + g;
                    countIn++;
                    break;
                case Node.NTYPE_HIDDEN:
                    if (section != Node.NTYPE_HIDDEN) {
                        assert (section == Node.NTYPE_INPUT) : "Genome encoded false network: hidden\n" + g;
                        section = Node.NTYPE_HIDDEN;
                    }
                    break;
                case Node.NTYPE_OUTPUT:
                    if (section != Node.NTYPE_OUTPUT) {
                        assert (section == Node.NTYPE_HIDDEN || section == Node.NTYPE_INPUT) : "Genome encoded false network: output\n" + g;
                        section = Node.NTYPE_OUTPUT;
                    }
                    countOut++;
                    break;
            }
            nodes.add(n);
        }

        this.numIn = countIn;
        this.numOut = countOut;
        this.numModes = g.numModes;
        this.neuronsPerMode = g.neuronsPerMode;
        this.standardMultitask = g.standardMultitask;
        this.hierarchicalMultitask = g.hierarchicalMultitask;
        if(g.modeAssociations != null) { // This is a backwards compatibility issue:
            // This array was added for the Hierarchical Multitask networks
            this.modeAssociations = Arrays.copyOf(g.modeAssociations, numModes);
        } else { // In older networks, simply associate each module with its own mode
            modeAssociations = new int[this.numModes];
            for (int i = 0; i < this.numModes; i++) {
                modeAssociations[i] = i;
            }
        }
        // Is true if net has one mode
        assert (numModes != 1 || numOut <= neuronsPerMode + 1) :
                "Too many outputs for one mode" + "\n"
                + "g.getId():" + g.getId() + "\n"
                + "g.archetypeIndex:" + g.archetypeIndex + "\n"
                + "g.numIn:" + g.numIn + "\n"
                + "g.numOut:" + g.numOut + "\n"
                + "g.nodes.size():" + g.nodes.size() + "\n"
                + "EvolutionaryHistory.archetypeOut:" + Arrays.toString(EvolutionaryHistory.archetypeOut);
        // Is true if net has more than one mode
        assert (numModes == 1 || numOut == (neuronsPerMode + (standardMultitask || CommonConstants.ensembleModeMutation ? 0 : 1)) * numModes) :
                "multitask:" + standardMultitask + "\n"
                + "Wrong number of outputs (" + numOut + ") for the number of modes (" + numModes + ")";

        this.modeUsage = new int[numModes];
        this.preferenceFatigue = new double[numModes];

        for (LinkGene lg : g.links) {
            if (lg.active) {
//                try {
                Node source = getNode(lg.sourceInnovation);
                Node target = getNode(lg.targetInnovation);
                assert (target != null) : "No target: " + lg + "\nNet:" + g.getId();
                source.connect(target, lg.weight, lg.innovation, lg.recurrent, lg.frozen);
//                } catch (NullPointerException np) {
//                    System.out.println("Null pointer when decoding links");
//                    System.out.println(g);
//                    System.exit(1);
//                }
            }
        }
        outputStart = nodes.size() - numOut;
        allViable();
    }

    // Getters
    
    public int numInputs() {
        return numIn;
    }

    public int numOutputs() {
        return numOut;
    }

    public int effectiveNumOutputs() {
        return this.neuronsPerMode;
    }

    public int numModes() {
        return numModes;
    }

    public int neuronsPerMode() {
        return neuronsPerMode;
    }

    public boolean isMultitask() {
        return standardMultitask || hierarchicalMultitask;
    }

    public boolean isStandardMultitask() {
        return standardMultitask;
    }

    public boolean isHierarchicalMultitask() {
        return hierarchicalMultitask;
    }

    /**
     * Tell network to use the designated mode on the next process. With
     * hierarchical multitask networks, multiple modules can become viable.
     *
     * @param mode to use
     */
    public void chooseMode(int mode) {
        presetMode = mode;
        if (hierarchicalMultitask) {
            for (int i = 0; i < modeAssociations.length; i++) {
                // Set each mode as viable or not, if it is associated with the mode choice
                viableModes[i] = modeAssociations[i] == mode;
            }
        }
    }

    public int lastMode() {
        return chosenMode;
    }

    /**
     * Take array of inputs to network and process them to produce an output
     * vector.
     *
     * @param inputs sensor readings for the network
     * @return network output (single mode)
     */
    public double[] process(double[] inputs) {
        //System.out.println("Process " + id);

        assert (inputs.length == numIn) :
                "Input mismatch! numIn = " + numIn + "\n"
                + "inputs.length = " + inputs.length + "\n"
                + Arrays.toString(inputs);
        assert (numIn <= nodes.size()) :
                "Input mismatch! numIn = " + numIn + "\n"
                + "nodes.size() = " + nodes.size() + "\n"
                + nodes;

        // Load inputs
        for (int i = 0; i < numIn; i++) {
            nodes.get(i).load(inputs[i]);
        }

        // Activate nodes in forward order
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).activateAndTransmit();
        }

        // All outputs
//        for(int i = 0; i < numOut; i++){
//            System.out.println(nodes.get(outputStart + i).output() + ",");
//        }
        double[] preferences = new double[numModes];
        if (CommonConstants.ensembleModeMutation) {
            // Give all equal preference and then take average across modes
            Arrays.fill(preferences, 1.0);
        } else {
            if (numModes == 1) {
                preferences[0] = 1.0;
            } else if (standardMultitask) { // But NOT Hierarchical Multitask
                preferences[presetMode] = 1.0;
            } else {
                for (int m = 0; m < numModes; m++) {
                    Node out = nodes.get(outputStart + (m * (neuronsPerMode + 1)) + neuronsPerMode);
                    // Inviable modes have minimal preference
                    preferences[m] = viableModes[m] ? out.output() : -Double.MAX_VALUE;
                }
            }
            // subtract fatigue
            for (int i = 0; i < preferenceFatigue.length; i++) {
                preferences[i] -= preferenceFatigue[i];
            }

            if (CommonConstants.disabledMode >= 0) {
                preferences[CommonConstants.disabledMode] = -2; // Can never be selected
            }

            if (canDraw && preferenceNeuronPanel != null && numModes > 1 && !standardMultitask) {
                refreshPreferencePlot(preferenceNeuronPanel, preferences);
            }

            // determine winner
            chosenMode = CommonConstants.softmaxModeSelection ? StatisticsUtilities.softmax(preferences, CommonConstants.softmaxTemperature) : StatisticsUtilities.argmax(preferences);
            this.modeUsage[chosenMode]++;

            // add new fatigue
            preferenceFatigue[chosenMode] += CommonConstants.preferenceNeuronFatigueUnit;
            // decay fatigue
            for (int i = 0; i < preferenceFatigue.length; i++) {
                if (i != chosenMode) { // don't decay chosen mode
                    preferenceFatigue[i] *= CommonConstants.preferenceNeuronDecay;
                }
            }
        }

        double[] outputs = new double[neuronsPerMode];
        if (CommonConstants.ensembleModeMutation || CommonConstants.weightedAverageModeAggregation) {
            // Calculate weighted average across all modes
            for (int i = 0; i < outputs.length; i++) {
                for (int j = 0; j < numModes; j++) {
                    int modeStart = outputStart + (j * (neuronsPerMode + (CommonConstants.ensembleModeMutation ? 0 : 1)));
                    outputs[i] += preferences[j] * nodes.get(modeStart + i).output();
                }
                outputs[i] /= numModes;
            }
        } else {
            outputs = modeOutput(chosenMode);
        }

        //System.out.println("final outputs: " + Arrays.toString(outputs));
        if (canDraw) {
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
     * @param mode mode outputs to access
     * @return outputs of specific mode
     */
    public double[] modeOutput(int mode) {
        int selectedModeStart = outputStart + (mode * (neuronsPerMode + (standardMultitask ? 0 : 1)));
        double[] outputs = new double[neuronsPerMode];
        for (int i = 0; i < neuronsPerMode; i++) {
            Node out = nodes.get(selectedModeStart + i);
            outputs[i] = out.output();
        }
        return outputs;
    }

    @SuppressWarnings("unchecked")
	public void flush() {
        //System.out.println("Flush: " + id);
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
            refreshActivation(inputPanel, new double[numIn], new double[neuronsPerMode], new double[numModes], standardMultitask, new double[numModes]);
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
//        if(targetNode == null){
//            System.out.println("No node with innovation " + targetInnovation + " existed");
//            System.exit(1);
//        }
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
            Plot.linePlot(preferenceNeuronPanel, -1, 1, preferenceActivationHistory[i], CombinatoricUtilities.colorFromInt(i));
        }
    }

    private static void refreshActivation(DrawingPanel inputPanel, double[] inputs, double[] outputs, double[] preferences, boolean multitask, double[] preferenceFatigue) {
        NetworkTask task = (NetworkTask) MMNEAT.task; //ClassCreation.createObject("task");
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
        if (MMNEAT.sharedMultitaskNetwork != null && !multitask) {
            // This network determines preferences for the mode of a shared multitask network
            labels = new String[outputs.length];
            for (int j = 0; j < labels.length; j++) {
                labels[j] = "Pref Mode " + j;
            }
        }
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
    transient private ArrayList<ArrayList<Node>> layers = null;

    public void draw(DrawingPanel panel, boolean showInnovationNumbers, boolean showWeights) {
        TWEANN.panel = panel;
        if (layers == null) { // Only construct the layers once
            layers = new ArrayList<ArrayList<Node>>();

            ArrayList<Node> inputs = new ArrayList<Node>(numIn);
            for (int i = 0; i < numIn; i++) {
                Node inNode = nodes.get(i);
                if (inNode.ntype == Node.NTYPE_INPUT) {
                    inputs.add(inNode);
                } else {
                    System.out.println("Impossible network configuration: Wrong number of inputs");
                    System.out.println(this);
                    while (true);
                    //    System.exit(1);
                }
            }

            layers.add(inputs);

            ArrayList<Node> hidden = new ArrayList<Node>(nodes.size() - numIn - numOut);
            for (int h = numIn; h < (nodes.size() - numOut); h++) {
                Node hiddenNode = nodes.get(h);
                if (hiddenNode.ntype == Node.NTYPE_HIDDEN) {
                    hidden.add(hiddenNode);
                } else {
                    System.out.println("Impossible network configuration: Wrong number of hidden nodes");
                    System.out.println("h = " + h + "/" + nodes.size() + ", hiddenNode.ntype = " + hiddenNode.ntype);
                    System.out.println(this);
                    while (true);
                    //    System.exit(1);
                }
            }

            ArrayList<ArrayList<Node>> hiddenLayers = new ArrayList<ArrayList<Node>>();
            hiddenLayers.add(hidden);
            int hiddenLayer = 0;
            int loop = 0;
            while (true) {
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
                if (loop > 10000) {
                    System.out.println("Can't escape loop in network draw!");
                    break;
                }
            }

            for (int i = 0; i < hiddenLayers.size(); i++) {
                layers.add(hiddenLayers.get(i));
            }

            ArrayList<Node> outputs = new ArrayList<Node>(numOut);
            for (int o = (nodes.size() - numOut); o < nodes.size(); o++) {
                Node outNode = nodes.get(o);
                if (outNode.ntype == Node.NTYPE_OUTPUT) {
                    outputs.add(outNode);
                } else {
                    System.out.println("Impossible network configuration: Wrong number of outputs");
                    System.out.println(this);
                    while (true);
                    //    System.exit(1);
                }
            }

            layers.add(outputs);
        }

        Graphics2D g = panel.getGraphics();
        g.setColor(Color.BLACK);
        g.drawString("" + id, 5, 10);
        //g.setColor(Color.white);
        //g.fillRect(0, 0, panel.getFrame().getWidth(), panel.getFrame().getHeight());

        int height = panel.getFrame().getHeight() - 46;
        int width = panel.getFrame().getWidth() - 6;

        for (int l = 0; l < layers.size(); l++) {
            ArrayList<Node> layer = layers.get(l);
            double verticalSpacing = ((height - (2.0 * DISPLAY_BORDER)) / (layers.size() - 1.0));
            for (int n = 0; n < layer.size(); n++) {
                double horizontalSpacing = ((width - (2.0 * DISPLAY_BORDER)) / layer.size());
                int x = (int) (DISPLAY_BORDER + (n * horizontalSpacing) + (horizontalSpacing / 2.0));
                int y = (int) ((height - DISPLAY_BORDER) - (l * verticalSpacing));
                Node display = layer.get(n);
                display.displayX = x;
                display.displayY = y;
                g.setColor(Color.white);
                g.fillRect(x, y, 2 * NODE_DIM, 2 * NODE_DIM); // erase previous activation
                if (display.ntype == Node.NTYPE_OUTPUT) {
                    g.fillRect(x, 0, 2 * NODE_DIM, 2 * NODE_DIM); // erase mode indicator
                    if (n / (neuronsPerMode + (standardMultitask ? 0.0 : 1.0)) == chosenMode) {
                        g.setColor(CombinatoricUtilities.colorFromInt(chosenMode));
                        g.fillRect(x, 0, 2 * NODE_DIM, 2 * NODE_DIM); // erase mode indicator
                    }
                    if (standardMultitask || CommonConstants.ensembleModeMutation || n % (neuronsPerMode + 1) == neuronsPerMode) {
                        g.setColor(Color.GRAY);
                    } else {
                        g.setColor(Color.ORANGE);
                    }
                } else if (display.ntype == Node.NTYPE_HIDDEN) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLACK);
                    g.drawString(n + "", x, y + 15);
                    g.setColor(Color.BLUE);
                }
                double activation = display.activation;
                //g.drawString(""+activation, x, y);
                if (display.frozen) {
                    Color component = g.getColor();
                    g.setColor(Color.CYAN);
                    g.fillRect(x - 2, y - 2, (int) ((1 + activation) * NODE_DIM) + 4, (int) ((1 + activation) * NODE_DIM) + 4);
                    g.setColor(component);
                }
                g.fillRect(x, y, (int) ((1 + activation) * NODE_DIM), (int) ((1 + activation) * NODE_DIM));
                if (showInnovationNumbers) {
                    AffineTransform t = g.getTransform();
                    g.setColor(Color.BLACK);
                    int sign = display.ntype == Node.NTYPE_INPUT ? -1 : 1;
                    g.rotate(sign * Math.PI / 4.0, x, y);
                    g.drawString("" + display.innovation, x + NODE_DIM + (NODE_DIM / 2), y + (NODE_DIM / 2) + (sign * NODE_DIM));
                    g.setTransform(t);
                }
            }
        }

        for (int l = 0; l < layers.size(); l++) {
            ArrayList<Node> layer = layers.get(l);
            for (int n = 0; n < layer.size(); n++) {
                Node display = layer.get(n);
                for (Link disLink : display.outputs) {
                    Node target = disLink.target;
                    if (target == null) {
                        System.out.println("Null link target?");
                        System.out.println("id:" + this.id);
                        System.out.println("numIn:" + this.numIn);
                        System.out.println("numOut:" + this.numOut);
                        System.out.println("neuronsPerMode:" + this.neuronsPerMode);
                        System.out.println("numModes:" + this.numModes);
                        System.out.println("multitask:" + this.standardMultitask);
                        System.out.println("nodes.size():" + nodes.size());
                        for (Node node : nodes) {
                            System.out.println(node == null ? "null" : node.innovation + ":" + node.ntype);
                        }
                        System.out.println("Done");
                    }
                    if (showInnovationNumbers) {
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
                    boolean recurrent = disLink.recurrent;
                    boolean frozen = disLink.frozen;
                    if (display.displayY == target.displayY) {
                        int mult = recurrent ? -1 : 1;
                        if (frozen) {
                            g.setColor(Color.CYAN);
                        } else if (!recurrent) {
                            g.setColor(Color.BLACK);
                        } else {
                            g.setColor(Color.GREEN);
                        }
                        drawLink(g, disLink.weight,
                                display.displayX + (NODE_DIM / 2),
                                display.displayY + (NODE_DIM / 2),
                                display.displayX + (NODE_DIM / 2) + (mult * LINK_CURVE_OFFSET),
                                display.displayY + (NODE_DIM / 2) - (mult * LINK_CURVE_OFFSET));
                        drawLink(g, disLink.weight,
                                display.displayX + (NODE_DIM / 2) + (mult * LINK_CURVE_OFFSET),
                                display.displayY + (NODE_DIM / 2) - (mult * LINK_CURVE_OFFSET),
                                target.displayX + (NODE_DIM / 2),
                                target.displayY + (NODE_DIM / 2));
                    } else {
                        if (frozen) {
                            g.setColor(Color.CYAN);
                        } else if (recurrent) {
                            g.setColor(Color.GREEN);
                        } else { // if (display.displayY > target.displayY) {
                            g.setColor(Color.BLACK);
                        }
                        drawLink(g, disLink.weight,
                                display.displayX + (NODE_DIM / 2),
                                display.displayY + (NODE_DIM / 2),
                                target.displayX + (NODE_DIM / 2),
                                target.displayY + (NODE_DIM / 2));
                    }
                }
            }
        }

        for (int i = 0; i < modeAssociations.length; i++) {
            g.setColor(CombinatoricUtilities.colorFromInt(i + 1));
            g.fillRect(100 + (i * 2 * NODE_DIM), 2, 2 * NODE_DIM, 2 * NODE_DIM);
            g.setColor(CombinatoricUtilities.colorFromInt(modeAssociations[i] + 1));
            g.fillRect(100 + (i * 2 * NODE_DIM), 2 + 2 * NODE_DIM, 2 * NODE_DIM, 2 * NODE_DIM);
        }
    }

    private static void drawLink(Graphics2D g, double weight, int x1, int y1, int x2, int y2) {
        final int MAX_LINES = NODE_DIM;
        int lines = Math.max(1, (int) (Math.abs(ActivationFunctions.tanh(weight)) * MAX_LINES));
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
                    g.drawLine(x1 - xOffset + j + (int) (xSign * i * xSegmentLength), y1 + (int) (ySign * i * ySegmentLength),
                            x1 - xOffset + j + (int) (xSign * (i + 0.5) * xSegmentLength), y1 + (int) (ySign * (i + 0.5) * ySegmentLength));
                }
            }
        }
    }
}
