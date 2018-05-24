package utopia.controllers.TWEANN;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import java.io.PrintWriter;
import java.util.HashMap;
import machinelearning.evolution.evolvables.Evolvable;
import machinelearning.networks.TWEANN;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.Utils;
import utopia.agentmodel.ActionLog;
import utopia.agentmodel.actions.*;
import utopia.agentmodel.sensormodel.SensorModel;

public class AlwaysShootTWEANNController extends TWEANNController {

    transient public HashMap<String, Double> actionStartTimes = new HashMap<String, Double>();
    transient public static final int CONSECUTIVE_STILL_LIMIT = 10; //7;
    transient public static final int NO_STILL_ZONE = 200; //300;
    public int consecutiveStills = 0;
    //transient public static final double STILL_WHILE_SNIPING_CHANCE = 0.9;
    transient public static final int ADVANCE_OUTPUT = 0;
    transient public static final int RETREAT_OUTPUT = 1;
    transient public static final int LEFT_OUTPUT = 2;
    transient public static final int RIGHT_OUTPUT = 3;
    transient public static final int ITEM_OUTPUT = 4;
    transient public static final int STILL_OUTPUT = 5;
    transient public static final int JUMP_OUTPUT = 6;
    transient public static final int NUM_OUTPUTS = 7;
    transient private Item nearestVisibleItem = null;
    /**
     * For tracking action preferences *
     */
    transient public static final int NUM_BATTLE_ACTIONS = 6;
    transient public int[] forcedActionCount = null;
    /**
     * Track shooting behavior *
     */
    transient public static final int NUM_SHOOT_CHOICES = 3;
    transient public int[] shootChoices = null;
    transient public static final int SHOOT_PRIMARY_INDEX = 0;
    transient public static final int SHOOT_SECONDARY_INDEX = 1;
    transient public static final int SHOOT_NOTHING_INDEX = 2;
    /**
     * Track jump behavior *
     */
    transient public static final int NUM_JUMP_CHOICES = 2;
    transient public int[] jumpChoices = null;
    transient public static final int JUMP_YES = 0;
    transient public static final int JUMP_NO = 1;
    /**
     * Track forced still behavior *
     */
    transient public static final int NUM_STILL_FORCED_CHOICES = 2;
    transient public int[] stillForcedChoices = null;
    transient public static final int STILL_FORCED_NO = 0;
    transient public static final int STILL_FORCED_YES = 1;
    transient public static double MOMENTUM_TIME = 2;

    @Override
    public void init() {
        forcedActionCount = new int[NUM_BATTLE_ACTIONS];
        shootChoices = new int[NUM_SHOOT_CHOICES];
        jumpChoices = new int[NUM_JUMP_CHOICES];
        stillForcedChoices = new int[NUM_STILL_FORCED_CHOICES];
    }

