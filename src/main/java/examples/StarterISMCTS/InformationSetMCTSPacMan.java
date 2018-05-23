package examples.StarterISMCTS;

import com.fossgalaxy.object.annotations.ObjectDef;
import pacman.controllers.PacmanController;
import pacman.game.Drawable;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.Maze;
import pacman.game.internal.PacMan;
import prediction.GhostLocation;
import prediction.PillModel;
import prediction.fast.GhostPredictionsFast;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Random;
import java.util.List;

import static pacman.game.Constants.*;

/**
 * Created by piers on 30/09/16.
 */
public class InformationSetMCTSPacMan extends PacmanController implements Drawable {
    protected final int maxTreeDepth;
    protected final int maxRolloutDepth;
    protected Random random = new Random();
    private GhostPredictionsFast predictions;
    private PillModel pillModel;
    private Maze currentMaze;
    private Game mostRecentGame;
    private Color[] redAlphas;
    private int[] ghostEdibleTime;
    public InformationSetMCTSPacMan() {
        maxTreeDepth = 50;
        maxRolloutDepth = 300;

        redAlphas = new Color[256];
        for (int i = 0; i < 256; i++) {
            redAlphas[i] = new Color(255, 0, 0, i);
        }

        ghostEdibleTime = new int[GHOST.values().length];
    }

    @ObjectDef("IS-MCTS")
    public InformationSetMCTSPacMan(int maxTreeDepth, int maxRolloutDepth) {
        this.maxTreeDepth = maxTreeDepth;
        this.maxRolloutDepth = maxRolloutDepth;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        if(currentMaze != game.getCurrentMaze()){
            currentMaze = game.getCurrentMaze();
            predictions = null;
            pillModel = null;
            System.out.println("New Maze");
            Arrays.fill(ghostEdibleTime, -1);
        }
        mostRecentGame = game;
        if (game.gameOver()) return null;

        if (game.wasPacManEaten()) {
            predictions = null;
        }

        if (predictions == null) {
            predictions = new GhostPredictionsFast(game.getCurrentMaze());
            predictions.preallocate();
        }
        if (pillModel == null) {
            pillModel = new PillModel(game.getNumberOfPills());

            int[] indices = game.getCurrentMaze().pillIndices;
            for (int index : indices) {
                pillModel.observe(index, true);
            }
        }

        // Update the pill model with what isn't available anymore
        int pillIndex = game.getPillIndex(game.getPacmanCurrentNodeIndex());
        if (pillIndex != -1) {
            Boolean pillState = game.isPillStillAvailable(pillIndex);
            if (pillState != null && !pillState) {
                pillModel.observe(pillIndex, false);
            }
        }

        // Get observations of ghosts and pass them in to the predictor
        for (GHOST ghost : GHOST.values()) {
            if (ghostEdibleTime[ghost.ordinal()] != -1) {
                ghostEdibleTime[ghost.ordinal()]--;
            }

            int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
            if (ghostIndex != -1) {
                predictions.observe(ghost, ghostIndex, game.getGhostLastMoveMade(ghost));
                ghostEdibleTime[ghost.ordinal()] = game.getGhostEdibleTime(ghost);
            } else {
                List<GhostLocation> locations = predictions.getGhostLocations(ghost);
                locations.stream().filter(location -> game.isNodeObservable(location.getIndex())).forEach(location -> {
                    predictions.observeNotPresent(ghost, location.getIndex());
                });
            }
        }
        ISNode root = new ISNode(this, game);
        while (System.currentTimeMillis() < timeDue) {
            // Determinise and form a game state!
            Game copy = obtainDeterminisedState(game);
            ISNode travel = root.select(copy);
            double score = travel.rollout(copy);
            travel.updateValues(score);
        }
        predictions.update();
        return root.selectBestMove();
    }

