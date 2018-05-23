package popacman.entrants.pacman.spooky;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.info.GameInfo;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class Derpy extends PacmanController {
    private MOVE myMove = MOVE.NEUTRAL;

    @Override
    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
    	int decision = (int) Math.floor(Math.random() * 4);

    	switch(decision) {
    		case 0:	myMove = MOVE.DOWN;
    				break;
    		case 1: myMove = MOVE.LEFT;
    				break;
    		case 2: myMove = MOVE.RIGHT;
    				break;
    		case 3: myMove = MOVE.UP;
    				break;
    		}

        return myMove;
    }
    
}