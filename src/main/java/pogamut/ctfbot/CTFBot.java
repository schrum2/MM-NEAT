package pogamut.ctfbot;

import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.module.comm.PogamutJVMComm;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.Heatup;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Example of Simple Pogamut bot, that randomly walks around the map searching
 * for preys shooting at everything that is in its way.
 * 
 * @author Rudolf Kadlec aka ik
 * @author Jimmy
 */
@AgentScoped
public class CTFBot extends UT2004BotModuleController<UT2004Bot> {

	/** boolean switch to activate engage behavior */
	@JProp
	public boolean shouldEngage = true;

	/** boolean switch to activate pursue behavior */
	@JProp
	public boolean shouldPursue = true;

	/** boolean switch to activate rearm behavior */
	@JProp
	public boolean shouldRearm = true;

	/** boolean switch to activate collect items behavior */
	@JProp
	public boolean shouldCollectItems = true;

	/** boolean switch to activate collect health behavior */
	@JProp
	public boolean shouldCollectHealth = true;

	/** how low the health level should be to start collecting health items */
	@JProp
	public int healthLevel = 90;

	/**
	 * how many bot the hunter killed other bots (i.e., bot has fragged them /
	 * got point for killing somebody)
	 */
	@JProp
	public int frags = 0;

	/** how many times the hunter died */
	@JProp
	public int deaths = 0;

	protected GameInfo gameInfo;

	@JProp
	protected Location pathTarget;

	/**
	 * Returns parameters of the bot.
	 * @return
	 */
	public CTFBotParams getParams() {
		if (!(bot.getParams() instanceof CTFBotParams)) return null;
		return (CTFBotParams)bot.getParams();
	}
	
	public Location getPathTarget() {
		return pathTarget;
	}

	/**
	 * {@link PlayerKilled} listener that provides "frag" counting + is switches
	 * the state of the hunter.
	 * 
	 * @param event
	 */
	@EventListener(eventClass = PlayerKilled.class)
	public void playerKilled(PlayerKilled event) {
		if (event.getKiller().equals(info.getId()))	++frags;
		if (enemy == null) return;
		if (enemy.getId().equals(event.getId())) {
			enemy = null;
		}
	}

	/**
	 * Used internally to maintain the information about the bot we're currently
	 * hunting, i.e., should be firing at.
	 */
	protected Player enemy = null;

	/**
	 * Taboo list of items that are forbidden for some time.
	 */
	protected TabooSet<Item> tabooItems = null;

	protected GetItems getItemsGoal;

	protected boolean firstLogic = true;

	private UT2004PathAutoFixer autoFixer;

	/**
	 * Bot's preparation - called before the bot is connected to GB2004 and
	 * launched into UT2004.
	 */
	@Override
	public void prepareBot(UT2004Bot bot) {
		tabooItems = new TabooSet<Item>(bot);

	    autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder); // auto-removes wrong navigation links between navpoints

		// listeners
		navigation.getPathExecutor().getState().addListener(
				new FlagListener<IPathExecutorState>() {
					@Override
					public void flagChanged(IPathExecutorState changedValue) {
						switch (changedValue.getState()) {
							case STUCK:
								Item item = getItemsGoal.getItem();
								if (item != null && pathTarget != null
										&& item.getLocation()
												.equals(pathTarget, 10d)) {
									tabooItems.add(item, 10);
								}
								reset();
								break;

							case TARGET_REACHED:
								reset();
								break;
						}
					}
				});

