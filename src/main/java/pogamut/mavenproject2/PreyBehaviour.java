package pogamut.mavenproject2;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.sposh.SPOSHAction;
import cz.cuni.amis.pogamut.sposh.SPOSHSense;
import cz.cuni.amis.pogamut.sposh.context.UT2004Behaviour;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.ut2004.SposhLogicController;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Behaviour definition for prey bot.
 * 
 * @author Honza
 */
public class PreyBehaviour extends UT2004Behaviour<UT2004Bot> {	
	
	TabooSet<Item> unreachable = null;
	
	Item runningToHealth;
	
	UT2004PathAutoFixer autoFixer;
	
    public PreyBehaviour(String name, UT2004Bot bot) {
        super(name, bot);
        // IMPORTANT: modules won't work without call of this method.
        initializeBehaviour(bot);
    }

    /**
     * Called from {@link PreyLogic#prepareBot(UT2004Bot)}.
     */
    @Override
    protected void prepareBehaviour(UT2004Bot bot) {
    	// initialize your custom data structures here
    	// things like agent modules / TabooSet(s), etc. / weaponPrefs
    	
    	unreachable = new TabooSet<Item>(bot);
    }
    
    /**
     * Called from {@link PreyLogic#botInitialized(GameInfo, ConfigChange, InitedMessage)}.
     */
    @Override
    public void botInitialized(GameInfo info, ConfigChange config, InitedMessage init) {
    	// This is a good place to setup SPOSH engine log level
    	bot.getLogger().getCategory(SposhLogicController.SPOSH_LOG_CATEGORY).setLevel(Level.ALL);
    	
    	pathExecutor.addStuckDetector(new UT2004TimeStuckDetector(bot, 3000, 10000)); // if the bot does not move for 3 seconds, consider that it is stuck / if bot waits for more than 10 seconds, consider that is is stuck
        pathExecutor.addStuckDetector(new UT2004PositionStuckDetector(bot));   // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
        pathExecutor.addStuckDetector(new UT2004DistanceStuckDetector(bot)); // watch over distances to target
        
        autoFixer = new UT2004PathAutoFixer(bot, pathExecutor, fwMap, aStar, navBuilder); // auto-removes wrong navigation links between navpoints
    	
        pathExecutor.getState().addListener(new FlagListener<IPathExecutorState>() {			

			@Override
			public void flagChanged(IPathExecutorState changedValue) {
				switch(changedValue.getState()) {
				case PATH_COMPUTATION_FAILED:
				case STUCK:
					if (runningToHealth != null) {
						unreachable.add(runningToHealth, 60); // ban the item for 60 secs.
						runningToHealth = null;
					}					
					return;
				}				
			}
        	
        });
        
    	// This is a correct place to use navBuilder (for instance)
    }

    /**
     * Called from {@link PreyLogic#botSpawned(GameInfo, ConfigChange, InitedMessage, Self)}.
     */
    @Override
    public void botSpawned(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
    	// "BotFirstSpawned" will be called only once.
    	
        // examine 'self' to examine current bot's location and other stuff, initialize things 
    	// that depends on that
    }
    
    /**
     * Called every time this bot dies from {@link PreyLogic#botKilled(BotKilled)}.
     */
    @Override
	public void botKilled(BotKilled event) {
    	// bot was killed, clean up your data structures
	}
    
    @EventListener(eventClass=ItemPickedUp.class)
    public void itemPickedUpListener(ItemPickedUp event) {
    	log.warning("PICKED UP: " + event.toString());
    }
    
    @ObjectClassEventListener(objectClass=Item.class, eventClass=WorldObjectUpdatedEvent.class)
    public void itemUpdated(WorldObjectUpdatedEvent<Item> itemEvent) {
    	log.warning("UPDATED: " + itemEvent.getObject());
    }
    
    //
    // ============
    // SPOSH SENSES
    // ============
    //
    
    /**
     * hitWall sense, used when bot bumps into wall due to
     * errors in pathfinding (like railing)
     */
    @SPOSHSense
    public boolean hitWall() {
    	// it is adviced to always log your senses in order for you to get a grasp on how SPOSH is evaluating the plan
    	log.info("hitWall() = " + senses.isColliding());
        return senses.isColliding();
    }

    /**
     * Get health of bot
     * @return how many points of health does bot have
     */
    @SPOSHSense
    public int health() {
        // it is adviced to always log your senses in order for you to get a grasp on how SPOSH is evaluating the plan
        log.info("health() = " + info.getHealth());
        return info.getHealth();
    }
    
    /**
     * Standard sense that has to be implemented everywhere.
     * @return false
     */
    @SPOSHSense
    public boolean fail() {
    	// it is adviced to always log your senses in order for you to get a grasp on how SPOSH is evaluating the plan
    	log.info("fail() = false");
        return false;
    }

    /**
     * Standard sense that has to be implemented everywhere.
     * @return true
     */
    @SPOSHSense
    public boolean succeed() {
    	// it is adviced to always log your senses in order for you to get a grasp on how SPOSH is evaluating the plan
    	log.info("succeed() = true");
        return true;
    }
    
    //
    // =============
    // SPOSH ACTIONS
    // =============
    //

    /**
     * Standard action that has to be implemented everywhere. Sleep for 250ms.
     * @return true
     */
    @SPOSHAction
    public ActionResult doNothing() {
    	// it is adviced to always log your actions as well in order for you to get a grasp on how SPOSH is evaluating the plan
    	log.info("doNothing()");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
        return ActionResult.FINISHED;
    }
    
    /**
     * Run from one health vial to another using list of health vials. 
     * No intelligence involved, doesn't matter if they are spawned or far away.
     * @return
     */
    @SPOSHAction
    public ActionResult runMedkits() {
    	// it is adviced to always log your actions as well in order for you to get a grasp on how SPOSH is evaluating the plan
    	log.info("runMedkits()");
    	
    	Item item = runningToHealth 
    		= fwMap.getNearestItem(
    				unreachable.filter(
    						items.getSpawnedItems(ItemType.Category.HEALTH).values()
    				), 
    				info.getNearestNavPoint()
    		  );
    	if (item == null) {
    		log.severe("No known spawned pickup!");
    		navigation.stopNavigation();
    		return ActionResult.FAILED;    		
    	}
    	
    	log.warning("GOING FOR ITEM: " + item);
    	log.warning("ITEM VISIBLE:   " + item.isVisible());
    	navigation.navigate(item);

        return ActionResult.RUNNING;
    }

    

    @SPOSHAction
    public ActionResult jump() {
    	// it is adviced to always log your actions as well in order for you to get a grasp on how SPOSH is evaluating the plan
    	log.info("jump()");
        move.jump();
		return ActionResult.RUNNING_ONCE;                
    }
    
    //
    // =====================
    // ADDITIONAL JAVA LOGIC
    // =====================
    //

    /**
     * This method can be used to execute anything that needs to be done BEFORE the plan evaluation takes place. E.g. {@link UT2004BotModuleController#logic()} 
     * method.
     */
    @Override
    public void logicBeforePlan() {   
    	log.info("--- LOGIC ITERATION ---");
    }
    
    /**
     * This method can be used to execute anything that needs to be done AFTER the plan evaluation takes place. E.g. {@link UT2004BotModuleController#logic()} 
     * method.
     */
    @Override
    public void logicAfterPlan() {
    	log.info("/// LOGIC END ///");
    }
}
