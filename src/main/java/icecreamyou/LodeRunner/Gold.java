package icecreamyou.LodeRunner;
/**
 * All Gold has a value and can be picked up to acquire that value.
 */
public class Gold extends Pickup {
	
	public static final String TITLE = "Gold";
	public static final String NAME = "gold";
	public static final String DEFAULT_IMAGE_PATH = "coin.png";
	
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
	
	private int value;

	public Gold(int x, int y) {
		super(x, y);
		value = 0;
	}
	public Gold(int x, int y, int val) {
		super(x, y);
		value = val;
	}

	public int getValue() {
		return value;
	}
	
}
