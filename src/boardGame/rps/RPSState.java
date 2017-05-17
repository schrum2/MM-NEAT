package boardGame.rps;

import boardGame.BoardGameState;

public class RPSState implements BoardGameState{

	private int[] playerMoves;
	
	private static final int ROCK = 0;
	private static final int PAPER = 1;
	private static final int SCISSORS = 2;	
	
	public RPSState(){
		playerMoves = new int[2];
	}
	
	@Override
	public double[] getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void chooseMove(int player, int move){
		playerMoves[player] = move;
	}
	
	@Override
	public boolean endState() {
		
		boolean over = false;
		
		int play1 = playerMoves[0];
		int play2 = playerMoves[1];

		if(play1 == ROCK && play2 == SCISSORS){
			over = true;
		}else if(play1 == PAPER && play2 == ROCK){
			over = true;
		}else if(play1 == SCISSORS && play2 == PAPER){
			over = true;
		}
		
		if(play2 == ROCK && play1 == SCISSORS){
			over = true;
		}else if(play2 == PAPER && play1 == ROCK){
			over = true;
		}else if(play2 == SCISSORS && play1 == PAPER){
			over = true;
		}
		
		return over;
	}

}
