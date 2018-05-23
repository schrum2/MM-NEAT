package examples.StarterNNPacMan;

import pacman.game.Game;

import java.util.Map;

import static pacman.game.Constants.MOVE;

/**
 * Created by piers on 18/10/16.
 */
public abstract class LocEvalPacMan extends NeuralPacMan{

    public LocEvalPacMan(NeuralNet net) {
        super(net);
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        int pacmanCurrentNodeIndex = game.getPacmanCurrentNodeIndex();
        MOVE bestMove = MOVE.NEUTRAL;
        double bestScore = -Double.MAX_VALUE;
        for(Map.Entry<MOVE, Integer> entry : game.getCurrentMaze().graph[pacmanCurrentNodeIndex].neighbourhood.entrySet()){
            double score = evalLocation(game, entry.getValue());
            if(score > bestScore){
                bestScore = score;
                bestMove = entry.getKey();
            }
        }
        return bestMove;
    }

    public abstract double evalLocation(Game game, int index);
}
