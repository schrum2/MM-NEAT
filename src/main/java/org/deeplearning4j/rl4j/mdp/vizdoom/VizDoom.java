package org.deeplearning4j.rl4j.mdp.vizdoom;


import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Pointer;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import vizdoom.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/28/16.
 * Modified by nazaruka (nazaruka@southwestern.edu) on 5/17/19.
 * 
 * Mother abstract class for all VizDoom scenarios
 *
 * is mostly configured by
 *
 *    String scenario;       name of the scenario
 *    double livingReward;   additional reward at each step for living
 *    double deathPenalty;   negative reward when ded
 *    int doomSkill;         skill of the ennemy
 *    int timeout;           number of step after which simulation time out
 *    int startTime;         number of internal tics before the simulation starts (useful to draw weapon by example)
 *    List<Button> buttons;  the list of inputs one can press for a given scenario (noop is automatically added)
 */
@Slf4j
abstract public class VizDoom implements MDP<VizDoom.GameScreen, Integer, DiscreteSpace> {

    private static Logger log = LoggerFactory.getLogger(VizDoom.class);

    final public static String DOOM_ROOT = "vizdoom";

    // Protected variables that will be manipulated later on.
    protected DoomGame game;
    final protected List<int[]> actions;
    final protected DiscreteSpace discreteSpace;
    final protected ObservationSpace<GameScreen> observationSpace;
    @Getter
    final protected boolean render;
    @Setter
    protected double scaleFactor = 1;

    // Constructor used in the case of a blank parameter field. If a user forgets to
    // specify what value render has, VizDoom will assume it is false.
    public VizDoom() {
        this(false);
    }

    /** 
     * In this constructor, which is the one most often cited by the subclasses, VizDoom:
     * 		- makes a decision on visualization depending on the boolean value of render
     * 		- creates an empty list of actions
     * 		- sets up a new Doom game and its discrete and observation spaces
     */
    public VizDoom(boolean render) {
        this.render = render;
        actions = new ArrayList<int[]>();
        game = new DoomGame();
        setupGame();
        discreteSpace = new DiscreteSpace(getConfiguration().getButtons().size() + 1);
        observationSpace = new ArrayObservationSpace<>(new int[] {game.getScreenHeight(), game.getScreenWidth(), 3});
    }

    // Access method for Boolean value of render.
    public boolean isRender() {
    	return render;
    }
    
    /** 
     * This method allows the user to set the scale factor, which is initialized to 1.
     * Recall that the user may not access the scale factor, for it is an important 
     * value that has to do with how rapidly network agents expand.
     */
    public void setScaleFactor(double sf) {
    	scaleFactor = sf;
    }
    
    // Method that is used to start the game.
    public void setupGame() {

        Configuration conf = getConfiguration();

        game.setViZDoomPath(DOOM_ROOT + "/bin/vizdoom");
        game.setDoomGamePath(DOOM_ROOT + "/bin/freedoom2.wad");
        game.setDoomScenarioPath(DOOM_ROOT + "/scenarios/" + conf.getScenario() + ".wad");

        game.setDoomMap("map01");

        game.setScreenFormat(ScreenFormat.RGB24);
        //game.setScreenResolution(ScreenResolution.RES_200X150); // Smallest possible
        game.setScreenResolution(ScreenResolution.RES_800X600);
        // Sets other rendering options
        game.setRenderHud(false);
        game.setRenderCrosshair(false);
        game.setRenderWeapon(true);
        game.setRenderDecals(false);
        game.setRenderParticles(false);


        GameVariable[] gameVar = new GameVariable[] {GameVariable.KILLCOUNT, GameVariable.ITEMCOUNT,
                        GameVariable.SECRETCOUNT, GameVariable.FRAGCOUNT, GameVariable.HEALTH, GameVariable.ARMOR,
                        GameVariable.DEAD, GameVariable.ON_GROUND, GameVariable.ATTACK_READY,
                        GameVariable.ALTATTACK_READY, GameVariable.SELECTED_WEAPON, GameVariable.SELECTED_WEAPON_AMMO,
                        GameVariable.AMMO1, GameVariable.AMMO2, GameVariable.AMMO3, GameVariable.AMMO4,
                        GameVariable.AMMO5, GameVariable.AMMO6, GameVariable.AMMO7, GameVariable.AMMO8,
                        GameVariable.AMMO9, GameVariable.AMMO0};
        // Adds game variables that will be included in state.

        // Schrum: My version of the code restricts the game variables ... which approach is better?
        // Nazaruk: Perhaps limiting to crucial variables like HEALTH, DEAD and ATTACK_READY and only
        // one kind of ammo will be more efficient for the deep learning task we are trying to solve.
        for (int i = 0; i < gameVar.length; i++) {
            game.addAvailableGameVariable(gameVar[i]);
        }


        // Causes episodes to finish after timeout tics
        game.setEpisodeTimeout(conf.getTimeout());

        game.setEpisodeStartTime(conf.getStartTime());

        game.setWindowVisible(render);
        game.setSoundEnabled(false);
        game.setMode(Mode.PLAYER);


        game.setLivingReward(conf.getLivingReward());

        // Adds buttons that will be allowed.
        List<Button> buttons = conf.getButtons();
        int size = buttons.size();

        actions.add(new int[size + 1]);
        for (int i = 0; i < size; i++) {
            game.addAvailableButton(buttons.get(i));
            int[] action = new int[size + 1];
            action[i] = 1;
            actions.add(action);
        }

        game.setDeathPenalty(conf.getDeathPenalty());
        game.setDoomSkill(conf.getDoomSkill());

        game.init();
    }

