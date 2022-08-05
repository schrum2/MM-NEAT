package edu.southwestern.tasks.rlglue.puddleworld;

import org.rlcommunity.environments.puddleworld.PuddleWorld;
import org.rlcommunity.environments.puddleworld.PuddleWorldState;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.RLGlueTask;
import edu.southwestern.util.datastructures.Pair;

public class PuddleWorldTask<T extends Network> extends RLGlueTask<T> {
	
	private final boolean moPuddleWorld;
	
	public PuddleWorldTask() {
		super();
		boolean puddleWorld = (RLGlueTask.environment instanceof PuddleWorld);
		moPuddleWorld = puddleWorld && Parameters.parameters.booleanParameter("moPuddleWorld");
		
		if (moPuddleWorld) {
			MMNEAT.registerFitnessFunction("Time Penalty");
			MMNEAT.registerFitnessFunction("Puddle Penalty");
		}
		MMNEAT.registerFitnessFunction("RL Return");

	}
	
	@Override
	public int numOtherScores() {
		return moPuddleWorld ? 1 : 0;
	}
	
	@Override
	public int numObjectives() {
		return moPuddleWorld ? 2 : 1;
	}
	
	@Override
	public Pair<double[], double[]> episodeResult(int num){
		Pair<double[], double[]> p = new Pair<double[], double[]>(new double[] { rlReturn[num] }, new double[0]);
		if(moPuddleWorld) {
			p = new Pair<double[], double[]>(
					new double[] { PuddleWorldState.finalStepScore, PuddleWorldState.finalPuddleScore },
					new double[] { rlReturn[num] });
		}
		PuddleWorldState.finalStepScore = 0;
		PuddleWorldState.finalPuddleScore = 0;
		return p;
	}
	
	@Override
	public String[] outputLabels() {
		return new String[]{"Right", "Left", "Up", "Down"};
	}
}
