package edu.southwestern.boardGame.agents;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.heuristics.BoardGameHeuristic;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.stats.StatisticsUtilities;

public class BoardGamePlayerOneStepEval<T extends BoardGameState> extends HeuristicBoardGamePlayer<T> {

	@SuppressWarnings("unchecked")
	public BoardGamePlayerOneStepEval(){
		try {
			boardHeuristic = (BoardGameHeuristic<T>) ClassCreation.createObject("boardGameOpponentHeuristic");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public BoardGamePlayerOneStepEval(BoardGameHeuristic<T> heuristic){
		boardHeuristic = heuristic;
	}
	
	@Override
	public T takeAction(T current) {
		List<T> poss = new ArrayList<T>();
		poss.addAll(current.possibleBoardGameStates(current));
		double[] utilities = new double[poss.size()]; // Stores the network's ouputs
		
		int index = 0;
		for(T bgs : poss){ // Gets the network's outputs for all possible BoardGameStates
			utilities[index++] = boardHeuristic.heuristicEvalution(bgs);
		}

		return poss.get(StatisticsUtilities.argmax(utilities)); // Returns the BoardGameState which produced the highest network output
	}


}