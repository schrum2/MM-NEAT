package pogamut.SPOSHprey;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.sposh.context.UT2004Behaviour;
import cz.cuni.amis.pogamut.sposh.executor.BehaviorWorkExecutor;
import cz.cuni.amis.pogamut.sposh.ut2004.SposhLogicController;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * @author Honza
 */
public class PreyLogic extends SposhLogicController<UT2004Bot, BehaviorWorkExecutor> {
    private String SPOSH_PLAN_RESOURCE = "sposh/plan/BotPlan.lap";

    private PreyBehaviour behaviour;

    /**
     * Note that this method was originally "createBehaviour(UT2004Bot bot)".
     * Also, if your behavior is relying on some modules (e.g. {@link AgentInfo}),
     * you have to call {@link UT2004Behaviour#initializeBehaviour(cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot) },
     * in which the modules are initialized. {@link PreyBehaviour} is calling the method in the constructor.
     * @param bot
     */
    @Override
    public void initializeController(UT2004Bot bot) {
        super.initializeController(bot);
        behaviour = new PreyBehaviour("preyBehaviour", bot); 
    }
    
    @Override
    public void prepareBot(UT2004Bot bot) {
    	super.prepareBot(bot);
    	behaviour.prepareBehaviour(bot);
    }

    @Override
    protected List<String> getPlans() throws IOException {
        return MyCollections.toList(getPlanFromResource(SPOSH_PLAN_RESOURCE));
    }

    /**
     * Note that this method was introduced in Pogamut 3.1 as we have swtiched from Python SPOSH engine to Java SPOSH engine
     * that allows you to have your own IWorkExecutor implementation (such as {@link StateWorkExecutor).
     * <p><p>
     * Because we wanted to maintain old-fashion "behaviour" style of Prey sample, we're using {@link BehaviorWorkExecutor} here that
     * is configured with 'behaviour' previously created by {@link PreyLogic#initializeController(cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot) },
     * which means that SPOSH primitives (actions/senses) are taken from this object (behaviour).
     * @return
     */
    @Override
    protected BehaviorWorkExecutor createWorkExecutor() {
        return new BehaviorWorkExecutor(behaviour);
    }

    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName("SPOSH-Prey");
    }
    
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
    	behaviour.botInitialized(gameInfo, currentConfig, init);
    }
    
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init, Self self) {
    	behaviour.botSpawned(gameInfo, currentConfig, init, self);
    }
    
    @Override
    public void finishControllerInitialization() {
    	super.finishControllerInitialization();
    	behaviour.finishBehaviourInitialization();
    }

    @Override
    public void botKilled(BotKilled event) {
        behaviour.botKilled(event);
    }

    /**
     * Create an {@link ExternalBot} with custom made logic and
     * try to connect to Unreal Server at localhost:3000
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws PogamutException {
    	new UT2004BotRunner(PreyLogic.class, "SPOSH-Prey").setMain(true).setLogLevel(Level.FINE).startAgent();    	
    }

}