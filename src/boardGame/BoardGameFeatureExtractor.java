package boardGame;

public interface BoardGameFeatureExtractor<T extends BoardGameState> {
	
	public double[] getFeatures(T bgs);
	
}