    private Game obtainDeterminisedState(Game game) {
        GameInfo info = game.getPopulatedGameInfo();
        info.setPacman(new PacMan(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade(), 0, false));
        EnumMap<GHOST, GhostLocation> locations = predictions.sampleLocations();
        info.fixGhosts(ghost -> {
            GhostLocation location = locations.get(ghost);
            if (location != null) {
                int edibleTime = ghostEdibleTime[ghost.ordinal()];
                return new Ghost(ghost, location.getIndex(), edibleTime, 0, location.getLastMoveMade());
            } else {
                return new Ghost(ghost, game.getGhostInitialNodeIndex(), 0, 0, MOVE.NEUTRAL);
            }
        });

        for (int i = 0; i < pillModel.getPills().length(); i++) {
            info.setPillAtIndex(i, pillModel.getPills().get(i));
        }
        return game.getGameFromInfo(info);
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public void draw(Graphics2D graphics) {
//        System.out.println("Drawing!");
        // Draw it
        for (int i = 0; i < mostRecentGame.getNumberOfNodes(); i++) {
            double probability = predictions.calculate(i);
            if (probability > 1E-4) {
                graphics.setColor(redAlphas[(int) Math.min(255 * probability, 255)]);
                graphics.fillRect(
                        mostRecentGame.getNodeXCood(i) * MAG - 1,
                        mostRecentGame.getNodeYCood(i) * MAG + 3,
                        14, 14
                );
            }
        }
    }
}

class ISNode {

    private final InformationSetMCTSPacMan informationSetMCTSPacMan;
    private ISNode parent;
    private MOVE moveToThisState;
    private ISNode[] children;
    private MOVE[] legalMoves;
    private int childrenExpandedSoFar;

    private int visits;
    private double score;
    private int treeDepth;

    public ISNode(InformationSetMCTSPacMan informationSetMCTSPacMan, Game game) {
        this.informationSetMCTSPacMan = informationSetMCTSPacMan;
        treeDepth = 0;
        this.legalMoves = getLegalMovesNotIncludingBackwards(game);
        this.children = new ISNode[legalMoves.length];
    }

    public ISNode(ISNode parent, MOVE moveToThisState, MOVE[] legalMoves) {
        this.informationSetMCTSPacMan = parent.informationSetMCTSPacMan;
        this.parent = parent;
        this.treeDepth = parent.treeDepth + 1;
        this.moveToThisState = moveToThisState;
        this.legalMoves = legalMoves;
        this.children = new ISNode[legalMoves.length];
    }

    public ISNode select(Game game) {
        ISNode current = this;
        while (current.treeDepth < informationSetMCTSPacMan.maxTreeDepth && !game.gameOver()) {
            if (current.isFullyExpanded()) {
                current = current.selectBestChild();
                advanceGame(game, current.moveToThisState);
            } else {
                current = current.expand(game);
                advanceGame(game, current.moveToThisState);
                return current;
            }
        }
        return current;
    }

    private void advanceGame(Game game, MOVE move) {
        game.advanceGame(move, getBasicGhostMoves(game));
//        game.advanceGameWithoutReverse(move, getBasicGhostMoves(game));
    }

    public double rollout(Game game) {
        int rolloutDepth = treeDepth;
        Random random = informationSetMCTSPacMan.random;
        while (rolloutDepth < informationSetMCTSPacMan.maxRolloutDepth) {
            if (game.gameOver()) break;
            MOVE[] legalMoves = getLegalMovesNotIncludingBackwards(game);
            MOVE randomMove = legalMoves[random.nextInt(legalMoves.length)];
            advanceGame(game, randomMove);
            rolloutDepth++;
        }
        return calculateHeuristic(game);
    }

    private int calculateHeuristic(Game game){
        return game.getScore() + game.getTotalTime() + (1000 * game.getCurrentLevel());
    }

    public void updateValues(double value) {
        ISNode current = this;
        while (current.parent != null) {
            current.visits++;
            current.score += value;
            current = current.parent;
        }
        // Root node
        current.visits++;
    }

    public ISNode expand(Game game) {
        // Select random unselected child
        int index = -1;
        double bestScore = -Double.MAX_VALUE;
        for (int i = 0; i < children.length; i++) {
            if (children[i] == null) {
                double score = informationSetMCTSPacMan.random.nextDouble();
                if (score > bestScore) {
                    index = i;
                    bestScore = score;
                }
            }
        }
        childrenExpandedSoFar++;

        advanceGame(game, legalMoves[index]);
        MOVE[] childMoves = (isRoot()) ? getAllLegalMoves(game) : getLegalMovesNotIncludingBackwards(game);
        ISNode child = new ISNode(this, legalMoves[index], childMoves);
        children[index] = child;
        return child;
    }

