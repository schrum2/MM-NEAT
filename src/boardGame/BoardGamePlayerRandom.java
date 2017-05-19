package boardGame;

import java.util.List;
import java.util.Random;

import edu.utexas.cs.nn.util.random.RandomNumbers;

public class BoardGamePlayerRandom implements BoardGamePlayer{

	/**
	 * Chooses a random action out of the list of possible actions at a given BoardGameState
	 * 
	 * @param current BoardGameState to be updated
	 * @return BoardGameState updated with the randomly selected move
	 */
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		List<BoardGameState> poss = current.possibleBoardGameStates(current);
		return poss.get(RandomNumbers.randomGenerator.nextInt(poss.size()));
	}

}
