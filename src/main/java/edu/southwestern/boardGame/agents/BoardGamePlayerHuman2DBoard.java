package edu.southwestern.boardGame.agents;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Set;

import edu.southwestern.boardGame.BoardGame;
import edu.southwestern.boardGame.TwoDimensionalBoardGameState;
import edu.southwestern.boardGame.TwoDimensionalBoardGameViewer;
import edu.southwestern.MMNEAT.MMNEAT;

public class BoardGamePlayerHuman2DBoard<T extends TwoDimensionalBoardGameState> implements BoardGamePlayer<T> {

	TwoDimensionalBoardGameViewer<T, ? extends BoardGame<T>> boardView;
	
	Point clicked = null;
	boolean ready = false;
	
	final int GRID_WIDTH = TwoDimensionalBoardGameViewer.GRID_WIDTH;
	final int XY_OFFSET = 0;
	
	/**
	 * Gets the BoardGameViewer that the Human will play on from MMNeat
	 */
	@SuppressWarnings("unchecked")
	public BoardGamePlayerHuman2DBoard(){
		boardView = (TwoDimensionalBoardGameViewer<T, ? extends BoardGame<T>>) MMNEAT.boardGameViewer;
	}
	
	@Override
	public T takeAction(T current) {
		Set<T> poss = current.possibleBoardGameStates(current);
		T move = null;
		
		// Pass action required
		if(poss.size() == 1){
			for(T pass : poss) { // Will only loop once
				if(pass.getCurrentPlayer() != current.getCurrentPlayer() // different player 
				&& Arrays.equals(pass.getDescriptor(), current.getDescriptor())) { // but same state
					// then pass
					return pass;
				}
			}
		}
		
		do{
			move = getInput(current);
			if(!poss.contains(move)) System.out.println("Please make a valid Move");
			
		}while(!poss.contains(move)); // Continue to get Moves from the Player until a valid Move is made
		
		boardView.reset(move);
		
		try {
			Thread.sleep(75);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return move;
	}
	
	/**
	 * Gets the input from the Human Player on the BoardGameViewer and returns the Move they made
	 * 
	 * @param current 2DBoardGameState to be played on
	 * @return A Move that the Player has made
	 */
	private T getInput(T current){
		@SuppressWarnings("unchecked")
		T temp = (T) current.copy();
		boardView.panel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getY();
				int y = e.getX();
				
				x = (x - XY_OFFSET)/GRID_WIDTH;
				y = (y - XY_OFFSET)/GRID_WIDTH;
				
				clicked = new Point(x, y);
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
		
		System.out.println("Click the Space to make a Move with");
		waitForClick();
		
		if(temp.moveOnePiece()){ // Does the BoardGame only require one Point?
			temp.moveSinglePoint(clicked);
		}else{
			System.out.println("Click a second Space");
			Point firstPoint = clicked;
			clicked = null;
			
			waitForClick();
			temp.moveDoublePoint(firstPoint, clicked);
		}
		
		return temp;
	}
	
	private void waitForClick(){
		ready = false; // Did not click yet
		while(!ready){ // Will continue to run until the Player clicks on a Space
			try {
				Thread.sleep(75);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
