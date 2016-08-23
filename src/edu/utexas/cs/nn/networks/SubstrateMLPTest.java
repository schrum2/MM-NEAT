package edu.utexas.cs.nn.networks;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
public class SubstrateMLPTest {

	SubstrateMLP mlp;
	HyperNEATCPPNGenotype hcppn;
	HyperNEATTask task;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "hyperNEAT:true", "evolveHyperNEATBias:false", "task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask", "ftype:1"});
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
		assertEquals(9, mlp.numInputs());
	}

	@Test
	public void testNumOutputs() {
		assertEquals(8, mlp.numOutputs());
	}

	@Test
	public void testProcess() {
		double[] inputs = new double[9];
		for(double i = 0; i < inputs.length; i++) {
			// All inputs scaled in [0,1]
			inputs[(int) i] = (1+i)/inputs.length;
		}
		mlp.flush();
		double[] outputs = mlp.process(inputs);
		assertEquals(outputs.length, 8);
		TWEANN tweann = hcppn.getPhenotype();
		double[] tweannOut = tweann.process(inputs);
		assertEquals(outputs.length, tweannOut.length);
		for(int i = 0; i < outputs.length; i++) {
			assertEquals(outputs[i], tweannOut[i], .0001);
		}
	}

	public abstract class AggregateHyperNEATTask implements Task, HyperNEATTask {
	}

	//@Test
	public void testComplexMLP() {
		try {
			tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "hyperNEAT:true", "task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask", "ftype:1"});
		MMNEAT.loadClasses();
		MMNEAT.task = new AggregateHyperNEATTask(){

			@Override
			public List<Substrate> getSubstrateInformation() {
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
				return subs;
			}

			@Override
			public List<Pair<String, String>> getSubstrateConnectivity() {
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
				return connections;
			}

			@Override
			public int numObjectives() {
				throw new UnsupportedOperationException("unimplemented method");
			}

			@Override
			public double[] minScores() {
				throw new UnsupportedOperationException("unimplemented method");
			}

			@Override
			public double getTimeStamp() {
				throw new UnsupportedOperationException("unimplemented method");
			}

			@Override
			public void finalCleanup() {
				throw new UnsupportedOperationException("unimplemented method");
			}

		};
		try {
			MMNEAT.hyperNEATOverrides();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}


		double[] inputs = new double[10 * 15 + 20 * 7];
		for(int i = 0; i < inputs.length; i++) {
			inputs[i] = i;
		}

		HyperNEATTask task = (HyperNEATTask) MMNEAT.task;
		hcppn = new HyperNEATCPPNGenotype();
		System.out.println("---------complexMLPtest----------");
		System.out.println("MMNEAT num outputs: " + MMNEAT.networkOutputs);
		mlp = new SubstrateMLP(task.getSubstrateInformation(), task.getSubstrateConnectivity(), hcppn.getCPPN());
		double[] outputs = mlp.process(inputs);
		assertEquals(outputs.length, 16);

		TWEANN tweann = hcppn.getPhenotype();
		System.out.println("TWEANN num out: " + tweann.numOut);
		double[] tweannOut = tweann.process(inputs);
		assertEquals(outputs.length, tweannOut.length);
		for(int i = 0; i < outputs.length; i++) {
			System.out.println("i: " + i);
			assertEquals(outputs[i], tweannOut[i], .0001);
		}
	}

	@Test
	public void valuesTest() { 
		MMNEAT.clearClasses();
		setNewTask();
		HyperNEATTask task = (HyperNEATTask) MMNEAT.task;
		hcppn = new HyperNEATCPPNGenotype();

		System.out.println("---------valuesTest----------");
		mlp = new SubstrateMLP(task.getSubstrateInformation(),task.getSubstrateConnectivity(), hcppn.getCPPN());
		mlp.flush();
		double[] inputs = {0.5};
		double[] networkOutputs = mlp.process(inputs);
		double[][][][] connection1 = mlp.getConnections(0);
		double[][][][] connection2 = mlp.getConnections(1);
		double[] actualOutputs = {ActivationFunctions.activation(CommonConstants.ftype, ActivationFunctions.activation(CommonConstants.ftype, connection1[0][0][0][0]*ActivationFunctions.activation(CommonConstants.ftype, inputs[0]))*connection2[0][0][0][0])};
		System.out.println("network outputs: " + Arrays.toString(networkOutputs));
		System.out.println("actual outputs: " + Arrays.toString(actualOutputs));
		assertEquals(networkOutputs[0], actualOutputs[0], .00000001);
	}

	@Test
	public void tanhSpecificValuesTest()  {
		MMNEAT.clearClasses();
		setNewTask();
		HyperNEATTask task = (HyperNEATTask) MMNEAT.task;
		hcppn = new HyperNEATCPPNGenotype();

		System.out.println("---------tanhValuestest----------");
		mlp = new SubstrateMLP(task.getSubstrateInformation(),task.getSubstrateConnectivity(), hcppn.getCPPN());
		mlp.flush();
		double[] inputs = {0.5};
		double[] networkOutputs = mlp.process(inputs);
		double[][][][] connection1 = mlp.getConnections(0);
		double[][][][] connection2 = mlp.getConnections(1);
		double[] actualOutputs = {Math.tanh(Math.tanh(connection1[0][0][0][0]*Math.tanh(inputs[0]))*connection2[0][0][0][0])};
		System.out.println("network outputs: " + Arrays.toString(networkOutputs));
		System.out.println("actual outputs: " + Arrays.toString(actualOutputs));
		assertEquals(networkOutputs[0], actualOutputs[0], .00000001);
	}

	public void setNewTask()  {
		MMNEAT.task = new AggregateHyperNEATTask(){

			@Override
			public int numObjectives() {
				throw new UnsupportedOperationException("unimplemented method");
			}

			@Override
			public double[] minScores() {
				throw new UnsupportedOperationException("unimplemented method");
			}

			@Override
			public double getTimeStamp() {
				throw new UnsupportedOperationException("unimplemented method");
			}

			@Override
			public void finalCleanup() {
				throw new UnsupportedOperationException("unimplemented method");
			}

			@Override
			public List<Substrate> getSubstrateInformation() {
				Substrate in = new Substrate(new Pair<Integer, Integer>(1, 1), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0,0,0), "I_0");
				Substrate process = new Substrate(new Pair<Integer, Integer>(1, 1), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(1,0,0), "P_0");
				Substrate out = new Substrate(new Pair<Integer, Integer>(1, 1), Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(2,0,0), "O_0");
				ArrayList<Substrate> subs = new ArrayList<Substrate>();
				subs.add(in);
				subs.add(process);
				subs.add(out);
				return subs;
			}

			@Override
			public List<Pair<String, String>> getSubstrateConnectivity() {
				ArrayList<Pair<String, String>> connections = new ArrayList<Pair<String, String>>();
				connections.add(new Pair<String, String>("I_0", "P_0"));
				connections.add(new Pair<String, String>("P_0", "O_0"));
				return connections;
			}
		};
		try {
			MMNEAT.hyperNEATOverrides();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
