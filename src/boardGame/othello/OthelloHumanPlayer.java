package boardGame.othello;

import java.awt.Point;
import java.util.Scanner;

import boardGame.BoardGamePlayer;

public class OthelloHumanPlayer<T extends OthelloState> implements BoardGamePlayer<T> {

	Scanner scan;
	
	public OthelloHumanPlayer(){
		scan = new Scanner(System.in);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T takeAction(T current) {

		System.out.println("Current State:\n" + current.toString());
		
		boolean valid = false;
		T temp = null;
		
		do{
			temp = (T) current.copy();
			System.out.println("Select your Move (choose a valid Empty Space)");
			Point goTo = new Point(getInput(scan, "Column"), getInput(scan, "Row"));

			temp.move(goTo);
			System.out.println(temp.toString());
			System.out.println(valid);
		}while(!valid);
		
		return temp;
	}
	
	private int getInput(Scanner scan, String dimension){
		
		String message = "Please select a character from 0-8 to choose your " + dimension;
		for(;;){
			if(!scan.hasNextInt()){
				System.out.println(message);
				scan.next();
			}else{
				int input = scan.nextInt();
				if(input < 0 || input > 8){
					System.out.println(message);
				}else{				
					return input;
				}
			}
		}
	}
	
	
	
}
