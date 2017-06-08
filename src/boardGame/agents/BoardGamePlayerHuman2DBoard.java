package boardGame.agents;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import boardGame.BoardGame;
import boardGame.TwoDimensionalBoardGameState;
import boardGame.TwoDimensionalBoardGameViewer;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;

public class BoardGamePlayerHuman2DBoard<T extends TwoDimensionalBoardGameState> implements BoardGamePlayer<T> {

	TwoDimensionalBoardGameViewer<T, ? extends BoardGame<T>> board;
	
	Point clicked1 = null;
	Point clicked2 = null;
	
	boolean ready = false;
	
	static final int GRID_WIDTH = TwoDimensionalBoardGameViewer.GRID_WIDTH;
	static final int XY_OFFSET = 10;
	
	/**
	 * Gets the BoardGameViewer that the Human will play on from MMNeat
	 */
	@SuppressWarnings("unchecked")
	public BoardGamePlayerHuman2DBoard(){
		board = (TwoDimensionalBoardGameViewer<T, ? extends BoardGame<T>>) MMNEAT.boardGameViewer;
		board.panel.getFrame().addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// decode click location
				// save in global variable
				// set boolean true to indicate ready
				int x = e.getX();
				int y = e.getY();
				
				x = (x - XY_OFFSET)/GRID_WIDTH;
				y = (y - XY_OFFSET)/GRID_WIDTH;
				
				if(clicked1 == null){
					clicked1 = new Point(x, y);
				}else if(clicked2 == null){
					clicked2 = new Point(x, y);
				}
				
				ready = true;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// Not needed for this class
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// Not needed for this class
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// Not needed for this class
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// Not needed for this class
			}
			
		});
	}
	
	@Override
	public T takeAction(T current) {
		Set<T> poss = current.possibleBoardGameStates(current);
		T move = null;
		
		do{
			move = getInput(current);
			if(!poss.contains(move)) System.out.println("Please make a valid Move");
			
		}while(!poss.contains(move)); // Continue to get Moves from the Player until a valid Move is made
		
		return move;
	}
	
	/**
	 * Gets the input from the Human Player on the BoardGameViewer and returns the Move they made
	 * 
	 * @param current 2DBoardGameState to be played on
	 * @return A Move that the Player has made
	 */
	private T getInput(T current){
		T temp = (T) current.copy();
		
		//while(!ready){
			// Thread.sleep for short time
		//}
		
		while(!ready){ // Will continue to run until the Player clicks on a Space
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// TODO: Need a universal Move method for 2D BoardGames
		
		return temp;
	}
	
	
}
