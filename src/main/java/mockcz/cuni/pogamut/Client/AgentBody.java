package mockcz.cuni.pogamut.Client;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.ut2004.agent.module.logic.SyncUT2004BotLogic;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Game;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.flag.FlagListener;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import java.util.ArrayList;
import java.util.Map;
import javax.vecmath.Vector3d;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import utopia.Utils;

/**
 *
 * @author HeDeceives
 */
public class AgentBody {

    public static final double MAX_DISTANCE_NOISE = 5; //3.0;
    public final CompleteBotCommandsWrapper body;
    public final Raycasting raycasting;
    public ArrayList<String> autoTraceIds;
    public ArrayList<AutoTraceRay> rays;
    public final IAct act;
    private boolean pendingAutoTraceEnable;
    public final SyncUT2004BotLogic logic;
    public final AgentInfo info;
    public final Senses senses;
    public final Game game;
    public final IVisionWorldView world;
    public final Items items;
    public final Weaponry weaponry;
    private int shockOrbsToBeams = 0;
    public static final double CLOSE_WALL_DISTANCE = 150;
    public double lastDodgeTime = 0;

    public AgentBody(CompleteBotCommandsWrapper body, Raycasting raycasting, IAct act, SyncUT2004BotLogic logic, AgentInfo info, Senses senses, Game game, IVisionWorldView world, Items items, Weaponry weaponry) {
        this.body = body;
        this.raycasting = raycasting;
        this.act = act;
        this.autoTraceIds = new ArrayList<String>();
        this.rays = new ArrayList<AutoTraceRay>();
        this.pendingAutoTraceEnable = false;
        this.logic = logic;
        this.info = info;
        this.senses = senses;
        this.game = game;
        this.world = world;
        this.items = items;
        this.weaponry = weaponry;
    }

    public boolean isGetCloseWeapon(Weapon w) {
        return w != null && (w.getType().equals(UT2004ItemType.ASSAULT_RIFLE) || w.getType().equals(UT2004ItemType.FLAK_CANNON) || w.getType().equals(UT2004ItemType.BIO_RIFLE));
    }

    public boolean isSecondaryChargingWeapon(Weapon w) {
        return w != null && (w.getType().equals(UT2004ItemType.ASSAULT_RIFLE) || w.getType().equals(UT2004ItemType.ROCKET_LAUNCHER) || w.getType().equals(UT2004ItemType.BIO_RIFLE));
    }

    public boolean isAutomaticWeaponShooting(Weapon w) {
        return w != null
                && ((w.getType().equals(UT2004ItemType.ASSAULT_RIFLE) && info.isPrimaryShooting())
                || (w.getType().equals(UT2004ItemType.MINIGUN) && info.isShooting()));
    }

    public boolean usingSnipingWeapon() {
        return isSnipingWeapon(weaponry.getCurrentWeapon());
    }

    public boolean isSnipingWeapon(Weapon w) {
        return w != null && isSnipingWeapon(w.getType());
    }

    boolean isSnipingWeapon(UT2004ItemType type) {
        return type != null && (type.equals(UT2004ItemType.SNIPER_RIFLE) || type.equals(UT2004ItemType.LIGHTNING_GUN));
    }

    public boolean isSplashDamageWeapon(Weapon w) {
        return w != null && (w.getType().equals(UT2004ItemType.ROCKET_LAUNCHER) || w.getType().equals(UT2004ItemType.FLAK_CANNON) || w.getType().equals(UT2004ItemType.SHOCK_RIFLE) || w.getType().equals(UT2004ItemType.BIO_RIFLE));
    }
    
    public boolean usingSplashDamageWeapon() {
        return isSplashDamageWeapon(weaponry.getCurrentWeapon());
    }

    public void shootAlternate(Triple shootTarget) {
        //System.out.println("ALT SHOOT " + shootTarget);
        this.body.getShooting().shootSecondary(shootTarget);
    }

