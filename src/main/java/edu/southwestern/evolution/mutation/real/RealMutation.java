package edu.southwestern.evolution.mutation.real;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 */
public abstract class RealMutation extends Mutation<ArrayList<Double>> {

	protected final double rate;
	private int numVectorIndexMutations;

	public RealMutation() {
		this.rate = Parameters.parameters.doubleParameter("realMutateRate");
		this.numVectorIndexMutations = Parameters.parameters.integerParameter("numVectorIndexMutations");
	}

	protected RealMutation(String paramLabel) {
		this(Parameters.parameters.doubleParameter(paramLabel));
	}
	
	protected RealMutation(double rate) {
		this.rate = rate;
	}
	
	/*
	 * Each index is checked to see if mutation should be performed
	 */
	@Override
	public boolean perform() {
		return RandomNumbers.randomGenerator.nextDouble() <= rate;
	}

	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		if(numVectorIndexMutations == -1) {
			// Each index has its own chance of mutation
			for (int i = 0; i < genotype.getPhenotype().size(); i++) {
				if (perform()) {
					mutateIndex((RealValuedGenotype) genotype, i);
				}
			}
		} else {
			// Pick several different indices and mutate each one
			List<Integer> indices = IntStream.range(0, genotype.getPhenotype().size())
	                .boxed()
	                .collect(Collectors.toList());
	        Collections.shuffle(indices);
	        // shuffle the indices of the genotype: no repeats
	        
	        // mutate each index
	        for(int i = 0; i < numVectorIndexMutations; i++) {
	        	mutateIndex((RealValuedGenotype) genotype, indices.get(i));
	        }
		}
	}

	public abstract void mutateIndex(RealValuedGenotype genotype, int i);
}
