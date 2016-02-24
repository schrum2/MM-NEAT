package pacman.game;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManOnlyPillScoreInFullVsEdibleFromCornersMultitask;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNPacManController;
import edu.utexas.cs.nn.tasks.mspacman.data.ScentPath;
import edu.utexas.cs.nn.tasks.mspacman.data.TrainingCampManager;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.random.ResumableRandom;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.awt.Color;
import java.util.*;
import java.util.Map.Entry;
import pacman.Executor;
import static pacman.game.Constants.*;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.*;

/**
 * The implementation of Ms Pac-Man. This class contains the game engine and all
 * methods required to query the state of the game. First, the mazes are loaded
 * once only as they are immutable. The game then proceeds to initialise all
 * variables using default values. The game class also provides numerous methods
 * to extract the game state as a string (used for replays and for communication
 * via pipes during the competition) and to create copies. Care has been taken
 * to implement the game efficiently to ensure that copies can be created
 * quickly.
 *
 * The game has a central update method called advanceGame which takes a move
 * for Ms Pac-Man and up to 4 moves for the ghosts. It then updates the
 * positions of all characters, check whether pills or power pills have been
 * eaten and updates the game state accordingly.
 *
 * All other methods are to access the gamestate and to compute numerous aspects
 * such as directions to taken given a target or a shortest path from a to b.
 * All shortest path distances from any node to any other node are pre-computed
 * and loaded from file. This makes these methods more efficient. Note about the
 * ghosts: ghosts are not allowed to reverse. Hence it is not possible to simply
 * look up the shortest path distance. Instead, one can approximate this
 * greedily or use A* to compute it properly. The former is somewhat quicker and
 * has a low error rate. The latter takes a bit longer but is absolutely
 * accurate. We use the pre-computed shortest path distances as admissable
 * heuristic so it is very efficient.
 */
public final class Game {

    // Needed because the first move when watching is often null
    private int moves = 0;
    //pills stored as bitsets for efficient copying
    private BitSet pills, powerPills;
    //all the game's variables
    private int mazeIndex, levelCount, currentLevelTime, totalTime, score, ghostEatMultiplier, timeOfLastGlobalReversal = -1;
    private int levelCount2; // used with justMaze so difficulty is correct
    private boolean gameOver, pacmanWasEaten, pillWasEaten, powerPillWasEaten;
    private EnumMap<GHOST, Boolean> ghostsEaten;
    // Jacob's parameters
    public LinkedList<Integer> pillEatTimes;
    public LinkedList<Integer> ghostEatTimes;
    private double lureDistanceFitness;
    private long timeInDeadSpace;
    private int timeGhostReward;
    private int timePillReward;
    private int ghostReward;
    private int ghostRegret;
    private int eatenGhosts;
    private int[] specificEatenGhosts;
    private int eatenPills;
    private int properlyEatenPowerPills;
    private int improperlyEatenPowerPills;
    private int powerPillsEatenWhenGhostFar;
    protected boolean exitLairEdible = false;
    protected boolean endOnlyOnTimeLimit = false;
    protected boolean ghostsStartOutsideLair = false;
    protected boolean onlyOneLairExitAllowed = false;
    protected boolean randomLairExit = false;
    protected boolean lairExitDatabase = false;
    protected boolean simultaneousLairExit = false;
    protected boolean endAfterPowerPillsEaten = false;
    protected boolean endAfterGhostEatingChances = false;
    protected boolean playWithoutPillsUntilGhostsThreaten = false;
    protected boolean noPowerPills = false;
    protected boolean removePillsNearPowerPills = false;
    //the data relating to pacman and the ghosts are stored in respective data structures for clarity
    private PacMan pacman;
    private EnumMap<GHOST, Ghost> ghosts;
    //mazes are only loaded once since they don't change over time
    private static Maze[] mazes = new Maze[NUM_MAZES];
    private Maze currentMaze;
    boolean pacManGainsLives = Parameters.parameters.booleanParameter("pacManGainsLives");
    private ArrayList<Integer> scores;
    private ArrayList<Integer> eatenGhostsPerPowerPill;
    private ArrayList<Integer> timesToEatAllGhosts;
    private int timeLastPowerPillEaten = -1;

    // For trouble shooting
    //public static ArrayList<Integer> color = new ArrayList<Integer>();
    
    static {
        for (int i = 0; i < mazes.length; i++) {
            mazes[i] = new Maze(i);
        }
    }
    public static PathsCache[] caches = new PathsCache[NUM_MAZES];

    static {
        resetPathCaches();
    }

    public static int amountCachedPathData() {
        int total = 0;
        for (int i = 0; i < mazes.length; i++) {
            total += caches[i].junctionIndexConverter.size();
        }
        return total;
    }

    public static void resetPathCaches() {
        for (int i = 0; i < mazes.length; i++) {
            caches[i] = new PathsCache(i);
        }
    }
    public ResumableRandom rnd;

    /////////////////////////////////////////////////////////////////////////////
    ///////////////////  Constructors and initialisers  /////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /**
     * Instantiates a new game. The seed is used to initialise the pseudo-random
     * number generator. This way, a game may be replicated exactly by using
     * identical seeds. Note: in the competition, the games received from the
     * game server are using different seeds. Otherwise global reversal events
     * would be predictable.
     *
     * @param seed The seed for the pseudo-random number generator
     */
    public Game(long seed) {
        this(seed, CommonConstants.initialMaze);
    }

    /**
     * Initiates a new game specifying the maze to start with.
     *
     * @param seed Seed used for the pseudo-random numbers
     * @param initialMaze The maze to start the game with
     */
    public Game(long seed, int initialMaze) {
//        if (MONE.evalReport != null) {
//            MONE.evalReport.log("Seed: " + seed);
//            MONE.evalReport.log("Initial Maze: " + initialMaze);
//        }
        rnd = new ResumableRandom(seed);

        _init(initialMaze);
    }

    /**
     * Empty constructor used by the copy method.
     */
    private Game() {
    }

    public void setSimultaneousLairExit(boolean b) {
        simultaneousLairExit = b;
    }

    public void setRandomLairExit(boolean b) {
        randomLairExit = b;
    }

    public void setExitLairEdible(boolean b) {
        exitLairEdible = b;
    }

    public void playWithoutPowerPills() {
        noPowerPills = true;
        powerPills.clear();
    }

    public void playWithPowerPills() {
        noPowerPills = false;
    }

    public void setEndAfterPowerPillsEaten(boolean b) {
        endAfterPowerPillsEaten = b;
    }

    public void setEndAfterGhostEatingChances(boolean b) {
        endAfterGhostEatingChances = b;
    }

    public void playWithoutPills() {
        playWithoutPillsUntilGhostsThreaten = true;
        pills.clear();
    }

    public void playWithPills() {
        playWithoutPillsUntilGhostsThreaten = false;
    }

    public void setEndOnlyOnTimeLimit(boolean b) {
        endOnlyOnTimeLimit = b;
    }
    
    public void setGhostsStartOutsideLair(boolean b) {
        ghostsStartOutsideLair = b;
    }

    public void setOnlyOneLairExitAllowed(boolean b) {
        onlyOneLairExitAllowed = b;
    }
    
    public void setLairExitDatabase(boolean b) {
        lairExitDatabase = b;
    }

    public void setRemovePillsNearPowerPills(boolean b) {
        removePillsNearPowerPills = b;
        if(b) removePillsNearPowerPills();
    }
    
    /**
     * _init.
     *
     * @param initialMaze the initial maze
     */
    private void _init(int initialMaze) {
        scores = new ArrayList<Integer>();
        eatenGhostsPerPowerPill = new ArrayList<Integer>();
        timesToEatAllGhosts = new ArrayList<Integer>();
        if (CommonConstants.justMaze == -1) {
            mazeIndex = initialMaze;
            levelCount2 = 0; // Could make a latter level easier ...
        } else {
            mazeIndex = CommonConstants.justMaze;
            levelCount2 = CommonConstants.justMaze; // So difficulty level is appropriate
        }
        score = currentLevelTime = levelCount = totalTime = 0;
        pillEatTimes = new LinkedList<Integer>();
        ghostEatTimes = new LinkedList<Integer>();

        // Lure distance starts at worst score possible
        lureDistanceFitness = -(Constants.NUM_MAZES * Constants.NUM_GHOSTS * 4 * GameFacade.MAX_DISTANCE);
        ghostReward = eatenGhosts = eatenPills = timeGhostReward = timePillReward = ghostRegret = 0;
        improperlyEatenPowerPills = powerPillsEatenWhenGhostFar = properlyEatenPowerPills = 0;
        timeInDeadSpace = 0;
        specificEatenGhosts = new int[Constants.NUM_GHOSTS];
        ghostEatMultiplier = 1;
        gameOver = false;
        timeOfLastGlobalReversal = -1;
        pacmanWasEaten = false;
        pillWasEaten = false;
        powerPillWasEaten = false;

        ghostsEaten = new EnumMap<GHOST, Boolean>(GHOST.class);

        for (GHOST ghost : GHOST.values()) {
            ghostsEaten.put(ghost, false);
        }

        _setPills(currentMaze = mazes[mazeIndex]);
        _initGhosts();

        int startingNode = currentMaze.initialPacManNodeIndex;
        if(CommonConstants.pacmanStartingPowerPillIndex > -1) { // If pacman should start eval at position of power pill
            startingNode = getPowerPillIndices()[CommonConstants.pacmanStartingPowerPillIndex];
        }
        pacman = new PacMan(startingNode, MOVE.LEFT, NUM_LIVES, false);

        /*
        System.out.println("After _setPills");
        int node = getPowerPillIndices()[0];
        int[] neighbors = getNeighbouringNodes(node);
        System.out.println("INIT:getNeighbouringNodes("+node+") = " + Arrays.toString(neighbors));
        */
    }

