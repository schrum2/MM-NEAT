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

import java.net.URL;
import java.util.ArrayList;

import org.rlcommunity.environments.tetris.messages.TetrisStateResponse;
import org.rlcommunity.environments.tetris.visualizer.TetrisVisualizer;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

import edu.southwestern.tasks.rlglue.RLGlueEnvironment;
import edu.southwestern.tasks.rlglue.featureextractors.tetris.BertsekasTsitsiklisTetrisExtractor;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.HasImageInterface;

public class Tetris extends RLGlueEnvironment implements HasAVisualizerInterface, HasImageInterface {

	/**
	 * Initialize the scores to 0 and game state to null
	 */
	private int currentScore = 0;
	private int linesCleared = 0;
	protected TetrisState gameState = null;
	static final int terminalScore = 0;
	private int rowsOf1 = 0;
	private int rowsOf2 = 0;
	private int rowsOf3 = 0;
	private int rowsOf4 = 0;	
	private double averageNumEmptyBlocks = 0;
	private double averageNumHoles = 0;
	private int numBlockPlacements = 0;

	/**
	 * This Tetris method calls getDefaultParameters so it can initialize with them
	 */
	public Tetris() {
		this(getDefaultParameters());
	}

	/**
	 * This Tetris method calls super, so that it can get the default parameters
	 * and use them. It then starts the game state.
	 * 
	 * @param p contains parameters
	 */
	public Tetris(ParameterHolder p) {
		super();
		gameState = new TetrisState();
	}

