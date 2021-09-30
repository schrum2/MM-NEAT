package edu.southwestern.evolution.mutation.real;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.MarioGANLevelTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;
import edu.southwestern.tasks.megaman.MegaManGANLevelTask;
import edu.southwestern.tasks.megaman.MegaManTrackSegmentType;
import edu.southwestern.tasks.megaman.LevelNovelty;

public class SegmentSwapMutationTest {

	@After
	public void teardown() {
		GANProcess.terminateGANProcess();
	}

	@Test
	public void testMario() {
		Parameters.initializeParameterCollections(new String[] { "runNumber:0", "randomSeed:0", "io:false",
				"netio:false", "trials:1", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
				"task:edu.southwestern.tasks.mario.MarioGANLevelTask", "marioGANLevelChunks:3",
				"marioGANModel:Mario1_Overworld_5_Epoch5000.pth", "GANInputSize:5",
				"marioGANUsesOriginalEncoding:false", "GANSegmentSwapMutationRate:1.0" });
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;
		GANProcess.getGANProcess();
		MMNEAT.loadClasses();
		ArrayList<Double> geno = new ArrayList<>(15);
		for (double num : new double[] { 0.0, 0.6, -0.5 }) {
			for (int i = 0; i < 5; i++) {
				// System.out.println("Adding " + num);
				geno.add(num);
			}
		}
		System.out.println("Genotype at start: " + geno); // print genotype
		ArrayList<List<Integer>> levelRepresentation = MarioGANLevelTask
				.getMarioLevelListRepresentationFromStaticGenotype(geno); // get level
		MarioLevelUtil.printSingleLevel(levelRepresentation); // print level
		List<List<List<Integer>>> originalLevelSegments = MarioLevelUtil.getSegmentsFromLevel(levelRepresentation); // get
																													// segments

		RealValuedGenotype realGeno = new RealValuedGenotype(geno);
		SegmentSwapMutation mutation = new SegmentSwapMutation();
		mutation.mutate(realGeno);

		System.out.println("Genotype after mutation: " + realGeno.getPhenotype()); // print genotype
		ArrayList<List<Integer>> mutatedLevelRepresentation = MarioGANLevelTask
				.getMarioLevelListRepresentationFromStaticGenotype(realGeno.getPhenotype()); // get level
		MarioLevelUtil.printSingleLevel(mutatedLevelRepresentation); // print level
		List<List<List<Integer>>> mutatedLevelSegments = MarioLevelUtil
				.getSegmentsFromLevel(mutatedLevelRepresentation); // get segments

		// Should be swapping segment 0 and segment 1 with randomSeed of 0
		assertEquals(originalLevelSegments.get(0), mutatedLevelSegments.get(0));
		assertEquals(originalLevelSegments.get(1), mutatedLevelSegments.get(2));
		assertEquals(originalLevelSegments.get(2), mutatedLevelSegments.get(1));

		////////////////////////////

		Parameters.parameters.setInteger("randomSeed", 1);

		System.out.println("Genotype at start: " + geno); // print genotype
		MarioLevelUtil.printSingleLevel(levelRepresentation); // print level

		realGeno = new RealValuedGenotype(geno); // phenotype -> genotype
		mutation = new SegmentSwapMutation(); // make mutation
		mutation.mutate(realGeno); // mutate genotype

		System.out.println("Genotype after mutation: " + realGeno.getPhenotype()); // print genotype
		mutatedLevelRepresentation = MarioGANLevelTask
				.getMarioLevelListRepresentationFromStaticGenotype(realGeno.getPhenotype()); // get level
		MarioLevelUtil.printSingleLevel(mutatedLevelRepresentation); // print level
		mutatedLevelSegments = MarioLevelUtil.getSegmentsFromLevel(mutatedLevelRepresentation); // get segments

		// Should be swapping segment 1 and segment 2 with randomSeed of 1
		assertEquals(originalLevelSegments.get(0), mutatedLevelSegments.get(2));
		assertEquals(originalLevelSegments.get(1), mutatedLevelSegments.get(1));
		assertEquals(originalLevelSegments.get(2), mutatedLevelSegments.get(0));

		////////////////////////////

		Parameters.parameters.setInteger("randomSeed", 2);

		System.out.println("Genotype at start: " + geno); // print genotype
		MarioLevelUtil.printSingleLevel(levelRepresentation); // print level

		realGeno = new RealValuedGenotype(geno); // phenotype -> genotype
		mutation = new SegmentSwapMutation(); // make mutation
		mutation.mutate(realGeno); // mutate genotype

		System.out.println("Genotype after mutation: " + realGeno.getPhenotype()); // print genotype
		mutatedLevelRepresentation = MarioGANLevelTask
				.getMarioLevelListRepresentationFromStaticGenotype(realGeno.getPhenotype()); // get level
		MarioLevelUtil.printSingleLevel(mutatedLevelRepresentation); // print level
		mutatedLevelSegments = MarioLevelUtil.getSegmentsFromLevel(mutatedLevelRepresentation); // get segments

		// Should be swapping segment 2 and segment 0 with randomSeed of 2
		assertEquals(originalLevelSegments.get(0), mutatedLevelSegments.get(1));
		assertEquals(originalLevelSegments.get(1), mutatedLevelSegments.get(0));
		assertEquals(originalLevelSegments.get(2), mutatedLevelSegments.get(2));
	}

	
	
	
	
