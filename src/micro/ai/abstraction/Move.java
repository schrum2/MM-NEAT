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
public class Move extends AbstractAction {

    int x,y;
    PathFinding pf;

    
    public Move(Unit u, int a_x, int a_y, PathFinding a_pf) {
        super(u);
        x = a_x;
        y = a_y;
        pf = a_pf;
    }
    
    public boolean completed(GameState gs) {
        if (unit.getX()==x && unit.getY()==y) return true;
        return false;
    }

    public UnitAction execute(GameState gs, ResourceUsage ru) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        UnitAction move = pf.findPath(unit, x+y*pgs.getWidth(), gs, ru);
//        System.out.println("AStarAttak returns: " + move);
        if (move!=null && gs.isUnitActionAllowed(unit, move)) return move;
        return null;
    }
}
