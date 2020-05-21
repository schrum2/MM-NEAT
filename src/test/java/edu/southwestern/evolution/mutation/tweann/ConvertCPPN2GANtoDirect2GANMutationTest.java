package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANVectorMatrixBuilder;
import edu.southwestern.util.datastructures.Pair;

public class ConvertCPPN2GANtoDirect2GANMutationTest {
	CPPNOrDirectToGANGenotype tg;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false", "indirectToDirectTransitionRate:1.0", "task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask", 
						"zeldaGANUsesOriginalEncoding:false", /*"base:zeldagan", "log:ZeldaGAN-DistTraversed", "saveTo:DistTraversed",*/ 
						"zeldaGANLevelWidthChunks:10", "zeldaGANLevelHeightChunks:10", "zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth",
						"GANInputSize:10", "mating:true"/*, "maxGens:500"*/ });
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
		double[] inputMultipliers = new double[cppn.numInputs()];
		for(int i = 0;i<cppn.numInputs();i++) {
			inputMultipliers[i] = 1.0;
		}
		ZeldaCPPNtoGANVectorMatrixBuilder builder = new ZeldaCPPNtoGANVectorMatrixBuilder(cppn, inputMultipliers);
		int height = Parameters.parameters.integerParameter("cppn2ganHeight");
		int width = Parameters.parameters.integerParameter("cppn2ganWidth");
		//ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder, width, height);
		Pair<double[][][],double[][][]> k = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder, width, height);
		tg.mutate();
		Network cppn1 = (Network) tg.getPhenotype();
		
		ZeldaCPPNtoGANVectorMatrixBuilder builder1 = new ZeldaCPPNtoGANVectorMatrixBuilder(cppn1, inputMultipliers);
		
		Pair<double[][][],double[][][]> k1 = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(builder1, width, height);
		assertEquals(k,k1);
	}

}
