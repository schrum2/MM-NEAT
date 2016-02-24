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
public class PillTaskMediator extends UnionInputOutputMediator {

    public PillTaskMediator() {
        this(Parameters.parameters.booleanParameter("absolute"),
                Parameters.parameters.booleanParameter("prox"),
                Parameters.parameters.booleanParameter("diff"),
                Parameters.parameters.booleanParameter("sim"),
                Parameters.parameters.booleanParameter("nearestDir"),
                Parameters.parameters.booleanParameter("nearestDis"),
                Parameters.parameters.booleanParameter("farthestDis"),
                Parameters.parameters.booleanParameter("lairDis"),
                Parameters.parameters.booleanParameter("veryClose"),
                Parameters.parameters.booleanParameter("mazeTime"),
                Parameters.parameters.booleanParameter("staticLookAhead"));
    }

    public PillTaskMediator(boolean absolute, boolean prox, boolean diff, boolean sim, boolean nearestDir, boolean nearestDis, boolean farthestDis, boolean lairDis, boolean veryClose, boolean mazeTime, boolean staticLookAhead) {
        super(mediators(absolute, prox, diff, sim, nearestDir, nearestDis, farthestDis, lairDis, veryClose, mazeTime, staticLookAhead));
    }

    public static ArrayList<BlockLoadedInputOutputMediator> mediators(boolean absolute, boolean prox, boolean diff, boolean sim, boolean nearestDir, boolean nearestDis, boolean farthestDis, boolean lairDis, boolean veryClose, boolean mazeTime, boolean staticLookAhead) {
        ArrayList<BlockLoadedInputOutputMediator> mediators = new ArrayList<BlockLoadedInputOutputMediator>(10);
        mediators.add(new BaseSensors());
        mediators.add(new GhostPresenceSensors(false));
        mediators.add(new PillCountingSensors(true, false));

        boolean powerPillsPresent = !Parameters.parameters.booleanParameter("noPowerPills");

        if (absolute) {
            mediators.add(new AbsolutePositionSensors(3, 3));
        }
        if (prox) {
            mediators.add(new DirectionalProximitySensors(true, false, powerPillsPresent));
        }
        if (diff) {
            mediators.add(new DistanceDifferenceSensors(true, false, powerPillsPresent));
        }
        if (nearestDir) {
            mediators.add(new NearestDirectionSensors(true, false, powerPillsPresent));
        }
        if (nearestDis) {
            mediators.add(new NearestDistanceSensors(true, false, powerPillsPresent));
        }
        if (farthestDis) {
            mediators.add(new FarthestDistanceSensors(true, false, powerPillsPresent));
        }
        if (sim) {
            mediators.add(new ForwardSimulationSensors(true, false, powerPillsPresent));
        }
        if (lairDis) {
            mediators.add(new LairDistanceSensors());
        }
        if (veryClose) {
            mediators.add(new VeryCloseSensors(true, false, powerPillsPresent));
        }
        if (mazeTime) {
            mediators.add(new MazeTimeSensors());
        }
        if (staticLookAhead) {
            mediators.add(new StaticLookAheadSensors(true, false, powerPillsPresent));
        }

        return mediators;
    }
}
