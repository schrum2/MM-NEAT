package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.GhostsWithinDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestFarthestEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPillBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity.ThreatGhostDirectionalProximityBlock;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class StaticPacManController extends NNDirectionalPacManController {

    private final boolean playWithoutPowerPills;
    private final boolean ignorePillScore;
    private final int futureSteps;

    private int lastMove = -1;
    private int eatenGhosts = 0;
    
    public StaticPacManController(){
        // default depth?
        this(2);
    }
    
    public StaticPacManController(int depth) {
        this(new TWEANN(1, 1, false, 1, 1, 1), depth);
    }

    public StaticPacManController(Network n, int depth) {
        super(n);
        futureSteps = depth;
        playWithoutPowerPills = Parameters.parameters.booleanParameter("noPowerPills");
        ignorePillScore = Parameters.parameters.booleanParameter("ignorePillScore");

        inputMediator = new BlockLoadedInputOutputMediator();
        ((BlockLoadedInputOutputMediator) inputMediator).blocks.add(new EscapeNodeDistanceDifferenceBlock(inputMediator.escapeNodes, false, false, futureSteps, !ignorePillScore, !playWithoutPowerPills, false));
        ((BlockLoadedInputOutputMediator) inputMediator).blocks.add(new NearestPillBlock());
        ((BlockLoadedInputOutputMediator) inputMediator).blocks.add(new NearestPowerPillBlock());
        ((BlockLoadedInputOutputMediator) inputMediator).blocks.add(new NearestFarthestEdibleGhostBlock(true));
        ((BlockLoadedInputOutputMediator) inputMediator).blocks.add(new GhostsWithinDistanceBlock(new boolean[]{false, false, false, true}, true, 80));
        ((BlockLoadedInputOutputMediator) inputMediator).blocks.add(new ThreatGhostDirectionalProximityBlock());
    }

    @Override
    public int getDirection(GameFacade gs) {
        if(gs.ghostReversal() || 
                gs.justAtePowerPill() || 
                eatenGhosts < gs.getNumEatenGhosts() ||
                gs.isJunction(gs.getPacmanCurrentNodeIndex())){
            //System.out.println("Allow reverse");
            lastMove = -1;
        } 
        eatenGhosts = gs.getNumEatenGhosts();
//        else {
//            System.out.println("No reverse");
//        }
        double[] dirPreferences = getDirectionPreferences(gs);
        if(lastMove != -1){
            //System.out.println("Disable: " +lastMove);
            dirPreferences[lastMove] = -Double.MAX_VALUE;
        }
        System.arraycopy(dirPreferences, 0, previousPreferences, 0, GameFacade.NUM_DIRS);
        //System.out.println(Arrays.toString(dirPreferences));
        int direction = directionFromPreferences(dirPreferences);
        //System.out.println("Direction: " + direction);
        lastMove = GameFacade.getReverse(direction);
        return direction;
    }    
    
    @Override
    public double[] getDirectionPreferences(GameFacade gf) {
        int lastDir = gf.getPacmanLastMoveMade();
        final int[] neighbors = gf.neighbors(gf.getPacmanCurrentNodeIndex());
        
        if(lastDir == -1) {
            //System.out.println("LastDir = -1");
            if(lastMove == -1) { // Never happens?
                System.out.println("LastMove = -1: FAIL");
                System.exit(1);
            }
            lastDir = lastMove;
        }
        final int referenceDir = CommonConstants.relativePacmanDirections ? lastDir : 0;
        double[] inputs = inputMediator.getInputs(gf, lastDir);

        String[] causes = new String[4];
        // Get the pills eaten info for the directions that are safe
        int timeLevelWidth = GameFacade.NUM_DIRS + (!ignorePillScore ? GameFacade.NUM_DIRS : 0) + (!playWithoutPowerPills ? GameFacade.NUM_DIRS : 0);
        int nearestPill = (futureSteps * timeLevelWidth) + GameFacade.NUM_DIRS;
        int deepestGroup = ((futureSteps - 1) * timeLevelWidth) + GameFacade.NUM_DIRS;
        int edibleGhost = inputs.length - (2 * GameFacade.NUM_DIRS) - 1;
        int allNear = inputs.length - GameFacade.NUM_DIRS - 1;
        double[] outputs = new double[4];
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            int dir = (referenceDir + i) % GameFacade.NUM_DIRS;
            if (neighbors[dir] == -1) {
                outputs[i] = -Double.MAX_VALUE;
//            }
//            if (inputs[i + nearestPill] == -1 && inputs[i + (2 * GameFacade.NUM_DIRS) + nearestPill] == -1) { // Wall
//                outputs[i] = -100;
                causes[i] = "Wall";
            } else if (!ignorePillScore && inputs[i] > 0 && inputs[i + 8] == 1.0 && gf.getActivePowerPillsIndices().length == 0 && !gf.anyIsEdible()) { // can get the last pill!
                outputs[i] = 100000;
                causes[i] = "Last Pill!";
            } else if (inputs[i + deepestGroup] > 0) { // Option 4 steps ahead that gets most pills
                if (gf.getActivePowerPillsIndices().length == 0 && !gf.anyIsEdible()) {
                    double preference = 1;
                    for (int j = 0; j < futureSteps; j++) {
                        preference += 100 * Math.pow(10, j) * inputs[i + deepestGroup + GameFacade.NUM_DIRS - (j * timeLevelWidth)]; // Pill value
                    }
                    preference += inputs[i + nearestPill] * 10; // Nearest Pill
                    preference += inputs[i + deepestGroup]; // Safest direction
                    outputs[i] = preference;
                    causes[i] = "Get Most Pills On Safe Path";
                } else if (!playWithoutPowerPills) {
                    double preference = 1;
                    String rewards = "";
                    double portionEaten = 0;
                    for (int j = 0; j < futureSteps; j++) {
                        double portion = inputs[i + deepestGroup + (timeLevelWidth * (1 - j)) - GameFacade.NUM_DIRS];
                        portionEaten += portion;
                        double amount = 100 * Math.pow(10, j) * portion; // Ghost value
                        rewards += amount + ",";
                        preference += amount;
                    }
                    boolean eatAll = portionEaten > 0.99 && portionEaten < 1.01; // allow wiggle room for floating point add
                    if (eatAll) {
                        preference += 1000;
                    }
                    causes[i] = "Get Most Edible Ghosts On Safe Path ";
                    if (!gf.anyIsEdible() && preference > 1 && !eatAll) { // No edible ghosts, but there will be, but not all eaten
                        causes[i] += "(Avoid Power Pill)";
                        preference = inputs[i + nearestPill] == 1 ? 0 : 0.1; // Nearest Edible Ghost
                    } else if (inputs[i + edibleGhost] > 0) {
                        causes[i] += "(Edible Bias " + rewards + ")";
                        preference += inputs[i + edibleGhost] * 1000; // Nearest Edible Ghost
//                    } else if (inputs[i + edibleGhost] > 0 && inputs[i + inputs.length - GameFacade.NUM_DIRS] == -1) {
//                        causes[i] += "(Safe Edible Bias " + rewards + ")";
//                        preference += inputs[i + edibleGhost] * 10; // Nearest Edible Ghost
                    } else if (!gf.anyActiveGhostInLair() && !gf.anyIsEdible() && (inputs[allNear] == 1 || RandomNumbers.randomGenerator.nextDouble() < 0.2 || eatAll)) {
                        causes[i] += "(Power Pill Bias " + rewards + ")";
                        preference += inputs[i + nearestPill] * 10; // Nearest Power Pill
                    } else {
                        causes[i] += "(No Bias " + rewards + ")";
                    }
                    preference += inputs[i + deepestGroup]; // Safest direction
                    outputs[i] = preference;
                }
            } else {
                boolean filled = false;
                for (int group = deepestGroup - timeLevelWidth, depth = futureSteps - 1; group > GameFacade.NUM_DIRS; group -= timeLevelWidth) {
                    if (inputs[i + group] > 0) {
                        outputs[i] = inputs[i + group];
                        causes[i] = "Safe Option " + depth + " Steps Ahead";
                        filled = true;
                        break;
                    }
                }

                if (!filled) {
                    if (inputs[i] > 0) { // Safe option 1 step ahead
                        outputs[i] = inputs[i] / 100.0;
                        causes[i] = "Safe Option One Step Ahead";
                    } else {
                        outputs[i] = inputs[i] / 100.0 - inputs[i + inputs.length - GameFacade.NUM_DIRS];
                        causes[i] = "Screwed";
                    }
                }
            }
        }

        double[] absoluteDirectionPreferences = new double[GameFacade.NUM_DIRS];
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            int dir = (referenceDir + i) % GameFacade.NUM_DIRS;
            if (neighbors[dir] == -1) {
                outputs[i] = -Double.MAX_VALUE;
            }
            absoluteDirectionPreferences[dir] = outputs[i];
        }

        //System.out.println(Arrays.toString(causes));
        
        return absoluteDirectionPreferences;
    }
}
