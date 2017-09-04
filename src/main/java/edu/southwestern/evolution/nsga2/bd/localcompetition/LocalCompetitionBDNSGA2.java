package edu.southwestern.evolution.nsga2.bd.localcompetition;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.nsga2.bd.BDNSGA2;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.scores.MultiObjectiveScore;
import edu.southwestern.scores.Score;
import edu.southwestern.util.ClassCreation;
import java.util.ArrayList;

/**
 * This class is based on some ideas from Joel Lehman's dissertation:
 * http://joellehman.com/ The idea is to put members of the population into
 * niches based on their behavior, and only have them compete locally against
 * similar individuals.
 *
 * @author Jacob Schrum
 */
public class LocalCompetitionBDNSGA2<T> extends BDNSGA2<T> {

	private MMNEATLog nicheLog;
	private NicheDefinition<T> nicheDefinition;

	// Track number of niches and sizes
	@SuppressWarnings("unchecked")
	public LocalCompetitionBDNSGA2() {
		try {
			nicheDefinition = (NicheDefinition<T>) ClassCreation.createObject("nicheDefinition");
		} catch (NoSuchMethodException ex) {
			System.out.println("Don't know how to define niche");
			ex.printStackTrace();
			System.exit(1);
		}
		if (writeOutput) {
			nicheLog = new MMNEATLog("Niches");
		}
	}

	@Override
	public ArrayList<Score<T>> prepareSourcePopulation(ArrayList<Score<T>> parentScores,
			ArrayList<Score<T>> childrenScores) {
		// BDNSGA2 addes diversity scores as a final objective to each score
		ArrayList<Score<T>> originalPopulation = super.prepareSourcePopulation(parentScores, childrenScores);
		// Give population to niche definition
		nicheDefinition.loadPopulation(originalPopulation);
		// Determine each niche
		ArrayList<ArrayList<Score<T>>> niches = new ArrayList<ArrayList<Score<T>>>(originalPopulation.size());
		for (int i = 0; i < originalPopulation.size(); i++) {
			ArrayList<Score<T>> niche = nicheDefinition.getNiche(originalPopulation.get(i));
			niches.add(niche);
		}
		// Now that niches are calculated, remove the BD scores, but put them in
		// the new score
		ArrayList<Score<T>> localCompetitonPopulation = new ArrayList<Score<T>>(originalPopulation.size());
		for (int i = 0; i < originalPopulation.size(); i++) {
			Score<T> s = originalPopulation.get(i);
			// Start new score with just BD
			MultiObjectiveScore<T> mos = new MultiObjectiveScore<T>(s.individual,
					new double[] { s.scores[s.scores.length - 1] }, null, null);
			localCompetitonPopulation.add(mos);
			// Drop BD from old score
			s.dropLastScore();
		}
		// Now get niche dominance count for each individual
		for (int i = 0; i < originalPopulation.size(); i++) {
			Score<T> s = originalPopulation.get(i);
			int nicheDominationCount = 0;
			ArrayList<Score<T>> niche = niches.get(i);
			for (Score<T> n : niche) {
				if (s.isAtLeastAsGood(n)) {
					nicheDominationCount++;
				}
			}
			// Add niche domination ratio to score
			double ratio = (nicheDominationCount * 1.0) / niche.size();
			nicheLog.log(s.individual.getId() + "\t" + nicheDominationCount + "\t" + niche.size() + "\t" + ratio);
			localCompetitonPopulation.get(i).extraScore(ratio);
		}
		nicheLog.log("---Gen " + generation + " Over-----------------");
		// Pair of BD and local dominance count will be used for selection
		return localCompetitonPopulation;
	}

	@Override
	public void close(ArrayList<Genotype<T>> population) {
		super.close(population);
		if (writeOutput) {
			this.nicheLog.close();
		}
	}
}
