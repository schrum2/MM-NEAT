package edu.utexas.cs.nn.tasks.microrts.fitness;

import edu.utexas.cs.nn.tasks.microrts.MicroRTSInformation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;

public abstract class RTSFitnessFunction {
	
	protected final int RESULTRANGE = 2;
	protected int maxCycles;
	protected PhysicalGameState pgs = null;
	protected MicroRTSInformation task = null;
	
	/**
	 * judges an individual agent's fitness
	 * @param gs
	 * @return fitness of an organism
	 */
	public abstract Pair<double[], double[]> getFitness(GameState gs);
	public abstract String[] getFunctions();
	
	public void givePhysicalGameState(PhysicalGameState pgs){
		this.pgs = pgs;
	}
	
	public void setMaxCycles(int maxCycles){
		this.maxCycles = maxCycles;
	}
	
	@SuppressWarnings("rawtypes") //doesn't need to know phenotype
	public void giveTask(MicroRTSInformation task) {
		this.task = task;
	}

}