    // Main shooting method
    public void shootAlternate(Player enemy, boolean perfect, Triple adjust, double chargeTime, double aimVelDiffDistortion, double aimRandomDistortion) {
        Weapon w = weaponry.getCurrentWeapon();
        if (w != null && w.getType().equals(UT2004ItemType.SHOCK_RIFLE)) {
            this.shockOrbsToBeams++;
        }
        if (isSecondaryChargingWeapon(w)) {
            if (info.isSecondaryShooting()) {
                // will discharge in own time
                return;
            } else {
                //System.out.println("Start charging");
                this.body.getShooting().shootSecondaryCharged(enemy, chargeTime);
            }
        }
        targetCheck(enemy);
        if (perfect) {
            //System.out.println("ALT SHOOT [PERFECT] " + enemy.getName());
            this.body.getShooting().shootSecondary(enemy);
        } else if (adjust == null) {
            shootAlternate(getImperfectTarget(enemy, aimVelDiffDistortion, aimRandomDistortion));
        } else {
            Triple target = Triple.locationToTriple(enemy.getLocation());
            shootAlternate(Triple.add(target, adjust));
        }
    }

    public double verticalAngleToTarget(Location target) {
        Location bot = info.getLocation();
        Location toTarget = target.sub(bot);
        Vector3d vectorToTarget = new Vector3d(toTarget.x, toTarget.y, toTarget.z);
        Location targetLevel = new Location(target.x, target.y, bot.z);
        Location toLevel = targetLevel.sub(bot);
        Vector3d vectorToLevel = new Vector3d(toLevel.x, toLevel.y, toLevel.z);
        // in radians
        return vectorToLevel.angle(vectorToTarget);
    }

    public void shoot(Triple shootTarget) {
        //System.out.println("PRI SHOOT " + shootTarget);
        this.body.getShooting().shootPrimary(shootTarget);
    }

    // Main shooting method
    public void shoot(Player enemy, boolean perfect, Triple adjust, double aimVelDiffDistortion, double aimRandomDistortion) {
        Weapon w = weaponry.getCurrentWeapon();
        if (w != null && w.getType().equals(UT2004ItemType.SHOCK_RIFLE)) {
            this.shockOrbsToBeams--;
        }
        if (isSecondaryChargingWeapon(w) && info.isSecondaryShooting()) {
            // will discharge in own time
            return;
        }
        targetCheck(enemy);
        if (perfect) {
            //System.out.println("PRI SHOOT [PERFECT] " + enemy.getName());
            this.body.getShooting().shootPrimary(enemy);
        } else if (adjust == null) {
            shoot(getImperfectTarget(enemy, aimVelDiffDistortion, aimRandomDistortion));
        } else {
            Triple target = getImperfectTarget(enemy, aimVelDiffDistortion, aimRandomDistortion);
            shoot(Triple.add(target, adjust));
        }
    }

    private Triple getImperfectTarget(Player enemy, double aimVelDiffDistortion, double aimRandomDistortion) {
        Triple agent = Triple.locationToTriple(info.getLocation());
        Triple target = Triple.locationToTriple(enemy.getLocation());
        double distance = (agent == null ? WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE + 1 : Triple.distanceInSpace(agent, target));
        // Add noise to target
        Velocity enemyVelocity = enemy.getVelocity();
        Velocity myVelocity = info.getVelocity();
        //System.out.println("Perfect target: " + target);
        if (enemyVelocity != null && myVelocity != null && !enemyVelocity.isZero(5)) {
            double distanceNoiseMultiplier = noiseForDistance(distance);
            //System.out.println(enemyVelocity + " " + myVelocity);
            target.x += (aimRandomDistortion * Utils.randposneg() * Utils.randomFloat() * distanceNoiseMultiplier) + (Utils.randposneg() * Utils.randomFloat() * distanceNoiseMultiplier * ((Math.abs(enemyVelocity.x - myVelocity.x)) / aimVelDiffDistortion));
            target.y += (aimRandomDistortion * Utils.randposneg() * Utils.randomFloat() * distanceNoiseMultiplier) + (Utils.randposneg() * Utils.randomFloat() * distanceNoiseMultiplier * ((Math.abs(enemyVelocity.y - myVelocity.y)) / aimVelDiffDistortion));
            if(!this.usingSplashDamageWeapon()){
                target.z += (aimRandomDistortion * Utils.randomFloat() * distanceNoiseMultiplier) + (Utils.randposneg() * Utils.randomFloat() * distanceNoiseMultiplier * ((Math.abs(enemyVelocity.z - myVelocity.z)) / aimVelDiffDistortion));
            }
        }
        // Add enemy velocity to anticipate
        //target.add(enemyVelocity);
        //System.out.println((target.x - enemy.getLocation().x)+","+(target.y - enemy.getLocation().y)+","+(target.z - enemy.getLocation().z));
        // This magnitude seems too big
        return target;
    }

