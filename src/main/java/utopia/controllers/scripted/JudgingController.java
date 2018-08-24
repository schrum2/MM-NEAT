package utopia.controllers.scripted;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import java.util.HashMap;
import java.util.Map;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.agentmodel.Controller;
import utopia.agentmodel.actions.Action;
import utopia.agentmodel.actions.ApproachEnemyAction;
import utopia.agentmodel.actions.StillAction;

public class JudgingController extends Controller {

    public static final int REQUIRED_JUDGING_HITS = 1; //3;
    public static final int MIN_INTERACTION_TIME_STEPS = 75; //100; // Igor thought 100 was too much
    public static final int MAX_JUDGING_DISTANCE = 2500;
    public static final double HUMAN_BIAS_FACTOR = 1.35;
    public HashMap<String, Integer> judgments;
    public HashMap<String, Integer> sightings;
    public HashMap<String, String> assignedLabels;
    public HashMap<String, Boolean> pendingJudgement;
    public HashMap<String, Integer> playerScores;
    public final AgentBody body;
    public static final String HUMAN = "Human";
    public static final String BOT = "Bot";
    public static final String UNKNOWN = "Unknown";
    private final Double matchTime;
    public Player judgeTarget = null;
    private boolean judgeAsHuman = false;
    public double lastJudgmentTime;
    public int currentJudgingHits = 0;
    private final Controller battleController;

    private boolean enoughInteractionForAnotherJudgment(int numSightings, int numJudgments) {
        //System.out.println("enoughInteractionForAnotherJudgment: " + numSightings + " >= " + (MIN_INTERACTION_TIME_STEPS * (numJudgments + 1)));
        return numSightings >= (MIN_INTERACTION_TIME_STEPS * Math.pow(numJudgments + 1, 2));
    }

    public boolean judgingTargetAvailable(AgentMemory memory) {
        if (judgeTarget != null) {
            String key = judgeTarget.getId().getStringId();
            Integer numJudgments = judgments.get(key);
            Integer numSightings = sightings.get(key);

            if (numSightings == null || numJudgments == null) {
                return false;
            }
            // The amount of interaction required for judging increases for
            // targets that have already been judged. Bots can change their minds
            if (!enoughInteractionForAnotherJudgment(numSightings, numJudgments)) {
                return false;
            }

            Player vis = memory.players.getVisiblePlayer(judgeTarget.getId());
            if (vis != null) {
                // Makes bot focus on this opponent
                memory.lastPlayerDamaged = vis.getId();
                return true;
            }
            return false;
        }
        return false;
    }

    public JudgingController(AgentMemory memory, Controller battleController) {
        super();
        this.battleController = battleController;
        this.body = memory.body;
        this.pendingJudgement = new HashMap<String, Boolean>();
        this.judgments = new HashMap<String, Integer>();
        this.judgments.put("SELF", 1);
        this.sightings = new HashMap<String, Integer>();
        this.playerScores = new HashMap<String, Integer>();
        this.assignedLabels = new HashMap<String, String>();
        this.assignedLabels.put("SELF", BOT);
        this.matchTime = memory.game.getTimeLimit();
        this.lastJudgmentTime = 0;

        memory.world.addEventListener(PlayerDamaged.class, playerDamagedHandler);
        memory.world.addEventListener(BotDamaged.class, botDamagedHandler);
        memory.world.addEventListener(PlayerScore.class, globalListener);
    }

