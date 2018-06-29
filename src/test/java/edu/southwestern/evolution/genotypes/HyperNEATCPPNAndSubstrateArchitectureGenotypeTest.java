package edu.southwestern.evolution.genotypes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.networks.hyperneat.architecture.CascadeNetworks;
import edu.southwestern.networks.hyperneat.architecture.CascadeTest;
import edu.southwestern.networks.hyperneat.architecture.HiddenSkipsHidden;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.random.RandomNumbers;

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
	public void cascadeEqualityTest() {

		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:false", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:false", "senseTetrisHolesAsPositive:true"});
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
	
	@Test
	public void cascadeTest() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:false", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:true", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);

		HyperNEATCPPNAndSubstrateArchitectureGenotype hncasag = new HyperNEATCPPNAndSubstrateArchitectureGenotype();
		//		System.out.println(hncasag);
		//		MiscUtil.waitForReadStringAndEnterKeyPress();
		hncasag.cascadeExpansion(1, 10, 20, SubstrateConnectivity.CTYPE_CONVOLUTION);
		//		System.out.println(hncasag);
		//		MiscUtil.waitForReadStringAndEnterKeyPress();
		assertEquals(8, hncasag.numOut);
	}

	@Test
	public void behaviorAfterCascadeTest() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		final int PARENT_POPULATION = 10;
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:" + PARENT_POPULATION, "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:false", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:true", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);

		HyperNEATCPPNAndSubstrateArchitectureGenotype hncasag = new HyperNEATCPPNAndSubstrateArchitectureGenotype();
		ArrayList<Genotype<TWEANN>> constantPopulation = PopulationUtil.initialPopulation(hncasag, PARENT_POPULATION);
		ArrayList<TWEANN> originalTWEANNs = new ArrayList<TWEANN>();
		for(Genotype<TWEANN> tg : constantPopulation) {
			originalTWEANNs.add(tg.getPhenotype());
		}
		
		ArrayList<Genotype<TWEANN>> experimentalPopulation = new ArrayList<Genotype<TWEANN>>();
		for(int i = 0; i < constantPopulation.size(); i++) {
			experimentalPopulation.add(constantPopulation.get(i).copy());
		}
		for(Genotype<TWEANN> i : experimentalPopulation) {			
			((HyperNEATCPPNAndSubstrateArchitectureGenotype) i).cascadeExpansion(1, 6, 16, SubstrateConnectivity.CTYPE_CONVOLUTION);
		}
	
		assertEquals(experimentalPopulation.size(), constantPopulation.size());
		int numInputs = constantPopulation.get(0).getPhenotype().numInputs();
		for (int i = 0; i < 50; i++) {
			double[] inputs = RandomNumbers.randomArray(numInputs);
			for (int j = 0; j < constantPopulation.size(); j++) {
				double[] originalOutputs = originalTWEANNs.get(j).process(inputs);
				double[] cascadeOutputs = experimentalPopulation.get(j).getPhenotype().process(inputs);
				
				assertArrayEquals(originalOutputs, cascadeOutputs, 0);
			}
		}
	}
}
