package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANVectorMatrixBuilder;
import edu.southwestern.tasks.zelda.ZeldaDirectGANVectorMatrixBuilder;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class ConvertCPPN2GANtoDirect2GANMutationTest {
	CPPNOrDirectToGANGenotype tg;
	CPPNOrDirectToGANGenotype tg1;
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
		GANProcess.terminateGANProcess();
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testZelda() {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false", "indirectToDirectTransitionRate:1.0", "task:edu.southwestern.tasks.zelda.ZeldaCPPNOrDirectToGANDungeonTask", 
						"zeldaGANUsesOriginalEncoding:false", /*"base:zeldagan", "log:ZeldaGAN-DistTraversed", "saveTo:DistTraversed",*/ 
						"zeldaGANLevelWidthChunks:10", "zeldaGANLevelHeightChunks:10", "zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth",
						"GANInputSize:10", "mating:true", "fs:false"/*, "maxGens:500"*/ });
		MMNEAT.loadClasses();
		tg = new CPPNOrDirectToGANGenotype();
		Network cppn = (Network) tg.getPhenotype();
		//System.out.println(cppn.numInputs());
		double[] inputMultipliers = new double[cppn.numInputs()];
		for(int i = 0;i<cppn.numInputs();i++) {
			inputMultipliers[i] = 1.0;
		}
		
		ZeldaCPPNtoGANVectorMatrixBuilder builder = new ZeldaCPPNtoGANVectorMatrixBuilder(cppn, inputMultipliers);
		int height = Parameters.parameters.integerParameter("cppn2ganHeight");
		int width = Parameters.parameters.integerParameter("cppn2ganWidth");

		//ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder, width, height);
		Pair<double[][][],double[][][]> k = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder, width, height);
		//tg.mutate();
		StringBuilder sb = new StringBuilder();
		//sb.append(this.getId());
		sb.append(" ");
		new ConvertZeldaCPPN2GANtoDirect2GANMutation().go(tg, sb);
		ArrayList<Double> m = (ArrayList<Double>) tg.getPhenotype();
		double[] a = new double[m.size()];
		for(int i = 0;i<m.size();i++) {
			a[i]=m.get(i);
		}
		int segmentLength = (GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables());
		ZeldaDirectGANVectorMatrixBuilder builder1 =  new ZeldaDirectGANVectorMatrixBuilder(a, segmentLength);
		
		
		
		//ZeldaCPPNtoGANVectorMatrixBuilder builder1 = new ZeldaCPPNtoGANVectorMatrixBuilder(cppn1, inputMultipliers);
		
		Pair<double[][][],double[][][]> k1 = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder1, width, height);
		
		assertArrayEquals(k.t1,k1.t1);
		assertArrayEquals(k.t2,k1.t2);

	}
	@SuppressWarnings("unchecked")
	@Test
	public void testMario() {
		
		
		Parameters.initializeParameterCollections(	new String[] {"runNumber:0","indirectToDirectTransitionRate:1.0", "randomSeed:0",
				"marioGANLevelChunks:6", "marioGANUsesOriginalEncoding:false", "marioGANModel:Mario1_Overworld_30_Epoch5000.pth", 
				"GANInputSize:30", "printFitness:true", "trials:1", "mu:10","maxGens:500","io:false","netio:false",
				"genotype:edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype","mating:true","fs:false",
				"task:edu.southwestern.tasks.mario.MarioCPPNOrDirectToGANLevelTask","allowMultipleFunctions:true","ftype:0",
				"netChangeActivationRate:0.3","cleanFrequency:50","recurrency:false","saveInteractiveSelections:false",
				"simplifiedInteractiveInterface:false","saveAllChampions:false","cleanOldNetworks:true","logTWEANNData:false",
				"logMutationAndLineage:false","marioLevelLength:120","marioStuckTimeout:20","watch:false","marioProgressPlusJumpsFitness:true",
				"marioRandomFitness:false","marioLevelMatchFitness:false"});

		MMNEAT.loadClasses();
		tg1 = new CPPNOrDirectToGANGenotype();
		Network cppn = (Network) tg1.getPhenotype();
		double[] longResult = MarioCPPNtoGANLevelBreederTask.createLatentVectorFromCPPN(cppn, ArrayUtil.doubleOnes(cppn.numInputs()), Parameters.parameters.integerParameter("marioGANLevelChunks"));
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		new ConvertMarioCPPN2GANtoDirect2GANMutation().go(tg1, sb); //mutate, then get the genotype again and see what happens.
		ArrayList<Double> m = (ArrayList<Double>) tg1.getPhenotype();
		double [] newResult = new double[m.size()]; //for copying ArrayList<Double> to double[]
		for(int i = 0;i<m.size();i++) {
			newResult[i] = m.get(i);
		}
		assertTrue(Arrays.equals(longResult, newResult));
	}

}
