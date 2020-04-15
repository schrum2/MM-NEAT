package edu.southwestern.tasks.zelda;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class ZeldaGANDungeonTask extends ZeldaDungeonTask<ArrayList<Double>>{

	private int segmentLength;

	public ZeldaGANDungeonTask() {
		super();
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
		segmentLength = GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.NUM_NON_LATENT_INPUTS;
	}
	
	public static int genomeLength() {
		return (GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.NUM_NON_LATENT_INPUTS)*
				Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks")*
				Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
	}
	
	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> latentVector = individual.getPhenotype();
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		int width = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks");
		int height = Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
		Pair<double[][][],double[][][]> gridRepresentation = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(new ZeldaDirectGANVectorMatrixBuilder(doubleArray, segmentLength), width, height);
		Dungeon dungeon = ZeldaCPPNtoGANLevelBreederTask.gridDataToDungeon(gridRepresentation.t1, gridRepresentation.t2);
		return dungeon;
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:0 randomSeed:0 zeldaDungeonDistanceFitness:true zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:10 makeZeldaLevelsPlayable:false base:zeldagan log:ZeldaGAN-DistTraversed saveTo:DistTraversed zeldaGANLevelWidthChunks:10 zeldaGANLevelHeightChunks:10 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:500 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype".split(" "));
	}
}
