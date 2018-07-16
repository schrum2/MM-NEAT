package edu.utexas.cs.nn;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import java.util.SortedMap;

/**
 * A Heatmap stores information about player movements and allows prediction of
 * events given the current player state
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public interface Heatmap {

    /**
     * Get all the paths that originate within spaceHorizon of info.getLocation
     * Get all events that lie within timeHorizon on these paths
     * Tally them by event type
     * Return the tally
     * @param info current agent state
     * @param timeHorizon
     * @param spaceHorizon
     * @return a map of events predicted by their probability within the specified time horizon
     */
    public SortedMap<Double, String> predictEvents(AgentInfo info, double timeHorizon, double spaceHorizon);
}
