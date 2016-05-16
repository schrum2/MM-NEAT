package edu.utexas.cs.nn.tasks.testmatch.imagematch;

import static org.junit.Assert.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.*;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.testmatch.MatchDataTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

public class ImageMatchTaskTest {

	public static final String IMAGE_MATCH_PATH = "data\\imagematch";
	private static final int IMAGE_PLACEMENT = 200;
	private static final int X_COORDINATE_INDEX = 0;
	private static final int Y_COORDINATE_INDEX = 1;
	private static final int DISTANCE_COORDINATE_IND = 2;
	private static final int hIndex = 0;
	private static final int sIndex = 1;
	private static final int bIndex = 2;
	private static final double MAX_COLOR_INTENSITY = 255.0;//this variable needed to scale RGB values to a 0-1 range
	private BufferedImage img = null;
	private int imageHeight, imageWidth;
	
//	MMNEAT.clearClasses();
//	EvolutionaryHistory.setInnovation(0);
//	EvolutionaryHistory.setHighestGenotypeId(0);
//	Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false", "includeHalfLinearPiecewiseFunction:true", "includeSawtoothFunction:true"});
//	MMNEAT.loadClasses();
	
	ImageMatchTask test = new ImageMatchTask("Tiny");
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test_getterFunctions() {
		String[] outputLabels = test.outputLabels();
		String[] sensorLabels = test.sensorLabels();
		assertTrue(sensorLabels[0].equals("X-coordinate"));
		assertTrue(outputLabels[0].equals("r-value"));
		assertTrue(test.numInputs() == 4);
		assertTrue(test.numOutputs() == 3);
	}
	
	@Test
	public void test_getTrainingPairs() {
		ArrayList<Pair<double[], double[]>> pair = test.getTrainingPairs();
		assertTrue(pair.get(pair.size()-1).t1[0] == imageHeight);
		assertTrue(pair.get(pair.size()-1).t1[1] == imageHeight);
		assertTrue(pair.get(pair.size() -1).t2.length == 3);
	}

	@Test
	public void test_Scale() {
		double[] leftTopCorner = {0,0};
		test.scale(leftTopCorner, imageWidth, imageHeight);
		double[] leftBottomCorner = {0, imageHeight};
		test.scale(leftBottomCorner, imageWidth, imageHeight);
		double[] rightTopCorner = {imageWidth, 0};
		test.scale(rightTopCorner, imageWidth, imageHeight);
		double [] rightBottomCorner = {imageWidth, imageHeight};
		test.scale(rightBottomCorner, imageWidth, imageHeight);
		double[] center = {imageWidth/2, imageHeight/2};
		test.scale(center, imageWidth, imageHeight);
		assertTrue(leftTopCorner[0] == -1 && leftTopCorner[1] == -1);
		assertTrue(leftBottomCorner[0] == -1 && leftBottomCorner[1] == 1);
		assertTrue(rightTopCorner[0] == 1 && rightTopCorner[1] == -1);
		assertTrue(rightBottomCorner[0] == 1 && rightBottomCorner[1] == 1);
		assertTrue(center[0] == 0 && center[1] == 0);
		
	}
}
