package examples.demo;

import pacman.Executor;
import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

import java.util.EnumMap;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by piers on 04/10/16.
 */
public class DemoPacMan extends PacmanController {
    public static void main(String[] args) {
        Executor co = new Executor.Builder()
                .setPacmanPO(false)
                .setGhostPO(false)
                .setGhostsMessage(false)
                .setTickLimit(4000)
                .setGraphicsDaemon(true)
                .build();
        Executor po = new Executor.Builder()
                .setTickLimit(4000)
                .setGraphicsDaemon(true)
                .build();


        co.runGame(new DemoPacMan(), new POCommGhosts(50),  40);
        po.runGame(new DemoPacMan(), new POCommGhosts(50),  40);
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        Game coGame;
        if (game.isGamePo()) {
            GameInfo info = game.getPopulatedGameInfo();
            info.fixGhosts((ghost) -> new Ghost(
                    ghost,
                    game.getCurrentMaze().lairNodeIndex,
                    -1,
                    -1,
                    MOVE.NEUTRAL
            ));
            coGame = game.getGameFromInfo(info);

        } else {
            coGame = game.copy();
        }

        // Make some ghosts
        MASController ghosts = new POCommGhosts(50);
        // Ask what they would do
        EnumMap<GHOST, MOVE> ghostMoves = ghosts.getMove(coGame.copy(), 40);

        // Get the best one step lookahead move
        MOVE bestMove = null;
        int bestScore = -Integer.MAX_VALUE;
        for (MOVE move : MOVE.values()) {
            Game forwardCopy = coGame.copy();
            forwardCopy.advanceGame(move, ghostMoves);
            int score = forwardCopy.getScore();
            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }

        System.out.println("Best MOVE: " + bestMove + " With Score: " + bestScore);
        return bestMove;
    }


}
