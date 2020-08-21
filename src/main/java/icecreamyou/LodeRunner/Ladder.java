package icecreamyou.LodeRunner;
/**
 * Actors can climb ladders.
 * Well, sort of. Right now only Players can climb ladders.
 * Climbing behavior should really be abstracted into this class.
 */
public class Ladder extends WorldNode implements ActorCollision {
	

	public static final String TITLE = "Ladder";
	public static final String NAME = "ladder";
	public static final String DEFAULT_IMAGE_PATH = "ladder.png";
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

	public Ladder(int x, int y) {
		super(x, y);
	}
	
	@Override
	public boolean actorIsOn(Actor a) {
		return (a.getY() + a.getHeight() >= getY() &&
				a.getY() + a.getHeight() / 2 <= getY() + getHeight() &&
				a.getX() + a.getWidth() / 2 > getX() &&
				a.getX() + a.getWidth() / 2 < getX() + getWidth());
	}

}
