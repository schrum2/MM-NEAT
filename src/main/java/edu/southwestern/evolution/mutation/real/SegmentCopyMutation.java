package edu.southwestern.evolution.mutation.real;

import java.util.ArrayList;
import edu.southwestern.tasks.mario.gan.GANProcess;
import cern.colt.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
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
public class SegmentCopyMutation extends RealMutation {
	
	protected final int segmentSize;
	protected final int segmentAmount;
	protected final double rate;
	
	protected ArrayList<Double> storedSegment;
	
	public SegmentCopyMutation() {
		this.segmentSize = GANProcess.evolvedSegmentLength();
		switch(GANProcess.type) {
		case MARIO:
			this.segmentAmount = Parameters.parameters.integerParameter("marioGANLevelChunks");
			break;
		case ZELDA:
			throw new UnsupportedOperationException("figure this out later");
		case MEGA_MAN:
			this.segmentAmount = Parameters.parameters.integerParameter("megaManGANLevelChunks");
			break;
		case LODE_RUNNER:
			this.segmentAmount = 1;
			break;
		default:
			throw new UnsupportedOperationException("Pick a game");
		}
		this.rate = Parameters.parameters.doubleParameter("GANSegmentCopyMutationRate");
		this.storedSegment = new ArrayList<Double>(this.segmentSize);
		}
	
	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		if (segmentAmount >= 2) {
			int[] randomValues = RandomNumbers.randomDistinct(2, segmentAmount);
			//System.out.println(Arrays.toString(randomValues));
			int randSegment1 = randomValues[0];
			int randSegment2 = randomValues[1];
			
			storedSegment.clear();
			//System.out.println("Copying segment " + randSegment1 + " to segment " + randSegment2); // DEBUG
			
			storedSegment.addAll(genotype.getPhenotype().subList(randSegment1*segmentSize, randSegment1*segmentSize+segmentSize)); // Stores segment 1
			for (int i = 0; i < segmentSize; i++) {
				mutateIndex((RealValuedGenotype) genotype, randSegment2*segmentSize+i); // change second segment values to first segment
			}			
			
		} else {
			throw new IllegalArgumentException("Cannot swap segments if there are fewer than 2!");
		}
		
		
	}
	
	@Override
	public void mutateIndex(RealValuedGenotype genotype, int i) {
		genotype.getPhenotype().set(i, storedSegment.get(i%segmentSize)); // change from stored value
	}
}
