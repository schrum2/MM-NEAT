package prediction;

import pacman.game.internal.Maze;
import prediction.fast.IndividualLocationsFast;

import java.util.EnumMap;
import java.util.List;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by pwillic on 12/05/2016.
 */
public class GhostPredictions {

    private EnumMap<GHOST, IndividualLocationsFast> locations = new EnumMap<>(GHOST.class);
    private Maze maze;

    public GhostPredictions(Maze maze) {
        this.maze = maze;
        for (GHOST ghost : GHOST.values()) {
            locations.put(ghost, new IndividualLocationsFast(maze));
        }
    }

    public void observe(GHOST ghost, int index, MOVE lastMoveMade) {
        locations.get(ghost).observe(index, lastMoveMade);
    }

    public void observeNotPresent(GHOST ghost, int index) {
        locations.get(ghost).observeNotPresent(index);
    }

    public void update() {
        for (GHOST loc : locations.keySet()) {
            locations.get(loc).update();
        }
    }

    public double calculate(int index) {
        double sum = 0.0d;
        for (GHOST ghost : locations.keySet()) {
            sum += locations.get(ghost).getProbability(index);
        }
        return sum;
    }

    @Override
    public String toString() {
        return "GhostPredictions{" +
                "locations=" + locations +
                '}';
    }

    public String getGhostInfo(GHOST ghost) {
        return locations.get(ghost).toString();
    }

    public EnumMap<GHOST, GhostLocation> sampleLocations() {
        EnumMap<GHOST, GhostLocation> sample = new EnumMap<>(GHOST.class);

        sample.put(GHOST.INKY, locations.get(GHOST.INKY).sample());
        sample.put(GHOST.BLINKY, locations.get(GHOST.BLINKY).sample());
        sample.put(GHOST.PINKY, locations.get(GHOST.PINKY).sample());
        sample.put(GHOST.SUE, locations.get(GHOST.SUE).sample());

        return sample;
    }

    public GhostPredictions copy() {
        GhostPredictions other = new GhostPredictions(this.maze);
        other.locations = new EnumMap<>(GHOST.class);
        for (GHOST ghost : locations.keySet()) {
            other.locations.put(ghost, locations.get(ghost).copy());
        }
        return other;
    }

    public List<GhostLocation> getGhostLocations(GHOST ghost) {
        return locations.get(ghost).getGhostLocations();
    }
}


