package edu.utexas.cs.nn.breve2D;

import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.breve2D.agent.Breve2DAction;
import edu.utexas.cs.nn.breve2D.dynamics.Breve2DDynamics;
import edu.utexas.cs.nn.breve2D.dynamics.RammingDynamics;
import edu.utexas.cs.nn.breve2D.sensor.RaySensor;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.util2D.Box2D;
import edu.utexas.cs.nn.util.util2D.Distance2DComparator;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Breve2DGame {

	public static Random rand = new Random(0);
	public static final int SIZE_X = 500;
	public static final int SIZE_Y = 500;
	public static final double FORCE_MULTIPLIER = 2; // 3;
	public static final double TURN_MULTIPLIER = Math.PI / 12; // Math.PI / 6;
	public static final int AGENT_MAGNITUDE = 10;
	public static final int RAM_MAGNITUDE = 7;
	/*
	 * Duration of agent response to being hit
	 */
	public static final int RESPONSE_INTERVAL = 5; // 10;
	protected int totalTime;
	protected final int timeLimit;
	protected boolean gameOver;
	protected Agent player;
	protected int numMonsters;
	protected double startingDistance = 60; // 75; //50;
	protected int playerReactingTime = 0;
	protected int[] monsterReactingTime;
	protected Agent[] monsters = null;
	public final Breve2DDynamics dynamics;
	private int lastTimePlayerReactedToMonster;
	private int[] lastTimeMonsterReactedToPlayer;
	private int[] lastTimePlayerReactedToThisMonster;
	public boolean resetAll;
	private RaySensor[][] monsterRays;
	public int numMonsterRays;
	public ArrayList<Triple<ILocated2D, ILocated2D, Color>> lines = new ArrayList<Triple<ILocated2D, ILocated2D, Color>>();
	public final boolean rams;

	/////////////////////////////////////////////////////////////////////////////
	///////////////// Constructors and Initializers //////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	// Constructor
	protected Breve2DGame(int numMonsters, Breve2DDynamics dynamics) {
		this.numMonsters = numMonsters;
		this.dynamics = dynamics;
		this.rams = dynamics instanceof RammingDynamics;
		this.timeLimit = Parameters.parameters.integerParameter("breve2DTimeLimit");
		this.resetAll = false;
	}
	
	/**
	 * Initial conditions when game is restarted: positions of all agents are reset and the time is set to 0
	 */
	protected void init() {
		newPlacement();
		this.totalTime = 0;
	}
	
	/**
	 * Calls newPlacement() to reset positions of monsters and player
	 */
	protected void reset() {
		newPlacement();
	}
	
	/**
	 * Resets position of monsters and player.
	 */
	protected void newPlacement() {
		
		//resets reacting times
		playerReactingTime = 0;
		this.monsterReactingTime = new int[numMonsters];
		this.lastTimePlayerReactedToMonster = -1;
		this.lastTimeMonsterReactedToPlayer = new int[numMonsters];
		Arrays.fill(lastTimeMonsterReactedToPlayer, -1);
		this.lastTimePlayerReactedToThisMonster = new int[numMonsters];
		Arrays.fill(lastTimePlayerReactedToThisMonster, -1);

		resetAll = true;
		double heading = 0;
		if (!Parameters.parameters.booleanParameter("deterministic")) {
			heading = rand.nextDouble() * 2 * Math.PI;
		} else {
			rand = new Random(0);
		}
		player = new Agent(new Tuple2D(SIZE_X / 2, SIZE_Y / 2), heading);
		
		//brings monsters back on screen if they have escaped
		if (monsters == null) {
			monsters = new Agent[numMonsters];
			for (int i = 0; i < numMonsters; i++) {
				monsters[i] = new Agent(new Tuple2D(0, 0), 0);
				monsters[i].setIdentifier(i);
			}

			numMonsterRays = Parameters.parameters.integerParameter("numMonsterRays");
			assert numMonsterRays % 2 == 1 : "Number of rays must be odd: " + numMonsterRays;
			double spacing = Parameters.parameters.doubleParameter("monsterRaySpacing");
			double rayLength = Parameters.parameters.doubleParameter("monsterRayLength");
			monsterRays = new RaySensor[numMonsters][numMonsterRays];
			for (int i = 0; i < numMonsters; i++) {
				int fanOut = 0;
				int sign = -1;
				for (int j = 0; j < numMonsterRays; j++) {
					monsterRays[i][j] = new RaySensor(monsters[i], fanOut * spacing * sign, rayLength);
					if (sign == -1) {
						fanOut++;
					}
					sign *= -1;
				}
			}
		}
		double theta = 0;
		//resets position and rotation of all monsters
		for (int i = 0; i < numMonsters; i++, theta += (2 * Math.PI) / numMonsters) {
			if (!monsters[i].isDead()) {
				double[] coords = CartesianGeometricUtilities.polarToCartesian(startingDistance, theta);
				monsters[i].setPosition(new Tuple2D(coords[0] + (SIZE_X / 2), coords[1] + (SIZE_Y / 2)));

				double adjacent = Math.abs(monsters[i].getX() - player.getX());
				double opposite = Math.abs(monsters[i].getY() - player.getY());
				double hypotenuse = Math.sqrt((opposite * opposite) + (adjacent * adjacent));
				double angle = Math.acos(adjacent / hypotenuse);
				if (monsters[i].getX() < player.getX()) {
					if (monsters[i].getY() > player.getY()) {
						monsters[i].setHeading(-angle);
					} else {
						monsters[i].setHeading(angle);
					}
				} else {
					if (monsters[i].getY() > player.getY()) {
						monsters[i].setHeading(Math.PI + angle);
					} else {
						monsters[i].setHeading(Math.PI - angle);
					}
				}
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	///////////////////////////// Game Play //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/**
	 * Central method that advances the game state
	 *
	 * @param playerAction
	 *            acceleration and turn of player
	 * @param monsterActions
	 *            accelerations and turns of each monster/NPC
	 */
	public void advanceGame(Breve2DAction playerAction, Breve2DAction[] monsterActions) {
		// System.out.println(playerAction + ":" +
		// Arrays.toString(monsterActions));
		this.totalTime++;
		
		if (rams) {
			RammingDynamics ramDynamics = (RammingDynamics) dynamics;
			// Interactions between rams and player
			if (ramDynamics.monstersHaveRams()) {
				Tuple2D playerLocation = getPlayerPosition();
				ArrayList<ILocated2D> ramPositions = new ArrayList<ILocated2D>();
				for (int i = 0; i < monsters.length; i++) {
					Agent a = monsters[i];
					if (!monsterLocked(i) && !a.isDead()) {
						ramPositions.add(a.getPosition().add(ramDynamics.getRamOffset().rotate(a.getHeading())));
					}
				}
				//if the monster has not been hit and is not dead
				if (!ramPositions.isEmpty()) {
					ILocated2D nearest = nearestPositionToPosition(playerLocation, ramPositions);
					double distance = nearest.distance(playerLocation);
					boolean playerResponseStarting = false;
					// Collisions supercede chosen actions
					if (distance < 2 * RAM_MAGNITUDE && !playerLocked()) {
						this.playerReactingTime = RESPONSE_INTERVAL;
						playerResponseStarting = true;
						// overwrites action
						playerAction = ramDynamics.playerInitialResponseToRam(getPlayer(), nearest, totalTime);
						lastTimePlayerReactedToMonster = totalTime;
						lastTimePlayerReactedToThisMonster[monsterWithRam(nearest).getIdentifier()] = totalTime;
					}
					
					//if player has been hit
					if (playerLocked()) {
						Breve2DAction continued = ramDynamics.playerContinuedResponseToRam(getPlayer(), nearest,
								totalTime);
						if (playerResponseStarting) {
							// Add to initial action
							playerAction = new Breve2DAction(playerAction.add(continued));
						} else {
							// overwrite
							playerAction = continued;
						}
						playerReactingTime--;
					}
				}
			}
		}

		if (dynamics.playerRespondsToMonster()) {
			// Interactions between monsters and player
			Tuple2D playerLocation = getPlayerPosition();
			Agent nearest = nearestMonsterToPosition(playerLocation);
			if (nearest != null) {
				double distance = nearest.distance(playerLocation);
				boolean playerResponseStarting = false;
				// Collisions supercede chosen actions
				if (distance < 2 * AGENT_MAGNITUDE && !playerLocked()) {
					this.playerReactingTime = RESPONSE_INTERVAL;
					playerResponseStarting = true;
					// overwrites action
					playerAction = dynamics.playerInitialResponseToMonster(getPlayer(), nearest, totalTime);
					lastTimePlayerReactedToMonster = totalTime;
					lastTimePlayerReactedToThisMonster[nearest.getIdentifier()] = totalTime;
					// System.out.println("Player responsing to monster: " +
					// nearest.getIdentifier() + ":" + playerAction);
				}
				//if player has been hit
				if (playerLocked()) {
					Breve2DAction continued = dynamics.playerContinuedResponseToMonster(getPlayer(), nearest,
							totalTime);
					if (playerResponseStarting) {
						// Add to initial action
						playerAction = new Breve2DAction(playerAction.add(continued));
					} else {
						// overwrite
						playerAction = continued;
					}
					playerReactingTime--;
					// System.out.println("Continued Player response to monster:
					// " + nearest.getIdentifier() + ":" + playerAction + " from
					// " + continued);
				}
			}
		}

		// Carry out player action
		player.turn(playerAction.getTurn() * TURN_MULTIPLIER);
		double[] offset = CartesianGeometricUtilities.polarToCartesian(playerAction.getForce() * FORCE_MULTIPLIER,
				player.getHeading());
		player.move(new Tuple2D(offset[0], offset[1]));

		int dead = 0;
		for (int i = 0; i < numMonsters; i++) {
			if (!monsters[i].isDead()) {
				boolean monsterResponseStarting = false;
				Agent monster = getMonster(i);
				if (dynamics.monsterRespondsToPlayer()) {
					double distance = monster.distance(getPlayerPosition());
					// Collisions supercede chosen actions
					if (distance < 2 * AGENT_MAGNITUDE && !monsterLocked(i)) {
						this.monsterReactingTime[i] = RESPONSE_INTERVAL;
						monsterResponseStarting = true;
						// overwrites action
						monsterActions[i] = dynamics.monsterInitialResponseToPlayer(getPlayer(), monster, totalTime);
						lastTimeMonsterReactedToPlayer[i] = totalTime;
						if (monsters[i].isDead()) {
							dead++;
						}
					}
					//if monster has been hit
					if (monsterLocked(i)) {
						Breve2DAction continued = dynamics.monsterContinuedResponseToPlayer(getPlayer(), monster,
								totalTime);
						if (monsterResponseStarting) {
							// Add to initial action
							monsterActions[i] = new Breve2DAction(monsterActions[i].add(continued));
						} else {
							// overwrite
							monsterActions[i] = continued;
						}
						monsterReactingTime[i]--;
					}
				}

				if (rams) {
					RammingDynamics ramDynamics = (RammingDynamics) dynamics;
					// Interactions between monsters and player ram
					if (ramDynamics.playerHasRam() && !playerLocked()) {
						Tuple2D monsterLocation = getMonsterPosition(i);
						ILocated2D ramPosition = getPlayer().getPosition()
								.add(ramDynamics.getRamOffset().rotate(getPlayer().getHeading()));
						double distance = ramPosition.distance(monsterLocation);
						monsterResponseStarting = false;
						// Collisions supercede chosen actions
						if (distance < 2 * RAM_MAGNITUDE && !monsterLocked(i)) {
							monsterReactingTime[i] = RESPONSE_INTERVAL;
							monsterResponseStarting = true;
							// overwrites action
							monsterActions[i] = ramDynamics.monsterInitialResponseToRam(getMonster(i), ramPosition,
									totalTime);
							lastTimeMonsterReactedToPlayer[i] = totalTime;
							// System.out.println("Monster " + i + " responding:
							// " + monsterActions[i]);
						}
						
						//if monster has been hit
						if (monsterLocked(i)) {
							Breve2DAction continued = ramDynamics.monsterContinuedResponseToRam(getMonster(i),
									ramPosition, totalTime);
							if (monsterResponseStarting) {
								// Add to initial action
								monsterActions[i] = new Breve2DAction(monsterActions[i].add(continued));
							} else {
								// overwrite
								monsterActions[i] = continued;
							}
							monsterReactingTime[i]--;
							// System.out.println("Continued Monster " + i + "
							// response: " + monsterActions[i] + " from " +
							// continued);
						}
					}
				}

				monsters[i].turn(monsterActions[i].getTurn() * TURN_MULTIPLIER);
				offset = CartesianGeometricUtilities.polarToCartesian(monsterActions[i].getForce() * FORCE_MULTIPLIER,
						monsters[i].getHeading());
				monsters[i].move(new Tuple2D(offset[0], offset[1]));
			} else {
				dead++;
				monsters[i].setPosition(null);
			}
		}
		
		//check whether agents have left the screen
		boolean boxEscaped = false;
		if (dynamics.playerRespondsToMonster() && !dynamics.monsterRespondsToPlayer()
				&& (!rams || (!((RammingDynamics) dynamics).playerHasRam()
						&& !((RammingDynamics) dynamics).monstersHaveRams()))) {
			ArrayList<Agent> livingMonsters = this.getMonsters();
			ILocated2D[] perimeter = new ILocated2D[livingMonsters.size()];
			for (int i = 0; i < perimeter.length; i++) {
				perimeter[i] = livingMonsters.get(i);
			}
			boxEscaped = !(new Box2D(perimeter).insideBox(player, 20));
		}
		if (dead == numMonsters || boxEscaped) {
			gameOver = true;
		} else if (player.isDead()) {
			newPlacement();
		}
	}

	// Whether the game is over or not
	public boolean gameOver() {
		return gameOver || totalTime >= timeLimit;
	}

	/////////////////////////////////////////////////////////////////////////////
	///////////////// Accessors //////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	public RaySensor getRaySensor(int monster, int sensor) {
		return this.monsterRays[monster][sensor];
	}
	
	/**
	 * Returns the position of the Player
	 * 
	 * @return Position of the Player
	 */
	public Tuple2D getPlayerPosition() {
		return player.getPosition();
	}
	
	/**
	 * Returns the Player's heading
	 * 
	 * @return Double representing the Player's heading
	 */
	public double getPlayerHeading() {
		return player.getHeading();
	}
	
	/**
	 * Returns the Player
	 * 
	 * @return Agent representing the Player
	 */
	public Agent getPlayer() {
		return player;
	}
	
	/**
	 * Returns the position of a specified monster
	 * 
	 * @param index Index representing a specific monster Agent
	 * @return The Tuple2D position of the monster
	 */
	public Tuple2D getMonsterPosition(int index) {
		return monsters[index].getPosition();
	}

	/**
	 * Returns the heading of a specified monster Agent
	 * 
	 * @param index index of a specific monster Agent
	 * @return Double representing the monster heading
	 */
	public double getMonsterRadians(int index) {
		return monsters[index].getHeading();
	}
	
	/**
	 * Creates and returns a list of all monster Agents which are not dead
	 * 
	 * @return ArrayList containing all living monster Agents
	 */
	public ArrayList<Agent> getMonsters() {
		ArrayList<Agent> monstersList = new ArrayList<Agent>(numMonsters);
		for (int i = 0; i < numMonsters; i++) {
			if (!monsters[i].isDead()) {
				monstersList.add(monsters[i]);
			}
		}
		return monstersList;
	}
	
	/**
	 * Returns monster agent
	 * 
	 * @param index Index of a specific monster Agent
	 * @return The specified monster Agent
	 */
	public Agent getMonster(int index) {
		return monsters[index];
	}
	
	/**
	 * Returns the number of all monster Agents
	 * @return The number of all monster Agents
	 */
	public int getNumMonsters() {
		return numMonsters;
	}
	
	/**
	 * Creates a list of positions organized by distance from a given position
	 * 
	 * @param pos ILocated2D representing a specified location
	 * @param positions ArrayList<ILocated2D> of positions
	 * @return Given ArrayList<ILocated2D> "positions" sorted in increasing order by distance from specified location pos
	 */
	public ArrayList<ILocated2D> positionsByDistanceFrom(ILocated2D pos, ArrayList<ILocated2D> positions) {
		Distance2DComparator comparator = new Distance2DComparator(pos);
		Collections.sort(positions, comparator);
		return positions;
	}
	
	/**
	 * Returns first item on list of positions sorted by distance from given position;
	 * Returns the position closest to a given location
	 * 
	 * @param pos ILocated2D representingn a specified location
	 * @param positions ArrayList<ILocated2D> of positions
	 * @return Position on given ArrayList<ILocated2D> "positions" closest to the specified location "pos"
	 */
	public ILocated2D nearestPositionToPosition(ILocated2D pos, ArrayList<ILocated2D> positions) {
		ArrayList<ILocated2D> list = positionsByDistanceFrom(pos, positions);
		return list.get(0);
	}
	
	/**
	 * Creates a list of monsters organized by distance from a given position
	 * 
	 * @param pos ILocated2D representing a specific location
	 * @return ArrayList<Agent> of monsters sorted in increasing order by distance from the specified location "pos"
	 */
	public ArrayList<Agent> monstersByDistanceFrom(ILocated2D pos) {
		Distance2DComparator comparator = new Distance2DComparator(pos);
		ArrayList<Agent> list = getMonsters();
		Collections.sort(list, comparator);
		return list;
	}
	
	/**
	 * Returns first item on list of monsters sorted by distance from position, or null if no monsters
	 * 
	 * @param pos ILocated2D representing a specified location
	 * @return monster Agent closest to the specified location "pos"
	 */
	public Agent nearestMonsterToPosition(ILocated2D pos) {
		ArrayList<Agent> list = monstersByDistanceFrom(pos);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	
	//methods that return total time and response times
	
	/**
	 * Returns the total time of the game
	 * 
	 * @return Total time of the game
	 */
	public int getTime() {
		return totalTime;
	}

	/**
	 * Returns the time of the last Player response to a Monster
	 * 
	 * @return The time of the last Player response to a Monster
	 */
	public int lastPlayerResponseToMonster() {
		return this.lastTimePlayerReactedToMonster;
	}

	/**
	 * Returns the time of the last Player response to a specified monster Agent
	 * 
	 * @param index Index of a specific monster Agent
	 * @return The time of the last Player response to the specified monster Agent
	 */
	public int lastPlayerResponseToMonster(int index) {
		return this.lastTimePlayerReactedToThisMonster[index];
	}

	/**
	 * Returns the last time a specified monster Agent responded to the Player
	 * 
	 * @param index Index of a specific monster Agent
	 * @return The last time the specified monster Agent responded to the Player
	 */
	public int lastMonsterResponseToPlayer(int index) {
		return this.lastTimeMonsterReactedToPlayer[index];
	}

	/**
	 * Returns the last time any monster Agent responded to the Player
	 * 
	 * @return The last time any monster Agent responded to the Player
	 */
	public int lastMonsterResponseToPlayer() {
		int m = -1;
		for (int i = 0; i < numMonsters; i++) {
			if (lastTimeMonsterReactedToPlayer[i] > m) {
				m = lastTimeMonsterReactedToPlayer[i];
			}
		}
		return m;
	}
	
	//true if player has been hit
	/**
	 * Returns true if the Player is Locked
	 * 
	 * @return True if the Player is Locked, else returns false
	 */
	public boolean playerLocked() {
		return this.playerReactingTime > 0;
	}
	
	//true if monster has been hit
	/**
	 * Returns true if a specific monster Agent is Locked, else returns false
	 * 
	 * @param index Index of a specific monster Agent
	 * @return True if the specified monster Agent is Locked, else returns false
	 */
	public boolean monsterLocked(int index) {
		return this.monsterReactingTime[index] > 0;
	}
	
	//true if monster is dead
	/**
	 * Returns true if a specified monster Agent is dead, else returns false
	 * 
	 * @param index Index of a specific monster Agent
	 * @return True if the specified monster Agent is dead, else returns false
	 */
	public boolean monsterDead(int index) {
		return this.monsters[index].isDead();
	}
	
	//adds lines
	/**
	 * Adds a line to the global ArrayList<> "lines"
	 * 
	 * @param c Color of the line to be drawn
	 * @param p1 ILocated2D representing the starting point of the line
	 * @param p2 ILocated2D representing the ending point of the line
	 */
	public void addLine(Color c, ILocated2D p1, ILocated2D p2) {
		if (p1 != null && p2 != null && c != null) {
			lines.add(new Triple<ILocated2D, ILocated2D, Color>(p1, p2, c));
		}
	}
	
	//adds rams to monsters
	/**
	 * Returns a specific monster Agent if that monster is able to use a Ram is a specific location
	 * 
	 * @param ram ILocated2D representing a specific location where a Ram is located
	 * @return A monster Agent if the monster is able to use the Ram, else returns null
	 */
	private Agent monsterWithRam(ILocated2D ram) {
		Tuple2D offset = ((RammingDynamics) dynamics).getRamOffset();
		for (int i = 0; i < monsters.length; i++) {
			Agent a = monsters[i];
			if (!monsterLocked(i) && !a.isDead()) {
				if (a.getPosition().add(offset.rotate(a.getHeading())).equals(ram)) {
					return a;
				}
			}
		}
		return null;
	}
}