    private double noiseForDistance(double distance) {
        if (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE) {
            return MAX_DISTANCE_NOISE;
        }
        return ((distance * distance) / (WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE * WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE)) * MAX_DISTANCE_NOISE;
    }

    public void stopShoot() {
        stopShoot(false);
    }

    public void stopShoot(boolean force) {
        // Only stop shooting if not charging, unless force is used
        Weapon w = weaponry.getCurrentWeapon();
        if (force || !(isSecondaryChargingWeapon(w) && info.isSecondaryShooting() && weaponry.getCurrentAlternateAmmo() > 1)) {
            //new NullPointerException().printStackTrace();
            this.body.getShooting().stopShoot();
            //this.body.getShooting().stopShooting();
        }
    }

    public void stop() {
        this.body.getLocomotion().stopMovement();
    }

    public void turnHorizontal(int i) {
        this.body.getLocomotion().turnHorizontal(i);
    }

    public void strafeToLocation(Triple target, Triple lookAt, double DUMMY) {
        this.body.getLocomotion().strafeTo(target, lookAt);
    }

    public void contMove() {
        contMove(0f);
    }

    public void contMove(float DUMMY) {
        this.body.getLocomotion().moveContinuos();
    }

    public void removeAllRaysFromAutoTrace() {
        act.act(new RemoveRay("All"));
    }

    /**
     * If not left, assume right
     **/
    public boolean sideWallClose(boolean left) {
        if (leftRayID == null || rightRayID == null) {
            return false;
        }

        int id = left ? leftRayID : rightRayID;
        return closeWallAlongRay(id);
    }

    public boolean backWallClose() {
        if (backRayID == null) {
            return false;
        }
        return closeWallAlongRay(backRayID);
    }

    public boolean frontWallClose() {
        if (frontRayID == null) {
            return false;
        }
        return closeWallAlongRay(frontRayID);
    }

    public boolean veryCloseFrontWall(){
        if (frontRayID == null) {
            return false;
        }
        return closeWallAlongRay(frontRayID,CLOSE_WALL_DISTANCE/3.0);
    }

    private boolean closeWallAlongRay(int id){
        return closeWallAlongRay(id,CLOSE_WALL_DISTANCE);
    }

    public AutoTraceRay frontRayTrace(){
        if (frontRayID == null) {
            return null;
        }
        if (!raycasting.getAllRaysInitialized().getFlag()) {
            return null;
        }
        return rays.get(frontRayID);
    }
    
    private boolean closeWallAlongRay(int id, double distance) {
        if (!raycasting.getAllRaysInitialized().getFlag()) {
            return false;
        }
        AutoTraceRay trace = rays.get(id);
        if (!trace.isResult()) {
            return false;
        }
        double result = Triple.distanceInSpace(Triple.locationToTriple(info.getLocation()), Triple.locationToTriple(trace.getHitLocation()));
        return result < distance;
    }
    private Integer leftRayID = null;
    private Integer rightRayID = null;
    private Integer backRayID = null;
    private Integer frontRayID = null;
    protected ArrayList<Integer> levelRays = new ArrayList<Integer>();

