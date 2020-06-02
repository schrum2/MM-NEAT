package edu.southwestern.tasks.loderunner.astar;

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

import edu.southwestern.tasks.loderunner.LodeRunnerRenderUtil;
import edu.southwestern.tasks.loderunner.LodeRunnerVGLCUtil;
//import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import edu.southwestern.util.search.State;

/**
 * 
 * @author kdste
 *
 */
public class LodeRunnerState extends State<LodeRunnerState.LodeRunnerAction>{
	public static final int LODE_RUNNER_TILE_EMPTY = 0;
	public static final int LODE_RUNNER_TILE_GOLD = 1;
	public static final int LODE_RUNNER_TILE_ENEMY = 2;
	public static final int LODE_RUNNER_TILE_DIGGABLE = 3;
	public static final int LODE_RUNNER_TILE_LADDER = 4;
	public static final int LODE_RUNNER_TILE_ROPE = 5;
	public static final int LODE_RUNNER_TILE_GROUND = 6;
	public static final int LODE_RUNNER_TILE_SPAWN = 7;
	private List<List<Integer>> level;
	private HashSet<Point> goldLeft; //set containing the points with gold 
	private HashSet<Point> dugHoles;
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
			int maxDistance = 0;
			HashSet<Point> goldLeft = s.goldLeft;
			for(Point p: goldLeft) {
				int xDistance = Math.abs(s.currentX - p.x);
				int yDistance = Math.abs(s.currentY - p.y);
				Math.max(maxDistance, (xDistance+yDistance));
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
		public enum MOVE {RIGHT,LEFT,UP,DOWN, DIG_RIGHT, DIG_LEFT}//removed dig up and dig down while testing on level 1
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

	public static void main(String args[]) {
		//converts Level in VGLC to hold all 8 tiles so we can get the real spawn point from the level 
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH+"Level 3.txt"); //converts to JSON
		LodeRunnerState start = new LodeRunnerState(level);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, 100000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		System.out.println(mostRecentVisited.toString());
		System.out.println("actionSequence: " + actionSequence);
		try {
			//visualizes the points visited with red and whit x's
			vizualizePath(level,mostRecentVisited,actionSequence,start);
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
	private static HashSet<Point> fillGold(List<List<Integer>> level) {
		HashSet<Point> gold = new HashSet<>();
		int tile; 
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				tile = level.get(i).get(j);
				//System.out.println("The tile at " + j + "," + i + " = " +tile);
				if(tile == LODE_RUNNER_TILE_GOLD) { 
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

	/**
	 * Constructor that only takes a level, 
	 * this makes it so that it grabs the original spawn point and fills the gold set with
	 * the locations of the gold for that level 
	 * @param level A level in JSON form 
	 */
	public LodeRunnerState(List<List<Integer>> level) {
		this(level, getSpawnFromVGLC(level));
	}

	/**
	 * Constructor that takes a level and a start point. 
	 * This construct is can be used to specify a starting point for easier testing 
	 * @param level Level in JSON form 
	 * @param start The spawn point 
	 */
	public LodeRunnerState(List<List<Integer>> level, Point start) {
		this(level, getGoldLeft(level), getDugHoles(), start.x, start.y);
	}


	private static HashSet<Point> getDugHoles() {
		HashSet<Point> dugHoles = new HashSet<>();
		return dugHoles;
	}

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
	private LodeRunnerState(List<List<Integer>> level, HashSet<Point> goldLeft, HashSet<Point> dugHoles, int currentX, int currentY) {
		this.level = level;
		this.goldLeft = goldLeft;
		this.dugHoles = dugHoles;
		this.currentX = currentX;
		this.currentY = currentY;
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
	public static void vizualizePath(List<List<Integer>> level, HashSet<LodeRunnerState> mostRecentVisited, 
			ArrayList<LodeRunnerAction> actionSequence, LodeRunnerState start) throws IOException {
		List<List<Integer>> fullLevel = ListUtil.deepCopyListOfLists(level);
		fullLevel.get(start.currentY).set(start.currentX, LODE_RUNNER_TILE_SPAWN);// puts the spawn back into the visualization
		for(Point p : start.goldLeft) { //puts all the gold back 
			fullLevel.get(p.y).set(p.x, LODE_RUNNER_TILE_GOLD);
		}
		BufferedImage visualPath = LodeRunnerRenderUtil.createBufferedImage(fullLevel, LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X, 
				LodeRunnerRenderUtil.LODE_RUNNER_ROWS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
		if(mostRecentVisited != null) {
			Graphics2D g = (Graphics2D) visualPath.getGraphics();
			g.setColor(Color.WHITE);
			for(LodeRunnerState s : mostRecentVisited) {
				int x = s.currentX;
				int y = s.currentY;
				g.drawLine(x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y,(x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
				g.drawLine((x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
			}
			if(actionSequence != null) {
				g.setColor(Color.RED);
				LodeRunnerState current = start;
				for(LodeRunnerAction a : actionSequence) {
					int x = current.currentX;
					int y = current.currentY;
					g.drawLine(x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y,(x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
					g.drawLine((x+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,y*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, x*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X,(y+1)*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y);
					current = (LodeRunnerState) current.getSuccessor(a);
				}
			}
		}
		try {
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
	}

	/**
	 * Gets the next state from the current state
	 * @return The next state, if null the state was not legal at that time 
	 */
	@Override
	public State<LodeRunnerAction> getSuccessor(LodeRunnerAction a) {
		int newX = currentX;
		int newY = currentY; 
		HashSet<Point> newDugHoles = new HashSet<>();
		//assert inBounds(newX,newY): "x is:" + newX + "\ty is:"+newY + "\t" + inBounds(newX,newY);
		if(a.getMove().equals(LodeRunnerAction.MOVE.RIGHT)) {
			int beneath = tileAtPosition(newX,newY+1);
			if(passable(newX+1, newY) && tileAtPosition(newX, newY) == LODE_RUNNER_TILE_ROPE) {
				newX++;
			} else
				if(tileAtPosition(newX,newY) != LODE_RUNNER_TILE_LADDER &&// Could run on/across ladders too
				beneath != LODE_RUNNER_TILE_LADDER &&
				beneath != LODE_RUNNER_TILE_DIGGABLE &&
				beneath != LODE_RUNNER_TILE_GROUND)//checks if there is ground under the player
					return null;//fall down 
				else if(passable(newX+1, newY)) {
					//System.out.println("right");
					newX++;
				}  
				else return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.LEFT)) {
			int beneath = tileAtPosition(newX,newY+1);
			if(passable(newX-1, newY) && tileAtPosition(newX, newY) == LODE_RUNNER_TILE_ROPE) {
				newX--;
			} 
			else if(tileAtPosition(newX,newY) != LODE_RUNNER_TILE_LADDER &&// Could run on/across ladders too
					beneath != LODE_RUNNER_TILE_LADDER &&
					beneath != LODE_RUNNER_TILE_DIGGABLE &&
					beneath != LODE_RUNNER_TILE_GROUND)//checks if there is ground under the player
				return null;//fall down 
			else if(passable(newX-1,newY)) {
				//System.out.println("left");
				newX--;
			}  
			else return null; 
		}
		//		if(a.getMove().equals(LodeRunnerAction.MOVE.NOTHING)) {
		//			//if the tile at the new position is gold, then it removes it from the set
		//			//you can still collect gold while in free fall 
		//			//no new action, if the action is nothing you are most likely in a free fall and have to wait until you hit the ground to make a new action
		//			return null;  
		//		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.UP)) {
			if(tileAtPosition(newX, newY)==LODE_RUNNER_TILE_LADDER) {
				//System.out.println("up");
				newY--;
			} else return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.DOWN)) { 
			if(tileAtPosition(newX,newY+1) != LODE_RUNNER_TILE_DIGGABLE &&
					tileAtPosition(newX,newY+1) != LODE_RUNNER_TILE_GROUND) {
				//System.out.println("down");
				newY++;
			} else return null;
		}
		//have these two actions return null while testing on level one because it doesn't require digging to win 
		else if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_LEFT)) {
//			if(inBounds(newX-1,newY-1) && tileAtPosition(newX-1,newY+1) == LODE_RUNNER_TILE_DIGGABLE) {
//				for(Point p:dugHoles) {
//					newDugHoles.add(p);
//				}
//				newDugHoles.add(new Point(newX-1, newY-1));
//			}	
			return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_RIGHT)) {
//			if(inBounds(newX+1,newY+1) && tileAtPosition(newX+1,newY+1) == LODE_RUNNER_TILE_DIGGABLE) {
//				for(Point p:dugHoles) {
//					newDugHoles.add(p);
//				}
//				newDugHoles.add(new Point(newX+1, newY+1));
//			}
			return null;
		}
		//check if it is in teh set, then create a new set that contains all but that one
		HashSet<Point> newGoldLeft = new HashSet<>();
		for(Point p : goldLeft) {
			if(!p.equals(new Point(newX, newY))){
				newGoldLeft.add(p);
			}
		}
		//System.out.println(dugHoles.toString());
		//System.out.println(inBounds(newX,newY));
		//assert inBounds(newX,newY) : "x is:" + newX + "\ty is:"+newY + "\t"+ inBounds(newX,newY);
		return new LodeRunnerState(level, newGoldLeft, newDugHoles, newX, newY);
	}

	/**
	 * Gets a list of all of the lode runner actions
	 * @return List of valid lode runner actions
	 */
	@Override
	public ArrayList<LodeRunnerAction> getLegalActions(State<LodeRunnerAction> s) {
		ArrayList<LodeRunnerAction> vaildActions = new ArrayList<>();
		//System.out.println(level);
		// This code renders an image of the level with the agent in it
		//		try {
		//			LodeRunnerState theState = ((LodeRunnerState) s);
		//			List<List<Integer>> copy = ListUtil.deepCopyListOfLists(theState.level );
		//			copy.get(theState.currentY).set(theState.currentX, LODE_RUNNER_TILE_SPAWN); 
		//			for(Point t : theState.goldLeft) {
		//				copy.get(t.y).set(t.x, LODE_RUNNER_TILE_GOLD); 
		//			}
		//			LodeRunnerRenderUtil.getBufferedImage(copy);
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		MiscUtil.waitForReadStringAndEnterKeyPress();
		for(LodeRunnerAction.MOVE move: LodeRunnerAction.MOVE.values()) {
			//Everything besides the if statement is for debugging purposes, delete later 
			LodeRunnerAction a = new LodeRunnerAction(move);
			System.out.println(s+"\t"+move+"\t"+s.getSuccessor(a));
			if(s.getSuccessor(new LodeRunnerAction(move)) != null) {
				vaildActions.add(new LodeRunnerAction(move));
			}
		}
		//System.out.println(vaildActions);

		return vaildActions;
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
		return 1;
	}

	/**
	 * Returns a string repesentation of a Lode Runner State 
	 */
	@Override
	public String toString() {
		return "Size: " + goldLeft.size() + " (" + currentX + ", " + currentY + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentX;
		result = prime * result + currentY;
		result = prime * result + ((goldLeft == null) ? 0 : goldLeft.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof LodeRunnerState))
			return false;
		LodeRunnerState other = (LodeRunnerState) obj;
		if (currentX != other.currentX)
			return false;
		if (currentY != other.currentY)
			return false;
		if (goldLeft == null) {
			if (other.goldLeft != null)
				return false;
		} else if (!goldLeft.equals(other.goldLeft))
			return false;
		return true;
	}



}
