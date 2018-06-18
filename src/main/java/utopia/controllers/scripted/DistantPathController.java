package utopia.controllers.scripted;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.future.FutureStatus;
import edu.southwestern.parameters.Parameters;
import edu.utexas.cs.nn.Constants;
import java.io.PrintWriter;
import java.util.*;
import mockcz.cuni.amis.pogamut.base.agent.navigation.PathPlanner;
import mockcz.cuni.amis.pogamut.ut2004.agent.navigation.MyUTPathExecutor;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.Utils;
import utopia.agentmodel.ActionLog;
import utopia.agentmodel.actions.Action;
import utopia.agentmodel.actions.EmptyAction;
import utopia.agentmodel.actions.OpponentRelativeAction;
import utopia.agentmodel.actions.PathToLocationAction;

public class DistantPathController extends PathController {

    public static final int ITEM_IGNORE_TIME = 30;
    protected final AgentMemory memory;
    public int currentPlanner = PATH_PLANNER_UTASTAR;
    public static final int SHORT_REMEAINING_PATH_LENGTH = 0; // 1; // Need to remove this completely
    public boolean focused = false;
    private double lastNewPathTime = 0;
    public static final double MINIMUM_PATH_CHANCE = 4;

    /**
     * Returns a new path action that causes the bot to focus on and seek the
     * specified item.
     *
     * @param memory bot's memory
     * @param item item for bot to approach
     * @return action to approach item
     */
    public Action getItem(AgentMemory memory, Item item) {
        if (item != null) {
            //takeAction("Important Item");
            Action temp = newPathAction(item);
            focused = true;
            return temp;
        } else {
            return null;
        }
    }

    /**
     * Returns new path action to approach nearest worthwhile weapon. This
     * excludes the link gun, which is a judging weapon in botprize.
     *
     * @param memory bot's memory
     * @return action to approach nearest worthwhile weapon
     */
    public Action getNeededWeapon(AgentMemory memory) {
        Collection<Item> linkGuns = memory.items.getAllItems(UT2004ItemType.LINK_GUN).values();
        // Never go for link gun
        for (Item lg : linkGuns) {
            tabooItems.add(lg);
        }
        Collection<Item> weapons = tabooItems.filter(memory.items.getSpawnedItems(UT2004ItemType.Category.WEAPON).values());
        //System.out.println("WEAPONS: " + weapons);
        Item closest = DistanceUtils.getNearest(weapons, memory.info.getLocation());
        if (closest != null) {
            takeAction("Needed Weapon");
            Action temp = newPathAction(closest);
            focused = true;
            return temp;
        } else {
            return null;
        }
    }

    public Action proceedToNavPoint(String id) {
        Map<WorldObjectId, NavPoint> navs = memory.world.getAll(NavPoint.class);
        NavPoint navTarget = navs.get(UnrealId.get(id));
        return newPathAction(navTarget);
    }

    protected Action newPathAction(NavPoint current) {
        IPathFuture<NavPoint> path;
        path = pathPlanner.computePath(current);
        if (path != null && path.getStatus().equals(FutureStatus.FUTURE_IS_READY) && path.get() != null) {
            currentPlanner = PATH_PLANNER_FLOYD_WARSHALL;
            pathPlannerActions[PATH_PLANNER_FLOYD_WARSHALL]++;
            lastNewPathTime = memory.game.getTime();
            return new PathToLocationAction(pathExecutor, path, current.getLocation());
        }
        return newPathAction(current.getLocation());
    }

    protected Action newPathAction(Item current) {
        if (!wasStuck && current != null && item != null && current.getLocation() != null
                && current.getLocation().equals(item.getLocation(), 20)) {
            // Don't re-plan path to same item
            return new EmptyAction();
        }
        stop();
        item = current;
        focused = false;
        if (current.getNavPoint() == null) {
            return newPathAction(current.getLocation());
        }
        return newPathAction(current.getNavPoint());
    }

