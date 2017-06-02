package boardGame;

public class RawBoardGameFeatureExtractor<T extends BoardGameState> implements BoardGameFeatureExtractor<T> {

	@Override
	public double[] getFeatures(T bgs) {
		return bgs.getDescriptor();
	}

}
