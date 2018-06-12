package edu.southwestern.networks.hyperneat;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask;
import edu.southwestern.util.datastructures.Triple;

public class FlexibleSubstrateArchitectureTest {

	@Test
	public void testGetHiddenArchitectureD1W1CNN() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:true", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(new Integer(1), test.get(0).t1);
		assertEquals(new Integer(8), test.get(0).t2);
		assertEquals(new Integer(18), test.get(0).t3);

		MMNEAT.clearClasses();
	}

	@Test
	public void testGetHiddenArchitectureD2W1CNN() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:2", "HNProcessWidth:1", "convolution:true", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(test.size(), 2);
		assertEquals(test.get(0).t1, new Integer(1));
		assertEquals(new Integer(8), test.get(0).t2);
		assertEquals(new Integer(18), test.get(0).t3);
		assertEquals(new Integer(1), test.get(1).t1);
		assertEquals(new Integer(6), test.get(1).t2);
		assertEquals(new Integer(16), test.get(1).t3);

		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetHiddenArchitectureD1W2CNN() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:1", "HNProcessWidth:2", "convolution:true", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		System.out.print("here: " + test.size());
		assertEquals(test.size(), 1);
		assertEquals(test.get(0).t1, new Integer(2));
		assertEquals(new Integer(8), test.get(0).t2);
		assertEquals(new Integer(18), test.get(0).t3);

		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetHiddenArchitectureD2W2CNN() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:2", "HNProcessWidth:2", "convolution:true", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(test.size(), 2);
		assertEquals(new Integer(2), test.get(0).t1);
		assertEquals(new Integer(8), test.get(0).t2);
		assertEquals(new Integer(18), test.get(0).t3);
		assertEquals(new Integer(2), test.get(1).t1);
		assertEquals(new Integer(6), test.get(1).t2);
		assertEquals(new Integer(16), test.get(1).t3);

		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetHiddenArchitectureD1W1() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:1", "HNProcessWidth:1", "convolution:false", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(test.size(), 1);
		assertEquals(new Integer(1), test.get(0).t1);
		assertEquals(new Integer(10), test.get(0).t2);
		assertEquals(new Integer(20), test.get(0).t3);

		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetHiddenArchitectureD2W2() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:2", "HNProcessWidth:2", "convolution:false", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(new Integer(2), test.get(0).t1);
		assertEquals(new Integer(10), test.get(0).t2);
		assertEquals(new Integer(20), test.get(0).t3);
		assertEquals(new Integer(2), test.get(1).t1);
		assertEquals(new Integer(10), test.get(1).t2);
		assertEquals(new Integer(20), test.get(1).t3);

		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetHiddenArchitectureD4W4() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:4", "HNProcessWidth:4", "convolution:false", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(test.size(), 4);
		assertEquals(new Integer(4), test.get(0).t1);
		assertEquals(new Integer(10), test.get(0).t2);
		assertEquals(new Integer(20), test.get(0).t3);
		assertEquals(new Integer(4), test.get(1).t1);
		assertEquals(new Integer(10), test.get(1).t2);
		assertEquals(new Integer(20), test.get(1).t3);
		assertEquals(new Integer(4), test.get(2).t1);
		assertEquals(new Integer(10), test.get(2).t2);
		assertEquals(new Integer(20), test.get(2).t3);
		assertEquals(new Integer(4), test.get(3).t1);
		assertEquals(new Integer(10), test.get(3).t2);
		assertEquals(new Integer(20), test.get(3).t3);
		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetHiddenArchitectureD2W4Rec5x5() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:2", "HNProcessWidth:4", "convolution:true", "senseTetrisHolesAsPositive:true", "receptiveFieldHeight:5", "receptiveFieldWidth:5"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;

		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(test.size(),  2);
		assertEquals(new Integer(4), test.get(0).t1);
		assertEquals(new Integer(6), test.get(0).t2);
		assertEquals(new Integer(16), test.get(0).t3);
		assertEquals(new Integer(4), test.get(1).t1);
		assertEquals(new Integer(2), test.get(1).t2);
		assertEquals(new Integer(12), test.get(1).t3);

		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetHiddenArchitectureCNND4W4() {
		MMNEAT.clearClasses();
		HyperNEATTetrisTask.hardSubstrateReset();
		EvolutionaryHistory.archetypes = null;
		EvolutionaryHistory.setInnovation(0l);
		Parameters.initializeParameterCollections(new String[] {"runNumber:1", "randomSeed:1", "trials:3", "maxGens:500", "mu:50", "io:false", "netio:false", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",  "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false",  "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "hyperNEAT:true", "genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype", "allowMultipleFunctions:true", "ftype:1", "netChangeActivationRate:0.3", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "steps:500000", "perLinkMutateRate:0.05", "netLinkRate:0.4", "netSpliceRate:0.2", "crossoverRate:0.5", "extraHNLinks:true", "HNProcessDepth:4", "HNProcessWidth:4", "convolution:true", "tetrisAllowLine:false", "tetrisAllowSquare:false", "tetrisAllowTri:false", "tetrisAllowLShape:false", "tetrisAllowJShape:false", "senseTetrisHolesAsPositive:true"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);
		
		HyperNEATTask tetris = (HyperNEATTask) MMNEAT.task;
		List<Triple<Integer, Integer, Integer>> test = FlexibleSubstrateArchitecture.getHiddenArchitecture(tetris);
		assertEquals(test.size(), 4);
		assertEquals(new Integer(4), test.get(0).t1);
		assertEquals(new Integer(8), test.get(0).t2);
		assertEquals(new Integer(18), test.get(0).t3);
		assertEquals(new Integer(4), test.get(1).t1);
		assertEquals(new Integer(6), test.get(1).t2);
		assertEquals(new Integer(16), test.get(1).t3);
		assertEquals(new Integer(4), test.get(2).t1);
		assertEquals(new Integer(4), test.get(2).t2);
		assertEquals(new Integer(14), test.get(2).t3);
		assertEquals(new Integer(4), test.get(3).t1);
		assertEquals(new Integer(2), test.get(3).t2);
		assertEquals(new Integer(12), test.get(3).t3);
		MMNEAT.clearClasses();
	}
}
