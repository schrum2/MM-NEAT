package examples.StarterNNPacMan.examples;


import examples.StarterNNPacMan.NeuralNet;
import examples.StarterNNPacMan.NeuralPacMan;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.Collections;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by piers on 12/10/16.
 */
public class NNPacMan extends NeuralPacMan {

    public NNPacMan(NeuralNet net) {
        super(net);
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {

        double[] inputs = new double[12];
        int[] ghostLocations = new int[]{
                game.getGhostCurrentNodeIndex(GHOST.INKY),
                game.getGhostCurrentNodeIndex(GHOST.BLINKY),
                game.getGhostCurrentNodeIndex(GHOST.PINKY),
                game.getGhostCurrentNodeIndex(GHOST.SUE)
        };


        int index = game.getPacmanCurrentNodeIndex();

        int x1, y1;
        x1 = game.getNodeXCood(index);
        y1 = game.getNodeYCood(index);
        for (int i = 0; i < ghostLocations.length; i++) {
            if (ghostLocations[i] == -1) continue;
            int x2, y2;
            x2 = game.getNodeXCood(ghostLocations[i]);
            y2 = game.getNodeYCood(ghostLocations[i]);
            if (x1 < x2) {
                //Right
                inputs[i]++;
            } else if (x1 > x2) {
                // Left
                inputs[i]++;
            } else if (y1 < y2) {
                // Up
                inputs[i]++;
            } else if (y1 > y2) {
                // Down
                inputs[i]++;
            }
        }

        // List of nodes that contain an active pill that we can see
        int[] pillIndices = game.getActivePillsIndices();

        for (int pillIndex : pillIndices) {
            int x2, y2;
            x2 = game.getNodeXCood(pillIndex);
            y2 = game.getNodeYCood(pillIndex);
            if (x1 < x2) {
                //Right
                inputs[4]++;
            } else if (x1 > x2) {
                // Left
                inputs[5]++;
            } else if (y1 < y2) {
                // Up
                inputs[6]++;
            } else if (y1 > y2) {
                // Down
                inputs[7]++;
            }
        }

        // Work out the walls
        for (MOVE move : game.getCurrentMaze().graph[index].neighbourhood.keySet()) {
            switch (move) {
                case RIGHT:
                    inputs[8]++;
                    break;
                case LEFT:
                    inputs[9]++;
                    break;
                case UP:
                    inputs[10]++;
                    break;
                case DOWN:
                    inputs[11]++;
                    break;
            }
        }

        ArrayList<Double> temp = new ArrayList<>();
        for (double input : inputs) {
            temp.add(input);
        }

        ArrayList<Double> outputs = net.getOutputs(temp);
        return MOVE.values()[outputs.indexOf(Collections.max(outputs))];
    }

    @Override
    public int getInputLength() {
        return 12;
    }

    @Override
    public int getOutputLength() {
        return 4;
    }

    @Override
    public int getNumberOfHiddenLayers() {
        return 2;
    }

    @Override
    public int getNeuronsPerHiddenLayer() {
        return 20;
    }

    @Override
    public NeuralPacMan getPacManForTraining(NeuralNet net) {
        return new NNPacMan(net);
    }

}
