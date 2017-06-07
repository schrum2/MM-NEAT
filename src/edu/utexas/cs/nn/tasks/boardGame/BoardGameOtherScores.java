package edu.utexas.cs.nn.tasks.boardGame;

import boardGame.BoardGame;
import boardGame.agents.BoardGamePlayer;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class BoardGameOtherScores<T extends BoardGamePlayer, S extends BoardGame> {
	
	S bg;
	T opponent;
	T player;
	
	
	public BoardGameOtherScores(S boardGame, T testOpponent, T testPlayer){
		bg = boardGame;
		opponent = testOpponent; // The Opponent
		player = testPlayer;
	}
	
	public Pair<double[], double[]> otherEval(){
		
		BoardGameUtil.playGame(bg, new BoardGamePlayer[]{player, opponent});
		
		return null;
		
	}
	
}
