package megaManMaker;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

public class MegaManGANLevelTask extends MegaManLevelTask<List<Double>> {
	public static GANProcess ganProcessHorizontal = null;
	public static GANProcess ganProcessVertical = null;
	public static GANProcess ganProcessUp = null;
	public static GANProcess ganProcessDown = null;

	public MegaManGANLevelTask(){
		super();
		PythonUtil.setPythonProgram();
		//super();
		//if(Parameters.parameters.booleanParameter("useThreeGANsMegaMan")) {
			//GANProcess.terminateGANProcess();
			ganProcessHorizontal = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
			ganProcessVertical = MegaManGANUtil.initializeGAN("MegaManGANVerticalModel");
			ganProcessUp = MegaManGANUtil.initializeGAN("MegaManGANUpModel");
	//		ganProcessDown = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter("MegaManGANDownModel"), 
	//		Parameters.parameters.integerParameter("GANInputSize"), 
	//		/*Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLYMegaManAllLevel") ? */MegaManGANUtil.MEGA_MAN_ALL_TERRAIN /*: MegaManGANUtil.MEGA_MAN_FIRST_LEVEL_ALL_TILES*/,
	//		GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
			MegaManGANUtil.startGAN(ganProcessUp);
			MegaManGANUtil.startGAN(ganProcessVertical);
			MegaManGANUtil.startGAN(ganProcessHorizontal);
//		}else {
//			GANProcess.getGANProcess();
//		}
	}
	
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	@Override
	public List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<List<Double>> individual) {
		List<Double> latentVector = individual.getPhenotype();
		return getMegaManLevelListRepresentationFromStaticGenotype(ganProcessHorizontal, ganProcessUp, ganProcessVertical, latentVector);
	}

	public static List<List<Integer>> getMegaManLevelListRepresentationFromStaticGenotype(GANProcess ganProcessHorizontal, GANProcess ganProcessUp, GANProcess ganProcessDown,List<Double> latentVector) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = MegaManGANUtil.generateOneLevelListRepresentationFromGANVerticalAndHorizontal(ganProcessHorizontal, ganProcessUp,ganProcessDown,doubleArray);
		return level;
	}

	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Uses original GECCO 2018 Mario GAN
		MMNEAT.main("runNumber:0 randomSeed:0 base:megaManGAN log:MegaManGAN-Test saveTo:Test trials:1 GANInputSize:5 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:megaManMaker.MegaManGANLevelTask useThreeGANsMegaMan:true megaManGANLevelChunks:10 megaManAllowsSimpleAStarPath:true megaManAllowsConnectivity:true saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
	}
}
