package icecreamyou.LodeRunner;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.southwestern.tasks.loderunner.LodeRunnerVGLCUtil;

/**
 * The level environment.
 */
public class Level {
	/**
	 * Pre-compiled regex patterns for parsing layout files.
	 * These temporary classes are very ugly, but it moves the actual regex to
	 * the class files (and, because of inheritance, removes the need to write
	 * regexes for most classes).
	 */
	public static final Pattern barRegex       = new Bar(0,0).pattern();
	public static final Pattern coinRegex      = new Coin(0,0).pattern();
	public static final Pattern diggableRegex  = new Diggable(0,0).pattern();
	public static final Pattern enemyRegex     = new Enemy(0,0).pattern();
	public static final Pattern gateRegex      = new Gate(0,0,KeyColor.RED).pattern();
	public static final Pattern gateKeyRegex   = new GateKey(0,0,KeyColor.RED).pattern();
	public static final Pattern holeRegex      = new Hole(0,0).pattern();
	public static final Pattern ladderRegex    = new Ladder(0,0).pattern();
	public static final Pattern playerRegex    = new Player(0,0).pattern();
	public static final Pattern portalRegex    = new Portal(0,0).pattern();
	public static final Pattern portalKeyRegex = new PortalKey(0,0).pattern();
	public static final Pattern slipperyRegex  = new Slippery(0,0).pattern();
	public static final Pattern spikesRegex    = new Spikes(0,0).pattern();
	public static final Pattern solidRegex     = new Solid(0,0).pattern();
	public static final Pattern treasureRegex  = new Treasure(0,0).pattern();
	public static final Pattern nextRegex	   = Pattern.compile("nextLevel:([\\-\\w\\s]+)");
	
	/**
	 * Sets of types of WorldNodes that are important to each level.
	 */
	public Set<Bar> bars           = new HashSet<Bar>();
	public Set<Diggable> diggables = new HashSet<Diggable>();
	public Set<Enemy> enemies 	   = new HashSet<Enemy>(23);
	public Set<Gate> gates         = new HashSet<Gate>();
	public Set<Hole> holes 	       = new HashSet<Hole>(37);
	public Set<Key> keys           = new HashSet<Key>(11);
	public Set<Ladder> ladders     = new HashSet<Ladder>();
	public Set<Pickup> pickups     = new HashSet<Pickup>();
	public Set<Slippery> slipperys = new HashSet<Slippery>();
	public Set<Spikes> spikes      = new HashSet<Spikes>();
	public Set<Solid> solids       = new HashSet<Solid>();
	Set<Solid> solidsOnly          = new HashSet<Solid>();

	public Player player1;
	public Player player2;
	public Portal portal;
	
	public boolean portalKeyExists = false;

	private String name;
	private String nextLevel = null;
	
	public Level() {
		bars      = new HashSet<Bar>();
		diggables = new HashSet<Diggable>();
		enemies   = new HashSet<Enemy>(23);
		gates     = new HashSet<Gate>();
		holes 	  = new HashSet<Hole>(37);
		keys      = new HashSet<Key>(11);
		ladders   = new HashSet<Ladder>();
		pickups   = new HashSet<Pickup>();
		slipperys = new HashSet<Slippery>();
		spikes    = new HashSet<Spikes>();
		solids    = new HashSet<Solid>();
		solidsOnly= new HashSet<Solid>();
		player1   = null;
		player2   = null;
		portal    = null;
		name	  = "";
	}
	public Level(String name) {
		this.name = name;
		String[] lines = Layout.getLayoutAsArray(name +".txt");
		for (String line : lines) {
			if (line == null || line.equals(""))
				continue;
			add(line);
		}
	}
	
	/**
	 * Level from list of lists format from a GAN or json
	 * @param level
	 */
	public Level(List<List<Integer>> level) {
		String newLevel = LodeRunnerVGLCUtil.convertLodeRunnerJSONtoIceCreamYou(level);
		constructLevelFromString(newLevel);
	}
	
	/**
	 * Copy level from an existing level
	 * @param copy
	 */
	public Level(Level copy) {
		String stringLevel = Layout.levelToString(copy);
		constructLevelFromString(stringLevel);
	}