    public void onPlayerDamaged(PlayerDamaged pd) {
        //System.out.println(pd);
        //System.out.println("judgeTarget: " + judgeTarget);
        if (pd == null || pd.getDamageType() == null || judgeTarget == null) {
            return;
        }

        //System.out.println("pd.getDamageType(): " + pd.getDamageType());
        // Bot judged target
        //if (pd.getDamageType().equals("GameBots2004.BotPrizeDamageType")) {
        if (pd.getDamageType().equals("XWeapons.DamTypeLinkPlasma")) {
            System.out.println("JUDGE: Hit " + pd.getId().getStringId() + " With Judging Gun");
            currentJudgingHits++;
            if (currentJudgingHits > REQUIRED_JUDGING_HITS) {
                System.out.println("JUDGE: Done judging " + pd.getId().getStringId());
                currentJudgingHits = 0;
                lastJudgmentTime = body.game.getTime();
                assignLabel(pd.getId(), this.judgeAsHuman ? HUMAN : BOT);
            }
        }
    }
    IWorldEventListener<PlayerDamaged> playerDamagedHandler = new IWorldEventListener<PlayerDamaged>() {

        @Override
        public void notify(PlayerDamaged pd) {
            onPlayerDamaged(pd);
        }
    };
    IWorldEventListener<BotDamaged> botDamagedHandler = new IWorldEventListener<BotDamaged>() {

        @Override
        public void notify(BotDamaged bd) {
            if (bd == null || bd.getDamageType() == null) {
                return;
            }

            if (bd.getDamageType().equals("GameBots2004.BotPrizeDamageType")) {
                System.out.println("JUDGE: Opponent Judged Me: " + (bd.getInstigator() == null ? "???" : bd.getInstigator().getStringId()));
            }
        }
    };
    IWorldEventListener<PlayerScore> globalListener = new IWorldEventListener<PlayerScore>() {

        @Override
        public void notify(PlayerScore event) {
            playerScores.put(event.getId().getStringId(), event.getScore());
        }
    };

    @Override
    public Action control(AgentMemory memory) {
        judgeAsHuman = isHuman(memory);
        Weapon w = memory.weaponry.getCurrentWeapon();
        if (w == null || !w.getType().equals(UT2004ItemType.LINK_GUN)) {
            memory.changeWeapon(UT2004ItemType.LINK_GUN);
            memory.linkGunSwitchTime = memory.game.getTime();
        }
        Player target = (judgingTargetAvailable(memory) ? judgeTarget : memory.getCombatTarget());
        setJudgingTarget(memory, target);
        if (memory.info.getLocation() != null
                && judgeTarget != null
                && judgeTarget.getLocation() != null
                && memory.info.getLocation().getDistance(judgeTarget.getLocation()) > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2) {
            return new ApproachEnemyAction(memory, true, judgeAsHuman, false, false);
        } else {
            //return new StillAction(memory, true, judgeAsHuman);
            return battleController.control(memory);
        }
    }

    public void setJudgingTarget(AgentMemory memory, Player target) {
        judgeTarget = target;
        memory.setJudgingTarget(judgeTarget);
    }

    @Override
    public void reset() {
        judgeTarget = null;
        currentJudgingHits = 0;
    }

    private void firstSightingCheck(String key) {
        if (!judgments.containsKey(key)) {
            judgments.put(key, 0);
        }
        if (!sightings.containsKey(key)) {
            sightings.put(key, 0);
        }
        if (!assignedLabels.containsKey(key)) {
            assignedLabels.put(key, UNKNOWN);
        }
    }

    private boolean seeTargetPendingJudgement(AgentMemory memory) {
        Map<UnrealId, Player> enemies = memory.players.getVisibleEnemies();
        for (Player e : enemies.values()) {
            String key = e.getId().getStringId();
            Boolean isPending = this.pendingJudgement.get(key);

            if (isPending != null && isPending) {
                setJudgingTarget(memory, e);
                System.out.println(e.getName() + " is pending judgment");
                return true;
            }
        }
        return false;
    }

    public boolean shouldJudge(AgentMemory memory) {
        if (this.seeTargetPendingJudgement(memory)) {
            // This resets the judgeTarget on its own
            System.out.println("Target is pending judgment");
            return true;
        }

        Player nearest = memory.info.getNearestVisiblePlayer();
        if (nearest != null) {
            String key = nearest.getId().getStringId();
            String weapon = nearest.getWeapon();
            firstSightingCheck(key);

            int numSightings = sightings.get(key) + 1;
            sightings.put(key, numSightings);

            Location loc = nearest.getLocation();
            if (loc == null || loc.getDistance(memory.info.getLocation()) > MAX_JUDGING_DISTANCE) {
                return false;
            }

            Integer numJudgments = judgments.get(key);
            if (enoughInteractionForAnotherJudgment(numSightings, numJudgments)) {
                Double rt = memory.game.getRemainingTime();
                if (rt == null) {
                    System.out.println("Judge because remaining time is null");
                    return true;
                }
                double remainingTime = rt.doubleValue();
                boolean shouldJudge = timeForJudgement(numSightings, remainingTime, weapon);
                if (shouldJudge) {
                    System.out.println("Make " + nearest.getName() + " new pending judgment target");
                    setJudgingTarget(memory, nearest);
                    pendingJudgement.put(key, true);
                }
                return shouldJudge;
            }
        } else {
            if (memory.info.isShooting() && memory.weaponry.getCurrentWeapon().getType().equals(UT2004ItemType.LINK_GUN)) {
                body.stopShoot();
            }
        }
        return false;
    }

