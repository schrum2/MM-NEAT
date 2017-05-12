/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.abstraction.pathfinding;

import micro.rts.GameState;
import micro.rts.ResourceUsage;
import micro.rts.UnitAction;
import micro.rts.units.Unit;

/**
 *
 * @author santi
 */
public abstract class PathFinding {
    public abstract boolean pathExists(Unit start, int targetpos, GameState gs, ResourceUsage ru);
    public abstract boolean pathToPositionInRangeExists(Unit start, int targetpos, int range, GameState gs, ResourceUsage ru);
    public abstract UnitAction findPath(Unit start, int targetpos, GameState gs, ResourceUsage ru);
    public abstract UnitAction findPathToPositionInRange(Unit start, int targetpos, int range, GameState gs, ResourceUsage ru);
    public abstract UnitAction findPathToAdjacentPosition(Unit start, int targetpos, GameState gs, ResourceUsage ru);

    public String toString() {
        return getClass().getSimpleName();
    }
}
