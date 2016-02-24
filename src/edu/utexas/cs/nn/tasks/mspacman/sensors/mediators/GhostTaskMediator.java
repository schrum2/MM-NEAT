package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.UnionInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components.*;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class GhostTaskMediator extends UnionInputOutputMediator {

    public GhostTaskMediator() {
        this(true);
    }

    public GhostTaskMediator(boolean senseEdibleGhosts) {
        this(senseEdibleGhosts,
                Parameters.parameters.booleanParameter("absolute"),
                Parameters.parameters.booleanParameter("prox"),
                Parameters.parameters.booleanParameter("diff"),
                Parameters.parameters.booleanParameter("sim"),
                Parameters.parameters.booleanParameter("cluster"),
                Parameters.parameters.booleanParameter("nearestDir"),
                Parameters.parameters.booleanParameter("nearestDis"),
                Parameters.parameters.booleanParameter("farthestDis"),
                Parameters.parameters.booleanParameter("specific"),
                Parameters.parameters.booleanParameter("specialPowerPill"),
                Parameters.parameters.booleanParameter("ghostTimes"),
                Parameters.parameters.booleanParameter("lairDis"),
                Parameters.parameters.booleanParameter("veryClose"),
                Parameters.parameters.booleanParameter("mazeTime"),
                Parameters.parameters.booleanParameter("incoming"),
                Parameters.parameters.booleanParameter("staticLookAhead"));
    }

    public GhostTaskMediator(boolean senseEdibleGhosts, boolean absolute, boolean prox, boolean diff, boolean sim, boolean cluster, boolean nearestDir, boolean nearestDis, boolean farthestDis, boolean specific, boolean specialPowerPill, boolean ghostTimes, boolean lairDis, boolean veryClose, boolean mazeTime, boolean incoming, boolean staticLookAhead) {
        super(mediators(senseEdibleGhosts, absolute, prox, diff, sim, cluster, nearestDir, nearestDis, farthestDis, specific, specialPowerPill, ghostTimes, lairDis, veryClose, mazeTime, incoming, staticLookAhead));
    }

    public static ArrayList<BlockLoadedInputOutputMediator> mediators(boolean senseEdibleGhosts, boolean absolute, boolean prox, boolean diff, boolean sim, boolean cluster, boolean nearestDir, boolean nearestDis, boolean farthestDis, boolean specific, boolean specialPowerPill, boolean ghostTimes, boolean lairDis, boolean veryClose, boolean mazeTime, boolean incoming, boolean staticLookAhead) {
        ArrayList<BlockLoadedInputOutputMediator> mediators = new ArrayList<BlockLoadedInputOutputMediator>(12);
        mediators.add(new BaseSensors());
        mediators.add(new GhostPresenceSensors(true));
        mediators.add(new PillCountingSensors(false, true));

        if (absolute) {
            mediators.add(new AbsolutePositionSensors(3, 3));
        }
        if (specific) {
            mediators.add(new SpecificGhostSensors());
        }
        if (prox) {
            mediators.add(new DirectionalProximitySensors(false, senseEdibleGhosts, true));
        }
        if (diff) {
            mediators.add(new DistanceDifferenceSensors(false, senseEdibleGhosts, true));
        }
        if (nearestDir) {
            mediators.add(new NearestDirectionSensors(false, senseEdibleGhosts, true));
        }
        if (nearestDis) {
            mediators.add(new NearestDistanceSensors(false, senseEdibleGhosts, true));
        }
        if (farthestDis) {
            mediators.add(new FarthestDistanceSensors(false, senseEdibleGhosts, true));
        }
        if (sim) {
            mediators.add(new ForwardSimulationSensors(false, senseEdibleGhosts, true));
        }
        if (cluster) {
            mediators.add(new GhostClusterSensors(senseEdibleGhosts));
        }
        if (specialPowerPill) {
            mediators.add(new SpecialPowerPillSensors());
        }
        if (ghostTimes) {
            mediators.add(new GhostTimeSensors(senseEdibleGhosts));
        }
        if (lairDis) {
            mediators.add(new LairDistanceSensors());
        }
        if (veryClose) {
            mediators.add(new VeryCloseSensors(false, senseEdibleGhosts, true));
        }
        if (mazeTime) {
            mediators.add(new MazeTimeSensors());
        }

        if (incoming) {
            mediators.add(new IncomingGhostSensors(senseEdibleGhosts));
        }
        if (staticLookAhead) {
            mediators.add(new StaticLookAheadSensors(false, senseEdibleGhosts, true));
        }

        return mediators;
    }
}
