package edu.southwestern.tasks.megaman.astar;

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
import edu.southwestern.tasks.megaman.MegaManRenderUtil;
import edu.southwestern.tasks.megaman.MegaManVGLCUtil;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import edu.southwestern.util.search.State;
/**
 * This class defines an A* agent to traverse MegaMan levels
 * @author Benjamin Capps
 *
 */
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
	public static final int MEGA_MAN_TILE_WATER = 10;
	public static final int FOOTHOLDER_ENEMY = 27;

	
	private List<List<Integer>> level;
	private Point orb; 
	//private HashSet<Point> dugHoles; // Too expensive to track the dug up spaces in the state. Just allow the agent to move downward through diggable blocks
	public int currentX; 
	public int currentY;
	private int jumpVelocity;
	private int fallHorizontalModInt;
	//private boolean climbing;
	//the distance to the level orb
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
		public enum MOVE {RIGHT,LEFT,UP,DOWN, JUMP};
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
					//System.out.println(orb);
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
		this(level, getJumpVelocity(), getOrb(level), start.x, start.y, getFallHorizontalModInt());
	}
	private static int getFallHorizontalModInt() {
		// TODO Auto-generated method stub
		return 0;
	}
	private static int getJumpVelocity() {
		return 0;
	}
	public MegaManState(List<List<Integer>> level, int jumpVelocity, Point orb, int currentX, int currentY, int fallHorizontalModInt) {
		this.level = level;
		this.orb = orb;
		this.jumpVelocity =jumpVelocity;
		this.currentX = currentX;
		this.currentY = currentY;
		this.fallHorizontalModInt = fallHorizontalModInt;
	}

	@Override
	public State<MegaManAction> getSuccessor(MegaManAction a) {
		int newJumpVelocity = jumpVelocity;
		int newX = currentX;
		int newY = currentY;
		int newFallHorizontalModInt = fallHorizontalModInt;
		boolean falling = false;
		boolean jumping = false;
		assert inBounds(newX,newY): "x is:" + newX + "\ty is:"+newY + "\t" + inBounds(newX,newY);
		// Falling off bottom of screen (into a gap). No successor (death)
		//System.out.print("("+newX+", "+newY+")");

		if(!inBounds(currentX,currentY+1)) return null;
		
		// Affects of jumping based on previous velocity setting happen before JUMP action processed.
		// Executing in this order is important to allow MegaMan to jump directly to a diagonal without
		// bumping his head on a ceiling above him first.
		if(newJumpVelocity > 0) { // Jumping up
			if(passable(newX,newY-1)||(inBounds(newX,newY-1)&&tileAtPosition(newX, newY-1)==MEGA_MAN_TILE_MOVING_PLATFORM)) {
				jumping=true;
				newY--; // Jump up
				newJumpVelocity--; // decelerate
			} else {
				newJumpVelocity = 0; // Can't jump if blocked above
			}
		}
		
		// Potentially deal with JUMP action
		if(newJumpVelocity == 0) { // Not mid-Jump
			jumping = false;
			//int beneath = tileAtPosition(newX,newY+1);
			if(passable(newX,newY+1)&& tileAtPosition(newX, newY+1)!=MEGA_MAN_TILE_LADDER) { // Falling
				newY++; // Fall down
				newFallHorizontalModInt++;
				newFallHorizontalModInt=newFallHorizontalModInt%2;
				falling = true;
			} else if(a.getMove().equals(MegaManAction.MOVE.JUMP)&& tileAtPosition(newX, newY)!=MEGA_MAN_TILE_LADDER) { // Start jump
				jumping = true;
				newJumpVelocity = Parameters.parameters.integerParameter("megaManAStarJumpHeight"); // Accelerate up
			}
		} else if(a.getMove().equals(MegaManAction.MOVE.JUMP)) {
			return null; // Can't jump mid-jump. Reduces search space.
		}

		// Right movement
		if(a.getMove().equals(MegaManAction.MOVE.RIGHT)) {
			if(!jumping&&((falling&&passable(newX+1,newY)&&passable(newX+1, newY-1)&&newFallHorizontalModInt%2==0)||(!falling&&passable(newX+1,newY)))||(jumping&&passable(newX+1, newY)&&passable(newX+1, newY-1))) newX++;
			else if(currentY == newY) { // vertical position did not change
				// This action does not change the state. Neither jumping up nor falling down, and could not move right, so there is no NEW state to go to
				return null;
			}
		}

		// Left movement
		if(a.getMove().equals(MegaManAction.MOVE.LEFT)) {
			if(!jumping&&(falling&&passable(newX-1,newY)&&passable(newX-1, newY-1)&&newFallHorizontalModInt%2==0)||(!falling&&passable(newX-1, newY))||(jumping&&passable(newX-1, newY)&&passable(newX-1, newY-1))) {
				newX--;
			}
			else if(currentY == newY) { // vertical position did not change
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
			if(inBounds(newX, newY+1) && tileAtPosition(newX,newY+1) != MEGA_MAN_TILE_GROUND&&tileAtPosition(newX, newY+1)==MEGA_MAN_TILE_LADDER) 
					newY++;
			else return null;
		}
		
		if(!inBounds(newX, newY)){
			return null;
		}
		MegaManState result = new MegaManState(level, newJumpVelocity, orb, newX, newY, newFallHorizontalModInt);
		//renderLevelAndPause((MegaManState) result);
		//System.out.println(newX+", "+newY);
		return result;
	}

	private boolean passable(int x, int y) {
		if(!inBounds(x,y)) return false; // fail for bad bounds before tileAtPosition check
		int tile = tileAtPosition(x,y);
//		int beneath;
//		if(inBounds(x,y+1)) beneath = tileAtPosition(x,y+1);
//		else beneath = tile;
		if((	tile==MEGA_MAN_TILE_EMPTY ||tile==MEGA_MAN_TILE_LADDER||tile==MEGA_MAN_TILE_ORB||tile==MEGA_MAN_TILE_BREAKABLE||tile==MEGA_MAN_TILE_WATER||tile>10&&tile!=FOOTHOLDER_ENEMY)) {
			return true;
		}
		return false; 
	}
	/**
	 * checks if a point in the level is in bounds
	 * @param x x coordinate of the tile
	 * @param y y coordinate of the tile
	 * @return true if in bounds, false otherwise
	 */
	private boolean inBounds(int x, int y) {
		// TODO Auto-generated method stub
		return x>=0&&y>=0&&y<level.size()&&x<level.get(y).size()&&level.get(y).get(x)!=9&&noHazardBeneath(x, y);
	}
	/**
	 * checks to make sure that there are no deadly hazards beneath
	 * 
	 * @param x x coordinate of the tile
	 * @param y y coordinate of the tile
	 * @return
	 */
	private boolean noHazardBeneath(int x, int y) {
		if(!inBounds(x, y+1)) {
			return true;
		}else if(tileAtPosition(x,y+1)!=MEGA_MAN_TILE_HAZARD) {
			return true;
		}else {
			return false;
		}
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
	/**
	 * gets the set of legal actions for the megaman state
	 */
	public ArrayList<MegaManAction> getLegalActions(State<MegaManAction> s) {
		ArrayList<MegaManAction> vaildActions = new ArrayList<>();
		for(MegaManAction.MOVE move: MegaManAction.MOVE.values()) {
			if(s.getSuccessor(new MegaManAction(move)) != null) {
				vaildActions.add(new MegaManAction(move));
			}
//			MegaManAction a = new MegaManAction(move);
//			System.out.println(s+"\t"+move+"\t"+s.getSuccessor(a));
		}
		return vaildActions;
		
	}

	@Override
	/**
	 * checks if the current position is the level orb
	 */
	public boolean isGoal() {
		// TODO Auto-generated method stub
		return currentX==orb.x&&currentY==orb.y;
	}
	@Override
	/**
	 * returns the hashcode based on current position and jump velocity
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + jumpVelocity;
		result = prime * result + currentX;
		result = prime * result + currentX;
		result = prime * result + fallHorizontalModInt;
		return result;
	}
	@Override
	/**
	 * returns 1, the cost of traversing one space
	 */
	public double stepCost(State<MegaManAction> s, MegaManAction a) {
		return 1;
	}
	
	
	@Override
	/**
	 * checks if a state is equal to another
	 */
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
		if(fallHorizontalModInt != other.fallHorizontalModInt)
			return false;
		if (orb == null) {
			if (other.orb != null)
				return false;
		} else if (!orb.equals(other.orb))
			return false;
		return true;
	}
	
	@Override
	/**
	 * returns the (x,y) position
	 */
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
	public static BufferedImage vizualizePath(List<List<Integer>> level, HashSet<MegaManState> mostRecentVisited, 
			ArrayList<MegaManAction> actionSequence, MegaManState start) throws IOException {
		List<List<Integer>> fullLevel = ListUtil.deepCopyListOfLists(level);
		fullLevel.get(start.currentY).set(start.currentX, MEGA_MAN_TILE_SPAWN);// puts the spawn back into the visualization
		//for(Point p : start.orb) { //puts all the gold back 
			fullLevel.get(getOrb(level).y).set(getOrb(level).x, MEGA_MAN_TILE_ORB);
		//!!
		BufferedImage visualPath = MegaManRenderUtil.createBufferedImage(fullLevel, level.get(0).size()*MegaManRenderUtil.MEGA_MAN_TILE_X, 
				level.size()*MegaManRenderUtil.MEGA_MAN_TILE_Y);
		if(mostRecentVisited != null) {
			Graphics2D g = (Graphics2D) visualPath.getGraphics();
			g.setColor(Color.WHITE);
			for(MegaManState s : mostRecentVisited) {
				int x = s.currentX;
				int y = s.currentY;
				g.setStroke(new BasicStroke(3));
				g.drawLine(x*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y,(x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
				g.drawLine((x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y, x*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
			}
			if(actionSequence != null) {
				g.setColor(Color.RED);
				MegaManState current = start;
				for(MegaManAction a : actionSequence) {
					int x = current.currentX;
					int y = current.currentY;
					g.setStroke(new BasicStroke(3));
					g.drawLine(x*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y,(x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
					g.drawLine((x+1)*MegaManRenderUtil.MEGA_MAN_TILE_X,y*MegaManRenderUtil.MEGA_MAN_TILE_Y, x*MegaManRenderUtil.MEGA_MAN_TILE_X,(y+1)*MegaManRenderUtil.MEGA_MAN_TILE_Y);
					current = (MegaManState) current.getSuccessor(a);
				}
			}
		}
//		try {
//			JFrame frame = new JFrame();
//			JPanel panel = new JPanel();
//			JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(level.get(0).size()*MegaManRenderUtil.MEGA_MAN_TILE_X, 
//					level.size()*MegaManRenderUtil.MEGA_MAN_TILE_Y, Image.SCALE_FAST)));
//			panel.add(label);
//			frame.add(panel);
//			frame.pack();
//			frame.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return visualPath;
	}
	/**
	 * useful for testing
	 * @param args
	 */
	public static void main(String args[]) {
		//converts Level in VGLC to hold all 8 tiles so we can get the real spawn point from the level 
		List<List<Integer>> level = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_LEVEL_PATH+"megaman_1_"+1+".txt"); //converts to JSON
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false"
				, "megaManAStarJumpHeight:4" });
		MegaManVGLCUtil.printLevel(level);
		MegaManState start = new MegaManState(level);
		Search<MegaManAction,MegaManState> search = new AStarSearch<>(MegaManState.manhattanToOrb);
		HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			actionSequence = ((AStarSearch<MegaManAction, MegaManState>) search).search(start, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
		mostRecentVisited = ((AStarSearch<MegaManAction, MegaManState>) search).getVisited();
		System.out.println(mostRecentVisited.toString());
		System.out.println("actionSequence: " + actionSequence);
		BufferedImage visualPath = null;
		//BufferedImage m;
		
		try {
			//visualizes the points visited with red and whit x's
			visualPath=vizualizePath(level,mostRecentVisited,actionSequence,start);
			
			//BufferedImage[] images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH);
			//m=MegaManRenderUtil.getBufferedImageWithRelativeRendering(level, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		int screenx;
		int screeny;
		if(level.get(0).size()>level.size()) {
			screenx = 1800;
			screeny = 950*level.size()/level.get(0).size();
		}else {
			screeny = 950;
			screenx = 1800*level.get(0).size()/level.size();
		}
		JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(screenx,screeny, Image.SCALE_FAST)));
		panel.add(label);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);

	}
	
	@SuppressWarnings("unused")
	/**
	 * used for troubleshooting
	 * @param theState
	 */
	private void renderLevelAndPause(MegaManState theState) {
		// This code renders an image of the level with the agent in it
		try {
			List<List<Integer>> copy = ListUtil.deepCopyListOfLists(theState.level );
			copy.get(theState.currentY).set(theState.currentX, MEGA_MAN_TILE_SPAWN); 
			copy.get(orb.y).set(orb.x, MEGA_MAN_TILE_ORB); 

			MegaManRenderUtil.getBufferedImage(copy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
	}
}
