package edu.southwestern.tasks.gvgai.zelda;

import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.Pair;

public abstract class ZeldaLevelTask<T> extends NoisyLonerTask<T> {

	public ZeldaLevelTask() {
		MMNEAT.registerFitnessFunction("MaxDistanceSouth");
		MMNEAT.registerFitnessFunction("MaxDistanceNorth");
		MMNEAT.registerFitnessFunction("MaxDistanceEast");
		MMNEAT.registerFitnessFunction("MaxDistanceWest");		
	}
	
	@Override
	public int numObjectives() {
		// longest shortest path distance of zelda level
		return 4;  
	}
	
	public int numOtherScores() {
		return 0; // Not used
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}
	
	public abstract List<List<Integer>> getZeldaLevelFromGenotype(Genotype<T> individual);
	
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		List<List<Integer>> room = getZeldaLevelFromGenotype(individual);
		int[][] level = ZeldaLevelUtil.listToArray(room);
		double maxDistanceS = ZeldaLevelUtil.findMaxDistanceOfLevel(level, 8, 8);
		double maxDistanceN = ZeldaLevelUtil.findMaxDistanceOfLevel(level, 8, 2);
		double maxDistanceW = ZeldaLevelUtil.findMaxDistanceOfLevel(level, 2, 6);
		double maxDistanceE = ZeldaLevelUtil.findMaxDistanceOfLevel(level, 13, 6);
		
		return new Pair<double[], double[]>(new double[]{maxDistanceS, maxDistanceN, maxDistanceE, maxDistanceW}, new double[0]);
	}
	
	@Override
	public void postConstructionInitialization() {
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
	}
}
