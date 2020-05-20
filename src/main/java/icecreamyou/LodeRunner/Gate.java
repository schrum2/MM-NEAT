package icecreamyou.LodeRunner;
import java.awt.Graphics;
import java.util.regex.Pattern;

/**
 * Gates act like Solids but can be disabled using matching GateKeys.
 */
public class Gate extends WorldNode implements Unlockable, ActorCollision {
	
	@Override
	public Pattern pattern() {
		return Pattern.compile(name() +":(\\d+),(\\d+),(\\w+)");
	}
	
	public static final String TITLE = "Gate";
	public static final String NAME = "gate";
	public static final String DEFAULT_IMAGE_PATH = "gate-red.png";
	
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
	 * Whether the gate is currently open or locked.
	 */
	private boolean open = false;
	
	/**
	 * The color of the gate. Only keys of the same color can open the gate.
	 */
	private KeyColor color;

	public Gate(int x, int y, KeyColor c) {
		super(x, y);
		color = c;
	}

	/**
	 * Get the gate's color.
	 */
	public KeyColor getColor() {
		return color;
	}
	
	@Override
	public boolean intersects(WorldNode other) {
		// No collision if the gate is open.
		if (!open)
			return getBoundingBox().intersects(other.getBoundingBox());
		return false;
	}

	@Override
	public void draw(Graphics g) {
		if (open)
			Picture.draw(g, "gate-disabled.png", getX(), getY());
		else
			Picture.draw(g, "gate-"+ GateKey.colorToString(color).toLowerCase() +".png", getX(), getY());
	}

	@Override
	public boolean isUnlockedBy(Key key) {
		if (key instanceof GateKey) {
			GateKey gk = (GateKey) key;
			return getColor().equals(gk.getColor());
		}
		return false;
	}

	@Override
	public boolean isLocked() {
		return !open;
	}

	@Override
	public void setLocked(boolean locked) {
		open = !locked;
	}

	@Override
	public boolean actorIsOn(Actor a) {
		return (isLocked() &&
				GamePanel.sorta_equals(a.getY() + a.getHeight(), getY(), a.getMaxVelocity() - 2) &&
				a.getX() + a.getWidth() > getX() &&
				a.getX() < getX() + getWidth());
	}
	
	@Override
	public String toString() {
		return name() +":"+ getX() +","+ getY() +","+ GateKey.colorToString(getColor());
	}

}
