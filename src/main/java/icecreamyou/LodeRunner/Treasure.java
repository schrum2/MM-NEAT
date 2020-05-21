package icecreamyou.LodeRunner;
/**
 * Treasure is Gold with value 3.
 */
public class Treasure extends Gold {
	

	public static final String TITLE = "Treasure";
	public static final String NAME = "treasure";
	public static final String DEFAULT_IMAGE_PATH = "treasure.png";
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

	public Treasure(int x, int y) {
		super(x, y);
	}
	
	@Override
	public int getValue() {
		return 3;
	}

}
