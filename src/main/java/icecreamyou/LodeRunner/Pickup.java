package icecreamyou.LodeRunner;
import java.awt.Graphics;

/**
 * Pickups can be removed from the world by Actors.
 */
public class Pickup extends WorldNode {

	private boolean pickedUp = false;
	
	public Pickup(int x, int y) {
		super(x, y);
	}

	/**
	 * Determine whether this pickup has been picked up.
	 */
	public boolean isPickedUp() {
		return pickedUp;
	}
	
	/**
	 * Pick up the pickup.
	 */
	public void pickUp() {
		pickedUp = true;
	}
	
	@Override
	public void draw(Graphics g) {
		if (!pickedUp) {
			super.draw(g);
		}
	}
	
	@Override
	public boolean canOccupySameLocationInEditorAs(WorldNode other)  {
		return (other instanceof Hole) || (other instanceof Enemy);
	}
	
}
