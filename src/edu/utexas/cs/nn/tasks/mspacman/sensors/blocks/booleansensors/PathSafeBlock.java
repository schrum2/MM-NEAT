package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.facades.GhostControllerFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public abstract class PathSafeBlock extends BooleanSensorBlock {

    private final GhostControllerFacade ghostModel;

    public PathSafeBlock(GhostControllerFacade ghostModel) {
        this.ghostModel = ghostModel;
    }

    public String senseLabel() {
        return "Path To " + targetLabel() + " Safe";
    }

    @Override
    public boolean predicate(GameFacade gf, int lastDirection) {
        int target = getTarget(gf, lastDirection);
        if (target == -1) {
            return false;
        } else {
            GameFacade sim = gf.simulateTowardsLocation(target, ghostModel);
            return sim != null;
        }
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            PathSafeBlock other = (PathSafeBlock) o;
            return other.ghostModel == this.ghostModel;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.ghostModel != null ? this.ghostModel.hashCode() : 0);
        hash = 47 * hash + super.hashCode();
        return hash;
    }

    public abstract String targetLabel();

    public abstract int getTarget(GameFacade gf, int lastDirection);
}
