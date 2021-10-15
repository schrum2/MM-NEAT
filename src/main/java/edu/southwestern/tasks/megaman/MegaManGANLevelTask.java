package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.BoundedTask;
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
public class MegaManGANLevelTask extends MegaManLevelTask<ArrayList<Double>> implements BoundedTask {

	private MegaManGANGenerator megaManGenerator;
	// Bounds used for GAN-based solutions
	private static double[] upper;
	private static double[] lower;
	
	public MegaManGANLevelTask(){
		super();

		if(Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")) {
			megaManGenerator = new MegaManSevenGANGenerator();
		}
		else {
			megaManGenerator = new MegaManOneGANGenerator();
		}
	}
	
	public void finalCleanup() {
		megaManGenerator.finalCleanup();
	}
	
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	@Override
	public List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<ArrayList<Double>> individual, MegaManTrackSegmentType segmentCount) {
		List<Double> latentVector = individual.getPhenotype();
		return getMegaManLevelListRepresentationFromStaticGenotype(megaManGenerator, latentVector, Parameters.parameters.integerParameter("megaManGANLevelChunks"), segmentCount);
	}
	/**
	 * static version of method above
	 * @param ganProcessHorizontal
	 * @param ganProcessUp
	 * @param ganProcessDown
	 * @param latentVector
	 * @return
	 */
	private List<List<Integer>> getMegaManLevelListRepresentationFromStaticGenotype(
			MegaManGANGenerator megaManGenerator, List<Double> latentVector, int chunks, MegaManTrackSegmentType segmentCount) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = MegaManGANUtil.longVectorToMegaManLevel(megaManGenerator, doubleArray, chunks, segmentCount);
		return level;
	}

	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Uses original GECCO 2018 Mario GAN
		MMNEAT.main("runNumber:0 randomSeed:0 base:megaManGAN log:MegaManGAN-Test saveTo:Test trials:1 GANInputSize:5 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask megaManGANLevelChunks:10 megaManAllowsSimpleAStarPath:true megaManAllowsConnectivity:true useMultipleGANsMegaMan:false saveAllChampions:false megaManAllowsLeftSegments:false megaManMaximizeEnemies:true cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false watch:true".split(" "));
	}

	public static double[] getStaticUpperBounds() {
		if(upper == null) upper = ArrayUtil.doubleOnes((Parameters.parameters.integerParameter("GANInputSize") + MegaManGANGenerator.numberOfAuxiliaryVariables()) * Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		return upper;
	}

	public static double[] getStaticLowerBounds() {
		if(lower == null) lower = ArrayUtil.doubleNegativeOnes((Parameters.parameters.integerParameter("GANInputSize") + MegaManGANGenerator.numberOfAuxiliaryVariables()) * Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		return lower;
	}

	@Override
	public double[] getUpperBounds() {
		return getStaticUpperBounds();
	}

	@Override
	public double[] getLowerBounds() {
		return getStaticLowerBounds();
	}
}
