package boardGame;

import boardGame.rps.RockPaperScissors;
import boardGame.ttt.TicTacToe;

public class Testing {

	public static void main(String[] args) {
		BoardGame game = new TicTacToe();
		while(!game.isGameOver()){
			System.out.println(game);
			game.move();
		}
		System.out.println("Game over");
		System.out.println(game);
	}
}
