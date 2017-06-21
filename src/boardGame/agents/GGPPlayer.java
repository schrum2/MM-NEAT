package boardGame.agents;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import boardGame.BoardGameState;

public class GGPPlayer<T extends BoardGameState> extends StateMachineGamer implements BoardGamePlayer<T>{
	
	/**
     * Defines which state machine this gamer will use;
     * From GGP
     * 
     * @return
     */
	@Override
	public StateMachine getInitialStateMachine() {
		// All example Players used this specific return
		return new CachedStateMachine(new ProverStateMachine());
	}

    /**
     * Defines the metagaming action taken by a player during the START_CLOCK;
     * From GGP
     * 
     * @param timeout time in milliseconds since the era when this function must return
     * @throws TransitionDefinitionException
     * @throws MoveDefinitionException
     * @throws GoalDefinitionException
     */
	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// All example Players did not do any Metagaming
	}

    /**
     * Defines the algorithm that the player uses to select their move;
     * From GGP
     * 
     * @param timeout time in milliseconds since the era when this function must return
     * @return Move - the move selected by the player
     * @throws TransitionDefinitionException
     * @throws MoveDefinitionException
     * @throws GoalDefinitionException
     */
	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		// TODO: This code was copied from the GGP RandomGamer class; replace with our desired code
	    
		long start = System.currentTimeMillis();
		
		// This is apparently the equivalent of possibleBoardGameStates
        List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
        Move selection = (moves.get(ThreadLocalRandom.current().nextInt(moves.size())));

        long stop = System.currentTimeMillis();

        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        return selection;
	}

    /**
     * Defines any actions that the player takes upon the game cleanly ending.
     */
	@Override
	public void stateMachineStop() {
		// All example Players did not take any special actions upon a game ending normally
	}

    /**
     * Defines any actions that the player takes upon the game abruptly ending;
     * From GGP
     */
	@Override
	public void stateMachineAbort() {
		// All example Players did nothing upon a game abruptly ending
	}
	
	/**
	 * From GGP
	 */
	@Override
	public void preview(Game g, long timeout) throws GamePreviewException {
		// All example Players did not preview the game
	}
	
	/**
	 * Returns the name of the Player;
	 * From GGP
	 */
	@Override
	public String getName() {
		return "SU GGP Player";
	}

	/**
	 * From BoardGamePlayer
	 */
	@Override
	public T takeAction(T current) {
		// TODO What do we need here?
		return null;
	}

}