    protected Action newPathAction(Location current) {
        if (current == null) {
        	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        		System.out.println("Null location in newPathAction()");
        	}
            return new EmptyAction();
        }
        currentPlanner = PATH_PLANNER_UTASTAR;
        pathPlannerActions[PATH_PLANNER_UTASTAR]++;
        lastNewPathTime = memory.game.getTime();
        return new PathToLocationAction(pathExecutor, pathPlanner, current);
    }

    public void pathEvent(boolean makeTaboo) {
        //System.out.println("path event: " + item);
        if (item != null) {
            if (makeTaboo) {
                //System.out.println(itemName() + " is taboo");
                tabooItems.add(item, ITEM_IGNORE_TIME);
                tabooNavPoints.add(item.getNavPoint(), ITEM_IGNORE_TIME);
            }
        }
        reset();
    }

    public DistantPathController(UT2004Bot bot, final MyUTPathExecutor pathExecutor, final PathPlanner pathPlanner, final AgentMemory memory) {
        super(bot, pathExecutor, pathPlanner);
        this.pathPlannerActions = new int[NUM_PATH_PLANNERS];
        this.memory = memory;

        register("Needed Weapon");
        register("Follow and Shoot");
        register("Just Follow");
        register("Random Far Item");
        register("Failed Path Replan");
        register("Path After State Change");

        // Stuck detectors taken directly from NavigationBot
        pathExecutor.addStuckDetector(new UT2004TimeStuckDetector(bot, 3000, 10000));       // if the bot does not move for 3 seconds, considered that it is stuck
        pathExecutor.addStuckDetector(new UT2004PositionStuckDetector(bot)); // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
        pathExecutor.addStuckDetector(new UT2004DistanceStuckDetector(bot));

        pathExecutor.getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                //System.out.println(changedValue.getState());
                switch (changedValue.getState()) {
                    case PATH_COMPUTATION_FAILED:
                    case TARGET_REACHED:
                    case STUCK:
                        focused = false;
                        pathEvent(true);

                        // Don't create tons of new paths indiscriminately
                        double timeDiff = memory.game.getTime() - lastNewPathTime;
                        boolean lastPathRecent = (timeDiff < MINIMUM_PATH_CHANCE);
                        if (!lastPathRecent) {
                            // Start the next path immediately, since the listener
                            // is faster than the logic cycle
                            Action a = newPathAction(farthestOfX(3));
                            if (Constants.PRINT_ACTIONS.getBoolean()) {
                            	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
                            		System.out.println("PATH:" + changedValue.getState() + " -> NEXT:" + itemName() + ":" + a);
                            	}
                            }
                            takeAction("Path After State Change");
                            a.execute(memory.body);
                        }
                        break;
                }
            }
        });
    }
    public int[] pathActions = null;
    public static boolean SHOOT_ON_RUN = true;
    public static final int NUM_PATH_PLANNERS = 2;
    public int[] pathPlannerActions = null;
    public static final int PATH_PLANNER_UTASTAR = 0;
    public static final int PATH_PLANNER_FLOYD_WARSHALL = 1;

    @Override
    public String lastActionLabel() {
        String type = "";
        switch (currentPlanner) {
            case PATH_PLANNER_UTASTAR:
                type = "A*";
                break;
            case PATH_PLANNER_FLOYD_WARSHALL:
                type = "FW";
                break;
        }
        return (focused ? "FOCUSED:" : "") + type + ":" + super.lastActionLabel();
    }

    @Override
    public Action control(AgentMemory memory) {
        Action result;
        Integer pathLength = this.pathExecutor.remainingPathSize();
        boolean longLengthLeft = pathLength == null || pathLength > SHORT_REMEAINING_PATH_LENGTH;
        double timeDiff = memory.game.getTime() - lastNewPathTime;
        boolean lastPathRecent = (timeDiff < MINIMUM_PATH_CHANCE);

        //System.out.println("focused:" + focused + ",wasStuck:" + wasStuck + ",isMoving:" + isMoving + ",longLengthLeft:" + longLengthLeft + ",pathLength:" + pathLength + ",timeDiff:" + timeDiff + ",lastNewPathTime:" + lastNewPathTime + ",lastPathRecent:" + lastPathRecent + ",v:" + v);
        //System.out.println("("+!wasStuck+" && "+isMoving+" && ("+focused+" || "+longLengthLeft+" || "+lastPathRecent+")) ");

        boolean nullItem = getItem() == null;
        boolean isMoving = (pathExecutor.isMoving() || memory.onElevator());

        if (!wasStuck && !retraceFailed
                // Only plans paths to candidates
                && !nullItem
                // Must be moving
                && (isMoving || lastPathRecent)
                // Keep running if focused
                && (focused
                // Or the remaining path is long
                || longLengthLeft
                // Or last path call too recent
                || lastPathRecent)) {
            Player enemy = memory.getCombatTarget();
            Weapon weapon = memory.getCurrentWeapon();
            if (SHOOT_ON_RUN
                    && enemy != null
                    && memory.info.isFacing(enemy)
                    && enemy.isVisible()
                    && weapon != null
                    && !weapon.getType().equals(UT2004ItemType.LINK_GUN)) {
                takeAction("Follow and Shoot");
                pathExecutor.setFocus(enemy);
                OpponentRelativeAction.shootDecision(memory, enemy, true, false);
            } else {
                takeAction("Just Follow");
                pathExecutor.setFocus(null);
                if (memory.info.isShooting()) {
                    memory.body.stopShoot();
                }
            }
            result = new EmptyAction();
        } else {
        	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        		System.out.println("Need New Path: "
                    + (wasStuck ? "wasStuck " : "")
                    + (retraceFailed ? "retraceFailed " : "")
                    + (nullItem ? "nullItem " : "")
                    + (!isMoving ? "not moving " : "")
                    + (!focused ? "not focused " : "")
                    + (!longLengthLeft ? "not longLengthLeft " : "")
                    + (!lastPathRecent ? "not lastPathRecent " : ""));
        	}

            lastNewPathTime = memory.game.getTime();
            pathExecutor.setFocus(null);
            result = newPathAction(farthestOfX(3));
            if (result instanceof PathToLocationAction) {
                takeAction("Random Far Item");
            } else {
                takeAction("Failed Path Replan");
            }
            wasStuck = false;
            retraceFailed = false;
            return result;
        }
        return result;
    }

    @Override
    public void logActionChoices(PrintWriter actionLog) {
        super.logActionChoices(actionLog);

        actionLog.println("PLANNED PATHS");
        int totalPathsPlanned = pathPlannerActions[PATH_PLANNER_UTASTAR] + pathPlannerActions[PATH_PLANNER_FLOYD_WARSHALL];
        actionLog.println("Total Paths Planned: " + totalPathsPlanned);
        actionLog.println(ActionLog.actionLogLine(null, "UT A Star     ", pathPlannerActions[PATH_PLANNER_UTASTAR], totalPathsPlanned));
        actionLog.println(ActionLog.actionLogLine(null, "Floyd Warshall", pathPlannerActions[PATH_PLANNER_FLOYD_WARSHALL], totalPathsPlanned));
        actionLog.println();
    }

    @Override
    public void reset() {
        focused = false;
        super.reset();
    }

    protected Collection<Item> allGoodItems() {
        Collection<Item> spawned = memory.items.getSpawnedItems().values();
        Collection<Item> items = tabooItems.filter(spawned);
        Iterator<Item> itr = items.iterator();
        while (itr.hasNext()) {
            Item i = itr.next();
            // FIXME: isVisible -> isReachable
            //if (!i.isVisible() || !wantItem(memory, i)) {
            if (!wantItem(memory, i)) {
                itr.remove();
            }
        }
        return items;
    }

    protected Collection<Item> goodItems() {
        Collection<Item> candidates = allGoodItems();
        candidates.remove(item); // Don't go to the last item chosen
        boolean emptyCandidates = candidates.isEmpty();
        if (emptyCandidates) {
            candidates = memory.items.getAllItems(UT2004ItemType.Category.WEAPON).values();
            Iterator<Item> itr = candidates.iterator();
            while (itr.hasNext()) {
                if (itr.next().getType().equals(UT2004ItemType.LINK_GUN)) {
                    itr.remove();
                }
            }
        }
        Collection<Item> items = tabooItems.filter(candidates);
        boolean noSafeItems = items.isEmpty();
        if (noSafeItems) {
            items = candidates;
            if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
            	System.out.println("\tClear " + tabooItems.size() + " taboo items");
            }
            tabooItems.clear();
        }

        // Favor moving up
        HashSet<Item> sameLevel = new HashSet<Item>();
        Location botFloorLocation = memory.info.getFloorLocation();
        final double magicLevel = 20;
        for (Item i : items) {
            if (botFloorLocation.z - magicLevel < i.getLocation().z) {
                sameLevel.add(i);
            }
        }
        if (!sameLevel.isEmpty()) {
        	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        		System.out.println("\tPursue items at current level or higher");
        	}
            items = sameLevel;
        }

        // Now favor visible items
        HashSet<Item> visible = new HashSet<Item>();
        for (Item i : items) {
            if (i.isVisible()) {
                visible.add(i);
            }
        }
        if (!visible.isEmpty()) {
        	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        		System.out.println("\tPursue visible items");
        	}
            items = visible;
        }

        if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        	System.out.println("\t" + items.size() + " options");
        }
        return items;
    }

    /**
     * Picks three random "good" items, and returns whichever one is farthest
     * away.
     *
     * @param num
     * @return farthest item of three randomly chosen "good" items
     */
    protected Item farthestOfX(int num) {
        Collection<Item> candidates = goodItems();
        ArrayList<Item> keepers = new ArrayList<Item>(num);
        for (int i = 0; i < num; i++) {
            Item option = MyCollections.getRandom(candidates);
            if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
            	System.out.println("\tOption:" + option.getType().getName());
            }
            keepers.add(option);
        }
        //System.out.println("PICKS: " + candidates);
        return Utils.getFarthest(candidates, memory.info.getLocation());
    }
}
