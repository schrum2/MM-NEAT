/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.abstraction;

import micro.rts.GameState;
import micro.rts.ResourceUsage;
import micro.rts.UnitAction;
import micro.rts.units.Unit;

/**
 *
 * @author santi
 */
public abstract class AbstractAction {
    
    Unit unit;
    
    public AbstractAction(Unit a_unit) {
        unit = a_unit;
    }

    
    public Unit getUnit() {
        return unit;
    }
    
    
    public void setUnit(Unit u) {
        unit = u;
    }
    
    
    public abstract boolean completed(GameState pgs);
    
    
    public UnitAction execute(GameState pgs){
    	return execute(pgs,null);
    };
    
    
    public abstract UnitAction execute(GameState pgs, ResourceUsage ru);
}
