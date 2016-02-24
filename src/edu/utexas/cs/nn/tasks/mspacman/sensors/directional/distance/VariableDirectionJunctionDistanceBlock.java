/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionJunctionDistanceBlock extends VariableDirectionDistanceBlock {

    public VariableDirectionJunctionDistanceBlock(int dir) {
        this(dir,0);
    }
    
    public VariableDirectionJunctionDistanceBlock(int dir, int exclude) {
        super(dir,exclude);
    }

    @Override
    public String getType() {
        return "Junction";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getJunctionIndices();
    }
}
