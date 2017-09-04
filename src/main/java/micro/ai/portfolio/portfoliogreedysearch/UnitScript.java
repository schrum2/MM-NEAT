/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.portfolio.portfoliogreedysearch;

import micro.rts.GameState;
import micro.rts.UnitAction;
import micro.rts.units.Unit;

/**
 *
 * @author santi
 */
public abstract class UnitScript {
    public abstract UnitAction getAction(Unit u, GameState gs);
    public abstract UnitScript instantiate(Unit u, GameState gs);
}
