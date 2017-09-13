package edu.southwestern.tasks.innovationengines;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;

public class PictureInnovationTask<T extends Network> extends LonerTask<T> {

	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return 0; // Should this be 1 or 1000?
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		// TODO Auto-generated method stub
		return null; // Use ImageNet
	}

}
