package edu.southwestern.tasks.mspacman.init;

import static edu.southwestern.MMNEAT.MMNEAT.pacmanInputOutputMediator;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.crossover.network.CombiningTWEANNCrossover;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.OneGhostAndPillsMonitorInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.OneGhostMonitorInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.combining.GhostMonitorNetworkBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.combining.SubNetworkBlock;
import edu.southwestern.util.ClassCreation;
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
	
	/**
	 * Assumes the subnets are always in SubNetworkBlocks at the end of a
	 * BlockLoadedInputOutputMediator, so the nets to replace are found based on
	 * the length of the input subnets.
         * @param <T> phenotype
         * @param subnets change the subnetworks in the
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> void replaceSubnets(ArrayList<Genotype<T>> subnets) {
		BlockLoadedInputOutputMediator blockMediator = ((BlockLoadedInputOutputMediator) pacmanInputOutputMediator);
		int numBlocks = blockMediator.blocks.size();
		for (int i = 0; i < subnets.size(); i++) {
			((SubNetworkBlock) blockMediator.blocks.get(numBlocks - subnets.size() + i)).changeNetwork(subnets.get(i));
		}
	}
        
	@SuppressWarnings("rawtypes")
	public static void setupCooperativeCoevolutionGhostMonitorsForMsPacman() throws NoSuchMethodException { 
		boolean includeInputs = Parameters.parameters.booleanParameter("subsumptionIncludesInputs");
		int outputsPerMonitor = GameFacade.NUM_DIRS;
		boolean ghostMonitorsSensePills = Parameters.parameters.booleanParameter("ghostMonitorsSensePills");
		// Use the specified mediator, but add the required ghost monitor blocks
		// to it later
		int ghostMonitorInputs = (ghostMonitorsSensePills ? 
                        new OneGhostAndPillsMonitorInputOutputMediator(0) : 
                        new OneGhostMonitorInputOutputMediator(0)).numIn();
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
		int numInputs = MMNEAT.pacmanInputOutputMediator.numIn() + (CommonConstants.numActiveGhosts * (includeInputs ? outputsPerMonitor + ghostMonitorInputs : outputsPerMonitor));
		MMNEAT.setNNInputParameters(numInputs, GameFacade.NUM_DIRS);

		MMNEAT.genotypeExamples = new ArrayList<Genotype>(CommonConstants.numActiveGhosts + 1);
		MMNEAT.genotypeExamples.add(new TWEANNGenotype(numInputs, GameFacade.NUM_DIRS, 0));

		ArrayList<Genotype<TWEANN>> ghostMonitorExamples = new ArrayList<Genotype<TWEANN>>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			TWEANNGenotype tg = new TWEANNGenotype(ghostMonitorInputs, outputsPerMonitor, (i + 1));
			MMNEAT.genotypeExamples.add(tg);
			ghostMonitorExamples.add(tg);
		}
		MMNEAT.prepareCoevolutionArchetypes();
		// Needed so that sensor and output labels can be retrieved
		for (int i = 0; i < ghostMonitorExamples.size(); i++) {
			((BlockLoadedInputOutputMediator) MMNEAT.pacmanInputOutputMediator).blocks
					.add(new GhostMonitorNetworkBlock((TWEANNGenotype) ghostMonitorExamples.get(i), includeInputs, i));
		}
	}
					
	public static void setupSingleMultitaskSeedForMsPacman() {
		Genotype<TWEANN> ghostEating = EvolutionaryHistory
				.getSubnetwork(Parameters.parameters.stringParameter("ghostEatingSubnetwork"));
		Genotype<TWEANN> pillEating = EvolutionaryHistory
				.getSubnetwork(Parameters.parameters.stringParameter("pillEatingSubnetwork"));
		CombiningTWEANNCrossover cross = new CombiningTWEANNCrossover(true, true);
		// Copy assures a fresh genotype id
		MMNEAT.genotype = cross.crossover(ghostEating, pillEating).copy();
		MMNEAT.seedExample = true;
	}
}
