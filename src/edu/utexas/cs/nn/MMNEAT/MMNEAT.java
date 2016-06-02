package edu.utexas.cs.nn.MMNEAT;

import edu.utexas.cs.nn.breve2D.dynamics.Breve2DDynamics;
import edu.utexas.cs.nn.data.ResultSummaryUtilities;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.GenerationalEA;
import edu.utexas.cs.nn.evolution.crossover.Crossover;
import edu.utexas.cs.nn.evolution.crossover.network.CombiningTWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HierarchicalTWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.SimpleBlueprintGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.pool.GenotypePool;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.evolution.metaheuristics.*;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.experiment.Experiment;
import edu.utexas.cs.nn.experiment.SinglePopulationGenerationalEAExperiment;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.log.EvalLog;
import edu.utexas.cs.nn.log.MONELog;
import edu.utexas.cs.nn.log.PerformanceLog;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.MultiplePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.tasks.breve2D.Breve2DTask;
import edu.utexas.cs.nn.tasks.breve2D.NNBreve2DMonster;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyController;
import edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.TorusPredPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativePredatorsVsStaticPrey;
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativePreyVsStaticPredators;
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativeTorusPredPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.sensors.TorusPredPreySensorBlock;
import edu.utexas.cs.nn.tasks.motests.FunctionOptimization;
import edu.utexas.cs.nn.tasks.motests.testfunctions.FunctionOptimizationSet;
import edu.utexas.cs.nn.tasks.mspacman.*;
import edu.utexas.cs.nn.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.utexas.cs.nn.tasks.mspacman.facades.ExecutorFacade;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.GhostMonitorNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.SubNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts.GhostControllerInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts.mediators.GhostsCheckEachDirectionMediator;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueEnvironment;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;
import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.utexas.cs.nn.tasks.testmatch.MatchDataTask;
import edu.utexas.cs.nn.tasks.ut2004.UT2004Task;
import edu.utexas.cs.nn.tasks.vizdoom.VizDoomTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.random.RandomGenerator;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.stats.Statistic;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import pacman.Executor;
import pacman.game.Constants;
import wox.serial.Easy;

/**
 *
 * @author Jacob Schrum
 */
public class MMNEAT {

	public static boolean seedExample = false;
	public static int networkInputs = 0;
	public static int networkOutputs = 0;
	public static int modesToTrack = 0;
	public static double[] lowerInputBounds;
	public static double[] upperInputBounds;
	public static int[] discreteCeilings;
	public static Experiment experiment;
	public static Task task;
	public static GenerationalEA ea;
	public static Genotype genotype;
	public static ArrayList<Genotype> genotypeExamples;
	public static Crossover crossoverOperator;
	public static FunctionOptimizationSet fos;
	public static RLGlueEnvironment rlGlueEnvironment;
	public static ArrayList<Metaheuristic> metaheuristics;
	public static ArrayList<String> fitnessFunctions;
	public static ArrayList<Statistic> aggregationOverrides;
	public static TaskSpec tso;
	public static FeatureExtractor rlGlueExtractor;
	public static boolean blueprints = false;
	public static PerformanceLog performanceLog;
	public static MsPacManControllerInputOutputMediator pacmanInputOutputMediator;
	public static GhostControllerInputOutputMediator ghostsInputOutputMediator;
	public static MsPacManControllerInputOutputMediator[] coevolutionMediators = null;
	public static MsPacManEnsembleArbitrator ensembleArbitrator = null;
	private static int actualFitnessFunctions = 0;
	public static MsPacManModeSelector pacmanMultitaskScheme = null;
	public static VariableDirectionBlock directionalSafetyFunction;
	public static TWEANNGenotype sharedMultitaskNetwork = null;
	public static TWEANNGenotype sharedPreferenceNetwork = null;
	public static EvalLog evalReport = null;
	public static RandomGenerator weightPerturber = null;
	public static MONELog ghostLocationsOnPowerPillEaten = null;

	public static ArrayList<String> fitnessPlusMetaheuristics() {
		ArrayList<String> result = (ArrayList<String>) fitnessFunctions.clone();
		ArrayList<String> meta = new ArrayList<String>();
		for (Metaheuristic m : metaheuristics) {
			meta.add(m.getClass().getSimpleName());
		}
		result.addAll(actualFitnessFunctions, meta);
		return result;
	}

