package edu.southwestern.evolution.genotypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask;
import edu.southwestern.util.stats.StatisticsUtilities;

public class TWEANNGenotypeTest {

	final static int MUTATIONS1 = 10;

	@Before
	public void setup() {
		// Default test params for tests that don't need more specific settings
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false" });
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
	}

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@Test
	public void test_Equals() {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false" });
		MMNEAT.loadClasses();

		TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);

		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);

		TWEANNGenotype check = tg1;
		assertTrue(check.equals(tg1));
		assertFalse(tg1.equals(tg2));
		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}
	}

	@Test
	public void test_sameStructure() {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false" });
		CommonConstants.freezeBeforeModeMutation = true;
		MMNEAT.loadClasses();

		TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);

		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}

		TWEANNGenotype tg1Copy = (TWEANNGenotype) tg1.copy();
		assertTrue(TWEANNGenotype.sameStructure(tg1Copy, tg1));
		assertFalse(TWEANNGenotype.sameStructure(tg1, tg2));
	}

	@Test
	public void test_getLinksBetween() {
		TWEANNGenotype tg1 = new TWEANNGenotype(2, 1, 0);
		LinkGene lg = tg1.getLinkBetween(-1, -3);
		assertTrue(lg != null);
		assertTrue(lg.sourceInnovation == -1);
		assertTrue(lg.targetInnovation == -3);
	}

	@Test
	public void test_biggestInnovation() {
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l); // reset
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false" });
		MMNEAT.loadClasses();
		TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);

		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
		}

//		System.out.println(tg1);
//		System.out.println(EvolutionaryHistory.largestUnusedInnovationNumber);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		ArrayList<NodeGene> tg1Genes = tg1.nodes;
		ArrayList<LinkGene> tg1Links = tg1.links;

		long[] tg1innoGene = new long[tg1Genes.size()];
		long[] tg1innoLink = new long[tg1Links.size()];
		for (int i = 0; i < tg1Genes.size(); i++) {
			tg1innoGene[i] = tg1Genes.get(i).innovation;
		}
		for (int i = 0; i < tg1Links.size(); i++) {
			tg1innoLink[i] = tg1Links.get(i).innovation;
		}
		long trueMaxInnoGene = StatisticsUtilities.maximum(tg1innoGene);
		long trueMaxInnoLink = StatisticsUtilities.maximum(tg1innoLink);
		long trueMaxInno = Math.max(trueMaxInnoGene, trueMaxInnoLink);
		long maxInno = tg1.biggestInnovation();

		assertEquals(trueMaxInno,maxInno); // Failed?
	}
	
	@Test
	public void test_addMSSNeuronsToCPPNThreshold() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:true", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		//HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair for leo. + 2 for bias
		//TWEANNGenotype tg1 = new TWEANNGenotype(4, 3 * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair + 2, 0);
		//MMNEAT.genotype = tg1.copy();
		
		HyperNEATCPPNAndSubstrateArchitectureGenotype hncasag = new HyperNEATCPPNAndSubstrateArchitectureGenotype();
		hncasag.cascadeExpansion(1, 10, 20, SubstrateConnectivity.CTYPE_CONVOLUTION);
		//System.out.println("before 2nd thresh expansion" + hncasag.nodes);
		hncasag.cascadeExpansion(4, 10, 20, SubstrateConnectivity.CTYPE_CONVOLUTION);
		//System.out.println("after 2nd thresh expansion" + hncasag.nodes);
	}
	
	@Test
	public void test_addMSSNeuronsToCPPNLeo() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:true", "senseTetrisHolesAsPositive:true", "leo:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		//HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair for leo. + 2 for bias
		//TWEANNGenotype tg1 = new TWEANNGenotype(4, 3 * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair + 2, 0);
		//MMNEAT.genotype = tg1.copy();
		
		HyperNEATCPPNAndSubstrateArchitectureGenotype hncasag = new HyperNEATCPPNAndSubstrateArchitectureGenotype();
		//System.out.println("before leo expansion" + hncasag.nodes);
		hncasag.cascadeExpansion(1, 10, 20, SubstrateConnectivity.CTYPE_CONVOLUTION);
		//System.out.println("after leo expansion" + hncasag.nodes);

		//System.out.println("before 2nd leo expansion" + hncasag.nodes);
		hncasag.cascadeExpansion(4, 10, 20, SubstrateConnectivity.CTYPE_CONVOLUTION);
		//System.out.println("after 2nd leo expansion" + hncasag.nodes);
	}
	
}
