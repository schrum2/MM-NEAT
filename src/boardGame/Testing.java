package boardGame;

import boardGame.rps.RockPaperScissors;

public class Testing {

	public static void main(String[] args) {
		RockPaperScissors game = new RockPaperScissors();
		while(!game.isGameOver()){
			game.move();
		}
		
	}
}
