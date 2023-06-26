package edu.southwestern.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.scores.Score;
import jmetal.qualityIndicator.Hypervolume;

public class MultiobjectiveUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public <T> void testHypervolumeFromParetoFrontSinglePoint() {
		//needs a genotype
//		Genotype<T> testGenotype = new Genotype<T>();
		List<Score<T>> testList = new ArrayList<Score<T>>();
		double[] scores = new double[2];
		scores[0] = 1.0;
		scores[1] = 2.0;
		Score<T> testScore = new Score<T>(null, scores);
		testList.add(testScore);
		double result = MultiobjectiveUtil.hypervolumeFromParetoFront(testList);
		assertEquals(2, result, 0.0);
		
		//fail("Not yet implemented");
	}
	@Test
	public <T> void testHypervolumeFromParetoFrontTwoPoints() {
		List<Score<T>> testList = new ArrayList<Score<T>>();
		double[] scores = new double[2];
		scores[0] = 5.0;
		scores[1] = 3.0;
		Score<T> testScore = new Score<T>(null, scores);
		double[] scores2 = new double[2];
		scores2[0] = 2.0;
		scores2[1] = 15.0;
		Score<T> testScore2 = new Score<T>(null, scores2);
		testList.add(testScore);
		testList.add(testScore2);
		double result = MultiobjectiveUtil.hypervolumeFromParetoFront(testList);
		assertEquals(39, result, 0.0);
	}

	@Test
	public <T> void testHypervolumeFromPopulationTwoScoresNeitherDominated() {
		List<Score<T>> testList = new ArrayList<Score<T>>();
		//score 1
		double[] scores = new double[2];
		scores[0] = 5.0;
		scores[1] = 3.0;
		Score<T> testScore = new Score<T>(null, scores);
		//score 2
		double[] scores2 = new double[2];
		scores2[0] = 2.0;
		scores2[1] = 5.0;
		Score<T> testScore2 = new Score<T>(null, scores2);
		
		testList.add(testScore);
		testList.add(testScore2);
		
		double result = MultiobjectiveUtil.hypervolumeFromParetoFront(testList);
		assertEquals(19, result, 0.0);
	}
	@Test
	public <T> void testHypervolumeFromPopulationThreeScoresOneDominated() {
		List<Score<T>> testList = new ArrayList<Score<T>>();
		//score 1
		double[] scores = new double[2];
		scores[0] = 5.0;
		scores[1] = 3.0;
		Score<T> testScore = new Score<T>(null, scores);
		//score 2
		double[] scores2 = new double[2];
		scores2[0] = 2.0;
		scores2[1] = 5.0;
		Score<T> testScore2 = new Score<T>(null, scores2);

		//score 3
		double[] scores3 = new double[2];
		scores3[0] = 2.0;
		scores3[1] = 1.0;
		Score<T> testScore3 = new Score<T>(null, scores3);
		
		testList.add(testScore);
		testList.add(testScore2);
		testList.add(testScore3);
		
		double result = MultiobjectiveUtil.hypervolumeFromParetoFront(testList);
		assertEquals(19, result, 0.0);
	}
	@Test
	public <T> void testHypervolumeFromPopulationThreeScoresThreeObjectivesNoneDominated() {
		List<Score<T>> testList = new ArrayList<Score<T>>();
		//score 1
		double[] scores = new double[3];
		scores[0] = 5.0;
		scores[1] = 3.0;
		scores[2] = 1.0;
		Score<T> testScore = new Score<T>(null, scores);
		//score 2
		double[] scores2 = new double[3];
		scores2[0] = 2.0;
		scores2[1] = 5.0;
		scores2[2] = 1.0;
		Score<T> testScore2 = new Score<T>(null, scores2);

		//score 3
		double[] scores3 = new double[3];
		scores3[0] = 2.0;
		scores3[1] = 1.0;
		scores3[2] = 5.0;
		Score<T> testScore3 = new Score<T>(null, scores3);
		
		testList.add(testScore);
		testList.add(testScore2);
		testList.add(testScore3);
		
		double result = MultiobjectiveUtil.hypervolumeFromParetoFront(testList);
		assertEquals(27, result, 0.0);
	}
}
