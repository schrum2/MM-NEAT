package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.UnionInputOutputMediator;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class FullTaskMediator extends UnionInputOutputMediator {

    public FullTaskMediator() {
        super(Arrays.asList(new BlockLoadedInputOutputMediator[]{
                    new GhostTaskMediator(),
                    new PillTaskMediator()}));
    }
//    public FullTaskMediator() {
//        this(Parameters.parameters.booleanParameter("absolute"),
//                Parameters.parameters.booleanParameter("prox"),
//                Parameters.parameters.booleanParameter("diff"),
//                Parameters.parameters.booleanParameter("sim"),
//                Parameters.parameters.booleanParameter("cluster"),
//                Parameters.parameters.booleanParameter("nearestDir"),
//                Parameters.parameters.booleanParameter("nearestDis"),
//                Parameters.parameters.booleanParameter("farthestDis"),
//                Parameters.parameters.booleanParameter("specific"),
//                Parameters.parameters.booleanParameter("specialPowerPill"),
//                Parameters.parameters.booleanParameter("ghostTimes"),
//                Parameters.parameters.booleanParameter("lairDis"),
//                Parameters.parameters.booleanParameter("veryClose"),
//                Parameters.parameters.booleanParameter("mazeTime"),
//                Parameters.parameters.booleanParameter("incoming"));
//    }
//
//    public FullTaskMediator(boolean absolute, boolean prox, boolean diff, boolean sim, boolean cluster, boolean nearestDir, boolean nearestDis, boolean farthestDis, boolean specific, boolean specialPowerPill, boolean ghostTimes, boolean lairDis, boolean veryClose, boolean mazeTime, boolean incoming) {
//        super(Arrays.asList(new BlockLoadedInputOutputMediator[]{
//                    new GhostTaskMediator(true, absolute, prox, diff, sim, cluster, nearestDir, nearestDis, farthestDis, specific, specialPowerPill, ghostTimes, lairDis, veryClose, mazeTime, incoming),
//                    new PillTaskMediator(absolute, prox, diff, sim, nearestDir, nearestDis, farthestDis, lairDis, veryClose, mazeTime)}));
//    }
}
