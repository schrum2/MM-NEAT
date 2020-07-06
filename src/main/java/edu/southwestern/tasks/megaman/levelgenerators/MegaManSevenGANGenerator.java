package edu.southwestern.tasks.megaman.levelgenerators;

import java.util.List;

import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.PythonUtil;

public class MegaManSevenGANGenerator extends MegaManGANGenerator {

	// TODO: Split Horizontal into left and right
	private GANProcess ganProcessHorizontal = null;
	
	private GANProcess ganProcessDown = null;
	private GANProcess ganProcessUp = null;
	private GANProcess ganProcessUpperLeft = null;
	private GANProcess ganProcessUpperRight = null;
	private GANProcess ganProcessLowerLeft = null;
	private GANProcess ganProcessLowerRight = null;

	public MegaManSevenGANGenerator() {
		PythonUtil.setPythonProgram();

		ganProcessHorizontal = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
		ganProcessDown = MegaManGANUtil.initializeGAN("MegaManGANDownModel");
		ganProcessUp = MegaManGANUtil.initializeGAN("MegaManGANUpModel");
		ganProcessUpperLeft = MegaManGANUtil.initializeGAN("MegaManGANUpperLeftModel");
		ganProcessUpperRight = MegaManGANUtil.initializeGAN("MegaManGANUpperRightModel");
		ganProcessLowerLeft = MegaManGANUtil.initializeGAN("MegaManGANLowerLeftModel");
		ganProcessLowerRight = MegaManGANUtil.initializeGAN("MegaManGANLowerRightModel");

		MegaManGANUtil.startGAN(ganProcessUp);
		MegaManGANUtil.startGAN(ganProcessDown);
		MegaManGANUtil.startGAN(ganProcessHorizontal);
		MegaManGANUtil.startGAN(ganProcessUpperLeft);
		MegaManGANUtil.startGAN(ganProcessUpperRight);
		MegaManGANUtil.startGAN(ganProcessLowerLeft);
		MegaManGANUtil.startGAN(ganProcessLowerRight);
	}
	
	@Override
	public List<List<Integer>> generateSegmentFromLatentVariables(double[] latentVariables, SEGMENT_TYPE type) {
		// TODO Use the appropriate GAN to generate the segment.
		return null;
	}

}
