package edu.southwestern.evolution.genotypes;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask;

public class NormalizedNodesHyperNEATCPPNGenotypeTest {
	@Test
	public void testEvolvingGammaAndBeta() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		final int PARENT_POPULATION = 10;
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:" + PARENT_POPULATION, "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.NormalizedNodesHyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:false", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:true", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		NormalizedNodesHyperNEATCPPNGenotype nnhncg = new NormalizedNodesHyperNEATCPPNGenotype();
		nnhncg.mutate();
		ArrayList<Double> mutatedBeta = nnhncg.betaValues.getPhenotype();
		//System.out.println("mutatedBeta: " + mutatedBeta);
		boolean mutatedValueExists = false;
		for(Double i: mutatedBeta) {
			if(i != 1.0) {
				mutatedValueExists = true;
				break;
			}
		}
		assertTrue(mutatedValueExists);
		ArrayList<Double> mutatedGamma = nnhncg.gammaValues.getPhenotype();
		//System.out.println("mutatedGamma: " + mutatedGamma);
		mutatedValueExists = false;
		for(Double i: mutatedGamma) {
			if(i != 0.0) {
				mutatedValueExists = true;
				break;
			}
		}
		assertTrue(mutatedValueExists);
		
	}
}