    private void removePillsNearPowerPills() {
        /*
        System.out.println("currentMaze.graph.length:"+currentMaze.graph.length);
        System.out.println("currentMaze.initialPacManNodeIndex:"+currentMaze.initialPacManNodeIndex);
        System.out.println("currentMaze.name:"+currentMaze.name);
        System.out.println("currentMaze.powerPillIndices:"+Arrays.toString(currentMaze.powerPillIndices));
        System.out.println("currentMaze.graph[97].nodeIndex:"+currentMaze.graph[97].nodeIndex);
        System.out.println("currentMaze.graph[97].x:"+currentMaze.graph[97].x);
        System.out.println("currentMaze.graph[97].y:"+currentMaze.graph[97].y);
        System.out.println("currentMaze.graph[97].powerPillIndex:"+currentMaze.graph[97].powerPillIndex);
        System.out.println("currentMaze.graph[97].allNeighbouringNodes:"+currentMaze.graph[97].allNeighbouringNodes);
        */
        //new NullPointerException().printStackTrace();
        
        for(int i = 0; i < getPowerPillIndices().length; i++){
            //System.out.println("getPowerPillIndices " + i);
            int node = getPowerPillIndices()[i];
            //System.out.println("Power pill: " + node);
            //color.add(node); // To color for debugging
            Set<Integer> visited = new HashSet<Integer>();
            visited.add(node);
            int[] neighbors = Arrays.copyOf(getNeighbouringNodes(node),2); // Should only be 2 neighbors
            //System.out.println("Immediate neighbors: " + currentMaze + ":getNeighbouringNodes("+node+") = " + Arrays.toString(neighbors));
            
            for(int j = 0; j < neighbors.length; j++){
                int value = neighbors[j];
                //if(i == 0) System.out.println("neighbors " + j + ":" + neighbors[j]);
                //color.add(neighbors[j]);
                visited.add(neighbors[j]);
                int pillIndex = getPillIndex(neighbors[j]);
                //System.out.println("pillIndex:"+pillIndex);
                if(pillIndex != -1 && isPillStillAvailable(pillIndex)){
                    //System.out.println("Before clear: " + currentMaze + ":getNeighbouringNodes("+node+") = " + Arrays.toString(getNeighbouringNodes(node)));
                    pills.clear(pillIndex); // Remove pills encountered
                    //System.out.println("After clear: " + currentMaze + ":getNeighbouringNodes("+node+") = " + Arrays.toString(getNeighbouringNodes(node)));
                    //if(true) return;        
                }
                int[] nextNeighbors = Arrays.copyOf(getNeighbouringNodes(neighbors[j]),2); // Should only be 2 neighbors
                for(Integer n : nextNeighbors) {
                    //System.out.println("nextNeighbors " + n + ":" + visited);
                    if(!visited.contains(n)) {
                        neighbors[j] = n;
                    }
                }
                // second check prevents infinite loop ... though why would that happen?
                if(!isJunction(neighbors[j]) && value != neighbors[j]) {
                    j--; // Stay along this path until all pills are removed
                }
            }

        }
    }
    
    /**
     * _new level reset.
     */
    private void _newLevelReset() {
        if (this.getNumEdibleGhosts() > 0) { // Level ended while ghosts edible
            allGhostsEaten(Integer.MAX_VALUE);
        }
        if (MMNEAT.evalReport != null) {
            MMNEAT.evalReport.log("Level " + levelCount + "/Maze " + mazeIndex);
        }

        // A CEC2011 rule that gives half credit for pills in the level when time limit runs out
        if(Parameters.parameters.booleanParameter("getRemainingPills")){
            score += 0.5 * POWER_PILL * this.getNumberOfActivePowerPills();
            score += 0.5 * PILL * this.getNumberOfActivePills();
            eatenPills += 0.5 * (this.getNumberOfActivePowerPills() + this.getNumberOfActivePills());
        }
        
        addThisLevelScore();
        if (CommonConstants.justMaze == -1) {
            mazeIndex = ++mazeIndex % NUM_MAZES;
        }
        levelCount++;
        levelCount2++;
        currentMaze = mazes[mazeIndex];

        currentLevelTime = 0;
        ghostEatMultiplier = 1;

        if (levelCount >= Parameters.parameters.integerParameter("pacmanMaxLevel")) {
            NNPacManController.timesAllLevelsBeaten++;
            if (MMNEAT.evalReport != null) {
                MMNEAT.evalReport.log("All Levels Beaten");
                MMNEAT.evalReport.log("");
            }
            if (CommonConstants.watch) {
                System.out.println("Beat All Levels");
            }
            gameOver = true;
            return;
        }

        _setPills(currentMaze);
        if(removePillsNearPowerPills) removePillsNearPowerPills();
        _levelReset();
        
        ScentPath.resetAll();
    }

    /**
     * _level reset.
     */
    private void _levelReset() {
        ghostEatMultiplier = 1;

        _initGhosts();

        int startingNode = currentMaze.initialPacManNodeIndex;
        if(CommonConstants.pacmanStartingPowerPillIndex > -1) { // If pacman should start eval at position of power pill
            startingNode = getPowerPillIndices()[CommonConstants.pacmanStartingPowerPillIndex];
        }
        pacman.currentNodeIndex = startingNode;
        pacman.lastMoveMade = MOVE.LEFT;
    }

    /**
     * _set pills.
     *
     * @param maze the maze
     */
    private void _setPills(Maze maze) {
        pills = new BitSet(currentMaze.pillIndices.length);
        pills.set(0, currentMaze.pillIndices.length);
        //System.out.println("Maze " + this.mazeIndex + ": " + currentMaze.pillIndices.length);
        if (playWithoutPillsUntilGhostsThreaten) {
            pills.clear();
        }
        // An option to randomly eat/remove a portion of the pills before the level starts.
        // Makes the task easier along a smooth gradient.
        double preEatenPillPercentage = Parameters.parameters.doubleParameter("preEatenPillPercentage");
        if (preEatenPillPercentage > 0) {
            int numPillsToEat = (int) Math.floor(preEatenPillPercentage * currentMaze.pillIndices.length);
            int[] pillNodesToEat = RandomNumbers.randomXofY(numPillsToEat, currentMaze.pillIndices.length);
            for (int node : pillNodesToEat) {
                pills.clear(node);
            }
        }
        powerPills = new BitSet(currentMaze.powerPillIndices.length);
        powerPills.set(0, currentMaze.powerPillIndices.length);
        if (noPowerPills) {
            powerPills.clear();
        }
    }

    /**
     * _init ghosts.
     */
    private void _initGhosts() {
        ghosts = new EnumMap<GHOST, Ghost>(GHOST.class);

        for (GHOST ghostType : GHOST.values()) {
            ghosts.put(ghostType, new Ghost(ghostType, currentMaze.lairNodeIndex, 0,
                    (int) (ghostType.initialLairTime * (Math.pow(LAIR_REDUCTION, levelCount2 % LEVEL_RESET_REDUCTION))), MOVE.NEUTRAL));
        }
    }

    /**
     * Gets the game state as a string: all variables are written to a string in
     * a pre-determined order. The string may later be used to recreate a game
     * state using the setGameState() method.
     *
     * Variables not included: enableGlobalReversals
     *
     * @return The game state as a string
     */
    public String getGameState() {
        //System.out.println("getGameState()");
        StringBuilder sb = new StringBuilder();

        sb.append(rnd.getSeed());
        sb.append(",");

        sb.append(specificEatenGhosts[0]).append(",").append(specificEatenGhosts[1]).append(",").append(specificEatenGhosts[2]).append(",").append(specificEatenGhosts[3]).append(",");

        sb.append(lureDistanceFitness).append(",").append(timeInDeadSpace).append(",");
        sb.append(powerPillsEatenWhenGhostFar).append(",").append(improperlyEatenPowerPills).append(",").append(properlyEatenPowerPills).append(",");
        sb.append(timePillReward).append(",").append(timeGhostReward).append(",").append(ghostReward).append(",").append(ghostRegret).append(",").append(eatenGhosts).append(",").append(eatenPills).append(",").append(playWithoutPillsUntilGhostsThreaten).append(",").append(noPowerPills).append(",");
        sb.append(endAfterGhostEatingChances).append(",").append(endAfterPowerPillsEaten).append(",");
        sb.append(mazeIndex).append(",").append(totalTime).append(",").append(score).append(",").append(currentLevelTime).append(",").append(levelCount).append(",").append(levelCount2).append(",").append(pacman.currentNodeIndex).append(",").append(pacman.lastMoveMade).append(",").append(pacman.numberOfLivesRemaining).append(",").append(pacman.hasReceivedExtraLife).append(",");

        for (Ghost ghost : ghosts.values()) {
            sb.append(ghost.currentNodeIndex).append(",").append(ghost.edibleTime).append(",").append(ghost.lairTime).append(",").append(ghost.lastMoveMade).append(",");
        }

        for (int i = 0; i < currentMaze.pillIndices.length; i++) {
            if (pills.get(i)) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }

        sb.append(",");

        for (int i = 0; i < currentMaze.powerPillIndices.length; i++) {
            if (powerPills.get(i)) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }

        sb.append(",");
        sb.append(timeOfLastGlobalReversal);
        sb.append(",");
        sb.append(pacmanWasEaten);
        sb.append(",");

        for (GHOST ghost : GHOST.values()) {
            sb.append(ghostsEaten.get(ghost));
            sb.append(",");
        }

        sb.append(pillWasEaten);
        sb.append(",");
        sb.append(powerPillWasEaten);

        return sb.toString();
    }

