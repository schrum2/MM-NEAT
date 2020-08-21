package icecreamyou.LodeRunner;
import java.awt.Graphics;
import java.util.regex.Pattern;

/**
 * GateKeys open Gates of matching color.
 */
public class GateKey extends Key {
	
	@Override
	public Pattern pattern() {
		return Pattern.compile(name() +":(\\d+),(\\d+),(\\w+)");
	}
	
	public static final String TITLE = "Gate key";
	public static final String NAME = "gateKey";
	public static final String DEFAULT_IMAGE_PATH = "key-red.png";
	
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

	/**
	 * The color of the Key.
	 */
	private KeyColor color;
	
	public GateKey(int x, int y) {
		super(x, y);
		color = KeyColor.RED;
	}
	public GateKey(int x, int y, KeyColor color) {
		super(x, y);
		this.color = color;
	}
	GateKey(int x, int y, String color) {
		super(x, y);
		this.color = stringToColor(color);
	}
	
	/**
	 * Get the Key's color.
	 */
	public KeyColor getColor() {
		return color;
	}
	
	@Override
	public void draw(Graphics g) {
		if (!isPickedUp())
			Picture.draw(g, "key-"+ colorToString(color).toLowerCase() +".png", getX(), getY());
	}
	
	@Override
	public String toString() {
		return name() +":"+ getX() +","+ getY() +","+ colorToString(getColor());
	}

	/**
	 * Convert a String to its corresponding KeyColor.
	 */
	public static KeyColor stringToColor(String s) {
		if (s == null)
			return null;
		else if (s.equals("RED"))
			return KeyColor.RED;
		else if (s.equals("BLUE"))
			return KeyColor.BLUE;
		else if (s.equals("GREEN"))
			return KeyColor.GREEN;
		else if (s.equals("YELLOW"))
			return KeyColor.YELLOW;
		else if (s.equals("ORANGE"))
			return KeyColor.ORANGE;
		else if (s.equals("PURPLE"))
			return KeyColor.PURPLE;
		return null;
	}
	/**
	 * Convert a KeyColor to its corresponding String.
	 * Of course, you could also just do (String) KeyColor.
	 */
	public static String colorToString(KeyColor c) {
		if (c == null)
			return null;
		else if (c.equals(KeyColor.RED))
			return "RED";
		else if (c.equals(KeyColor.BLUE))
			return "BLUE";
		else if (c.equals(KeyColor.GREEN))
			return "GREEN";
		else if (c.equals(KeyColor.YELLOW))
			return "YELLOW";
		else if (c.equals(KeyColor.ORANGE))
			return "ORANGE";
		else if (c.equals(KeyColor.PURPLE))
			return "PURPLE";
		return null;
	}
	
}