		// DEFINE WEAPON PREFERENCES
		weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);

		goalManager = new GoalManager(this.bot);

		goalManager.addGoal(new GetEnemyFlag(this));
		goalManager.addGoal(new GetOurFlag(this));
		goalManager.addGoal(new GetHealth(this));
		goalManager.addGoal(getItemsGoal = new GetItems(this));
		goalManager.addGoal(new CloseInOnEnemy(this));
	}

	@Override
	public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
		this.gameInfo = gameInfo;
	}

	/**
	 * Here we can modify initialization-command for our bot.
	 * @return
	 */
	@Override
	public Initialize getInitializeCommand() {
		if (getParams() == null) {
			return new Initialize();
		} else {
			return new Initialize().setDesiredSkill(getParams().getSkillLevel()).setSkin(getParams().getBotSkin()).setTeam(getParams().getTeam());
		}
	}
	
	@Override
	public void botFirstSpawn(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init, Self self) {
		PogamutJVMComm.getInstance().registerAgent(bot, self.getTeam());
	}
	
	@Override
	public void botShutdown() {
		PogamutJVMComm.getInstance().unregisterAgent(bot);
	}

	protected GoalManager goalManager = null;
	
	protected final Heatup targetHU = new Heatup(5000);

	public boolean goTo(ILocated target) {
		if (target == null) {
			log.info("goTo: null");
			return false;
		}
		log.info(String.format(
				"goTo: %s %s",
				target.toString(),
				info.getLocation()));

		pathTarget = target.getLocation();
		navigation.navigate(target);
		
		return true;
	}
	public boolean goTo(Location target) {

		if (target == null) {
			log.info("goTo: null");
			return false;
		}
		log.info(String.format(
				"goTo: %s %s",
				target.toString(),
				info.getLocation()));

		pathTarget = target.getLocation();
		navigation.navigate(target);
		
		return true;
	}

	public boolean holdingOrSupporting() {
		FlagInfo ourFlag = getOurFlag();

		if (ourFlag == null)
			return false;

		UnrealId holderId = ourFlag.getHolder();

		if (holderId == null)
			return false;

		if (info.getId().equals(holderId))
			return true;

		Player holder = players.getPlayer(holderId);

		if (holder.getTeam() == info.getTeam()
				&& getInfo().getDistance(holder) < 60d) {
			return true;
		}

		return false;
	}

	public void updateFight() {
		if (enemy == null || enemy.isVisible())
			enemy = (Player) getPlayers().getNearestVisibleEnemy();


		Player nearest_target = players.getNearestVisibleEnemy();

		if (enemy == null) {
			enemy = nearest_target;
		} else {
			if (nearest_target == enemy) {
				targetHU.heat();
			} else if (targetHU.isCool()) {
				enemy = nearest_target;
			}
		}

		shoot();
	}

	public void updateFight(Player newEnemy) {
		if (newEnemy == null || !newEnemy.isVisible())
			newEnemy = (Player) getPlayers().getNearestVisibleEnemy();

		enemy = newEnemy;

		shoot();
	}

	public void shoot() {
		if (enemy != null && enemy.isVisible())
			shoot.shoot(weaponPrefs, enemy);
		else {
			shoot.stopShoot();
			enemy = null;
		}
	}


	/**
	 * Resets the state of the Hunter.
	 */
	protected void reset() {
		notMoving = 0;
		enemy = null;
		navigation.stopNavigation();
	}

	/**
	 * Global anti-stuck mechanism. When this counter reaches a certain
	 * constant, the bot's mind gets a {@link CTFBot#reset()}.
	 */
	protected int notMoving = 0;

	/*
	 * protected IWorldEventListener<GameInfoMessage> gameInfoListener = new
	 * IWorldEventListener<GameInfoMessage>() {
	 * 
	 * @Override public void notify(GameInfoMessage event) {
	 * log.info(String.format("Glorious! %s", event.toString())); switch
	 * (getInfo().getTeam()) { case 0: ourBase = event.getRedBaseLocation();
	 * enemyBase = event.getBlueBaseLocation(); break; case 1: ourBase =
	 * event.getBlueBaseLocation(); enemyBase = event.getRedBaseLocation();
	 * break; } } };
	 */

	public NavPoint getOurFlagBase() {
		return ctf.getOurBase();
	}

	public NavPoint getEnemyFlagBase() {
		return ctf.getEnemyBase();
	}

	public FlagInfo getOurFlag() {
		return ctf.getOurFlag();
	}

	public FlagInfo getEnemyFlag() {
		return ctf.getEnemyFlag();
	}

	public Player getEnemy() {
		return enemy;
	}

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
	public void logic() {
		goalManager.executeBestGoal();
		
		log.info("OUR FLAG:                      " + ctf.getOurFlag());
		log.info("OUR BASE:                      " + ctf.getOurBase());
		log.info("CAN OUR TEAM POSSIBLY SCORE:   " + ctf.canOurTeamPossiblyScore());
		log.info("CAN OUR TEAM SCORE:            " + ctf.canOurTeamScore());
		log.info("CAN BOT SCORE:                 " + ctf.canBotScore());
		log.info("ENEMY FLAG:                    " + ctf.getEnemyFlag());
		log.info("ENEMY BASE:                    " + ctf.getEnemyBase());
		log.info("CAN ENEMY TEAM POSSIBLY SCORE: " + ctf.canEnemyTeamPossiblyScore());
		log.info("CAN ENEMY TEAM SCORE:          " + ctf.canEnemyTeamScore());
	}

	public TabooSet<Item> getTaboo() {
		return tabooItems;
	}


	// //////////
	// //////////////
	// BOT KILLED //
	// //////////////
	// //////////

	@Override
	public void botKilled(BotKilled event) {
		reset();		
	}

	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////

	public static void main(String args[]) throws PogamutException {
		// starts 2 or 4 CTFBots at once
		// note that this is the most easy way to get a bunch of bots running at the same time
		new UT2004BotRunner<UT2004Bot, UT2004BotParameters>(CTFBot.class, "CTFBot").setMain(true)
			.startAgents(
				new CTFBotParams().setBotSkin("HumanMaleA.MercMaleC")       .setSkillLevel(5).setTeam(0).setAgentId(new AgentId("CTFBot"))
				//,new CTFBotParams().setBotSkin("HumanFemaleA.MercFemaleA").setSkillLevel(5).setTeam(1).setAgentId(new AgentId("Team BLUE - Bot 1"))
				//,new CTFBotParams().setBotSkin("HumanMaleA.MercMaleA")    .setSkillLevel(5).setTeam(0).setAgentId(new AgentId("Team RED - Bot 2"))				
				//,new CTFBotParams().setBotSkin("HumanFemaleA.MercFemaleB").setSkillLevel(5).setTeam(1).setAgentId(new AgentId("Team BLUE - Bot 2"))
			);
		
	}

}
