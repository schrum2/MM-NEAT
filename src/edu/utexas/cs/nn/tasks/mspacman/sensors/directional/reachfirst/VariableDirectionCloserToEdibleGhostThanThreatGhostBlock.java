/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 * This sensor block sees if pacman can safely reach the place where an edible
 * ghost currently is ... does not account for the movement of the edible ghost.
 * 
 * Another problem: If the edible ghost itself will transition to being a threat
 * before pacman can possibly reach it, the sensor does not report this. In other
 * words, the sensor says it is safe to attack the edible ghost, even though it
 * may be guaranteed that the ghost will become a deadly threat before eaten.
 * 
 * @author Jacob Schrum
 */
public class VariableDirectionCloserToEdibleGhostThanThreatGhostBlock extends VariableDirectionCloserToTargetThanThreatGhostBlock {

    public VariableDirectionCloserToEdibleGhostThanThreatGhostBlock(int dir) {
        super(dir);
    }

    public VariableDirectionCloserToEdibleGhostThanThreatGhostBlock(int dir, int[] ghosts) {
        super(dir, ghosts);
    }

    @Override
    public String getTargetType() {
        return "Edible Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations();
    }
}
