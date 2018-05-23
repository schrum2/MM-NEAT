package prediction;

import pacman.game.Constants;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by pwillic on 13/05/2016.
 */ // Stores and keeps track of the possible locations of the ghosts
public class IndividualLocations {

    private List<GhostLocation> ghostLocations = new ArrayList<>();
    private Maze maze;
    private List<GhostLocation> newLocations = new ArrayList<>();

    public IndividualLocations(Maze maze) {
        this.maze = maze;
    }

    public void observe(int index, Constants.MOVE lastMoveMade) {
        ghostLocations.clear();
        GhostLocation location = new GhostLocation(index, lastMoveMade, 1.0d);
        ghostLocations.add(location);
    }

    public void observeNotPresent(int index) {
        ListIterator<GhostLocation> itr = ghostLocations.listIterator();
        double priorProbability = 1.0d;
        while (itr.hasNext()) {
            GhostLocation location = itr.next();
            if (location.getIndex() == index) {
                priorProbability = (1 - location.getProbability());
                itr.remove();
                break;
            }
        }
        for (GhostLocation location : ghostLocations) {
            location.setProbability(location.getProbability() / priorProbability);
        }
    }

    public List<GhostLocation> getGhostLocations() {
        return ghostLocations;
    }

    public void update() {
        ListIterator<GhostLocation> itr = ghostLocations.listIterator();

        while (itr.hasNext()) {
            GhostLocation location = itr.next();
            Node currentNode = maze.graph[location.getIndex()];

            int numberNodes = currentNode.numNeighbouringNodes;
            double probability = location.getProbability() / (numberNodes - 1);
            boolean hasReusedLocation = false;

            Constants.MOVE back = location.getLastMoveMade().opposite();
            for (Constants.MOVE move : Constants.MOVE.values()) {
                if (move.equals(back)) continue;
                if (currentNode.neighbourhood.containsKey(move)) {
                    if (!hasReusedLocation) {
                        location.setIndex(currentNode.neighbourhood.get(move));
                        location.setLastMoveMade(move);
                        location.setProbability(probability);
                        hasReusedLocation = true;
                    } else {
                        newLocations.add(new GhostLocation(currentNode.neighbourhood.get(move), move, probability));
                    }
                }
            }
        }
        ghostLocations.addAll(newLocations);
        newLocations.clear();
    }


    public IndividualLocations copy() {
        IndividualLocations other = new IndividualLocations(maze);
        other.ghostLocations = new ArrayList<>(ghostLocations.size());
        for (GhostLocation location : ghostLocations) {
            other.ghostLocations.add(new GhostLocation(location.getIndex(), location.getLastMoveMade(), location.getProbability()));
        }
        return other;
    }

    @Override
    public String toString() {
        return "IndividualLocations{" +
                "length: " + ghostLocations.size() +
                "ghostLocations=" + ghostLocations +
                '}';
    }

    public GhostLocation sample() {
        double x = Math.random();
        double sum = 0.0d;
        for (GhostLocation location : ghostLocations) {
            sum += location.getProbability();
            if (sum >= x) return location;
        }
        return null;
    }

    public double getProbability(int index) {
        for (GhostLocation location : ghostLocations) {
            if (location.getIndex() == index) return location.getProbability();
        }
        return 0.0d;
    }

}
