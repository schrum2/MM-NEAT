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
		this.rate = Parameters.parameters.doubleParameter("GANSegmentSwapMutationRate"); //Parameters.parameters.doubleParameter(""); // Maxx: temporarily set to 100% for testing
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
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { //default for mega man
				"runNumber:0", "randomSeed:0", 
				"io:false", "netio:false",
				"trials:1", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
				"task:edu.southwestern.tasks.megaman.MegaManGANLevelTask", "megaManGANLevelChunks:10",
				"MegaManGANModel:MegaManOneGANWith12Tiles_5_Epoch5000.pth", "GANInputSize:5",
				"GANSegmentSwapMutationRate:1.0"
				});
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;
		GANProcess.getGANProcess();
		MMNEAT.loadClasses();
		
		
		ArrayList<Double> geno;

		geno = new ArrayList<>(90);
		for (double num : new double[] {0.0, 0.6, -0.5, 0.5, 0.7, -0.7, -0.4, 0.4, 0.12, -0.12}) {
			for (int i = 0; i < 5; i++) {
				//System.out.println("Adding " + num);
				geno.add(num);
				
			}
			geno.add(0.0);
			geno.add(1.0);
			geno.add(0.0);
			geno.add(0.0);
			
		}
		
		
		
		
	}
}
