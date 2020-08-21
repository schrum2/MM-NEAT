package edu.southwestern.tasks.megaman.levelgenerators;

import java.util.List;

import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.PythonUtil;

public class MegaManSevenGANGenerator extends MegaManGANGenerator {

	// TODO: Split Horizontal into left and right
	private GANProcess ganProcessRight = null;
	private GANProcess ganProcessLeft = null;

	private GANProcess ganProcessDown = null;
	private GANProcess ganProcessUp = null;
	private GANProcess ganProcessUpperLeft = null;
	private GANProcess ganProcessUpperRight = null;
	private GANProcess ganProcessLowerLeft = null;
	private GANProcess ganProcessLowerRight = null;

	public MegaManSevenGANGenerator() {
		PythonUtil.setPythonProgram();

		ganProcessRight = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
		ganProcessLeft = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
		ganProcessDown = MegaManGANUtil.initializeGAN("MegaManGANDownModel");
		ganProcessUp = MegaManGANUtil.initializeGAN("MegaManGANUpModel");
		ganProcessUpperLeft = MegaManGANUtil.initializeGAN("MegaManGANUpperLeftModel");
		ganProcessUpperRight = MegaManGANUtil.initializeGAN("MegaManGANUpperRightModel");
		ganProcessLowerLeft = MegaManGANUtil.initializeGAN("MegaManGANLowerLeftModel");
		ganProcessLowerRight = MegaManGANUtil.initializeGAN("MegaManGANLowerRightModel");

		MegaManGANUtil.startGAN(ganProcessUp);
		MegaManGANUtil.startGAN(ganProcessLeft);
		MegaManGANUtil.startGAN(ganProcessDown);
		MegaManGANUtil.startGAN(ganProcessRight);
		MegaManGANUtil.startGAN(ganProcessUpperLeft);
		MegaManGANUtil.startGAN(ganProcessUpperRight);
		MegaManGANUtil.startGAN(ganProcessLowerLeft);
		MegaManGANUtil.startGAN(ganProcessLowerRight);
	}
	
	@Override
	public List<List<Integer>> generateSegmentFromLatentVariables(double[] latentVariables, SEGMENT_TYPE type) {
		switch(type) {
			case UP:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, latentVariables).get(0);
			case DOWN:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, latentVariables).get(0);
			case RIGHT:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessRight, latentVariables).get(0);
			case LEFT:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessLeft, latentVariables).get(0);
			case TOP_RIGHT:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUpperRight, latentVariables).get(0);
			case TOP_LEFT:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUpperLeft, latentVariables).get(0);
			case BOTTOM_RIGHT:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessLowerRight, latentVariables).get(0);
			case BOTTOM_LEFT:
				return MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessLowerLeft, latentVariables).get(0);
			default: throw new IllegalArgumentException("Valid SEGMENT_TYPE not specified");
		}
	}

	@Override
	public void finalCleanup() {
		ganProcessRight.terminate();
		ganProcessLeft.terminate();
		ganProcessDown.terminate();
		ganProcessUp.terminate();
		ganProcessUpperLeft.terminate();
		ganProcessUpperRight.terminate();
		ganProcessLowerLeft.terminate();
		ganProcessLowerRight.terminate();
	}
}
