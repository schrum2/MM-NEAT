package edu.utexas.cs.nn.tasks.gridTorus;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.competitive.CompetitiveHomogeneousPredatorsVsPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativePredatorsVsStaticPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.cooperative.CooperativePreyVsStaticPredatorsTask;
import edu.utexas.cs.nn.tasks.gridTorus.cooperativeAndCompetitive.CompetitiveAndCooperativePredatorsVsPreyTask;

public class TorusPredPreyTaskTest {

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:3",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController" });
		MMNEAT.loadClasses();
	}

	@Test
	public void testConstructors() {
		MMNEAT.task = new TorusEvolvedPredatorsVsStaticPreyTask();
		TorusEvolvedPredatorsVsStaticPreyTask task = (TorusEvolvedPredatorsVsStaticPreyTask)MMNEAT.task;
		assertEquals(task.preyEvolve, false);
		assertEquals(task.competitive, false);
		assertEquals(task.objectives.size(), 1);
		
		MMNEAT.task = new TorusEvolvedPreyVsStaticPredatorsTask();
		TorusEvolvedPreyVsStaticPredatorsTask task2 = (TorusEvolvedPreyVsStaticPredatorsTask)MMNEAT.task;
		assertEquals(task2.preyEvolve, true);
		assertEquals(task2.competitive, false);
		assertEquals(task2.objectives.size(), 1);
		
		MMNEAT.task = new CooperativePredatorsVsStaticPreyTask();
		CooperativePredatorsVsStaticPreyTask task3 = (CooperativePredatorsVsStaticPreyTask)MMNEAT.task;
		assertEquals(task3.getLonerTaskInstance().preyEvolve, false);
		assertEquals(task3.getLonerTaskInstance().competitive, false);
		assertEquals(task3.getLonerTaskInstance().objectives.size(), 3);
		assertEquals(task3.getLonerTaskInstance().objectives.size(), task3.numberOfPopulations());
		
		MMNEAT.task = new CooperativePreyVsStaticPredatorsTask();
		CooperativePreyVsStaticPredatorsTask task4 = (CooperativePreyVsStaticPredatorsTask)MMNEAT.task;
		assertEquals(task4.getLonerTaskInstance().preyEvolve, true);
		assertEquals(task4.getLonerTaskInstance().competitive, false);
		assertEquals(task4.getLonerTaskInstance().objectives.size(), 2);
		assertEquals(task4.getLonerTaskInstance().objectives.size(), task4.numberOfPopulations());
		
		MMNEAT.task = new CompetitiveHomogeneousPredatorsVsPreyTask();
		CompetitiveHomogeneousPredatorsVsPreyTask task5 = (CompetitiveHomogeneousPredatorsVsPreyTask)MMNEAT.task;
		assertEquals(task5.getLonerTaskInstance().competitive, true);
		assertEquals(task5.getLonerTaskInstance().objectives.size(), 2);
		assertEquals(task5.getLonerTaskInstance().objectives.size(), task5.numberOfPopulations());
		
		MMNEAT.task = new CompetitiveAndCooperativePredatorsVsPreyTask();
		CompetitiveAndCooperativePredatorsVsPreyTask task6 = (CompetitiveAndCooperativePredatorsVsPreyTask)MMNEAT.task;
		assertEquals(task6.getLonerTaskInstance().competitive, true);
		assertEquals(task6.getLonerTaskInstance().objectives.size(), 5);
		assertEquals(task6.getLonerTaskInstance().objectives.size(), task6.numberOfPopulations());
	}

}
