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

		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					// TODO: loop through like I would for VectorToVolume, and place values in the array being returned.
				}
			}
		}	
		
		
		// TODO Auto-generated method stub
		return results;
	}

}
