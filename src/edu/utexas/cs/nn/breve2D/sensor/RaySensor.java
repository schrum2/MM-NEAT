package edu.utexas.cs.nn.breve2D.sensor;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class RaySensor {

    private final Agent agent;
    private final double angle;
    private final double length;

    public RaySensor(Agent a, double angle, double len) {
        this.agent = a;
        this.angle = angle;
        this.length = len;
    }

    /**
     * Returns position in 2D space of end of sensor
     *
     * @return tuple containing location
     */
    public Tuple2D getEndpoint() {
        Tuple2D result = new Tuple2D(agent.getX() + (length * Math.cos(agent.getHeading() + angle)), agent.getY() + (length * Math.sin(agent.getHeading() + angle)));
        //System.out.println("End:" + agent + ", " +  result + "," + length + "," + angle);
        return result;
    }

    /**
     * How far the sensor line is from a given point. This is the shortest
     * distance from the line, which at most points means distance along a line
     * perpendicular to the sensor (not the case at the tip of the sensor
     * though).
     *
     * @param p location of thing to be sensed
     * @return shortest distance to p from sensor
     */
    private double distanceTo(ILocated2D p) {
        Tuple2D end = getEndpoint();
        return CartesianGeometricUtilities.shortestDistanceToLineSegment(p, agent, end);
    }

    /**
     * Whether or not the sensor is intersecting the body of the given agent.
     *
     * @param a agent to sense
     * @return true if sensor intersects agent body, i.e. distance to center of
     * agent a is less than agent radius (assuming agents are circular)
     */
    public boolean sensingAgent(Agent a) {
        return agent.getPosition() != null && a != null && !a.isDead() && distanceTo(a) < Breve2DGame.AGENT_MAGNITUDE;
    }

    /**
     * Whether any of the agents in as are touched by the sensor
     *
     * @param as list of agents to sense
     * @return true if sensor touches any agent in as, false otherwise
     */
    public boolean sensingAgent(ArrayList<Agent> as) {
        for (Agent a : as) {
            if (a != null && a.getIdentifier() != agent.getIdentifier() && sensingAgent(a)) {
                return true;
            }
        }
        return false;
    }
}
