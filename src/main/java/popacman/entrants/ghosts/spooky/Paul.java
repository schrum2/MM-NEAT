package popacman.entrants.ghosts.spooky;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * Created by Piers on 11/11/2015.
 */
public class Paul extends IndividualGhostController {
	private MOVE myMove = MOVE.NEUTRAL;
	
    public Paul() {
        super(Constants.GHOST.PINKY);
    }

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
