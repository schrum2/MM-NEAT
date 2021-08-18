package edu.southwestern.tasks.mspacman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.nsga2.tug.TUGTask;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.mspacman.agentcontroller.ghosts.SharedNNGhosts;
import edu.southwestern.tasks.mspacman.agentcontroller.pacman.NNMsPacMan;
import edu.southwestern.tasks.mspacman.data.TrainingCampManager;
import edu.southwestern.tasks.mspacman.facades.ExecutorFacade;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.facades.GhostControllerFacade;
import edu.southwestern.tasks.mspacman.facades.PacManControllerFacade;
import edu.southwestern.tasks.mspacman.init.MsPacManInitialization;
import edu.southwestern.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.southwestern.tasks.mspacman.objectives.AvoidDeadSpaceScore;
import edu.southwestern.tasks.mspacman.objectives.ClearTimeScore;
import edu.southwestern.tasks.mspacman.objectives.EatenGhostScore;
import edu.southwestern.tasks.mspacman.objectives.EdibleTimeParameter;
import edu.southwestern.tasks.mspacman.objectives.FastGhostEatingScore;
import edu.southwestern.tasks.mspacman.objectives.FastPillEatingScore;
import edu.southwestern.tasks.mspacman.objectives.GameScore;
import edu.southwestern.tasks.mspacman.objectives.GhostRegretScore;
import edu.southwestern.tasks.mspacman.objectives.GhostRewardScore;
import edu.southwestern.tasks.mspacman.objectives.GhostsPerPowerPillScore;
import edu.southwestern.tasks.mspacman.objectives.ImproperlyEatenPowerPillScore;
import edu.southwestern.tasks.mspacman.objectives.LairTimeParameter;
import edu.southwestern.tasks.mspacman.objectives.LevelGameScore;
import edu.southwestern.tasks.mspacman.objectives.LevelScore;
import edu.southwestern.tasks.mspacman.objectives.LuringScore;
import edu.southwestern.tasks.mspacman.objectives.MsPacManObjective;
import edu.southwestern.tasks.mspacman.objectives.PillScore;
import edu.southwestern.tasks.mspacman.objectives.PowerPillEatenWhenGhostFarScore;
import edu.southwestern.tasks.mspacman.objectives.ProperlyEatenPowerPillScore;
import edu.southwestern.tasks.mspacman.objectives.RandomScore;
import edu.southwestern.tasks.mspacman.objectives.RawTimeScore;
import edu.southwestern.tasks.mspacman.objectives.RemainingLivesScore;
import edu.southwestern.tasks.mspacman.objectives.SurvivalAndSpeedTimeScore;
import edu.southwestern.tasks.mspacman.objectives.TimeToEatAllGhostsScore;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.tasks.mspacman.sensors.ghosts.GhostControllerInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.ghosts.mediators.GhostsCheckEachDirectionMediator;
import edu.southwestern.tasks.popacman.controllers.OldToNewPacManIntermediaryController;
import edu.southwestern.tasks.popacman.ghosts.controllers.OldToNewGhostIntermediaryController;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.Average;
import edu.southwestern.util.stats.Max;
import edu.southwestern.util.stats.Mode;
import edu.southwestern.util.stats.Statistic;
import oldpacman.Executor;
import oldpacman.controllers.NewGhostController;
import oldpacman.controllers.NewPacManController;
import oldpacman.game.Constants;
import oldpacman.game.Game;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import popacman.CustomExecutor;


/**
 * Ms. Pac-Man vs. four ghosts across four mazes.
 * 
 * @author Jacob Schrum
 * @param <T> phenotype of evolved agent
 */
public class MsPacManTask<T extends Network> extends NoisyLonerTask<T>implements TUGTask, NetworkTask, HyperNEATTask {

	// Global variables that used to be in the MMNEAT class
	public static MsPacManControllerInputOutputMediator pacmanInputOutputMediator;
	public static GhostControllerInputOutputMediator ghostsInputOutputMediator;
	public static MsPacManModeSelector pacmanMultitaskScheme = null;
	public static VariableDirectionBlock directionalSafetyFunction;
	
