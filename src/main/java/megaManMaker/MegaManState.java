package megaManMaker;

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

import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import edu.southwestern.util.search.State;

public class MegaManState extends State<MegaManState.MegaManAction>{
	public static final int MEGA_MAN_TILE_EMPTY = 0;
	public static final int MEGA_MAN_TILE_GROUND = 1;
	public static final int MEGA_MAN_TILE_LADDER = 2;
	public static final int MEGA_MAN_TILE_HAZARD = 3;
	public static final int MEGA_MAN_TILE_BREAKABLE = 4;
	public static final int MEGA_MAN_TILE_MOVING_PLATFORM = 5;
	public static final int MEGA_MAN_TILE_CANNON = 6;
	public static final int MEGA_MAN_TILE_ORB = 7;
	public static final int MEGA_MAN_TILE_NULL = 9;
	public static final int MEGA_MAN_TILE_SPAWN = 8;
	
	
	private List<List<Integer>> level;
	private Point orb; 
	//private HashSet<Point> dugHoles; // Too expensive to track the dug up spaces in the state. Just allow the agent to move downward through diggable blocks
	public int currentX; 
	public int currentY;
	private int jumpVelocity;

	public static Heuristic<MegaManAction,MegaManState> manhattanToOrb = new Heuristic<MegaManAction,MegaManState>(){

		@Override
		public double h(MegaManState s) {
			int maxDistance = 0;
			Point orb = s.orb;
			int xDistance = Math.abs(s.currentX - orb.x);
			int yDistance = Math.abs(s.currentY - orb.y);
			Math.max(maxDistance, (xDistance+yDistance));
			return maxDistance;
		}
	
	};

	
	
	public  static class MegaManAction implements Action{
		public enum MOVE {RIGHT,LEFT,UP,DOWN, JUMP}; //, DIG_RIGHT, DIG_LEFT} // Too computationally expensive to model digging as actions that change the state
		private MOVE movement;
		/**
		 * Constructor for a Mega Man action
		 * @param m A move the player can make 
		 */
		public MegaManAction(MOVE m) {
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
			if(other instanceof MegaManAction) {
				return ((MegaManAction) other).movement.equals(this.movement); 
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
	 * Fills a set with points, to keep a reference of where the gold is in the level
	 * then removes them and makes them emtpy spaces 
	 * @param level A level 
	 * @return Set of points 
	 */
	private static Point getOrb(List<List<Integer>> level) {
		Point orb = new Point();
		int tile; 
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				tile = level.get(i).get(j);
				if(tile == MEGA_MAN_TILE_ORB) { 
					orb.setLocation(j,i);
					break;
				}

			}
		}
		return orb;
	}
	/**
	 * Constructor that only takes a level, 
	 * this makes it so that it grabs the original spawn point sets
	 * the location of the orb for that level 
	 * @param level A level in JSON form 
	 */
	public MegaManState(List<List<Integer>> level) {
		this(level, getSpawnFromVGLC(level));
	}
	/**
	 * Constructor that takes a level and a start point. 
	 * This construct is can be used to specify a starting point for easier testing 
	 * @param level Level in JSON form 
	 * @param start The spawn point 
	 */
	public MegaManState(List<List<Integer>> level, Point start) {
		this(level, getJumpVelocity(), getOrb(level), start.x, start.y);
	}
	private static int getJumpVelocity() {
		return 0;
	}
	public MegaManState(List<List<Integer>> level, int jumpVelocity, Point orb, int currentX, int currentY) {
		this.level = level;
		this.orb = orb;
		this.jumpVelocity =jumpVelocity;
		this.currentX = currentX;
		this.currentY = currentY;
	}

