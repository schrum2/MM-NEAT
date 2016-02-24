/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 * This is fairly sloppy code. If I had planned to evolve ghosts from
 * the beginning, I would have set up the sensor blocks differently,
 * but since I didn't, I have to find a way to duplicate some similar sensors
 * without re-coding everything.
 * 
 * This sensor block takes a MsPacManSensorBlock in the constructor, and simply
 * uses its sensor values to get sensor values for the ghosts. This only works
 * for aspects of the world that are not dependent on the position of the sensing
 * agent.
 * @author Jacob Schrum
 */
public class GhostReuseMsPacManSensorBlock extends GhostSensorBlock {
    private final MsPacManSensorBlock block;

    public GhostReuseMsPacManSensorBlock(MsPacManSensorBlock block){
        this.block = block;
    }
    
    @Override
    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int ghostIndex) {
        return block.incorporateSensors(inputs, in, gf, 0); // Direction should be meaningless
    }

    @Override
    public int incorporateLabels(String[] labels, int in) {
        return block.incorporateLabels(labels, in);
    }

    @Override
    public int numberAdded() {
        return block.numberAdded();
    }

}
