package icecreamyou.LodeRunner;
import java.awt.Graphics;

/**
 * Diggables are Solids that can be replaced with holes.
 * The player can "dig" such a hole in order to get through a surface or trap
 * an enemy.
 */
public class Diggable extends Solid {
	public static final String TITLE = "Ground";
	public static final String NAME = "diggable";
	public static final String DEFAULT_IMAGE_PATH = "diggable.gif";
	
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
	 * Is the Diggable filled?
	 */
	private boolean filled = true;

	public Diggable(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void draw(Graphics g) {
		if (filled)
			super.draw(g);
	}
	
	/**
	 * Fill or dig this.
	 */
	public void setFilled(boolean b) {
		filled = b;
	}
	/**
	 * Whether this is filled.
	 */
	public boolean isFilled() {
		return filled;
	}

}
