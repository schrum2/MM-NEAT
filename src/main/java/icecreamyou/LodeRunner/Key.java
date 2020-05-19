package icecreamyou.LodeRunner;
/**
 * Keys unlock Unlockables.
 */
public abstract class Key extends Pickup {

	public static final String DEFAULT_IMAGE_PATH = "key-portal.png";
	public static final String FILE_PATH = "src/main/java/icecreamyou/LodeRunner/";
	@Override
	public String defaultImagePath() {
		return FILE_PATH+DEFAULT_IMAGE_PATH;
	}

	public Key(int x, int y) {
		super(x, y);
	}
	
}