    /**
     * Sets the game state from a string: the inverse of getGameState(). It
     * reconstructs all the game's variables from the string.
     *
     * @param gameState The game state represented as a string
     */
    public void setGameState(String gameState) {
        //System.out.println("setGameState()");
        String[] values = gameState.split(",");

        int index = 0;

        rnd.hardSetSeed(Long.parseLong(values[index++]));

        specificEatenGhosts = new int[4];
        specificEatenGhosts[0] = Integer.parseInt(values[index++]);
        specificEatenGhosts[1] = Integer.parseInt(values[index++]);
        specificEatenGhosts[2] = Integer.parseInt(values[index++]);
        specificEatenGhosts[3] = Integer.parseInt(values[index++]);

        lureDistanceFitness = Double.parseDouble(values[index++]);
        timeInDeadSpace = Long.parseLong(values[index++]);
        powerPillsEatenWhenGhostFar = Integer.parseInt(values[index++]);
        improperlyEatenPowerPills = Integer.parseInt(values[index++]);
        properlyEatenPowerPills = Integer.parseInt(values[index++]);
        timePillReward = Integer.parseInt(values[index++]);
        timeGhostReward = Integer.parseInt(values[index++]);
        ghostReward = Integer.parseInt(values[index++]);
        ghostRegret = Integer.parseInt(values[index++]);
        eatenGhosts = Integer.parseInt(values[index++]);
        eatenPills = Integer.parseInt(values[index++]);
        playWithoutPillsUntilGhostsThreaten = Boolean.parseBoolean(values[index++]);
        noPowerPills = Boolean.parseBoolean(values[index++]);

        endAfterGhostEatingChances = Boolean.parseBoolean(values[index++]);
        endAfterPowerPillsEaten = Boolean.parseBoolean(values[index++]);

        mazeIndex = Integer.parseInt(values[index++]);
        totalTime = Integer.parseInt(values[index++]);
        score = Integer.parseInt(values[index++]);
        currentLevelTime = Integer.parseInt(values[index++]);
        levelCount = Integer.parseInt(values[index++]);
        levelCount2 = Integer.parseInt(values[index++]);

        pacman = new PacMan(Integer.parseInt(values[index++]), MOVE.valueOf(values[index++]),
                Integer.parseInt(values[index++]), Boolean.parseBoolean(values[index++]));

        ghosts = new EnumMap<GHOST, Ghost>(GHOST.class);

        for (GHOST ghostType : GHOST.values()) {
            ghosts.put(ghostType, new Ghost(ghostType, Integer.parseInt(values[index++]), Integer.parseInt(values[index++]),
                    Integer.parseInt(values[index++]), MOVE.valueOf(values[index++])));
        }

        _setPills(currentMaze = mazes[mazeIndex]);

        for (int i = 0; i < values[index].length(); i++) {
            if (values[index].charAt(i) == '1') {
                pills.set(i);
            } else {
                pills.clear(i);
            }
        }

        index++;

        for (int i = 0; i < values[index].length(); i++) {
            if (values[index].charAt(i) == '1') {
                powerPills.set(i);
            } else {
                powerPills.clear(i);
            }
        }

        timeOfLastGlobalReversal = Integer.parseInt(values[++index]);
        pacmanWasEaten = Boolean.parseBoolean(values[++index]);

        ghostsEaten = new EnumMap<GHOST, Boolean>(GHOST.class);

        for (GHOST ghost : GHOST.values()) {
            ghostsEaten.put(ghost, Boolean.parseBoolean(values[++index]));
        }

        pillWasEaten = Boolean.parseBoolean(values[++index]);
        powerPillWasEaten = Boolean.parseBoolean(values[++index]);
    }

    /**
     * Returns an exact copy of the game. This may be used for forward searches
     * such as minimax. The copying is relatively efficient.
     *
     * @return the game
     */
    public Game copy() {
        Game copy = new Game();

        //copy.scores = (ArrayList<Integer>) this.scores.clone();

        copy.rnd = new ResumableRandom();
        copy.rnd.hardSetSeed(rnd.getSeed());

        copy.currentMaze = currentMaze;
        copy.pills = (BitSet) pills.clone();
        copy.powerPills = (BitSet) powerPills.clone();
        copy.mazeIndex = mazeIndex;
        copy.levelCount = levelCount;
        copy.levelCount2 = levelCount2;
        copy.currentLevelTime = currentLevelTime;
        copy.totalTime = totalTime;
        copy.score = score;
        copy.ghostEatMultiplier = ghostEatMultiplier;
        copy.timeOfLastGlobalReversal = timeOfLastGlobalReversal;
        copy.gameOver = gameOver;

        copy.lureDistanceFitness = lureDistanceFitness;
        copy.timeInDeadSpace = timeInDeadSpace;
        copy.timePillReward = timePillReward;
        copy.timeGhostReward = timeGhostReward;
        copy.ghostReward = ghostReward;
        copy.ghostRegret = ghostRegret;
        copy.eatenGhosts = eatenGhosts;
        copy.specificEatenGhosts = Arrays.copyOf(specificEatenGhosts, specificEatenGhosts.length);
        copy.powerPillsEatenWhenGhostFar = powerPillsEatenWhenGhostFar;
        copy.improperlyEatenPowerPills = improperlyEatenPowerPills;
        copy.properlyEatenPowerPills = properlyEatenPowerPills;
        copy.eatenPills = eatenPills;
        copy.playWithoutPillsUntilGhostsThreaten = playWithoutPillsUntilGhostsThreaten;
        copy.noPowerPills = noPowerPills;

        copy.endAfterGhostEatingChances = endAfterGhostEatingChances;
        copy.endAfterPowerPillsEaten = endAfterPowerPillsEaten;
        copy.ghostsStartOutsideLair = ghostsStartOutsideLair;
        copy.onlyOneLairExitAllowed = onlyOneLairExitAllowed;

        copy.pacman = pacman.copy();

        copy.ghostsEaten = new EnumMap<GHOST, Boolean>(GHOST.class);
        copy.ghosts = new EnumMap<GHOST, Ghost>(GHOST.class);

        for (GHOST ghostType : GHOST.values()) {
            copy.ghosts.put(ghostType, ghosts.get(ghostType).copy());
            copy.ghostsEaten.put(ghostType, ghostsEaten.get(ghostType));
        }

        return copy;
    }

    /////////////////////////////////////////////////////////////////////////////
    ///////////////////////////  Game-engine   //////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /**
     * Central method that advances the game state using the moves supplied by
     * the controllers. It first updates Ms Pac-Man, then the ghosts and then
     * the general game logic.
     *
     * @param pacManMove The move supplied by the Ms Pac-Man controller
     * @param ghostMoves The moves supplied by the ghosts controller
     */
    public void advanceGame(MOVE pacManMove, EnumMap<GHOST, MOVE> ghostMoves) {
        //System.out.println("Seed: " + rnd.getSeed());
        //System.out.println(this.getGameState());
        //System.out.println(this.getCurrentLevelTime() + ":G:"+ghostMoves.values()+":PM=" + pacManMove);
        // Explicitly kill pacman if the special end game code returns null action,
        // but watch out for null action often returned as first move when starting new experiment
        if (pacManMove == null && moves > 10) {
            if (MMNEAT.evalReport != null) {
                MMNEAT.evalReport.log("NULL Move = Special Termination Request");
                MMNEAT.evalReport.log("");
            }
            if (CommonConstants.watch) {
                System.out.println("NULL Action");
            }
            gameOver = true;
            return;
        }
        updatePacMan(pacManMove);
        updateGhosts(ghostMoves);
        updateGame();
        if (TrainingCampManager.recentStates != null) {
            TrainingCampManager.recentStates.giveCurrentState(this);
        }
        moves++;
    }

    public void advanceGameWithoutReverse(MOVE pacManMove, EnumMap<GHOST, MOVE> ghostMoves) {
        updatePacMan(pacManMove);
        updateGhostsWithoutReverse(ghostMoves);
        updateGame();
    }

    public void advanceGameWithForcedReverse(MOVE pacManMove, EnumMap<GHOST, MOVE> ghostMoves) {
        updatePacMan(pacManMove);
        updateGhostsWithForcedReverse(ghostMoves);
        updateGame();
    }

    public void advanceGameWithPowerPillReverseOnly(MOVE pacManMove, EnumMap<GHOST, MOVE> ghostMoves) {
        updatePacMan(pacManMove);

        if (powerPillWasEaten) {
            updateGhostsWithForcedReverse(ghostMoves);
        } else {
            updateGhostsWithoutReverse(ghostMoves);
        }

        updateGame();
    }

    /**
     * Updates the state of Ms Pac-Man given the move returned by the
     * controller.
     *
     * @param pacManMove The move supplied by the Ms Pac-Man controller
     */
    public void updatePacMan(MOVE pacManMove) {
        _updatePacMan(pacManMove);					//move pac-man		
        _eatPill();									//eat a pill
        _eatPowerPill();							//eat a power pill
        if (!this.wasPillEaten() && !this.wasPowerPillEaten()) {
            timeInDeadSpace++;
        }
    }

    /**
     * Updates the states of the ghosts given the moves returned by the
     * controller.
     *
     * @param ghostMoves The moves supplied by the ghosts controller
     */
    public void updateGhosts(EnumMap<GHOST, MOVE> ghostMoves) {
        ghostMoves = _completeGhostMoves(ghostMoves);

        if (!_reverseGhosts(ghostMoves, false)) {
            _updateGhosts(ghostMoves);
        }
    }

    public void updateGhostsWithoutReverse(EnumMap<GHOST, MOVE> ghostMoves) {
        ghostMoves = _completeGhostMoves(ghostMoves);
        _updateGhosts(ghostMoves);
    }

    public void updateGhostsWithForcedReverse(EnumMap<GHOST, MOVE> ghostMoves) {
        ghostMoves = _completeGhostMoves(ghostMoves);
        _reverseGhosts(ghostMoves, true);
    }

    /**
     * Updates the game once the individual characters have been updated: check
     * if anyone can eat anyone else. Then update the lair times and check if Ms
     * Pac-Man should be awarded the extra live. Then update the time and see if
     * the level or game is over.
     */
    public void updateGame() {
        _feast();									//ghosts eat pac-man or vice versa		
        _updateLairTimes();
        _updatePacManExtraLife();

        totalTime++;
        currentLevelTime++;

        _checkLevelState();							//check if level/game is over
    }

    /**
     * This method is for specific purposes such as searching a tree in a
     * specific manner. It has to be used cautiously as it might create an
     * unstable game state and may cause the game to crash.
     *
     * @param feast Whether or not to enable feasting
     * @param updateLairTimes Whether or not to update the lair times
     * @param updateExtraLife Whether or not to update the extra life
     * @param updateTotalTime Whether or not to update the total time
     * @param updateLevelTime Whether or not to update the level time
     */
    public void updateGame(boolean feast, boolean updateLairTimes, boolean updateExtraLife, boolean updateTotalTime, boolean updateLevelTime) {
        if (feast) {
            _feast();				//ghosts eat pac-man or vice versa		
        }
        if (updateLairTimes) {
            _updateLairTimes();
        }
        if (updateExtraLife) {
            _updatePacManExtraLife();
        }

        if (updateTotalTime) {
            totalTime++;
        }
        if (updateLevelTime) {
            currentLevelTime++;
        }

        _checkLevelState();							//check if level/game is over
    }

