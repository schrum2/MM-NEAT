package edu.southwestern.tasks.megaman.levelgenerators;

import java.util.List;

public class MegaManOneGANGenerator extends MegaManGANGenerator {

	public MegaManOneGANGenerator() {
		// TODO: initialize the one global GAN
	}
	
	@Override
	public List<List<Integer>> generateSegmentFromLatentVariables(double[] latentVariables, SEGMENT_TYPE type) {
		// TODO: Ignore type and simply generate the segment
		return null;
	}

}
