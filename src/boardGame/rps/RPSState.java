package boardGame.rps;

import boardGame.BoardGameState;

public class RPSState implements BoardGameState{
	// package private
	int[] playerMoves;
	
	int nextPlayer;
	
	public static final int UNDECIDED = -1;
	public static final int ROCK = 0;
	public static final int PAPER = 1;
	public static final int SCISSORS = 2;	
	
	public RPSState(){
		playerMoves = new int[]{UNDECIDED, UNDECIDED};
		nextPlayer = 0;
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

	public String toString() {
		if(endState()) {
			return label(playerMoves[0]) + " vs. " + label(playerMoves[1]);
		} else {
			return "Result Pending";
		}
	}

	private String label(int choice) {
		switch(choice) {
		case SCISSORS: return "Scissors";
		case ROCK: return "Rock";
		case PAPER: return "Paper";
		}
		throw new IllegalArgumentException("Must choose Rock, Paper, or Scissors, not " + choice);
	}
	
	
}
