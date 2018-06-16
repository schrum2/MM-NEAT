package edu.southwestern.MMNEAT;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;

import edu.southwestern.boardGame.BoardGame;
import edu.southwestern.boardGame.TwoDimensionalBoardGame;
import edu.southwestern.boardGame.TwoDimensionalBoardGameViewer;
import edu.southwestern.breve2D.dynamics.Breve2DDynamics;
import edu.southwestern.data.ResultSummaryUtilities;
import edu.southwestern.evolution.EA;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.ScoreHistory;
import edu.southwestern.evolution.crossover.Crossover;
import edu.southwestern.evolution.genotypes.CombinedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNforDL4JGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.halloffame.HallOfFame;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.evolution.metaheuristics.AntiMaxModuleUsageFitness;
import edu.southwestern.evolution.metaheuristics.FavorXModulesFitness;
import edu.southwestern.evolution.metaheuristics.LinkPenalty;
import edu.southwestern.evolution.metaheuristics.MaxModulesFitness;
import edu.southwestern.evolution.metaheuristics.Metaheuristic;
import edu.southwestern.evolution.metaheuristics.SubstrateLinkPenalty;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.log.EvalLog;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.log.PerformanceLog;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.Bottom1DSubstrateMapping;
import edu.southwestern.networks.hyperneat.HyperNEATDummyTask;
import edu.southwestern.networks.hyperneat.HyperNEATSpeedTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.SubstrateArchitectureDefinition;
import edu.southwestern.networks.hyperneat.SubstrateCoordinateMapping;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.MultiplePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.tasks.boardGame.MultiPopulationCompetativeCoevolutionBoardGameTask;
import edu.southwestern.tasks.boardGame.SinglePopulationCompetativeCoevolutionBoardGameTask;
import edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask;
import edu.southwestern.tasks.breve2D.Breve2DTask;
import edu.southwestern.tasks.breve2D.NNBreve2DMonster;
import edu.southwestern.tasks.gridTorus.GroupTorusPredPreyTask;
import edu.southwestern.tasks.gridTorus.NNTorusPredPreyController;
import edu.southwestern.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask;
import edu.southwestern.tasks.gridTorus.TorusPredPreyTask;
import edu.southwestern.tasks.gridTorus.competitive.CompetitiveHomogeneousPredatorsVsPreyTask;
import edu.southwestern.tasks.gridTorus.cooperative.CooperativePredatorsVsStaticPreyTask;
import edu.southwestern.tasks.gridTorus.cooperativeAndCompetitive.CompetitiveAndCooperativePredatorsVsPreyTask;
import edu.southwestern.tasks.gvgai.GVGAISinglePlayerTask;
import edu.southwestern.tasks.innovationengines.PictureInnovationTask;
import edu.southwestern.tasks.innovationengines.ShapeInnovationTask;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.MarioTask;
import edu.southwestern.tasks.microrts.MicroRTSTask;
import edu.southwestern.tasks.microrts.SinglePopulationCompetativeCoevolutionMicroRTSTask;
import edu.southwestern.tasks.motests.FunctionOptimization;
import edu.southwestern.tasks.motests.testfunctions.FunctionOptimizationSet;
import edu.southwestern.tasks.mspacman.CooperativeCheckEachMultitaskSelectorMsPacManTask;
import edu.southwestern.tasks.mspacman.CooperativeGhostMonitorNetworksMsPacManTask;
import edu.southwestern.tasks.mspacman.CooperativeMsPacManTask;
import edu.southwestern.tasks.mspacman.CooperativeNonHierarchicalMultiNetMsPacManTask;
import edu.southwestern.tasks.mspacman.CooperativeSubtaskCombinerMsPacManTask;
import edu.southwestern.tasks.mspacman.CooperativeSubtaskSelectorMsPacManTask;
import edu.southwestern.tasks.mspacman.MsPacManTask;
import edu.southwestern.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.southwestern.tasks.mspacman.facades.ExecutorFacade;
import edu.southwestern.tasks.mspacman.init.MsPacManInitialization;
import edu.southwestern.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.MultipleInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.tasks.mspacman.sensors.ghosts.GhostControllerInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.ghosts.mediators.GhostsCheckEachDirectionMediator;
import edu.southwestern.tasks.pinball.PinballTask;
import edu.southwestern.tasks.rlglue.RLGlueEnvironment;
import edu.southwestern.tasks.rlglue.RLGlueTask;
import edu.southwestern.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.southwestern.tasks.rlglue.init.RLGlueInitialization;
import edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask;
import edu.southwestern.tasks.testmatch.MatchDataTask;
import edu.southwestern.tasks.ut2004.UT2004Task;
import edu.southwestern.tasks.vizdoom.VizDoomTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.random.RandomGenerator;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.Statistic;
import oldpacman.Executor;
import wox.serial.Easy;

