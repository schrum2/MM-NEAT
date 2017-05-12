/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.portfolio.portfoliogreedysearch;

import micro.ai.abstraction.AbstractAction;
import micro.ai.abstraction.Attack;
import micro.ai.abstraction.pathfinding.PathFinding;
import micro.rts.GameState;
import micro.rts.UnitAction;
import micro.rts.units.Unit;
//import micro.rts.units.UnitType;
//import micro.rts.units.UnitTypeTable;

/**
 *
 * @author santi
 */
public class UnitScriptAttack extends UnitScript {
    
    AbstractAction action = null;
    PathFinding pf = null;
    
    public UnitScriptAttack(PathFinding a_pf) {
        pf = a_pf;
    }
    
    public UnitAction getAction(Unit u, GameState gs) {
        if (action.completed(gs)) {
            return null;
        } else {
            return action.execute(gs);
        }
    }
    
    public UnitScript instantiate(Unit u, GameState gs) {
        Unit closestEnemy = closestEnemyUnit(u, gs);
        if (closestEnemy != null) {
            UnitScriptAttack script = new UnitScriptAttack(pf);
            script.action = new Attack(u, closestEnemy, pf);
            return script;
        } else {
            return null;
        }
    }
    
    
    public Unit closestEnemyUnit(Unit u, GameState gs) {
        Unit closest = null;
        int closestDistance = 0;
        for (Unit u2 : gs.getPhysicalGameState().getUnits()) {
            if (u2.getPlayer()>=0 && u2.getPlayer() != u.getPlayer()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closest == null || d < closestDistance) {
                    closest = u2;
                    closestDistance = d;
                }
            }
        }
        return closest;
    }
    
}