    // Access method for Boolean value of isEpisodeFinished(), which tells if current game is done.
    public boolean isDone() {
        return game.isEpisodeFinished();
    }

    public GameScreen reset() {
        log.info("free Memory: " + (Pointer.availablePhysicalBytes()) + "/" + (Pointer.totalPhysicalBytes()));

        game.newEpisode();
        return new GameScreen(game.getState().screenBuffer);
    }

    // Method that is used to close the game.
    public void close() {
        game.close();
    }

    // Merely a variable that contains data returned after every step (action).
    public StepReply<GameScreen> step(Integer action) {

        double r = game.makeAction(actions.get(action)) * scaleFactor;
        log.info(game.getEpisodeTime() + " " + r + " " + action + " ");
        return new StepReply<>(new GameScreen(game.isEpisodeFinished()
                ? new byte[game.getScreenSize()]
                : game.getState().screenBuffer), r, game.isEpisodeFinished(), null);
    }

    /**
     *  The next two methods are access methods for the observationSpace and 
     *  discreteSpace variables. Recall that the observation space contains basic 
     *  information about a state space (e.g. its name and its shape), but the
     *  discrete space follows its topological convention in that it generates some
     *  number of actions and then isolates them from one another.
     *  
     *  ...in other words, the two are very different and are not to be confused.
     */
    public ObservationSpace<GameScreen> getObservationSpace() {
        return observationSpace;
    }
    
    public DiscreteSpace getActionSpace() {
        return discreteSpace;
    }

    // The next two methods are declared abstract so as to give VizDoom.java's subclasses
    // complete leverage over how they want to be instantiated.
    public abstract Configuration getConfiguration();

    public abstract VizDoom newInstance();

    /**
     * This class enables the creation of environments to go along with every scenario. 
     * Each environment is influenced by seven qualities, which were described by
     * Fiszel before the VizDoom class header but are rephrased below for your convenience.
     */
    @Value
    public static class Configuration {
        String scenario; // What is the given scenario?
        double livingReward; // What, if the network succeeds, reinforces it?
        double deathPenalty; // What is the negative reward if the network fails?
        int doomSkill; // How powerful are the opponents, if there are any?
        int timeout; // After how many actions would the environment time out?
        int startTime; // After how many ticks may an environment start?
        List<Button> buttons; // What are the actions a network can take on?
        
        // Constructor that initializes based on values assigned by subclasses.
        public Configuration(String scenario, double livingReward, double deathPenalty, int doomSkill, int timeout, int startTime, List<Button> buttons) {
        	this.scenario = scenario;
        	this.livingReward = livingReward;
        	this.deathPenalty = deathPenalty; 
        	this.doomSkill = doomSkill;
        	this.timeout = timeout;
        	this.startTime = startTime;
        	this.buttons = buttons;
        }

        // What follows are several access methods for Configuration's variables.
		public String getScenario() {
			return scenario;
		}

		public double getLivingReward() {
			return livingReward;
		}

		public double getDeathPenalty() {
			return deathPenalty;
		}

		public int getDoomSkill() {
			return doomSkill;
		}

		public int getTimeout() {
			return timeout;
		}

		public int getStartTime() {
			return startTime;
		}

		public List<Button> getButtons() {
			return buttons;
		}
    }

    // This class represents how the game stores the data displayed as a linear array,
    // a common practice in machine learning.
    public static class GameScreen implements Encodable {

    	// Array of doubles is initialized.
        double[] array;

        // Assigns values from an array "screen" to an array of doubles, where the values
        // in the array of doubles become their quotients after being divided by 255.
        public GameScreen(byte[] screen) {
            array = new double[screen.length];
            for (int i = 0; i < screen.length; i++) {
                array[i] = (screen[i] & 0xFF) / 255.0;
            }
        }
        
        // Encodable's sole method is implemented as an access method.
        public double[] toArray() {
            return array;
        }
    }

}
