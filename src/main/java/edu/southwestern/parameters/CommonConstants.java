package edu.southwestern.parameters;

/**
 * Contains Parameter values loaded via Parameters that never change once they
 * are loaded, and are repeatedly used through the simulation. It is more
 * efficient to save the variable values here than to use the HashMap of each
 * ParameterCollection over and over.
 *
 * @author Jacob Schrum
 */
public class CommonConstants {


	// boolean
	public static boolean cascadeExpansion;
	public static boolean inputsUseID;
	public static boolean substrateBiasLocationInputs;
	public static boolean substrateLocationInputs;
	public static boolean convolution;
	public static boolean averageScoreHistory;
	public static boolean monitorSubstrates;
	public static boolean inheritFitness;
	public static boolean splitRawTetrisInputs;
	public static boolean allowMultipleFunctions;
	public static boolean allowRandomGhostReversals;
	public static boolean checkEachAbsoluteDistanceGhostSort;
	public static boolean checkEachFlushWalls;
	public static boolean connectToInputs;
	public static boolean constantTUGGoalIncrements;
	public static boolean cullCrossovers;
	public static boolean cullModeMutations;
	public static boolean deleteLeastUsed;
	public static boolean eliminateImpossibleDirections;
	public static boolean ensembleModeMutation;
	public static boolean evalReport;
	public static boolean exploreWeightsOfNewStructure;
	public static boolean freezeBeforeModeMutation;
	public static boolean fs;
	public static boolean hierarchicalMultitask;
	public static boolean hyperNEAT;
	public static boolean hybrID;
	public static boolean imprisonedWhileEdible;
	public static boolean infiniteEdibleTime;
	public static boolean leo;
	public static boolean evolveHyperNEATBias;
	public static boolean logChildScores;
	public static boolean meltAfterCrossover;
	public static boolean minimalSubnetExecution;
	public static boolean minimizeSpliceImpact;
	public static boolean mmpActivationId;
	public static boolean monitorInputs;
	public static boolean mutationChancePerMode;
	public static boolean netio;
	public static boolean nicheRestrictionOnModeMutation;
	public static boolean onlyModeMutationWhenModesSame;
	public static boolean pacmanFatalTimeLimit;
	public static boolean pacManSensorCaching;
	public static boolean polynomialWeightMutation;
	public static boolean probabilisticSelection;
	public static boolean randomArgMaxTieBreak;
	public static boolean reachabilityReportsBuffers;
	public static boolean recordPacman;
	public static boolean recurrency;
	public static boolean relativePacmanDirections;
	public static boolean replayPacman;
	public static boolean requireFitnessDifferenceForChange;
	public static boolean showNetworks;
	public static boolean showSubnetAnalysis;
	public static boolean softmaxModeSelection;
	public static boolean softmaxSelection;
	public static boolean timedPacman;
	public static boolean trackCombiningCrossover;
	public static boolean trialsMatchGenerations;
	public static boolean tugKeepsParetoFront;
	public static boolean tugObjectiveModeLinkage;
	public static boolean tugObjectiveUsageLinkage;
	public static boolean tugResetsToPreviousGoals;
	public static boolean ucb1Evaluation;
	public static boolean viewModePreference;
	public static boolean watch;
	public static boolean weakenBeforeModeMutation;
	public static boolean weightedAverageModeAggregation;
	// integer
	public static int disabledMode;
	public static int fsLinksPerOut;
	public static int ftype;
	public static int ghostsForBonus;
	public static int initialMaze;
	public static int justMaze;
	public static int litterSize;
	public static int maxCampTrials;
	public static int maxModes;
	public static int maxTrials;
	public static int multitaskModules;
	public static int numActiveGhosts;
	public static int pacManLevelTimeLimit;
	public static int pacmanStartingPowerPillIndex = -1; // Not a commandline param yet
	public static int stopMode;
	public static int syllabusSize;
	public static int trialIncreaseFrequency;
	public static int trials;
	// double

	public static double campPercentOfTrials;
	public static double distanceForNewMode;
	public static double inheritProportion;
	public static double linkExpressionThreshold;
	public static double netChangeActivationRate;
	public static double preferenceNeuronDecay;
	public static double preferenceNeuronFatigueUnit;
	public static double percentDeathCampsToSave;
	public static double percentPowerPillCampsToSave;
	public static double perLinkMutateRate;
	public static double remainingTUGGoalRatio;
	public static double softmaxTemperature;
	public static double usageForNewMode;