	// Approximate size of Ms. Pac-Man screen in terms of nodes
	public static final int MS_PAC_MAN_NODE_WIDTH = 112;
	public static final int MS_PAC_MAN_NODE_HEIGHT = 120;
	// Number of nodes between pills (effective cell size)
	public static final int MS_PAC_MAN_NODE_DIM = 4;
	// Size of substrate in terms of cells
	public static final int MS_PAC_MAN_SUBSTRATE_WIDTH = MS_PAC_MAN_NODE_WIDTH/MS_PAC_MAN_NODE_DIM;
	public static final int MS_PAC_MAN_SUBSTRATE_HEIGHT = MS_PAC_MAN_NODE_HEIGHT/MS_PAC_MAN_NODE_DIM;
	public static final int MS_PAC_MAN_SUBSTRATE_SIZE = MS_PAC_MAN_SUBSTRATE_WIDTH * MS_PAC_MAN_SUBSTRATE_HEIGHT;
	// Substrate index that deals with power pills
	public static final int POWER_PILL_SUBSTRATE_INDEX = 1; 
	public List<Substrate> subs = null; // filled below
	public List<SubstrateConnectivity> connections = null; // filled below
	public HashMap<Integer, List<Substrate>> substratesForMaze = new HashMap<Integer, List<Substrate>>();
	public static String saveFilePrefix = "";
	//boolean variables
	protected boolean deterministic;
	protected boolean ignorePillScore;
	protected boolean noPills;
	protected boolean noPowerPills;
	protected boolean endAfterGhostEatingChances;
	protected boolean luringTask;
	protected boolean removePillsNearPowerPills;
	protected boolean exitLairEdible;
	protected boolean endOnlyOnTimeLimit;
	protected boolean randomLairExit;
	protected boolean lairExitDatabase;
	protected boolean simultaneousLairExit;
	protected boolean ghostsStartOutsideLair;
	protected boolean onlyOneLairExitAllowed;
	protected boolean evolveGhosts;
	//objectives and scores used for multitask
	protected ArrayList<MsPacManObjective<T>> objectives;
	protected ArrayList<MsPacManObjective<T>> otherScores;
	protected GhostControllerFacade ghosts;
	protected PacManControllerFacade mspacman;
	protected ExecutorFacade exec;
	protected GameFacade game;
	//indices used for calculation of fitness TODO
	private final int scoreIndexInOtherScores;
	private final int pillScoreIndexInOtherScores;
	private final int ghostRewardIndexInOtherScores;
	@SuppressWarnings("unused")
	private final int maxScoreIndexInOtherScores;
	@SuppressWarnings("unused")
	private final int maxPillScoreIndexInOtherScores;
	@SuppressWarnings("unused")
	private final int maxGhostRewardIndexInOtherScores;
	private final int properPowerPillIndexInOtherScores;
	private final int improperPowerPillIndexInOtherScores;
	private final int luringScoreIndexInOtherScores;
	private final int specificLevelScoreFirstIndexInOtherScores;
	private final int powerPillEatenWhenGhostFarIndexInOtherScores;
	private final int avgLevelIndexInOtherScores;
	@SuppressWarnings("unused")
	private final int maxLevelIndexInOtherScores;
	@SuppressWarnings("unused")
	private final int ghostRegretScoreInOtherScores;
	private final int ghostsEatenIndexInOtherScores;
	protected int rawTimeScoreIndex = -1;
	protected int usedGhostScoreIndex = -1;
	public int[] pillTimeFrameIndices;
	public int[] ghostTimeFrameIndices;
	private final boolean eachComponentTracksScoreToo;
	private final boolean plainGhostScore;
	private final TrainingCampManager tcManager;

	/**
	 * Default constructor
	 */
	public MsPacManTask() {
		this(Parameters.parameters.booleanParameter("deterministic"));
	}

