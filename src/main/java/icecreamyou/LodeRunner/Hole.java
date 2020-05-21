package icecreamyou.LodeRunner;
/**
 * A hole is a visual environment object which has no collision.
 * Its only real functional purpose is to trick the player or conceal pickups.
 * Counter-intuitively, this is not the kind of hole that Players can dig;
 * that kind of hole is called a Dug.
 */
public class Hole extends WorldNode {
	
	public static final String TITLE = "Hole";
	public static final String NAME = "hole";
	public static final String DEFAULT_IMAGE_PATH = "hole.png";

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
	
	public Hole() {
		super();
	}
	public Hole(int x, int y) {
		super(x, y, 0, 0);
	}
	
	@Override
	public boolean canOccupySameLocationInEditorAs(WorldNode other)  {
		return (other instanceof Pickup);
	}
	
}
