/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.combine;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionMaxBlock extends VariableDirectionBlock {
    private final VariableDirectionBlock[] blocks;

    public VariableDirectionMaxBlock(VariableDirectionBlock[] blocks){
        this(-1, blocks);
    }
    
    public VariableDirectionMaxBlock(int dir, VariableDirectionBlock[] blocks){
        super(dir);
        this.blocks = blocks;
    }
    
    @Override
    public double wallValue() {
        double result = -1;
        for(int i = 0; i < blocks.length; i++) {
            result = Math.max(result, blocks[i].wallValue());
        }
        return result;
    }

    @Override
    public double getValue(GameFacade gf) {
        double result = -1;
        for(int i = 0; i < blocks.length; i++) {
            blocks[i].setDirection(dir);
            result = Math.max(result, blocks[i].getValue(gf));
        }
        return result;
    }

    @Override
    public String getLabel() {
        String result = "Max";
        for(int i = 0; i < blocks.length; i++) {
            result += "("+ blocks[i].getLabel() +")";
        }
        return result;
    }

}
