/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob
 */
public class VariableDirectionLastDirectionBlock extends VariableDirectionBlock {

    public VariableDirectionLastDirectionBlock(int dir) {
        super(dir);
    }

    @Override
    public double wallValue() {
        return 0;
    }

    @Override
    public double getValue(GameFacade gf) {
        return dir == gf.getPacmanLastMoveMade() ? 1 : 0;
    }

    @Override
    public String getLabel() {
        return "Last Direction?";
    }
}
