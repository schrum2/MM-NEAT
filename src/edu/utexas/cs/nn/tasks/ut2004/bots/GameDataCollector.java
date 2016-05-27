package edu.utexas.cs.nn.tasks.ut2004.bots;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Game;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.*;
import edu.utexas.cs.nn.tasks.ut2004.Util;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Wrapper for Agent's collecting various statistical information. Eg.:
 * <ul>
 * <li>Damage caused</li>
 * <li>Players killed</li>
 * <li>Damage suffered</li>
 * <li>Number of times the bot hit a wall</li>
 * <li>TODO add more</li>
 * </ul>
 *
 * @author Ik
 */
public class GameDataCollector implements Serializable {

	public double evalTime = 0;
	private boolean successfulEval = false;
	public int timeNotMoving = 0;
	protected UnrealId lastPlayerKilled = null;
	protected double lastPlayerKilledTime = -1;
	protected int pickups = 0;
	protected int currentStreak = 0;
	protected int streak = 0;
	protected int enemyEscapes = 0;
	protected int frags = 0;
	protected int score = 0;
	protected int deaths = 0;
	protected int damageCaused = 0;
	protected int damageEvents = 0; // Number of times bot is responsible for
									// PlayerDamaged message
	protected int damageSuffered = 0;
	protected int wallHits = 0;
	protected int previousHealth = 100;
	protected Map<String, Integer> damageDone = new TreeMap<String, Integer>();
	protected Map<String, Integer> weaponDamage = new TreeMap<String, Integer>();
	protected Map<ItemType, Integer> ammoUsed = new TreeMap<ItemType, Integer>();
	protected Map<ItemType, Integer> previousAmmo = new TreeMap<ItemType, Integer>();
	protected int bumps = 0;
	protected int opponentSightings = 0;
	protected double focusAccumulation = 0.0;
	protected int bestEnemyScore = Integer.MIN_VALUE;
	protected AgentInfo info = null;
	protected Weaponry weaponry = null;

	public void unregisterListeners() {
		seePlayerListener = null;
		disappearPlayerListener = null;
		pickupListener = null;
		killListener = null;
		damageListener = null;
		botKillListener = null;
		botDamageListener = null;
		bumpedListener = null;
		wallCollisionListener = null;
		selfListener = null;
	}

	transient protected IWorldObjectEventListener<Player, WorldObjectUpdatedEvent<Player>> seePlayerListener;
	transient protected IWorldObjectEventListener<Player, WorldObjectDisappearedEvent<Player>> disappearPlayerListener;
	transient protected IWorldEventListener<ItemPickedUp> pickupListener;
	transient protected IWorldEventListener<PlayerKilled> killListener;
	transient protected IWorldEventListener<PlayerDamaged> damageListener;
	transient protected IWorldEventListener<BotKilled> botKillListener;
	transient protected IWorldEventListener<BotDamaged> botDamageListener;
	transient protected IWorldEventListener<Bumped> bumpedListener;
	transient protected IWorldEventListener<WallCollision> wallCollisionListener;
	transient protected IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> selfListener;

	public GameDataCollector() {
		frags = 0;
		// System.out.println("Reset frags: " + frags);
		damageDone = new TreeMap<String, Integer>();
		// System.out.println("Reset damageDone: " + damageDone);
		deaths = 0;
		// System.out.println("Reset deaths: " + deaths);
		wallHits = 0;
		// System.out.println("Reset wallHits: " + wallHits);
	}

	@Override
	public String toString() {
		return "Frags:" + frags + ",Deaths:" + deaths + ",Wall:" + wallHits + ",Best Enemy Score:" + bestEnemyScore();
	}

	public boolean evalWasSuccessful() {
		return this.successfulEval;
	}

	public void endEval(UT2004BotModuleController bot) {
		this.successfulEval = true;
		// Get best enemy score
		Map<UnrealId, Player> enemies = bot.getPlayers().getEnemies();
		bestEnemyScore = Integer.MIN_VALUE;
		for (UnrealId id : enemies.keySet()) {
			bestEnemyScore = Math.max(bestEnemyScore, getEnemyScore(bot.getGame(), id));
		}
	}

