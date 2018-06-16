/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utopia.agentmodel.sensormodel;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import javax.vecmath.Vector3d;

/**
 *
 * @author Jacob Schrum
 */
public class ProjectileSensorModel extends MimicSensorModel {

    public ProjectileSensorModel(int levelTraces, int airTraces, int groundTraces, double[] sliceLimits, int secondsHistory) {
        super(levelTraces, airTraces, groundTraces, sliceLimits, secondsHistory);
    }

    public ProjectileSensorModel() {
        this(12, 6, 4, new double[]{0, PI / 128, PI / 32, PI / 4, PI / 2, PI}, 3);
    }

    @Override
    public double[] getMiscSensors(AgentMemory memory) {
        double[] sensors = new double[getNumMiscSensors()];
        double[] oldSensors = super.getMiscSensors(memory);
        int numOldMisc = super.getNumMiscSensors();
        System.arraycopy(oldSensors, 0, sensors, 0, numOldMisc);

        int numMisc = 0;

        // Incoming Projectile
        boolean incoming = memory.senses.seeIncomingProjectile();
        sensors[numOldMisc + (numMisc++)] = incoming ? 1 : 0;
        if (incoming) {
            IncomingProjectile ip = memory.senses.getLastIncomingProjectile();
            if(ip != null){
                Location agent = memory.info.getLocation();
                Location proj = ip.getLocation();
                Vector3d dir = ip.getDirection();
                Location origin = ip.getOrigin();
                Location futureLocation = Triple.add(Triple.locationToTriple(proj), Triple.vector3dToTriple(dir)).getLocation();
                if(proj != null && agent != null && dir != null && origin != null && futureLocation != null){
                    sensors[numOldMisc + (numMisc++)] = scaleDistance(agent.getDistance(proj));
                    sensors[numOldMisc + (numMisc++)] = scaleDistance(agent.getDistance(futureLocation) - agent.getDistance(proj));
                    sensors[numOldMisc + (numMisc++)] = scaleDistance(agent.getDistance(origin) - agent.getDistance(proj));
                }
            }
        }

        return sensors;
    }

    @Override
    public int getNumMiscSensors() {
        return super.getNumMiscSensors()
                + 4; // Incoming projectile
    }
}
