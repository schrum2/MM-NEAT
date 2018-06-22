package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.parameters.Parameters;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import utopia.controllers.scripted.ShieldGunController;

@SuppressWarnings("serial")
/**
 * 
 * @author Jacob Schrum
 */
public abstract class OpponentRelativeAction extends Action {

    public static final double AIM_VELOCITY_DIFFERENCE_DISTORTION = 40.0; // Higher values make aim better
    public static final double AIM_RANDOM_DISTORTION = 0.75; // Higher values make aim worse
    public static final double DODGE_CHANCE = 0.15;
    public static final double SHOOT_FACING_ALLOWANCE = 30;
    private static final double MAX_SHOOT_ANGLE = 70;
    private static double RECENT_DODGE_TIME = 0.05;
    private static final double SHOCK_ABORT_CHANCE = 0.35;
    protected final AgentMemory memory;
    protected boolean shoot;
    protected boolean secondary;
    public static final double DO_NOT_SHOOT_FAR_CHANCE = 0.9;
    public static final double JUMP_NEAR_WALL_TOLERANCE = 200;
    protected boolean jump;
    protected boolean observing;

    /**
     * initializes the action's
     * 
     * @param memory (the agent memory to use for access)
     * @param shoot (should the bot shoot)
     * @param secondary (should the bot use secondary firing mode)
     * @param jump (should the bot jump)
     */
    public OpponentRelativeAction(AgentMemory memory, boolean shoot, boolean secondary, boolean jump) {
        this.memory = memory;
        this.shoot = shoot;
        this.secondary = secondary;
        this.jump = jump;
        this.observing = false;
    }

    /**
     * initializes the action without jumping, bot assumes it SHOULD NOT jump
     * 
     * @param memory (the agent memory to use for access)
     * @param shoot (should the bot shoot)
     * @param secondary (should the bot use secondary firing mode)
     */
    public OpponentRelativeAction(AgentMemory memory, boolean shoot, boolean secondary) {
        this(memory, shoot, secondary, false);
    }

    /**
     * tells the bot to look at an enemy and NOT shoot
     */
    public void observe(){
        this.observing = true;
        this.shoot = false;
    }

    /**
     * shoot if the bot is NOT observing, call a more complicated shootDecision method
     * 
     * @param enemy (opponent to shoot)
     * @return (returns whether or not the bot will shoot)
     */
    public boolean shootDecision(Player enemy) {
        if(observing) return false;
        return shootDecision(memory, enemy, this.shoot, this.secondary);
    }

    /**
     * adds a random disturbance to the bot's aim with weapons that fire in arcs, so it isn't always killing 
     * everyone perfectly with a hard to aim weapon
     * 
     * @param enemy (enemy the bot is shooting at)
     * @param agent (the bot shooting)
     * @return returns a slight disturbance so the bot doesn't have perfect aim
     */
    private static Triple lobAdjustment(Player enemy, Triple agent) {
        Triple target = Triple.locationToTriple(enemy.getLocation());
        return Triple.subtract(agent, target).multiplyByNumber((Math.random() * 0.3) + 0.3);
    }

    /**
     * helps the bot lead an opponent for projectiles that take time to reach their target
     * 
     * @param enemy (enemy the bot is shooting at)
     * @param agent (the bot shooting)
     * @return returns where the bot should shoot
     */
    private static Triple slowAdjustment(Player enemy, Triple agent) {
        Triple adjust = Triple.velocityToTriple(enemy.getVelocity()).multiplyByNumber((Math.random() * 0.2) + 0.8);
        if (enemy.getLocation().z > agent.z) {
            adjust.z = 0;
        } else {
            adjust.z -= 5;
        }
        return adjust;
    }

