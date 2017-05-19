package boardGame.rps;

import java.util.Scanner;

import boardGame.BoardGameState;

public class RPSPlayerHuman extends RPSPlayer{
	private Scanner scan;
	
	/**
	 * Allows a Human Player to select a Move to be used
	 * 
	 * @param current RPSState being used
	 * @return int representing the Move a Player took; 0 (Rock), 1 (Paper), or 2 (Scissors).
	 */
	@Override
	public int selectMove(RPSState current) {
		scan  = new Scanner(System.in);
		System.out.println("Choose your Move: 0 (Rock), 1 (Paper), or 2 (Scissors).");
		return getInput(scan);
	}

	/**
	 * Helper Method to allow a Human to play Rock-Paper-Scissors
	 * 
	 * @param scan Scanner used to take in the User's input
	 * @return Integer representing the Move the Human Player took; 0 (Rock), 1 (Paper), or 2 (Scissors).
	 */
	private int getInput(Scanner scan){
		String message = "Please choose 0 (Rock), 1 (Paper), or 2 (Scissors).";
		
		for(;;){
			if(!scan.hasNextInt()){
				System.out.println(message);
				scan.next();
			}else{
				int input = scan.nextInt();
				if(input < 0 || input > 2){
					System.out.println(message);
				}else{				
					return input;
				}
			}
		}
	}

	
}
