package edu.utexas.cs.nn.tasks.mspacman.init;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.network.CombiningTWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HierarchicalTWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.SimpleBlueprintGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.pool.GenotypePool;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.experiment.SinglePopulationGenerationalEAExperiment;
import edu.utexas.cs.nn.log.MMNEATLog;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.CooperativeBlueprintSubtaskMsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.CooperativeNonHierarchicalMultiNetMsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.OneGhostAndPillsMonitorInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.OneGhostMonitorInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.GhostMonitorNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.SubNetworkBlock;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;
import pacman.game.Constants;
import wox.serial.Easy;

public class MsPacManInitialization {
  
	public static void setupMsPacmanParameters() {
		if (Parameters.parameters.booleanParameter("logGhostLocOnPowerPill")) {
			MMNEAT.ghostLocationsOnPowerPillEaten = new MMNEATLog("PowerPillToGhostLocationMapping");
		}

		Constants.NUM_LIVES = Parameters.parameters.integerParameter("pacmanLives");
		Constants.EDIBLE_TIME = Parameters.parameters.integerParameter("edibleTime");
		if (Parameters.parameters.booleanParameter("incrementallyDecreasingEdibleTime")) {
			setEdibleTimeBasedOnGeneration(Parameters.parameters.integerParameter("lastSavedGeneration"));
		}
		Constants.COMMON_LAIR_TIME = Parameters.parameters.integerParameter("lairTime");
		if (Parameters.parameters.booleanParameter("incrementallyDecreasingLairTime")) {
			setLairTimeBasedOnGeneration(Parameters.parameters.integerParameter("lastSavedGeneration"));
		}
	}

	public static void setLairTimeBasedOnGeneration(int generation) {
		double maxGens = Parameters.parameters.integerParameter("maxGens");
		int consistentLairTimeGens = Parameters.parameters.integerParameter("consistentLairTimeGens");
		int minLairTime = Parameters.parameters.integerParameter("minLairTime");
		if ((maxGens - generation) > consistentLairTimeGens) {
			int maxLairTime = Parameters.parameters.integerParameter("maxLairTime");
			int lairRange = maxLairTime - minLairTime;
			double scale = generation / (maxGens - consistentLairTimeGens);
			int lairTimeProgress = (int) Math.floor(scale * lairRange);
			Constants.COMMON_LAIR_TIME = maxLairTime - lairTimeProgress;
		} else {
			Constants.COMMON_LAIR_TIME = minLairTime;
		}
		Parameters.parameters.setInteger("lairTime", Constants.COMMON_LAIR_TIME);
		System.out.println("LAIR TIME: " + Constants.COMMON_LAIR_TIME);
	}

	public static void setEdibleTimeBasedOnGeneration(int generation) {
		double maxGens = Parameters.parameters.integerParameter("maxGens");
		int consistentEdibleTimeGens = Parameters.parameters.integerParameter("consistentEdibleTimeGens");
		int minEdibleTime = Parameters.parameters.integerParameter("minEdibleTime");
		if ((maxGens - generation) > consistentEdibleTimeGens) {
			int maxEdibleTime = Parameters.parameters.integerParameter("maxEdibleTime");
			int edibleRange = maxEdibleTime - minEdibleTime;
			double scale = generation / (maxGens - consistentEdibleTimeGens);
			int edibleTimeProgress = (int) Math.floor(scale * edibleRange);
			Constants.EDIBLE_TIME = maxEdibleTime - edibleTimeProgress;
		} else {
			Constants.EDIBLE_TIME = minEdibleTime;
		}
		Parameters.parameters.setInteger("edibleTime", Constants.EDIBLE_TIME);
		System.out.println("EDIBLE TIME: " + Constants.EDIBLE_TIME);
	}
	
	public static void setupGenotypePoolsForMsPacman() {
		// Use genotype pools
		if (Parameters.parameters.classParameter("genotype").equals(HierarchicalTWEANNGenotype.class)) {
			System.out.println("Ghost eating pool");
			GenotypePool.addPool(Parameters.parameters.stringParameter("ghostEatingSubnetworkDir"));
			System.out.println("Pill eating pool");
			GenotypePool.addPool(Parameters.parameters.stringParameter("pillEatingSubnetworkDir"));

			MMNEAT.discreteCeilings = new int[2];
			MMNEAT.discreteCeilings[0] = GenotypePool.poolSize(0);
			MMNEAT.discreteCeilings[1] = GenotypePool.poolSize(1);
		}
	}
	
