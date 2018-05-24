package edu.utexas.cs.nn.bots;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.KefikRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueNavigator;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.utils.exception.PogamutException;
import edu.utexas.cs.nn.Constants;
import mockcz.cuni.amis.pogamut.base.agent.navigation.PathPlanner;
import mockcz.cuni.amis.pogamut.ut2004.agent.navigation.MyUTPathExecutor;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Pogamut's "Hello world!" example showing few extra things such as introspection
 * and various bot-initializing methods.
 *
 * @author Michal Bida aka Knight
 * @author Rudolf Kadlec aka ik
 * @author Jakub Gemrot aka Jimmy
 */
@AgentScoped
public class BaseBot extends UT2004BotModuleController<UT2004Bot> {

    public static final boolean KILL_AT_END = true;
    //public MyAgentStats mystats = null;
    public static boolean evolving = false;
    public static boolean limitedEvaluation = false; // Switch to terminate after fixed time
    public static double evalTime = 500; // Used for limited evaluations
    public double startTime = -1;
    private AgentBody agentbody;
    private AgentMemory agentmemory;
    private UnrealId lastNavPointId = null;
    public int sameNavInstances = 0;
    public static final int SAME_NAV_ALLOWANCES = 15; //12;   // This may need to be different for each level
    protected int logicCycleCounter = 0;
    protected IUT2004Server server = null;
    public static final long KILL_TIME = 4000;

    public String getServerIdentifier() {
        if (server != null) {
            return "" + server.hashCode();
        }
        return "UNKNOWN";
    }

    public static String randomSkin() {
        String[] skins = new String[]{"HumanMaleA.NightMaleA",
            "HumanMaleA.NightMaleB",
            "HumanMaleA.MercMaleA",
            "HumanMaleA.MercMaleB",
            "HumanMaleA.MercMaleC",
            "HumanMaleA.MercMaleD",
            "HumanMaleA.EgyptMaleA",
            "HumanMaleA.EgyptMaleB",
            "HumanFemaleA.MercFemaleA",
            "HumanFemaleA.MercFemaleB",
            "HumanFemaleA.MercFemaleC",
            "HumanFemaleA.NightFemaleA",
            "HumanFemaleA.NightFemaleB",
            "HumanFemaleA.EgyptFemaleA",
            "HumanFemaleA.EgyptFemaleB",
            "Aliens.AlienMaleA",
            "Aliens.AlienMaleB",
            "Aliens.AlienFemaleA",
            "Aliens.AlienFemaleB",
            "Bot.BotA",
            "Bot.BotB",
            "Bot.BotC",
            "Bot.BotD",
            "Jugg.JuggMaleA",
            "Jugg.JuggMaleB",
            "Jugg.JuggFemaleA",
            "Jugg.JuggFemaleB"};

        return skins[(int) (Math.random() * skins.length)];
    }

    public UnrealId getId() {
        return this.info.getId();
    }

    public AgentBody getAgentBody() {
        return (agentbody == null ? agentbody = new AgentBody(this.body, this.raycasting, this.act, this.logicModule, this.info, this.senses, this.game, this.getWorldView(), this.items, this.weaponry) : agentbody);
    }

    /** timing for the logic */
    private long[] timing = new long[10];

    /** timing index */
    private int itime = 0;
    private int itime_ended = 0;

    protected void timingStart() {
        if (Constants.TIMING.getBoolean()) {
            if (itime_ended > 0) {
                timing[itime++] = timing[itime_ended];
                timing[itime++] = System.currentTimeMillis();
            }
        }
    }

    protected void timingStep() {
        if (Constants.TIMING.getBoolean()) {
            timing[itime++] = System.currentTimeMillis();
        }
    }

    protected void timingEnd() {
        if (Constants.TIMING.getBoolean()) {
            StringBuffer sb = new StringBuffer("timing");
            for (int i = 0; i < itime - 1; i++) {
                sb.append(":");
                sb.append(timing[i + 1] - timing[i]);
            }
            System.out.println(sb);
            itime_ended = itime - 1;
            itime = 0;
        }
    }

