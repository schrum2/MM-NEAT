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
import micro.rts.units.UnitType;

/**
 *
 * @author santi
 */
public class Build extends AbstractAction  {
    UnitType type;
    int x,y;
    PathFinding pf;
    
    public Build(Unit u, UnitType a_type, int a_x, int a_y, PathFinding a_pf) {
        super(u);
        type = a_type;
        x = a_x;
        y = a_y;
        pf = a_pf;
    }

    public boolean completed(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit u = pgs.getUnitAt(x, y);
        if (u!=null) return true;
        return false;
    }

    public UnitAction execute(GameState gs, ResourceUsage ru) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
//        System.out.println("findPathToAdjacentPosition from Build: (" + x + "," + y + ")");
        UnitAction move = pf.findPathToAdjacentPosition(unit, x+y*pgs.getWidth(), gs, ru);
        if (move!=null) {
            if (gs.isUnitActionAllowed(unit, move)) return move;
            return null;
        }
       
        // build:
        UnitAction ua = null;
        if (x == unit.getX() &&
            y == unit.getY()-1) ua = new UnitAction(UnitAction.TYPE_PRODUCE,UnitAction.DIRECTION_UP,type);
        if (x == unit.getX()+1 &&
            y == unit.getY()) ua = new UnitAction(UnitAction.TYPE_PRODUCE,UnitAction.DIRECTION_RIGHT,type);
        if (x == unit.getX() &&
            y == unit.getY()+1) ua = new UnitAction(UnitAction.TYPE_PRODUCE,UnitAction.DIRECTION_DOWN,type);
        if (x == unit.getX()-1 &&
            y == unit.getY()) ua = new UnitAction(UnitAction.TYPE_PRODUCE,UnitAction.DIRECTION_LEFT,type);
        if (ua!=null && gs.isUnitActionAllowed(unit, ua)) return ua;        
        
//        System.err.println("Build.execute: something weird just happened " + unit + " builds at " + x + "," + y);
        return null;
    } 
}
