package edu.southwestern.evolution.genotypes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.networks.hyperneat.architecture.HiddenSkipsHidden;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask;
import edu.southwestern.util.datastructures.Triple;

public class HyperNEATCPPNAndSubstrateArchitectureGenotypeTest {
	
	@Before
	public void setUp() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
	}

	@After
	public void tearDown() {
		MMNEAT.clearClasses();
	}
	
	@Test
	public void nonCNNWithExtraTest() {
		
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HiddenSkipsHidden hsh = new HiddenSkipsHidden();
		HyperNEATTask hshTask = ((HyperNEATTask) MMNEAT.task);

		HyperNEATCPPNAndSubstrateArchitectureGenotype hncasag = new HyperNEATCPPNAndSubstrateArchitectureGenotype();
		hncasag.cascadeExpansion(1, 10, 20, SubstrateConnectivity.CTYPE_CONVOLUTION);
		
		//check hidden architecture equality
		Iterator<Triple<Integer, Integer, Integer>> it_hsh = hsh.getNetworkHiddenArchitecture().iterator();
		assertEquals(hsh.getNetworkHiddenArchitecture().size(), hncasag.hiddenArchitecture.size());
		for(Triple<Integer, Integer, Integer> substrate: hncasag.hiddenArchitecture) {
			assertEquals(substrate, it_hsh.next());
		}
		
		//check connectivity set equality
		List<SubstrateConnectivity> hshConnectivity = hsh.getSubstrateConnectivity(hshTask);
		assertEquals(hshConnectivity.size(), hncasag.allSubstrateConnectivity.size());
		for(SubstrateConnectivity sub: hshConnectivity) {
			assertTrue(hncasag.allSubstrateConnectivity.contains(sub));
		}
	}
}