	/**
	 * Constructor
	 * @param det boolean parameter "deterministic"
	 */
	public MsPacManTask(boolean det) {
		super();//constructor for noisy loner task
		exec = Parameters.parameters.booleanParameter("partiallyObservablePacman") ?
				new ExecutorFacade(new CustomExecutor.Builder().build()) :
				new ExecutorFacade(new Executor());
		this.deterministic = det;//if game is deterministic
		tcManager = new TrainingCampManager();

		//variables from command line parameters
		onlyOneLairExitAllowed = false;
		evolveGhosts = Parameters.parameters.booleanParameter("evolveGhosts");
		boolean rawScorePacMan = Parameters.parameters.booleanParameter("rawScorePacMan");
		boolean clearTimeScore = Parameters.parameters.booleanParameter("clearTimeScore");
		boolean rewardFasterGhostEating = Parameters.parameters.booleanParameter("rewardFasterGhostEating");
		boolean rewardFasterPillEating = Parameters.parameters.booleanParameter("rewardFasterPillEating");
		luringTask = Parameters.parameters.booleanParameter("luringTask");
		removePillsNearPowerPills = Parameters.parameters.booleanParameter("removePillsNearPowerPills");
		simultaneousLairExit = Parameters.parameters.booleanParameter("simultaneousLairExit");
		exitLairEdible = Parameters.parameters.booleanParameter("exitLairEdible");
		endOnlyOnTimeLimit = Parameters.parameters.booleanParameter("endOnlyOnTimeLimit");
		endAfterGhostEatingChances = Parameters.parameters.booleanParameter("endAfterGhostEatingChances");
		noPills = Parameters.parameters.booleanParameter("noPills");
		noPowerPills = Parameters.parameters.booleanParameter("noPowerPills");
		ignorePillScore = Parameters.parameters.booleanParameter("ignorePillScore");
		eachComponentTracksScoreToo = Parameters.parameters.booleanParameter("eachComponentTracksScoreToo");
		plainGhostScore = Parameters.parameters.booleanParameter("plainGhostScore");
		//used in constructor, not global
		boolean avgGhostsPerPowerPill = Parameters.parameters.booleanParameter("avgGhostsPerPowerPill");
		boolean punishDeadSpace = Parameters.parameters.booleanParameter("punishDeadSpace");
		boolean randomSelection = Parameters.parameters.booleanParameter("randomSelection");
		boolean partiallyObservablePacman = Parameters.parameters.booleanParameter("partiallyObservablePacman");

		objectives = new ArrayList<MsPacManObjective<T>>(17);//why 17? TODO
		otherScores = new ArrayList<MsPacManObjective<T>>(17);//why 17? TODO
		if (randomSelection) {//command line parameter, "Only objective is a random objective"
			addObjective(new RandomScore<T>(), objectives, true);//random score since only objective
		} else if (rawScorePacMan) {// "Pac-Man uses Game Score as only fitness"
			addObjective(new GameScore<T>(), objectives, true);
		} else if (luringTask) {//"Pac-Man rewarded for luring ghosts to power pills before eating pill"
			addObjective(new LuringScore<T>(), objectives, true);//
		} else if (Parameters.parameters.booleanParameter("individualLevelFitnesses")) {
			for (int i = 0; i < Constants.NUM_MAZES; i++) {//"One fitness function for each level"
				addObjective(new LevelGameScore<T>(i), objectives, new Average(), true);
			}
		} else {//more than one objective is a random objective
			if (!noPowerPills && CommonConstants.numActiveGhosts > 0) {//power pills still present and ghosts still active
				if (!Parameters.parameters.booleanParameter("ignoreGhostScores")) {//"No fitness from edible ghosts in Ms Pac-Man, even though there are present"
					usedGhostScoreIndex = objectives.size();//keeps track of ghost scores since not used by agent TODO
					if (avgGhostsPerPowerPill && !partiallyObservablePacman) {//command line parameter, "Ghost score used is the average eaten per power pill eaten"
						addObjective(new GhostsPerPowerPillScore<T>(true), objectives, true);
					} else if (rewardFasterGhostEating) {//command line parameter, "Ghost reward fitness gives higher fitness to eating ghosts quickly after power pills"
						addObjective(new FastGhostEatingScore<T>(), objectives, true);
					} else if (plainGhostScore) {//command line parameter, "For ghost fitness, just use eaten ghosts instead of ghost score"
						addObjective(new EatenGhostScore<T>(), objectives, true);
					} else {//otherwise, ghost fitness determined by proportional ms. pac man and ghost score 
						addObjective(new GhostRewardScore<T>(), objectives, true);
					}
					//command line parameter, "Include negative fitness for ghosts that pacman fails to eat"
					if (Parameters.parameters.booleanParameter("ghostRegretFitness") && !partiallyObservablePacman) {
						addObjective(new GhostRegretScore<T>(), objectives, true);
					}//command line parameter, "Fitness based on time to eat all ghosts after power pill"
					if (Parameters.parameters.booleanParameter("timeToEatAllFitness")) {
						addObjective(new TimeToEatAllGhostsScore<T>(), objectives, true);
					}
				}//command line parameter,  "Fitness for eating power pills when all ghosts are threats"
				if (Parameters.parameters.booleanParameter("awardProperPowerPillEating")) {
					addObjective(new ProperlyEatenPowerPillScore<T>(), objectives, true);
				}
				if (Parameters.parameters.booleanParameter("punishImproperPowerPillEating")) {
					addObjective(new ImproperlyEatenPowerPillScore<T>(), objectives, true);
				}
			}
			if (!ignorePillScore && !noPills) {
				if (rewardFasterPillEating) {
					addObjective(new FastPillEatingScore<T>(), objectives, true);
				} else {
					addObjective(new PillScore<T>(), objectives, true);
				}
			}
		}
		if (Parameters.parameters.booleanParameter("rawTimeScore")) {
			rawTimeScoreIndex = objectives.size();
			addObjective(new RawTimeScore<T>(), objectives, true);
		}
		if (punishDeadSpace) {
			addObjective(new AvoidDeadSpaceScore<T>(), objectives, true);
		}
		if (clearTimeScore && CommonConstants.justMaze != -1) {
			addObjective(new ClearTimeScore<T>(), objectives, true);
		}
		if (Parameters.parameters.booleanParameter("livesObjective")) {
			addObjective(new RemainingLivesScore<T>(), objectives, true);
		}
		if (Parameters.parameters.booleanParameter("levelObjective")) {
			addObjective(new LevelScore<T>(), objectives, true);
		}
		if (Parameters.parameters.booleanParameter("consistentLevelObjective")) {
			addObjective(new LevelScore<T>(), objectives, new Mode(), true);
		}
		if (Parameters.parameters.booleanParameter("pacManTimeFitness")) {
			addObjective(new SurvivalAndSpeedTimeScore<T>(), objectives, true);
		}
		if (Parameters.parameters.booleanParameter("pacManLureFitness")) {
			addObjective(new LuringScore<T>(), objectives, true);
		}
		// Game Score
		scoreIndexInOtherScores = otherScores.size();
		addObjective(new GameScore<T>(), otherScores, new Average(), false);
		maxScoreIndexInOtherScores = otherScores.size();
		addObjective(new GameScore<T>(), otherScores, new Max(), false);
		// Pill Score
		pillScoreIndexInOtherScores = otherScores.size();
		maxPillScoreIndexInOtherScores = otherScores.size();
		// Ghost Reward
		if(!partiallyObservablePacman) {
			addObjective(new GhostsPerPowerPillScore<T>(true), otherScores, new Average(), false);
			addObjective(new GhostsPerPowerPillScore<T>(false), otherScores, new Average(), false);
			addObjective(new PillScore<T>(), otherScores, new Average(), false);
			addObjective(new PillScore<T>(), otherScores, new Max(), false);
		}
		ghostRewardIndexInOtherScores = otherScores.size();
		if(!partiallyObservablePacman) {
			addObjective(new GhostRewardScore<T>(), otherScores, new Average(), false);	
		}
		maxGhostRewardIndexInOtherScores = otherScores.size();
		if(!partiallyObservablePacman) {
			addObjective(new GhostRewardScore<T>(), otherScores, new Max(), false);
		}
		// Level Scores
		avgLevelIndexInOtherScores = otherScores.size();
		addObjective(new LevelScore<T>(), otherScores, new Average(), false);
		maxLevelIndexInOtherScores = otherScores.size();
		addObjective(new LevelScore<T>(), otherScores, new Max(), false);
		addObjective(new LevelScore<T>(), otherScores, new Mode(), false);
		// Ghosts Eaten
		ghostsEatenIndexInOtherScores = otherScores.size();
		addObjective(new EatenGhostScore<T>(), otherScores, new Average(), false);
		addObjective(new EatenGhostScore<T>(), otherScores, new Max(), false);
		// Missed Ghosts
		ghostRegretScoreInOtherScores = otherScores.size();
		if(!partiallyObservablePacman) {
			addObjective(new GhostRegretScore<T>(), otherScores, new Average(), false);
		}
		// Luring
		luringScoreIndexInOtherScores = otherScores.size();
		if(!partiallyObservablePacman) {
			addObjective(new LuringScore<T>(), otherScores, false);
		}
		// How/When Power Pills Are Eaten
		properPowerPillIndexInOtherScores = otherScores.size();
		if(!partiallyObservablePacman) {
			addObjective(new ProperlyEatenPowerPillScore<T>(), otherScores, false);
		}
		improperPowerPillIndexInOtherScores = otherScores.size();
		if(!partiallyObservablePacman) {	
			addObjective(new ImproperlyEatenPowerPillScore<T>(), otherScores, false);
		}
		powerPillEatenWhenGhostFarIndexInOtherScores = otherScores.size();
		if(!partiallyObservablePacman) {
			addObjective(new PowerPillEatenWhenGhostFarScore<T>(), otherScores, false);
		}
		addObjective(new SurvivalAndSpeedTimeScore<T>(), otherScores, new Average(), false);
		addObjective(new SurvivalAndSpeedTimeScore<T>(), otherScores, new Max(), false);

		specificLevelScoreFirstIndexInOtherScores = otherScores.size();
		for (int i = 0; i < Constants.NUM_MAZES; i++) {
			addObjective(new LevelGameScore<T>(i), otherScores, new Average(), false);
		}
		// Kind of a silly way to track this, but easy
		addObjective(new EdibleTimeParameter<T>(), otherScores, new Average(), false);
		addObjective(new LairTimeParameter<T>(), otherScores, new Average(), false);
	}

