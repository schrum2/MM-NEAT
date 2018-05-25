/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utopia.agentmodel.sensormodel;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 *
 * @author Jacob Schrum
 */
public class OrientationSensorModel extends ProjectileSensorModel {

    public OrientationSensorModel(int levelTraces, int airTraces, int groundTraces, double[] sliceLimits, int secondsHistory) {
        super(levelTraces, airTraces, groundTraces, sliceLimits, secondsHistory);
    }

    public OrientationSensorModel() {
        this(12, 6, 4, new double[]{0, PI / 128, PI / 32, PI / 4, PI / 2, PI}, 3);
    }

    @Override
    public double[] getMiscSensors(AgentMemory memory) {
        double[] sensors = new double[getNumMiscSensors()];
        double[] oldSensors = super.getMiscSensors(memory);
        int numOldMisc = super.getNumMiscSensors();
        System.arraycopy(oldSensors, 0, sensors, 0, numOldMisc);

        int numMisc = 0;

        Player p = memory.getCombatTarget();
        if (p != null && p.getLocation() != null) {
            sensors[numOldMisc + (numMisc++)] = memory.angleBetweenBotRotationAndVectorToLocation(p.getLocation()) / Math.PI;
            sensors[numOldMisc + (numMisc++)] = memory.info.isFacing(p.getLocation(),AgentMemory.FACING_ANGLE_DEGREES_THRESHOLD) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = AgentMemory.angleBetweenBotRotationAndVectorToLocation(p.getLocation(), p.getRotation(), memory.getAgentLocation()) / Math.PI;
            sensors[numOldMisc + (numMisc++)] = AgentMemory.sourceIsFacingLocation(p.getLocation(), p.getRotation(), memory.getAgentLocation()) ? 1 : 0;
        }

        return sensors;
    }

    @Override
    public int getNumMiscSensors() {
        return super.getNumMiscSensors()
                + 2  // Enemy orientation
                + 2; // Bot orientation
    }
}
