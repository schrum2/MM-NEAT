package boardGame.ttt;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import edu.utexas.cs.nn.util.random.RandomNumbers;

public class TicTacToePlayerRandom extends TicTacToePlayer{
	
	public TicTacToePlayerRandom(){
	}
	
	@Override
	public Point selectMove(TicTacToeState current) {
		List<Point> index = current.getEmptyIndex();
		Point p = index.get(RandomNumbers.randomGenerator.nextInt(index.size()));
		return p;
	}

}
