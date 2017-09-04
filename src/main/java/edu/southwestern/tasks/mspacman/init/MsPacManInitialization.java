package edu.southwestern.tasks.mspacman.init;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import static edu.southwestern.MMNEAT.MMNEAT.pacmanInputOutputMediator;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.crossover.network.CombiningTWEANNCrossover;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HierarchicalTWEANNGenotype;
import edu.southwestern.evolution.genotypes.SimpleBlueprintGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.pool.GenotypePool;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.experiment.evolution.SinglePopulationGenerationalEAExperiment;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.CooperativeBlueprintSubtaskMsPacManTask;
import edu.southwestern.tasks.mspacman.CooperativeNonHierarchicalMultiNetMsPacManTask;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.OneGhostAndPillsMonitorInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.OneGhostMonitorInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.combining.GhostMonitorNetworkBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.combining.SubNetworkBlock;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.FileUtilities;
import pacman.game.Constants;
import wox.serial.Easy;
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
	 * Sets up directory that stores genotypes for both pill and ghost eating agent subnetworks
	 */
	public static void setupGenotypePoolsForMsPacman() {
		// Use genotype pools
		//below check should be true unless new network utilized
		if (Parameters.parameters.classParameter("genotype").equals(HierarchicalTWEANNGenotype.class)) {
			System.out.println("Ghost eating pool");
			GenotypePool.addPool(Parameters.parameters.stringParameter("ghostEatingSubnetworkDir"));
			System.out.println("Pill eating pool");
			GenotypePool.addPool(Parameters.parameters.stringParameter("pillEatingSubnetworkDir"));
			//sets ceilings of genotype size based on initial pool size 
			MMNEAT.discreteCeilings = new int[2];
			MMNEAT.discreteCeilings[0] = GenotypePool.poolSize(0);
			MMNEAT.discreteCeilings[1] = GenotypePool.poolSize(1);
		}
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
	
	@SuppressWarnings("rawtypes")
	public static void setupCooperativeCoevolutionSelectorForMsPacman() throws NoSuchMethodException {
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
		MMNEAT.setNNInputParameters(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.pacmanInputOutputMediator.numOut());
		// subcontrollers and selector and possibly blueprints
		MMNEAT.genotypeExamples = new ArrayList<Genotype>(MMNEAT.modesToTrack + 2); 
		MMNEAT.genotypeExamples.add(new TWEANNGenotype(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.modesToTrack, 0));
		MMNEAT.coevolutionMediators = new MsPacManControllerInputOutputMediator[MMNEAT.modesToTrack];
		for (int i = 1; i <= MMNEAT.modesToTrack; i++) {
			MMNEAT.coevolutionMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass" + i);
			MMNEAT.genotypeExamples.add(new TWEANNGenotype(MMNEAT.coevolutionMediators[i - 1].numIn(), GameFacade.NUM_DIRS, i));
		}

		MMNEAT.prepareCoevolutionArchetypes();
		// Now the blueprints come in
		if (MMNEAT.task instanceof CooperativeBlueprintSubtaskMsPacManTask) {
			MMNEAT.blueprints = true;
			// subcontrollers and selector
			MMNEAT.genotypeExamples.add(new SimpleBlueprintGenotype(MMNEAT.modesToTrack + 1)); 
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void setupCooperativeCoevolutionCheckEachMultitaskPreferenceNetForMsPacman() throws NoSuchMethodException {
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
		MMNEAT.modesToTrack = CommonConstants.multitaskModules;
		// Setup the preference net settings
		CommonConstants.multitaskModules = 1; // Needed before set NN params
		MMNEAT.setNNInputParameters(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.modesToTrack);
		CommonConstants.multitaskModules = MMNEAT.modesToTrack; // Restore value

		MMNEAT.genotypeExamples = new ArrayList<Genotype>(2);
		// Multitask
		MMNEAT.genotypeExamples.add(new TWEANNGenotype(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.modesToTrack, CommonConstants.fs, ActivationFunctions.newNodeFunction(), MMNEAT.modesToTrack, 0));
		// Pref Net
		MMNEAT.genotypeExamples.add(new TWEANNGenotype(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.modesToTrack, CommonConstants.fs, ActivationFunctions.newNodeFunction(), 1, 1));

		MMNEAT.prepareCoevolutionArchetypes();
	}
	
	@SuppressWarnings("rawtypes")
	public static void setupCooperativeCoevolutionCombinerForMsPacman() throws NoSuchMethodException {
		boolean includeInputs = Parameters.parameters.booleanParameter("subsumptionIncludesInputs");
		int outputsPerSubnet = GameFacade.NUM_DIRS;
		// Use the specified mesiator, but add required subnet blocks to it later
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
		MMNEAT.coevolutionMediators = new MsPacManControllerInputOutputMediator[MMNEAT.modesToTrack];
		int numInputs = MMNEAT.pacmanInputOutputMediator.numIn();
		for (int i = 1; i <= MMNEAT.modesToTrack; i++) {
			MMNEAT.coevolutionMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass" + i);
			numInputs += outputsPerSubnet + (includeInputs ? MMNEAT.coevolutionMediators[i - 1].numIn() : 0);
		}
		MMNEAT.setNNInputParameters(numInputs, GameFacade.NUM_DIRS);

		MMNEAT.genotypeExamples = new ArrayList<Genotype>(MMNEAT.modesToTrack + 1);
		MMNEAT.genotypeExamples.add(new TWEANNGenotype(numInputs, GameFacade.NUM_DIRS, 0));
		for (int i = 0; i < MMNEAT.modesToTrack; i++) {
			TWEANNGenotype tg = new TWEANNGenotype(MMNEAT.coevolutionMediators[i].numIn(), outputsPerSubnet, (i + 1));
			MMNEAT.genotypeExamples.add(tg);
		}
		MMNEAT.prepareCoevolutionArchetypes();
		// Needed so that sensor and output labels can be retrieved
		for (int i = 0; i < MMNEAT.coevolutionMediators.length; i++) {
			TWEANNGenotype tg = (TWEANNGenotype) MMNEAT.genotypeExamples.get(i + 1); // skip combiner
			((BlockLoadedInputOutputMediator) MMNEAT.pacmanInputOutputMediator).blocks
					.add(new SubNetworkBlock(tg.getPhenotype(), MMNEAT.coevolutionMediators[i],
							MMNEAT.coevolutionMediators[i].getClass().getSimpleName() + "[" + i + "]", includeInputs));
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void setupCooperativeCoevolutionNonHierarchicalForMsPacman() throws NoSuchMethodException {
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
		MMNEAT.setNNInputParameters(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.pacmanInputOutputMediator.numOut());

		CooperativeNonHierarchicalMultiNetMsPacManTask theTask = (CooperativeNonHierarchicalMultiNetMsPacManTask) MMNEAT.task;
		int pops = theTask.numberOfPopulations();
		MMNEAT.genotypeExamples = new ArrayList<Genotype>(pops);
		boolean specializeMediators = !Parameters.parameters.booleanParameter("defaultMediator");
		for (int i = 1; i <= pops; i++) {
			if (specializeMediators) {
				theTask.inputMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass" + i);
			}
			MMNEAT.genotypeExamples.add(new TWEANNGenotype(theTask.inputMediators[i - 1].numIn(), GameFacade.NUM_DIRS, i - 1));
		}
		MMNEAT.prepareCoevolutionArchetypes();
	}
	
	@SuppressWarnings("unchecked")
	public static void setupMultitaskSeedPopulationForMsPacman(String ghostDir, String pillDir) {
		// A combined archetype is needed
		CombiningTWEANNCrossover cross = new CombiningTWEANNCrossover(true, true);
		ArrayList<TWEANNGenotype.NodeGene> ghostArchetype = (ArrayList<TWEANNGenotype.NodeGene>) Easy
				.load(Parameters.parameters.stringParameter("ghostArchetype"));
		ArrayList<TWEANNGenotype.NodeGene> pillArchetype = (ArrayList<TWEANNGenotype.NodeGene>) Easy
				.load(Parameters.parameters.stringParameter("pillArchetype"));
		TWEANNGenotype ghostArchetypeNet = new TWEANNGenotype(ghostArchetype, new ArrayList<TWEANNGenotype.LinkGene>(),
				GameFacade.NUM_DIRS, false, false, -1);
		TWEANNGenotype pillArchetypeNet = new TWEANNGenotype(pillArchetype, new ArrayList<TWEANNGenotype.LinkGene>(),
				GameFacade.NUM_DIRS, false, false, -1);
		EvolutionaryHistory.archetypes[0] = ((TWEANNGenotype) cross.crossover(ghostArchetypeNet,
				pillArchetypeNet)).nodes;

		// Load component populations, create multitask network combinations,
		// and save them all to an initial population dir
		int mu = Parameters.parameters.integerParameter("mu");
		int layers = 1;
		while (layers * layers < mu) {
			layers++;
		}
		ArrayList<Genotype<TWEANN>> ghostPop = PopulationUtil.load(ghostDir);
		ArrayList<Genotype<TWEANN>> pillPop = PopulationUtil.load(pillDir);

		String ghostScoreFile = Parameters.parameters.stringParameter("multinetworkScores1");
		String pillScoreFile = Parameters.parameters.stringParameter("multinetworkScores2");


                NSGA2Score<TWEANN>[] ghostScores = null;
                NSGA2Score<TWEANN>[] pillScores = null;
		try {
			ghostScores = PopulationUtil.loadScores(ghostScoreFile);
			pillScores = PopulationUtil.loadScores(pillScoreFile);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		// Reduce to the top performers of each population
		PopulationUtil.pruneDownToTopParetoLayers(ghostPop, ghostScores, layers);
		PopulationUtil.pruneDownToTopParetoLayers(pillPop, pillScores, layers);

		// Now create a population of combined multitask networks
		ArrayList<Genotype<TWEANN>> combinedPopulation = new ArrayList<Genotype<TWEANN>>(mu);
		int ghostPos = 0;
		int pillPos = 0;
		for (int i = 0; i < mu; i++) {
			Genotype<TWEANN> ghostEating = ghostPop.get(ghostPos).copy();
			Genotype<TWEANN> pillEating = pillPop.get(pillPos).copy();
			Genotype<TWEANN> combination = cross.crossover(ghostEating, pillEating);
			combinedPopulation.add(combination);
			// Pick next pair of networks to combine
			pillPos++;
			if (pillPos >= pillPop.size()) {
				pillPos = 0;
				ghostPos++;
			}
		}
		// Save the population so it will be loaded naturally, as if a resume is
		// being performed
		String saveDirectory = FileUtilities.getSaveDirectory();
		SinglePopulationGenerationalEAExperiment.save("initial", saveDirectory, combinedPopulation, false);
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
