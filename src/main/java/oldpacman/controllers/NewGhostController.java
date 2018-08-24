package oldpacman.controllers;

import java.util.EnumMap;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import oldpacman.game.Constants;
import oldpacman.game.Game;
import oldpacman.game.Constants.GHOST;
import oldpacman.game.Constants.MOVE;


/**
 * TODO
 *
 * @author Jacob
 */
public abstract class NewGhostController extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>> {
	
	public static final int END_GAME_CODE = 27;

	/**
	 * Finds the action to do based off of the game and the game's various
	 * current factors, such as the number of ghosts eaten, pacman's lives,
	 * direction, and location
	 * 
	 * Used by oldpacman
	 * 
	 * @param gs
	 *            the gameFacade
	 * @param timeDue
	 * @return the action to take or a number indicating the end of the game
	 */
	public int getAction(final Game gs, long timeDue, GHOST ghost) {
		return getAction(new GameFacade(gs), timeDue, ghost);
	}

	/**
	 * Finds the action to do based off of the game and the game's various
	 * current factors, such as the number of ghosts eaten, pacman's lives,
	 * direction, and location
	 * 
	 * @param gs
	 *            the gameFacade
	 * @param timeDue
	 * @return the action to take or a number indicating the end of the game
	 */
	public abstract int getAction(GameFacade gs, long timeDue, GHOST ghost);
	
	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(oldpacman.game.Constants.GHOST.class);
		//for each enumerated ghost
		for(GHOST g : oldpacman.game.Constants.GHOST.values()) {
			try {
				//get that ghosts action
				int move = getAction(game, timeDue, g);
				//add it to the EnumMap
				switch (move) {
				case END_GAME_CODE:
					result.put(g, null);
					break;
				case 0:
					result.put(g, MOVE.UP);
					break;
				case 1:
					result.put(g, MOVE.RIGHT);
					break;
				case 2:
					result.put(g, MOVE.DOWN);
					break;
				case 3:
					result.put(g, MOVE.LEFT);
					break;
				default:
					result.put(g, MOVE.NEUTRAL);
					break;
				}
			} catch (Exception e) {
				System.out.println("Move failure");
				e.printStackTrace();
				System.out.println("Resort to previous move");
				return getMove(); // default to last move
			}
		}
		
		return result;
	}
	
	/**
	 * TODO
	 */
	public void reset() {
		super.threadRevive();
	}
}
