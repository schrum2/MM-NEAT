package boardGame.ttt;

import java.awt.Point;
import java.util.Scanner;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public class TicTacToePlayerHuman<T extends BoardGameState> implements BoardGamePlayer<T>{
	
	private Scanner scan;
	
	/**
	 * Default Constructor
	 */
	public TicTacToePlayerHuman(){
		scan  = new Scanner(System.in);
		System.out.println("Press the Number Keys to place your mark:");
		System.out.println("-------\n|7|8|9|\n-------\n|4|5|6|\n-------\n|1|2|3|\n-------\n\n");
	}
	
	/**
	 * Returns a Point to play Tic-Tac-Toe
	 * 
	 * @return Point to place a Mark and make a Move
	 */
	@SuppressWarnings("unchecked")
	public BoardGameState takeAction(BoardGameState current) {
		TicTacToeState clone = (TicTacToeState)current.copy();
		clone.fill(getInput(scan));
		return clone;
	}
	
	/**
	 * Helper method to allow a Human Player to play Tic-Tac-Toe
	 * 
	 * @param scan Scanner reading the Human Player's input
	 * @return Point to place a Mark and make a Move
	 */
	private Point getInput(Scanner scan){
		String message = "Please choose a number between 1 and 9";
		
		for(;;){
			if(!scan.hasNextInt()){
				System.out.println(message);
				scan.next();
			}else{
				int input = scan.nextInt();
				if(input < 1 || input > 9){
					System.out.println(message);
				}else{				
					Point here = new Point();
					
					switch(input){
						case 1: here.x = 2; here.y = 0; break;
						case 2: here.x = 2; here.y = 1; break;
						case 3: here.x = 2; here.y = 2; break;
						case 4: here.x = 1; here.y = 0; break;
						case 5: here.x = 1; here.y = 1; break;
						case 6: here.x = 1; here.y = 2; break;
						case 7: here.x = 0; here.y = 0; break;
						case 8: here.x = 0; here.y = 1; break;
						case 9: here.x = 0; here.y = 2; break;
					}
					
					return here;
				}
			}
		}
	}

}
	
