package pacman.controllers;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 *
 * @author Jacob Schrum
 */
public abstract class NewPacManController extends Controller<MOVE> {

    //public NewPacManController() {System.out.println("NewPacManController created");}
    // Just some unique value
    public static final int END_GAME_CODE = 27;

    public abstract int getAction(Game game, long timeDue);

    @Override
    public MOVE getMove(Game game, long timeDue) {
//        assert this.lastMove() == GameFacade.moveToIndex(game.getPacmanLastMoveMade()) : "Int Last move is not consistent:" + this.lastMove() + ":" + game.getPacmanLastMoveMade();
//        assert this.lastMove == null || this.lastMove == game.getPacmanLastMoveMade() : "MOVE Last move is not consistent:" + lastMove + ":" + game.getPacmanLastMoveMade();
        try {
            int move = getAction(game, timeDue);
            switch (move) {
                case END_GAME_CODE:
                    return null;
                case 0:
                    return MOVE.UP;
                case 1:
                    return MOVE.RIGHT;
                case 2:
                    return MOVE.DOWN;
                case 3:
                    return MOVE.LEFT;
                default:
                    return MOVE.NEUTRAL;
            }
        } catch (Exception e) {
            System.out.println("Move failure");
            e.printStackTrace();
            System.out.println("Resort to previous move");
            return getMove(); // default to last move
        }
    }

    public void reset() {
        threadRevive();
    }
    // Stop using this since the built in one in Game is not as bad as suspected
    // Though I may find otherwise if I stop using eliminateImpossibleDirections
//    public int lastMove(){
//        MOVE move = this.getMove();
//        return move == null ? 3 : GameFacade.moveToIndex(move);
//    }

    public abstract void logEvaluationDetails();
}
