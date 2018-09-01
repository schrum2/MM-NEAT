package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.EvaluationInfo;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.MarioGANUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * Evolve Mario levels with latent vectors 
 * for the Mario GAN using an agent,
 * like the Mario A* Agent, as a means of evaluating
 * 
 * @author Jacob Schrum
 *
 * @param <T> real vector
 */
public class MarioGANLevelTask extends MarioLevelTask<ArrayList<Double>> {

	// This is the length of one GAN level segment excluding starting and ending areas
	public static final int BASE_LEVEL_LENGTH = 448;
	// This is how much traversable area is added to the start and end of each level produced by the GAN
	public static final int BUFFER_LENGTH = 240;
	
	public MarioGANLevelTask() {
		super();
	}

	@Override
	public double totalPassableDistance(EvaluationInfo info) {
		return BUFFER_LENGTH + Parameters.parameters.integerParameter("marioGANLevelChunks")*BASE_LEVEL_LENGTH;
	}
		
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a Mario level
	 */
	@Override
	public Level getMarioLevelFromGenotype(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> latentVector = individual.getPhenotype();
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		Level level = MarioGANUtil.generateLevelFromGAN(doubleArray);
		return level;
	}

	/**
	 * For quick testing
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Uses original GECCO 2018 Mario GAN
		//MMNEAT.main("runNumber:0 randomSeed:0 base:mariogan log:MarioGAN-Test saveTo:Test trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
		// Uses underworld GAN
		//MMNEAT.main("runNumber:0 randomSeed:0 base:mariogan log:MarioGAN-UnderWorld saveTo:UnderWorld trials:1 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Underground_30_Epoch_5000.pth GANInputSize:30 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
		// Uses underworld GAN to combine three level segments
		//MMNEAT.main("runNumber:0 randomSeed:0 base:mariogan log:MarioGAN-UnderWorld3Segments saveTo:UnderWorld3Segments marioGANLevelChunks:3 trials:1 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Underground_30_Epoch_5000.pth GANInputSize:30 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
		// Uses overworld GAN to combine 4 level segments
		MMNEAT.main("runNumber:0 randomSeed:0 base:mariogan log:MarioGAN-OverWorld4Segments saveTo:OverWorld4Segments marioGANLevelChunks:4 trials:1 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_30_Epoch5000.pth GANInputSize:30 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
	}
}
