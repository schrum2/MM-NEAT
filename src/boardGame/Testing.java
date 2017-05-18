package boardGame;

import boardGame.rps.RPSPlayer;
import boardGame.rps.RPSPlayerHuman;
import boardGame.rps.RPSPlayerRandom;
import boardGame.rps.RockPaperScissors;
import boardGame.ttt.TicTacToe;
import boardGame.ttt.TicTacToePlayer;
import boardGame.ttt.TicTacToePlayerHuman;
import boardGame.ttt.TicTacToePlayerRandom;
import boardGame.ttt.TicTacToeState;

public class Testing {

	public static void main(String[] args) {
		// Code similar to what is below should eventually be placed into the
		// oneEval method of BoardGameTask
		
//		BoardGame game = new TicTacToe();
//		BoardGamePlayer[] players = new TicTacToePlayer[]{new TicTacToePlayerHuman(), new TicTacToePlayerRandom()};
		BoardGame game = new RockPaperScissors();
		BoardGamePlayer[] players = new RPSPlayer[]{new RPSPlayerRandom(), new RPSPlayerRandom()};
		while(!game.isGameOver()){
			System.out.println(game);
			game.move(players[game.getCurrentPlayer()]);
		}
		System.out.println("Game over");
		System.out.println(game);
	}
}
