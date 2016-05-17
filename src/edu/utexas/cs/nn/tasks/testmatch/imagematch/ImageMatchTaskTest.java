package edu.utexas.cs.nn.tasks.testmatch.imagematch;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
/**
 * JUnit test class for the ImageMatchTask
 * 
 * @author gillespl
 *
 */
public class ImageMatchTaskTest {

	public static final String IMAGE_MATCH_PATH = "data\\imagematch";
	private static final int X_COORDINATE_INDEX = 0;
	private static final int Y_COORDINATE_INDEX = 1;
	private static final int BIAS_IND = 3;
	private static final double BIAS = 1.0;//input to neural network
	public int imageHeight, imageWidth;
	
	ImageMatchTask<TWEANN> test;
	
	/**
	 * Instantiates ImageMatchTask
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false", "includeHalfLinearPiecewiseFunction:true", "includeSawtoothFunction:true"});
		MMNEAT.loadClasses();
		test = new ImageMatchTask<TWEANN>("Tiny.png");
		imageHeight = test.imageHeight;
		imageWidth = test.imageWidth;
	}

	/**
	 * Tests the getter functions of ImageMatchTask
	 */
	@Test
	public void test_getterFunctions() {
		String[] outputLabels = test.outputLabels();
		String[] sensorLabels = test.sensorLabels();
		assertTrue(sensorLabels[0].equals("X-coordinate"));
		assertTrue(outputLabels[0].equals("hue-value"));
		assertTrue(test.numInputs() == 4);
		assertTrue(test.numOutputs() == 3);
	}
	
	/**
	 * Tests proper values are stored in getTrainingPairs method
	 */
	@Test
	public void test_getTrainingPairs() {
		ArrayList<Pair<double[], double[]>> pair = test.getTrainingPairs();
		System.out.print(Arrays.toString(pair.get(pair.size()-1).t1));
		assertEquals(pair.get(pair.size()-1).t1[X_COORDINATE_INDEX], ImageMatchTask.scale(0, imageHeight), .001);
		assertEquals(pair.get(pair.size()-1).t1[Y_COORDINATE_INDEX], ImageMatchTask.scale(0, imageHeight), .001);
		assertEquals(pair.get(pair.size()-1).t1[BIAS_IND], BIAS, .001);
		assertEquals(pair.get(pair.size() -1).t2.length, test.numOutputs(), .001);
	}
	
	/**
	 * Tests the distance calculation is correct. Employs a 
	 * helper method from Tuple2D class
	 */
	@Test
	public void test_distance() {
		ILocated2D distance = new Tuple2D(imageWidth, imageHeight);
		double euclidianDistance = Math.sqrt(Math.pow(imageWidth, 2) + Math.pow(imageHeight, 2));
		assertTrue(euclidianDistance == distance.distance(new Tuple2D(0,0)));
	}

	/**
	 * Tests the scale function works
	 */
	@Test
	public void test_Scale() {
		double[] leftTopCorner = {0,0};
		ImageMatchTask.scale(leftTopCorner, imageWidth, imageHeight);
		double[] leftBottomCorner = {0, imageHeight};
		ImageMatchTask.scale(leftBottomCorner, imageWidth, imageHeight);
		double[] rightTopCorner = {imageWidth, 0};
		ImageMatchTask.scale(rightTopCorner, imageWidth, imageHeight);
		double [] rightBottomCorner = {imageWidth, imageHeight};
		ImageMatchTask.scale(rightBottomCorner, imageWidth, imageHeight);
		double[] center = {imageWidth/2.0, imageHeight/2.0};
		ImageMatchTask.scale(center, imageWidth, imageHeight);
		assertEquals(leftTopCorner[X_COORDINATE_INDEX], -1, .01); 
		assertEquals(leftTopCorner[Y_COORDINATE_INDEX], -1, .01);
		assertEquals(leftBottomCorner[X_COORDINATE_INDEX], -1, .01);
		assertEquals(leftBottomCorner[Y_COORDINATE_INDEX], 1, .01);
		assertEquals(rightTopCorner[X_COORDINATE_INDEX], 1, .01); 
		assertEquals(rightTopCorner[Y_COORDINATE_INDEX], -1, .01);
		assertEquals(rightBottomCorner[X_COORDINATE_INDEX], 1, .01);
		assertEquals(rightBottomCorner[Y_COORDINATE_INDEX], 1, .01);
		assertEquals(center[X_COORDINATE_INDEX], 0, .01); 
		assertEquals(center[Y_COORDINATE_INDEX], 0, .001);

	}
}
