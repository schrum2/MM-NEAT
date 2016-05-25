package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.testmatch.imagematch.ImageMatchTask;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.awt.Color;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import vizdoom.Button;
import vizdoom.DoomGame;
import vizdoom.GameState;
import vizdoom.GameVariable;
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

    public static String SCENARIO_WAD = "basic.wad";
    public static String DOOM_MAP = "map01";
    public static int DOOM_EPISODE_LENGTH = 200;
    public static final int RED_INDEX = 2;
    public static final int GREEN_INDEX = 1;
    public static final int BLUE_INDEX = 0;

    public DoomGame game;
    public List<int[]> actions;
    public List<String> actionLabels;
    
    public VizDoomTask() {
        // These should not be here ... put in an init call?
        game = doomInit();
        actionLabels = new ArrayList<String>();
        actions = new ArrayList<int[]>(); 
        setDoomActions();
        setDoomStateVariables();
        setDoomMiscSettings();
        // Initialize the game. Further configuration won't take any effect from now on.
        game.init();
        MMNEAT.registerFitnessFunction("DoomReward");
    }

    public DoomGame doomInit() {
        SpecifyDLL.specifyDLLPath();
        // Create DoomGame instance. It will run the game and communicate with you.
        DoomGame game = new DoomGame();
        // Sets path to vizdoom engine executive which will be spawned as a separate process.
        game.setViZDoomPath("vizdoom/bin/vizdoom_nosound");
        // Sets path to doom2 iwad resource file which contains the actual doom game-> Default is "./doom2.wad".
        game.setDoomGamePath("vizdoom/scenarios/freedoom2.wad");
        //game.setDoomGamePath("vizdoom/scenarios/doom2.wad");   // Not provided with environment due to licences.

        // Sets path to additional resources iwad file which is basically your scenario iwad.
        // If not specified default doom2 maps will be used and it's pretty much useles... unless you want to play doom.
        game.setDoomScenarioPath("vizdoom/scenarios/" + SCENARIO_WAD);
        // Set map to start (scenario .wad files can contain many maps).
        game.setDoomMap(DOOM_MAP);
        // Sets resolution. Default is 320X240
        //game.setScreenResolution(ScreenResolution.RES_640X480);
        game.setScreenResolution(ScreenResolution.RES_160X120);
        // Sets the screen buffer format. Not used here but now you can change it. Defalut is CRCGCB.
        game.setScreenFormat(ScreenFormat.RGB24);
        // Sets other rendering options
        game.setRenderHud(false);
        game.setRenderCrosshair(false);
        game.setRenderWeapon(true);
        game.setRenderDecals(false);
        game.setRenderParticles(false);

        return game;
    }

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
        //game.setEpisodeStartTime(10);
        // Makes the window appear (turned on by default)
        game.setWindowVisible(CommonConstants.watch);
        // Turns on the sound. (turned off by default)
        game.setSoundEnabled(false);
        // Sets ViZDoom mode (PLAYER, ASYNC_PLAYER, SPECTATOR, ASYNC_SPECTATOR, PLAYER mode is default)
        game.setMode(Mode.PLAYER);
    }

    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
    	// Need to remove rewards from previous episodes I think
    	game.newEpisode();
        while (!game.isEpisodeFinished()) {
            // Get the state
            GameState s = game.getState();
            //System.out.print("Image Buffer Length: ");
            //System.out.println(s.imageBuffer.length);
            //System.out.println(Arrays.toString(s.imageBuffer));
            
            //drawGameStateRow(s, 160, 120, 61);
            double[] temp = isolateRow(s, 160, 120, 61, RED_INDEX);
            System.out.println(Arrays.toString(temp));
            
            // Make random action and get reward
            // TODO: Change to get action from neural network encoded by "individual"
            // TODO: Need to extract sensor readings ... decide what these are
            double r = game.makeAction(actions.get(RandomNumbers.randomGenerator.nextInt(3)));

            // You can also get last reward by using this function
            // double r = game.getLastReward();
            System.out.println("State #" + s.number);
            System.out.println("Game variables: " + Arrays.toString(s.gameVariables));
            System.out.println("Action reward: " + r);
            System.out.println("=====================");

        }
        return new Pair<double[], double[]>(new double[]{game.getTotalReward()}, new double[]{});
    }

    public void cleanup() {
    	game.close();
    }
    
    @Override
    public int numObjectives() {
        return 1;
    }

    @Override
    public double getTimeStamp() {
        return game.getEpisodeTime(); // Confirm that this works
    }

    @Override
    public String[] sensorLabels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] outputLabels() {
        // Derive from actionLabels
        return actionLabels.toArray(new String[actionLabels.size()]);
    }

    /**
     * This method outputs the Gamestate according to the width and height given
     * You may change which of the RGB values appear as well, currently set to all red values
     * @param s
     * @param width
     * @param height
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
     * You may change which of the RGB values appear as well, currently set to all red values
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
    
    public static double[] isolateRow(GameState s, int width, int height, int row, int color){
    	assert color == RED_INDEX || color == GREEN_INDEX || color == BLUE_INDEX : "color is not valid! " + color;
    	double[] result = new double[width];
    	int index = row * width * 3;
		for(int y = 0; y < height; y++) {
        	int bufferPos = 0;
        	for(int x = 0; x < width; x++) {
        		int c = index + bufferPos + color;
                //int rgb = new Color(s.imageBuffer[c], s.imageBuffer[c], s.imageBuffer[c]).getRGB();
                result[x] = (s.imageBuffer[c]) / 255.0;
        		bufferPos += 3;
        	}
        }
		return result;
    }
}
