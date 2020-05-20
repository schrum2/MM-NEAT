package icecreamyou.LodeRunner;
/**
 * Slippery ground makes the player decelerate gradually rather than stop suddenly.
 */
public class Slippery extends Diggable {
	

	public static final String TITLE = "Slippery";
	public static final String NAME = "slippery";
	public static final String DEFAULT_IMAGE_PATH = "slippery.png";
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

	public Slippery(int x, int y) {
		super(x, y);
	}

}
