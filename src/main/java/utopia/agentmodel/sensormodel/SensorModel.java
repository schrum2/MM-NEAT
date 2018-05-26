package utopia.agentmodel.sensormodel;

import java.io.Serializable;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 12:18:47 PM
 */
public abstract class SensorModel implements Serializable {

    //Should be called before the doLogic cycle to set up the sensors
    public abstract void prepareSensors(AgentBody body);

    public abstract double[] getSensors(AgentMemory memory);

    public abstract int getNumSensors();
}
