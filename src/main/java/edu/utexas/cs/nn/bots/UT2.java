package edu.utexas.cs.nn.bots;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutPlatform;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.agent.utils.UT2004BotDescriptor;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.*;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.MultipleUT2004BotRunner;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.retrace.HumanRetraceController;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable.WeaponTableEntry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import utopia.agentmodel.ActionLog;
import utopia.agentmodel.actions.*;
import utopia.controllers.TWEANN.TWEANNController;
import utopia.controllers.scripted.*;
import wox.serial.Easy;

/**
 * UT's bot for botprize competition.
 *
 * @author Igor Karpov
 * @author Jacob Schrum
 */
@AgentScoped
public class UT2 extends BaseBot {

    public static class UT2Parameters extends UT2004BotParameters {

        private TWEANNController battleController;
        private IUT2004Server server;

        public UT2Parameters(TWEANNController cont, IUT2004Server server) {
            this.battleController = cont;
            this.server = server;
        }

        public TWEANNController getBattleController() {
            return this.battleController;
        }

        public IUT2004Server getServer() {
            return this.server;
        }
    }
    private String cachedName = null;
    private String cachedMap = null;
    private PrintWriter actionLog = null;
    private static final String DEFAULT_FILE = Constants.UT2_ROOT.get() + "candidates/FRONT-DM-1on1-Albatross-vs-1Native_98_7.xml";
    private String actionLogFilename = null;
    private boolean logAtEnd = false;
    private PathController pathController;
    public TWEANNController battleController;
    private UnstuckController unstuckController;
    private ObservingController observingController;
    private HumanRetraceController humanTraceController;
    private ChasingController chaseController;
    private JudgingController judgingController = null;
    private ShieldGunController shieldGunController;
    private WaterController waterController = null;
    private Action action;
    public static WeaponPreferenceTable weaponPreferences;
    private int notMoving;
    private static final int MAX_STILL_TIME = 8;
    private static final int INTERESTING_HEALTH_ITEM = 5;
    private static final int OFF_GRID_DISTANCE = 600;
    private static final double TIME_BETWEEN_QUICK_TURNS = 2.5;
    private static boolean printActions = Constants.PRINT_ACTIONS.getBoolean();
    public static final int CONSECUTIVE_OBSERVE_ALLOWANCE = 10;
    private double lastRetraceFailure = 0;
    private ActionLog actions = new ActionLog("UT^2");
    private ActionLog stuckTriggers = new ActionLog("Stuck Trigger");
    private boolean ANONYMOUS_NAME = true;
    private Player lastTargetObserved = null;
    private int consecutiveObservations = 0;
    public static final double START_OBSERVING_CHANCE = 0.75;
    public static final double BREAK_OBSERVING_CHANCE = 0.15;
    public static final double BROKEN_OBSERVATION_PAUSE = 5.0;
    public static final double JUDGE_THEN_OBSERVE_PAUSE = 10;
    //public static final double STILL_IN_WATER_TIME = 5;