    private boolean timeForJudgement(int numSightings, double remainingTime, String weapon) {
        if (numSightings < MIN_INTERACTION_TIME_STEPS) {
            return false;
        }
        if (remainingTime == 0) {
            return true; // Should be impossible
        }

        double pJudge = 1.0 - (Math.pow(0.99, numSightings - MIN_INTERACTION_TIME_STEPS));

        if (matchTime != null) {
            double portionTimeLeft = remainingTime / matchTime;
            pJudge *= (1.0 - portionTimeLeft);
        }
        // Humans more likely to use judging gun
        if (weapon.equals("XWeapons.LinkGun")) {
            pJudge += (1.0 - pJudge) / 2.0;
        }
        return (Math.random() < pJudge);
    }

    private boolean isHuman(AgentMemory memory) {
        double pHuman = probabilityTargetIsHuman(memory);
        if (pHuman == 0.0) {
            return false;
        }
        Player target = (judgingTargetAvailable(memory) ? judgeTarget : memory.getCombatTarget());
        if (target == null) {
            return false;
        }
        String key = target.getId().getStringId();
        String currentLabel = this.assignedLabels.get(key);
        if (currentLabel != null) {
            // If already judged, then change the label
            if (currentLabel.equals(BOT)) {
                return true;
            } else if (currentLabel.equals(HUMAN)) {
                return false;
            }
        }
        if (target != null) {
            String weapon = target.getWeapon();

            boolean judging = (weapon != null && weapon.equals("XWeapons.LinkGun"));
            boolean judgingMeAsBot = (target.getFiring() == 1);
            if (judging && judgingMeAsBot) {
                return true;
            }

            boolean notFiring = (target.getFiring() == 0);
            boolean notMoving = (target.getVelocity() != null && target.getVelocity().isZero());
            boolean sniping = weapon != null && (weapon.equals("XWeapons.SniperRifle") || weapon.equals("UTClassic.ClassicSniperRifle"));

            // Probably a stuck bot
            if (notFiring && notMoving && !sniping) {
                return false;
            }
        }

        // Err on the side of human simply because judge will expect to be judged
        // correctly by human (HUMAN_BIAS_FACTOR > 1.0)
        return Math.random() < (pHuman * HUMAN_BIAS_FACTOR);
    }

    private int numberAssigned(String type) {
        int result = 0;
        for (String judgement : assignedLabels.values()) {
            if (judgement.equals(type)) {
                result++;
            }
        }
        return result;
    }

    private int numberAssignedHumans() {
        return numberAssigned(HUMAN);
    }

    private int numberAssignedBots() {
        return numberAssigned(BOT);
    }

    private int numberPlayers(AgentMemory memory) {
        return memory.players.getPlayers().size();
    }

    private int numberUnassigned(AgentMemory memory) {
        return numberPlayers(memory) - (numberAssignedHumans() + numberAssignedBots());
    }

    private double expectedHumans(AgentMemory memory) {
        return numberPlayers(memory) / 2.0;
    }

    private double remainingHumans(AgentMemory memory) {
        double result = expectedHumans(memory) - numberAssignedHumans();
        return (Math.max(result, 0));   // Negative numbers should not happen
    }

    private double probabilityTargetIsHuman(AgentMemory memory) {
        return remainingHumans(memory) / (1.0 * numberUnassigned(memory));
    }

    private void assignLabel(UnrealId id, String classification) {
        assignLabel(id.getStringId(), classification);
    }

    private void assignLabel(String key, String classification) {
        int numJudgments = this.judgments.get(key);
        String label = this.assignedLabels.get(key);
        // The judgment has to change
        if (!label.equals(classification)) {
            this.judgments.put(key, numJudgments + 1);
            this.assignedLabels.put(key, classification);
        }
        this.pendingJudgement.put(key, false);
    }
}
