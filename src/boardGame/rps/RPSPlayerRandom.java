package boardGame.rps;

import edu.utexas.cs.nn.util.random.RandomNumbers;

public class RPSPlayerRandom extends RPSPlayer{

	public int takeAction(RPSState current){
		return RandomNumbers.randomGenerator.nextInt(3);
	}
	
}
