package edu.southwestern.tasks.mspacman.facades;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import pacman.game.Game;

import java.awt.Color;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

/**
 *Contains pac man game. Includes harnesses for both
 *generations of game. Game mechanics changed drastically
 *during course of coding for ms pacman so the facade class
 *was necessary to ensure compatibility between both generations
 * @author Jacob Schrum
 */
public class GameFacade {
	
	//public static variables
	public static final int MAX_DISTANCE = 200;
	public static final int NUM_DIRS = 4;
	public static final int DANGEROUS_TIME = 5;
	public oldpacman.game.Game oldG = null;
	public pacman.game.Game poG = null; // New pacman from Maven

	/**
	 * Has a popacman version.
	 * returns what move to make based on the 
	 * @param index
	 * @return enum corresponding to move made
	 */
	public static oldpacman.game.Constants.MOVE indexToMove(int index) {
		switch (index) {
		case 0:
			return oldpacman.game.Constants.MOVE.UP;
		case 1:
			return oldpacman.game.Constants.MOVE.RIGHT;
		case 2:
			return oldpacman.game.Constants.MOVE.DOWN;
		case 3:
			return oldpacman.game.Constants.MOVE.LEFT;
		default:
			return null;
		}
	}
	
	/**
	 * Used for popacman
	 * returns what move to make based on the 
	 * @param index
	 * @return enum corresponding to move made
	 */
	public static pacman.game.Constants.MOVE indexToMovePO(int index) {
		switch (index) {
		case 0:
			return pacman.game.Constants.MOVE.UP;
		case 1:
			return pacman.game.Constants.MOVE.RIGHT;
		case 2:
			return pacman.game.Constants.MOVE.DOWN;
		case 3:
			return pacman.game.Constants.MOVE.LEFT;
		default:
			return null;
		}
	}


	/**
	 * Given direction to move, returns index that
	 * cor responds to said move
	 * @param m enum move made
	 * @return index of move
	 */
	public static int moveToIndex(oldpacman.game.Constants.MOVE m) {
		switch (m) {
		case UP:
			return 0;
		case RIGHT:
			return 1;
		case DOWN:
			return 2;
		case LEFT:
			return 3;
		default:
			return -1;
		}
	}
	
	/**
	 * Used for popacman
	 * Given direction to move, returns index that
	 * cor responds to said move
	 * @param m enum move made
	 * @return index of move
	 */
	public static int moveToIndex(pacman.game.Constants.MOVE m) {
		switch (m) {
		case UP:
			return 0;
		case RIGHT:
			return 1;
		case DOWN:
			return 2;
		case LEFT:
			return 3;
		default:
			return -1;
		}
	}

	/**
	 * Returns enum corresponding to index given
	 * @param index index of given ghost
	 * @return ghost that corresponds to given index
	 */
	public static oldpacman.game.Constants.GHOST indexToGhost(int index) {
		assert index >= 0 && index <= 3 : "Must be a valid ghost index: " + index;
		switch (index) {
		case 0:
			return oldpacman.game.Constants.GHOST.BLINKY;
		case 1:
			return oldpacman.game.Constants.GHOST.PINKY;
		case 2:
			return oldpacman.game.Constants.GHOST.INKY;
		case 3:
			return oldpacman.game.Constants.GHOST.SUE;
		default:
			System.out.println("Index " + index + " is a NULL ghost!");
			return null;
		}
	}
	
	/**
	 * Used for popacman
	 * Returns enum corresponding to index given
	 * @param index index of given ghost
	 * @return ghost that corresponds to given index
	 */
	public static pacman.game.Constants.GHOST indexToGhostPO(int index) {
		assert index >= 0 && index <= 3 : "Must be a valid ghost index: " + index;
		switch (index) {
		case 0:
			return pacman.game.Constants.GHOST.BLINKY;
		case 1:
			return pacman.game.Constants.GHOST.PINKY;
		case 2:
			return pacman.game.Constants.GHOST.INKY;
		case 3:
			return pacman.game.Constants.GHOST.SUE;
		default:
			System.out.println("Index " + index + " is a NULL ghost!");
			return null;
		}
	}

	/**
	 * returns index corresponding to given ghost
	 * @param ghost ghost enum
	 * @return index of ghost
	 */
	public static int ghostToIndex(oldpacman.game.Constants.GHOST ghost) {
		switch (ghost) {
		case BLINKY:
			return 0;
		case PINKY:
			return 1;
		case INKY:
			return 2;
		case SUE:
			return 3;
		default:
			return -1;
		}
	}
	
	/**
	 * Used for popacman
	 * returns index corresponding to given ghost
	 * @param ghost ghost enum
	 * @return index of ghost
	 */
	public static int ghostToIndexPO(pacman.game.Constants.GHOST ghost) {
		switch (ghost) {
		case BLINKY:
			return 0;
		case PINKY:
			return 1;
		case INKY:
			return 2;
		case SUE:
			return 3;
		default:
			return -1;
		}
	}