	private static void setupSaveDirectory() {
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		if (Parameters.parameters.booleanParameter("io") && !saveTo.isEmpty()) {
			String directory = FileUtilities.getSaveDirectory();
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdir();
			}
		}
	}

	private static void setupCrossover() throws NoSuchMethodException {
		// Crossover operator
		if (Parameters.parameters.booleanParameter("mating")) {
			crossoverOperator = (Crossover) ClassCreation.createObject("crossover");
		}
	}

	private static void setupRLGlue() throws NoSuchMethodException {
		// RL-Glue environment, if RL-Glue is being used
		rlGlueEnvironment = (RLGlueEnvironment) ClassCreation.createObject("rlGlueEnvironment");
		if (rlGlueEnvironment != null) {
			System.out.println("Define RL-Glue Task Spec");
			tso = rlGlueEnvironment.makeTaskSpec();
			rlGlueExtractor = (FeatureExtractor) ClassCreation.createObject("rlGlueExtractor");
		}
	}

	private static void setupFunctionOptimization() throws NoSuchMethodException {
		// Function minimization benchmarks, if they are used
		fos = (FunctionOptimizationSet) ClassCreation.createObject("fos");
		if (Parameters.parameters.booleanParameter("lengthDependentMutationRate") && fos != null) {
			Parameters.parameters.setDouble("realMutateRate", 1.0 / fos.getLowerBounds().length);
		}
	}

	public static void setupMsPacmanParameters() {
		if (Parameters.parameters.booleanParameter("logGhostLocOnPowerPill")) {
			ghostLocationsOnPowerPillEaten = new MONELog("PowerPillToGhostLocationMapping");
		}

		Constants.NUM_LIVES = Parameters.parameters.integerParameter("pacmanLives");
		Constants.EDIBLE_TIME = Parameters.parameters.integerParameter("edibleTime");
		if (Parameters.parameters.booleanParameter("incrementallyDecreasingEdibleTime")) {
			MMNEAT.setEdibleTimeBasedOnGeneration(Parameters.parameters.integerParameter("lastSavedGeneration"));
		}
		Constants.COMMON_LAIR_TIME = Parameters.parameters.integerParameter("lairTime");
		if (Parameters.parameters.booleanParameter("incrementallyDecreasingLairTime")) {
			MMNEAT.setLairTimeBasedOnGeneration(Parameters.parameters.integerParameter("lastSavedGeneration"));
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

	private static void setupGenotypePoolsForMsPacman() {
		// Use genotype pools
		if (Parameters.parameters.classParameter("genotype").equals(HierarchicalTWEANNGenotype.class)) {
			System.out.println("Ghost eating pool");
			GenotypePool.addPool(Parameters.parameters.stringParameter("ghostEatingSubnetworkDir"));
			System.out.println("Pill eating pool");
			GenotypePool.addPool(Parameters.parameters.stringParameter("pillEatingSubnetworkDir"));

			discreteCeilings = new int[2];
			discreteCeilings[0] = GenotypePool.poolSize(0);
			discreteCeilings[1] = GenotypePool.poolSize(1);
		}
	}

	private static void setupCooperativeCoevolutionGhostMonitorsForMsPacman() throws NoSuchMethodException {
		boolean includeInputs = Parameters.parameters.booleanParameter("subsumptionIncludesInputs");
		int outputsPerMonitor = GameFacade.NUM_DIRS;
		boolean ghostMonitorsSensePills = Parameters.parameters.booleanParameter("ghostMonitorsSensePills");
		// Use the specified mediator, but add the required ghost monitor blocks
		// to it later
		int ghostMonitorInputs = (ghostMonitorsSensePills ? new OneGhostAndPillsMonitorInputOutputMediator(0)
				: new OneGhostMonitorInputOutputMediator(0)).numIn();
		pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		int numInputs = pacmanInputOutputMediator.numIn() + (CommonConstants.numActiveGhosts
				* (includeInputs ? outputsPerMonitor + ghostMonitorInputs : outputsPerMonitor));
		setNNInputParameters(numInputs, GameFacade.NUM_DIRS);

		genotypeExamples = new ArrayList<Genotype>(CommonConstants.numActiveGhosts + 1);
		genotypeExamples.add(new TWEANNGenotype(numInputs, GameFacade.NUM_DIRS, 0));

		ArrayList<Genotype<TWEANN>> ghostMonitorExamples = new ArrayList<Genotype<TWEANN>>(
				CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			TWEANNGenotype tg = new TWEANNGenotype(ghostMonitorInputs, outputsPerMonitor, (i + 1));
			genotypeExamples.add(tg);
			ghostMonitorExamples.add(tg);
		}
		prepareCoevolutionArchetypes();
		// Needed so that sensor and output labels can be retrieved
		for (int i = 0; i < ghostMonitorExamples.size(); i++) {
			((BlockLoadedInputOutputMediator) pacmanInputOutputMediator).blocks
					.add(new GhostMonitorNetworkBlock((TWEANNGenotype) ghostMonitorExamples.get(i), includeInputs, i));
		}
	}

	/**
	 * Assumes the subnets are always in SubNetworkBlocks at the end of a
	 * BlockLoadedInputOutputMediator, so the nets to replace are found based on
	 * the length of the input subnets.
	 */
	public static <T> void replaceSubnets(ArrayList<Genotype<T>> subnets) {
		BlockLoadedInputOutputMediator blockMediator = ((BlockLoadedInputOutputMediator) pacmanInputOutputMediator);
		int numBlocks = blockMediator.blocks.size();
		for (int i = 0; i < subnets.size(); i++) {
			((SubNetworkBlock) blockMediator.blocks.get(numBlocks - subnets.size() + i)).changeNetwork(subnets.get(i));
		}
	}

	private static void setupCooperativeCoevolutionSelectorForMsPacman() throws NoSuchMethodException {
		pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		setNNInputParameters(pacmanInputOutputMediator.numIn(), pacmanInputOutputMediator.numOut());
		// subcontrollers and selector and possibly blueprints
		genotypeExamples = new ArrayList<Genotype>(modesToTrack + 2); 
		genotypeExamples.add(new TWEANNGenotype(pacmanInputOutputMediator.numIn(), modesToTrack, 0));
		coevolutionMediators = new MsPacManControllerInputOutputMediator[modesToTrack];
		for (int i = 1; i <= modesToTrack; i++) {
			coevolutionMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation
					.createObject("pacManMediatorClass" + i);
			genotypeExamples.add(new TWEANNGenotype(coevolutionMediators[i - 1].numIn(), GameFacade.NUM_DIRS, i));
		}

		prepareCoevolutionArchetypes();
		// Now the blueprints come in
		if (task instanceof CooperativeBlueprintSubtaskMsPacManTask) {
			blueprints = true;
			genotypeExamples.add(new SimpleBlueprintGenotype(modesToTrack + 1)); // subcontrollers
																					// and
																					// selector
		}
	}

	private static void setupCooperativeCoevolutionCheckEachMultitaskPreferenceNetForMsPacman() throws NoSuchMethodException {
		pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
		MMNEAT.modesToTrack = CommonConstants.multitaskModules;
		// Setup the preference net settings
		CommonConstants.multitaskModules = 1; // Needed before set NN params
		setNNInputParameters(pacmanInputOutputMediator.numIn(), MMNEAT.modesToTrack);
		CommonConstants.multitaskModules = MMNEAT.modesToTrack; // Restore value

		genotypeExamples = new ArrayList<Genotype>(2);
		// Multitask
		genotypeExamples.add(new TWEANNGenotype(pacmanInputOutputMediator.numIn(), modesToTrack, CommonConstants.fs, ActivationFunctions.newNodeFunction(), modesToTrack, 0));
		// Pref Net
		genotypeExamples.add(new TWEANNGenotype(pacmanInputOutputMediator.numIn(), modesToTrack, CommonConstants.fs, ActivationFunctions.newNodeFunction(), 1, 1));

		prepareCoevolutionArchetypes();
	}

	private static void setupCooperativeCoevolutionCombinerForMsPacman() throws NoSuchMethodException {
		boolean includeInputs = Parameters.parameters.booleanParameter("subsumptionIncludesInputs");
		int outputsPerSubnet = GameFacade.NUM_DIRS;
		// Use the specified mesiator, but add required subnet blocks to it
		// later
		pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		coevolutionMediators = new MsPacManControllerInputOutputMediator[modesToTrack];
		int numInputs = pacmanInputOutputMediator.numIn();
		for (int i = 1; i <= modesToTrack; i++) {
			coevolutionMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation
					.createObject("pacManMediatorClass" + i);
			numInputs += outputsPerSubnet + (includeInputs ? coevolutionMediators[i - 1].numIn() : 0);
		}
		setNNInputParameters(numInputs, GameFacade.NUM_DIRS);

		genotypeExamples = new ArrayList<Genotype>(modesToTrack + 1);
		genotypeExamples.add(new TWEANNGenotype(numInputs, GameFacade.NUM_DIRS, 0));
		for (int i = 0; i < modesToTrack; i++) {
			TWEANNGenotype tg = new TWEANNGenotype(coevolutionMediators[i].numIn(), outputsPerSubnet, (i + 1));
			genotypeExamples.add(tg);
		}
		prepareCoevolutionArchetypes();
		// Needed so that sensor and output labels can be retrieved
		for (int i = 0; i < coevolutionMediators.length; i++) {
			TWEANNGenotype tg = (TWEANNGenotype) genotypeExamples.get(i + 1); // skip
																				// combiner
			((BlockLoadedInputOutputMediator) pacmanInputOutputMediator).blocks
					.add(new SubNetworkBlock(tg.getPhenotype(), coevolutionMediators[i],
							coevolutionMediators[i].getClass().getSimpleName() + "[" + i + "]", includeInputs));
		}
	}

	private static void setupCooperativeCoevolutionNonHierarchicalForMsPacman() throws NoSuchMethodException {
		pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
				.createObject("pacmanInputOutputMediator");
		setNNInputParameters(pacmanInputOutputMediator.numIn(), pacmanInputOutputMediator.numOut());

		CooperativeNonHierarchicalMultiNetMsPacManTask theTask = (CooperativeNonHierarchicalMultiNetMsPacManTask) task;
		int pops = theTask.numberOfPopulations();
		genotypeExamples = new ArrayList<Genotype>(pops);
		boolean specializeMediators = !Parameters.parameters.booleanParameter("defaultMediator");
		for (int i = 1; i <= pops; i++) {
			if (specializeMediators) {
				theTask.inputMediators[i - 1] = (MsPacManControllerInputOutputMediator) ClassCreation
						.createObject("pacManMediatorClass" + i);
			}
			genotypeExamples.add(new TWEANNGenotype(theTask.inputMediators[i - 1].numIn(), GameFacade.NUM_DIRS, i - 1));
		}
		prepareCoevolutionArchetypes();
	}

	private static void setupMetaHeuristics() {
		// Metaheuristics are objectives that are not associated with the
		// domain/task
		System.out.println("Use meta-heuristics?");
		metaheuristics = new ArrayList<Metaheuristic>();
		if (Parameters.parameters.booleanParameter("penalizeModeWaste")) {
			System.out.println("Penalize Mode Waste");
			metaheuristics.add(new WastedModeUsageFitness());
		}
		if (Parameters.parameters.booleanParameter("antiMaxModeUsage")) {
			System.out.println("Penalize Max Mode Usage");
			metaheuristics.add(new AntiMaxModuleUsageFitness());
		}
		if (Parameters.parameters.integerParameter("numModesToPrefer") > 0) {
			int target = Parameters.parameters.integerParameter("numModesToPrefer");
			System.out.println("Prefer even usage of " + target + " modes");
			metaheuristics.add(new FavorXModulesFitness(target));
		}
		if (Parameters.parameters.booleanParameter("penalizeLinks")) {
			System.out.println("Penalize Links");
			metaheuristics.add(new LinkPenalty());
		}
		if (Parameters.parameters.booleanParameter("maximizeModes")) {
			System.out.println("Maximize Modes");
			metaheuristics.add(new MaxModulesFitness());
		}
	}

	private static void setupMultitaskSeedPopulationForMsPacman(String ghostDir, String pillDir) {
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

	private static void setupSingleMultitaskSeedForMsPacman() {
		Genotype<TWEANN> ghostEating = EvolutionaryHistory
				.getSubnetwork(Parameters.parameters.stringParameter("ghostEatingSubnetwork"));
		Genotype<TWEANN> pillEating = EvolutionaryHistory
				.getSubnetwork(Parameters.parameters.stringParameter("pillEatingSubnetwork"));
		CombiningTWEANNCrossover cross = new CombiningTWEANNCrossover(true, true);
		// Copy assures a fresh genotype id
		genotype = cross.crossover(ghostEating, pillEating).copy();
		seedExample = true;
	}

	private static void setupTWEANNGenotypeDataTracking(boolean coevolution) {
		if (genotype instanceof TWEANNGenotype) {
			if (Parameters.parameters.booleanParameter("io")
					&& Parameters.parameters.booleanParameter("logTWEANNData")) {
				System.out.println("Init TWEANN Log");
				EvolutionaryHistory.initTWEANNLog();
			}
			if (!coevolution) {
				EvolutionaryHistory.initArchetype(0);
			}

			long biggestInnovation = ((TWEANNGenotype) genotype).biggestInnovation();
			if (biggestInnovation > EvolutionaryHistory.largestUnusedInnovationNumber) {
				EvolutionaryHistory.setInnovation(biggestInnovation + 1);
			}
		}
	}

	private static void prepareCoevolutionArchetypes() {
		for (int i = 0; i < genotypeExamples.size(); i++) {
			String archetypeFile = Parameters.parameters.stringParameter("seedArchetype" + (i + 1));
			if (!EvolutionaryHistory.archetypeFileExists(i) && archetypeFile != null && !archetypeFile.isEmpty()) {
				System.out.println("Using seed archetype: " + archetypeFile);
				EvolutionaryHistory.initArchetype(i, archetypeFile);
			} else {
				System.out.println("New or resumed archetype");
				EvolutionaryHistory.initArchetype(i);
			}
			System.out.println("---------------------------------------------");
		}
	}

	public static boolean taskHasSubnetworks() {
		return CooperativeSubtaskSelectorMsPacManTask.class.equals(Parameters.parameters.classParameter("task"))
				|| CooperativeSubtaskCombinerMsPacManTask.class.equals(Parameters.parameters.classParameter("task"));
	}

	public MMNEAT(String[] args) {
		Parameters.initializeParameterCollections(args);
	}

	public MMNEAT(String parameterFile) {
		Parameters.initializeParameterCollections(parameterFile);
	}

	public static void registerFitnessFunction(String name) {
		registerFitnessFunction(name, null, true);
	}

	public static void registerFitnessFunction(String name, boolean affectsSelection) {
		registerFitnessFunction(name, null, affectsSelection);
	}

	public static void registerFitnessFunction(String name, Statistic override, boolean affectsSelection) {
		if (affectsSelection) {
			actualFitnessFunctions++;
		}
		fitnessFunctions.add(name);
		aggregationOverrides.add(override);
	}

	public static void loadClasses() {
		try {
			ActivationFunctions.initFunctionSet();
			setupSaveDirectory();

			fitnessFunctions = new ArrayList<String>();
			aggregationOverrides = new ArrayList<Statistic>();

			boolean loadFrom = !Parameters.parameters.stringParameter("loadFrom").equals("");
			System.out.println("Init Genotype Ids");
			EvolutionaryHistory.initGenotypeIds();
			weightPerturber = (RandomGenerator) ClassCreation.createObject("weightPerturber");

			setupCrossover();
			setupRLGlue();
			setupFunctionOptimization();

			// A task is always required
			System.out.println("Set Task");
			// modesToTrack has to be set before task initialization
			if (taskHasSubnetworks()) {
				modesToTrack = Parameters.parameters.integerParameter("numCoevolutionSubpops");
			} else {
				int multitaskModes = CommonConstants.multitaskModules;
				if (!CommonConstants.hierarchicalMultitask && multitaskModes > 1) {
					modesToTrack = multitaskModes;
				}
			}
			task = (Task) ClassCreation.createObject("task");
			boolean coevolution = false;
			// For all types of Ms Pac-Man tasks
			if (Parameters.parameters.booleanParameter("scalePillsByGen")
					&& Parameters.parameters.stringParameter("lastSavedDirectory").equals("")
					&& Parameters.parameters.integerParameter("lastSavedGeneration") == 0) {
				System.out.println("Set pre-eaten pills high, since we are scaling pills with generation");
				Parameters.parameters.setDouble("preEatenPillPercentage", 0.999);
			}

			if (task instanceof MsPacManTask) {
				setupGenotypePoolsForMsPacman();
				System.out.println("Setup Ms. Pac-Man Task");
				pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation
						.createObject("pacmanInputOutputMediator");
				if (MMNEAT.pacmanInputOutputMediator instanceof VariableDirectionBlockLoadedInputOutputMediator) {
					directionalSafetyFunction = (VariableDirectionBlock) ClassCreation
							.createObject("directionalSafetyFunction");
					ensembleArbitrator = (MsPacManEnsembleArbitrator) ClassCreation.createObject("ensembleArbitrator");
				}
				String preferenceNet = Parameters.parameters.stringParameter("fixedPreferenceNetwork");
				String multitaskNet = Parameters.parameters.stringParameter("fixedMultitaskPolicy");
				if (multitaskNet != null && !multitaskNet.isEmpty()) {
					// Preference networks are being evolved to pick outputs of
					// fixed multitask network
					MMNEAT.sharedMultitaskNetwork = (TWEANNGenotype) Easy.load(multitaskNet);
					if (CommonConstants.showNetworks) {
						DrawingPanel panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM,
								"Fixed Multitask Network");
						MMNEAT.sharedMultitaskNetwork.getPhenotype().draw(panel);
					}
					// One preference neuron per multitask mode
					setNNInputParameters(pacmanInputOutputMediator.numIn(), MMNEAT.sharedMultitaskNetwork.numModules);
				} else if (preferenceNet != null && !preferenceNet.isEmpty()) {
					MMNEAT.sharedPreferenceNetwork = (TWEANNGenotype) Easy.load(preferenceNet);
					if (CommonConstants.showNetworks) {
						DrawingPanel panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM,
								"Fixed Preference Network");
						MMNEAT.sharedPreferenceNetwork.getPhenotype().draw(panel);
					}
					// One preference neuron per multitask mode
					setNNInputParameters(pacmanInputOutputMediator.numIn(), MMNEAT.sharedPreferenceNetwork.numOut);
				} else if (Parameters.parameters.booleanParameter("evolveGhosts")) {
					System.out.println("Evolving the Ghosts!");
					ghostsInputOutputMediator = new GhostsCheckEachDirectionMediator();
					setNNInputParameters(ghostsInputOutputMediator.numIn(), ghostsInputOutputMediator.numOut());
				} else {
					// Regular Check-Each-Direction networks
					setNNInputParameters(pacmanInputOutputMediator.numIn(), pacmanInputOutputMediator.numOut());
				}
				setupMsPacmanParameters();
				if (CommonConstants.multitaskModules > 1) {
					pacmanMultitaskScheme = (MsPacManModeSelector) ClassCreation.createObject("pacmanMultitaskScheme");
				}
			} else if (task instanceof CooperativeMsPacManTask) {
				System.out.println("Setup Coevolution Ms. Pac-Man Task");
				coevolution = true;
				// Is this next line redundant
				EvolutionaryHistory.initInnovationHistory();
				setupMsPacmanParameters();
				if (task instanceof CooperativeGhostMonitorNetworksMsPacManTask) {
					setupCooperativeCoevolutionGhostMonitorsForMsPacman();
				} else if (task instanceof CooperativeSubtaskSelectorMsPacManTask) {
					setupCooperativeCoevolutionSelectorForMsPacman();
				} else if (task instanceof CooperativeSubtaskCombinerMsPacManTask) {
					setupCooperativeCoevolutionCombinerForMsPacman();
				} else if (task instanceof CooperativeNonHierarchicalMultiNetMsPacManTask) {
					setupCooperativeCoevolutionNonHierarchicalForMsPacman();
				} else if (task instanceof CooperativeCheckEachMultitaskSelectorMsPacManTask) {
					setupCooperativeCoevolutionCheckEachMultitaskPreferenceNetForMsPacman();
				}
			} else if (task instanceof RLGlueTask) {
				RLGlueTask rlTask = (RLGlueTask) task;
				setNNInputParameters(rlGlueExtractor.numFeatures(), rlTask.agent.getNumberOutputs());
			} else if (task instanceof Breve2DTask) {
				System.out.println("Setup Breve 2D Task");
				Breve2DDynamics dynamics = (Breve2DDynamics) ClassCreation.createObject("breveDynamics");
				setNNInputParameters(dynamics.numInputSensors(), NNBreve2DMonster.NUM_OUTPUTS);
			} else if (task instanceof TorusPredPreyTask) {
				System.out.println("Setup Torus Predator/Prey Task");
				int numInputs = determineNumPredPreyInputs();
				NetworkTask t = (NetworkTask) task;
				setNNInputParameters(numInputs, t.outputLabels().length);
			} else if (task instanceof CooperativeTorusPredPreyTask) {
				System.out.println("Setup Cooperative Torus Predator/Prey Task");
				coevolution = true;
				int numInputs = determineNumPredPreyInputs();
				NetworkTask t = (NetworkTask) task;
				setNNInputParameters(numInputs, t.outputLabels().length);
				// Setup genotype early
				genotype = (Genotype) ClassCreation.createObject("genotype");
				int numAgents = (task instanceof CooperativePredatorsVsStaticPrey) ? Parameters.parameters.integerParameter("torusPredators") : Parameters.parameters.integerParameter("torusPreys");
				genotypeExamples = new ArrayList<Genotype>(numAgents);
				for(int i = 0; i < numAgents; i++) {
					if(genotype instanceof TWEANNGenotype) {
						((TWEANNGenotype) genotype).archetypeIndex = i;
					}
					genotypeExamples.add(genotype.newInstance());
				}
				prepareCoevolutionArchetypes();
			} else if (task instanceof UT2004Task) {
				System.out.println("Setup UT2004 Task");
				UT2004Task utTask = (UT2004Task) task;
				setNNInputParameters(utTask.sensorModel.numberOfSensors(), utTask.outputModel.numberOfOutputs());
			} else if (task instanceof MatchDataTask) {
				System.out.println("Setup Match Data Task");
				MatchDataTask t = (MatchDataTask) task;
				setNNInputParameters(t.numInputs(), t.numOutputs());
			} else if (task instanceof VizDoomTask) {
				System.out.println("Set up VizDoom Task");
				VizDoomTask t = (VizDoomTask) task;
				setNNInputParameters(t.numInputs(), t.numActions());
			} else if (task == null) {
				// this else statement should only happen for JUnit testing cases.
				// Some default network setup is needed.
				setNNInputParameters(5, 3);
			} else {
				System.out.println("A valid task must be specified!");
				System.out.println(task);
				System.exit(1);
			}

			// Changes network input setting to HyperNEAT settings
			if (CommonConstants.hyperNEAT) {
				System.out.println("Using HyperNEAT");
				hyperNEATOverrides();
			}

			setupMetaHeuristics();
			// An EA is always needed. Currently only GenerationalEA classes are
			// supported
			if (!loadFrom) {
				System.out.println("Create EA");
				ea = (GenerationalEA) ClassCreation.createObject("ea");
			}
			// A Genotype to evolve with is always needed
			System.out.println("Example genotype");
			String seedGenotype = Parameters.parameters.stringParameter("seedGenotype");
			if (task instanceof MsPacManTask && Parameters.parameters.booleanParameter("pacmanMultitaskSeed")
					&& CommonConstants.multitaskModules == 2) {
				System.out.println("Seed genotype is combo of networks");

				String ghostDir = Parameters.parameters.stringParameter("ghostEatingSubnetworkDir");
				String pillDir = Parameters.parameters.stringParameter("pillEatingSubnetworkDir");
				String lastSavedDirectory = Parameters.parameters.stringParameter("lastSavedDirectory");
				if (lastSavedDirectory.isEmpty()) {
					if (!ghostDir.isEmpty() && !pillDir.isEmpty()) {
						setupMultitaskSeedPopulationForMsPacman(ghostDir, pillDir);
					} else {
						setupSingleMultitaskSeedForMsPacman();
					}
				}

				// Revise settings to accommodate multitask seed
				System.out.println("Revising network info based on multitask seed");
				MsPacManControllerInputOutputMediator ghostMediator = (MsPacManControllerInputOutputMediator) ClassCreation
						.createObject("pacManMediatorClass1");
				MsPacManControllerInputOutputMediator pillMediator = (MsPacManControllerInputOutputMediator) ClassCreation
						.createObject("pacManMediatorClass2");
				pacmanInputOutputMediator = new MultipleInputOutputMediator(
						new MsPacManControllerInputOutputMediator[] { ghostMediator, pillMediator });
				setNNInputParameters(pacmanInputOutputMediator.numIn(), pacmanInputOutputMediator.numOut());
			} else if (seedGenotype.isEmpty()) {
				genotype = (Genotype) ClassCreation.createObject("genotype");
			} else {
				// Copy assures a fresh genotype id
				System.out.println("Loading seed genotype: " + seedGenotype);
				genotype = ((Genotype) Easy.load(seedGenotype)).copy();
				// System.out.println(genotype);
				seedExample = true;
			}
			setupTWEANNGenotypeDataTracking(coevolution);
			// An Experiment is always needed
			System.out.println("Create Experiment");
			experiment = (Experiment) ClassCreation.createObject("experiment");
			experiment.init();
			if (!loadFrom && Parameters.parameters.booleanParameter("io")) {
				if (Parameters.parameters.booleanParameter("logPerformance") && !coevolution) {
					performanceLog = new PerformanceLog("Performance");
				}
				if (Parameters.parameters.booleanParameter("logMutationAndLineage")) {
					EvolutionaryHistory.initLineageAndMutationLogs();
				}
			}
		} catch (Exception ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		}
	}

	/**
	 * Using HyperNEAT means certain parameters values need to be overridden
	 * @throws NoSuchMethodException 
	 */
	private static void hyperNEATOverrides() throws NoSuchMethodException {
		// Cannot monitor inputs with HyperNEAT because the NetworkTask
		// interface no longer applies
		CommonConstants.monitorInputs = false;
		Parameters.parameters.setBoolean("monitorInputs", false);
		HyperNEATTask hnt = (HyperNEATTask) task;
		setNNInputParameters(HyperNEATTask.NUM_CPPN_INPUTS, hnt.getSubstrateConnectivity().size());
	}

	/**
	 * finds the number of inputs for the predPrey task, which is based on the
	 * type of agent that is being evolved's sensor inputs defined in its
	 * controller This has to be done to prevent a null pointer exception when
	 * first getting the sensor labels/number of sensors
	 * 
	 * @return numInputs
	 */
	private static int determineNumPredPreyInputs() {
		boolean isPredator = task instanceof TorusEvolvedPredatorsVsStaticPreyTask;
		NNTorusPredPreyController temp = new NNTorusPredPreyController(null, isPredator);
		return temp.getNumInputs();
	}

	public static void clearClasses() {
		rlGlueEnvironment = null;
		task = null;
		fos = null;
		metaheuristics = null;
		fitnessFunctions = null;
		aggregationOverrides = null;
		ea = null;
		genotype = null;
		experiment = null;
		performanceLog = null;
		EvolutionaryHistory.archetypes = null;
		Executor.close();
	}

	public void run() {
		System.out.println("Run:");
		clearClasses();
		loadClasses();

		if (Parameters.parameters.booleanParameter("io")) {
			Parameters.parameters.saveParameters();
		}
		System.out.println("Run");
		experiment.run();
		System.out.println("Experiment finished");
	}

	public static void process(int runs) throws FileNotFoundException, NoSuchMethodException {
		try {
			task = (Task) ClassCreation.createObject("task");
		} catch (NoSuchMethodException ex) {
			System.out.println("Failed to instantiate task " + Parameters.parameters.classParameter("task"));
			System.exit(1);
		}
		ResultSummaryUtilities.processExperiment(
				Parameters.parameters.stringParameter("base") + "/" + Parameters.parameters.stringParameter("saveTo"),
				Parameters.parameters.stringParameter("log"), runs, Parameters.parameters.integerParameter("maxGens"),
				"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_log.txt",
				"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_gen",
				Parameters.parameters.stringParameter("base"));
	}

	public static void calculateHVs(int runs) throws FileNotFoundException {
		try {
			task = (Task) ClassCreation.createObject("task");
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(MMNEAT.class.getName()).log(Level.SEVERE, null, ex);
		}
		ResultSummaryUtilities.hypervolumeProcessing(
				Parameters.parameters.stringParameter("base") + "/" + Parameters.parameters.stringParameter("saveTo"),
				runs, Parameters.parameters.stringParameter("log"),
				"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_gen",
				Parameters.parameters.integerParameter("maxGens"), Parameters.parameters.stringParameter("base"));
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		if (args.length == 0) {
			System.out.println("First command line parameter must be one of the following:");
			System.out.println("\tmultiple:n\twhere n is the number of experiments to run in sequence");
			System.out.println("\trunNumber:n\twhere n is the specific experiment number to assign");
			System.out.println("\tprocess:n\twhere n is the number of experiments to do data processing on");
			System.out.println("\tlineage:n\twhere n is the experiment number to do lineage browsing on");
			System.exit(0);
		}
		long start = System.currentTimeMillis();
		// Executor.main(args);
		StringTokenizer st = new StringTokenizer(args[0], ":");
		if (args[0].startsWith("multiple:")) {
			st.nextToken(); // "multiple"
			String value = st.nextToken();

			int runs = Integer.parseInt(value);
			for (int i = 0; i < runs; i++) {
				args[0] = "runNumber:" + i;
				evolutionaryRun(args);
			}
			process(runs);
		} else if (args[0].startsWith("hv:")) {
			st.nextToken(); // "hv"
			String value = st.nextToken();

			int runs = Integer.parseInt(value);
			args[0] = "runNumber:0";
			Parameters.initializeParameterCollections(args); // file should
			// exist
			loadClasses();
			calculateHVs(runs);
		} else if (args[0].startsWith("lineage:")) {
			System.out.println("Lineage browser");
			st.nextToken(); // "lineage"
			String value = st.nextToken();

			int run = Integer.parseInt(value);
			args[0] = "runNumber:" + run;
			Parameters.initializeParameterCollections(args); // file should
			// exist
			System.out.println("Params loaded");
			String saveTo = Parameters.parameters.stringParameter("saveTo");
			String loadFrom = Parameters.parameters.stringParameter("loadFrom");
			boolean includeChildren = false;
			if (loadFrom == null || loadFrom.equals("")) {
				loadFrom = saveTo;
				includeChildren = true;
			}
			Offspring.fillInLineage(Parameters.parameters.stringParameter("base"), saveTo, run,
					Parameters.parameters.stringParameter("log"), loadFrom, includeChildren);
			Offspring.browse();
		} else if (args[0].startsWith("process:")) {
			st.nextToken(); // "process"
			String value = st.nextToken();

			int runs = Integer.parseInt(value);
			args[0] = "runNumber:0";
			Parameters.initializeParameterCollections(args); // file should
			// exist
			loadClasses();
			process(runs);
		} else {
			evolutionaryRun(args);
		}
		System.out.println("done: " + (((System.currentTimeMillis() - start) / 1000.0) / 60.0) + " minutes");
		if (!(task instanceof FunctionOptimization)) {
			System.exit(0);
		}
	}

	private static void evolutionaryRun(String[] args) {
		// Commandline
		MMNEAT mone = new MMNEAT(args);
		if (CommonConstants.replayPacman) {
			if (CommonConstants.showNetworks) {
				String replayNetwork = Parameters.parameters.stringParameter("replayNetwork");
				FileUtilities.drawTWEANN(replayNetwork);
			}
			ExecutorFacade ef = new ExecutorFacade(new Executor());
			ef.replayGame(Parameters.parameters.stringParameter("pacmanSaveFile"), CommonConstants.watch);
			System.exit(1);
		}
		String branchRoot = Parameters.parameters.stringParameter("branchRoot");
		String lastSavedDirectory = Parameters.parameters.stringParameter("lastSavedDirectory");
		if (branchRoot != null && !branchRoot.isEmpty()
				&& (lastSavedDirectory == null || lastSavedDirectory.isEmpty())) {
			// This run is a branch off of another run.
			Parameters rootParameters = new Parameters(new String[0]);
			System.out.println("Loading root parameters from " + branchRoot);
			rootParameters.loadParameters(branchRoot);
			// Copy the needed parameters
			Parameters.parameters.setString("lastSavedDirectory", rootParameters.stringParameter("lastSavedDirectory"));
			Parameters.parameters.setString("archetype", rootParameters.stringParameter("archetype"));
			Parameters.parameters.setLong("lastInnovation", rootParameters.longParameter("lastInnovation"));
			Parameters.parameters.setLong("lastGenotypeId", rootParameters.longParameter("lastGenotypeId"));
		}
		RandomNumbers.reset();

		mone.run();

		closeLogs();
	}

	private static void closeLogs() {
		if (performanceLog != null) {
			performanceLog.close();
		}
		if (EvolutionaryHistory.tweannLog != null) {
			EvolutionaryHistory.tweannLog.close();
			EvolutionaryHistory.tweannLog = null;
		}
		if (EvolutionaryHistory.mutationLog != null) {
			EvolutionaryHistory.mutationLog.close();
			EvolutionaryHistory.mutationLog = null;
		}
		if (EvolutionaryHistory.lineageLog != null) {
			EvolutionaryHistory.lineageLog.close();
			EvolutionaryHistory.lineageLog = null;
		}
	}

	/**
	 * Signals that neural networks will be used, and sets them up
	 * 
	 * @param numIn
	 *            Number of task inputs to network
	 * @param numOut
	 *            Number of task outputs to network (not counting extra modes)
	 */
	private static void setNNInputParameters(int numIn, int numOut) throws NoSuchMethodException {
		networkInputs = numIn;
		networkOutputs = numOut;
		int multitaskModes = CommonConstants.multitaskModules;
		if (CommonConstants.hierarchicalMultitask) {
			multitaskModes = 1; // Initialize the network like a preference
			// neuron net instead
		}
		networkOutputs *= multitaskModes;
		System.out.println("Networks will have " + networkInputs + " inputs and " + networkOutputs + " outputs.");

		lowerInputBounds = new double[networkInputs];
		upperInputBounds = new double[networkInputs];
		for (int i = 0; i < networkInputs; i++) {
			lowerInputBounds[i] = -1.0;
			upperInputBounds[i] = 1.0;
		}
	}

	/**
	 * Write information to the performance log, if it is being used
	 * 
	 * @param combined
	 *            Combined population of scores/genotypes
	 * @param generation
	 *            Current generation information is being logged for
	 */

	public static <T> void logPerformanceInformation(ArrayList<Score<T>> combined, int generation) {
		if (performanceLog != null)
			performanceLog.log(combined, generation);
	}
}
