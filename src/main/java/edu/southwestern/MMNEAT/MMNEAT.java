package edu.southwestern.MMNEAT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;

import edu.southwestern.data.ResultSummaryUtilities;
import edu.southwestern.evolution.EA;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.ScoreHistory;
import edu.southwestern.evolution.crossover.Crossover;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.CombinedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNforDL4JGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNPlusParametersGenotype;
import edu.southwestern.evolution.halloffame.HallOfFame;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.evolution.metaheuristics.AntiMaxModuleUsageFitness;
import edu.southwestern.evolution.metaheuristics.FavorXModulesFitness;
import edu.southwestern.evolution.metaheuristics.LinkPenalty;
import edu.southwestern.evolution.metaheuristics.MaxModulesFitness;
import edu.southwestern.evolution.metaheuristics.Metaheuristic;
import edu.southwestern.evolution.metaheuristics.SubstrateLinkPenalty;
import edu.southwestern.evolution.mulambda.MuLambda;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.log.EvalLog;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.log.PerformanceLog;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.hyperneat.Bottom1DSubstrateMapping;
import edu.southwestern.networks.hyperneat.HyperNEATDummyTask;
import edu.southwestern.networks.hyperneat.HyperNEATSpeedTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.SubstrateCoordinateMapping;
import edu.southwestern.networks.hyperneat.architecture.SubstrateArchitectureDefinition;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.MultiplePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask;
import edu.southwestern.tasks.gridTorus.GroupTorusPredPreyTask;
import edu.southwestern.tasks.gridTorus.NNTorusPredPreyController;
import edu.southwestern.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask;
import edu.southwestern.tasks.gridTorus.TorusPredPreyTask;
import edu.southwestern.tasks.gridTorus.competitive.CompetitiveHomogeneousPredatorsVsPreyTask;
import edu.southwestern.tasks.gridTorus.cooperative.CooperativePredatorsVsStaticPreyTask;
import edu.southwestern.tasks.gridTorus.cooperativeAndCompetitive.CompetitiveAndCooperativePredatorsVsPreyTask;
import edu.southwestern.tasks.gvgai.GVGAISinglePlayerTask;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANLevelTask;
import edu.southwestern.tasks.gvgai.zelda.ZeldaLevelTask;
import edu.southwestern.tasks.gvgai.zelda.study.HumanSubjectStudy2019Zelda;
import edu.southwestern.tasks.innovationengines.PictureInnovationTask;
import edu.southwestern.tasks.innovationengines.PictureTargetTask;
import edu.southwestern.tasks.innovationengines.ShapeInnovationTask;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import edu.southwestern.tasks.interactive.loderunner.LodeRunnerGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioLevelBreederTask;
import edu.southwestern.tasks.interactive.megaman.MegaManCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.megaman.MegaManGANLevelBreederTask;
import edu.southwestern.tasks.loderunner.LodeRunnerGANLevelSequenceTask;
import edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask;
import edu.southwestern.tasks.loderunner.LodeRunnerLevelTask;
import edu.southwestern.tasks.mario.MarioCPPNOrDirectToGANLevelTask;
import edu.southwestern.tasks.mario.MarioCPPNtoGANLevelTask;
import edu.southwestern.tasks.mario.MarioGANLevelTask;
import edu.southwestern.tasks.mario.MarioLevelTask;
import edu.southwestern.tasks.mario.MarioTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.MegaManCPPNtoGANLevelTask;
import edu.southwestern.tasks.megaman.MegaManGANLevelTask;
import edu.southwestern.tasks.megaman.MegaManLevelTask;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.tasks.motests.MultipleFunctionOptimization;
import edu.southwestern.tasks.motests.testfunctions.FunctionOptimizationSet;
import edu.southwestern.tasks.mspacman.MsPacManTask;
import edu.southwestern.tasks.mspacman.facades.ExecutorFacade;
import edu.southwestern.tasks.mspacman.init.MsPacManInitialization;
import edu.southwestern.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
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
import edu.southwestern.tasks.ut2004.UT2004Util;
import edu.southwestern.tasks.ut2004.testing.HumanSubjectStudy2018TeammateServer;
import edu.southwestern.tasks.zelda.ZeldaCPPNOrDirectToGANDungeonTask;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANDungeonTask;
import edu.southwestern.tasks.zelda.ZeldaDungeonTask;
import edu.southwestern.tasks.zelda.ZeldaGANDungeonTask;
import edu.southwestern.tasks.zentangle.ZentangleTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
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
	@SuppressWarnings("rawtypes") // applies to any population type
	public static PerformanceLog performanceLog;
	public static MsPacManControllerInputOutputMediator pacmanInputOutputMediator;
	public static GhostControllerInputOutputMediator ghostsInputOutputMediator;
	private static ArrayList<Integer> actualFitnessFunctions;
	public static MsPacManModeSelector pacmanMultitaskScheme = null;
	public static VariableDirectionBlock directionalSafetyFunction;
	public static EvalLog evalReport = null;
	public static RandomGenerator weightPerturber = null;
	public static MMNEATLog ghostLocationsOnPowerPillEaten = null;
	public static boolean browseLineage = false;
	public static SubstrateCoordinateMapping substrateMapping = null;
	@SuppressWarnings("rawtypes")
	public static HallOfFame hallOfFame;
	public static SubstrateArchitectureDefinition substrateArchitectureDefinition;
	@SuppressWarnings("rawtypes")
	public static Archive pseudoArchive;
	public static boolean usingDiversityBinningScheme = false;
	
	public static MMNEAT mmneat;

	@SuppressWarnings("rawtypes")
	public static BinLabels getArchiveBinLabelsClass() {
		if (pseudoArchive != null) {
			return pseudoArchive.getBinLabelsClass();
		} else if (ea instanceof MAPElites) {
			return ((MAPElites) ea).getBinLabelsClass();
		}
		throw new IllegalStateException("Attempted to get archive without using MAP Elites or a psuedo-archive");
	}
	
	@SuppressWarnings("rawtypes")
	public static Archive getArchive() {
		if (pseudoArchive != null) {
			return pseudoArchive;
		} else if (ea instanceof MAPElites) {
			return ((MAPElites) ea).getArchive();
		}
		throw new IllegalStateException("Attempted to get archive without using MAP Elites or a psuedo-archive");
		
	}
	
	
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
				genotype instanceof TWEANNPlusParametersGenotype ||
				genotype instanceof CombinedGenotype || // Assume first member of pair is TWEANNGenotype
				genotype instanceof CPPNOrDirectToGANGenotype || // Assume first form is TWEANNGenotype
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
			long biggestInnovation = genotype instanceof TWEANNPlusParametersGenotype ?
					((TWEANNPlusParametersGenotype) genotype).getTWEANNGenotype().biggestInnovation() :
						genotype instanceof CPPNOrDirectToGANGenotype ?
								((TWEANNGenotype) ((CPPNOrDirectToGANGenotype) genotype).getCurrentGenotype()).biggestInnovation():
									(genotype instanceof CombinedGenotype ? 
											((TWEANNGenotype) ((CombinedGenotype) genotype).t1).biggestInnovation() :
												(genotype instanceof HyperNEATCPPNforDL4JGenotype ?
														((HyperNEATCPPNforDL4JGenotype) genotype).getCPPN().biggestInnovation()	:
															((TWEANNGenotype) genotype).biggestInnovation()));
					
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
			int multitaskModes = CommonConstants.multitaskModules;
			if (multitaskModes > 1) {
				modesToTrack = multitaskModes;
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
				//				HyperNEATCPPNGenotype.numBiasOutputs = CommonConstants.evolveHyperNEATBias ? 
				//						(HNTSeedTask == null ? 
				//							HyperNEATUtil.numBiasOutputsNeeded() :
				//							HyperNEATUtil.numBiasOutputsNeeded(HNTSeedTask)) : 
				//						0;				
			}
			if(Parameters.parameters.booleanParameter("hallOfFame")){
				hallOfFame = new HallOfFame();
			}
			if(task instanceof MultipleFunctionOptimization) {
				System.out.println("Setup Multiple Function Optimization");
				// Already setup in setupFunctionOptimization();
			} else if(task instanceof FunctionOptimizationTask) {
					System.out.println("Setup Function Optimization");
					// Anything to do?
			} else if (task instanceof MsPacManTask) {
				//TODO: Allow for evolution of ghost teams
				if(Parameters.parameters.booleanParameter("evolveGhosts")){
					System.out.println("we are evolving a ghost!");
					ghostsInputOutputMediator = new GhostsCheckEachDirectionMediator();
					setNNInputParameters(ghostsInputOutputMediator.numIn(), ghostsInputOutputMediator.numOut());
				} else {
					System.out.println("Setup Ms. Pac-Man Task");
					pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
					if (MMNEAT.pacmanInputOutputMediator instanceof VariableDirectionBlockLoadedInputOutputMediator) {
						directionalSafetyFunction = (VariableDirectionBlock) ClassCreation.createObject("directionalSafetyFunction");
					}
					// Regular Check-Each-Direction networks
					setNNInputParameters(pacmanInputOutputMediator.numIn(), pacmanInputOutputMediator.numOut());
					MsPacManInitialization.setupMsPacmanParameters();
					if (CommonConstants.multitaskModules > 1) {
						pacmanMultitaskScheme = (MsPacManModeSelector) ClassCreation.createObject("pacmanMultitaskScheme");
					}
				}
			} else if (task instanceof RLGlueTask) {
				setNNInputParameters(rlGlueExtractor.numFeatures(), RLGlueTask.agent.getNumberOutputs());
			} else if (task instanceof PinballTask) {
				PinballTask temp = (PinballTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);
			} else if (task instanceof GVGAISinglePlayerTask) {
				GVGAISinglePlayerTask temp = (GVGAISinglePlayerTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);
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
				if(Parameters.parameters.booleanParameter("overwriteGameBots")) {
					if(Parameters.parameters.booleanParameter("botprizeMod")) {
						UT2004Util.copyBotPrizeVersionOfGameBots();
					} else {
						UT2004Util.copyDefaultVersionOfGameBots();
					}
				}
				System.out.println("Setup UT2004 Task");
				UT2004Task utTask = (UT2004Task) task;
				setNNInputParameters(utTask.sensorModel.numberOfSensors(), utTask.outputModel.numberOfOutputs());
			} else if (task instanceof MatchDataTask) {
				System.out.println("Setup Match Data Task");
				MatchDataTask t = (MatchDataTask) task;
				setNNInputParameters(t.numInputs(), t.numOutputs());
			} else if(task instanceof InteractiveEvolutionTask) {
				System.out.println("set up Interactive Evolution Task");
				InteractiveEvolutionTask temp = (InteractiveEvolutionTask) task;
				// Since these tasks use real-vector genotypes, to not set the NN params
				//if(!(temp instanceof MarioGANLevelBreederTask) && !(temp instanceof ZeldaGANLevelBreederTask)) setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
				if(!(temp instanceof InteractiveGANLevelEvolutionTask)) setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if (task instanceof ZentangleTask) {
				System.out.println("Setup Zentangle Task");
				ZentangleTask t = (ZentangleTask) task;
				setNNInputParameters(t.numInputs(), t.numOutputs());
			} else if(task instanceof PictureInnovationTask) {
				System.out.println("set up Innovation Engine Task");
				PictureInnovationTask temp = (PictureInnovationTask) task;
				setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if(task instanceof ShapeInnovationTask) {
				System.out.println("set up Innovation Engine Task");
				ShapeInnovationTask temp = (ShapeInnovationTask) task;
				setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if(task instanceof PictureTargetTask) {
				System.out.println("set up Picture Target Task");
				PictureTargetTask temp = (PictureTargetTask) task;
				setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if (task instanceof MarioTask) {
				setNNInputParameters(((Parameters.parameters.integerParameter("marioInputWidth") * Parameters.parameters.integerParameter("marioInputHeight")) * 2) + 1, MarioTask.MARIO_OUTPUTS); //hard coded for now, 5 button outputs
				System.out.println("Set up Mario Task");
			} else if (task instanceof MarioLevelTask) {
				GANProcess.type = GANProcess.GAN_TYPE.MARIO;
				if(task instanceof MarioCPPNtoGANLevelTask|| task instanceof MarioCPPNOrDirectToGANLevelTask) {
					// Evolving CPPNs that create latent vectors that are sent to a GAN
					setNNInputParameters(MarioCPPNtoGANLevelBreederTask.UPDATED_INPUTS.length, GANProcess.latentVectorLength());
				} else {
					// This line only matters for the CPPN version of the task, but doesn't hurt the GAN version, which does not evolve networks
					setNNInputParameters(MarioLevelBreederTask.INPUTS.length, MarioLevelBreederTask.OUTPUTS.length);
				}
				System.out.println("Set up Mario Level Task");
			} else if (task instanceof ZeldaDungeonTask) { // Full dungeons using the Rogue-like engine
				GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
				if(task instanceof ZeldaCPPNtoGANDungeonTask || task instanceof ZeldaCPPNOrDirectToGANDungeonTask) {
					// Evolving CPPNs that create latent vectors that are sent to a GAN
					setNNInputParameters(ZeldaCPPNtoGANLevelBreederTask.SENSOR_LABELS.length, GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables());
				}
			} else if (task instanceof ZeldaLevelTask){ // For evolving Zelda levels in GVG-AI ... not well developed
				GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
				System.out.println("Set up Zelda Level Task");
			} else if (task instanceof MegaManLevelTask){
				GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN;
				// Ok to set the CPPN input parameters even if they are not used
				setNNInputParameters(MegaManCPPNtoGANLevelBreederTask.SENSOR_LABELS.length, MegaManCPPNtoGANLevelBreederTask.staticNumCPPNOutputs());
				System.out.println("Set up Mega Man Task");
			} else if(task instanceof LodeRunnerLevelTask) {
				GANProcess.type = GANProcess.GAN_TYPE.LODE_RUNNER;
				System.out.println("Set up Lode Runner Task");
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
			if(HNTSeedTask != null && Parameters.parameters.integerParameter("lastSavedGeneration") == 0) { // hyperNEATseed is not null
				Parameters.parameters.setBoolean("randomizeSeedWeights", true); // Makes sure PopulationUtil randomized all weights

				// Since this approach required many large TWEANNs to be saved in memory, alternative gene representations are used with optional fields removed
				TWEANNGenotype.smallerGenotypes = true;            
				substrateMapping = (SubstrateCoordinateMapping) ClassCreation.createObject("substrateMapping");
				int numSubstratePairings = HNTSeedTask.getSubstrateConnectivity().size();
				System.out.println("Number of substrate pairs being connected: "+ numSubstratePairings);
				assert HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair > 0 : "HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair must be positive";
				HyperNEATCPPNGenotype hntGeno = new HyperNEATCPPNGenotype(HyperNEATUtil.numCPPNInputs(HNTSeedTask),  numSubstratePairings * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair + HyperNEATUtil.numBiasOutputsNeeded(HNTSeedTask), 0);
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
			
			if (Parameters.parameters.booleanParameter("trackPseudoArchive")) {
				usingDiversityBinningScheme = true;
				// Create a pseudo archive for use with objective evolution TODO
				pseudoArchive = new Archive<>(Parameters.parameters.booleanParameter("netio"), Parameters.parameters.stringParameter("archiveSubDirectoryName"));
				int startSize = Parameters.parameters.integerParameter("mu");
				ArrayList<Genotype> startingPopulation = PopulationUtil.initialPopulation(genotype.newInstance(),startSize);
				for (Genotype g : startingPopulation) {
					System.out.println("genotype: " + g);
					Score s = ((LonerTask) task).evaluate(g);
					System.out.println("score: " + s);
					pseudoArchive.add(s); // Fill the archive with random starting individuals
				}
				if (ea instanceof MuLambda)
					((MuLambda) ea).setUpPseudoArchive();
			}
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
		HyperNEATCPPNGenotype.normalizedNodeMemory = Parameters.parameters.booleanParameter("normalizedNodeMemory");
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
		GANProcess.terminateGANProcess();
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
		//args = "zeldaType:generated randomSeed:4 zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.GANLoader".split(" ");
		
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
		} else if (args[0].startsWith("utStudyTeammate:")) {
			// This launch code is associated with the 2018 Human Subject Study using
			// Unreal Tournament 2004. The purpose is to evaluate different types of
			// teammates in team deathmatch.
			
			Parameters.initializeParameterCollections(args);
			String teammateString = Parameters.parameters.stringParameter("utStudyTeammate");
			HumanSubjectStudy2018TeammateServer.BOT_TYPE type; 
			switch(teammateString) {
			case "none":
				type = HumanSubjectStudy2018TeammateServer.BOT_TYPE.None;
				break;
			case "jude":
				type = HumanSubjectStudy2018TeammateServer.BOT_TYPE.Jude;
				break;
			case "ethan":
				type = HumanSubjectStudy2018TeammateServer.BOT_TYPE.Ethan;
				break;
			case "native":
				type = HumanSubjectStudy2018TeammateServer.BOT_TYPE.Native;
				break;
			default:
				throw new IllegalArgumentException("utStudyTeammate parameter must be ethan, jude, or native");
			}
			try {
				HumanSubjectStudy2018TeammateServer.runTrial(type);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("\n\n\n");
				System.out.println("This trial terminated unexpectedly. Please inform the researcher immediately.");
				System.exit(1);
			}
		} else if(args[0].startsWith("zeldaType:")){
			
			Parameters.initializeParameterCollections(args);
			String type = Parameters.parameters.stringParameter("zeldaType");
			HumanSubjectStudy2019Zelda.Type t = null;
			switch(type) {
			case "original":
				t = HumanSubjectStudy2019Zelda.Type.ORIGINAL;
				break;
			case "generated":
				t = HumanSubjectStudy2019Zelda.Type.GENERATED_DUNGEON;
				break;
			case "tutorial":
				t = HumanSubjectStudy2019Zelda.Type.TUTORIAL;
				break;
			default:
				throw new IllegalArgumentException("zeldaType : " + type + " unrecognized. (original, generated, tutorial)");
			}
			HumanSubjectStudy2019Zelda.runTrial(t);
		} else {
			evolutionaryRun(args);
		}
		System.out.println("done: " + (((System.currentTimeMillis() - start) / 1000.0) / 60.0) + " minutes");
		if (!(task instanceof MultipleFunctionOptimization) && !(task instanceof FunctionOptimizationTask)) {
			System.exit(1);
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

	/**
	 * This method only applies to bounded real-valued genotypes.
	 * Bounded real-valued genotypes are currently only used in two types of domains.
	 * @return
	 */
	public static double[] getLowerBounds() {
		// Function Optimization Tasks use these genotypes and know their lower bounds
		if(fos != null) return fos.getLowerBounds();
		else if (task instanceof FunctionOptimizationTask) return ArrayUtil.doubleSpecified(Parameters.parameters.integerParameter("foVectorLength"), Parameters.parameters.doubleParameter("foLowerBounds")); 
		// For Mario GAN, the latent vector length determines the size, but the lower bounds are all zero
		else if(task instanceof MarioGANLevelTask || task instanceof MarioGANLevelBreederTask|| task instanceof MarioCPPNOrDirectToGANLevelTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("marioGANLevelChunks")); // all -1
		// Similar for ZeldaGAN
		else if(task instanceof ZeldaGANLevelBreederTask || task instanceof ZeldaGANLevelTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength()); // all -1
		else if(task instanceof ZeldaGANDungeonTask) return ArrayUtil.doubleNegativeOnes(ZeldaGANDungeonTask.genomeLength()); // all -1
		else if(task instanceof ZeldaCPPNOrDirectToGANDungeonTask) return ArrayUtil.doubleNegativeOnes(ZeldaGANDungeonTask.genomeLength()); // all -1
		else if(task instanceof LodeRunnerGANLevelBreederTask || task instanceof LodeRunnerGANLevelTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength());
		else if(task instanceof LodeRunnerGANLevelSequenceTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")); 
		else if(task instanceof MegaManGANLevelBreederTask || task instanceof MegaManGANLevelTask || task instanceof MegaManCPPNtoGANLevelBreederTask|| task instanceof MegaManCPPNtoGANLevelTask) return ArrayUtil.doubleNegativeOnes((Parameters.parameters.integerParameter("GANInputSize") + MegaManGANGenerator.numberOfAuxiliaryVariables()) * Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		else {
			throw new IllegalArgumentException("BoundedRealValuedGenotypes only supported for Function Optimization and Mario/Zelda/LodeRuner/MegaMan GAN");
		}
	}

	/**
	 * Similar to the lower bounds method above. Only used
	 * for two domains, currently.
	 * @return
	 */
	public static double[] getUpperBounds() {
		if(fos != null) return fos.getUpperBounds();
		else if (task instanceof FunctionOptimizationTask) return ArrayUtil.doubleSpecified(Parameters.parameters.integerParameter("foVectorLength"), Parameters.parameters.doubleParameter("foUpperBounds")); 
		else if(task instanceof MarioGANLevelTask || task instanceof MarioGANLevelBreederTask||task instanceof MarioCPPNOrDirectToGANLevelTask) return ArrayUtil.doubleOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("marioGANLevelChunks")); // all ones
		else if(task instanceof ZeldaGANLevelBreederTask || task instanceof ZeldaGANLevelTask) return ArrayUtil.doubleOnes(GANProcess.latentVectorLength()); // all ones
		else if(task instanceof ZeldaGANDungeonTask) return ArrayUtil.doubleOnes(ZeldaGANDungeonTask.genomeLength()); // all ones
		else if(task instanceof ZeldaCPPNOrDirectToGANDungeonTask) return ArrayUtil.doubleOnes(ZeldaGANDungeonTask.genomeLength()); // all ones
		else if(task instanceof LodeRunnerGANLevelBreederTask || task instanceof LodeRunnerGANLevelTask) return ArrayUtil.doubleOnes(GANProcess.latentVectorLength());
		else if(task instanceof LodeRunnerGANLevelSequenceTask) return ArrayUtil.doubleOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")); 
		else if(task instanceof MegaManGANLevelBreederTask || task instanceof MegaManGANLevelTask || task instanceof MegaManCPPNtoGANLevelBreederTask|| task instanceof MegaManCPPNtoGANLevelTask) return ArrayUtil.doubleOnes((Parameters.parameters.integerParameter("GANInputSize") + MegaManGANGenerator.numberOfAuxiliaryVariables()) * Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		else {
			throw new IllegalArgumentException("BoundedRealValuedGenotypes only supported for Function Optimization and Mario/Zelda/LodeRunner/MegaMan GAN");
		}
	}
}
