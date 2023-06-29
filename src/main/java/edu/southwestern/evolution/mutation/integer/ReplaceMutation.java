package edu.southwestern.evolution.mutation.integer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Replace integer with new random integer
 *
 * @author Jacob Schrum
 */
public class ReplaceMutation extends Mutation<ArrayList<Integer>> {

	protected final double rate;
	private int numVectorIndexMutations;

	public ReplaceMutation() {
		this.rate = Parameters.parameters.doubleParameter("intReplaceRate");
		this.numVectorIndexMutations = Parameters.parameters.integerParameter("numVectorIndexMutations");
	}

	/*
	 * Each index is checked to see if mutation should be performed
	 */
	@Override
	public boolean perform() {
		return RandomNumbers.randomGenerator.nextDouble() <= rate;
	}

	@Override
	public void mutate(Genotype<ArrayList<Integer>> genotype) {
		if(numVectorIndexMutations == -1) {
			// Each index has its own chance of mutation
			for (int i = 0; i < genotype.getPhenotype().size(); i++) {
				if (perform()) {
					mutateIndex((BoundedIntegerValuedGenotype) genotype, i);
				}
			}
		} else {
	        //System.out.println("BEFORE: "+genotype.getPhenotype());
			// Pick several different indices and mutate each one
			List<Integer> indices = IntStream.range(0, genotype.getPhenotype().size())
	                .boxed()
	                .collect(Collectors.toList());
	        Collections.shuffle(indices);
	        // shuffle the indices of the genotype: no repeats
	        
	        // mutate each index
	        for(int i = 0; i < numVectorIndexMutations; i++) {
	        	mutateIndex((BoundedIntegerValuedGenotype) genotype, indices.get(i));
	        }
	        
	        //System.out.println("AFTER : "+genotype.getPhenotype());
		}		
	}

	public void mutateIndex(BoundedIntegerValuedGenotype genotype, int i) {
		genotype.getPhenotype().set(i, RandomNumbers.randomGenerator.nextInt(MMNEAT.discreteCeilings[i]));
	}
}
