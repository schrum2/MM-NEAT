package icecreamyou.LodeRunner;
/**
 * Players (and eventually actors) can move horizontally across bars, as well as drop vertically from them.
 */
public class Bar extends WorldNode implements ActorCollision {
	
	public static final String TITLE = "Bar";
	public static final String NAME = "bar";
	public static final String DEFAULT_IMAGE_PATH = "bar.gif";
	
	
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

	public Bar(int x, int y) {
		super(x, y);
	}

	@Override
	public boolean actorIsOn(Actor a) {
		return (GamePanel.sorta_equals(a.getY(), getY(), a.getMaxVelocity() - 2) &&
				a.getX() + a.getWidth() > getX() &&
				a.getX() < getX() + getWidth());
	}

}