	/**
	 * Tetris doesn't really have any parameters
	 *
	 * @return empty parameter holder
	 */
	public static ParameterHolder getDefaultParameters() {
		ParameterHolder p = new ParameterHolder();
		rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider()); 
		// no doc to check what this does?
		return p;
	}

	/**
	 * Using the parameters, this method get the task spec payload for the
	 * current game world.
	 * 
	 * @param parameter holder
	 * @return task spec payload
	 */
	public static TaskSpecPayload getTaskSpecPayload(ParameterHolder p) {
		Tetris theWorld = new Tetris(p);
		String taskSpec = theWorld.makeTaskSpec().getStringRepresentation();
		return new TaskSpecPayload(taskSpec, false, "");
	}

	/* Base RL-Glue Functions */
	/**
	 *  creates initial environment
	 */
	@Override
	public String env_init() {
		return makeTaskSpec().getStringRepresentation();
	}

	/**
	 * Starts the game environment and returns the initial game environment
	 * 
	 * @return Observation of game state
	 */
	@Override
	public Observation env_start() {
		gameState.reset();
		gameState.spawn_block();
		gameState.blockMobile = true;
		currentScore = 0;
		linesCleared = 0;
		rowsOf1 = 0;
		rowsOf2 = 0;
		rowsOf3 = 0;
		rowsOf4 = 0;	
		averageNumEmptyBlocks = 0;
		//averageNumHoles = 0;
		numBlockPlacements = 0;

		Observation o = gameState.get_observation();
		return o;
	}

	/**
	 * Environment step that checks for moves that won't work and gives the
	 * appropriate errors. Then, checks for a moving block, continues to the
	 * next movement or adds the next block. It also connects reward with the
	 * game state, checking for game overs and the current score
	 * @param actionObject action to perform
	 * @return reward and observation details
	 */
	@Override
	public Reward_observation_terminal env_step(Action actionObject) {
		int theAction = 0;
		try {
			theAction = actionObject.intArray[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Error: Action was expected to have 1 dimension but got ArrayIndexOutOfBoundsException when trying to get element 0:" + e);
			System.err.println("Error: Choosing action 0");
			theAction = 0;
		}

		// Defined in TetrisState, 0 represents the first available action, 5
		// represents that last available actions
		if (theAction > 5 || theAction < 0) {
			System.err.println("Invalid action selected in Tetrlais: " + theAction);
			theAction = gameState.getRandom().nextInt(5);
		}

		// If the block is still mobile, then take the next action and update,
		// otherwise spawn in a new block
		if (gameState.blockMobile) { 
			gameState.take_action(theAction);
			gameState.update();
		} else {
			// Track average number of empty blocks after each block placement
			double numEmpty = gameState.numEmptySpaces();
			double numHoles = BertsekasTsitsiklisTetrisExtractor.totalHoles(TetrisState.worldWidth, TetrisState.worldHeight, gameState.worldState);
			// Incremental average calculation
			numBlockPlacements++;
			averageNumHoles += (numHoles - averageNumHoles)/(numBlockPlacements);
			///System.out.println("average num holes:" + averageNumHoles);
			averageNumEmptyBlocks += (numEmpty - averageNumEmptyBlocks) / (numBlockPlacements);
			gameState.spawn_block();
		}

		Reward_observation_terminal ro = new Reward_observation_terminal();

		ro.terminal = 1;
		ro.o = gameState.get_observation();

		// 1 represents "last state" and 0 means "keep playing"
		// If game is not over, reward is new score - current score
		// otherwise, reward is zero
		if (!gameState.gameOver()) {
			ro.terminal = 0;
			ro.r = gameState.get_score() - currentScore;
			if(ro.r == 1){
				rowsOf1++;
				linesCleared += 1;
			} else if(ro.r == 2){
				rowsOf2++;
				linesCleared += 2;
			} else if(ro.r == 4){
				rowsOf3++;
				linesCleared += 3;
			} else if(ro.r == 8){
				rowsOf4++;
				linesCleared += 4;
			}
			currentScore = gameState.get_score();
		} else {
			ro.r = Tetris.terminalScore;
			currentScore = 0;
		}

		return ro;
	}

	/**
	 * At end of game, the array will contain the number of times
	 * that 1, 2, 3, and 4 rows were cleared simultaneously.
	 * Used for "other score" tracking.
	 * 
	 * @return Array of numbers of simultaneous row clearings for 1-4 rows
	 */
	public double[] getNumberOfRowsCleared(){
		double[] rows = {rowsOf1, rowsOf2, rowsOf3, rowsOf4};
		return rows;
	}

	/**
	 * Average number of game spaces that do not contain any blocks
	 * across all states in which a block was just placed (afterstates)
	 * @return The average number of empty spaces across all afterstates
	 */
	public double getAverageNumEmptySpaces() {
		return averageNumEmptyBlocks;
	}

	/**
	 * Average number of holes across all states in which a block was just placed (afterstates)
	 * @return The average number of holes across all afterstates
	 */
	public double getAverageNumHoles() {
		return averageNumHoles;
	}

	/**
	 * Cleans the environment
	 */
	@Override
	public void env_cleanup() {
		//nothing here
	}

	/**
	 * This method returns a string of a message responding to a given string
	 * parameter
	 * 
	 * @param theMessage
	 * @return string of the message
	 */
	@Override
	public String env_message(String theMessage) {
		EnvironmentMessages theMessageObject;
		try {
			theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
		} catch (Exception e) {
			System.err.println("Someone sent Tetris a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}

		if (theMessageObject.canHandleAutomatically(this)) {
			return theMessageObject.handleAutomatically(this);
		}

		if (theMessageObject.getTheMessageType() == rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()) {

			String theCustomType = theMessageObject.getPayLoad();

			if (theCustomType.equals("GETTETRLAISSTATE")) {
				// It is a request for the state
				TetrisStateResponse theResponseObject = new TetrisStateResponse(currentScore, gameState.getWidth(),
						gameState.getHeight(), gameState.getNumberedStateSnapShot(), gameState.getCurrentPiece());
				return theResponseObject.makeStringResponse();
			}
			System.out.println("We need some code written in Env Message for Tetrlais.. unknown custom message type received");
			Thread.dumpStack();

			return null;
		}

		System.out.println("We need some code written in Env Message for  Tetrlais!");
		Thread.dumpStack();

		return null;
	}

	/* End of Base RL-Glue Functions */
	/* RL-Viz Methods */
	// Note: RL-Viz is also incomplete according to the Google Archive, it is
	// used to communicated between languages on top of RL-Glue
	/**
	 * Getter for the observation of the current game state
	 * @return state observation
	 */
	@Override
	protected Observation makeObservation() {
		return gameState.get_observation();
	}

	/**
	 * Getter for the name of the visualizer class
	 * @return name of class
	 */
	@Override
	public String getVisualizerClassName() {
		return TetrisVisualizer.class.getName();
	}

	/**
	 * Doesn't seem to be used ... file does not exist.
	 * @return dead link
	 */
	@Override
	public URL getImageURL() {
		URL imageURL = Tetris.class.getResource("/images/tetris/tetris.png");
		return imageURL;
	}

	/**
	 * Method returns a task spec that reflects the current game state
	 * 
	 * @return provides task details
	 */
	@Override
	public TaskSpec makeTaskSpec() {
		int boardSize = gameState.getHeight() * gameState.getWidth();
		int numPieces = TetrisState.POSSIBLE_BLOCKS.size();

		TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
		theTaskSpecObject.setEpisodic();
		theTaskSpecObject.setDiscountFactor(1.0d);
		// First add the binary variables for the board
		theTaskSpecObject.addDiscreteObservation(new IntRange(0, 1, boardSize));
		// Now the binary features to tell what piece is falling
		theTaskSpecObject.addDiscreteObservation(new IntRange(0, 1, numPieces));
		// Now the actual board size in the observation. The reason this was
		// here is/was because there was no way to add meta-data to the task 
		// spec before. First height
		theTaskSpecObject.addDiscreteObservation(new IntRange(gameState.getHeight(), gameState.getHeight()));
		// Then width
		theTaskSpecObject.addDiscreteObservation(new IntRange(gameState.getWidth(), gameState.getWidth()));

		theTaskSpecObject.addDiscreteAction(new IntRange(0, 5));
		// This is actually a lie... the rewards aren't in that range.
		theTaskSpecObject.setRewardRange(new DoubleRange(0, 8.0d));

		// This is a better way to tell the rows and cols
		theTaskSpecObject.setExtra("EnvName:Tetris HEIGHT:" + gameState.getHeight() + " WIDTH:" + gameState.getWidth()
		+ " Revision: " + this.getClass().getPackage().getImplementationVersion());

		String taskSpecString = theTaskSpecObject.toTaskSpec();

		TaskSpec.checkTaskSpec(taskSpecString);
		// return taskSpecString;

		//Can this be deleted?
		TaskSpec.checkTaskSpec(taskSpecString);

		return new TaskSpec(theTaskSpecObject);
	}

	public static void main(String[] args) {
		EnvironmentLoader L = new EnvironmentLoader(new Tetris());
		L.run();
	}

	/**
	 * Array list, or vector, to hold the current world state.s
	 * @return vector representation of entire game grid
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		ArrayList<Double> result = new ArrayList<Double>(gameState.worldState.length);
		TetrisState state = gameState;
		//if (Parameters.parameters.booleanParameter("tetrisLinesBDCharecterization")) {
			
		//} else {
			for (Integer b : state.worldState) {
				result.add(b * 1.0);
			}
		//}
		return result;
	}

	/**
	 * Schrum: added.
	 * 
	 * Number of lines cleared (distinct from score)
	 * @return raw number of lines cleared
	 */
	public int getLinesCleared() {
		return linesCleared;
	}
}

/**
 * Details about the authors and the sources
 * 
 * @author gonzale9
 *
 */
class DetailsProvider implements hasVersionDetails {

	@Override
	public String getName() {
		return "Tetris 1.1";
	}

	@Override
	public String getShortName() {
		return "Tetris";
	}

	@Override
	public String getAuthors() {
		return "Brian Tanner, Leah Hackman, Matt Radkie, Andrew Butcher";
	}

	@Override
	public String getInfoUrl() {
		//dead link
		return "http://library.rl-community.org/tetris";
	}

	@Override
	public String getDescription() {
		return "Tetris problem from the reinforcement learning library.";
	}
}