    /**
     * _update lair times.
     */
    private void _updateLairTimes() {
        boolean exitOccurred = false;
        for (Ghost ghost : ghosts.values()) {
            // Control total number of ghosts by simply keeping non-active ghosts confined to lair
            if (ghost.lairTime > 0 && GameFacade.ghostToIndex(ghost.type) < CommonConstants.numActiveGhosts) {
                ghost.lairTime--;
                if (ghost.lairTime == 0 || (ghostsStartOutsideLair && currentLevelTime == 0)) {
                    if (CommonConstants.imprisonedWhileEdible && this.getNumEdibleGhosts() > 0) {
                        ghost.lairTime += newLairTime();
                    } else {
                        exitOccurred = true;
                        ghostExitsLair(ghost);
                    }
                }
            }
        }
        // See if others need to exit
        if (simultaneousLairExit && exitOccurred) {
            for (Ghost ghost : ghosts.values()) {
                if (GameFacade.ghostToIndex(ghost.type) < CommonConstants.numActiveGhosts) {
                    ghost.lairTime = 0;
                    if (ghost.currentNodeIndex == currentMaze.lairNodeIndex) {
                        ghostExitsLair(ghost);
                    }
                }
            }
        }
    }

    private void ghostExitsLair(Ghost ghost) {
        if(onlyOneLairExitAllowed && currentLevelTime > 0) {
            // If an exit request is occuring after the start of the level, then the level should end
            _newLevelReset();
        } else {
            if (randomLairExit) {
                ghost.currentNodeIndex = currentMaze.junctionIndices[RandomNumbers.randomGenerator.nextInt(currentMaze.junctionIndices.length)];
            } else if(lairExitDatabase) {
                //System.out.println("currentMaze.junctionIndices.length = "+currentMaze.junctionIndices.length);
                ghost.currentNodeIndex = currentMaze.graph[MsPacManOnlyPillScoreInFullVsEdibleFromCornersMultitask.newGhostLocation(mazeIndex, ghost)].nodeIndex;
            } else {
                ghost.currentNodeIndex = currentMaze.initialGhostNodeIndex;
            }
            if (exitLairEdible) {
                ghost.edibleTime = newEdibleTime();
            }
        }
    }

    /**
     * _update pac man extra life.
     */
    private void _updatePacManExtraLife() {
        if (pacManGainsLives && !pacman.hasReceivedExtraLife && score >= EXTRA_LIFE_SCORE) //award 1 extra life at 10000 points
        {
            pacman.hasReceivedExtraLife = true;
            pacman.numberOfLivesRemaining++;
        }
    }

    /**
     * _update pac man.
     *
     * @param move the move
     */
    private void _updatePacMan(MOVE move) {
        pacman.lastMoveMade = _correctPacManDir(move);
        pacman.currentNodeIndex = pacman.lastMoveMade == MOVE.NEUTRAL ? pacman.currentNodeIndex
                : currentMaze.graph[pacman.currentNodeIndex].neighbourhood.get(pacman.lastMoveMade);
    }

    /**
     * _correct pac man dir.
     *
     * @param direction the direction
     * @return the mOVE
     */
    private MOVE _correctPacManDir(MOVE direction) {
        Node node = currentMaze.graph[pacman.currentNodeIndex];

        //direction is correct, return it
        if (node.neighbourhood.containsKey(direction)) {
            return direction;
        } else {
            //try to use previous direction (i.e., continue in the same direction)
            if (node.neighbourhood.containsKey(pacman.lastMoveMade)) {
                return pacman.lastMoveMade;
            } //else stay put
            else {
                return MOVE.NEUTRAL;
            }
        }
    }

    /**
     * _update ghosts.
     *
     * @param moves the moves
     */
    private void _updateGhosts(EnumMap<GHOST, MOVE> moves) {
        for (Entry<GHOST, MOVE> entry : moves.entrySet()) {
            Ghost ghost = ghosts.get(entry.getKey());

            if (ghost.lairTime == 0) {
                if (ghost.edibleTime == 0 || ghost.edibleTime % GHOST_SPEED_REDUCTION != 0) {
                    ghost.lastMoveMade = _checkGhostDir(ghost, entry.getValue());
                    moves.put(entry.getKey(), ghost.lastMoveMade);
                    ghost.currentNodeIndex = currentMaze.graph[ghost.currentNodeIndex].neighbourhood.get(ghost.lastMoveMade);
                }
            }
        }
    }

    private EnumMap<GHOST, MOVE> _completeGhostMoves(EnumMap<GHOST, MOVE> moves) {
        if (moves == null) {
            moves = new EnumMap<GHOST, MOVE>(GHOST.class);

            for (GHOST ghostType : GHOST.values()) {
                moves.put(ghostType, ghosts.get(ghostType).lastMoveMade);
            }
        }

        if (moves.size() < NUM_GHOSTS) {
            for (GHOST ghostType : GHOST.values()) {
                if (!moves.containsKey(ghostType)) {
                    moves.put(ghostType, MOVE.NEUTRAL);
                }
            }
        }

        return moves;
    }

    /**
     * _check ghost dir.
     *
     * @param ghost the ghost
     * @param direction the direction
     * @return the mOVE
     */
    private MOVE _checkGhostDir(Ghost ghost, MOVE direction) {
        //Gets the neighbours of the node with the node that would correspond to reverse removed
        Node node = currentMaze.graph[ghost.currentNodeIndex];

        //The direction is possible and not opposite to the previous direction of that ghost
        if (node.neighbourhood.containsKey(direction) && direction != ghost.lastMoveMade.opposite()) {
            return direction;
        } else {
            if (node.neighbourhood.containsKey(ghost.lastMoveMade)) {
                return ghost.lastMoveMade;
            } else {
                MOVE[] moves = node.allPossibleMoves.get(ghost.lastMoveMade);
                return moves[rnd.nextInt(moves.length)];
            }
        }
    }

    /**
     * _eat pill.
     */
    private void _eatPill() {
        pillWasEaten = false;

        int pillIndex = currentMaze.graph[pacman.currentNodeIndex].pillIndex;

        if (pillIndex >= 0 && pills.get(pillIndex)) {
            eatenPills++;
            timePillReward += levelTimeLeft();
            score += PILL;
            if (pillEatTimes != null) {
                pillEatTimes.add(this.getTotalTime());
            }
            pills.clear(pillIndex);
            pillWasEaten = true;
        }
    }

    public int levelTimeLeft() {
        return CommonConstants.pacManLevelTimeLimit - currentLevelTime;
    }

    public List<Integer> getPillEatTimes() {
        return pillEatTimes;
    }

    public List<Integer> getGhostEatTimes() {
        return ghostEatTimes;
    }

    /**
     * _eat power pill.
     */
    private void _eatPowerPill() {
        powerPillWasEaten = false;

        int powerPillIndex = currentMaze.graph[pacman.currentNodeIndex].powerPillIndex;

        if (powerPillIndex >= 0 && powerPills.get(powerPillIndex)) {
            if (this.getNumEdibleGhosts() > 0 || // Ate new power pill while ghosts still edible
                    this.getThreatGhostLocations().length == 0) { // Ate new power pill while all ghosts in lair  
                allGhostsEaten(Integer.MAX_VALUE);
            }
            timeLastPowerPillEaten = this.getTotalTime();
            score += POWER_PILL;
            
            // Track locations of ghosts at point when power pill is eaten, if all ghosts are threats
            if(MMNEAT.ghostLocationsOnPowerPillEaten != null && getThreatGhostLocations().length == Constants.NUM_GHOSTS) {
                // Maze, Power Pill, Ghost 1, Ghost 2, Ghost 3, Ghost 4
                String data = this.mazeIndex+"\t"+powerPillIndex;
                for(Ghost g: ghosts.values()){
                    data += "\t" + g.currentNodeIndex;
                }
                MMNEAT.ghostLocationsOnPowerPillEaten.log(data);
            }
            
            if (eatenGhostsPerPowerPill != null) {
                eatenGhostsPerPowerPill.add(0);
            }
            int[] threats = this.getThreatGhostLocations();
            if (threats.length == CommonConstants.numActiveGhosts) {
                properlyEatenPowerPills++;

                int current = this.getPacmanCurrentNodeIndex();
                double minDistance = Double.MAX_VALUE;
                for (Integer threat : threats) {
                    minDistance = Math.min(minDistance, this.getShortestPathDistance(current, threat));
                }
                if (minDistance > Parameters.parameters.integerParameter("closeGhostDistance")) {
                    powerPillsEatenWhenGhostFar++;
                }
            } else {
                if (Parameters.parameters.booleanParameter("dieOnImproperPowerPillEating")) {
                    if (MMNEAT.evalReport != null) {
                        MMNEAT.evalReport.log("Death By Improper Power Pill Eating");
                        MMNEAT.evalReport.log("");
                    }
                    if (CommonConstants.watch) {
                        System.out.println("Death By Improper Power Pill Eating");
                    }
                    gameOver = true;
                    return;
                }
                improperlyEatenPowerPills++;
                ghostRegret += (CommonConstants.numActiveGhosts - threats.length);
            }
            int pacManCurrent = this.getPacmanCurrentNodeIndex();
            //System.out.println("---------------------------------");
            for (Integer node : threats) {
                double distance = Math.min(getDistance(pacManCurrent, node, DM.PATH), GameFacade.MAX_DISTANCE);
                //System.out.println(distance + " out of " + GameFacade.MAX_DISTANCE);
                lureDistanceFitness += GameFacade.MAX_DISTANCE - distance;
            }
            if (pillEatTimes != null) {
                pillEatTimes.add(this.getTotalTime());
            }
            eatenPills++;
            ghostEatMultiplier = 1;
            powerPills.clear(powerPillIndex);

            int newEdibleTime = newEdibleTime();

            for (Ghost ghost : ghosts.values()) {
                if (ghost.lairTime == 0) {
                    ghost.edibleTime = newEdibleTime;
                } else {
                    ghost.edibleTime = 0;
                }
            }

            powerPillWasEaten = true;
        }
    }

    public int getGhostReward() {
        return ghostReward;
    }

