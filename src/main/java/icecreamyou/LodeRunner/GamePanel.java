package icecreamyou.LodeRunner;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerEnhancedEnemiesUtil;
import edu.southwestern.util.datastructures.Pair;

public class GamePanel extends JPanel implements MouseMotionListener {
	public static final String FILE_PATH = "src/main/java/icecreamyou/LodeRunner/";

	/**
	 * Automatically generated. Required for subclasses of JPanel.
	 */
	private static final long serialVersionUID = -6163550764769049810L;

	/**
	 * The width and height of the Panel. 
	 * We changed this from 600x600 to allow for us to load in our own levels 
	 */
	public static final int WIDTH = 960, HEIGHT = 880;
	/**
	 * The width and height of each unit in the Panel.
	 * Units are basically squares on a grid. Every WorldNode is one square
	 * unit. The level editor only allows placing WorldNodes at exact unit
	 * locations.
	 */
	public static final int UNIT_HEIGHT = 40, UNIT_WIDTH = 30;

	/**
	 * The minimum amount of time between each run of the main game loop. 
	 */
	private static final int TIMER_INTERVAL = 35;

	/**
	 * The current game mode.
	 * @see Mode
	 */
	static Mode mode;
	/**
	 * The timer that executes the main game loop.
	 * @see TIMER_INTERVAL
	 */
	private Timer timer;
	/**
	 * The level currently being played, viewed, or edited in this Panel.
	 */
	public Level level;
	/**
	 * Properties describing the next WorldNode to place in the editor.
	 */
	private String editorKey;
	private String editorImgPath;
	private KeyColor editorColor;
	/**
	 * The parent frame.
	 */
	private LodeRunner parent;
	/**
	 * Whether the player was on a ladder one tick ago.
	 * This is something of a hack to keep the player from falling when
	 * reaching the top of a ladder.
	 */
	private boolean playerOneWasOnLadder = false;
	/**
	 * The holes that a Player has dug.
	 */
	private Set<Dug> dugs = new HashSet<Dug>();
	/**
	 * The last coordinates of the mouse in the Panel.
	 */
	private int mouseXPos = 0, mouseYPos = 0;

