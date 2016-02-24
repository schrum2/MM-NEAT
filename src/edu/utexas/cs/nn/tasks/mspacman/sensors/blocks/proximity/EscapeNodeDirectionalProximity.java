/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.tasks.mspacman.data.EscapeNodes;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class EscapeNodeDirectionalProximity extends DirectionalProximityBlock {

    private final EscapeNodes escapeNodes;

    public EscapeNodeDirectionalProximity(EscapeNodes escapeNodes) {
        this.escapeNodes = escapeNodes;
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return escapeNodes.getNodes();
    }

    @Override
    public String targetType() {
        return "Escape Node";
    }
}
