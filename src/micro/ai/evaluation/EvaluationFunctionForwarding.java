/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.evaluation;

import micro.rts.GameState;
//import micro.rts.PhysicalGameState;
//import micro.rts.UnitAction;
//import micro.rts.UnitActionAssignment;
//import micro.rts.units.*;

/**
 *
 * @author santi
 */
public class EvaluationFunctionForwarding extends EvaluationFunction {
    
    EvaluationFunction baseFunction = null;

    public EvaluationFunctionForwarding(EvaluationFunction base) {
        baseFunction = base;
    }
    
    
    public float evaluate(int maxplayer, int minplayer, GameState gs) {
        GameState gs2 = gs.clone();
        gs2.forceExecuteAllActions();
        
        return baseFunction.evaluate(maxplayer,minplayer,gs) + 
               baseFunction.evaluate(maxplayer,minplayer,gs2) * 0.5f;
    }
    
    public float upperBound(GameState gs) {
        return baseFunction.upperBound(gs)*1.5f;
    }
}
