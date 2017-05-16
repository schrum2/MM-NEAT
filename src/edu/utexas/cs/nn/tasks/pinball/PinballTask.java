package edu.utexas.cs.nn.tasks.pinball;

import java.util.ArrayList;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import pinball.PinBall;
import pinball.State;

public class PinballTask<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {

	PinballViewer view = null; 
	ArrayList<Double> listOfCoordinates;

	/**
	 * Constructor for a new PinballTask
	 */
	public PinballTask(){
		MMNEAT.registerFitnessFunction("Reward");
		
        if(Parameters.parameters.booleanParameter("moPinball")){
       	 MMNEAT.registerFitnessFunction("Distance");
       }
	}

	/**
	 * Returns the number of Objectives for the PinballTask
	 * 
	 * @return 1, The number of Objectives for the PinballTask
	 */
	@Override
	public int numObjectives() {
		if(Parameters.parameters.booleanParameter("moPinball")){ // If MO, has two Objectives: Reach Goal, and Distance from Goal
			return 2;
		}else{ // Else, only Reach Goal
			return 1;			
		}
	}

	/**
	 * Returns the TimeStamp for a PinballTask
	 * 
	 * @return 0, because the TimeStamp doesn't appear useful for this task
	 */
	@Override
	public double getTimeStamp() {
		// Doesn't appear to be necessary for this Task, but may be used later.
		return 0;
	}

	/**
	 * Returns a String containing the Sensor Labels for the PinballTask
	 * 
	 * @return String containing the Sensor Labels for the PinballTask
	 */
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-Position", "Y-Position", "X-Velocity", "Y-Velocity"};
	}

	/**
	 * Returns a String containing the Output Labels for the PinballTask
	 * 
	 * @return String containing the Output Labels for the PinballTask
	 */
	@Override
	public String[] outputLabels() {
		return new String[]{"Right", "Down", "Left", "Up", "None"};
	}

	/**
	 * Clears the listOfCoordinates for Behavioral Diversity
	 */
	@Override
	public void prep() {
		listOfCoordinates = new ArrayList<Double>();
	}
	
	/**
	 * Returns the listOfCoordinates for Behavioral Diversity
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		return listOfCoordinates;
	}
	
	/**
	 * Evaluates a given individual network's Fitness;
	 * If the CommonConstants Watch variable is set to "True," runs a visual evaluation,
	 * Else runs a non-visual evaluation
	 * 
	 * @param individual Genotype<T> specifying a Network to be evaluated
	 * @param num Integer value
	 * @return Pair of Double Arrays that show the Fitness of an individual network
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		PinBall p = new PinBall("data/pinball/" + Parameters.parameters.stringParameter("pinballConfig"));


		if(CommonConstants.watch){ // If set to Visually Evaluate the Task
			if(view != null){ 
				view.dispose();
				view = null;
			}
			view = new PinballViewer(p); // Create a new PinballViewer
			view.setVisible(true); // Makes the PinballViewer visible
			view.setAlwaysOnTop(true); // Makes the PinballViewer always on top
		}

		//MiscUtil.waitForReadStringAndEnterKeyPress();

		Network n = individual.getPhenotype();
		double fitness = 0;
		int timeLimit = 1000;

		do {
			State s = p.getState();
			double[] sensors = s.getDescriptor();
			double[] outputs = n.process(sensors);
			int action = StatisticsUtilities.argmax(outputs);
			double rew = p.step(action);

			if(view != null){ // If the PinballViewer exists, update it
				view.repaint();

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Parameters.parameters.booleanParameter("stepByStep")){
					System.out.print("Press enter to continue");
					MiscUtil.waitForReadStringAndEnterKeyPress();
				}
			}	

			fitness += rew;
			timeLimit--;
		} while(!p.episodeEnd() && timeLimit > 0);

		Double distance = p.getBall().getCenter().distanceTo(p.getTarget().getCenter()); // Subtracts the distance from the Ball to the Target from the overall Fitness; getting closer means a higher score
		
		listOfCoordinates.add(p.getBall().getX());
		listOfCoordinates.add(p.getBall().getY());
		
		Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] {fitness}, new double[0]);			

		if(Parameters.parameters.booleanParameter("moPinball")){
			evalResults = new Pair<double[], double[]>(new double[] { fitness, -distance }, new double[0]); // Distance from the Target is a negative reward because we want the Distance to get smaller, not larger.
		} else {
			evalResults = new Pair<double[], double[]>(new double[] { fitness }, new double[0]);			
		}
		
		return evalResults; // Returns the Fitness of the individual's Genotype<T>
	}

}
