package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class PictureFourQuadrantBrightnessBinLabelsTest {

	BufferedImage allBlack;
	BufferedImage allWhite;
	BufferedImage checkeredBlackAndWhite;
	BufferedImage largeCheckeredBlackAndWhite;
	BufferedImage randomBlackAndWhite;
	
	public static final int SIDE_LENGTH = 16;
	public static final int SMALL_BIN_DIMENSION = 2;
	public static final int MEDIUM_BIN_DIMENSION = 10;
	
	PictureFourQuadrantBrightnessBinLabels smallLabels;
	PictureFourQuadrantBrightnessBinLabels mediumLabels;
	
	@Before
	public void setUp() throws Exception {
		
		smallLabels = new PictureFourQuadrantBrightnessBinLabels(SMALL_BIN_DIMENSION);
		mediumLabels = new PictureFourQuadrantBrightnessBinLabels(MEDIUM_BIN_DIMENSION);
		
		// An image with only black pixels
		allBlack = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		// An image with only white pixels
		allWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		// An image with black and white checkered pixels
		checkeredBlackAndWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		// An image with black and white checkered quadrants
		largeCheckeredBlackAndWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		// An image with 'random' black and white pixels
		randomBlackAndWhite = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		
		// Image colors
		int blackRGB = Color.BLACK.getRGB();
		int whiteRGB = Color.WHITE.getRGB();
				
		// Setting all pixels in the image to black
		for(int x = 0; x < allBlack.getWidth(); x++) {
			for(int y = 0; y < allBlack.getHeight(); y++) {
				allBlack.setRGB(x, y, blackRGB);
			}
		}
		
		
		// Setting all pixels in the image to white
		for(int x = 0; x < allWhite.getWidth(); x++) {
			for(int y = 0; y < allWhite.getHeight(); y++) {
				allWhite.setRGB(x, y, whiteRGB);
			}
		}
	
		
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
		
		
		// Setting quadrant 1 to be black
		for(int x = 0; x < largeCheckeredBlackAndWhite.getWidth() / 2; x++) {
			for(int y = 0; y < largeCheckeredBlackAndWhite.getHeight() / 2; y++) {
				largeCheckeredBlackAndWhite.setRGB(x, y, blackRGB);
			}
		}
		
		// Setting quadrant 2 to be white
		for(int x = largeCheckeredBlackAndWhite.getWidth() / 2; x < largeCheckeredBlackAndWhite.getWidth(); x++) {
			for(int y = 0; y < largeCheckeredBlackAndWhite.getHeight() / 2; y++) {
				largeCheckeredBlackAndWhite.setRGB(x, y, whiteRGB);
			}
		}
		
		// Setting quadrant 3 to be white
		for(int x = 0; x < largeCheckeredBlackAndWhite.getWidth() / 2; x++) {
			for(int y = largeCheckeredBlackAndWhite.getHeight() / 2; y < largeCheckeredBlackAndWhite.getHeight(); y++) {
				largeCheckeredBlackAndWhite.setRGB(x, y, whiteRGB);
			}
		}
		
		// Setting quadrant 4 to be black
		for(int x = largeCheckeredBlackAndWhite.getWidth() / 2; x < largeCheckeredBlackAndWhite.getWidth(); x++) {
			for(int y = largeCheckeredBlackAndWhite.getHeight() / 2; y < largeCheckeredBlackAndWhite.getHeight(); y++) {
				largeCheckeredBlackAndWhite.setRGB(x, y, blackRGB);
			}
		}
		
		
		// An image with 'random' black and white pictures
		for(int x = 0; x < randomBlackAndWhite.getWidth(); x++) {
			for(int y = 0; y < randomBlackAndWhite.getHeight(); y++) {
				if(x % 3 == 0 && y % 4 == 0) {
					randomBlackAndWhite.setRGB(x, y, blackRGB);
				} else if(x % 2 != 0 && y % 6 == 0) {
					randomBlackAndWhite.setRGB(x, y, blackRGB);
				} else if(x % 4 == 0 && y % 3 == 0) {
					randomBlackAndWhite.setRGB(x, y, blackRGB);
				} else if(x % 4 == 0 && y % 2 == 0) {
					randomBlackAndWhite.setRGB(x, y, whiteRGB);
				} else if(x % 8 == 0 && y % 5 != 0) {
					randomBlackAndWhite.setRGB(x, y, whiteRGB);
				} else if(x % 7 == 0 && y % 4 != 0) {
					randomBlackAndWhite.setRGB(x, y, blackRGB);
				} else if(x % 2 == 0 && y % 9 == 0) {
					randomBlackAndWhite.setRGB(x, y, whiteRGB);
				} else if(x % 6 == 0 && y % 3 != 0) {
					randomBlackAndWhite.setRGB(x, y, whiteRGB);
				} else if(x % 2 == 0 && y % 2 != 0) {
					randomBlackAndWhite.setRGB(x, y, whiteRGB);
				} else {
					randomBlackAndWhite.setRGB(x, y, whiteRGB);
				}
			}
		}
		
	}

	@Test
	public void testBinLabels() {
		ArrayList<String> answer = new ArrayList<>();

		answer.add("0-0-0-0");
		answer.add("0-0-0-1");
		answer.add("0-0-1-0");
		answer.add("0-0-1-1");
		answer.add("0-1-0-0");
		answer.add("0-1-0-1");
		answer.add("0-1-1-0");
		answer.add("0-1-1-1");
		answer.add("1-0-0-0");
		answer.add("1-0-0-1");
		answer.add("1-0-1-0");
		answer.add("1-0-1-1");
		answer.add("1-1-0-0");
		answer.add("1-1-0-1");
		answer.add("1-1-1-0");
		answer.add("1-1-1-1");
		
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
		
		// Testing a largeCheckeredBlackAndWhite image
		assertArrayEquals(new double[] {0,64,64,0}, PictureFourQuadrantBrightnessBinLabels.getQuadrantBehaviorCharacterization(largeCheckeredBlackAndWhite), 0);
		
		// Testing a randomBlackAndWhite image
		assertArrayEquals(new double[] {40,41,44,45}, PictureFourQuadrantBrightnessBinLabels.getQuadrantBehaviorCharacterization(randomBlackAndWhite), 0);
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
	
		// Testing a largeCheckeredBlackAndWhite image
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(largeCheckeredBlackAndWhite, 0, 0, largeCheckeredBlackAndWhite.getWidth() / 2, largeCheckeredBlackAndWhite.getHeight() / 2), 0);
		assertEquals(64.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(largeCheckeredBlackAndWhite, largeCheckeredBlackAndWhite.getWidth() / 2, 0, largeCheckeredBlackAndWhite.getWidth(), largeCheckeredBlackAndWhite.getHeight() / 2), 0);
		assertEquals(64.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(largeCheckeredBlackAndWhite, 0, largeCheckeredBlackAndWhite.getHeight() / 2, largeCheckeredBlackAndWhite.getWidth() / 2, largeCheckeredBlackAndWhite.getHeight()), 0);
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(largeCheckeredBlackAndWhite, largeCheckeredBlackAndWhite.getWidth() / 2, largeCheckeredBlackAndWhite.getHeight() / 2, largeCheckeredBlackAndWhite.getWidth(), largeCheckeredBlackAndWhite.getHeight()), 0);
		
		// Testing a randomBlackAndWhite image
		assertEquals(40.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(randomBlackAndWhite, 0, 0, randomBlackAndWhite.getWidth() / 2, randomBlackAndWhite.getHeight() / 2), 0);
		assertEquals(41.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(randomBlackAndWhite, randomBlackAndWhite.getWidth() / 2, 0, randomBlackAndWhite.getWidth(), randomBlackAndWhite.getHeight() / 2), 0);
		assertEquals(44.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(randomBlackAndWhite, 0, randomBlackAndWhite.getHeight() / 2, randomBlackAndWhite.getWidth() / 2, randomBlackAndWhite.getHeight()), 0);
		assertEquals(45.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(randomBlackAndWhite, randomBlackAndWhite.getWidth() / 2, randomBlackAndWhite.getHeight() / 2, randomBlackAndWhite.getWidth(), randomBlackAndWhite.getHeight()), 0);
		
	}

	@Test
	public void testBinCoordinates() {
		// Testing smallLabels against each image
		assertArrayEquals(new int[] {0,0,0,0}, smallLabels.binCoordinates(allBlack));
		assertArrayEquals(new int[] {1,1,1,1}, smallLabels.binCoordinates(allWhite));
		assertArrayEquals(new int[] {1,1,1,1}, smallLabels.binCoordinates(checkeredBlackAndWhite));
		assertArrayEquals(new int[] {0,1,1,0}, smallLabels.binCoordinates(largeCheckeredBlackAndWhite));
		assertArrayEquals(new int[] {1,1,1,1}, smallLabels.binCoordinates(randomBlackAndWhite));
		
		// Testing mediumLabels against each image
		assertArrayEquals(new int[] {0,0,0,0}, mediumLabels.binCoordinates(allBlack));
		assertArrayEquals(new int[] {9,9,9,9}, mediumLabels.binCoordinates(allWhite));
		assertArrayEquals(new int[] {5,5,5,5}, mediumLabels.binCoordinates(checkeredBlackAndWhite));
		assertArrayEquals(new int[] {0,9,9,0}, mediumLabels.binCoordinates(largeCheckeredBlackAndWhite));
		assertArrayEquals(new int[] {6,6,6,7}, mediumLabels.binCoordinates(randomBlackAndWhite));
		
	}
}


