package edu.southwestern.util.graphics;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraphicsUtilTest {

	BufferedImage checkeredBlackAndWhite;
	
	public static final int SIDE_LENGTH = 16;

	@Before
	public void setUp() throws Exception {
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

	@Test
	public void testGetConfiguration() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testImageFromCPPNNetworkIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testImageFromCPPNNetworkIntIntDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testImageFromCPPNNetworkIntIntDoubleArrayDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testImageFromCPPNNetworkIntIntDoubleArrayDoubleDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testZentangleImagesBufferedImageBufferedImageBufferedImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testZentangleImagesBufferedImageBufferedImageBufferedImageBufferedImageBufferedImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testZentangleImagesBufferedImageBufferedImageBufferedImageBufferedImageBufferedImageBufferedImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemixedImageFromCPPN() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBrightnessFromImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetHSBFromCPPN() {
		fail("Not yet implemented");
	}

	@Test
	public void testRangeRestrictHSB() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet2DObjectCPPNInputsIntIntIntIntDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet2DObjectCPPNInputsIntIntIntIntDoubleDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testDrawImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testSolidColorImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testFlatFeatureArrayFromBufferedImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetHSB() {
		fail("Not yet implemented");
	}

	@Test
	public void testImageFromINDArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testLinePlotDrawingPanelDoubleDoubleArrayListOfDoubleColor() {
		fail("Not yet implemented");
	}

	@Test
	public void testLinePlotImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testLinePlotGraphicsDoubleDoubleIntIntArrayListOfDoubleColor() {
		fail("Not yet implemented");
	}

	@Test
	public void testWavePlotFromFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testWavePlotFromDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testScaleDoubleDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testScaleDoubleDoubleDoubleInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvertDoubleDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvertDoubleDoubleDoubleInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testToBufferedImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testConvertToBufferedImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testRotateImageByDegrees() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTwoByTwoTiledImage() {
		fail("Not yet implemented");
	}

}