    /**
     * There is a particular area in GrendelKeep where the bot gets stuck on a
     * curved rim that requires a double jump to get over. Bot should not
     * retrace around this area.
     */
    private boolean grendelKeepRim() {
        if (game.getMapName().equals("DM-DE-GrendelKeep")) {
            Location botLoc = bot.getLocation();
            if (botLoc != null && botLoc.z < -398.1) { // Must be below the rim
                NavPoint nearest = info.getNearestNavPoint();
                if (nearest != null) {
                    String id = nearest.getId().getStringId();
                    if (id.equals("DM-DE-GrendelKeep.InventorySpot521") // health vial
                            || id.equals("DM-DE-GrendelKeep.InventorySpot518") // health vial
                            || id.equals("DM-DE-GrendelKeep.InventorySpot517") // health vial
                            || id.equals("DM-DE-GrendelKeep.InventorySpot538") // ammo
                            || id.equals("DM-DE-GrendelKeep.InventorySpot562") // ammo
                            || id.equals("DM-DE-GrendelKeep.InventorySpot506") // Link Gun
                            || id.equals("DM-DE-GrendelKeep.PathNode25") // stairs bottom
                            || id.equals("DM-DE-GrendelKeep.PathNode34") // upper
                            || id.equals("DM-DE-GrendelKeep.PathNode27") // lower
                            || id.equals("DM-DE-GrendelKeep.PathNode28") // lower
                            || id.equals("DM-DE-GrendelKeep.PathNode29") // lower
                            || id.equals("DM-DE-GrendelKeep.PathNode30") // lower
                            || id.equals("DM-DE-GrendelKeep.PathNode32")) // upper
                    {
                        System.out.println("\tGrendelKeep Rim");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public UT2() {
    }

    public static boolean canJudge() {
        return JUDGE && !evolving;
    }

    /**
     * Initialize all necessary variables here, before the bot actually receives
     * anything from the environment.
     */
    @Override
    public void prepareBot(UT2004Bot bot) {
        UT2Parameters params = (UT2Parameters) bot.getParams();
        this.battleController = params.getBattleController();
        super.server = params.getServer();
        initActionLog(this.battleController.filename);

        if (!evolving || Constants.LOG_CONTROLLER_EVALUATIONS.getBoolean()) {
            logAtEnd = true;

            actions.register("Quick Turn");
            actions.register("Unstuck");
            actions.register("Observe");
            actions.register("Get Dropped Weapon");
            actions.register("Important Item");
            actions.register("Get Weapon");
            actions.register("Trace To Weapon");
            actions.register("Judge");
            actions.register("Shield Gun");
            actions.register("Battle");
            actions.register("Chase");
            actions.register("Retrace");
            actions.register("Path");
            actions.register("Empty");
            actions.register("Die");

            stuckTriggers.register("Same Navpoint");
            stuckTriggers.register("Collision Frequency");
            stuckTriggers.register("Still");
            stuckTriggers.register("Collision");
            stuckTriggers.register("Agent Bump");
            stuckTriggers.register("Off Grid");
            stuckTriggers.register("Unknown");
            stuckTriggers.register("Under Elevator");
            stuckTriggers.register("Water Trap");
        }

        // Weapon preference behavior
        if (weaponPreferences == null) {
            weaponPreferences = new WeaponPreferenceTable();
        }
        this.humanTraceController = (evolving ? null : new HumanRetraceController());
        this.unstuckController = new UnstuckController(humanTraceController);
        this.observingController = new ObservingController(battleController);
    }

    /**
     * Here we can modify initializing command for our bot, e.g., sets its
     * cachedName or skin.
     *
     * @return instance of {@link Initialize}
     */
    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName(Constants.BOT_NAME.get());
    }

    /**
     * Handshake with GameBots2004 is over - bot has information about the map
     * in its world view. Many agent modules are usable since this method is
     * called.
     *
     * @param gameInfo information about the game type
     * @param config information about configuration
     * @param init information about configuration
     */
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
        super.botInitialized(gameInfo, currentConfig, init);
        getLog().setLevel(Level.OFF);
        getMapName();

        //Start tracking mystats for performance evaluation
        // schrum2: 5/17/12: No MyAgentStats
//        if (evolving || Evolve.evaluate) {
//            mystats = new MyAgentStats(new BaseAgent(this));
//            battleController.stats = mystats;
//        }

        //((Act)getAgentBody().act).getLog().setLevel(Level.ALL);

        if (!evolving) {
            this.judgingController = new JudgingController(getAgentMemory(), battleController);
        }
        this.chaseController = new ChasingController(bot, getAgentMemory());
        this.pathController = new DistantPathController(bot, getAgentMemory().itemPathExecutor, getAgentMemory().pathPlanner, getAgentMemory());
        this.shieldGunController = new ShieldGunController(this.pathController);

        String map = game.getMapName();
        if (map.toLowerCase().equals("DM-GoatswoodPlay".toLowerCase())
                || map.toLowerCase().equals("DM-IceHenge".toLowerCase())) {
            this.waterController = new WaterController();
        }

        if (humanTraceController != null) {
            this.humanTraceController.onBotInitialized(getAgentMemory());
        }
        this.unstuckController.onBotInitialized(getAgentMemory());

        battleController.registerActions();
        battleController.prepareSensorModel(getAgentBody());
        getAgentBody().finalAutoTraceDecision();

        getWorldView().addEventListener(PlayerKilled.class, playerKilledHandler);
        getWorldView().addEventListener(AddInventoryMsg.class, addInventoryHandler);
        getWorldView().addEventListener(BotDamaged.class, botDamagedHandler);
        getWorldView().addObjectListener(Player.class, WorldObjectDisappearedEvent.class, playerDisappeared);

    }
    IWorldObjectEventListener<Player, WorldObjectDisappearedEvent<Player>> playerDisappeared = new IWorldObjectEventListener<Player, WorldObjectDisappearedEvent<Player>>() {

        @Override
        public void notify(WorldObjectDisappearedEvent<Player> event) {
            //System.out.println("Disappear:" + event);
            if (event != null) {
                Player disappeared = event.getObject();
                Player lastCombatTarget = getAgentMemory().lastCombatTarget;
                Location agent = info.getLocation();
                if (disappeared != null) {
                    getAgentMemory().playerDisappearedTimes.put(disappeared.getId(), game.getTime());
                    if (lastCombatTarget != null && disappeared.getId().equals(lastCombatTarget.getId())) {
                        Location enemy = disappeared.getLocation();
                        if (agent != null && enemy != null) {
                            agent = new Location(agent.x, agent.y, 0);
                            enemy = new Location(enemy.x, enemy.y, 0);
                            if (agent.getDistance(enemy) < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2) {
                                quickTurn("Disappeared While Close");
                            }
                        }
                        getAgentMemory().lastCombatTarget = null;
                    }
                }
            }
        }
    };
    IWorldEventListener<AddInventoryMsg> addInventoryHandler = new IWorldEventListener<AddInventoryMsg>() {

        @Override
        public void notify(AddInventoryMsg add) {
            if (printActions) {
                System.out.println(getIdentifier() + ":Added " + add.getType());
            }
            if (add.getDescriptor().getItemCategory().equals(UT2004ItemType.Category.WEAPON)) {
                // Controller only focuses on weapons
                ((DistantPathController) pathController).focused = false;
                equipBestWeapon(true);
            }
        }
    };
    IWorldEventListener<PlayerKilled> playerKilledHandler = new IWorldEventListener<PlayerKilled>() {

        @Override
        public void notify(PlayerKilled pk) {
            UnrealId dead = pk.getId();
            getAgentMemory().targetDies(dead);
            chaseController.enemyDies(dead);
        }
    };
    IWorldEventListener<BotDamaged> botDamagedHandler = new IWorldEventListener<BotDamaged>() {

        @Override
        public void notify(BotDamaged bd) {
            ((DistantPathController) pathController).focused = false;
            // If bot cannot see attacker, and not fighting someone else
            Player p = getAgentMemory().getCombatTarget();
            if (p == null && bd.getInstigator() == null && getAgentMemory().getCombatTarget() == null && canFight()) {
                quickTurn("Unseen Damager");
            }
        }
    };

    public boolean quickTurn(String reason) {
        if ((game.getTime() - getAgentMemory().lastQuickTurn > TIME_BETWEEN_QUICK_TURNS)
                && !actions.lastActionLabel().equals("Chase")) {
            pathController.stop();
            getAgentMemory().lastQuickTurn = game.getTime();
            actions.takeAction("Quick Turn");
            if (printActions) {
                String label = (getIdentifier() + ":QUICK_TURN:" + reason);
                System.out.println(label);
                if (!ANONYMOUS_NAME) {
                    config.setName(label);
                }
            }
            (new QuickTurnAction(getAgentMemory())).execute(getAgentBody());
            return true;
        }
        return false;
    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param gameInfo information about the game type
     * @param config information about configuration
     * @param init information about configuration
     * @param self information about the agent
     */
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        this.notMoving = 0;
        resetControllers();
    }
    /**
     * record pose and event traces, save to heatmap.db at the end
     */
    public static final boolean RECORDING = Constants.RECORDING.getBoolean();
    /**
     * print out timing information every logic step
     */
    public static final boolean TIMING = Constants.TIMING.getBoolean();
    /**
     * timing for the logic
     */
    private long[] timing = new long[10];
//    boolean first = true;

    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini
     * file in UT2004/System folder.
     *
     * @throws cz.cuni.amis.pogamut.base.exceptions.PogamutException
     */
    @Override
    public void logic() throws PogamutException {
// This code tests if the STOPSHOOT command works. Keep until Phil fixes mod.
//        if (first) {
//            this.getBody().getShooting().shoot();
//            first = false;
//        } else {
//            // Not sure what the difference is, but using both just in case
//            this.getBody().getShooting().stopShooting();
//            this.getBody().getShooting().stopShoot();
//        }
//        if(true) return;

        timingStart();

        super.logic();

        timingStep();

        getAgentMemory().seeEnemy();
        if (actions.lastActionLabel().equals("Judge")) {
            getAgentMemory().consecutiveJudgeActions++;
        } else {
            getAgentMemory().consecutiveJudgeActions = 0;
        }
        if (getAgentBody().veryCloseFrontWall()) {
            boolean turned = quickTurn("Facing Wall");
            if (turned) {
                return;
            }
        }

        // global anti-stuck?
        if (!getAgentBody().isMoving()) {
            notMoving++;
        }

        timingStep();

        this.chaseController.countdown();
        this.chaseController.updateEnemyMemory(getAgentMemory());

        timingStep();

        if (!usingLinkGun()) {
            this.equipBestWeapon();
        }

        timingStep();

        action = getAction();
        if (this.humanTraceController != null) {
            if (!actions.lastActionLabel().equals("Retrace") && !actions.lastActionLabel().equals("Unstuck")) {
                // Whenever traces are not being used, controller needs 
                // to be reset so a new trace is used next time
                this.humanTraceController.reset();
            }
        }

        timingStep();

        if (action instanceof StillAction) {
            // Bot not stuck if it wants to be still
            notMoving = 0;
            sameNavInstances = 0;
        }

        timingStep();

        action.execute(getAgentBody());

        if (TIMING) {
            timingStep();
            timingEnd();
        }

        getAgentMemory().updatePosition();
    }

    /**
     * Name of map bot is currently on. Caches the result for later use during
     * logging, in case the bot has already lost info on the current level by
     * the time it is writing the log output.
     *
     * @return
     */
    private String getMapName() {
        if (cachedMap == null) {
            cachedMap = game.getMapName();
            System.out.println("Map is: " + cachedMap);
        }
        return cachedMap;
    }

    private String getIdentifier() {
        if (cachedName == null || cachedName.startsWith(Constants.BOT_NAME.get())) {
            cachedName = info.getName();
            cachedName = (cachedName == null || cachedName.startsWith(Constants.BOT_NAME.get()) ? Constants.BOT_NAME.get() : cachedName);
        }
        return cachedName;
    }
    /**
     * Control module usage *
     */
    public static final boolean UNSTUCK = true;
    public static final boolean SHIELD_GUN = true;
    public static final boolean IMPORTANT = true;
    public static final boolean OBSERVE = false;
    public static final boolean JUDGE = true;
    public static final boolean BATTLE = true;
    public static final boolean PURSUIT = true;
    public static boolean RETRACE = false;
    public static final boolean PATH = true;
    /**
     * Stuck triggers *
     */
    public static final int STUCK_INDEX_SAME_NAV = 0;
    public static final int STUCK_INDEX_STILL = 1;
    public static final int STUCK_INDEX_COLLIDING = 2;
    public static final int STUCK_INDEX_BUMPING = 3;
    public static final int STUCK_INDEX_UNKNOWN = 4;
    public static final int STUCK_INDEX_OFF_GRID = 5;
    public static final int STUCK_INDEX_NO_PROGRESS = 6;
    public static final int STUCK_INDEX_WATER_REPEAT = 7;
    public static final int STUCK_INDEX_COLLISION_FREQUENCY = 8;
    public static final int STUCK_INDEX_UNDER_ELEVATOR = 9;
    public static final int STUCK_IN_WATER = 10;
    /**
     * Stuck trigger usage *
     */
    public static final boolean USE_UNDER_ELEVATOR = true;
    public static final boolean USE_SAME_NAV = true;
    public static final boolean USE_STILL = true;
    public static final boolean USE_COLLIDING = true;
    public static final boolean USE_BUMPING = false;
    public static final boolean USE_OFF_GRID = true;
    public static final boolean USE_COLLISION_FREQUENCY = true;

    /**
     * Goes through all control modules in priority order and returns an action
     * from the controller/module with highest priority and a firing trigger.
     *
     * @return
     */
    private Action getAction() {
        Action result;
        StringBuilder label = new StringBuilder();
        label.append(getIdentifier());
        label.append(":");

//        if (this.humanTracePathController.targetReached(getAgentMemory())) {
//            this.humanTracePathController.reset();
//        }

        Player target = getAgentMemory().getCombatTarget();
        if (target == null || !target.isVisible()) {
            Weapon w = weaponry.getCurrentWeapon();
            if (w != null && w.getType().equals(UT2004ItemType.BIO_RIFLE)) {
                // Charge secondary fire for big blob
                if (!info.isSecondaryShooting() && weaponry.getCurrentAlternateAmmo() >= 9) {
                    body.getShooting().shootSecondary();
                }
            }
            // Won't stop charging shots
            if (info.isShooting()) {
                getAgentBody().stopShoot();
            }
        }

        Location botLoc = info.getLocation();
        if (botLoc == null) {
            System.out.println("\tDon't know where bot is");
            return new EmptyAction();
        }

        double nearestNavDistance = Double.MAX_VALUE;
        NavPoint nearestNav = info.getNearestNavPoint();
        if (nearestNav != null && nearestNav.getLocation() != null) {
            nearestNavDistance = nearestNav.getLocation().getDistance(botLoc);
        }

        this.unstuckController.decay();

        if (UNSTUCK
                && ((USE_UNDER_ELEVATOR && getAgentMemory().underElevator())
                || (USE_SAME_NAV && sameNavInstances > SAME_NAV_ALLOWANCES)
                || (waterController != null && actions.lastActionLabel().equals("Unstuck") && getAgentMemory().inWater())
                || (USE_STILL && notMoving > MAX_STILL_TIME)
                || (USE_COLLIDING && senses.isColliding() && !actions.lastActionLabel().equals("Retrace"))
                || (USE_COLLISION_FREQUENCY && unstuckController.stuckFromFrequentCollisions())
                || (USE_BUMPING && senses.isBumping())
                || (USE_OFF_GRID && nearestNavDistance > OFF_GRID_DISTANCE))) {

            int stuckReason;
            if (USE_UNDER_ELEVATOR && getAgentMemory().underElevator()) {
                stuckReason = STUCK_INDEX_UNDER_ELEVATOR;
                stuckTriggers.takeAction("Under Elevator");
            } else if (waterController != null && getAgentMemory().inWater()) {
                stuckReason = STUCK_IN_WATER;
                stuckTriggers.takeAction("Water Trap");
            } else if (USE_SAME_NAV && sameNavInstances > SAME_NAV_ALLOWANCES) {
                stuckReason = STUCK_INDEX_SAME_NAV;
                stuckTriggers.takeAction("Same Navpoint");
            } else if (USE_COLLISION_FREQUENCY && unstuckController.stuckFromFrequentCollisions()) {
                stuckReason = STUCK_INDEX_COLLISION_FREQUENCY;
                stuckTriggers.takeAction("Collision Frequency");
            } else if (USE_STILL && notMoving > MAX_STILL_TIME) {
                stuckReason = STUCK_INDEX_STILL;
                stuckTriggers.takeAction("Still");
            } else if (USE_COLLIDING && senses.isColliding() && !actions.lastActionLabel().equals("Retrace")) {
                stuckReason = STUCK_INDEX_COLLIDING;
                stuckTriggers.takeAction("Collision");
            } else if (USE_BUMPING && senses.isBumping()) {
                stuckReason = STUCK_INDEX_BUMPING;
                stuckTriggers.takeAction("Agent Bump");
            } else if (USE_OFF_GRID && nearestNavDistance > OFF_GRID_DISTANCE) {
                stuckReason = STUCK_INDEX_OFF_GRID;
                stuckTriggers.takeAction("Off Grid");
            } else {
                stuckReason = STUCK_INDEX_UNKNOWN;
                stuckTriggers.takeAction("Unknown");
            }
            this.chaseController.wasStuck = true;
            this.pathController.wasStuck = true;

            getAgentMemory().stopPathExecutors();
            this.notMoving = 0;

            if (stuckReason == STUCK_IN_WATER) {
                result = this.waterController.control(getAgentMemory());
            } else {
                result = this.unstuckController.control(getAgentMemory(), stuckReason);
            }

            if (result != null) {
                actions.takeAction("Unstuck");
                if (printActions) {
                    label.append(unstuckController.consecutiveStuckActions);
                    label.append(":UNSTUCK:");
                    label.append(stuckTriggers.lastActionLabel());
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }
        this.unstuckController.tick(getAgentMemory());
        this.unstuckController.reset();
        if (waterController != null) {
            waterController.reset();
        }

        // Always pickup dropped weapons
        Map<UnrealId, Item> weapons = items.getVisibleItems(UT2004ItemType.Category.WEAPON);
        for (Item weapon : weapons.values()) {
            // FIXME: isReachable -> isVisble
            if (weapon.isDropped() && weapon.isVisible() /*
                     * && weapon.isReachable()
                     */ && weapon.getLocation().getDistance(botLoc) < Constants.NEAR_ITEM_DISTANCE.getInt()) {
                result = new GotoItemAction(getAgentMemory(), weapon, true, false, false);
                actions.takeAction("Get Dropped Weapon");
                if (printActions) {
                    label.append("GET_DROPPED_WEAPON:");
                    label.append(weapon.getType().getName());
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }

        Collection<Item> important = getAllVeryImportantItems(getAgentMemory());
        if (IMPORTANT && !important.isEmpty()) {
            Item closest = DistanceUtils.getNearest(important, botLoc);
            if (closest.getLocation().getDistance(botLoc) < Constants.NEAR_ITEM_DISTANCE.getInt()
                    && closest.getLocation().getDistanceZ(botLoc) < Constants.NEAR_ITEM_HEIGHT.getInt()) {
                result = ((DistantPathController) this.pathController).getItem(getAgentMemory(), closest);
                //result = new GotoItemAction(getAgentMemory(), closest, true, false, false);
                actions.takeAction("Important Item");
                //this.pathController.stop();
                if (printActions) {
                    label.append("IMPORTANT:");
                    label.append(closest.getType().getName());
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }

        boolean hasGoodWeapon = weaponPreferences.hasGoodWeapon(weaponry.getLoadedRangedWeapons(), this.players, getAgentMemory());
        //System.out.println("hasGoodWeapon:" + hasGoodWeapon);
        if (!hasGoodWeapon && itemPathControllerNotDoingAnythingImportant()) {
            result = ((DistantPathController) this.pathController).getNeededWeapon(getAgentMemory());
            if (result != null) {
                actions.takeAction("Get Weapon");
                String name = pathController.itemName();
                if (printActions) {
                    label.append("GET_WEAPON:");
                    label.append(pathController.lastActionLabel());
                    label.append(":");
                    label.append(name);
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }

        Weapon current = weaponry.getCurrentWeapon();
        double distance = Double.MAX_VALUE;
        if (target != null && target.getLocation() != null) {
            distance = target.getLocation().getDistance(info.getLocation());
        }
        //boolean linkGunDistance = (current == null || current.getType().equals(UT2004ItemType.LINK_GUN) || (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 3));
        boolean canFight = canFight();
        boolean shouldJudge = (judgingController.shouldJudge(getAgentMemory()) || consecutiveObservations > CONSECUTIVE_OBSERVE_ALLOWANCE);
        if (target != null
                && canJudge()
                // Don't suddenly switch to link gun when too close
                //&& linkGunDistance
                // Don't judge when using UDamage
                && !info.hasUDamage()
                && canFight
                && shouldJudge) {
            result = judgingController.control(getAgentMemory());
            if (result != null) {
                this.chaseController.abort();
                this.pathController.stop();
                actions.takeAction("Judge");
                if (printActions) {
                    label.append("JUDGE:");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }

        if (OBSERVE && target != null
                // Have a good weapon to fall back on
                && hasGoodWeapon
                // Always a small chance of breaking out of OBSERVE
                && (Math.random() > BREAK_OBSERVING_CHANCE)
                // Don't OBSERVE if close and holding a sniping weapon
                && (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE
                || !getAgentBody().usingSnipingWeapon())
                // Not in middle of crowd
                && (getAgentMemory().numVisibleOpponents() == 1
                || target.getLocation().getDistance(botLoc) > (WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 3))
                // Observe at the start of interaction, but not in middle
                //&& !actions.lastActionLabel().equals("Battle")
                && !actions.lastActionLabel().equals("Chase")
                && !actions.lastActionLabel().equals("Judge")
                //&& ((game.getTime() - observeBrokenTime) < BROKEN_OBSERVATION_PAUSE)
                //&& (consecutiveObservations > 0 || Math.random() < START_OBSERVING_CHANCE)
                && (// Opponent has judging gun out
                (target.getWeapon() != null && target.getWeapon().equals("XWeapons.LinkGun"))
                || (// Not with UDamge
                !info.hasUDamage()
                // Not threatened
                && (!getAgentMemory().isThreatening(target, 50))))) {
            result = this.observingController.control(getAgentMemory());
            if (result != null) {
                if (lastTargetObserved != null && lastTargetObserved.getId().equals(target.getId())) {
                    consecutiveObservations++;
                }
                lastTargetObserved = target;
                this.pathController.stop();
                actions.takeAction("Observe");
                if (printActions) {
                    label.append("OBSERVE:");
                    label.append(consecutiveObservations);
                    label.append(":");
                    label.append(observingController.lastActionLabel());
                    label.append(":");
                    label.append(target.getName());
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }
        consecutiveObservations = 0;

        // Give bot chance to stop having link gun equipped after observing
        if (actions.lastActionLabel().equals("Observe")) {
            this.equipBestWeapon(true);
        }

        if (SHIELD_GUN && usingShieldGun()) {
            result = this.shieldGunController.control(getAgentMemory());
            if (result != null) {
                actions.takeAction("Shield Gun");
                if (printActions) {
                    label.append("SHIELD_GUN:");
                    label.append(weaponry.getCurrentAmmo());
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }
        this.shieldGunController.reset();

        // Having shouldFight() first is important because of side-effects
        if (BATTLE && shouldFight(target) && !shouldChase(target) && !usingLinkGun() && target != null && canFight()) {
            result = battleController.control(getAgentMemory());
            if (result != null) {
                this.chaseController.abort();
                this.pathController.stop();
                actions.takeAction("Battle");
                Weapon w = weaponry.getCurrentWeapon();
                if (printActions) {
                    label.append("BATTLE:");
                    label.append(w.getType().getName());
                    label.append(":");
                    label.append(weaponry.getCurrentPrimaryAmmo());
                    label.append(":");
                    label.append(weaponry.getCurrentAlternateAmmo());
                    label.append(":");
                    label.append(getAgentMemory().getCombatTarget().getName());
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }

        if (PURSUIT && shouldFight(target) && chaseController.enemyAvailable()) { // && !getAgentMemory().isAboveMe(chaseController.lastEnemyLocation())) {
            result = chaseController.control(getAgentMemory());
            if (result != null) {
                pathController.stop();
                actions.takeAction("Chase");
                if (printActions) {
                    label.append("CHASE:");
                    label.append(chaseController.timeOut);
                    label.append(":");
                    label.append(chaseController.lastActionLabel());
                    label.append(":");
                    label.append(chaseController.chaseTargetName(getAgentMemory()));

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }

        // Special case to get the UDamage in Curse4
        NavPoint nearest = info.getNearestNavPoint();
        String map = game.getMapName();
        if (map.toLowerCase().equals("DM-Curse4".toLowerCase())) {
            if (nearest != null) {
                String id = nearest.getId().getStringId();
                if (id.equals(map + ".InventorySpot93")
                        || id.equals(map + ".InventorySpot92")
                        || id.equals(map + ".PathNode48")) {
                    System.out.println("By secret UDamage in Curse4");
                    //Location uDamageLocation = new Location(-1936.21,-1172.24,49.90);
                    Map<UnrealId, Item> uDamage = items.getSpawnedItems(UT2004ItemType.U_DAMAGE_PACK);
                    if (!uDamage.isEmpty()) {
                        for (Item i : uDamage.values()) {
                            System.out.println("SPECIAL:Setting up door shoot");
                            this.getBody().getLocomotion().strafeTo(getAgentMemory().getNavPoint(map + ".InventorySpot93"), i.getLocation());
                            if (this.getInfo().isFacing(i.getLocation())) {
                                System.out.println("SPECIAL:Shoot open UDamage door");
                                this.getBody().getShooting().shootPrimary(i.getLocation());
                            }
                        }
                        return new EmptyAction();
                    }
                }

                // 450 is magic height in Curse4
                if (id.equals(map + ".LiftCenter1") && botLoc.z > 450) {
                    System.out.println("SPECIAL:Step off elevator");
                    NavPoint navTarget = getAgentMemory().getNavPoint(map + ".PathNode44");
                    this.getBody().getLocomotion().strafeTo(navTarget, navTarget);
                    return new EmptyAction();
                }

                if (id.equals(map + ".LiftExit2")
                        //|| id.equals(map + ".LiftExit3")
                        || id.equals(map + ".PathNode44")) {
                    // Leave elevator in correct direction
                    System.out.println("SPECIAL:Move further from elevator");
                    NavPoint navTarget = getAgentMemory().getNavPoint(map + ".PathNode16");
                    this.getBody().getLocomotion().strafeTo(navTarget, navTarget);
                    return new EmptyAction();
                }

                if (id.equals(map + ".PathNode57")
                        || id.equals(map + ".PathNode90")
                        || (id.equals(map + ".InventorySpot54") && getAgentMemory().facingNavPoint(map + ".PathNode57", 45))) {
                    // Continue through shaft
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot99");
                    if (result != null) {
                        System.out.println("SPECIAL:Path through Curse4 shaft");
                        return result;
                    }
                }
            }
        }

        if (map.toLowerCase().equals("DM-Antalus".toLowerCase())) {
            if (nearest != null) {
                String id = nearest.getId().getStringId();
                if (id.equals(map + ".PlayerStart0")
                        || id.equals(map + ".PathNode39")
                        || id.equals(map + ".PathNode21")
                        || id.equals(map + ".PathNode38")) {

                    Map<UnrealId, Item> shields = items.getSpawnedItems(UT2004ItemType.SUPER_SHIELD_PACK);
                    if (!shields.isEmpty()) {
                        for (Item i : shields.values()) {
                            this.getBody().getLocomotion().strafeTo(i.getLocation(), i.getLocation());
                            result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot97");
                            if (result != null) {
                                System.out.println("SPECIAL:Get the shield pack in Antalus cave");
                                return result;
                            }
                        }
                    }
                    // Shield pack was not present, but bot still in problem area
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot103");
                    if (result != null) {
                        System.out.println("SPECIAL:Path to UDamage in Antalus");
                        return result;
                    }
                }
            }
        }

        if (map.toLowerCase().equals("DM-DE-GrendelKeep".toLowerCase())) {
            if (nearest != null) {
                String id = nearest.getId().getStringId();
                if (id.equals(map + ".LiftCenter3")
                        || id.equals(map + ".LiftExit10")
                        || id.equals(map + ".InventorySpot532")) {

                    System.out.println("Exiting problem elevator in GrendelKeep");
                    Map<UnrealId, Item> shields = items.getSpawnedItems(UT2004ItemType.SUPER_SHIELD_PACK);
                    if (!shields.isEmpty()) {
                        for (Item i : shields.values()) {
                            System.out.println("SPECIAL:Get the shield pack");
                            this.getBody().getLocomotion().strafeTo(i.getLocation(), i.getLocation());
                        }
                        return new EmptyAction();
                    }
                    // Shield pack was not present, but bot still in problem area
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot558");
                    if (result != null) {
                        System.out.println("SPECIAL:Path from GrendelKeep platform");
                        return result;
                    }
                }
            }
        }

        if (map.toLowerCase().equals("DM-DE-Osiris2".toLowerCase())) {
            if (nearest != null) {
                //NavPoint liftCenter3 = getAgentMemory().getNavPoint(map + ".LiftCenter3");
                NavPoint liftCenter2 = getAgentMemory().getNavPoint(map + ".LiftCenter2");
                Map<UnrealId, Item> uDamages = items.getSpawnedItems(UT2004ItemType.U_DAMAGE_PACK);
                //(liftCenter3.isVisible() && info.isFacing(liftCenter3) // Falls off elevator
                if (!uDamages.isEmpty()
                        && liftCenter2.isVisible()
                        && info.isFacing(liftCenter2)) {

                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot17");
                    if (result != null) {
                        System.out.println("SPECIAL:Goto elevator to get UDamage in Osiris2");
                        return result;
                    }
                }

                String id = nearest.getId().getStringId();
                if (botLoc.z > -90
                        && (id.equals(map + ".LiftCenter3")
                        || id.equals(map + ".LiftCenter2")
                        || id.equals(map + ".LiftExit3")
                        || id.equals(map + ".LiftExit4"))) {

                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot17");
                    if (result != null) {
                        System.out.println("SPECIAL:Goto UDamage from elevator in Osiris2");
                        return result;
                    }
                }

                // Problems around ramps facing the downward path
                if ((id.equals(map + ".PathNode13") && info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode14"), 60))
                        || (id.equals(map + ".PathNode68") && info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode69"), 60))) {
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot11");
                    if (result != null) {
                        System.out.println("SPECIAL:Goto underground Lightning Gun in Osiris2");
                        return result;
                    }
                }

                // Problems around ramps facing away from ramp
                if (id.equals(map + ".PathNode13") && info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode9"), 60)) {
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot12");
                    if (result != null) {
                        System.out.println("SPECIAL:Goto Flak Cannon in Osiris2");
                        return result;
                    }
                }

                // Problems around ramps facing away from ramp
                if (id.equals(map + ".PathNode68") && info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode65"), 60)) {
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot13");
                    if (result != null) {
                        System.out.println("SPECIAL:Goto Shock Rifle in Osiris2");
                        return result;
                    }
                }
            }
        }

        if (map.toLowerCase().equals("DM-Asbestos".toLowerCase())) {
            if (nearest != null) {
                NavPoint pathNode180 = getAgentMemory().getNavPoint(map + ".PathNode180");
                NavPoint rocketLauncherInWater = getAgentMemory().getNavPoint(map + ".InventorySpot18");
                //NavPoint bioRifleUnderPipe = getAgentMemory().getNavPoint(map + ".InventorySpot30");
                String id = nearest.getId().getStringId();
                if (id.equals(map + ".LiftExit3")
                        || (id.equals(map + ".LiftCenter1") && botLoc.z > 700)) {
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot16");
                    if (result != null) {
                        System.out.println("SPECIAL:Goto Lightning Gun after leaving elevator in Asbestos");
                        return result;
                    }
                }

                if (id.equals(map + ".LiftExit1")
                        || (id.equals(map + ".LiftCenter0") && botLoc.z > 440)) {
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot29");
                    if (result != null) {
                        System.out.println("SPECIAL:Goto UDamage after leaving elevator in Asbestos");
                        return result;
                    }
                }

                if (id.equals(map + ".PathNode177") && info.isFacing(pathNode180, 60)) {
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot57");
                    if (result != null) {
                        System.out.println("SPECIAL:Round corner of ditch in Asbestos");
                        return result;
                    }
                }

                if (id.equals(map + ".PlayerStart11") || id.equals(map + ".PlayerStart57")) {
                    //NavPoint ditchExit = getAgentMemory().getNavPoint(map + ".InventorySpot47");
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode37");
                    if (result != null) {
                        System.out.println("SPECIAL:Escape ditch in Asbestos");
                        return result;
                    }
                }

                if (id.equals(map + ".PathNode185")) {
                    if (info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode92"), 60)) {
                        //result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode92");
                        result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot28");
                        if (result != null) {
                            System.out.println("SPECIAL:Go up stairs1 in Asbestos");
                            return result;
                        }
                    } else if (info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode25"), 60)) {
                        //result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode25");
                        result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot27");
                        if (result != null) {
                            System.out.println("SPECIAL:Go down stairs1 in Asbestos");
                            return result;
                        }
                    }
                }

                if (id.equals(map + ".PathNode10")) {
                    if (info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode11"), 60)) {
                        //result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode11");
                        result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot28");
                        if (result != null) {
                            System.out.println("SPECIAL:Go up stairs2 in Asbestos");
                            return result;
                        }
                    } else if (info.isFacing(getAgentMemory().getNavPoint(map + ".PathNode9"), 60)) {
                        //result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode9");
                        result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode2");
                        if (result != null) {
                            System.out.println("SPECIAL:Go down stairs2 in Asbestos");
                            return result;
                        }
                    }
                }

                if (id.equals(map + ".PathNode184") || id.equals(map + ".PathNode92")) {
                    result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode11");
                    if (result != null) {
                        System.out.println("SPECIAL:Avoid corner at top of stairs in Asbestos");
                        return result;
                    }
                }

                if (rocketLauncherInWater.isVisible()) {
                    if (id.equals(map + ".InventorySpot37")) {
                        result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".PathNode122");
                        if (result != null) {
                            System.out.println("SPECIAL:Go left around water pool in Asbestos");
                            return result;
                        }
                    } else if (id.equals(map + ".InventorySpot38") || id.equals(map + ".PathNode0")) {
                        result = ((DistantPathController) this.pathController).proceedToNavPoint(map + ".InventorySpot8");
                        if (result != null) {
                            System.out.println("SPECIAL:Go right around water pool in Asbestos");
                            return result;
                        }
                    }
                }
            }
        }

        //Boolean ftrResult = getAgentMemory().lastQuickTraceThroughWallResult();
        if (RETRACE && humanTraceController != null
                // Special case in GrendelKeep where human traces are bad
                && !grendelKeepRim()
                // Paths are not directed enough in combat situations
                && !getPlayers().canSeeEnemies()
                // Tracing near elevators is bad
                && !getAgentMemory().nearElevator()
                // Tracing under elevators causes big problems
                && !getAgentMemory().underElevator()
                // Last point did not go through wall
                //&& (ftrResult == null || !ftrResult)
                // Traces did not fail recently
                && (game.getTime() - lastRetraceFailure > Constants.RETRACE_REBOOT_DELAY.getDouble())
                // Not using the path controller in important way
                && itemPathControllerNotDoingAnythingImportant()) {
            result = this.humanTraceController.control(getAgentMemory());
            if (result != null) {
                if (!actions.lastActionLabel().equals("Retrace")) {
                    pathController.stop();
                    ((DistantPathController) pathController).pathEvent(true);
                }
                actions.takeAction("Retrace");
                if (printActions) {
                    label.append("RETRACE:");
                    label.append(humanTraceController.lastActionLabel());
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            } else {
//                registerRetraceFailure("RETRACE");
                if (printActions) {
                    label.append("ABORT RETRACE:");
                    label.append(humanTraceController.lastActionLabel());

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                    label = new StringBuilder();
                    label.append(getIdentifier());
                    label.append(":");
                }
            }
        }
        if (actions.lastActionLabel().equals("Retrace")) {
            pathController.retraceFailed = true;
        }

        if (PATH) {
            result = pathController.control(getAgentMemory());
            if (waterController != null && getAgentMemory().inWater() && !(result instanceof EmptyAction)) {
                result = this.waterController.control(getAgentMemory());
                if (result != null) {
                    actions.takeAction("Path");
                    if (printActions) {
                        label.append("WATER:");
                        label.append("Escape Water");
                        label.append(":");
                        label.append(result);

                        System.out.println(label);
                        if (!ANONYMOUS_NAME) {
                            config.setName(label.toString());
                        }
                    }
                    return result;
                }
            }
            if (result != null) {
                actions.takeAction("Path");
                String name = (pathController.getItem() == null ? "" : pathController.getItem().getType().getName());
                if (printActions) {
                    label.append("PATH:");
                    label.append(pathController.lastActionLabel());
                    label.append(":");
                    label.append(name);
                    label.append(":");
                    label.append(result);

                    System.out.println(label);
                    if (!ANONYMOUS_NAME) {
                        config.setName(label.toString());
                    }
                }
                return result;
            }
        }

        actions.takeAction("Empty");
        result = new EmptyAction();
        if (printActions) {
            label.append("EMPTY:");
            label.append(result);

            System.out.println(label);
            if (!ANONYMOUS_NAME) {
                config.setName(label.toString());
            }
        }
        return result;
    }

    /**
     * True if shield gun is equipped
     *
     * @return
     */
    public boolean usingShieldGun() {
        Weapon current = weaponry.getCurrentWeapon();
        return (current != null && current.getType().equals(UT2004ItemType.SHIELD_GUN));
    }

    /**
     * True if link gun is equipped
     *
     * @return
     */
    public boolean usingLinkGun() {
        Weapon current = weaponry.getCurrentWeapon();
        return (current != null && current.getType().equals(UT2004ItemType.LINK_GUN));
    }
    private UT2004ItemType testing_desiredWeapon = null;

    /**
     * Equips the best weapon for the current situation and returns true if the
     * weapon is good enough that the bot should fight with it. Will not change
     * the weapon in the middle of combat.
     *
     * @return true if the weapon is good for fighting.
     */
    public boolean equipBestWeapon() {
        return this.equipBestWeapon(false);
    }

    /**
     * Same as equipBestWeapon(), but the added parameter informs the method
     * that it is being called after the bot picked up an item
     *
     * @param added if true, the bot can switch weapons even in the middle of
     * combat
     * @return whether the weapons it good to fight with
     */
    public boolean equipBestWeapon(boolean added) {
        boolean hasGoodWeapon = weaponPreferences.hasGoodWeapon(weaponry.getLoadedRangedWeapons(), this.players, getAgentMemory());
        Weapon recommendation = weaponPreferences.savedRec;
        Weapon current = getAgentMemory().weaponry.getCurrentWeapon();

        double distance = WeaponTableEntry.MAX_RANGED_RANGE - 1;
        if (players.canSeeEnemies() && getAgentMemory().getCombatTarget() != null && getAgentMemory().getCombatTarget().getLocation() != null && info.getLocation() != null) {
            distance = Triple.distanceInSpace(getAgentMemory().getCombatTarget().getLocation(), info.getLocation());
        }

        // Don't switch weapons in the heat of battle, unless out of ammo, or just got new weapon, or using crap weapon
        if (!added
                && current != null
                && !current.getType().equals(UT2004ItemType.LINK_GUN)
                && !current.getType().equals(UT2004ItemType.BIO_RIFLE)
                && !current.getType().equals(UT2004ItemType.ASSAULT_RIFLE)
                && !current.getType().equals(UT2004ItemType.LIGHTNING_GUN) // Sniping weapons are crap at close range
                && !current.getType().equals(UT2004ItemType.SNIPER_RIFLE)
                && !current.getType().equals(UT2004ItemType.SHIELD_GUN)
                && !(getAgentBody().isSecondaryChargingWeapon(current) && info.isSecondaryShooting())
                && (getAgentMemory().isThreatened()
                || getAgentMemory().isThreatening(getAgentMemory().getCombatTarget())
                || info.isShooting()
                || (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2))
                && weaponry.hasAmmoForWeapon(current.getType())) {
            return hasGoodWeapon;
        }

        if (testing_desiredWeapon != null) {
            if (current != null && !current.getType().equals(testing_desiredWeapon) && this.weaponry.hasWeapon(testing_desiredWeapon)) {
                this.weaponry.changeWeapon(testing_desiredWeapon);
            }
            return hasGoodWeapon;
        } else {
            if (recommendation != null && recommendation != null) {
                if (current == null || !current.getType().equals(recommendation.getType())) {
                    this.weaponry.changeWeapon(recommendation);
                    this.getAgentMemory().weaponSwitchTime = game.getTime();
                }
            }
            current = this.weaponry.getCurrentWeapon();
            if (current == null || weaponry.getCurrentAmmo() == 0) {
                Map<ItemType, Weapon> loadedWeapons = weaponry.getLoadedWeapons();
                for (Weapon x : loadedWeapons.values()) {
                    ItemType t = x.getType();
                    if (!t.equals(UT2004ItemType.LINK_GUN)
                            && !t.equals(UT2004ItemType.SHIELD_GUN)
                            && !t.equals(UT2004ItemType.ONS_GRENADE_LAUNCHER)
                            && weaponry.getAmmo(t) > 0) {

                        current = weaponry.getCurrentWeapon();
                        if (current == null || !current.getType().equals(t)) {
                            weaponry.changeWeapon(t);
                            // This check seems unreliable, so it is being done in two different ways
                            if (weaponry.getCurrentAmmo() > 0 || weaponry.getAmmo(t) > 0) {
                                return hasGoodWeapon;
                            }
                        }
                    }
                }
            }
            // Final resort is shield gun
            current = this.weaponry.getCurrentWeapon();
            if (current != null && weaponry.getCurrentAmmo() == 0 && weaponry.getAmmo(current.getType()) == 0) {
                weaponry.changeWeapon(UT2004ItemType.SHIELD_GUN);
                return false;
            }

            return hasGoodWeapon;
        }
    }

    /**
     * Called each time the bot dies. Good for reseting all bot's state
     * dependent variables.
     *
     * @param event
     */
    @Override
    public void botKilled(BotKilled event) {
        lastTargetObserved = null;
        super.botKilled(event);
        if (event.isCausedByWorld()) {
            if (pathController.getItem() != null) {
                this.pathController.tabooItems.add(pathController.getItem(), DistantPathController.ITEM_IGNORE_TIME);
            }
        }
        getAgentMemory().lastCombatTarget = null;
        actions.takeAction("Die");
        resetControllers();
    }

    /**
     * Called when the bot is shutting down.
     */
    @Override
    public void botShutdown() {
        if (this.humanTraceController != null) {
            this.humanTraceController.onBotShutdown(getAgentMemory());
        }
        if (this.unstuckController != null) {
            this.unstuckController.onBotShutdown(getAgentMemory());
        }
        if (!evolving && logAtEnd) {
            //System.out.println(getIdentifier() + " printing log");
            saveActionLog();
        }
        super.botShutdown();
    }

    public void saveActionLog() {
        String map = getMapName();
        String name = getIdentifier();
        if (map == null || name == null) {
            return;
        }

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");

        try {
            actionLog = new PrintWriter(new FileOutputStream(new File(df.format(date) + "-" + map + "-" + name + "-" + actionLogFilename)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UT2.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("COULD NOT CREATE LOG!");
        }

        actionLog.println("Log for " + name);
        actionLog.println("Using: " + battleController.filename);
        actionLog.println("Map: " + map);
        actionLog.println("Score: " + info.getScore());
        actionLog.println("Suicides: " + info.getSuicides());
        actionLog.println("Deaths: " + info.getDeaths());
        actionLog.println("Kills: " + info.getKills());
        actionLog.println();

        actionLog.println("PLAYER SCORES");
        Collection<String> keys = this.judgingController.playerScores.keySet();
        for (String p : keys) {
            actionLog.println("PLAYER:" + p + " has " + this.judgingController.playerScores.get(p));
        }
        actionLog.println();

        actions.logActionChoices(actionLog);
        stuckTriggers.logActionChoices(actionLog);
        this.unstuckController.logActionChoices(actionLog);
        this.battleController.logActionChoices(actionLog);
        this.shieldGunController.logActionChoices(actionLog);
        this.pathController.logActionChoices(actionLog);
        this.chaseController.logActionChoices(actionLog);

        if (this.humanTraceController != null) {
            this.humanTraceController.logActionChoices(actionLog);
        }
        // Now that judgments give no knowledge, there is nothing to log
//        if (canJudge()) {
//            this.judgingController.logActionChoices(actionLog);
//        }

        actionLog.close();
    }

    public void resetControllers() {
        getAgentMemory().consecutiveJudgeActions = 0;
        getAgentMemory().resetWaterMemory();

        this.unstuckController.reset();
        this.observingController.reset();
        if (humanTraceController != null) {
            this.humanTraceController.reset();
        }
        this.battleController.reset();
        this.pathController.stop();
        this.chaseController.reset();
        if (judgingController != null) {
            this.judgingController.reset();
        }
        if (waterController != null) {
            waterController.reset();
        }
        getAgentMemory().itemPathExecutor.stop();
        getAgentMemory().playerPathExecutor.stop();
    }

    private Collection<Item> getAllVeryImportantItems(AgentMemory memory) {
        Map<UnrealId, Item> tempMap = new HashMap<UnrealId, Item>();
        tempMap.putAll(memory.items.getSpawnedItems(UT2004ItemType.U_DAMAGE_PACK));
        tempMap.putAll(memory.items.getSpawnedItems(UT2004ItemType.SUPER_HEALTH_PACK));
        tempMap.putAll(memory.items.getSpawnedItems(UT2004ItemType.SUPER_SHIELD_PACK));

        tempMap.putAll(memory.items.getSpawnedItems(UT2004ItemType.Category.ARMOR));
        tempMap.putAll(memory.items.getSpawnedItems(UT2004ItemType.Category.WEAPON));

        if (memory.info.getHealth() < Constants.MINIMUM_BATTLE_HEALTH.getInt()) {
            Map<UnrealId, Item> healths = memory.items.getSpawnedItems(Category.HEALTH);
            tempMap.putAll(healths);
        }

        Collection<Item> result = tempMap.values();
        Iterator<Item> itr = result.iterator();
        while (itr.hasNext()) {
            Item item = itr.next();
            if ( // Only go to reachable items
                    // FIXME: isReachable -> isVisible
                    //!item.isVisible()
                    item.getLocation().getDistance(this.getInfo().getLocation()) > Constants.NEAR_ITEM_DISTANCE.getInt()
                    // Can't go directly below through floor
                    || (getAgentMemory().isBeneathMe(item))
                    // Don't get link gun
                    || (canJudge() && item.getType().equals(UT2004ItemType.LINK_GUN))
                    // Bio Rifle is not important
                    //|| (item.getType().equals(UT2004ItemType.BIO_RIFLE))
                    // Don't go to weapons you own
                    || memory.hasWeaponOfType(item.getType())
                    // Don't waste time with small health items
                    //|| (item.getDescriptor().getItemCategory().equals(Category.HEALTH)
                    //&& item.getAmount() < INTERESTING_HEALTH_ITEM)
                    // Level specific points to ignore:
                    || (item.getNavPointId() != null
                    // On Antalus, don't go to the hard-to-reach Lightning Gun
                    && (item.getNavPointId().getStringId().equals("DM-Antalus.InventorySpot127")
                    // UDamage in GrendelKeep is sensed through wall, and is too hard to reach anyway
                    || item.getNavPointId().getStringId().equals("DM-DE-GrendelKeep.InventorySpot561")
                    // Rocket Launcher in Asbestos is surrounded by railing
                    || (item.getNavPointId().getStringId().equals("DM-Asbestos.InventorySpot18")
                    && !info.getNearestNavPoint().getId().getStringId().equals("DM-Asbestos.PathNode122") && !info.getNearestNavPoint().getId().getStringId().equals("DM-Asbestos.PathNode196")
                    && !info.getNearestNavPoint().getId().getStringId().equals("DM-Asbestos.PathNode198") && !info.getNearestNavPoint().getId().getStringId().equals("DM-Asbestos.PathNode119"))
                    // UDamage in Curse4 is hidden behind door
                    || (item.getNavPointId().getStringId().equals("DM-Curse4.InventorySpot81") && !item.isVisible() && info.isFacing(item.getLocation(), 110))
                    // On Asbestos, don't go to the hard-to-reach Shield
                    || item.getNavPointId().getStringId().equals("DM-Asbestos.InventorySpot45")
                    // On Compressed, don't go to the hard-to-reach Lightning Gun
                    || item.getNavPointId().getStringId().equals("DM-Compressed.InventorySpot98")))) {
                itr.remove();
            }
        }

        return result;
    }

    public boolean shouldChase(Player currentTarget) {
        Player chasingEnemy = this.chaseController.getLastEnemy();
        if (currentTarget != null && chasingEnemy != null
                && currentTarget.getId().equals(chasingEnemy.getId())) {
            // Fight what can be seen
            return false;
        }
        Player nearest = getAgentMemory().getSeeEnemy();
        if (getAgentMemory().numVisibleOpponents() > 2 && nearest != null && nearest.getLocation().getDistance(info.getLocation()) < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE) {
            return false;
        }
        // Only chase if can't see target
        return getAgentMemory().canFocusOn(chasingEnemy);
    }

    public boolean shouldFight(Player target) {
        boolean result = (this.equipBestWeapon() || info.hasUDamage());

        if (target != null && target.getLocation() != null && info.getLocation() != null) {
            double distance = target.getLocation().getDistance(info.getLocation());
            ItemType enemyWeaponType = getAgentMemory().enemyWeaponType(target);
            Weapon myWeapon = getAgentMemory().weaponry.getCurrentWeapon();
            ItemType myWeaponType = myWeapon == null ? null : myWeapon.getType();

            if (myWeaponType == null) {
                return false;
            }

            if (myWeaponType != null && enemyWeaponType != null && enemyWeaponType.equals(myWeaponType)) {
                // Even odds because weapons are the same
                return true;
            }
            boolean enemyWeaponBetter = weaponPreferences.betterWeapon(myWeaponType, enemyWeaponType, distance);
            if (!enemyWeaponBetter) {
                return true;
            } else {
                // Enemy weapon is better
                if (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE
                        || ((myWeaponType.equals(UT2004ItemType.ASSAULT_RIFLE) || myWeaponType.equals(UT2004ItemType.BIO_RIFLE))
                        && distance > 2 * WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE)) {
                    System.out.println("Should not fight, weapons/distance unfavorable");
                    return false;
                }
            }

//            if(getAgentBody().isSnipingWeapon(myWeapon) && enemyWeaponBetter && distance < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE / 2){
//                // Don't rush into combat with sniping weapons
//                System.out.println(info.getName() + ": Don't rush with sniping weapon");
//                return false;
//            }
            // Would be nice if we could tell whether opponent has UDamage or not
        }
//        System.out.println("shouldFight? " + result);
        return result;
    }

    public boolean itemPathControllerNotDoingAnythingImportant() {
        //Player target = getAgentMemory().getCombatTarget();
        return (// Not trying to start a path
                !getAgentMemory().itemPathExecutor.inState(PathExecutorState.FOLLOW_PATH_CALLED, PathExecutorState.PATH_COMPUTED)
                // Not path focused
                && !((DistantPathController) pathController).focused);
//                // And not currently being threatened
//                && (!getAgentMemory().isThreatened()
//                // Unless the threat is far away
//                || (target != null && info.getLocation().getDistance(target.getLocation()) > 2 * WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE)));
    }

    public boolean canFight() {
        Player targetEnemy = getAgentMemory().getCombatTarget();
        Location agentLocation = info.getLocation();
        Weapon w = weaponry.getCurrentWeapon();
        if (agentLocation == null) {
            return false;
        }
        if (weaponry.getCurrentAmmo() == 0) {
            return false;
        }
        if (targetEnemy != null && targetEnemy.getLocation() != null) {
            double distance = targetEnemy.getLocation().getDistance(agentLocation);
            if (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE && getAgentBody().isGetCloseWeapon(w)) {
                return false;
            }

            if (distance > WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE && getAgentBody().isSnipingWeapon(w)) {
                return true;
            }
        }
        if (targetEnemy != null && getAgentMemory().isAboveMe(targetEnemy.getLocation())) {
            return false;
        }
        return true;
    }

    public static void main(String args[]) throws PogamutException {

        if (args.length > 0 && args[0].equals("retrace")) {
            new UT2004BotRunner(HumanRetraceBot.class, "HumanRetraceBot").startAgent();
        } else {
            // java -jar 00-EmptyBot.jar <network.xml> <host> <port>
            switch (args.length) {
                case 0:
                    while (true) {
                        try {
                            System.out.println("Launch bot");
                            launchBot();
                        } catch (Exception e) {
                            System.out.println("Connection broken, try resetting bot");
                        }
                    }
                //break;
                case 1:
                    //launchBot(args[0]);
                    //break;
                    //case 2:
                    //launchBot(args[0], Integer.parseInt(args[1]));
                    //break;
                    //case 3:
                    while (true) {
                        try {
                            System.out.println("Launch bot");
                            launchBot(DEFAULT_FILE, 0, args[0]);
                        } catch (Exception e) {
                            System.out.println("Connection broken, try resetting bot");
                        }
                    }
                //launchBot(args[0], Integer.parseInt(args[1]), args[2]);
                //break;
                case 4:
                    launchBot(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
                    break;
                default:
                    System.out.println("Improper number of arguments");
                    System.out.println("Test one bot:");
                    System.out.println("       java -jar 04-Hunter.jar [battleController.xml [numOpponents [host [port]]]]");
                    System.out.println("Evolve bots:");
                    System.out.println("       java -jar 04-Hunter.jar evolve generations:int population:int repetitions:int controller:EvolvableController descriptor:<BaseExperimentDescriptor> [descriptor args]");
            }
        }
    }

    public static void launchBot() {
        PogamutPlatform pl = Pogamut.getPlatform();
        launchBot(DEFAULT_FILE, 0,
                pl.getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
                pl.getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
    }

    public static void launchBot(String battleControllerFile) {
        PogamutPlatform pl = Pogamut.getPlatform();
        launchBot(battleControllerFile, 0,
                pl.getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
                pl.getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
    }

    public static void launchBot(String battleControllerFile, int opponents) {
        PogamutPlatform pl = Pogamut.getPlatform();
        launchBot(battleControllerFile, opponents,
                pl.getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
                pl.getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
    }

    public static void launchBot(String battleControllerFile, int opponents, String host) {
        PogamutPlatform pl = Pogamut.getPlatform();
        launchBot(battleControllerFile, opponents, host,
                pl.getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
    }

    public static void launchBot(String battleControllerFile, int opponents, String host, int port) {
        System.out.println("Loading from file: " + battleControllerFile);
        TWEANNController controller = (TWEANNController) Easy.load(battleControllerFile);
        controller.filename = battleControllerFile;
        launchBot(controller, opponents, host, port);
    }

    public static void launchBot(TWEANNController battleController) {
        PogamutPlatform pl = Pogamut.getPlatform();
        launchBot(battleController, 0,
                pl.getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
                pl.getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
    }

    public static void launchBot(TWEANNController battleController, int opponents) {
        PogamutPlatform pl = Pogamut.getPlatform();
        launchBot(battleController, opponents,
                pl.getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
                pl.getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
    }

    public static void launchBot(TWEANNController battleController, int opponents, String host, int port) {
        launchBot(battleController, null, opponents, host, port);
    }

    public static void launchBot(TWEANNController battleController, Class<? extends IUT2004BotController> opponentClass, int opponents) {
        launchBot(battleController, opponentClass, opponents, 0, null);
    }

    public static void launchBot(TWEANNController battleController, Class<? extends IUT2004BotController> opponentClass, int opponents, int nativeBots, IUT2004Server server) {
        PogamutPlatform pl = Pogamut.getPlatform();
        launchBot(battleController, opponentClass, opponents, nativeBots, server,
                pl.getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey()),
                pl.getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey()));
    }

    public static void launchBot(TWEANNController battleController, Class<? extends IUT2004BotController> opponentClass, int opponents, String host, int port) {
        launchBot(battleController, opponentClass, opponents, 0, null, host, port);
    }

    public static void launchBot(TWEANNController battleController, Class<? extends IUT2004BotController> opponentClass, int opponents, int nativeBots, IUT2004Server server, String host, int port) {
        long start = System.currentTimeMillis();
        System.out.println(port + ":launchBot at: " + start);

        UT2004BotDescriptor ut2 = new UT2004BotDescriptor().setController(UT2.class).addParams(new UT2Parameters(battleController, server).setAgentId(new AgentId(Constants.BOT_NAME.get())));

        // Add native bots
        //if (server != null) {
        //    for (int i = 0; i < nativeBots; i++) {
        //        server.getAct().act(new AddBot("Native(" + i + ")", null, null, 4, null));
        //    }
        //}

        UT2004BotDescriptor opps = new UT2004BotDescriptor().setController(opponentClass);
        for (int i = 0; i < opponents; i++) {
            opps.addParams(new UT2004AgentParameters().setAgentId(new AgentId(opponentClass.getSimpleName() + "(" + i + ")")));
        }
        // Run bots
        MultipleUT2004BotRunner runner = new MultipleUT2004BotRunner("Multiple").setHost(host).setPort(port);

        System.out.println("Launch single bot: UT^2");
        try {
            runner.setMain(true).startAgents(ut2);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + " in launchBot()");
        }
    }

    private void initActionLog(String battleControllerFile) {
        // Give a cachedName below
        actionLogFilename = null;
        this.battleController.init();
        if ((battleControllerFile != null && !battleControllerFile.equals(""))) {
            //Action logging
            String filename = battleControllerFile.replace('/', '_').replace('\\', '_') + ".log";
            actionLogFilename = filename;
        }

    }
}
