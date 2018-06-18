package mockcz.cuni.pogamut.Client;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.*;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.*;
import edu.southwestern.parameters.Parameters;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.bots.UT2;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import java.util.*;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import mockcz.cuni.amis.pogamut.base.agent.navigation.PathPlanner;
import mockcz.cuni.amis.pogamut.ut2004.agent.navigation.MyUTPathExecutor;
import mockcz.cuni.pogamut.MessageObjects.Triple;

/**
 *
 * @author HeDeceives
 */
public class AgentMemory {

    public static final double FACING_TO_SHOOT_DEGREES = 20;
    public static final double FACING_ANGLE_DEGREES_THRESHOLD = 45;
    public static final double TIME_UNTIL_SAFE = 5;
    public static final double CLOSE_PROJECTILE_DISTANCE = 500;
    public static final double CLOSE_ENEMY_DISTANCE = 500;
    public static final double HIGH_GROUND_Z_DISTANCE = 200;
    public static final double MIN_CONTEXT_SWITCH_TIME = 3; //1;
    public static final double CONTEXT_FORGET_TIME = 15;
    public static final double JUST_LOST_PLAYER_TIME = 3;
    public final AgentInfo info;
    public final Senses senses;
    public final Players players;
    public final Items items;
    public final PathPlanner pathPlanner;
    public final Weaponry weaponry;
    public final AgentBody body;
    public final Raycasting raycasting;
    public final IVisionWorldView world;
    public final MyUTPathExecutor itemPathExecutor;
    public final MyUTPathExecutor playerPathExecutor;
    public final Game game;
    public UnrealId lastPlayerDamaged = null;
    private Player judgeTarget = null;
    public double lastCombatJumpTime = 0;
    public static final double MIN_TIME_BETWEEN_JUMPS = 0.08;
    public static final double MIN_TIME_BETWEEN_DODGES = 0.08;
    public Player lastCombatTarget;
    public Location lastPosition;
    public HashMap<UnrealId, Double> playerAppearedTimes;
    public HashMap<UnrealId, Double> playerDisappearedTimes;
    public double linkGunSwitchTime = 0;
    public double weaponSwitchTime = 0;
    public double lastQuickTurn = 0;
    public Player lastEnemySpotting = null;
    public int consecutiveJudgeActions = 0;

    public static final int GOATSWOOD_WATER_LEVEL = -185;

    private VolumeChanged lastVolumeChanged = null;
    private Double lastTimeInWater = null;
    
