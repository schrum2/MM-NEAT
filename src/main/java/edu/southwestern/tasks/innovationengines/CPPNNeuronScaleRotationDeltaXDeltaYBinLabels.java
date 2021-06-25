package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;

public class CPPNNeuronScaleRotationDeltaXDeltaYBinLabels extends BaseBinLabels{
	List<String> labels = null;
	
	public static final int BIN_INDEX_NEURONS = 0;
	public static final int BIN_INDEX_SCALE = 1;
	public static final int BIN_INDEX_ROTATION = 2;
	public static final int BIN_INDEX_DELTA_X = 3;
	public static final int BIN_INDEX_DELTA_Y = 4;

	static final double MAX_ROTATION = 2*Math.PI; // 360;

	
	/**
	 * Creates the bin labels using the number of neurons
	 * number of scale bins, number of rotation bins, and 
	 * number of translation bins for both deltaX and deltaY.
	 * 
	 * @return List of bin labels as strings
	 */
	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = ((Parameters.parameters.integerParameter("maxNumNeurons") - CPPNNeuronCountBinLabels.MIN_NUM_NEURONS + 1) * (Parameters.parameters.integerParameter("numScaleIntervals")
					* Parameters.parameters.integerParameter("numRotationIntervals") * Parameters.parameters.integerParameter("numTranslationIntervals") * Parameters.parameters.integerParameter("numTranslationIntervals")));
			System.out.println("Archive Size: "+size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = CPPNNeuronCountBinLabels.MIN_NUM_NEURONS; i <= Parameters.parameters.integerParameter("maxNumNeurons"); i++) {
				for(int j = 0; j < Parameters.parameters.integerParameter("numScaleIntervals"); j++) {
					for(int k = 0; k < Parameters.parameters.integerParameter("numRotationIntervals"); k++) {
						for(int m = 0; m < Parameters.parameters.integerParameter("numTranslationIntervals"); m++) {
							for(int n = 0; n < Parameters.parameters.integerParameter("numTranslationIntervals"); n++){
								labels.add("Neurons" + i + "-scale" + j + "-rotation" + k + "-deltaX" + m + "-deltaY" + n);
								count++;
							}
						}
					}
				}
			}
			assert count == size : "Incorrect number of bins created in archive: " + count + " size: " + size;
			
		}
		return labels;
	}

	/**
	 * Calculating the index of the correct bin label
	 * in an int array multi.  
	 * 
	 * @param multi An array containing just one value, the number of neurons
	 * @return The index in multi containing the correct bin
	 */
	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (((multi[BIN_INDEX_NEURONS] - CPPNNeuronCountBinLabels.MIN_NUM_NEURONS) * (Parameters.parameters.integerParameter("numScaleIntervals") * Parameters.parameters.integerParameter("numRotationIntervals") * Parameters.parameters.integerParameter("numTranslationIntervals") * Parameters.parameters.integerParameter("numTranslationIntervals")) + 
						(multi[BIN_INDEX_SCALE] * Parameters.parameters.integerParameter("numRotationIntervals") * Parameters.parameters.integerParameter("numTranslationIntervals") * Parameters.parameters.integerParameter("numTranslationIntervals") +
						(multi[BIN_INDEX_ROTATION] * Parameters.parameters.integerParameter("numTranslationIntervals") * Parameters.parameters.integerParameter("numTranslationIntervals")) + 
						(multi[BIN_INDEX_DELTA_X] * Parameters.parameters.integerParameter("numTranslationIntervals") + 
						(multi[BIN_INDEX_DELTA_Y])))));
		assert binIndex >= 0 : "Negative index "+Arrays.toString(multi) + " -> " + binIndex;
		return binIndex;
	}
	
	/**
	 * Gets the names of each bin dimension
	 */
	@Override
	public String[] dimensions() {
		return new String[] {"Neurons, Scale, Rotation, DeltaX, DeltaY"};
	}
	
	/**
	 * Gets the size of each archive dimension
	 */
	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("maxNumNeurons") - CPPNNeuronCountBinLabels.MIN_NUM_NEURONS + 1};
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int nodes = (int) keys.get("Nodes");
		double scale = (double) keys.get("Scale");
		double rotation = (double) keys.get("Rotation");
		double deltaX = (double) keys.get("Horizontal Shift");
		double deltaY = (double) keys.get("Vertical Shift");
		
		double imageTranslationRange = Parameters.parameters.doubleParameter("imageCenterTranslationRange");		
		
		double scaledScale =  (scale / Parameters.parameters.doubleParameter("maxScale"));
		double scaledRotation = (rotation / MAX_ROTATION);
		double scaledDeltaX = (deltaX + imageTranslationRange) / 2*imageTranslationRange; // Multiply range by 2 to account for extreme negative and extreme positive
		double scaledDeltaY = (deltaY + imageTranslationRange) / 2*imageTranslationRange; // Multiply range by 2 to account for extreme negative and extreme positive;		
		
		int nodesIndex = Math.min(nodes, Parameters.parameters.integerParameter("maxNumNeurons"));
		int scaleIndex = (int) Math.min(scaledScale * Parameters.parameters.integerParameter("numScaleIntervals"), Parameters.parameters.integerParameter("numScaleIntervals") - 1);
		int rotationIndex = (int) Math.min(scaledRotation * Parameters.parameters.integerParameter("numRotationIntervals"), Parameters.parameters.integerParameter("numRotationIntervals") - 1);
		int deltaXIndex = (int) Math.min(Math.max(0, scaledDeltaX * Parameters.parameters.integerParameter("numTranslationIntervals")), Parameters.parameters.integerParameter("numTranslationIntervals") - 1);
		int deltaYIndex = (int) Math.min(Math.max(0, scaledDeltaY * Parameters.parameters.integerParameter("numTranslationIntervals")), Parameters.parameters.integerParameter("numTranslationIntervals") - 1);
		
		return new int[] {nodesIndex, scaleIndex, rotationIndex, deltaXIndex, deltaYIndex};
	}
}
