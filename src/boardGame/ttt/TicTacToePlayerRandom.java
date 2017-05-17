package boardGame.ttt;

import java.awt.Point;
import java.util.List;
import java.util.Random;

public class TicTacToePlayerRandom extends TicTacToePlayer{
	
	private int playerNum;
	
	public TicTacToePlayerRandom(){
		playerNum = 0;
	}
	
	@Override
	public Point takeAction(TicTacToeState current) {
		List<Point> index = current.getEmptyIndex();
		Random random = new Random();
		Point p = index.get(random.nextInt(index.size()));
		return p;
	}

}
