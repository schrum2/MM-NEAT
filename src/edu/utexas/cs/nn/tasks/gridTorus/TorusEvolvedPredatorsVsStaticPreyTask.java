package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchCloseObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchCloseQuickObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorCatchObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorEatEachPreyQuicklyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorMinimizeDistanceFromIndividualPreyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorMinimizeDistanceFromPreyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorMinimizeGameTimeObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PredatorRawalRajagopalanMiikkulainenObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.cooperative.IndividualPredatorMinimizeDistanceFromIndividualPreyObjective;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.cooperative.IndividualPredatorMinimizeDistanceFromPreyObjective;

/**
 * The following class sets up tasks for
 * learning agents and NPCs. This class is for a task where the
 * predators are evolved while the prey are kept static
 * 
 * @author Alex Rollins, Jacob Schrum 
 * @param <T> evolved phenotype
 */
public class TorusEvolvedPredatorsVsStaticPreyTask<T extends Network> extends TorusPredPreyTask<T> {

	private TorusPredPreyController[] staticAgents = null;

	/**
	 * constructor for a task where the predators are evolved while the prey are
	 * kept static sends false to the parent constructor, indicating that the
	 * predator is the agent evolving Includes all of the fitness scores that
	 * the user wants from the command line parameters
	 */
	public TorusEvolvedPredatorsVsStaticPreyTask() {
		super(false);
		if (Parameters.parameters.booleanParameter("predatorMinimizeTotalTime"))
			addObjective(new PredatorMinimizeGameTimeObjective<T>(), objectives);
		if (Parameters.parameters.booleanParameter("predatorsEatQuick"))
			addObjective(new PredatorEatEachPreyQuicklyObjective<T>(), objectives);
		if (Parameters.parameters.booleanParameter("predatorMinimizeDistance"))
			addObjective(new PredatorMinimizeDistanceFromPreyObjective<T>(), objectives);
		if (Parameters.parameters.booleanParameter("predatorRRM"))
			addObjective(new PredatorRawalRajagopalanMiikkulainenObjective<T>(), objectives);
		if (Parameters.parameters.booleanParameter("predatorCatchClose"))
			addObjective(new PredatorCatchCloseObjective<T>(), objectives);
		if (Parameters.parameters.booleanParameter("predatorCatch"))
			addObjective(new PredatorCatchObjective<T>(), objectives);
		if (Parameters.parameters.booleanParameter("predatorCatchCloseQuick"))
			addObjective(new PredatorCatchCloseQuickObjective<T>(), objectives);
		if (Parameters.parameters.booleanParameter("predatorMinimizeIndividualDistance")){
			//get separate distance fitness functions for each prey and add them as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), objectives);
			}
		}
		if (Parameters.parameters.booleanParameter("indivPredMinDist")){
			//get separate distance fitness functions for each predator and add them as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				addObjective(new IndividualPredatorMinimizeDistanceFromPreyObjective<T>(i), objectives);
			}
		}
		if (Parameters.parameters.booleanParameter("indivPredMinDistIndivPrey")){
			//get separate distance fitness functions for each predator to each prey individually and add them all as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), objectives);
				}
			}
		}


		//add other scores to be able to show each fitness score even if it's not effecting evolution
		addObjective(new PredatorMinimizeGameTimeObjective<T>(), otherScores, false);
		addObjective(new PredatorEatEachPreyQuicklyObjective<T>(), otherScores, false);
		addObjective(new PredatorMinimizeDistanceFromPreyObjective<T>(), otherScores, false);
		if(Parameters.parameters.integerParameter("torusPreys") == 2)
			addObjective(new PredatorRawalRajagopalanMiikkulainenObjective<T>(), otherScores, false);
		addObjective(new PredatorCatchCloseObjective<T>(), otherScores, false);
		addObjective(new PredatorCatchObjective<T>(), otherScores, false);
		addObjective(new PredatorCatchCloseQuickObjective<T>(), otherScores, false);
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
			addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), otherScores, false);
		}
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
			addObjective(new IndividualPredatorMinimizeDistanceFromPreyObjective<T>(i), otherScores, false);
		}
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
			for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
				addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), otherScores, false);
			}
		}

	}

	/**
	 * A method that gives a list of controllers for the evolving agents
	 * (predators) The predators are all defined as a new agent of the given
	 * genotype with an evolved controller The user also indicates in a command
	 * line parameter how many predators there will be (default of 4)
	 * 
	 * @return evolvedAgents a list of controllers for the evolved agents for
	 *         this class, which is the predators
	 * @param individual
	 *            the genotype that will be given to all predator agents
	 *            (homogeneous team)
	 */
	@Override
	public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
		evolved = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPredators")];
		getEvolvedControllers(evolved, individual, true);
		return evolved;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the static agents (prey)
	 * The prey are all given a simple, non-evolving controller (specified by
	 * user) The user also indicates in a command line parameter how many prey
	 * there will be (default of 1)
	 * 
	 * @return staticAgents a list of controllers for the static agents for this
	 *         class, which is the prey (static meaning the agent type that is
	 *         chosen by the user to not evolve)
	 * @param individual
	 *            the genotype that will be given to all prey agents
	 *            (homogeneous team)
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
		if (staticAgents == null)
			staticAgents = getStaticControllers(false,Parameters.parameters.integerParameter("torusPreys"));

		return staticAgents;
	}
}
