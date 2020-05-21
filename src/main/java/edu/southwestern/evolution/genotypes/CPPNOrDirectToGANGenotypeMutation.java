package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANVectorMatrixBuilder;
import edu.southwestern.util.random.RandomNumbers;

public class CPPNOrDirectToGANGenotypeMutation extends Mutation<ArrayList<Double>>{
	protected double rate;
	
	public CPPNOrDirectToGANGenotypeMutation(String rateName) {
		this(Parameters.parameters.doubleParameter(rateName));

	}
	public CPPNOrDirectToGANGenotypeMutation(double rate) {
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;	
		}
	@Override
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
	}

	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		Network cppn = (Network) genotype.getPhenotype();
		
		double[] inputMultipliers = new double[cppn.numInputs()];
		for(int i = 0;i<cppn.numInputs();i++) {
			inputMultipliers[i] = 1.0;
		}
		
		ZeldaCPPNtoGANVectorMatrixBuilder builder = new ZeldaCPPNtoGANVectorMatrixBuilder(cppn, inputMultipliers);
		int height = Parameters.parameters.integerParameter("cppn2ganHeight");
		int width = Parameters.parameters.integerParameter("cppn2ganWidth");
		int segmentLength = (GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables());
		double[] longResult = new double[segmentLength];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				double[] vector = builder.latentVectorAndMiscDataForPosition(width, height, x, y);
				int nextIndex = longResult.length*(y*height+x);
				System.arraycopy(vector, 0, longResult, nextIndex, vector.length);
				
			}
		}
	}

}
