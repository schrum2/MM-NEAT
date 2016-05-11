package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.MultiplePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.mutation.tweann.*;
import edu.utexas.cs.nn.evolution.nsga2.bd.characterizations.GeneralNetworkCharacterization;
import edu.utexas.cs.nn.evolution.nsga2.bd.localcompetition.TWEANNModesNicheDefinition;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.random.RandomGenerator;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

import java.util.*;

/**
 * Genotype for a Topology and Weight Evolving Neural Network.
 * Standard genotype used by NEAT.
 *
 * @author Jacob Schrum
 */
public class TWEANNGenotype implements Genotype<TWEANN> {

	/**
	 * Common features of both node and link genes
	 * @author Jacob Schrum
	 */
    public abstract class Gene {

        public long innovation; // unique number for each gene
        public boolean frozen; // frozen genes cannot be changed by mutation

        public Gene(long innovation, boolean frozen) {
            this.innovation = innovation;
            this.frozen = frozen;
        }

        public void freeze() {
            frozen = true;
        }

        public void melt() {
            frozen = false;
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
     * @author Jacob Schrum
     */
    public class NodeGene extends Gene {

        public int ntype;
        public int ftype;
        //public String origin = "";
        public boolean fromCombiningCrossover = false;

        /**
         * New node gene, not frozen by default
         *
         * @param ftype = type of activation function
         * @param ntype = type of node (input, hidden, output)
         * @param innovation = unique innovation number for node
         */
        public NodeGene(int ftype, int ntype, long innovation) {
            this(ftype, ntype, innovation, false);
        }

        /**
         * New node gene
         *
         * @param ftype = type of activation function
         * @param ntype = type of node (input, hidden, output)
         * @param innovation = unique innovation number for node
         * @param frozen = false if node can accept new inputs
         */
        public NodeGene(int ftype, int ntype, long innovation, boolean frozen) {
            super(innovation, frozen);
            this.ftype = ftype;
            this.ntype = ntype;
        }

        /**
         * Nodes are equal if they have the same innovation number
         * @param o another node gene
         * @return
         */
        @Override
        public boolean equals(Object o) {
            NodeGene other = (NodeGene) o;
            return innovation == other.innovation;
        }

        @Override
        public NodeGene clone() {
            return new NodeGene(ftype, ntype, innovation, frozen);
        }

        @Override
        public String toString() {
            return "(inno=" + this.innovation + ",ftype=" + this.ftype + ",ntype=" + this.ntype + ",frozen=" + this.frozen + ")"; //  + (origin.isEmpty() ? "" : ",origin=" + origin)
        }
    }

    /**
     * Single link between neurons in a neural network
     * @author Jacob Schrum
     */
    public class LinkGene extends Gene {

        public long sourceInnovation;
        public long targetInnovation;
        public double weight;
        public boolean active;
        public boolean recurrent;

        /**
         * New link which is active and not frozen by default
         *
         * @param sourceInnovation = innovation of node of origin
         * @param targetInnovation = innovation of node target
         * @param weight = weight of new link
         * @param innovation = innovation number of link gene itself
         * @param recurrent = true if link is recurrent
         */
        public LinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean recurrent) {
            this(sourceInnovation, targetInnovation, weight, innovation, recurrent, false);
        }

        /**
         * New link gene, which is active by default
         *
         * @param sourceInnovation = innovation of node of origin @param
         * targetInnovation = innovation of node target @param weights =
         * synaptic weights @param innovation = innovation number of link gene
         * itself @param recurrent = true if link is recurrent @param frozen =
         * true if link cannot be changed
         */
        public LinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean recurrent, boolean frozen) {
            this(sourceInnovation, targetInnovation, weight, innovation, true, recurrent, frozen);
        }

        /**
         * New link gene in which it needs to be specified whether or not it is
         * active
         *
         * @param sourceInnovation = innovation of node of origin @param
         * targetInnovation = innovation of node target @param weights =
         * synaptic weights @param innovation = innovation number of link gene
         * itself @param active = whether the link is present in phenotype
         * network @param recurrent = true if link is recurrent @param frozen =
         * true if this link can no longer be changed by mutation
         */
        public LinkGene(long sourceInnovation, long targetInnovation, double weight, long innovation, boolean active, boolean recurrent, boolean frozen) {
            super(innovation, frozen);
            this.sourceInnovation = sourceInnovation;
            this.targetInnovation = targetInnovation;
            this.weight = weight;
            this.active = active;
            this.recurrent = recurrent;
        }

        @Override
        public LinkGene clone() {
            return new LinkGene(sourceInnovation, targetInnovation, weight, innovation, active, recurrent, frozen);
        }

