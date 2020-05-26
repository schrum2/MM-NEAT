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

	/**
	 * Initialize the GAN for Zelda, and configure the genome segment length (num latent variables needed per room).
	 */
	public ZeldaGANDungeonTask() {
		super();
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
		segmentLength = GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables();
	}
	/**
	 * Calculates the length of the genome representing the dungeon.
	 * @return the product of the width, height, and the sum of the GAN latent
	 * 			vector length and the number of non-latent inputs from the ZeldaCPPNtoGAN
	 */
	public static int genomeLength() {
		return (GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables())*
				Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks")*
				Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
	}
	
	@Override
	/**
	 * Takes in a lengthy real-valued genome with latent variables for each room, then returns a dungeon.
	 * The variables for each room also contain additional information about door connectivity/type and
	 * where the start/end rooms of the dungeon are.
	 * 
	 * @param individual - the genotype containing latent vectors pertaining the Zelda dungeon
	 * @return dungeon - the Zelda dungeon given the genotype.
	 */
	public Dungeon getZeldaDungeonFromGenotype(Genotype<ArrayList<Double>> individual) {
		int width = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"); //the width of the dungeon
		int height = Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"); //the height of the dungeon
		return getZeldaDungeonFromDirectArrayList(individual.getPhenotype(), segmentLength, width, height);
	}
	
	/**
	 * Takes in a lengthy real-valued genome with latent variables for each room, then returns a dungeon.
	 * The variables for each room also contain additional information about door connectivity/type and
	 * where the start/end rooms of the dungeon are.
	 * 
	 * @param latentVector the array of latent variables (and additional info) for all rooms in dungeon
	 * @param segmentLength the latent vector length plus the number of non-latent variables
	 * @param width width chunks
	 * @param height height chunks
	 * @return dungeon the dungeon given the genotype
	 */
	public static Dungeon getZeldaDungeonFromDirectArrayList(ArrayList<Double> latentVector, int segmentLength, int width, int height) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		Pair<double[][][],double[][][]> gridRepresentation = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(new ZeldaDirectGANVectorMatrixBuilder(doubleArray, segmentLength), width, height);
		Dungeon dungeon = ZeldaCPPNtoGANLevelBreederTask.gridDataToDungeon(gridRepresentation.t1, gridRepresentation.t2); //transforms the grid data into a dungeon
		return dungeon;
	}

	/**
	 * Main method, useful for testing
	 *
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:0 randomSeed:0 zeldaDungeonDistanceFitness:true zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:10 makeZeldaLevelsPlayable:false base:zeldagan log:ZeldaGAN-DistTraversed saveTo:DistTraversed zeldaGANLevelWidthChunks:10 zeldaGANLevelHeightChunks:10 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:500 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype".split(" "));
	}
}