    /**
     * Decides whether or not the bot should shoot
     * 
     * @param memory (agent memory to be used)
     * @param enemy (opponent the bot is looking at)
     * @param shoot (is the bot shooting)
     * @param secondary (should the bot use secondary firing mode)
     * @return
     */
    public static boolean shootDecision(AgentMemory memory, Player enemy, boolean shoot, boolean secondary) {
        double distance = enemy.getLocation().getDistance(memory.info.getLocation());
        double shootingAllowanceAngle = SHOOT_FACING_ALLOWANCE;
        Weapon w = memory.weaponry.getCurrentWeapon();
        boolean forceNewFire = false;
        boolean perfect = false;
        double chargeTime = 0;
        boolean forcedHoldFire = false;
        double aimVelDiffDistortion = AIM_VELOCITY_DIFFERENCE_DISTORTION;
        double aimRandomDistortion = AIM_RANDOM_DISTORTION;
        Triple adjust = null;
        if (w != null) {
            ItemType type = w.getType();
            // Potentially revise primary/secondary choice based on weapon
            // and deal with charging issues. Some weapons may even choose
            // not to fire if too far away.
            if (type.equals(UT2004ItemType.SHIELD_GUN)) {
                perfect = true;
                if (distance < ShieldGunController.SHIELD_RUSH_RANGE) {
                    secondary = false;
                    shoot = true;
                } else {
                    secondary = true;
                    shoot = enemy.getFiring() != 0 || memory.senses.seeIncomingProjectile() || memory.weaponry.getSecondaryWeaponAmmo(type) > 20;
                    forceNewFire = !memory.info.isSecondaryShooting();
                    if(shoot && forceNewFire){
                        memory.body.body.getShooting().shootSecondary(enemy);
                        return true;
                    }
                }
            } else if (type.equals(UT2004ItemType.ASSAULT_RIFLE)) {
                if (distance >= WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE) {
                    // Don't waste primary shots from so far away
                    memory.body.stopShoot();
                    shoot = false;
                    forcedHoldFire = true;
                    return false;
                }
                // Ammo required
                int altAmmo = memory.weaponry.getCurrentAlternateAmmo();
                if (altAmmo > 0 && (memory.weaponry.getCurrentPrimaryAmmo() == 0 || enemy.getVelocity().isZero(20))) {
                    secondary = true;
                } else if (altAmmo == 0) {
                    secondary = false;
                } else {
                    secondary = (Math.random() < 0.25);
                }
                if (memory.info.isSecondaryShooting()) {
                    // Let loose grenade
                    memory.body.stopShoot(true);
                    return true;
                } else if (memory.info.isPrimaryShooting()) {
                    // Continue automatic fire
                    secondary = false;
                    //shoot = true;
                }

                if (secondary) {
                    // Lob grenades at nearer target
                    adjust = lobAdjustment(enemy, memory.getAgentLocation());
                } else {
                    shootingAllowanceAngle = 90;
                    forceNewFire = true;
                    perfect = Math.random() < 0.2 ? true : false;
                }
            } else if (type.equals(UT2004ItemType.SHOCK_RIFLE)) {
                // Mix up alt and primary
                aimRandomDistortion += Math.random() * 0.25;
                forceNewFire = true;
                secondary = !memory.body.forceShockBeam() && (Math.random() < 0.5);
                if (secondary) {
                    // Slow projectiles need to aim where the enemy is headed
                    adjust = slowAdjustment(enemy, memory.getAgentLocation());
                }
                if(distance > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE && Math.random() < SHOCK_ABORT_CHANCE){
                    shoot = false;
                } else if(Math.random() < 0.5){
                    shoot = false;
                }
            } else if (type.equals(UT2004ItemType.LINK_GUN)) {
                shootingAllowanceAngle = 90;
                aimRandomDistortion = 0.1;
                aimVelDiffDistortion = 50;
                forceNewFire = false; //true;
                if (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE / 2
                        || !memory.judgingGunReady()) {
                    // Don't waste shots from so far away
                    memory.body.stopShoot();
                    return false;
                }
                perfect = Math.random() < 0.5 ? true : false;
                if(!perfect){
                    // Link gun can be slow
                    adjust = slowAdjustment(enemy, memory.getAgentLocation());
                }
            } else if (type.equals(UT2004ItemType.BIO_RIFLE)) {
                chargeTime = 3.7;

                // Continue doing the same thing
                if (memory.info.isPrimaryShooting() && distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 3) {
                    secondary = false;
                    shoot = true;
                } else {
                    // Need enough ammo for secondary fire
                    secondary = memory.weaponry.getSecondaryWeaponAmmo(UT2004ItemType.BIO_RIFLE) >= 9
                            && (Math.random() < 0.4);
                }
                // Lob blobs at nearer target
                adjust = lobAdjustment(enemy, memory.getAgentLocation());
                if (Math.random() < 0.3) {
                    perfect = true;
                }
                if (!secondary) {
                    forceNewFire = true;
                }

                if (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2) {
                    if (memory.info.isSecondaryShooting()) {
                        // Release the blob
                        memory.body.stopShoot(true);
                        return true;
                    }
                    shoot = true;
                    secondary = false;
                } else if (distance >= (WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE / 2)) {
                    // Don't waste primary fire shots from so far away
                    memory.body.stopShoot();
                    shoot = false;
                    forcedHoldFire = true;
                }
            } else if (type.equals(UT2004ItemType.MINIGUN)) {
                shootingAllowanceAngle = 90;
                if (distance >= WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE && memory.isMoving()) {
                    // Don't waste shots from so far away
                    memory.body.stopShoot();
                    shoot = false;
                    forcedHoldFire = true;
                    return false;
                } else {
                    shoot = true;
                }
                forceNewFire = true;
                perfect = Math.random() < 0.2 ? true : false;
                // Continue automatic fire of same type
                if (memory.info.isPrimaryShooting()) {
                    secondary = false;
                } else if (memory.info.isSecondaryShooting()) {
                    secondary = true;
                }
            } else if (type.equals(UT2004ItemType.FLAK_CANNON)) {
                shootingAllowanceAngle = 30;
                aimRandomDistortion = 0.2;
                forceNewFire = true;
                if (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 3) {
                    // Primary fire when close
                    secondary = false;
                    shoot = true;
                } else if (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE) {
                    // Sometimes secondary when farther
                    secondary = (Math.random() < 0.3);
                    if(!secondary){
                        // Regular shots are slow, which is important when far
                        adjust = slowAdjustment(enemy, memory.getAgentLocation());
                    }
                } else {
                    // Don't bother when far
                    memory.body.stopShoot();
                    shoot = false;
                    forcedHoldFire = true;
                    if (Math.random() < DO_NOT_SHOOT_FAR_CHANCE) {
                        return false;
                    }
                }
                if (secondary) {
                    // Lob grenades at nearer target
                    if (enemy.getLocation().z > memory.info.getLocation().z + 100) {
                        //System.out.println("Flak grenade stop shoot");
                        memory.body.stopShoot();
                        return true;
                    }
                    adjust = lobAdjustment(enemy, memory.getAgentLocation());
                } else {
                    perfect = Math.random() < 0.2 ? true : false;
                }
            } else if (type.equals(UT2004ItemType.ROCKET_LAUNCHER)) {
                aimRandomDistortion = 0.1;
                chargeTime = 2;
                // Need enough ammo for secondary fire
                secondary = distance > (WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2)
                        && memory.weaponry.getSecondaryWeaponAmmo(UT2004ItemType.ROCKET_LAUNCHER) >= 3
                        && (Math.random() < 0.4);
                if (memory.info.isSecondaryShooting()) {
                    shoot = true;
                    secondary = true;
                    // Continue charge for 3 rockets
                    if (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2) {
                        memory.body.stopShoot(true);
                        return true;
                    }
                } else if (distance >= WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE) {
                    // Don't waste primary rockets from so far away
                    memory.body.stopShoot();
                    shoot = false;
                    forcedHoldFire = true;
                    if (Math.random() < DO_NOT_SHOOT_FAR_CHANCE) {
                        return false;
                    }
                } else {
                    //shoot = true;
                    if (!secondary) {
                        forceNewFire = true;
                    }
                }

                if (secondary) {
                    perfect = true;
                } else {
                    // Slow projectiles need to aim where the enemy is headed
                    adjust = slowAdjustment(enemy, memory.getAgentLocation());
                }
            } else if (type.equals(UT2004ItemType.SNIPER_RIFLE) || type.equals(UT2004ItemType.LIGHTNING_GUN)) {
                // Sniper mode is meaningless to bots
                aimRandomDistortion += 0.25 + (Math.random() * 0.50);
                aimVelDiffDistortion -= 10;
                secondary = false;
                forceNewFire = true;
                // Disable sniping while jumping, most of the time
                Velocity v = memory.info.getVelocity();
                if (v != null && Math.abs(v.z) > 100 && Math.random() < 0.8) {
                    shoot = false;
                }
            }
        }

        // Don't disable shooting if bot has high ground
        if (forcedHoldFire && AgentMemory.isBeneath(enemy.getLocation(), memory.info.getLocation())) {
            shoot = true;
        }

        boolean veryClose = distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE;
        boolean noFocus = !memory.canFocusOn(enemy);
        boolean recentDodge = (memory.game.getTime() - memory.body.lastDodgeTime) < RECENT_DODGE_TIME;
        boolean notGunReady = !memory.gunReady() && !veryClose;
        boolean intoWall = memory.body.veryCloseFrontWall() || (memory.frontWallClose() && memory.body.isSplashDamageWeapon(w));
        boolean notFacing = !memory.info.isFacing(enemy, shootingAllowanceAngle) && !veryClose;
        boolean highGround = memory.botHasHighGround();
        boolean notVisible = !enemy.isVisible();
        boolean tooSteep = !veryClose && (enemy.getLocation() != null && memory.info.getLocation() != null
                && memory.body.verticalAngleToTarget(enemy.getLocation()) > Math.toRadians(MAX_SHOOT_ANGLE));
        // Don't shoot if
        if (    // Cannot focus on enemy yet (due to context switching)
                noFocus
                // Cannot focus due to recent dodge
                || recentDodge
                // Cannot focus due to recent Quick Turn (canFocusOn should handle this)
                //|| (memory.game.getTime() - memory.lastQuickTurn < AgentMemory.MIN_CONTEXT_SWITCH_TIME)
                // Gun switch was too recent to be ready to use it
                || notGunReady
                // Facing wall with splash damage weapon
                || intoWall
                // Not actually facing enemy, and not way above
                || (notFacing && !highGround)
                // Enemy not visible
                || notVisible
                // Enemy is too far above or below bot for shooting to make sense
                || tooSteep) {
        	if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
        		System.out.println(memory.info.getName() + ": Shooting disallowed: " + (noFocus?"noFocus ":"")+(recentDodge?"recentDodge ":"")+(notGunReady?"notGunReady ":"")+(intoWall?"intoWall ":"")+(notFacing?"notFacing ":"")+(notVisible?"notVisible ":"")+(tooSteep?"tooSteep ":"")+(!highGround?"notHighGround ":""));
        	}
            shoot = false;
        }

