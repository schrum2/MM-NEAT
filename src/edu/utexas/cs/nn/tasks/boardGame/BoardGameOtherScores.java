package edu.utexas.cs.nn.tasks.boardGame;

import boardGame.BoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class BoardGameOtherScores<T extends BoardGamePlayer, S extends BoardGame> {
	
	S bg;
	T opponent;
	T player;
	@SuppressWarnings("rawtypes")
	BoardGameFitnessFunction fitnessFunction;

	
	public BoardGameOtherScores(S boardGame, T testOpponent, T testPlayer){
		bg = boardGame;
		opponent = testOpponent; // The Opponent
		player = testPlayer;

		try {
			fitnessFunction = (BoardGameFitnessFunction) ClassCreation.createObject("boardGameFitnessFunction");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public Pair<double[], double[]> otherEval(){
		
		BoardGameUtil.playGame(bg, new BoardGamePlayer[]{player, opponent}, fitnessFunction);
		
		return null;
		
	}
	
}
