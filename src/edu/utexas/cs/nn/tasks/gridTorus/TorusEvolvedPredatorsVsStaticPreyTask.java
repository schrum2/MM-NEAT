package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.graphics.Plot;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchCloseObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorEatEachPreyQuicklyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorMinimizeDistanceFromPreyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorMinimizeGameTimeObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorRawalRajagopalanMiikkulainenObjective;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 *
 * @author Alex Rollins, Jacob Schrum
 * The following class sets up tasks for learning agents and NPCs.
 * This class is for a task where the predators are evolved while the prey are kept static
 */
public class TorusEvolvedPredatorsVsStaticPreyTask<T extends Network> extends TorusPredPreyTask<T> {

	private TorusPredPreyController[] staticAgents = null;

	/**
	 * constructor for a task where the predators are evolved while the prey are kept static
	 * sends false to the parent constructor, indicating that the predator is the agent evolving
	 * Includes all of the fitness scores that the user wants from the command line parameters
	 */
	public TorusEvolvedPredatorsVsStaticPreyTask() {
		super(false); 
		if(Parameters.parameters.booleanParameter("predatorMinimizeTotalTime"))
			addObjective(new PredatorMinimizeGameTimeObjective<T>(), objectives);
		if(Parameters.parameters.booleanParameter("predatorsEatQuick"))
			addObjective(new PredatorEatEachPreyQuicklyObjective<T>(), objectives);
		if(Parameters.parameters.booleanParameter("predatorMinimizeDistance"))
			addObjective(new PredatorMinimizeDistanceFromPreyObjective<T>(), objectives);
		if(Parameters.parameters.booleanParameter("predatorRRM"))
			addObjective(new PredatorRawalRajagopalanMiikkulainenObjective<T>(), objectives);
		if(Parameters.parameters.booleanParameter("predatorCatchClose"))
			addObjective(new PredatorCatchCloseObjective<T>(), objectives);
		if(Parameters.parameters.booleanParameter("predatorCatch"))
			addObjective(new PredatorCatchObjective<T>(), objectives);
	}

	@Override
	/**
	 * A method that gives a list of controllers for the evolving agents (predators)
	 * The predators are all defined as a new agent of the given genotype with an evolved controller 
	 * The user also indicates in a command line parameter how many predators there will be (default of 4)
	 * @return evolvedAgents a list of controllers for the evolved agents for this class, which is the predators 
	 * @param individual the genotype that will be given to all predator agents (homogeneous team)
	 */
	public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
		int numPredators = Parameters.parameters.integerParameter("torusPredators");
		evolved = new TorusPredPreyController[numPredators];    	
		for(int i = 0; i < numPredators; i++){
			//true to indicate that this is a predator
			evolved[i] = new NNTorusPredPreyAgent<T>(individual, true).getController(); 
			// if requested, adds visual panels for each of the evolved agents showing its inputs
			// (offsets to other agents), outputs (possible directional movements), and game time
			if(CommonConstants.monitorInputs) {
				DrawingPanel panel = new DrawingPanel(Plot.BROWSE_DIM, (int) (Plot.BROWSE_DIM * 3.5), "Predator " + i);
				((NNTorusPredPreyController) evolved[i]).networkInputs = panel;
				panel.setLocation(i * (Plot.BROWSE_DIM + 10), 0);
				Offspring.fillInputs(panel, (TWEANNGenotype) individual);
			}
		} 
		return evolved;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the static agents (prey)
	 * The prey are all given a simple, non-evolving controller (specified by user)
	 * The user also indicates in a command line parameter how many prey there will be (default of 1)
	 * @return staticAgents a list of controllers for the static agents for this class,
	 * which is the prey (static meaning the agent type that is chosen by the user to not evolve)
	 * @param individual the genotype that will be given to all prey agents (homogeneous team)
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
		if(staticAgents == null) {
			int numPrey = Parameters.parameters.integerParameter("torusPreys"); 
			staticAgents = new TorusPredPreyController[numPrey];
			for(int i = 0; i < numPrey; i++) {
				try {
					staticAgents[i] = (TorusPredPreyController) ClassCreation.createObject("staticPreyController");
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
					System.out.println("Could not load static prey");
					System.exit(1);
				} 
			}
		}
		return staticAgents;
	}
}

