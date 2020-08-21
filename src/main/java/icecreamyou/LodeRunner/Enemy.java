package icecreamyou.LodeRunner;
import java.awt.Graphics;

/**
 * Enemies try to hunt down and kill Players.
 */
public class Enemy extends Actor implements ActorCollision {
	public static final String TITLE = "Enemy";
	public static final String NAME = "enemy";
	public static final String DEFAULT_IMAGE_PATH = "enemy.png";
	
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
	 * Enemies' maximum speed.
	 */
	public static final int VELOCITY = 4;
	/**
	 * This is the probability per check for intersection. The actual
	 * probability that an Enemy picks up a given coin is approximately
	 * PICKUP_PROBABILITY * (Coin.WIDTH / VELOCITY) == 0.15.
	 */
	public static final double PICKUP_PROBABILITY = 0.02;
	/**
	 * The minimum amount of time to wait before climbing out of a hole.
	 */
	public static final int CLIMB_OUT_TIME = 10000;
	
	/**
	 * The coordinates at which the Enemy will respawn after being trapped in a
	 * filled hole.
	 */
	private int initX, initY;
	/**
	 * The last time the Enemy fell into a hole.
	 */
	private long lastClimbOutTime;
	/**
	 * Whether the Enemy is in a hole.
	 */
	private boolean isInHole = false;
	/**
	 * Whether the enemy will try to respawn at the next tick.
	 */
	private boolean needsRespawning = false;

	public Enemy(int x, int y) {
		super(x, y);
		initX = x;
		initY = y;
		lastClimbOutTime = System.currentTimeMillis();
		if (Math.random() < 0.5)
			setVelocity(VELOCITY, 0);
		else
			setVelocity(-VELOCITY, 0);
	}
	
	/**
	 * Reverse directions.
	 */
	public void reverse() {
		xVel = -xVel;
	}
	
	/**
	 * Reverse the Enemy's direction when it hits a Solid.
	 * @param n The WorldNode to check for intersection.
	 */
	public void adjustX(WorldNode n) {
		// If an Enemy is barely overlapping a solid, move it outside of the solid.
		if (n instanceof Solid) {
			int oX = x;
			if (getX() < n.getX()
					&& getX() + getWidth() < n.getX() + VELOCITY + 1
					&& getY() == n.getY())
				x = n.getX() - getWidth();
			else if (getX() > n.getX()
					&& getX() > n.getX() + n.getWidth() - VELOCITY - 1
					&& getY() == n.getY())
				x = n.getX() + n.getWidth();
			// If we hit a solid, reverse direction.
			if (oX != x)
				reverse();
		}
	}
	
	@Override
	public void draw(Graphics g) {
		if (isInHole)
			Picture.draw(g, "enemy.png", getX(), getY());
		else if (xVel < 0)
			Picture.draw(g, "enemy-left.png", getX(), getY());
		else if (xVel > 0)
			Picture.draw(g, "enemy-right.png", getX(), getY());
		else
			Picture.draw(g, "enemy.png", getX(), getY());
	}
	
	@Override
	public boolean canOccupySameLocationInEditorAs(WorldNode other)  {
		return (other instanceof Pickup);
	}
	@Override
	public void pickUp(Pickup item) {
		// Only pick up a single coin.
		if (item instanceof Coin
				&& getGoldValue() == 0
				&& Math.random() < Enemy.PICKUP_PROBABILITY)
			super.pickUp(item);
	}
	
	/**
	 * Mark the Enemy as currently in a hole.
	 */
	public void setInHole(boolean b) {
		isInHole = b;
		if (isInHole)
			lastClimbOutTime = System.currentTimeMillis();
	}
	/**
	 * Whether the enemy is currently in a hole.
	 */
	public boolean isInHole() {
		return isInHole;
	}
	/**
	 * Check whether this Enemy can fall into a given hole.
	 */
	public boolean canFallInDug(Dug d) {
		return (GamePanel.sorta_equals(d.getX(), getX(), getMaxVelocity()) // Check x-location
				&& (d.getY() == getY() + getHeight() // Check y-location
						|| d.getY() == getY())); // Let Enemies walk into holes from the side
	}
	/**
	 * Whether the Enemy can climb out of a hole.
	 * Assumes the Enemy is actually in a hole.
	 */
	public boolean canClimbOutYet() {
		long l = System.currentTimeMillis();
		if (l - lastClimbOutTime > CLIMB_OUT_TIME) {
			return true;
		}
		return false;
	}
	/**
	 * Climb out of a hole.
	 * Assumes the enemy is actually in a hole.
	 */
	public void climbOut() {
		isInHole = false;
		setY(getY() - getHeight());
		// Make sure the Enemy doesn't fall back into the hole.
		accelerate();
		move();
	}
	/**
	 * Try climbing out of a hole.
	 * @return true if the Enemy ends up out of a hole; false if it is still in one.
	 */
	public boolean tryClimbOut() {
		if (isInHole) {
			if (canClimbOutYet())
				climbOut();
			else
				return false;
		}
		return true;
	}
	/**
	 * Attempt to respawn the Enemy. This will succeed if:
	 * - The Enemy has just been trapped in a hole, and
	 * - The Player is not very close to the respawn point.
	 * If it continues to fail, the Enemy will just climb out even though it's
	 * buried.
	 */
	public void tryRespawn(Player p) {
		if (needsRespawning
				&& !(p.getY() >= initY
				&& p.getY() <= initY + getHeight()
				&& p.getX() >= initX - getWidth() * 2
				&& p.getX() <= initX + getWidth() * 3)) {
			needsRespawning = false;
			x = initX;
			y = initY;
			lastClimbOutTime = System.currentTimeMillis();
			isInHole = false;
		}
	}
	/**
	 * Mark this Enemy as ready for respawning.
	 */
	public void markForRespawn() {
		needsRespawning = true;
	}

	@Override
	public boolean actorIsOn(Actor a) {
		return (GamePanel.sorta_equals(a.getY() + a.getHeight(), getY(), a.getMaxVelocity() - 2) &&
				a.getX() + a.getWidth() > getX() &&
				a.getX() < getX() + getWidth());
	}
	
	@Override
	public int getMaxVelocity() {
		return VELOCITY;
	}

}
