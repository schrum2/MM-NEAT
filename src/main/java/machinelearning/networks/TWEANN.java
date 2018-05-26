package machinelearning.networks;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import machinelearning.Tools;
import machinelearning.evolution.evolvables.Evolvable;
import utopia.Utils;

/**
 * Created by Jacob Schrum
 *
 * NEAT style networks
 */
public class TWEANN implements Evolvable, Serializable, FunctionApproximator {

    private static TWEANN initialMember = null;
    transient private static final double LINK_MUTATE_POWER = 1.0;
    transient private static final double LINK_MUTATE_RATE = 0.2;
    transient private static final double ADD_NODE_RATE = 0.2;
    transient private static final double ADD_LINK_RATE = 0.3;
    transient private static final double GENE_TOGGLE_RATE = 0.2;
    transient private static final int ADD_LINK_TRIES = 10;
    transient private static final int GENE_TOGGLE_TIMES = 5;
    public static Vector<Innovation> innovations = new Vector<Innovation>();
    public static double cur_innov_num = 1;

    public static double getCurr_innov_num_and_increment() {
        return cur_innov_num++;
    }
    public static int cur_node_id = 1;

    public static int getCur_node_id_and_increment() {
        return cur_node_id++;
    }
    public static int cur_net_id = 1;

    public static int getCur_net_id_and_increment() {
        return cur_net_id++;
    }

    @Override
    public void mutate() {
        this.mutate_link_weight(LINK_MUTATE_POWER, LINK_MUTATE_RATE, MUTATION_TYPE_PERTURB);
        if (Utils.randomFloat() < ADD_NODE_RATE) {
            this.mutate_add_node();
        }
        if (Utils.randomFloat() < ADD_LINK_RATE) {
            this.mutate_add_link(ADD_LINK_TRIES);
        }
        if (Utils.randomFloat() < GENE_TOGGLE_RATE) {
            this.mutate_toggle_enable(GENE_TOGGLE_TIMES);
        }
    }

    private static double randomWeight() {
        return Utils.randposneg() * Utils.randomFloat() * 10.0;
    }
    /*
    public static enum ACTIVATION_FUNCTION {SIGMOID, TANH}
    public static enum NODE_TYPE {NEURON, SENSOR}
    public static enum NODE_LABEL {INPUT, OUTPUT, HIDDEN, BIAS}
    public static enum MUTATION_TYPE {PERTURB, REPLACE}
    public static enum INNOVATION_TYPE {NEWNODE, NEWLINK}
     */
    transient public static final int ACTIVATION_FUNCTION_SIGMOID = 0;
    transient public static final int ACTIVATION_FUNCTION_TANH = 1;
    transient public static final int NODE_TYPE_NEURON = 2;
    transient public static final int NODE_TYPE_SENSOR = 3;
    transient public static final int NODE_LABEL_INPUT = 4;
    transient public static final int NODE_LABEL_OUTPUT = 5;
    transient public static final int NODE_LABEL_HIDDEN = 6;
    transient public static final int NODE_LABEL_BIAS = 7;
    transient public static final int MUTATION_TYPE_PERTURB = 8;
    transient public static final int MUTATION_TYPE_REPLACE = 9;
    transient public static final int INNOVATION_TYPE_NEWNODE = 10;
    transient public static final int INNOVATION_TYPE_NEWLINK = 11;

    /**
     * This class serves as a way to record innovations
     *  specifically, so that an innovation in one genome can be
     *  compared with other innovations in the same epoch, and if they
     *  Are the same innovation, they can both be assigned the same innnovation number.
     *  This class can encode innovations that represent a new link
     *  forming, or a new node being added.  In each case, two
     *  nodes fully specify the innovation and where it must have
     *  occured.  (Between them)
     *
     */
    public static class Innovation implements Serializable {

        /**
         * Either NEWNODE or NEWLINK
         */
        int innovation_type;
        /**
         * Two nodes specify where the innovation took place : this is the node input
         */
        int node_in_id;
        /**
         * Two nodes specify where the innovation took place : this is the node output
         */
        int node_out_id;
        /**
         * The number assigned to the innovation
         */
        double innovation_num1;
        /**
         * If this is a new node innovation,then there are 2 innovations (links)
         * added for the new node
         */
        double innovation_num2;
        /**
         * If a link is added, this is its weight
         */
        double new_weight;
        /**
         * If a new node was created, this is its node_id
         */
        int newnode_id;
        /**
         * If a new node was created, this is
         *  the innovnum of the gene's link it is being
         * stuck inside
         */
        double old_innov_num;

        @Override
        public String toString() {
            String ret = "Innovation:";
            switch (innovation_type) {
                case INNOVATION_TYPE_NEWLINK:
                    ret += "link:(" + this.innovation_num1 + ") " + this.node_in_id + "->(" + this.new_weight + ")->" + this.node_out_id;
                    break;
                case INNOVATION_TYPE_NEWNODE:
                    ret += "node:(" + this.innovation_num1 + "," + this.innovation_num2 + ") " + this.node_in_id + "->|" + this.newnode_id + "|->" + this.node_out_id;
                    break;
            }
            return ret;
        }

        public Innovation(int nin, int nout, double num1, double w) {
            this(INNOVATION_TYPE_NEWLINK, nin, nout, num1, 0, 0, 0, w);
        }

        public Innovation(int nin, int nout, double num1, double num2, int newid, double oldinnov) {
            this(INNOVATION_TYPE_NEWNODE, nin, nout, num1, num2, newid, oldinnov, 0);
        }

        public Innovation(Gene gene) {
            this(gene.lnk.in_node.node_id, gene.lnk.out_node.node_id, gene.innovation_num, gene.lnk.weight);
        }

        public Innovation(int itype, int nin, int nout, double num1, double num2, int newid, double oldinnov, double nweight) {
            innovation_type = itype;
            node_in_id = nin;
            node_out_id = nout;
            innovation_num1 = num1;
            innovation_num2 = num2;
            newnode_id = newid;
            old_innov_num = oldinnov;
            new_weight = nweight;
        }
    }

    public static class Gene implements Serializable {

        /** if a reference to object for identify input/output node and features */
        protected Link lnk;
        /** is historical marking of node */
        protected double innovation_num;
        /** how much mutation has changed the link */
        protected double mutation_num;
        /** is a flag: is TRUE the Gene is enabled FALSE otherwise. */
        protected boolean enable;

        @Override
        public String toString() {
            return "Gene:(" + (enable ? "on" : "off") + ")Inno:" + innovation_num + "," + lnk;
        }

