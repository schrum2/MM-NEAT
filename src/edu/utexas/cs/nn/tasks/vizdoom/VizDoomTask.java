package edu.utexas.cs.nn.tasks.vizdoom;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.GraphicsUtil;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
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
	public static final double MAX_COLOR = 255;

	public DoomGame game;
	public List<int[]> actions;
	public List<String> actionLabels;
	protected ScreenResolution designatedResolution;

	public VizDoomTask() {
		// These should not be here ... put in an init call?
		doomInit();
		actionLabels = new ArrayList<String>();
		actions = new ArrayList<int[]>();
		setDoomActions();
		setDoomStateVariables();
		setRewards();
		setDoomMiscSettings();
		MMNEAT.registerFitnessFunction("DoomReward");
	}

	@Override
	public void prep() {
		// Initialize the game. Further configuration won't 
		// take any effect from now on.
		game.init();
	}

	public void doomInit() {
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

		// Sets path to additional resources iwad file which is basically your
		// scenario iwad.
		// If not specified default doom2 maps will be used and it's pretty much
		// useles... unless you want to play doom.
                // TODO: Completely remove this and the associated parameter. Move this to each specific child class
		game.setDoomScenarioPath("vizdoom/scenarios/" + Parameters.parameters.stringParameter("scenarioWad"));
		// Set map to start (scenario .wad files can contain many maps).
                // TODO: Completely remove this and the associated parameter. Move this to each specific child class
		game.setDoomMap(Parameters.parameters.stringParameter("doomMap"));
		// Sets resolution. Default is 320X240
		// TODO: Should be be able to set this from the command line somehow?
		setRestrictedScreenResolution(ScreenResolution.RES_200X150);
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

	public void setDoomMiscSettings() {
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

			double[] inputs = getInputs(s);
			double[] outputs = n.process(inputs);
			// This now takes the arg max of the action outputs
			double r = game.makeAction(actions.get(StatisticsUtilities.argmax(outputs))); 
			// This r seems worthless ... does it give any information?
			// MiscUtil.waitForReadStringAndEnterKeyPress();
		}
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
	 * Get scaled intensity values for a specific color from a specific
	 * row of the image of the current game state.
	 * 
	 * @param s game state
	 * @param row row on screen
	 * @param colorIndex RED_INDEX, GREEN_INDEX, or BLUE_INDEX
	 * @return scaled intensity values in specified color for specified row
	 */
	public double[] colorFromRow(GameState s, int row, int colorIndex) {
		int width = game.getScreenWidth();
		double[] result = new double[width];
		int index = row * width * 3; // 3 is for the three different color components: RGB
		for (int x = 0; x < width; x++) {
			int c = index + (3 * x) + colorIndex;
			result[x] = (s.imageBuffer[c]) / MAX_COLOR;
		}
		return result;
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

	/**
	 * This method outputs the given row stretched across the height You may
	 * change which of the RGB values appear as well, currently set to all red
	 * values.
	 * 
	 * This is another utility method.
	 * 
	 * @param s
	 * @param width
	 * @param height
	 * @param row
	 */
	public static void drawGameStateRow(GameState s, int width, int height, int row) { 
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int index = row * width * 3;
		for (int y = 0; y < height; y++) {
			int bufferPos = 0;
			for (int x = 0; x < width; x++) {
				int r = index + bufferPos + RED_INDEX;
				int g = index + bufferPos + GREEN_INDEX;
				int b = index + bufferPos + BLUE_INDEX;
				// int rgb = new Color(s.imageBuffer[r], s.imageBuffer[g],s.imageBuffer[b]).getRGB();
				int rgb = new Color(s.imageBuffer[r], s.imageBuffer[r], s.imageBuffer[r]).getRGB();
				image.setRGB(x, y, rgb);
				bufferPos += 3;
			}
		}
		DrawingPanel dp = GraphicsUtil.drawImage(image, "Doom", width, height);
		MiscUtil.waitForReadStringAndEnterKeyPress();
	}
}
