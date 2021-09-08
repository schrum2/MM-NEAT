package edu.southwestern.evolution.mutation.real;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.MarioGANLevelTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;

public class SegmentSwapMutationTest {
	
	ArrayList<Double> geno;
	
	@Before
	public void setUp() throws Exception {
		
		Parameters.initializeParameterCollections(new String[] {
				"runNumber:0", "randomSeed:0", 
				"io:false", "netio:false",
				"trials:1", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
				"task:edu.southwestern.tasks.mario.MarioGANLevelTask", "marioGANLevelChunks:3",
				"marioGANModel:Mario1_Overworld_5_Epoch5000.pth", "GANInputSize:5",
				"marioGANUsesOriginalEncoding:false",
				"GANSegmentSwapMutationRate:1.0"
				});
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;
		GANProcess.getGANProcess();
		MMNEAT.loadClasses();
		geno = new ArrayList<>(15);
		for (double num : new double[] {0.0, 0.6, -0.5}) {
			for (int i = 0; i < 5; i++) {
				//System.out.println("Adding " + num);
				geno.add(num);
			}
		}
		//System.out.println(geno);
		//geno = ArrayUtil.doubleVectorFromArray(new double[] {0,0,0,0,0,0.3,0.3,0.3,0.3,0.3,0.432,1,1,-1,-0.23});
	}
	
	@Test
	public void testMario() {
		System.out.println("Genotype at start: " + geno); // print genotype
		ArrayList<List<Integer>> levelRepresentation = MarioGANLevelTask.getMarioLevelListRepresentationFromStaticGenotype(geno); // get level
		MarioLevelUtil.printSingleLevel(levelRepresentation); // print level
		List<List<List<Integer>>> originalLevelSegments = MarioLevelUtil.getSegmentsFromLevel(levelRepresentation); // get segments
		
		RealValuedGenotype realGeno = new RealValuedGenotype(geno);
		SegmentSwapMutation mutation = new SegmentSwapMutation();
		mutation.mutate(realGeno);
		
		System.out.println("Genotype after mutation: " + realGeno.getPhenotype()); // print genotype
		ArrayList<List<Integer>> mutatedLevelRepresentation = MarioGANLevelTask.getMarioLevelListRepresentationFromStaticGenotype(realGeno.getPhenotype()); // get level
		MarioLevelUtil.printSingleLevel(mutatedLevelRepresentation); // print level
		List<List<List<Integer>>> mutatedLevelSegments = MarioLevelUtil.getSegmentsFromLevel(mutatedLevelRepresentation); // get segments
		
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
		mutatedLevelRepresentation = MarioGANLevelTask.getMarioLevelListRepresentationFromStaticGenotype(realGeno.getPhenotype()); // get level
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
		mutatedLevelRepresentation = MarioGANLevelTask.getMarioLevelListRepresentationFromStaticGenotype(realGeno.getPhenotype()); // get level
		MarioLevelUtil.printSingleLevel(mutatedLevelRepresentation); // print level
		mutatedLevelSegments = MarioLevelUtil.getSegmentsFromLevel(mutatedLevelRepresentation); // get segments
		
		// Should be swapping segment 2 and segment 0 with randomSeed of 2
		assertEquals(originalLevelSegments.get(0), mutatedLevelSegments.get(1));
		assertEquals(originalLevelSegments.get(1), mutatedLevelSegments.get(0));
		assertEquals(originalLevelSegments.get(2), mutatedLevelSegments.get(2));
		
	}

}
