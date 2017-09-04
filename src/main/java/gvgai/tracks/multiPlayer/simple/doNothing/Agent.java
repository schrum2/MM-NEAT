package gvgai.tracks.multiPlayer.simple.doNothing;

import gvgai.core.game.StateObservationMulti;
import gvgai.core.player.AbstractMultiPlayer;
import gvgai.ontology.Types.ACTIONS;
import gvgai.tools.ElapsedCpuTimer;

public class Agent extends AbstractMultiPlayer {

	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 * @param playerID ID if this agent
	 */
	public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID) {
	}

	/**
	 * return ACTION_NIL on every call to simulate doNothing player
	 * @param stateObs Observation of the current state.
	 * @param elapsedTimer Timer when the action returned is due.
	 * @return 	ACTION_NIL all the time
	 */
	@Override
	public ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {
		return ACTIONS.ACTION_NIL;
	}
}


