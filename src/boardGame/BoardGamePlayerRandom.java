package boardGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.utexas.cs.nn.util.random.RandomNumbers;

public class BoardGamePlayerRandom<T extends BoardGameState> implements BoardGamePlayer<T> {
	
	Random random = RandomNumbers.randomGenerator;
	
	/**
	 * Chooses a random action out of the list of possible actions at a given BoardGameState
	 * 
	 * @param current BoardGameState to be updated
	 * @return BoardGameState updated with the randomly selected move
	 */
	@Override
	public T takeAction(T current) {
		List<T> poss = new ArrayList<T>();
		poss.addAll(current.possibleBoardGameStates(current));
		T temp = poss.get(random.nextInt(poss.size()));
		return temp;
	}
}
