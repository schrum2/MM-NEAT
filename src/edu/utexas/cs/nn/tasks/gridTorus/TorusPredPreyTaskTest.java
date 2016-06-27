package edu.utexas.cs.nn.tasks.gridTorus;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.competitive.CompetitiveHomogeneousPredatorsVsPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativePredatorsVsStaticPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativePreyVsStaticPredatorsTask;
import edu.utexas.cs.nn.tasks.gridTorus.cooperativeAndCompetitive.CompetitiveAndCooperativePredatorsVsPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.GridTorusObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchCloseObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchCloseQuickObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorEatEachPreyQuicklyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorMinimizeDistanceFromPreyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorMinimizeGameTimeObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PreyLongSurvivalTimeObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PreyMaximizeDistanceFromPredatorsObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PreyMaximizeGameTimeObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PreyMinimizeCaughtObjective;

public class TorusPredPreyTaskTest <T extends Network> {
	
	public static final double doubleThreshold = .001;

	@SuppressWarnings("rawtypes")
	private static TorusEvolvedPredatorsVsStaticPreyTask homoPred;
	@SuppressWarnings("rawtypes")
	private static TorusEvolvedPreyVsStaticPredatorsTask homoPrey;
	@SuppressWarnings("rawtypes")
	private static CooperativePredatorsVsStaticPreyTask coPred;
	@SuppressWarnings("rawtypes")
	private static CooperativePreyVsStaticPredatorsTask coPrey;
	@SuppressWarnings("rawtypes")
	private static CompetitiveHomogeneousPredatorsVsPreyTask homoComp;
	@SuppressWarnings("rawtypes")
	private static CompetitiveAndCooperativePredatorsVsPreyTask coComp;

