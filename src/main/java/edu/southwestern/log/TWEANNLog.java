package edu.southwestern.log;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * Log for details about an evolving population of TWEANNs
 *
 * @author Jacob Schrum
 */
public class TWEANNLog extends StatisticsLog<TWEANNGenotype> {

	/**
	 * Zero is used because TWEANN logs don't work for coevolution yet
	 */
	private static final int ARCHETYPE_INDEX = 0;

	public static ArrayList<String> getLabels() {
		String[] sensors = ( (NetworkTask) MMNEAT.task).sensorLabels();
		ArrayList<String> result = new ArrayList<String>(8 + sensors.length);

		result.add("Number of Links");
		result.add("Number of Forward Links");
		result.add("Number of Recurrent Links");
		result.add("Number of Neurons");
		result.add("Number of Modes");
		result.add("Max Mode Usage");
		result.add("Min Mode Usage");
		result.add("Mode 0 Usage");
		result.add("Mode 1 Usage");
		result.add("Mode 2 Usage");
		result.add("Mode 3 Usage");
		result.add("Archetype Nodes");

		for (int i = 0; i < sensors.length; i++) {
			result.add(sensors[i] + " Usage");
		}

		return result;
	}

	public TWEANNLog(String prefix) {
		super(prefix, getLabels());
	}

	@Override
	public void log(ArrayList<TWEANNGenotype> scores, int generation) {
		double[][] nextStage = new double[scores.size()][];
		for (int i = 0; i < scores.size(); i++) {
			nextStage[i] = new double[12 + scores.get(i).numIn];
			boolean[] inputUsage = scores.get(i).inputUsageProfile();

			TWEANNGenotype tg = scores.get(i);
			nextStage[i][0] = tg.links.size();
			nextStage[i][1] = tg.numLinks(false);
			nextStage[i][2] = tg.numLinks(true);
			nextStage[i][3] = tg.nodes.size();
			nextStage[i][4] = tg.numModules;
			nextStage[i][5] = tg.maxModuleUsage();
			nextStage[i][6] = tg.minModuleUsage();
			double[] modeUsageDistribution = StatisticsUtilities.distribution(tg.getModuleUsage());
			// Why was I printing out the module usage distribution with each generation?
                        //System.out.println(Arrays.toString(tg.getModuleUsage()));
			//System.out.println(Arrays.toString(modeUsageDistribution));

			nextStage[i][7] = modeUsageDistribution[0];
			nextStage[i][8] = modeUsageDistribution.length > 1 ? modeUsageDistribution[1] : 0;
			nextStage[i][9] = modeUsageDistribution.length > 2 ? modeUsageDistribution[2] : 0;
			nextStage[i][10] = modeUsageDistribution.length > 3 ? modeUsageDistribution[3] : 0;
			nextStage[i][11] = EvolutionaryHistory.archetypeSize(ARCHETYPE_INDEX);
			for (int j = 0; j < inputUsage.length; j++) {
				nextStage[i][12 + j] = inputUsage[j] ? 1.0 : 0.0;
			}
		}
		logAverages(nextStage, generation);
	}
}