	public GamePanel(Level lvl, LodeRunner parent) {
		level = lvl;
		this.parent = parent;

		mode = Mode.MODE_PAUSED;

		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setFocusable(true);

		// Instantiate the game timer that will run the main action loop.
		timer = new Timer(TIMER_INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) { tick(); }});

		// React when the player presses keys.
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				boolean playerOneIsOnBar = false;
				for (Bar b : level.bars) {
					if (b.actorIsOn(level.player1)) {
						playerOneIsOnBar = true;
						break;
					}
				}
				boolean playerOneIsOnLadder = false;
				for (Ladder l : level.ladders) {
					if (l.actorIsOn(level.player1)) {
						playerOneIsOnLadder = true;
						break;
					}
				}

				// Set the player's direction.
				if 		(e.getKeyCode() == KeyEvent.VK_A)
					level.player1.setVelocity(-Player.VELOCITY, 0);
				else if (e.getKeyCode() == KeyEvent.VK_D)
					level.player1.setVelocity(Player.VELOCITY, 0);
				else if (e.getKeyCode() == KeyEvent.VK_S && (playerOneIsOnBar || playerOneIsOnLadder))
					level.player1.setVelocity(0, Player.VELOCITY);
				else if (e.getKeyCode() == KeyEvent.VK_W && playerOneIsOnLadder)
					level.player1.setVelocity(0, -Player.VELOCITY);

				// Dig holes.
				if (e.getKeyCode() == KeyEvent.VK_Q && mode == Mode.MODE_PLAYING) {
					for (Diggable d : level.diggables) {
						if (d.getX() == getXUnitPosition(level.player1.getX() - UNIT_WIDTH)
								&& d.getY() == level.player1.getY() + UNIT_HEIGHT) {
							digHole(d);
						}
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_E && mode == Mode.MODE_PLAYING) {
					int pX = level.player1.getX(), pXU = getXUnitPosition(pX);
					for (Diggable d : level.diggables) {
						if (((pX - pXU <= level.player1.getWidth() / 2 && d.getX() == pXU + UNIT_WIDTH)
								|| (pX - pXU > level.player1.getWidth() / 2 && d.getX() == pXU + 2*UNIT_WIDTH))
								&& d.getY() == level.player1.getY() + UNIT_HEIGHT) {
							digHole(d);
						}
					}

				}
			}
			// Stop moving.
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_A ||
						e.getKeyCode() == KeyEvent.VK_D ||
						e.getKeyCode() == KeyEvent.VK_W ||
						e.getKeyCode() == KeyEvent.VK_S)
					level.player1.setVelocity(0, 0);
			}
		});

		// Keep track of the mouse when editing.
		addMouseMotionListener(this);

		grabFocus();
	}

	/**
	 * Determines whether any Gold is left in the level (including Gold picked up by Enemies).
	 */
	private boolean noCoinsLeft() {
		for (Pickup p : level.pickups)
			if (p instanceof Gold && !p.isPickedUp())
				return false;
		for (Enemy e : level.enemies)
			if (e.getGoldValue() > 0)
				return false;
		return true;
	}

	/**
	 * React when the level is completed successfully.
	 */
	public void win() {
		reset();
		parent.stopPlaying();
		JOptionPane.showMessageDialog(parent.frame, getWinMessage());
		if (level.getNextLevel() != null) {
			parent.status.setText(level.getNextLevel());
			parent.setLevelName(level.getNextLevel());
			level = new Level(level.getNextLevel());
			repaint();
		}
	}
	/**
	 * React when the player is killed.
	 */
	public void lose() {
		reset();
		parent.lives.subtractValue(1);
		parent.score.resetValue();
		if (parent.lives.getValue() < 1) {
			parent.lives.resetValue();
			JOptionPane.showMessageDialog(parent.frame, getLoseMessage());
		}
		else
			JOptionPane.showMessageDialog(parent.frame, getDeathMessage());
		parent.stopPlaying();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Paint border
		level.draw(g, mode); // Paint WorldNodes
		for (Dug d : dugs) // Paint holes
			d.draw(g);

		// If editing, draw the currently selected WorldNode at the cursor.
		if (mode == Mode.MODE_EDITING
				&& editorKey != null
				&& !editorKey.equals("")) {
			// Special case for WorldNodes where color is important.
			if (editorKey.equals("gate"))
				editorImgPath = "gate-"+ GateKey.colorToString(editorColor).toLowerCase() +".png";
			else if (editorKey.equals("gateKey"))
				editorImgPath = "key-"+ GateKey.colorToString(editorColor).toLowerCase() +".png";
			Picture.draw(g, editorImgPath, mouseXPos, mouseYPos);
		}
	}


	@Override
	public Dimension getMinimumSize() {
		//changed from the original because the levels we are bringing don't fit in the window for IceCreamYou, original was 600x600 
		return new Dimension(WIDTH,HEIGHT); 
	}
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	@Override
	public Dimension getMaximumSize() {
		return getMinimumSize();
	}

	/**
	 * Reset the game to its last important state.
	 * If the game is currently paused, this will start it playing.
	 */
	public void reset() {
		grabFocus();
		if (mode == Mode.MODE_EDITING) {
			level = Level.cleanCopy(level);
			repaint();
			return;
		}
		if (mode == Mode.MODE_PAUSED) {
			mode = Mode.MODE_PLAYING;
			timer.start();
			level = Level.cleanCopy(level);
			repaint();
		}
		else if(mode==Mode.GAN) {
			mode=Mode.MODE_PLAYING;
			timer.start();
			level = new Level(LodeRunner.levelCopy);
			repaint();
		}
		else if (mode == Mode.MODE_PLAYING) {
			mode = Mode.MODE_PAUSED;
			timer.stop();
			level = Level.cleanCopy(level);
			dugs = new HashSet<Dug>();
			repaint();
		}
	}

	/**
	 * Helper functions to determine the game mode.
	 */
	public boolean isPlaying() {
		return (mode == Mode.MODE_PLAYING);
	}
	public boolean isPaused() {
		return (mode == Mode.MODE_PAUSED);
	}
	public boolean isEditing() {
		return (mode == Mode.MODE_EDITING);
	}
	public Mode getMode() {
		return mode;
	}

	/**
	 * Start editing.
	 */
	public void useEditor() {
		grabFocus();
		mode = Mode.MODE_EDITING;
		timer.stop();
		repaint();
		parent.score.resetValue();
		parent.lives.resetValue();
	}
	/**
	 * Stop editing.
	 */
	public void stopUsingEditor() {
		grabFocus();
		mode = Mode.MODE_PAUSED;
		repaint();
	}
	/**
	 * Let the GamePanel know which editor button was pressed.
	 */
	public void setEditorKey(String key, String imgPath, KeyColor color) {
		editorKey = key;
		editorImgPath = imgPath;
		editorColor = color;
	}
	public String getEditorKey() {
		return editorKey;
	}
	/**
	 * Add a new node to the world.
	 * @param key The type of node to add.
	 * @param xUnit The x-coordinate of the location to add the node.
	 * @param yUnit The y-coordinate of the location to add the node.
	 */
	public void addNode(String key, int xUnit, int yUnit) {
		WorldNode wn = null;
		if (key.equals("gateKey") || key.equals("gate"))
			wn = level.add(key +":"+ xUnit +","+ yUnit +","+ editorColor);
		else if (key.equals("player"))
			wn = level.add(key +":"+ xUnit +","+ yUnit +",1");
		else if (key.equals("erase"))
			wn = new Solid(xUnit, yUnit); // Solids cannot exist in the same space as anything else.
		else
			wn = level.add(key +":"+ xUnit +","+ yUnit);
		level.checkRemoveCollisionInEditor(wn);
		repaint();
	}
	/**
	 * Save the current level.
	 */
	public void save(String name) {
		// All custom levels end in -level.
		level.save(name +"-level");
	}

	/**
	 * Switch to a new level.
	 * @param name The name of the level to which to switch.
	 */
	public void switchLevel(String name) {
		level = new Level(name +"-level");
		mode = Mode.MODE_PAUSED;
		timer.stop();
		repaint();
	}

	/**
	 * Reset level from instance of Level class
	 * @param lvl
	 */
	public void switchLevel(Level lvl) {
		level = new Level(lvl);
		mode = Mode.MODE_PAUSED;
		timer.stop();
		repaint();
	}


	/**
	 * Create a new, blank level.
	 */
	public void switchLevel() {
		level = new Level();
		mode = Mode.MODE_PAUSED;
		timer.stop();
		repaint();
	}
	/**
	 * Switch to the first campaign level.
	 */
	public void startCampaign() {
		level = new Level("CAMPAIGN-1");
		mode = Mode.MODE_PAUSED;
		timer.stop();
		repaint();
	}

	/**
	 * Checks whether a player exists in the current level.
	 * This is used to make sure a new level is not saved without a player.
	 * @see LodeRunner()
	 */
	public boolean playerOneExists() {
		return (level.player1 != null);
	}

	/**
	 * Determines whether two values are reasonably close.
	 * This looks ugly at first, but it actually makes sense in context.
	 * 
	 * @param a The first value to compare.
	 * @param b The second value to compare.
	 * @param tolerance How close the numbers can be and still be considered equal
	 * @return true if the numbers are more or less equal; false otherwise.
	 */
	public static boolean sorta_equals(int a, int b, int tolerance) {
		return a < b + tolerance && a > b - tolerance;
	}

	/**
	 * Choose a random win message.
	 */
	public static String getWinMessage() {
		String[] options = {
				"You win!",
				"Great job!",
				"Excellent!",
				"Nicely done!",
				"Well-done!",
				"Impressive!",
				"Congratulations!",
				"You finished the level!",
				"Nice job!",
				"You make it look easy!",
		};
		return options[(int) (Math.random() * options.length)];
	}
	/**
	 * Choose a random death message.
	 */
	public static String getDeathMessage() {
		String[] options = {
				"Oh no!",
				"Ouch!",
				"You died!",
				"Try again.",
				"That's gotta hurt.",
				"Well, that's embarrassing. You died. Try again!",
				"You died, but at least there's room for improvement.",
		};
		return options[(int) (Math.random() * options.length)];
	}
	/**
	 * Choose a random loss message.
	 */
	public String getLoseMessage() {
		String[] options = {
				"You lose!",
				"Game over.",
				"All your Gold are belong to us",
				"Game over! Not ready for the big leagues, eh?",
				"Game over! You could use some more practice.",
		};
		return options[(int) (Math.random() * options.length)];
	}

	/**
	 * Dig a hole at the location of Diggable d if one does not already exist.
	 */
	private void digHole(Diggable d) {
		boolean alreadyExists = false;
		for (Dug d0 : dugs)
			if (d0.getX() == d.getX() && d0.getY() == d.getY()) {
				alreadyExists = true;
				break;
			}
		if (!alreadyExists) {
			d.setFilled(false);
			dugs.add(new Dug(level, d.getX(), d.getY()));
		}
	}

	/**
	 * Get the x-coordinate (in pixels) of the upper-left corner of the unit in
	 * which int x is found.
	 */
	public static int getXUnitPosition(int x) {
		return (x / GamePanel.UNIT_WIDTH) * GamePanel.UNIT_WIDTH;
	}
	/**
	 * Get the y-coordinate (in pixels) of the upper-left corner of the unit in
	 * which int y is found.
	 */
	public static int getYUnitPosition(int y) {
		return (y / GamePanel.UNIT_HEIGHT) * GamePanel.UNIT_HEIGHT;
	}

	/**
	 * Determines whether the portal is available.
	 */
	boolean canWin() {
		if (level.portal != null) { 
			if (!level.portalKeyExists || level.player1.canOpen(level.portal)) {
				if (noCoinsLeft()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines whether an actor is on any of the nodes in a set.
	 */
	public static boolean actorIsOnAnySolid(Actor a, Set<Solid> solids) {
		for (ActorCollision s : solids)
			if (s.actorIsOn(a))
				return true;
		return false;
	}
	public static boolean actorIsOnAnyLadder(Actor a, Set<Ladder> ladders) {
		for (ActorCollision l : ladders)
			if (l.actorIsOn(a))
				return true;
		return false;
	}
	public static boolean actorIsOnAnyEnemy(Actor a, Set<Enemy> enemies) {
		for (Enemy e : enemies)
			if (a != e && e.isInHole() && e.actorIsOn(a))
				return true;
		return false;
	}

	/**
	 * Determines whether an enemy is falling.
	 */
	boolean enemyIsFalling(Enemy e) {
		return !(e.getY() >= HEIGHT - e.getHeight()
				|| actorIsOnAnySolid(e, level.solids)
				|| actorIsOnAnyLadder(e, level.ladders)
				|| actorIsOnAnyEnemy(e, level.enemies));
	}

	/**
	 * Checks for collision between an Enemy and Player, and kills the player if appropriate.
	 */
	void checkPlayerEnemyCollision(Player p, Enemy e) {
		if (p.intersects(e)) {
			// If the Enemy is not in a hole, the Player will die if they touch.
			if (!e.isInHole())
				p.setAlive(false);
			// If the Enemy is in a hole...
			else {
				// ...then if the player is also in a hole and they touch, the player will die.
				for (Dug d : dugs) {
					if (d.intersects(p)) {
						p.setAlive(false);
						break;
					}
				}
			}
		}
	}

	/**
	 * Checks whether an Enemy e can get trapped in a hole. If it can, trap it.
	 */
	boolean checkEnemyFallInHole(Enemy e) {
		for (Dug d : dugs) {
			if (e.canFallInDug(d)) {
				// If the enemy has a coin, drop it.
				if (e.getGoldValue() > 0) {
					level.add("coin:"+ e.getX() +","+ e.getY());
					e.resetGoldValue();
				}
				// Move the enemy into the hole.
				e.setX(d.getX());
				e.setY(d.getY());
				e.setInHole(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * This is where all the magic happens: the main game loop.
	 * All the movement, pickups, etc. are updated here.
	 */
	void tick() {
		// Check whether the player has completed the level or died.
		if (!level.player1.isAlive()) {
			lose();
			return;
		}
		if (canWin()) {
			level.portal.setLocked(false);
			if (level.player1.intersects(level.portal)) {
				win();
				return;
			}
		}
		//added this to the original code because the levels we load in do not have portals so 
		//we set it to the goal being to get all of the gold instead of getting to the portal to win 
		if(level.portal == null && noCoinsLeft()) {
			win();
			return;
		}
		//		if(mode==Mode.GAN && level.portal==null && noCoinsLeft()) {
		//			win();
		//			return;
		//		}

		// Process enemies.
		for (Enemy e : level.enemies) {
			e.tryRespawn(level.player1);
			checkPlayerEnemyCollision(level.player1, e);
			if (!e.tryClimbOut())
				continue;
			if (checkEnemyFallInHole(e))
				continue;
			Parameters.initializeParameterCollections(new String[] {});
			//this function does not work yet, it is set to false to maintain the old enemy AI 
			if(Parameters.parameters.booleanParameter("smartLodeRunnerEnemies")) {
				Pair<Integer, Integer> enemyPosition = new Pair<Integer, Integer>(e.x, e.y);
				Pair<Integer, Integer> playerPosition = new Pair<Integer,Integer>(level.player1.x, level.player1.y);
				//set variables to be max value, to make sure that it can be replaced with the lower value, or does not get selected as the lowest value
				double left = Double.MAX_VALUE;
				double right= Double.MAX_VALUE;
				double up= Double.MAX_VALUE;
				double down= Double.MAX_VALUE;
				if(e.getX()-e.getWidth() > 0) { //checks in bounds to the left 
					enemyPosition.t1 = enemyPosition.t1 - e.getWidth(); //moves enemy to the left 
					left = LodeRunnerEnhancedEnemiesUtil.getManhattanDistance(enemyPosition, playerPosition);
					enemyPosition.t1 = enemyPosition.t1 + e.getWidth(); //moves enemy back to where it was 
				}
				if(e.getX()+e.getWidth() < WIDTH - e.getWidth()) { //checks in bounds to the right 
					enemyPosition.t1 = enemyPosition.t1 + e.getWidth(); //moves enemy to the left
					right = LodeRunnerEnhancedEnemiesUtil.getManhattanDistance(enemyPosition, playerPosition);
					enemyPosition.t1 = enemyPosition.t1 - e.getWidth(); //moves enemy back to where it was  
				}
				if(e.getY()+e.getHeight() > 0 && actorIsOnAnyLadder(e, level.ladders)){ //checks upward boundry and if the enemy is on a ladder to be able to go up 
					enemyPosition.t2 = enemyPosition.t2 + e.getHeight(); //moves enemy to the left
					up = LodeRunnerEnhancedEnemiesUtil.getManhattanDistance(enemyPosition, playerPosition);
					enemyPosition.t2 = enemyPosition.t2 - e.getHeight(); //moves enemy back to where it was
				}
				if(e.getY()-e.getHeight() < HEIGHT - e.getHeight() && actorIsOnAnyLadder(e,level.ladders)) {
					enemyPosition.t2 = enemyPosition.t2 - e.getHeight(); //moves enemy to the left
					down = LodeRunnerEnhancedEnemiesUtil.getManhattanDistance(enemyPosition, playerPosition);
					enemyPosition.t2 = enemyPosition.t2 + e.getHeight(); //moves enemy back to where it was
				}
				double min = LodeRunnerEnhancedEnemiesUtil.findMin(left, right, up, down);
				if(min == left) {
					e.setVelocity(-e.getMaxVelocity(), 0);
				}
				else if(min == right) {
					e.setVelocity(e.getMaxVelocity(), 0);
				}
				else if(min == up) {
					e.setVelocity(0, -e.getMaxVelocity());
				}
				else {// down
					e.setVelocity(0, e.getMaxVelocity());
				}
				e.accelerate();
				e.move();
			}
			else if (enemyIsFalling(e) || e.getX() < 0 || e.getX() > WIDTH - e.getWidth()) {
				e.reverse(); //changes direction of enemy x movement 
				e.accelerate();
				e.move();
				//after moving, if the enemy is still falling, then you move it back and 
				//give it falling velocity to fall to solid ground
				if(enemyIsFalling(e)) {
					e.reverse();
					e.accelerate();
					e.move();
					e.setVelocity(e.xVel, Enemy.VELOCITY);
				}
			}
			else {
				if(!enemyIsFalling(e)) //this stops the enemy from falling down through the floor 
					e.setVelocity(e.xVel, 0);
				e.accelerate();
				e.move();
			}
		}

		// Process pickups.
		for (Pickup n : level.pickups) {
			if (!n.isPickedUp()) {
				// Let the player try to pick it up.
				if (n.intersects(level.player1)) {
					level.player1.pickUp(n);
					// Add to the score.
					if (n instanceof Gold) {
						Gold g = (Gold) n;
						parent.score.addValue(g.getValue());
					}
				}
				// Let Enemies try to pick it up.
				else
					for (Enemy e : level.enemies)
						if (n.intersects(e))
							e.pickUp(n);
			}
		}

		// Fill holes.
		Dug.removeOldDugs(dugs);

		// Check whether spikes will kill the player.
		for (Spikes s : level.spikes) {
			if (s.intersects(level.player1)) {
				level.player1.setAlive(false);
				return; // We will reset on the next tick.
			}
		}

		// Check horizontal collision and clip as appropriate.
		for (Solid s : level.solids) {
			// Ignore Diggables if they have been dug.
			if (s instanceof Diggable) {
				Diggable d = (Diggable) s;
				if (!d.isFilled())
					continue;
			}
			// Adjust the Player's position to keep it from walking through walls.
			if (level.player1.intersects(s)) {
				level.player1.adjustY();
				if (level.player1.intersects(s)) {
					level.player1.adjustX(s);
				}
			}
			// Adjust Enemies' position to keep them from walking through walls.
			for (Enemy e : level.enemies) {
				if (e.intersects(s))
					e.adjustX(s);
			}
		}

		// Process gates.
		for (Gate g : level.gates) {
			if ((level.player1.intersects(g) || g.actorIsOn(level.player1)) && g.isLocked()) {
				// Open the gate if possible.
				if (level.player1.canOpen(g)) {
					g.setLocked(false);
				}
				// Treat the gate like Solid geometry.
				else {
					level.player1.adjustY();
					if (level.player1.intersects(g))
						level.player1.adjustX(g);
				}
			}
		}

		// If the Player is scaling a Ladder, move it to the center of the Ladder.
		for (Ladder l : level.ladders) {
			if (l.actorIsOn(level.player1)) {
				level.player1.adjustX(l);
				break;
			}
		}

		// Let the Player know if it can move along a Bar.
		boolean playerOneIsOnBar = false;
		for (Bar b : level.bars) {
			if (b.actorIsOn(level.player1)) {
				playerOneIsOnBar = true;
				break;
			}
		}
		level.player1.setOnBar(playerOneIsOnBar);

		/**
		 * Ideally, this large chunk of difficult-to-read logic would be moved out into smaller functions.
		 */
		boolean playerOneIsFalling = (level.player1.getY() < HEIGHT - level.player1.getHeight());
		boolean playerOneIsStandingOnSlippery = false;
		boolean playerOneIsOnLadder = false;
		if (playerOneIsFalling) {
			for (Solid s : level.solids) {
				if (s instanceof Diggable) {
					Diggable d = (Diggable) s;
					if (!d.isFilled())
						continue;
				}
				if (s.actorIsOn(level.player1)) {
					//					System.out.println("G:"+s.getX()+","+s.getY()+":"+s.getBoundingBox());
					//					System.out.println("P:"+level.player1.getX()+","+level.player1.getY()+":"+level.player1.getBoundingBox());
					playerOneIsFalling = false;
					if (s instanceof Slippery)
						playerOneIsStandingOnSlippery = true;
					break;
				}
			}
			if (playerOneIsFalling) {
				for (Bar b : level.bars) {
					if (b.actorIsOn(level.player1)) {
						playerOneIsFalling = false;
						break;
					}
				}
				if (playerOneIsFalling) {
					for (Ladder l : level.ladders) {
						if (l.actorIsOn(level.player1)) {
							playerOneIsFalling = false;
							playerOneIsOnLadder = true;
							break;
						}
					}
					if (playerOneIsFalling) {
						for (Gate g : level.gates) {
							if (g.actorIsOn(level.player1)) {
								if (level.player1.canOpen(g))
									g.setLocked(false);
								else {
									playerOneIsFalling = false;
									break;
								}
							}
						}
						if (playerOneIsFalling) {
							for (Enemy e : level.enemies) {
								if (e.isInHole()) {
									if (e.actorIsOn(level.player1)) {
										playerOneIsFalling = false;
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		if (playerOneIsFalling && !playerOneWasOnLadder)
			level.player1.setVelocity(0, Player.VELOCITY);
		else if (level.player1.getY() % UNIT_HEIGHT != 0 && !playerOneIsOnLadder)
			level.player1.adjustY();
		level.player1.accelerate(playerOneIsStandingOnSlippery);
		level.player1.move();
		level.player1.tickAnimFrame();
		level.player1.setIsFalling(playerOneIsFalling);
		playerOneWasOnLadder = playerOneIsOnLadder;

		repaint(); // Repaint indirectly calls paintComponent().
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isEditing()) {
			String key = getEditorKey();
			if (key != null && !key.equals("")) {
				// Allow clicking-and-dragging to add nodes.
				addNode(
						key,
						GamePanel.getXUnitPosition(e.getX()),
						GamePanel.getYUnitPosition(e.getY())
						);
				// This is necessary for the eraser. In all other cases, we're placing a node under the cursor,
				// so you can't tell that the cursor image isn't following the cursor.
				mouseXPos = getXUnitPosition(e.getX());
				mouseYPos = getYUnitPosition(e.getY());
				parent.openNew.setEnabled(false);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Keep track of mouse coordinates so we can display the current editor tool at the cursor.
		mouseXPos = getXUnitPosition(e.getX());
		mouseYPos = getYUnitPosition(e.getY());
		repaint();
	}

}
