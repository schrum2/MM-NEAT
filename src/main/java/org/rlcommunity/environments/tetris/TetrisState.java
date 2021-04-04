/*
 Copyright 2007 Brian Tanner
 http://rl-library.googlecode.com/
 brian@tannerpages.com
 http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.rlcommunity.environments.tetris;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import org.rlcommunity.rlglue.codec.types.Observation;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.tetris.TetrisViewer;
import edu.southwestern.util.datastructures.ArrayUtil;

public class TetrisState {
	/* Action values */

	public static final int LEFT = 0; /* Action value for a move left */
	public static final int RIGHT = 1; /* Action value for a move right */
	public static final int CW = 2; /* Action value for a clockwise rotation */
	public static final int CCW = 3; /*Action value for a counter clockwise rotation */
	public static final int NONE = 4; /* The no-action Action */
	public static final int FALL = 5; /* fall down */

	public static final int TETRIS_STATE_NUMBER_WORLD_GRID_BLOCKS = 200;
	public static final int TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS = 7;
	public static final int TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS_ORIENTATIONS = 4;
	public static final int TETRIS_STATE_CURRENT_X_INDEX = TETRIS_STATE_NUMBER_WORLD_GRID_BLOCKS
			+ TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS;
	public static final int TETRIS_STATE_CURRENT_Y_INDEX = TETRIS_STATE_CURRENT_X_INDEX + 1;
	public static final int TETRIS_STATE_CURRENT_ROTATION_INDEX = TETRIS_STATE_CURRENT_Y_INDEX + 1;
	public static final int TETRIS_STATE_CURRENT_HEIGHT_INDEX = TETRIS_STATE_CURRENT_ROTATION_INDEX + 1;
	public static final int TETRIS_STATE_CURRENT_WIDTH_INDEX = TETRIS_STATE_CURRENT_HEIGHT_INDEX + 1;
	public static final int TETRIS_STATE_NUMBER_OF_DISCRETE_FEATURES = TETRIS_STATE_NUMBER_WORLD_GRID_BLOCKS
			+ TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS + 5;
	public static final int[] BLOCK_ROTATIONS = { 2, 1, 4, 2, 2, 4, 4 };

	public static Random randomGenerator;
	public boolean blockMobile = true;
	public int currentBlockId;/* which block we're using in the block table */

	public int currentRotation = 0;
	public int currentX;/* where the falling block is currently */

	public int currentY;
	public int score;/* what is the current_score */
	private int linesCleared; /* the total number of lines cleared */

	public boolean is_game_over;/* have we reached the end state yet */

	public static int worldWidth = 10;/* how wide our board is */

	public static int worldHeight = 20;/* how tall our board is */

	public int[] worldState;/*
							 * what the world looks like without the current
							 * block
							 */

	/*Hold all the possible bricks that can fall*/
	// Schrum: Making this static to save repeated computation
	public static final Vector<TetrisPiece> POSSIBLE_BLOCKS = new Vector<TetrisPiece>();

	static { // gets called the first time the class is loaded
		initPossibleBlocks();
	}
	
	public static void forceResetBlocks() {
		POSSIBLE_BLOCKS.clear();
		initPossibleBlocks();
	}
	
	public static void initPossibleBlocks() {
		if(Parameters.parameters.booleanParameter("tetrisAllowLine")){
			POSSIBLE_BLOCKS.add(TetrisPiece.makeLine());
		}
		if(Parameters.parameters.booleanParameter("tetrisAllowSquare")){
			POSSIBLE_BLOCKS.add(TetrisPiece.makeSquare());
		}
		if(Parameters.parameters.booleanParameter("tetrisAllowTri")){
			POSSIBLE_BLOCKS.add(TetrisPiece.makeTri());
		}
		if(Parameters.parameters.booleanParameter("tetrisAllowSShape")){
			POSSIBLE_BLOCKS.add(TetrisPiece.makeSShape());
		}
		if(Parameters.parameters.booleanParameter("tetrisAllowZShape")){
			POSSIBLE_BLOCKS.add(TetrisPiece.makeZShape());
		}
		if(Parameters.parameters.booleanParameter("tetrisAllowLShape")){
			POSSIBLE_BLOCKS.add(TetrisPiece.makeLShape());
		}
		if(Parameters.parameters.booleanParameter("tetrisAllowJShape")){
			POSSIBLE_BLOCKS.add(TetrisPiece.makeJShape());
		}
	}
	
	private TetrisViewer viewer = null;

	public TetrisState() {
		if (CommonConstants.watch) {
			if (TetrisViewer.current == null) {
				// System.out.println("New TetrisViewer");
				viewer = new TetrisViewer();
			} else {
				// System.out.println("Same TetrisViewer");
				viewer = TetrisViewer.current;
			}
		}

		worldState = new int[worldHeight * worldWidth];
		reset();
	}

	public void reset() {
		currentX = worldWidth / 2 - 1;
		currentY = 0;
		score = 0;
		linesCleared = 0;
		for (int i = 0; i < worldState.length; i++) {
			worldState[i] = 0;
		}
		currentRotation = 0;
		is_game_over = false;
	}

	public Observation get_observation() {
		return get_observation(true);
	}
	
	public Observation get_observation(boolean includeMobile) {
		// get observation with only the state space
		try {
			int[] worldObservation = new int[worldState.length];

			for (int i = 0; i < worldObservation.length; i++) {
				worldObservation[i] = worldState[i];
			}

			// Schrum: Don't want to write the block in afterstates
			if(includeMobile) writeCurrentBlock(worldObservation);

			Observation o = new Observation(TETRIS_STATE_NUMBER_OF_DISCRETE_FEATURES, 0);
			for (int i = 0; i < worldObservation.length; i++) {
				if (worldObservation[i] == 0) {
					o.intArray[i] = 0;
				} else {
					o.intArray[i] = 1;
				}
			}
			for (int j = 0; j < POSSIBLE_BLOCKS.size(); ++j) {
				o.intArray[worldObservation.length + j] = 0;
			}
			// Set the bit vector value for which block is currently following
			o.intArray[worldObservation.length + currentBlockId] = 1;
			// Falling piece x
			o.intArray[TETRIS_STATE_CURRENT_X_INDEX] = this.currentX; 
			// Falling piece y
			o.intArray[TETRIS_STATE_CURRENT_Y_INDEX] = this.currentY; 
			// Falling piece rotation
			o.intArray[TETRIS_STATE_CURRENT_ROTATION_INDEX] = this.currentRotation; 
			o.intArray[TETRIS_STATE_CURRENT_HEIGHT_INDEX] = getHeight();
			o.intArray[TETRIS_STATE_CURRENT_WIDTH_INDEX] = getWidth();
			return o;

		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Error: ArrayIndexOutOfBoundsException in GameState::get_observation");
			System.err.println("Error: The Exception was: " + e);
			Thread.dumpStack();
			System.err.println("Current X is: " + currentX + " Current Y is: " + currentY + " Rotation is: " + currentRotation + " blockId: " + currentBlockId);
			System.err.println("Not realy sure what to do, so crashing.  Sorry.");
			System.exit(1);
			// Can never happen
			return null;
		}
	}

	public void writeCurrentBlock() {
		writeCurrentBlock(this.worldState);
	}

	public void writeCurrentBlock(int[] game_world) {
		int[][] thisPiece = POSSIBLE_BLOCKS.get(currentBlockId).getShape(currentRotation);

		for (int y = 0; y < thisPiece[0].length; ++y) {
			for (int x = 0; x < thisPiece.length; ++x) {
				if (thisPiece[x][y] != 0) {
					// Writing currentBlockId +1 because blocks are 0 indexed,
					// and we want spots to be
					// 0 if they are clear, and >0 if they are not.
					int linearIndex = calculateLinearArrayPosition(currentX + x, currentY + y);
					if (linearIndex < 0) {
						System.err.printf("Bogus linear index %d for %d + %d, %d + %d\n", linearIndex, currentX, x, currentY, y);
						Thread.dumpStack();
						System.exit(1);
					}
					game_world[linearIndex] = currentBlockId + 1;
				}
			}
		}

	}

	public boolean gameOver() {
		return is_game_over;
	}

	/*
	 * This code applies the action, but doesn't do the default fall of 1 square
	 */
	public boolean take_action(int theAction) {
		// System.out.println(toString(false));
		if (theAction > 5 || theAction < 0) {
			System.err.println("Invalid action selected in take_action: " + theAction);
			// Random >=0 < 6
			theAction = randomGenerator.nextInt(6);
		}

		int nextRotation = currentRotation;
		int nextX = currentX;
		int nextY = currentY;

		switch (theAction) {
		case CW:
			nextRotation = (currentRotation + 1) % 4;
			break;
		case CCW:
			nextRotation = (currentRotation - 1);
			if (nextRotation < 0) {
				nextRotation = 3;
			}
			break;
		case LEFT:
			nextX = currentX - 1;
			break;
		case RIGHT:
			nextX = currentX + 1;
			break;
		case FALL:
			nextY = currentY;

			boolean isInBounds = true;
			boolean isColliding = false;

			// Fall until you hit something then back up once
			while (isInBounds && !isColliding) {
				nextY++;
				isInBounds = inBounds(nextX, nextY, nextRotation);
				if (isInBounds) {
					isColliding = colliding(nextX, nextY, nextRotation);
				}
			}
			nextY--;
			break;
		default:
			break;
		}
		boolean canMove = false;
		// Check if the resulting position is legal. If so, accept it.
		// Otherwise, don't change anything
		if (inBounds(nextX, nextY, nextRotation)) {
			if (!colliding(nextX, nextY, nextRotation)) {
				currentRotation = nextRotation;
				currentX = nextX;
				currentY = nextY;
				canMove = true;
			}
		}

		return canMove;

	}

	/**
	 * Number of game spaces that do not contain a block
	 * @return
	 */
	public int numEmptySpaces() {
		return ArrayUtil.countOccurrences(0, worldState);
	}
	
	/**
	 * Calculate the learn array position from (x,y) components based on
	 * worldWidth. Package level access so we can use it in tests.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int calculateLinearArrayPosition(int x, int y) {
		int returnValue = y * worldWidth + x;
		assert returnValue >= 0 : " " + y + " * " + worldWidth + " + " + x + " was less than 0.";
		return returnValue;
	}

	/**
	 * Check if any filled part of the 5x5 block array is either out of bounds
	 * or overlapping with something in wordState
	 *
	 * @param checkX
	 *            X location of the left side of the 5x5 block array
	 * @param checkY
	 *            Y location of the top of the 5x5 block array
	 * @param checkOrientation
	 *            Orientation of the block to check
	 * @return
	 */
	private boolean colliding(int checkX, int checkY, int checkOrientation) {
		int[][] thePiece = POSSIBLE_BLOCKS.get(currentBlockId).getShape(checkOrientation);
		try {

			for (int y = 0; y < thePiece[0].length; ++y) {
				for (int x = 0; x < thePiece.length; ++x) {
					if (thePiece[x][y] != 0) {
						// First check if a filled in piece of the block is out
						// of bounds!
						// if the height of this square is negative or the X of
						// this square is negative, then we're "colliding" with
						// the wall
						if (checkY + y < 0 || checkX + x < 0) {
							return true;
						}

						// if the height of this square is more than the board
						// size or the X of
						// this square is more than the board size, then we're
						// "colliding" with the wall
						if (checkY + y >= worldHeight || checkX + x >= worldWidth) {
							return true;
						}

						// Otherwise check if it hits another piece
						int linearArrayIndex = calculateLinearArrayPosition(checkX + x, checkY + y);
						if (worldState[linearArrayIndex] != 0) {
							return true;
						}
					}
				}
			}
			return false;

		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Error: ArrayIndexOutOfBoundsException in GameState::colliding called with params: "
					+ checkX + " , " + checkY + ", " + checkOrientation);
			System.err.println("Error: The Exception was: " + e);
			Thread.dumpStack();
			System.err.println("Returning true from colliding to help save from error");
			System.err.println("Setting is_game_over to true to hopefully help us to recover from this problem");
			is_game_over = true;
			return true;
		}
	}

	private boolean collidingCheckOnlySpotsInBounds(int checkX, int checkY, int checkOrientation) {
		int[][] thePiece = POSSIBLE_BLOCKS.get(currentBlockId).getShape(checkOrientation);
		try {

			for (int y = 0; y < thePiece[0].length; ++y) {
				for (int x = 0; x < thePiece.length; ++x) {
					if (thePiece[x][y] != 0) {

						// This checks to see if x and y are in bounds
						if ((checkX + x >= 0 && checkX + x < worldWidth && checkY + y >= 0
								&& checkY + y < worldHeight)) {
							// This array location is in bounds
							// Check if it hits another piece
							int linearArrayIndex = calculateLinearArrayPosition(checkX + x, checkY + y);
							if (worldState[linearArrayIndex] != 0) {
								return true;
							}
						}
					}
				}
			}
			return false;

		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(
					"Error: ArrayIndexOutOfBoundsException in GameState::collidingCheckOnlySpotsInBounds called with params: "
							+ checkX + " , " + checkY + ", " + checkOrientation);
			System.err.println("Error: The Exception was: " + e);
			Thread.dumpStack();
			System.err.println("Returning true from colliding to help save from error");
			System.err.println("Setting is_game_over to true to hopefully help us to recover from this problem");
			is_game_over = true;
			return true;
		}
	}

	/**
	 * This function checks every filled part of the 5x5 block array and sees if
	 * that piece is in bounds if the entire block is sitting at (checkX,checkY)
	 * on the board.
	 *
	 * @param checkX
	 *            X location of the left side of the 5x5 block array
	 * @param checkY
	 *            Y location of the top of the 5x5 block array
	 * @param checkOrientation
	 *            Orientation of the block to check
	 * @return
	 */
	private boolean inBounds(int checkX, int checkY, int checkOrientation) {
		try {
			int[][] thePiece = POSSIBLE_BLOCKS.get(currentBlockId).getShape(checkOrientation);

			for (int y = 0; y < thePiece[0].length; ++y) {
				for (int x = 0; x < thePiece.length; ++x) {
					if (thePiece[x][y] != 0) {
						// if ! (thisX is non-negative AND thisX is less than
						// width
						// AND thisY is non-negative AND thisY is less than
						// height)
						// Through demorgan's law is
						// if thisX is negative OR thisX is too big or
						// thisY is negative OR this Y is too big
						if (!(checkX + x >= 0 && checkX + x < worldWidth && checkY + y >= 0
								&& checkY + y < worldHeight)) {
							return false;
						}
					}
				}
			}

			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Error: ArrayIndexOutOfBoundsException in GameState::inBounds called with params: "
					+ checkX + " , " + checkY + ", " + checkOrientation);
			System.err.println("Error: The Exception was: " + e);
			Thread.dumpStack();
			System.err.println("Returning false from inBounds to help save from error.  Not sure if that's wise.");
			System.err.println("Setting is_game_over to true to hopefully help us to recover from this problem");
			is_game_over = true;
			return false;
		}

	}

	private boolean nextInBounds() {
		return inBounds(currentX, currentY + 1, currentRotation);
	}

	private boolean nextColliding() {
		return colliding(currentX, currentY + 1, currentRotation);
	}

	/*
	 * Ok, at this point, they've just taken their action. We now need to make
	 * them fall 1 spot, and check if the game is over, etc
	 */
	public void update() {
		// Sanity check. The game piece should always be in bounds.
		if (!inBounds(currentX, currentY, currentRotation)) {
			System.err.println("In GameState.Java the Current Position of the board is Out Of Bounds... Consistency Check Failed");
		}

		// Need to be careful here because can't check nextColliding if not in bounds

		// onSomething means we're basically done with this piece
		boolean onSomething = false;
		if (!nextInBounds()) {
			onSomething = true;
		}
		if (!onSomething) {
			if (nextColliding()) {
				onSomething = true;
			}
		}

		if (onSomething) {
			blockMobile = false;
			writeCurrentBlock(worldState);
			checkIfRowAndScore();
		} else {
			// fall
			currentY += 1;
		}

		if (CommonConstants.watch) {
			viewer.update(this);
		}
	}

	public void spawn_block() {
		blockMobile = true;

		currentBlockId = randomGenerator.nextInt(POSSIBLE_BLOCKS.size());

		currentRotation = 0;
		currentX = (worldWidth / 2) - 2;
		currentY = -4;

		// Colliding checks both bounds and piece/piece collisions. We really
		// only want the piece to be falling
		// If the filled parts of the 5x5 piece are out of bounds.. IE... we
		// want to stop falling when its all on the screen
		boolean hitOnWayIn = false;
		while (!inBounds(currentX, currentY, currentRotation)) {
			// We know its not in bounds, and we're bringing it in. Let's see if
			// it would have hit anything...
			hitOnWayIn = collidingCheckOnlySpotsInBounds(currentX, currentY, currentRotation);
			currentY++;
		}
		is_game_over = colliding(currentX, currentY, currentRotation) || hitOnWayIn;
		if (is_game_over) {
			blockMobile = false;
		}
	}

	void checkIfRowAndScore() {
		int numRowsCleared = 0;

		// Start at the bottom, work way up
		for (int y = worldHeight - 1; y >= 0; --y) {
			if (isRow(y)) {
				removeRow(y);
				numRowsCleared += 1;
				y += 1;
			}
		}

		// 1 line == 1
		// 2 lines == 2
		// 3 lines == 4
		// 4 lines == 8
		score += java.lang.Math.pow(2.0d, numRowsCleared - 1);
		linesCleared += numRowsCleared;
	}

	/**
	 * Check if a row has been completed at height y. Short circuits, returns
	 * false whenever we hit an unfilled spot.
	 *
	 * @param y
	 * @return
	 */
	boolean isRow(int y) {
		for (int x = 0; x < worldWidth; ++x) {
			int linearIndex = calculateLinearArrayPosition(x, y);
			if (worldState[linearIndex] == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Dec 13/07. Radkie + Tanner found 2 bugs here. Bug 1: Top row never gets
	 * updated when removing lower rows. So, if there are pieces in the top row,
	 * and we clear something, they will float there.
	 *
	 * @param y
	 */
	void removeRow(int y) {
		if (!isRow(y)) {
			System.err.println(
					"In GameState.java remove_row you have tried to remove a row which is not complete. Failed to remove row");
			return;
		}

		for (int x = 0; x < worldWidth; ++x) {
			int linearIndex = calculateLinearArrayPosition(x, y);
			worldState[linearIndex] = 0;
		}

		// Copy each row down one (except the top)
		for (int ty = y; ty > 0; --ty) {
			for (int x = 0; x < worldWidth; ++x) {
				int linearIndexTarget = calculateLinearArrayPosition(x, ty);
				int linearIndexSource = calculateLinearArrayPosition(x, ty - 1);
				worldState[linearIndexTarget] = worldState[linearIndexSource];
			}
		}

		// Clear the top row
		for (int x = 0; x < worldWidth; ++x) {
			int linearIndex = calculateLinearArrayPosition(x, 0);
			worldState[linearIndex] = 0;
		}

	}

	/**
	 * Game score (multiplies contributions for simultaneously cleared lines)
	 * @return Game score
	 */
	public int get_score() {
		return score;
	}
	
	/**
	 * total number of lines cleared for current evaluation
	 * @return total number of lines cleared
	 */
	public int get_linesCleared() {
		return linesCleared;
	}
	
	/**
	 * Game board width
	 * @return
	 */
	public int getWidth() {
		return worldWidth;
	}

	/**
	 * Game board height
	 * @return
	 */
	public int getHeight() {
		return worldHeight;
	}

	// Schrum: What is this method?
	public int[] getNumberedStateSnapShot() {
		int[] numberedStateCopy = new int[worldState.length];
		for (int i = 0; i < worldState.length; i++) {
			numberedStateCopy[i] = worldState[i];
		}
		writeCurrentBlock(numberedStateCopy);
		return numberedStateCopy;

	}

	/**
	 * ID of currently falling Tetris piece
	 * @return falling piece id
	 */
	public int getCurrentPiece() {
		return currentBlockId;
	}

	/**
	 * Utility method for debugging
	 */
	public void printState() {
		for (int i = 0; i < worldHeight - 1; i++) {
			for (int j = 0; j < worldWidth; j++) {
				System.out.print(worldState[i * worldWidth + j]);
			}
			System.out.print("\n");
		}
		System.out.println("-------------");

	}

	/**
	 * Return the random number generator used
	 * @return
	 */
	public Random getRandom() {
		return randomGenerator;
	}

	/* End of Tetris Helper Functions */
	
	/**
	 * Copy constructor
	 * @param stateToCopy
	 */
	public TetrisState(TetrisState stateToCopy) {
		this.blockMobile = stateToCopy.blockMobile;
		this.currentBlockId = stateToCopy.currentBlockId;
		this.currentRotation = stateToCopy.currentRotation;
		this.currentX = stateToCopy.currentX;
		this.currentY = stateToCopy.currentY;
		this.score = stateToCopy.score;
		this.linesCleared = stateToCopy.linesCleared;
		this.is_game_over = stateToCopy.is_game_over;

		this.worldState = new int[stateToCopy.worldState.length];
		for (int i = 0; i < this.worldState.length; i++) {
			this.worldState[i] = stateToCopy.worldState[i];
		}
	}

	/**
	 * Produce a String form a provided world state array
	 * @param worldState 1D array representing the 2D game grid
	 * @return String representation of game board
	 */
	public static String toString(int[] worldState) {
		String result = "";
		for (int y = 0; y < worldHeight; y++) {
			for (int x = 0; x < worldWidth; x++) {
				result += (worldState[y * worldWidth + x]) > 0 ? 1 : 0;
			}
			result += ("\n");
		}
		result += ("-------------");
		return result;		
	}
	
	/**
	 * String method that shows falling piece by default
	 */
	@Override
	public String toString() {
		return toString(true);
	}

	/**
	 * String representation that may or may not include the currently falling piece
	 * @param showFallingPiece Whether falling piece is part of representation
	 * @return String representation of board
	 */
	public String toString(boolean showFallingPiece) {
		int[][] thePiece = POSSIBLE_BLOCKS.get(currentBlockId).getShape(this.currentRotation);
		String result = "";
		for (int y = 0; y < worldHeight; y++) {
			for (int x = 0; x < worldWidth; x++) {
				// if(currentY <= y && y < currentY + thePiece[0].length)
				// System.out.println("y " + y);
				// if(currentX <= x && x < currentX + thePiece.length)
				// System.out.println("x " + x);
				if (showFallingPiece && (currentY <= y && y < currentY + thePiece[0].length)
						&& (currentX <= x && x < currentX + thePiece.length)) {
					result += (int) Math.max(2 * thePiece[x - currentX][y - currentY],
							(worldState[y * worldWidth + x]));
				} else {
					result += (worldState[y * worldWidth + x]) > 0 ? 1 : 0;
				}
			}
			result += ("\n");
		}
		result += ("-------------");
		return result;
	}

	/**
	 * Auto-gen by Eclipse
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (blockMobile ? 1231 : 1237);
		result = prime * result + currentBlockId;
		result = prime * result + currentRotation;
		result = prime * result + currentX;
		result = prime * result + currentY;
		result = prime * result + (is_game_over ? 1231 : 1237);
		result = prime * result + score;
		result = prime * result + Arrays.hashCode(worldState);
		return result;
	}

	/**
	 * Auto-gen by Eclipse
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TetrisState other = (TetrisState) obj;
		if (blockMobile != other.blockMobile)
			return false;
		if (currentBlockId != other.currentBlockId)
			return false;
		if (currentRotation != other.currentRotation)
			return false;
		if (currentX != other.currentX)
			return false;
		if (currentY != other.currentY)
			return false;
		if (is_game_over != other.is_game_over)
			return false;
		if (score != other.score)
			return false;
		if (!Arrays.equals(worldState, other.worldState))
			return false;
		return true;
	}
}
