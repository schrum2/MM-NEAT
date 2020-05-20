package icecreamyou.LodeRunner;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Set;

/**
 * Dug represents a hole dug by Players that can trap enemies.
 * This is not the environment-decoration kind of hole; for that, see Hole.
 */
public class Dug extends WorldNode {
	
	/**
	 * The amount of time in milliseconds before the hole refills.
	 */
	public static final int RECOVERY_TIME = 16000;
	
	/**
	 * The last time the hole was dug.
	 */
	private long lastUpdated;
	/**
	 * The environment.
	 */
	private Level level;
	
	public Dug(Level l, int x, int y) {
		super(x, y);
		this.level = l;
		lastUpdated = System.currentTimeMillis();
	}
	
	/**
	 * Check whether to refill, and if it's time, do so.
	 * @return
	 *    true if the hole was refilled; false otherwise.
	 */
	public boolean update() {
		long l = System.currentTimeMillis();
		if (l - lastUpdated > RECOVERY_TIME) {
			if (intersects(level.player1))
				level.player1.setAlive(false);
			if (level.player2 != null && intersects(level.player2))
				level.player2.setAlive(false);
			for (Enemy e : level.enemies)
				if (intersects(e))
					e.markForRespawn();
			for (Diggable w : level.diggables)
				if (!w.isFilled() && w.getX() == getX() && w.getY() == getY()) {
					w.setFilled(true);
					break;
				}
			return true;
		}
		return false;
	}
	/**
	 * Remove old Dugs.
	 */
	public static void removeOldDugs(Set<Dug> dugs) {
		for (Iterator<Dug> i = dugs.iterator(); i.hasNext();)
			if (i.next().update())
				i.remove();
	}
	
	@Override
	public void draw(Graphics g) {
		Picture.draw(g, "hole-empty.png", getX(), getY());
	}

}
