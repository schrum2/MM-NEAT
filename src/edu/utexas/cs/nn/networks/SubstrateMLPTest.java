package edu.utexas.cs.nn.networks;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
public class SubstrateMLPTest {

	SubstrateMLP mlp;
	HyperNEATCPPNGenotype hcppn;
	HyperNEATDummyTask<?> task;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "hyperNEAT:true", "task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask"});//TODO
		MMNEAT.loadClasses();
		hcppn = new HyperNEATCPPNGenotype();
		task = (HyperNEATDummyTask<?>) MMNEAT.task;
		mlp = new SubstrateMLP(task.getSubstrateInformation(), task.getSubstrateConnectivity(), hcppn.getCPPN());
	}

	@After
	public void tearDown() throws Exception {
		hcppn = null;
		mlp = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void testNumInputs() {
		assertTrue(25 == mlp.numInputs());
	}

	@Test
	public void testNumOutputs() {
		assertTrue(4 == mlp.numOutputs());
	}

	@Test
	public void testProcess() {
		double[] inputs = new double[25];
		for(int i = 0; i < inputs.length; i++) {
			inputs[i] = i;
		}
		System.out.println("inputs: " + Arrays.toString(inputs));
		double[] outputs = mlp.process(inputs);
		System.out.println("outputs: " + Arrays.toString(outputs));
		assertTrue(outputs.length == 4);
	}
	
	@Test
	public void testComplexMLP() {
		try {
			tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "hyperNEAT:true", "task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask"});//TODO
		MMNEAT.loadClasses();
		hcppn = new HyperNEATCPPNGenotype();
		task = (HyperNEATDummyTask<?>) MMNEAT.task;
		
		
		ArrayList<Substrate> subs = new ArrayList<Substrate>();
		ArrayList<Pair<String, String>> connections = new ArrayList<Pair<String, String>>();
		mlp = new SubstrateMLP(subs, connections,  hcppn.getCPPN());
	}

}
