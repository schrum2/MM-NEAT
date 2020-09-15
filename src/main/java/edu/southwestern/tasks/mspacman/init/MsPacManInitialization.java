package edu.southwestern.tasks.mspacman.init;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import oldpacman.game.Constants;
/**
 * Initializes ms Pac Man
 * 
 * @author Jacob Schrum
 *
 */
public class MsPacManInitialization {
  
	/**
	 * Sets up different components of ms PacMan based on parameters
	 */
	public static void setupMsPacmanParameters() {
		//parameter, "Log ghost locations corresponding to each eaten power pill"
		if (Parameters.parameters.booleanParameter("logGhostLocOnPowerPill")) {
			MMNEAT.ghostLocationsOnPowerPillEaten = new MMNEATLog("PowerPillToGhostLocationMapping");
		}
		//sets enums equal to command line parameter, "Lives that a pacman agent starts with"
		Constants.NUM_LIVES = Parameters.parameters.integerParameter("pacmanLives");
		//parameter, "Initial edible ghost time in Ms. Pac-Man"
		Constants.EDIBLE_TIME = Parameters.parameters.integerParameter("edibleTime");
		//"Edible time decreases as generations pass"
		if (Parameters.parameters.booleanParameter("incrementallyDecreasingEdibleTime")) {
			setEdibleTimeBasedOnGeneration(Parameters.parameters.integerParameter("lastSavedGeneration"));
		}//How long ghosts are imprisoned in lair after being eaten"
		Constants.COMMON_LAIR_TIME = Parameters.parameters.integerParameter("lairTime");
		// "Lair time decreases as generations pass"
		if (Parameters.parameters.booleanParameter("incrementallyDecreasingLairTime")) {
			setLairTimeBasedOnGeneration(Parameters.parameters.integerParameter("lastSavedGeneration"));
		}
	}

	/**
	 * 
	 * @param generation generation
	 */
	public static void setLairTimeBasedOnGeneration(int generation) {
		double maxGens = Parameters.parameters.integerParameter("maxGens");
		//"Number of gens at end of evolution when lair time is settled"
		int consistentLairTimeGens = Parameters.parameters.integerParameter("consistentLairTimeGens");
		int minLairTime = Parameters.parameters.integerParameter("minLairTime");
		if ((maxGens - generation) > consistentLairTimeGens) {//TODO after a specific generation, time ghosts spend
			//in lair begins to decrease
			int maxLairTime = Parameters.parameters.integerParameter("maxLairTime");
			int lairRange = maxLairTime - minLairTime;
			double scale = generation / (maxGens - consistentLairTimeGens);
			int lairTimeProgress = (int) Math.floor(scale * lairRange);
			Constants.COMMON_LAIR_TIME = maxLairTime - lairTimeProgress;
		} else {//otherwise lair time remains constant
			Constants.COMMON_LAIR_TIME = minLairTime;
		}//resets command line parameter
		Parameters.parameters.setInteger("lairTime", Constants.COMMON_LAIR_TIME);
		System.out.println("LAIR TIME: " + Constants.COMMON_LAIR_TIME);
	}

	/**
	 * sets amount of time ghosts are edible based on the generation #
	 * @param generation
	 */
	public static void setEdibleTimeBasedOnGeneration(int generation) {
		double maxGens = Parameters.parameters.integerParameter("maxGens");
		//"Number of gens at end of evolution when edible ghost time is settled"
		int consistentEdibleTimeGens = Parameters.parameters.integerParameter("consistentEdibleTimeGens");
		int minEdibleTime = Parameters.parameters.integerParameter("minEdibleTime");
		if ((maxGens - generation) > consistentEdibleTimeGens) {//TODO after a specific generation, time ghosts are edible
			//begins to decrease
			int maxEdibleTime = Parameters.parameters.integerParameter("maxEdibleTime");
			int edibleRange = maxEdibleTime - minEdibleTime;
			double scale = generation / (maxGens - consistentEdibleTimeGens);
			int edibleTimeProgress = (int) Math.floor(scale * edibleRange);
			Constants.EDIBLE_TIME = maxEdibleTime - edibleTimeProgress;
		} else {//else that time remains the same
			Constants.EDIBLE_TIME = minEdibleTime;
		}//resets command line parameter 
		Parameters.parameters.setInteger("edibleTime", Constants.EDIBLE_TIME);
		System.out.println("EDIBLE TIME: " + Constants.EDIBLE_TIME);
	}
	        
}
