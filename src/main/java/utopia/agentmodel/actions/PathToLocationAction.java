package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import mockcz.cuni.amis.pogamut.base.agent.navigation.PathPlanner;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import mockcz.cuni.amis.pogamut.ut2004.agent.navigation.MyUTPathExecutor;
import mockcz.cuni.pogamut.Client.AgentBody;

/**
 * Finds a path to a location for the bot
 * @author nvh
 */
public class PathToLocationAction extends Action {

    private final Location target;
    private final MyUTPathExecutor pathExecutor;
    private final IPathFuture<? extends ILocated> plannedPath;
   
    @Override
    /**
     * allows the bot to print out a description of its actions
     */
    public String toString() {
        return "PathToLocation:" + target.toString();
    }

    /**
     * Initializes the action with the pathExecutor, location, and target
     * @param pathExecutor (what will help the both follow the path)
     * @param plannedPath (creates the path)
     * @param target (where the bot wants to go)
     */
    public PathToLocationAction(MyUTPathExecutor pathExecutor, IPathFuture<? extends ILocated> plannedPath, Location target) {
        this.target = target;
        this.pathExecutor = pathExecutor;
        this.plannedPath = plannedPath;
    }

    /**
	 * Initializes the action with the pathExecutor, location, and target, but with a pre planned path
     * @param pathExecutor (what will help the both follow the path)
     * @param plannedPath (creates the path)
     * @param target (where the bot wants to go)
     */
    public PathToLocationAction(MyUTPathExecutor pathExecutor, PathPlanner pathPlanner, Location target) {
        this(pathExecutor, pathPlanner.computePath(target), target);
    }

    @Override
    /**
     * tells the bot to execute the action
     */
    public void execute(AgentBody body) {
        //System.out.println("Path to " + target);
        pathExecutor.followPath(plannedPath);
    }
}
