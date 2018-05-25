package utopia.controllers.scripted;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.ArrayList;
import java.util.Map;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.NavLocation;
import utopia.agentmodel.Controller;
import utopia.agentmodel.actions.Action;
import utopia.agentmodel.actions.MoveToLocationAction;
import utopia.agentmodel.actions.OpponentRelativeAction;

public class WaterController extends Controller {

    private static ArrayList<NavLocation> pseudoNavPoints = null;

    private static ArrayList<NavLocation> getGoatswoodPseudoNavs() {
        if (pseudoNavPoints == null) {
            pseudoNavPoints = new ArrayList<NavLocation>(40);
            pseudoNavPoints.add(new NavLocation(2351, 3464, -117));
            pseudoNavPoints.add(new NavLocation(2538, 3572, -110));
            pseudoNavPoints.add(new NavLocation(3271, 3202, -190));
            pseudoNavPoints.add(new NavLocation(3713, 3320, -169));
            pseudoNavPoints.add(new NavLocation(3995, 3282, -90));
            pseudoNavPoints.add(new NavLocation(4222, 4635, -170));
            pseudoNavPoints.add(new NavLocation(195, 2263, -145));
            pseudoNavPoints.add(new NavLocation(909, 2470, -138));
            pseudoNavPoints.add(new NavLocation(478, 2321, -131));
            pseudoNavPoints.add(new NavLocation(1260, 2644, -190));
            pseudoNavPoints.add(new NavLocation(1529, 2301, -190));
            pseudoNavPoints.add(new NavLocation(1756, 1945, -190));
            pseudoNavPoints.add(new NavLocation(2622, 2544, -190));
            pseudoNavPoints.add(new NavLocation(2336, 2911, -190));
            pseudoNavPoints.add(new NavLocation(2112, 3200, -190));
            pseudoNavPoints.add(new NavLocation(5474, 4369, -190));
            pseudoNavPoints.add(new NavLocation(5932, 3956, -191));
            pseudoNavPoints.add(new NavLocation(5606, 3653, -150));
            pseudoNavPoints.add(new NavLocation(-2799, -6752, -150));
            pseudoNavPoints.add(new NavLocation(-3400, -6558, -150));
            pseudoNavPoints.add(new NavLocation(-3865, -6196, -150));
            pseudoNavPoints.add(new NavLocation(-4108, -5706, -150));
            pseudoNavPoints.add(new NavLocation(-4229, -4957, -150));
            pseudoNavPoints.add(new NavLocation(-4068, -4243, -150));
            pseudoNavPoints.add(new NavLocation(-3288, -3852, -116));
            pseudoNavPoints.add(new NavLocation(-4264, -3734, -101));
            pseudoNavPoints.add(new NavLocation(-3634, -3826, -154));
            pseudoNavPoints.add(new NavLocation(-3999, -3787, -155));
            pseudoNavPoints.add(new NavLocation(-3772, -3300, -150));
            pseudoNavPoints.add(new NavLocation(-4496, -2039, -171));
            pseudoNavPoints.add(new NavLocation(-4525, -1320, -188));
            pseudoNavPoints.add(new NavLocation(-4059, 318, -185));
            pseudoNavPoints.add(new NavLocation(-4449, -858, -189));
            pseudoNavPoints.add(new NavLocation(-4227, -117, -190));
            pseudoNavPoints.add(new NavLocation(-3337, 1835, -187));
            pseudoNavPoints.add(new NavLocation(-3120, 2380, -173));
            pseudoNavPoints.add(new NavLocation(-2815, 2904, -121));
            pseudoNavPoints.add(new NavLocation(-1942, 2969, -106));
            pseudoNavPoints.add(new NavLocation(-1289, 2587, -162));
            pseudoNavPoints.add(new NavLocation(-624, 2093, -191));
            pseudoNavPoints.add(new NavLocation(4340, 4718, -187));
            pseudoNavPoints.add(new NavLocation(4117, 4664, -121));
        }
        return pseudoNavPoints;
    }
    ArrayList<NavLocation> ignoreList;
    NavLocation lastBestNav;
    int bestNavRepeats;
    int collisionsFromPath;
    private static final int SAME_NAV_TOLERANCE = 10;
    private static final int COLLISION_TOLERANCE = 5;