    public ISNode selectBestChild() {
        ISNode bestChild = null;
        double bestScore = -Double.MAX_VALUE;
        for (ISNode child : children) {
            double score = child.calculateChild();
            if (score > bestScore) {
                bestChild = child;
                bestScore = score;
            }
        }
        return bestChild;
    }

    public MOVE selectBestMove() {
        ISNode bestChild = null;
        double bestScore = -Double.MAX_VALUE;
        for (ISNode child : children) {
            if (child == null) continue;
            double score = child.score;
            if (score > bestScore) {
                bestChild = child;
                bestScore = score;
            }
        }
        return bestChild == null ? MOVE.NEUTRAL : bestChild.moveToThisState;
    }

    public boolean isRoot() {
        return parent == null;
    }

    private boolean isFullyExpanded() {
        return children != null && childrenExpandedSoFar == children.length;
    }

    private double calculateChild() {
        return (score / visits) + Math.sqrt(2 * Math.log((parent.visits + 1) / visits));
    }

    // Send all the ghosts towards Ms. Pac-Man
    protected EnumMap<GHOST, MOVE> getBasicGhostMoves(Game game) {
        EnumMap<GHOST, MOVE> moves = new EnumMap<>(GHOST.class);
        int pacmanLocation = game.getPacmanCurrentNodeIndex();
        for (GHOST ghost : GHOST.values()) {
            int index = game.getGhostCurrentNodeIndex(ghost);
            MOVE previousMove = game.getGhostLastMoveMade(ghost);
            if (game.isJunction(index)) {
                try {
                    MOVE move = (game.isGhostEdible(ghost))
                            ? game.getApproximateNextMoveAwayFromTarget(index, pacmanLocation, previousMove, DM.PATH)
                            : game.getNextMoveTowardsTarget(index, pacmanLocation, previousMove, DM.PATH);
                    moves.put(ghost, move);
                }catch(NullPointerException npe){
                    System.err.println("PacmanLocation: " + pacmanLocation + " Maze Index: " + game.getMazeIndex() + " Last Move: " + previousMove);
                }
            } else {
                moves.put(ghost, previousMove);
            }
        }
        return moves;
    }

    protected EnumMap<GHOST, MOVE> getRandomGhostMoves(Game game) {
        EnumMap<GHOST, MOVE> moves = new EnumMap<>(GHOST.class);
        Random random = informationSetMCTSPacMan.random;
        for (GHOST ghost : GHOST.values()) {
            int index = game.getGhostCurrentNodeIndex(ghost);
            MOVE previousMove = game.getGhostLastMoveMade(ghost);
            if (previousMove == null) {
                System.out.println("Problem");
            }
            // Get allowed moves from there
            MOVE[] possibleMoves;
            if (previousMove != MOVE.NEUTRAL) {
                possibleMoves = game.getCurrentMaze().graph[index].allPossibleMoves.get(previousMove);
            } else {
                possibleMoves = new MOVE[game.getCurrentMaze().graph[index].neighbourhood.keySet().size()];
                possibleMoves = game.getCurrentMaze().graph[index].neighbourhood.keySet().toArray(possibleMoves);
            }
            if (possibleMoves == null || possibleMoves.length == 0) {
                moves.put(ghost, MOVE.NEUTRAL);
            } else {
                moves.put(ghost, possibleMoves[random.nextInt(possibleMoves.length)]);
            }
        }
        return moves;
    }

    protected MOVE[] getLegalMovesNotIncludingBackwards(Game game) {
        return game.getCurrentMaze().graph[game.getPacmanCurrentNodeIndex()].allPossibleMoves.get(game.getPacmanLastMoveMade());
    }

    protected MOVE[] getAllLegalMoves(Game game) {
        Maze maze = game.getCurrentMaze();
        int index = game.getPacmanCurrentNodeIndex();
        return maze.graph[index].neighbourhood.keySet().toArray(new MOVE[maze.graph[index].neighbourhood.keySet().size()]);
    }

    public void printChildren() {
        if (children == null) return;
        System.out.println("Children: ");
        for (ISNode child : children) {
            if (child != null) {
                System.out.println("\tMove: " + child.moveToThisState + "Visits: " + child.visits + " Score: " + child.score);
            }
        }
    }

    public int getVisits() {
        return visits;
    }
}

