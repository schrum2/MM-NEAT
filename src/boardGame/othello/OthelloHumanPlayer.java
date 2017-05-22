package boardGame.othello;

import java.awt.Point;
import java.util.Scanner;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public class OthelloHumanPlayer<T extends OthelloState> implements BoardGamePlayer<T> {

	Scanner scan;
	
	OthelloHumanPlayer(){
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
			Point useThis = getInput(scan);
			Point goTo = getInput(scan);

			valid = temp.move(useThis, goTo);			
		}while(!valid);
		
		return temp;
	}
	
	private Point getInput(Scanner scan){
		int columnNum = 0;
		int rowNum = 0;
		
		System.out.println("Please select a character from A-H to choose your Column");
		for(;;){
			String select = scan.next();
			char column = select.toUpperCase().charAt(0);
			if(column != 'A' || column != 'B' || column != 'C' || column != 'D' ||
			   column != 'E' || column != 'F' || column != 'G' || column != 'H'){
				System.out.println("Please select a character from A-H to choose your Column");
				scan.next();
			}else{
				switch(column){
				case 'A': columnNum = 0; break;
				case 'B': columnNum = 1; break;
				case 'C': columnNum = 2; break;
				case 'D': columnNum = 3; break;
				case 'E': columnNum = 4; break;
				case 'F': columnNum = 5; break;
				case 'G': columnNum = 6; break;
				case 'H': columnNum = 7; break;
				}
				break;
			}
		}
		System.out.println("Please select a number from 1-8 to choose your Row");
		for(;;){
			if(!scan.hasNextInt()){
				System.out.println("Please select a number from 1-8 to choose your Row");				
				scan.next();				
			}else{
				int row = scan.nextInt();

				if(row < 0 || row > 8){
					System.out.println("Please select a number from 1-8 to choose your Row");
					scan.next();
				}else{
					rowNum = row;
					break;
				}
			}
		}
		
		return new Point(columnNum, rowNum);
	}
	
	
	
}
