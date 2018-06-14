package utopia.controllers.TWEANN;

import machinelearning.networks.FunctionApproximator;
import machinelearning.networks.TWEANN;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.Utils;
import utopia.agentmodel.Controller;
import utopia.agentmodel.EvolvableController;
import utopia.agentmodel.sensormodel.SensorModel;

/**
 * @author Niels van Hoorn
 */
public abstract class TWEANNController extends EvolvableController {

    transient protected boolean firePrimary = true;
    transient private static final double TURNING_THRESHOLD = 0.35;
    public TWEANN tweann;
    protected int numOutputs;
    protected int numNonSensory;
    transient public static final boolean featureSelective = true;

    public void init(){
    }

    //schrum2: need a way to get the NN part of controller
    @Override
    public FunctionApproximator getFunctionApproximator() {
        return tweann;
    }

    public double turnAmountFromNetOutput(double turning) {
        if (Math.abs(turning) < TURNING_THRESHOLD) {
            turning = 0.0;
        } else {
            // stretch the remaining range to fill it out
            turning = (turning - (Math.signum(turning) * TURNING_THRESHOLD)) / (1 - TURNING_THRESHOLD);
        }
        return turning;
    }

    protected double[] processInputsToOutputs(AgentMemory memory) {
        double[] sensors = model.getSensors(memory);
        double[] inputs = new double[this.getNumInputs()];
        inputs[0] = 1;
        System.arraycopy(sensors, 0, inputs, this.numNonSensory, sensors.length);
        double[] outputs = tweann.propagate(inputs);

        return outputs;
    }

    public static int argmax(double[] values) {
        int x = Utils.randomInt(0, values.length - 1);
        for (int i = 0; i < values.length; i++) {
            if (values[i] > values[x]) {
                x = i;
            }
        }
        return x;
    }

    public TWEANNController() {
        this.model = null;
    }

    public TWEANNController(TWEANN tweann, SensorModel model) {
        this.tweann = tweann;
        this.numOutputs = tweann.getNumberOfOutputs();
        this.model = model;
    }

    @Override
    public void reset() {
        this.tweann.flush();
    }

    @Override
    public void mutate() {
        this.tweann.mutate();
    }

    protected int getNumInputs() {
        return model.getNumSensors() + this.numNonSensory;
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    @Override
    public final Controller clone() {
        return (Controller) this.copy();
    }
}
