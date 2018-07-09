package popacman.prediction.fast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import popacman.prediction.GhostLocation;

/**
 * Created by Piers on 16/05/2016.
 */
public class GhostPredictionsFast {
    private static final int numGhosts = GHOST.values().length;
    // First mazeSize indices are for ghost Ordinal 0 etc ...
    private double[] probabilities;
    private double[] backProbabilities;
    private int[] ghostEdibleTime; // This is a reference to an array that is updated elsewhere
    
    private MOVE[] moves;
    private MOVE[] backMoves;
    private Maze maze;
    private int mazeSize;
    
    //MODIFIED
    private static final double THRESHOLD = Parameters.parameters.doubleParameter("probabilityThreshold");
    
    private EnumMap<GHOST, Boolean> beenSpotted;
    private Random random;

    /**
     * 
     * @param maze
     * @param ghostEdibleTime Array of edible ghost times
     */
    public GhostPredictionsFast(Maze maze, int[] ghostEdibleTime) {
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
        
        //OUR CODE
        assert ghostEdibleTime != null;
        this.ghostEdibleTime = ghostEdibleTime;

    }

    public void preallocate() {
        // Always one index at the end that shouldn't be used
        double probability = 1 / ((probabilities.length * 1.0d) / GHOST.values().length);
        Arrays.fill(probabilities, probability);
        Arrays.fill(moves, MOVE.NEUTRAL);
    }

    /**
     * When we observe a ghost, We keep track of which ghost it was (not directly but encoded),
     * where we saw it, and what its last move was.
     * have it decay at a given rate, and give a cap at what we accept to be likely 
     * @param ghost
     * @param index
     * @param lastMoveMade
     */
    public void observe(GHOST ghost, int index, MOVE lastMoveMade, GameFacade game) {
        int startIndex = (ghost.ordinal() * mazeSize);
        int arrayIndex = startIndex + index;
        //fill this ghosts portion of the recorded probabilities with zeros
        Arrays.fill(probabilities, startIndex, startIndex + mazeSize, 0);
        //fill this ghosts portion of the recorded Move probabilities with zeros
        Arrays.fill(moves, startIndex, startIndex + mazeSize, null);
        //record that we saw it here with 100% probability
        probabilities[arrayIndex] = 1.0d;
        beenSpotted.put(ghost, true);
        moves[arrayIndex] = lastMoveMade;
    }

    public void observeNotPresent(GHOST ghost, int index, GameFacade game) {
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
    
    /**
     * Calculates the likelihood that a ghost is in this location
     * @param index
     * @return
     */
    public final double calculate(int index) {
        if(index >= mazeSize) return 0;
        double sum = 1.0d;
        // Calculate the likelihood of there being no ghosts at all
        for (int ghost = 0; ghost < numGhosts; ghost++) {
        	//get the probability that ghost we are indexing is in this index based on our calculated probabilities
            sum *= (1 - probabilities[(mazeSize * ghost) + index]);
        }
        // Then reverse the probability to work out the chance of a ghost
        return 1 - sum;
    }
    
    /**
     * calculates the probability that the index has an edible ghost
     * @param index
     * @return
     */
    public final double calculateEdible(int index) {
    	if(index >= mazeSize) return 0;
    	double sum = 1.0d;
    	for(int ghost = 0; ghost < numGhosts; ghost++) {
    		if(ghostEdibleTime[ghost] > 0)
    			sum *= (1 - probabilities[(mazeSize * ghost) + index]);
    	}
    	return 1 - sum;
    }
    
    /**
     * We don't need this
     * @return
     */
    public EnumMap<GHOST, GhostLocation> sampleLocations() {
        EnumMap<GHOST, GhostLocation> results = new EnumMap<GHOST, GhostLocation>(GHOST.class);

        for (int ghost = 0; ghost < numGhosts; ghost++) {
            double x = Math.random();
            double sum = 0.0d;
            for (int i = (mazeSize * ghost); i < (mazeSize * (ghost + 1)); i++) {
                sum += probabilities[i];
                if (sum >= x) {
                    if (!moves[i].equals(MOVE.NEUTRAL)) {
                        results.put(GHOST.values()[ghost], new GhostLocation(i % mazeSize, moves[i], probabilities[i], 0));
                    } else {
                        MOVE[] possibleMoves = maze.graph[i % mazeSize].neighbourhood.keySet().toArray(new MOVE[0]);
                        results.put(
                                GHOST.values()[ghost],
                                new GhostLocation(
                                        i % mazeSize,
                                        possibleMoves[random.nextInt(possibleMoves.length)].opposite(),
                                        probabilities[i], 0
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
        GhostPredictionsFast other = new GhostPredictionsFast(this.maze, this.ghostEdibleTime);
        System.arraycopy(this.probabilities, 0, other.probabilities, 0, probabilities.length);
        System.arraycopy(this.backProbabilities, 0, other.backProbabilities, 0, backProbabilities.length);
        System.arraycopy(this.moves, 0, other.moves, 0, moves.length);
        System.arraycopy(this.backMoves, 0, other.backMoves, 0, backMoves.length);
        return other;
    }
     
    /**
     * Takes a ghost, and returns an array list of ghost locations representing that ghost. 
     * Each location represents an index on the map.
     * @param ghost
     * @return
     */
    public List<GhostLocation> getGhostLocations(GHOST ghost) {
        ArrayList<GhostLocation> locations = new ArrayList<>();
        
        //for this ghosts chuck of the recorded probabilites
        for (int i = ghost.ordinal() * mazeSize; i < (ghost.ordinal() + 1) * mazeSize; i++) {
            //if there is more than a zero percent chance the ghost is there
        	if (probabilities[i] > 0) {
        		//add a ghost location with all of the recorded information about that maze index to what we return
        		locations.add(new GhostLocation(i % mazeSize, moves[i], probabilities[i], ghostEdibleTime[i / mazeSize]));
          	}
        }
        return locations;
    }

    /**
     * returns an array list of all ghost locations, irrespective of which ghost is which.
     * @return
     */
    public List<GhostLocation> getGhostLocations() {
        ArrayList<GhostLocation> locations = new ArrayList<>();
        //for every recorded probability
        for (int i = 0; i < probabilities.length; i++) {
            //if that probability is greater than 0%
        	if (probabilities[i] > 0) {
        		//add that to what we are returning
            	locations.add(new GhostLocation(i % mazeSize, moves[i], probabilities[i], ghostEdibleTime[i / mazeSize]));
        	}
        	
        }
        return locations;
    }

    /**
     * To String method
     * @param ghost
     * @return
     */
    public String getGhostInfo(GHOST ghost) {
        List<GhostLocation> ghostLocations = getGhostLocations(ghost);
        return "IndividualLocations{" +
                "length: " + ghostLocations.size() +
                "ghostLocations=" + ghostLocations +
                '}';
    }
}