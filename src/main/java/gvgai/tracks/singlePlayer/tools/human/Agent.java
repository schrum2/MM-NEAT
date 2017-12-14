package gvgai.tracks.singlePlayer.tools.human;

import gvgai.core.game.Game;
import gvgai.core.game.StateObservation;
import gvgai.core.player.AbstractPlayer;
import gvgai.ontology.Types;
import gvgai.tools.Direction;
import gvgai.tools.ElapsedCpuTimer;
import gvgai.tools.Utils;

/**
 * Created by diego on 06/02/14.
 */
public class Agent extends AbstractPlayer
{

	// Added by Jacob Schrum, though I'm not clear why this wasn't already here.
	public Agent() {
	}
	
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer)
    {
        Direction move = Utils.processMovementActionKeys(Game.ki.getMask(), Types.DEFAULT_SINGLE_PLAYER_KEYIDX);
        boolean useOn = Utils.processUseKey(Game.ki.getMask(), Types.DEFAULT_SINGLE_PLAYER_KEYIDX);

        //In the keycontroller, move has preference.
        Types.ACTIONS action = Types.ACTIONS.fromVector(move);

        //if(action == Types.ACTIONS.ACTION_NIL && useOn)
        if(useOn) //This allows switching to Use when moving.
            action = Types.ACTIONS.ACTION_USE;


        return action;
    }

    public void result(StateObservation stateObservation, ElapsedCpuTimer elapsedCpuTimer)
    {
        //System.out.println("Thanks for playing! " + stateObservation.isAvatarAlive());
    }
}