    @Override
    public void logActionChoices(PrintWriter actionLog) {
        super.logActionChoices(actionLog);

        actionLog.println("STILL FORCED CHOICES");
        int totalStillActions = stillForcedChoices[STILL_FORCED_YES] + stillForcedChoices[STILL_FORCED_NO];
        actionLog.println("Total Still Actions: " + totalStillActions);
        actionLog.println(ActionLog.actionLogLine(null, "Forced Still ", stillForcedChoices[STILL_FORCED_YES], totalStillActions));
        actionLog.println(ActionLog.actionLogLine(null, "Natural Still", stillForcedChoices[STILL_FORCED_NO], totalStillActions));
        actionLog.println();

        actionLog.println("FORCED ACTIONS");
        int totalForcedActions = forcedActionCount[ADVANCE_OUTPUT]
                + forcedActionCount[RETREAT_OUTPUT]
                + forcedActionCount[LEFT_OUTPUT]
                + forcedActionCount[RIGHT_OUTPUT]
                + forcedActionCount[ITEM_OUTPUT]
                + forcedActionCount[STILL_OUTPUT];
        actionLog.println("Total Forced Actions: " + totalForcedActions);
        actionLog.println(ActionLog.actionLogLine(null, "Forced Advance    ", forcedActionCount[ADVANCE_OUTPUT], totalForcedActions));
        actionLog.println(ActionLog.actionLogLine(null, "Forced Retreat    ", forcedActionCount[RETREAT_OUTPUT], totalForcedActions));
        actionLog.println(ActionLog.actionLogLine(null, "Forced StrafeLeft ", forcedActionCount[LEFT_OUTPUT], totalForcedActions));
        actionLog.println(ActionLog.actionLogLine(null, "Forced StrafeRight", forcedActionCount[RIGHT_OUTPUT], totalForcedActions));
        actionLog.println(ActionLog.actionLogLine(null, "Forced GotoItem   ", forcedActionCount[ITEM_OUTPUT], totalForcedActions));
        actionLog.println(ActionLog.actionLogLine(null, "Forced Still      ", forcedActionCount[STILL_OUTPUT], totalForcedActions));
        actionLog.println();

        actionLog.println("SHOOT CHOICES");
        int totalShootChoices = shootChoices[SHOOT_PRIMARY_INDEX]
                + shootChoices[SHOOT_SECONDARY_INDEX]
                + shootChoices[SHOOT_NOTHING_INDEX];
        actionLog.println("Total Shoot Choices: " + totalShootChoices);
        actionLog.println(ActionLog.actionLogLine(null, "Primary  ", shootChoices[SHOOT_PRIMARY_INDEX], totalShootChoices));
        actionLog.println(ActionLog.actionLogLine(null, "Secondary", shootChoices[SHOOT_SECONDARY_INDEX], totalShootChoices));
        actionLog.println(ActionLog.actionLogLine(null, "None     ", shootChoices[SHOOT_NOTHING_INDEX], totalShootChoices));
        actionLog.println();

        actionLog.println("JUMP CHOICES");
        int totalJumpChoices = jumpChoices[JUMP_YES] + jumpChoices[JUMP_NO];
        actionLog.println("Total Jump Choices: " + totalJumpChoices);
        actionLog.println(ActionLog.actionLogLine(null, "Jump   ", jumpChoices[JUMP_YES], totalJumpChoices));
        actionLog.println(ActionLog.actionLogLine(null, "No Jump", jumpChoices[JUMP_NO], totalJumpChoices));
        actionLog.println();
    }

    public AlwaysShootTWEANNController(TWEANN tweann, SensorModel model) {
        super(tweann, model);
        numNonSensory = 1;
    }

    @Override
    public void registerActions() {
        register("Still");
        register("Advance");
        register("Retreat");
        register("Strafe Left");
        register("Strafe Right");
        register("Goto Item");
        register("Empty");
    }

    public AlwaysShootTWEANNController(SensorModel model) {
        this(new TWEANN(model.getNumSensors() + 1, NUM_OUTPUTS, TWEANNController.featureSelective), model);
    }

    @Override
    public Action control(AgentMemory memory) {
        if (actionStartTimes == null) {
            actionStartTimes = new HashMap<String, Double>();
        }
        double[] outputs = processInputsToOutputs(memory);

        // Remove jump output
        double[] actionchoices = new double[outputs.length - 1];
        System.arraycopy(outputs, 0, actionchoices, 0, actionchoices.length);

        int choice = actionChoiceFilter(memory, argmax(actionchoices), actionchoices);
        boolean shoot = true;
        boolean jump = outputs[JUMP_OUTPUT] > 0.0;

        return getActionDecision(memory, choice, shoot, jump);
    }

