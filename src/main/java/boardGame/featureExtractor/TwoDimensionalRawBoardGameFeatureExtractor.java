package boardGame.featureExtractor;

import boardGame.TwoDimensionalBoardGameState;
import edu.southwestern.MMNEAT.MMNEAT;

public class TwoDimensionalRawBoardGameFeatureExtractor<T extends TwoDimensionalBoardGameState> implements BoardGameFeatureExtractor<T> {

	T board;

	@SuppressWarnings("unchecked")
	public TwoDimensionalRawBoardGameFeatureExtractor(){
		this((T) MMNEAT.boardGame.getCurrentState());
	}
	
	public TwoDimensionalRawBoardGameFeatureExtractor(T bgs){
		board = bgs;
	}
	
	@Override
	public double[] getFeatures(T bgs) {
		return bgs.getDescriptor();
	}

	/**
	 * Simply names all features according to the coordinates associated with each space.
	 */
	@Override
	public String[] getFeatureLabels() {
		String[] result = new String[board.getBoardHeight() * board.getBoardWidth()];
		for(int j = 0; j < board.getBoardHeight(); j++) {
			for(int i = 0; i < board.getBoardWidth(); i++) {
				result[j * board.getBoardWidth() + i] = "Space ("+i+","+j+")";
			}
		}
		return result;
	}
}
