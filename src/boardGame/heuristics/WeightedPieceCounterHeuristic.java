package boardGame.heuristics;

import boardGame.TwoDimensionalBoardGameState;

public class WeightedPieceCounterHeuristic<T extends TwoDimensionalBoardGameState> implements BoardGameHeuristic<T> {
	
	double[][] pieceWeights;
	
	public WeightedPieceCounterHeuristic(double[][] weights) {
		pieceWeights = weights;
	}

	@Override
	public double heuristicEvalution(T bgState) {
		
		double fitness = 0;
		double[] description = bgState.getDescriptor();
		
		for(int i = 0; i < bgState.getBoardWidth(); i++){
			for(int j = 0; j < bgState.getBoardHeight(); j++){
				
				if(description[i+j] == bgState.getCurrentPlayer()){ // Found Player Chip at a given Space
					fitness += pieceWeights[i][j]; // Adds the Weight on the given Space
				}else if(description[i+j] == (bgState.getCurrentPlayer() + 1) % 2){ // Found Enemy Chip at a given Space
					fitness -= pieceWeights[i][j]; // Subtracts the Weight on the given Space
				}
				
			}
		}
		
		return fitness;
	}

}