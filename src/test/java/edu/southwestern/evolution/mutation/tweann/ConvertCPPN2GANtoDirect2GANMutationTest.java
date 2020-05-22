package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANVectorMatrixBuilder;
import edu.southwestern.tasks.zelda.ZeldaDirectGANVectorMatrixBuilder;
import edu.southwestern.util.datastructures.Pair;

public class ConvertCPPN2GANtoDirect2GANMutationTest {
	CPPNOrDirectToGANGenotype tg;
	CPPNOrDirectToGANGenotype tg1;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false", "indirectToDirectTransitionRate:1.0", "task:edu.southwestern.tasks.zelda.ZeldaCPPNOrDirectToGANDungeonTask", 
						"zeldaGANUsesOriginalEncoding:false", /*"base:zeldagan", "log:ZeldaGAN-DistTraversed", "saveTo:DistTraversed",*/ 
						"zeldaGANLevelWidthChunks:10", "zeldaGANLevelHeightChunks:10", "zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth",
						"GANInputSize:10", "mating:true", "fs:false"/*, "maxGens:500"*/ });
		MMNEAT.loadClasses();
	}

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	@Test
	public void test() {
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
		System.out.println();
		System.out.println();

		System.out.println();

		System.out.println("Height and width: "+height+","+width);
		//ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder, width, height);
		Pair<double[][][],double[][][]> k = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder, width, height);
		tg.mutate();
		ArrayList<Double> m = (ArrayList<Double>) tg.getPhenotype();
		double[] a = new double[m.size()];
		for(int i = 0;i<m.size();i++) {
			a[i]=m.get(i);
		}
		int segmentLength = (GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables());
		ZeldaDirectGANVectorMatrixBuilder builder1 =  new ZeldaDirectGANVectorMatrixBuilder(a, segmentLength);
		
		
		
		//ZeldaCPPNtoGANVectorMatrixBuilder builder1 = new ZeldaCPPNtoGANVectorMatrixBuilder(cppn1, inputMultipliers);
		
		Pair<double[][][],double[][][]> k1 = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder1, width, height);
		System.out.println("here it comes");
		for(double[][] o : k1.t2) {
			for(double[] p : o) {
				for(double l : p) {
					System.out.print(l+", ");
				}
			}
		}
		System.out.println();
		System.out.println("here it comes2");
		for(double[][] o : k.t2) {
			for(double[] p : o) {
				for(double l : p) {
					System.out.print(l+", ");
				}
			}
		}
		System.out.println();

		assertEquals(k,k1);
	}

}
