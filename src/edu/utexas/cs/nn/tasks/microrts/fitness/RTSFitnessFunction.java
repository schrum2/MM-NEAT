package edu.utexas.cs.nn.tasks.microrts.fitness;

import java.util.ArrayList;

import edu.utexas.cs.nn.tasks.microrts.MicroRTSInformation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;

/**
 * 
 * TODO: Comment describing this class
 * 
 * @author 
 *
 */
public abstract class RTSFitnessFunction {
	
	protected final int RESULTRANGE = 2;
	protected int maxCycles;
	protected PhysicalGameState pgs = null;
	protected MicroRTSInformation task = null;
	protected boolean coevolution;
	
	/**
	 * judges an individual agent's fitness
	 * @param gs
	 * @return fitness of an organism
	 */
	public abstract ArrayList<Pair<double[], double[]>> getFitness(GameState gs);
	public abstract String[] getFunctions();
	
	public void givePhysicalGameState(PhysicalGameState pgs){
		this.pgs = pgs;
	}
	
	public void setMaxCycles(int maxCycles){
		this.maxCycles = maxCycles;
	}
	
	public void giveTask(MicroRTSInformation task) {
		this.task = task;
	}
	public void setCoevolution(boolean b) {
		coevolution = b;
	}

}