	/**
	 * Take String read from level file and create the level from it
	 * @param newLevel
	 */
	public void constructLevelFromString(String newLevel) {
		Scanner scan = new Scanner(newLevel);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			if(line == null || line.equals("")) 
				continue;
			add(line);
			//System.out.println(line);
		}
		scan.close();
	}
	
	/**
	 * Draw everything in a level.
	 */
	public void draw(Graphics g, Mode mode) {
		// These are drawn in careful order.
		if (mode == Mode.MODE_EDITING)
			for (WorldNode n : holes)
				n.draw(g);
		for (WorldNode n : solids)
			n.draw(g);
		for (WorldNode n : bars)
			n.draw(g);
		for (WorldNode n : gates)
			n.draw(g);
		for (WorldNode n : ladders)
			n.draw(g);
		if (portal != null && mode != Mode.MODE_PAUSED)
			portal.draw(g);
		for (WorldNode n : pickups)
			n.draw(g);
		if (mode != Mode.MODE_PAUSED)
			for (WorldNode n : enemies)
				n.draw(g);
		if (player1 != null)
			player1.draw(g);
		if (player2 != null)
			player2.draw(g);
		if (mode != Mode.MODE_EDITING)
			for (WorldNode n : holes)
				n.draw(g);
	}
	
	/**
	 * Check that nothing interferes with the placement of the WorldNode other,
	 * and if something does interfere, then remove it.
	 */
	public void checkRemoveCollisionInEditor(WorldNode other) {
		for (Iterator<Bar> i = bars.iterator(); i.hasNext();) {
			Bar n = i.next();
			if (n != other && n.getX() == other.getX() && n.getY() == other.getY() &&
					!n.canOccupySameLocationInEditorAs(other))
				i.remove();
		}
		for (Iterator<Enemy> i = enemies.iterator(); i.hasNext();) {
			Enemy n = i.next();
			if (n != other && n.getX() == other.getX() && n.getY() == other.getY() &&
					!n.canOccupySameLocationInEditorAs(other))
				i.remove();
		}
		for (Iterator<Gate> i = gates.iterator(); i.hasNext();) {
			Gate n = i.next();
			if (n != other && n.getX() == other.getX() && n.getY() == other.getY() &&
					!n.canOccupySameLocationInEditorAs(other))
				i.remove();
		}
		for (Iterator<Hole> i = holes.iterator(); i.hasNext();) {
			Hole n = i.next();
			if (n != other && n.getX() == other.getX() && n.getY() == other.getY() &&
					!n.canOccupySameLocationInEditorAs(other))
				i.remove();
		}
		for (Iterator<Ladder> i = ladders.iterator(); i.hasNext();) {
			Ladder n = i.next();
			if (n != other && n.getX() == other.getX() && n.getY() == other.getY() &&
					!n.canOccupySameLocationInEditorAs(other))
				i.remove();
		}
		for (Iterator<Pickup> i = pickups.iterator(); i.hasNext();) {
			Pickup n = i.next();
			if (n != other && n.getX() == other.getX() && n.getY() == other.getY() &&
					!n.canOccupySameLocationInEditorAs(other)) {
				i.remove();
				keys.remove(n);
			}
		}
		for (Iterator<Solid> i = solids.iterator(); i.hasNext();) {
			Solid n = i.next();
			if (n != other && n.getX() == other.getX() && n.getY() == other.getY() &&
					!n.canOccupySameLocationInEditorAs(other)) {
				i.remove();
				solidsOnly.remove(n);
				diggables.remove(n);
				slipperys.remove(n);
				spikes.remove(n);
			}
		}
		if (portal != null && portal != other && portal.getX() == other.getX() && portal.getY() == other.getY() &&
				!portal.canOccupySameLocationInEditorAs(other))
			portal = null;
		if (player1 != null && player1 != other && player1.getX() == other.getX() && player1.getY() == other.getY() &&
				player1.canOccupySameLocationInEditorAs(other))
			player1 = null;
		if (player2 != null &&  player2 != other && player2.getX() == other.getX() && player2.getY() == other.getY() &&
				player2.canOccupySameLocationInEditorAs(other))
			player2 = null;
	}
	
	/**
	 * Generate a fresh copy of the current level by reading it again from the
	 * layout file, if possible.
	 */
	public static Level cleanCopy(Level other) {
		if (other.name == null || other.name.equals(""))
			return new Level();
		return new Level(other.name);
	}
	
	/**
	 * Return the name of the next level, if there is one.
	 */
	public String getNextLevel() {
		return nextLevel;
	}
	
	/**
	 * Save the current level to a layout file.
	 */
	public void save(String filename) {
		Layout.save(this, filename);
		name = filename;
	}
	
	/**
	 * Match a layout line string, add the generated object to the relevant sets, and return the new object.
	 * It would be nice to get each match a little closer to the class itself,
	 * but that is not really possible in a clean way.
	 */
	public WorldNode add(String line) {
		Matcher m;

		m = barRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Bar b = new Bar(x, y);
			bars.add(b);
			return b;
		}

		m = coinRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Coin c = new Coin(x, y);
			pickups.add(c);
			return c;
		}

		m = diggableRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Diggable d = new Diggable(x, y);
			diggables.add(d);
			solids.add(d);
			return d;
		}

		m = enemyRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			//y = dropToSolid(x, y);
			Enemy e = new Enemy(x, y);
			enemies.add(e);
			return e;
		}

		m = gateRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			KeyColor c = GateKey.stringToColor(m.group(3));
			Gate g = new Gate(x, y, c);
			gates.add(g);
			return g;
		}

		m = gateKeyRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			String c = m.group(3);
			GateKey k = new GateKey(x, y, c);
			keys.add(k);
			pickups.add(k);
			return k;
		}

		m = holeRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Hole h = new Hole(x, y);
			holes.add(h);
			return h;
		}

		m = ladderRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Ladder l = new Ladder(x, y);
			ladders.add(l);
			return l;
		}
		
		m = playerRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			int p = Integer.parseInt(m.group(3));
			if (p == 1) {
				player1 = new Player(x, y, 1);
				return player1;
			}
			else {
				player2 = new Player(x, y, 2);
				return player2;
			}
		}

		m = portalRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			portal = new Portal(x, y);
			return portal;
		}

		m = portalKeyRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			PortalKey k = new PortalKey(x, y);
			keys.add(k);
			pickups.add(k);
			portalKeyExists = true;
			return k;
		}

		m = slipperyRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Slippery s = new Slippery(x, y);
			slipperys.add(s);
			diggables.add(s);
			solids.add(s);
			return s;
		}

		m = spikesRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Spikes s = new Spikes(x, y);
			spikes.add(s);
			solids.add(s);
			return s;
		}
		
		m = solidRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Solid s = new Solid(x, y);
			solids.add(s);
			solidsOnly.add(s);
			return s;
		}
		
		m = treasureRegex.matcher(line);
		if (m.matches()) {
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			Treasure t = new Treasure(x, y);
			pickups.add(t);
			return t;
		}
		
		m = nextRegex.matcher(line);
		if (m.matches()) {
			nextLevel = m.group(1);
			return null;
		}
		
		return null;
	}
}
