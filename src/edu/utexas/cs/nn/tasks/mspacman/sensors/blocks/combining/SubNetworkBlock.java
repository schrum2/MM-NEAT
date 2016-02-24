package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 * Input block that makes the outputs of one network the inputs to another in a
 * hierarchical fashion
 *
 * @author Jacob Schrum
 */
public class SubNetworkBlock<T extends Network> extends MsPacManSensorBlock {
    /*
     * Subnetwork that provides inputs to another network
     */

    private Network subnet;
    /*
     * Inputs fed to subnetwork
     */
    private final MsPacManControllerInputOutputMediator subnetMediator;
    /**
     * Name describing network in labels
     */
    private final String name;
    private final boolean includeInputs;

    public SubNetworkBlock(Network n, MsPacManControllerInputOutputMediator subnetMediator, String name, boolean includeInputs) {
        this.subnet = n;
        if (subnet instanceof TWEANN) {
            ((TWEANN) subnet).canDraw = false;
        }
        this.subnetMediator = subnetMediator;
        this.name = name;
        this.includeInputs = includeInputs;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            SubNetworkBlock other = (SubNetworkBlock) o;
            return other.name.equals(this.name) && other.includeInputs == this.includeInputs && this.subnetMediator.getClass() == other.subnetMediator.getClass();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.subnetMediator != null ? this.subnetMediator.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.includeInputs ? 1 : 0);
        hash = 67 * hash + super.hashCode();
        return hash;
    }

    public void changeNetwork(Genotype<? extends Network> g) {
        changeNetwork(g.getPhenotype());
    }

    public void changeNetwork(Network n) {
        subnet = n;
        if (subnet instanceof TWEANN) {
            ((TWEANN) subnet).canDraw = false;
        }
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        subnetMediator.mediatorStateUpdate(gf);
        double[] subnetInputs = subnetMediator.getInputs(gf, lastDirection);
        if (includeInputs) {
            for (int i = 0; i < subnetInputs.length; i++) {
                inputs[in++] = subnetInputs[i];
            }
        }
        assert ((TWEANN) subnet).canDraw == false : "Shouldn't try to draw subnet inputs";
        double[] subnetOutputs = subnet.process(subnetInputs);
        for (int i = 0; i < subnetOutputs.length; i++) {
            inputs[in++] = subnetOutputs[i];
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        if (includeInputs) {
            String[] subnetInputs = subnetMediator.sensorLabels();
            for (int i = 0; i < subnetInputs.length; i++) {
                labels[in++] = name + ": " + subnetInputs[i];
            }
        }
        String[] outputs = subnetMediator.outputLabels();
        for (int i = 0; i < outputs.length; i++) {
            //System.out.println(name + ": " + outputs[i]);
            labels[in++] = name + ": " + outputs[i];
        }
        return in;
    }

    public int numberAdded() {
        return (includeInputs ? subnet.numInputs() : 0) + subnet.effectiveNumOutputs();
    }

    @Override
    public void reset() {
        subnet.flush();
        subnetMediator.reset();
    }
}
