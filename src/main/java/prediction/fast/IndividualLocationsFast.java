package prediction.fast;

import pacman.game.Constants;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import prediction.GhostLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pwillic on 13/05/2016.
 */
public class IndividualLocationsFast {

    private Maze maze;

    private double[] probabilities;
    private double[] backProbabilities;

    private int[] moves;
    private int[] backMoves;

    public IndividualLocationsFast(Maze maze) {
        this.maze = maze;
        probabilities = new double[maze.graph.length];
        backProbabilities = new double[maze.graph.length];
        moves = new int[maze.graph.length];
        backMoves = new int[maze.graph.length];
    }

    public void observe(int index, Constants.MOVE lastMoveMade) {
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = 0;
            moves[i] = -1;
        }
        probabilities[index] = 1.0d;
        moves[index] = lastMoveMade.ordinal();
    }

    public void observeNotPresent(int index) {
        double probabilityAdjustment = (1 - probabilities[index]);
        probabilities[index] = 0;
        moves[index] = -1;

        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= probabilityAdjustment;
        }
    }

    public void update() {
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > 0) {
                Node currentNode = maze.graph[i];
                int numberNodes = currentNode.numNeighbouringNodes;
                double probability = probabilities[i] / (numberNodes - 1);
                Constants.MOVE back = Constants.MOVE.values()[moves[i]].opposite();
                for (Constants.MOVE move : Constants.MOVE.values()) {
                    if (move == back) continue;
                    if (currentNode.neighbourhood.containsKey(move)) {
                        int index = currentNode.neighbourhood.get(move);
                        // If we haven't already written to there or what we wrote was less probable
                        if (backProbabilities[index] <= probabilities[i]) {
                            backProbabilities[index] = probability;
                            backMoves[index] = move.ordinal();
                        }
                    }
                }
            }
        }

        System.arraycopy(backProbabilities, 0, probabilities, 0, probabilities.length);
        Arrays.fill(backProbabilities, 0.0d);

        System.arraycopy(backMoves, 0, moves, 0, moves.length);
        Arrays.fill(backMoves, -1);

    }

    public IndividualLocationsFast copy() {
        IndividualLocationsFast other = new IndividualLocationsFast(maze);
        System.arraycopy(this.probabilities, 0, other.probabilities, 0, probabilities.length);
        System.arraycopy(this.backProbabilities, 0, other.backProbabilities, 0, backProbabilities.length);
        System.arraycopy(this.moves, 0, other.moves, 0, moves.length);
        System.arraycopy(this.backMoves, 0, other.backMoves, 0, backMoves.length);
        return other;
    }

    @Override
    public String toString() {
        List<GhostLocation> ghostLocations = getGhostLocations();
        return "IndividualLocations{" +
                "length: " + ghostLocations.size() +
                "ghostLocations=" + ghostLocations +
                '}';
    }

    public GhostLocation sample() {
        double x = Math.random();
        double sum = 0.0d;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum >= x) return new GhostLocation(i, Constants.MOVE.values()[moves[i]], probabilities[i]);
        }
        return null;
    }

    public double getProbability(int index) {
        return probabilities[index];
    }

    public List<GhostLocation> getGhostLocations() {
        ArrayList<GhostLocation> locations = new ArrayList<>();
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > 0) {
                locations.add(new GhostLocation(i, Constants.MOVE.values()[moves[i]], probabilities[i]));
            }
        }
        return locations;
    }


}
