package edu.southwestern.tasks.megaman.levelgenerators;

import java.util.List;

import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.PythonUtil;

public class MegaManOneGANGenerator extends MegaManGANGenerator {

	public MegaManOneGANGenerator() {
		// TODO: initialize the one global GAN
		PythonUtil.setPythonProgram();
		GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN;
	
	}
	
	@Override
	public List<List<Integer>> generateSegmentFromLatentVariables(double[] latentVariables, SEGMENT_TYPE type) {
		// TODO: Ignore type and simply generate the segment
		return MegaManGANUtil.getLevelListRepresentationFromGAN(GANProcess.getGANProcess(), latentVariables).get(0);
	}

}
