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
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.testmatch.imagematch.ImageMatchTask;
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
 * @author Jacob Schrum
 * @param <T> Phenotype being evolved
 */
public abstract class VizDoomTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask {

    // All of these constants should probably become command line 
    // parameters once we fully figure out the task
    public static String SCENARIO_WAD = "basic.wad";
    public static String GAME_WAD = "freedoom2.wad";
    public static String DOOM_MAP = "map01";
    public static int DOOM_EPISODE_LENGTH = 200;
    
    // For each pixel in the image buffer, the colors are sorted in this order
    public static final int RED_INDEX = 2;
    public static final int GREEN_INDEX = 1;
    public static final int BLUE_INDEX = 0;

    public DoomGame game;
    public List<int[]> actions;
    public List<String> actionLabels;
    
    public VizDoomTask() {
        // These should not be here ... put in an init call?
        doomInit();
        actionLabels = new ArrayList<String>();
        actions = new ArrayList<int[]>(); 
        setDoomActions();
        setDoomStateVariables();
        setDoomMiscSettings();
        MMNEAT.registerFitnessFunction("DoomReward");
    }
    
    @Override
    public void prep() {
    	// Initialize the game. Further configuration won't take any effect from now on.
        game.init();        
    }

    public void doomInit() {
        SpecifyDLL.specifyDLLPath();
        // Create DoomGame instance. It will run the game and communicate with you.
        game = new DoomGame();
        // Sets path to vizdoom engine executive which will be spawned as a separate process.
        game.setViZDoomPath("vizdoom/bin/vizdoom_nosound");
        // Sets path to doom2 iwad resource file which contains the actual doom game-> Default is "./doom2.wad".
        game.setDoomGamePath("vizdoom/scenarios/" + GAME_WAD);
        //game.setDoomGamePath("vizdoom/scenarios/doom2.wad");   // Not provided with environment due to licences.

        // Sets path to additional resources iwad file which is basically your scenario iwad.
        // If not specified default doom2 maps will be used and it's pretty much useles... unless you want to play doom.
        game.setDoomScenarioPath("vizdoom/scenarios/" + SCENARIO_WAD);
        // Set map to start (scenario .wad files can contain many maps).
        game.setDoomMap(DOOM_MAP);
        // Sets resolution. Default is 320X240
        setRestrictedScreenResolution(ScreenResolution.RES_200X150);
        // Sets the screen buffer format. Not used here but now you can change it. Defalut is CRCGCB.
        game.setScreenFormat(ScreenFormat.RGB24);
        // Sets other rendering options
        game.setRenderHud(false);
        game.setRenderCrosshair(false);
        game.setRenderWeapon(true);
        game.setRenderDecals(false);
        game.setRenderParticles(false);
    }

