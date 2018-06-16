package utopia.controllers.scripted;

import edu.utexas.cs.nn.bots.UT2;
import edu.utexas.cs.nn.retrace.HumanRetraceController;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import utopia.Utils;
import utopia.agentmodel.actions.Action;
import utopia.agentmodel.actions.DodgeShootAction;
import utopia.agentmodel.actions.GotoItemAction;
import utopia.agentmodel.actions.MoveAwayFromLocationAction;
import utopia.agentmodel.actions.MoveForwardAction;
import utopia.agentmodel.actions.MoveToLocationAction;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 *  Gets unstuck
 */
public final class UnstuckController extends NeedyController {

    public static final int HUMAN_TRACES_NOT_WORKING = 15;
    public static final int STANDARD_UNSTUCK_CHANCES = 3;
    public int consecutiveStuckActions;
    private int stuckReason;
    public static final double COLlISION_ACCUMULATOR_DECAY_RATE = 0.1;
    public static final double COLLISION_ACCUMULATION_TOLERANCE = 2;
    public static final double STUCK_ACCUMULATOR_DECAY_RATE = 0.1;
    public static final double STUCK_ACCUMULATION_TOLERANCE = 3;
    private double collisionAccumulator = 0;
    private double stuckAccumulator = 0;
    private HumanRetraceController humanRetraceController;
    public static boolean USE_HUMAN_TRACES = true;

    public UnstuckController(HumanRetraceController hrc) {
        super();
        reset();
        this.humanRetraceController = hrc;

        register("Human Trace");
        register("Goto Item");
        register("Move Away");
        register("Dodge");
        register("Forward");
        register("Move To");
    }

    public Action control(AgentMemory memory, int reason) {
        this.setStuckReason(reason);
        return this.control(memory);
    }

    public boolean stuckFromFrequentCollisions() {
        return collisionAccumulator > COLLISION_ACCUMULATION_TOLERANCE;
    }

    public void setStuckReason(int reason) {
        if (reason == UT2.STUCK_INDEX_COLLIDING) {
            collisionAccumulator++;
        }
        stuckAccumulator++;
        stuckReason = reason;
    }

    public Action humanAction(AgentMemory memory) {
//        NavPoint nearest = memory.info.getNearestNavPoint();
//        if(nearest != null && nearest.isLiftCenter()){
//            // Don't trust human traces through an elevator to get unstuck
//            return null;
//        }
        if (!USE_HUMAN_TRACES || stuckReason == UT2.STUCK_INDEX_UNDER_ELEVATOR) {
            return null;
        }
        if (consecutiveStuckActions > HUMAN_TRACES_NOT_WORKING) {
            if (humanRetraceController != null) {
                // Puts trace in time out
                humanRetraceController.reset();
            }
            return null;
        }
        //System.out.println("Try human unstuck");
        if (this.humanRetraceController != null) {
            //System.out.println("Human unstuck exists");
            Action a = humanRetraceController.control(memory);
            if (a != null) {
                //System.out.println("Human unstuck found trace");
                takeAction("Human Trace");
                return a;
            }
        }
        return null;
    }