    public WaterController() {
        super();
        reset();
    }

    public Controller create() {
        return new WaterController();
    }

    @Override
    public Action control(AgentMemory memory) {
        //System.out.println(memory.info.getLocation() + " " + memory.info.getFloorLocation());
        Location loc = memory.info.getLocation();
        Location floor = memory.info.getFloorLocation();
        if (loc != null && floor != null) {
            double nearestDistance = Double.MAX_VALUE;
            NavLocation bestNav = null;
            Map<WorldObjectId, NavPoint> navPoints = memory.world.getAll(NavPoint.class);
            // Merge pseudo navpoints
            ArrayList<NavLocation> navLocations = mergeWithPseudoNavs(memory, navPoints, floor.z, loc.z);
            for (NavLocation np : navLocations) {
                double navDis = np.getLocation().getDistance(loc);
                if (navDis < 50) {
                    ignoreList.add(np);
                } else if (navDis < nearestDistance && !ignoreList.contains(np)) {
                    nearestDistance = navDis;
                    bestNav = np;
                }
            }

            if (bestNav == null) {
                return null;
            }
            if (lastBestNav != null && bestNav.equals(lastBestNav)) {
                bestNavRepeats++;
                if (memory.senses.isCollidingOnce()) {
                    collisionsFromPath++;
                    //System.out.println("Colliding in water");
                }
                if (bestNavRepeats > SAME_NAV_TOLERANCE || !memory.isMoving() || collisionsFromPath > COLLISION_TOLERANCE) {
                    //System.out.println("New best");
                    ignoreList.add(bestNav);
                    bestNavRepeats = 0;
                    collisionsFromPath = 0;
                }
            }
            System.out.println("Best nav: " + bestNavRepeats);
            lastBestNav = bestNav;
            Player enemy = memory.info.getNearestVisiblePlayer();
            if (enemy != null) {
                OpponentRelativeAction.shootDecision(memory, enemy, true, false);
            } else {
                if (memory.info.isShooting()) {
                    memory.body.stopShoot();
                }
            }
            //return new PathToLocationAction(memory.pathExecutor,memory.pathPlanner,nearestNav.getLocation());
            return new MoveToLocationAction(memory, bestNav.getLocation(),enemy);
        }
        return null;
    }

    @Override
    public void reset() {
        ignoreList = new ArrayList<NavLocation>();
        lastBestNav = null;
        bestNavRepeats = 0;
        collisionsFromPath = 0;
    }

    private ArrayList<NavLocation> mergeWithPseudoNavs(AgentMemory memory, Map<WorldObjectId, NavPoint> navPoints, double floor_z, double loc_z) {
        ArrayList<NavLocation> navs;
        if (memory.game.getMapName().toLowerCase().equals("DM-GoatswoodPlay".toLowerCase())) {
            navs = getGoatswoodPseudoNavs();
        } else {
            navs = new ArrayList<NavLocation>();
        }
        for (NavPoint np : navPoints.values()) {
            if (!navHasName(np,"DM-GoatswoodPlay.PathNode186")
                    && !navHasName(np,"DM-GoatswoodPlay.PathNode109")
                    && !navHasName(np,"DM-GoatswoodPlay.PathNode94")
                    && !navHasName(np,"DM-IceHenge.PathNode71")
                    && !navHasName(np,"DM-IceHenge.InventorySpot100")
                    && !navHasName(np,"DM-IceHenge.PlayerStart6")
                    && !navHasName(np,"DM-IceHenge.PathNode93")
                    && np.getLocation().z > floor_z && np.getLocation().z < loc_z + 125) {
                navs.add(new NavLocation(np));
            }
        }
        return navs;
    }

    private static boolean navHasName(NavPoint np, String name){
        return np.getId().getStringId().toLowerCase().equals(name.toLowerCase());
    }
}