	/**
	 * Based on a designation from the fitness mode map, return a collection of
	 * fitness values.
	 *
	 * @param id
	 *            an id from FitnessToModeMap
	 * @param taskScores
	 * @return array of designated fitness values
	 */
	public double[] fitnessArray(int id, Score<T> taskScores) {
		int scoreIndex;
		switch (id) {
		case MsPacManModeSelector.ACTIVE_GHOST_SCORE:
			// Assumes the first subnet is always the ghost network
			scoreIndex = ghostTimeFrameIndices[0];
			break;
		case MsPacManModeSelector.ACTIVE_PILL_SCORE:
			// Assumes the second subnet is always the pill network
			scoreIndex = pillTimeFrameIndices[1];
			break;
		case MsPacManModeSelector.LEVEL_SCORE:
			scoreIndex = avgLevelIndexInOtherScores;
			break;
		case MsPacManModeSelector.GAME_SCORE:
			scoreIndex = scoreIndexInOtherScores;
			break;
		case MsPacManModeSelector.GHOST_SCORE:
			scoreIndex = plainGhostScore ? ghostsEatenIndexInOtherScores : ghostRewardIndexInOtherScores;
			break;
		case MsPacManModeSelector.PILL_SCORE:
			scoreIndex = pillScoreIndexInOtherScores;
			break;
		case MsPacManModeSelector.PROPER_POWER_PILL_SCORE:
			scoreIndex = properPowerPillIndexInOtherScores;
			break;
		case MsPacManModeSelector.LURING_FITNESS:
			scoreIndex = luringScoreIndexInOtherScores;
			break;
		case MsPacManModeSelector.IMPROPER_POWER_PILL_SCORE:
			scoreIndex = improperPowerPillIndexInOtherScores;
			break;
		case MsPacManModeSelector.GHOST_AND_LEVEL_COMBO:
			return new double[] { taskScores.otherStats[plainGhostScore ? ghostsEatenIndexInOtherScores
					: ghostRewardIndexInOtherScores], taskScores.otherStats[avgLevelIndexInOtherScores] };
		case MsPacManModeSelector.PROPER_POWER_PILL_GHOST_COMBO:
			return new double[] {
					taskScores.otherStats[plainGhostScore ? ghostsEatenIndexInOtherScores
							: ghostRewardIndexInOtherScores],
					taskScores.otherStats[properPowerPillIndexInOtherScores] };
		case MsPacManModeSelector.IMPROPER_POWER_PILL_GHOST_COMBO:
			return new double[] {
					taskScores.otherStats[plainGhostScore ? ghostsEatenIndexInOtherScores
							: ghostRewardIndexInOtherScores],
					taskScores.otherStats[improperPowerPillIndexInOtherScores] };
		case MsPacManModeSelector.PILL_AND_NO_POWER_PILL_COMBO:
			return new double[] { taskScores.otherStats[pillScoreIndexInOtherScores],
					taskScores.otherStats[powerPillEatenWhenGhostFarIndexInOtherScores] };
		case MsPacManModeSelector.NO_PREFERENCE:
			// Just use all standard scores, whatever they are
			return Arrays.copyOf(taskScores.scores, taskScores.scores.length);
		default:
			scoreIndex = -100; // error value
			// Check specific ghosts
			for (int j = 0; j < MsPacManModeSelector.SPECIFIC_GHOSTS.length; j++) {
				if (id == MsPacManModeSelector.SPECIFIC_GHOSTS[j]) {
					scoreIndex = taskScores.otherStats.length - CommonConstants.numActiveGhosts + j;
					break;
				}
			}
			// Check specific levels
			for (int j = 0; j < MsPacManModeSelector.SPECIFIC_LEVELS.length; j++) {
				if (id == MsPacManModeSelector.SPECIFIC_LEVELS[j]) {
					scoreIndex = specificLevelScoreFirstIndexInOtherScores + j;
					break;
				}
			}
			// score not found
			if (scoreIndex == -100) {
				System.out.println("Error! Fitness index does not exist: " + id);
				System.exit(1);
			}
			break;
		}
		// Game score is fitness in addition to preferred
		return (eachComponentTracksScoreToo && scoreIndex != scoreIndexInOtherScores 
				? new double[] { taskScores.otherStats[scoreIndex], taskScores.otherStats[scoreIndexInOtherScores] }
		: new double[] { taskScores.otherStats[scoreIndex] });
	}

