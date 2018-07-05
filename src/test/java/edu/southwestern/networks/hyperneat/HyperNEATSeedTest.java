package edu.southwestern.networks.hyperneat;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask;
import edu.southwestern.util.ClassCreation;

public class HyperNEATSeedTest {
	
	@Test
	public void test() throws NoSuchMethodException {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.TetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEATSeedTask:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "HNProcessDepth:1", "netLinkRate:0.0", "netSpliceRate:0.0", "linkExpressionThreshold:-1", "steps:500000", "extraHNLinks:true", "convolution:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		HyperNEATTask HNTSeedTask = (HyperNEATTask) ClassCreation.createObject("hyperNEATSeedTask");
		int numSubstratePairings = HNTSeedTask.getSubstrateConnectivity().size();
		HyperNEATCPPNGenotype hntGeno = new HyperNEATCPPNGenotype(HyperNEATUtil.numCPPNInputs(HNTSeedTask), numSubstratePairings * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair + HyperNEATUtil.numBiasOutputsNeeded(HNTSeedTask), 0);
		TWEANNGenotype seedGeno = hntGeno.getSubstrateGenotypeForEvolution(HNTSeedTask);
		assertEquals(seedGeno.links.size(), 8*18+8*18+8*18*3*3+8*18*3*3+1+10*20+10*20);
	}

}