    /**
     * Ghost regret tracks the number of edible ghost chances that were missed.
     * To count towards ghost regret, the chance to eat the ghost must first
     * arise by eating a power pill. If certain ghosts could not go into the
     * edible state because they were in the lair at the time, then they
     * contribute to regret. If ghosts were already edible, then a chance to eat
     * them was missed, and they contribute to regret. Most obviously, ghosts
     * whose edible time runs out become threats again, and they contribute to
     * regret.
     *
     * @return amount of ghost regret (positive)
     */
    public int getGhostRegret() {
        return ghostRegret;
    }

    public int getTimeGhostReward() {
        return timeGhostReward;
    }

    public int getEatenGhosts() {
        return eatenGhosts;
    }

    public int getEatenPills() {
        return eatenPills;
    }

    /**
     * Power pill was properly eaten if no ghosts were edible at the time
     *
     * @return
     */
    public int getProperlyEatenPowerPills() {
        return properlyEatenPowerPills;
    }

    /**
     * Power pill was improperly eaten if a ghost was edible at the time
     *
     * @return
     */
    public int getImproperlyEatenPowerPills() {
        return improperlyEatenPowerPills;
    }

    /**
     * Number of power pills eaten when the nearest ghost is at a distance
     * defined in CompleteTaskModeSelector
     *
     * @return
     */
    public int getPowerPillsEatenWhenGhostFar() {
        return powerPillsEatenWhenGhostFar;
    }

    public int getEatenPowerPills() {
        return this.properlyEatenPowerPills + this.improperlyEatenPowerPills;
    }