    IWorldEventListener<VolumeChanged> volumeChangedHandler = new IWorldEventListener<VolumeChanged>() {

        @Override
        public void notify(VolumeChanged vol) {
            lastVolumeChanged = vol;
            if (vol.isWaterVolume()) {
                lastTimeInWater = game.getTime();
                if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
                	System.out.println("In water at time: " + lastTimeInWater);
                }
            }
        }
    };

    public void resetWaterMemory() {
        lastVolumeChanged = null;
        lastTimeInWater = null;
    }
    
    public boolean inWater() {
        return (this.levelGoatswood() && info.getFloorLocation() != null && info.getFloorLocation().z < GOATSWOOD_WATER_LEVEL)
                || (this.lastVolumeChanged != null && this.lastVolumeChanged.isWaterVolume() && info.isCurrentVolumeWater());
    }
    
    Boolean inGoatswood = null;
    public boolean levelGoatswood() {
        if (inGoatswood != null) {
            return inGoatswood;
        }
        String map = game.getMapName();
        if (map != null) {
            inGoatswood = map.toLowerCase().equals("DM-GoatswoodPlay".toLowerCase());
            return inGoatswood;
        }
        return false;
    }    

    public boolean inWater(double timeframe) {
        return (timeSinceLastInWater() < timeframe) || inWater();
    }

    private double timeSinceLastInWater() {
        double time = game.getTime();
        if (this.lastTimeInWater != null) {
            return (time - lastTimeInWater);
        }
        return Double.MAX_VALUE;
    }
    
    public void seeEnemy(){
        if(players.canSeeEnemies()){
            lastEnemySpotting = players.getNearestVisibleEnemy();
        }
    }
    
    IWorldEventListener<PlayerDamaged> playerDamagedHandler = new IWorldEventListener<PlayerDamaged>() {

        @Override
        public void notify(PlayerDamaged pd) {
            lastPlayerDamaged = pd.getId();
        }
    };
    IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>> playerAppeared = new IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>>() {

        @Override
        public void notify(WorldObjectAppearedEvent<Player> event) {
            if (event != null) {
                Player p = event.getObject();
                if (p != null) {
                    //System.out.println(event);
                    playerAppearedTimes.put(p.getId(), game.getTime());
                }
            }
        }
    };

    public AgentMemory(AgentBody body, AgentInfo info, Senses senses, Players players, PathPlanner pathPlanner, MyUTPathExecutor itemPathExecutor, MyUTPathExecutor playerPathExecutor, Items items, Weaponry weaponry, IVisionWorldView world, Game game) {
        this.body = body;
        this.info = info;
        this.senses = senses;
        this.players = players;
        this.pathPlanner = pathPlanner;
        this.itemPathExecutor = itemPathExecutor;
        this.playerPathExecutor = playerPathExecutor;
        this.items = items;
        this.weaponry = weaponry;
        this.raycasting = body.raycasting;
        this.world = world;
        this.game = game;

        this.playerAppearedTimes = new HashMap<UnrealId, Double>();
        this.playerDisappearedTimes = new HashMap<UnrealId, Double>();

        this.world.addEventListener(PlayerDamaged.class, playerDamagedHandler);
//        this.world.addEventListener(FastTraceResponse.class, fastTraceResponseHandler);
        this.world.addObjectListener(Player.class, WorldObjectAppearedEvent.class, playerAppeared);
    }

    public static double getContextSwitchDelay() {
        return MIN_CONTEXT_SWITCH_TIME + (Math.random() * 1.0);
    }

    public boolean justLostOpponent(Player p) {
        if (p == null) {
            return false;
        }
        Double disappearedTime = playerDisappearedTimes.get(p.getId());
        if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        	System.out.println("Disappear time: " + disappearedTime);
        }
        if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        	System.out.println("Current time: " + game.getTime());
        }
        boolean result = (disappearedTime != null && (game.getTime() - disappearedTime) < JUST_LOST_PLAYER_TIME);
        if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        	System.out.println(info.getName() + ":Just lost " + p.getName() + " = " + result);
        }
        return result;
    }

    /**
     * Returns true if the player p has been visible long enough for the
     * bot to reliably focus on it.
     * @param p
     * @return
     */
    public boolean canFocusOn(Player p) {
        if (p == null) {
            return false;
        }
        
        /**
         * Can focus immediately if damaged recently
         */
        BotDamaged damaged = senses.getLastDamage();
        if (damaged != null && damaged.getInstigator() != null && damaged.getInstigator().equals(p.getId())) {
            double currentTime = info.getTime();
            double lastDamagedTime = damaged.getSimTime();
            double diff = (currentTime - lastDamagedTime);
            if (diff < 5 && diff > 1) {
                return true;
            }
        }
        
        Location playerLocation = p.getLocation();
        double delayMult = 1.0;
        if(playerLocation != null && info.getLocation() != null){
            // Can always focus on very close players
            double distance = playerLocation.getDistance(info.getLocation());
            if(distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE){
                return true;
            } else {
                delayMult += distance / WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE;
//                veryFar = distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE;
            }
        }

        Double appearedTime = playerAppearedTimes.get(p.getId());
        Double disappearedTime = playerDisappearedTimes.get(p.getId());
        boolean result = false;
        if (appearedTime != null) {
            if (((game.getTime() - appearedTime) > (getContextSwitchDelay() * Math.pow(delayMult,2)))
                    || (disappearedTime != null && (game.getTime() - disappearedTime) < CONTEXT_FORGET_TIME)) {
                // Opponent appeared recently or dissappeared recently (and has possible reappeared since)
                result = true;
            }
        }
        if (!result) {
        	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        		System.out.println(info.getName() + ":Context Switching");
        	}
        }
        //System.out.println("canFocusOn(" + p.getName() + ") = " + result);
        return result;
    }

    public boolean judgingGunReady() {
        return (game.getTime() - linkGunSwitchTime) > 1.5; //MIN_CONTEXT_SWITCH_TIME;
    }

    public boolean gunReady() {
        return (game.getTime() - weaponSwitchTime) > 1.5; //MIN_CONTEXT_SWITCH_TIME;
    }