        if (shoot) { // || (!BaseBot.evolving && Math.random() < SHOOT_ANYWAY_CHANCE)) {
            if (secondary && (forceNewFire || !memory.info.isSecondaryShooting())) {
                memory.body.shootAlternate(enemy, perfect, adjust, chargeTime, aimVelDiffDistortion, aimRandomDistortion);
            } else if (!secondary && (forceNewFire || !memory.info.isPrimaryShooting())) {
                memory.body.shoot(enemy, perfect, adjust, aimVelDiffDistortion, aimRandomDistortion);
            }
            return true;
        } else {
            memory.body.stopShoot();
            return false;
        }
    }

    
    /**
     * Decides whether or not the bot should jump
     * @param body
     */
    public void jumpDecision(AgentBody body) {
        Player enemy = memory.getCombatTarget();
        double distance = 0;
        Location agent = memory.info.getLocation();
        if (agent != null && enemy != null) {
            distance = agent.getDistance(enemy.getLocation());
        }
        // Only jump if moving
        Velocity velocity = body.info.getVelocity();
        Velocity v = (velocity == null) ? new Velocity(0, 0, 0) : new Velocity(velocity.x, velocity.y, 0);
        if ( // Want to jump
                this.jump
                // And not colliding
                && !memory.senses.isColliding()
                // And far from opponent
                && (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE)
                // Not near wall
                && memory.getShortestTraceToWallDistance() > JUMP_NEAR_WALL_TOLERANCE
                // And didn't jump too recently
                && (memory.game.getTime() - memory.lastCombatJumpTime > AgentMemory.MIN_TIME_BETWEEN_JUMPS)
                // And not holding a sniping weapon
                && !body.usingSnipingWeapon()
                // And moving enough
                && v.size() > 50) {
            memory.lastCombatJumpTime = memory.game.getTime();
            body.jump();
        }
    }

    /**
     * Turn bot in place
     * @param body  needed to execute actions
     * @param right true for turn right, false for turn left
     */
    public void turn(AgentBody body, boolean right) {
        body.act.act(new Rotate().setAmount((right ? 1 : -1) * 32000));
    }
}
