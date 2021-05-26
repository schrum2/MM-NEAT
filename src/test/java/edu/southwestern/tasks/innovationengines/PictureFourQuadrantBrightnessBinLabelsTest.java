package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

public class PictureFourQuadrantBrightnessBinLabelsTest {

	BufferedImage allBlack;
	BufferedImage allWhite;
	BufferedImage checkeredBlackAndWhite;
	BufferedImage largeCheckeredBlackAndWhite;
	
	public static final int SIDE_LENGTH = 16;
	public static final int SMALL_BIN_DIMENSION = 2;
	public static final int MEDIUM_BIN_DIMENSION = 10;
	
	PictureFourQuadrantBrightnessBinLabels smallLabels;
	PictureFourQuadrantBrightnessBinLabels mediumLabels;
	
	@Before
	public void setUp() throws Exception {
		
		smallLabels = new PictureFourQuadrantBrightnessBinLabels(SMALL_BIN_DIMENSION);
		mediumLabels = new PictureFourQuadrantBrightnessBinLabels(MEDIUM_BIN_DIMENSION);
		
		allBlack = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		allWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		
		// Setting all pixels in the image to black
		int blackRGB = Color.BLACK.getRGB();
		for(int x = 0; x < allBlack.getWidth(); x++) {
			for(int y = 0; y < allBlack.getHeight(); y++) {
				allBlack.setRGB(x, y, blackRGB);
			}
		}
		
		// Setting all pixels in the image to white
		int whiteRGB = Color.WHITE.getRGB();
		for(int x = 0; x < allWhite.getWidth(); x++) {
			for(int y = 0; y < allWhite.getHeight(); y++) {
				allWhite.setRGB(x, y, whiteRGB);
			}
		}
		
		// Setting the pixels in the image to be black and white checkered
		checkeredBlackAndWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, blackRGB);
			}
		}
		
		for(int x = 1; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 1; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, blackRGB);
			}
		}
		
		for(int x = 1; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 0; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, whiteRGB);
			}
		}
		
		for(int x = 0; x < checkeredBlackAndWhite.getWidth(); x += 2) {
			for(int y = 1; y < checkeredBlackAndWhite.getHeight(); y += 2) {
				checkeredBlackAndWhite.setRGB(x, y, whiteRGB);
			}
		}
		
		// Setting the quadrants in the image to be black and white checkered
		largeCheckeredBlackAndWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < largeCheckeredBlackAndWhite.getWidth() / 2; x++) {
			for(int y = 0; y < largeCheckeredBlackAndWhite.getHeight() / 2; y++) {
				largeCheckeredBlackAndWhite.setRGB(x, y, blackRGB);
			}
		}
		
		
	}

	@Test
	public void testBinLabels() {
		ArrayList<String> answer = new ArrayList<>();

		answer.add("(0,0,0,0)");
		answer.add("(0,0,0,1)");
		answer.add("(0,0,1,0)");
		answer.add("(0,0,1,1)");
		answer.add("(0,1,0,0)");
		answer.add("(0,1,0,1)");
		answer.add("(0,1,1,0)");
		answer.add("(0,1,1,1)");
		answer.add("(1,0,0,0)");
		answer.add("(1,0,0,1)");
		answer.add("(1,0,1,0)");
		answer.add("(1,0,1,1)");
		answer.add("(1,1,0,0)");
		answer.add("(1,1,0,1)");
		answer.add("(1,1,1,0)");
		answer.add("(1,1,1,1)");
		
		assertEquals(answer, smallLabels.binLabels());
	}

	@Test
	public void testGetQuadrantBehaviorCharacterization() {
		// Testing an allBlack image
		assertArrayEquals(new double[] {0,0,0,0}, PictureFourQuadrantBrightnessBinLabels.getQuadrantBehaviorCharacterization(allBlack), 0);
		
		// Testing an allWhite image
		assertArrayEquals(new double[] {64,64,64,64}, PictureFourQuadrantBrightnessBinLabels.getQuadrantBehaviorCharacterization(allWhite), 0);
		
		// Testing a checkeredBlackAndWhite image
		assertArrayEquals(new double[] {32,32,32,32}, PictureFourQuadrantBrightnessBinLabels.getQuadrantBehaviorCharacterization(checkeredBlackAndWhite), 0);
		
	}

	@Test
	public void testSumQuadrantBrightnessValues() {
		// Testing an allBlack image
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, 0, 0, allBlack.getWidth() / 2, allBlack.getHeight() / 2), 0);
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, allBlack.getWidth() / 2, 0, allBlack.getWidth(), allBlack.getHeight() / 2), 0);
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, 0, allBlack.getHeight() / 2, allBlack.getWidth() / 2, allBlack.getHeight()), 0);
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, allBlack.getWidth() / 2, allBlack.getHeight() / 2, allBlack.getWidth(), allBlack.getHeight()), 0);
	
		// Testing an allWhite image
		assertEquals(64.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allWhite, 0, 0, allWhite.getWidth() / 2, allWhite.getHeight() / 2), 0);
		assertEquals(64.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allWhite, allWhite.getWidth() / 2, 0, allWhite.getWidth(), allWhite.getHeight() / 2), 0);
		assertEquals(64.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allWhite, 0, allWhite.getHeight() / 2, allWhite.getWidth() / 2, allWhite.getHeight()), 0);
		assertEquals(64.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allWhite, allWhite.getWidth() / 2, allWhite.getHeight() / 2, allWhite.getWidth(), allWhite.getHeight()), 0);
		
		// Testing a checkeredBlackAndWhite image
		assertEquals(32.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(checkeredBlackAndWhite, 0, 0, checkeredBlackAndWhite.getWidth() / 2, checkeredBlackAndWhite.getHeight() / 2), 0);
		assertEquals(32.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(checkeredBlackAndWhite, checkeredBlackAndWhite.getWidth() / 2, 0, checkeredBlackAndWhite.getWidth(), checkeredBlackAndWhite.getHeight() / 2), 0);
		assertEquals(32.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(checkeredBlackAndWhite, 0, checkeredBlackAndWhite.getHeight() / 2, checkeredBlackAndWhite.getWidth() / 2, checkeredBlackAndWhite.getHeight()), 0);
		assertEquals(32.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(checkeredBlackAndWhite, checkeredBlackAndWhite.getWidth() / 2, checkeredBlackAndWhite.getHeight() / 2, checkeredBlackAndWhite.getWidth(), checkeredBlackAndWhite.getHeight()), 0);
	}

	@Test
	public void testBinCoordinates() {
		//assertEquals("[0,0,0,0]", smallLabels.binCoordinates(allBlack));
		assertArrayEquals(new int[] {0,0,0,0}, smallLabels.binCoordinates(allBlack));
		assertArrayEquals(new int[] {1,1,1,1}, smallLabels.binCoordinates(allWhite));
		assertArrayEquals(new int[] {1,1,1,1}, smallLabels.binCoordinates(checkeredBlackAndWhite));
		
		assertArrayEquals(new int[] {0,0,0,0}, mediumLabels.binCoordinates(allBlack));
		assertArrayEquals(new int[] {9,9,9,9}, mediumLabels.binCoordinates(allWhite));
		assertArrayEquals(new int[] {5,5,5,5}, mediumLabels.binCoordinates(checkeredBlackAndWhite));
	}
}


