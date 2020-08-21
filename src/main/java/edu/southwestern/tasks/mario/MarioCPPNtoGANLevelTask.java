package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ch.idsia.tools.EvaluationInfo;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.MarioGANUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * Evolve Mario levels with CPPNs and GAN using an agent,
 * like the Mario A* Agent, as a means of evaluating
 * 
 * @author Jacob Schrum
 *
 * @param <T> A NN that can generate levels as a CPPN
 */
public class MarioCPPNtoGANLevelTask<T extends Network> extends MarioLevelTask<T> {
	
	// This is the length of one GAN level segment excluding starting and ending areas
	public static final int BASE_LEVEL_LENGTH = 448;
	// This is how much traversable area is added to the start and end of each level produced by the GAN
	public static final int BUFFER_LENGTH = 240;
	
	public MarioCPPNtoGANLevelTask() {
		super();
	}

	@Override
	public double totalPassableDistance(EvaluationInfo info) {
		return BUFFER_LENGTH + Parameters.parameters.integerParameter("marioGANLevelChunks")*BASE_LEVEL_LENGTH;
	}
	
	/**
	 * Generate the level from a CPPN
	 */
	@Override
	public ArrayList<List<Integer>> getMarioLevelListRepresentationFromGenotype(Genotype<T> individual) {
		return getMarioLevelListRepresentationFromStaticGenotype(individual.getPhenotype());
	}

	public static ArrayList<List<Integer>> getMarioLevelListRepresentationFromStaticGenotype(Network cppn) {
		//Network cppn = individual.getPhenotype();
		double[] doubleArray = MarioCPPNtoGANLevelBreederTask.createLatentVectorFromCPPN(cppn, ArrayUtil.doubleOnes(cppn.numInputs()), Parameters.parameters.integerParameter("marioGANLevelChunks"));
		ArrayList<List<Integer>> level = MarioGANUtil.generateLevelListRepresentationFromGAN(doubleArray);
		return level;
	}

	/**
	 * For quick testing
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:0 randomSeed:0 base:mariocppntogan log:MarioCPPNtoGAN-Test saveTo:Test marioGANLevelChunks:6 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_30_Epoch5000.pth GANInputSize:30 printFitness:true trials:1 mu:10 maxGens:500 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNtoGANLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:50 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
	}

}
