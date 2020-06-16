package megaManMaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.Pair;

public abstract class MegaManLevelTask<T> extends NoisyLonerTask<T> {
	private static int numFitnessFunctions = 0; 
	private static final int numOtherScores = 2;

	MegaManLevelTask(){
		this(true);
	}
	protected MegaManLevelTask(boolean register) {
		if(register) {
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
				MMNEAT.registerFitnessFunction("simpleAStarDistance");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
				MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
				numFitnessFunctions++;
			}

			//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
			MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("percentConnected", false);
		}
	}
	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTimeStamp() {
		return 0; //not used
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		GANProcess ganProcessHorizontal = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter("MegaManGANHorizontalModel"), 
				Parameters.parameters.integerParameter("GANInputSize"), 
				/*Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLYMegaManAllLevel") ? */MegaManGANUtil.MEGA_MAN_ALL_TERRAIN /*: MegaManGANUtil.MEGA_MAN_FIRST_LEVEL_ALL_TILES*/,
				GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
		GANProcess ganProcessVertical = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter("MegaManGANVerticalModel"), 
				Parameters.parameters.integerParameter("GANInputSize"), 
				/*Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLYMegaManAllLevel") ? */MegaManGANUtil.MEGA_MAN_ALL_TERRAIN /*: MegaManGANUtil.MEGA_MAN_FIRST_LEVEL_ALL_TILES*/,
				GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
		ganProcessVertical.start();
		ganProcessHorizontal.start();
		String response = "";
		while(!response.equals("READY")) {
			response = ganProcessVertical.commRecv();
			response = ganProcessHorizontal.commRecv();
		}
		List<List<Integer>> level = getMegaManLevelListRepresentationFromGenotype(ganProcessHorizontal, ganProcessVertical, (Genotype<List<Double>>) individual); //gets a level 
		return null;
	}

	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	public abstract List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(GANProcess ganProcessHorizontal, GANProcess ganProcessVertical, Genotype<List<Double>> individual);

}
