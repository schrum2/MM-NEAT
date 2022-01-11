package edu.southwestern.tasks.gvgai.zelda;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;


public class ZeldaGANLevelTask extends ZeldaLevelTask<ArrayList<Double>> implements BoundedTask{

	private static double[] upper;
	private static double[] lower;
	
	@Override
	public List<List<Integer>> getZeldaLevelFromGenotype(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> latentVector = individual.getPhenotype();
		double[] room = ArrayUtil.doubleArrayFromList(latentVector);
		return ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(room);
	}
	
	@Override
	public GameBundle getBundleFromGenotype(Genotype<ArrayList<Double>> individual) {
		return ZeldaGANLevelBreederTask.setUpGameWithLevelFromLatentVector(individual.getPhenotype());
	}
	
	/**
	 * For quick testing
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// zeldaGANModel:ZeldaDungeonsAll_5000_10.pth
		MMNEAT.main("runNumber:2 randomSeed:0 base:zeldagan log:ZeldaGAN-AllDungeonsFixed saveTo:AllDungeonsFixed trials:1 zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth GANInputSize:10 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.gvgai.zelda.ZeldaGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false watch:false zeldaGANUsesOriginalEncoding:false".split(" "));
	}

	public static double[] getStaticUpperBounds() {
		if(upper == null) upper = ArrayUtil.doubleOnes(GANProcess.latentVectorLength()); // all ones
		return upper;
	}

	public static double[] getStaticLowerBounds() {
		if(lower == null) lower = ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength()); // all -1
		return lower;
	}

	@Override
	public double[] getUpperBounds() {
		return getStaticUpperBounds() ;
	}

	@Override
	public double[] getLowerBounds() {
		return getStaticLowerBounds();
	}


	
}