	@Override
	public State<MegaManAction> getSuccessor(MegaManAction a) {
		int newJumpVelocity = jumpVelocity;
		int newX = currentX;
		int newY = currentY;
		assert inBounds(newX,newY): "x is:" + newX + "\ty is:"+newY + "\t" + inBounds(newX,newY);
		// Falling off bottom of screen (into a gap). No successor (death)
		//System.out.print("("+newX+", "+newY+")");

		if(!inBounds(currentX,currentY+1)) return null;
		
		if(newJumpVelocity == 0) { // Not mid-Jump
			//int beneath = tileAtPosition(newX,newY+1);
			if(passable(newX,newY+1)) { // Falling
				newY++; // Fall down
			} else if(a.getMove().equals(MegaManAction.MOVE.JUMP)) { // Start jump
				newJumpVelocity = 4; // Accelerate up
			} 
		} else if(a.getMove().equals(MegaManAction.MOVE.JUMP)) {
			return null; // Can't jump mid-jump. Reduces search space.
		}

		if(newJumpVelocity > 0) { // Jumping up
			if(passable(newX,newY-1)||tileAtPosition(newX, newY+1)==MEGA_MAN_TILE_MOVING_PLATFORM) {
				newY--; // Jump up
				newJumpVelocity--; // decelerate
			} else {
				newJumpVelocity = 0; // Can't jump if blocked above
			}
			// TODO: Add breakable case
		}

		// Right movement
		if(a.getMove().equals(MegaManAction.MOVE.RIGHT)) {
			if(passable(newX+1,newY)) {
				newX++;
			} else if(currentY == newY) { // vertical position did not change
				// This action does not change the state. Neither jumping up nor falling down, and could not move right, so there is no NEW state to go to
				return null;
			}
		}

		// Left movement
		if(a.getMove().equals(MegaManAction.MOVE.LEFT)) {
			if(passable(newX-1,newY)) {
				newX--;
			} else if(currentY == newY) { // vertical position did not change
				// This action does not change the state. Neither jumping up nor falling down, and could not move left, so there is no NEW state to go to
				return null;
			}
		}
		//up movement (on ladder)
		if(a.getMove().equals(MegaManAction.MOVE.UP)) {
			if(inBounds(newX, newY-1) && tileAtPosition(newX, newY)==MEGA_MAN_TILE_LADDER) 
				newY--;
			else return null; 
		}
		//down movement(on ladder)
		if(a.getMove().equals(MegaManAction.MOVE.DOWN)) {
			if(inBounds(newX, newY+1) && tileAtPosition(newX,newY+1) != MEGA_MAN_TILE_GROUND) 
					newY++;
			else return null;
		}
		
		if(!inBounds(newX, newY)){
			return null;
		}
		MegaManState result = new MegaManState(level, newJumpVelocity, orb, newX, newY);
		//renderLevelAndPause((MegaManState) result);

		return result;
	}

	private boolean passable(int x, int y) {
		if(!inBounds(x,y)) return false; // fail for bad bounds before tileAtPosition check
		int tile = tileAtPosition(x,y);
		if((	tile==MEGA_MAN_TILE_EMPTY )) {
			return true;
		}
		return false; 
	}

	private boolean inBounds(int currentX2, int i) {
		// TODO Auto-generated method stub
		return currentX2>=0&&i>=0&&i<level.size()&&currentX2<level.get(i).size()&&level.get(i).get(currentX2)!=9;
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
				if(tile == MEGA_MAN_TILE_SPAWN) {//7 maps to spawn point  
					start = new Point(j, i);
					level.get(i).set(j, MEGA_MAN_TILE_EMPTY);//removes spawn point and places an empty tile 
					done = true;
				}
			}
		}
		System.out.println(start.toString());
		return start;
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
	@Override
	public ArrayList<MegaManAction> getLegalActions(State<MegaManAction> s) {
		ArrayList<MegaManAction> vaildActions = new ArrayList<>();
		for(MegaManAction.MOVE move: MegaManAction.MOVE.values()) {
			if(s.getSuccessor(new MegaManAction(move)) != null) {
				vaildActions.add(new MegaManAction(move));
			}
			MegaManAction a = new MegaManAction(move);
			System.out.println(s+"\t"+move+"\t"+s.getSuccessor(a));
		}
		return vaildActions;
		
	}

