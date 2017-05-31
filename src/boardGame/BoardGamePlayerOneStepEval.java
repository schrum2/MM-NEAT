package boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.heuristics.BoardGameHeuristic;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class BoardGamePlayerOneStepEval implements BoardGamePlayer<BoardGameState> {

	BoardGameHeuristic<BoardGameState> boardHeuristic;
	
	@SuppressWarnings("unchecked")
	public BoardGamePlayerOneStepEval(){
		try {
			boardHeuristic = (BoardGameHeuristic<BoardGameState>) ClassCreation.createObject("boardGameOpponentHeuristic");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public BoardGamePlayerOneStepEval(BoardGameHeuristic<BoardGameState> heuristic){
		boardHeuristic = heuristic;
	}
	
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		List<BoardGameState> poss = new ArrayList<BoardGameState>();
		poss.addAll(current.possibleBoardGameStates(current));
		double[] utilities = new double[poss.size()]; // Stores the network's ouputs
		
		int index = 0;
		for(BoardGameState bgs : poss){ // Gets the network's outputs for all possible BoardGameStates
			utilities[index++] = boardHeuristic.heuristicEvalution(bgs);
		}

		return poss.get(StatisticsUtilities.argmax(utilities)); // Returns the BoardGameState which produced the highest network output
	}


}