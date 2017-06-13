package boardGame.featureExtractor;

import boardGame.BoardGameState;

public interface BoardGameFeatureExtractor<T extends BoardGameState> {
	
	/**
	 * Returns a Double Array representing a given BoardGameState
	 * 
	 * @param bgs BoardGameState to be evaluated
	 * @return Double Array representing the given BoardGameState
	 */
	public double[] getFeatures(T bgs);
	
	/**
	 * Returns the Feature Labels for the specific BoardGame
	 * 
	 * @return String[] containing the Feature Labels for the specific BoardGame
	 */
	public String[] getFeatureLabels();
}
