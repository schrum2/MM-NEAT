package edu.utexas.cs.nn.tasks.microrts.evaluation;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.utexas.cs.nn.parameters.Parameters;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.Player;
import micro.rts.units.Unit;
import micro.rts.units.UnitTypeTable;

/**
 * 
 * JUnit tests for NNComplexEvaluationFunction
 *  
 * @author alicequint
 * @param <T>
 *
 */
public class NNComplexEvaluationFunctionTest {
	
	private final double EPSILON = 0.000001;
	
	NNEvaluationFunction cef = new NNComplexEvaluationFunction();
	PhysicalGameState pgs = new PhysicalGameState(4, 4); 
	UnitTypeTable utt = new UnitTypeTable();
	GameState gs;
	Player blue = new Player(0, 0); //id: 0
	Player red = new Player(1, 0); //id: 1
	
	Unit blueWorker = new Unit(0, utt.getUnitType("Worker"), 0, 0, 1);
	Unit blueHeavy = new Unit(0, utt.getUnitType("Heavy"), 2, 0, 0);
	Unit redWorker = new Unit(1, utt.getUnitType("Worker"), 3, 0, 0);
	Unit blueBase = new Unit(0, utt.getUnitType("Base"), 0, 1, 6); //six resources
	Unit redBase = new Unit(1, utt.getUnitType("Base"), 2, 2, 10); //10 resources
	Unit resourceTile = new Unit(-1, utt.getUnitType("Resource"), 0, 3, 7);
	
	private double resourceScore = .272727272727272;
	private int resourceWorkerSqrt = 50;
	private int workerSqrt = 40;
	private double halfHpHeavy = 69.28203230275508;
	private double[] fadedValues = {0.6876560219336321, 0.6876560219336321*0.6876560219336321,
			0.6876560219336321*0.6876560219336321*0.6876560219336321, 0.6876560219336321*0.6876560219336321*0.6876560219336321*0.6876560219336321,
			0.6876560219336321*0.6876560219336321*0.6876560219336321*0.6876560219336321*0.6876560219336321}; //calculated for 4 x 4 grid
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"watch:false","io:false","netio:false","task:edu.utexas.cs.nn.tasks.microrts.MicroRTSTask",
				"mRTSMobileUnits:true","mRTSBuildings:true", "mRTSMyMobileUnits:true","mRTSMyBuildings:true","mRTSOpponentsMobileUnits:true",
				"mRTSOpponentsBuildings:true","mRTSMyAll:true","mRTSOpponentsAll:true","mRTSAll:true","mRTSResources:true","mRTSTerrain:true",
				"mRTSObjectivePath:true","mRTSAllSqrt3MobileUnits:true","mRTSMyBuildingGradientMobileUnits:true",
				"mRTSResourceProportion:true"
				});
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		pgs.addPlayer(blue);
		pgs.addPlayer(red);
		
		blueHeavy.setHitPoints(blueHeavy.getHitPoints()*3/4);
		
		blue.setResources(7);
		red.setResources(10);
		
		pgs.addUnit(blueWorker);
		pgs.addUnit(blueHeavy);
		pgs.addUnit(redWorker);
		pgs.addUnit(blueBase);
		pgs.addUnit(redBase);
		pgs.addUnit(resourceTile);
		pgs.setTerrain(3, 2, PhysicalGameState.TERRAIN_WALL);
		pgs.setTerrain(3, 3, PhysicalGameState.TERRAIN_WALL);
		
		gs = new GameState(pgs, utt);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGameStateToArray() {
		
	 	double[] bluePerspective = cef.gameStateToArray(gs, 0);
	 	double[] redPerspective  = cef.gameStateToArray(gs, 1);
	 	
	 	double[] expectedBlueValues = new double[]{
	 	//mobile
	 			1,0,1,-1,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//buildings
	 			0,0,0,0,
	 			1,0,0,0,
	 			0,0,-1,0,
	 			0,0,0,0,
	 	//my mobile
	 			1,0,1,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//my buildings
	 			0,0,0,0,
	 			1,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//opponents mobile
	 			0,0,0,-1,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//opponents buildings
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,-1,0,
	 			0,0,0,0,
	 	//my all
	 			1,0,1,0,
	 			1,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//opponenets all
	 			0,0,0,-1,
	 			0,0,0,0,
	 			0,0,-1,0,
	 			0,0,0,0,
	 	//all
	 			1,0,1,-1,
	 			1,0,0,0,
	 			0,0,-1,0,
	 			0,0,0,0,
	 	//resources
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 			-1,0,0,0,
	 	//terrain
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,1,
	 			0,0,0,1,
	 	//path
	 			fadedValues[3],fadedValues[2],fadedValues[1],fadedValues[2],
	 			fadedValues[2],fadedValues[1],fadedValues[0],fadedValues[1],
	 			fadedValues[1],fadedValues[0],			  1 ,			 0,
	 			fadedValues[2],fadedValues[1],fadedValues[0],			 0,
	 	//sqrt3 mobile
	 			resourceWorkerSqrt,0,halfHpHeavy,-workerSqrt,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//my building-gradient mobile
	 			fadedValues[3],0,fadedValues[1],0,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//resource substrate (1x1)
	 			-resourceScore,
	 	};
	 	
	 	double[] expectedRedValues = new double[]{
	 	//mobile
	 			-1,0,-1,1,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//buildings
	 			0,0,0,0,
	 			-1,0,0,0,
	 			0,0,1,0,
	 			0,0,0,0,
	 	//my mobile
	 			0,0,0,1,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//my buildings
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,1,0,
	 			0,0,0,0,
	 	//opponents mobile
	 			-1,0,-1,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//opponents buildings
	 			0,0,0,0,
	 			-1,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//my all
	 			0,0,0,1,
	 			0,0,0,0,
	 			0,0,1,0,
	 			0,0,0,0,
	 	//opponenets all
	 			-1,0,-1,0,
	 			-1,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//all
	 			-1,0,-1,1,
	 			-1,0,0,0,
	 			0,0,1,0,
	 			0,0,0,0,
	 	//resources
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 			-1,0,0,0,
	 	//terrain
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,1,
	 			0,0,0,1,
	 	//path
	 			fadedValues[0], fadedValues[1], fadedValues[2], fadedValues[3],
	 			1, 				fadedValues[0], fadedValues[1], fadedValues[2],
	 			fadedValues[0], fadedValues[1], fadedValues[2], 0,
	 			fadedValues[1], fadedValues[2], fadedValues[3], 0,
	 	//sqrt3 mobile
	 			-resourceWorkerSqrt,0,-halfHpHeavy,workerSqrt,
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//my building-gradient mobile
	 			0,0,0,fadedValues[3],
	 			0,0,0,0,
	 			0,0,0,0,
	 			0,0,0,0,
	 	//resource substrate (1x1)
	 			resourceScore,
	 	};
	 	
	 	assertArrayEquals(bluePerspective, expectedBlueValues, EPSILON);
	 	assertArrayEquals(redPerspective, expectedRedValues, EPSILON);
	 	
	}

	@Test
	public void testGetNumInputSubstrates() {
//		fail("Not yet implemented");
	}

}
