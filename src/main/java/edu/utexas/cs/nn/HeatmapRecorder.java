package edu.utexas.cs.nn;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import edu.utexas.cs.nn.logs.LogEntry;

/**
 *
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public interface HeatmapRecorder extends Heatmap {
    /**
     * Calculate how far the agent has moved in the past t seconds
     * @param info current agent state
     * @param time seconds to measure movement since
     * @return distance in Unreal units or null
     */
    public abstract Double getDistanceMoved(AgentInfo info, double horizon);

    public int recordEvent(LogEntry logEntry);

    public int recordPose(String player, Point point);

    public String export();

    public boolean export(String filename);
}
