package edu.southwestern.evolution.mutation.real;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
* Segment Swap Mutation
* Copies a segment over another segment in a genotype.
*
* @author Maxx Batterton
*/
public class SegmentCopyMutation extends SegmentMutation {
	
	protected final double rate;
	
	public SegmentCopyMutation() {
		super("GANSegmentCopyMutationRate");
		this.rate = Parameters.parameters.doubleParameter("GANSegmentCopyMutationRate");
	}
	
	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		//System.out.println("SegmentCopyMutation mutate triggered!");
		if (segmentAmount >= 2) {
			int[] randomValues = RandomNumbers.randomDistinct(2, segmentAmount);
			//System.out.println(Arrays.toString(randomValues));
			int randSegment1 = randomValues[0];
			int randSegment2 = randomValues[1];
			
			storedSegment.clear();
			//System.out.println("Copying segment " + randSegment1 + " to segment " + randSegment2); // DEBUG
			
			storedSegment.addAll(genotype.getPhenotype().subList(randSegment1*segmentSize, randSegment1*segmentSize+segmentSize)); // Stores segment 1
			for (int i = 0; i < segmentSize; i++) {
				if((i < auxVariableStartLocation || i > auxVariableEndLocation) || segmentSwapAuxiliaryVarialbes) // only swap outside of auxiliary variable range if feature flag is on
					mutateIndex((RealValuedGenotype) genotype, randSegment2*segmentSize+i); // change second segment values to first segment
			}			
			
		} else {
			throw new IllegalArgumentException("Cannot copy segments if there are fewer than 2!");
		}
		
		
	}
	
	@Override
	public void mutateIndex(RealValuedGenotype genotype, int i) {
		genotype.getPhenotype().set(i, storedSegment.get(i%segmentSize)); // change from stored value
	}
}