	public static void setupCooperativeCoevolutionGhostMonitorsForMsPacman() throws NoSuchMethodException { 
		boolean includeInputs = Parameters.parameters.booleanParameter("subsumptionIncludesInputs");
		int outputsPerMonitor = GameFacade.NUM_DIRS;
		boolean ghostMonitorsSensePills = Parameters.parameters.booleanParameter("ghostMonitorsSensePills");
		// Use the specified mediator, but add the required ghost monitor blocks
		// to it later
		int ghostMonitorInputs = (ghostMonitorsSensePills ? new OneGhostAndPillsMonitorInputOutputMediator(0)
				: new OneGhostMonitorInputOutputMediator(0)).numIn();
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		int numInputs = MMNEAT.pacmanInputOutputMediator.numIn() + (CommonConstants.numActiveGhosts
				* (includeInputs ? outputsPerMonitor + ghostMonitorInputs : outputsPerMonitor));
		MMNEAT.setNNInputParameters(numInputs, GameFacade.NUM_DIRS);

		MMNEAT.genotypeExamples = new ArrayList<Genotype>(CommonConstants.numActiveGhosts + 1);
		MMNEAT.genotypeExamples.add(new TWEANNGenotype(numInputs, GameFacade.NUM_DIRS, 0));

		ArrayList<Genotype<TWEANN>> ghostMonitorExamples = new ArrayList<Genotype<TWEANN>>(
				CommonConstants.numActiveGhosts);
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
	
	public static void setupCooperativeCoevolutionSelectorForMsPacman() throws NoSuchMethodException {
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		MMNEAT.setNNInputParameters(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.pacmanInputOutputMediator.numOut());
		// subcontrollers and selector and possibly blueprints
		MMNEAT.genotypeExamples = new ArrayList<Genotype>(MMNEAT.modesToTrack + 2); 
		MMNEAT.genotypeExamples.add(new TWEANNGenotype(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.modesToTrack, 0));
		MMNEAT.coevolutionMediators = new MsPacManControllerInputOutputMediator[MMNEAT.modesToTrack];
		for (int i = 1; i <= MMNEAT.modesToTrack; i++) {
			MMNEAT.coevolutionMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation
					.createObject("pacManMediatorClass" + i);
			MMNEAT.genotypeExamples.add(new TWEANNGenotype(MMNEAT.coevolutionMediators[i - 1].numIn(), GameFacade.NUM_DIRS, i));
		}

		MMNEAT.prepareCoevolutionArchetypes();
		// Now the blueprints come in
		if (MMNEAT.task instanceof CooperativeBlueprintSubtaskMsPacManTask) {
			MMNEAT.blueprints = true;
			MMNEAT.genotypeExamples.add(new SimpleBlueprintGenotype(MMNEAT.modesToTrack + 1)); // subcontrollers
																					// and
																					// selector
		}
	}
	
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
	
	public static void setupCooperativeCoevolutionCombinerForMsPacman() throws NoSuchMethodException {
		boolean includeInputs = Parameters.parameters.booleanParameter("subsumptionIncludesInputs");
		int outputsPerSubnet = GameFacade.NUM_DIRS;
		// Use the specified mesiator, but add required subnet blocks to it
		// later
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		MMNEAT.coevolutionMediators = new MsPacManControllerInputOutputMediator[MMNEAT.modesToTrack];
		int numInputs = MMNEAT.pacmanInputOutputMediator.numIn();
		for (int i = 1; i <= MMNEAT.modesToTrack; i++) {
			MMNEAT.coevolutionMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation
					.createObject("pacManMediatorClass" + i);
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
			TWEANNGenotype tg = (TWEANNGenotype) MMNEAT.genotypeExamples.get(i + 1); // skip
																				// combiner
			((BlockLoadedInputOutputMediator) MMNEAT.pacmanInputOutputMediator).blocks
					.add(new SubNetworkBlock(tg.getPhenotype(), MMNEAT.coevolutionMediators[i],
							MMNEAT.coevolutionMediators[i].getClass().getSimpleName() + "[" + i + "]", includeInputs));
		}
	}
	
	public static void setupCooperativeCoevolutionNonHierarchicalForMsPacman() throws NoSuchMethodException {
		MMNEAT.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		MMNEAT.setNNInputParameters(MMNEAT.pacmanInputOutputMediator.numIn(), MMNEAT.pacmanInputOutputMediator.numOut());

		CooperativeNonHierarchicalMultiNetMsPacManTask theTask = (CooperativeNonHierarchicalMultiNetMsPacManTask) MMNEAT.task;
		int pops = theTask.numberOfPopulations();
		MMNEAT.genotypeExamples = new ArrayList<Genotype>(pops);
		boolean specializeMediators = !Parameters.parameters.booleanParameter("defaultMediator");
		for (int i = 1; i <= pops; i++) {
			if (specializeMediators) {
				theTask.inputMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation
						.createObject("pacManMediatorClass" + i);
			}
			MMNEAT.genotypeExamples.add(new TWEANNGenotype(theTask.inputMediators[i - 1].numIn(), GameFacade.NUM_DIRS, i - 1));
		}
		MMNEAT.prepareCoevolutionArchetypes();
	}
	
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