	@Test
	public void testMegaMan() {
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
		
		
		ArrayList<Double> geno = new ArrayList<>(90);
		for (double num : new double[] {0.12, 0.612, -0.5123, 0.53123, 0.7123, -0.7421, -1, 0.45324, 0.12123, -0.12123}) {
			geno.add(0.0);
			geno.add(0.0);
			geno.add(1.0); // right
			geno.add(0.0);
			for (int i = 0; i < 5; i++) {
				geno.add(num);
				
			}
			

		}
		
		Genotype<ArrayList<Double>> realGeno = new BoundedRealValuedGenotype(geno);
		MegaManTrackSegmentType segmentCount = new MegaManTrackSegmentType();
		// Passing this parameter inside the hash map instead of as a normal parameter is confusing, 
		// but allows this class to conform to the JsonLevelGenerationTask easily.
		
		
		List<List<Integer>> level = ((MegaManGANLevelTask) MMNEAT.task).getMegaManLevelListRepresentationFromGenotype(realGeno, segmentCount); //gets a level 
		
		
		List<List<List<Integer>>> beforeMutation = LevelNovelty.partitionSegments(level, 14, 16);

		SegmentSwapMutation mutation = new SegmentSwapMutation();
		mutation.mutate(realGeno);
		MegaManTrackSegmentType segmentCount1 = new MegaManTrackSegmentType();
		List<List<Integer>> level1 = ((MegaManGANLevelTask) MMNEAT.task).getMegaManLevelListRepresentationFromGenotype(realGeno, segmentCount1); //gets a level
		
		List<List<List<Integer>>> afterMutation = LevelNovelty.partitionSegments(level1, 14, 16);
		assertEquals(beforeMutation.get(0), afterMutation.get(0));
		assertEquals(beforeMutation.get(1), afterMutation.get(6));
		assertEquals(beforeMutation.get(2), afterMutation.get(2));
		assertEquals(beforeMutation.get(3), afterMutation.get(3));
		assertEquals(beforeMutation.get(4), afterMutation.get(4));
		assertEquals(beforeMutation.get(5), afterMutation.get(5));
		assertEquals(beforeMutation.get(6), afterMutation.get(1));
		assertEquals(beforeMutation.get(7), afterMutation.get(7));
		assertEquals(beforeMutation.get(8), afterMutation.get(8));
		assertEquals(beforeMutation.get(9), afterMutation.get(9));

	}

}
