package icecreamyou.LodeRunner;
/**
 * The superclass of everything that can move, including the player, enemies,
 * and movable environment geometry.
 */
public class Movable extends WorldNode {
	
	//The velocity of the object. At least one of xVel and yVel must be zero at any given time.
	int xVel, yVel;

	public Movable(int x, int y) {
		super(x, y);
		setVelocity(0, 0);
	}
	public Movable(int x, int y, int w, int h) {
		super(x, y, w, h);
		setVelocity(0, 0);
	}
	public Movable(int x, int y, int w, int h, int xv, int yv) {
		super(x, y, w, h);
		setVelocity(xv, yv);
	}
	
	/**
	 * The x-velocity of the object.
	 */
	public int getXVelocity() {
		return xVel;
	}
	/**
	 * The y-velocity of the object.
	 */
	public int getYVelocity() {
		return yVel;
	}
	
	/**
	 * Move the object based on its velocity.
	 */
	public void move() {
		x += xVel;
		y += yVel;
		r.setLocation(x, y);
	}
	/**
	 * Perform actions that affect the object's motion.
	 */
	public void accelerate() {
		clip();
	}
	/**
	 * Set the object's velocity.
	 * @param xv The new x-velocity
	 * @param yv The new y-velocity
	 */
	public void setVelocity(int xv, int yv) {
		xVel = xv;
		yVel = yv;
	}
	
}
