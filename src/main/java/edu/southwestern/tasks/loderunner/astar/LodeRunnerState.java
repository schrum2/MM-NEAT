package edu.southwestern.tasks.loderunner.astar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.LodeRunnerRenderUtil;
import edu.southwestern.tasks.loderunner.LodeRunnerVGLCUtil;
import edu.southwestern.util.MiscUtil;
//import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import edu.southwestern.util.search.State;

/**
 * Runs an A* algorithm on a lode runner level to see if it is beatable 
 * @author kdste
 *
 */
public class LodeRunnerState extends State<LodeRunnerState.LodeRunnerAction>{
	//private static final int LODE_RUNNER_DIG_ACTION_COST = 20; // No dig action, and thus no extra cost
	
	public static final int LODE_RUNNER_TILE_EMPTY = 0;
	public static final int LODE_RUNNER_TILE_GOLD = 1;
	public static final int LODE_RUNNER_TILE_ENEMY = 2;
	public static final int LODE_RUNNER_TILE_DIGGABLE = 3;
	public static final int LODE_RUNNER_TILE_LADDER = 4;
	public static final int LODE_RUNNER_TILE_ROPE = 5;
	public static final int LODE_RUNNER_TILE_GROUND = 6;
	public static final int LODE_RUNNER_TILE_SPAWN = 7;
	// Big cost to discourage moving sideways through diggable ground in a way that is sometimes illegal
	private static final double SIDEWAYS_DIG_COST_MULTIPLIER = 100;
	
	private boolean allowWeirdMoves;
	private List<List<Integer>> level;
	private HashSet<Point> goldLeft; //set containing the points with gold 
	//private HashSet<Point> dugHoles; // Too expensive to track the dug up spaces in the state. Just allow the agent to move downward through diggable blocks
	public int currentX; 
	public int currentY;

	/**
	 * Declares a heuristic for the search to depend on 
	 */
	public static Heuristic<LodeRunnerAction,LodeRunnerState> manhattanToFarthestGold = new Heuristic<LodeRunnerAction,LodeRunnerState>(){

		/**
		 * Calculates the Manhattan distance from the player to the farthest gold coin
		 * @return Manhattan distance from play to farthest coin 
		 */
		@Override
		public double h(LodeRunnerState s) {
			double maxDistance = 0;
			HashSet<Point> goldLeft = s.goldLeft;
			for(Point p: goldLeft) {
				int xDistance = Math.abs(s.currentX - p.x);
				int yDistance = Math.abs(s.currentY - p.y);
				maxDistance = Math.max(maxDistance, (xDistance+yDistance));
			}
			return maxDistance;
		}

	};

	/**
	 * Defines the Actions that can be used by a player for Lode Runner 
	 * @author kdste
	 *
	 */
	public static class LodeRunnerAction implements Action{
		public enum MOVE {RIGHT,LEFT,UP,DOWN}; //, DIG_RIGHT, DIG_LEFT} // Too computationally expensive to model digging as actions that change the state
		private MOVE movement;

		/**
		 * Constructor for a lode runnner action
		 * @param m A move the player can make 
		 */
		public LodeRunnerAction(MOVE m) {
			this.movement = m;
		}

		/**
		 * Gets the current action 
		 * @return The current action 
		 */
		public MOVE getMove() {
			return movement;
		}

		/**
		 * Checks if the current action is equal to the parameter
		 * @return True if they are equal, false otherwise 
		 */
		public boolean equals(Object other) {
			if(other instanceof LodeRunnerAction) {
				return ((LodeRunnerAction) other).movement.equals(this.movement); 
			}
			return false;
		}

		/**
		 * @return String representation of the action
		 */
		public String toString() {
			return movement.toString();
		}
	}

