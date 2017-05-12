/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.abstraction;

import micro.ai.abstraction.pathfinding.PathFinding;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.ResourceUsage;
import micro.rts.UnitAction;
import micro.rts.units.Unit;

/**
 *
 * @author santi
 */
public class Attack extends AbstractAction  {
    Unit target;
    PathFinding pf;
    
    public Attack(Unit u, Unit a_target, PathFinding a_pf) {
        super(u);
        target = a_target;
        pf = a_pf;
    }
    
    public boolean completed(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        if (!pgs.getUnits().contains(target)) return true;
        return false;
    }

    public UnitAction execute(GameState gs, ResourceUsage ru) {
        
        int dx = target.getX()-unit.getX();
        int dy = target.getY()-unit.getY();
        double d = Math.sqrt(dx*dx+dy*dy);
        if (d<=unit.getAttackRange()) {
            return new UnitAction(UnitAction.TYPE_ATTACK_LOCATION,target.getX(),target.getY());
        } else {
            // move towards the unit:
    //        System.out.println("AStarAttak returns: " + move);
            UnitAction move = pf.findPathToPositionInRange(unit, target.getX()+target.getY()*gs.getPhysicalGameState().getWidth(), unit.getAttackRange(), gs, ru);
            if (move!=null && gs.isUnitActionAllowed(unit, move)) return move;
            return null;
        }        
    }    
}
