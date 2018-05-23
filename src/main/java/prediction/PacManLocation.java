package prediction;

import pacman.game.Constants;
import pacman.game.internal.Maze;

/**
 * Created by Piers on 27/06/2016.
 */
public class PacManLocation {
    private int index;
    private Constants.MOVE lastMoveMade;
    private Maze maze;

    public PacManLocation(int index, Constants.MOVE lastMoveMade, Maze maze) {
        this.index = index;
        this.lastMoveMade = lastMoveMade;
        this.maze = maze;
    }

    // Returns all possible moves except the opposite of the last move made.
    public Constants.MOVE[] possibleMoves() {
        return maze.graph[index].allPossibleMoves.get(lastMoveMade);
    }

    public Constants.MOVE[] allPossibleMovesIncludingBackwards() {
        return maze.graph[index].neighbourhood.keySet().toArray(new Constants.MOVE[maze.graph[index].neighbourhood.keySet().size()]);
    }

    public void update(Constants.MOVE move) {
        if(isPossible(move)) {
            index = maze.graph[index].neighbourhood.get(move);
            lastMoveMade = move;
        }
    }

    public boolean isPossible(Constants.MOVE move) {
        return maze.graph[index].neighbourhood.containsKey(move);
    }

    public PacManLocation copy() {
        return new PacManLocation(this.index, this.lastMoveMade, this.maze);
    }

    public int getIndex() {
        return index;
    }

    public Constants.MOVE getLastMoveMade() {
        return lastMoveMade;
    }

    public Maze getMaze() {
        return maze;
    }
}
