/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.mcts.naivemcts;

import java.util.List;
import micro.rts.UnitAction;
import micro.rts.units.Unit;

/**
 *
 * @author santi
 */
public class UnitActionTableEntry {
    public Unit u;
    public int nactions = 0;
    public List<UnitAction> actions = null;
    public double[] accum_evaluation = null;
    public int[] visit_count = null;
}
