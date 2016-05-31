package edu.utexas.cs.nn.tasks.rlglue.puddleworld;

import org.rlcommunity.environments.puddleworld.PuddleWorld;
import org.rlcommunity.environments.puddleworld.PuddleWorldState;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class PuddleWorldTask<T extends Network> extends RLGlueTask<T> {
	
	private final boolean moPuddleWorld;
	
	public PuddleWorldTask() {
		super();
		boolean puddleWorld = (MMNEAT.rlGlueEnvironment instanceof PuddleWorld);
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
