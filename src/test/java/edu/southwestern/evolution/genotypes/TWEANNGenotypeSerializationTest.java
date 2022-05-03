package edu.southwestern.evolution.genotypes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.random.GaussianGenerator;

public class TWEANNGenotypeSerializationTest {

	private static final String TWEANN_GENOTYPE = "TWEANNGenotypeTest";
	private static final String ARCHETYPE_NAME = "TestArchetype";
	private static final int GENS = 20;
	private static final int POP_SIZE = 10;

	@Before
	public void setup() {
		// Default test params for tests that don't need more specific settings
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "randomSeed:0" });
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
	}

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
		File f = new File(ARCHETYPE_NAME+".xml");
		if(f.exists()) {
			f.delete();
		}
	}
	
	@Test 
	public void test_serialization() throws IOException, ClassNotFoundException {
		
		TWEANNGenotype tgBefore = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tgAfter = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(TWEANN_GENOTYPE+"1");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(tgBefore);
			objectOutputStream.flush();
			objectOutputStream.close();

			FileInputStream fileInputStream = new FileInputStream(TWEANN_GENOTYPE+"1");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			tgAfter = (TWEANNGenotype) objectInputStream.readObject();
			objectInputStream.close(); 
		} finally {
			File ser = new File(TWEANN_GENOTYPE+"1");
			if(ser.exists()) {
				ser.delete();
			}
		}
	    assertEquals(tgBefore, tgAfter);
	    
	    
	}
	
	@Test 
	public void test_serialization2() throws IOException, ClassNotFoundException {
		
		TWEANNGenotype tgBefore = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tgAfter = null;
		try {
			Serialization.save(tgBefore, TWEANN_GENOTYPE+"2");
			tgAfter = (TWEANNGenotype) Serialization.load(TWEANN_GENOTYPE+"2");
		} finally {
			File ser = new File(TWEANN_GENOTYPE+"2." + (Parameters.parameters.booleanParameter("useWoxSerialization") ? "xml" : "ser"));
			if(ser.exists()) {
				ser.delete();
			}
		}
	    assertEquals(tgBefore, tgAfter);   
	}
	
	@Test
	public void test_archetype_serialization() {
		MMNEAT.weightPerturber = new GaussianGenerator();
		MMNEAT.networkInputs = 5; 
		MMNEAT.networkOutputs = 2;
		
		MMNEAT.genotype = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		EvolutionaryHistory.initArchetype(0);		
		assertEquals(1, EvolutionaryHistory.archetypes.length);
		int originalSize = EvolutionaryHistory.archetypes[0].size();
		assertTrue(originalSize > 0);

		@SuppressWarnings("unchecked")
		ArrayList<Genotype<TWEANN>> pop = PopulationUtil.initialPopulation(MMNEAT.genotype, POP_SIZE);
		//System.out.println(MMNEAT.genotype);
		//System.out.println(pop.get(0));
		for (int i = 0; i < GENS; i++) {
			for(Genotype<TWEANN> tg : pop) {
				tg.mutate();
			}
		}
		int sizeAfterMutations = EvolutionaryHistory.archetypes[0].size();
		assertNotEquals(originalSize, sizeAfterMutations);
		
		Serialization.save(EvolutionaryHistory.archetypes, ARCHETYPE_NAME);
		@SuppressWarnings("unchecked")
		ArrayList<NodeGene>[] newArchetypes = (ArrayList<NodeGene>[]) Serialization.load(ARCHETYPE_NAME);
		
		assertArrayEquals(newArchetypes, EvolutionaryHistory.archetypes);
		
		System.out.println(EvolutionaryHistory.archetypes[0]);
	}
}
