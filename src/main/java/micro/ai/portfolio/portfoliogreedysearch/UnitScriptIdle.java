/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.portfolio.portfoliogreedysearch;

import micro.ai.abstraction.AbstractAction;
import micro.ai.abstraction.Idle;
//import micro.ai.abstraction.pathfinding.PathFinding;
import micro.rts.GameState;
import micro.rts.UnitAction;
import micro.rts.units.Unit;

/**
 *
 * @author santi
 */
public class UnitScriptIdle extends UnitScript {
    
    AbstractAction action = null;
    
    public UnitScriptIdle() {
    }
    
    public UnitAction getAction(Unit u, GameState gs) {
        if (action.completed(gs)) {
            return null;
        } else {
            return action.execute(gs);
        }
    }
    
    public UnitScript instantiate(Unit u, GameState gs) {
        UnitScriptIdle script = new UnitScriptIdle();
        script.action = new Idle(u);
        return script;
    }    
}
