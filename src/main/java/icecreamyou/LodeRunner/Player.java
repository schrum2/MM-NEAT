package icecreamyou.LodeRunner;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Players are Actors controlled by human input.
 */
public class Player extends Actor {

	@Override
	public Pattern pattern() {
		return Pattern.compile(name() +":(\\d+),(\\d+),(\\d+)");
	}
	

	public static final String TITLE = "Player";
	public static final String NAME = "player";
	public static final String DEFAULT_IMAGE_PATH = "player.png";
	@Override
	public String title() {
		return TITLE;
	}
	@Override
	public String name() {
		return NAME;
	}
	@Override
	public String defaultImagePath() {
		return DEFAULT_IMAGE_PATH;
	}
	
	/**
	 * The Player's maximum speed.
	 */
	public static final int VELOCITY = 6;
	/**
	 * Controls how quickly the Player stops when sliding on slippery surfaces.
	 * Time to stop without reversing in milliseconds is
	 * CEIL(ln(SLIPPERY_TOLERANCE / VELOCITY) / ln(SLIPPERY_COEFF)) * TIMER_INTERVAL
	 * While reversing, SLIPPERY_COEFF is replaced with SLIPPERY_COEFF ^ 4.
	 */
	public static final double SLIPPERY_COEFF = 0.95;
	/**
	 * How slow the Player can go before stopping on a slippery surface.
	 */
	public static final double SLIPPERY_TOLERANCE = 0.5;
	
	private int player = 1;
	private int animFrame = 0;
	private boolean isOnBar = false;
	private boolean isFalling = false;
	
	private double lastXVel = 0.0;
	
	private HashSet<Key> keys = new HashSet<Key>();
	
	private boolean alive = true;
	
	public Player(int x, int y) {
		// Schrum: Subtract 2 to make the player slightly smaller, and enable him to slip through all dug holes
		super(x, y, GamePanel.UNIT_WIDTH-2, GamePanel.UNIT_HEIGHT);
	}
	public Player(int x, int y, int player) {
		// Schrum: Subtract 2 to make the player slightly smaller, and enable him to slip through all dug holes
		super(x, y, GamePanel.UNIT_WIDTH-2, GamePanel.UNIT_HEIGHT);
		this.player = player;
	}
	
	@Override
	public void move() {
		if (alive)
			super.move();
	}
	
	/**
	 * React to movement on slippery surfaces.
	 * @param slippery Whether the player is currently on a slippery surface.
	 */
	public void accelerate(boolean slippery) {
		// Allow getting to maximum speed instantly from a full stop or going in the same direction.
		if (Math.abs(xVel) == VELOCITY
				&& lastXVel != xVel * SLIPPERY_COEFF
				&& (lastXVel == 0.0
						|| (xVel > 0 && lastXVel > 0.0)
						|| (xVel < 0 && lastXVel < 0.0)
					)
			)
			lastXVel = xVel;
		// Slow down gradually.
		else if (slippery && yVel == 0 && lastXVel != 0.0) {
			lastXVel *= SLIPPERY_COEFF;
			if ((xVel > 0 && lastXVel < 0.0) || (xVel < 0 && lastXVel > 0.0))
				lastXVel *= SLIPPERY_COEFF * SLIPPERY_COEFF * SLIPPERY_COEFF;
			if (Math.abs(lastXVel) < SLIPPERY_TOLERANCE)
				lastXVel = 0.0;
		}
		// Apply the acceleration.
		if (slippery) {
			if (lastXVel > 0.0)
				xVel = (int) Math.round(lastXVel);
			else if (lastXVel < 0.0) {
				// Rounding works slightly differently for negative numbers.
				xVel = (int) (-Math.round(Math.abs(lastXVel)));
			}
			else
				xVel = 0;
		}
		// If we moved onto a non-slippery surface, stop.
		else if (Math.abs(xVel) != VELOCITY) {
			xVel = 0;
			lastXVel = 0.0;
		}
		accelerate();
	}
	
	@Override
	public void draw(Graphics g) {
		if (isOnBar) {
			Picture.draw(g, "player-bar.png", getX(), getY());
			return;
		}
		else if (isFalling && (animFrame % 4 == 0 || animFrame % 4 == 1)) {
			Picture.draw(g, "player-falling.png", getX(), getY());
			return;
		}
		int frame = animFrame / 2 + 1;
		if (Math.abs(lastXVel) != VELOCITY)
			Picture.draw(g, "player.png", getX(), getY());
		else if (xVel < 0)
			Picture.draw(g,"player-left-"+ frame +".png", getX(), getY());
		else if (xVel > 0)
			Picture.draw(g, "player-right-"+ frame +".png", getX(), getY());
		else
			Picture.draw(g, "player.png", getX(), getY());
	}
	
	@Override
	public void pickUp(Pickup p) {
		if (p instanceof Key) {
			keys.add((Key) p);
			p.pickUp();
		}
		else
			super.pickUp(p);
	}
	/**
	 * Determines whether the Player has a key that can open the Unlockable.
	 */
	public boolean canOpen(Unlockable u) {
		for (Key k : keys)
			if (u.isUnlockedBy(k))
				return true;
		return false;
	}
	/**
	 * Make sure the player is at a unit height.
	 */
	public void adjustY() {
		int origY = y;
		y = (int) Math.round(y / (double) GamePanel.UNIT_HEIGHT) * GamePanel.UNIT_HEIGHT;
		if (y != origY) {
			yVel = 0;
		}
	}
	/**
	 * Move the player to the center of a ladder or keep the player from overlapping a solid.
	 */
	public void adjustX(WorldNode n) {
		// If the player is going up or down a ladder, move it to the center of the ladder.
		if (n instanceof Ladder) {
			if (yVel != 0) {
				int origX = x;
				x = (int) Math.round(x / (double) GamePanel.UNIT_WIDTH) * GamePanel.UNIT_WIDTH;
				if (x != origX) {
					xVel = 0;
				}
			}
		}
		// If a player is barely overlapping a solid, move it outside of the solid.
		else if (n instanceof Solid || n instanceof Gate) {
			/*
			if (getX() < n.getX())
				x = n.getX() - getWidth() - 1;
			else if (getX() > n.getX())
				x = n.getX() + n.getWidth() + 1;
			 */
			if (getX() < n.getX() && getX() + getWidth() < n.getX() + VELOCITY + 1)
				x = n.getX() - getWidth() - 1;
			else if (getX() > n.getX() && getX() > n.getX() + n.getWidth() - VELOCITY - 1)
				x = n.getX() + n.getWidth() + 1;
		}
	}
	
	/**
	 * Switch animation frames.
	 */
	void tickAnimFrame() {
		animFrame++;
		if (animFrame > 9)
			animFrame = 0;
	}
	/**
	 * Keep track of whether the player is on a bar.
	 */
	public void setOnBar(boolean playerIsOnBar) {
		isOnBar = playerIsOnBar;
	}
	/**
	 * Keep track of whether the player is falling.
	 */
	public void setIsFalling(boolean playerIsFalling) {
		isFalling = playerIsFalling;
	}
	/**
	 * Set the player to alive or dead.
	 */
	public void setAlive(boolean b) {
		alive = b;
	}
	/**
	 * Determines whether the player is alive or dead.
	 */
	public boolean isAlive() {
		return alive;
	}
	
	@Override
	public int getMaxVelocity() {
		return VELOCITY;
	}
	
	@Override
	public String toString() {
		return name() +":"+ getX() +","+ getY() +","+ player;
	}

}
