package boardGame.heuristics;

import boardGame.TwoDimensionalBoardGameState;

public class WeightedPieceCounterHeuristic<T extends TwoDimensionalBoardGameState> implements BoardGameHeuristic<T> {
	
	double[][] pieceWeights;
	
	public WeightedPieceCounterHeuristic(double[][] weights) {
		pieceWeights = weights;
	}

	@Override
	public double heuristicEvalution(T bgState) {
		assert bgState.getNumPlayers() == 2 : "WeightedPieceCounterHeuristic only works for two player games!";
		
		double score = 0;
		double[] description = bgState.getDescriptor();
		
		for(int i = 0; i < bgState.getBoardWidth(); i++){
			for(int j = 0; j < bgState.getBoardHeight(); j++){
				double piece = description[i*bgState.getBoardHeight()+j];
				score += piece * pieceWeights[i][j];
			}
		}
		//System.out.println(bgState + " gets " + score);
		return score;
	}

}