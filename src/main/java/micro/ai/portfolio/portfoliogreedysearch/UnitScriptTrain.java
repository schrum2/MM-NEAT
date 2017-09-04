/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.portfolio.portfoliogreedysearch;

import micro.ai.abstraction.AbstractAction;
import micro.ai.abstraction.Train;
//import java.util.List;
import micro.rts.GameState;
//import micro.rts.PhysicalGameState;
//import micro.rts.Player;
import micro.rts.UnitAction;
import micro.rts.units.Unit;
import micro.rts.units.UnitType;
//import micro.rts.units.UnitTypeTable;

/**
 *
 * @author santi
 */
public class UnitScriptTrain extends UnitScript {
    
    AbstractAction action = null;
    UnitType ut = null;
    
    public UnitScriptTrain(UnitType a_ut) {
        ut = a_ut;
    }
    
    public UnitAction getAction(Unit u, GameState gs) {
        if (action.completed(gs)) {
            return null;
        } else {
            return action.execute(gs);
        }
    }
    
    public UnitScript instantiate(Unit u, GameState gs) {
        UnitScriptTrain script = new UnitScriptTrain(ut);
        script.action = new Train(u, ut);
        return script;
    }
}
