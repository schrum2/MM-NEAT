package mockcz.cuni.amis.pogamut.base.agent.navigation;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import mockcz.cuni.pogamut.Client.AgentBody;

/**
 * There was a class in Pogamut 3.011 with this name.
 * In version 3.1, this class is a facade for accessing the
 * functionality previously claimed by PathPlanner.
 * 
 * Now, with the move to Pogamut 3.7.0, I added the getDistance
 * method. This class remains a facade for a IPathPlanner instance.
 *
 * @author Jacob Schrum
 */
public class PathPlanner implements IPathPlanner<ILocated> {

    private IPathPlanner<ILocated> planner;
    private final AgentBody body;
    private final FloydWarshallMap fwMap;
    public static final double SAFE_FLOYD_WARSHALL_START_DISTANCE = 200;

    public PathPlanner(IPathPlanner<ILocated> planner, FloydWarshallMap fwMap, AgentBody body){
        this.planner = planner;
        this.fwMap = fwMap;
        this.body = body;
    }

    public IPathFuture<ILocated> computePath(ILocated to) {
        Location start = body.info.getLocation();
        if(start != null){
            return this.computePath(start, to);
        }
        return null;
    }

    /**
     * Should only use FloydWarshall if the nearest nav point
     * is close enough to the bot
     */
    public IPathFuture<NavPoint> computePath(NavPoint to) {
        NavPoint nearest = body.info.getNearestVisibleNavPoint();
        Location start = body.info.getLocation();
        if(nearest != null && start != null && start.getDistance(nearest.getLocation()) < SAFE_FLOYD_WARSHALL_START_DISTANCE){
            return this.computePath(nearest, to);
        }
        return null;
    }

    public IPathFuture<NavPoint> computePath(NavPoint from, NavPoint to) {
        return fwMap.computePath(from, to);
    }

    @Override
    public IPathFuture<ILocated> computePath(ILocated from, ILocated to) {
        return planner.computePath(from, to);
    }

	@Override
	public double getDistance(ILocated from, ILocated to) {
		return planner.getDistance(from, to);
	}

}
