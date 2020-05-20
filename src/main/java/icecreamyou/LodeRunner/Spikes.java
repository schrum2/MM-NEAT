package icecreamyou.LodeRunner;
/**
 * Spikes kill Players on touch.
 */
public class Spikes extends Solid {
	public static final String FILE_PATH = "src/main/java/icecreamyou/LodeRunner/";

	public static final String TITLE = "Spikes";
	public static final String NAME = "spikes";
	public static final String DEFAULT_IMAGE_PATH = FILE_PATH+"spikes.png";
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

	public Spikes(int x, int y) {
		super(x, y);
	}

}
