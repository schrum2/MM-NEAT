package edu.southwestern.evolution.mapelites;

import edu.southwestern.scores.Score;

public interface BinMapping<T> {
	public String binForScore(Score<T> s);
}
