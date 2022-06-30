package edu.southwestern.evolution.mutation.tweann;

import java.util.ArrayList;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * @author Alejandro Medina
 *
 */
public class ConvertMinecraftCPPNtoBlockVectorMutation extends ConvertCPPN2GANtoDirect2GANMutation {

	@Override
	protected double[] getLongVectorResultFromCPPN(Network cppn) {
		
		// assert that there will only be one output label for the block type and block orientation
		assert Parameters.parameters.booleanParameter("oneOutputLabelForBlockTypeCPPN");
		assert Parameters.parameters.booleanParameter("oneOutputLabelForBlockOrientationCPPN");
		
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		int numbersPerBlock = 1; // 1 is the lowest number of numbers corresponding to a block
		if(Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) numbersPerBlock++; // evolve orientation is true, number of corresponding numbers per block should be increased by 1
		if(Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) numbersPerBlock++; // presence is true, number of corresponding numbers per block should be increased by 1 
		
		int numBlocks = numbersPerBlock * (ranges.x() * ranges.y() * ranges.z()); // one or more numbers per block depending on command line parameters
		int numOrientations = MinecraftUtilClass.getnumOrientationDirections();
		
		double[] upper = ArrayUtil.doubleSpecified(numBlocks, 1.0); // upper bounds
		double[] lower = ArrayUtil.doubleSpecified(numBlocks, 0.0); // lower bounds
	
		double[] results = new double[numBlocks];
		//System.out.println("number of blocks " + numBlocks);
		boolean distanceInEachPlane = Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane");

		for (int xi = 0; xi < ranges.x(); xi++) {
			for (int yi = 0; yi < ranges.y(); yi++) {
				for (int zi = 0; zi < ranges.z(); zi++) {
					double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
					cppn.flush(); // There should not be any left over recurrent activation, but clear each time just in case
					double[] outputs = cppn.process(inputs);
					//System.out.println("length of outputs " + outputs.length);
					// two or three values per block
					if (Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) {
						final int PRESENCE_INDEX = counter;
						final int TYPE_INDEX = counter + 1;
						results[counter++] = outputs[PRESENCE_INDEX];
						//System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
						
						results[counter++] = outputs[TYPE_INDEX];
						//System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
						
						if (Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) { // three values per block
							final int ORIENTATION_INDEX = counter + 2;
							results[counter++] = outputs[ORIENTATION_INDEX];
							//System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
							
						}
					} else { // one or two values per block
						final int TYPE_INDEX = counter;
						results[counter++] = outputs[TYPE_INDEX];
						//System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
						
						if (Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) { // two values per block
							final int ORIENTATION_INDEX = counter + 1;
							results[counter++] = outputs[ORIENTATION_INDEX];
							//System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
							
						}
					}

				}
			}
		}	
		
		
		// TODO Auto-generated method stub
		return results;
	}

}
