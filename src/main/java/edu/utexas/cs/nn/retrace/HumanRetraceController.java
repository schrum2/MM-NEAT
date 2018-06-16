package edu.utexas.cs.nn.retrace;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.Point;
import edu.utexas.cs.nn.bots.HumanRetraceBot;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.agentmodel.actions.Action;
import utopia.agentmodel.actions.MoveAlongAction;
import utopia.controllers.scripted.NeedyController;

/**
 * HumanRetraceController picks the closest human trace and tries to follow it
 * for as long as it can
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class HumanRetraceController extends NeedyController {

    Future<SimpleNavPointIndex> theIndex = null;
    PoseSequence sequence = null;
    double lastTime = 0;

    /**
     * Frame length estimate
     */
    double timeEstimate = 0.2;

    boolean firstStep = true;

    /**
     * Agent speed estimate
     */
    double distanceEstimate = 100;
    private boolean discontinuityOccurred = false;

    public HumanRetraceController(){
        register("Index Not Ready");
        register("Null Sequence");
        register("Path Depleted");
        register("First Step");
        register("Distance Progress");
        register("Time Progress");
        register("Trace Failure");
        register("Discontinuity");
        register("Null Points");
        register("Restart Failed");
    }

    @Override
    public void onBotInitialized(AgentMemory memory) {
        final String level = memory.game.getMapName();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        theIndex = executor.submit(
                new Callable<SimpleNavPointIndex>() {

            @Override
                    public SimpleNavPointIndex call() throws Exception {
                        return new SimpleNavPointIndex(level);
                    }
                });

    }

    private void updateEstimates(AgentMemory memory) {
        if (lastTime > 0) {
            double timeSinceLast = memory.game.getTime() - lastTime;
            timeEstimate = Constants.ESTIMATE_DECAY.getDouble() * timeEstimate + (1.0 - Constants.ESTIMATE_DECAY.getDouble()) * timeSinceLast;
        }
        if (memory.lastPosition != null) {
            double distance = memory.body.info.getLocation().getDistance(memory.lastPosition);
            distanceEstimate = Constants.ESTIMATE_DECAY.getDouble() * distanceEstimate + (1.0 - Constants.ESTIMATE_DECAY.getDouble()) * distance;
            if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("HRC: actually moved " + distance + " from " + memory.lastPosition + " to " + memory.body.info.getLocation());
        }
        lastTime = memory.game.getTime();
    }

    @Override
    public void tick(AgentMemory memory) {
        //updateEstimates(memory);
    }

    /**
     * Return true if the controller currently has a sequence it is following
     * @param memory agent memory
     * @return
     */
    @Override
    public boolean isStillInterested(AgentMemory memory) {
        return sequence != null && sequence.hasNext();
    }

    @Override
    public void onBotShutdown(AgentMemory memory) {
        // pass
    }

    public boolean ready(){
        return theIndex.isDone();
    }

    /**
     * Lookup a human-like path connecting two points, A and B. If such a path
     * exists, set the controller to start following it.
     * @param a starting location to search for the path
     * @param b ending location to search for the path
     * @return the location of the endpoint the agent is now going to, or null
     */
    public Location setTarget(Location a, Location b) {
        if (!theIndex.isDone()) {
            return null;
        } else {
            try {
                NavpointRetraceLink link = theIndex.get().getLink(a, b);
                if (link == null) {
                    return null;
                }
                sequence = link.getPath();
                firstStep = true;
                theIndex.get().resetTimeout();
                return link.getEndPoint().getLocation();
            } catch (InterruptedException ex) {
                Logger.getLogger(HumanRetraceController.class.getName()).log(Level.SEVERE, "HRC get index exception", ex);
                return null;
            } catch (ExecutionException ex) {
                Logger.getLogger(HumanRetraceController.class.getName()).log(Level.SEVERE, "HRC get index exception", ex);
                return null;
            }
        }
    }

    @Override
    public Action control(AgentMemory memory) {
        updateEstimates(memory);
        try {
            if (!theIndex.isDone()) {
                takeAction("Index Not Ready");
                return null;
            }
            Location O = memory.body.info.getLocation();
            Point B, C;
            if (sequence == null) {
                sequence = theIndex.get().getClosestPath(O);
                firstStep = true;
            }
            if (sequence == null) {
                takeAction("Null Sequence");
                return null;
            }
            if (!sequence.hasNext()) {
                // check if we ran out of path data
                if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("HRC: sequence depleted: " + sequence);
                this.theIndex.get().putInTimeout(sequence, Constants.TRACE_TIME_OUT_DURATION.getInt());
                sequence = null;
                lastTime = 0;
                takeAction("Path Depleted");
                return null;
            }
            //sequence = theIndex.get().recalibratePath(sequence, loc);
            Point A = sequence.getCurrent();
            double adist = A.getLocation().getDistance(O.getLocation());
            if (firstStep) {
                // if this is the first step, get started first
                B = sequence.nextByTime(0);
                C = sequence.peekByTime(timeEstimate);
                takeAction("First Step");
                firstStep = false;
                if (B != null && C != null && Constants.VIEW_HRTC_MESSAGES.getBoolean()) {
                    double bdist = B.getLocation().getDistance(O.getLocation());
                    double cdist = C.getLocation().getDistance(O.getLocation());
                    System.out.println("HRC:nextByTime(0) O:"+O+" A:"+adist+" B:"+bdist+" C:"+cdist);
                }
            } else if (distanceEstimate > Constants.MIN_DISTANCE_SKIP.getDouble()) {
                //sequence = theIndex.get().recalibratePath(sequence, loc);
                // most of the time we want to make progress in terms of distance
                B = sequence.nextByDistance(Constants.HRTC_DISTANCE_FACTOR.getDouble()*distanceEstimate);
                C = sequence.peekByDistance(Constants.HRTC_DISTANCE_FACTOR.getDouble()*distanceEstimate*1.5);
                takeAction("Distance Progress");
                if (B != null && C != null && Constants.VIEW_HRTC_MESSAGES.getBoolean()) {
                    double bdist = B.getLocation().getDistance(O.getLocation());
                    double cdist = C.getLocation().getDistance(O.getLocation());
                    System.out.println("HRC:nextByDistance(" + distanceEstimate + ") O:"+O+" A:"+adist+" B:"+bdist+" C:"+cdist);
                }
            } else if (timeEstimate > Constants.MIN_TIME_SKIP.getDouble()) {
                // if that isn't really working, make progress along the human timeline
                B = sequence.nextByTime(timeEstimate);
                C = sequence.peekByTime(timeEstimate);
                takeAction("Time Progress");
                if (B != null && C != null && Constants.VIEW_HRTC_MESSAGES.getBoolean()) {
                    double bdist = B.getLocation().getDistance(O.getLocation());
                    double cdist = C.getLocation().getDistance(O.getLocation());
                    if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("HRC:nextByTime(" + timeEstimate + ") O:"+O+" A:"+adist+" B:"+bdist+" C:"+cdist);
                }
            } else {
                System.err.println("not first and estimates are bad");
                takeAction("Trace Failure");
                return null;
            }
            if (A != null && B != null && C != null) {
                // schrum2: See if the bot can head to point without hitting wall.
                // Won't know result until next logic() cycle.
                //memory.quickTraceThroughWallRequest(B.getLocation());
                double OA = O.getDistance(A.getLocation());
                double OB = O.getDistance(B.getLocation());
                double BC = B.getLocation().getDistance(C.getLocation());
                double OC = O.getDistance(C.getLocation());
                if (Constants.VIEW_HRTC_MESSAGES.getBoolean()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("HRC:DISTANCES:");
                    sb.append(OA).append(" ");
                    sb.append(OB).append(" ");
                    sb.append(BC).append(" ");
                    sb.append(OC).append(" ");
                    System.out.println(sb.toString());
                    sb = new StringBuilder();
                    sb.append("HRC:POINTS: ");
                    sb.append(O).append(" ");
                    sb.append(A.getLocation()).append(" ");
                    sb.append(B.getLocation()).append(" ");
                    sb.append(C.getLocation()).append(" ");
                    System.out.println(sb.toString());
                }
                if (OB > HumanRetraceBot.SPACE_DISCONTINUITY && OC > OB) {
                    // sequence.jump(current.getIndex()); // rewind
                    sequence = this.theIndex.get().recalibratePath(sequence, O);
                    if(sequence == null || discontinuityOccurred){
                        takeAction("Restart Failed");
                        return null;
                    }
                    // act as if this is the first step
                    B = sequence.nextByTime(0);
                    C = sequence.peekByTime(timeEstimate);
                    takeAction("Discontinuity");
                    discontinuityOccurred = true;
                    if (B == null || C == null) return null;
                } else {
                    discontinuityOccurred = false;
                }
                Action a = new MoveAlongAction(
                        B.getLocation(), C.getLocation(), // move to B and then toward A
                        sequence.getJumpFlag() // jump if there was a jump in the data
                        && (B.getLocation().z - A.getLocation().z > 5) // but only if the slope of the trace is up
                        );
                sequence.resetJumpFlag();
                return a;
            } else {
                if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("HRC: point(s) are null");
                sequence = null;
                lastTime = 0;
                takeAction("Null Points");
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(HumanRetraceController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void reset() {
        if(this.theIndex.isDone() && this.sequence != null){
            try {
                this.theIndex.get().putInTimeout(sequence, Constants.TRACE_TIME_OUT_DURATION.getInt());
            } catch (InterruptedException ex) {
                Logger.getLogger(HumanRetraceController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(HumanRetraceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        lastTime = 0;
        sequence = null;
    }

    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(HumanRetraceBot.class, "HumanRetraceBot").startAgent();
    }
}
