package icecreamyou.LodeRunner;
import java.awt.Graphics;

/**
 * When a player touches an unlocked portal, it wins the level.
 */
public class Portal extends WorldNode implements Unlockable {
	

	public static final String TITLE = "Portal";
	public static final String NAME = "portal";
	public static final String DEFAULT_IMAGE_PATH = "portal.png";
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
	
	private boolean locked = true;

	public Portal(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void draw(Graphics g) {
		if (locked)
			Picture.draw(g, "portal-locked.png", getX(), getY());
		else
			super.draw(g);
	}

	@Override
	public boolean isUnlockedBy(Key key) {
		return (key instanceof PortalKey);
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

}
