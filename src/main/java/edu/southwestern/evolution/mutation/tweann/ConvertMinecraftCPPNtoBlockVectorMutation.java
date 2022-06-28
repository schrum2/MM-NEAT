package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.graphics.ThreeDimensionalUtil;

/**
 * Convert CPPN to Block Vector in Minecraft.
 * Cannot specific type of phenotype since it changes.
 * 
 * @author Alejandro Medina
 *
 */
public class ConvertMinecraftCPPNtoBlockVectorMutation extends ConvertCPPN2GANtoDirect2GANMutation {

	public ConvertMinecraftCPPNtoBlockVectorMutation() {
		super();
	}

	@Override
	protected double[] getLongVectorResultFromCPPN(Network cppn) {

		// assert that there will only be one output label for the block type and block
		// orientation
		assert Parameters.parameters.booleanParameter("oneOutputLabelForBlockTypeCPPN");
		assert Parameters.parameters.booleanParameter("oneOutputLabelForBlockOrientationCPPN");

		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		int numbersPerBlock = 1; // 1 is the lowest number of numbers corresponding to a block
		if (Parameters.parameters.booleanParameter("minecraftEvolveOrientation"))
			numbersPerBlock++; // evolve orientation is true, number of corresponding numbers per block should
								// be increased by 1
		if (Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock"))
			numbersPerBlock++; // presence is true, number of corresponding numbers per block should be
								// increased by 1
		int numBlocks = numbersPerBlock * (ranges.x() * ranges.y() * ranges.z());
		int counter = 0;

		double[] results = new double[numBlocks];

		boolean distanceInEachPlane = Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane");

		for (int xi = 0; xi < ranges.x(); xi++) {
			for (int yi = 0; yi < ranges.y(); yi++) {
				for (int zi = 0; zi < ranges.z(); zi++) {
					double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
					cppn.flush(); // There should not be any left over recurrent activation, but clear each time just in case
					double[] outputs = cppn.process(inputs);
					
					// two or three values per block
					if (Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) {
						final int PRESENCE_INDEX = 0;
						final int TYPE_INDEX = 1;
						results[counter++] = outputs[PRESENCE_INDEX];
						results[counter++] = outputs[TYPE_INDEX];

						if (Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) { // three values per block
							final int ORIENTATION_INDEX = 2;
							results[counter++] = outputs[ORIENTATION_INDEX];
						}
					} else { // one or two values per block
						final int TYPE_INDEX = 0;
						results[counter++] = outputs[TYPE_INDEX];
						
						if (Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) { // two values per block
							final int ORIENTATION_INDEX = 1;
							results[counter++] = outputs[ORIENTATION_INDEX];
						}
					}
				}
			}
		}
		return results;
	}

}
