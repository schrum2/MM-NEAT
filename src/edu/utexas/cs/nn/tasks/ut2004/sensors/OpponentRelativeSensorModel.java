package edu.utexas.cs.nn.tasks.ut2004.sensors;

import edu.utexas.cs.nn.tasks.ut2004.sensors.blocks.*;

/**
 *
 * @author Jacob Schrum
 */
public class OpponentRelativeSensorModel extends UT2004BlockLoadedSensorModel {

    public OpponentRelativeSensorModel() {
        blocks.add(new AutoRayTraceSensorBlock());
        blocks.add(new PieSliceOpponentSensorBlock());
        blocks.add(new NearestOpponentDistanceBlock());
        blocks.add(new EnemyBehaviorBlock());
        blocks.add(new SelfAwarenessBlock());
    }

    public UT2004SensorModel copy() {
        return new OpponentRelativeSensorModel();
    }
}
