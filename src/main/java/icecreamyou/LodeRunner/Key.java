package icecreamyou.LodeRunner;
/**
 * Keys unlock Unlockables.
 */
public abstract class Key extends Pickup {
	public static final String FILE_PATH = "src/main/java/icecreamyou/LodeRunner/";

	public static final String DEFAULT_IMAGE_PATH = FILE_PATH+"key-portal.png";
	@Override
	public String defaultImagePath() {
		return DEFAULT_IMAGE_PATH;
	}

	public Key(int x, int y) {
		super(x, y);
	}
	
}
