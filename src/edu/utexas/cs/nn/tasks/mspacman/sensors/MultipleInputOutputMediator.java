package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 * Combine any set of mediators into one
 *
 * @author Jacob Schrum
 */
public class MultipleInputOutputMediator extends MsPacManControllerInputOutputMediator {

    protected MsPacManControllerInputOutputMediator[] mediators;

    public MultipleInputOutputMediator(MsPacManControllerInputOutputMediator[] mediators) {
        this.mediators = mediators;
    }

    @Override
    public void mediatorStateUpdate(GameFacade gs) {
        for (int i = 0; i < mediators.length; i++) {
            mediators[i].mediatorStateUpdate(gs);
        }
    }

    @Override
    public void reset() {
        for (int i = 0; i < mediators.length; i++) {
            mediators[i].reset();
        }
    }

    @Override
    public double[] getInputs(GameFacade gs, int currentDir) {
        double[] inputs = new double[numIn()];
        int in = 0;
        for (int i = 0; i < mediators.length; i++) {
            double[] subIn = mediators[i].getInputs(gs, currentDir);
            for (int j = 0; j < subIn.length; j++) {
                inputs[in++] = subIn[j];
            }
        }
        return inputs;
    }

    @Override
    public String[] sensorLabels() {
        String[] labels = new String[numIn()];
        int in = 0;
        for (int i = 0; i < mediators.length; i++) {
            String[] subLabels = mediators[i].sensorLabels();
            for (int j = 0; j < subLabels.length; j++) {
                labels[in++] = i + ":" + subLabels[j];
            }
        }
        return labels;
    }

    @Override
    public int numIn() {
        int in = 0;
        for (int i = 0; i < mediators.length; i++) {
            int subIn = mediators[i].numIn();
            for (int j = 0; j < subIn; j++) {
                in++;
            }
        }
        return in;
    }
}