        @Override
        public String toString() {
            return "(inno=" + this.innovation + ",source=" + this.sourceInnovation + ",target=" + this.targetInnovation + ",weight=" + this.weight + ",active=" + this.active + ",recurrent=" + this.recurrent + ",frozen=" + this.frozen + ")";
            // A shorter output option
            //return "(" + this.innovation + ":" + this.sourceInnovation + "->" + this.targetInnovation + ")";
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
    public int numModes;
    public int neuronsPerMode;
    public boolean standardMultitask;
    public boolean hierarchicalMultitask;
    // For Hierarchical Multitask Networks, each module is associated with one multitask mode
    public int[] modeAssociations; 
    public int[] modeUsage;
    private long id = EvolutionaryHistory.nextGenotypeId();
    public int archetypeIndex;

    /**
     * Copy constructor
     *
     * @param copy
     */
    public TWEANNGenotype(TWEANNGenotype copy) {
        this(copy.nodes, copy.links, copy.neuronsPerMode, copy.standardMultitask, copy.hierarchicalMultitask, copy.archetypeIndex);
    }

    /*
     * Construct new genotype from component node and link lists, along with
     * important parameters
     */
    public TWEANNGenotype(ArrayList<NodeGene> nodes, ArrayList<LinkGene> links, int neuronsPerMode, boolean standardMultitask, boolean hierarchicalMultitask, int archetypeIndex) {
        this.archetypeIndex = archetypeIndex;
        this.nodes = nodes;
        this.links = links;
        this.neuronsPerMode = neuronsPerMode;
        this.standardMultitask = standardMultitask;
        this.hierarchicalMultitask = hierarchicalMultitask;
        
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
        this.numModes = numModes();
        this.modeUsage = new int[numModes];
        //System.out.println("fresh modeUsage from constructor");
        
        // In a new network, each Multitask mode has one network module.
        // This is really only needed if hierarchicalMultitask is true.
        // This information will be incorrect if the network was created by crossover.
        modeAssociations = new int[numModes];
        for(int i = 0; i < numModes; i++) {
            modeAssociations[i] = i;
        }
    }

    private int numModes() {
        return (int) Math.max(1, numOut / (neuronsPerMode + (standardMultitask || CommonConstants.ensembleModeMutation ? 0 : 1)));
    }

    public double numLinks(boolean recurrent) {
        int count = 0;
        for (LinkGene l : links) {
            if (l.recurrent == recurrent) {
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
        numModes = tweann.numModes();
        neuronsPerMode = tweann.neuronsPerMode();
        standardMultitask = tweann.isStandardMultitask();
        hierarchicalMultitask = tweann.isHierarchicalMultitask();
        modeAssociations = Arrays.copyOf(tweann.modeAssociations, numModes);
        modeUsage = tweann.modeUsage;
        //System.out.println("modeUsage transfer from TWEANN:" + Arrays.toString(modeUsage));

        //System.out.println("TWEANNGenotype:" + id + " : numOut:" + numOut + ",numModes:" + numModes + ",neuronsPerMode:" + neuronsPerMode);

        nodes = new ArrayList<NodeGene>(tweann.nodes.size());
        links = new ArrayList<LinkGene>(tweann.nodes.size());

        for (int i = 0; i < tweann.nodes.size(); i++) {
            TWEANN.Node n = tweann.nodes.get(i);
            NodeGene ng = new NodeGene(n.ftype, n.ntype, n.innovation, n.frozen);
            nodes.add(ng);
            LinkedList<LinkGene> temp = new LinkedList<LinkGene>();
            for (TWEANN.Link l : n.outputs) {
                LinkGene lg = new LinkGene(n.innovation, l.target.innovation, l.weight, l.innovation, n.isLinkRecurrnt(l.target.innovation), l.frozen);
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
     * @param numIn = actual number of input sensors
     * @param numOut = number of actuators (in multitask case, #actuations times
     * num tasks. For mode mutation, # of policy neurons per mode)
     * @param archetypeIndex = which archetype to reference for crossover
     */
    public TWEANNGenotype(int numIn, int numOut, int archetypeIndex) {
        this(numIn, numOut, CommonConstants.fs, CommonConstants.ftype, CommonConstants.multitaskModes, archetypeIndex);
    }

    /**
     * New starting genotype with a given number of input and output neurons
     *
     * @param numIn = actual number of input sensors
     * @param numOut = number of actuators (in multitask case, #actuations times
     * num tasks. For mode mutation, # of policy neurons per mode)
     * @param featureSelective = whether initial network is sparsely connected
     * @param ftype = activation function to use on neurons
     * @param numModes = number of MULTITASK modes (does not apply for multiple
     * modes with preference neurons)
     * @param archetypeIndex = which archetype to reference for crossover
     */
    public TWEANNGenotype(int numIn, int numOut, boolean featureSelective, int ftype, int numModes, int archetypeIndex) {
        this(new TWEANN(numIn, numOut, featureSelective, ftype, numModes, archetypeIndex));
    }

    public void calculateNumModes() {
        int oldModes = numModes;
        int count = 0;
        // Need to recalculate outputs as well
        for (NodeGene n : nodes) {
            if (n.ntype == TWEANN.Node.NTYPE_OUTPUT) {
                count++;
            }
        }
        this.numOut = count;
        this.numModes = numModes();
        if (numModes != oldModes) {
            modeUsage = Arrays.copyOf(modeUsage, numModes);
        }
        if (modeUsage == null) {
            throw new IllegalArgumentException("How did modeUsage become null? numModes = " + numModes);
        }
        //System.out.println(this.id + ": calculateNumModes: Reset modeUsage");
    }

    public double lastModesDistance() {
        return twoModesDistance(numModes - 2, numModes - 1);
        //System.out.println("Mode Distance: " + result);
        //return result;
    }

    /**
     * Given two modes in the network, use the GeneralNetworkCharacterization to
     * determine the distance between their behaviors.
     *
     * @param m1 mode index 1
     * @param m2 mode index 2
     * @return mode distance, or max double value if only one mode exists
     */
    public double twoModesDistance(int m1, int m2) {
        if (numModes == 1) {
            return Double.MAX_VALUE;
        } else {
            ArrayList<double[]> syllabus = GeneralNetworkCharacterization.newRandomSyllabus(CommonConstants.syllabusSize);
            ArrayList<Double> last = new ArrayList<Double>();
            ArrayList<Double> prev = new ArrayList<Double>();
            TWEANN t = this.getPhenotype();
            for (double[] inputs : syllabus) {
                t.process(inputs);
                double[] outPrev = t.modeOutput(m1);
                double[] outLast = t.modeOutput(m2);
                prev.addAll(ArrayUtil.doubleVectorFromArray(outPrev));
                last.addAll(ArrayUtil.doubleVectorFromArray(outLast));
            }
            return CartesianGeometricUtilities.euclideanDistance(prev, last);
        }
    }

//    public static boolean test(String label, boolean b) {
//        System.out.println(label + ":" + b);
//        return b;
//    }
    public void mutate() {
        //System.out.println("Mutate:" + this.id);
        StringBuilder sb = new StringBuilder();
        sb.append(this.getId());
        sb.append(" ");
        // Melting/Freezing
        new MeltThenFreezePolicyMutation().go(this, sb);
        new MeltThenFreezePreferenceMutation().go(this, sb);
        new MeltThenFreezeAlternateMutation().go(this, sb);
        // Delete
        new DeleteLinkMutation().go(this, sb);
        new DeleteModeMutation().go(this, sb);
        // Forms of mode mutation
        if ( // No more modes than max allowed
                this.numModes < CommonConstants.maxModes
                // Make sure modes are somewhat evenly used
                && (CommonConstants.ensembleModeMutation
                || modeUsage.length != numModes // possible if mode usage is actually selector's subnet usage
                || CommonConstants.minimalSubnetExecution
                || this.minModeUsage() >= (1.0 / (CommonConstants.usageForNewMode * numModes)))
                && // Only allow mode mutation when number of modes is same for all
                (!CommonConstants.onlyModeMutationWhenModesSame || EvolutionaryHistory.minModes == EvolutionaryHistory.maxModes)
                && // Make sure modes are different 
                (CommonConstants.distanceForNewMode == -1 || CommonConstants.distanceForNewMode < lastModesDistance())
                && // If using niche restriction
                (!CommonConstants.nicheRestrictionOnModeMutation
                || // Only allow new modes if niche with more or equal modes is doing well
                this.numModes <= TWEANNModesNicheDefinition.bestHighModeNiche())) {
            //System.out.println("In Mode Mutation Block");
            new MMP().go(this, sb);
            new MMR().go(this, sb);
            new MMD().go(this, sb);
            new FullyConnectedModeMutation().go(this, sb);
        }
        // Standard NEAT mutations
        int chance = 0;
        do {
            new SpliceNeuronMutation().go(this, sb);
            new NewLinkMutation().go(this, sb);
            chance++;
        } while (CommonConstants.mutationChancePerMode && chance < this.numModes);
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
     * @return
     */
    public int fullyConnectedModeMutation() {
        int linksAdded = 0;
        int neuronsToAdd = neuronsPerMode + (TWEANN.preferenceNeuron() ? 1 : 0);
        for (int i = 0; i < neuronsToAdd; i++) {
            addRandomFullyConnectedOutputNode(CommonConstants.ftype);
            linksAdded += numIn;
        }
        numModes++;
        //EvolutionaryHistory.netHasModes(numModes);
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
     * false if mode is connected to old mode.
     * @param numLinks = number of links going in to each new output neuron
     * return the number of links actually added
     * @return Number of new links created to connect to new module
     */
    public int modeMutation(boolean randomSources, int numLinks) {
        if (!randomSources && numLinks > 1) {
            System.out.println("MM(P) can only add one link per mode!");
            System.exit(1);
        }

        int ftype = CommonConstants.mmpActivationId ? ActivationFunctions.FTYPE_ID : CommonConstants.ftype;
        int numLinksActuallyAdded = 0; // Add up since duplicate links won't be added
        //int neuronsToAdd = neuronsPerMode + (TWEANN.preferenceNeuron() ? 1 : 0);
        for (int i = 0; i < neuronsPerMode; i++) {
            double[] weights = new double[numLinks];
            long[] linkInnovations = new long[numLinks];
            long[] sourceInnovations = new long[numLinks];
            for (int j = 0; j < numLinks; j++) {
                sourceInnovations[j] = randomSources ? getRandomNonOutputNodeInnovationNumber() : nodes.get(nodes.size() - (neuronsPerMode + 1)).innovation;
                linkInnovations[j] = EvolutionaryHistory.nextInnovation();
                //weights[j] = randomSources ? RandomNumbers.fullSmallRand() : 1.0;
                weights[j] = 2 * RandomNumbers.fullSmallRand();
            }
            numLinksActuallyAdded += addOutputNode(ftype, sourceInnovations, weights, linkInnovations);
        }
        // Preference neuron only has one input regardless of numLinks.
        // Preference neurons need to be easily alterable.
        if (TWEANN.preferenceNeuron()) {
            addRandomPreferenceNeuron(1);
        }

        numModes++;
        //EvolutionaryHistory.netHasModes(numModes);
        return numLinksActuallyAdded;
    }

    /**
     * Delete a random link. Doesn't care about making the network disconnected.
     * @return The link deleted
     */
    public LinkGene deleteLinkMutation() {
        return deleteLink(RandomNumbers.randomGenerator.nextInt(links.size()));
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

    public void deleteRandomModeMutation() {
        if (numModes > 1) {
            deleteMode(RandomNumbers.randomGenerator.nextInt(numModes));
        }
    }

    public void deleteLeastUsedModeMutation() {
        if (numModes > 1) {
            deleteMode(StatisticsUtilities.argmin(modeUsage));
        }
    }

    /**
     * Remove specified output mode from network.
     *
     * @param modeNum = mode to delete
     */
    private void deleteMode(int modeNum) {
        /* Changes in the way the network archetype is stored make
        mode deletion very complicated. To do it correctly, all innovation
        numbers for the output nodes when need to be shifted over. However,
        even worse, the target innovation number of the links need to be
        shifted. However, the final nail in the coffin is that these
        shifted links need to be checked against existing links connecting
        the shifted innovation numbers, so that the innovation number of
        the links can be reassigned. It's a mess, so the code dies on any
        mode deletion attempt.
        */
        System.out.println("Can't do mode deletion");
        System.exit(1);
        
        //System.out.println("Delete mode: " + modeNum);
        int outputStart = outputStartIndex();
        int actualNeuronsPerMode = neuronsPerMode + (TWEANN.preferenceNeuron() ? 1 : 0);
        for (int i = actualNeuronsPerMode - 1; i >= 0; i--) {
            deleteOutputNeuron(outputStart + (modeNum * actualNeuronsPerMode) + i);
        }
        numModes--;
    }

    /**
     * Delete single output neuron from network. Links into and out of the node
     * are deleted as well.
     *
     * @param nodeNum = index in "nodes" of neuron to delete (must be an output
     * node)
     */
    private void deleteOutputNeuron(int nodeNum) {
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
        //perturbLink(randomAlterableLink(), RandomNumbers.fullSmallRand());
        perturbLink(randomAlterableLink(), MMNEAT.weightPerturber.randomOutput());
    }

    /**
     * Mutation involving a chance of each weight being perturbed
     * @param rand = Random number generator to use
     * @param rate = Chance for each individual link mutation
     */
    public void allWeightMutation(RandomGenerator rand, double rate) {
        for (LinkGene l : links) {
            if (!l.frozen && RandomNumbers.randomGenerator.nextDouble() < rate) {
                //System.out.println("perturbed");
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
            if (!lg.frozen && lg.active) {
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
            if (!lg.frozen && lg.active) {
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
     * @param linkIndex = index of link in links. Cannot be frozen
     * @param delta = amount to change weight by
     */
    public void perturbLink(int linkIndex, double delta) {
        LinkGene lg = links.get(linkIndex);
        perturbLink(lg, delta);
    }

    public void perturbLink(LinkGene lg, double delta) {
        if (lg.frozen) {
            System.out.println("Cannot perturb frozen link!");
            System.exit(1);
        }
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
     * Set weight at index to new value
     *
     * @param lg link gene to alter
     * @param w new weight
     */
//    public void setWeight(int linkIndex, double w) {
//        LinkGene lg = links.get(linkIndex);
//        setWeight(lg, w);
//    }
    public void setWeight(LinkGene lg, double w) {
        if (lg.frozen) {
            System.out.println("Cannot set frozen link!");
            System.exit(1);
        }
        lg.weight = w;
    }

    /**
     * Returns LinkGene for newNode between nodes with the given linkInnovations
     * numbers
     *
     * @param sourceInnovation = linkInnovations of nodeInnovation node
     * @param targetInnovation = linkInnovations of sourceInnovation node
     * @return = null on failure, LinkGene otherwise
     */
    private LinkGene getLinkBetween(long sourceInnovation, long targetInnovation) {
        for (LinkGene l : links) {
            if (l.sourceInnovation == sourceInnovation && l.targetInnovation == targetInnovation) {
                return l;
            }
        }
        return null;
    }

    public void linkMutation() {
        linkMutation(getRandomLinkSourceNodeInnovationNumber(), RandomNumbers.fullSmallRand());
    }

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
     * @param includeInputs = true if input neurons can potentially be selected
     * @return innovation number of chosen random node (under restrictions)
     */
    private long getRandomAlterableConnectedNodeInnovationNumber(long source, boolean includeInputs) {
        int sourceIndex = indexOfNodeInnovation(source);
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
            if (n.frozen) {
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
                // Small possibility with mode deletion: fix
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
     * @param sourceInnovation = linkInnovations number of nodeInnovation node
     * @param targetInnovation = linkInnovations number of sourceInnovation node
     * @param weights = weights of new newNode
     * @param linkInnovations = linkInnovations number of new newNode
     */
    private void addLink(long sourceInnovation, long targetInnovation, double weight, long innovation) {
        if (getLinkBetween(sourceInnovation, targetInnovation) == null) {
            int target = indexOfNodeInnovation(targetInnovation);
            int source = indexOfNodeInnovation(sourceInnovation);
            //System.out.println(nodeInnovation + "->" + sourceInnovation);
            LinkGene lg = new LinkGene(sourceInnovation, targetInnovation, weight, innovation, target <= source);
            links.add(lg);
        }
    }

    public void spliceMutation() {
        spliceMutation(CommonConstants.ftype);
    }

    private void spliceMutation(int ftype) {
        LinkGene lg = randomAlterableLink();
        long source = lg.sourceInnovation;
        long target = lg.targetInnovation;
        long newNode = EvolutionaryHistory.nextInnovation();
        double weight1 = CommonConstants.minimizeSpliceImpact ? RandomNumbers.randomSign() * 0.00001 : RandomNumbers.fullSmallRand();
        double weight2 = CommonConstants.minimizeSpliceImpact ? RandomNumbers.randomSign() * 0.00001 : RandomNumbers.fullSmallRand();
        long toLink = EvolutionaryHistory.nextInnovation();
        long fromLink = EvolutionaryHistory.nextInnovation();
        spliceNode(ftype, newNode, source, target, weight1, weight2, toLink, fromLink);
    }

    /**
     * Modifies archetype!
     *
     * Splice a new node between two connected nodes along the newNode
     *
     * @param ftype = activation function type of new node
     * @param newNodeInnovation = linkInnovations number of new node
     * @param sourceInnovation = linkInnovations of nodeInnovation node for
     * splice
     * @param targetInnovation = linkInnovations of sourceInnovation node for
     * splice
     * @param weights = weights of the new newNode from the nodeInnovation to
     * the new node
     * @param toLinkInnovation = new linkInnovations number for newNode between
     * nodeInnovation and new node
     * @param fromLinkInnovation = new linkInnovations number for newNode
     * between new node and sourceInnovation
     */
    private void spliceNode(int ftype, long newNodeInnovation, long sourceInnovation, long targetInnovation, double weight1, double weight2, long toLinkInnovation, long fromLinkInnovation) {
        NodeGene ng = new NodeGene(ftype, TWEANN.Node.NTYPE_HIDDEN, newNodeInnovation);
        LinkGene lg = getLinkBetween(sourceInnovation, targetInnovation);
        lg.active = CommonConstants.minimizeSpliceImpact;
        nodes.add(Math.min(outputStartIndex(), Math.max(numIn, indexOfNodeInnovation(sourceInnovation) + 1)), ng);
        int index = EvolutionaryHistory.indexOfArchetypeInnovation(archetypeIndex, sourceInnovation);
        //System.out.println("Innovation " + sourceInnovation + " is at index " + index);
        int pos = Math.min(EvolutionaryHistory.firstArchetypeOutputIndex(archetypeIndex), Math.max(numIn, index + 1));
        //System.out.println("Pos:" + pos + ", numIn:" + numIn);
        EvolutionaryHistory.archetypeAdd(archetypeIndex, pos, ng.clone(), numModes == 1, "splice " + sourceInnovation + "->" + targetInnovation);
        LinkGene toNew = new LinkGene(sourceInnovation, newNodeInnovation, weight1, toLinkInnovation, indexOfNodeInnovation(newNodeInnovation) <= indexOfNodeInnovation(sourceInnovation));
        LinkGene fromNew = new LinkGene(newNodeInnovation, targetInnovation, weight2, fromLinkInnovation, indexOfNodeInnovation(targetInnovation) <= indexOfNodeInnovation(newNodeInnovation));
        links.add(toNew);
        links.add(fromNew);
    }

    /*
     * Modifies archetype
     *
     * Should always be called for archetype as well
     */
    private void addFullyConnectedOutputNode(int ftype, long newNodeInnovation, ArrayList<Double> weights, ArrayList<Long> linkInnovations) {
        NodeGene ng = new NodeGene(ftype, TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
        nodes.add(ng);
        numOut++;
        EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "full output");
        //EvolutionaryHistory.archetypeOut[archetypeIndex]++;
        for (int i = 0; i < numIn; i++) {
            LinkGene toNew = new LinkGene(nodes.get(i).innovation, newNodeInnovation, weights.get(i), linkInnovations.get(i), false);
            links.add(toNew);
        }
    }

    public void weakenAllModes(double portion) {
        for (int i = 0; i < numModes; i++) {
            weakenModePreference(i, portion);
        }
    }

    /**
     * Decrease the weights going into a given preference neuron so that its
     * mode will keep the same behavior, but be less likely to be chosen.
     *
     * @param mode = mode to weaken
     * @param portion should be between 0 and 1: Fraction to reduce weight by
     */
    public void weakenModePreference(int mode, double portion) {
        System.out.println("Weaken mode " + mode + " by " + portion);
        // Identify preference neuron
        int outputStart = outputStartIndex();
        int preferenceLoc = outputStart + (mode * (neuronsPerMode + 1)) + neuronsPerMode;
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
     * @param ftype = Activation function type
     * @param sourceInnovations = list of neurons that will link to the new one
     * @param weights = weights for the synapses linking the sourceInnovations
     * to this new node
     * @param linkInnovations = innovation numbers for each of the new synapses
     * @return number of links actually added
     */
    private int addOutputNode(int ftype, long[] sourceInnovations, double[] weights, long[] linkInnovations) {
        long newNodeInnovation = -(numIn + numOut) - 1;
        NodeGene ng = new NodeGene(ftype, TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
        HashSet<Long> addedLinks = new HashSet<Long>();
        for (int i = 0; i < weights.length; i++) {
            if (!addedLinks.contains(sourceInnovations[i])) {
                addedLinks.add(sourceInnovations[i]);
                LinkGene toNew = new LinkGene(sourceInnovations[i], newNodeInnovation, weights[i], linkInnovations[i], false);
                links.add(toNew);
            }
        }
        nodes.add(ng);
        numOut++;
        EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "new output");
        //EvolutionaryHistory.archetypeOut[archetypeIndex]++;
        return addedLinks.size();
    }

    private int indexOfNodeInnovation(long innovation) {
        return indexOfGeneInnovation(innovation, nodes);
    }

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

    @SuppressWarnings("unchecked")
	public Genotype<TWEANN> crossover(Genotype<TWEANN> g) {
        return MMNEAT.crossoverOperator.crossover(this, g);
    }
    
    /**
     * I have serious reservations about this method. I'm not
     * sure it will really work properly, but it should serve as
     * a good starting point.
     * @param first mode associations of the network this one is replacing.
     * @param second mode associations of the network this one was crossed with.
     */
    public void crossModeAssociations(int[] first, int[] second) {
        modeAssociations = new int[numModes];
        for(int i = 0; i < modeAssociations.length; i++) {
            if(i < first.length) {
                modeAssociations[i] = first[i];
            } else {
                modeAssociations[i] = second[i]; // Will this ever be used?
            }
        }
    }

    /**
     * Generate and return phenotype TWEANN from genotype
     *
     * @return executable TWEANN
     */
    public TWEANN getPhenotype() {
        TWEANN result = new TWEANN(this);
        // This is the point where old parent mode usage us finally erased
        this.modeUsage = result.modeUsage;
        return result;
    }

    /**
     * Copies the TWEANNGenotype via the trick of generating a TWEANN, then
     * using it to generate a new Genotype
     *
     * @return = copy of genotype
     */
    public Genotype<TWEANN> copy() {
        int[] temp = modeUsage;
        TWEANNGenotype result = new TWEANNGenotype(this.getPhenotype());
        // Mode usage is erased by getPhenotype(), so it is restored here
        modeUsage = temp;
        result.modeUsage = new int[temp.length];
        System.arraycopy(this.modeUsage, 0, result.modeUsage, 0, modeUsage.length);
        //System.out.println("Source copy genotype: " + Arrays.toString(modeUsage));
        //System.out.println("Destination copy genotype: " + Arrays.toString(result.modeUsage));
        //System.out.println("Copied: " + id + " -> " + result.id);
        return result;
    }

    /**
     * Get fresh new instance of genotype, in order to start evolution
     *
     * @return new genotype for starting population
     */
    public Genotype<TWEANN> newInstance() {
        TWEANNGenotype result;
        if (MMNEAT.ea instanceof MultiplePopulationGenerationalEA) {
            // Networks from different sub-populations could have differeing numbers of inputs and outputs
            result = new TWEANNGenotype(this.numIn, this.numOut, this.archetypeIndex);
        } else {
            result = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, this.archetypeIndex);
        }
        result.modeUsage = new int[result.numModes];
        return result;
    }

    @Override
    public String toString() {
        String result = id + " (modes:" + numModes + ")" + "\n" + this.nodes + "\n" + this.links;
        return result;
    }

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

    public void modeDuplication() {
        int mode = RandomNumbers.randomGenerator.nextInt(this.numModes);
        duplicateMode(mode);
    }

    /**
     * Duplicate each individual neuron of an output mode
     *
     * @param mode = mode to duplicate
     */
    private void duplicateMode(int mode) {
        // One-mode network is missing first preference neuron
        if (numModes == 1 && numOut == neuronsPerMode && TWEANN.preferenceNeuron()) {
            addRandomPreferenceNeuron(1);
        }
        int outputStart = outputStartIndex();
        // Duplicate the policy neurons
        int modeStart = outputStart + (mode * (neuronsPerMode + (TWEANN.preferenceNeuron() ? 1 : 0)));
        for (int i = 0; i < neuronsPerMode; i++) {
            duplicateOutputNeuron(modeStart + i); // Add policy neurons
        }
        if (TWEANN.preferenceNeuron()) {
            addRandomPreferenceNeuron(1);
        }
        // Increase num modes
        this.numModes++;
        //EvolutionaryHistory.netHasModes(numModes);
    }

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
        addOutputNode(CommonConstants.ftype, sourceInnovations, weights, linkInnovations);
    }

    /**
     * Modifies archetype!
     *
     * Take a network without preference neurons (unimodal or multitask) and
     * insert a preference neuron after a mode. In order to keep the innovation
     * numbers sorted in decreasing negative order, the innovation numbers of
     * the policy neurons need to be shifted.
     *
     * @param modeIndex = index of mode to give preference neuron
     */
    public void insertPreferenceNeuron(int modeIndex) {
        int outputStart = outputStartIndex();
        // Assume other modes before modeIndex have NOT had preference neurons added yet
        int desiredPreferenceLoc = outputStart + (modeIndex * neuronsPerMode) + neuronsPerMode;
        assert desiredPreferenceLoc <= nodes.size() : "Desired location too high! desiredPreferenceLoc:" + desiredPreferenceLoc + ",nodes.size()=" + nodes.size() + ",neuronsPerMode=" + neuronsPerMode + ",outputStart=" + outputStart;
        // Node that will link into the new preference neuron
        long randomSourceInnovation = nodes.get(RandomNumbers.randomGenerator.nextInt(outputStart)).innovation;
        // Last mode?
        long newNodeInnovation;
        if (desiredPreferenceLoc == nodes.size()) {
            // Then just add the preference neuron ... easy
            newNodeInnovation = -(numIn + numOut) - 1;
            //System.out.println("Pref node at end: " + desiredPreferenceLoc + " w/innovation " + newNodeInnovation);
            // Create the output node
            NodeGene ng = new NodeGene(CommonConstants.ftype, TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
            nodes.add(ng);
            EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "insert end preference");
        } else {
            NodeGene current = nodes.get(desiredPreferenceLoc);
            // Get the innovation num of node currently in that position
            newNodeInnovation = current.innovation;
            //System.out.println("Node at " + desiredPreferenceLoc + " w/innovation " + newNodeInnovation + " being replaced");
            NodeGene newPref = new NodeGene(CommonConstants.ftype, TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
            // Put preference neuron after mode and before next mode
            nodes.add(desiredPreferenceLoc, newPref);

//            System.out.print("Output innovations before: ");
//            for(int i = 0; i < numOut; i++){
//                System.out.print(nodes.get(outputStart + i).innovation + " ");
//            }
//            System.out.println();

            // Shift all subsequent innovation numbers
            for (int i = desiredPreferenceLoc + 1; i < nodes.size(); i++) {
                //System.out.print("\t" + nodes.get(i).innovation + " becomes ");
                nodes.get(i).innovation--;
                //System.out.println(nodes.get(i).innovation);
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
            // The last node is actually the one with the new innovation, so it is added to archetype
            EvolutionaryHistory.archetypeAdd(archetypeIndex, nodes.get(nodes.size() - 1).clone(), "insert middle preference");
        }
        // Add one random link to new preference neuron
        //System.out.println("Add link from " + randomSourceInnovation + " to " + newNodeInnovation);
        this.addLink(randomSourceInnovation, newNodeInnovation, RandomNumbers.fullSmallRand(), EvolutionaryHistory.nextInnovation());
        numOut++;
        //EvolutionaryHistory.archetypeOut[archetypeIndex]++;
    }

    /**
     * Modifies archetype!
     *
     * Duplicates a single output neuron at index neuronIndex in nodes.
     * Duplication means copying all links that go into the node, including the
     * weights.
     *
     * @param neuronIndex = index in nodes of neuron to duplicate
     */
    private void duplicateOutputNeuron(int neuronIndex) {
        NodeGene n = nodes.get(neuronIndex);
        if (n.ntype != TWEANN.Node.NTYPE_OUTPUT) {
            System.out.println("Node to duplicate not an output node");
            System.exit(1);
        }

        //long newNodeInnovation = EvolutionaryHistory.nextInnovation();
        // Slots are already reserved for future output nodes
        long newNodeInnovation = -(numIn + numOut) - 1;
        // Create the output node
        NodeGene ng = new NodeGene(CommonConstants.ftype, TWEANN.Node.NTYPE_OUTPUT, newNodeInnovation);
        // Copy all links from old node
        for (NodeGene p : nodes) {
            LinkGene lg = getLinkBetween(p.innovation, n.innovation);
            if (lg != null && lg.active) {
                // Copy newNode if it exists
                LinkGene duplicate;
                if (p.innovation == n.innovation) {
                    duplicate = new LinkGene(ng.innovation, ng.innovation, lg.weight, EvolutionaryHistory.nextInnovation(), false);
                } else {
                    duplicate = new LinkGene(p.innovation, ng.innovation, lg.weight, EvolutionaryHistory.nextInnovation(), false);
                }
                links.add(duplicate);
            }
        }
        nodes.add(ng);
        numOut++;
        EvolutionaryHistory.archetypeAdd(archetypeIndex, ng.clone(), "duplicate output");
        //EvolutionaryHistory.archetypeOut[archetypeIndex]++;
    }

    /**
     * Freezes all preferences neurons along with all components that influence
     * them.
     */
    public void freezePreferenceNeurons() {
        assert TWEANN.preferenceNeuron() : "Cannot freeze preference neurons if there are none";
        //System.out.println("\tFreeze preference neurons in " + this.getId());
        int outputStart = this.outputStartIndex();
        for (int i = 0; i < numModes; i++) {
            int neuronIndex = outputStart + neuronsPerMode + (i * (neuronsPerMode + 1));
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
        //System.out.println("\tFreeze policy neurons in " + this.getId());
        int outputStart = this.outputStartIndex();
        for (int i = 0; i < numModes; i++) {
            for (int j = 0; j < neuronsPerMode; j++) {
                int neuronIndex = outputStart + (i * (neuronsPerMode + 1)) + j;
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
        int firstPreference = outputStart + this.neuronsPerMode;
        // If first preference neuron is frozen, assume all are
        if (nodes.get(firstPreference).frozen) {
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
                // Don't allow recurrent connected to cascade across other outputs
                // to freeze the whole network
                otherOutputs.add(ng.innovation);
            }
        }
        freezeInfluences(nodeInnovation, otherOutputs);
    }

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
     * Freeze all components that influence a particular mode, both policy and
     * preference.
     *
     * @param m mode to freeze, 0-indexed
     */
    public void freezeMode(int m) {
        int outputStart = this.outputStartIndex();
        int neuronsInMode = neuronsPerMode + (TWEANN.preferenceNeuron() ? 1 : 0);
        for (int j = 0; j < neuronsInMode; j++) {
            int neuronIndex = outputStart + (m * neuronsInMode) + j;
            long innovation = nodes.get(neuronIndex).innovation;
            freezeInfluences(innovation);
        }
    }

    /**
     * Freeze whole network so components cannot be altered by mutation. Should
     * only be used before adding a new mode. The new mode will be alterable.
     */
    public void freezeNetwork() {
        //System.out.println("Freeze network");
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
        //System.out.println("\tMelt " + this.getId());
        for (NodeGene ng : nodes) {
            ng.melt();
        }
        for (LinkGene lg : links) {
            lg.melt();
        }
    }

    /**
     * Returns the percentage of the time that the most-used mode is used
     *
     * @return
     */
    public double maxModeUsage() {
        if (CommonConstants.ensembleModeMutation) {
            return 0;
        }
        double[] dist = StatisticsUtilities.distribution(modeUsage);
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
    public double minModeUsage() {
        if (CommonConstants.ensembleModeMutation) {
            return 0;
        }
        double[] dist = StatisticsUtilities.distribution(modeUsage);
        //System.out.println("minModeUsage: modeUsage=" + Arrays.toString(modeUsage) + ": dist=" + Arrays.toString(dist));
        if (dist.length == 0) {
            return 0;
        } else {
            return StatisticsUtilities.minimum(dist);
        }
    }

    public double wastedModeUsage(int maxModes) {
        double waste = 0;
        double[] dist = StatisticsUtilities.distribution(modeUsage);
        for (int i = 0; i < dist.length; i++) {
            waste += Math.max(0, dist[i] - (1 / maxModes));
        }
        return waste / maxModes;
    }

    public long getId() {
        return id;
    }

    /**
     * This function gives a measure of compatibility between two
     * TWEANNGenotypes by computing a linear combination of 3 characterizing
     * variables of their compatibilty. The 3 variables represent PERCENT
     * DISJOINT GENES, PERCENT EXCESS GENES, MUTATIONAL DIFFERENCE WITHIN
     * MATCHING GENES. So the formula for compatibility is:
     * disjoint_coeff*pdg+excess_coeff*peg+mutdiff_coeff*mdmg. The 3
     * coefficients are global system parameters
     * @param g genotype
     * @return measure of compatability
     */
    public double compatibility(TWEANNGenotype g) {

        //Innovation numbers
        long p1innov;
        long p2innov;

        //Intermediate value
        double mut_diff;

        //Set up the counters
        double num_disjoint = 0.0;
        double num_excess = 0.0;
        double mut_diff_total = 0.0;
        double num_matching = 0.0; //Used to normalize mutation_num differences

        LinkGene _gene1;
        LinkGene _gene2;

        double max_genome_size; //Size of larger Genome

        //Get the length of the longest Genome for percentage computations
        int size1 = this.links.size();
        int size2 = g.links.size();
        max_genome_size = Math.max(size1, size2);
        //Now move through the Genes of each potential parent
        //until both Genomes end
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

                //Extract current linkInnovations numbers
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

        // Return the compatibility number using compatibility formula
        // Note that mut_diff_total/num_matching gives the AVERAGE
        // difference between mutation_nums for any two matching Genes
        // in the Genome.
        // Look at disjointedness and excess in the absolute (ignoring size)

        return ((num_disjoint / max_genome_size) + (num_excess / max_genome_size)
                + 0.4 * (mut_diff_total / num_matching));
    }

    public static void sortLinkGenes(ArrayList<LinkGene> temp) {
        Collections.sort(temp, new Comparator<LinkGene>() {

            public int compare(LinkGene o1, LinkGene o2) {
                return (int) Math.signum(o1.innovation - o2.innovation);
            }
        });
    }

    public long biggestInnovation() {
        long max = 0;
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
    private int outputStartIndex() {
        return nodes.size() - numOut;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof TWEANNGenotype)) {
            return false;
        }
        TWEANNGenotype other = (TWEANNGenotype) o;
        return id == other.id;
    }

    /**
     * A main method with some informal tests
     */
    public static void main(String[] args) {
        Parameters.initializeParameterCollections(new String[]{"io:false", "recurrency:false", "mmdRate:0.1"});
        //CommonConstants.freezeBeforeModeMutation = true;
        MMNEAT.loadClasses();
        TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
        MMNEAT.genotype = tg1.copy();
        EvolutionaryHistory.initArchetype(0);
        TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);

        final int MUTATIONS1 = 10;

        for (int i = 0; i < MUTATIONS1; i++) {
            tg1.mutate();
            tg2.mutate();
        }

        double[] inputs = RandomNumbers.randomArray(tg1.numIn);

        //tg1.freezeInfluences(tg1.nodes.get(tg1.nodes.size()-2).innovation);

        DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1");
        DrawingPanel p2 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2");
        p2.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
        tg1.getPhenotype().draw(p1, true);
        tg2.getPhenotype().draw(p2, true);

        new MMR().mutate(tg1);
        tg1.freezePreferenceNeurons();
        System.out.println("Frozen Pref:" + Arrays.toString(tg1.getPhenotype().process(inputs)));

        DrawingPanel p3 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1 MMD");
        p3.setLocation(0, TWEANN.NETWORK_VIEW_DIM + 10);
        tg1.getPhenotype().draw(p3, true);

        new MMR().mutate(tg2);
        tg2.freezePolicyNeurons();
        System.out.println("Frozen Policy:" + Arrays.toString(tg2.getPhenotype().process(inputs)));

        DrawingPanel p4 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2 MMD");
        p4.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, TWEANN.NETWORK_VIEW_DIM + 10);
        tg2.getPhenotype().draw(p4, true);


        //TWEANNCrossover cross = new TWEANNCrossover();
        //TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);

        for (int i = 0; i < MUTATIONS1; i++) {
            tg1.mutate();
            tg2.mutate();
        }

        System.out.println("Post Mutate Frozen Pref:" + Arrays.toString(tg1.getPhenotype().process(inputs)));
        System.out.println("Post Mutate Frozen Policy:" + Arrays.toString(tg2.getPhenotype().process(inputs)));

        DrawingPanel p5 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 1");
        p5.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), 0);
        tg1.getPhenotype().draw(p5, true);

        DrawingPanel p6 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 2");
        p6.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), TWEANN.NETWORK_VIEW_DIM + 10);
        //new2.getPhenotype().draw(p6, true);
        tg2.getPhenotype().draw(p6, true);


    }
}
