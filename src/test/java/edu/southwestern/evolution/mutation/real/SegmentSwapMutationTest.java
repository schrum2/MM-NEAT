package edu.southwestern.evolution.mutation.real;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;

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
				"marioGANUsesOriginalEncoding:false"
				});
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;
		GANProcess.getGANProcess();
		MMNEAT.loadClasses();
		geno = new ArrayList<>(15);
		for (double num : new double[] {0.3, 0.6, 0.9}) {
			for (int i = 0; i < 5; i++) {
				System.out.println("Adding " + num);
				geno.add(num);
			}
		}
		System.out.println(geno);
	}
	
	@Test
	public void test() {
		System.out.println("Genotype at start: " + geno);
		//fail("Not yet implemented");
	}

}
