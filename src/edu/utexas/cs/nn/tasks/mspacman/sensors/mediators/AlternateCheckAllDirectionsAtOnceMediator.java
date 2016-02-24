/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;

/**
 * Based on Brandstetter's CIG 2012 paper
 *
 * @author Jacob Schrum
 */
public class AlternateCheckAllDirectionsAtOnceMediator extends MsPacManControllerInputOutputMediator {
    private final AlternateCheckEachDirectionMediator[] mediators;

    public AlternateCheckAllDirectionsAtOnceMediator() {
        super();
        mediators = new AlternateCheckEachDirectionMediator[GameFacade.NUM_DIRS];
        for(int i = 0; i < GameFacade.NUM_DIRS; i++) {
            mediators[i] = new AlternateCheckEachDirectionMediator(i);
        }
    }

    @Override
    public double[] getInputs(GameFacade gs, int currentDir) {
        // May need to change the absolute directions that the sensor blocks look at
        // to match the relative directions
        if (CommonConstants.relativePacmanDirections) {
            for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                mediators[i].setDirection((currentDir + i) % GameFacade.NUM_DIRS);
            }
        }

        double[] inputs = new double[this.numIn()];
        for(int i = 0; i < GameFacade.NUM_DIRS; i++){
            double[] partialInputs = mediators[i].getInputs(gs, currentDir);
            System.arraycopy(partialInputs, 0, inputs, i*partialInputs.length, partialInputs.length);
        }
        
        return inputs;
    }

    @Override
    public String[] sensorLabels() {
        String[] labels = new String[this.numIn()];
        for(int i = 0; i < GameFacade.NUM_DIRS; i++){
            String[] partialInputs = mediators[i].sensorLabels();
            System.arraycopy(partialInputs, 0, labels, i*partialInputs.length, partialInputs.length);
        }
        return labels;
    }

    @Override
    public int numIn() {
        return mediators.length * mediators[0].numIn();
    }
}
