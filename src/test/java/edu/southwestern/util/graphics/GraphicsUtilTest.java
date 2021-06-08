package edu.southwestern.util.graphics;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.parameters.Parameters;
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
//	}
//
//	@Test
//	public void testGetBrightnessFromImage() {
//		//fail("Not yet implemented");
//	}
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
//	// AssertArrayEquals
//	@Test
//	public void testGet2DObjectCPPNInputsIntIntIntIntDouble() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGet2DObjectCPPNInputsIntIntIntIntDoubleDoubleDouble() {
//		//fail("Not yet implemented");
//	}

	// Not testing things involving drawing panels
//	@Test
//	public void testDrawImage() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testSolidColorImage() {
		//fail("Not yet implemented");
		BufferedImage allWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		int white = Color.WHITE.getRGB();
		for(int x = 0; x < allWhite.getWidth(); x++) {
			for(int y = 0; y < allWhite.getHeight(); y++) {
				allWhite.setRGB(x, y, white);
				assertEquals(allWhite.getRGB(x, y), GraphicsUtil.solidColorImage(Color.WHITE, SIDE_LENGTH, SIDE_LENGTH).getRGB(x, y));
			}
		}
	}

//	@Test
//	public void testFlatFeatureArrayFromBufferedImage() {
//		//fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetHSB() {
//		//fail("Not yet implemented");
//		Float[] checkeredHSB = new Float[]{};//GraphicsUtil.getHSB(checkeredBlackAndWhite, 0, 0);
//		for(int i = 0; i < checkeredHSB.length; i++) {
//			checkeredHSB[i] = GraphicsUtil.getHSB(checkeredBlackAndWhite, 0, 0)[i];
//		}
//		assertArrayEquals(new Float[]{1,1,1}, checkeredHSB);
//	}
//
//	@Test
//	public void testImageFromINDArray() {
//		//fail("Not yet implemented");
//	}

//	@Test
//	public void testLinePlotDrawingPanelDoubleDoubleArrayListOfDoubleColor() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testLinePlotImage() {
//		fail("Not yet implemented");
//	}
//
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
//	@Test
//	public void testWavePlotFromDoubleArray() {
//		fail("Not yet implemented");
//	}
//
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
//		BufferedImage threeSixty = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
//		BufferedImage rotatedCheckered = GraphicsUtil.rotateImageByDegrees(checkeredBlackAndWhite, 360.0);
//		for(int x = 0; x < threeSixty.getWidth(); x++) {
//			for(int y = 0; y < threeSixty.getHeight(); y++) {
//				threeSixty.setRGB(x, y, checkeredBlackAndWhite.getRGB(x, y));
//				assertEquals(threeSixty.getRGB(x, y), rotatedCheckered.getRGB(x, y));
//			}
//		}
//		
//		
//		BufferedImage oneEighty = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
//		for(int x = 0; x < oneEighty.getWidth(); x++) {
//			for(int y = 0; y < oneEighty.getHeight(); y++) {
//				oneEighty.setRGB(Math.abs(x - oneEighty.getWidth()), Math.abs(y - oneEighty.getHeight()), checkeredBlackAndWhite.getRGB(x, y));
//				//assertEquals(oneEighty.getRGB(x, y), GraphicsUtil.rotateImageByDegrees(oneEighty, 180).getRGB(x, y));
//			}
//		}
//		
//		BufferedImage ninety = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
//		for(int x = 0; x < ninety.getWidth(); x++) {
//			for(int y = 0; y < ninety.getHeight(); y++) {
//				ninety.setRGB(x, y, checkeredBlackAndWhite.getRGB(x, y));
//			}
//		}
//		
//		BufferedImage twoSeventy = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
//		
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

}
