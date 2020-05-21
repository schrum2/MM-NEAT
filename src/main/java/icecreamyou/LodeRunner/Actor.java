package icecreamyou.LodeRunner;
/**
 * An actor is something which the player may perceive to "have a mind of its own."
 * That is, an actor moves autonomously, rather than simply being a static piece of the environment.
 * Actors can also pick up items.
 */
public class Actor extends Movable {
	
	/**
	 * The actor's maximum speed.
	 * @see getMaxVelocity()
	 */
	private static final int VELOCITY = 5;
	
	/**
	 * The amount of gold the player has picked up.
	 */
	private int goldValue = 0;

	/**
	 * Create an actor with various properties.
	 */
	public Actor(int x, int y) {
		super(x, y);
	}
	public Actor(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	public Actor(int x, int y, int w, int h, int xv, int yv) {
		super(x, y, w, h, xv, yv);
	}

	/**
	 * Pick up an item.
	 * @param item The item to be picked up.
	 */
	public void pickUp(Pickup item) {
		if (item instanceof Coin) {
			goldValue += ((Coin) item).getValue();
			item.pickUp();
		}
		else if (item instanceof Treasure) {
			goldValue += ((Treasure) item).getValue();
			item.pickUp();
		}
	}
	/**
	 * The amount of gold the actor currently possesses.
	 */
	public int getGoldValue() {
		return goldValue;
	}
	/**
	 * Take away the actor's gold.
	 */
	public void resetGoldValue() {
		goldValue = 0;
	}
	
	/**
	 * The actor's maximum speed.
	 */
	public int getMaxVelocity() {
		return VELOCITY;
	}

}
