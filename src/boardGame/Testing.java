package boardGame;

import boardGame.ttt.TicTacToe;
import boardGame.ttt.TicTacToePlayerHuman;
import boardGame.ttt.TicTacToeState;

public class Testing {

	public static void main(String[] args) {
		// Code similar to what is below should eventually be placed into the
		// oneEval method of BoardGameTask BoardGamePlayerRandom
		
		BoardGame<TicTacToeState> game = new TicTacToe();
		BoardGamePlayer[] players = new BoardGamePlayer[]{new TicTacToePlayerHuman(), new BoardGamePlayerRandom<TicTacToeState>()};
//		BoardGame game = new RockPaperScissors();
//		BoardGamePlayer[] players = new RPSPlayer[]{new RPSPlayerRandom(), new RPSPlayerRandom()};
//		Checkers game = new Checkers();
//		BoardGamePlayer[] players = new RPSPlayer[]{new RPSPlayerRandom(), new RPSPlayerRandom()};
		while(!game.isGameOver()){
			System.out.println(game);
			game.move(players[game.getCurrentPlayer()]);
}
		System.out.println("Game over");
		System.out.println(game);
	}
}
