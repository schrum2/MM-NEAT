package edu.southwestern.evolution.mutation.real;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;

public class SegmentSwapMutationTest {

	RealValuedGenotype geno1;
	
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false"});
		MMNEAT.loadClasses();
		geno1 = new RealValuedGenotype();
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
