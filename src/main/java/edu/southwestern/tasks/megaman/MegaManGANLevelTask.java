package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
/**
 * Allows for objective evolution using GANs
 * @author Benjamin Capps
 *
 */
public class MegaManGANLevelTask extends MegaManLevelTask<List<Double>> {
	public static GANProcess ganProcessHorizontal = null;
	public static GANProcess ganProcessDown = null;
	public static GANProcess ganProcessUp = null;
	public static GANProcess ganProcessUpperLeft = null;
	public static GANProcess ganProcessUpperRight = null;
	public static GANProcess ganProcessLowerLeft = null;
	public static GANProcess ganProcessLowerRight = null;
	//public static GANProcess ganProcessDown = null;

	public MegaManGANLevelTask(){
		super();
		PythonUtil.setPythonProgram();
		//super();
		//if(Parameters.parameters.booleanParameter("useThreeGANsMegaMan")) {
			//GANProcess.terminateGANProcess();
			ganProcessHorizontal = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
			ganProcessDown = MegaManGANUtil.initializeGAN("MegaManGANDownModel");
			ganProcessUp = MegaManGANUtil.initializeGAN("MegaManGANUpModel");
			ganProcessUpperLeft = MegaManGANUtil.initializeGAN("MegaManGANUpperLeftModel");
			ganProcessUpperRight = MegaManGANUtil.initializeGAN("MegaManGANUpperRightModel");
			ganProcessLowerLeft = MegaManGANUtil.initializeGAN("MegaManGANLowerLeftModel");
			ganProcessLowerRight = MegaManGANUtil.initializeGAN("MegaManGANLowerRightModel");
	//		ganProcessDown = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter("MegaManGANDownModel"), 
	//		Parameters.parameters.integerParameter("GANInputSize"), 
	//		/*Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLYMegaManAllLevel") ? */MegaManGANUtil.MEGA_MAN_ALL_TERRAIN /*: MegaManGANUtil.MEGA_MAN_FIRST_LEVEL_ALL_TILES*/,
	//		GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
			MegaManGANUtil.startGAN(ganProcessUp);
			MegaManGANUtil.startGAN(ganProcessDown);
			MegaManGANUtil.startGAN(ganProcessHorizontal);
			MegaManGANUtil.startGAN(ganProcessUpperLeft);
			MegaManGANUtil.startGAN(ganProcessUpperRight);
			MegaManGANUtil.startGAN(ganProcessLowerLeft);
			MegaManGANUtil.startGAN(ganProcessLowerRight);
	}
	
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	@Override
	public List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<List<Double>> individual) {
		List<Double> latentVector = individual.getPhenotype();
		return getMegaManLevelListRepresentationFromStaticGenotype(ganProcessHorizontal, ganProcessUp, ganProcessDown,ganProcessLowerLeft, ganProcessLowerRight, ganProcessUpperLeft, ganProcessUpperRight, Parameters.parameters.integerParameter("megaManGANLevelChunks"), latentVector);
	}
	/**
	 * static version of method above
	 * @param ganProcessHorizontal
	 * @param ganProcessUp
	 * @param ganProcessDown
	 * @param latentVector
	 * @return
	 */
	public static List<List<Integer>> getMegaManLevelListRepresentationFromStaticGenotype(GANProcess ganProcessHorizontal, GANProcess ganProcessUp, GANProcess ganProcessDown,GANProcess ganProcessLowerLeft,GANProcess ganProcessLowerRight,GANProcess ganProcessUpperLeft ,GANProcess ganProcessUpperRight, int chunks, List<Double> latentVector) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = MegaManGANUtil.wholeVectorToMegaManLevel(ganProcessHorizontal,ganProcessUp,ganProcessDown,ganProcessLowerLeft,ganProcessLowerRight,ganProcessUpperLeft,ganProcessUpperRight, chunks, doubleArray);
		return level;
	}

	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Uses original GECCO 2018 Mario GAN
		MMNEAT.main("runNumber:0 randomSeed:0 base:megaManGAN log:MegaManGAN-Test saveTo:Test trials:1 GANInputSize:5 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask useThreeGANsMegaMan:true megaManGANLevelChunks:10 megaManAllowsSimpleAStarPath:true megaManAllowsConnectivity:true saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false watch:true".split(" "));
	}

	@Override
	public HashMap<String, Integer> findMiscSegments(List<List<Integer>> level) {
		// TODO Auto-generated method stub
		return MegaManGANUtil.findMiscSegments(level);
	}
}
