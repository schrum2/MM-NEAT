/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.fromjunction;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionDistanceFromJunctionToGhostBlock extends VariableDirectionDistanceFromJunctionBlock {

    private final boolean threat;
    private final boolean edible;

    public VariableDirectionDistanceFromJunctionToGhostBlock(int dir, boolean threat, boolean edible) {
        super(dir);
        this.threat = threat;
        this.edible = edible;
        assert threat || edible : "Must focus on threats and/or edible ghosts";
    }

    public String getType() {
        return (threat && edible ? "Ghost" : (threat ? "Threat" : "Edible") + " Ghost");
    }

    public int[] getTargets(GameFacade gf) {
        return (threat && edible ? ArrayUtil.combineArrays(gf.getThreatGhostLocations(), gf.getEdibleGhostLocations())
                : (threat ? gf.getThreatGhostLocations() : gf.getEdibleGhostLocations()));
    }
}