        public Gene(Gene g, NNode inode, NNode onode) {
            this(g.lnk.weight, inode, onode, g.innovation_num, g.mutation_num, g.enable);
        }

        public Gene(Link l, double innov, double mnum) {
            this(l.weight, l.in_node, l.out_node, innov, mnum);
        }

        public Gene(double w, NNode inode, NNode onode, double innov, double mnum) {
            this(w, inode, onode, innov, mnum, true);
        }

        public Gene(double w, NNode inode, NNode onode, double innov, double mnum, boolean _enable) {
            lnk = new Link(w, inode, onode);
            innovation_num = innov;
            mutation_num = mnum;
            enable = _enable;
        }
    }

    public static class NNode implements Serializable {

        /** type is either SIGMOID ..or others that can be added */
        protected int ftype;
        /** type is either NEURON or SENSOR */
        protected int type;
        /** The total activation entering in this Node */
        protected double activation;
        /** A list of pointers to incoming weighted signals from other nodes */
        protected Vector<Link> incoming = new Vector<Link>();
        /** A list of pointers to links carrying this node's signal */
        protected Vector<Link> outgoing = new Vector<Link>();
        /** Numeric identification of node */
        protected int node_id;
        /** Used for genetic marking of nodes. are 4  type of node : input,bias,hidden,output */
        protected int gen_node_label;
        /** this value is how many time this node are activated during activation of network */
        protected double activation_count;
        /** activation value of node at time t-1; Holds the previous step's activation for recurrency */
        protected double last_activation;
        /** activation value of node at time t-2 Holds the activation before  the previous step's */
        protected double last_activation2;
        /**
         * Is a  temporary reference  to a  Node ; Has used for generate a new genome during duplicateWithNewID phase of genotype.
         * @supplierCardinality 1
         * @clientCardinality 1
         */
        protected NNode dup;
        public int inner_level;
        public boolean is_traversed;

        @Override
        public String toString() {
            String ret = "NNode:id:" + node_id + ",activation_count:" + activation_count + ",node_type:" + type + ",node_label:" + gen_node_label + "\n";
            ret += "In\n";
            for (Link l : incoming) {
                ret += "   (" + l.in_node.activation + ")" + l + "\n";
            }
            ret += "Out (" + this.activation + ")\n";
            for (Link l : outgoing) {
                ret += "   " + l + "\n";
            }
            return ret;
        }

        public NNode(int ntype, int nodeid) {
            this(ntype, nodeid, NODE_LABEL_HIDDEN);
        }

        public NNode(int ntype, int nodeid, int placement) {
            activation = 0;
            last_activation = 0;
            last_activation2 = 0;
            type = ntype; //NEURON or SENSOR type
            activation_count = 0; //Inactive upon creation
            node_id = nodeid; // id del nodo
            ftype = ACTIVATION_FUNCTION_TANH; // funt act : signmoide
            gen_node_label = placement;
            dup = null;
            is_traversed = false;
            inner_level = 0;
        }

        public NNode(NNode n) {
            this(n.type, n.node_id, n.gen_node_label);
        }

        public void activate() {
            if (type != NODE_TYPE_SENSOR) {
                double activesum = 0.0; // reset activation value

                double add_amount = 0.0; //For adding to the activesum
                for (Link _link : incoming) {
                    add_amount = _link.weight * _link.in_node.get_active_out();
                    activesum += add_amount;
                } //End for over incoming links

                last_activation2 = last_activation;
                last_activation = activation;

                if (ftype == ACTIVATION_FUNCTION_SIGMOID) {
                    activation = Tools.sigmoid(activesum);
                } else if (ftype == ACTIVATION_FUNCTION_TANH) {
                    activation = Tools.tanh(activesum);
                }
                activation_count += 1.0;
            } //End if _node.type !=SENSOR
        }

        //tested
        public int depth(int xlevel, TWEANN mynet, int xmax_level) {
            // control for loop
            if (xlevel > 100) {
                System.out.print("\n ** DEPTH NOT DETERMINED FOR NETWORK WITH LOOP ");
                System.out.println(mynet);
                System.exit(1);
                return 10;
            }

            //Base Case
            if (this.type == NODE_TYPE_SENSOR) {
                return xlevel;
            }
            xlevel++;

            // recursion case
            int cur_depth = 0; //The depth of the current node
            for (Link _link : this.incoming) {
                NNode _ynode = _link.in_node;

                if (!_ynode.is_traversed) {
                    _ynode.is_traversed = true;
                    cur_depth = _ynode.depth(xlevel, mynet, xmax_level);
                    _ynode.inner_level = cur_depth - xlevel;
                } else {
                    cur_depth = xlevel + _ynode.inner_level;
                }
                if (cur_depth > xmax_level) {
                    xmax_level = cur_depth;
                }
            }
            return xmax_level;

        }

        public double get_active_out() {
            if (activation_count > 0) {
                return activation;
            } else {
                return 0.0;
            }
        }

        /**
         *	Return activation currently in node
         *	from PREVIOUS (time-delayed) time step,
         *	if there is one
         */
        public double get_active_out_td() {
            if (activation_count > 1) {
                return last_activation;
            } else {
                return 0.0;
            }
        }

        public boolean sensor_load(double value) {
            if (type == NODE_TYPE_SENSOR) {
                //Time delay memory
                last_activation2 = last_activation;
                last_activation = activation;

                activation_count++;  //Puts sensor into next time-step
                activation = value;    //ovviamente non viene applicata la f(x)!!!!
                return true;
            } else {
                return false;
            }
        }

        public void resetNNode() {
            activation_count = 0;
            activation = 0;
            last_activation = 0;
            last_activation2 = 0;

            //Flush back link
            for (Link _link : incoming) {
                _link.is_traversed = false;
            }

            //Flush forw link
            for (Link _link : outgoing) {
                _link.is_traversed = false;
            }

        }
    }

    public static class Link implements Serializable {

        /** is a real value of weight of connection(link) */
        protected double weight;
        /** is a reference to an  input node */
        protected NNode in_node;
        /** is a reference to a output node; */
        protected NNode out_node;
        /** this is a flag for compute depth; if TRUE the connection(link) is already analyzed; FALSE otherwise; */
        transient protected boolean is_traversed = false;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Link) {
                Link link = (Link) obj;
                return (this.in_node == link.in_node)
                        && (this.out_node == link.out_node)
                        && (this.weight == link.weight);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "Link:" + in_node.node_id + "->(" + weight + ")->" + out_node.node_id;
        }

