package edu.utexas.cs.nn.networks;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATSpeedTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.parameters.Parameters;

public class SpeedTest {

	TWEANN tweann;
	SubstrateMLP mlp;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "allowMultipleFunctions:true", "netio:false", "recurrency:false", "hyperNEAT:true", "task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATSpeedTask", "ftype:1"});
		MMNEAT.loadClasses();
		HyperNEATCPPNGenotype hcppn = new HyperNEATCPPNGenotype();
		for(int i = 0; i < 100; i++) {
			hcppn.mutate();
		}
		tweann = hcppn.getPhenotype();
		HyperNEATTask task = (HyperNEATSpeedTask) MMNEAT.task;
		mlp = new SubstrateMLP(task.getSubstrateInformation(), task.getSubstrateConnectivity(), hcppn.getCPPN());
		
	}

	@After
	public void tearDown() throws Exception {
		tweann = null;
		mlp = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void test() {
		double[] inputs = new double[1000];
		for(int i = 0; i < inputs.length; i++) {
			inputs[i] = ((double)i) / inputs.length;
		}
		double mlpStartTime = System.currentTimeMillis();
		double[] mlpout = mlp.process(inputs);
		double mlpEndTime = System.currentTimeMillis();
		double tweannStartTime = System.currentTimeMillis();
		double[] tweannout = tweann.process(inputs);
		double tweannEndTime = System.currentTimeMillis();
		System.out.println("mlp time: " + (mlpEndTime - mlpStartTime));
		System.out.println("tweann time: " + (tweannEndTime - tweannStartTime));
		System.out.println("mlp outputs: " + Arrays.toString(mlpout));
		System.out.println("tweann outputs: " + Arrays.toString(tweannout));
		assertTrue((mlpEndTime - mlpStartTime) > (tweannEndTime - tweannStartTime));
	}

}