	/**
	 * Return indices for certain types of ghosts.
	 * Supports popacman (TODO: test)
	 * 
	 * @param edibleVsThreatOnly
	 *            true for edible only, false for threat only, unless "all" is
	 *            true.
	 * @param all
	 *            get all the ghosts, except the ones in the lair
	 * @return list of ghost indices
	 */
	public ArrayList<Integer> getGhostIndices(boolean edibleVsThreatOnly, boolean all) {
		ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (!ghostInLair(i) && (all || (edibleVsThreatOnly && isGhostEdible(i)) || (!edibleVsThreatOnly && isGhostThreat(i)))) {
				ghosts.add(i);
			}
		}
		return ghosts;
	}

	/**
	 * return the node index of the node that is the neighbor of current in the
	 * direction of move
	 *
	 * @param g
	 *            pacman game
	 * @param current
	 *            current node index
	 * @param move
	 *            direction to move from current
	 * @return neighboring node index
	 */
	public static int getNeighbourInDirection(oldpacman.game.Game g, int current, oldpacman.game.Constants.MOVE move) {
		return neighbors(g, current)[moveToIndex(move)];
	}
	
	/**
	 * Used for popacman.
	 * return the node index of the node that is the neighbor of current in the
	 * direction of move
	 *
	 * @param g
	 *            pacman game
	 * @param current
	 *            current node index
	 * @param move
	 *            direction to move from current
	 * @return neighboring node index
	 */
	public static int getNeighbourInDirection(pacman.game.Game g, int current, oldpacman.game.Constants.MOVE move) {
		return neighbors(g, current)[moveToIndex(move)];
	}

	/**
	 * Given pacman game and node index, an array of size 4 is returned
	 * containing the neighboring node indices. Each slot in the array
	 * corresponds to a specific direction, and if there is no neighbor in the
	 * given direction, then slot is filled with a -1
	 *
	 * @param gs
	 *            Pacman game
	 * @param currentNodeIndex
	 *            node to get neighbors of
	 * @return contents of four neighboring positions
	 */
	public static int[] neighbors(oldpacman.game.Game gs, int currentNodeIndex) {
		assert currentNodeIndex != -1 : "-1 is not a valid node index";
		oldpacman.game.Constants.MOVE[] possible = gs.getPossibleMoves(currentNodeIndex);
		int[] neighbors = gs.getNeighbouringNodes(currentNodeIndex);
		int[] result = new int[NUM_DIRS];
		Arrays.fill(result, -1);
		int cx = gs.getNodeXCoord(currentNodeIndex);
		int cy = gs.getNodeYCoord(currentNodeIndex);
		for (int i = 0; i < possible.length; i++) {
			int nx = gs.getNodeXCoord(neighbors[i]);
			int ny = gs.getNodeYCoord(neighbors[i]);
			assert!((possible[i].equals(oldpacman.game.Constants.MOVE.UP) && (cx != nx || ny >= cy))
					|| (possible[i].equals(oldpacman.game.Constants.MOVE.DOWN) && (cx != nx || ny <= cy))
					|| (possible[i].equals(oldpacman.game.Constants.MOVE.LEFT) && (cy != ny || (nx >= cx && nx != 108 & cx != 0)))
					|| (possible[i].equals(oldpacman.game.Constants.MOVE.RIGHT) && (cy != ny
							|| (nx <= cx && nx != 0 & cx != 108)))) : "Error in neighbor calculation: move:"
									+ possible[i] + ":current:" + cx + "," + cy + ":neighbor:" + nx + "," + ny;
			result[moveToIndex(possible[i])] = neighbors[i];
		}
		return result;
	}
	
	/**
	 * Used for popacman. 
	 * Given pacman game and node index, an array of size 4 is returned
	 * containing the neighboring node indices. Each slot in the array
	 * corresponds to a specific direction, and if there is no neighbor in the
	 * given direction, then slot is filled with a -1
	 *
	 * @param gs
	 *            Pacman game
	 * @param currentNodeIndex
	 *            node to get neighbors of
	 * @return contents of four neighboring positions
	 */
	public static int[] neighbors(pacman.game.Game gs, int currentNodeIndex) {
		assert currentNodeIndex != -1 : "-1 is not a valid node index";
		pacman.game.Constants.MOVE[] possible = gs.getPossibleMoves(currentNodeIndex);
		int[] neighbors = gs.getNeighbouringNodes(currentNodeIndex);
		int[] result = new int[NUM_DIRS];
		Arrays.fill(result, -1);
		int cx = gs.getNodeXCood(currentNodeIndex);
		int cy = gs.getNodeYCood(currentNodeIndex);
		for (int i = 0; i < possible.length; i++) {
			int nx = gs.getNodeXCood(neighbors[i]);
			int ny = gs.getNodeYCood(neighbors[i]);
			assert!((possible[i].equals(pacman.game.Constants.MOVE.UP) && (cx != nx || ny >= cy))
					|| (possible[i].equals(pacman.game.Constants.MOVE.DOWN) && (cx != nx || ny <= cy))
					|| (possible[i].equals(pacman.game.Constants.MOVE.LEFT) && (cy != ny || (nx >= cx && nx != 108 & cx != 0)))
					|| (possible[i].equals(pacman.game.Constants.MOVE.RIGHT) && (cy != ny
							|| (nx <= cx && nx != 0 & cx != 108)))) : "Error in neighbor calculation: move:"
									+ possible[i] + ":current:" + cx + "," + cy + ":neighbor:" + nx + "," + ny;
			result[moveToIndex(possible[i])] = neighbors[i];
		}
		return result;
	}

	/**
	 * Sets game facade based on given game
	 * @param g game
	 */
	public GameFacade(oldpacman.game.Game g) {
		oldG = g;
	}

	/**
	 * For Partially Observable Pac-Man
	 * 
	 * Sets game facade based on given game
	 * @param g game
	 */
	public GameFacade(pacman.game.Game g) {
		poG = g;
	}

	/**
	 * gets times taken to eat each pill.
	 * TODO
	 * @return list of times
	 */
	public List<Integer> getPillEatTimes() {
		if(oldG == null) throw new UnsupportedOperationException("TODO: implement Get getPillEatTimes");
		
		return oldG.getPillEatTimes();
	}

	/**
	 * How long the ghosts will be edible for the next time a power pill is
	 * eaten.
	 *
	 * @return time
	 */
	public int getNextEdibleTime() {
		if(oldG == null) throw new UnsupportedOperationException("TODO: implement Get getNextEdibleTime");
		
		return oldG.newEdibleTime();
	}

	/**
	 * Gets how many times given ghost was eaten
	 * @param ghostIndex index of ghost
	 * @return how many times ghost was eaten
	 */
	public int getSpecificGhostEatenCount(int ghostIndex) {
		if(oldG == null) throw new UnsupportedOperationException("TODO: implement Get SpecificGhostEatenCount");
		
		return oldG.getEatenGhosts(ghostIndex);
	}

	/**
	 * Return number of eaten ghosts across all levels.
	 * Supports popacman (TODO: test)
	 * @return
	 */
	public int getNumEatenGhosts() {
		return oldG == null ?
				poG.getNumGhostsEaten():
				oldG.getEatenGhosts();
	}

	/**
	 * Return current score.
	 * Supports popacman (TODO: test)
	 * @return
	 */
	public double getScore() {
		return oldG == null ?
				poG.getScore():
				oldG.getScore();
	}

	/**
	 * Return reward for ghosts eaten across all levels, with those eaten in a
	 * single power pill run worth more.
	 *
	 * @return
	 */
	public double getGhostReward() {
		return oldG == null ?
				-1: //TODO: write a getGhostReward for poG
				oldG.getGhostReward();
	}

	/**
	 * Returns current level of game.
	 * Supports popacman (TODO: test)
	 * @return current level
	 */
	public int getCurrentLevel() {
		return oldG == null ?
				poG.getCurrentLevel():
				oldG.getCurrentLevel();
	}

	/**
	 * gets number of pills that have been eaten
	 * @return number of eaten pills
	 */
	public int getEatenPills() {
		return oldG == null ?
				-1: //TODO: write a getEatenPills method for poG
				oldG.getEatenPills();
	}

	/**
	 * gets total time in play.
	 * Supports popacman (TODO: test)
	 * @return total game play time
	 */
	public int getTotalTime() {
		return oldG == null ?
				poG.getTotalTime():
				oldG.getTotalTime();
	}

	/**
	 * gets number of lives pacman has remaining.
	 * Supports popacman (TODO: test)
	 * 
	 * @return num lives remaining
	 */
	public int getPacmanNumberOfLivesRemaining() {
		return oldG == null ?
				poG.getPacmanNumberOfLivesRemaining():
				oldG.getPacmanNumberOfLivesRemaining();
	}

	/**
	 * Gets current time spent on level.
	 * Supports popacman (TODO: test)
	 * @return time spent on level
	 */
	public int getCurrentLevelTime() {
		return oldG == null ? 
			poG.getCurrentLevelTime():
			oldG.getCurrentLevelTime();
	}

	/**
	 * Gets index of node pacman is currently
	 * occupying. Supports popacman (TODO: test)
	 * @return current node
	 */
	public int getPacmanCurrentNodeIndex() {
		return oldG == null ? 
			poG.getPacmanCurrentNodeIndex(): // assumes must be poG 
			oldG.getPacmanCurrentNodeIndex();
	}

	/**
	 * Gets the last move made by pacman.
	 * Supports popacman (TODO: test)
	 * @return index corresponding to pacmans last move
	 */
	public int getPacmanLastMoveMade() {
		return oldG == null ?
				moveToIndex(poG.getPacmanLastMoveMade()):
				moveToIndex(oldG.getPacmanLastMoveMade());
	}

	/**
	 * returns whether or not game requires an action currently
	 * Supports popacman (TODO: test)
	 * @return whether or not game requires action
	 */
	public boolean anyRequiresAction() {
		return oldG == null ?
				anyRequiresActionPO(poG):
				anyRequiresAction(oldG);
	}

	/**
	 * returns whether or not any of the ghosts are edible.
	 * Supports popacman (TODO: test)
	 * @return ghosts edible
	 */
	public boolean anyIsEdible() {
		return oldG == null ? 
				anyIsEdible(poG) :
				anyIsEdible(oldG);
	}

	/**
	 * Given node index, an array of size 4 is returned containing the
	 * neighboring node indices. Each slot in the array corresponds to a
	 * specific direction, and if there is no neighbor in the given direction,
	 * then slot is filled with a -1. Supports popacman (TODO: test)
	 *
	 * @param current
	 *            start node
	 * @return array of neighbor nodes with -1 for walls
	 */
	public int[] neighbors(int current) {
			assert current != -1 : "-1 is not a valid node index";
			return neighbors(oldG, current);
	}

	/**
	 * Returns index of neighbor node in given direction.
	 * Returns -1 if no neighbor.
	 * Supports popacman (TODO: test).
	 * @param current index of current node
	 * @param dir index of direction to take
	 * @return index of neighboring node
	 */
	public int neighborInDir(int current, int dir) {
			return neighbors(current)[dir];
	}

	/**
	 * The index for the direction pacman came from is -1.
	 * Supports popacman (TODO: test)
	 *
	 * @param current
	 *            = position to get neighbors of
	 * @param lastMove
	 *            = exclude opposite of this direction
	 * @return neighbors without source node
	 */
	public int[] restrictedNeighbors(int current, int lastMove) {
		int[] neighbors = neighbors(current);
		assert neighbors[0] != current : "The upward neighbor of " + current + " is " + neighbors[0] + ":"
				+ Arrays.toString(neighbors);
		assert neighbors[1] != current : "The right neighbor of " + current + " is " + neighbors[1] + ":"
				+ Arrays.toString(neighbors);
		assert neighbors[2] != current : "The downward neighbor of " + current + " is " + neighbors[2] + ":"
				+ Arrays.toString(neighbors);
		assert neighbors[3] != current : "The left neighbor of " + current + " is " + neighbors[3] + ":"
				+ Arrays.toString(neighbors);
		if (lastMove == -1) {
			return neighbors;
		}
		
		//flow control to differentiate between newG and poG
		if(oldG == null) {
			neighbors[getReversePO(lastMove)] = -1;
			return neighbors;
		} else {
			neighbors[getReverse(lastMove)] = -1;
			return neighbors;
		}
	}

	/**
	 * Whether or not a ghost reversal occurred on the current time step.
	 * Supports popacman (TODO: test)
	 *
	 * @return true or false
	 */
	public boolean ghostReversal() {
		return oldG == null ?
				poG.getTimeOfLastGlobalReversal() == poG.getTotalTime():
				oldG.getTimeOfLastGlobalReversal() == oldG.getTotalTime();
	}

	/**
	 * Returns time of last ghost reversal.
	 * Supports popacman (TODO: test)
	 * @return time of last ghost reversal
	 */
	public int getTimeOfLastGlobalReversal() {
		return oldG == null ?
				poG.getTimeOfLastGlobalReversal():
				oldG.getTimeOfLastGlobalReversal();
	}

	/**
	 * Returns how much time has passed since a ghost.
	 * Supports popacman (TODO: test)
	 * last reversed
	 * @return time since last reversal
	 */
	public int timeSinceLastGlobalReversal() {
		//flow control to differentiate between newG and poG
		if(oldG == null) {
			int timeOfReversal = poG.getTimeOfLastGlobalReversal();
			return timeOfReversal == -1 ? -1 : poG.getTotalTime() - timeOfReversal;
		} else {
			int timeOfReversal = oldG.getTimeOfLastGlobalReversal();
			return timeOfReversal == -1 ? -1 : oldG.getTotalTime() - timeOfReversal;
		}
	}

	/**
	 * Returns the max time a ghost is edible.
	 * Supports popacman (TODO: test)
	 * @return max time edible ghost
	 */
	public int maxEdibleTime() {
		return oldG == null ?
				maxEdibleTime(poG):
				maxEdibleTime(oldG);
	}
	
	/**
	 * gets index of node ghost is currently occupying.
	 * Supports popacman (TODO: test)
	 * @param ghostIndex index of ghost in question
	 * @return index of occupied node
	 */
	public int getGhostCurrentNodeIndex(int ghostIndex) {
		return oldG == null ?
				poG.getGhostCurrentNodeIndex(indexToGhostPO(ghostIndex)):
				oldG.getGhostCurrentNodeIndex(indexToGhost(ghostIndex));
	}

	/**
	 * Returns last move ghost made.
	 * Supports popacman (TODO: test)
	 * @param ghostIndex index of ghost in question
	 * @return move ghost made
	 */
	public int getGhostLastMoveMade(int ghostIndex) {
		return oldG == null ?
				moveToIndex(poG.getGhostLastMoveMade(indexToGhostPO(ghostIndex))):
				moveToIndex(oldG.getGhostLastMoveMade(indexToGhost(ghostIndex)));
	}

	/**
	 * Gets shortest path from one node to another.
	 * Supports popacman (TODO: test)
	 * @param from source node
	 * @param to target node
	 * @return int array containing nodes in shortest path
	 */
	public int[] getShortestPath(int from, int to) {
		int result[];
		
		//flow control to differentiate between oldpacman and popacman
		if(oldG == null) {
			result = oldG.getShortestPath(from, to);
		} else {
			result = poG.getShortestPath(from, to);
		}
		
		assert(validPath(result)) : "Invalid path! " + Arrays.toString(result) + ":" + ("new");
		assert(result.length == 0
				|| result[result.length - 1] == to) : "Last element of path should be the to location! " + ("new");
		assert(result.length == 0 || result[0] != from) : "Path should NOT start at  location! " + ("new");
		return result;
	}

	/**
	 * returns whether given ghost is edible or not.
	 * Supports popacman (TODO: test).
	 * @param ghostIndex ghost in question
	 * @return ghost edible
	 */
	public boolean isGhostEdible(int ghostIndex) {
		return oldG == null ?
				poG.isGhostEdible(indexToGhostPO(ghostIndex)): 
				oldG.isGhostEdible(indexToGhost(ghostIndex));
	}

	/**
	 * Returns node index for next junction along current path. If current
	 * direction dead-ends into a corner, then there are only two junctions on
	 * either side of pacman: it is assumed the "next" junction is the one in
	 * the alternate direction to the one pacman came from, which would be the
	 * opposite of currentDir. Supports popacman (TODO: test)
	 *
	 * @param current
	 *            = node index to start from
	 * @param currentDir
	 *            = last direction moved in
	 * @return node index of first junction encountered in given direction.
	 */
	public int nextJunctionInDirection(int current, int currentDir) {
		return nextJunctionInDirection(current, currentDir, false);
	}

	/**
	 * Gets type of next junction in direction pacman is traveling.
	 * Supports popacman (TODO: test)
	 * @param current current node index
	 * @param currentDir current direction of pacman
	 * @param powerPillsToo if there are power pills in way ??TODO??
	 * @return
	 */
	public int nextJunctionInDirection(int current, int currentDir, boolean powerPillsToo) {
		
		int[] neighbors = restrictedNeighbors(current, currentDir);
		int numBlocked = ArrayUtil.countOccurrences(-1, neighbors);
		int pos = -1;
		int move = -1;
		
		switch (numBlocked) {
		case 2:// Facing a T-junction, so there is no junction "ahead", only to the sides
			if (neighbors[currentDir] == -1) {	
				return -1;
			}
		case 1://can continue in direction through joint
			pos = neighbors[currentDir];
			move = currentDir;
			break;
		case 3:// One option Elbow joint		
			pos = ArrayUtil.filter(neighbors, -1)[0];
			move = ArrayUtil.position(neighbors, pos);
			break;
		default:
			// No option
			System.out.println("Problem in nextJunctionInDirection(" + current + "," + currentDir + ") : "
					+ Arrays.toString(neighbors));
			System.exit(1);
		}
		// Go until a junction is reached
		while (!isJunction(pos) && (!powerPillsToo || !isPowerPillIndex(pos))) { 
			neighbors = restrictedNeighbors(pos, move);
			pos = ArrayUtil.filter(neighbors, -1)[0];
			move = ArrayUtil.position(neighbors, pos);
		}
		return pos;
	}

	/**
	 * Return next direction to go to get around corner.
	 * Supports popacman (TODO: test)
	 *
	 * @param current
	 *            = current location, MUST be an elbow/corner
	 * @param currentDir
	 *            = current pacman dir
	 * @return direction to get through elbow.
	 */
	public int nextMoveAtElbow(int current, int currentDir) {
		int[] neighbors = restrictedNeighbors(current, currentDir);
		if (neighbors[currentDir] != -1) {
			return currentDir;
		}
		int numBlocked = ArrayUtil.countOccurrences(-1, neighbors);
		assert(numBlocked == 3) : "Asked for elbow move, but not at elbow!\n" + current + ":" + currentDir + ":"
				+ Arrays.toString(neighbors) + ":" + Arrays.toString(neighbors(current));

		int pos = ArrayUtil.filter(neighbors, -1)[0];
		int move = ArrayUtil.position(neighbors, pos);

		return move;
	}

	/**
	 * Returns whether given index has a power pill on it.
	 * Supports popacman (TODO: test)
	 * @param index index in question
	 * @return power pill or not
	 */
	public boolean isPowerPillIndex(int index) {
		return oldG == null ?
				ArrayUtils.contains(poG.getActivePowerPillsIndices(), index):
				ArrayUtils.contains(oldG.getActivePowerPillsIndices(), index);
	}

	/**
	 * returns whether or not current index is a junction.
	 * Supports popacman (TODO: test)
	 * @param current current index
	 * @return whether node is a junction
	 */
	public boolean isJunction(int current) {
		return oldG == null ?
				poG.isJunction(current):
				oldG.isJunction(current);
	}

	/**
	 * Returns true if node index is a corner in the maze, meaning there are two
	 * routes out, but neither is directly in the opposite direction of the
	 * other.
	 * Supports popacman (TODO: test)
	 *
	 * @param current
	 *            = node index
	 * @return whether the node is an elbow/corner
	 */
	public boolean isElbow(int current) {

		int[] neighbors = neighbors(current);
		int numBlocked = ArrayUtil.countOccurrences(-1, neighbors);
		if (numBlocked == 2) { // Possible elbow
			// One open path
			int open = ArrayUtil.filter(neighbors, -1)[0];
			int move = ArrayUtil.position(neighbors, open);
			
			//flow control to differentiate between oldpacman and popacman
			if(oldG == null) {
				if (-1 == neighbors[getReversePO(move)]) {
					return true;
				}
			} else {
				if (-1 == neighbors[getReverse(move)]) {
					return true;
				}
			}
		
		}
		return false;
		
	}

	/**
	 * Gets euclidian distance between two nodes.
	 * Supports popacman (TODO: test)
	 * @param from source node
	 * @param to target node
	 * @return euclidian distance
	 */
	public double getEuclideanDistance(int from, int to) {
			return oldG == null ?
					poG.getEuclideanDistance(from, to):
					oldG.getEuclideanDistance(from, to);
		
	}

	/**
	 * Gets shortest path(length pacman can travel) distance.
	 * Supports popacman (TODO: test)
	 * @param from source node
	 * @param to target node
	 * @return distance
	 */
	public double getShortestPathDistance(int from, int to) {
		return oldG == null ?
				poG.getShortestPathDistance(from, to):
				oldG.getShortestPathDistance(from, to);
	}

	/**
	 * returns array containing indices of nodes that are 
	 * junctions. Supports popacman (TODO: test)
	 * @return array of node junction indices
	 */
	public int[] getJunctionIndices() {
			return oldG == null ?
					poG.getJunctionIndices():
					oldG.getJunctionIndices();
	}

	/**
	 * Number of neighbors around node that are not walls.
	 * Supports popacman (TODO: test)
	 * @param node
	 *            in maze
	 * @return number open neighbors
	 */
	public int getNumNeighbours(int node) {
		return oldG == null ?
				poG.getNeighbouringNodes(node).length:
				oldG.getNeighbouringNodes(node).length;
	}

	/**
	 * whether or not node has neighbors.
	 * Supports popacman (TODO: test)
	 * @param node index
	 * @return neighbors or no
	 */
	public boolean hasNeighbors(int node) {
		return this.getNumNeighbours(node) > 0;
	}

	/**
	 * Gets the index of the current maze.
	 * Supports popacman (TODO: test)
	 *
	 * @return The maze index
	 */
	public int getMazeIndex() {
		return oldG == null ?
				poG.getMazeIndex():
				oldG.getMazeIndex();
	}

	/**
	 * Shortest path from "from" to "to" in given "direction".
	 * Supports popacman (TODO: test)
	 * @param from
	 *            starting point, will NOT be in final path result
	 * @param to
	 *            end point, will be last member of path array returned
	 * @param direction
	 *            first step of path is in this direction from "from"
	 * @return shortest directional path
	 */
	public int[] getDirectionalPath(int from, int to, int direction) {
		int[] result = getPathInDirFromNew(from, to, direction);
		assert(validPath(result)) : ("Invalid path! " + Arrays.toString(result));
		assert(result[result.length - 1] == to) : ("Last element of path should be the to location!");
		assert(result[0] != from) : ("Path should NOT start at  location!");
		return result;
	}

	/**
	 * gets indices of active power pills on map.
	 * Supports popacman (TODO: test)
	 * @return indices of power pills
	 */
	public int[] getActivePowerPillsIndices() {
		return oldG == null ?
				poG.getActivePowerPillsIndices():
				oldG.getActivePowerPillsIndices();
	}

	/**
	 * gets time ghosts are in the lair.
	 * Supports popacman (TODO: test).
	 * @param ghostIndex ghost
	 * @return time ghost in lair
	 */
	public int getGhostLairTime(int ghostIndex) {
		return oldG == null ?
				poG.getGhostLairTime(indexToGhostPO(ghostIndex)) :
				oldG.getGhostLairTime(indexToGhost(ghostIndex));
	}

	/**
	 * gets indices of active pills.
	 * Supports popacman (TODO: test)
	 * @return indices of active pills
	 */
	public int[] getActivePillsIndices() {
		return oldG == null ?
				poG.getActivePillsIndices():
				oldG.getActivePillsIndices();
	}

	/**
	 * Gets the closest node index from node index.
	 * Supports popacman (TODO: test)
	 * @param fromNodeIndex
	 *            the from node index
	 * @param targetNodeIndices
	 *            the target node indices
	 * @param distanceMeasure
	 *            the distance measure
	 * @return the closest node index from node index
	 */
	public int getClosestNodeIndexFromNodeIndex(int current, int[] targets) {
		return oldG == null ?
				poG.getClosestNodeIndexFromNodeIndex(current, targets, pacman.game.Constants.DM.PATH):
				oldG.getClosestNodeIndexFromNodeIndex(current, targets, oldpacman.game.Constants.DM.PATH);
	}

	/**
	 * colors given set of nodes.
	 * Supportes popacman (TODO: test)
	 * @param c color to set
	 * @param nodes nodes to set color
	 */
	public void addPoints(Color c, Set<Integer> nodes) {
		addPoints(c, ArrayUtil.integerSetToArray(nodes));
	}

	/**
	 * colors given array of nodes.
	 * Supportes popacman (TODO: test)
	 * @param c color
	 * @param nodes nodes to set color
	 */
	public void addPoints(Color c, int[] nodes) {
		if(oldG == null) {
			if(nodes.length > 0) {
				pacman.game.GameView.addPoints(poG, c, ArrayUtil.filter(nodes, -1));
			}
		} else {
			if (nodes.length > 0) {
				oldpacman.game.GameView.addPoints(oldG, c, ArrayUtil.filter(nodes, -1));
			}
		}
	}

	/**
	 * Gets distance of path from one node to another.
	 * Supportes popacman (TODO: test)
	 * @param from index of sourceNode
	 * @param to index of TargetNode
	 * @return distance
	 */
	public double getPathDistance(int from, int to) {
		return oldG == null ?
				poG.getDistance(from, to, pacman.game.Constants.DM.PATH):
				oldG.getDistance(from, to, oldpacman.game.Constants.DM.PATH);
	}

	/**
	 * returns whether or not pacman is hitting a wall.
	 * Supportes popacman (TODO: test)
	 * @return hitting wall
	 */
	public boolean pacmanHittingWall() {
		return oldG == null ?
				poG.getPacmanLastMoveMade().equals(pacman.game.Constants.MOVE.NEUTRAL):
				oldG.getPacmanLastMoveMade().equals(oldpacman.game.Constants.MOVE.NEUTRAL);
	}

	/**
	 * This is the lair exit.
	 * Supports popacman (TODO: test)
	 * @return node of the lair exit
	 */
	public int getGhostInitialNodeIndex() {
		return oldG == null ?
				poG.getGhostInitialNodeIndex():
				oldG.getGhostInitialNodeIndex();
	}

	/**
	 * Adds a line to be drawn using the color specified.
	 * Supportes popacman (TODO: test)
	 * @param color
	 *            the color
	 * @param fromNnodeIndex
	 *            the from nnode index
	 * @param toNodeIndex
	 *            the to node index
	 */
	public void addLines(Color c, int from, int to) {
		//flow control to differentiate between oldpacman and popacman
		if(oldG == null) {
			pacman.game.GameView.addLines(poG, c, from, to);
		} else {
			oldpacman.game.GameView.addLines(oldG, c, from, to);
		}

	}

	/**
	 * Gets time ghost is edible.
	 * Supportes popacman (TODO: test)
	 * @param ghostIndex index of ghost
	 * @return time ghost is edible
	 */
	public int getGhostEdibleTime(int ghostIndex) {
		return oldG == null ?
				poG.getGhostEdibleTime(indexToGhostPO(ghostIndex)):
				oldG.getGhostEdibleTime(indexToGhost(ghostIndex));
	}

	/**
	 * Supportes popacman (TODO: test)
	 * @param whichGhost ghost
	 * @param to index of node to move to
	 * @return index of move
	 */
	public int getNextGhostDirTowards(int whichGhost, int to) {
		return oldG == null ?
				moveToIndex(poG.getApproximateNextMoveTowardsTarget(getGhostCurrentNodeIndex(whichGhost), to,
						poG.getGhostLastMoveMade(indexToGhostPO(whichGhost)), pacman.game.Constants.DM.PATH)):
				moveToIndex(oldG.getApproximateNextMoveTowardsTarget(getGhostCurrentNodeIndex(whichGhost), to,
						oldG.getGhostLastMoveMade(indexToGhost(whichGhost)), oldpacman.game.Constants.DM.PATH));
	}	

	/**
	 * Supportes popacman (TODO: test)
	 * @param whichGhost ghost
	 * @param to index of node to move to
	 * @return index of move
	 */
	public int getNextGhostDirAway(int whichGhost, int to) {
		return oldG == null ?
				moveToIndex(poG.getApproximateNextMoveAwayFromTarget(getGhostCurrentNodeIndex(whichGhost), to,
						poG.getGhostLastMoveMade(indexToGhostPO(whichGhost)), pacman.game.Constants.DM.PATH)):
				moveToIndex(oldG.getApproximateNextMoveAwayFromTarget(getGhostCurrentNodeIndex(whichGhost), to,
						oldG.getGhostLastMoveMade(indexToGhost(whichGhost)), oldpacman.game.Constants.DM.PATH));
	}

	/**
	 * Returns direction pacman should move in to reach "to" given that pacman
	 * cannot go in reverse from the lastDir direction it came from.
	 * Supportes popacman (TODO: test)
	 * @param from
	 *            starting point
	 * @param to
	 *            destination
	 * @param lastDir
	 *            last move made (prevent reversing)
	 * @return move
	 */
	public int getRestrictedNextDir(int from, int to, int lastDir) {
		return oldG == null ?
				moveToIndex(poG.getApproximateNextMoveTowardsTarget(from, to, indexToMovePO(lastDir), pacman.game.Constants.DM.PATH)):
				moveToIndex(oldG.getApproximateNextMoveTowardsTarget(from, to, indexToMove(lastDir), oldpacman.game.Constants.DM.PATH));
	}

	/**
	 * gets direction to go towards target.
	 * Supportes popacman (TODO: test)
	 * @param to node index of target
	 * @return direction
	 */
	public int getNextPacManDirTowardsTarget(int to) {
		return oldG == null ?
				moveToIndex(poG.getNextMoveTowardsTarget(poG.getPacmanCurrentNodeIndex(), to, pacman.game.Constants.DM.PATH)):
				moveToIndex(oldG.getNextMoveTowardsTarget(oldG.getPacmanCurrentNodeIndex(), to, oldpacman.game.Constants.DM.PATH));
	}

	/**
	 * gets direction to go away from target.
	 * Supportes popacman (TODO: test)
	 * @param to index of target 
	 * @return direction
	 */
	public int getNextPacManDirAwayFromTarget(int to) {
		return oldG == null ?
				moveToIndex(poG.getNextMoveAwayFromTarget(poG.getPacmanCurrentNodeIndex(), to, pacman.game.Constants.DM.PATH)):
				moveToIndex(oldG.getNextMoveAwayFromTarget(oldG.getPacmanCurrentNodeIndex(), to, oldpacman.game.Constants.DM.PATH));
	}

	/**
	 * gets number of pills left in game.
	 * Supportes popacman (TODO: test)
	 * @return num pills
	 */
	public int getNumberOfPills() {
		return oldG == null ?
				poG.getNumberOfPills():
				oldG.getNumberOfPills();
	}

	/**
	 * number of power pills left.,
	 * Supportes popacman (TODO: test)
	 * @return num power pills
	 */
	public int getNumberOfPowerPills() {
		return oldG == null ?
				poG.getNumberOfPowerPills():
				oldG.getNumberOfPowerPills();
	}

	/**
	 * Returns the current value awarded for eating a ghost.
	 * Supportes popacman (TODO: test)
	 * @return the current value awarded for eating a ghost.
	 */
	public int getGhostCurrentEdibleScore() {
			return oldG == null ?
				poG.getGhostCurrentEdibleScore():
				oldG.getGhostCurrentEdibleScore();
	}

	/**
	 * gets x coordinate of given node in maze input space.
	 * Supportes popacman (TODO: test)
	 * @param current index of node
	 * @return x coordinate
	 */
	public int getNodeXCoord(int current) {
		return oldG == null ?
				poG.getNodeXCood(current):
				oldG.getNodeXCoord(current);
	}

	/**
	 * gets y coordinate of given node in maze input space .
	 * Supportes popacman (TODO: test)
	 * @param current index of node 
	 * @return y coordinate
	 */
	public int getNodeYCoord(int current) {
		return oldG == null ?
				poG.getNodeYCood(current):
				oldG.getNodeYCoord(current);
	}

	/**
	 * gets indices where pills still are.
	 * Supportes popacman (TODO: test)
	 * @return indices of pills
	 */
	public int[] getPillIndices() {
		return oldG == null ?
				oldG.getPillIndices():
				oldG.getPillIndices();
	}

	/**
	 * gets indices where power pills still are.
	 * Supportes popacman (TODO: test)
	 * @return indices of power pills
	 */
	public int[] getPowerPillIndices() {
		return oldG == null ?
				poG.getPowerPillIndices():
				oldG.getPowerPillIndices();
	}

	/**
	 * returns whether or not given index from input space
	 * is a node in the maze. Supportes popacman (TODO: test)
	 * @param index index in maze
	 * @return whether or not in maze
	 */
	public boolean nodeInMaze(int index) {
		return oldG == null ?
				index < poG.getCurrentMaze().graph.length:
				index < oldG.getCurrentMaze().graph.length;
	}
	

	/**
	 * Gets the length of the node array.
	 * Supportes popacman (TODO: test)
	 * @return length of maze
	 */
	public int lengthMaze() {
		return oldG == null ?
				poG.getCurrentMaze().graph.length:
				oldG.getCurrentMaze().graph.length;
	}
	
	/**
	 * Returns whether or not given indices are in maze or not.
	 * True iff all nodes are in maze.
	 * Supportes popacman (TODO: test)
	 * @param indices indices to check
	 * @return iff nodes are in maze 
	 */
	public boolean allNodesInMaze(int[] indices) {
		for (int i = 0; i < indices.length; i++) {
			if (indices[i] != -1 && !nodeInMaze(indices[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * From fromNodeIndex heading in direction, find the closest node within
	 * targetNodeIndices and return a pair of both the target and the path to
	 * it. No "from" at start, but "to" is at the end. Supports popacman (TODO: test)
	 *
	 * @param fromNodeIndex
	 * @param targetNodeIndices
	 * @param direction
	 * @return
	 */
	public Pair<Integer, int[]> getTargetInDir(int fromNodeIndex, int[] targetNodeIndices, int direction) {
		return getTargetInDir(fromNodeIndex, targetNodeIndices, direction, true); // default to shortest
	}

	/**
	 *Can return either the shortest or longest path in a given direction to
	 * any one of several available targets. The chosen target is returned as
	 * well, in a pair. Supports popacman (TODO: test)
	 * 
	 * @param fromNodeIndex
	 *            start point
	 * @param targetNodeIndices
	 *            potential targets
	 * @param direction
	 *            direction pacman must go in
	 * @param shortest
	 *            true for shortest path, longest path otherwise
	 * @return path and target pair
	 */
	public Pair<Integer, int[]> getTargetInDir(int fromNodeIndex, int[] targetNodeIndices, int direction, boolean shortest) {
		assert fromNodeIndex != -1 : "Invalid from node: " + fromNodeIndex;
		assert direction >= 0 && direction <= 3 : "Not a valid direction: " + direction;
		Pair<Integer, int[]> result = getTargetInDirFromNew(fromNodeIndex, targetNodeIndices, direction, shortest);
		assert(result != null && result.t2 != null) : ("Why is pair null? " + result);
		assert(validPath(result.t2)) : ("Invalid path! " + Arrays.toString(result.t2));
		assert(result.t2.length == 0 || result.t2[0] != fromNodeIndex) : ("Path should NOT start at  location!");
		return result;
	}

	/**
	 * Can return either the shortest or longest path in a given direction to
	 * any one of several available targets. The chosen target is returned as
	 * well, in a pair. Supports popacman (TODO: test)
	 * 
	 * @param fromNodeIndex
	 *            start point
	 * @param targetNodeIndices
	 *            potential targets
	 * @param direction
	 *            direction pacman must go in
	 * @param shortest
	 *            true for shortest path, longest path otherwise
	 * @return path and target pair
	 */
	private Pair<Integer, int[]> getTargetInDirFromNew(int fromNodeIndex, int[] targetNodeIndices, int direction, boolean shortest) {
		
		assert targetNodeIndices.length > 0 : "targetNodeIndices empty:" + Arrays.toString(targetNodeIndices);
		int[] neighbors = neighbors(fromNodeIndex);
		assert(neighbors[direction] != -1) : ("Picked invalid direction " + direction + " given neighbors "	+ Arrays.toString(neighbors));
		
		double extremeDistance = shortest ? Integer.MAX_VALUE : -Integer.MAX_VALUE;
		int target = -1;
		int[] extremePath = null;
		for (int i = 0; i < targetNodeIndices.length; i++) {
			if (targetNodeIndices[i] == -1) {
				continue;
			}
			
			//flow control to differentiate between oldpacman and popacman
			if(oldG == null) {
				assert targetNodeIndices[i] < poG.getCurrentMaze().graph.length : targetNodeIndices[i]
						+ " is not an index in the maze " + poG.getCurrentLevel() + "/" + poG.getCurrentMaze().name
						+ " : " + Arrays.toString(targetNodeIndices) + ":" + targetNodeIndices.length;
			} else {
				assert targetNodeIndices[i] < oldG.getCurrentMaze().graph.length : targetNodeIndices[i]
						+ " is not an index in the maze " + oldG.getCurrentLevel() + "/" + oldG.getCurrentMaze().name
						+ " : " + Arrays.toString(targetNodeIndices) + ":" + targetNodeIndices.length;
			}
			
			int[] path = getPathInDirFromNew(fromNodeIndex, targetNodeIndices[i], direction);

			assert(path.length == 0 || path[path.length- 1] == targetNodeIndices[i]) 
			: ("Last element of path should be the to location! " + ("new"));
		        				
		    assert(path.length == 0 || path[0] != fromNodeIndex) : ("Path should NOT start at  location! " + ("new"));
			    
			// Shortest distance lower bound on direction distance
			if (shortest ? path.length < extremeDistance : path.length > extremeDistance) {
				extremeDistance = path.length;
				target = targetNodeIndices[i];
				extremePath = path;
			}
		}
		
		assert extremePath != null : "Extreme path is null: targetNodeIndices:" + Arrays.toString(targetNodeIndices)
				+ ":extremeDistance:" + extremeDistance;
		return new Pair<Integer, int[]>(target, extremePath);
}

	/**
	 * gets distance ghost must travel to get from current index to given index.
	 * Supports popacman (TODO: test)
	 * @param ghostIndex index of ghost
	 * @param toNodeIndex index to travel to
	 * @return euclidian distance
	 */
	public double getGhostPathDistance(int ghostIndex, int toNodeIndex) {
		return oldG == null ?
			poG.getDistance(getGhostCurrentNodeIndex(ghostIndex), toNodeIndex,
				poG.getGhostLastMoveMade(indexToGhostPO(ghostIndex)), pacman.game.Constants.DM.PATH):
			oldG.getDistance(getGhostCurrentNodeIndex(ghostIndex), toNodeIndex,
					oldG.getGhostLastMoveMade(indexToGhost(ghostIndex)), oldpacman.game.Constants.DM.PATH);
	}

	/**
	 * Determines how long it will take a ghost to reach a given destination,
	 * factoring int speed reduction from being edible. Supports popacman (TODO: test)
	 * 
	 * @param ghostIndex
	 * @param toNodeIndex
	 * @return
	 */
	public int getGhostTravelTime(int ghostIndex, int toNodeIndex) {
		int distance;
		int edibleTime;
		int effectiveEdibleTime;
		
		distance = (int) getGhostPathDistance(ghostIndex, toNodeIndex);
		edibleTime = this.getGhostEdibleTime(ghostIndex);
		
		//flow control to differentiate between oldpacman and popacman
		if(oldG == null) {
			effectiveEdibleTime = Math.min(edibleTime, distance * pacman.game.Constants.GHOST_SPEED_REDUCTION);
			return effectiveEdibleTime + distance
					- ((int) Math.ceil(effectiveEdibleTime / pacman.game.Constants.GHOST_SPEED_REDUCTION));
		} else {
			effectiveEdibleTime = Math.min(edibleTime, distance * oldpacman.game.Constants.GHOST_SPEED_REDUCTION);
			return effectiveEdibleTime + distance
					- ((int) Math.ceil(effectiveEdibleTime / oldpacman.game.Constants.GHOST_SPEED_REDUCTION));
		}
		
	}

	/**
	 * Supports popacman (TODO: test)
	 * @param ghostIndex
	 * @param toNodeIndex
	 * @return
	 */
	public boolean isGhostEdibleAfterTravel(int ghostIndex, int toNodeIndex) {
		return this.getGhostEdibleTime(ghostIndex) > getGhostTravelTime(ghostIndex, toNodeIndex);
	}

	/**
	 * Get the shortest path that a specific ghost could possibly take to reach
	 * the target location. The ghost is restricted in that it cannot reverse
	 * the last move it made. If non-empty, the last index of the array will be
	 * the same as the target. The ghost's position is not included in the
	 * array. Supports popacman (TODO: test)
	 *
	 * @param ghostIndex
	 *            ghost id
	 * @param target
	 *            target ghost is approaching
	 * @return shortest path ghost can take as array of int
	 */
	public int[] getGhostPath(int ghostIndex, int target) {
		int[] result;
		
		//flow control to differentiate between oldpacman and popacman
		if(oldG == null) {
			result = poG.getShortestPath(getGhostCurrentNodeIndex(ghostIndex), target, poG.getGhostLastMoveMade(indexToGhostPO(ghostIndex)));
		} else {
			result = oldG.getShortestPath(getGhostCurrentNodeIndex(ghostIndex), target, oldG.getGhostLastMoveMade(indexToGhost(ghostIndex)));
		}
		
		assert(result.length == 0 || result[result.length - 1] == target) : ("Last element of path should be the to location!");
		assert(result.length == 0 || result[0] != this.getGhostCurrentNodeIndex(ghostIndex)) : ("Path should NOT start at  location!");
		return result;
	}

	/**
	 * Gets number of ghosts that are edible.
	 * Supports popacman (TODO : test)
	 * @return num edible ghosts
	 */
	public int getNumberOfEdibleGhosts() {
		int total = 0;
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			
			//flow control to differentiate between popacman and oldpacman
			if(oldG == null) {
				if (poG.isGhostEdible(indexToGhostPO(i))) {
					total++;
				}
			} else {
				if (oldG.isGhostEdible(indexToGhost(i))) {
					total++;
				}
			}
		}
		return total;
	}

	/**
	 * says whether given ghost is a threat.
	 * Supports popacman (TODO: test)
	 * @param ghostIndex index of ghost
	 * @return whether threat
	 */
	public boolean isGhostThreat(int ghostIndex) {
			return !isGhostEdible(ghostIndex) && getNumNeighbours(getGhostCurrentNodeIndex(ghostIndex)) > 0;
	}

	/**
	 * Lair time of each active ghost, including those not in lair (value of 0).
	 * Supports popacman (TODO: test)
	 * @return
	 */
	public int[] getGhostLairTimes() {
		int[] times = new int[CommonConstants.numActiveGhosts];
		for (int i = 0; i < times.length; i++) {
			times[i] = this.getGhostLairTime(i);
		}
		return times;
	}

	/**
	 * Gets the time each ghost is edible.
	 * Supports popacman (TODO: test)
	 * @return array of edible times
	 */
	public int[] getGhostEdibleTimes() {
		int[] times = new int[CommonConstants.numActiveGhosts];
		for (int i = 0; i < times.length; i++) {
			times[i] = this.getGhostEdibleTime(i);
		}
		return times;
	}

	/**
	 * Gets the farthest node index from current index.
	 * Supports popacman (TODO: test)
	 * @param current current index
	 * @param targets indices of target nodes
	 * @return farthest node from targets array
	 */
	public int getFarthestNodeIndexFromNodeIndex(int current, int[] targets) {
		return oldG == null ?
				poG.getFarthestNodeIndexFromNodeIndex(current, targets, pacman.game.Constants.DM.PATH):
				oldG.getFarthestNodeIndexFromNodeIndex(current, targets, oldpacman.game.Constants.DM.PATH);
	}	

	/**
	 * gets indices of edible ghosts.
	 * Supports popacman (TODO: test)
	 * @return indices of edible ghosts
	 */
	public int[] getEdibleGhostLocations() {
		return getEdibleGhostLocations(new boolean[] { true, true, true, true });
	}

	/**
	 * Gets edible ghosts location with decision
	 * on whether or not to include certain ghosts.
	 * Supports popacman (TODO: test)
	 * @param include which ghosts are included
	 * @return indices of chosen edible ghosts
	 */
	public int[] getEdibleGhostLocations(boolean[] include) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (include[i] && isGhostEdible(i)) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * True if threat is coming at pacman along direction.
	 * Supports popacman (TODO: test)
	 * @param pacmanDir
	 *            direction relative to pacman
	 * @return true if threat imminent
	 */
	public boolean isThreatIncoming(int pacmanDir) {
			return isAnyGhostIncoming(pacmanDir, true);
	}

	/**
	 * true if edible ghost coming at pacman along direction.
	 * Supports popacman (TODO: test)
	 * @param pacmanDir pacman's direction
	 * @return true if edible ghost is imminent
	 */
	public boolean isEdibleIncoming(int pacmanDir) {
		return isAnyGhostIncoming(pacmanDir, false);
	}

	/**
	 * True if any ghost is coming at pacman along direction.
	 * Supports popacman (TODO: test)
	 * @param pacmanDir direction of pacman
	 * @param threatNotEdible boolean allowing for reuse of method
	 * @return whether ghost incoming
	 */
	public boolean isAnyGhostIncoming(int pacmanDir, boolean threatNotEdible) {
		for (int i = 0; i < this.getNumActiveGhosts(); i++) {
			if ((threatNotEdible && isGhostThreat(i)) || (!threatNotEdible && isGhostEdible(i))) {
				if (isGhostIncoming(pacmanDir, i)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * True if ghost is coming at pacman along a path that goes through the
	 * neighbor of pacman in direction "pacmanDir". Supports popacman (TODO: test)
	 *
	 * @param pacmanDir
	 *            direction from pacman of neighbor
	 * @param ghostIndex
	 *            ghost that may be approaching along that neighbor
	 * @return true if ghost is approaching through that neighbor
	 */
	public boolean isGhostIncoming(int pacmanDir, int ghostIndex) {
		int current = this.getPacmanCurrentNodeIndex();
		int[] neighbors = this.neighbors(current);
		assert neighbors[pacmanDir] != -1 : "Pacman dir is a wall: " + pacmanDir + "; " + Arrays.toString(neighbors);
		int[] ghostPath = getGhostPath(ghostIndex, current);
		return ArrayUtils.contains(ghostPath, neighbors[pacmanDir]);
	}

	/**
	 * Returns true if there are no junctions between the specified ghost and
	 * pacman facing in the given direction. Supports popacman (TODO: test).
	 * 
	 * @param pacmanDir
	 *            Direction pacman could face
	 * @param ghostIndex
	 *            specific ghost
	 * @return True if there are no junctions from pacman to ghost in direction
	 */
	public boolean isGhostTrapped(int pacmanDir, int ghostIndex) {
		int current = this.getPacmanCurrentNodeIndex();
		int[] neighbors = this.neighbors(current);
		assert neighbors[pacmanDir] != -1 : "Pacman dir is a wall: " + pacmanDir + "; " + Arrays.toString(neighbors);
		int[] pacmanPath = this.getDirectionalPath(current, this.getGhostCurrentNodeIndex(ghostIndex), pacmanDir);
		int[] junctions = this.getJunctionIndices();
		return ArrayUtil.intersection(pacmanPath, junctions).length == 0;
	}

	/**
	 * Array containing locations of as many ghosts that are threats. Edible
	 * ghosts and ghosts in the lair are not present at all. Supports popacman (TODO: test)
	 *
	 * @return
	 */
	public int[] getThreatGhostLocations() {
		return getThreatGhostLocations(new boolean[] { true, true, true, true });
	}

	/**
	 * gets indices of threat ghosts.
	 * Supports popacman (TODO: test)
	 * @param include which ghosts to include
	 * @return indices of threat ghosts
	 */
	public int[] getThreatGhostLocations(boolean[] include) {
			ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				if (include[i] && isGhostThreat(i)) {
					ghostPositions.add(getGhostCurrentNodeIndex(i));
				}
			}
			return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * Get the locations of ghosts that are directly pursuing pacman along the
	 * shortest possible path. Supports popacman (TODO: test)
	 *
	 * @return
	 */
	public int[] getApproachingThreatGhostLocations() {
		return getApproachingThreatGhostLocations(new boolean[] { true, true, true, true });
	}

	/**
	 * Gets the locations of ghosts that are directly pursuing pacman along 
	 * the shortest possible path. Chosen ghosts only. Supports popacman (TODO: test)
	 * @param include chosen ghosts
	 * @return indices of ghost locations
	 */
	public int[] getApproachingThreatGhostLocations(boolean[] include) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (include[i] && isGhostThreat(i) && ghostApproachingPacman(i)) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * "Incoming" is different from "approaching". A ghost is approaching pacman
	 * if it is taking the shortest possible path to reach pacman. The concept
	 * of incoming is defined with respect to a specific direction away from
	 * where pacman currently is. A ghost is "incoming" from this direction if a
	 * direct path from it to pacman comes in along this direction. Supports popacman (TODO: test)
	 *
	 * @param pacmanDir pacman's direction
	 * @return indices of ghosts incoming
	 */
	public int[] getIncomingThreatGhostLocations(int pacmanDir) {
		return getIncomingThreatGhostLocations(pacmanDir, new boolean[] { true, true, true, true });
	}

	/**
	 * "Incoming" is different from "approaching". A ghost is approaching pacman
	 * if it is taking the shortest possible path to reach pacman. The concept
	 * of incoming is defined with respect to a specific direction away from
	 * where pacman currently is. A ghost is "incoming" from this direction if a
	 * direct path from it to pacman comes in along this direction. Supports popacman (TODO: test)
	 *
	 * @param pacmanDir pacman's direction
	 * @param include which ghosts to include in eval
	 * @return indices of ghosts incoming
	 */
	public int[] getIncomingThreatGhostLocations(int pacmanDir, boolean[] include) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (include[i] && isGhostThreat(i) && isGhostIncoming(pacmanDir, i)) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * gets indices of approaching and incoming threat ghosts.
	 * Supports popacman (TODO: test)
	 * @param pacmanDir pacman's direction 
	 * @return indices of ghosts 
	 */
	public int[] getApproachingOrIncomingThreatGhostLocations(int pacmanDir) {
		return getApproachingOrIncomingThreatGhostLocations(pacmanDir, new boolean[] { true, true, true, true });
	}

	/**
	 * gets indices of approaching and incoming threat ghosts.
	 * Supports popacman (TODO: test)
	 * @param pacmanDir pacman's direction
	 * @param include which ghosts to include
	 * @return indices of ghosts
	 */
	public int[] getApproachingOrIncomingThreatGhostLocations(int pacmanDir, boolean[] include) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (include[i] && isGhostThreat(i) && (isGhostIncoming(pacmanDir, i) || ghostApproachingPacman(i))) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * Gets indices of approaching and incoming edible ghosts.
	 * Supports popacman (TODO: test)
	 * @param pacmanDir pacman's direction
	 * @return indices of ghosts
	 */
	public int[] getApproachingOrIncomingEdibleGhostLocations(int pacmanDir) {
		return getApproachingOrIncomingEdibleGhostLocations(pacmanDir, new boolean[] { true, true, true, true });
	}

	/**
	 * Gets indices of approaching and imcoming edible ghosts.
	 * Supports popacman (TODO: test)
	 * @param pacmanDir pacman's direction
	 * @param include which ghosts to include
	 * @return indices of ghosts
	 */
	public int[] getApproachingOrIncomingEdibleGhostLocations(int pacmanDir, boolean[] include) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (include[i] && isGhostEdible(i) && (isGhostIncoming(pacmanDir, i) || ghostApproachingPacman(i))) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * Get the locations of edible ghosts that are directly approaching pacman
	 * along the shortest possible path. Supports popacman (TODO: test)
	 *
	 * @return indices of ghosts
	 */
	public int[] getApproachingEdibleGhostLocations() {
		return getApproachingEdibleGhostLocations(new boolean[] { true, true, true, true });
	}

	/**
	 * Get the locations of edible ghosts that are directly approaching pacman.
	 * Supports popacman (TODO: test)
	 * @param include which ghosts to include
	 * @return indices of ghosts
	 */
	public int[] getApproachingEdibleGhostLocations(boolean[] include) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (include[i] && isGhostEdible(i) && ghostApproachingPacman(i)) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * Return locations of edible ghosts that are "incoming" along the given
	 * direction relative to pacman's location. Supports popacman (TODO: test)
	 *
	 * @param pacmanDir
	 * @return indices of ghosts
	 */
	public int[] getIncomingEdibleGhostLocations(int pacmanDir) {
		return getIncomingEdibleGhostLocations(pacmanDir, new boolean[] { true, true, true, true });
	}

	/**
	 * Gets locations of edible ghosts that are "incoming" along given
	 * direction relative to pacman's location. Supports popacman (TODO: test)
	 * @param pacmanDir direction of pacman
	 * @param include which ghosts to include
	 * @return indices of ghosts
	 */
	public int[] getIncomingEdibleGhostLocations(int pacmanDir, boolean[] include) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (include[i] && isGhostEdible(i) && isGhostIncoming(pacmanDir, i)) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * Return true if the current shortest path to pacman that the ghost can
	 * possibly take (keeping in mind no reversal restrictions) is the same as
	 * the direction for the absolute shortest path. Supports popacman (TODO: test)
	 *
	 * @param ghostIndex
	 *            which ghost to check
	 * @return true if directly approaching pacman
	 */
	public boolean ghostApproachingPacman(int ghostIndex) {
		final int current = this.getPacmanCurrentNodeIndex();
		int[] ghostPath = getGhostPath(ghostIndex, current);
		int[] shortestPath = getShortestPath(getGhostCurrentNodeIndex(ghostIndex), current);
		// Paths could be different if two equal-length paths exist
		return (ghostPath.length == shortestPath.length);
	}

	/**
	 * Returns positions of all ghosts that are not currently confined to the
	 * lair, whether they are edible or threats. Supports popacman (TODO: test)
	 *
	 * @return array with node indices of active ghosts
	 */
	public int[] getActiveGhostLocations() {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (this.getNumNeighbours(getGhostCurrentNodeIndex(i)) > 0) {
				ghostPositions.add(getGhostCurrentNodeIndex(i));
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	/**
	 * If ghosts are located at the ghostLocation, 
	 * then an array of the ghost indices
	 * is returned. The array is empty if there
	 * are no ghosts. Supports popacman (TODO: test)
	 *
	 * @param ghostLocation
	 *            node index where ghost may be
	 * @return array of ghost indices (0 through 3) at location
	 */
	public int[] getGhostIndexOfGhostAt(int ghostLocation) {
		ArrayList<Integer> locs = new ArrayList<Integer>();
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (getGhostCurrentNodeIndex(i) == ghostLocation) {
				locs.add(i);
			}
		}
		return ArrayUtil.intArrayFromArrayList(locs);
	}

	/**
	 * Gets next move to take in order to reach given target.
	 * Supports popacman (TODO: test)
	 * @param from index of source 
	 * @param to index of target
	 * @return next move
	 */
	public int getNextMoveTowardsTarget(int from, int to) {
		return oldG == null ?
				moveToIndex(poG.getNextMoveTowardsTarget(from, to, pacman.game.Constants.DM.PATH)):
				moveToIndex(oldG.getNextMoveTowardsTarget(from, to, oldpacman.game.Constants.DM.PATH));
	}

	/**
	 * returns number of active ghosts.
	 * Supports popacman (TODO: test)
	 * @return num active ghosts
	 */
	public int getNumActiveGhosts() {
		return CommonConstants.numActiveGhosts;
	}

	/**
	 * Advances the game one step.
	 * Supports popacman (TODO: test)
	 * @param pacManDir direction pacman is to take
	 * @param ghostDirs direction ghosts are to take
	 */
	public void advanceGame(int pacManDir, int[] ghostDirs) {
		//flow control to differentiate between popacman and oldpacman 
		if(oldG == null) {
			EnumMap<pacman.game.Constants.GHOST, pacman.game.Constants.MOVE> myMoves = 
					new EnumMap<pacman.game.Constants.GHOST, pacman.game.Constants.MOVE>(pacman.game.Constants.GHOST.class);
			
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				myMoves.put(indexToGhostPO(i), indexToMovePO(ghostDirs[i]));
			}
			
			poG.advanceGame(indexToMovePO(pacManDir), myMoves);
		} else {
			EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> myMoves = 
					new EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE>(oldpacman.game.Constants.GHOST.class);
			
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				myMoves.put(indexToGhost(i), indexToMove(ghostDirs[i]));
			}
			
			oldG.advanceGame(indexToMove(pacManDir), myMoves);
		}
	}

	/**
	 * Simulate forward from pacman's current location to any node along the
	 * most direct path. Supports popacman (TODO: test)
	 *
	 * @param destination
	 *            node to move towards
	 * @param ghostModel
	 *            how to model the movement of the ghosts
	 * @return copy of game facade that moved towards location
	 */
	public GameFacade simulateTowardsLocation(int destination, GhostControllerFacade ghostModel) {
		int startLevel = this.getCurrentLevel();
		int previousLives = getPacmanNumberOfLivesRemaining();
		GameFacade copy = this;
		while (copy.getPacmanCurrentNodeIndex() != destination && copy.getCurrentLevel() == startLevel && !copy.gameOver()) {
			int simCurrent = copy.getPacmanCurrentNodeIndex();
			int dir = copy.getNextMoveTowardsTarget(simCurrent, destination);
			copy = copy.simulateInDir(dir, ghostModel);
			if (previousLives > copy.getPacmanNumberOfLivesRemaining()) {
				return null;
			}
			// must be updated in case of a life gain
			previousLives = copy.getPacmanNumberOfLivesRemaining();
		}
		return copy;
	}

	/**
	 * Given a ghost team (a model of how the ghosts will behave), and a
	 * direction for pacman to move, simulate pacman's movement along that
	 * direction, including turning at elbows, until the target is reached
	 * (targets will usually be junctions and power pill locations), or new
	 * level, or game over. Return the resulting game state within a new
	 * GameFacade.
	 *
	 * Returns null if pacman died. If pacman reached the target, then the
	 * current location of pacman in the returned facade will equal the target.
	 * Supports popacman (TODO: test)
	 *
	 * pre: number of current pacman lives is greater than 0
	 *
	 * @param dir
	 *            direction to go
	 * @param ghostModel
	 *            how the ghosts will behave
	 * @param target
	 *            stop when reached
	 * @return resulting game state, or null in case of death
	 */
	public GameFacade simulateToNextTarget(int dir, GhostControllerFacade ghostModel, int target) {
		int startLevel = this.getCurrentLevel();
		int previousLives = getPacmanNumberOfLivesRemaining();
		GameFacade copy = this;
		int steps = 0;
		while (copy.getPacmanCurrentNodeIndex() != target && copy.getCurrentLevel() == startLevel && !copy.gameOver()) {
			int simCurrent = copy.getPacmanCurrentNodeIndex();
			dir = steps == 0 ? dir : copy.getRestrictedNextDir(simCurrent, target, dir);
			copy = copy.simulateInDir(dir, ghostModel);
			steps++;
			if (previousLives > copy.getPacmanNumberOfLivesRemaining()) {
				return null;
			}
			// must be updated in case of a life gain
			previousLives = copy.getPacmanNumberOfLivesRemaining();
		}
		return copy;
	}

	/**
	 * Simulate one step in direction, given model of how to move ghosts. Don't
	 * allow reversals. Supports popacman (TODO: test)
	 *
	 * @param dir
	 *            direction to move
	 * @param ghostModel
	 *            how ghosts move
	 * @return new game state
	 */
	public GameFacade simulateInDir(int dir, GhostControllerFacade ghostModel) {
		GameFacade copy = this.copy();
		int[] ghostDirs = ghostModel.getActions(copy, 0);

		GameFacade backup = copy.copy();
		// Loop prevents reversals
		do {
			copy = backup.copy();
			copy.advanceGame(dir, ghostDirs);
		} while (copy.ghostReversal() && copy.getNumActivePowerPills() == backup.getNumActivePowerPills());
		return copy;
	}

	/**
	 * Return true if a proposed path contains the location of a threatening
	 * ghost. Supports popacman (TODO: test)
	 *
	 * @param path
	 *            path for pacman to traverse
	 * @return is threat on path?
	 */
	public boolean pathGoesThroughThreateningGhost(int[] path) {
		int[] ghostLocs = this.getThreatGhostLocations();
		for (int i = 0; i < path.length; i++) {
			for (int g = 0; g < ghostLocs.length; g++) {
				if (path[i] == ghostLocs[g]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * gets current score.
	 * Supports popacman (TODO: test)
	 * @param level level pacman is on
	 * @return score
	 */
	public double getScore(int level) {
		return oldG == null ?
				poG.getScore():
				oldG.getScore(level);
	}

	/**
	 * copies game facade.
	 * Supports popacman (TODO: test)
	 * @return copy
	 */
	public GameFacade copy() {
		return oldG == null ?
				new GameFacade(poG.copy()):
				new GameFacade(oldG.copy());
	}

	/**
	 * Gets reverse of given move. Has a popacman version
	 * @param move move made
	 * @return index of move
	 */
	public static int getReverse(int move) {
			return moveToIndex(indexToMove(move).opposite());
	}
	
	/**
	 * Gets reverse of given move.
	 * Used for popacman.
	 * @param move move made
	 * @return index of move
	 */
	public static int getReversePO(int move) {
			return moveToIndex(indexToMovePO(move).opposite());
	}
	

	/**
	 * Gets index of node to left of move made.
	 * POTENTIALLY Supports popacman (TODO: test)
	 * @param move move made
	 * @return index of left node
	 */
	public static int getLeftOf(int move) {
		//TODO: make sure that poG indexes moves in the same way as oldpacman
		return (move + 3) % 4;
	}

	/**
	 * gets index of node to right of move made.
	 * POTENTIALLY Supports popacman (TODO: test)
	 * @param move move made
	 * @return index of right node
	 */
	public static int getRightOf(int move) {
		//TODO: make sure that poG indexes moves in the same way as oldpacman
		return (move + 1) % 4;
	}

	/**
	 * Gets number of active pills.
	 * Supports popacman (TODO: test)
	 * @return num active pills
	 */
	public int getNumActivePills() {
		return oldG == null ?
				poG.getNumberOfActivePills():
				oldG.getNumberOfActivePills();
	}

	/**
	 * gets whether game is over or not.
	 * Supports popacman (TODO: test)
	 * @return game over
	 */
	public boolean gameOver() {
		return oldG == null ?
				poG.gameOver():
				oldG.gameOver();
	}

	/**
	 * Gets number of active power pills.
	 * Supports popacman (TODO: test)
	 * @return num active power pills
	 */
	public int getNumActivePowerPills() {
		return oldG == null ?
				poG.getNumberOfActivePowerPills():
				oldG.getNumberOfActivePowerPills();
	}

	/**
	 * To any given location, there may be multiple paths of equal length. The
	 * default path calculation functions break ties, but this method considers
	 * all equal length paths, and returns a collection that contains all nodes
	 * along all such paths (no particular order). Supports popacman (TODO: test)
	 *
	 * @param ghostIndex
	 * @param to index to move towards
	 * @return indices of nodes that will get pacman to target node
	 */
	public int[] getAllGhostPathNodes(int ghostIndex, int to) {
		assert this.nodeInMaze(to) : "Node (to) " + to + " not in maze " + this.getMazeIndex();
			int from = this.getGhostCurrentNodeIndex(ghostIndex);
			assert this.nodeInMaze(from) : "Node (from) " + from + " not in maze " + this.getMazeIndex();
			int[] tempPath = this.getGhostPath(ghostIndex, to);
			int[] model = new int[tempPath.length + 1];
			System.arraycopy(tempPath, 0, model, 1, tempPath.length);
			model[0] = from;
			int[] options = this.restrictedNeighbors(from, this.getGhostLastMoveMade(ghostIndex));
			return sameDistancePathNodes(to, model, options);
	}

	/**
	 * Returns a path of the same distance as model, including options.
	 * Supports popacman (TODO: test)
	 * @param to target node
	 * @param model distance to model
	 * @param options different options
	 * @return different path same length
	 */
	private int[] sameDistancePathNodes(int to, int[] model, int[] options) {
	
		HashSet<Integer> set = new HashSet<Integer>();
		Queue<Pair<Integer, Integer>> junctionDistancePairs = new LinkedList<Pair<Integer, Integer>>();
		for (int i = 0; i < model.length; i++) {
			int node = model[i];
			if (isJunction(node)) {
				junctionDistancePairs.add(new Pair<Integer, Integer>(node, i));
			}
			set.add(node);
		}
		// Neighbors of start point
		for (int i = 0; i < options.length; i++) {
			if (options[i] != -1) {
				
				assert this.nodeInMaze(options[i]) : "Option " + options[i] + " not in maze " + this.getMazeIndex();
				
				assert this.nodeInMaze(model[0]) : "Model start " + model[0] + " not in maze " + this.getMazeIndex();
				
				assert this.nodeInMaze(to) : "To " + to + " not in maze " + this.getMazeIndex();
				
				assert options[i] == this.neighbors(model[0])[i] : "(using neighbors) The option " + options[i]
						+ " in dir " + i + " does not correspond to the neighbor " + this.neighbors(model[0])[i]
						+ " of " + model[0] + ":\nmodel = " + Arrays.toString(model) + ":\nmodel[0] neighbors = "
						+ Arrays.toString(this.neighbors(model[0]));
				
				assert options[i] == this.neighborInDir(model[0], i) : "(using neighborInDir) The option " + options[i]
						+ " in dir " + i + " does not correspond to the neighbor " + this.neighborInDir(model[0], i)
						+ " of " + model[0] + ":\nmodel = " + Arrays.toString(model) + ":\nmodel[0] neighbors = "
						+ Arrays.toString(this.neighbors(model[0]));

				int[] branch = this.getDirectionalPath(model[0], to, i);
				
				if (branch.length == model.length) {
					// Alternate path is same length
					for (int p = 1; p < branch.length; p++) {
						Pair<Integer, Integer> pair = new Pair<Integer, Integer>(branch[p], p + 1);
						if (isJunction(branch[p]) && !junctionDistancePairs.contains(pair)) {
							junctionDistancePairs.add(pair);
						}
						if (set.contains(branch[p])) {
							// One repeated node means rest of path can be ignored
							break;
						} else {
							set.add(branch[p]);
						}
					}
				}
			}
		}
		// Branches at junctions
		while (!junctionDistancePairs.isEmpty()) {
			Pair<Integer, Integer> pair = junctionDistancePairs.poll();
			int junction = pair.t1;
			int soFar = pair.t2;
			int[] neighbors = neighbors(junction);
			for (int i = 0; i < neighbors.length; i++) {
				if (neighbors[i] != -1 && !set.contains(neighbors[i])) {
					// Now check to see if path is right distance
					int[] branch = this.getDirectionalPath(junction, to, i);
					if (branch.length + soFar == model.length) {
						// this.addPoints(Color.GRAY, branch);
						// Alternate path is same length
						for (int p = 1; p < branch.length; p++) {
							Pair<Integer, Integer> pair2 = new Pair<Integer, Integer>(branch[p], p + soFar + 1);
							if (isJunction(branch[p]) && !junctionDistancePairs.contains(pair2)) {
								junctionDistancePairs.add(pair2);
							}
							if (set.contains(branch[p])) {
							// One repeated node means rest of path can be ignored
								break;
							} else {
								set.add(branch[p]);
							}
						}
					}
				}
			}
		}
		// Prepare results
		int[] result = new int[set.size()];
		int in = 0;
		for (Integer node : set) {
			result[in++] = node;
		}
		return result;
	}

	/**
	 * Adds color to given nodes.
	 * Supports popacman (TODO: test)
	 * @param c color
	 * @param list points to color
	 */
	public void addPoints(Color c, ArrayList<Integer> list) {
		this.addPoints(c, ArrayUtil.intArrayFromArrayList(list));
	}

	/**
	 * Assumes agent can move in the given direction, i.e. the neighbor exists.
	 * Calculates path is newG != null (using new pacman version).
	 * Supports popacman (TODO: test)
	 * @param from
	 *            from node (will not be in path)
	 * @param to
	 *            to node (will be at end of path)
	 * @param direction
	 *            direction to move in [0/UP, 1/RIGHT, 2/DOEN, 3/LEFT]
	 * @return path from -> to in direction
	 */
	public int[] getPathInDirFromNew(int from, int to, int direction) {
		/**
		 * This method depends on the newG method getShortestPath, which
		 * excludes the opposite of "direction". The other neighbors need to be
		 * checked.
		 */
		int[] neighbors = neighbors(from);
		assert(neighbors[direction] != -1) : ("Picked invalid direction " + direction + " given neighbors " + Arrays.toString(neighbors));
		int[] finalPath;
		int[] pathAfterStep;
		
		//TODO: MAKE SURE getLeftOf and getRightOf work propperly
		if (neighbors[getLeftOf(direction)] == -1 && neighbors[getRightOf(direction)] == -1) {
			// Can't go left or right, and getShortestPath prevents reverse, so
			// getShortestPath
			// will return the desired directional path
			assert neighbors[direction] != -1 : from + "'s neighbor in dir " + direction + " not available towards " + to;
			
			//flow control to differentiate between oldpacman and popacman
			if(oldG == null) {
				finalPath = poG.getShortestPath(from, to, indexToMovePO(direction));
			} else {
				finalPath = oldG.getShortestPath(from, to, indexToMove(direction));
			}
		
		} else {
			// Left and right are neighbors, so getShortestPath won't
			// necessarily go in
			// "direction" first, so let's take one step
			int oneStepNode = neighbors[direction];
					
			//flow control to differentiate between oldpacman and popacman
			if(oldG == null) {
				// Path after first step
				pathAfterStep = poG.getShortestPath(oneStepNode, to, indexToMovePO(direction));
			} else {
				// Path after first step
				pathAfterStep = oldG.getShortestPath(oneStepNode, to, indexToMove(direction));
			}
			
			// put back the first step
			int[] resultPath = new int[pathAfterStep.length + 1];
			resultPath[0] = oneStepNode;
			System.arraycopy(pathAfterStep, 0, resultPath, 1, pathAfterStep.length);
			finalPath = resultPath;
		}
		assert(finalPath.length == 0
				|| finalPath[finalPath.length - 1] == to) : ("Last element of path should be the to location!");
		assert(finalPath.length == 0 || finalPath[0] != from) : ("Path should NOT start at  location!");
		assert(validPath(finalPath)) : "Invalid path! " + Arrays.toString(finalPath);
		return finalPath;
		
	}

	/**
	 * Too expensive to actually run this on every path.
	 * Supports popacman (TODO: test)
	 * @param path
	 * @return if valid
	 */
	private boolean validPath(int[] path) {
		if (path == null) {
			return false;
		}
		for (int i = 1; i < path.length; i++) {
			// System.out.println(path.length +":" + i);
			if (!ArrayUtils.contains(neighbors(path[i - 1]), path[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return true if any ghost currently requires an action. Has a popacman version
	 *
	 * @param newG
	 *            instance of new pacman Game
	 * @return true if ghost requires action
	 */
	private static boolean anyRequiresAction(oldpacman.game.Game newG) {
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (newG.doesGhostRequireAction(indexToGhost(i))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Used for popacman. This method is incomplete.
	 * Return true if any ghost currently requires an action.
	 * Supports popacman (TODO: test)
	 * @param newG
	 *            instance of new pacman Game
	 * @return true if ghost requires action
	 */
	private static boolean anyRequiresActionPO(pacman.game.Game poG) {
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (poG.doesGhostRequireAction(indexToGhostPO(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns if any of the ghosts require action.
	 * Supports popacman (TODO: test)
	 * @param ghostIndex index of ghost to check
	 * @return whether ghost requires action
	 */
	public boolean doesGhostRequireAction(int ghostIndex) {
			return oldG == null ?
					poG.doesGhostRequireAction(indexToGhostPO(ghostIndex)):
					oldG.doesGhostRequireAction(indexToGhost(ghostIndex));
	}
	
	/**
	 * Return max edible time across all ghosts.
	 * has popacman version
	 * @param newG
	 *            new pacman Game instance
	 * @return max edible time
	 */
	private static int maxEdibleTime(oldpacman.game.Game newG) {
		int max = -1;
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			max = Math.max(max, newG.getGhostEdibleTime(indexToGhost(i)));
		}
		return max;
	}
	
	/**
	 * Return max edible time across all ghosts.
	 * Used for popacman.
	 *
	 * @param newG
	 *            new pacman Game instance
	 * @return max edible time
	 */
	private static int maxEdibleTime(pacman.game.Game newG) {
			int max = -1;
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				max = Math.max(max, newG.getGhostEdibleTime(indexToGhostPO(i)));
			}
			return max;
	}


	/**
	 * Return true if any ghost is edible.
	 * has a popacman version
	 * @param newG
	 *            new pacman Game instance
	 * @return any ghost edible?
	 */
	private boolean anyIsEdible(oldpacman.game.Game newG) {
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (newG.isGhostEdible(indexToGhost(i))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return true if any ghost is edible.
	 * Used for popacman
	 * @param newG
	 *            new pacman Game instance
	 * @return any ghost edible?
	 */
	private boolean anyIsEdible(pacman.game.Game newG) {
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (newG.isGhostEdible(indexToGhostPO(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns if pacman just ate a power pill. 
	 * Supports popacman (TODO: test)
	 * @return if power pill just previously eaten
	 */
	public boolean justAtePowerPill() {
		return oldG == null ?
				poG.wasPowerPillEaten():
				oldG.wasPowerPillEaten();
	}

	/**
	 * Given some path that pacman wants to follow (must be short, to the next
	 * junction or power pill), return a pair: first is pacman's effective
	 * distance to destination, and then nearest threat's effective distance to
	 * destination. "Effective" because movement restrictions on ghosts are
	 * considered, as well as lair times. The "distance" may be 0 if a ghost can
	 * reach it first. Supports popacman (TODO: test)
	 *
	 * @param path
	 * @param target
	 * @return coordinates of closest threat
	 */
	public Pair<Double, Double> closestThreatToPacmanPath(int[] path, int target) {
		double closestThreatDistance = Double.MAX_VALUE;
		double pacManDistance = path.length;
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (isGhostThreat(i)) { // Ghost is a threat
				double distanceToNode;

				int[] gPath = getGhostPath(i, target);
				/*
				 * If ghost path is subset of pacman path on way to same
				 * location, then ghost must be moving away from pacman
				 */
				if (pathGoesThroughThreateningGhost(path)) {
					if (ArrayUtil.subset(gPath, path)) {
						 // Ghost will go through node, making it safe to follow
						distanceToNode = pacManDistance + GameFacade.MAX_DISTANCE;
					} else {
						distanceToNode = 0; // Really bad
					}
				} else {
					distanceToNode = gPath.length;
				}

				closestThreatDistance = Math.min(closestThreatDistance, distanceToNode);
			} else if (getGhostLairTime(i) > 0) {
				// Ghost may pop out when pacman passes
				int ghostStart = getGhostInitialNodeIndex();
				if (ArrayUtils.contains(path, ghostStart)) {
					pacManDistance = getShortestPathDistance(getPacmanCurrentNodeIndex(), ghostStart);
					int lairTime = getGhostLairTime(i);
					closestThreatDistance = Math.min(closestThreatDistance, lairTime);
				}
			}
		}
		return new Pair<Double, Double>(pacManDistance, closestThreatDistance);
		
	}

	/**
	 * gets the time since eating ghost, reward.
	 * TODO: Impelement getTimeGhostReward for popacman
	 * @return time
	 */
	public int getTimeGhostReward() {
		if(oldG == null) {
			System.out.println("TODO: Implement getTimeGhostReward for poG, GameFacade.java");
			return -1;
		} else {
				return oldG.getTimeGhostReward();
		}
	}

	/**
	 * Gets the time since eating pill, reward
	 * TODO: implement getTimePillReward for popacman
	 * @return time
	 */
	public double getTimePillReward() {
		if(oldG == null) {
			System.out.println("TODO: Implement getTimePillReward for poG, GameFacade.java");
			return -1;
		} else {
			return oldG.getTimePillReward();
		}
	}

	/**
	 * Get the times at which ghosts are eaten (with respect to total time).
	 * This is used as a fitness score and/or other score, and can thus be ignored
	 * in popacman for the moment.
	 * 
	 * TODO: implement getGhostEAtTimes for popacman
	 * @return
	 */
	public List<Integer> getGhostEatTimes() {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getGhostEatTimes for poG, GameFacade.java");
		} else {
			return	oldG.getGhostEatTimes();
		}
	}

	/**
	 * Set whether or not to end game after ghost eating chances.
	 * This terminates evaluation early, but this feature is not currently
	 * allowed in PO PacMan.
	 * 
	 * TODO: implement setEndAfterGhostEatingChances for popacman
	 * @param endAfterGhostEatingChances to set
	 */
	public void setEndAfterGhostEatingChances(boolean endAfterGhostEatingChances) {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getGhostEatTimes for poG, GameFacade.java");
		} else {
			oldG.setEndAfterGhostEatingChances(endAfterGhostEatingChances);
		}
	}

	/**
	 * Plays game with no pills.
	 * TODO: implement playWithoutPills and playWithPills for popacman
	 * @param noPills whether to play with pills or not
	 */
	public void playWithoutPills(boolean noPills) {
		if(oldG == null) {
			System.out.println("TODO: Implement playWithoutPills for poG, GameFacade.java");
		} else {
			if (noPills) {
				oldG.playWithoutPills();
			} else {
				oldG.playWithPills();
			}
		}
	}

	/**
	 * Plays game with no power pills.
	 * TODO: implement these methods for popacman
	 * @param noPowerPills play with power pills or not
	 */
	public void playWithoutPowerPills(boolean noPowerPills) {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement playWithoutPowerPills for poG, GameFacade.java");
		} else {
			if (noPowerPills) {
				oldG.playWithoutPowerPills();
			} else {
				oldG.playWithPowerPills();
			}
		}
	}

	/**
	 * Gets the sum of lure distances.
	 * TODO: implement this method in popacman
	 * @return sum
	 */
	public double getLureDistanceSum() {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getLureDistanceSum for poG, GameFacade.java");
		} else {
			return oldG.getLureDistanceSum();
		}
	}

	/**
	 * sets the end after power pill eaten
	 * @param luringTask whether to have luring task
	 */
	public void setEndAfterPowerPillsEaten(boolean luringTask) {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement setEndAfterPowerPillsEaten for poG, GameFacade.java");
		} else {
			oldG.setEndAfterPowerPillsEaten(luringTask);
		}
	}

	/**
	 * Gets the time spent in dead space
	 * @return time
	 */
	public double getTimeInDeadSpace() {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getTimeInDeadSpace for poG, GameFacade.java");
		} else {
			return oldG.getTimeInDeadSpace();
		}
	}

	/**
	 * Given a collection of targets, pair-wise shortest paths are computed
	 * between them all, and the nodes contained in all such paths are collected
	 * in a Set that is returned. Supports popacman (TODO: test)
	 *
	 * @param targets
	 * @return
	 */
	public Set<Integer> clusterTreeNodes(int[] targets) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < targets.length; i++) {
			for (int j = 0; j < targets.length; j++) {
				if (i != j && hasNeighbors(targets[i]) && hasNeighbors(targets[j])) {
					int[] path = this.getShortestPath(targets[i], targets[j]);
					for (Integer x : path) {
						result.add(x);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Gets size of tree for threat ghost shortest path.
	 * Supports popacman (TODO: test)
	 * @return size of tree
	 */
	public int threatGhostClusterTreeSize() {
		return clusterTreeNodes(this.getThreatGhostLocations()).size();
	}

	/**
	 * Gets size of tree for edible ghost shortest path.
	 * Supports popacman (TODO: test)
	 * @return size of tree
	 */
	public int edibleGhostClusterTreeSize() {
		return clusterTreeNodes(this.getEdibleGhostLocations()).size();
	}

	/**
	 * Gets number of nodes in maze.
	 * Supports popacman (TODO: test)
	 * @return num modes in maze
	 */
	public int getNumMazeNodes() {
		if(oldG == null) {
			return poG.getCurrentMaze().graph.length;
		} else {
			return oldG.getCurrentMaze().graph.length;
		}
	}

	/**
	 * Gets location of ghost based on proximity.
	 * Supports popacman (TODO: test)
	 * @param order ??TODO??
	 * @return node of ghost
	 */
	public int ghostLocationByProximity(int order) {
		ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < getNumActiveGhosts(); i++) {
			ghosts.add(i); // Put indices of ghosts in array
		}
		Collections.sort(ghosts, new GhostComparator(this, true, true));
		return getGhostCurrentNodeIndex(ghosts.get(order));
	}

	/**
	 * Gets properly eaten power pills
	 * @return num properly eaten power pills
	 */
	public double getProperlyEatenPowerPills() {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getProperlyEatenPowerPills for poG, GameFacade.java");
		} else {
			return oldG.getProperlyEatenPowerPills();
		}
	}

	/**
	 * Gets improperly eaten power pills
	 * @return num improperly eaten power pills
	 */
	public double getImproperlyEatenPowerPills() {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getImproperlyEatenPowerPills for poG, GameFacade.java");
		} else {
			return oldG.getImproperlyEatenPowerPills();
		}
	}

	/**
	 * Gets power pills eaten when ghosts far away
	 * @return num power pills eaten when ghosts far
	 */
	public double getPowerPillsEatenWhenGhostFar() {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getPowerPillsEatenWhenGhostFar for poG, GameFacade.java");
		} else {
			return oldG.getPowerPillsEatenWhenGhostFar();
		}
	}

	/**
	 * Return true if any ghost is outside of the lair and not edible.
	 * Supports popacman (TODO: test)
	 * @return
	 */
	public boolean anyIsThreat() {
		return this.getThreatGhostLocations().length > 0;
	}

	/**
	 * Ghost regret tracks the number of edible ghost chances that were missed.
	 * To count towards ghost regret, the chance to eat the ghost must first
	 * arise by eating a power pill. If certain ghosts could not go into the
	 * edible state because they were in the lair at the time, then they
	 * contribute to regret. If ghosts were already edible, then a chance to eat
	 * them was missed, and they contribute to regret. Most obviously, ghosts
	 * whose edible time runs out become threats again, and they contribute to
	 * regret.
	 *
	 * @return amount of ghost regret (positive)
	 */
	public int getGhostRegret() {
		if(oldG == null) {
			throw new UnsupportedOperationException("TODO: Implement getGhostRegret for poG, GameFacade.java");
		} else {
			return oldG.getGhostRegret();
		}
	}

	/**
	 * From a given start index, search in each direction until junctions are
	 * found. If includePowerPills is true, then they are included in this
	 * check. The indices found are considered to be at depth 1 away from the
	 * start. If the process is repeated with each of these nodes as start
	 * points, then the nodes found are at depth 2 and so on. All such nodes are
	 * returned in an array list organized to contain a set corresponding to
	 * each depth, up to the original depth parameter. A depth of 0 contains
	 * just the start point. Supports popcamn (TODO: test)
	 *
	 * @param startIndex
	 *            starting point of search
	 * @param depth
	 *            depth of search tree from starting point, branching at
	 *            junctions and maybe power pills
	 * @param includePowerPills
	 *            whether or not to junction at power pills
	 * @return collection of leaves in search tree
	 */
	public ArrayList<Set<Integer>> junctionsAtDepth(int startIndex, int depth, boolean includePowerPills, int lastNodeVisited) {
		Set<Integer> start = new HashSet<Integer>();
		// eliminate most recently visited node as option
		if (lastNodeVisited != -1 && !isJunction(startIndex)) {
			startIndex = lastNodeVisited;
		}
		start.add(startIndex);
		ArrayList<Set<Integer>> result = new ArrayList<Set<Integer>>();
		junctionsAtDepth(start, depth, includePowerPills, result);
		return result;
	}

	/**
	 * Recursive helper to above, that can have multiple candidate start points.
	 * Supports popacman (TODO: test)
	 *
	 * @param startPoints
	 * @param depth
	 * @param includePowerPills
	 * @return
	 */
	public void junctionsAtDepth(Set<Integer> startPoints, int depth, boolean includePowerPills, ArrayList<Set<Integer>> result) {
		result.add(startPoints);
		if (depth > 0) {
			Set<Integer> nextJunctions = new HashSet<Integer>();
			for (Integer start : startPoints) {
				assert start != -1 : "Cannot start at -1";
				int[] neighbors = neighbors(start);
				for (int i = 0; i < neighbors.length; i++) {
					if (neighbors[i] != -1) {
						int nextJunction = nextJunctionInDirection(start, i, includePowerPills);
						assert nextJunction != -1 : "Why can't a junction be reached from " + start + " in direction "
								+ i + "? neighbors = " + Arrays.toString(neighbors);
						nextJunctions.add(nextJunction);
					}
				}
			}
			junctionsAtDepth(nextJunctions, depth - 1, includePowerPills, result);
		}			
	}

	/**
	 * Return true if ghost with given index is in the lair.
	 * Supports popacman (TODO: test)
	 * @param ghostIndex
	 * @return
	 */
	public boolean ghostInLair(int ghostIndex) {
		return getGhostLairTime(ghostIndex) > 0;
	}

	/**
	 * If active ghosts are in lair, return the time remaining until one exits.
	 * Else return -1. Supports popacman (TODO: test)
	 * 
	 * @return time of next lair exit, -1 if none are in lair
	 */
	public int timeUntilNextLairExit() {
		int[] lairTimes = this.getGhostLairTimes();
		Arrays.sort(lairTimes);
		int nextExitIndex = 0;
		while (nextExitIndex < lairTimes.length && lairTimes[nextExitIndex] == 0) {
			nextExitIndex++;
		}
		if (nextExitIndex == lairTimes.length) {
			return -1;
		}
		return lairTimes[nextExitIndex];
	}

	/**
	 * returns if there are any active ghosts in lair.
	 * Supports popacman (TODO: test)
	 * @return if active ghosts in lair
	 */
	public boolean anyActiveGhostInLair() {
		int num = this.getNumActiveGhosts();
		for (int i = 0; i < num; i++) {
			if (ghostInLair(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if starting a new level, which is the case if the level time
	 * is zero. Supports popacman (TODO: test)
	 *
	 * @return
	 */
	public boolean levelJustChanged() {
		return this.getCurrentLevelTime() == 0;
	}

	/**
	 * Supports popacman (TODO: test)
	 * @return
	 */
	public int getNumberOfLairGhosts() {
		int count = 0;
		//branching to differentiate between popacman and oldpacman
		if(oldG == null) {
			for (int i = 0; i < pacman.game.Constants.NUM_GHOSTS; i++) {
				if (this.getGhostLairTime(i) > 0) {
					count++;
				}
			}
			return count;
		} else {
			for (int i = 0; i < oldpacman.game.Constants.NUM_GHOSTS; i++) {
				if (this.getGhostLairTime(i) > 0) {
					count++;
				}
			}
			return count;
		}
	}

	/**
	 * TODO: Implement averageGhostsEatenPerPowerPill for poG, GameFacade.java
	 * 
	 * AVG number of ghosts eaten per each power pill
	 * 
	 * @param punishUneatenPowerPills
	 *            include scores of 0 for each power pill that wasn't eaten at
	 *            all (even in levels that were not reached).
	 * @return
	 */
	public double averageGhostsEatenPerPowerPill(boolean punishUneatenPowerPills) {
		if(oldG == null) throw new UnsupportedOperationException("TODO: implement averageGhostsEatenPerPowerPill");
		
		return oldG.averageGhostsEatenPerPowerPill(punishUneatenPowerPills);
	}

	/**
	 * TODO: Implement averageTimeToEatAllGhostsAfterPowerPill for poG, GameFacade.java
	 * 
	 * average time it took pacman to eat all ghosts after eating
	 * a power pill
	 * @return average time
	 */
	public double averageTimeToEatAllGhostsAfterPowerPill() {
		if(oldG == null) throw new UnsupportedOperationException("TODO: implement averageTimeToEatAllGhostsAfterPowerPill");
		
		return oldG.averageTimeToEatAllGhostsAfterPowerPill();
	}

	/**
	 * sets whether pacman is edible by 
	 * ghosts exiting the lair
	 * @param exitLairEdible if pacman edible
	 */
	public void setExitLairEdible(boolean exitLairEdible) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setExitLairEdible for poG, GameFacade.java");
		} else {
			oldG.setExitLairEdible(exitLairEdible);
		}
	}

	/**
	 * ends game only when time limit reached
	 * @param endOnlyOnTimeLimit iff games ends on time limit
	 */
	public void setEndOnlyOnTimeLimit(boolean endOnlyOnTimeLimit) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setEndOnlyOnTimeLimit for poG, GameFacade.java");
		} else {
			oldG.setEndOnlyOnTimeLimit(endOnlyOnTimeLimit);
		}
	}

	/**
	 * Sets a random exit for lair
	 * @param randomLairExit if lair exit random
	 */
	public void setRandomLairExit(boolean randomLairExit) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setRandomLairExit for poG, GameFacade.java");
		} else {
			oldG.setRandomLairExit(randomLairExit);
		}
	}

	/**
	 * If more than one ghost can exit lair at same time
	 * @param simultaneousLairExit if simultaneous ghost exits
	 */
	public void setSimultaneousLairExit(boolean simultaneousLairExit) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setSimultaneousLairExit for poG, GameFacade.java");
		} else {
			oldG.setSimultaneousLairExit(simultaneousLairExit);
		}
	}

	/**
	 * Ghosts start outside of lair at beginning of game
	 * @param ghostsStartOutsideLair if ghosts start outside lair
	 */
	public void setGhostsStartOutsideLair(boolean ghostsStartOutsideLair) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setGhostsStartOutsideLair for poG, GameFacade.java");
		} else {
			oldG.setGhostsStartOutsideLair(ghostsStartOutsideLair);
		}
	}

	/**
	 * Allows ghosts to leave via only one lair exit
	 * @param onlyOneLairExitAllowed if ghosts can leave from more
	 * than one exit
	 */
	public void setOnlyOneLairExitAllowed(boolean onlyOneLairExitAllowed) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setOnlyOneLairExitAllowed for poG, GameFacade.java");
		} else {
			oldG.setOnlyOneLairExitAllowed(onlyOneLairExitAllowed);
		}
	}

	/**
	 * Whether or not to keep a database of lair exits
	 * @param lairExitDatabase database lair exists
	 */
	public void setLairExitDatabase(boolean lairExitDatabase) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setLairExitDatabase for poG, GameFacade.java");
		} else {
			oldG.setLairExitDatabase(lairExitDatabase);
		}
	}

	/**
	 * Removes pills near power pills
	 * @param removePillsNearPowerPills if remove pills near power pills
	 */
	public void setRemovePillsNearPowerPills(boolean removePillsNearPowerPills) {
		if (oldG == null){
			throw new UnsupportedOperationException("TODO: Implement setRemovePillsNearPowerPills for poG, GameFacade.java");
		} else {
			oldG.setRemovePillsNearPowerPills(removePillsNearPowerPills);
		}
	}
	
	/**
	 * Takes an popacman move and returns the equivalent oldpacman move
	 * @param move
	 * @return
	 * @throws NoSuchFieldException
	 * @author pricew 
	 */
	public static oldpacman.game.Constants.MOVE moveConverterPOOld(pacman.game.Constants.MOVE move){
		switch(move) {
			case NEUTRAL:
				return oldpacman.game.Constants.MOVE.NEUTRAL;
			case UP:
				return oldpacman.game.Constants.MOVE.UP;
			case LEFT:
				return oldpacman.game.Constants.MOVE.LEFT;
			case DOWN:
				return oldpacman.game.Constants.MOVE.DOWN;
			case RIGHT:
				return oldpacman.game.Constants.MOVE.RIGHT;
			default:
				System.out.println("ERROR in moveConverterPOOld, GAmeFacade.java");
				return null;
		}
	}
	
	/**
	 * Takes an oldpacman move and returns the equivalent popacman move
	 * @param move
	 * @return
	 * @throws NoSuchFieldException
	 * @author pricew
	 */
	public static pacman.game.Constants.MOVE moveConverterOldToPO(oldpacman.game.Constants.MOVE move){
		switch(move) {
			case NEUTRAL:
				return pacman.game.Constants.MOVE.NEUTRAL;
			case UP:
				return pacman.game.Constants.MOVE.UP;
			case LEFT:
				return pacman.game.Constants.MOVE.LEFT;
			case DOWN:
				return pacman.game.Constants.MOVE.DOWN;
			case RIGHT:
				return pacman.game.Constants.MOVE.RIGHT;
			default:
				System.out.println("ERROR in moveConverterOldPO, GAmeFacade.java");
				return null;
		}
	}

}
