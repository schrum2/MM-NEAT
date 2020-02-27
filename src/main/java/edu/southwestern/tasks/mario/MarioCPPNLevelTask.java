package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ch.idsia.tools.EvaluationInfo;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * Evolve Mario levels with CPPNs using an agent,
 * like the Mario A* Agent, as a means of evaluating
 * 
 * @author Jacob Schrum
 *
 * @param <T> A NN that can generate levels as a CPPN
 */
public class MarioCPPNLevelTask<T extends Network> extends MarioLevelTask<T> {
	// There is space in each level that Mario cannot traverse because it is after the goal post.
	// This constant allows that space to be subtracted out.
	public static final int SPACE_AT_LEVEL_END = 225;
	
	public MarioCPPNLevelTask() {
		super();
	}

	@Override
	public double totalPassableDistance(EvaluationInfo info) {
		double totalDistanceInLevel = info.totalLengthOfLevelPhys;
		return (totalDistanceInLevel - SPACE_AT_LEVEL_END);
	}
	
	/**
	 * Generate the level from a CPPN
	 */
	@Override
	public ArrayList<List<Integer>> getMarioLevelListRepresentationFromGenotype(Genotype<T> individual) {
		Network cppn = individual.getPhenotype();
		String[] stringBlock = MarioLevelUtil.generateLevelLayoutFromCPPN(cppn, ArrayUtil.doubleOnes(cppn.numInputs()), Parameters.parameters.integerParameter("marioLevelLength"));
		
		ArrayList<List<Integer>> level = MarioLevelUtil.listLevelFromStringLevel(stringBlock);
		return level;
	}

	/**
	 * For quick testing
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:0 randomSeed:0 base:mariocppn log:MarioCPPN-Test saveTo:Test printFitness:true trials:1 mu:10 maxGens:500 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:50 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
	}

}