	@Before
	public void setUp() throws Exception {
		//NOTE: MAKE SURE THAT THE BELOW PARAMETER INITIALIZATION SETS THE DEFAULT FITNESSES TO FALSE
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"torusPreys:2", "torusPredators:3", 
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController",
				"predatorCatchClose:false", "preyRRM:false" });
		MMNEAT.loadClasses();
	}

	@SuppressWarnings({ "rawtypes", "static-access" })
	@Test
	public void testConstructors() {
		MMNEAT.task = new TorusEvolvedPredatorsVsStaticPreyTask();
		homoPred = (TorusEvolvedPredatorsVsStaticPreyTask)MMNEAT.task;
		assertEquals(homoPred.preyEvolve, false);
		assertEquals(homoPred.competitive, false);
		assertEquals(homoPred.objectives.size(), 1);

		MMNEAT.task = new TorusEvolvedPreyVsStaticPredatorsTask();
		homoPrey = (TorusEvolvedPreyVsStaticPredatorsTask)MMNEAT.task;
		assertEquals(homoPrey.preyEvolve, true);
		assertEquals(homoPrey.competitive, false);
		assertEquals(homoPrey.objectives.size(), 1);

		MMNEAT.task = new CooperativePredatorsVsStaticPreyTask();
		coPred = (CooperativePredatorsVsStaticPreyTask)MMNEAT.task;
		assertEquals(coPred.getLonerTaskInstance().preyEvolve, false);
		assertEquals(coPred.getLonerTaskInstance().competitive, false);
		assertEquals(coPred.getLonerTaskInstance().objectives.size(), 3);
		assertEquals(coPred.getLonerTaskInstance().objectives.size(), coPred.numberOfPopulations());

		MMNEAT.task = new CooperativePreyVsStaticPredatorsTask();
		coPrey = (CooperativePreyVsStaticPredatorsTask)MMNEAT.task;
		assertEquals(coPrey.getLonerTaskInstance().preyEvolve, true);
		assertEquals(coPrey.getLonerTaskInstance().competitive, false);
		assertEquals(coPrey.getLonerTaskInstance().objectives.size(), 2);
		assertEquals(coPrey.getLonerTaskInstance().objectives.size(), coPrey.numberOfPopulations());

		MMNEAT.task = new CompetitiveHomogeneousPredatorsVsPreyTask();
		homoComp = (CompetitiveHomogeneousPredatorsVsPreyTask)MMNEAT.task;
		assertEquals(homoComp.getLonerTaskInstance().competitive, true);
		assertEquals(homoComp.getLonerTaskInstance().objectives.size(), 2);
		assertEquals(homoComp.getLonerTaskInstance().objectives.size(), homoComp.numberOfPopulations());

		MMNEAT.task = new CompetitiveAndCooperativePredatorsVsPreyTask();
		coComp = (CompetitiveAndCooperativePredatorsVsPreyTask)MMNEAT.task;
		assertEquals(coComp.getLonerTaskInstance().competitive, true);
		assertEquals(coComp.getLonerTaskInstance().objectives.size(), 5);
		assertEquals(coComp.getLonerTaskInstance().objectives.size(), coComp.numberOfPopulations());
	}

	@SuppressWarnings({ "hiding", "rawtypes", "unchecked" })
	@Test
	public <T extends Network> void testAddObjective(){
		//Test for homogeneous pred vs static prey
		ArrayList<ArrayList<GridTorusObjective<T>>> objectives = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		ArrayList<ArrayList<GridTorusObjective<T>>> otherScores = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		MMNEAT.task = new TorusEvolvedPredatorsVsStaticPreyTask();
		homoPred = (TorusEvolvedPredatorsVsStaticPreyTask)MMNEAT.task;
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.size(), 1);
		assertEquals(otherScores.get(0).size(), 0);
		assertEquals(otherScores.size(), 1);
		
		homoPred.addObjective(new PredatorCatchCloseObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 1);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 1);
		assertTrue(otherScores.get(0).isEmpty());
		
		homoPred.addObjective(new PredatorCatchObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(objectives.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 1);
		assertTrue(otherScores.get(0).isEmpty());
		
		homoPred.addObjective(new PredatorCatchCloseObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 1);
		assertTrue(otherScores.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PredatorCatchObjective);
		assertEquals(otherScores.size(), 1);
		assertFalse(otherScores.get(0).isEmpty());
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(objectives.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 1);
		
		homoPred.addObjective(new PredatorCatchObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 2);
		assertTrue(otherScores.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(otherScores.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(otherScores.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(otherScores.size(), 1);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(objectives.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 1);
		
		
		//Test for homogeneous prey vs static pred
		objectives = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		otherScores = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		MMNEAT.task = new TorusEvolvedPreyVsStaticPredatorsTask();
		homoPrey = (TorusEvolvedPreyVsStaticPredatorsTask)MMNEAT.task;
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.size(), 1);
		assertEquals(otherScores.get(0).size(), 0);
		assertEquals(otherScores.size(), 1);
		
		homoPrey.addObjective(new PreyLongSurvivalTimeObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 1);
		assertTrue(objectives.get(0).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 1);
		assertTrue(otherScores.get(0).isEmpty());
		
		homoPrey.addObjective(new PreyMinimizeCaughtObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(objectives.get(0).get(1) instanceof PreyLongSurvivalTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 1);
		assertTrue(otherScores.get(0).isEmpty());
		
		homoPrey.addObjective(new PreyLongSurvivalTimeObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 1);
		assertTrue(otherScores.get(0).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertEquals(otherScores.size(), 1);
		assertFalse(otherScores.get(0).isEmpty());
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(objectives.get(0).get(1) instanceof PreyLongSurvivalTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 1);
		
		homoPrey.addObjective(new PreyMinimizeCaughtObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 2);
		assertTrue(otherScores.get(0).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(otherScores.get(0).get(1) instanceof PreyLongSurvivalTimeObjective);
		assertTrue(otherScores.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(otherScores.size(), 1);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(objectives.get(0).get(1) instanceof PreyLongSurvivalTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 1);
		
		
		//Test for cooperative pred vs static prey
		objectives = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		otherScores = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		MMNEAT.task = new CooperativePredatorsVsStaticPreyTask();
		coPred = (CooperativePredatorsVsStaticPreyTask)MMNEAT.task;
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.get(1).size(), 0);
		assertEquals(objectives.get(2).size(), 0);
		assertEquals(objectives.size(), 3);
		assertEquals(otherScores.get(0).size(), 0);
		assertEquals(otherScores.get(1).size(), 0);
		assertEquals(otherScores.get(2).size(), 0);
		assertEquals(otherScores.size(), 3);
		
		coPred.getLonerTaskInstance().addObjective(new PredatorEatEachPreyQuicklyObjective(), objectives, 1);
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.get(1).size(), 1);
		assertEquals(objectives.get(2).size(), 0);
		assertTrue(objectives.get(1).get(0) instanceof PredatorEatEachPreyQuicklyObjective);
		assertEquals(objectives.size(), 3);
		assertEquals(otherScores.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		assertTrue(otherScores.get(1).isEmpty());
		assertTrue(otherScores.get(2).isEmpty());
		
		
		coPred.getLonerTaskInstance().addObjective(new PredatorCatchCloseObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 1);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		
		coPred.getLonerTaskInstance().addObjective(new PredatorMinimizeDistanceFromPreyObjective(), otherScores, 2);
		assertEquals(objectives.get(0).size(), 1);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(1).get(0) instanceof PredatorEatEachPreyQuicklyObjective);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 3);
		assertEquals(otherScores.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		assertTrue(otherScores.get(1).isEmpty());
		assertEquals(otherScores.get(2).size(), 1);
		assertTrue(otherScores.get(2).get(0) instanceof PredatorMinimizeDistanceFromPreyObjective);
		
		coPred.getLonerTaskInstance().addObjective(new PredatorCatchObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(objectives.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		
		coPred.getLonerTaskInstance().addObjective(new PredatorCatchCloseObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 1);
		assertTrue(otherScores.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PredatorCatchObjective);
		assertEquals(otherScores.size(), 3);
		assertFalse(otherScores.get(0).isEmpty());
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(objectives.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 3);
		
		coPred.getLonerTaskInstance().addObjective(new PredatorCatchObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 2);
		assertTrue(otherScores.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(otherScores.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(otherScores.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(otherScores.size(), 3);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
		assertFalse(objectives.get(0).get(0) instanceof PredatorCatchObjective);
		assertFalse(objectives.get(0).get(1) instanceof PredatorCatchCloseObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchObjective);
		assertEquals(objectives.size(), 3);
		
		//Test for cooperative prey vs static pred
		objectives = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		otherScores = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		MMNEAT.task = new CooperativePreyVsStaticPredatorsTask();
		coPrey = (CooperativePreyVsStaticPredatorsTask)MMNEAT.task;
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.get(1).size(), 0);
		assertEquals(objectives.get(2).size(), 0);
		assertEquals(objectives.size(), 3);
		assertEquals(otherScores.get(0).size(), 0);
		assertEquals(otherScores.get(1).size(), 0);
		assertEquals(otherScores.get(2).size(), 0);
		assertEquals(otherScores.size(), 3);
		
		coPrey.getLonerTaskInstance().addObjective(new PreyLongSurvivalTimeObjective(), objectives, 1);
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.get(1).size(), 1);
		assertEquals(objectives.get(2).size(), 0);
		assertTrue(objectives.get(1).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertEquals(objectives.size(), 3);
		assertEquals(otherScores.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		assertTrue(otherScores.get(1).isEmpty());
		assertTrue(otherScores.get(2).isEmpty());
		
		
		coPrey.getLonerTaskInstance().addObjective(new PreyMaximizeGameTimeObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 1);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(0).get(0) instanceof PreyMaximizeGameTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		
		coPrey.getLonerTaskInstance().addObjective(new PreyMaximizeDistanceFromPredatorsObjective(), otherScores, 2);
		assertEquals(objectives.get(0).size(), 1);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(1).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertTrue(objectives.get(0).get(0) instanceof PreyMaximizeGameTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 3);
		assertEquals(otherScores.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		assertTrue(otherScores.get(1).isEmpty());
		assertEquals(otherScores.get(2).size(), 1);
		assertTrue(otherScores.get(2).get(0) instanceof PreyMaximizeDistanceFromPredatorsObjective);
		
		coPrey.getLonerTaskInstance().addObjective(new PreyMinimizeCaughtObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PreyMaximizeGameTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(objectives.get(0).get(1) instanceof PreyMaximizeGameTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 3);
		assertTrue(otherScores.get(0).isEmpty());
		
		coPrey.getLonerTaskInstance().addObjective(new PreyMaximizeGameTimeObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 1);
		assertTrue(otherScores.get(0).get(0) instanceof PreyMaximizeGameTimeObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertEquals(otherScores.size(), 3);
		assertFalse(otherScores.get(0).isEmpty());
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PreyMaximizeGameTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(objectives.get(0).get(1) instanceof PreyMaximizeGameTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 3);
		
		coPrey.getLonerTaskInstance().addObjective(new PreyMinimizeCaughtObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 2);
		assertTrue(otherScores.get(0).get(0) instanceof PreyMaximizeGameTimeObjective);
		assertFalse(otherScores.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(otherScores.get(0).get(1) instanceof PreyMaximizeGameTimeObjective);
		assertTrue(otherScores.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(otherScores.size(), 3);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PreyMaximizeGameTimeObjective);
		assertFalse(objectives.get(0).get(0) instanceof PreyMinimizeCaughtObjective);
		assertFalse(objectives.get(0).get(1) instanceof PreyMaximizeGameTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PreyMinimizeCaughtObjective);
		assertEquals(objectives.size(), 3);
		
		//Test for homogeneous competitive pred vs prey
		objectives = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		otherScores = new ArrayList<ArrayList<GridTorusObjective<T>>>();
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		objectives.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());
		MMNEAT.task = new CompetitiveHomogeneousPredatorsVsPreyTask();
		homoComp = (CompetitiveHomogeneousPredatorsVsPreyTask)MMNEAT.task;
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.get(1).size(), 0);
		assertEquals(objectives.size(), 2);
		assertEquals(otherScores.get(0).size(), 0);
		assertEquals(otherScores.get(1).size(), 0);
		assertEquals(otherScores.size(), 2);
		
		homoComp.getLonerTaskInstance().addObjective(new PreyLongSurvivalTimeObjective(), objectives, 1);
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(1).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertEquals(objectives.size(), 2);
		assertEquals(otherScores.size(), 2);
		assertTrue(otherScores.get(0).isEmpty());
		assertTrue(otherScores.get(1).isEmpty());
		
		
		homoComp.getLonerTaskInstance().addObjective(new PredatorMinimizeGameTimeObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 1);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(0).get(0) instanceof PredatorMinimizeGameTimeObjective);
		assertEquals(objectives.size(), 2);
		assertTrue(otherScores.get(0).isEmpty());
		
		homoComp.getLonerTaskInstance().addObjective(new PreyMaximizeDistanceFromPredatorsObjective(), otherScores, 1);
		assertEquals(objectives.get(0).size(), 1);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(1).get(0) instanceof PreyLongSurvivalTimeObjective);
		assertTrue(objectives.get(0).get(0) instanceof PredatorMinimizeGameTimeObjective);
		assertEquals(objectives.size(), 2);
		assertEquals(otherScores.size(), 2);
		assertTrue(otherScores.get(0).isEmpty());
		assertEquals(otherScores.get(1).size(), 1);
		assertTrue(otherScores.get(1).get(0) instanceof PreyMaximizeDistanceFromPredatorsObjective);
		
		homoComp.getLonerTaskInstance().addObjective(new PredatorCatchCloseQuickObjective(), objectives, 0);
		assertEquals(objectives.get(0).size(), 2);
		assertEquals(objectives.get(1).size(), 1);
		assertTrue(objectives.get(0).get(0) instanceof PredatorMinimizeGameTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchCloseQuickObjective);
		assertEquals(objectives.size(), 2);
		assertTrue(otherScores.get(0).isEmpty());
		
		homoComp.getLonerTaskInstance().addObjective(new PredatorCatchObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 1);
		assertTrue(otherScores.get(0).get(0) instanceof PredatorCatchObjective);
		assertEquals(otherScores.size(), 2);
		assertFalse(otherScores.get(0).isEmpty());
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorMinimizeGameTimeObjective);
		assertEquals(objectives.size(), 2);
		
		homoComp.getLonerTaskInstance().addObjective(new PredatorEatEachPreyQuicklyObjective(), otherScores, false, 0);
		assertEquals(otherScores.get(0).size(), 2);
		assertTrue(otherScores.get(0).get(0) instanceof PredatorCatchObjective);
		assertTrue(otherScores.get(0).get(1) instanceof PredatorEatEachPreyQuicklyObjective);
		assertEquals(otherScores.size(), 2);
		assertEquals(objectives.get(0).size(), 2);
		assertTrue(objectives.get(0).get(0) instanceof PredatorMinimizeGameTimeObjective);
		assertTrue(objectives.get(0).get(1) instanceof PredatorCatchCloseQuickObjective);
		assertEquals(objectives.size(), 2);

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testNumObjectives() {
		MMNEAT.task = new TorusEvolvedPredatorsVsStaticPreyTask();
		homoPred = (TorusEvolvedPredatorsVsStaticPreyTask)MMNEAT.task;
		ArrayList<ArrayList<GridTorusObjective<T>>> objectives = homoPred.objectives;
		ArrayList<ArrayList<GridTorusObjective<T>>> otherScores = homoPred.otherScores;
		
		assertEquals(objectives.get(0).size(), 0);
		assertEquals(objectives.size(), 1);
		assertEquals(homoPred.numObjectives(), 0);
		homoPred.addObjective(new PredatorCatchCloseObjective(), objectives, 0);
		assertEquals(homoPred.numObjectives(), 1);
		homoPred.addObjective(new PredatorCatchObjective(), objectives, 0);
		assertEquals(homoPred.numObjectives(), 2);
		homoPred.addObjective(new PreyMinimizeCaughtObjective(), objectives, 0);
		assertEquals(homoPred.numObjectives(), 3);
		homoPred.addObjective(new PredatorEatEachPreyQuicklyObjective(), otherScores, 0);
		assertEquals(homoPred.numObjectives(), 3);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testNumOtherScores() {
		//NOTE: this variable depends on how many fitnesses are currently added to other scores
		//in addAllObjectives from the constructor, which is all the fitnesses
		int numOthers = 22;
		
		MMNEAT.task = new TorusEvolvedPredatorsVsStaticPreyTask();
		homoPred = (TorusEvolvedPredatorsVsStaticPreyTask)MMNEAT.task;
		ArrayList<ArrayList<GridTorusObjective<T>>> objectives = homoPred.objectives;
		ArrayList<ArrayList<GridTorusObjective<T>>> otherScores = homoPred.otherScores;
		assertEquals(otherScores.size(), 1);
		assertEquals(homoPred.numOtherScores(), numOthers);
		homoPred.addObjective(new PredatorCatchCloseObjective(), otherScores, 0);
		assertEquals(homoPred.numOtherScores(), numOthers + 1);
		homoPred.addObjective(new PredatorCatchObjective(), otherScores, 0);
		assertEquals(homoPred.numOtherScores(), numOthers + 2);
		homoPred.addObjective(new PreyMinimizeCaughtObjective(), otherScores, 0);
		assertEquals(homoPred.numOtherScores(), numOthers + 3);
		homoPred.addObjective(new PredatorEatEachPreyQuicklyObjective(), objectives, 0);
		assertEquals(homoPred.numOtherScores(), numOthers + 3);
		
	}
	
	@Test
	public void testStartingGoals() {
		
	}
	
	@Test
	public void testMinScores() {
		
	}
	
	@Test
	public void testSensorLabels() {
		
	}
	
	@Test
	public void testOutputLabels() {
		
	}
	
	@Test
	public void testGetTimeStamp() {
		
	}
	
	@Test
	public void testGetStaticControllers() {
		
	}
	
	@Test
	public void testGetEvolvedControllers() {
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testAddAllObjectives() {
		//NOTE: this variable depends on how many fitnesses are currently added to other scores
		//in addAllObjectives from the constructor, which is all the fitnesses
		int numOthers = 22;
		
		MMNEAT.task = new TorusEvolvedPredatorsVsStaticPreyTask();
		homoPred = (TorusEvolvedPredatorsVsStaticPreyTask)MMNEAT.task;
		ArrayList<ArrayList<GridTorusObjective<T>>> objectives = homoPred.objectives;
		ArrayList<ArrayList<GridTorusObjective<T>>> otherScores = homoPred.otherScores;
		//NOTE: addAllObjectives already called once in constructor
		assertEquals(objectives.size(), 1);
		assertTrue(objectives.get(0).isEmpty());
		assertFalse(otherScores.isEmpty());
		
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"torusPreys:2", "torusPredators:3",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController",
				"predatorCatchClose:true", "preyRRM:true" });
		MMNEAT.loadClasses();
		MMNEAT.task = new TorusEvolvedPredatorsVsStaticPreyTask();
		homoPred = (TorusEvolvedPredatorsVsStaticPreyTask)MMNEAT.task;
		objectives = homoPred.objectives;
		otherScores = homoPred.otherScores;
		//NOTE: addAllObjectives already called once in constructor
		assertEquals(objectives.size(), 1);
		assertFalse(objectives.get(0).isEmpty());
		assertFalse(otherScores.isEmpty());
		assertTrue(objectives.get(0).get(0) instanceof PredatorCatchCloseObjective);
	}
	
	//NOTE: HyperNEAT methods are untested

}
