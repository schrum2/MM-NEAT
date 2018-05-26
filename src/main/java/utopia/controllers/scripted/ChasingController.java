package utopia.controllers.scripted;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.flag.FlagListener;
import edu.utexas.cs.nn.Constants;
import javax.vecmath.Vector3d;
import mockcz.cuni.amis.pogamut.ut2004.agent.navigation.MyUTPathExecutor;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.Utils;
import utopia.agentmodel.Controller;
import utopia.agentmodel.actions.*;

public class ChasingController extends Controller {

    public boolean startFollowing = false;
    public static final int MAX_TIMEOUT = 30;
    public int timeOut = -1;
    private Player lastSeenEnemy = null;
    public Player currentSeenEnemy = null;
    private boolean change;
    public static final int CLOSE_DISTANCE = 100;
    private int consecutiveTurns = 0;
    public static final int TOO_MANY_TURNS = 5;
    private Boolean turnRight;
    private final MyUTPathExecutor playerPathExecutor;
    public Location myLocationWhenEnemySeen = null;
    private static final double COLLISION_RISK_DISTANCE = 150;

    public ChasingController(UT2004Bot bot, final AgentMemory memory) {
        super();
        change = false;
        this.playerPathExecutor = memory.playerPathExecutor;

        register("Abort/Too Many Turns");
        register("Abort/What Direction");
        register("Turn To Find");
        register("Follow");
        register("Start Following");
        register("Timeout");
        register("Resume Following");
        register("Backtrack");
        register("Parallel to Wall");

        // Stuck detectors taken directly from NavigationBot
        memory.playerPathExecutor.addStuckDetector(new UT2004TimeStuckDetector(bot, 3000, 10000));       // if the bot does not move for 3 seconds, considered that it is stuck
        memory.playerPathExecutor.addStuckDetector(new UT2004PositionStuckDetector(bot)); // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
        memory.playerPathExecutor.addStuckDetector(new UT2004DistanceStuckDetector(bot));

        memory.playerPathExecutor.getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                String response = null;
                switch (changedValue.getState()) {
                    case PATH_COMPUTATION_FAILED:
                        abort();
                        response = "Abort";
                    case STUCK:
                    case TARGET_REACHED:
                        if (response == null) {
                            memory.body.contMove();
                            response = "Forward";
                        }
                        if (Constants.PRINT_ACTIONS.getBoolean()) {
                            System.out.println("CHASE:" + changedValue.getState() + " -> " + response);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public Action control(AgentMemory memory) {
        // About to walk into a wall
        if (!memory.playerPathExecutor.isExecuting() && memory.frontWallClose()) {
            System.out.println("\tAbout to crash!");
            // Turn and then allow walking forward to take over again
            AutoTraceRay trace = memory.frontRayTrace();
            if (trace != null) {
                Location wall = trace.getHitLocation();
                Location bot = memory.getAgentLocation().getLocation();
                if (bot != null && wall != null) {
                    Vector3d wallNormal = trace.getHitNormal();
                    Vector3d up = new Vector3d(0,0,1);
                    Vector3d cross = new Vector3d(0,0,0);
                    cross.cross(wallNormal, up);
                    cross.normalize();
                    cross.scale(500); // Should be parallel to wall
                    Location target = bot.add(new Location(cross));
                    
                    takeAction("Parallel to Wall");
                    System.out.println("\tParallel to wall! " + bot + " plus " + cross);
                    memory.body.body.getLocomotion().moveTo(target);
                }
            }
        }

        // Turning to face failed to spot enemy, so give up
        if (consecutiveTurns > TOO_MANY_TURNS) {
            takeAction("Abort/Too Many Turns");
            reset();
            return null;
        }

        Location enemyLoc = lastEnemyLocation();
        Location botLoc = memory.info.getLocation();
        // If close to last enemy sighting, then turn to find it
        if (enemyLoc != null && botLoc != null && botLoc.getDistance(enemyLoc) < CLOSE_DISTANCE) {
            // Haven't picked a turning direction yet
            if (consecutiveTurns == 0) {
                turnRight = memory.isToMyRight(enemyLoc);
                // Looking right at where the enemy was and can't find it, so give up
                if (turnRight == null) {
                    takeAction("Abort/What Direction");
                    abort();
                    return null;
                }
            }
            takeAction("Turn To Find");
            consecutiveTurns++;
            return new TurnAction(memory, turnRight);
        }
        turnRight = null;
        consecutiveTurns = 0;

        if (!change && timeOut >= 0 && startFollowing && memory.info.isMoving()) {
            takeAction("Follow");
            return new EmptyAction();
        } else if (!startFollowing) {
            // Go to where bot was when it last saw opponent, if it just lost it
            //System.out.println("Last saw at: " + myLocationWhenEnemySeen);
            if (memory.justLostOpponent(lastSeenEnemy) && myLocationWhenEnemySeen != null && botLoc != null && enemyLoc != null) {
                double toWhereILastSaw = myLocationWhenEnemySeen.getDistance(botLoc);
                double toEnemy = botLoc.getDistance(enemyLoc);
                //System.out.println("Distances: toWhereILastSaw: " + toWhereILastSaw + ", toEnemy: " + toEnemy);
                if (toWhereILastSaw < toEnemy && toWhereILastSaw > CLOSE_DISTANCE) {
                    takeAction("Backtrack");
                    return new MoveAlongAction(myLocationWhenEnemySeen, enemyLoc, enemyLoc, false);
                }
                System.out.println(memory.info.getName() + ": Distances not right for backtrack");
            }

            takeAction("Start Following");
            System.out.println("\tMe:" + botLoc + ":target:" + enemyLoc);
            change = false;
            timeOut = MAX_TIMEOUT;
            startFollowing = true;
            return new ApproachEnemyAction(memory, Utils.randomBool(), false, false, memory.getShortestTraceToWallDistance() < COLLISION_RISK_DISTANCE);
        } else if (startFollowing && timeOut >= 0) {
            takeAction("Resume Following");
            change = false;
            return new ApproachEnemyAction(memory, Utils.randomBool(), false, false, memory.getShortestTraceToWallDistance() < COLLISION_RISK_DISTANCE);
        }
        reset();
        takeAction("Timeout");
        return null;
    }

    public void countdown() {
        if (timeOut > 0) {
            timeOut--;
        }
        if (timeOut == 0) {
            reset();
        }
    }

    @Override
    public synchronized void reset() {
        abort();
        change = false;
        consecutiveTurns = 0;
        lastSeenEnemy = null;
        myLocationWhenEnemySeen = null;
        currentSeenEnemy = null;
    }

    public void abort() {
        playerPathExecutor.stop();
        wasStuck = false;
        startFollowing = false;
        timeOut = -1;
    }

    public synchronized boolean enemyAvailable() {
        boolean result = lastSeenEnemy != null;
//        System.out.println("enemyAvailable? " + result);
        return result;
    }

    public synchronized void enemyDies(UnrealId dead) {
        if (lastSeenEnemy != null && lastSeenEnemy.getId().equals(dead)) {
            lastSeenEnemy = null;
            myLocationWhenEnemySeen = null;
        }
        if (currentSeenEnemy != null && currentSeenEnemy.getId().equals(dead)) {
            currentSeenEnemy = null;
        }
    }

    public synchronized void updateEnemyMemory(AgentMemory memory) {
        currentSeenEnemy = memory.getCombatTarget();
        if (currentSeenEnemy != null) {
            if (!memory.canFocusOn(lastSeenEnemy)) {
                if (lastSeenEnemy == null || !currentSeenEnemy.getId().equals(lastSeenEnemy.getId())) {
                    change = true;
                }
                lastSeenEnemy = currentSeenEnemy;
            }
            myLocationWhenEnemySeen = memory.info.getLocation();
        }
    }

    public String chaseTargetName(AgentMemory memory) {
        try {
            Player enemy = memory.players.getNearestEnemy(Constants.MEMORY_TIME.getDouble());
            return enemy.getName();
        } catch (NullPointerException e) {
            return "?";
        }
    }

    public synchronized Location lastEnemyLocation() {
        return lastSeenEnemy == null ? null : lastSeenEnemy.getLocation();
    }

    public synchronized Player getLastEnemy() {
        return lastSeenEnemy;
    }
}