        public Link(double w, NNode inode, NNode onode) {
            weight = w;
            in_node = inode;
            out_node = onode;
        }
    }
    /**
     * Each Gene in (3) has a marker telling when it arose historically;
     * Thus, these Genes can be used to speciate the population, and
     * the list of Genes provide an evolutionary history of innovation and link-building
     */
    Vector<Gene> genes;
    /**
     * Is a collection of object NNode can be mapped in a Vector container;
     * this collection represent a group of references to input nodes;
     */
    transient Vector<NNode> inputs;
    /**
     * Is a collection of object NNode can be mapped in a Vector container;
     * this collection represent a group of references to output nodes;
     */
    transient Vector<NNode> outputs;
    /**
     * Is a collection of object NNode can be mapped in a Vector container;
     * this collection represent a group of references to all nodes of this net;
     */
    Vector<NNode> allnodes;
    /** Numeric identification of this network */
    int net_id;

    @Override
    public String toString() {
        String ret = "";
        ret += "TWEANN:" + this.net_id + ":" + allnodes.size() + "\n";
        ret += "Genes:\n";
        for (Gene g : genes) {
            ret += " " + g + "\n";
        }
        ret += "Nodes:\n";
        for (NNode n : allnodes) {
            ret += " " + n + "\n";
        }

        ret += "InputNodes:\n";
        for (NNode n : inputs) {
            ret += " " + n + "\n";
        }
        ret += "OutputNodes:\n";
        for (NNode n : outputs) {
            ret += " " + n + "\n";
        }
        return ret;
    }

    //tested
    public TWEANN(Vector<NNode> in, Vector<NNode> out, Vector<NNode> all, int xnet_id, Vector<Gene> _genes) {
        rebuild(in, out, all, xnet_id, _genes);
    }

    //tested
    private void rebuild(Vector<NNode> in, Vector<NNode> out, Vector<NNode> all, int xnet_id, Vector<Gene> _genes) {
        inputs = in;
        outputs = out;
        allnodes = all;
        net_id = xnet_id;
        genes = _genes;
    }

    //tested
    public TWEANN(Vector<NNode> all, Vector<Gene> the_genes, int xnet_id) {
        rebuild(all, the_genes, xnet_id);
    }

    //tested
    private void rebuild(Vector<NNode> all, Vector<Gene> the_genes, int xnet_id) {
        rebuild(null, null, all, xnet_id, the_genes);
        stripLinks(all);
        fillInputsAndOutputs(all);
        fillInLinks(the_genes);

        //System.out.println(this);
        //System.out.println("------------");
    }

    //tested
    public void rebuild() {
        rebuild(this.allnodes, this.genes, this.net_id);
    }

    /**
     * Creation of a new random TWEANN with :
     * new_id   = numerical identification of TWEANNGenome
     *      numInputs   = number of input nodes
     *      numOutputs   = number of output nodes
     */
    //tested
    public TWEANN(int numInputs, int numOutputs) {
        this(numInputs, numOutputs, false);
    }

    public TWEANN(int numInputs, int numOutputs, boolean featureSelective) {

        NNode newnode = null;

        int totalnodes = numInputs + numOutputs;

        inputs = new Vector<NNode>(numInputs);
        outputs = new Vector<NNode>(numOutputs);
        allnodes = new Vector<NNode>(totalnodes);
        genes = new Vector<Gene>(totalnodes);

        //Build the input nodes
        int count = 0;
        for (count = 1; count <= numInputs; count++) {
            if (count < numInputs) {
                newnode = new NNode(NODE_TYPE_SENSOR, TWEANN.getCur_node_id_and_increment(), NODE_LABEL_INPUT);
            } else {
                newnode = new NNode(NODE_TYPE_SENSOR, TWEANN.getCur_node_id_and_increment(), NODE_LABEL_BIAS);
            }
            allnodes.add(newnode);
            inputs.add(newnode);
        }

        int first_output = totalnodes - numOutputs + 1;

        //Build the output nodes
        for (count = first_output; count <= totalnodes; count++) {
            newnode = new NNode(NODE_TYPE_NEURON, TWEANN.getCur_node_id_and_increment(), NODE_LABEL_OUTPUT);
            //Add the node to the list of nodes
            allnodes.add(newnode);
            outputs.add(newnode);
        }

        if (featureSelective) {
            for (NNode out_node : outputs) {
                int inputNode = Utils.randomInt(0, inputs.size() - 1);
                //System.out.println("IN: " + inputNode + " less than " + (inputs.size() - 1));
                NNode in_node = inputs.get(inputNode);
                double new_weight = Utils.randposneg() * Utils.randomFloat();
                linkNodes(new_weight, in_node, out_node, genes);
            }
        } else {
            for (NNode in_node : inputs) {
                for (NNode out_node : outputs) {
                    //Create the Gene + link
                    double new_weight = Utils.randposneg() * Utils.randomFloat();
                    linkNodes(new_weight, in_node, out_node, genes);
                }
            }
        }

        this.net_id = TWEANN.getCur_net_id_and_increment();

        if (TWEANN.initialMember == null) {
            initialMember = this;
        }
    }

    // By default, do not add genes to innovation list or gene list
    public static Link linkNodes(double weight, NNode from, NNode into) {
        return linkNodes(weight, from, into, null, false, -1);
    }
    // If adding genes, then innovate by default

    public static Link linkNodes(double weight, NNode from, NNode into, Vector<Gene> the_genes) {
        return linkNodes(weight, from, into, the_genes, true, -1);
    }
    // If the_genes != null, a new Gene is added to the gene list.
    // If innovate == false and there is an innovation_num, use the innovation_num instead of
    // a new one and do not add to innovations.
    //tested

    public static Link linkNodes(double weight, NNode from, NNode into, Vector<Gene> the_genes, boolean innovate, double innovation_num) {
        Link newlink = new Link(weight, from, into);
        into.incoming.add(newlink);
        from.outgoing.add(newlink);

        if (the_genes != null) {
            int i; //determine index to place gene so it comes after its last input
            for (i = the_genes.size() - 1; i >= 0; i--) {
                if (the_genes.elementAt(i).lnk.out_node.node_id == from.node_id) {
                    break;
                }
            }
            if (innovate) {
                addInnovativeLinkGene(newlink, i, the_genes);
            } else {
                addUninnovativeLinkGene(newlink, i, the_genes, innovation_num);
            }
        }

        return newlink;
    }
    // Determines whether innovation is needed
    //tested

    public Link add_link(double weight, NNode from, NNode into) {
        Link link = null;
        for (Innovation _innov : TWEANN.innovations) {
            if ((_innov.innovation_type == INNOVATION_TYPE_NEWLINK) && (_innov.node_in_id == from.node_id) && (_innov.node_out_id == into.node_id)) {
                //link = linkNodes(_innov.new_weight, from, into, genes, false, _innov.innovation_num1);
                // schrum2: this way a new link at least has a new weight
                _innov.new_weight = weight;
                link = linkNodes(weight, from, into, genes, false, _innov.innovation_num1);
                break;
            }
        }

        if (link == null) {
            link = linkNodes(weight, from, into, genes, true, -1);
        }
        rebuild();
        return link;
    }

    //tested
    public Link add_link(double weight, int from_pos, int into_pos) {
        return add_link(weight, allnodes.elementAt(from_pos), allnodes.elementAt(into_pos));
    }

    private static void addUninnovativeLinkGene(Link newlink, int gene_pos, Vector<Gene> the_genes, double innovation_num) {
        Gene newGene = new Gene(newlink, innovation_num, 0.0);
        //Add the Gene to the TWEANNGenome
        the_genes.insertElementAt(newGene, gene_pos + 1);
    }

    private static void addInnovativeLinkGene(Link newlink, int gene_pos, Vector<Gene> the_genes) {
        Gene newGene = new Gene(newlink, TWEANN.getCurr_innov_num_and_increment(), 0.0);
        TWEANN.innovations.add(new Innovation(newGene));
        //Add the Gene to the TWEANNGenome
        the_genes.insertElementAt(newGene, gene_pos + 1);
    }

    //tested
    private void fillInputsAndOutputs(Vector<NNode> the_nodes) {
        inputs = new Vector<NNode>();
        outputs = new Vector<NNode>();

        for (NNode _node : the_nodes) {
            if (_node.gen_node_label == NODE_LABEL_INPUT) {
                inputs.add(_node);
            }
            if (_node.gen_node_label == NODE_LABEL_BIAS) {
                inputs.add(_node);
            }
            if (_node.gen_node_label == NODE_LABEL_OUTPUT) {
                outputs.add(_node);
            }
        }
    }

    //tested
    public int indexOfLinkInGenes(Link link) {
        for (int i = 0; i < genes.size(); i++) {
            if (genes.elementAt(i).lnk.equals(link)) {
                return i;
            }
        }
        return -1; //failure
    }
    //tested

    private void stripLinks(Vector<NNode> all) {
        for (NNode node : all) {
            node.incoming = new Vector<Link>();
            node.outgoing = new Vector<Link>();
        }
    }

    // Updates network links to match gene description
    //tested
    private void fillInLinks(Vector<Gene> the_genes) {

        for (Gene gene : the_genes) {
            //System.out.println(gene);
            //Only create the link if the Gene is enabled
            if (gene.enable == true) {
                Link curlink = gene.lnk;

                NNode inode = curlink.in_node;
                NNode onode = curlink.out_node;
                //NOTE: This line could be run through a recurrency check if desired
                // (no need to in the current implementation of NEAT)
                if(inode == null || onode == null){
                    System.out.println("ERROR: A Gene has a null node in the link");
                    System.out.println("IN: " + inode);
                    System.out.println("OUT: " + onode);
                    System.out.println("Link: " + curlink);
                    System.out.println("Net:\n" + this);
                    System.exit(1);
                }
                linkNodes(curlink.weight, inode, onode);
            }
        }
    }

    //tested
    public boolean activate() {
        boolean onetime = false; //Make sure we at least activate once
        int abortcount = 0; //Used in case the output is somehow

        while (outputsoff() || !onetime) {

            ++abortcount;
            if (abortcount >= 30) {
                System.out.print("\n *ERROR* Inputs disconnected from output!");
                System.out.println(this);
                System.exit(1);
                return false;
            }

            // For each node, compute the sum of its incoming activation
            for (NNode _node : allnodes) {
                _node.activate();
            } //End for over all nodes
            onetime = true;
        }

        return true;
    }

    //tested
    public void flush() {
        for (NNode _node : allnodes) {
            _node.resetNNode();
        }
    }

    //tested
    public void load_sensors(double[] sensvals) {
        int counter = 0;
        for (NNode _node : inputs) {
            if (_node.type == NODE_TYPE_SENSOR) {
                _node.sensor_load(sensvals[counter++]);
            }
        }
    }

    /**
     *
     * Find the maximum number of neurons between
     * an ouput and an input
     */
    //tested
    public int max_depth() {

        int cur_depth = 0;
        int max = 0;

        for (NNode _node : allnodes) {
            _node.inner_level = 0;
            _node.is_traversed = false;
        }

        for (NNode _node : outputs) {
            cur_depth = _node.depth(0, this, max);
            if (cur_depth > max) {
                max = cur_depth;
            }
        }

        return max;
    }

    //tested
    public boolean outputsoff() {
        for (NNode _node : outputs) {
            //System.out.println(_node);
            if (_node.activation_count == 0) {
                return true;
            }
        }
        return false;
    }

    //tested
    public int getNumberOfGenes() {
        return genes.size();
    }

    //tested
    @Override
    public double[] propagate(double[] doubles) {
        return processInputsToOutputs(doubles);
    }

    public double[] processInputsToOutputs(double[] doubles) {
        if (doubles.length != getNumberOfInputs()) {
            System.out.println("Incorrect number of outputs!");
            System.exit(1);
        }

        this.load_sensors(doubles);
        this.activate();

        double[] output = new double[outputs.size()];
        for (int i = 0; i < outputs.size(); i++) {
            output[i] = outputs.get(i).get_active_out();
        }

        return output;
    }

    //tested
    @Override
    public int getNumberOfInputs() {
        return inputs.size();
    }

    //tested
    @Override
    public int getNumberOfOutputs() {
        return outputs.size();
    }

    /*
     * Checks that network representation satisfies various invariants
     */
    public boolean verify() {
        int i1 = 0;
        int o1 = 0;
        boolean disab = false;
        int last_id = 0;

        Iterator<Gene> itr_Gene = genes.iterator();
        Iterator<Gene> itr_Gene1 = genes.iterator();

        if (genes.size() == 0) {
            //         System.out.print("\n DEBUG TWEANNGenome.costructor.TWEANNGenome.random");
            //         System.out.println("\n *ERROR* are not Genes");
            return false;
        }

        if (allnodes.size() == 0) {
            //         System.out.print("\n DEBUG TWEANNGenome.costructor.TWEANNGenome.random");
            //         System.out.println("\n *ERROR* are not nodes");
            return false;
        }

        // control if nodes in Gene are defined and are the same nodes il nodes list
        for (Gene _Gene : genes) {
            NNode inode = _Gene.lnk.in_node;
            NNode onode = _Gene.lnk.out_node;

            if (inode == null) {
                System.out.println(" *ERROR* inode = null in TWEANNGenome #" + net_id);
                return false;
            }
            if (onode == null) {
                System.out.println(" *ERROR* onode = null in TWEANNGenome #" + net_id);
                return false;
            }
            if (!allnodes.contains(inode)) {
                System.out.println(
                        "Missing inode:  node defined in Gene not found in Vector nodes of TWEANNGenome #" + net_id);
                System.out.print("\n the inode is=" + inode.node_id);
                return false;
            }
            if (!allnodes.contains(onode)) {
                System.out.println(
                        "Missing onode:  node defined in Gene not found in Vector nodes of TWEANNGenome #" + net_id);
                System.out.print("\n the onode is=" + onode.node_id);
                return false;
            }
        }

        // verify if list nodes is ordered
        for (NNode _node : allnodes) {
            if (_node.node_id < last_id) {
                System.out.println("ALERT: NODES OUT OF ORDER : ");
                System.out.println(
                        " last node_id is= " + last_id + " , current node_id=" + _node.node_id);
                return false;
            }
            last_id = _node.node_id;
        }

        // control in Genes are Gene duplicateWithNewID for contents

        itr_Gene = genes.iterator();
        while (itr_Gene.hasNext()) {
            Gene _Gene = itr_Gene.next();
            i1 = _Gene.lnk.in_node.node_id;
            o1 = _Gene.lnk.out_node.node_id;

            itr_Gene1 = itr_Gene;
            while (itr_Gene1.hasNext()) {
                Gene _Gene1 = itr_Gene1.next();
                if (_Gene1.lnk.in_node.node_id == i1 && _Gene1.lnk.out_node.node_id == o1) {
                    System.out.print(" \n  ALERT: DUPLICATE GeneS :");
                    System.out.print("  inp_node=" + i1 + " out_node=" + o1);
                    System.out.print("  in TWEANNGenome id -->" + net_id);
                    System.out.print("  Gene1 is : ");
//                    gene.op_view();
                    System.out.print("  Gene2 is : ");
//                    _Gene1.op_view();

                    return false;
                }
            }
        }

        if (allnodes.size() >= 500) {
            disab = false;
            itr_Gene = genes.iterator();
            while (itr_Gene.hasNext()) {
                Gene _Gene = itr_Gene.next();

                if (!_Gene.enable && disab) {
                    System.out.print("\n ALERT: 2 DISABLES IN A ROW: " + _Gene.lnk.in_node.node_id);
                    System.out.print(" inp node=" + _Gene.lnk.in_node.node_id);
                    System.out.print(" out node=" + _Gene.lnk.out_node.node_id);
                    System.out.print(" for TWEANN " + net_id);
                    System.out.print("\n Gene is :");
                    //gene.op_view();
                }

                if (!_Gene.enable) {
                    disab = true;
                } else {
                    disab = false;
                }
            }
        }

        return true;
    }

    //tested
    public TWEANN duplicateWithNewID() {
        Vector<NNode> nodes_dup = new Vector<NNode>(allnodes.size());
        Vector<Gene> genes_dup = new Vector<Gene>(genes.size());

        /**
         * duplicateWithNewID NNodes
         */
        for (NNode _node : allnodes) {
            NNode newnode = new NNode(_node);
            _node.dup = newnode;
            nodes_dup.add(newnode);
        }

        /**
         * duplicateWithNewID genes
         */
        for (Gene _gene : genes) {
            // point to news nodes created  at precedent step
            NNode inode = _gene.lnk.in_node.dup;
            NNode onode = _gene.lnk.out_node.dup;

            // creation of new gene with a pointer to new node
            Gene new_gene = new Gene(_gene, inode, onode);
            genes_dup.add(new_gene);
        }

        for (NNode _node : allnodes) {
            _node.dup = null;
        }

        // okay all nodes created, the new TWEANNGenome can be generate
        TWEANN newTWEANN = new TWEANN(nodes_dup, genes_dup, TWEANN.getCur_net_id_and_increment());
        return newTWEANN;
    }

    //tested
    public void mutate_link_weight(double power, double rate, int mutation_type) {

        //The power of mutation will rise farther into the TWEANNGenome
        //on the theory that the older genes are more fit since
        //they have stood the test of time

        // for 50% of Prob. // severe is true

        boolean severe; //Once in a while really shake things up
        if (Utils.randomFloat() > 0.5) {
            severe = true;
        } else {
            severe = false;
        }
        double num = 0.0; //counts gene placement
        double gene_total = (double) genes.size();
        double endpart = gene_total * 0.8; //Signifies the last part of the TWEANNGenome
        double powermod = 1.0; //Modified power by gene number

        for (Gene _gene : genes) {
            //schrum2: are all of these calculations necessary?
            double alter_threshold;
            double change_threshold;
            if (severe) {
                alter_threshold = 0.3;
                change_threshold = 0.1;
            } // with other 50%.....
            else {
                if ((gene_total >= 10.0) && (num > endpart)) {
                    alter_threshold = 0.5;
                    change_threshold = 0.3;
                } else {
                    if (Utils.randomFloat() > 0.5) {
                        alter_threshold = 1.0 - rate;
                        change_threshold = 1.0 - rate - 0.1;
                    } else {
                        alter_threshold = 1.0 - rate;
                        change_threshold = 1.0 - rate;
                    }
                }
            }
            // choise a number from ]-1,+1[
            double weight_amount = Utils.randposneg() * Utils.randomFloat() * power * powermod;
            mutate_gene_weight(_gene, weight_amount, mutation_type, alter_threshold, change_threshold);
            num += 1.0;
        }

        rebuild();
    }

    //tested
    private void mutate_gene_weight(Gene _gene, double weight_amount, int mutation_type, double alter_threshold, double change_threshold) {
        if (mutation_type == MUTATION_TYPE_PERTURB) {
            double randchoice = Utils.randomFloat(); //Decide what kind of mutation to do on a gene, a number from ]0,1[
            mutate_gene_weight(_gene, randchoice, alter_threshold, change_threshold, weight_amount);
        } else if (mutation_type == MUTATION_TYPE_REPLACE) {
            guarranteed_replace_gene_weight(_gene, weight_amount); // duplicateWithNewID to mutation_num, the current weight
        }
    }

    //tested
    public double get_gene_weight(int gene_pos) {
        return genes.elementAt(gene_pos).lnk.weight;
    }
    //tested

    public void guarranteed_replace_gene_weight(int gene_pos, double new_weight) {
        guarranteed_replace_gene_weight(genes.elementAt(gene_pos), new_weight); // duplicateWithNewID to mutation_num, the current weight
    }
    //tested

    public void guarranteed_perturb_gene_weight(int gene_pos, double weight_amount) {
        mutate_gene_weight(genes.elementAt(gene_pos), 1.0, 0.9, 0.0, weight_amount);
    }
    //tested

    public void mutate_gene_weight(int gene_pos, double randchoice, double alter_threshold, double change_threshold, double weight_amount) {
        mutate_gene_weight(genes.elementAt(gene_pos), randchoice, alter_threshold, change_threshold, weight_amount);
    }

    private void guarranteed_replace_gene_weight(Gene _gene) {
        guarranteed_replace_gene_weight(_gene, randomWeight());
    }

    //tested
    private void guarranteed_replace_gene_weight(Gene _gene, double new_weight) {
        mutate_gene_weight(_gene, 0.5, 1.0, 0.0, new_weight); // duplicateWithNewID to mutation_num, the current weight
    }

    //tested
    private void mutate_gene_weight(Gene _gene, double randchoice, double alter_threshold, double change_threshold, double weight_amount) {
        if (randchoice > alter_threshold) {
            _gene.lnk.weight += weight_amount;
        } else if (randchoice > change_threshold) {
            _gene.lnk.weight = weight_amount;
        }
        _gene.mutation_num = _gene.lnk.weight;
    }

    /*
    public TWEANN mate_multipoint(TWEANN g, int genomeid, double fitness1, double fitness2) {

    boolean disable = false; //Set to true if we want to disabled a chosen Gene

    NNode new_inode = null;
    NNode new_onode = null;
    NNode curnode = null;

    Gene chosenGene = null;
    Gene _p1Gene = null;
    Gene _p2Gene = null;
    double p1innov = 0;
    double p2innov = 0;

    int j1;
    int j2;

    //Tells if the first TWEANNGenome (this one) has better fitness or not
    boolean skip = false;

    //Figure out which TWEANNGenome is better
    //The worse TWEANNGenome should not be allowed to add extra structural baggage
    //If they are the same, use the smaller one's disjoint and excess Genes only

    boolean p1better = false;

    int size1 = genes.size();
    int size2 = g.genes.size();

    if (fitness1 > fitness2) {
    p1better = true;
    } else if (fitness1 == fitness2) {
    if (size1 < size2) {
    p1better = true;
    }
    }

    int len_genome = Math.max(size1, size2);
    int len_nodes = allnodes.size();

    Vector<Gene> newGenes = new Vector<Gene>(len_genome);
    Vector<NNode> newnodes = new Vector<NNode>(len_nodes);

    j1 = 0;
    j2 = 0;

    int control_disable = 0;
    int exist_disable = 0;

    while (j1 < size1 || j2 < size2) {
    //  chosen of 'just' Gene

    skip = false; //Default to not skipping a chosen Gene
    if (j1 >= size1) {
    chosenGene = g.genes.elementAt(j2);
    j2++;
    if (p1better) {
    skip = true; //Skip excess from the worse TWEANNGenome
    }
    } else if (j2 >= size2) {
    chosenGene = genes.elementAt(j1);
    j1++;
    if (!p1better) {
    skip = true; //Skip excess from the worse TWEANNGenome
    }
    } else {

    _p1Gene = genes.elementAt(j1);
    _p2Gene = g.genes.elementAt(j2);

    p1innov = _p1Gene.innovation_num;
    p2innov = _p2Gene.innovation_num;
    if (p1innov == p2innov) {
    if (Utils.randomFloat() < 0.5) {
    chosenGene = _p1Gene;
    } else {
    chosenGene = _p2Gene;                    //If one is disabled, the corresponding Gene in the offspring
    //will likely be disabled
    }
    disable = false;
    if ((_p1Gene.enable == false) || (_p2Gene.enable == false)) {
    exist_disable++;
    if (Utils.randomFloat() < 0.75) {
    disable = true;
    control_disable++;
    }
    }
    j1++;
    j2++;

    } else if (p1innov < p2innov) {
    chosenGene = _p1Gene;
    j1++;
    if (!p1better) {
    skip = true;
    }
    } else if (p2innov < p1innov) {
    chosenGene = _p2Gene;
    j2++;
    if (p1better) {
    skip = true;
    }
    }
    }// end chosen Gene

    //Check to see if the chosenGene conflicts with an already chosen Gene
    //gene_pos.e. do they represent the same link

    for (Gene _curGene2 : newGenes) {
    if (_curGene2.lnk.in_node.node_id == chosenGene.lnk.in_node.node_id && _curGene2.lnk.out_node.node_id == chosenGene.lnk.out_node.node_id) {
    skip = true;
    break;
    }
    if (_curGene2.lnk.in_node.node_id == chosenGene.lnk.out_node.node_id && _curGene2.lnk.out_node.node_id == chosenGene.lnk.in_node.node_id) {
    skip = true;
    break;
    }

    }

    if (!skip) {
    //Now add the chosenGene to the baby
    //First, get the trait pointer
    NNode inode = chosenGene.lnk.in_node;
    NNode onode = chosenGene.lnk.out_node;

    //Check for from in the newnodes list
    //Check for from, into in the newnodes list

    //--------------------------------------------------------------------------------
    boolean found;
    if (inode.node_id < onode.node_id) {
    //
    // search the from
    //
    found = false;
    for (int ix = 0; ix < newnodes.size(); ix++) {
    curnode = newnodes.elementAt(ix);
    if (curnode.node_id == inode.node_id) {
    found = true;
    break;
    }

    }

    // if exist , point to exitsting version
    if (found) {
    new_inode = curnode;                    // else create the from
    } else {
    new_inode = new NNode(inode);
    //insert in newnodes list
    node_insert(newnodes, new_inode);
    }
    // search the into
    //
    found = false;
    for (int ix = 0; ix < newnodes.size(); ix++) {
    curnode = (NNode) newnodes.elementAt(ix);
    if (curnode.node_id == onode.node_id) {
    found = true;
    break;
    }

    }

    // if exist , point to exitsting version
    if (found) {
    new_onode = curnode;                    // else create the into
    } else {
    new_onode = new NNode(onode);
    //insert in newnodes list
    node_insert(newnodes, new_onode);
    }




    } // end block : from.node_id < into.node_id
    else {


    //
    // search the into
    //
    found = false;
    for (int ix = 0; ix < newnodes.size(); ix++) {
    curnode = (NNode) newnodes.elementAt(ix);
    if (curnode.node_id == onode.node_id) {
    found = true;
    break;
    }

    }

    // if exist , point to exitsting version
    if (found) {
    new_onode = curnode;                    // else create the into
    } else {
    new_onode = new NNode(onode);
    //insert in newnodes list
    node_insert(newnodes, new_onode);
    }




    //
    // search the from
    //
    found = false;
    for (int ix = 0; ix < newnodes.size(); ix++) {
    curnode = newnodes.elementAt(ix);
    if (curnode.node_id == inode.node_id) {
    found = true;
    break;
    }

    }

    // if exist , point to exitsting version
    if (found) {
    new_inode = curnode;                    // else create the from
    } else {
    new_inode = new NNode(inode);
    //insert in newnodes list
    node_insert(newnodes, new_inode);
    }


    }
    //Add the Gene
    Gene newGene = new Gene(chosenGene, new_inode, new_onode);
    if (disable) {
    newGene.enable = false;
    disable = false;
    }
    newGenes.add(newGene);
    }

    } // end block TWEANNGenome (while)

    TWEANN new_TWEANN = new TWEANN(newnodes, newGenes, genomeid);

    // ----------------------------------------------------------------------------------------

    //	boolean h = new_TWEANN.verify();
    boolean found = false;
    for (int ix = 0; ix < newnodes.size(); ix++) {
    curnode = (NNode) newnodes.elementAt(ix);
    if (curnode.gen_node_label == NODE_LABEL_OUTPUT) {
    found = true;
    break;
    }
    }

    if (!found) {
    System.out.print("\n *--------------- not found output node ----------------------------");
    System.out.print("\n * during mate_multipoint : please control the following's *********");
    System.out.print("\n * control block : ");
    System.out.print("\n TWEANNGenome A= ");
    System.out.print("\n TWEANNGenome B= ");
    System.out.print("\n Result = ");
    System.out.println(this);

    System.exit(0);
    }
    // ----------------------------------------------------------------------------------------


    return new_TWEANN;
    }
     */    //tested
    public static void node_insert(Vector<NNode> nlist, NNode n, int after) {
        for (int j = 0; j < nlist.size(); j++) {
            if (nlist.elementAt(j).node_id == after) {
                nlist.insertElementAt(n, j + 1);
                break;
            }
        }
    }

    //tested
    public boolean mutate_add_link(int tries) {
        Iterator<NNode> itr_node = null;

        //Make attempts to find an unconnected pair
        int trycount = 0;

        itr_node = allnodes.iterator();
        int first_nonsensor = 0;

        //Find the first non-sensor so that the to-node won't look at sensors as
        //possible destinations
        NNode thenode1 = null;
        NNode thenode2 = null;
        boolean found = false;
        while (itr_node.hasNext()) {
            thenode1 = itr_node.next();
            if (thenode1.type != NODE_TYPE_SENSOR) {
                break;
            }
            first_nonsensor++;
        }
        found = false;
        int nodenum1;
        int nodenum2;

        while (trycount < tries) {
            nodenum1 = Utils.randomInt(0, allnodes.size() - 1);
            nodenum2 = Utils.randomInt(first_nonsensor, allnodes.size() - 1);

            // now point to object's nodes
            thenode1 = allnodes.elementAt(nodenum1);
            thenode2 = allnodes.elementAt(nodenum2);

            // verify if the possible new Gene already EXIST
            boolean bypass = false;
            for (Gene _gene : genes) {
                // No inputs to input nodes allowed
                if (thenode2.type == NODE_TYPE_SENSOR) {
                    bypass = true;
                    break;
                }
                // Link already exists
                if (_gene.lnk.in_node == thenode1 && _gene.lnk.out_node == thenode2) {
                    bypass = true;
                    break;
                }
            }

            if (!bypass) {
                trycount = tries;
                found = true;
            } // end block bypass
            // if bypass is true, this Gene is not good
            // and skip to next cycle
            else {
                trycount++;
            }
        } // end block trycount

        if (found) {
            //Choose the new weight
            double new_weight = randomWeight();
            add_link(new_weight, thenode1, thenode2);
            return true;
        }
        return false;
    }

    /*
     * Returns position of disabled gene, or -1 on failure.
     */    //tested
    public int mutate_add_node() {

        Gene newGene1 = null;
        Gene newGene2 = null;
        NNode new_node = null;

        int trycount = 0;

        boolean found = false;

        Gene _Gene = null;
        int Genenum = -1;
        while ((trycount < 20) && (!found)) {
            //Pure random splittingUtils.randomInt
            Genenum = Utils.randomInt(0, genes.size() - 1);
            _Gene = genes.elementAt(Genenum);
            if (_Gene.enable && (_Gene.lnk.in_node.gen_node_label != NODE_LABEL_BIAS)) {
                found = true;
            }
            ++trycount;
        }

        if (!found) {
            return -1;
        }
        _Gene.enable = false;

        //Extract the link
        Link thelink = _Gene.lnk;
        //Extract the weight;
        double oldweight = thelink.weight;
        //Get the old link's trait

        //Extract the nodes
        NNode in_node = thelink.in_node;
        NNode out_node = thelink.out_node;

        boolean done = false;
        Iterator<Innovation> itr_innovation = TWEANN.innovations.iterator();

        while (!done) {
            //Check to see if this innovation already occured in the population
            if (!itr_innovation.hasNext()) {

                //The innovation is totally novel
                //Create the new genes
                //Create the new NNode
                //By convention, it will point to the first trait
                // get the current node id with postincrement

                int curnode_id = TWEANN.getCur_node_id_and_increment();

                // pass this current nodeid to newnode and create the new node
                new_node = new NNode(NODE_TYPE_NEURON, curnode_id, NODE_LABEL_HIDDEN);

                // get the current Gene inovation with post increment
                double Gene_innov1 = TWEANN.getCurr_innov_num_and_increment();

                // create Gene with the current Gene inovation
                newGene1 = new Gene(1.0, in_node, new_node, Gene_innov1, 0);

                // re-read the current innovation with increment
                double Gene_innov2 = TWEANN.getCurr_innov_num_and_increment();

                // create the second Gene with this innovation incremented
                newGene2 = new Gene(oldweight, new_node, out_node, Gene_innov2, 0);

                TWEANN.innovations.add(new Innovation(in_node.node_id, out_node.node_id, Gene_innov1, Gene_innov2, new_node.node_id, _Gene.innovation_num));
                done = true;
            } // end for new innovation case
            else {
                Innovation _innov = itr_innovation.next();

                if ((_innov.innovation_type == INNOVATION_TYPE_NEWNODE) && (_innov.node_in_id == in_node.node_id) && (_innov.node_out_id == out_node.node_id) && (_innov.old_innov_num == _Gene.innovation_num)) {
                    // Create the new genes
                    // pass this current nodeid to newnode
                    new_node = new NNode(NODE_TYPE_NEURON, _innov.newnode_id, NODE_LABEL_HIDDEN);

                    newGene1 = new Gene(1.0, in_node, new_node, _innov.innovation_num1, 0);
                    newGene2 = new Gene(oldweight, new_node, out_node, _innov.innovation_num2, 0);
                    done = true;
                }
            }

        }

        //Now add the new NNode and new genes to the TWEANNGenome

        genes.insertElementAt(newGene1, Genenum + 1);
        genes.insertElementAt(newGene2, Genenum + 2);
        node_insert(allnodes, new_node, newGene1.lnk.in_node.node_id);

        rebuild();

        return Genenum;

    }

    /**
     *     This function gives a measure of compatibility between
     *     two TWEANNGenomes by computing a linear combination of 3
     *     characterizing variables of their compatibilty.
     *     The 3 variables represent PERCENT DISJOINT GeneS,
     *     PERCENT EXCESS GeneS, MUTATIONAL DIFFERENCE WITHIN
     *     MATCHING GeneS.  So the formula for compatibility
     *     is:  disjoint_coeff*pdg+excess_coeff*peg+mutdiff_coeff*mdmg.
     *     The 3 coefficients are global system parameters
     */
    public double compatibility(TWEANN g) {

        //Innovation numbers
        double p1innov;
        double p2innov;

        //Intermediate value
        double mut_diff;

        //Set up the counters
        double num_disjoint = 0.0;
        double num_excess = 0.0;
        double mut_diff_total = 0.0;
        double num_matching = 0.0; //Used to normalize mutation_num differences

        Gene _Gene1 = null;
        Gene _Gene2 = null;

        double max_TWEANNGenome_size; //Size of larger TWEANNGenome

        //Get the length of the longest TWEANNGenome for percentage computations
        int size1 = genes.size();
        int size2 = g.genes.size();
        max_TWEANNGenome_size = Math.max(size1, size2);
        //Now move through the genes of each potential parent
        //until both TWEANNGenomes end
        int j = 0;
        int j1 = 0;
        int j2 = 0;

        for (j = 0; j < max_TWEANNGenome_size; j++) {

            if (j1 >= size1) {
                num_excess += 1.0;
                j2++;
            } else if (j2 >= size2) {
                num_excess += 1.0;
                j1++;
            } else {
                _Gene1 = genes.elementAt(j1);
                _Gene2 = g.genes.elementAt(j2);

                //Extract current innovation numbers
                p1innov = _Gene1.innovation_num;
                p2innov = _Gene2.innovation_num;

                if (p1innov == p2innov) {
                    num_matching += 1.0;
                    mut_diff = Math.abs(_Gene1.mutation_num - _Gene2.mutation_num);
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
        // difference between mutation_nums for any two matching genes
        // in the TWEANNGenome.
        // Look at disjointedness and excess in the absolute (ignoring size)

        double p_disjoint_coeff = 1.0;
        double p_excess_coeff = 1.0;
        double p_mutdiff_coeff = 0.4;

        return (p_disjoint_coeff * (num_disjoint / 1.0) + p_excess_coeff * (num_excess / 1.0) + p_mutdiff_coeff * (mut_diff_total / num_matching));

    }

    //tested
    public boolean mutate_gene_reenable() {
        for (int i = 0; i < genes.size(); i++) {
            if (enable_gene(i)) {
                return true;
            }
        }
        // Means all genes are already enabled
        return false;
    }
    // Toggle TWEANNGenes from enable on to enable off or
    //   vice versa.  Do it times times.
    //tested

    public void mutate_toggle_enable(int times) {
        for (int count = 1; count <= times; count++) {
            //Choose a random gene_num
            int gene_num = Utils.randomInt(0, genes.size() - 1);
            //Toggle the enable on this TWEANNGene
            toggle_gene(gene_num);
        }
    }

    //tested
    public boolean toggle_gene(int position) {
        return ((genes.elementAt(position).enable) ? disable_gene(position) : enable_gene(position));
    }
    //tested

    public boolean enable_gene(int position) {
        Gene _gene = genes.elementAt(position);
        if (!_gene.enable) {
            _gene.enable = true;
            rebuild();
            return true;
        } else {
            return false;
        }
    }

    //tested: for unit tests only
    public boolean hard_disable_gene(int position) {
        return disable_gene(position, true);
    }
    //tested

    public boolean disable_gene(int position) {
        return disable_gene(position, false);
    }
    // hard == true disables gene no matter what
    //tested

    public boolean disable_gene(int position, boolean hard) {
        Gene _gene = genes.elementAt(position);
        if (_gene.enable) {
            //We need to make sure that another TWEANNGene connects out of the in-node
            //Because if not a section of TWEANN will break off and become isolated
            for (int j = 0; j < genes.size(); j++) {
                Gene second_pass_gene = genes.elementAt(j);
                if (hard
                        || ((_gene.lnk.out_node.incoming.size() > 1)
                        && (_gene.lnk.in_node == second_pass_gene.lnk.in_node)
                        && second_pass_gene.enable
                        && (second_pass_gene.innovation_num != _gene.innovation_num))) {

                    _gene.enable = false;
                    rebuild();
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private static TWEANN changeAllWeights(TWEANN net) {
        TWEANN dup = net.duplicateWithNewID();
        for (Gene _gene : dup.genes) {
            dup.guarranteed_replace_gene_weight(_gene);
        }
        dup.rebuild();
        return dup;
    }

    @Override
    public Evolvable getNewInstance() {
        if (TWEANN.initialMember == null) {
            throw new UnsupportedOperationException("No initial TWEANN was available");
        }

        return changeAllWeights(TWEANN.initialMember);
    }

    @Override
    public Evolvable copy() {
        return this.duplicateWithNewID();
    }

    @Override
    public void reset() {
        flush();
    }
}
