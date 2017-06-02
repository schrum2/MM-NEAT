package boardGame;

import java.util.ArrayList;
import java.util.List;

public class SplitPiecesBoardGameFeatureExtractor<T extends BoardGameState> implements BoardGameFeatureExtractor<T> {

	@Override
	public double[] getFeatures(T bgs) {

		double[] description = bgs.getDescriptor();
		double[] result = new double[bgs.getNumPlayers()*description.length]; // Add the pieces for every Player
		
		List<Double> usedPieces = new ArrayList<Double>();
		usedPieces.add((double) -1);
		
		int resultIndex = 0;
		
		do{
		
		double currentPiece = -1; // Used to keep track of which piece is being extracted

		for(double d : description){

			if(currentPiece == -1 && !usedPieces.contains(d)){ // Hasn't been changed yet, and hasn't been used before
				currentPiece = d;
				usedPieces.add(d); // Adds the value to usedPieces; unable to re-use this value
			}
			
			if(d == -1 || d == currentPiece) result[resultIndex++] = d; // If Empty Space or currentPiece, add it to result
		}
		
		}while(resultIndex < result.length); // Keeps adding to result until result is full
		
		return result;
	}

}