	@Override
	public boolean isGoal() {
		// TODO Auto-generated method stub
		return currentX==orb.x&&currentY==orb.y;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + jumpVelocity;
		result = prime * result + currentX;
		result = prime * result + currentX;
		return result;
	}
	@Override
	public double stepCost(State<MegaManAction> s, MegaManAction a) {
		return 1;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MegaManState other = (MegaManState) obj;
		if (currentX != other.currentX)
			return false;
		if (currentY != other.currentY)
			return false;
		if (jumpVelocity != other.jumpVelocity)
			return false;
		if (orb == null) {
			if (other.orb != null)
				return false;
		} else if (!orb.equals(other.orb))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return "("+currentX + "," + currentY +")";		

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
	public static void vizualizePath(List<List<Integer>> level, HashSet<MegaManState> mostRecentVisited, 
			ArrayList<MegaManAction> actionSequence, MegaManState start) throws IOException {
		List<List<Integer>> fullLevel = ListUtil.deepCopyListOfLists(level);
		fullLevel.get(start.currentY).set(start.currentX, MEGA_MAN_TILE_SPAWN);// puts the spawn back into the visualization
		//for(Point p : start.orb) { //puts all the gold back 
			fullLevel.get(getOrb(level).y).set(getOrb(level).x, MEGA_MAN_TILE_ORB);
		//!!
		BufferedImage visualPath = MegaManRenderUtil.createBufferedImage(fullLevel, MegaManRenderUtil.MEGA_MAN_COLUMNS*MegaManRenderUtil.MEGA_MAN_TILE_X, 
				MegaManRenderUtil.MEGA_MAN_ROWS*MegaManRenderUtil.MEGA_MAN_TILE_Y);
		if(mostRecentVisited != null) {
			Graphics2D g = (Graphics2D) visualPath.getGraphics();
			g.setColor(Color.WHITE);
			for(MegaManState s : mostRecentVisited) {
				int x = s.currentX;
				int y = s.currentY;
				g.drawLine(x*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y,(x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
				g.drawLine((x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y, x*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
			}
			if(actionSequence != null) {
				g.setColor(Color.BLUE);
				MegaManState current = start;
				for(MegaManAction a : actionSequence) {
					int x = current.currentX;
					int y = current.currentY;
					g.drawLine(x*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y,(x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
					g.drawLine((x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y, x*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
					current = (MegaManState) current.getSuccessor(a);
				}
			}
		}
		try {
			JFrame frame = new JFrame();
			JPanel panel = new JPanel();
			JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(MegaManRenderUtil.MEGA_MAN_COLUMNS*MegaManRenderUtil.MEGA_MAN_TILE_X, 
					MegaManRenderUtil.MEGA_MAN_ROWS*MegaManRenderUtil.MEGA_MAN_TILE_Y, Image.SCALE_FAST)));
			panel.add(label);
			frame.add(panel);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) {
		//converts Level in VGLC to hold all 8 tiles so we can get the real spawn point from the level 
		List<List<Integer>> level = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_LEVEL_PATH+"megaman_1_"+1+".txt"); //converts to JSON
		MegaManState start = new MegaManState(level);
		Search<MegaManAction,MegaManState> search = new AStarSearch<>(MegaManState.manhattanToOrb);
		HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			actionSequence = ((AStarSearch<MegaManAction, MegaManState>) search).search(start, true, 1000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
		mostRecentVisited = ((AStarSearch<MegaManAction, MegaManState>) search).getVisited();
		System.out.println(mostRecentVisited.toString());
		System.out.println("actionSequence: " + actionSequence);
		try {
			//visualizes the points visited with red and whit x's
			vizualizePath(level,mostRecentVisited,actionSequence,start);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void renderLevelAndPause(MegaManState theState) {
		// This code renders an image of the level with the agent in it
		try {
			List<List<Integer>> copy = ListUtil.deepCopyListOfLists(theState.level );
			copy.get(theState.currentY).set(theState.currentX, MEGA_MAN_TILE_SPAWN); 
			//for(Point t : theState.goldLeft) {
			copy.get(orb.y).set(orb.x, MEGA_MAN_TILE_ORB); 
			//}
//			for(Point dug : theState.dugHoles) {
//				copy.get(dug.y).set(dug.x, LODE_RUNNER_TILE_EMPTY); 
//			}
			MegaManRenderUtil.getBufferedImage(copy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
	}
}
