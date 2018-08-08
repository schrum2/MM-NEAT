package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;

import ch.idsia.mario.engine.level.Level;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;

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

	public MarioCPPNLevelTask() {
		super();
	}
	
	/**
	 * Generate the level from a CPPN
	 */
	@Override
	public Level getMarioLevelFromGenotype(Genotype<T> individual) {
		Network cppn = individual.getPhenotype();
		Level level = MarioLevelUtil.generateLevelFromCPPN(cppn, Parameters.parameters.integerParameter("marioLevelLength"));
		return level;
	}

	/**
	 * For quick testing
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:0 randomSeed:0 trials:1 mu:10 maxGens:500 io:false netio:false mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:50 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
	}


}
