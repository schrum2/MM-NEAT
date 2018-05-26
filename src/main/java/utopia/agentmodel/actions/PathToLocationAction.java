/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import mockcz.cuni.amis.pogamut.base.agent.navigation.PathPlanner;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import mockcz.cuni.amis.pogamut.ut2004.agent.navigation.MyUTPathExecutor;
import mockcz.cuni.pogamut.Client.AgentBody;

/**
 *
 * @author nvh
 */
public class PathToLocationAction extends Action {

    private final Location target;
    private final MyUTPathExecutor pathExecutor;
    private final IPathFuture<? extends ILocated> plannedPath;
   
    @Override
    public String toString() {
        return "PathToLocation:" + target.toString();
    }

    public PathToLocationAction(MyUTPathExecutor pathExecutor, IPathFuture<? extends ILocated> plannedPath, Location target) {
        this.target = target;
        this.pathExecutor = pathExecutor;
        this.plannedPath = plannedPath;
    }

    public PathToLocationAction(MyUTPathExecutor pathExecutor, PathPlanner pathPlanner, Location target) {
        this(pathExecutor, pathPlanner.computePath(target), target);
    }

    @Override
    public void execute(AgentBody body) {
        //System.out.println("Path to " + target);
        pathExecutor.followPath(plannedPath);
    }
}
