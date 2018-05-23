package prediction.fast;

import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import prediction.GhostLocation;

import java.util.*;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by Piers on 16/05/2016.
 */
public class GhostPredictionsFast {
    private static final int numGhosts = GHOST.values().length;
    // First mazeSize indices are for ghost Ordinal 0 etc ...
    private double[] probabilities;
    private double[] backProbabilities;
    private MOVE[] moves;
    private MOVE[] backMoves;
    private Maze maze;
    private int mazeSize;
    private static final double THRESHOLD = 1 / 256.0d;
    private EnumMap<GHOST, Boolean> beenSpotted;
    private Random random;

    public GhostPredictionsFast(Maze maze) {
        this.maze = maze;
        // Cut out the end node - it always has no neighbours
        mazeSize = maze.graph.length - 1;
        probabilities = new double[mazeSize * numGhosts];
        backProbabilities = new double[mazeSize * numGhosts];
        this.beenSpotted = new EnumMap<>(GHOST.class);
        for (GHOST ghost : GHOST.values()) {
            beenSpotted.put(ghost, false);
        }
        moves = new MOVE[mazeSize * numGhosts];
        backMoves = new MOVE[mazeSize * numGhosts];
        random = new Random();
    }

    public void preallocate() {
        // Always one index at the end that shouldn't be used
        double probability = 1 / ((probabilities.length * 1.0d) / GHOST.values().length);
        Arrays.fill(probabilities, probability);
        Arrays.fill(moves, MOVE.NEUTRAL);
    }

    public void observe(GHOST ghost, int index, MOVE lastMoveMade) {
        int startIndex = (ghost.ordinal() * mazeSize);
        int arrayIndex = startIndex + index;
        Arrays.fill(probabilities, startIndex, startIndex + mazeSize, 0);
        Arrays.fill(moves, startIndex, startIndex + mazeSize, null);
        probabilities[arrayIndex] = 1.0d;
        beenSpotted.put(ghost, true);
        moves[arrayIndex] = lastMoveMade;
    }

    public void observeNotPresent(GHOST ghost, int index) {
        int startIndex = (ghost.ordinal() * mazeSize);
        int arrayIndex = startIndex + index;
        double probabilityAdjustment = (1 - probabilities[arrayIndex]);
        probabilities[arrayIndex] = 0;
        moves[arrayIndex] = null;
        for (int i = startIndex; i < startIndex + mazeSize; i++) {
            probabilities[i] /= probabilityAdjustment;
        }
    }

    public void update() {
        for (int ghost = 0; ghost < numGhosts; ghost++) {
            if (!beenSpotted.get(GHOST.values()[ghost])) {
                continue;
            }
            for (int i = (mazeSize * ghost); i < (mazeSize * (ghost + 1)); i++) {
                if (probabilities[i] > THRESHOLD) {
                    Node currentNode = maze.graph[i % mazeSize];
                    int numberNodes = currentNode.numNeighbouringNodes;
                    double probability = probabilities[i] / (numberNodes - 1);
//                    System.out.println(probability + " n: " + numberNodes + " orig: " + probabilities[i]);
                    MOVE back = moves[i].opposite();
                    for (MOVE move : MOVE.values()) {
                        if (move == back) {
                            continue;
                        }
                        if (currentNode.neighbourhood.containsKey(move)) {
                            int index = currentNode.neighbourhood.get(move);
                            // If we haven't already written to there or what we wrote was less probable
                            if (backProbabilities[(mazeSize * ghost) + index] <= probabilities[(mazeSize * ghost) + index]) {
                                backProbabilities[(mazeSize * ghost) + index] = probability;
                                backMoves[(mazeSize * ghost) + index] = move;
                            }
                        }
                    }
                }
            }
        }

        System.arraycopy(backProbabilities, 0, probabilities, 0, probabilities.length);
        Arrays.fill(backProbabilities, 0.0d);

        System.arraycopy(backMoves, 0, moves, 0, moves.length);
        Arrays.fill(backMoves, null);
    }

    public final double calculate(int index) {
        if(index >= mazeSize) return 0;
        double sum = 1.0d;
        // Calculate the likelihood of there being no ghosts at all
        for (int ghost = 0; ghost < numGhosts; ghost++) {
            sum *= (1 - probabilities[(mazeSize * ghost) + index]);
        }
        // Then reverse the probability to work out the chance of a ghost
        return 1 - sum;
    }

    public EnumMap<GHOST, GhostLocation> sampleLocations() {
        EnumMap<GHOST, GhostLocation> results = new EnumMap<GHOST, GhostLocation>(GHOST.class);

        for (int ghost = 0; ghost < numGhosts; ghost++) {
            double x = Math.random();
            double sum = 0.0d;
            for (int i = (mazeSize * ghost); i < (mazeSize * (ghost + 1)); i++) {
                sum += probabilities[i];
                if (sum >= x) {
                    if (!moves[i].equals(MOVE.NEUTRAL)) {
                        results.put(GHOST.values()[ghost], new GhostLocation(i % mazeSize, moves[i], probabilities[i]));
                    } else {
                        MOVE[] possibleMoves = maze.graph[i % mazeSize].neighbourhood.keySet().toArray(new MOVE[0]);
                        results.put(
                                GHOST.values()[ghost],
                                new GhostLocation(
                                        i % mazeSize,
                                        possibleMoves[random.nextInt(possibleMoves.length)].opposite(),
                                        probabilities[i]
                                )
                        );
                    }
                    break;
                }
            }
        }
        return results;
    }

    public GhostPredictionsFast copy() {
        GhostPredictionsFast other = new GhostPredictionsFast(this.maze);
        System.arraycopy(this.probabilities, 0, other.probabilities, 0, probabilities.length);
        System.arraycopy(this.backProbabilities, 0, other.backProbabilities, 0, backProbabilities.length);
        System.arraycopy(this.moves, 0, other.moves, 0, moves.length);
        System.arraycopy(this.backMoves, 0, other.backMoves, 0, backMoves.length);
        return other;
    }

    public List<GhostLocation> getGhostLocations(GHOST ghost) {
        ArrayList<GhostLocation> locations = new ArrayList<>();
        for (int i = ghost.ordinal() * mazeSize; i < (ghost.ordinal() + 1) * mazeSize; i++) {
            if (probabilities[i] > 0) {
                locations.add(new GhostLocation(i % mazeSize, moves[i], probabilities[i]));
            }
        }
        return locations;
    }

    public List<GhostLocation> getGhostLocations() {
        ArrayList<GhostLocation> locations = new ArrayList<>();
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > 0) {
                locations.add(new GhostLocation(i % mazeSize, moves[i], probabilities[i]));
            }
        }
        return locations;
    }

    public String getGhostInfo(GHOST ghost) {
        List<GhostLocation> ghostLocations = getGhostLocations(ghost);
        return "IndividualLocations{" +
                "length: " + ghostLocations.size() +
                "ghostLocations=" + ghostLocations +
                '}';
    }
}