	/**
	 * Define the Ghost team to evolve against
	 */
	public void loadGhosts() {
		if (ghosts == null) {
			try {			
				//TODO: generalize this by allowing the GhostTeam to be specified as a class parameter
				if(Parameters.parameters.booleanParameter("partiallyObservablePacman")) {
					//create individual ghost controllers
					popacman.examples.StarterGhost.POGhost blinky = new popacman.examples.StarterGhost.POGhost(pacman.game.Constants.GHOST.BLINKY);
					popacman.examples.StarterGhost.POGhost pinky = new popacman.examples.StarterGhost.POGhost(pacman.game.Constants.GHOST.PINKY);
					popacman.examples.StarterGhost.POGhost inky = new popacman.examples.StarterGhost.POGhost(pacman.game.Constants.GHOST.INKY);
					popacman.examples.StarterGhost.POGhost sue = new popacman.examples.StarterGhost.POGhost(pacman.game.Constants.GHOST.SUE);	
					
					//create an EnumMap of ghosts to controllers
					EnumMap<pacman.game.Constants.GHOST, IndividualGhostController> map = new EnumMap<pacman.game.Constants.GHOST, IndividualGhostController>(pacman.game.Constants.GHOST.class);
					
					//put controllers in map
					map.put(pacman.game.Constants.GHOST.BLINKY, blinky);
					map.put(pacman.game.Constants.GHOST.PINKY, pinky);
					map.put(pacman.game.Constants.GHOST.INKY, inky);
					map.put(pacman.game.Constants.GHOST.SUE, sue);
					
					//create GhostControllerFacade
					this.ghosts = new GhostControllerFacade(new MASController(map));
				} else {
					this.ghosts = new GhostControllerFacade((NewGhostController) ClassCreation.createObject("ghostTeam"));
				}
				
			} catch (NoSuchMethodException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		} else {
			ghosts.reset();
		}
	}

	/**
	 * If a static pacman is being used against evolving ghosts, then this
	 * method loads it.
	 * 
	 * Is this actually used anywhere?
	 */
	public void loadPacMan() {
		if (mspacman == null) {
			try {
				//an oldpacman controller
				NewPacManController controller = (NewPacManController) ClassCreation.createObject("staticPacMan");				
				pacman.controllers.PacmanController poController = (pacman.controllers.PacmanController) ClassCreation.createObject("staticPacManPO");
				//pacman.controllers.PacmanController poController = (pacman.controllers.PacmanController) ClassCreation.createObject(popacman.examples.StarterPacMan.MyPacMan.class);
				

				if(evolveGhosts && Parameters.parameters.booleanParameter("partiallyObservablePacman")) {
					this.mspacman = new PacManControllerFacade(poController);
				} else {
					//if partially observable
					this.mspacman = Parameters.parameters.booleanParameter("partiallyObservablePacman") ?
							//convert controller from oldpacman to popacman via OldToNewPacManInterMediaryController
							new PacManControllerFacade(new OldToNewPacManIntermediaryController(controller)) :
							//else use the oldpacman controller
							new PacManControllerFacade(controller);
				}
			} catch (NoSuchMethodException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		} else {
			mspacman.reset();
		}
	}

	@SuppressWarnings("rawtypes")
	public final void addObjective(MsPacManObjective o, ArrayList<MsPacManObjective<T>> list, boolean affectsSelection) {
		addObjective(o, list, null, affectsSelection);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void addObjective(MsPacManObjective o, ArrayList<MsPacManObjective<T>> list, Statistic override, boolean affectsSelection) {
		list.add(o);
		MMNEAT.registerFitnessFunction(o.getClass().getSimpleName(), override, affectsSelection);
	}

	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		exec.log("Genotype ID: " + individual.getId());
		return super.evaluate(individual);
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		Organism<T> organism = evolveGhosts ? new SharedNNGhosts<T>(individual) : new NNMsPacMan<T>(individual);
		//if we are evolving ghosts
		if (evolveGhosts) {
			if(Parameters.parameters.booleanParameter("partiallyObservablePacman")) {
				//create a GhostControllerFacade with a ghost network
				ghosts = new GhostControllerFacade(new OldToNewGhostIntermediaryController(((SharedNNGhosts<T>) organism).controller) );
			} else {
				//throw new UnsupportedOperationException("As of now, Ghost can only be evolved in PO conditions");
				ghosts = new GhostControllerFacade( (NewGhostController) ((SharedNNGhosts<T>) organism).controller );
			}
			loadPacMan();
		//if we are not evolving ghosts, we are evolving pacman
		} else {
			mspacman = Parameters.parameters.booleanParameter("partiallyObservablePacman") ?
					//convert the controls from oldpacman controls to popacman controls
					new PacManControllerFacade(new OldToNewPacManIntermediaryController((NewPacManController) ((NNMsPacMan<T>) organism).controller)):
					//use oldpacman controller, don't convert to popacman
					new PacManControllerFacade((NewPacManController) ((NNMsPacMan<T>) organism).controller);
			loadGhosts();	
		}

		// Side-effects to "game"
		agentEval(mspacman, ghosts, num);
		
		double[] fitnesses = new double[this.numObjectives()];
		double[] scores = new double[this.numOtherScores()];
		// When evolving ghosts, all fitness scores are flipped to negative,
		// because the ghosts are in direct opposition to pacman
		for (int j = 0; j < objectives.size(); j++) {
			fitnesses[j] = (evolveGhosts ? -1 : 1) * objectives.get(j).score(game, organism);
		}
		for (int j = 0; j < otherScores.size(); j++) {
			scores[j] = otherScores.get(j).score(game, organism);
		}
		return new Pair<double[], double[]>(fitnesses, scores);
	}

	/**
	 * Only use if not evolving the ghosts
	 * @param mspacman
	 * @param num
	 * @return
	 */
	public GameFacade agentEval(PacManControllerFacade mspacman, int num) {
		loadGhosts();
		return agentEval(mspacman, this.ghosts, num);
	}	
	
	public GameFacade agentEval(PacManControllerFacade mspacman, GhostControllerFacade ghosts, int num) {
		tcManager.preEval();
		game = Parameters.parameters.booleanParameter("partiallyObservablePacman") ? 
				new GameFacade(new pacman.game.Game(deterministic ? num : RandomNumbers.randomGenerator.nextLong())) : 
				new GameFacade(new Game(deterministic ? num : RandomNumbers.randomGenerator.nextLong()));

		// Collection of options that are not currently possible to set in PO PacMan
		if(!Parameters.parameters.booleanParameter("partiallyObservablePacman")) {
			game.setExitLairEdible(exitLairEdible);
			game.setEndOnlyOnTimeLimit(endOnlyOnTimeLimit);
			game.setRandomLairExit(randomLairExit);
			game.setLairExitDatabase(lairExitDatabase);
			game.setSimultaneousLairExit(simultaneousLairExit);
			game.setGhostsStartOutsideLair(ghostsStartOutsideLair);
			game.setOnlyOneLairExitAllowed(onlyOneLairExitAllowed);
			game.setEndAfterGhostEatingChances(endAfterGhostEatingChances);
			game.setRemovePillsNearPowerPills(removePillsNearPowerPills);
			game.playWithoutPills(noPills);
			game.playWithoutPowerPills(noPowerPills);
			game.setEndAfterPowerPillsEaten(luringTask);
		}
		
		int campNum = tcManager.campSetup(game, num);
		int startingLevel = game.getCurrentLevel();
		mspacman.reset();
		
		if (CommonConstants.recordPacman) {
			exec.runGameTimedRecorded(game, mspacman, ghosts, CommonConstants.watch,
					saveFilePrefix + Parameters.parameters.stringParameter("pacmanSaveFile"));
			// } else if(CommonConstants.replayPacman){
			// exec.replayGame(Parameters.parameters.stringParameter("pacmanSaveFile"),
			// CommonConstants.watch);
		} else if (CommonConstants.watch) {
			// System.out.println("Watch game: " + mspacman);
			exec.runGameTimed(mspacman, ghosts, game);
			// exec.runGameTimed(new HumanController(new KeyBoardInput()),ghosts, true, game);
		} else if (CommonConstants.timedPacman) {
			exec.runGameTimedNonVisual(game, mspacman, ghosts);
		} else {
			exec.runExperiment(mspacman, ghosts, game);
		}
		tcManager.postEval(game, campNum, startingLevel);
		if (MMNEAT.evalReport != null) {
			mspacman.logEvaluationDetails();
		}

		return game;
	}

	@Override
	public int numOtherScores() {
		return otherScores.size();
	}

	@Override
	public int numObjectives() {
		return objectives.size();
	}

	/**
	 * All zeroes, since objectives are positive
	 *
	 * @return
	 */
	@Override
	public double[] minScores() {
		double[] result = new double[numObjectives()];
		for (int i = 0; i < result.length; i++) {
			result[i] = objectives.get(i).minScore();
		}
		return result;
	}

	@Override
	public double[] startingGoals() {
		return minScores();
	}

	@Override
	public String[] sensorLabels() {
		return pacmanInputOutputMediator.sensorLabels();
	}

	@Override
	public String[] outputLabels() {
		return pacmanInputOutputMediator.outputLabels();
	}

	@Override
	public double getTimeStamp() {
		return game.getTotalTime();
	}



	@Override
	public List<Substrate> getSubstrateInformation() {
		if(subs == null) {
			subs = getSubstrateInformationFromScratch();
		}
		return subs;
	}

	/**
	 * Full substrate without dead neurons for specific mazes
	 * @return
	 */
	public List<Substrate> getSubstrateInformationFromScratch() {
		
		int numInputSubstrates = getNumInputSubstrates();
		
		List<Triple<String,Integer,Integer>> output = new LinkedList<>();
		if(Parameters.parameters.booleanParameter("pacManFullScreenOutput")) {
			output.add(new Triple<>("DesiredLocation",MS_PAC_MAN_SUBSTRATE_WIDTH, MS_PAC_MAN_SUBSTRATE_HEIGHT));
		}else {
			output.add(new Triple<>("Direction",3,3));
		}
		List<Substrate> substrateInformation = HyperNEATUtil.getSubstrateInformation(MS_PAC_MAN_SUBSTRATE_WIDTH, MS_PAC_MAN_SUBSTRATE_HEIGHT, numInputSubstrates, output);
		// Will always be the second substrate: compact representation may replace full screen version
		if(!Parameters.parameters.booleanParameter("pacmanFullScreenPowerInput")) {
			Substrate originalPowerPillSubstrate = substrateInformation.get(POWER_PILL_SUBSTRATE_INDEX); // Always the second substrate
			Substrate powerPillSubstrate = new Substrate(new Pair<Integer, Integer>(2, 2), Substrate.INPUT_SUBSTRATE, originalPowerPillSubstrate.getSubLocation(), originalPowerPillSubstrate.getName());		
			substrateInformation.set(POWER_PILL_SUBSTRATE_INDEX, powerPillSubstrate);
		}

		// Provide a way to resize the processing substrates		
		if(!Parameters.parameters.booleanParameter("pacmanFullScreenProcess")) { // Will NOT work with convolution
			int current = numInputSubstrates;
			while(substrateInformation.get(current).getName().startsWith("process")) {
				Substrate originalProcessingSubstrate = substrateInformation.get(current);
				// 10 by 10 are magic numbers ... configure? Move into HyperNEATUtil.getSubstrateInformation?
				Substrate newProcessingSubstrate = new Substrate(new Pair<Integer, Integer>(10, 10), Substrate.PROCCESS_SUBSTRATE, originalProcessingSubstrate.getSubLocation(), originalProcessingSubstrate.getName());		
				substrateInformation.set(current, newProcessingSubstrate);
				current++;
			}			
		} 		

		// Kill some neurons in the D-pad
		if(!Parameters.parameters.booleanParameter("pacManFullScreenOutput")) {
			//kills neurons in corner and center of output substrate
			substrateInformation.get(substrateInformation.size() - 1).addDeadNeuron(0, 0);
			substrateInformation.get(substrateInformation.size() - 1).addDeadNeuron(2, 0);
			substrateInformation.get(substrateInformation.size() - 1).addDeadNeuron(1, 1);
			substrateInformation.get(substrateInformation.size() - 1).addDeadNeuron(0, 2);
			substrateInformation.get(substrateInformation.size() - 1).addDeadNeuron(2, 2);
		}

		return substrateInformation;
	}

	private int getNumInputSubstrates() {
		int numInputSubstrates = 0;
		numInputSubstrates += 2; // One for regular pills, and one for power pills
		numInputSubstrates += Parameters.parameters.booleanParameter("pacmanBothThreatAndEdibleSubstrate") ? 2 : 1; // 2 or 1 substrate for ghosts
		numInputSubstrates += 1; // A substrate for Ms. Pac-Man's location
		return numInputSubstrates;
	}

	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
		if(connections == null) {
			List<String> outputNames = new LinkedList<>();
			if(Parameters.parameters.booleanParameter("pacManFullScreenOutput")) {
				outputNames.add("DesiredLocation");
			}else {
				outputNames.add("Direction");
			}
			connections = HyperNEATUtil.getSubstrateConnectivity(getNumInputSubstrates(), outputNames);			
			// The four input power pill substrate does not contain visual information
			if(!Parameters.parameters.booleanParameter("pacmanFullScreenPowerInput")) {
				for(SubstrateConnectivity sub : connections) { // So do not allow convolution
					if(sub.sourceSubstrateName.equals("Input(" + POWER_PILL_SUBSTRATE_INDEX + ")")) { // Power pill substrate
						sub.connectivityType = SubstrateConnectivity.CTYPE_FULL; // Convolution not allowed
					}
					
				}
			}
		}
		return connections;
	}

	/**
	 * Figures out which neurons can be killed in the
	 * current maze.
	 * 
	 * @param gf game facade, which accesses current maze
	 */
	public void customizeSubstratesForMaze(GameFacade gf) {
		int mazeIndex = gf.getMazeIndex();
		if(!substratesForMaze.containsKey(mazeIndex)) {
			List<Substrate> localSubs = getSubstrateInformationFromScratch(); // Just to make sure subs is not null
			for(Substrate s : localSubs){
				// Only reset full screen substrates that are not processing layers
				if(s.getSize().t1 == MS_PAC_MAN_SUBSTRATE_WIDTH && s.getSize().t2 == MS_PAC_MAN_SUBSTRATE_HEIGHT && s.getStype() != Substrate.PROCCESS_SUBSTRATE) {
					s.killAllNeurons();
					for(int i = 0; i < gf.lengthMaze(); i++) {
						int x = gf.getNodeXCoord(i);
						int y = gf.getNodeYCoord(i);
						int scaledX = x / MsPacManTask.MS_PAC_MAN_NODE_DIM;
						int scaledY = y / MsPacManTask.MS_PAC_MAN_NODE_DIM;
						s.resurrectNeuron(scaledX, scaledY);
					}
				}
			}
			substratesForMaze.put(mazeIndex, localSubs);
		}
		subs = substratesForMaze.get(mazeIndex);
	}
	
	/**
	 * Default behavior
	 */
	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	/**
	 * Default behavior
	 */
	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs;
	}

