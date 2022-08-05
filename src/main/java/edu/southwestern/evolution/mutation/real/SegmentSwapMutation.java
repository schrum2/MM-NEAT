package edu.southwestern.evolution.mutation.real;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.MegaManTrackSegmentType;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.tasks.megaman.MegaManGANLevelTask;
import edu.southwestern.tasks.megaman.MegaManRenderUtil;
import edu.southwestern.tasks.megaman.MegaManVGLCUtil;

/**
* Segment Swap Mutation
* Swaps two random segments in a genotype.
*
* @author Maxx Batterton
*/
public class SegmentSwapMutation extends SegmentMutation {
	
	public SegmentSwapMutation() {
		super("GANSegmentSwapMutationRate");
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
				if((i < auxVariableStartLocation || i > auxVariableEndLocation) || segmentSwapAuxiliaryVarialbes) // only swap outside of auxiliary variable range if feature flag is on
					mutateIndex((RealValuedGenotype) genotype, randSegment2*segmentSize+i); // change second segment values to first segment
			}
			for (int i = 0; i < segmentSize; i++) {
				if((i < auxVariableStartLocation || i > auxVariableEndLocation) || segmentSwapAuxiliaryVarialbes) // only swap outside of auxiliary variable range if feature flag is on
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
				"GANSegmentSwapMutationRate:1.0", "megaManAllowsLeftSegments:true"
				});
		GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN;
		GANProcess.getGANProcess();
		MMNEAT.loadClasses();
		
		
		ArrayList<Double> geno;
		
		geno = new ArrayList<>(90);
		for (double num : new double[] {0.12, 0.612, -0.5123, 0.53123, 0.7123, -0.7421, -1, 0.45324, 0.12123, -0.12123}) {
			geno.add(0.0);
			geno.add(0.0);
			geno.add(1.0); // right
			geno.add(0.0);
			for (int i = 0; i < 5; i++) {
				//System.out.println("Adding " + num);
				geno.add(num);
				
			}
			

		}
		
		Genotype<ArrayList<Double>> realGeno = new BoundedRealValuedGenotype(geno);
		MegaManTrackSegmentType segmentTypeTracker = new MegaManTrackSegmentType();
		// Passing this parameter inside the hash map instead of as a normal parameter is confusing, 
		// but allows this class to conform to the JsonLevelGenerationTask easily.
		
		
		
		System.out.println("Before: ");
		System.out.println(realGeno.getPhenotype());
		List<List<Integer>> level = ((MegaManGANLevelTask) MMNEAT.task).getMegaManLevelListRepresentationFromGenotype(realGeno, segmentTypeTracker); //gets a level 
		MegaManVGLCUtil.printLevel(level);
		
//		BufferedImage image;
		BufferedImage[] images;
		try {
			//
//			int width1 = MegaManRenderUtil.renderedImageWidth(level.get(0).size());
//			int height1 = MegaManRenderUtil.renderedImageHeight(level.size());

			images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH); //7 different tiles to display 
//			image = MegaManRenderUtil.createBufferedImage(level,width1,height1, images);
			MegaManRenderUtil.getBufferedImageWithRelativeRendering(level, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("After: ");
		SegmentSwapMutation mutation = new SegmentSwapMutation();
		mutation.mutate(realGeno);
		System.out.println(realGeno.getPhenotype());
		MegaManTrackSegmentType segmentTypeTracker1 = new MegaManTrackSegmentType();
		List<List<Integer>> level1 = ((MegaManGANLevelTask) MMNEAT.task).getMegaManLevelListRepresentationFromGenotype(realGeno, segmentTypeTracker1); //gets a level
		MegaManVGLCUtil.printLevel(level1);
		
		System.out.println(level.size() + " " + level1.get(0).size());
		System.out.println(level.size()%14 + " " + level1.get(0).size()%16);
		System.out.println(level.size()%14 + " " + level1.get(0).size()/16);
		try {
			//
//			int width1 = MegaManRenderUtil.renderedImageWidth(level1.get(0).size());
//			int height1 = MegaManRenderUtil.renderedImageHeight(level1.size());

			images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH); //7 different tiles to display 
			MegaManRenderUtil.getBufferedImageWithRelativeRendering(level1, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