/**
 * Modular Multiobjective Neuro-Evolution of Augmenting Topologies.
 * 
 * Main class that launches experiments.
 * Running "mvn -U install" will create a SNAPSHOT jar recognized by all
 * batch files.
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
	public static EA ea;
	@SuppressWarnings("rawtypes") // could hold any type, depending on command line
	public static Genotype genotype;
	@SuppressWarnings("rawtypes") // could hold any type, depending on command line
	public static ArrayList<Genotype> genotypeExamples;
	@SuppressWarnings("rawtypes") // can crossover any type, depending on command line
	public static Crossover crossoverOperator;
	public static FunctionOptimizationSet fos;
	public static RLGlueEnvironment rlGlueEnvironment;
	@SuppressWarnings("rawtypes") // depends on genotypes
	public static ArrayList<Metaheuristic> metaheuristics;
	public static ArrayList<ArrayList<String>> fitnessFunctions;
	public static ArrayList<Statistic> aggregationOverrides;
	public static TaskSpec tso;
	public static FeatureExtractor rlGlueExtractor;
	public static boolean blueprints = false;
	@SuppressWarnings("rawtypes") // applies to any population type
	public static PerformanceLog performanceLog;
	public static MsPacManControllerInputOutputMediator pacmanInputOutputMediator;
	public static GhostControllerInputOutputMediator ghostsInputOutputMediator;
	public static MsPacManControllerInputOutputMediator[] coevolutionMediators = null;
	public static MsPacManEnsembleArbitrator ensembleArbitrator = null;
	private static ArrayList<Integer> actualFitnessFunctions;
	public static MsPacManModeSelector pacmanMultitaskScheme = null;
	public static VariableDirectionBlock directionalSafetyFunction;
	public static TWEANNGenotype sharedMultitaskNetwork = null;
	public static TWEANNGenotype sharedPreferenceNetwork = null;
	public static EvalLog evalReport = null;
	public static RandomGenerator weightPerturber = null;
	public static MMNEATLog ghostLocationsOnPowerPillEaten = null;
	public static boolean browseLineage = false;
	public static SubstrateCoordinateMapping substrateMapping = null;
	@SuppressWarnings("rawtypes")
	public static HallOfFame hallOfFame;
	@SuppressWarnings("rawtypes")
	public static BoardGame boardGame;
	@SuppressWarnings("rawtypes")
	public static TwoDimensionalBoardGameViewer boardGameViewer;
	public static SubstrateArchitectureDefinition substrateArchitectureDefinition;
	
	public static MMNEAT mmneat;

	@SuppressWarnings("rawtypes")
	public static ArrayList<String> fitnessPlusMetaheuristics(int pop) {
		@SuppressWarnings("unchecked")
		ArrayList<String> result = (ArrayList<String>) fitnessFunctions.get(pop).clone();
		if(pop == 0) {
			ArrayList<String> meta = new ArrayList<String>();
			for (Metaheuristic m : metaheuristics) {
				meta.add(m.getClass().getSimpleName());
			}
			result.addAll(actualFitnessFunctions.get(pop), meta);
		}
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

	@SuppressWarnings("rawtypes") // type of genotypes being crossed could be anything
	private static void setupCrossover() throws NoSuchMethodException {
		// Crossover operator
		if (Parameters.parameters.booleanParameter("mating")) {
			crossoverOperator = (Crossover) ClassCreation.createObject("crossover");
		}
	}

	private static void setupFunctionOptimization() throws NoSuchMethodException {
		// Function minimization benchmarks, if they are used
		fos = (FunctionOptimizationSet) ClassCreation.createObject("fos");
		if (Parameters.parameters.booleanParameter("lengthDependentMutationRate") && fos != null) {
			Parameters.parameters.setDouble("realMutateRate", 1.0 / fos.getLowerBounds().length);
		}
	}

	@SuppressWarnings("rawtypes") // Metaheuristic can be applied to any type of population
	private static void setupMetaHeuristics() {
		// Metaheuristics are objectives that are not associated with the
		// domain/task
		System.out.println("Use meta-heuristics?");
		metaheuristics = new ArrayList<Metaheuristic>();
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
		if (Parameters.parameters.booleanParameter("penalizeSubstrateLinks")) {
			System.out.println("Penalize Substrate Links");
			metaheuristics.add(new SubstrateLinkPenalty());
		}
	}

	private static void setupTWEANNGenotypeDataTracking(boolean coevolution) {
		if (genotype instanceof TWEANNGenotype || 
			genotype instanceof CombinedGenotype || // Assume first member of pair is TWEANNGenotype
			genotype instanceof HyperNEATCPPNforDL4JGenotype) { // Contains CPPN that is TWEANNGenotype
			if (Parameters.parameters.booleanParameter("io")
					&& Parameters.parameters.booleanParameter("logTWEANNData")) {
				System.out.println("Init TWEANN Log");
				EvolutionaryHistory.initTWEANNLog();
			}
			if (!coevolution) {
				EvolutionaryHistory.initArchetype(0);
			}

			@SuppressWarnings("rawtypes")
			long biggestInnovation = genotype instanceof CombinedGenotype ? 
					((TWEANNGenotype) ((CombinedGenotype) genotype).t1).biggestInnovation() :
						(genotype instanceof HyperNEATCPPNforDL4JGenotype ?
						((HyperNEATCPPNforDL4JGenotype) genotype).getCPPN().biggestInnovation()	:
					((TWEANNGenotype) genotype).biggestInnovation());
			if (biggestInnovation > EvolutionaryHistory.largestUnusedInnovationNumber) {
				EvolutionaryHistory.setInnovation(biggestInnovation + 1);
			}
		}
	}

	public static void prepareCoevolutionArchetypes() {
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

	/**
	 * Currently, this check only applies to Ms Pac-Man tasks, but could
	 * be used for other coevolution experiments in the future.
	 * @return Whether task involves agents cooperating with subnetworks
	 */
	public static boolean taskHasSubnetworks() {
		return CooperativeSubtaskSelectorMsPacManTask.class.equals(Parameters.parameters.classParameter("task"))
				|| CooperativeSubtaskCombinerMsPacManTask.class.equals(Parameters.parameters.classParameter("task"));
	}

	/**
	 * Constructor takes the command line parameters
	 * to initialize the systems parameter values.
	 * @param args directly from command line
	 */
	public MMNEAT(String[] args) {
		Parameters.initializeParameterCollections(args);
	}

	/**
	 * Constructor that takes a parameter file
	 * string to initialize the systems
	 * parameter values.
	 * @param parameterFile
	 */
	public MMNEAT(String parameterFile) {
		Parameters.initializeParameterCollections(parameterFile);
	}

	public static void registerFitnessFunction(String name) {
		registerFitnessFunction(name, 0);
	}

	/**
	 * For plotting purposes. Let simulation know that a given fitness function
	 * will be tracked.
	 * @param name Name of fitness function in plot files
	 * @param pop population index (for coevolution)
	 */
	public static void registerFitnessFunction(String name, int pop) {
		registerFitnessFunction(name, null, true, pop);
	}

	public static void registerFitnessFunction(String name, boolean affectsSelection) {
		registerFitnessFunction(name, affectsSelection, 0);
	}


	/**
	 * Like above, but indicating that the "fitness" function does not affect 
	 * selection means that it is simply an other score that is being tracked
	 * in the logs.
	 * @param name Name of score
	 * @param affectsSelection Whether or not score is actually used for selection
	 * @param pop population index (for coevolution)
	 */
	public static void registerFitnessFunction(String name, boolean affectsSelection, int pop) {
		registerFitnessFunction(name, null, affectsSelection, pop);
	}

	public static void registerFitnessFunction(String name, Statistic override, boolean affectsSelection) {
		registerFitnessFunction(name, override, affectsSelection, 0);
	}

	/**
	 * As above, but it is now possible to indicate how the score is statistically
	 * summarized when noisy evaluations occur. The default is to average scores
	 * across evaluations, but if an overriding statistic is used, then this will
	 * also be mentioned in the log.
	 * @param name Name of score column
	 * @param override Statistic applied across evaluations (null is default/average)
	 * @param affectsSelection whether it affects selection
	 * @param pop population index (for coevolution)
	 */
	public static void registerFitnessFunction(String name, Statistic override, boolean affectsSelection, int pop) {
		if(actualFitnessFunctions == null){
			actualFitnessFunctions = new ArrayList<Integer>();
		}
		while(actualFitnessFunctions.size() <= pop){
			actualFitnessFunctions.add(0);
		}
		if (affectsSelection) {
			int num = actualFitnessFunctions.get(pop) + 1;
			actualFitnessFunctions.set(pop, num);
		}
		// For coevolution.
		// Create enough objective arrays to accomadate each population
		while(fitnessFunctions.size() <= pop) {
			fitnessFunctions.add(new ArrayList<String>());
		}
		fitnessFunctions.get(pop).add(name);
		aggregationOverrides.add(override);
	}

	/**
	 * Load important classes from class parameters.
	 * Other important experiment setup also occurs.
	 * Perhaps the most important classes that always
	 * need to be loaded at the task, the experiment, 
	 * and the ea. These get stored in public static 
	 * variables of this class so they are easily accessible
	 * from all parts of the code.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void loadClasses() {
		try {
			ActivationFunctions.resetFunctionSet();
			setupSaveDirectory();

			fitnessFunctions = new ArrayList<ArrayList<String>>();
			fitnessFunctions.add(new ArrayList<String>());
			aggregationOverrides = new ArrayList<Statistic>();

			boolean loadFrom = !Parameters.parameters.stringParameter("loadFrom").equals("");
			System.out.println("Init Genotype Ids");
			EvolutionaryHistory.initGenotypeIds();
			weightPerturber = (RandomGenerator) ClassCreation.createObject("weightPerturber");

			setupCrossover();
			RLGlueInitialization.setupRLGlue();
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
			
			if(Parameters.parameters.classParameter("boardGame") != null){
				boardGame = (BoardGame) ClassCreation.createObject("boardGame");
				if(boardGame instanceof TwoDimensionalBoardGame){
					if(CommonConstants.watch){
						boardGameViewer = new TwoDimensionalBoardGameViewer((TwoDimensionalBoardGame) boardGame);
					}else{
						boardGameViewer = null;
					}
				}else{
					boardGameViewer = null;
				}
			}
			
			task = (Task) ClassCreation.createObject("task");
			System.out.println("Load task: " + task);
			boolean multiPopulationCoevolution = false;
			// For all types of Ms Pac-Man tasks
			if (Parameters.parameters.booleanParameter("scalePillsByGen")
					&& Parameters.parameters.stringParameter("lastSavedDirectory").equals("")
					&& Parameters.parameters.integerParameter("lastSavedGeneration") == 0) {
				System.out.println("Set pre-eaten pills high, since we are scaling pills with generation");
				Parameters.parameters.setDouble("preEatenPillPercentage", 0.999);
			}
			
			HyperNEATTask HNTSeedTask = (HyperNEATTask) ClassCreation.createObject("hyperNEATSeedTask");
			if(CommonConstants.hyperNEAT || HNTSeedTask != null) {
				if(Parameters.parameters.booleanParameter("useHyperNEATCustomArchitecture")) {
					substrateArchitectureDefinition = (SubstrateArchitectureDefinition) ClassCreation.createObject("hyperNEATCustomArchitecture");
				}
				// For each substrate layer pairing, there can be multiple output neurons in the CPPN
				HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair = CommonConstants.leo ? 2 : 1;
				// Number of output neurons needed to designate bias values across all substrates
				HyperNEATCPPNGenotype.numBiasOutputs = CommonConstants.evolveHyperNEATBias ? 
						(HNTSeedTask == null ? 
							HyperNEATUtil.numBiasOutputsNeeded() :
							HyperNEATUtil.numBiasOutputsNeeded(HNTSeedTask)) : 
						0;				
			}
			if(Parameters.parameters.booleanParameter("hallOfFame")){
				hallOfFame = new HallOfFame();
			}
			if(task instanceof FunctionOptimization) {
				System.out.println("Setup Function Optimization");
				// Already setup in setupFunctionOptimization();
			} else if (task instanceof MsPacManTask) {
				MsPacManInitialization.setupGenotypePoolsForMsPacman();
				System.out.println("Setup Ms. Pac-Man Task");
				pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
				if (MMNEAT.pacmanInputOutputMediator instanceof VariableDirectionBlockLoadedInputOutputMediator) {
					directionalSafetyFunction = (VariableDirectionBlock) ClassCreation.createObject("directionalSafetyFunction");
					ensembleArbitrator = (MsPacManEnsembleArbitrator) ClassCreation.createObject("ensembleArbitrator");
				}
				String preferenceNet = Parameters.parameters.stringParameter("fixedPreferenceNetwork");
				String multitaskNet = Parameters.parameters.stringParameter("fixedMultitaskPolicy");
				if (multitaskNet != null && !multitaskNet.isEmpty()) {
					// Preference networks are being evolved to pick outputs of
					// fixed multitask network
					MMNEAT.sharedMultitaskNetwork = (TWEANNGenotype) Easy.load(multitaskNet);
					if (CommonConstants.showNetworks) {
						DrawingPanel panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Fixed Multitask Network");
						MMNEAT.sharedMultitaskNetwork.getPhenotype().draw(panel);
					}
					// One preference neuron per multitask mode
					setNNInputParameters(pacmanInputOutputMediator.numIn(), MMNEAT.sharedMultitaskNetwork.numModules);
				} else if (preferenceNet != null && !preferenceNet.isEmpty()) {
					MMNEAT.sharedPreferenceNetwork = (TWEANNGenotype) Easy.load(preferenceNet);
					if (CommonConstants.showNetworks) {
						DrawingPanel panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Fixed Preference Network");
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
				MsPacManInitialization.setupMsPacmanParameters();
				if (CommonConstants.multitaskModules > 1) {
					pacmanMultitaskScheme = (MsPacManModeSelector) ClassCreation.createObject("pacmanMultitaskScheme");
				}
			} else if (task instanceof CooperativeMsPacManTask) {
				System.out.println("Setup Coevolution Ms. Pac-Man Task");
				multiPopulationCoevolution = true;
				// Is this next line redundant
				EvolutionaryHistory.initInnovationHistory();
				MsPacManInitialization.setupMsPacmanParameters();
				if (task instanceof CooperativeGhostMonitorNetworksMsPacManTask) {
					MsPacManInitialization.setupCooperativeCoevolutionGhostMonitorsForMsPacman();
				} else if (task instanceof CooperativeSubtaskSelectorMsPacManTask) {
					MsPacManInitialization.setupCooperativeCoevolutionSelectorForMsPacman();
				} else if (task instanceof CooperativeSubtaskCombinerMsPacManTask) {
					MsPacManInitialization.setupCooperativeCoevolutionCombinerForMsPacman();
				} else if (task instanceof CooperativeNonHierarchicalMultiNetMsPacManTask) {
					MsPacManInitialization.setupCooperativeCoevolutionNonHierarchicalForMsPacman();
				} else if (task instanceof CooperativeCheckEachMultitaskSelectorMsPacManTask) {
					MsPacManInitialization.setupCooperativeCoevolutionCheckEachMultitaskPreferenceNetForMsPacman();
				}
			} else if (task instanceof MicroRTSTask) {
				MicroRTSTask temp = (MicroRTSTask) task;
				setNNInputParameters(temp.sensorLabels().length, 1); //only one output because it is utility value for state being evaluated
			} else if (task instanceof SinglePopulationCompetativeCoevolutionMicroRTSTask){
				SinglePopulationCompetativeCoevolutionMicroRTSTask temp = (SinglePopulationCompetativeCoevolutionMicroRTSTask) task;
				setNNInputParameters(temp.sensorLabels().length, 1); //only one output because it is utility value for state being evaluated
				
			} else if (task instanceof RLGlueTask) {
				setNNInputParameters(rlGlueExtractor.numFeatures(), RLGlueTask.agent.getNumberOutputs());
			} else if (task instanceof PinballTask) {
				PinballTask temp = (PinballTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);
			} else if (task instanceof StaticOpponentBoardGameTask) {
				StaticOpponentBoardGameTask temp = (StaticOpponentBoardGameTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);
			} else if (task instanceof SinglePopulationCompetativeCoevolutionBoardGameTask) {
				SinglePopulationCompetativeCoevolutionBoardGameTask temp = (SinglePopulationCompetativeCoevolutionBoardGameTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);
			} else if (task instanceof MultiPopulationCompetativeCoevolutionBoardGameTask) {
				System.out.println("Setup Multi-Population Board Game Coevolution Task");
				multiPopulationCoevolution = true;
				
				MultiPopulationCompetativeCoevolutionBoardGameTask temp = (MultiPopulationCompetativeCoevolutionBoardGameTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);	

				genotype = (Genotype) ClassCreation.createObject("genotype");
				genotypeExamples = new ArrayList<Genotype>(boardGame.getNumPlayers());
				for(int i = 0; i < boardGame.getNumPlayers(); i++){
					Genotype gene = genotype.newInstance();
					if(genotype instanceof TWEANNGenotype) {
						((TWEANNGenotype) gene).archetypeIndex = i;
					}
					
					genotypeExamples.add(gene);
				}
				prepareCoevolutionArchetypes();
				
			} else if (task instanceof GVGAISinglePlayerTask) {
				GVGAISinglePlayerTask temp = (GVGAISinglePlayerTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);
			} else if (task instanceof Breve2DTask) {
				System.out.println("Setup Breve 2D Task");
				Breve2DDynamics dynamics = (Breve2DDynamics) ClassCreation.createObject("breveDynamics");
				setNNInputParameters(dynamics.numInputSensors(), NNBreve2DMonster.NUM_OUTPUTS);
			} else if (task instanceof TorusPredPreyTask) {
				System.out.println("Setup Torus Predator/Prey Task");
				int numInputs = determineNumPredPreyInputs();
				NetworkTask t = (NetworkTask) task;
				setNNInputParameters(numInputs, t.outputLabels().length);
			} else if (task instanceof CompetitiveHomogeneousPredatorsVsPreyTask || task instanceof CompetitiveAndCooperativePredatorsVsPreyTask) { // must appear before GroupTorusPredPreyTask
				System.out.println("Setup Competitive Torus Predator/Prey Task");
				multiPopulationCoevolution = true;
				int numPredInputs = determineNumPredPreyInputs(true);
				int numPreyInputs = determineNumPredPreyInputs(false);

				int numPredOutputs = TorusPredPreyTask.outputLabels(true).length;
				int numPreyOutputs = TorusPredPreyTask.outputLabels(false).length;

				// Setup genotype early
				if(task instanceof CompetitiveHomogeneousPredatorsVsPreyTask){
					genotypeExamples = new ArrayList<Genotype>(2); // one pred pop, one prey pop
				} else if(task instanceof CompetitiveAndCooperativePredatorsVsPreyTask){
					genotypeExamples = new ArrayList<Genotype>(Parameters.parameters.integerParameter("torusPredators") + 
							Parameters.parameters.integerParameter("torusPreys"));
				}

				// Is this valid for multiple populations?

				// Setup pred population
				setNNInputParameters(numPredInputs, numPredOutputs);
				genotype = (Genotype) ClassCreation.createObject("genotype");
				//add one for each pred if cooperative and competitive coevolution
				if(task instanceof CompetitiveAndCooperativePredatorsVsPreyTask){ 
					for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
						Genotype temp = genotype.newInstance();
						if(genotype instanceof TWEANNGenotype) {
							((TWEANNGenotype) temp).archetypeIndex = i;
						}
						genotypeExamples.add(temp);
					}
				} else{ //just one pred pop
					if(genotype instanceof TWEANNGenotype) {
						((TWEANNGenotype) genotype).archetypeIndex = 0;
					}
					genotypeExamples.add(genotype.newInstance());
				}

				// Setup prey population
				setNNInputParameters(numPreyInputs, numPreyOutputs);
				genotype = (Genotype) ClassCreation.createObject("genotype");
				if(genotype instanceof TWEANNGenotype) {
					((TWEANNGenotype) genotype).archetypeIndex = 1;
				}
				//add one for each prey if cooperative and competitive coevolution
				if(task instanceof CompetitiveAndCooperativePredatorsVsPreyTask){ 
					for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
						Genotype temp = genotype.newInstance();
						if(genotype instanceof TWEANNGenotype) {
							((TWEANNGenotype) temp).archetypeIndex = i + Parameters.parameters.integerParameter("torusPredators");
						}
						genotypeExamples.add(temp);
					}
				} else{ //just one prey pop
					if(genotype instanceof TWEANNGenotype) {
						((TWEANNGenotype) genotype).archetypeIndex = 1;
					}
					genotypeExamples.add(genotype.newInstance());
				}

				prepareCoevolutionArchetypes();
			} else if (task instanceof GroupTorusPredPreyTask) { // Technically, the competitive task also overrides this
				System.out.println("Setup Cooperative Torus Predator/Prey Task");
				multiPopulationCoevolution = true;
				int numInputs = determineNumPredPreyInputs();
				NetworkTask t = (NetworkTask) task;
				setNNInputParameters(numInputs, t.outputLabels().length);
				// Setup genotype early
				genotype = (Genotype) ClassCreation.createObject("genotype");
				int numAgents = (task instanceof CooperativePredatorsVsStaticPreyTask) ? Parameters.parameters.integerParameter("torusPredators") : Parameters.parameters.integerParameter("torusPreys");
				System.out.println("There will be " + numAgents + " evolved agents");
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
			} else if(task instanceof InteractiveEvolutionTask) {
				System.out.println("set up Interactive Evolution Task");
				InteractiveEvolutionTask temp = (InteractiveEvolutionTask) task;
				setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if(task instanceof PictureInnovationTask) {
				System.out.println("set up Innovation Engine Task");
				PictureInnovationTask temp = (PictureInnovationTask) task;
				setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if(task instanceof ShapeInnovationTask) {
				System.out.println("set up Innovation Engine Task");
				ShapeInnovationTask temp = (ShapeInnovationTask) task;
				setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if (task instanceof MarioTask) {
				setNNInputParameters(((Parameters.parameters.integerParameter("marioInputWidth") * Parameters.parameters.integerParameter("marioInputHeight")) * 2) + 1, MarioTask.MARIO_OUTPUTS); //hard coded for now, 5 button outputs
				System.out.println("Set up Mario Task");
			} else if(task instanceof HyperNEATDummyTask) {
				System.out.println("set up dummy hyperNEAT task. Used for testing purposes only");
			} else if(task instanceof HyperNEATSpeedTask) {
				System.out.println("set up dummy hyperNEAT task. Used for testing purposes only");
			} else if (task == null) {
				// this else statement should only happen for JUnit testing cases.
				// Some default network setup is needed.
				System.out.println("No task defined! It is assumed that this is part of a JUnit test.");
				setNNInputParameters(5, 3);
			} else {
				System.out.println("A valid task must be specified!");
				System.out.println(task);
				System.exit(1);
			}
			
			// Only loads if settings indicate that this should be used
			ScoreHistory.load();

			// Changes network input setting to HyperNEAT settings
			if (CommonConstants.hyperNEAT) {
				System.out.println("Using HyperNEAT");
				hyperNEATOverrides();
			}

			setupMetaHeuristics();
			// An EA is always needed. Currently only GenerationalEA classes are supported
			if (!loadFrom) {
				System.out.println("Create EA");
				ea = (EA) ClassCreation.createObject("ea");
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
						MsPacManInitialization.setupMultitaskSeedPopulationForMsPacman(ghostDir, pillDir);
					} else {
						MsPacManInitialization.setupSingleMultitaskSeedForMsPacman();
					}
				}

				// Revise settings to accommodate multitask seed
				System.out.println("Revising network info based on multitask seed");
				MsPacManControllerInputOutputMediator ghostMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass1");
				MsPacManControllerInputOutputMediator pillMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass2");
				pacmanInputOutputMediator = new MultipleInputOutputMediator(new MsPacManControllerInputOutputMediator[] { ghostMediator, pillMediator });
				setNNInputParameters(pacmanInputOutputMediator.numIn(), pacmanInputOutputMediator.numOut());

			} else if(HNTSeedTask != null && Parameters.parameters.integerParameter("lastSavedGeneration") == 0) { // hyperNEATseed is not null
				Parameters.parameters.setBoolean("randomizeSeedWeights", true); // Makes sure PopulationUtil randomized all weights

				// Since this approach required many large TWEANNs to be saved in memory, alternative gene representations are used with optional fields removed
				TWEANNGenotype.smallerGenotypes = true;            
				substrateMapping = (SubstrateCoordinateMapping) ClassCreation.createObject("substrateMapping");
				int numSubstratePairings = HNTSeedTask.getSubstrateConnectivity().size();
				System.out.println("Number of substrate pairs being connected: "+ numSubstratePairings);
				assert HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair > 0 : "HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair must be positive";
				HyperNEATCPPNGenotype hntGeno = new HyperNEATCPPNGenotype(HyperNEATUtil.numCPPNInputs(HNTSeedTask),  numSubstratePairings * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair + HyperNEATCPPNGenotype.numBiasOutputs, 0);
				TWEANNGenotype seedGeno = hntGeno.getSubstrateGenotypeForEvolution(HNTSeedTask);
				genotype = seedGeno;
				System.out.println("Genotype seeded from HyperNEAT task substrate specification");
				seedExample = true;
				// Cleanup data we don't need any more
				HNTSeedTask = null;
			} else if (seedGenotype.isEmpty()) {
				genotype = (Genotype) ClassCreation.createObject("genotype");
			} else {
				// Copy assures a fresh genotype id
				System.out.println("Loading seed genotype: " + seedGenotype);
				genotype = ((Genotype) Easy.load(seedGenotype)).copy();
				// System.out.println(genotype);
				seedExample = true;
			}
			setupTWEANNGenotypeDataTracking(multiPopulationCoevolution);
			// An Experiment is always needed
			System.out.println("Create Experiment");
			experiment = (Experiment) ClassCreation.createObject("experiment");
			experiment.init();
			if (!loadFrom && Parameters.parameters.booleanParameter("io")) {
				if (Parameters.parameters.booleanParameter("logPerformance") && !multiPopulationCoevolution) {
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
	public static void hyperNEATOverrides() throws NoSuchMethodException {
		// Already set to 1 as default value
		//HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair = 1;

		// Cannot monitor inputs with HyperNEAT because the NetworkTask
		// interface no longer applies
		CommonConstants.monitorInputs = false;
		// Setting the common constant should be sufficient, but keeping the parameter means
		// that hybrID can turn it back on if it needs to
		//Parameters.parameters.setBoolean("monitorInputs", false);

		substrateMapping = (SubstrateCoordinateMapping) ClassCreation.createObject("substrateMapping");

		// This substrate mapping does not require all CPPN inputs
		if(substrateMapping instanceof Bottom1DSubstrateMapping) {
			// Other tasks may also use this mapping in the future.
			HyperNEATTetrisTask.reduce2DTo1D = true;
		}		
	}

	/**
	 * Finds the number of inputs for the predPrey task, which is based on the
	 * type of agent that is being evolved's sensor inputs defined in its
	 * controller This has to be done to prevent a null pointer exception when
	 * first getting the sensor labels/number of sensors
	 * 
	 * @return numInputs
	 */
	private static int determineNumPredPreyInputs() {
		//this is probably covering all the cases, but this must cover all cases for all types
		//of predators tasks. 
		boolean isPredator = task instanceof TorusEvolvedPredatorsVsStaticPreyTask || 
				task instanceof CooperativePredatorsVsStaticPreyTask;
		return determineNumPredPreyInputs(isPredator);
	}

	private static int determineNumPredPreyInputs(boolean isPredator) {
		NNTorusPredPreyController temp = new NNTorusPredPreyController(null, isPredator);
		return temp.getNumInputs();
	}

	/**
	 * Resets the classes used in MMNEAT and and sets them to null.
	 */
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

	/**
	 * Initializes and runs the experiment given the loaded classes.
	 */
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

	/**
	 * Processes the task experiment for a given number of runs.
	 * @param runs
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void process(int runs) throws FileNotFoundException, NoSuchMethodException {
		try {
			task = (Task) ClassCreation.createObject("task");
		} catch (NoSuchMethodException ex) {
			System.out.println("Failed to instantiate task " + Parameters.parameters.classParameter("task"));
			System.exit(1);
		}
		String base = Parameters.parameters.stringParameter("base");
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		int run = Parameters.parameters.integerParameter("runNumber");
		if(task instanceof MultiplePopulationTask) {
			String runDir = base + "/" + saveTo + run + "/";
			int i = 0;
			// Note: Only works for populations of evolved TWEANNs,
			// because an archetype is required. There will be one
			// archetype file for each population, so checking for the
			// existence of the files verifies the number of populations.
			while(new File(runDir + "archetype"+i+".xml").exists()) {
				ResultSummaryUtilities.processExperiment(
						base + "/" + saveTo,
						Parameters.parameters.stringParameter("log"), runs, Parameters.parameters.integerParameter("maxGens"),
						"_" + ("pop" + i) + "parents_log.txt",
						"_" + ("pop" + i) + "parents_gen",
						base, i);
				i++;
			}
		} else { //for lonerTask sending in default of population 0
			ResultSummaryUtilities.processExperiment(
					base + "/" + saveTo,
					Parameters.parameters.stringParameter("log"), runs, Parameters.parameters.integerParameter("maxGens"),
					"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_log.txt",
					"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_gen",
					base, 0);
		}
	}

	/**
	 * Processes the hypervolume(HV) for a given number of runs.
	 * @param runs
	 * @throws FileNotFoundException
	 */
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
		// Simple way of debugging using the profiler
		//args = new String[]{"runNumber:1", "randomSeed:0", "base:tetris", "logPerformance:false", "logTWEANNData:false", "trials:1", "maxGens:300", "mu:50", "io:true", "netio:true", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.TetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "log:Tetris-moRawHNSeedFixedSplitInputs", "saveTo:moRawHNSeedFixedSplitInputs", "hyperNEATSeedTask:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "HNTTetrisProcessDepth:1", "netLinkRate:0.0", "netSpliceRate:0.0", "linkExpressionThreshold:-1"};

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
			Parameters.initializeParameterCollections(args); // file should exist
			loadClasses();
			calculateHVs(runs);
		} else if (args[0].startsWith("lineage:")) {
			System.out.println("Lineage browser");
			browseLineage = true;
			st.nextToken(); // "lineage"
			String value = st.nextToken();

			int run = Integer.parseInt(value);
			args[0] = "runNumber:" + run;
			Parameters.initializeParameterCollections(args); // file should exist			
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
			Parameters.initializeParameterCollections(args); // file should exist
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

	/**
	 * Runs a single evolution experiment with a given run number.
	 * @param args Command line parameters
	 */
	private static void evolutionaryRun(String[] args) {
		// Commandline
		mmneat = new MMNEAT(args);
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

		mmneat.run();

		closeLogs();
	}

	/**
	 * Checks for logs that aren't null, closes them and sets them to null.
	 */
	public static void closeLogs() {
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
	public static void setNNInputParameters(int numIn, int numOut) {
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
	 * @param <T> phenotype
	 * @param combined
	 *            Combined population of scores/genotypes
	 * @param generation
	 *            Current generation information is being logged for
	 */
	@SuppressWarnings("unchecked")
	public static <T> void logPerformanceInformation(ArrayList<Score<T>> combined, int generation) {
		if (performanceLog != null)
			performanceLog.log(combined, generation);
	}
}