    /**
     * We came across an issue with higher screen resolutions and using the drawGameState(), drawGameStateRow() and getRow() methods
     * The higher the resolution, the better the chance the width and height are incorrect, thus making the calculations incorrect for inputs and display
     * @param res ScreenResolution
     */
    private void setRestrictedScreenResolution(ScreenResolution res) {
    	assert !res.equals(ScreenResolution.RES_800X600) : "800X600 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1024X768) : "1024X768 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1280X960) : "1280X960 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1400X1050) : "1400X1050 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1600X1200) : "1600X1200 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_800X500) : "800X500 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1024X640) : "1024X640 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1280X800) : "1280X800 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1400X875) : "1400X875 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1600X1000) : "1600X1000 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_800X450) : "800X450 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1024X576) : "1024X576 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1280X720) : "1280X720 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1400X787) : "1400X787 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1600X900) : "1600X900 is too high of a resolution!";
    	assert !res.equals(ScreenResolution.RES_1920X1080) : "1920X1080 is too high of a resolution!";
        game.setScreenResolution(res);
	}

    /**
     * Add new action to the list of possible actions the Doom agent can perform
     * @param buttonPresses The combination of buttons being pressed
     * @param label display label for the action
     */
    public final void addAction(int[] buttonPresses, String label) {
    	actionLabels.add(label);
    	actions.add(buttonPresses);
    }
    
    public abstract void setDoomActions();

    public abstract void setDoomStateVariables();

    public void setDoomMiscSettings() {
        // Causes episodes to finish after 200 tics (actions)
        game.setEpisodeTimeout(DOOM_EPISODE_LENGTH);

        // Makes episodes start after 10 tics (~after raising the weapon)
        game.setEpisodeStartTime(10);
        // Makes the window appear (turned on by default)
        // TODO: This doesn't work! Can we fix it, or do the VizDoom designers need to fix it?
        game.setWindowVisible(CommonConstants.watch);
        // Turns on the sound. (turned off by default)
        // game.setSoundEnabled(false); // This seems to be controlled by the game we run, not this setting
        // Sets ViZDoom mode (PLAYER, ASYNC_PLAYER, SPECTATOR, ASYNC_SPECTATOR, PLAYER mode is default)
        game.setMode(Mode.PLAYER);
    }

    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
    	// Need to remove rewards from previous episodes I think
    	game.newEpisode();
    	Network n = individual.getPhenotype();
        while (!game.isEpisodeFinished()) {
            // Get the state
            GameState s = game.getState();

            // Trouble shooting code
            System.out.println(s.imageBuffer.length);
            System.out.println(game.getScreenWidth());
            System.out.println(game.getScreenHeight());
            //drawGameState(s, game.getScreenWidth(), game.getScreenHeight());
            drawGameStateRow(s, game.getScreenWidth(), game.getScreenHeight(), getRow());
            double[] inputs = getInputs(s); 
            double[] outputs = n.process(inputs);
                       
            //double r = game.makeAction(actions.get(RandomNumbers.randomGenerator.nextInt(3)));
            double r = game.makeAction(actions.get(StatisticsUtilities.argmax(outputs))); // This now takes the arg max ofthe action outputs
            
            // You can also get last reward by using this function
            // double r = game.getLastReward();
        }
        return new Pair<double[], double[]>(new double[]{game.getTotalReward()}, new double[]{});
    }

    /**
     * Given the game state, return an array of doubles that the learning
     * agent will use to make a decision.
     * @param s Class containing all information about the game state
     * @return Array of sensor values/features
     */
    public abstract double[] getInputs(GameState s);
    
    /**
     * Is run at the conclusion of all evolution.
     * Terminates the DoomGame instance.
     */
    public void finalCleanup() {
    	game.close();
    }
    
    /**
     * Just the reward. Will probably override this at some point
     * @return Number of objectives
     */
    @Override
    public int numObjectives() {
        return 1;
    }
    
    /**
     * Number of available actions.
     * Generally matches the number of
     * policy outputs.
     * @return Number of actions
     */
    public int numActions() {
        return actions.size();
    }
    
    public abstract int numInputs();
    
    public abstract int getRow();
    
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
     * You may change which of the RGB values appear as well, currently set to all red values.
     * 
     * This is primarily a utility class for troubleshooting purposes.
     * @param s Game state
     * @param width screen width
     * @param height screen height
     */
    public static void drawGameState(GameState s, int width, int height) {
    	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int bufferPos = 0;
        for(int y = 0; y < height; y++) {
        	for(int x = 0; x < width; x++) {              
                int r = bufferPos + RED_INDEX;
                int g = bufferPos + GREEN_INDEX;
                int b = bufferPos + BLUE_INDEX;
        		//int rgb = new Color(s.imageBuffer[r], s.imageBuffer[g], s.imageBuffer[b]).getRGB();
                int rgb = new Color(s.imageBuffer[r], s.imageBuffer[r], s.imageBuffer[r]).getRGB();
        		image.setRGB(x, y, rgb);
        		bufferPos += 3;
        	}
        }
        DrawingPanel dp = ImageMatchTask.drawImage(image, "Doom", width, height);
        MiscUtil.waitForReadStringAndEnterKeyPress();
    }
    
    /**
     * This method outputs the given row stretched across the height
     * You may change which of the RGB values appear as well, currently set to all red values.
     * This is another utility class.
     * 
     * @param s
     * @param width
     * @param height
     * @param row
     */
    public static void drawGameStateRow(GameState s, int width, int height, int row) { // TODO: actually change this -Gab
    	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int index = row * width * 3;
        for(int y = 0; y < height; y++) {
        	int bufferPos = 0;
        	for(int x = 0; x < width; x++) {
        		int r = index + bufferPos + RED_INDEX;
                int g = index + bufferPos + GREEN_INDEX;
                int b = index + bufferPos + BLUE_INDEX;
        		//int rgb = new Color(s.imageBuffer[r], s.imageBuffer[g], s.imageBuffer[b]).getRGB();
                int rgb = new Color(s.imageBuffer[r], s.imageBuffer[r], s.imageBuffer[r]).getRGB();
        		image.setRGB(x, y, rgb);
        		bufferPos += 3;
        	}
        }
        DrawingPanel dp = ImageMatchTask.drawImage(image, "Doom", width, height);
        MiscUtil.waitForReadStringAndEnterKeyPress();
    }
}
