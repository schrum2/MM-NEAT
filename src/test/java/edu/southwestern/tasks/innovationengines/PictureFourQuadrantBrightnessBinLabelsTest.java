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
	
	public static final int SIDE_LENGTH = 16;
	public static final int SMALL_BIN_DIMENSION = 2;
	
	PictureFourQuadrantBrightnessBinLabels smallLabels;
	
	@Before
	public void setUp() throws Exception {
		
		smallLabels = new PictureFourQuadrantBrightnessBinLabels(SMALL_BIN_DIMENSION);
		
		allBlack = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		int blackRGB = Color.BLACK.getRGB();
		for(int x = 0; x < allBlack.getWidth(); x++) {
			for(int y = 0; y < allBlack.getHeight(); y++) {
				allBlack.setRGB(x, y, blackRGB);
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
		assertArrayEquals(new double[] {0,0,0,0}, PictureFourQuadrantBrightnessBinLabels.getQuadrantBehaviorCharacterization(allBlack), 0);
	}

	@Test
	public void testSumQuadrantBrightnessValues() {
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, 0, 0, allBlack.getWidth() / 2, allBlack.getHeight() / 2), 0);
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, allBlack.getWidth() / 2, 0, allBlack.getWidth(), allBlack.getHeight() / 2), 0);
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, 0, allBlack.getHeight() / 2, allBlack.getWidth() / 2, allBlack.getHeight()), 0);
		assertEquals(0.0, PictureFourQuadrantBrightnessBinLabels.sumQuadrantBrightnessValues(allBlack, allBlack.getWidth() / 2, allBlack.getHeight() / 2, allBlack.getWidth(), allBlack.getHeight()), 0);
	}

	@Test
	public void testBinCoordinates() {
		assertEquals("[0,0,0,0]", smallLabels.binCoordinates(allBlack));
	}

}
