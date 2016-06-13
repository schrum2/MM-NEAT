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
import edu.utexas.cs.nn.log.MMNEATLog;
import edu.utexas.cs.nn.log.PerformanceLog;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.SubstrateCoordinateMapping;
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
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativeTorusPredPreyTask;
import edu.utexas.cs.nn.tasks.motests.FunctionOptimization;
import edu.utexas.cs.nn.tasks.motests.testfunctions.FunctionOptimizationSet;
import edu.utexas.cs.nn.tasks.mspacman.*;
import edu.utexas.cs.nn.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.utexas.cs.nn.tasks.mspacman.facades.ExecutorFacade;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.init.MsPacManInitialization;
import edu.utexas.cs.nn.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.GhostMonitorNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.SubNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts.GhostControllerInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts.mediators.GhostsCheckEachDirectionMediator;
import edu.utexas.cs.nn.tasks.picbreeder.PicbreederTask;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueEnvironment;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;
import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.utexas.cs.nn.tasks.testmatch.MatchDataTask;
import edu.utexas.cs.nn.tasks.ut2004.UT2004Task;
import edu.utexas.cs.nn.tasks.vizdoom.VizDoomTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.random.RandomGenerator;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.stats.Statistic;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
	public static MMNEATLog ghostLocationsOnPowerPillEaten = null;
	public static boolean browseLineage = false;
	public static SubstrateCoordinateMapping substrateMapping = null;
	
	public static MMNEAT mmneat;
	
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

	public static boolean taskHasSubnetworks() {
		return CooperativeSubtaskSelectorMsPacManTask.class.equals(Parameters.parameters.classParameter("task"))
				|| CooperativeSubtaskCombinerMsPacManTask.class.equals(Parameters.parameters.classParameter("task"));
	}

        /**
         * Constructor takes the command like parameters
         * to initialize the systems parameter values.
         * @param args directly from command line
         */
	public MMNEAT(String[] args) {
		Parameters.initializeParameterCollections(args);
	}

	public MMNEAT(String parameterFile) {
		Parameters.initializeParameterCollections(parameterFile);
	}

        /**
         * For plotting purposes. Let simulation know that a given fitness function
         * will be tracked.
         * @param name Name of fitness function in plot files
         */
	public static void registerFitnessFunction(String name) {
		registerFitnessFunction(name, null, true);
	}

        /**
         * Like above, but indicating that the "fitness" function does not affect 
         * selection means that it is simply an other score that is being tracked
         * in the logs.
         * @param name Name of score
         * @param affectsSelection Whether or not score is actually used for selection
         */
	public static void registerFitnessFunction(String name, boolean affectsSelection) {
		registerFitnessFunction(name, null, affectsSelection);
	}

        /**
         * As above, but it is now possible to indicate how the score is statistically
         * summarized when noisy evaluations occur. The default is to average scores
         * across evaluations, but if an overriding statistic is used, then this will
         * also be mentioned in the log.
         * @param name Name of score column
         * @param override Statistic applied across evaluations (null is default/average)
         * @param affectsSelection whether it affects selection
         */
	public static void registerFitnessFunction(String name, Statistic override, boolean affectsSelection) {
		if (affectsSelection) {
			actualFitnessFunctions++;
		}
		fitnessFunctions.add(name);
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
	public static void loadClasses() {
		try {
			ActivationFunctions.resetFunctionSet();
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
				MsPacManInitialization.setupGenotypePoolsForMsPacman();
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
				MsPacManInitialization.setupMsPacmanParameters();
				if (CommonConstants.multitaskModules > 1) {
					pacmanMultitaskScheme = (MsPacManModeSelector) ClassCreation.createObject("pacmanMultitaskScheme");
				}
			} else if (task instanceof CooperativeMsPacManTask) {
				System.out.println("Setup Coevolution Ms. Pac-Man Task");
				coevolution = true;
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
			} else if(task instanceof PicbreederTask) {
				System.out.println("set up Picbreeder Task");
				setNNInputParameters(PicbreederTask.CPPN_NUM_INPUTS, PicbreederTask.CPPN_NUM_OUTPUTS);
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
			// An EA is always needed. Currently only GenerationalEA classes are supported
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
						MsPacManInitialization.setupMultitaskSeedPopulationForMsPacman(ghostDir, pillDir);
					} else {
						MsPacManInitialization.setupSingleMultitaskSeedForMsPacman();
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
		
		substrateMapping = (SubstrateCoordinateMapping) ClassCreation.createObject("substrateMapping");
		
		
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
	public static void setNNInputParameters(int numIn, int numOut) throws NoSuchMethodException {
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