    private boolean _reverseGhosts(EnumMap<GHOST, MOVE> moves, boolean force) {
        boolean reversed = false;
        boolean globalReverse = false;

        if (CommonConstants.allowRandomGhostReversals && rnd.nextDouble() < GHOST_REVERSAL) {
            globalReverse = true;
        }

        try {
            if (!moves.isEmpty()) {
                for (Entry<GHOST, MOVE> entry : moves.entrySet()) {
                    Ghost ghost = ghosts.get(entry.getKey());

                    if (currentLevelTime > 1 && ghost.lairTime == 0 && ghost.lastMoveMade != MOVE.NEUTRAL) {
                        if (force || (powerPillWasEaten || globalReverse)) {
                            ghost.lastMoveMade = ghost.lastMoveMade.opposite();
                            ghost.currentNodeIndex = currentMaze.graph[ghost.currentNodeIndex].neighbourhood.get(ghost.lastMoveMade);
                            reversed = true;
                            timeOfLastGlobalReversal = totalTime;
                        }
                    }
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("moves:" + moves);
            System.out.println("moves.isEmpty:" + moves.isEmpty());
            System.out.println("size:" + moves.size());
            System.out.println("entrySet:" + moves.entrySet());
            throw e;
        }

        return reversed;
    }

    /**
     * _feast.
     */
    private void _feast() {
        pacmanWasEaten = false;

        for (GHOST ghost : GHOST.values()) {
            ghostsEaten.put(ghost, false);
        }

        for (Ghost ghost : ghosts.values()) {
            int distance = getShortestPathDistance(pacman.currentNodeIndex, ghost.currentNodeIndex);

            if (distance <= EAT_DISTANCE && distance != -1) {
                if (ghost.edibleTime > 0) //pac-man eats ghost
                {
                    score += GHOST_EAT_SCORE * ghostEatMultiplier;
                    int last = eatenGhostsPerPowerPill == null ? 0 : eatenGhostsPerPowerPill.size() - 1;
                    if (!exitLairEdible && eatenGhostsPerPowerPill != null) {
                        eatenGhostsPerPowerPill.set(last, eatenGhostsPerPowerPill.get(last) + 1);
                    }
                    if (ghostEatTimes != null) {
                        ghostEatTimes.add(this.getTotalTime());
                    }
                    eatenGhosts++;
                    specificEatenGhosts[GameFacade.ghostToIndex(ghost.type)]++;
                    ghostReward += ghostEatMultiplier;
                    timeGhostReward += ghost.edibleTime * ghostEatMultiplier; // Reward eating more ghosts faster
                    ghostEatMultiplier *= 2;
                    
                    // Pretty sure this doesn't need to be here at all.
                    // If I wanted this effect, I could just use the ghosts eaten fitness with this setting
//                    if (exitLairEdible) {
//                        ghostEatMultiplier = 1;
//                    }
                    ghost.edibleTime = 0;
                    ghost.lairTime = newLairTime();
                    ghost.currentNodeIndex = currentMaze.lairNodeIndex;
                    ghost.lastMoveMade = MOVE.NEUTRAL;

                    ghostsEaten.put(ghost.type, true);
                    if (!exitLairEdible) {
                        if (eatenGhostsPerPowerPill != null && eatenGhostsPerPowerPill.get(last) == CommonConstants.numActiveGhosts) {
                            allGhostsEaten(this.getTotalTime() - this.timeLastPowerPillEaten);
                        } else if (this.getNumEdibleGhosts() == 0) {
                            allGhostsEaten(Integer.MAX_VALUE);
                        }
                    }
                } else //ghost eats pac-man
                {
                    // Since pacman died, chance to eat currently edible ghosts was missed
                    ghostRegret += this.getNumEdibleGhosts();

                    pacman.numberOfLivesRemaining--;
                    pacmanWasEaten = true;
                    if (!CommonConstants.watch && Executor.deaths != null) {
                        Executor.deaths.log(this.mazeIndex + ":" + this.getPacmanCurrentNodeIndex());
                    }

                    if (pacman.numberOfLivesRemaining <= 0) {
                        if (MMNEAT.evalReport != null) {
                            MMNEAT.evalReport.log("Lost All Lives");
                            MMNEAT.evalReport.log("");
                        }
                        if (CommonConstants.watch) {
                            System.out.println("Lost All Lives: " + ghost.type);
                        }
                        gameOver = true;
                    } else {
                        _levelReset();
                    }

                    return;
                }
            }
        }

        for (Ghost ghost : ghosts.values()) {
            if (ghost.edibleTime > 0) {
                ghost.edibleTime--;
                // Ghost just became inedible, so a chance to eat it was missed
                if (ghost.edibleTime == 0) {
                    if (CommonConstants.infiniteEdibleTime) {
                        // So that ghosts can keep moving even though they will remain edible
                        ghost.edibleTime = Constants.GHOST_SPEED_REDUCTION;
                    } else {
                        ghostRegret++;
                        TrainingCampManager.ghostEatingCamp(this.getTotalTime());
                        if (RandomNumbers.randomGenerator.nextDouble() < Parameters.parameters.doubleParameter("powerPillPunishmentRate")) {
                            // Harsh punishment for failing to eat all ghosts
                            if (MMNEAT.evalReport != null) {
                                MMNEAT.evalReport.log("Dead For Failing To Eat All Ghosts");
                                MMNEAT.evalReport.log("");
                            }
                            if (CommonConstants.watch) {
                                System.out.println("Dead For Failing To Eat All Ghosts");
                            }
                            gameOver = true;
                        }
                        if (this.getNumEdibleGhosts() == 0) { // Edible time ran out on last edible ghost
                            allGhostsEaten(Integer.MAX_VALUE);
                        }
                    }
                }
            }
        }

        if (gameOver && CommonConstants.watch) {
            System.out.println("Premature end for failing to eat ghosts");
        }
    }

    /**
     * _check level state.
     */
    private void _checkLevelState() {
        //put a cap on the total time a game can be played for
        if (totalTime + 1 > MAX_TIME) {
            if (MMNEAT.evalReport != null) {
                MMNEAT.evalReport.log("Max Game Time Reached");
                MMNEAT.evalReport.log("");
            }
            if (CommonConstants.watch) {
                System.out.println("Max Game Time Reached");
            }
            gameOver = true;
            score += pacman.numberOfLivesRemaining * AWARD_LIFE_LEFT;
        }

//        boolean threats = false;
//        for(Ghost g: ghosts.values()) {
//            if(g.edibleTime == 0 && g.lairTime == 0) {
//                System.out.println(g.type +" threat: g.edibleTime="+g.edibleTime+", g.lairTime="+g.lairTime);
//                threats = true;
//            }
//        }
//        if(threats){
//            System.out.println("Time:"+this.currentLevelTime);
//            System.out.println("!endOnlyOnTimeLimit:"+!endOnlyOnTimeLimit);
//            System.out.println("(pills.isEmpty() || endAfterGhostEatingChances || endAfterPowerPillsEaten):"+(pills.isEmpty() || endAfterGhostEatingChances || endAfterPowerPillsEaten)+":"+pills.isEmpty() +"||"+ endAfterGhostEatingChances +"||"+ endAfterPowerPillsEaten);
//            System.out.println("powerPills.isEmpty():"+powerPills.isEmpty());
//            System.out.println("(endAfterPowerPillsEaten || !(playWithoutPillsUntilGhostsThreaten || endAfterGhostEatingChances) || !anyIsEdible()):"+(endAfterPowerPillsEaten || !(playWithoutPillsUntilGhostsThreaten || endAfterGhostEatingChances) || !anyIsEdible()));
//            System.out.println("endAfterPowerPillsEaten/"+endAfterPowerPillsEaten+" || !(playWithoutPillsUntilGhostsThreaten || endAfterGhostEatingChances)/"+!(playWithoutPillsUntilGhostsThreaten || endAfterGhostEatingChances)+" || !anyIsEdible()/"+!anyIsEdible());
//            System.out.println("Also: playWithoutPillsUntilGhostsThreaten/"+playWithoutPillsUntilGhostsThreaten+" || endAfterGhostEatingChances/"+endAfterGhostEatingChances);
//            System.out.println("------------------------------");
//        }
        
        //if all pills have been eaten or the time is up...
        if ((!endOnlyOnTimeLimit
                && (pills.isEmpty() || endAfterGhostEatingChances || endAfterPowerPillsEaten)
                && powerPills.isEmpty()
                && (endAfterPowerPillsEaten || !(playWithoutPillsUntilGhostsThreaten || endAfterGhostEatingChances) || !anyIsEdible()))
                || (!CommonConstants.pacmanFatalTimeLimit && currentLevelTime >= CommonConstants.pacManLevelTimeLimit)) {
            _newLevelReset();
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    ///////////////////  Query Methods (return only)  ///////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /**
     * Returns whether pacman was eaten in the last time step
     *
     * @return whether Ms Pac-Man was eaten.
     */
    public boolean wasPacManEaten() {
        return pacmanWasEaten;
    }

    /**
     * Returns whether a ghost was eaten in the last time step
     *
     * @return whether a ghost was eaten.
     */
    public boolean wasGhostEaten(GHOST ghost) {
        return ghostsEaten.get(ghost);
    }

    public int getNumGhostsEaten() {
        int count = 0;

        for (GHOST ghost : GHOST.values()) {
            if (ghostsEaten.get(ghost)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns whether a pill was eaten in the last time step
     *
     * @return whether a pill was eaten.
     */
    public boolean wasPillEaten() {
        return pillWasEaten;
    }

    /**
     * Returns whether a power pill was eaten in the last time step
     *
     * @return whether a power pill was eaten.
     */
    public boolean wasPowerPillEaten() {
        return powerPillWasEaten;
    }

    /**
     * Returns the time when the last global reversal event took place.
     *
     * @return time the last global reversal event took place (not including
     * power pill reversals)
     */
    public int getTimeOfLastGlobalReversal() {
        return timeOfLastGlobalReversal;
    }

    /**
     * Checks whether the game is over or not: all lives are lost or 16 levels
     * have been played. The variable is set by the methods _feast() and
     * _checkLevelState().
     *
     * @return true, if successful
     */
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * Returns the current maze of the game.
     *
     * @return The current maze.
     */
    public Maze getCurrentMaze() {
        return currentMaze;
    }

    /**
     * Returns the x coordinate of the specified node.
     *
     * @param nodeIndex the node index
     * @return the node x cood
     */
    public int getNodeXCoord(int nodeIndex) {
        return currentMaze.graph[nodeIndex].x;
    }

    /**
     * Returns the y coordinate of the specified node.
     *
     * @param nodeIndex The node index
     * @return The node's y coordinate
     */
    public int getNodeYCoord(int nodeIndex) {
        return currentMaze.graph[nodeIndex].y;
    }

    /**
     * Gets the index of the current maze.
     *
     * @return The maze index
     */
    public int getMazeIndex() {
        return mazeIndex;
    }

    /**
     * Returns the current level.
     *
     * @return The current level
     */
    public int getCurrentLevel() {
        return levelCount;
    }

    /**
     * Returns the number of nodes in the current maze.
     *
     * @return number of nodes in the current maze.
     */
    public int getNumberOfNodes() {
        return currentMaze.graph.length;
    }

    /**
     * Returns the current value awarded for eating a ghost.
     *
     * @return the current value awarded for eating a ghost.
     */
    public int getGhostCurrentEdibleScore() {
        return GHOST_EAT_SCORE * ghostEatMultiplier;
    }

    /**
     * Returns the node index where ghosts start in the maze once leaving the
     * lair.
     *
     * @return the node index where ghosts start after leaving the lair.
     */
    public int getGhostInitialNodeIndex() {
        return currentMaze.initialGhostNodeIndex;
    }

    /**
     * Whether the pill specified is still there or has been eaten.
     *
     * @param pillIndex The pill index
     * @return true, if is pill still available
     */
    public boolean isPillStillAvailable(int pillIndex) {
        return pills.get(pillIndex);
    }

    /**
     * Whether the power pill specified is still there or has been eaten.
     *
     * @param powerPillIndex The power pill index
     * @return true, if is power pill still available
     */
    public boolean isPowerPillStillAvailable(int powerPillIndex) {
        return powerPills.get(powerPillIndex);
    }

    /**
     * Returns the pill index of the node specified. This can be -1 if there is
     * no pill at the specified node.
     *
     * @param nodeIndex The Index of the node.
     * @return a number corresponding to the pill index (or -1 if node has no
     * pill)
     */
    public int getPillIndex(int nodeIndex) {
        return currentMaze.graph[nodeIndex].pillIndex;
    }

    /**
     * Returns the power pill index of the node specified. This can be -1 if
     * there is no power pill at the specified node.
     *
     * @param nodeIndex The Index of the node.
     * @return a number corresponding to the power pill index (or -1 if node has
     * no pill)
     */
    public int getPowerPillIndex(int nodeIndex) {
        return currentMaze.graph[nodeIndex].powerPillIndex;
    }

    /**
     * Returns the array of node indices that are junctions (3 or more
     * neighbours).
     *
     * @return the junction indices
     */
    public int[] getJunctionIndices() {
        return currentMaze.junctionIndices;
    }

    /**
     * Returns the indices to all the nodes that have pills.
     *
     * @return the pill indices
     */
    public int[] getPillIndices() {
        return currentMaze.pillIndices;
    }

    /**
     * Returns the indices to all the nodes that have power pills.
     *
     * @return the power pill indices
     */
    public int[] getPowerPillIndices() {
        return currentMaze.powerPillIndices;
    }

    /**
     * Current node index of Ms Pac-Man.
     *
     * @return the pacman current node index
     */
    public int getPacmanCurrentNodeIndex() {
        return pacman.currentNodeIndex;
    }

    /**
     * Current node index of Ms Pac-Man.
     *
     * @return the pacman last move made
     */
    public MOVE getPacmanLastMoveMade() {
        return pacman.lastMoveMade;
    }

    /**
     * Lives that remain for Ms Pac-Man.
     *
     * @return the number of lives remaining
     */
    public int getPacmanNumberOfLivesRemaining() {
        return pacman.numberOfLivesRemaining;
    }

    /**
     * Current node at which the specified ghost resides.
     *
     * @param ghostType the ghost type
     * @return the ghost current node index
     */
    public int getGhostCurrentNodeIndex(GHOST ghostType) {
        return ghosts.get(ghostType).currentNodeIndex;
    }

    /**
     * Current direction of the specified ghost.
     *
     * @param ghostType the ghost type
     * @return the ghost last move made
     */
    public MOVE getGhostLastMoveMade(GHOST ghostType) {
        return ghosts.get(ghostType).lastMoveMade;
    }

    /**
     * Returns the edible time for the specified ghost.
     *
     * @param ghostType the ghost type
     * @return the ghost edible time
     */
    public int getGhostEdibleTime(GHOST ghostType) {
        return ghosts.get(ghostType).edibleTime;
    }

    /**
     * Simpler check to see if a ghost is edible.
     *
     * @param ghostType the ghost type
     * @return true, if is ghost edible
     */
    public boolean isGhostEdible(GHOST ghostType) {
        return ghosts.get(ghostType).edibleTime > 0;
    }

    /**
     * Only used to check for level end in the no pills variant of the game, and
     * therefore does not interfere with the main game code.
     *
     * @return
     */
    private boolean anyIsEdible() {
        for (GHOST g : ghosts.keySet()) {
            if (isGhostEdible(g)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the score of the game.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the time of the current level (important with respect to
     * LEVEL_LIMIT).
     *
     * @return the current level time
     */
    public int getCurrentLevelTime() {
        return currentLevelTime;
    }

    /**
     * Total time the game has been played for (at most LEVEL_LIMIT*MAX_LEVELS).
     *
     * @return the total time
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Total number of pills in the mazes[gs.curMaze]
     *
     * @return the number of pills
     */
    public int getNumberOfPills() {
        return currentMaze.pillIndices.length;
    }

    /**
     * Total number of power pills in the mazes[gs.curMaze]
     *
     * @return the number of power pills
     */
    public int getNumberOfPowerPills() {
        return currentMaze.powerPillIndices.length;
    }

    /**
     * Total number of pills in the mazes[gs.curMaze]
     *
     * @return the number of active pills
     */
    public int getNumberOfActivePills() {
        return pills.cardinality();
    }

    /**
     * Total number of power pills in the mazes[gs.curMaze]
     *
     * @return the number of active power pills
     */
    public int getNumberOfActivePowerPills() {
        return powerPills.cardinality();
    }

    /**
     * Time left that the specified ghost will spend in the lair.
     *
     * @param ghostType the ghost type
     * @return the ghost lair time
     */
    public int getGhostLairTime(GHOST ghostType) {
        return ghosts.get(ghostType).lairTime;
    }

    /**
     * returns the indices of all active pills in the mazes[gs.curMaze]
     *
     * @return the active pills indices
     */
    public int[] getActivePillsIndices() {
        int[] indices = new int[pills.cardinality()];

        int index = 0;

        for (int i = 0; i < currentMaze.pillIndices.length; i++) {
            if (pills.get(i)) {
                indices[index++] = currentMaze.pillIndices[i];
            }
        }

        return indices;
    }

    /**
     * returns the indices of all active power pills in the mazes[gs.curMaze]
     *
     * @return the active power pills indices
     */
    public int[] getActivePowerPillsIndices() {
        int[] indices = new int[powerPills.cardinality()];

        int index = 0;

        for (int i = 0; i < currentMaze.powerPillIndices.length; i++) {
            if (powerPills.get(i)) {
                indices[index++] = currentMaze.powerPillIndices[i];
            }
        }

        return indices;
    }

    /**
     * If in lair (getLairTime(-)>0) or if not at junction.
     *
     * @param ghostType the ghost type
     * @return true, if successful
     */
    public boolean doesGhostRequireAction(GHOST ghostType) {
        //inlcude neutral here for the unique case where the ghost just left the lair
        return ((isJunction(ghosts.get(ghostType).currentNodeIndex) || (ghosts.get(ghostType).lastMoveMade == MOVE.NEUTRAL) && ghosts.get(ghostType).currentNodeIndex == currentMaze.initialGhostNodeIndex)
                && (ghosts.get(ghostType).edibleTime == 0 || ghosts.get(ghostType).edibleTime % GHOST_SPEED_REDUCTION != 0));
    }

    /**
     * Checks if the node specified by the nodeIndex is a junction.
     *
     * @param nodeIndex the node index
     * @return true, if is junction
     */
    public boolean isJunction(int nodeIndex) {
        return currentMaze.graph[nodeIndex].numNeighbouringNodes > 2;
    }

    /**
     * Gets the possible moves from the node index specified.
     *
     * @param nodeIndex The current node index
     * @return The set of possible moves
     */
    public MOVE[] getPossibleMoves(int nodeIndex) {
        return currentMaze.graph[nodeIndex].allPossibleMoves.get(MOVE.NEUTRAL);
    }

    /**
     * Gets the possible moves except the one that corresponds to the reverse of
     * the move supplied.
     *
     * @param nodeIndex The current node index
     * @param lastModeMade The last mode made (possible moves will exclude the
     * reverse)
     * @return The set of possible moves
     */
    public MOVE[] getPossibleMoves(int nodeIndex, MOVE lastModeMade) {
        return currentMaze.graph[nodeIndex].allPossibleMoves.get(lastModeMade);
    }

    /**
     * Gets the neighbouring nodes from the current node index.
     *
     * @param nodeIndex The current node index
     * @return The set of neighbouring nodes
     */
    public int[] getNeighbouringNodes(int nodeIndex) {
        return currentMaze.graph[nodeIndex].allNeighbouringNodes.get(MOVE.NEUTRAL);
    }

    /**
     * Gets the neighbouring nodes from the current node index excluding the
     * node that corresponds to the opposite of the last move made which is
     * given as an argument.
     *
     * @param nodeIndex The current node index
     * @param lastModeMade The last mode made
     * @return The set of neighbouring nodes except the one that is opposite of
     * the last move made
     */
    public int[] getNeighbouringNodes(int nodeIndex, MOVE lastModeMade) {
        return currentMaze.graph[nodeIndex].allNeighbouringNodes.get(lastModeMade);
    }

    /**
     * Given a node index and a move to be made, it returns the node index the
     * move takes one to. If there is no neighbour in that direction, the method
     * returns -1.
     *
     * @param nodeIndex The current node index
     * @param moveToBeMade The move to be made
     * @return The node index of the node the move takes one to
     */
    public int getNeighbour(int nodeIndex, MOVE moveToBeMade) {
        Integer neighbour = currentMaze.graph[nodeIndex].neighbourhood.get(moveToBeMade);

        return neighbour == null ? -1 : neighbour;
    }

    /**
     * Method that returns the direction to take given a node index and an index
     * of a neighbouring node. Returns null if the neighbour is invalid.
     *
     * @param currentNodeIndex The current node index.
     * @param neighbourNodeIndex The direct neighbour (node index) of the
     * current node.
     * @return the move to make to reach direct neighbour
     */
    public MOVE getMoveToMakeToReachDirectNeighbour(int currentNodeIndex, int neighbourNodeIndex) {
        for (MOVE move : MOVE.values()) {
            if (currentMaze.graph[currentNodeIndex].neighbourhood.containsKey(move)
                    && currentMaze.graph[currentNodeIndex].neighbourhood.get(move) == neighbourNodeIndex) {
                return move;
            }
        }

        return null;
    }

    /////////////////////////////////////////////////////////////////////////////
    ///////////////////  Helper Methods (computational)  ////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the PATH distance from any node to any other node.
     *
     * @param fromNodeIndex the from node index
     * @param toNodeIndex the to node index
     * @return the shortest path distance
     */
    public int getShortestPathDistance(int fromNodeIndex, int toNodeIndex) {
        if (fromNodeIndex == toNodeIndex) {
            return 0;
        } else if (fromNodeIndex < toNodeIndex) {
            return currentMaze.shortestPathDistances[((toNodeIndex * (toNodeIndex + 1)) / 2) + fromNodeIndex];
        } else {
            return currentMaze.shortestPathDistances[((fromNodeIndex * (fromNodeIndex + 1)) / 2) + toNodeIndex];
        }
    }

    /**
     * Returns the EUCLEDIAN distance between two nodes in the current
     * mazes[gs.curMaze].
     *
     * @param fromNodeIndex the from node index
     * @param toNodeIndex the to node index
     * @return the euclidean distance
     */
    public double getEuclideanDistance(int fromNodeIndex, int toNodeIndex) {
        return Math.sqrt(Math.pow(currentMaze.graph[fromNodeIndex].x - currentMaze.graph[toNodeIndex].x, 2) + Math.pow(currentMaze.graph[fromNodeIndex].y - currentMaze.graph[toNodeIndex].y, 2));
    }

    /**
     * Returns the MANHATTAN distance between two nodes in the current
     * mazes[gs.curMaze].
     *
     * @param fromNodeIndex the from node index
     * @param toNodeIndex the to node index
     * @return the manhattan distance
     */
    public int getManhattanDistance(int fromNodeIndex, int toNodeIndex) {
        return (int) (Math.abs(currentMaze.graph[fromNodeIndex].x - currentMaze.graph[toNodeIndex].x) + Math.abs(currentMaze.graph[fromNodeIndex].y - currentMaze.graph[toNodeIndex].y));
    }

    /**
     * Gets the distance.
     *
     * @param fromNodeIndex the from node index
     * @param toNodeIndex the to node index
     * @param distanceMeasure the distance measure
     * @return the distance
     */
    public double getDistance(int fromNodeIndex, int toNodeIndex, DM distanceMeasure) {
        switch (distanceMeasure) {
            case PATH:
                return getShortestPathDistance(fromNodeIndex, toNodeIndex);
            case EUCLID:
                return getEuclideanDistance(fromNodeIndex, toNodeIndex);
            case MANHATTAN:
                return getManhattanDistance(fromNodeIndex, toNodeIndex);
        }

        return -1;
    }

    /**
     * Returns the distance between two nodes taking reversals into account.
     *
     * @param fromNodeIndex the index of the originating node
     * @param toNodeIndex the index of the target node
     * @param lastMoveMade the last move made
     * @param distanceMeasure the distance measure to be used
     * @return the distance between two nodes.
     */
    public double getDistance(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade, DM distanceMeasure) {
        switch (distanceMeasure) {
            case PATH:
                return getShortestPathDistance(fromNodeIndex, toNodeIndex, lastMoveMade);
            case EUCLID:
                return getEuclideanDistance(fromNodeIndex, toNodeIndex);
            case MANHATTAN:
                return getManhattanDistance(fromNodeIndex, toNodeIndex);
        }

        return -1;
    }

    /**
     * Gets the closest node index from node index.
     *
     * @param fromNodeIndex the from node index
     * @param targetNodeIndices the target node indices
     * @param distanceMeasure the distance measure
     * @return the closest node index from node index
     */
    public int getClosestNodeIndexFromNodeIndex(int fromNodeIndex, int[] targetNodeIndices, DM distanceMeasure) {
        double minDistance = Integer.MAX_VALUE;
        int target = -1;

        for (int i = 0; i < targetNodeIndices.length; i++) {
            if (targetNodeIndices[i] == -1) {
                continue;
            }
            double distance = 0;

            distance = getDistance(targetNodeIndices[i], fromNodeIndex, distanceMeasure);

            if (distance < minDistance) {
                minDistance = distance;
                target = targetNodeIndices[i];
            }
        }

        return target;
    }

    /**
     * Gets the farthest node index from node index.
     *
     * @param fromNodeIndex the from node index
     * @param targetNodeIndices the target node indices
     * @param distanceMeasure the distance measure
     * @return the farthest node index from node index
     */
    public int getFarthestNodeIndexFromNodeIndex(int fromNodeIndex, int[] targetNodeIndices, DM distanceMeasure) {
        double maxDistance = Integer.MIN_VALUE;
        int target = -1;

        for (int i = 0; i < targetNodeIndices.length; i++) {
            double distance = 0;

            distance = getDistance(targetNodeIndices[i], fromNodeIndex, distanceMeasure);

            if (distance > maxDistance) {
                maxDistance = distance;
                target = targetNodeIndices[i];
            }
        }

        return target;
    }

    /**
     * Gets the next move towards target.
     *
     * @param fromNodeIndex the from node index
     * @param toNodeIndex the to node index
     * @param distanceMeasure the distance measure
     * @return the next move towards target
     */
    public MOVE getNextMoveTowardsTarget(int fromNodeIndex, int toNodeIndex, DM distanceMeasure) {
        MOVE move = null;

        double minDistance = Integer.MAX_VALUE;

        for (Entry<MOVE, Integer> entry : currentMaze.graph[fromNodeIndex].neighbourhood.entrySet()) {
            double distance = getDistance(entry.getValue(), toNodeIndex, distanceMeasure);

            if (distance < minDistance) {
                minDistance = distance;
                move = entry.getKey();
            }
        }

        return move;
    }

    /**
     * Gets the next move away from target.
     *
     * @param fromNodeIndex the from node index
     * @param toNodeIndex the to node index
     * @param distanceMeasure the distance measure
     * @return the next move away from target
     */
    public MOVE getNextMoveAwayFromTarget(int fromNodeIndex, int toNodeIndex, DM distanceMeasure) {
        MOVE move = null;

        double maxDistance = Integer.MIN_VALUE;

        for (Entry<MOVE, Integer> entry : currentMaze.graph[fromNodeIndex].neighbourhood.entrySet()) {
            double distance = getDistance(entry.getValue(), toNodeIndex, distanceMeasure);

            if (distance > maxDistance) {
                maxDistance = distance;
                move = entry.getKey();
            }
        }

        return move;
    }

    /**
     * Gets the approximate next move towards target not considering directions
     * opposing the last move made.
     *
     * @param fromNodeIndex The node index from which to move (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @param distanceMeasure The distance measure required (Manhattan,
     * Euclidean or Straight line)
     * @return The approximate next move towards target (chosen greedily)
     */
    public MOVE getApproximateNextMoveTowardsTarget(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade, DM distanceMeasure) {
        MOVE move = null;

        double minDistance = Integer.MAX_VALUE;

        for (Entry<MOVE, Integer> entry : currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            double distance = getDistance(entry.getValue(), toNodeIndex, distanceMeasure);

            if (distance < minDistance) {
                minDistance = distance;
                move = entry.getKey();
            }
        }

        return move;
    }

    /**
     * Gets the approximate next move away from a target not considering
     * directions opposing the last move made.
     *
     * @param fromNodeIndex The node index from which to move (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @param distanceMeasure The distance measure required (Manhattan,
     * Euclidean or Straight line)
     * @return The approximate next move towards target (chosen greedily)
     */
    public MOVE getApproximateNextMoveAwayFromTarget(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade, DM distanceMeasure) {
        MOVE move = null;

        double maxDistance = Integer.MIN_VALUE;

        for (Entry<MOVE, Integer> entry : currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            double distance = getDistance(entry.getValue(), toNodeIndex, distanceMeasure);

            if (distance > maxDistance) {
                maxDistance = distance;
                move = entry.getKey();
            }
        }

        return move;
    }

    /**
     * Gets the exact next move towards target taking into account reversals.
     * This uses the pre-computed paths.
     *
     * @param fromNodeIndex The node index from which to move (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @param distanceMeasure the distance measure to be used
     * @return the next move towards target
     */
    public MOVE getNextMoveTowardsTarget(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade, DM distanceMeasure) {
        MOVE move = null;

        double minDistance = Integer.MAX_VALUE;

        for (Entry<MOVE, Integer> entry : currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            // schrum2: 12/24: The last move made has to be updated, as it changes with each step on the path
            lastMoveMade = entry.getKey();
            /////////////////////////////
            double distance = getDistance(entry.getValue(), toNodeIndex, lastMoveMade, distanceMeasure);

            if (distance < minDistance) {
                minDistance = distance;
                move = entry.getKey();
            }
        }

        return move;
    }

    /**
     * Gets the exact next move away from target taking into account reversals.
     * This uses the pre-computed paths.
     *
     * @param fromNodeIndex The node index from which to move (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @param distanceMeasure the distance measure to be used
     * @return the next move away from target
     */
    public MOVE getNextMoveAwayFromTarget(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade, DM distanceMeasure) {
        MOVE move = null;

        double maxDistance = Integer.MIN_VALUE;

        for (Entry<MOVE, Integer> entry : currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            double distance = getDistance(entry.getValue(), toNodeIndex, lastMoveMade, distanceMeasure);

            if (distance > maxDistance) {
                maxDistance = distance;
                move = entry.getKey();
            }
        }

        return move;
    }

    /**
     * Gets the A* path considering previous moves made (i.e., opposing actions
     * are ignored)
     *
     * @param fromNodeIndex The node index from which to move (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @return The A* path
     *
     * @deprecated use getShortestPath() instead.
     */
    @Deprecated
    public int[] getAStarPath(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade) {
        return getShortestPath(fromNodeIndex, toNodeIndex, lastMoveMade);
    }

    /**
     * Gets the shortest path from node A to node B as specified by their
     * indices.
     *
     * @param fromNodeIndex The node index from where to start (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @return the shortest path from start to target
     */
    public int[] getShortestPath(int fromNodeIndex, int toNodeIndex) {
        return caches[mazeIndex].getPathFromA2B(fromNodeIndex, toNodeIndex);
    }

    /**
     * Gets the approximate shortest path taking into account the last move made
     * (i.e., no reversals). This is approximate only as the path is computed
     * greedily. A more accurate path can be obtained using A* which is slightly
     * more costly.
     *
     * @param fromNodeIndex The node index from where to start (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @return the shortest path from start to target
     *
     * @deprecated use getShortestPath() instead.
     */
    @Deprecated
    public int[] getApproximateShortestPath(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade) {
        return getShortestPath(fromNodeIndex, toNodeIndex, lastMoveMade);
    }

    /**
     * Gets the shortest path taking into account the last move made (i.e., no
     * reversals). This is approximate only as the path is computed greedily. A
     * more accurate path can be obtained using A* which is slightly more
     * costly.
     *
     * @param fromNodeIndex The node index from where to start (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @return the shortest path from start to target
     */
    public int[] getShortestPath(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade) {
        if (currentMaze.graph[fromNodeIndex].neighbourhood.size() == 0)//lair
        {
            return new int[0];
        }
        assert currentMaze.graph.length > fromNodeIndex : "From node " + fromNodeIndex + " not in graph";
        assert currentMaze.graph.length > toNodeIndex : "To node " + toNodeIndex + " not in graph";
        return caches[mazeIndex].getPathFromA2B(fromNodeIndex, toNodeIndex, lastMoveMade);
    }

    /**
     * Similar to getApproximateShortestPath but returns the distance of the
     * path only. It is slightly more efficient.
     *
     * @param fromNodeIndex The node index from where to start (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @return the exact distance of the path
     *
     * @deprecated use getShortestPathDistance() instead.
     */
    @Deprecated
    public int getApproximateShortestPathDistance(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade) {
        int result = getShortestPathDistance(fromNodeIndex, toNodeIndex, lastMoveMade);
        GameView.addPoints(this, Color.yellow, this.getShortestPath(fromNodeIndex, toNodeIndex, lastMoveMade));
        return result;
    }

    /**
     * Similar to getShortestPath but returns the distance of the path only. It
     * is slightly more efficient.
     *
     * @param fromNodeIndex The node index from where to start (i.e., current
     * position)
     * @param toNodeIndex The target node index
     * @param lastMoveMade The last move made
     * @return the exact distance of the path
     */
    public int getShortestPathDistance(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade) {
        if (currentMaze.graph[fromNodeIndex].neighbourhood.size() == 0)//lair
        {
            return 0;
        }

        return caches[mazeIndex].getPathDistanceFromA2B(fromNodeIndex, toNodeIndex, lastMoveMade);
    }

    public double getScore(int level) {
        if (level >= scores.size()) {
            addThisLevelScore();
        }
        assert !Parameters.parameters.booleanParameter("ignorePillScore") || scores.get(level) <= 4 * Constants.POWER_PILL + (CommonConstants.numActiveGhosts >= 1 ? 4 * Constants.GHOST_EAT_SCORE : 0) + (CommonConstants.numActiveGhosts >= 2 ? 4 * 2 * Constants.GHOST_EAT_SCORE : 0) + (CommonConstants.numActiveGhosts >= 3 ? 4 * 4 * Constants.GHOST_EAT_SCORE : 0) + (CommonConstants.numActiveGhosts >= 4 ? 4 * 8 * Constants.GHOST_EAT_SCORE : 0) : "GhostRewardScore for level " + level + " too high: " + scores.get(level) + " out of " + scores;
        return scores.get(level);
    }

    private void addThisLevelScore() {
        if (scores != null) {
            // scores contains points accrued on each specific level
            int thisLevelScore = this.getScore();
            for (Integer previousScore : scores) {
                thisLevelScore -= previousScore;
            }
            assert !Parameters.parameters.booleanParameter("ignorePillScore") || thisLevelScore <= 4 * Constants.POWER_PILL + (CommonConstants.numActiveGhosts >= 1 ? 4 * Constants.GHOST_EAT_SCORE : 0) + (CommonConstants.numActiveGhosts >= 2 ? 4 * 2 * Constants.GHOST_EAT_SCORE : 0) + (CommonConstants.numActiveGhosts >= 3 ? 4 * 4 * Constants.GHOST_EAT_SCORE : 0) + (CommonConstants.numActiveGhosts >= 4 ? 4 * 8 * Constants.GHOST_EAT_SCORE : 0) : "GhostRewardScore for level current too high: " + thisLevelScore + " out of " + scores;
            scores.add(thisLevelScore);
        }
    }

    public int getEatenGhosts(int ghostIndex) {
        return specificEatenGhosts[ghostIndex];
    }

    public int getTimePillReward() {
        return timePillReward;
    }

    public double getLureDistanceSum() {
        return lureDistanceFitness;
    }

    public long getTimeInDeadSpace() {
        return timeInDeadSpace;
    }

    // Copied from GameFacade
    public int[] getThreatGhostLocations() {
        ArrayList<Integer> ghostPositions = new ArrayList<Integer>(Constants.NUM_GHOSTS);
        for (int i = 0; i < Constants.NUM_GHOSTS; i++) {
            if (isGhostThreat(i)) {
                ghostPositions.add(getGhostCurrentNodeIndex(GameFacade.indexToGhost(i)));
            }
        }
        return ArrayUtil.intArrayFromArrayList(ghostPositions);
    }

    // copied from GameFacade
    public boolean isGhostThreat(int ghostIndex) {
        GHOST g = GameFacade.indexToGhost(ghostIndex);
        return !isGhostEdible(g) && getNeighbouringNodes(getGhostCurrentNodeIndex(g)).length > 0;
    }

    /**
     * Count current number of edible ghosts
     *
     * @return
     */
    public int getNumEdibleGhosts() {
        int result = 0;
        for (int i = 0; i < Constants.NUM_GHOSTS; i++) {
            GHOST g = GameFacade.indexToGhost(i);
            if (isGhostEdible(g)) {
                result++;
            }
        }
        return result;
    }

    /**
     * For each power pill, 0 to 4 ghosts can be eaten. This returns the average
     * of ghosts eaten for each power pill eaten.
     *
     * @return
     */
    public double averageGhostsEatenPerPowerPill(boolean punishUneatenPowerPills) {
        // Average across power pills that were eaten, or all that could have been eaten?
        if(punishUneatenPowerPills) {
            int numPowerPills = 4;
            int total = numPowerPills * Parameters.parameters.integerParameter("pacmanMaxLevel");
            while (eatenGhostsPerPowerPill.size() < total) {
                eatenGhostsPerPowerPill.add(0);
            }
        }
        return StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(eatenGhostsPerPowerPill));
    }

    public double averageTimeToEatAllGhostsAfterPowerPill() {
        int numPowerPills = 4;
        int total = numPowerPills * Parameters.parameters.integerParameter("pacmanMaxLevel");
        while (timesToEatAllGhosts.size() < total) {
            allGhostsEaten(Integer.MAX_VALUE);
        }
        return StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(timesToEatAllGhosts));
    }

    public int newEdibleTime() {
        return (int) (EDIBLE_TIME * (Math.pow(EDIBLE_TIME_REDUCTION, levelCount2 % LEVEL_RESET_REDUCTION)));
    }

    private int newLairTime() {
        return (int) (COMMON_LAIR_TIME * (Math.pow(LAIR_REDUCTION, levelCount2 % LEVEL_RESET_REDUCTION)));
    }

    private void allGhostsEaten(int time) {
        if (timesToEatAllGhosts != null) {
            if (CommonConstants.watch) {
                System.out.println("Time " + time + " taken to eat all ghosts");
            }
            timesToEatAllGhosts.add(time);
        }
    }
}
