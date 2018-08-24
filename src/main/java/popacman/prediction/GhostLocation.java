package popacman.prediction;

import pacman.game.Constants;

/**
 * Created by pwillic on 13/05/2016.
 */
public class GhostLocation {
    private int index;
    private Constants.MOVE lastMoveMade;
    private double probability;
    private double edibleTime;

    public GhostLocation(int index, Constants.MOVE lastMoveMade, double probability, double edibleTime) {
        this.index = index;
        this.lastMoveMade = lastMoveMade;
        this.probability = probability;
        this.edibleTime = edibleTime;
    }

    public double getEdibleProbability() {
    	return edibleTime;
    }
    
    public void setEdibleProbability(double probability) {
    	this.edibleTime = probability;
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
                ", edibleTime=" + edibleTime +
                '}';
    }
}
