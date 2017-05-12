/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.ai.minimax.RTMiniMax;

import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;
//import micro.rts.PlayerAction;
//import micro.rts.PlayerActionGenerator;
//import micro.util.Pair;

/**
 *
 * @author santi
 */
public class RTMiniMaxRandomizedRootNode extends RTMiniMaxNode {

    public int iterations_run = 0;
    float scores[] = null;
    
    public RTMiniMaxRandomizedRootNode(GameState a_gs) {
        super(3, a_gs, -EvaluationFunction.VICTORY, EvaluationFunction.VICTORY);
    }
}
