package edu.southwestern.tasks.gvgai.zelda;

import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.util.datastructures.Pair;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLRegistry;

public abstract class ZeldaLevelTask<T> extends NoisyLonerTask<T> {

	public ZeldaLevelTask() {
		MMNEAT.registerFitnessFunction("MaxDistance");
		
		if(CommonConstants.watch) {
			VGDLFactory.GetInstance().init(); // Get an instant of VGDL Factor and initialize the characters cache
			VGDLRegistry.GetInstance().init(); // Get an instance of VGDL Registry and initialize the sprite factory
		}
	}
	
	@Override
	public int numObjectives() {
		// longest shortest path distance of zelda level
		return 1;  
	}
	
	public int numOtherScores() {
		return 0; // Not used
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}
	
	public abstract List<List<Integer>> getZeldaLevelFromGenotype(Genotype<T> individual);
	
	public abstract GameBundle getBundleFromGenotype(Genotype<T> individual);
	
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		List<List<Integer>> room = getZeldaLevelFromGenotype(individual);
		int[][] level = ZeldaLevelUtil.listToArray(room);
		double maxDistance = ZeldaLevelUtil.findMaxDistanceOfLevel(level, 8, 8);
		
		if(CommonConstants.watch) {
			GameBundle bundle = getBundleFromGenotype(individual);
			GVGAIUtil.runOneGame(bundle, true);
		}
		
		return new Pair<double[], double[]>(new double[]{maxDistance}, new double[0]);
	}
}
