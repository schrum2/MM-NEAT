package edu.southwestern.tasks.microrts.fitness;

import java.util.ArrayList;

import edu.southwestern.tasks.microrts.MicroRTSInformation;
import edu.southwestern.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;

/**
 * 
 * Parent class for all fitness functions usable in MicroRTSTask or similar
 * 
 * @author alicequint 
 *
 */
public abstract class RTSFitnessFunction {
	
	protected final int RESULTRANGE = 2;
	protected int maxCycles;
	//The state of the game, including map information, unit information, and player information
	protected PhysicalGameState pgs = null;
	//See MicroRTSInformation; Classes that implement MicroRTSInformation have methods that measure fitness
	protected MicroRTSInformation task = null;
	//Are we coevolving?
	protected boolean coevolution;
	//number of different maps per evaluation
	protected int mapSwitches = 0;
	//number of different enemies per evaluation
	protected int enemySwitches = 0;
	protected int numEvals = -1;
	protected int gameEndTime; // for progressive fitness function, to scale timestamps of events to game length
	
	/**
	 * Judges an individual agent's fitness. Each Pair represents one network, where the first double
	 * is it's fitness scores and the second double is it's other scores. The ArrayList of Pairs represents
	 * the population.
	 * @param gs, a game state
	 * @return fitness of an organism
	 */
	public abstract ArrayList<Pair<double[], double[]>> getFitness(GameState gs);
	
	//Gets the functions that are used to measure fitness
	public abstract String[] getFunctions();
	
	//retrieves the current state of the game and stores it in pgs.
	public void givePhysicalGameState(PhysicalGameState pgs){
		this.pgs = pgs;
	}
	
	//Setter function for maxCycles
	public void setMaxCycles(int maxCycles){
		this.maxCycles = maxCycles;
	}
	
	
	public void giveTask(MicroRTSInformation task) {
		this.task = task;
	}
	
	//Set boolean coevolution to true or false
	public void setCoevolution(boolean b) {
		coevolution = b;
	}
	
	//Other scores are those that don't affect fitness but still should be measured.
	//This is to be defined in a class extending RTSFitnessFunction
	public abstract String[] getOtherScores();
	
	//Keep track of number of times map has switched by incrementing mapSwitches
	public void informOfMapSwitch(){
		mapSwitches++;
	}
	
	//Keep track of number of times enemy has switched by incrementing enemySwitches
	public void informOfEnemySwitch(){
		enemySwitches++;
	}
	
	//Setter for integer numEvals
	public void setNumEvals(int num){
		numEvals = num;
	}
	
	//Getter for boolean coevolution
	public boolean getCoevolution(){
		return coevolution;
	}
	
	//Setter for gameEndTime. 
	public void setGameEndTime(int time) {
		gameEndTime = time;
	}

}