//    public boolean inWater() {
//        Boolean water = this.info.isCurrentVolumeWater();
//        boolean result = (water != null) && water.booleanValue();
//        if(result) {
//            System.out.println("\tIn Water: Fluid Friction: " +info.getCurrentVolumeFluidFriction()+ ": Ground: " + info.isTouchingGround() + ": On Graph: " + info.isOnNavGraph() + ": Water Speed: " + info.getWaterSpeed());
//        }
//        return result;
//    }

    /**
     * This method can probably be improved with the cognitive notion of 'attention'.
     * This is supposedly what ConsciousRobots did, but their paper described it poorly.
     * The idea is to focus on who was previously focused on, even at the risk of ignoring
     * new, better targets.
     *
     * @return Player to focus on during fighting, amidst multiple opponents
     */
    public Player getCombatTarget() {
        Player target = pickCombatTarget();
        if (target != null) {
            lastCombatTarget = target;
        }
        return target;
    }

    private Player pickCombatTarget() {
        if (UT2.JUDGE && !UT2.evolving && weaponry.getCurrentWeapon() != null && weaponry.getCurrentWeapon().getType().equals(UT2004ItemType.LINK_GUN)) {
            if (judgeTarget != null) {
                return judgeTarget;
            }
        }

        Player result = null;
        Player nearest = getSeeEnemy();
        int numberOpponentsVisible = numVisibleOpponents();
        if (lastPlayerDamaged != null && nearest != null) {
            Player lastEnemy = players.getVisiblePlayer(lastPlayerDamaged);
            Location agent = info.getLocation();
            if (lastEnemy != null && agent != null && !lastPlayerDamaged.equals(nearest.getId())) {
                double lastEnemyDistance = agent.getDistance(lastEnemy.getLocation());
                double nearestEnemyDistance = agent.getDistance(nearest.getLocation());
                if (    // If lastDamaged is very far, but nearest is in ranged
                        (lastEnemyDistance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE && nearestEnemyDistance < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE)
                        || // Or lastDamaged is ranged and nearest is melee
                        (lastEnemyDistance > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE && nearestEnemyDistance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE)
                        || // Or more than two enemies present and close to one
                        (numberOpponentsVisible > 2 && nearestEnemyDistance < (WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 3))) {
                    // Situations in which a closer enemy should be the focus instead of the last enemy hit
                    result = nearest;
                } else {
                    result = lastEnemy;
                }
            }
        }
        if (result == null) {
            result = nearest;
        }
        return result;
    }

    public boolean onElevator() {
        NavPoint nearest = info.getNearestNavPoint();
        if (nearest != null && nearest.isLiftCenter()) {
            return true;
        }
        return false;
    }

    public double getAgentHealth() {
        return this.info.getHealth().doubleValue();
    }

    public boolean isBeingDamaged() {
        return this.senses.isBeingDamaged();
    }

    public boolean getHearNoise() {
        return this.senses.isHearingNoise();
    }

    public ArrayList<Player> seenPlayers(int secondsHistory) {
        Map<UnrealId, Player> seenPlayers = this.players.getPlayers();
        ArrayList<Player> returnResult = new ArrayList<Player>();
        for (Player p : seenPlayers.values()) {
            if ((this.info.getSelf().getSimTime() - p.getSimTime()) <= secondsHistory) {
                returnResult.add(p);
            }
        }
        return returnResult;
    }

    public ArrayList<Player> getKnownPlayers() {
        return seenPlayers(Integer.MAX_VALUE);
    }

    public Triple getAgentLocation() {
        return body.getAgentLocation();
    }

    public Triple getAgentRotation() {
        return Triple.rotationToTriple(this.info.getRotation());
    }

    public AutoTraceRay getAutoTrace(int id) {
        return this.raycasting.getRay(id + "");
    }

    public ArrayList<Item> getKnownHealths() {
        Map<UnrealId, Item> candidates = this.items.getKnownPickups(Category.HEALTH);
        ArrayList<Item> result = new ArrayList<Item>();
        for (Item i : candidates.values()) {
            result.add(i);
        }
        return result;
    }

    public Weapon getCurrentWeapon() {
        return weaponry.getCurrentWeapon();
    }

    public ArrayList<Item> getKnownItems() {
        Map<UnrealId, Item> candidates = this.items.getKnownPickups();
        ArrayList<Item> result = new ArrayList<Item>();
        for (Item i : candidates.values()) {
            result.add(i);
        }
        return result;
    }

    /**
     * If not left, assume right
     **/
    public boolean sideWallClose(boolean left) {
        return body.sideWallClose(left);
    }

    public boolean backWallClose() {
        return body.backWallClose();
    }

    public boolean frontWallClose() {
        return body.frontWallClose();
    }

    public boolean hasWeaponOfType(ItemType weaponType) {
        return this.weaponry.hasWeapon(weaponType);
    }

    public ArrayList<AutoTraceRay> getAutoTraces() {
        if (raycasting.getAllRaysInitialized().getFlag()) {
            return this.body.rays;
        }
        return new ArrayList<AutoTraceRay>();
    }
    
    public AutoTraceRay frontRayTrace(){
        return body.frontRayTrace();
    }

    public Player getSeeEnemy() {
        Player result;
        try {
            result = this.players.getNearestVisibleEnemy();
        } catch (NullPointerException e) {
            // The Pogamut code has an error in getNearestVisibleEnemy that throws this exception
            result = null;
        }
        return result;
    }

    public Double distanceToNearestEnemy() {
        Player e = getSeeEnemy();
        Location agent = this.info.getLocation();
        if (e != null && e.getLocation() != null && agent != null) {
            return agent.getDistance(e.getLocation());
        } else {
            return null;
        }
    }

    public ArrayList<Item> seenHealths(int secondsHistory) {
        ArrayList<Item> candidates = getKnownHealths();
        ArrayList<Item> result = new ArrayList<Item>();
        for (Item h : candidates) {
            if ((this.info.getSelf().getSimTime() - h.getSimTime()) <= secondsHistory) {
                result.add(h);
            }
        }
        return result;
    }

    public UnrealId getAgentID() {
        return this.info.getId();
    }

    public Item getNearestWeapon() {
        return getNearestWeapon(new Triple(0, 0, 0));
    }

    /*
     * The offset is meant to represent something like the vector from the bot to
     * an enemy, namely something to be avoided. If the enemy is between the bot and
     * the item, then it will effectively be farther away.
     */
    public Item getNearestWeapon(Triple offset) {
        Map<UnrealId, Item> weapons = items.getVisibleItems(UT2004ItemType.Category.WEAPON);
        if (weapons == null || weapons.isEmpty()) {
            weapons = items.getSpawnedItems(UT2004ItemType.Category.WEAPON);
        }

        double distance = Double.MAX_VALUE;
        Item closest = null;
        if (weapons != null) {
            for (Item weapon : weapons.values()) {
                if (weapon.isDropped()) {
                    // Always pick up dropped weapons
                    closest = weapon;
                    break;
                }
                if (!weapon.getType().equals(UT2004ItemType.ONS_GRENADE_LAUNCHER)
                        && !weapon.getType().equals(UT2004ItemType.LINK_GUN)
                        && !weaponry.hasWeapon(weapon.getType())
                        && !weapon.getType().getCategory().equals(UT2004ItemType.Category.AMMO)) {

                    if (weapon.getLocation() == null || info.getLocation() == null) {
                        continue;
                    }
                    //double thisDistance = weapon.getLocation().getDistance(info.getLocation());
                    double thisDistance = Triple.distanceInSpace(Triple.add(Triple.locationToTriple(weapon.getLocation()), offset), info.getLocation());
                    if (thisDistance < distance) {
                        distance = thisDistance;
                        closest = weapon;
                    }
                }
            }
            return closest;
        }
        return null;
    }

    public Item getNearestUsableAmmo() {
        return getNearestUsableAmmo(new Triple(0, 0, 0));
    }

    public boolean isAboveMe(ILocated loc) {
        Location agent = info.getLocation();
        if (agent != null && loc != null && loc.getLocation() != null) {
            return (loc.getLocation().z - agent.getLocation().z > HIGH_GROUND_Z_DISTANCE);
        }
        return false;
    }

    public Player highestVisibleOpponent() {
        Collection<Player> enemies = players.getVisibleEnemies().values();
        Iterator<Player> itr = enemies.iterator();
        Player highest = null;
        while (itr.hasNext()) {
            Player current = itr.next();
            if (highest == null
                    || (current.getLocation() != null
                    && highest.getLocation() != null
                    && current.getLocation().z > highest.getLocation().z)) {
                highest = current;
            }
        }
        return highest;
    }

    public boolean opponentHasHighGround() {
        Player highest = highestVisibleOpponent();
        if (highest == null) {
            return false;
        }
        return isAboveMe(highest.getLocation());
    }

    public boolean botHasHighGround() {
        Player highest = highestVisibleOpponent();
        Location agentLoc = info.getLocation();
        return (agentLoc != null
                && highest != null
                && highest.getLocation() != null
                && (agentLoc.z - highest.getLocation().z > HIGH_GROUND_Z_DISTANCE));
    }

    /*
     * The offset is meant to represent something like the vector from the bot to
     * an enemy, namely something to be avoided. If the enemy is between the bot and
     * the item, then it will effectively be farther away.
     */
    public Item getNearestUsableAmmo(Triple offset) {
        Map<UnrealId, Item> ammos = items.getVisibleItems(UT2004ItemType.Category.AMMO);
        if (ammos == null || ammos.isEmpty()) {
            ammos = items.getSpawnedItems(UT2004ItemType.Category.AMMO);
        }

        double distance = Double.MAX_VALUE;
        Item closest = null;
        if (ammos != null) {
            for (Item ammo : ammos.values()) {
                if (!ammo.getType().equals(UT2004ItemType.LINK_GUN_AMMO)) {
                    Map<ItemType, Weapon> weapons = weaponry.getWeapons();
                    for (Weapon w : weapons.values()) {
                        ItemType type = w.getDescriptor().getPriAmmoItemType();
                        if (type.equals(ammo.getType())) {
                            if (ammo.getLocation() == null || info.getLocation() == null) {
                                continue;
                            }
                            //double thisDistance = ammo.getLocation().getDistance(info.getLocation());
                            double thisDistance = Triple.distanceInSpace(Triple.add(Triple.locationToTriple(ammo.getLocation()), offset), info.getLocation());
                            if (thisDistance < distance) {
                                distance = thisDistance;
                                closest = ammo;
                            }
                        }
                    }
                }
            }
            return closest;
        }
        return null;
    }

    public boolean isMoving() {
        return body.isMoving();
    }

    public double angleBetweenBotRotationAndVectorToLocation(ILocated loc) {
        Rotation rot = this.info.getRotation();
        return angleBetweenBotRotationAndVectorToLocation(info.getLocation(), rot, loc);
    }

    public static double angleBetweenBotRotationAndVectorToLocation(ILocated source, Rotation rot, ILocated loc) {
        Location locFromRot = rot.toLocation();
        double angle = Math.acos(loc.getLocation().sub(source.getLocation()).getNormalized().dot(locFromRot.getNormalized()));
        return angle;
    }

    public static boolean sourceIsFacingLocation(ILocated source, Rotation rot, ILocated loc) {
        return angleBetweenBotRotationAndVectorToLocation(source, rot, loc) < Math.toRadians(FACING_ANGLE_DEGREES_THRESHOLD);
    }

    public double signedAngleBetweenEnemyVelocityAndRouteToBot(Player p) {
        Location enemy = p.getLocation();
        Location bot = this.info.getLocation();
        Velocity vel = p.getVelocity();
        if (enemy != null && bot != null && vel != null) {
            Vector3d enemyToBot = Triple.locationToTriple(bot.sub(enemy)).normalize().getVector3d();
            Vector3d enemyMovement = Triple.velocityToTriple(vel).normalize().getVector3d();
            double angleDifference = enemyToBot.angle(enemyMovement);

            Vector3d positiveTurn = multMatrixVector(getTurnMatrix(angleDifference), enemyToBot);
            Vector3d negativeTurn = multMatrixVector(getTurnMatrix(-angleDifference), enemyToBot);

            if ((enemyMovement.angle(positiveTurn)) < (enemyMovement.angle(negativeTurn))) {
                return (-angleDifference);
            } else {
                return angleDifference;
            }
        }
        return Math.PI;
    }

    public static Vector3d multMatrixVector(Matrix3d m, Vector3d v) {
        return new Vector3d(m.getM00() * v.x + m.getM10() * v.y + m.getM20() * v.z,
                m.getM01() * v.x + m.getM11() * v.y + m.getM21() * v.z,
                m.getM02() * v.x + m.getM12() * v.y + m.getM22() * v.z);
    }

    public static Matrix3d getTurnMatrix(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);

        // Serious testing needed...
        Matrix3d matrix = new Matrix3d();

        matrix.setM00(c);
        matrix.setM01(0);
        matrix.setM02(-s);

        matrix.setM10(0);
        matrix.setM11(1);
        matrix.setM12(0);

        matrix.setM20(s);
        matrix.setM21(0);
        matrix.setM22(c);

        return matrix;

    }

    public boolean isAdvancing(Player p) {
        if (!isMovingInPlane(p) || isAboveMe(p.getLocation())) {
            return false;
        }
        double angle = Math.abs(signedAngleBetweenEnemyVelocityAndRouteToBot(p));
        // If movement is no more than 45 degrees off course from me,
        // then opponent is advancing.
        return angle < Math.PI / 4.0;
    }

    public boolean isRetreating(Player p) {
        if (!isMovingInPlane(p) || isAboveMe(p.getLocation())) {
            return false;
        }
        double angle = Math.abs(signedAngleBetweenEnemyVelocityAndRouteToBot(p));
        // If movement is more than 135 degrees off course from me,
        // then opponent is retreating.
        return angle > (3.0 * Math.PI) / 4.0;
    }

    public boolean isStrafing(Player p, boolean left) {
        if (!isMovingInPlane(p) || isAboveMe(p.getLocation())) {
            return false;
        }
        double angle = signedAngleBetweenEnemyVelocityAndRouteToBot(p);
        return (left && angle > Math.PI / 4.0 && angle < (3.0 * Math.PI) / 4.0)
                || (!left && angle < -Math.PI / 4.0 && angle > -(3.0 * Math.PI) / 4.0);
    }

    public static boolean isStill(Player p) {
        return (p.getVelocity() != null ? p.getVelocity().isZero() : true);
    }

    public static boolean isJumping(Player p) {
        Velocity v = p.getVelocity();
        if (v != null) {
            return Math.abs(v.z) > 50; // Magic number!!!
        }
        return false;
    }

    public static boolean isMovingInPlane(Player p) {
        Velocity v = p.getVelocity();
        return v != null && Math.abs(v.x) > 1 && Math.abs(v.y) > 1;
    }

    public void setJudgingTarget(Player judgeTarget) {
        this.judgeTarget = judgeTarget;
    }

    /**
     * Whether the otherLoc is to the right of the bot's current view
     * @param otherLoc
     * @return true if to the right, false if to the left, null if in front or unknown
     */
    public Boolean isToMyRight(Location otherLoc) {
        Location bot = this.info.getLocation();
        Rotation rot = this.info.getRotation();
        if (otherLoc != null && bot != null) {
            Vector3d botToEnemy = Triple.locationToTriple(otherLoc.sub(bot)).normalize().getVector3d();
            Vector3d botFacing = Triple.locationToTriple(rot.toLocation()).normalize().getVector3d();
            double angleDifference = botFacing.angle(botToEnemy);

            // If the bot is basically facing the player, then null result indicates this
            if (Math.toRadians(AgentInfo.IS_FACING_ANGLE) > angleDifference) {
                return null;
            }

            Vector3d positiveTurn = multMatrixVector(getTurnMatrix(angleDifference), botFacing);
            Vector3d negativeTurn = multMatrixVector(getTurnMatrix(-angleDifference), botFacing);

            return ((botToEnemy.angle(positiveTurn)) < (botToEnemy.angle(negativeTurn)));
        }
        return null;
    }

    public Boolean isFacingMe(Player enemy) {
        return isFacingMe(enemy, AgentInfo.IS_FACING_ANGLE);
    }

    public Boolean isFacingMe(Player enemy, double degrees) {
        if (enemy == null || enemy.getLocation() == null || enemy.getRotation() == null) {
            return null;
        }
        Location directionVector = info.getLocation().sub(enemy.getLocation()).getNormalized();
        Location agentFaceVector = enemy.getRotation().toLocation().getNormalized();

        if (Math.acos(directionVector.dot(agentFaceVector)) <= Math.toRadians(degrees)) {
            return true;
        }

        return false;
    }

    public boolean isThreatening(Player enemy) {
        return isThreatening(enemy, FACING_TO_SHOOT_DEGREES);
    }

    public boolean isThreatening(Player enemy, double facingDegrees) {
        if (enemy == null) {
            return false;
        }

        // Player is threatening if it damaged me recently
        BotDamaged damaged = senses.getLastDamage();
        if (damaged != null && damaged.getInstigator() != null && damaged.getInstigator().equals(enemy.getId())) {
            double currentTime = info.getTime();
            double lastDamagedTime = damaged.getSimTime();
            if ((currentTime - lastDamagedTime) < TIME_UNTIL_SAFE) {
            	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
            		System.out.println(enemy.getName() + " damaged me " + (currentTime - lastDamagedTime) + " ago");
            	}
                return true;
            }
        }
        // Invisible players cannot threaten
        if (!enemy.isVisible()) {
            return false;
        }
        // Enemy is threatening if shooting while facing me
        if (isShootingAtMe(enemy, facingDegrees)) {
            return true;
        }
        return false;
    }

    public boolean isShootingAtMe(Player enemy) {
        return isShootingAtMe(enemy, FACING_TO_SHOOT_DEGREES);
    }

    public boolean isShootingAtMe(Player enemy, double facingDegrees) {
        boolean isShooting = enemy.getFiring() != 0;
        boolean isFacingMe = isFacingMe(enemy, facingDegrees);
        return (isShooting && isFacingMe);
    }

    public boolean isMoving(Player enemy) {
        Velocity enemyVol = enemy.getVelocity();
        return (enemyVol == null) ? false : !enemyVol.isPlanarZero() && !enemyVol.equals(new Velocity(0, 0, 0), 20);
    }

    public boolean isThreatened() {
        if (senses.seeIncomingProjectile()) {
            IncomingProjectile ip = senses.getLastIncomingProjectile();
            if (info.getLocation().getDistance(ip.getLocation()) < CLOSE_PROJECTILE_DISTANCE) {
                return true;
            }
        }

//        Map<UnrealId, Player> enemies = players.getVisibleEnemies();
//        for (Player p : enemies.values()) {
//            if (p != null
//                    && (info.getLocation().getDistance(p.getLocation()) < CLOSE_ENEMY_DISTANCE
//                    || isFacingMe(p, FACING_TO_SHOOT_DEGREES))) {
//                return true;
//            }
//        }

        double currentTime = info.getTime();
        BotDamaged damage = senses.getLastDamage();
        if(damage == null || (damage.getInstigator() != null && damage.getInstigator().equals(info.getId()))){
            return false;
        }
        double lastDamagedTime = damage.getSimTime();

        return (currentTime - lastDamagedTime) < TIME_UNTIL_SAFE;
    }

    public static boolean isBeneath(ILocated lower, ILocated higher) {
        if (higher != null && higher.getLocation() != null
                && lower != null && lower.getLocation() != null) {
            return higher.getLocation().z - lower.getLocation().z > Constants.NEAR_ITEM_HEIGHT.getInt();
        }
        return false;
    }

    public boolean isBeneathMe(ILocated loc) {
        return isBeneath(loc, info.getFloorLocation());
    }

    public Triple getLongestTraceToWall() {
        double maxDistance = 0;
        Triple direction = null;
        Location agent = info.getLocation();
        if (agent == null) {
            return null;
        }
        for (Integer rayID : body.levelRays) {
            AutoTraceRay ray = body.rays.get(rayID);
            double distance = (ray == null || !ray.isResult() ? 0 : agent.getDistance(ray.getHitLocation()));
            if (distance > maxDistance) {
                maxDistance = distance;
                direction = Triple.locationToTriple(ray.getTo().sub(ray.getFrom())).normalize();
            }
        }
        return direction;
    }

    public double getShortestTraceToWallDistance() {
        double minDistance = Double.MAX_VALUE;
        Location agent = info.getLocation();
        if (agent == null) {
            return 0;
        }
        for (Integer rayID : body.levelRays) {
            AutoTraceRay ray = body.rays.get(rayID);
            double distance = (ray == null || !ray.isResult() ? 0 : agent.getDistance(ray.getHitLocation()));
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    public boolean changeWeapon(UT2004ItemType weaponType) {
        Weapon current = weaponry.getCurrentWeapon();
        if (current == null || !current.getType().equals(weaponType)) {
            //System.out.println("\tChange weapon to: " + weaponType.getName());
            weaponry.changeWeapon(weaponType);
            //System.out.println("\tWeapon is: " + weaponry.getCurrentWeapon().getType().getName());
            return true;
        }
        return false;
    }

    private Vector<NavPoint> getLiftCenters() {
        Map<WorldObjectId, NavPoint> navs = world.getAll(NavPoint.class);
        Vector<NavPoint> result = new Vector<NavPoint>();
        for (NavPoint np : navs.values()) {
            if (np.isLiftCenter()) {
                result.add(np);
            }
        }
        return result;
    }
    
    private Vector<NavPoint> getLiftExits() {
        Map<WorldObjectId, NavPoint> navs = world.getAll(NavPoint.class);
        Vector<NavPoint> result = new Vector<NavPoint>();
        for (NavPoint np : navs.values()) {
            if (np.isLiftExit()) {
                result.add(np);
            }
        }
        return result;
    }

    public boolean underElevator() {
        // Special case for nasty elevator in Curse4
        String map = game.getMapName();
        Location bot = info.getLocation();
        if (map.toUpperCase().equals("DM-CURSE4") && bot != null) {
            if (       950 < bot.x && bot.x < 1215
                    && -1252 < bot.y && bot.y < -1085
                    && -126 < bot.z && bot.z < -90) {
            	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
            		System.out.println("In Curse4 Elevator Box");
            	}
                return true;
            }
        }
        Vector<NavPoint> navs = getLiftCenters();
        if (navs.isEmpty()) {
            return false;
        }
        if (bot != null) {
            NavPoint nearest = DistanceUtils.getNearest(navs, bot);
            return elevatorAbove(bot, nearest.getLocation());
        }
        return false;
    }
    
    public boolean nearElevator(){
        Location bot = info.getLocation();
        Vector<NavPoint> navs = getLiftCenters();
        Vector<NavPoint> exits = getLiftExits();
        navs.addAll(exits);
        if (navs.isEmpty()) {
            return false;
        }
        if (bot != null) {
            NavPoint nearest = DistanceUtils.getNearest(navs, bot);
            boolean result = nearest.getLocation().getDistance(bot) < 300;
            if(result) {
            	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
            		System.out.println("\tToo close to elevator to RETRACE");
            	}
            }
            return result;
        }
        return false;
    }

    public boolean elevatorAbove(Location lower, Location upper) {
        if (lower != null && upper != null) {
            return (upper.z - lower.z > 30) && (lower.getDistance2D(upper) < 300);
        }
        return false;
    }

    public NavPoint nearestLiftExit() {
        // Be careful not to destroy knowledge of the nav graph
        Collection<NavPoint> original = world.getAll(NavPoint.class).values();
        Collection<NavPoint> navs = new ArrayList<NavPoint>();
        navs.addAll(original);
        Iterator<NavPoint> itr = navs.iterator();
        while (itr.hasNext()) {
            if (!itr.next().isLiftExit()) {
                itr.remove();
            }
        }
        Location bot = info.getLocation();
        if (!navs.isEmpty() && bot != null) {
            return DistanceUtils.getNearest(navs, bot);
        }
        return null;
    }

    public void updatePosition() {
        lastPosition = info.getLocation();
    }

    public void stopPathExecutors() {
        itemPathExecutor.stop();
        playerPathExecutor.stop();
    }

    public synchronized void targetDies(UnrealId dead) {
        if (dead != null) {
            if (lastCombatTarget != null && dead.equals(lastCombatTarget.getId())) {
                lastCombatTarget = null;
            }
            if (judgeTarget != null && dead.equals(judgeTarget.getId())) {
                judgeTarget = null;
            }
        }
    }

    public ItemType enemyWeaponType(Player target) {
        if (target == null) {
            return null;
        }
        String weapon = target.getWeapon() + "Pickup";
        ItemType enemyWeaponType = UT2004ItemType.getItemType(weapon);
        return enemyWeaponType;
    }

    public boolean enemyIsSniping(Player p) {
        if (p == null) {
            return false;
        }
        return body.isSnipingWeapon(enemyWeaponType(p));
    }

    public int numVisibleOpponents() {
        Map<UnrealId, Player> map = this.players.getVisibleEnemies();
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public boolean facingNavPoint(String id, double angle) {
        NavPoint navTarget = getNavPoint(id);
        if(navTarget == null){
            return false;
        }
        return info.isFacing(navTarget.getLocation(), angle);
    }

    public NavPoint getNavPoint(String id) {
        Map<WorldObjectId, NavPoint> navs = world.getAll(NavPoint.class);
        return navs.get(UnrealId.get(id));
    }
}
