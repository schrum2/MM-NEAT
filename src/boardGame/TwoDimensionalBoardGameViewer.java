package boardGame;

import java.awt.Color;
import java.awt.Graphics2D;

import boardGame.othello.Othello;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;

/**
 * Viewer for TwoDimensionalBoardGames
 * 
 * @author johnso17
 */
public class TwoDimensionalBoardGameViewer<S  extends TwoDimensionalBoardGameState,T extends TwoDimensionalBoardGame<S>> {

	public static TwoDimensionalBoardGameViewer current = null;
	
	private T board;
	private S state;
	public DrawingPanel panel;
	
	public static final int HEIGHT = 600;
	public static final int WIDTH = 600;
	
	public static final int GRID_WIDTH = 60;
		
	public TwoDimensionalBoardGameViewer(T bGame){
		board = bGame;
		state = board.getStartingState();
		
		int boardWidth = state.getBoardWidth();
		int boardHeight = state.getBoardHeight();
		
		panel = new DrawingPanel((boardWidth*GRID_WIDTH), (boardHeight*GRID_WIDTH), board.getName());
		
		panel.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
		reset(state);
		current = this;		
	}
	
	/**
	 * Resets the graphics for the view
	 */
	@SuppressWarnings("unchecked")
	public void reset(S newBoard) {
		Graphics2D g = panel.getGraphics();
		g.setColor(Color.black); // Set to Black to distinguish the Background from the Board
		g.fillRect(0, 0, WIDTH, HEIGHT);
		state = (S) newBoard.copy();
		renderBoard(g, newBoard);
	}
	
	public void renderBoard(Graphics2D g, TwoDimensionalBoardGameState newBoard){
		int boardWidth = newBoard.getBoardWidth();
		int boardHeight = newBoard.getBoardHeight();
		
		Color[] colors = newBoard.getPlayerColors();
				
		int[] pieces = new int[newBoard.getViewerPieces().length];
		int index = 0;
		for(double d : newBoard.getViewerPieces()){
			pieces[index++] = (int) d;
		}
		
		for(int i = 0; i < boardWidth; i++){ // Cycles through the Board and Draws the Grid
			for(int j = 0; j < boardHeight; j++){				
				
				if((i + j) % 2 == 0){ // If Even
					g.setColor(Color.lightGray);
				}else{
					g.setColor(Color.gray);
				}
				
				g.fillRect((i*GRID_WIDTH), (j*GRID_WIDTH), GRID_WIDTH, GRID_WIDTH); // Fills the Grid Square with the appropriate background Color
				
				if(pieces[i+(j*boardWidth)] != TwoDimensionalBoardGameState.EMPTY){
					g.setColor(colors[pieces[i+(j*boardWidth)]]);
					g.fillOval((i*GRID_WIDTH), (j*GRID_WIDTH), GRID_WIDTH, GRID_WIDTH);
				}
			
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		new TwoDimensionalBoardGameViewer(new Othello());
	}
	
}
