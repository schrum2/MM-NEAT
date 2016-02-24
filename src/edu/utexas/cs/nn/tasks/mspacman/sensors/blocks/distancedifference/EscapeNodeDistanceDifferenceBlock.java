package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.data.NodeCollection;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class EscapeNodeDistanceDifferenceBlock extends PacManVsThreatDistanceDifferencesBlock {

    private final NodeCollection escapeNodes;

    public EscapeNodeDistanceDifferenceBlock(NodeCollection en) {
        this(en, false, false);
    }

    public EscapeNodeDistanceDifferenceBlock(NodeCollection en, boolean ghostDistances, boolean pacmanDistances) {
        this(en, ghostDistances, pacmanDistances, Parameters.parameters.integerParameter("escapeNodeDepth"), !Parameters.parameters.booleanParameter("ignorePillScore"), !Parameters.parameters.booleanParameter("noPowerPills"), !Parameters.parameters.booleanParameter("noPowerPills"));
    }

    public EscapeNodeDistanceDifferenceBlock(NodeCollection en, boolean ghostDistances, boolean pacmanDistances, int simulationDepth, boolean futurePillsEaten, boolean futureGhostsEaten, boolean futurePowerPillsEaten) {
        super(ghostDistances, pacmanDistances, simulationDepth, futurePillsEaten, futureGhostsEaten, futurePowerPillsEaten);
        escapeNodes = en;
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        escapeNodes.updateNodes(gf, gf.getPacmanCurrentNodeIndex());
        int[] result = escapeNodes.getNodes();
        assert gf.allNodesInMaze(result) : "Some members of result are not in maze. Current: " + gf.getCurrentLevel() + ":targets:" + Arrays.toString(result) + ":junctions:" + Arrays.toString(gf.getJunctionIndices()) + ":power pills:" + Arrays.toString(gf.getActivePowerPillsIndices());
        return result;
    }

    @Override
    public String typeOfTarget() {
        return "Escape Node";
    }

    /**
     * Was used to track down the bug of the junctions returned by JunctionNodes
     * not matching the current maze
     *
     * @param actualJunctions Taken directly from the GameFacade
     * @param processedJunctions Returned by the NodeCollection
     * @return
     */
//    public static boolean tempJunctionArrayComparison(int[] actualJunctions, int[] processedJunctions) {
//        int overlap = 0;
//        for (int i = 0; i < actualJunctions.length; i++) {
//            int occurrences = ArrayUtil.countOccurrences(actualJunctions[i], processedJunctions);
//            if (occurrences > 2) { // Two allowed because of adding back last visited
//                System.out.println("WTF! " + occurrences + " of " + actualJunctions[i]);
//                System.out.println("in " + Arrays.toString(processedJunctions));
//                System.out.println("actualJunctions: " + Arrays.toString(actualJunctions));
//                return false;
//            } else if (occurrences == 1 || occurrences == 2) { // Two allowed because of adding back last visited
//                overlap++;
//            }
//        }
//        int extra = actualJunctions.length - overlap;
//        if (extra != 0 && extra != 1) {
//            System.out.println("--------------------------------------");
//            System.out.println("actualJunctions.length:"+actualJunctions.length+":overlap:"+overlap);
//            System.out.println("Why so little overlap? " + extra);
//            System.out.println("in " + Arrays.toString(processedJunctions));
//            System.out.println("actualJunctions: " + Arrays.toString(actualJunctions));
//            return false;
//        }
//        return true;
//    }
    @Override
    public void addDifferencesInGhostPacManDistancesToLocation(double[] inputs, int in, int[] neighbors, final int current, GameFacade previous, GameFacade gs, int[] targets, int callDepth) {
        assert previous.allNodesInMaze(targets) : "Some members of targets are not in maze " + previous.getCurrentLevel();
//        int mazeBefore = gs.getMazeIndex();
//        int[] originalTargets = Arrays.copyOf(targets, targets.length);
        // Each second call is in a different world view, so recalculate
        if (callDepth > 0) {
            //System.out.println("Secondary update from " + current);
            NodeCollection copy = this.escapeNodes.copy();
            copy.updateNodes(gs, current, false);
            if (previous.getCurrentLevel() != gs.getCurrentLevel()) {
                targets = new int[0];
            } else {
                targets = copy.getNodes();
                assert previous.allNodesInMaze(targets) : "Nodes update made some targets not in maze " + previous.getCurrentLevel();
            }
        }
//        if(!tempJunctionArrayComparison(gs.getJunctionIndices(), targets)){
//            System.out.println("--------------------------------------");
//            System.out.println("EscapeNodeDistanceDifferenceBlock.addDifferencesInGhostPacManDistancesToLocation");
//            System.out.println("mazeBefore:"+mazeBefore+":gs.getMazeIndex():" + gs.getMazeIndex()+":previous.getMazeIndex():" + previous.getMazeIndex());
//            System.out.println("callDepth:" + callDepth);
//            System.out.println("originalTargets:" + Arrays.toString(originalTargets));
//            System.out.println("targets:" + Arrays.toString(targets));
//            System.exit(1);
//        }
        super.addDifferencesInGhostPacManDistancesToLocation(inputs, in, neighbors, current, previous, gs, targets, callDepth);
    }
}
