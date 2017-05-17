package boardGame.rps;

import java.util.Random;

public class RPSPlayerRandom extends RPSPlayer{

	public int takeAction(){
		Random random = new Random();
		return random.nextInt(3);
	}
	
}
