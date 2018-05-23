package examples.StarterNNPacMan.examples;

import examples.StarterNNPacMan.LocEvalPacMan;
import examples.StarterNNPacMan.NeuralNet;
import examples.StarterNNPacMan.NeuralPacMan;
import pacman.game.Game;

import java.util.ArrayList;

import static pacman.game.Constants.GHOST;

/**
 * Created by piers on 14/10/16.
 */
public class NNLocPacMan extends LocEvalPacMan {

    private int numberOfHiddenLayers = 1;
    private int neuronsPerHiddenLayers = 7;

    public NNLocPacMan(NeuralNet net) {
        super(net);
    }

    public NNLocPacMan(NeuralNet net, int numberOfHiddenLayers, int neuronsPerHiddenLayers) {
        super(net);
        this.numberOfHiddenLayers = numberOfHiddenLayers;
        this.neuronsPerHiddenLayers = neuronsPerHiddenLayers;
    }

    @Override
    public double evalLocation(Game game, int index) {
        int numGhosts = GHOST.values().length;
        double[] inputs = new double[numGhosts * 2 + 5];
        for (GHOST ghost : GHOST.values()) {
            int ghostLocation = game.getGhostCurrentNodeIndex(ghost);
            if (ghostLocation == -1) {
                inputs[ghost.ordinal()] = 100;
                inputs[ghost.ordinal() + numGhosts] = 100;
            } else {
                boolean edible = (game.getGhostEdibleTime(ghost) > 0);
                inputs[ghost.ordinal()] = (edible) ? 100 : game.getShortestPathDistance(index, ghostLocation);
                inputs[ghost.ordinal() + numGhosts] = (edible) ? game.getShortestPathDistance(index, ghostLocation) : 100;
            }
        }

        int i = numGhosts * 2;
        inputs[i++] = game.getNodeXCood(index);
        inputs[i++] = game.getNodeYCood(index);
        int[] pillIndices = game.getActivePillsIndices();

        // Find nearest pillIndex
        int bestIndex = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int pillIndex : pillIndices) {
            int distance = game.getShortestPathDistance(index, pillIndex);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = pillIndex;
            }
        }
        inputs[i++] = ((bestIndex == -1) ? 100 : bestDistance) + 1;

        // Find nearest powerPill
        bestIndex = -1;
        bestDistance = Integer.MAX_VALUE;
        for (int pillIndex : game.getActivePowerPillsIndices()) {
            int distance = game.getShortestPathDistance(index, pillIndex);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = pillIndex;
            }
        }
        inputs[i++] = ((bestIndex == -1) ? 100 : bestDistance) + 1;

        bestIndex = -1;
        bestDistance = Integer.MAX_VALUE;
        for (int junctionIndex : game.getJunctionIndices()) {
            int distance = game.getShortestPathDistance(index, junctionIndex);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = junctionIndex;
            }
        }
        inputs[i] = ((bestIndex == -1) ? 400 : bestDistance) + 1;

        ArrayList<Double> temp = new ArrayList<>();
        for(double input : inputs){
            temp.add(input);
        }
        ArrayList<Double> outputs = net.getOutputs(temp);
        return outputs.stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public int getInputLength() {
        return 13;
    }

    @Override
    public int getOutputLength() {
        return 1;
    }

    @Override
    public int getNumberOfHiddenLayers() {
        return numberOfHiddenLayers;
    }

    @Override
    public int getNeuronsPerHiddenLayer() {
        return neuronsPerHiddenLayers;
    }

    @Override
    public NeuralPacMan getPacManForTraining(NeuralNet net) {
        return new NNLocPacMan(net);
    }
}