	@Override
	public void flushSubstrateMemory() {
		// Does nothing: This task does not cache substrate information
	}

	@Override
	public void postConstructionInitialization() {
		try {
			//TODO: Allow for evolution of ghost teams
			if(Parameters.parameters.booleanParameter("evolveGhosts")){
				MsPacManTask.ghostsInputOutputMediator = new GhostsCheckEachDirectionMediator();
				MMNEAT.setNNInputParameters(MsPacManTask.ghostsInputOutputMediator.numIn(), MsPacManTask.ghostsInputOutputMediator.numOut());
			} else {
				MsPacManTask.pacmanInputOutputMediator = (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacmanInputOutputMediator");
				if (MsPacManTask.pacmanInputOutputMediator instanceof VariableDirectionBlockLoadedInputOutputMediator) {
					MsPacManTask.directionalSafetyFunction = (VariableDirectionBlock) ClassCreation.createObject("directionalSafetyFunction");
				}
				// Regular Check-Each-Direction networks
				MMNEAT.setNNInputParameters(MsPacManTask.pacmanInputOutputMediator.numIn(), MsPacManTask.pacmanInputOutputMediator.numOut());
				MsPacManInitialization.setupMsPacmanParameters();
				if (CommonConstants.multitaskModules > 1) {
					MsPacManTask.pacmanMultitaskScheme = (MsPacManModeSelector) ClassCreation.createObject("pacmanMultitaskScheme");
				}
			}
		} catch (NoSuchMethodException e) {
			System.out.println("Failure to initialize classes in MsPacManTask");
			e.printStackTrace();
			System.exit(1);
		}
	}
}	
