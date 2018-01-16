package edu.southwestern.tasks.mario;

import java.util.List;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import competition.cig.robinbaumgarten.AStarAgent;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;
import edu.southwestern.util.datastructures.Pair;

/**
 * 
 * Evolve Mario levels with CPPNs using an agent,
 * like the Mario A* Agent, as a means of evaluating
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
public class MarioLevelTask<T extends Network> extends NoisyLonerTask<T> {

	private Agent agent;
	
	public MarioLevelTask() {
		// Replace this with a command line parameter
		agent = new AStarAgent();
		
		// Fitness
        MMNEAT.registerFitnessFunction("ProgressPlusTime");
        // Other scores
        MMNEAT.registerFitnessFunction("Distance", false);
        MMNEAT.registerFitnessFunction("Time", false);

	}
	
	@Override
	public int numObjectives() {
		// First maximize progress through the level.
		// If the level is cleared, then maximize the duration of the
		// level, which will indicate that it is challenging.
		return 1;  
	}
	
	public int numOtherScores() {
		return 0;
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		Network cppn = individual.getPhenotype();
		Level level = MarioLevelUtil.generateLevelFromCPPN(cppn, Parameters.parameters.integerParameter("marioLevelLength"));
		agent.reset(); // Get ready to play a new level
		EvaluationOptions options = new CmdLineOptions(new String[]{});
		options.setAgent(agent);
        options.setLevel(level);
        options.setMaxFPS(true);
        options.setVisualization(CommonConstants.watch);
		List<EvaluationInfo> infos = MarioLevelUtil.agentPlaysLevel(options);
		// For now, assume a single evaluation
		EvaluationInfo info = infos.get(0);
		double distancePassed = info.lengthOfLevelPassedPhys;
		double totalDistanceInLevel = info.totalLengthOfLevelPhys;
		double time = info.timeSpentOnLevel;

		double[] otherScores = new double[] {distancePassed, time};
		if(distancePassed < totalDistanceInLevel) {
			// If level is not completed, score the amount of distance covered
			return new Pair<double[],double[]>(new double[]{distancePassed}, otherScores);
		} else {
			// Add in the time so that more complicated, challenging levels will be favored
			return new Pair<double[],double[]>(new double[]{distancePassed+time}, otherScores);
		}
	}

}
