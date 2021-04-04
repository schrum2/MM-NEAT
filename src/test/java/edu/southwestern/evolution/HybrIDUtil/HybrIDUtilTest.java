package edu.southwestern.evolution.HybrIDUtil;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;

public class HybrIDUtilTest {

	@Test
	public void dummyTest() {
		MMNEAT.clearClasses();
		Parameters.parameters = null;
		final int PARENT_POPULATION = 10;
		Parameters.initializeParameterCollections(new String[] { "io:false", "randomSeed:0" , "netio:false", "recurrency:false", "hyperNEAT:true", "mu:"+PARENT_POPULATION,
				"task:edu.southwestern.networks.hyperneat.HyperNEATDummyTask","linkExpressionThreshold:-1","hybrID:true","genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype","evolveHyperNEATBias:true"});
		TWEANNGenotype.smallerGenotypes = false;
		MMNEAT.loadClasses();
		HyperNEATCPPNGenotype example = new HyperNEATCPPNGenotype();
		assertFalse(TWEANNGenotype.smallerGenotypes);
		ArrayList<Genotype<TWEANN>> preSwitchPopulation = PopulationUtil.initialPopulation(example, PARENT_POPULATION);

		ArrayList<TWEANN> preSwitchPhenotypes = new ArrayList<TWEANN>();
		for(Genotype<TWEANN> tg : preSwitchPopulation) {
			preSwitchPhenotypes.add(tg.getPhenotype()); // These phenotypes will have non-zero bias values
		}

		ArrayList<Genotype<TWEANN>> postSwitchPopulation = HybrIDUtil.switchPhenotypeToNEAT(preSwitchPopulation);
		assertTrue(TWEANNGenotype.smallerGenotypes);

		//		System.out.println("After HybrID switch");
		//		System.out.println("Substrate:" + postSwitchPopulation.get(0).getPhenotype());

		// Make sure at least one bias in the new substrate networks is non-zero
		TWEANN first = postSwitchPopulation.get(0).getPhenotype();
		int nonZeroBiasCount = 0;
		for(TWEANN.Node n :  first.nodes) {
			if(n.bias != 0.0) {
				nonZeroBiasCount++;
			}
		}
		assertTrue(nonZeroBiasCount > 0);


		assertEquals(preSwitchPopulation.size(), postSwitchPopulation.size());
		for (int i = 0; i < preSwitchPopulation.size(); i++) {
			//Genotype<TWEANN> cppn = preSwitchPopulation.get(i);
			//Genotype<TWEANN> tweann = postSwitchPopulation.get(i);
			TWEANN preSwitchNN = preSwitchPhenotypes.get(i);
			TWEANN postSwitchNN = postSwitchPopulation.get(i).getPhenotype();
			//assertTrue(preSwitchNN.equals(postSwitchNN));			
			for (int j = 0; j < PARENT_POPULATION; j++) {
				double[] inputs = RandomNumbers.randomArray(preSwitchNN.numInputs());
				double[] output1 = preSwitchNN.process(inputs);
				double[] output2 = postSwitchNN.process(inputs);		
				assertEquals(output1.length, output2.length);
				for (int k = 0; k < output1.length; k++) {
					assertEquals(output1[k], output2[k], 0);
				}
			}
		}
	}