	/**
	 * Runs an A* algorithm on the level specified and then displays a buffered image that shows the solution path if there is one 
	 * @param args
	 */
	public static void main(String args[]) {
		Parameters.initializeParameterCollections(args);
		//converts Level in VGLC to hold all 8 tiles so we can get the real spawn point from the level 
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH+"Level 1.txt"); //converts to JSON
		LodeRunnerState start = new LodeRunnerState(level);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
//			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, Parameters.parameters.integerParameter( "aStarSearchBudget"));
			//actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, 145000); // Fails on Level 4 with only 9 treasures
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, 150000); // Succeeds on Level 4 with only 9 treasures
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		System.out.println(mostRecentVisited.toString());
		System.out.println("actionSequence: " + actionSequence);
		//System.out.println("actionSequence length: " + actionSequence.size());
		try {
			//visualizes the points visited with blue and whit x's
			BufferedImage visualPath = vizualizePath(level,mostRecentVisited,actionSequence,start);
			try { //displays window with the rendered level and the solution path/visited states
				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X, 
						LodeRunnerRenderUtil.LODE_RUNNER_ROWS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, Image.SCALE_FAST)));
				panel.add(label);
				frame.add(panel);
				frame.pack();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fills a set with points, to keep a reference of where the gold is in the level
	 * then removes them and makes them emtpy spaces 
	 * @param level A level 
	 * @return Set of points 
	 */
	public static HashSet<Point> fillGold(List<List<Integer>> level) {
		HashSet<Point> gold = new HashSet<>();
		int tile; 
		//loop through level adding points where it finds gold 
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				tile = level.get(i).get(j);
				//System.out.println("The tile at " + j + "," + i + " = " +tile);
				if(tile == LODE_RUNNER_TILE_GOLD) { 
					// FOR DEBUGGING!
					//if(gold.size() < 9)
						gold.add(new Point(j,i));//saves reference to that gold in the 
					level.get(i).set(j, LODE_RUNNER_TILE_EMPTY);//removes gold and places an empty tile 
				}

			}
		}
		return gold;
	}

	/**
	 * Loops through the level to find the spawn point from the original level 
	 * @param level
	 * @return The spawn point 
	 */
	private static Point getSpawnFromVGLC(List<List<Integer>> level) {
		Point start = new Point();
		int tile;
		boolean done = false;
		for(int i = 0; !done && i < level.size(); i++) {
			for(int j = 0; !done && j < level.get(i).size(); j++){
				tile = level.get(i).get(j);
				//System.out.println("The tile at " + j + "," + i + " = " +tile);
				if(tile == LODE_RUNNER_TILE_SPAWN) {//7 maps to spawn point  
					start = new Point(j, i);
					level.get(i).set(j, LODE_RUNNER_TILE_EMPTY);//removes spawn point and places an empty tile 
					done = true;
				}
			}
		}
		return start;
	}

	public LodeRunnerState(List<List<Integer>> level, boolean weird) {
		this(level, getSpawnFromVGLC(level), weird);
	}
	
	/**
	 * Constructor that only takes a level, 
	 * this makes it so that it grabs the original spawn point and fills the gold set with
	 * the locations of the gold for that level 
	 * @param level A level in JSON form 
	 */
	public LodeRunnerState(List<List<Integer>> level) {
		this(level, getSpawnFromVGLC(level), Parameters.parameters.booleanParameter("allowWeirdLodeRunnerActions"));
	}

	/**
	 * Constructor that takes a level and a start point. 
	 * This construct is can be used to specify a starting point for easier testing 
	 * @param level Level in JSON form 
	 * @param start The spawn point 
	 */
	public LodeRunnerState(List<List<Integer>> level, Point start, boolean cheat) {
		this(level, getGoldLeft(level), start.x, start.y, cheat);
	}


