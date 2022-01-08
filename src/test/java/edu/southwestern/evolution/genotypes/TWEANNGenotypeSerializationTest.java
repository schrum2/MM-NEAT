package edu.southwestern.evolution.genotypes;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.parameters.Parameters;

public class TWEANNGenotypeSerializationTest {

	private static final String TWEANN_GENOTYPE_TXT = "TWEANNGenotype.txt";

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
	}
	
	@Test 
	public void test_serialization() throws IOException, ClassNotFoundException {
		
		TWEANNGenotype tgBefore = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tgAfter = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(TWEANN_GENOTYPE_TXT);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(tgBefore);
			objectOutputStream.flush();
			objectOutputStream.close();

			FileInputStream fileInputStream = new FileInputStream(TWEANN_GENOTYPE_TXT);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			tgAfter = (TWEANNGenotype) objectInputStream.readObject();
			objectInputStream.close(); 
		} finally {
			File txt = new File(TWEANN_GENOTYPE_TXT);
			if(txt.exists()) {
				//txt.delete();
			}
		}
	    assertEquals(tgBefore, tgAfter);
	    
	    
	}
}
