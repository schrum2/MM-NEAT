package icecreamyou.LodeRunner;
/**
 * Solids collide with everything.
 */
public class Solid extends WorldNode implements ActorCollision {
	

	public static final String TITLE = "Steel";
	public static final String NAME = "solid";
	public static final String DEFAULT_IMAGE_PATH = "solid.png";
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

	public Solid(int x, int y) {
		super(x, y);
	}

	@Override
	public boolean actorIsOn(Actor a) {
		return (GamePanel.sorta_equals(a.getY() + a.getHeight(), getY(), a.getMaxVelocity() - 2) &&
				a.getX() + a.getWidth() > getX() &&
				a.getX() < getX() + getWidth());
	}

}
