package boardGame;

import boardGame.othello.Othello;
import boardGame.othello.OthelloHumanPlayer;
import boardGame.othello.OthelloState;
import edu.utexas.cs.nn.util.MiscUtil;

public class Testing {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		// Code similar to what is below should eventually be placed into the
		// oneEval method of BoardGameTask BoardGamePlayerRandom

		BoardGame<BoardGameState> game = new Othello();
		BoardGamePlayer[] players = new BoardGamePlayer[]{new BoardGamePlayerRandom<OthelloState>(), new BoardGamePlayerRandom<OthelloState>()};

		while(!game.isGameOver()){
			System.out.println(game.toString());
			MiscUtil.waitForReadStringAndEnterKeyPress();
			game.move(players[game.getCurrentPlayer()]);
			MiscUtil.waitForReadStringAndEnterKeyPress();
		}

	}
}
