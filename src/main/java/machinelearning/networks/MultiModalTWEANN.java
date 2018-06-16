/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machinelearning.networks;

import java.util.Vector;
import machinelearning.evolution.evolvables.Evolvable;
import utopia.Utils;

/**
 *
 * @author Jacob Schrum
 */
public class MultiModalTWEANN extends TWEANN {

    protected int numModes;
    protected int outputWidth;
    public static final double ADD_OUTPUT_MODE_RATE = 0.1;
    public static final double DELETE_OUTPUT_MODE_RATE = 0.1;
    public Vector<Integer> modeHistory = new Vector<Integer>();

    public int getNumModes() {
        return numModes;
    }

    @Override
    public Evolvable getNewInstance() {
        TWEANN net = (TWEANN) super.getNewInstance();
        return new MultiModalTWEANN(net, net.getNumberOfOutputs(), 1);
    }

    private MultiModalTWEANN(TWEANN net, int outputWidth, int numModes) {
        this(net.allnodes, net.genes, net.net_id, outputWidth, numModes);
    }

    public MultiModalTWEANN(int numInputs, int numOutputs, boolean featureSelective) {
        super(numInputs, numOutputs, featureSelective);
        outputWidth = numOutputs;
        numModes = 1;
    }

    public MultiModalTWEANN(Vector<NNode> in, Vector<NNode> out, Vector<NNode> all, int xnet_id, Vector<Gene> _genes, int outputWidth, int numModes) {
        super(in, out, all, xnet_id, _genes);
        this.outputWidth = outputWidth;
        this.numModes = numModes;
    }

    public MultiModalTWEANN(Vector<NNode> all, Vector<Gene> _genes, int xnet_id, int outputWidth, int numModes) {
        super(all, _genes, xnet_id);
        this.outputWidth = outputWidth;
        this.numModes = numModes;
    }

    @Override
    public int getNumberOfOutputs() {
        return outputWidth;
    }

    @Override
    public TWEANN duplicateWithNewID() {
        return new MultiModalTWEANN(super.duplicateWithNewID(), this.outputWidth, this.numModes);
    }

    @Override
    public double[] propagate(double[] doubles) {
        double[] fullOutput = processInputsToOutputs(doubles);
        double[] modeOutput = new double[this.outputWidth];

        int bestMode = 0;
        if (fullOutput.length == modeOutput.length) {
            modeOutput = fullOutput;
        } else {
            double maxPreference = -Double.MAX_VALUE;
            for (int i = 0; i < this.numModes; i++) {
                double preferenceOutput = fullOutput[(i * (this.outputWidth + 1)) + this.outputWidth];
                if (preferenceOutput > maxPreference) {
                    maxPreference = preferenceOutput;
                    bestMode = i;
                }
            }

            for (int x = 0; x < this.outputWidth; x++) {
                modeOutput[x] = fullOutput[(bestMode * (this.outputWidth + 1)) + x];
            }
        }
        modeHistory.set(bestMode, modeHistory.get(bestMode) + 1);

        return modeOutput;
    }

    public void mutate_add_output_mode() {
        System.out.println("NET"+ this.net_id + ":mutate_add_output_mode");
        if (super.getNumberOfOutputs() == this.outputWidth) {
            System.out.println("NET"+ this.net_id + ":add preference for first mode");
            // Add preference node for the first mode
            addOutputNode();
        }

        for (int i = 0; i < this.outputWidth + 1; i++) {
            System.out.println("NET"+ this.net_id + ":add node");
            // Add nodes for new mode
            addOutputNode();
        }

        this.numModes++;
    }

    public int addOutputNode() {

        double weight = Utils.randposneg() * Utils.randomFloat();
        int nodenum1 = Utils.randomInt(0, allnodes.size() - outputs.size() - 1);

        NNode source = allnodes.elementAt(nodenum1);
        int curnode_id = TWEANN.getCur_node_id_and_increment();
        NNode new_node = new NNode(NODE_TYPE_NEURON, curnode_id, NODE_LABEL_OUTPUT);
        double Gene_innov1 = TWEANN.getCurr_innov_num_and_increment();
        Gene newGene1 = new Gene(weight, source, new_node, Gene_innov1, 0);
        TWEANN.innovations.add(new Innovation(newGene1));

        //Now add the new NNode and new genes to the TWEANNGenome

        genes.add(newGene1);
        allnodes.add(new_node);
        outputs.add(new_node);

        rebuild();

        return new_node.node_id;

    }

