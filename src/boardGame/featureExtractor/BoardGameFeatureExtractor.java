package boardGame.featureExtractor;

import boardGame.BoardGameState;

public interface BoardGameFeatureExtractor<T extends BoardGameState> {
	
	public double[] getFeatures(T bgs);
	
	/**
	 * Returns the Feature Labels for the specific BoardGame
	 * 
	 * @return String[] containing the Feature Labels for the specific BoardGame
	 */
	public String[] getFeatureLabels();
}
