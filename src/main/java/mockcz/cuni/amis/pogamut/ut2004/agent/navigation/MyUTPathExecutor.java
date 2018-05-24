/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mockcz.cuni.amis.pogamut.ut2004.agent.navigation;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import mockcz.cuni.pogamut.Client.AgentBody;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Jacob Schrum
 */
public class MyUTPathExecutor implements IUnrealPathExecutor<ILocated> {

    private IUnrealPathExecutor<ILocated> executor;
    private final AgentBody body;
    private ILocated lastDestination = null;

    public MyUTPathExecutor(IUnrealPathExecutor executor, AgentBody body) {
        this.executor = executor;
        this.body = body;
    }

    @Override
    public ImmutableFlag<IPathExecutorState> getState() {
        return executor.getState();
    }

    @Override
    public IPathFuture<ILocated> getPathFuture() {
        return executor.getPathFuture();
    }

    @Override
    public List<ILocated> getPath() {
        return executor.getPath();
//        if (result == null) {
//            result = new ArrayList<ILocated>();
//        }
//        return result;
    }

    @Override
    public int getPathElementIndex() {
        return executor.getPathElementIndex();
    }

    public Integer remainingPathSize() {
        List<ILocated> path = getPath();
        if(path == null) return null;
        int start = getPathElementIndex();
        int result = path.size() - (start + 1);
        return result;
    }

    @Override
    public boolean inState(PathExecutorState... states) {
        return executor.inState(states);
    }

    @Override
    public boolean notInState(PathExecutorState... states) {
        return executor.notInState(states);
    }

    @Override
    public boolean isExecuting() {
        return executor.isExecuting();
    }

    @Override
    public boolean isTargetReached() {
        return executor.isTargetReached();
    }

    @Override
    public boolean isStuck() {
        return executor.isStuck();
    }

    @Override
    public boolean isPathUnavailable() {
        return executor.isPathUnavailable();
    }

    @Override
    public void stop() {
        executor.stop();
    }

    @Override
    public void addStuckDetector(IStuckDetector stuckDetector) {
        executor.addStuckDetector(stuckDetector);
    }

    @Override
    public void removeStuckDetector(IStuckDetector stuckDetector) {
        executor.removeStuckDetector(stuckDetector);
    }

    @Override
    public Logger getLog() {
        return executor.getLog();
    }

    public boolean isMoving() {
        return body.isMoving();
    }

    @Override
    public void setFocus(ILocated located) {
        executor.setFocus(located);
    }

    @Override
    public ILocated getFocus() {
        return executor.getFocus();
    }

    @Override
    public void followPath(IPathFuture<? extends ILocated> path) {
        // Don't plan a new path to the location that the executor is already going to
        if(lastDestination != null && lastDestination.getLocation() != null
                && executor.isExecuting()
                && lastDestination.getLocation().equals(path.getPathTo().getLocation(), 20)){
            return;
        }
        lastDestination = path.getPathTo();
        executor.followPath(path);
    }

    @Override
    public ILocated getPathElement() {
        return executor.getPathElement();
    }

    @Override
    public ILocated getPathFrom() {
        return executor.getPathFrom();
    }

    @Override
    public ILocated getPathTo() {
        return executor.getPathTo();
    }

    @Override
    public void removeAllStuckDetectors() {
        executor.removeAllStuckDetectors();
    }
}