    public void mutate_delete_least_used_output_mode() {
        System.out.println("NET"+ this.net_id + ":mutate_delete_least_used_output_mode");
        if (numModes > 1) {
            System.out.println("NET"+ this.net_id + ":Enough modes to delete");
            int target = 0;
            int currentLeast = Integer.MAX_VALUE;
            for (int i = 0; i < modeHistory.size(); i++) {
                if (modeHistory.get(i) < currentLeast) {
                    target = i;
                    currentLeast = modeHistory.get(i);
                }
            }
            System.out.println("NET"+ this.net_id + ":Least used mode = "+currentLeast+" out of "+modeHistory.size());

            boolean success = deleteOutputMode(target);
            if (success) {
                this.numModes--;
            } else {
                System.out.println("FATAL ERROR: Failure to delete mode");
                System.out.println("modeHistory: " + modeHistory);
                System.out.println(this);
                System.exit(1);
            }
        }
    }

    public boolean deleteOutputMode(int mode) {
        int start = firstOutputOfMode(mode);
        boolean result = true;
        //System.out.println("DELETE MODE: ");
        for (int i = outputWidth; i >= 0; i--) {
            System.out.println("NET"+ this.net_id + "Removing: " + i + "/" + outputs.size());
            int id = outputs.get(start + i).node_id;
            boolean nodeResult = deleteOutputNode(id);
            if(!nodeResult){
                System.out.println("ERROR:");
                System.out.println("start: " + start);
                System.out.println("Failed removing id: " + id);
                System.out.println("Num removed: " + i);
                System.out.println("outputs: " + outputs);
                System.out.println("numModes: " + numModes);
                System.out.println("outputWidth: " + outputWidth);
            }
            result = result && nodeResult;
        }
        return result;
    }

    private int firstOutputOfMode(int mode) {
        return mode * (outputWidth + 1);
    }

    public boolean deleteOutputNode(int nodeID) {
        boolean outputRemoved = false;
        boolean allRemoved = false;
        int geneCountRemoved = 0;

        // Remove node from output list
        for (int i = 0; i < outputs.size(); i++) {
            if (outputs.get(i).node_id == nodeID) {
                outputs.remove(i);
                outputRemoved = true;
                break;
            }
        }

        // Remove node from all node list
        for (int i = allnodes.size() - 1; i >= 0; i--) {
            if (allnodes.get(i).node_id == nodeID) {
                allnodes.remove(i);
                allRemoved = true;
                break;
            }
        }

        // Remove links from the deleted node
        for (int i = genes.size() - 1; i >= 0; i--) {
            if (genes.get(i).lnk.in_node.node_id == nodeID) {
                genes.remove(i);
                geneCountRemoved++;
            }
        }

        // Remove links to the deleted node
        for (int i = genes.size() - 1; i >= 0; i--) {
            if (genes.get(i).lnk.out_node.node_id == nodeID) {
                genes.remove(i);
                geneCountRemoved++;
            }
        }

        boolean result = outputRemoved && allRemoved && (geneCountRemoved >= 1);
        if(!result) {
            System.out.println("ERROR:");
            System.out.println("outputRemoved: " + outputRemoved);
            System.out.println("allRemoved: " + allRemoved);
            System.out.println("geneCountRemoved: " + geneCountRemoved);
        }
        return result;
    }

    @Override
    public void mutate() {
        mutate(true);
    }

    public void mutate(boolean modeMutate) {
        super.mutate();

        if (modeMutate) {
            if (Utils.randomFloat() < DELETE_OUTPUT_MODE_RATE) {
                this.mutate_delete_least_used_output_mode();
            }

            if (Utils.randomFloat() < ADD_OUTPUT_MODE_RATE) {
                this.mutate_add_output_mode();
            }
        }
    }

    @Override
    public void flush() {
        super.flush();
        // Temp: for testing
        //System.out.println("MODE USAGE: " + modeHistory);

        modeHistory = new Vector<Integer>(numModes);
        for (int i = 0; i < numModes; i++) {
            modeHistory.add(0);
        }
    }
}
