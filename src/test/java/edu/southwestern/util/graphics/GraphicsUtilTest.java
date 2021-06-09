package edu.southwestern.util.graphics;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.random.RandomNumbers;

public class GraphicsUtilTest {

	BufferedImage checkeredBlackAndWhite;
	
	public static final int SIDE_LENGTH = 16;
	
	TWEANNGenotype tg1;

	@Before
	public void setUp() throws Exception {
		
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false"});
		MMNEAT.loadClasses();
		RandomNumbers.reset(50);
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		for(int i = 0; i < 10; i++) {
			tg1.mutate();
		}
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		
		// An image with black and white checkered pixels
		checkeredBlackAndWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);

		// Image colors
		int blackRGB = Color.BLACK.getRGB();
		int whiteRGB = Color.WHITE.getRGB();

		// Set every other pixel to black starting on the first row, first pixel
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, blackRGB);
			}
		}

		// Set every other pixel to be white starting on the first row, second pixel
		for(int x = 1; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, whiteRGB);
			}
		}

		// Set every other pixel to black starting on the second row, second pixel
		for(int x = 1; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 1; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, blackRGB);
			}
		}

		// Set every other pixel to white starting on the second row, first pixel
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 1; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, whiteRGB);
			}
		}	
//		GraphicsUtil.drawImage(checkeredBlackAndWhite, "checkered", SIDE_LENGTH, SIDE_LENGTH);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
	}

//	@Test
//	public void testGetConfiguration() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveImage() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testImageFromCPPNNetworkIntInt() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testImageFromCPPNNetworkIntIntDoubleArray() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testImageFromCPPNNetworkIntIntDoubleArrayDouble() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testImageFromCPPNNetworkIntIntDoubleArrayDoubleDoubleDouble() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testZentangleImagesBufferedImageBufferedImageBufferedImage() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testZentangleImagesBufferedImageBufferedImageBufferedImageBufferedImageBufferedImage() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testZentangleImagesBufferedImageBufferedImageBufferedImageBufferedImageBufferedImageBufferedImage() {
//		//fail("Not yet implemented");
//	}
//
//	// Make random CPPN using tg1
//	@Test
//	public void testRemixedImageFromCPPN() {
//		//fail("Not yet implemented");
//		
//	}

	@Test
	public void testGetBrightnessFromImage() {
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x++) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y++) {
				float firstPixel = GraphicsUtil.getBrightnessFromImage(checkeredBlackAndWhite, x, y);
				if(x % 2 == y % 2) {
					assertEquals(0.0, firstPixel, 0);
				} else if (x % 2 != 0 && y % 2 == 0){
					assertEquals(1.0, firstPixel, 0);
				} else if (x % 2 == 0 && y % 2 != 0) {
					assertEquals(1.0, firstPixel, 0);
				} else {
					assertEquals(0.0, firstPixel, 0);
				}
			}
		}
	}