    /**
     * Always call setStuckReason first
     * @param memory
     * @return
     */
    @Override
    public Action control(AgentMemory memory) {
        consecutiveStuckActions++;

        if (consecutiveStuckActions > STANDARD_UNSTUCK_CHANCES
                || stuckAccumulator > STUCK_ACCUMULATION_TOLERANCE) {
            Action human = humanAction(memory);
            if (human != null) {
                return human;
            }
        } else {
            if (stuckReason == UT2.STUCK_INDEX_UNDER_ELEVATOR) {
                Item item = memory.info.getNearestItem();
                Location loc = memory.info.getLocation();

                String map = memory.game.getMapName();
                System.out.println("\tUnder elevator in " + map);
                if (map.toUpperCase().equals("DM-CURSE4")) {
                    NavPoint nearestLiftExit = memory.nearestLiftExit();
                    System.out.println("\tNearest Exit:" + nearestLiftExit.getId().getStringId());
                    if (nearestLiftExit.getId().getStringId().toUpperCase().equals("DM-CURSE4.LIFTEXIT2")) {
                        System.out.println("\tEscape Curse4 Elevator Trap");
                        takeAction("Move Away");
                        return new MoveToLocationAction(memory, new Location(1125, -584, -105), memory.getCombatTarget(), true);
                    }
                }
                
                if (item != null && loc != null && item.getLocation() != null && item.getLocation().z <= loc.z + 30) {
                    return gotoItem(memory);
                } else {
                    return dodgeAction(memory);
                }
//                Map<WorldObjectId, NavPoint> navs = memory.world.getAll(NavPoint.class);
//                Location bot = memory.info.getLocation();
//                if (bot != null) {
//                    NavPoint nearest = DistanceUtils.getNearest(navs.values(), bot);
//                    NavPoint secondNearest = DistanceUtils.getSecondNearest(navs.values(), bot);
//                    Action result = null;
//                    if (nearest != null && nearest.isLiftExit()) {
//                        result = new MoveToLocationAction(nearest.getLocation(), memory.getCombatTarget());
//                    } else if (secondNearest != null && secondNearest.isLiftExit()) {
//                        result = new MoveToLocationAction(secondNearest.getLocation(), memory.getCombatTarget());
//                    }
//                    if(result != null){
//                        takeAction("Move To");
//                        return result;
//                    }
//                }
            }

            if (stuckReason == UT2.STUCK_INDEX_SAME_NAV) {
                Action human = humanAction(memory);
                if (human != null) {
                    return human;
                }
            }

            if (stuckReason == UT2.STUCK_INDEX_OFF_GRID) {
                Action human = humanAction(memory);
                if (human != null) {
                    return human;
                }
            }

            if (stuckReason == UT2.STUCK_INDEX_NO_PROGRESS) {
                Action human = humanAction(memory);
                if (human != null) {
                    return human;
                }
            }

            if (stuckReason == UT2.STUCK_INDEX_COLLISION_FREQUENCY) {
                return dodgeAction(memory);
            }

            // Move if still
            if (stuckReason == UT2.STUCK_INDEX_STILL) {
                Action human = humanAction(memory);
                if (human != null) {
                    return human;
                }
                return forwardAction();
            }

            // Move away from wall collisions
            if (stuckReason == UT2.STUCK_INDEX_COLLIDING && memory.senses.getCollisionLocation() != null) {
                return moveAway(memory, memory.senses.getCollisionLocation().getLocation());
            }
            // Move away from agent collisions
            if (stuckReason == UT2.STUCK_INDEX_BUMPING && memory.senses.getBumpLocation() != null) {
                return moveAway(memory, memory.senses.getBumpLocation().getLocation());
            }
            // Dodge randomly about if stuck some other way
            if (stuckReason == UT2.STUCK_INDEX_UNKNOWN) {
                return dodgeAction(memory);
            }
        }

        // Last-ditch effort: From Simple Unstuck Action
        double p = Math.random();
        if (p < 0.25) {
            return dodgeAction(memory);
        } else if (p < 0.5) {
            return forwardAction();
        } else if (p < 0.75) {
            return moveAway(memory, null);
        } else {
            return gotoItem(memory);
        }
    }

    public Action gotoItem(AgentMemory memory) {
        takeAction("Goto Item");
        return new GotoItemAction(memory, memory.info.getNearestItem());
    }

    @Override
    public void reset() {
        consecutiveStuckActions = 0;
        stuckReason = UT2.STUCK_INDEX_UNKNOWN;
    }

    @Override
    public void tick(AgentMemory memory) {
        if (humanRetraceController != null) {
            this.humanRetraceController.tick(memory);
        }
        return;
    }

    public void decay() {
        collisionAccumulator *= (1 - COLlISION_ACCUMULATOR_DECAY_RATE);
        stuckAccumulator *= (1 - STUCK_ACCUMULATOR_DECAY_RATE);
        //System.out.println("collisionAccumulator = " + collisionAccumulator);
    }

    @Override
    public boolean isStillInterested(AgentMemory agent) {
        // this controller is very lazy
        return false;
    }

    private Action moveAway(AgentMemory memory, Location hitLoc) {
        takeAction("Move Away");
        if (hitLoc == null || Math.random() < 0.5) {
            Triple longest = memory.getLongestTraceToWall();
            return new MoveToLocationAction(memory, memory.info.getLocation().add(longest.getLocation()), memory.getCombatTarget());
        } else {
            return new MoveAwayFromLocationAction(memory, hitLoc);
        }
    }

    private Action dodgeAction(AgentMemory memory) {
        takeAction("Dodge");
        Triple longest = memory.getLongestTraceToWall();
        if (longest != null && consecutiveStuckActions < STANDARD_UNSTUCK_CHANCES * 2) {
            return new DodgeShootAction(longest, memory.getAgentLocation(), memory.getAgentRotation());
        } else {
            return new DodgeShootAction(Utils.randposneg() * Math.random(), memory.getAgentLocation(), memory.getAgentRotation());
        }
    }

    private Action forwardAction() {
        takeAction("Forward");
        return new MoveForwardAction();
    }

    @Override
    public void onBotInitialized(AgentMemory memory) {
        // ignore
    }

    @Override
    public void onBotShutdown(AgentMemory memory) {
        // ignore
    }
}
