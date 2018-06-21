package popacman.prediction;

import pacman.game.Constants;

/**
 * Created by pwillic on 13/05/2016.
 */
public class GhostLocation {
    private int index;
    private Constants.MOVE lastMoveMade;
    private double probability;
    private double edibleProbability;

    public GhostLocation(int index, Constants.MOVE lastMoveMade, double probability, double edibleProbability) {
        this.index = index;
        this.lastMoveMade = lastMoveMade;
        this.probability = probability;
        this.edibleProbability = edibleProbability;
    }

    public double getEdibleProbability() {
    	return edibleProbability;
    }
    
    public void setEdibleProbability(double probability) {
    	this.edibleProbability = probability;
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Constants.MOVE getLastMoveMade() {
        return lastMoveMade;
    }

    public void setLastMoveMade(Constants.MOVE lastMoveMade) {
        this.lastMoveMade = lastMoveMade;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "GhostLocation{" +
                "index=" + index +
                ", lastMoveMade=" + lastMoveMade +
                ", probability=" + probability +
                ", edibleProbability=" + edibleProbability +
                '}';
    }
}
