package examples.StarterNNPacMan.examples;

import examples.StarterNNPacMan.LocEvalPacMan;
import examples.StarterNNPacMan.NeuralNet;
import examples.StarterNNPacMan.NeuralPacMan;
import pacman.game.Game;

import java.util.ArrayList;

import static pacman.game.Constants.GHOST;

/**
 * Created by piers on 18/10/16.
 */
public class SimpleNNLocPacMan extends LocEvalPacMan {

    public SimpleNNLocPacMan(NeuralNet net) {
        super(net);
    }


    @Override
    public double evalLocation(Game game, int index) {
        double[] inputs = new double[4];

        // For each ghost, calculate the shortest distance to it
        int shortestDistance = Integer.MAX_VALUE;
        int shortestEdibleDistance = Integer.MAX_VALUE;
        for (GHOST ghost : GHOST.values()) {
            int ghostLocation = game.getGhostCurrentNodeIndex(ghost);
            if (ghostLocation != -1) {
                int distance = game.getShortestPathDistance(index, ghostLocation);
                if (game.getGhostEdibleTime(ghost) > 0) {
                    if (distance < shortestEdibleDistance) {
                        shortestEdibleDistance = distance;
                    }
                } else {
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                    }
                }
            }
        }

        inputs[0] = shortestDistance;
        inputs[1] = shortestEdibleDistance;

        int[] pillIndices = game.getActivePillsIndices();
        int bestDistance = Integer.MAX_VALUE;
        for (int pillIndex : pillIndices) {
            int distance = game.getShortestPathDistance(index, pillIndex);
            if (distance < bestDistance) {
                bestDistance = distance;
            }
        }
        inputs[2] = bestDistance;

        bestDistance = Integer.MAX_VALUE;
        for (int pillIndex : game.getActivePowerPillsIndices()) {
            int distance = game.getShortestPathDistance(index, pillIndex);
            if (distance < bestDistance) {
                bestDistance = distance;
            }
        }
        inputs[3] = bestDistance;
        ArrayList<Double> temp = new ArrayList<>();
        for (double input : inputs) {
            temp.add(input);
        }
        ArrayList<Double> outputs = net.getOutputs(temp);
        return outputs.stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public int getInputLength() {
        return 4;
    }

    @Override
    public int getOutputLength() {
        return 1;
    }

    @Override
    public int getNumberOfHiddenLayers() {
        return 4;
    }

    @Override
    public int getNeuronsPerHiddenLayer() {
        return 20;
    }

    @Override
    public NeuralPacMan getPacManForTraining(NeuralNet net) {
        return new SimpleNNLocPacMan(net);
    }

}
