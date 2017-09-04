package edu.southwestern.boardGame.heuristics;

import edu.southwestern.boardGame.TwoDimensionalBoardGameState;

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
		
		for(int i = 0; i < bgState.getBoardHeight(); i++){
			for(int j = 0; j < bgState.getBoardWidth(); j++){
				double piece = description[i*bgState.getBoardWidth()+j];
				score += piece * pieceWeights[i][j];
			}
		}
		//System.out.println(bgState + " gets " + score);
		return score;
	}

}