//
//	@Test
//	public void testGetHSBFromCPPN() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testRangeRestrictHSB() {
//		//fail("Not yet implemented");
//	}
//
	// AssertArrayEquals
	// Write individual tests for a variety of coordinates not in a loop
	@Test
	public void testGet2DObjectCPPNInputsIntIntIntIntDouble() {
		//fail("Not yet implemented");
//		for(int x = 0; x < SIDE_LENGTH; x++) {
//			for(int y = 0; y < SIDE_LENGTH; y++) {
//				double[] cppnInputs = GraphicsUtil.get2DObjectCPPNInputs(x, y, SIDE_LENGTH, SIDE_LENGTH, 0);
//				assertArrayEquals(new double[] {-1.0, -1.0, 2.0, 0.0, 1.0}, cppnInputs, 0.000000000001);
//			}
//		}
		
		double[] cppnInputs = GraphicsUtil.get2DObjectCPPNInputs(0, 0, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {-1.0, -1.0, 2.0, 0.0, 1.0}, cppnInputs, 0.000000000000001);
		
		double[] cppnInputs1 = GraphicsUtil.get2DObjectCPPNInputs(SIDE_LENGTH, 0, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {1.1333333333333333, -1.0, 2.13749593891752, 0.0, 1.0}, cppnInputs1, 0.000000000000001);
		
		double[] cppnInputs2 = GraphicsUtil.get2DObjectCPPNInputs(SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {1.1333333333333333, 1.1333333333333333, 2.2666666666666666, 0.0, 1.0}, cppnInputs2, 0.000000000000001);
		
		double[] cppnInputs3 = GraphicsUtil.get2DObjectCPPNInputs(0, SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {-1.0, 1.1333333333333333, 2.13749593891752, 0.0, 1.0}, cppnInputs3, 0.000000000000001);
		
		double[] cppnInputs4 = GraphicsUtil.get2DObjectCPPNInputs(SIDE_LENGTH / 2, SIDE_LENGTH / 2, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {.06666666666666665, 0.06666666666666665, 0.1333333333333333, 0.0, 1.0}, cppnInputs4, 0.000000000000001);
		
		double[] cppnInputs5 = GraphicsUtil.get2DObjectCPPNInputs(12, 9, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {.6000000000000001, 0.1999999999999996, 0.894427190999916, 0.0, 1.0}, cppnInputs5, 0.000000000000001);
		
		double[] cppnInputs6 = GraphicsUtil.get2DObjectCPPNInputs(0, 11, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {-1.0, 0.4666666666666656, 1.5606266547626166, 0.0, 1.0}, cppnInputs6, 0.000000000000001);
		
		double[] cppnInputs7 = GraphicsUtil.get2DObjectCPPNInputs(7, 0, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {-0.06666666666666665, -1.0, 1.4173527750312869, 0.0, 1.0}, cppnInputs7, 0.000000000000001);
	
		double[] cppnInputs8 = GraphicsUtil.get2DObjectCPPNInputs(5, 7, SIDE_LENGTH, SIDE_LENGTH, 0);
		assertArrayEquals(new double[] {-0.33333333333333337, -0.06666666666666665, 0.4807401700618653, 0.0, 1.0}, cppnInputs8, 0.000000000000001);
	}

	@Test
	public void testGet2DObjectCPPNInputsIntIntIntIntDoubleDoubleDouble() {
		//fail("Not yet implemented");
//		for(int x = 0; x < SIDE_LENGTH; x++) {
//			for(int y = 0; y < SIDE_LENGTH; y++) {
//				double[] cppnInputs = GraphicsUtil.get2DObjectCPPNInputs(x, y, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0);
//				assertArrayEquals(new double[] {-1.0, -1.0, 2.0, 0.0, 1.0}, cppnInputs, 0.000000000001);
//			}
//		}
		
		double[] cppnInputs = GraphicsUtil.get2DObjectCPPNInputs(0, 0, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {-1.0, -1.0, 2.0, 0.0, 1.0}, cppnInputs, 0.000000000000001);
		
		double[] cppnInputs1 = GraphicsUtil.get2DObjectCPPNInputs(SIDE_LENGTH, 0, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {1.1333333333333333, -1.0, 2.13749593891752, 0.0, 1.0}, cppnInputs1, 0.000000000000001);
		
		double[] cppnInputs2 = GraphicsUtil.get2DObjectCPPNInputs(SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {1.1333333333333333, 1.1333333333333333, 2.2666666666666666, 0.0, 1.0}, cppnInputs2, 0.000000000000001);
		
		double[] cppnInputs3 = GraphicsUtil.get2DObjectCPPNInputs(0, SIDE_LENGTH, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {-1.0, 1.1333333333333333, 2.13749593891752, 0.0, 1.0}, cppnInputs3, 0.000000000000001);
		
		double[] cppnInputs4 = GraphicsUtil.get2DObjectCPPNInputs(SIDE_LENGTH / 2, SIDE_LENGTH / 2, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {.06666666666666665, 0.06666666666666665, 0.1333333333333333, 0.0, 1.0}, cppnInputs4, 0.000000000000001);
		
		double[] cppnInputs5 = GraphicsUtil.get2DObjectCPPNInputs(12, 9, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {.6000000000000001, 0.1999999999999996, 0.894427190999916, 0.0, 1.0}, cppnInputs5, 0.000000000000001);
		
		double[] cppnInputs6 = GraphicsUtil.get2DObjectCPPNInputs(0, 11, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {-1.0, 0.4666666666666656, 1.5606266547626166, 0.0, 1.0}, cppnInputs6, 0.000000000000001);
		
		double[] cppnInputs7 = GraphicsUtil.get2DObjectCPPNInputs(7, 0, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {-0.06666666666666665, -1.0, 1.4173527750312869, 0.0, 1.0}, cppnInputs7, 0.000000000000001);
	
		double[] cppnInputs8 = GraphicsUtil.get2DObjectCPPNInputs(5, 7, SIDE_LENGTH, SIDE_LENGTH, 0, 1.0, 0.0);
		assertArrayEquals(new double[] {-0.33333333333333337, -0.06666666666666665, 0.4807401700618653, 0.0, 1.0}, cppnInputs8, 0.000000000000001);
	
	}

	// Not testing things involving drawing panels
