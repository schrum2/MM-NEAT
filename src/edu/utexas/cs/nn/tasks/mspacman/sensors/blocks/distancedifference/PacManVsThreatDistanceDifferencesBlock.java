package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.facades.GhostControllerFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.awt.Color;
import java.util.Arrays;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public abstract class PacManVsThreatDistanceDifferencesBlock extends MsPacManSensorBlock {

    private final int absence;
    private final boolean includeGhostDistances;
    private final boolean includePacmanDistances;
    private final int simulationDepth;
    private final boolean futurePillsEaten;
    private final boolean futurePowerPillsEaten;
    private final boolean futureGhostsEaten;
    private final GhostControllerFacade ghostModel;

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            PacManVsThreatDistanceDifferencesBlock other = (PacManVsThreatDistanceDifferencesBlock) o;
            return this.includeGhostDistances == other.includeGhostDistances && this.includePacmanDistances == other.includePacmanDistances
                    && this.simulationDepth == other.simulationDepth && this.futureGhostsEaten == other.futureGhostsEaten && this.futurePillsEaten == other.futurePillsEaten
                    && this.futurePowerPillsEaten == other.futurePowerPillsEaten;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.includeGhostDistances ? 1 : 0);
        hash = 29 * hash + (this.includePacmanDistances ? 1 : 0);
        hash = 29 * hash + this.simulationDepth;
        hash = 29 * hash + (this.futurePillsEaten ? 1 : 0);
        hash = 29 * hash + (this.futurePowerPillsEaten ? 1 : 0);
        hash = 29 * hash + (this.futureGhostsEaten ? 1 : 0);
        hash = 29 * hash + super.hashCode();
        return hash;
    }

    public PacManVsThreatDistanceDifferencesBlock(boolean ghostDistances, boolean pacmanDistances, int simulationDepth, boolean futurePillsEaten, boolean futureGhostsEaten, boolean futurePowerPillsEaten) {
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
        this.includeGhostDistances = ghostDistances;
        this.includePacmanDistances = pacmanDistances;
        this.simulationDepth = simulationDepth;
        this.futurePillsEaten = futurePillsEaten;
        this.futurePowerPillsEaten = futurePowerPillsEaten;
        this.futureGhostsEaten = futureGhostsEaten;
        this.ghostModel = new GhostControllerFacade(new AggressiveGhosts());
    }

    public int incorporateSensors(final double[] inputs, int in, final GameFacade gf, final int currentDir) {
        assert currentDir <= 3 && currentDir >= 0 : "currentDir not a valid direction! " + currentDir;
        final int referenceDir = CommonConstants.relativePacmanDirections ? currentDir : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        double[] temp = new double[numberAdded()];
        addDifferencesInGhostPacManDistancesToLocation(temp, 0, neighbors, current, gf, gf.copy(), getTargets(gf), 0);
        //System.out.println("TEMP:" + Arrays.toString(temp));
        for (int i = 0; i < temp.length; i += GameFacade.NUM_DIRS) {
            for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
                int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
                //System.out.println(in + ":" + i + ":" + j + ":" + dir + ":" + (i + dir));
                inputs[in++] = temp[i + dir];
            }
        }
        return in;
    }

    public abstract int[] getTargets(GameFacade gf);

    /*
     * Modifies passed array of input senros values, returns next index in input
     * sensor list
     */
    public void addDifferencesInGhostPacManDistancesToLocation(double[] inputs, int in, int[] neighbors, final int current, GameFacade previous, GameFacade gs, int[] targets, int callDepth) {
        assert targets != null : "How can targets be null?";
        assert gs.allNodesInMaze(targets) : "Some members of targets are not in maze " + gs.getCurrentLevel();
        double[] pacManDistances = new double[GameFacade.NUM_DIRS];
        double[] ghostDistances = new double[GameFacade.NUM_DIRS];
        int[] directionalEscapeNodes = new int[GameFacade.NUM_DIRS];
        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            boolean wall = neighbors[j] == -1;
            if (wall || targets.length == 0) {
//                if (watch) {
//                    System.out.println((secondCall ? "Second call" : "--------------------\nFirst call") + " " + current + " skip");
//                }
                inputs[in++] = absence;
                pacManDistances[j] = absence;
                ghostDistances[j] = absence;
                directionalEscapeNodes[j] = -1;
            } else {
                Pair<Integer, int[]> pair = gs.getTargetInDir(current, targets, j);
                int closest = pair.t1;
                directionalEscapeNodes[j] = closest;
                int[] path = pair.t2;
//                if(watch && !secondCall){
//                    gs.addPoints(Util.colorFromInt(dir), path);
//                }
                Pair<Double, Double> dis = gs.closestThreatToPacmanPath(path, closest);
                double pacManDistance = dis.t1;
                double closestThreatDistance = dis.t2;

                double diff = closestThreatDistance - pacManDistance - (Constants.EAT_DISTANCE + 1);
                pacManDistances[j] = pacManDistance;
                ghostDistances[j] = closestThreatDistance;
                // Difference in Pac-Man/Threat Target Distance
                //inputs[in++] = ActivationFunctions.tanh(diff / GameFacade.MAX_DISTANCE);
                //inputs[in++] = Math.min(diff, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
                //inputs[in++] = Math.signum(diff);
                inputs[in++] = diff > 0 ? 1 : absence;
                if (CommonConstants.watch) {
//                    if (callDepth == 0) {
//                        Color c = diff > 0 ? Color.green : (diff > -2 ? Color.PINK : Color.red);
//                        previous.addPoints(c, path);
//                    }
                    //System.out.println((secondCall ? "Second call" : "--------------------\nFirst call") + " " + current + "->" + closest);
//                    if (closestThreatPath != null && !secondCall) {
//                        gs.addPoints(Color.blue, closestThreatPath);
//                    }
                    if (diff > 0) {
                        previous.addLines(Color.GREEN, current, closest);
                    }
//                    else if (closestThreatDistance == 0) {
//                        previous.addLines(Color.GRAY, current, closest);
//                    } else {
//                        previous.addLines(Color.RED, current, closest);
//                    }
                }
            }
        }
        if (includeGhostDistances) {
            for (int i = 0; i < ghostDistances.length; i++) {
                inputs[in++] = ghostDistances[i];
            }
        }

        if (includePacmanDistances) {
            for (int i = 0; i < pacManDistances.length; i++) {
                if (ghostDistances[i] == 0) {
                    //inputs[in++] = -1;
                    inputs[in++] = absence;
                } else {
                    inputs[in++] = pacManDistances[i];
                }
            }
        }

        if (callDepth < simulationDepth) {
            double[] recursiveResults = new double[((simulationDepth - callDepth - 1) * (GameFacade.NUM_DIRS + (futurePillsEaten ? GameFacade.NUM_DIRS : 0) + (futureGhostsEaten ? GameFacade.NUM_DIRS : 0) + (futurePowerPillsEaten ? GameFacade.NUM_DIRS : 0)))];
            // These values will be overwritten by recursive calls, but if no calls are made it means that no paths were safe,
            // in which case all recursive values should hold absence as well.
            Arrays.fill(recursiveResults, absence);
            //System.out.println(callDepth + ":Recursive size = " + recursiveResults.length);
            double[] pillsEaten = new double[GameFacade.NUM_DIRS];
            double[] powerPillsEaten = new double[GameFacade.NUM_DIRS];
            double[] ghostsEaten = new double[GameFacade.NUM_DIRS];
            for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                if (directionalEscapeNodes[i] == -1
                        || gs.neighborInDir(current, i) == -1
                        || (ghostDistances[i] - pacManDistances[i] - (Constants.EAT_DISTANCE + 1)) <= 0) {
                    inputs[in++] = absence;
                    pillsEaten[i] = absence;
                    powerPillsEaten[i] = absence;
                    ghostsEaten[i] = absence;
                } else {
                    int startLevel = gs.getCurrentLevel();
                    int startLives = gs.getPacmanNumberOfLivesRemaining();
                    int startPills = gs.getNumActivePills();
                    int startPowerPills = gs.getNumActivePowerPills();
                    //double startGhostReward = gs.getGhostReward();
                    int startGhostsEaten = gs.getNumEatenGhosts();
                    // Simulate forward to next escape node, assuming ghosts chase
                    GameFacade copy = gs.simulateToNextTarget(i, ghostModel, directionalEscapeNodes[i]);

                    if (copy == null) {
                        // Copied this code ... seems wrong
                        // Go here if Pacman dies, bad sensor values
                        inputs[in++] = absence;
                        pillsEaten[i] = absence;
                        powerPillsEaten[i] = absence;
                        ghostsEaten[i] = absence;
                    } else {
                        int endPills = (copy.getCurrentLevel() == startLevel) ? copy.getNumActivePills() : 0;
                        int endPowerPills = (copy.getCurrentLevel() == startLevel) ? copy.getNumActivePowerPills() : 0;

                        //double endGhostReward = copy.getGhostReward();
                        int endGhostsEaten = copy.getNumEatenGhosts();
                        //ghostsEaten[i] = (endGhostReward - startGhostReward) / (Math.pow(2, copy.getNumActiveGhosts()) - 1);
                        ghostsEaten[i] = (endGhostsEaten - startGhostsEaten) / (1.0 * copy.getNumActiveGhosts());

                        // Since safe routes are followed, game over via death should be impossible
                        if ((copy.getCurrentLevel() > startLevel || copy.gameOver())
                                && startLives <= copy.getPacmanNumberOfLivesRemaining()) {
                            // Beat the level, so all good
                            inputs[in++] = 1.0;
                            pillsEaten[i] = 1.0;
                            powerPillsEaten[i] = startPowerPills / 4.0; // 4 is initial number of power pills
                            // Don't fill future power pill slots with 1 automatically
                            int until = recursiveResults.length - (futurePowerPillsEaten ? GameFacade.NUM_DIRS : 0);
                            for (int k = 0; k < until; k += GameFacade.NUM_DIRS) {
                                recursiveResults[k + i] = 1.0;
                            }
                            if (futurePowerPillsEaten && recursiveResults.length >= GameFacade.NUM_DIRS) {
                                recursiveResults[recursiveResults.length - GameFacade.NUM_DIRS + i] = powerPillsEaten[i];
                            }
                            if (CommonConstants.watch) {
                                gs.addLines(Color.pink, directionalEscapeNodes[i], copy.getPacmanCurrentNodeIndex());
                            }
                        } else {
                            int simCurrent = copy.getPacmanCurrentNodeIndex();
                            int[] simNeighbors = copy.neighbors(simCurrent);
                            //System.out.println("Previous:"+directionalEscapeNodes[i] +", sim current:" + simCurrent);
                            double[] immediateRecursiveResults = new double[GameFacade.NUM_DIRS + recursiveResults.length];
                            addDifferencesInGhostPacManDistancesToLocation(immediateRecursiveResults, 0, simNeighbors, simCurrent, previous, copy, getTargets(copy), callDepth + 1);
                            double[] immediateReach = new double[GameFacade.NUM_DIRS];
                            System.arraycopy(immediateRecursiveResults, 0, immediateReach, 0, GameFacade.NUM_DIRS);
                            inputs[in++] = StatisticsUtilities.maximum(immediateReach);
                            pillsEaten[i] = (startPills - endPills) / (1.0 * startPills);
                            powerPillsEaten[i] = (startPowerPills - endPowerPills) / 4.0; // 4 is original number of power pills

                            if (immediateRecursiveResults.length > GameFacade.NUM_DIRS) {
                                for (int k = GameFacade.NUM_DIRS; k < immediateRecursiveResults.length; k += GameFacade.NUM_DIRS) {
                                    double[] recursiveGroup = new double[GameFacade.NUM_DIRS];
                                    System.arraycopy(immediateRecursiveResults, k, recursiveGroup, 0, GameFacade.NUM_DIRS);
                                    recursiveResults[(k - GameFacade.NUM_DIRS) + i] = StatisticsUtilities.maximum(recursiveGroup);
                                }
                            }
                        }
                    }
                }
            }

            if (futurePillsEaten) {
                for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                    inputs[in++] = pillsEaten[i];
                }
            }

            if (futureGhostsEaten) {
                for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                    inputs[in++] = ghostsEaten[i];
                }
            }

            if (futurePowerPillsEaten) {
                for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                    inputs[in++] = powerPillsEaten[i];
                }
            }

            if (recursiveResults.length > 0) {
                System.arraycopy(recursiveResults, 0, inputs, in, recursiveResults.length);
                in += recursiveResults.length;
            }
        }
    }

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = "Diff PM/Threat " + typeOfTarget() + " Dis " + first;
        labels[in++] = "Diff PM/Threat " + typeOfTarget() + " Dis Right";
        labels[in++] = "Diff PM/Threat " + typeOfTarget() + " Dis " + last;
        labels[in++] = "Diff PM/Threat " + typeOfTarget() + " Dis Left";

        if (simulationDepth == 0 && includeGhostDistances) {
            labels[in++] = "Threat " + typeOfTarget() + " Dis " + first;
            labels[in++] = "Threat " + typeOfTarget() + " Dis Right";
            labels[in++] = "Threat " + typeOfTarget() + " Dis " + last;
            labels[in++] = "Threat " + typeOfTarget() + " Dis Left";
        }

        if (simulationDepth == 0 && includePacmanDistances) {
            labels[in++] = "Pacman " + typeOfTarget() + " Dis " + first;
            labels[in++] = "Pacman " + typeOfTarget() + " Dis Right";
            labels[in++] = "Pacman " + typeOfTarget() + " Dis " + last;
            labels[in++] = "Pacman " + typeOfTarget() + " Dis Left";
        }

        for (int i = 1; i <= simulationDepth; i++) {
            labels[in++] = "Best D" + i + " " + first + " Diff PM/Threat " + typeOfTarget() + " Dis";
            labels[in++] = "Best D" + i + " Right Diff PM/Threat " + typeOfTarget() + " Dis";
            labels[in++] = "Best D" + i + " " + last + " Diff PM/Threat " + typeOfTarget() + " Dis";
            labels[in++] = "Best D" + i + " Left Diff PM/Threat " + typeOfTarget() + " Dis";

            if (futurePillsEaten) {
                labels[in++] = "D" + i + " " + first + " To " + typeOfTarget() + " Pills Eaten";
                labels[in++] = "D" + i + " Right To " + typeOfTarget() + " Pills Eaten";
                labels[in++] = "D" + i + " " + last + " To " + typeOfTarget() + " Pills Eaten";
                labels[in++] = "D" + i + " Left To " + typeOfTarget() + " Pills Eaten";
            }

            if (futureGhostsEaten) {
                labels[in++] = "D" + i + " " + first + " To " + typeOfTarget() + " Ghosts Eaten";
                labels[in++] = "D" + i + " Right To " + typeOfTarget() + " Ghosts Eaten";
                labels[in++] = "D" + i + " " + last + " To " + typeOfTarget() + " Ghosts Eaten";
                labels[in++] = "D" + i + " Left To " + typeOfTarget() + " Ghosts Eaten";
            }

            if (futurePowerPillsEaten) {
                labels[in++] = "D" + i + " " + first + " To " + typeOfTarget() + " Power Pills Eaten";
                labels[in++] = "D" + i + " Right To " + typeOfTarget() + " Power Pills Eaten";
                labels[in++] = "D" + i + " " + last + " To " + typeOfTarget() + " Power Pills Eaten";
                labels[in++] = "D" + i + " Left To " + typeOfTarget() + " Power Pills Eaten";
            }
        }

        return in;
    }

    public abstract String typeOfTarget();

    public int numberAdded() {
        return GameFacade.NUM_DIRS
                + (simulationDepth == 0 && includeGhostDistances ? GameFacade.NUM_DIRS : 0)
                + (simulationDepth == 0 && includePacmanDistances ? GameFacade.NUM_DIRS : 0)
                + (simulationDepth * (GameFacade.NUM_DIRS + (futurePillsEaten ? GameFacade.NUM_DIRS : 0) + (futureGhostsEaten ? GameFacade.NUM_DIRS : 0) + (futurePowerPillsEaten ? GameFacade.NUM_DIRS : 0)));
    }
}
