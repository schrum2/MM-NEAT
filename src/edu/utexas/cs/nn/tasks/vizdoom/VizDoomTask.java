package edu.utexas.cs.nn.tasks.vizdoom;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.GraphicsUtil;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.Statistic;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import vizdoom.DoomGame;
import vizdoom.GameState;
import vizdoom.Mode;
import vizdoom.ScreenFormat;
import vizdoom.ScreenResolution;
import vizdoom.SpecifyDLL;

/**
 *
 * Parent class for all VizDoom domains
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Phenotype being evolved
 */
public abstract class VizDoomTask<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {

	// For each pixel in the image buffer, the colors are sorted in this order
	public static final int RED_INDEX = 2;
	public static final int GREEN_INDEX = 1;
	public static final int BLUE_INDEX = 0;
	public static final int NUM_COLORS = 3;
	public static final double MAX_COLOR = 255;
	public static DrawingPanel dp = null;
	public static Statistic stat;

	public DoomGame game;
	public List<int[]> actions;
	public List<String> actionLabels;
	protected ScreenResolution designatedResolution;

	public VizDoomTask() {
		// These should not be here ... put in an init call?
		doomInit();
		taskSpecificInit();
		setRendering();
		actionLabels = new ArrayList<String>();
		actions = new ArrayList<int[]>();
		setDoomActions();
		setDoomStateVariables();
		setRewards();
		setDoomMiscSettings();
		MMNEAT.registerFitnessFunction("DoomReward");
		try {
			stat = (Statistic) ClassCreation.createObject("doomSmudgeStat");
		} catch (NoSuchMethodException e) {
			System.out.println("Could not determine Smudge Statistic");
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void prep() {
		// Initialize the game. Further configuration won't 
		// take any effect from now on.
		game.init();
	}

	public abstract void taskSpecificInit();

	public final void doomInit() {
		// My trick for loading the vizdoom.dll library
		SpecifyDLL.specifyDLLPath();
		// Create DoomGame instance. 
		// It will run the game and communicate with you.
		game = new DoomGame();
		// Sets path to vizdoom engine executive which will be spawned as a
		// separate process. Use the version without sound.
		game.setViZDoomPath("vizdoom/bin/vizdoom");
		// Sets path to doom2 iwad resource file which 
		// contains the actual doom game
		game.setDoomGamePath("vizdoom/scenarios/" + Parameters.parameters.stringParameter("gameWad"));
	}

	public final void setRendering() {
		// TODO: Should be be able to set this from the command line somehow?
		setRestrictedScreenResolution(ScreenResolution.RES_200X150); // smallest possible
		// Sets the screen buffer format. 
		game.setScreenFormat(ScreenFormat.RGB24);
		// Sets other rendering options
		game.setRenderHud(false);
		game.setRenderCrosshair(false);
		game.setRenderWeapon(true);
		game.setRenderDecals(false);
		game.setRenderParticles(false);
	}

	/**
	 * We came across an issue with higher screen resolutions and using the
	 * drawGameState(), drawGameStateRow() and getRow() methods The higher the
	 * resolution, the better the chance the width and height are incorrect,
	 * thus making the calculations incorrect for inputs and display
	 * 
	 * @param res
	 *            ScreenResolution
	 */
	private void setRestrictedScreenResolution(ScreenResolution res) {
		assert!res.equals(ScreenResolution.RES_800X600) : "800X600 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1024X768) : "1024X768 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1280X960) : "1280X960 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1400X1050) : "1400X1050 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1600X1200) : "1600X1200 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_800X500) : "800X500 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1024X640) : "1024X640 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1280X800) : "1280X800 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1400X875) : "1400X875 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1600X1000) : "1600X1000 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_800X450) : "800X450 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1024X576) : "1024X576 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1280X720) : "1280X720 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1400X787) : "1400X787 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1600X900) : "1600X900 is too high of a resolution!";
		assert!res.equals(ScreenResolution.RES_1920X1080) : "1920X1080 is too high of a resolution!";
		designatedResolution = res;
		game.setScreenResolution(res);
	}

	/**
	 * Add new action to the list of possible actions the Doom agent can perform
	 * 
	 * @param buttonPresses
	 *            The combination of buttons being pressed
	 * @param label
	 *            display label for the action
	 */
	public final void addAction(int[] buttonPresses, String label) {
		actionLabels.add(label);
		actions.add(buttonPresses);
	}

	public abstract void setDoomActions();

	public abstract void setDoomStateVariables();

	public final void setDoomMiscSettings() {
		// Causes episodes to finish after designated tics (actions)
		game.setEpisodeTimeout(Parameters.parameters.integerParameter("doomEpisodeLength"));

		game.setWindowVisible(CommonConstants.watch);
		// Sets ViZDoom mode (PLAYER, ASYNC_PLAYER, SPECTATOR, ASYNC_SPECTATOR,
		// PLAYER mode is default). Not really sure what the distinctions are
		game.setMode(Mode.PLAYER);
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// Start new trial from scratch
		game.newEpisode();
		Network n = individual.getPhenotype();
		while (!game.isEpisodeFinished()) {
			// Get the state
			GameState s = game.getState();

			// Trouble shooting code
			//System.out.println(s.imageBuffer.length);
			//System.out.println(game.getScreenWidth());
			//System.out.println(game.getScreenHeight());
			// drawGameState(s, game.getScreenWidth(), game.getScreenHeight());
			//drawGameStateRow(s, game.getScreenWidth(), game.getScreenHeight(), getRow());

			double[] inputs = getInputs(s); // This is already scaled and smudged
			//showInputs(s, inputs); //use this to look at the inputs that the agent is seeing
			double[] outputs = n.process(inputs);
			// This now takes the arg max of the action outputs
			//double r = game.makeAction(actions.get(StatisticsUtilities.argmax(outputs))); 
			game.makeAction(actions.get(StatisticsUtilities.argmax(outputs))); 
			// This r seems worthless ... does it give any information?
			// My hunch is that it picks the action, but I don't think we have to do anything with it? Make action returns a double for some reason.
			// I'll take out the r for now -Gab
			if(Parameters.parameters.booleanParameter("stepByStep")){
				 MiscUtil.waitForReadStringAndEnterKeyPress();	
			}
		}
		//if(CommonConstants.watch){
		//	System.out.print("Press enter to continue");
		//	MiscUtil.waitForReadStringAndEnterKeyPress();
		//}
		
		// TODO: Make this reward calculation more general, allow for multiple objectives
		return new Pair<double[], double[]>(new double[] { game.getTotalReward() }, new double[] {});
	}

	/**
	 * Given the game state, return an array of doubles that the learning agent
	 * will use to make a decision.
	 * 
	 * @param s
	 *            Class containing all information about the game state
	 * @return Array of sensor values/features
	 */
	public abstract double[] getInputs(GameState s);

	/**
	 * Sets all the rewards for the given game and agent
	 */
	public abstract void setRewards(); // TODO: Should be able to generalize this to set up multiple objectives

	/**
	 * Is run at the conclusion of all evolution. Terminates the DoomGame
	 * instance.
	 */
	@Override
	public void finalCleanup() {
		game.close();
	}

	/**
	 * Just the reward. Will probably override this at some point
	 * 
	 * @return Number of objectives
	 */
	@Override
	public int numObjectives() {
		// TODO: Generalize to allow for multiple objectives
		return 1;
	}

	/**
	 * Number of available actions. Generally matches the number of policy
	 * outputs.
	 * 
	 * @return Number of actions
	 */
	public int numActions() {
		return actions.size();
	}

	public abstract int numInputs();

	@Override
	public double getTimeStamp() {
		return game.getEpisodeTime(); // Confirm that this works
	}

	@Override
	public String[] outputLabels() {
		// Derive from actionLabels
		return actionLabels.toArray(new String[actionLabels.size()]);
	}

	/**
	 * This method outputs the Gamestate according to the width and height given
	 * You may change which of the RGB values appear as well, currently set to
	 * all red values.
	 * 
	 * This is primarily a utility method for troubleshooting purposes.
	 * 
	 * @param s
	 *            Game state
	 * @param width
	 *            screen width
	 * @param height
	 *            screen height
	 */
	@SuppressWarnings("unused")
	public static void drawGameState(GameState s, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int bufferPos = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = bufferPos + RED_INDEX;
				int g = bufferPos + GREEN_INDEX;
				int b = bufferPos + BLUE_INDEX;
				// Actual screen
				int rgb = new Color(s.imageBuffer[r], s.imageBuffer[g], s.imageBuffer[b]).getRGB();
				// Just red intensity
				// int rgb = new Color(s.imageBuffer[r], s.imageBuffer[r], s.imageBuffer[r]).getRGB();
				image.setRGB(x, y, rgb);
				bufferPos += 3;
			}
		}
		DrawingPanel dp = GraphicsUtil.drawImage(image, "Doom", width, height);
		MiscUtil.waitForReadStringAndEnterKeyPress();
	}
	
	public static void showInputs(GameState s, double[] inputs) {
		int height = Parameters.parameters.integerParameter("doomInputHeight");
		int width = Parameters.parameters.integerParameter("doomInputWidth");
		int smudge = Parameters.parameters.integerParameter("doomInputPixelSmudge");
		int reducedHeight = height / smudge;
		int reducedWidth = width / smudge;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// Scaling the color values
		int[] inputsScaled = new int[inputs.length];
		for (int i = 0; i < inputs.length; i++){
			inputsScaled[i] = (int) (inputs[i] * MAX_COLOR);
		}
		
		// Check for colors used
		int color = Parameters.parameters.integerParameter("doomInputColorVal");
		int rOffset = 0;
		int gOffset = 0;
		int bOffset = 0;
		if(color == 3){
			//we use all colors
			bOffset = BLUE_INDEX*reducedWidth*reducedHeight;
			rOffset = RED_INDEX*reducedWidth*reducedHeight;
			gOffset = GREEN_INDEX*reducedWidth*reducedHeight;
		} 
		
		
		for (int y = 0; y < reducedHeight; y++) {
			for (int x = 0; x < reducedWidth; x++) {
				int linearPosition = (y * reducedWidth) + x;
				int rgb = new Color(inputsScaled[rOffset + linearPosition],
									inputsScaled[gOffset + linearPosition], 
									inputsScaled[bOffset + linearPosition]).getRGB();					
				for(int i = 0; i < smudge; i++){
					for(int j = 0; j < smudge; j ++){
						image.setRGB((x*smudge)+j, (y*smudge)+i, rgb);
						//System.out.println("Adding [" + x + ", " + y + "] to ["  + ((x*smudge)+j) + ", " + ((y*smudge)+i) + "]");
					}
				}
			}
		}
		
		String name = "Inputs (" + (color == NUM_COLORS ? "All Colors)" : (color == RED_INDEX ? "Red)" : 
			(color == GREEN_INDEX ? "Green)" : "Blue)")));
		
		if(dp == null){
			dp = GraphicsUtil.drawImage(image, name, width, height);
		} else {
			dp.getGraphics().drawRenderedImage(image, null);
		}
	}

	/**
	 * According to the given [x, y] and width by height, this method pulls
	 * the entries from the image buffer to be inputs
	 * 
	 * @param s, GameState to pull from
	 * @param x, the starting x position
	 * @param y, the starting y position
	 * @param width, for the selected area
	 * @param height, for the selected area
	 * @param color, 0 -> blue, 1 -> green, 2 -> red, 3 -> all, any other number will default to red*
	 * @return inputs int array
	 */
	public double[] getInputs(GameState s, int x, int y, int width, int height, int color){
		//System.out.println("Getting inputs for [" + x + ", " + y + "] where width is " + width + " and height is " + height);
		if(color < BLUE_INDEX || color > RED_INDEX + 1){
			color = RED_INDEX; // set to red if not specified correctly
		}
		if(color == NUM_COLORS){ // if we are using all colors
			double[] inputs = new double[width*height*NUM_COLORS];
			for(int i = 0; i < NUM_COLORS; i++){
				double[] singleColor = colorInputs(s, x, y, width, height, i, game.getScreenWidth());
				System.arraycopy(singleColor, 0, inputs, i * singleColor.length, singleColor.length);
			}
			return inputs;
		} else {
			return colorInputs(s, x, y, width, height, color, game.getScreenWidth());
		}
	}
	
	/**
	 * Helper method for getInputs(), returns double[] linear array of 
	 * screen section for a specified color
	 * 
	 * @param s, GameState to pull from
	 * @param x, the starting x position
	 * @param y, the starting y position
	 * @param width, for the selected area
	 * @param height, for the selected area
	 * @param color, 0 -> blue, 1 -> green, 2 -> red, 3 -> all, any other number will default to red*
	 * @param screenWidth, the width of the full screen
	 * @return double[] inputs for 1 color
	 */
	public static double[] colorInputs(GameState s, int x, int y, int width, int height, int color, int screenWidth){
		double[] inputs = new double[width * height];
		//System.out.println("Input array size is " + inputs.length + " and color is " + (color == RED_INDEX? "Red": (color == GREEN_INDEX? "Green" : "Blue")));
		int pos = 0;
		for(int i = y; i < y + height; i++){
			for(int j = x; j < x + width; j++){
				//System.out.print("Adding Buffer[" + (color + (NUM_COLORS * ((i * screenWidth) + j))) + "](" + ((s.imageBuffer[color + (NUM_COLORS * ((i * screenWidth) + j))]) / MAX_COLOR) + ") to Inputs[" + pos + "]");
				inputs[pos++] = ((s.imageBuffer[color + (NUM_COLORS * ((i * screenWidth) + j))]) / MAX_COLOR); 
				//System.out.println(" for coordinate [" + j + ", " + i + "] for color " + (color == RED_INDEX ? "Red" : (color == GREEN_INDEX ? "Green" : "Blue")));
			}
		}
		return inputs;
	}
	
	
	/**
	 * Gives the labels for the inputs according to the information 
	 * used to get those inputs (getInputs())
	 * 
	 * @param x, the starting x position
	 * @param y, the starting y position
	 * @param width, for the selected area
	 * @param height, for the selected area
	 * @param color, 0 -> blue, 1 -> green, 2 -> red, 3 -> all, any other number will default to red*
	 * @return String[] of labels for inputs
	 */
	public static String[] getSensorLabels(int x, int y, int width, int height, int color) {
		int cStart = 0; // always start loop with 0
		int cEnd = (color == NUM_COLORS? NUM_COLORS : 1); // if color is 3, set the end as total number of colors, otherwise just 1 
		int colorIndex = (color == NUM_COLORS? BLUE_INDEX : color); // set index of starting color, or only color
		int size = (color == NUM_COLORS? NUM_COLORS : 1); // if color is 3, set to use all colors in finished array, otherwise just 1
		
		String[] labels = new String[width * height * size];
		for(int c = cStart; c < cEnd; c++){ // loops either 3 times or once
			for (int i = y; i < height; i++) {
				for (int j = x; j < width; j++) {
					labels[j + (i*width) + (c*width*height)] = "Position " + j + ", " + i + " at " + (colorIndex == RED_INDEX ? "Red" : (colorIndex == GREEN_INDEX ? "Green" : "Blue"));
				}
			}
		}
		return labels;
	}
	
	/**
	 * Given a rate of smudging, this method takes pixels and averages them in order
	 * to lower the amount of inputs being used for the screen.
	 * (This is similar to lowering resolution)
	 * @param inputs
	 * @param width
	 * @param height
	 * @param color
	 * @param smudge
	 * @return
	 */
	public static double[] smudgeInputs(double[] inputs, int width, int height, int color, int smudge){
		//System.out.println("Smudging inputs at a rate of " + smudge + " where width: " + width + " and height: " + height);
		int reducedWidth = width / smudge;
		int reducedHeight = height / smudge;
		
		if(color == NUM_COLORS){
			double[] result = new double[reducedWidth * reducedHeight * NUM_COLORS];
			for(int i = 0; i < NUM_COLORS; i++){
				double[] inputPortion = Arrays.copyOfRange(inputs, i*width*height, (i+1)*width*height);
				double[] oneColor = smudgeColor(inputPortion, width, reducedWidth, reducedHeight, color, smudge);
				System.arraycopy(oneColor, 0, result, i*oneColor.length, oneColor.length);
			}
			return result;
		} else {
			return smudgeColor(inputs, width, reducedWidth, reducedHeight, color, smudge);
		}
	}
	
	/**
	 * Smudges the inputs according to a rate and specific color given
	 * @param inputs
	 * @param width
	 * @param reducedWidth
	 * @param reducedHeight
	 * @param color
	 * @param smudge
	 * @return
	 */
	public static double[] smudgeColor(double[] inputs, int width, int reducedWidth, int reducedHeight, int color, int smudge){
		//System.out.println("Smudging for color " + (color == RED_INDEX? "Red": (color == GREEN_INDEX? "Green" : "Blue")) + " at a reduced width: " + reducedWidth + " and height: " + reducedHeight);
		double[] smudgedInputs = new double[reducedWidth*reducedHeight];
		int pos = 0;
		for(int i = 0; i < reducedHeight; i++){
			for(int j = 0; j < reducedWidth; j++){
				double[] values = new double[smudge*smudge];
				int localIndex = 0;
				//System.out.print("Sum: ");
				for(int y = 0; y < smudge; y++){
					for(int x = 0; x < smudge; x++){
						values[localIndex++] = inputs[(((i * smudge) + y) * width) + ((j * smudge) + x)];
						//System.out.print("[" + ((j * smudge) + x) + ", "+ ((i * smudge) + y) + "], ");
					}
				}
				//System.out.println("to SmudgedInputs[" + pos + "]");
				smudgedInputs[pos++] = stat.stat(values);
				//System.out.println(" for new coordinate [" + j + ", " + i + "] = " + (smudgedInputs[pos-1]));

			}
		}
		return smudgedInputs;
	}
	
}