//	@Test
//	public void testDrawImage() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testSolidColorImage() {
		BufferedImage allWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		int white = Color.WHITE.getRGB();
		for(int x = 0; x < allWhite.getWidth(); x++) {
			for(int y = 0; y < allWhite.getHeight(); y++) {
				allWhite.setRGB(x, y, white);
				assertEquals(allWhite.getRGB(x, y), GraphicsUtil.solidColorImage(Color.WHITE, SIDE_LENGTH, SIDE_LENGTH).getRGB(x, y));
			}
		}
	}

	@Test
	public void testFlatFeatureArrayFromBufferedImage() {
		double[] results = GraphicsUtil.flatFeatureArrayFromBufferedImage(checkeredBlackAndWhite);
		assertArrayEquals(new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
										0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,}, results, 0);
	}

	@Test
	public void testGetHSB() {
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x++) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y++) {
				float[] checkeredHSB = GraphicsUtil.getHSB(checkeredBlackAndWhite, x, y);
				//int count = y * checkeredBlackAndWhite.getWidth() + x;
				//System.out.println(x + "," + y + ":" + Arrays.toString(checkeredHSB) + "," + count);
				if(x % 2 == y % 2) {
					assertArrayEquals(new float[]{0.0f, 0.0f, 0.0f}, checkeredHSB, 0);
				} else {
					assertArrayEquals(new float[]{0.0f, 0.0f, 1.0f}, checkeredHSB, 0);
				}
			}
		}
	}
//
//	@Test
//	public void testImageFromINDArray() {
//		//fail("Not yet implemented");
//	}

	// Not testing things involving drawing panels
//	@Test
//	public void testLinePlotDrawingPanelDoubleDoubleArrayListOfDoubleColor() {
//		fail("Not yet implemented");
//	}
//
	// Not testing things involving drawing panels
//	@Test
//	public void testLinePlotImage() {
//		fail("Not yet implemented");
//	}
//
	// Not testing things involving drawing panels
//	@Test
//	public void testLinePlotGraphicsDoubleDoubleIntIntArrayListOfDoubleColor() {
//		fail("Not yet implemented");
//	}

	// Currently too hard to test
//	@Test
//	public void testWavePlotFromFile() {
//		fail("Not yet implemented");
//	}
//
	// Currently too hard to test
//	@Test
//	public void testWavePlotFromDoubleArray() {
//		fail("Not yet implemented");
//	}
//
//	// hard code numbers, assert with numeric result
//	// far left, far right, center
//	@Test
//	public void testScaleDoubleDoubleDouble() {
//		//fail("Not yet implemented");
//	}
//
//	// hard code numbers, assert with numeric result
//	// far left, far right, center
//	@Test
//	public void testScaleDoubleDoubleDoubleInt() {
//		//fail("Not yet implemented");
//	}
//
//	// hard code numbers, assert with numeric result
//	@Test
//	public void testInvertDoubleDoubleDouble() {
//		//fail("Not yet implemented");
//	}

//	@Test
//	public void testInvertDoubleDoubleDoubleInt() {
//		assertEquals(-24, GraphicsUtil.invert(5, 50, 5, SIDE_LENGTH));
//	}

	@Test
	public void testToBufferedImage() {
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x++) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y++) {
				assertEquals(checkeredBlackAndWhite.getRGB(x, y), GraphicsUtil.toBufferedImage(checkeredBlackAndWhite).getRGB(x, y));
			}
		}
	}

	// Make sure pixel values match
	@Test
	public void testConvertToBufferedImage() {
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x++) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y++) {
				assertEquals(checkeredBlackAndWhite.getRGB(x, y), GraphicsUtil.convertToBufferedImage(checkeredBlackAndWhite).getRGB(x, y));
			}
		}
	}

	// Test rotating 90, 180, 270, 360
	@Test
	public void testRotateImageByDegrees() {
		BufferedImage threeSixty = GraphicsUtil.rotateImageByDegrees(checkeredBlackAndWhite, 360.0);
//		GraphicsUtil.drawImage(threeSixty, "checkered", SIDE_LENGTH, SIDE_LENGTH);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		for(int x = 0; x < threeSixty.getWidth(); x++) {
			for(int y = 0; y < threeSixty.getHeight(); y++) {
				assertEquals(threeSixty.getRGB(x, y), checkeredBlackAndWhite.getRGB(x, y));
			}
		}
		
		
		BufferedImage oneEighty = GraphicsUtil.rotateImageByDegrees(checkeredBlackAndWhite, 180);
