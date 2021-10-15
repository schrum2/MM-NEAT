package edu.southwestern.evolution.mutation.real;

import java.io.FileNotFoundException;
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
* Swaps two random segments in a genotype.
*
* @author Maxx Batterton
*/
public class SegmentSwapMutation extends RealMutation {
	
	protected final int segmentSize;
	protected final int segmentAmount;
	
	protected ArrayList<Double> storedSegment;
	
	public SegmentSwapMutation() {
		super("GANSegmentSwapMutationRate");
		this.segmentSize = GANProcess.evolvedSegmentLength(); //Parameters.parameters.integerParameter(""); // Maxx: temporarily set to 448 for mario length testing
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
		//this.segmentAmount = Parameters.parameters.integerParameter("marioGANLevelChunks"); // temp, change to new param for usability in other games
		this.storedSegment = new ArrayList<Double>(this.segmentSize);
	}
	
	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		//System.out.println("SegmentSwapMutation mutate triggered!");
		if (segmentAmount >= 2) {
			int[] randomValues = RandomNumbers.randomDistinct(2, segmentAmount);
			//System.out.println(Arrays.toString(randomValues));
			int randSegment1 = randomValues[0];
			int randSegment2 = randomValues[1];
			
			storedSegment.clear();
			// System.out.println("Swapping segments " + randSegment1 + " and " + randSegment2); // DEBUG
			
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
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:97 randomSeed:97 base:extendedmariolevelsdistinctnsdecorate log:ExtendedMarioLevelsDistinctNSDecorate-Direct2GANSwapOnly saveTo:Direct2GANSwapOnly marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.RealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 GANSegmentSwapMutationRate:1.0 GANSegmentCopyMutationRate:1.0 logMutationAndLineage:true".split(" "));		
	}
}
