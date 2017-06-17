package boardGame.heuristics;

import boardGame.BoardGameState;

/**
 * Heuristics based on:
 * 
 * Evolutionary-based Heuristic Generators for
 * Checkers and Give-Away Checkers
 * 
 * (Jacek Mandziuk, Magdalena Kusiak, and Karol Waledzik)
 * 
 * https://pdfs.semanticscholar.org/91c9/d140267f3b008d00b330b6b0e9182fa4b62e.pdf
 * 
 * @author johnso17
 *
 */
public class StaticCheckersHeuristic<T extends BoardGameState> implements BoardGameHeuristic<T> {

	private static final int KING_BONUS = 2; // CheckersState is designed so that the value of a player's King is that player's Index + 2
	
	@Override
	public double heuristicEvalution(BoardGameState bgState) {
		// List of Piece Values to be used
		int playerPawn = bgState.getCurrentPlayer();
		int playerKing = playerPawn + KING_BONUS;
		
		int opponentPawn = (bgState.getCurrentPlayer() + 1) % 2;
		int opponentKing = opponentPawn + KING_BONUS;
		
		
		
		// List of Parameters to be kept track of
		int playPawns = 0; // Number of Player's Pawns
		int playKings = 0; // Number of Player's Kings
		int playSafePawns = 0; // Number of non-capturable Player's Pawns
		int playSafeKings = 0; // Number of non-capturable Player's Kings
		int playMovePawns = 0; // Number of Player's Pawns able to make a Non-Jump Move
		int playMoveKings = 0; // Number of Player's Kings able to make a Non-Jump Move
		
		
		int oppPawns = 0; // Number of Opponent's Pawns
		int oppKings = 0; // Number of Opponent's Kings
		int oppSafePawns = 0; // Number of non-capturable Opponent's Pawns
		int oppSafeKings = 0; // Number of non-capturable Opponent's Kings
		int oppMovePawns = 0; // Number of Opponents's Pawns able to make a Non-Jump Move
		int oppMoveKings = 0; // Number of Opponent's Kings able to make a Non-Jump Move
		
		
		int playTotalPawnDist = 0; // Aggregated distance of Player's Pawns to the Promotion Line
		int playOpenPromoFields = 0; // Total number of Promotion Fields open to the Player
		
		int oppTotalPawnDist = 0; // Aggregated distance of Opponent's Pawns to the Promotion Line
		int oppOpenPromoFields = 0; // Total number of Promotion Fields open to the Opponent
		
		int playDefendPieces = 0;
		int oppDefendPieces = 0;
		int playAttackPawns = 0;
		int oppAttackPawns = 0;

		int playCenterPawns = 0;
		int oppCenterPawns = 0;
		int playCenterKings = 0;
		int oppCenterKings = 0;
		
		int playMainDiagPawns = 0;
		int oppMainDiagPawns = 0;
		int playMainDiagKings = 0;
		int oppMainDiagKings = 0;
		
		int playDoubleDiagPawns = 0;
		int oppDoubleDiagPawns = 0;
		int playDoubleDiagKings = 0;
		int oppDoubleDiagKings = 0;

		int playLonePawns = 0;
		int oppLonePawns = 0;
		int playLoneKings = 0;
		int oppLoneKings = 0;
		
		
		int numHoles = 0; // (Empty Squares adjacent to at least 3 Pieces of the same color)
		
		// List of possible Patterns to be kept track of
		boolean playTriangle = false;
		boolean playOreo = false;
		boolean playBridge = false;
		boolean playDog = false;
		boolean playPawnCorner = false;
		boolean playKingCorner = false;
		
		boolean oppTriangle = false;
		boolean oppOreo = false;
		boolean oppBridge = false;
		boolean oppDog = false;
		boolean oppPawnCorner = false;
		boolean oppKingCorner = false;
		
		
		// Board Storage
		
		double[] desc = bgState.getDescriptor(); // Stores the Board State to be evaluated
		
		// Schrum: This is incomplete, but keep the following in mind when working on it.
		// A heuristic evaluation function should return the same score regardless of
		// which player's turn it is. All heuristics should be thought of as calculating
		// the score for "how good is this for the first player?" This works because the
		// second player in minimax search is actually trying to minimize the heuristic score.
		
		for(double piece : desc){ // Stores the non-location dependent info
			int pieceVal = (int) piece; // Makes comparison easier
			
			if(pieceVal == playerPawn){
				playPawns++;
			}else if(pieceVal == playerKing){
				playKings++;
			}else if(pieceVal == opponentPawn){
				oppPawns++;
			}else if(pieceVal == opponentKing){
				oppKings++;
			}
		}
		
		
		
		return 0;
	}

	
	
}