	/**
	 * To be called once and only once after Parameters are initialized. None of
	 * the variables here should ever be changed.
	 *
	 * TODO: In Parameters, way to specify if parameter is read-only
	 */
	public static void load() {
		// boolean
		cascadeExpansion = Parameters.parameters.booleanParameter("cascadeExpansion");
		inputsUseID = Parameters.parameters.booleanParameter("inputsUseID");
		averageScoreHistory = Parameters.parameters.booleanParameter("averageScoreHistory");
		convolution = Parameters.parameters.booleanParameter("convolution");
		substrateBiasLocationInputs = Parameters.parameters.booleanParameter("substrateBiasLocationInputs");
		substrateLocationInputs = Parameters.parameters.booleanParameter("substrateLocationInputs");
		monitorSubstrates = Parameters.parameters.booleanParameter("monitorSubstrates");
		inheritFitness = Parameters.parameters.booleanParameter("inheritFitness");
		splitRawTetrisInputs = Parameters.parameters.booleanParameter("splitRawTetrisInputs");
		allowMultipleFunctions = Parameters.parameters.booleanParameter("allowMultipleFunctions");
		allowRandomGhostReversals = Parameters.parameters.booleanParameter("allowRandomGhostReversals");
		checkEachAbsoluteDistanceGhostSort = Parameters.parameters.booleanParameter("checkEachAbsoluteDistanceGhostSort");
		checkEachFlushWalls = Parameters.parameters.booleanParameter("checkEachFlushWalls");
		connectToInputs = Parameters.parameters.booleanParameter("connectToInputs");
		constantTUGGoalIncrements = Parameters.parameters.booleanParameter("constantTUGGoalIncrements");
		cullCrossovers = Parameters.parameters.booleanParameter("cullCrossovers");
		cullModeMutations = Parameters.parameters.booleanParameter("cullModeMutations");
		deleteLeastUsed = Parameters.parameters.booleanParameter("deleteLeastUsed");
		eliminateImpossibleDirections = Parameters.parameters.booleanParameter("eliminateImpossibleDirections");
		ensembleModeMutation = Parameters.parameters.booleanParameter("ensembleModeMutation");
		evalReport = Parameters.parameters.booleanParameter("evalReport");
		exploreWeightsOfNewStructure = Parameters.parameters.booleanParameter("exploreWeightsOfNewStructure");
		freezeBeforeModeMutation = Parameters.parameters.booleanParameter("freezeBeforeModeMutation");
		fs = Parameters.parameters.booleanParameter("fs");
		hierarchicalMultitask = Parameters.parameters.booleanParameter("hierarchicalMultitask");
		hyperNEAT = Parameters.parameters.booleanParameter("hyperNEAT");
		hybrID = Parameters.parameters.booleanParameter("hybrID");
		imprisonedWhileEdible = Parameters.parameters.booleanParameter("imprisonedWhileEdible");
		infiniteEdibleTime = Parameters.parameters.booleanParameter("infiniteEdibleTime");
		leo = Parameters.parameters.booleanParameter("leo");
		evolveHyperNEATBias = Parameters.parameters.booleanParameter("evolveHyperNEATBias");
		logChildScores = Parameters.parameters.booleanParameter("logChildScores");
		meltAfterCrossover = Parameters.parameters.booleanParameter("meltAfterCrossover");
		minimalSubnetExecution = Parameters.parameters.booleanParameter("minimalSubnetExecution");
		minimizeSpliceImpact = Parameters.parameters.booleanParameter("minimizeSpliceImpact");
		mmpActivationId = Parameters.parameters.booleanParameter("mmpActivationId");
		monitorInputs = Parameters.parameters.booleanParameter("monitorInputs");
		mutationChancePerMode = Parameters.parameters.booleanParameter("mutationChancePerMode");
		netio = Parameters.parameters.booleanParameter("netio");
		nicheRestrictionOnModeMutation = Parameters.parameters.booleanParameter("nicheRestrictionOnModeMutation");
		onlyModeMutationWhenModesSame = Parameters.parameters.booleanParameter("onlyModeMutationWhenModesSame");
		pacmanFatalTimeLimit = Parameters.parameters.booleanParameter("pacmanFatalTimeLimit");
		pacManSensorCaching = Parameters.parameters.booleanParameter("pacManSensorCaching");
		polynomialWeightMutation = Parameters.parameters.booleanParameter("polynomialWeightMutation");
		probabilisticSelection = Parameters.parameters.booleanParameter("probabilisticSelection");
		randomArgMaxTieBreak = Parameters.parameters.booleanParameter("randomArgMaxTieBreak");
		reachabilityReportsBuffers = Parameters.parameters.booleanParameter("reachabilityReportsBuffers");
		recordPacman = Parameters.parameters.booleanParameter("recordPacman");
		recurrency = Parameters.parameters.booleanParameter("recurrency");
		relativePacmanDirections = Parameters.parameters.booleanParameter("relativePacmanDirections");
		replayPacman = Parameters.parameters.booleanParameter("replayPacman");
		requireFitnessDifferenceForChange = Parameters.parameters.booleanParameter("requireFitnessDifferenceForChange");
		showNetworks = Parameters.parameters.booleanParameter("showNetworks");
		showSubnetAnalysis = Parameters.parameters.booleanParameter("showSubnetAnalysis");
		softmaxModeSelection = Parameters.parameters.booleanParameter("softmaxModeSelection");
		softmaxSelection = Parameters.parameters.booleanParameter("softmaxSelection");
		timedPacman = Parameters.parameters.booleanParameter("timedPacman");
		trackCombiningCrossover = Parameters.parameters.booleanParameter("trackCombiningCrossover");
		trialsMatchGenerations = Parameters.parameters.booleanParameter("trialsMatchGenerations");
		tugKeepsParetoFront = Parameters.parameters.booleanParameter("tugKeepsParetoFront");
		tugObjectiveModeLinkage = Parameters.parameters.booleanParameter("tugObjectiveModeLinkage");
		tugObjectiveUsageLinkage = Parameters.parameters.booleanParameter("tugObjectiveUsageLinkage");
		tugResetsToPreviousGoals = Parameters.parameters.booleanParameter("tugResetsToPreviousGoals");
		ucb1Evaluation = Parameters.parameters.booleanParameter("ucb1Evaluation");
		viewModePreference = Parameters.parameters.booleanParameter("viewModePreference");
		watch = Parameters.parameters.booleanParameter("watch");
		weakenBeforeModeMutation = Parameters.parameters.booleanParameter("weakenBeforeModeMutation");
		weightedAverageModeAggregation = Parameters.parameters.booleanParameter("weightedAverageModeAggregation");

		// integer
		disabledMode = Parameters.parameters.integerParameter("disabledMode");
		fsLinksPerOut = Parameters.parameters.integerParameter("fsLinksPerOut");
		ftype = Parameters.parameters.integerParameter("ftype");
		ghostsForBonus = Parameters.parameters.integerParameter("ghostsForBonus");
		initialMaze = Parameters.parameters.integerParameter("initialMaze");
		justMaze = Parameters.parameters.integerParameter("justMaze");
		litterSize = Parameters.parameters.integerParameter("litterSize");
		maxCampTrials = Parameters.parameters.integerParameter("maxCampTrials");
		maxModes = Parameters.parameters.integerParameter("maxModes");
		maxTrials = Parameters.parameters.integerParameter("maxTrials");
		multitaskModules = Parameters.parameters.integerParameter("multitaskModes");
		numActiveGhosts = Parameters.parameters.integerParameter("numActiveGhosts");
		pacManLevelTimeLimit = Parameters.parameters.integerParameter("pacManLevelTimeLimit");
		stopMode = Parameters.parameters.integerParameter("stopMode");
		syllabusSize = Parameters.parameters.integerParameter("syllabusSize");
		trialIncreaseFrequency = Parameters.parameters.integerParameter("trialIncreaseFrequency");
		trials = Parameters.parameters.integerParameter("trials");

		// double
		campPercentOfTrials = Parameters.parameters.doubleParameter("campPercentOfTrials");
		distanceForNewMode = Parameters.parameters.doubleParameter("distanceForNewMode");
		inheritProportion = Parameters.parameters.doubleParameter("inheritProportion");
		linkExpressionThreshold = Parameters.parameters.doubleParameter("linkExpressionThreshold");
		netChangeActivationRate = Parameters.parameters.doubleParameter("netChangeActivationRate");
		preferenceNeuronDecay = Parameters.parameters.doubleParameter("preferenceNeuronDecay");
		preferenceNeuronFatigueUnit = Parameters.parameters.doubleParameter("preferenceNeuronFatigueUnit");
		percentDeathCampsToSave = Parameters.parameters.doubleParameter("percentDeathCampsToSave");
		percentPowerPillCampsToSave = Parameters.parameters.doubleParameter("percentPowerPillCampsToSave");
		perLinkMutateRate = Parameters.parameters.doubleParameter("perLinkMutateRate");
		remainingTUGGoalRatio = Parameters.parameters.doubleParameter("remainingTUGGoalRatio");
		softmaxTemperature = Parameters.parameters.doubleParameter("softmaxTemperature");
		usageForNewMode = Parameters.parameters.doubleParameter("usageForNewMode");
	}

	public static void trialsByGenerationUpdate(int generation) {
		if (trialsMatchGenerations && generation % trialIncreaseFrequency == 0) {
			trials++;
			trials = Math.min(trials, maxTrials);
			Parameters.parameters.setInteger("trials", trials);
			if (CommonConstants.ucb1Evaluation) {
				Parameters.parameters.setInteger("evaluationBudget",
						Parameters.parameters.integerParameter("evaluationBudget") + 1);
			}
			Parameters.parameters.saveParameters();
		}
	}
}
