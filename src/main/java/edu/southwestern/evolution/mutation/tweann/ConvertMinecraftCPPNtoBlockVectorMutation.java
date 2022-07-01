package edu.southwestern.evolution.mutation.tweann;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype;
import edu.southwestern.evolution.genotypes.EitherOrGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.graphics.ThreeDimensionalUtil;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Convert CPPN to Block Vector in Minecraft.
 * Cannot specific type of phenotype since it changes.
 * 
 * @author Alejandro Medina
 *
 */
@SuppressWarnings("rawtypes")
public class ConvertMinecraftCPPNtoBlockVectorMutation extends Mutation {

	protected double rate;

	public ConvertMinecraftCPPNtoBlockVectorMutation() {
		super();
		double rate = Parameters.parameters.doubleParameter("indirectToDirectTransitionRate");
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;
	}

	protected double[] getLongVectorResultFromCPPN(Network cppn) {

		// assert that there will only be one output label for the block type and block orientation
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
		// System.out.println("number of blocks in shape: " + numBlocks);
		
		double[] results = new double[numBlocks];
		//System.out.println("number of blocks " + numBlocks);
		boolean distanceInEachPlane = Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane");

	
		//for(int i = 0; i < outputs.length; i++) System.out.println(outputs[i]);
		
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					
					
					double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
					// System.out.println(Arrays.toString(inputs));
					cppn.flush(); // There should not be any left over recurrent activation, but clear each time just in case
					double[] outputs = cppn.process(inputs);
					// System.out.println(Arrays.toString(outputs));			
					
					// The CPPN can create negative or other out of bounds values. Need to bound to
					// appropriate ranges.
					for(int i = 0; i < outputs.length; i++) {
						// halfSawtooth: binds to range [0,1) in a cyclic fashion
						results[counter++] = ActivationFunctions.halfSawtooth(outputs[i]);
					}
					
					
				}
			}
		}
		
		// System.out.println("COUNTER: "+ counter);
		// System.out.println("results: " + Arrays.toString(results));
		return results;
		
		
		// From my first attempt, but I do not think this is needed.
	/*	
		for (int xi = 0; xi < ranges.x(); xi++) {
			for (int yi = 0; yi < ranges.y(); yi++) {
				for (int zi = 0; zi < ranges.z(); zi++) {
					double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
					cppn.flush(); // There should not be any left over recurrent activation, but clear each time just in case
					double[] outputs = cppn.process(inputs);
				
					for(int i = 0; i < outputs.length; i++) System.out.println(outputs[i]);
					
						
					System.out.println("length of outputs " + outputs.length);
					// two or three values per block
					if (Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) {
						final int PRESENCE_INDEX = counter;
						final int TYPE_INDEX = counter + 1;
						results[counter++] = outputs[PRESENCE_INDEX];
						System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
						
						results[counter++] = outputs[TYPE_INDEX];
						System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
						
						if (Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) { // three values per block
							final int ORIENTATION_INDEX = counter + 2;
							results[counter++] = outputs[ORIENTATION_INDEX];
							System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
							
						}
					} else { // one or two values per block
						final int TYPE_INDEX = counter;
						results[counter++] = outputs[TYPE_INDEX];
						System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
						
						if (Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) { // two values per block
							final int ORIENTATION_INDEX = counter + 1;
							results[counter++] = outputs[ORIENTATION_INDEX];
							System.out.println("new val in results "+results[counter] + " and counter value: " + counter);
						}
					}*/
			//	}
		///	}
		//}
		
	}

	@Override
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void mutate(Genotype genotype) {
		// Cannot do a transition mutation on a genotype that has already transitioned!
		if(!((CPPNOrBlockVectorGenotype) genotype).getFirstForm()) return;
		// Safe to assume phenotype is a network at this point
		Network cppn = (Network) genotype.getPhenotype();
		Genotype cppnOrBlockVectorGenotype = (CPPNOrBlockVectorGenotype) genotype;
		double[] longResult = getLongVectorResultFromCPPN(cppn); //Helper method call

		RealValuedGenotype k = new RealValuedGenotype(longResult);
		((EitherOrGenotype<TWEANN, ArrayList<Double>>) cppnOrBlockVectorGenotype).switchForms(k);
	}
	
}