    public AgentMemory getAgentMemory() {
        if (agentmemory == null) {
            // FIXME: hiding a field - do we need to use the native FloydWarshall?
            FloydWarshallMap fwMap = new FloydWarshallMap(bot);
            fwMap.refreshPathMatrix();
            //this.pathExecutor = new UT2004PathExecutor<ILocated>(bot, new LoqueNavigator<ILocated>(bot, new MySimplePathRunner(bot, info, move, log), log));
            IUnrealPathExecutor<ILocated> itemPathExecutor = new UT2004PathExecutor<ILocated>(bot, new LoqueNavigator<ILocated>(bot, new KefikRunner(bot, this.getInfo(), this.getMove(), this.getLog()), this.getLog()));
            IUnrealPathExecutor<ILocated> playerPathExecutor = new UT2004PathExecutor<ILocated>(bot, new LoqueNavigator<ILocated>(bot, new KefikRunner(bot, this.getInfo(), this.getMove(), this.getLog()), this.getLog()));
            //this.pathExecutor = new UT2004PathExecutor<ILocated>(bot, new LoqueNavigator<ILocated>(bot, new KefikRunner(bot, info, move, log), log));
            //agentmemory = new AgentMemory(getAgentBody(), this.info, this.senses, this.players, new PathPlanner(this.pathPlanner, getAgentBody()), new MyUTPathExecutor(this.pathExecutor, getAgentBody()), this.items, this.weaponry, this.getWorldView(), this.game);
            agentmemory = new AgentMemory(getAgentBody(), this.info, this.senses, this.players, new PathPlanner(this.pathPlanner, fwMap, getAgentBody()), new MyUTPathExecutor(itemPathExecutor, getAgentBody()), new MyUTPathExecutor(playerPathExecutor, getAgentBody()), this.items, this.weaponry, this.getWorldView(), this.game);
        }
        return agentmemory;
    }

    /**
     * Main method that controls the bot - makes decisions what to do next.
     * It is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini file in
     * UT2004/System folder.
     *
     * @throws cz.cuni.amis.pogamut.base.exceptions.PogamutException
     */
    @Override
    public void logic() throws PogamutException {
        logicCycleCounter++;

        //Collection<NavPoint> x = world.getAll(NavPoint.class).values();
        //System.out.println(x.size() + ":" + x);
        NavPoint nearest = DistanceUtils.getNearest(world.getAll(NavPoint.class).values(), bot);
        if (nearest != null && nearest.getId() != null) {
            if (lastNavPointId != null && lastNavPointId.equals(nearest.getId())) {
                sameNavInstances++;
            } else {
                lastNavPointId = nearest.getId();
                sameNavInstances = 0;
            }
        } else {
            sameNavInstances = 0;
        }
        //System.out.println("sameNavInstances: " + sameNavInstances + ":" + lastNavPointId);

        if (limitedEvaluation) {
            if (startTime < 0) {
                startTime = this.game.getTime();
                //System.out.println("New start at: " + startTime);
            }
            double elapsed = this.game.getTime();
            if ((elapsed - startTime) > evalTime) { // || allDie) { // Terminate evaluation
                //System.out.println(info.getName() + ": Evaluation time is up: " + elapsed);
                // schrum2: 5/17/12: Eval time only ends evals in evolution
//                if (mystats != null) {
//                    mystats.evalTime = (elapsed - startTime);
//                    destroyAgent();
//                }

                // schrum2: 5/17/12: Bots and servers are only killed in evolution
//                if (KILL_AT_END) {
//                    if (server != null) {
//                        MultiThreadExperimentRunner.killServerAgents(server, false);
//                    }
//                    BotKiller.killBot(bot);
//                }
            }
        }
    }

    @Override
    public void botShutdown() {
        super.botShutdown();
        destroyAgent();
    }

    private void destroyAgent() {
        //if(mystats != null) mystats.loseAgentRef();
        agentmemory = null;
        agentbody = null;
    }
}
