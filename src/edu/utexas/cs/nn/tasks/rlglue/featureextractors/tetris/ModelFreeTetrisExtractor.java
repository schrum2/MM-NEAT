/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Jacob Schrum
 */
public class ModelFreeTetrisExtractor extends BertsekasTsitsiklisTetrisExtractor {

    @Override
    public int numFeatures() {
        return super.numFeatures() + 5;
    }

    @Override
    public double[] extract(Observation o) {
        double[] base = super.extract(o);

        int[] worldState = new int[worldWidth * worldHeight];
        System.arraycopy(o.intArray, 0, worldState, 0, worldWidth * worldHeight);
        double[] blockIndicator = new double[possibleBlocks.size()];
        for (int i = 0; i < possibleBlocks.size(); i++) {
            blockIndicator[i] = o.intArray[worldState.length + i];
        }

        int blockId = StatisticsUtilities.argmax(blockIndicator);
        int blockX = o.intArray[o.intArray.length - 5];
        int blockY = o.intArray[o.intArray.length - 4];
        int blockRotation = o.intArray[o.intArray.length - 3];
        blotMobilePiece(worldState, blockId, blockX, blockY, blockRotation);

        double[] added = new double[5];
        for (int i = 0; i < added.length; i++) {
            added[i] = pieceDistanceFromBlocks(worldState, blockId, blockX, blockY, blockRotation, i);
        }

        double[] combined = new double[base.length + added.length];
        System.arraycopy(base, 0, combined, 0, base.length);
        System.arraycopy(added, 0, combined, base.length, added.length);

        //System.out.println(Arrays.toString(combined));
        return combined;
    }

    /*
     * worldState used needs to be after the floating piece is blotted out
     */
    protected int pieceDistanceFromBlocks(int[] worldState, int blockId, int blockX, int blockY, int blockRotation, int pieceColumn) {
        int[][] mobilePiece = this.possibleBlocks.get(blockId).getShape(blockRotation);
        boolean hasBottom = false;
        int bottom = 0;
        for (int i = mobilePiece[pieceColumn].length - 1; i >= 0; i--) {
            if (mobilePiece[pieceColumn][i] != 0) {
                hasBottom = true;
                bottom = i;
                break;
            }
        }

        if (!hasBottom) {
            return this.worldHeight * 2;
        }

        int absoluteBottom = blockY + bottom;
        int h = columnHeight(blockX + pieceColumn, worldState);

        return (worldHeight - h) - absoluteBottom;
    }
}