    public void addRayToAutoTrace(int id, Triple triple, double rayLength, boolean bFastTrace, boolean bTraceActors) {
        //Floor correction is always false
        if (triple.z == 0) {
            levelRays.add(id);
            if (triple.epsilonEquals(new Triple(1, 0, 0), 0.0001)) {
                frontRayID = id;
            } else if (triple.epsilonEquals(new Triple(0, 1, 0), 0.0001)) {
                rightRayID = id;
            } else if (triple.epsilonEquals(new Triple(-1, 0, 0), 0.0001)) {
                backRayID = id;
            } else if (triple.epsilonEquals(new Triple(0, -1, 0), 0.0001)) {
                leftRayID = id;
            }
        }
        boolean bFloorCorrection = false;
        String stringID = id + "";
        autoTraceIds.add(stringID);
        rays.add(null);
        this.raycasting.createRay(stringID, triple.getVector3d(), (int) rayLength, bFastTrace, bFloorCorrection, bTraceActors);
    }

    public void setAutoTrace(boolean bEnable) {
        this.pendingAutoTraceEnable = bEnable;
    }

    public void finalAutoTraceDecision() {
        if (this.pendingAutoTraceEnable) {
            //Dummy trace added because of ridiculous bug in Pogamut 3 that freezes things up
            //if all autotraces are accessed. Therefore, this trace is made in order to never be
            //accessed.
            this.raycasting.createRay("DUMMY", new Vector3d(0, 0, 0), 0, false, false, false);

            raycasting.getAllRaysInitialized().addListener(new FlagListener<Boolean>() {

                @Override
                public void flagChanged(Boolean changedValue) {
                    for (int i = 0; i < autoTraceIds.size(); i++) {
                        rays.set(i, raycasting.getRay(autoTraceIds.get(i)));
                    }
                }
            });
            raycasting.endRayInitSequence();

            act.act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));
        } else {
            act.act(new Configuration().setAutoTrace(false));
        }
    }

    public void dodge(Triple triple) {
        dodge(triple, false);
    }

    public void dodge(Triple triple, boolean force) {
        if (force || (game.getTime() - lastDodgeTime) > AgentMemory.MIN_TIME_BETWEEN_DODGES) {
            lastDodgeTime = game.getTime();
            this.body.getLocomotion().dodge(triple.getLocation(),false);
        }
    }

    public void runToTarget(Player enemy) {
        this.body.getLocomotion().moveTo(enemy);
    }

    public boolean isMoving() {
        try {
            Velocity v = info.getVelocity();
            //return !v.isPlanarZero() && !v.equals(new Velocity(0, 0, 0), 20);
            return v.size() > 50 && !v.isPlanarZero();
        } catch (NullPointerException npe) {
            //System.out.println("Strange NullPointerException ignored in AgentBody.isMoving()");
            return false;
        }
    }

    public void jump(boolean doubleJump, double delay, double force) {
        if (isMoving()) {
            //if(UT2.printActions) System.out.println("JUMP: doubleJump:" + doubleJump + ", delay:" + delay + ", force:" + force);
            this.body.getLocomotion().jump(doubleJump, delay, force);
        }
    }

    public void jump() {
        if (isMoving()) {
            this.body.getLocomotion().jump();
        }
    }

    public void doubleJump() {
        if (isMoving()) {
            this.body.getLocomotion().doubleJump();
        }
    }

    public void doubleJump(double delay, double jumpZ) {
        if (isMoving()) {
            this.body.getLocomotion().doubleJump(delay, jumpZ);
        }
    }

    public NavPoint getNearbyNav(Location loc) {
        double nearestDistance = Double.MAX_VALUE;
        NavPoint bestNav = null;
        Map<WorldObjectId, NavPoint> navPoints = world.getAll(NavPoint.class);
        // Merge pseudo navpoints
        for (NavPoint np : navPoints.values()) {
            double navDis = np.getLocation().getDistance(loc);
            if (navDis > 100 && navDis < nearestDistance) {
                nearestDistance = navDis;
                bestNav = np;
            }
        }
        return bestNav;
    }

    private void targetCheck(Player enemy) {
        if (!enemy.isVisible() && (this.info.isShooting() || this.info.isSecondaryShooting() || this.info.isPrimaryShooting())) {
            System.out.println("Stop shoot target check");
            this.stopShoot();
        }
    }

    public boolean forceShockBeam() {
        return shockOrbsToBeams >= 2;
    }

    public Triple getAgentLocation() {
        return Triple.locationToTriple(this.info.getLocation());
    }
}