//		GraphicsUtil.drawImage(oneEighty, "checkered", SIDE_LENGTH, SIDE_LENGTH);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		for(int x = 0; x < oneEighty.getWidth(); x++) {
			for(int y = 0; y < oneEighty.getHeight(); y++) {
				assertEquals(oneEighty.getRGB(x, y), checkeredBlackAndWhite.getRGB(x, y));
			}
		}
		
		BufferedImage ninety = GraphicsUtil.rotateImageByDegrees(checkeredBlackAndWhite, 90);
//		GraphicsUtil.drawImage(ninety, "checkered", SIDE_LENGTH, SIDE_LENGTH);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		for(int x = 0; x < ninety.getWidth(); x++) {
			for(int y = 0; y < ninety.getHeight(); y++) {
				assertEquals(ninety.getRGB(x, Math.abs(y - 1)), checkeredBlackAndWhite.getRGB(x, y));
				assertEquals(ninety.getRGB(Math.abs(x - 1), y), checkeredBlackAndWhite.getRGB(x, y));
			}
		}
		
		BufferedImage twoSeventy = GraphicsUtil.rotateImageByDegrees(checkeredBlackAndWhite, 270);
//		GraphicsUtil.drawImage(twoSeventy, "checkered", SIDE_LENGTH, SIDE_LENGTH);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		for(int x = 0; x < twoSeventy.getWidth(); x++) {
			for(int y = 0; y < twoSeventy.getHeight(); y++) {
				assertEquals(twoSeventy.getRGB(Math.abs(x - 1), y), checkeredBlackAndWhite.getRGB(x, y));
				assertEquals(twoSeventy.getRGB(x, Math.abs(y - 1)), checkeredBlackAndWhite.getRGB(x, y));
			}
		}
	}

	@Test
	public void testGetTwoByTwoTiledImage() {
		BufferedImage checkeredTwoTiled = new BufferedImage (SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		int blackRGB = Color.BLACK.getRGB();
		int whiteRGB = Color.WHITE.getRGB();
		// Set every other pixel to black starting on the first row, first pixel
		for(int x = 0; x < checkeredTwoTiled.getWidth(); x += 2) {
			for(int y = 0; y < checkeredTwoTiled.getHeight(); y += 2) {
				checkeredTwoTiled.setRGB(x, y, blackRGB);
			}
		}

		// Set every other pixel to be white starting on the first row, second pixel
		for(int x = 1; x < checkeredTwoTiled.getWidth(); x += 2) {
			for(int y = 0; y < checkeredTwoTiled.getHeight(); y += 2) {
				checkeredTwoTiled.setRGB(x, y, whiteRGB);
			}
		}

		// Set every other pixel to black starting on the second row, second pixel
		for(int x = 1; x < checkeredTwoTiled.getWidth(); x += 2) {
			for(int y = 1; y < checkeredTwoTiled.getHeight(); y += 2) {
				checkeredTwoTiled.setRGB(x, y, blackRGB);
			}
		}

		// Set every other pixel to white starting on the second row, first pixel
		for(int x = 0; x < checkeredTwoTiled.getWidth(); x += 2) {
			for(int y = 1; y < checkeredTwoTiled.getHeight(); y += 2) {
				checkeredTwoTiled.setRGB(x, y, whiteRGB);
			}
		}
		for(int x = 0; x < checkeredTwoTiled.getWidth(); x++) {
			for(int y = 0; y < checkeredTwoTiled.getHeight(); y++) {
				assertEquals(checkeredTwoTiled.getRGB(x, y), GraphicsUtil.getTwoByTwoTiledImage(checkeredBlackAndWhite).getRGB(x, y));
			}
		}
	}
	
	@Test
	public void testextractCenterOfDoubledRotatedImage() {		
	}

}
