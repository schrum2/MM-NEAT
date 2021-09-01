package edu.southwestern.evolution.mutation.real;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
* Segment Swap Mutation
* Swaps two random segments in a genotype.
*
* @author Maxx Batterton
*/
public class SegmentSwapMutation extends RealMutation {
	
	protected final int segmentSize;
	protected final int segmentAmount;
	protected final double rate;
	
	protected ArrayList<Double> storedSegment;
	
	public SegmentSwapMutation() {
		this.segmentSize = 448; //Parameters.parameters.integerParameter(""); // Maxx: temporarily set to 448 for mario length testing
		this.segmentAmount = Parameters.parameters.integerParameter("marioGANLevelChunks"); // temp, change to new param for usability in other games
		this.rate = 1.0; //Parameters.parameters.doubleParameter(""); // Maxx: temporarily set to 100% for testing
		
		}
	
	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		if (segmentAmount >= 2) {
			System.out.println(genotype.getPhenotype().toString()); // display genotype
			int randSegment1 = RandomNumbers.randomGenerator.nextInt(segmentAmount) + 1; // get first segment
			int randSegment2 = randSegment1;
			while (randSegment2 == randSegment1) {
				randSegment2 = RandomNumbers.randomGenerator.nextInt(segmentAmount) + 1; // get second segment different than first
			}
			storedSegment.clear();
			storedSegment.addAll(genotype.getPhenotype().subList(randSegment1*segmentSize, randSegment1*segmentSize+segmentSize));
			for (int i = 0; i < segmentSize; i++) {
				mutateIndex((RealValuedGenotype) genotype, randSegment2*segmentSize+i); // change second segment values to first segment
			}
			for (int i = 0; i < segmentSize; i++) {
				mutateIndex((RealValuedGenotype) genotype, randSegment1*segmentSize+i); // change first segment values to second segment
			}
			
			
		} else {
			throw new IllegalArgumentException("Cannot swap segments if there are fewer than 2!");
		}
		
		
	}
	
	@Override
	public void mutateIndex(RealValuedGenotype genotype, int i) {
		double val = genotype.getPhenotype().get(i); // get current value
		genotype.getPhenotype().set(i, storedSegment.get(i%segmentSize)); // change from stored value
		storedSegment.set(i%segmentSize, val); // put original in stored for second swap
	}

}
