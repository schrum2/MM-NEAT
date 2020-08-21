package icecreamyou.LodeRunner;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.regex.Pattern;

public abstract class WorldNode {
	
	// Placeable classes should generally override title() and name().
	/**
	 * The regex used to identify this node type in a layout file.
	 */
	public Pattern pattern() { return Pattern.compile(name() +":(\\d+),(\\d+)"); }
	/**
	 * The human-friendly name of the node type.
	 */
	public String title() { return "WorldNode"; }
	/**
	 * The machine name of the node type, e.g. for use in layout files.
	 */
	public String name() { return "worldNode"; }
	/**
	 * The location of the basic image representing this node type.
	 */
	public String defaultImagePath() { return null; }
	
	/**
	 * The coordinates of the upper-left corner of the object.
	 */
	int x, y;

	/**
	 * The bounding box. (All collision in this game is rectangular.)
	 */
	Rectangle r;
	
	public WorldNode() {
		x = 0;
		y = 0;
		r = new Rectangle(x, y, 0, 0);
	}
	public WorldNode(int x, int y) {
		this.x = x;
		this.y = y;
		r = new Rectangle(x, y, GamePanel.UNIT_WIDTH, GamePanel.UNIT_HEIGHT);
	}
	/**
	 * Create a new WorldNode.
	 * @param x The x-coordinate of the upper-left corner of the node.
	 * @param y The y-coordinate of the upper-left corner of the node.
	 * @param w The width of the WorldNode. (Try to avoid using this.)
	 * @param h The height of the WorldNode. (Try to avoid using this.)
	 */
	public WorldNode(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		r = new Rectangle(x, y, w, h);
	}
	
	/**
	 * The x-coordinate of the upper-left vertex of the node.
	 */
	public int getX() {
		return x;
	}
	/**
	 * The y-coordinate of the upper-left vertex of the node.
	 */
	public int getY() {
		return y;
	}
	/**
	 * The width of the bounding box.
	 */
	public int getWidth() {
		return (int) r.getWidth();
	}
	/**
	 * The height of the bounding box.
	 */
	public int getHeight() {
		return (int) r.getHeight();
	}
	/**
	 * The upper-left vertex of the bounding box.
	 */
	public Point getLocation() {
		return r.getLocation();
	}
	
	/**
	 * The bounding box (used for collision).
	 */
	public Rectangle getBoundingBox() {
		return r;
	}

	/**
	 * Keep the object in the GamePanel.
	 */
	public void clip() {
		if (x < 0)
			x = 0;
		else if (x + getWidth() > GamePanel.WIDTH)
			x = GamePanel.WIDTH - getWidth();
		if (y < 0)
			y = 0;
		else if (y + getHeight() > GamePanel.HEIGHT)
			y = GamePanel.HEIGHT - getHeight();
		r.setLocation(x,y);
	}
	
	/**
	 * Draw the current node.
	 * @param g The graphics context used to draw the node.
	 */
	public void draw(Graphics g) {
		// If we have an image, draw it; otherwise, just draw a rectangle.
		if (defaultImagePath() == null)
			g.fillRect(getX(), getY(), getWidth(), getHeight());
		else
			Picture.draw(g, defaultImagePath(), getX(), getY());
	}
	
	/**
	 * Determine whether another WorldNode intersects with this one.
	 * @param other The other WorldNode to check for intersection.
	 * @return true if the WorldNodes intersect; false otherwise.
	 */
	public boolean intersects(WorldNode other) {
		return getBoundingBox().intersects(other.getBoundingBox());
	}
	
	/**
	 * Determine whether this node and another node can be located in the same place when the game starts.
	 * Subclasses should override this if they can co-locate with other nodes.
	 * This is typically useful for classes without "hard" collision.
	 * Be sure to enforce the transitive invariant that
	 * A.canOccupySameLocationInEditorAs(B) == B.canOccupySameLocationInEditorAs(A)
	 * @param other The other node to check for collision.
	 * @return true if the nodes can co-locate; false otherwise.
	 */
	public boolean canOccupySameLocationInEditorAs(WorldNode other) {
		return false;
	}

	/**
	 * Move the node to newX.
	 * This is useful for adjustments, but should not be used for movement if
	 * possible. Movable classes should extend Movable and use move().
	 */
	protected void setX(int newX) {
		x = newX;
		r.setLocation(x, y);
	}
	/**
	 * Move the node to newY.
	 * This is useful for adjustments, but should not be used for movement if
	 * possible. Movable classes should extend Movable and use move().
	 */
	protected void setY(int newY) {
		y = newY;
		r.setLocation(x, y);
	}
	
	@Override
	public String toString() {
		return name() +":"+ getX() +","+ getY();
	}
	
}
