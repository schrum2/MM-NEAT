package boardGame.rps;

import edu.utexas.cs.nn.util.random.RandomNumbers;

public class RPSPlayerRandom extends RPSPlayer{

	/**
	 * Randomly selects a Move
	 * 
	 * @return 0 (Rock), 1 (Paper), or 2 (Scissors) as randomly selected.
	 */
	public int selectMove(RPSState current){
		return RandomNumbers.randomGenerator.nextInt(3);
	}
	
}
