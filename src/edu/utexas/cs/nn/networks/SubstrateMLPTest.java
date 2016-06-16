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
import edu.utexas.cs.nn.util.datastructures.Triple;
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

		Substrate input1 = new Substrate(new Pair<Integer, Integer>(10, 15), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 0, 0), "I_0");
		Substrate input2 = new Substrate(new Pair<Integer, Integer>(20, 7), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(2, 0, 0), "I_1");
		Substrate process01 = new Substrate(new Pair<Integer, Integer>(5, 5), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(-1, 2, 0), "P_0_0");
		Substrate process02 = new Substrate(new Pair<Integer, Integer>(5, 5), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(1, 2, 0), "P_0_1");
		Substrate process03 = new Substrate(new Pair<Integer, Integer>(8, 8), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(3, 2, 0), "P_0_2");
		Substrate process11 = new Substrate(new Pair<Integer, Integer>(2, 3), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 4, 0), "P_1_0");
		Substrate process12 = new Substrate(new Pair<Integer, Integer>(4, 7), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(2, 4, 0), "P_1_1");
		Substrate output1 = new Substrate(new Pair<Integer, Integer>(4, 4), Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(1, 6, 0), "O_0");
		ArrayList<Substrate> subs = new ArrayList<Substrate>();
		subs.add(input1);
		subs.add(input2);
		subs.add(process01);
		subs.add(process02);
		subs.add(process03);
		subs.add(process11);
		subs.add(process12);
		subs.add(output1);
		Pair<String, String> conn1 = new Pair<String, String>("I_0", "P_0_0");
		Pair<String, String> conn2 = new Pair<String, String>("I_1", "P_0_0");
		Pair<String, String> conn3 = new Pair<String, String>("I_0", "P_0_1");
		Pair<String, String> conn4 = new Pair<String, String>("I_1", "P_0_1");
		Pair<String, String> conn5 = new Pair<String, String>("I_0", "P_0_2");
		Pair<String, String> conn6 = new Pair<String, String>("I_1", "P_0_2");
		Pair<String, String> conn7 = new Pair<String, String>("P_0_0", "P_1_0");
		Pair<String, String> conn8 = new Pair<String, String>("P_0_0", "P_1_1");
		Pair<String, String> conn9 = new Pair<String, String>("P_0_1", "P_1_0");
		Pair<String, String> conn10 = new Pair<String, String>("P_0_1", "P_1_1");
		Pair<String, String> conn11 = new Pair<String, String>("P_0_2", "P_1_0");
		Pair<String, String> conn12 = new Pair<String, String>("P_0_2", "P_1_1");
		Pair<String, String> conn13 = new Pair<String, String>("P_1_0", "O_0");
		Pair<String, String> conn14 = new Pair<String, String>("P_1_1", "O_0");
		ArrayList<Pair<String, String>> connections = new ArrayList<Pair<String, String>>();
		connections.add(conn1);
		connections.add(conn2);
		connections.add(conn3);
		connections.add(conn4);
		connections.add(conn5);
		connections.add(conn6);
		connections.add(conn7);
		connections.add(conn8);
		connections.add(conn9);
		connections.add(conn10);
		connections.add(conn11);
		connections.add(conn12);
		connections.add(conn13);
		connections.add(conn14);
		double[] inputs = new double[10 * 15 + 20 * 7];
		for(int i = 0; i < inputs.length; i++) {
			inputs[i] = i;
		}
		mlp = new SubstrateMLP(subs, connections,  hcppn.getCPPN());
		double[] outputs = mlp.process(inputs);
		assertTrue(outputs.length == 16);
	}

}