	public void registerListeners(final UT2004BotModuleController bot) {
		this.info = bot.getInfo();
		this.weaponry = bot.getWeaponry();
		// Set up initial ammo (for assault rifle only?)
		Map<ItemType, Integer> ammos = weaponry.getAmmos();
		for (ItemType weapon : ammos.keySet()) {
			ammoUsed.put(weapon, 0);
			previousAmmo.put(weapon, ammos.get(weapon));
		}

		// Bumped into other player
		bot.getWorld().addEventListener(Bumped.class, bumpedListener = new IWorldEventListener<Bumped>() {
			@Override
			public void notify(Bumped bump) {
				bumps++;
			}
		});

		// Other player disappears from view
		bot.getWorld().addObjectListener(Player.class, WorldObjectDisappearedEvent.class,
				disappearPlayerListener = new IWorldObjectEventListener<Player, WorldObjectDisappearedEvent<Player>>() {
					@Override
					public void notify(WorldObjectDisappearedEvent<Player> dp) {
						Player dis = dp.getObject();
						// Don't care if self disappears
						if (dis != null && info.getId().equals(dis.getId())) {
							return;
						}
						double diff = Math.abs(bot.getGame().getTime() - lastPlayerKilledTime);
						if (dis == null || lastPlayerKilled == null || !lastPlayerKilled.equals(dis.getId())
								|| diff > 15) {
							// System.out.println("Enemy escaped: " +
							// dis.getName());
							enemyEscapes++;
						}
					}
				});

		// Seeing other players
		bot.getWorld().addObjectListener(Player.class, WorldObjectUpdatedEvent.class,
				seePlayerListener = new IWorldObjectEventListener<Player, WorldObjectUpdatedEvent<Player>>() {
					@Override
					public void notify(WorldObjectUpdatedEvent<Player> sp) {
						Player player = sp.getObject();
						Location playerLocation = player.getLocation();
						Location agentLocation = info.getLocation();
						if (playerLocation == null || agentLocation == null) {
							return;
						}

						double angle = Util.relativeAngleToTarget(agentLocation, info.getRotation(), playerLocation);
						opponentSightings++;
						// The more the bot looks at the player, the smaller
						// this will be.
						// Goal is to minimize the angle, corresponds to
						// maximizing focus.
						focusAccumulation += Math.abs(angle);
					}
				});

		// counting picked up items
		bot.getWorld().addEventListener(ItemPickedUp.class, pickupListener = new IWorldEventListener<ItemPickedUp>() {
			public void notify(ItemPickedUp add) {
				// System.out.println("Item Picked Up");
				pickups++;
			}
		});

		// counting frags
		bot.getWorld().addEventListener(PlayerKilled.class, killListener = new IWorldEventListener<PlayerKilled>() {
			@Override
			public void notify(PlayerKilled killed) {
				// System.out.println("Player killed");
				if (killed == null || killed.getKiller() == null) {
					return;
				}
				// someone was killed
				lastPlayerKilled = killed.getId();
				lastPlayerKilledTime = bot.getGame().getTime();
				if (killed.getKiller().equals(info.getId())) {
					// our agent killed him
					frags++;
					currentStreak++;
					// TODO: Carefull, if someone else kills him, it won't get
					// reset. Problem?
					damageDone.put(killed.getId().getStringId(), 0);
				}
			}
		});

		// damage caused
		bot.getWorld().addEventListener(PlayerDamaged.class, damageListener = new IWorldEventListener<PlayerDamaged>() {
			@Override
			public void notify(PlayerDamaged damaged) {
				if (damaged == null) {
					return;
				}
				// System.out.println("Player damaged");
				if (!damaged.getId().equals(info.getId())) {
					damageEvents++;
					// agent hasn't damaged itself
					damageCaused += damaged.getDamage();
					int previousWeaponDamage = weaponDamage.get(damaged.getWeaponName()) == null ? 0
							: weaponDamage.get(damaged.getWeaponName());
					weaponDamage.put(damaged.getWeaponName(), previousWeaponDamage + damaged.getDamage());
					int previousDamage = damageDone.get(damaged.getId().getStringId()) == null ? 0
							: damageDone.get(damaged.getId().getStringId());
					damageDone.put(damaged.getId().getStringId(), previousDamage + damaged.getDamage());
				}
			}
		});

		// number of deaths
		bot.getWorld().addEventListener(BotKilled.class, botKillListener = new IWorldEventListener<BotKilled>() {
			@Override
			public void notify(BotKilled botKilled) {
				// System.out.println("Bot killed");
				deaths++;
				streak = currentStreak;
				currentStreak = 0;
				previousHealth = 100;
			}
		});

		// damage suffered
		bot.getWorld().addEventListener(BotDamaged.class, botDamageListener = new IWorldEventListener<BotDamaged>() {
			@Override
			public void notify(BotDamaged damaged) {
				// System.out.println("Bot damaged: " +
				// agent.agentmemory.game.getTime() + ":" + damaged);
				damageSuffered += damaged.getDamage();
			}
		});

		// wall hits
		bot.getWorld().addEventListener(WallCollision.class,
				wallCollisionListener = new IWorldEventListener<WallCollision>() {
					@Override
					public void notify(WallCollision wc) {
						// System.out.println("Wall hit");
						wallHits++;
					}
				});

		// Self info
		bot.getWorld().addObjectListener(Self.class, WorldObjectUpdatedEvent.class,
				selfListener = new IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>() {
					@Override
					public void notify(WorldObjectUpdatedEvent<Self> event) {
						Self self = event.getObject();
						previousHealth = self.getHealth();
						// Velocity
						if (self.getVelocity().isZero()) {
							timeNotMoving++;
						}

						Map<ItemType, Integer> ammos = weaponry.getAmmos();
						for (ItemType weapon : ammos.keySet()) {
							System.out.println(weapon.getName());
							int currentAmmo = ammos.get(weapon);
							if (!previousAmmo.containsKey(weapon)) {
								previousAmmo.put(weapon, 0);
							}
							int previous = previousAmmo.get(weapon);
							if (previous < currentAmmo) {
								previousAmmo.put(weapon, currentAmmo);
							} else if (currentAmmo < previous) {
								if (!ammoUsed.containsKey(weapon)) {
									ammoUsed.put(weapon, 0);
								}
								int used = ammoUsed.get(weapon);
								used += previous - currentAmmo;
								ammoUsed.put(weapon, used);
								previousAmmo.put(weapon, currentAmmo);
							}
						}
						System.out.println("=======================");
					}
				});
	}