    protected int actionChoiceFilter(AgentMemory memory, int choice, double[] actionchoices) {
        // Don't go to unwanted or unreachable items
        nearestVisibleItem = memory.info.getNearestVisibleItem();
        //System.out.println("Start choice = " + choice);
        //System.out.println("Nearest item: " + nearestVisibleItem);
        if (nearestVisibleItem == null || !GotoItemAction.willGoto(memory, nearestVisibleItem)) {
            actionchoices[ITEM_OUTPUT] = -Double.MAX_VALUE;
            if (choice == ITEM_OUTPUT) {
                choice = argmax(actionchoices);
                //System.out.println("\t1. Swap GOTO with " + choice);
            }
        }

        boolean disallowRetreat = false;
        if (!actionAllowed("Retreat", memory)
                || memory.backWallClose()
                || memory.onElevator()) {
            disallowRetreat = true;
            actionchoices[RETREAT_OUTPUT] = -Double.MAX_VALUE;
            if (choice == RETREAT_OUTPUT) {
                choice = argmax(actionchoices);
                //System.out.println("\t2. Swap RETREAT with " + choice);
            }
        }

        // Don't advance when sniping or shooting rockets up close
        boolean disallowAdvance = false;

        if (!actionAllowed("Advance", memory)) {
            actionchoices[ADVANCE_OUTPUT] = -Double.MAX_VALUE;
            disallowAdvance = true;
            if (choice == ADVANCE_OUTPUT) {
                choice = argmax(actionchoices);
                //System.out.println("\t3. Swap ADVANCE with " + choice);
            }
        }

        Weapon w = memory.getCurrentWeapon();
        boolean snipingWeapon = memory.body.isSnipingWeapon(w);
        Player enemy = memory.getCombatTarget();
        Location loc = memory.info.getLocation();
        UT2004ItemType enemyWeaponType = memory.enemyWeaponType(enemy);

        if (enemy != null) {
            double distance = loc.getDistance(enemy.getLocation());
            if ( // Don't advance towards opponent attacking with shield gun
                    (enemyWeaponType != null && enemyWeaponType.equals(UT2004ItemType.SHIELD_GUN) && memory.isAdvancing(enemy))
                    // Don't advance with sniping weapons if already close
                    || (snipingWeapon && (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE))
                    // Or with rocket launcher if very close
                    || (w != null && w.getType().equals(UT2004ItemType.ROCKET_LAUNCHER)
                    && distance < (WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE / 2)
                    && !memory.isRetreating(enemy))
                    // Or if super close
                    || (distance < (WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2))) {
                actionchoices[ADVANCE_OUTPUT] = -Double.MAX_VALUE;
                disallowAdvance = true;
                if (choice == ADVANCE_OUTPUT) {
                    choice = argmax(actionchoices);
                    //System.out.println("\t4. Swap ADVANCE with " + choice);
                }
            }

            // Retreat if too close when sniping
            if (!disallowRetreat
                    && // Too close with a sniping weapon
                    ((distance < NO_STILL_ZONE && snipingWeapon)
                    || // Close wall in front, and enemy is farther away
                    (memory.frontWallClose() && distance > AgentBody.CLOSE_WALL_DISTANCE))) {
                forcedActionCount[RETREAT_OUTPUT]++;
                System.out.println("\tForced RETREAT");
                if (distance < NO_STILL_ZONE && snipingWeapon) {
                    System.out.println("\tToo close to snipe, so RETREAT");
                }
                if (memory.frontWallClose() && distance > AgentBody.CLOSE_WALL_DISTANCE) {
                    System.out.println("\tStaring at wall, so RETREAT");
                }
                return RETREAT_OUTPUT;
            }
        }

        Player nearestEnemy = memory.getSeeEnemy();
        double distance = Double.MAX_VALUE;
        if (nearestEnemy != null) {
            distance = loc.getDistance(nearestEnemy.getLocation());
        }
        boolean getCloseWeapon = memory.body.isGetCloseWeapon(w);
        //boolean chargeWeapon = memory.body.isSecondaryChargingWeapon(w);
        boolean generalThreatened = memory.isThreatened();
        boolean enemyThreatened = (enemy != null && memory.isThreatening(enemy)) || (nearestEnemy != null && memory.isThreatening(nearestEnemy));
        boolean threatened = (generalThreatened || enemyThreatened);
        //System.out.println("generalThreatened: " + generalThreatened + ", enemyThreatened:" + enemyThreatened);
        //boolean facingMe = enemy != null && memory.isFacingMe(enemy, AgentMemory.FACING_TO_SHOOT_DEGREES);
        boolean highGround = memory.botHasHighGround();
        boolean farWithCloseWeapon = (getCloseWeapon && (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2));

        // Need to be close to unleash super Bio Shot
        if (!disallowAdvance
                && ((w != null && w.getType().equals(UT2004ItemType.BIO_RIFLE)
                && memory.info.isSecondaryShooting()
                && (distance >= WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2))
                || farWithCloseWeapon)) {
            forcedActionCount[ADVANCE_OUTPUT]++;
            System.out.println("\tForced ADVANCE");
            System.out.println("\tADVANCE for Bio Rifle charge");
            return ADVANCE_OUTPUT;
        }

        boolean disallowLeft = false;
        if (!actionAllowed("Strafe Left", memory)) {
            actionchoices[LEFT_OUTPUT] = -Double.MAX_VALUE;
            if (choice == LEFT_OUTPUT) {
                choice = argmax(actionchoices);
                //System.out.println("6. Swap LEFT with " + choice);
            }
            disallowLeft = true;
        }

        boolean disallowRight = false;
        if (!actionAllowed("Strafe Right", memory)) {
            actionchoices[RIGHT_OUTPUT] = -Double.MAX_VALUE;
            if (choice == RIGHT_OUTPUT) {
                choice = argmax(actionchoices);
                //System.out.println("7. Swap RIGHT with " + choice);
            }
            disallowRight = true;
        }

        boolean disallowStrafe = false;
        if (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE) {
            actionchoices[LEFT_OUTPUT] = -Double.MAX_VALUE;
            actionchoices[RIGHT_OUTPUT] = -Double.MAX_VALUE;
            disallowStrafe = true;
            if (choice == LEFT_OUTPUT || choice == RIGHT_OUTPUT) {
                choice = argmax(actionchoices);
                //System.out.println("8. Swap STRAFE with " + choice);
            }
        }

        BotDamaged damaged = memory.senses.getLastDamage();
        boolean damagedRecently = false;
        boolean damagedSelf = damaged != null && damaged.getInstigator() != null && damaged.getInstigator().equals(memory.info.getId());
        if(damagedSelf){
            System.out.println("DAMAGED SELF:" + damaged.getWeaponName() + ":" + damaged.getDamageType());
        }
        if (damaged != null && !damaged.isCausedByWorld() && damaged.getInstigator() != null && !damaged.getInstigator().equals(memory.info.getId())) {
            double currentTime = memory.info.getTime();
            double lastDamagedTime = damaged.getSimTime();
            if ((currentTime - lastDamagedTime) < (AgentMemory.TIME_UNTIL_SAFE / 4.0)) {
                damagedRecently = true;
            }
        }

        boolean snipingEnemy = memory.enemyIsSniping(enemy);
        boolean onElevator = memory.onElevator();
        boolean closeThreat = (threatened && (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2));
        boolean disallowStill = false;
        
        boolean notFacing = !memory.info.isFacing(enemy, OpponentRelativeAction.SHOOT_FACING_ALLOWANCE);
        
        if (consecutiveStills > CONSECUTIVE_STILL_LIMIT
                // Shooting will be disallowed in this case, so still should not be allowed
                || (notFacing && !highGround)
                // Don't stay still if hit
                || damagedRecently
                // Don't let enemy snipe
                || snipingEnemy
                // Don't stand still when threatened at close range by certain weapons
                || closeThreat
                // No standing still on elevators
                || onElevator
                // No standing still within a certain range
                || (distance < NO_STILL_ZONE)
                // Or when using a close range weapon from far
                || farWithCloseWeapon) {
            actionchoices[STILL_OUTPUT] = -Double.MAX_VALUE;
            disallowStill = true;
            System.out.println((consecutiveStills > CONSECUTIVE_STILL_LIMIT ? "Still Limit, " : "")
                    + (notFacing && !highGround ? "won't shoot " : "")
                    + (damagedRecently ? "damagedRecently, " : "")
                    + (snipingEnemy ? "snipingEnemy, " : "")
                    + (closeThreat ? "closeThreat, " : "")
                    + (generalThreatened ? "generalThreatened, " : "")
                    + (enemyThreatened ? "enemyThreatened, " : "")
                    + (onElevator ? "onElevator, " : "")
                    + ((distance < NO_STILL_ZONE) ? "Too Close, " : "")
                    + (farWithCloseWeapon ? "farWithCloseWeapon, " : ""));
            if (choice == STILL_OUTPUT) {
                choice = argmax(actionchoices);
                System.out.println("\tSwap STILL with " + choice);
            }
        }

        boolean safeWithFarWeapon = (!threatened && !getCloseWeapon);
        boolean safeWithCloseWeapon = (!threatened && getCloseWeapon
                && nearestEnemy != null
                && (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE)
                && (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE));
        boolean sniping = (snipingWeapon && nearestEnemy != null
                && (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE / 2)
                && (highGround || !threatened || Math.random() < 0.5));

        if (!disallowStill
                // Can't force still if already still
                && choice != STILL_OUTPUT
                // Don't replace advance with still, unless bot has high ground or a close weapon
                && (choice != ADVANCE_OUTPUT || highGround || !getCloseWeapon)
                // If enemy is facing me, then don't replace strafe with still
                //&& (!facingMe || ((choice != LEFT_OUTPUT) && (choice != RIGHT_OUTPUT)))
                && // Stay still if not threatened and using a weapon that works from afar
                (safeWithFarWeapon
                || // Should still stand still with a close weapon if you are close, but not too close
                safeWithCloseWeapon
                || // High chance of standing still when sniping from afar
                sniping)) {
            stillForcedChoices[STILL_FORCED_YES]++;
            forcedActionCount[STILL_OUTPUT]++;
            System.out.println("\tForced STILL");
            System.out.println((safeWithFarWeapon ? "safeWithFarWeapon " : "")
                    + (safeWithCloseWeapon ? "safeWithCloseWeapon " : "")
                    + (sniping ? "sniping " : ""));
            return STILL_OUTPUT;
        } else if (choice == STILL_OUTPUT) {
            stillForcedChoices[STILL_FORCED_NO]++;
        }

        if (!disallowStrafe && choice == LEFT_OUTPUT && memory.sideWallClose(true)) {
            forcedActionCount[RIGHT_OUTPUT]++;
            System.out.println("\tForced RIGHT");
            System.out.println("\tRIGHT away from close wall");
            return RIGHT_OUTPUT;
        }

        if (!disallowStrafe && choice == RIGHT_OUTPUT && memory.sideWallClose(false)) {
            forcedActionCount[LEFT_OUTPUT]++;
            System.out.println("\tForced LEFT");
            System.out.println("\tLEFT away from close wall");
            return LEFT_OUTPUT;
        }
        //System.out.println("End choice = " + choice);
        return choice;
    }

