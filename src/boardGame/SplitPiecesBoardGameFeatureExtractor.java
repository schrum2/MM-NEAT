package boardGame;

import java.util.ArrayList;
import java.util.List;

public class SplitPiecesBoardGameFeatureExtractor<T extends BoardGameState> implements BoardGameFeatureExtractor<T> {

	static final double EMPTY = -1; // Value typically used to represent an Empty Space in the Board Games
	
	@Override
	public double[] getFeatures(T bgs) {

		double[] description = bgs.getDescriptor(); // Stores a Description of the Board Game
		double[] result = new double[bgs.getNumPlayers()*description.length]; // Stores the Pieces for every Player; each Player has a segment equal to the length of the Description
		
		List<Double> usedPieces = new ArrayList<Double>(); // Keeps track of which Pieces have been extracted
		
		int resultIndex = 0; // Keeps track of which Index in result to add Pieces to
		
		do{ // Adds the Pieces from the Board to the result Array
		
		double currentPiece = EMPTY; // Used to keep track of which piece is being extracted; initialized to EMPTY

		for(double d : description){ // Cycles through the Description and adds to the result Array

			if(currentPiece == EMPTY && !usedPieces.contains(d)){ // Hasn't been changed yet, and hasn't been used before
				currentPiece = d;
				usedPieces.add(d); // Adds the value to usedPieces; unable to re-use this value
			}
			
			if(d == EMPTY || d == currentPiece) result[resultIndex++] = d; // If Empty Space or currentPiece, add it to result
		}
		
		}while(resultIndex < result.length); // Keeps adding to result until result is full
		
		return result;
	}

}