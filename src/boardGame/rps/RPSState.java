package boardGame.rps;

import boardGame.BoardGameState;

public class RPSState implements BoardGameState{

	private int[] playerMoves;
	
	public static final int UNDECIDED = -1;
	public static final int ROCK = 0;
	public static final int PAPER = 1;
	public static final int SCISSORS = 2;	
	
	public RPSState(){
		playerMoves = new int[]{UNDECIDED, UNDECIDED};
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
		return playerMoves[0] != UNDECIDED && playerMoves[1] != UNDECIDED;
	}

}
