package icecreamyou.LodeRunner;
/**
 * Keys unlock Unlockables.
 */
public abstract class Key extends Pickup {
	

	public static final String DEFAULT_IMAGE_PATH = "key-portal.png";
	@Override
	public String defaultImagePath() {
		return DEFAULT_IMAGE_PATH;
	}

	public Key(int x, int y) {
		super(x, y);
	}
	
}
