package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManOneGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManSevenGANGenerator;
import edu.southwestern.util.datastructures.ArrayUtil;
/**
 * Allows for objective evolution using GANs
 * @author Benjamin Capps
 *
 */
public class MegaManGANLevelTask extends MegaManLevelTask<List<Double>> {

	private MegaManGANGenerator megaManGenerator;

	public MegaManGANLevelTask(){
		super();
		
		
		if(Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")) {
			megaManGenerator = new MegaManSevenGANGenerator();
		}
		else {
			megaManGenerator = new MegaManOneGANGenerator();
		}
	}
	
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	@Override
	public List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<List<Double>> individual) {
		List<Double> latentVector = individual.getPhenotype();
		return getMegaManLevelListRepresentationFromStaticGenotype(megaManGenerator, latentVector, Parameters.parameters.integerParameter("megaManGANLevelChunks"));
	}
	private List<List<Integer>> getMegaManLevelListRepresentationFromStaticGenotype(
			MegaManGANGenerator megaManGenerator, List<Double> latentVector, int chunks) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = MegaManGANUtil.longVectorToMegaManLevel(megaManGenerator, doubleArray, chunks);
		return level;
	}

	/**
	 * static version of method above
	 * @param ganProcessHorizontal
	 * @param ganProcessUp
	 * @param ganProcessDown
	 * @param latentVector
	 * @return
	 */
//	public static List<List<Integer>> getMegaManLevelListRepresentationFromStaticGenotype(GANProcess ganProcessHorizontal, GANProcess ganProcessUp, GANProcess ganProcessDown,GANProcess ganProcessLowerLeft,GANProcess ganProcessLowerRight,GANProcess ganProcessUpperLeft ,GANProcess ganProcessUpperRight, int chunks, List<Double> latentVector) {
//		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
//		List<List<Integer>> level = MegaManGANUtil.wholeVectorToMegaManLevel(ganProcessHorizontal,ganProcessUp,ganProcessDown,ganProcessLowerLeft,ganProcessLowerRight,ganProcessUpperLeft,ganProcessUpperRight, chunks, doubleArray);
//		return level;
//	}

	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Uses original GECCO 2018 Mario GAN
		MMNEAT.main("runNumber:0 randomSeed:0 base:megaManGAN log:MegaManGAN-Test saveTo:Test trials:1 GANInputSize:5 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask megaManGANLevelChunks:10 megaManAllowsSimpleAStarPath:true megaManAllowsConnectivity:true useMultipleGANsMegaMan:true saveAllChampions:false megaManAllowsLeftSegments:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false watch:true".split(" "));
	}

	@Override
	public HashMap<String, Integer> findMiscSegments(List<List<Integer>> level) {
		// TODO Auto-generated method stub
		return MegaManGANUtil.findMiscSegments(level);
	}
}
