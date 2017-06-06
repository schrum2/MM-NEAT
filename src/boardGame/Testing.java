package boardGame;

import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerRandom;
import boardGame.othello.Othello;
import boardGame.ttt.TicTacToeState;
import edu.utexas.cs.nn.util.MiscUtil;

public class Testing {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		// Code similar to what is below should eventually be placed into the
		// oneEval method of BoardGameTask BoardGamePlayerRandom

		Othello game = new Othello();
		BoardGamePlayer[] players = new BoardGamePlayer[]{new BoardGamePlayerRandom<TicTacToeState>(), new BoardGamePlayerRandom<TicTacToeState>()};

		while(!game.isGameOver()){
			System.out.println(game.toString());
			//MiscUtil.waitForReadStringAndEnterKeyPress();
			game.move(players[game.getCurrentPlayer()]);
			MiscUtil.waitForReadStringAndEnterKeyPress();
		}

		System.out.println(game.toString());
		
	}
}