//	private static HashSet<Point> getDugHoles() {
//		HashSet<Point> dug = new HashSet<>();
//		return dug;
//	}

	/**
	 * gets the gold left in the level by calling the fill gold method 
	 * @param level A level in JSON form 
	 * @return A set of point with the locations of the gold in the level 
	 */
	private static HashSet<Point> getGoldLeft(List<List<Integer>> level) {
		HashSet<Point> gold = fillGold(level);
		return gold;
	}


	/**
	 * The standard construct that takes all specifies all the parameters 
	 * used in the getSuccessor method to get a the next state 
	 * may add a way to track the enemies in the future, but we are using a simple version right now 
	 * @param level Level in JSON form 
	 * @param goldLeft Set with the locations of the gold 
	 * @param currentX X coordinate of spawn 
	 * @param currentY Y coordinate of spawn 
	 */
	private LodeRunnerState(List<List<Integer>> level, HashSet<Point> goldLeft, int currentX, int currentY, boolean cheat) {
		this.level = level;
		this.goldLeft = goldLeft;
		this.currentX = currentX;
		this.currentY = currentY;
		this.allowWeirdMoves = cheat;
	}

	/**
	 * Visualizes the solution path. 
	 * Red X's are the solution path, white X's are the other states that were visited 
	 * Displays in a window 
	 * @param level A level 
	 * @param mostRecentVisited Set of all visited locations 
	 * @param actionSequence Solution set 
	 * @param start Start state  
	 * @throws IOException
	 */
	public static BufferedImage vizualizePath(List<List<Integer>> level, HashSet<LodeRunnerState> mostRecentVisited, 
			ArrayList<LodeRunnerAction> actionSequence, LodeRunnerState start) throws IOException {
		List<List<Integer>> fullLevel = ListUtil.deepCopyListOfLists(level); //copies level to draw solution path over it 
		fullLevel.get(start.currentY).set(start.currentX, LODE_RUNNER_TILE_SPAWN);// puts the spawn back into the visualization
		for(Point p : start.goldLeft) { //puts all the gold back 
			fullLevel.get(p.y).set(p.x, LODE_RUNNER_TILE_GOLD);
		}
		BufferedImage[] images;
		BufferedImage visualPath;
		if(Parameters.parameters.booleanParameter("showInteractiveLodeRunnerIceCreamYouVisualization")) {
			images = LodeRunnerRenderUtil.loadIceCreamYouTiles(LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_PATH); //Initializes the array that hold the tile images
			visualPath = LodeRunnerRenderUtil.createIceCreamYouImage(fullLevel, LodeRunnerRenderUtil.ICE_CREAM_YOU_IMAGE_WIDTH, LodeRunnerRenderUtil.ICE_CREAM_YOU_IMAGE_HEIGHT, images);	
		}
		else {
			images = LodeRunnerRenderUtil.loadImagesNoSpawnTwoGround(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH); //Initializes the array that hold the tile images
			//creates a buffered image from the level to be displayed 
			visualPath = LodeRunnerRenderUtil.createBufferedImage(fullLevel, LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X, 
					LodeRunnerRenderUtil.LODE_RUNNER_ROWS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, images);
		}
//		//creates a buffered image from the level to be displayed 
//		BufferedImage visualPath = LodeRunnerRenderUtil.createBufferedImage(fullLevel, LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X, 
//				LodeRunnerRenderUtil.LODE_RUNNER_ROWS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, images);
		if(mostRecentVisited != null) {
			Graphics2D g = (Graphics2D) visualPath.getGraphics();
			if(Parameters.parameters.booleanParameter("showInteractiveLodeRunnerIceCreamYouVisualization")) {
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke((float)3.5));
			}
			else
				g.setColor(Color.WHITE);
			for(LodeRunnerState s : mostRecentVisited) {
				int x = s.currentX;
				int y = s.currentY;
				if(Parameters.parameters.booleanParameter("showInteractiveLodeRunnerIceCreamYouVisualization")) {
					g.drawLine(x*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,y*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y,(x+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,(y+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y);
					g.drawLine((x+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,y*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y, x*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,(y+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y);
				}
				else {
					g.drawLine(x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y,(x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
					g.drawLine((x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
				}
			}
			if(actionSequence != null) {
				if(Parameters.parameters.booleanParameter("showInteractiveLodeRunnerIceCreamYouVisualization")) {
					g.setColor(Color.RED);
					g.setStroke(new BasicStroke((float)3.5));
				}
				else
					g.setColor(Color.BLUE);
				LodeRunnerState current = start;
				for(LodeRunnerAction a : actionSequence) {
					int x = current.currentX;
					int y = current.currentY;
					if(Parameters.parameters.booleanParameter("showInteractiveLodeRunnerIceCreamYouVisualization")) {
						g.drawLine(x*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,y*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y,(x+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,(y+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y);
						g.drawLine((x+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,y*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y, x*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_X,(y+1)*LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_Y);
					}
					else {
						g.drawLine(x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y,(x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
						g.drawLine((x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
					}
					current = (LodeRunnerState) current.getSuccessor(a);
				}
			}
		}
		return visualPath;
	}

	/**
	 * Gets the next state from the current state
	 * @return The next state, if null the state was not legal at that time 
	 */
	@Override
	public State<LodeRunnerAction> getSuccessor(LodeRunnerAction a) {
		int newX = currentX;
		int newY = currentY; 
		// Too expensive to track dug holes in the state
//		HashSet<Point> newDugHoles = new HashSet<>(); 
//		for(Point p:dugHoles) {
//			newDugHoles.add(p);
//		}
		assert inBounds(newX,newY): "x is:" + newX + "\ty is:"+newY + "\t" + inBounds(newX,newY);
		if(a.getMove().equals(LodeRunnerAction.MOVE.RIGHT)) {
			int beneath = !inBounds(newX,newY+1) ? -1 : tileAtPosition(newX,newY+1);
			if(passable(newX+1, newY) && 
					( (tileAtPosition(newX, newY) == LODE_RUNNER_TILE_ROPE) || 
					  (tileAtPosition(newX, newY) == LODE_RUNNER_TILE_LADDER) ) )
				newX++;
			else if(allowWeirdMoves && 
					inBounds(newX+1,newY) &&
					(beneath == -1 || beneath == LODE_RUNNER_TILE_LADDER || beneath == LODE_RUNNER_TILE_DIGGABLE || beneath == LODE_RUNNER_TILE_GROUND) &&
					tileAtPosition(newX+1,newY) == LODE_RUNNER_TILE_DIGGABLE 
					&& diggablePath(newX+1,newY)) 
				// This is a weird case that allows moving sideways through diggable ground, which is only allowed because
				// the player could hypothetically have dug the ground above to make this possible. 
				newX++;
			else if(tileAtPosition(newX,newY) != LODE_RUNNER_TILE_LADDER &&// Could run on/across ladders too
					beneath != -1 && // Means out of bounds
					beneath != LODE_RUNNER_TILE_LADDER &&
					beneath != LODE_RUNNER_TILE_DIGGABLE &&
					beneath != LODE_RUNNER_TILE_GROUND)//checks if there is ground under the player
				return null;// cannot move right 
			else if(passable(newX+1, newY)) 
				newX++;
			else return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.LEFT)) {
			// Turns out you can walk on the bottom of the screen with nothing beneath you
			int beneath = !inBounds(newX,newY+1) ? -1 : tileAtPosition(newX,newY+1);
			if(passable(newX-1, newY) && 
					( (tileAtPosition(newX, newY) == LODE_RUNNER_TILE_ROPE) || 
							(tileAtPosition(newX, newY) == LODE_RUNNER_TILE_LADDER) ) )
				newX--;
			else if(allowWeirdMoves && 
					inBounds(newX-1,newY) && 
					(beneath == -1 || beneath == LODE_RUNNER_TILE_LADDER || beneath == LODE_RUNNER_TILE_DIGGABLE || beneath == LODE_RUNNER_TILE_GROUND) &&
					tileAtPosition(newX-1,newY) == LODE_RUNNER_TILE_DIGGABLE && 
					diggablePath(newX-1,newY)) 
				// This is a weird case that allows moving sideways through diggable ground, which is only allowed because
				// the player could hypothetically have dug the ground above to make this possible. 
				newX--;
			else if(tileAtPosition(newX,newY) != LODE_RUNNER_TILE_LADDER &&// Could run on/across ladders too
					beneath != -1 && // Bottom of screen, nothing beneath player
					beneath != LODE_RUNNER_TILE_LADDER &&
					beneath != LODE_RUNNER_TILE_DIGGABLE &&
					beneath != LODE_RUNNER_TILE_GROUND)//checks if there is ground under the player
				return null;//fall down 
			else if(passable(newX-1,newY)) 
				newX--;
			else return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.UP)) {
			if(	(passable(newX, newY-1) || // Do not allow moving up ladders into solid tiles
				(allowWeirdMoves && inBounds(newX, newY-1) && tileAtPosition(newX, newY-1)==LODE_RUNNER_TILE_DIGGABLE)) && // Except diggable of weird moves allowed 
				inBounds(newX, newY-1) &&
				tileAtPosition(newX, newY)==LODE_RUNNER_TILE_LADDER) // Be on a ladder to climb
				newY--;
			else return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.DOWN)) { 
			// Might be able to go down if resulting location is inbounds and tile beneath is not solid ground
			if(		inBounds(newX, newY+1) && 
					tileAtPosition(newX,newY+1) != LODE_RUNNER_TILE_GROUND) 
				// But special case for diggable ground: verify it was possible to dig out the square
				if(tileAtPosition(newX,newY+1) != LODE_RUNNER_TILE_DIGGABLE || 
					// Diggable, but tile to left was not empty, and provides platform for digging
				   (inBounds(newX-1,newY+1) && tileAtPosition(newX-1,newY+1) != LODE_RUNNER_TILE_EMPTY) || // && passable(newX-1,newY)) || 
					// Diggable, but tile to right was not empty, and provides platform for digging
				   (inBounds(newX+1,newY+1) && tileAtPosition(newX+1,newY+1) != LODE_RUNNER_TILE_EMPTY)) // && passable(newX+1,newY)))
					newY++;
			else return null;
		}
		//have these two actions return null while testing on level one because it doesn't require digging to win 
//		else if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_LEFT)) {
//			if(inBounds(newX-1,newY+1) && diggable(newX-1,newY+1)) {
//				newDugHoles.add(new Point(newX-1, newY+1));
//				//level.get(newY+1).set(newX-1, LODE_RUNNER_TILE_EMPTY); //just for testing if it actually digging
//			}else return null; 
//		}
//		else if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_RIGHT)) {
//			if(inBounds(newX+1,newY+1) && diggable(newX+1,newY+1)) {
//				newDugHoles.add(new Point(newX+1, newY+1));
//				//level.get(newY+1).set(newX+1, LODE_RUNNER_TILE_EMPTY);//just for testing if it actually digging
//			}else return null;
//		}
		//check if it is in teh set, then create a new set that contains all but that one
		HashSet<Point> newGoldLeft = new HashSet<>();
		for(Point p : goldLeft) {
			if(!p.equals(new Point(newX, newY))){
				newGoldLeft.add(p);
			}
		}

		assert inBounds(newX,newY) : "x is:" + newX + "\ty is:"+newY + "\t"+ inBounds(newX,newY);
		LodeRunnerState result = new LodeRunnerState(level, newGoldLeft, newX, newY, allowWeirdMoves);
//		if(a.getMove().equals(LodeRunnerAction.MOVE.LEFT)) {
//			System.out.println("AFTER");
//			renderLevelAndPause((LodeRunnerState) result);
//		}
		return result;
	}

	/**
	 * If there might be a way to reach this spot by digging from above
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean diggablePath(int x, int y) {
		while(inBounds(x,y) && tileAtPosition(x,y) == LODE_RUNNER_TILE_DIGGABLE) {
			y--; // Move up
		}
		return passable(x,y);
	}

	/**
	 * Gets a list of all of the lode runner actions
	 * @return List of valid lode runner actions
	 */
	@Override
	public ArrayList<LodeRunnerAction> getLegalActions(State<LodeRunnerAction> s) {
		ArrayList<LodeRunnerAction> vaildActions = new ArrayList<>();
		//System.out.println(level);
		//renderLevelAndPause((LodeRunnerState) s);
		for(LodeRunnerAction.MOVE move: LodeRunnerAction.MOVE.values()) {
			//Everything besides the if statement is for debugging purposes, delete later 
			//LodeRunnerAction a = new LodeRunnerAction(move);
			//System.out.println(s+"\t"+move+"\t"+s.getSuccessor(a));
			if(s.getSuccessor(new LodeRunnerAction(move)) != null) {
				vaildActions.add(new LodeRunnerAction(move));
			}
		}
		//System.out.println(vaildActions);

		return vaildActions;
	}

	/**
	 * For troubleshooting. Draw the level in a graphics window with the representation
	 * updated according to the contents of the state.
	 * @param s
	 */
	@SuppressWarnings("unused")
	private void renderLevelAndPause(LodeRunnerState theState) {
		// This code renders an image of the level with the agent in it
		try {
			List<List<Integer>> copy = ListUtil.deepCopyListOfLists(theState.level );
			copy.get(theState.currentY).set(theState.currentX, LODE_RUNNER_TILE_SPAWN); 
			for(Point t : theState.goldLeft) {
				copy.get(t.y).set(t.x, LODE_RUNNER_TILE_GOLD); 
			}
//			for(Point dug : theState.dugHoles) {
//				copy.get(dug.y).set(dug.x, LODE_RUNNER_TILE_EMPTY); 
//			}
			LodeRunnerRenderUtil.getBufferedImage(copy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
	}

	/**
	 * Determines if a tile is passable or not 
	 * @param x Current X coordinate
	 * @param y Current Y coordinate
	 * @return True if you can pass that tile, false otherwise 
	 */
	public boolean passable(int x, int y) {
		if(!inBounds(x,y)) return false; // fail for bad bounds before tileAtPosition check
		int tile = tileAtPosition(x,y);
		if((	tile==LODE_RUNNER_TILE_EMPTY  || tile==LODE_RUNNER_TILE_ENEMY || 
				tile==LODE_RUNNER_TILE_LADDER || tile==LODE_RUNNER_TILE_ROPE)) {
			return true;
		}
		return false; 
	}

	/**
	 * Helps to ensure search does not exceed boundarys of the level 
	 * @param x X coordinate 
	 * @param y Y coordinate 
	 * @return true if inside the level, false otherwise 
	 */
	private boolean inBounds(int x, int y) {
		return y>=0 && x>=0 && y<level.size() && x<level.get(0).size();
	}

	/**
	 * Easy access to the given tile integer at given (x,y) coordinates.
	 * 
	 * @param x horizontal position 
	 * @param y vertical position 
	 * @return tile int at those coordinates
	 */
	public int tileAtPosition(int x, int y) {
//		if(dugHoles.contains(new Point(x, y))) {
//			return LODE_RUNNER_TILE_EMPTY;
//		}
		return level.get(y).get(x);
	}

	/**
	 * Determines if you have won the level
	 * @return True if there is no gold left, false otherwise
	 */
	@Override
	public boolean isGoal() {
		//when the hash set is empty 
		return goldLeft.isEmpty();
	}

	/**
	 * It moves on square at a time 
	 * @return Number of tiles for every action 
	 */
	@Override
	public double stepCost(State<LodeRunnerAction> s, LodeRunnerAction a) {
		LodeRunnerState state = (LodeRunnerState) s;
		int beneath = !inBounds(state.currentX,state.currentY+1) ? -1 : tileAtPosition(state.currentX,state.currentY+1);
		// The model allows for moving sideways through diggable ground, with the assumption that the ground above would have been previously dug out
		if(		a.getMove().equals(LodeRunnerAction.MOVE.LEFT) && 
				state.inBounds(state.currentX - 1, state.currentY) &&
				state.tileAtPosition(state.currentX - 1, state.currentY) == LODE_RUNNER_TILE_DIGGABLE) {
			double cost = 1; // Base cost of 1 for the movement
			int y = state.currentY;
			int x = state.currentX - 1;
			while(state.inBounds(x,y) && state.tileAtPosition(x,y) == LODE_RUNNER_TILE_DIGGABLE) {
				cost++; // Increase cost
				y--; // Move up
				// It is assumed that getSuccessor will assure that only valid moves are considered, so we must reach an empty tile eventually
			}
			if(!state.inBounds(x,y)) cost = Double.POSITIVE_INFINITY; // Impossible action
			//System.out.println("High cost left: " + cost);
			return cost*cost*SIDEWAYS_DIG_COST_MULTIPLIER; // Arbitrarily double cost to discourage
		} else if(	a.getMove().equals(LodeRunnerAction.MOVE.RIGHT) &&
					state.inBounds(state.currentX + 1, state.currentY) &&
					state.tileAtPosition(state.currentX + 1, state.currentY) == LODE_RUNNER_TILE_DIGGABLE) {
			double cost = 1; // Base cost of 1 for the movement
			int y = state.currentY;
			int x = state.currentX + 1;
			while(state.inBounds(x,y) && state.tileAtPosition(x,y) == LODE_RUNNER_TILE_DIGGABLE) {
				cost++; // Increase cost
				y--; // Move up
				// It is assumed that getSuccessor will assure that only valid moves are considered, so we must reach an empty tile eventually
			}
			if(!state.inBounds(x,y)) cost = Double.POSITIVE_INFINITY; // Impossible action
			//System.out.println("High cost right: " + cost);
			return cost*cost*SIDEWAYS_DIG_COST_MULTIPLIER; // Arbitrarily double cost to discourage
		} else if(a.getMove().equals(LodeRunnerAction.MOVE.DOWN) && beneath == LODE_RUNNER_TILE_DIGGABLE) {
			// Digging down is expensive. Must move to side, dig, move back, then fall ... maybe more to make space.
			return 4;
		} else {
			return 1;
		}
	}

	/**
	 * Returns a string repesentation of a Lode Runner State 
	 */
	@Override
	public String toString() {
		return "Size:" + goldLeft.size() + " (" + currentX + ", " + currentY + ")"; // + " DugCount:" +dugHoles.size();
	}

	/**
	 * Helps to avoid excessive back tracking 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentX;
		result = prime * result + currentY;
		//result = prime * result + ((dugHoles == null) ? 0 : dugHoles.hashCode());
		result = prime * result + ((goldLeft == null) ? 0 : goldLeft.hashCode());
		return result;
	}

	/**
	 * Helps to avoid excessive back tracking 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LodeRunnerState other = (LodeRunnerState) obj;
		if (currentX != other.currentX)
			return false;
		if (currentY != other.currentY)
			return false;
//		if (dugHoles == null) {
//			if (other.dugHoles != null)
//				return false;
//		} else if (!dugHoles.equals(other.dugHoles))
//			return false;
		if (goldLeft == null) {
			if (other.goldLeft != null)
				return false;
		} else if (!goldLeft.equals(other.goldLeft))
			return false;
		return true;
	}





}
