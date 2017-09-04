package micro.ai;

import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;
import micro.rts.PlayerAction;

/**
 * @author alicequint
 */
public interface HasEvaluationFunction {
	public void setEvaluationFunction(EvaluationFunction a_ef);

	public PlayerAction getAction(int i, GameState gs) throws Exception;
}