    public void takeAction(String key, AgentMemory memory) {
        String previous = lastActionLabel();
        if (!previous.equals(key)) {
            actionStartTimes.put(key, memory.game.getTime());
        }
        takeAction(key);
    }

    private boolean actionAllowed(String key, AgentMemory memory) {
        Double actionTime = actionStartTimes.get(key);
        if (actionTime == null) {
            actionTime = 0.0;
        }
        double time = memory.game.getTime() - actionTime;
        return time > MOMENTUM_TIME;
    }

    public Action getActionDecision(AgentMemory memory, int choice, boolean shoot, boolean jump) {
        firePrimary = Utils.randomBool();
        Action result = null;
        if (choice == STILL_OUTPUT) {
            // Do not jump in place!
            jump = false;
            //Always fire when standing still
            shoot = true;
            takeAction("Still", memory);
            result = new StillAction(memory, true, !firePrimary, false);
        } else if (choice == ADVANCE_OUTPUT) {
            takeAction("Advance", memory);
            result = new ApproachEnemyAction(memory, shoot, !firePrimary, jump, false);
        } else if (choice == RETREAT_OUTPUT) {
            takeAction("Retreat", memory);
            result = new AvoidEnemyAction(memory, shoot, !firePrimary, jump);
        } else if (choice == LEFT_OUTPUT) {
            takeAction("Strafe Left", memory);
            result = new EnemyRelativeStrafeAction(memory, shoot, !firePrimary, true, jump);
        } else if (choice == RIGHT_OUTPUT) {
            takeAction("Strafe Right", memory);
            result = new EnemyRelativeStrafeAction(memory, shoot, !firePrimary, false, jump);
        } else if (choice == ITEM_OUTPUT && nearestVisibleItem != null) {
            takeAction("Goto Item", memory);
            //System.out.println("Goto Item: " + nearestVisibleItem);
            result = new GotoItemAction(memory, nearestVisibleItem, shoot, !firePrimary, jump);
        } else {
            takeAction("Empty", memory);
            result = new EmptyAction();
        }
        logShootAndJump(shoot, jump);
        if (result instanceof StillAction) {
            consecutiveStills++;
        } else {
            consecutiveStills = 0;
        }
        return result;
    }

    @Override
    public Evolvable getNewInstance() {
        return new AlwaysShootTWEANNController((TWEANN) this.tweann.getNewInstance(), this.model);
    }

    @Override
    public Evolvable copy() {
        return new AlwaysShootTWEANNController((TWEANN) this.tweann.copy(), this.model);
    }

    protected void logShootAndJump(boolean shoot, boolean jump) {
        if (shoot) {
            if (firePrimary) {
                shootChoices[SHOOT_PRIMARY_INDEX]++;
            } else {
                shootChoices[SHOOT_SECONDARY_INDEX]++;
            }
        } else {
            shootChoices[SHOOT_NOTHING_INDEX]++;
        }

        if (jump) {
            jumpChoices[JUMP_YES]++;
        } else {
            jumpChoices[JUMP_NO]++;
        }
    }

    @Override
    public void reset() {
        super.reset();
        //nearestVisibleItem = null;
    }
}