	/**
	 * Get score of player
	 *
	 * @param id
	 *            unreal id of player
	 * @return game score
	 */
	public int getEnemyScore(Game g, UnrealId id) {
		return g.getPlayerScore(id);
	}

	/**
	 * Returns the highest score that an enemy player has
	 *
	 * @return
	 */
	public int bestEnemyScore() {
		return bestEnemyScore;
	}

	/**
	 * @return Number of bots killed by this bot.
	 */
	public int getFrags() {
		return frags;
	}

	/**
	 * @return Number of bots killed by this bot in a row without dying
	 */
	public int getStreak() {
		return streak;
	}

	/**
	 * @return How many times was this bot killed.
	 */
	public int getDeaths() {
		return deaths;
	}

	/**
	 * @return How much damage caused the bot to the other bots.
	 */
	public int getDamageCaused() {
		return damageCaused;
	}

	/**
	 * @return a map with playerID's linked to how much damage they received
	 *         from this agent when they were still alive
	 */
	public Map<String, Integer> getDamageDone() {
		return damageDone;
	}

	/**
	 * @return Map of damage dealt with each specific weapon (String name of
	 *         weapon)
	 */
	public Map<String, Integer> getWeaponDamage() {
		return weaponDamage;
	}

	public int getWeaponDamage(ItemType weapon) {
		return weaponDamage.get(weapon.getName()) == null ? 0 : weaponDamage.get(weapon.getName());
	}

	/**
	 * @return How much damage the bot suffered.
	 */
	public int getDamageSuffered() {
		return damageSuffered;
	}

	public int getLastHealth() {
		return previousHealth;
	}

	public int getBumps() {
		return bumps;
	}

	public int getItemsCollected() {
		return pickups;
	}

	public double averageFocus() {
		return (this.opponentSightings == 0.0) ? Math.PI : (this.focusAccumulation / this.opponentSightings);
	}

	public int getWallHits() {
		return wallHits;
	}

	public int getScore() {
		if (info != null) {
			score = info.getScore();
		}
		return score;
	}

	public int getDamageEvents() {
		return damageEvents;
	}

	public void endExperiment() {
	}

	public double getNumberEscapes() {
		return enemyEscapes;
	}

	public int ammoUsed(ItemType weapon) {
		if (ammoUsed.containsKey(weapon)) {
			return ammoUsed.get(weapon);
		} else {
			return 0;
		}
	}
}