	@Test
	public void shortTest() {
		MMNEAT.clearClasses();
		Parameters.parameters = null;
		final int PARENT_POPULATION = 10;
		Parameters.initializeParameterCollections(new String[] {"io:false", "netio:false","task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "mu:"+PARENT_POPULATION,
				"rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  
				"rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", 
				"allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", 
				"netSpliceRate:0.2", "crossoverRate:0.5", "log:Tetris-ShortTest", "saveTo:ShortTest", "extraHNLinks:true", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:false",  "hybrIDSwitchGeneration:5", "hybrID:true","evolveHyperNEATBias:true", 
				"tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false"});
		MMNEAT.loadClasses();
		HyperNEATCPPNGenotype example = new HyperNEATCPPNGenotype();
		ArrayList<Genotype<TWEANN>> preSwitchPopulation = PopulationUtil.initialPopulation(example, PARENT_POPULATION);
		ArrayList<TWEANN> preSwitchPhenotypes = new ArrayList<TWEANN>();
		ArrayList<Genotype<TWEANN>> postSwitchPopulation = HybrIDUtil.switchPhenotypeToNEAT(preSwitchPopulation);
		assertEquals(preSwitchPopulation.size(), postSwitchPopulation.size());
		
		for(Genotype<TWEANN> tg : preSwitchPopulation) {
			preSwitchPhenotypes.add(tg.getPhenotype()); // These phenotypes will have non-zero bias values
		}
		assertTrue(TWEANNGenotype.smallerGenotypes);

		// Make sure at least one bias in the new substrate networks is non-zero
		TWEANN first = postSwitchPopulation.get(0).getPhenotype();
		int nonZeroBiasCount = 0;
		for(TWEANN.Node n :  first.nodes) {
			if(n.bias != 0.0) {
				nonZeroBiasCount++;
			}
		}
		

		assertTrue(nonZeroBiasCount > 0);
		for (int i = 0; i < preSwitchPopulation.size(); i++) {
			TWEANN preSwitchNN = preSwitchPopulation.get(i).getPhenotype();
			TWEANN postSwitchNN = postSwitchPopulation.get(i).getPhenotype();
			for (int j = 0; j < PARENT_POPULATION; j++) {
				double[] inputs = RandomNumbers.randomArray(preSwitchNN.numInputs());
				double[] output1 = preSwitchNN.process(inputs);
				double[] output2 = postSwitchNN.process(inputs);		
				assertEquals(output1.length, output2.length);
				for (int k = 0; k < output1.length; k++) {
					assertEquals(output1[k], output2[k], 0);
				}
			}
		}
	}
	//test maven compile
	@Test
	public void cnnTest() {
		MMNEAT.clearClasses();
		Parameters.parameters = null;
		final int PARENT_POPULATION = 10;
		Parameters.initializeParameterCollections(new String[] {"io:false", "netio:false", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "mu:"+PARENT_POPULATION,
				"rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  
				"rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", 
				"allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", 
				"netSpliceRate:0.2", "crossoverRate:0.5", "log:Tetris-ShortTest", "saveTo:ShortTest", "extraHNLinks:true", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:true",  "hybrIDSwitchGeneration:5", "hybrID:tru", 
				"tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false","evolveHyperNEATBias:true"});
		MMNEAT.loadClasses();
		HyperNEATCPPNGenotype example = new HyperNEATCPPNGenotype();
		ArrayList<Genotype<TWEANN>> preSwitchPopulation = PopulationUtil.initialPopulation(example, PARENT_POPULATION);
		ArrayList<Genotype<TWEANN>> postSwitchPopulation = HybrIDUtil.switchPhenotypeToNEAT(preSwitchPopulation);

		ArrayList<TWEANN> preSwitchPhenotypes = new ArrayList<TWEANN>();
		for(Genotype<TWEANN> tg : preSwitchPopulation) {
			preSwitchPhenotypes.add(tg.getPhenotype()); // These phenotypes will have non-zero bias values
		}
		assertTrue(TWEANNGenotype.smallerGenotypes);
		
		// Make sure at least one bias in the new substrate networks is non-zero
		TWEANN first = postSwitchPopulation.get(0).getPhenotype();
		int nonZeroBiasCount = 0;
		for(TWEANN.Node n :  first.nodes) {
			if(n.bias != 0.0) {
				nonZeroBiasCount++;
			}
		}
		assertTrue(nonZeroBiasCount > 0);
		
		assertEquals(preSwitchPopulation.size(), postSwitchPopulation.size());
		for (int i = 0; i < preSwitchPopulation.size(); i++) {
			TWEANN preSwitchNN = preSwitchPopulation.get(i).getPhenotype();
			TWEANN postSwitchNN = postSwitchPopulation.get(i).getPhenotype();
			for (int j = 0; j < PARENT_POPULATION; j++) {
				double[] inputs = RandomNumbers.randomArray(preSwitchNN.numInputs());
				double[] output1 = preSwitchNN.process(inputs);
				double[] output2 = postSwitchNN.process(inputs);		
				assertEquals(output1.length, output2.length);
				for (int k = 0; k < output1.length; k++) {
					assertEquals(output1[k], output2[k], 0);
				}
			}
		}
	}
}
