/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.lair;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class PacManLairDistanceBlock extends LairDistanceBlock {

    @Override
    public String sourceLabel() {
        return "Pac-Man";
    }

    @Override
    public int getTarget(GameFacade gf) {
        return gf.getPacmanCurrentNodeIndex();
    }
}
