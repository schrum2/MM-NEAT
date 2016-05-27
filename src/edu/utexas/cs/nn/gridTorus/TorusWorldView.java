package edu.utexas.cs.nn.gridTorus;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
/**
 * a class that provides the visual components of an evaluation
 *
 */
public final class TorusWorldView extends JComponent {

	public static final int CELL_SIZE = 7;
	public static final int BUFFER = 15;
	private GameFrame frame;
	private Graphics bufferGraphics;
	private Image offscreen;
	private final TorusPredPreyGame game;

	/**
	 * a constructor that creates an instance of this game for this object
	 * (TorusWorldView)
	 * 
	 * @param game
	 *            a given instance of the PredPrey game
	 */
	public TorusWorldView(TorusPredPreyGame game) {
		this.game = game;
	}

	////////////////////////////////////////
	////// Visual aids for debugging ///////
	////////////////////////////////////////
	@Override
	/**
	 * create the visual as a compilation of the grid, agents, and actions uses
	 * various utility methods to accomplish each specific visual component
	 * 
	 * @param g
	 *            graphics specifications
	 */
	public void paintComponent(Graphics g) {
		if (offscreen == null) {
			offscreen = createImage(this.getPreferredSize().width, this.getPreferredSize().height);
			bufferGraphics = offscreen.getGraphics();
		}
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.fillRect(0, 0, (2 * BUFFER) + (game.getWorld().width() * CELL_SIZE),
				(2 * BUFFER) + (game.getWorld().height() * CELL_SIZE));

		drawGrid();
		drawAgents();
		drawInfo();

		g.drawImage(offscreen, 0, 0, this);
	}

	/**
	 * Convert TorusWorld x cell left edge of draw cell.
	 *
	 * @param x
	 *            width index in TorusWorld
	 * @return left edge of graphics cell
	 */
	private int x(int x) {
		return BUFFER + (x * CELL_SIZE);
	}

	/**
	 * Convert TorusWorld y cell upper edge of draw cell.
	 *
	 * @param y
	 *            height index in TorusWorld (0 is the bottom, which is reversed
	 *            in the Graphics coords)
	 * @return upper edge of graphics cell
	 */
	private int y(int y) {
		return BUFFER + ((game.getWorld().height() - y) * CELL_SIZE);
	}

	/**
	 * provides the information for the visualization of the agents and places
	 * them on the grid
	 */
	private void drawAgents() {
		TorusAgent[][] agents = game.getAgents();
		for (int i = 0; i < agents.length; i++) {
			for (int j = 0; j < agents[i].length; j++) {
				if (agents[i][j] != null) {
					int x = x((int) agents[i][j].getX());
					int y = y((int) agents[i][j].getY());
					bufferGraphics.setColor(agents[i][j].getColor());
					bufferGraphics.fillRect(x + 1, (y - CELL_SIZE) + 1, CELL_SIZE - 1, CELL_SIZE - 1);
				}
			}
		}
	}

	/**
	 * draws the grid according to information such as the x & y dimensions
	 */
	private void drawGrid() {
		bufferGraphics.setColor(Color.BLACK);
		int width = game.getWorld().width() + 1;
		int bottom = y(0);
		int top = y(game.getWorld().height());
		for (int i = 0; i < width; i++) {
			int x = x(i);
			bufferGraphics.drawLine(x, top, x, bottom);
		}
		int height = game.getWorld().height() + 1;
		int left = x(0);
		int right = x(game.getWorld().width());
		for (int i = 0; i < height; i++) {
			int y = y(i);
			bufferGraphics.drawLine(left, y, right, y);
		}
	}

	/**
	 * add some additional information in the background of the visualization
	 */
	private void drawInfo() {
		int x = 1;
		int y = y(-2);
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawString("" + game.getTime(), x, y);
	}

	@Override
	/**
	 * readjusts the dimension to be of a size more easily used for the
	 * application because of cell sizes and dimensions
	 */
	public Dimension getPreferredSize() {
		return new Dimension((2 * BUFFER) + (game.getWorld().width() * CELL_SIZE),
				(2 * BUFFER) + (game.getWorld().height() * CELL_SIZE));
	}

	/**
	 * show a current view of the game (this frame, as in a single frame in a
	 * movie)
	 * 
	 * @return this TorusWorldView visual instance of the game
	 */
	public TorusWorldView showGame() {
		this.frame = new GameFrame(this);

		// just wait for a bit for player to be ready
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		return this;
	}

	/**
	 * get the current frame
	 * 
	 * @return frame
	 */
	public GameFrame getFrame() {
		return frame;
	}

	/**
	 * creates a game frame as an extension of the java JFrame, which details
	 * that it is invisible at first with the specified title and graphics
	 * configuration it is made visible and not resizable
	 */
	public class GameFrame extends JFrame {

		public GameFrame(JComponent comp) {
			getContentPane().add(BorderLayout.CENTER, comp);
			pack();
			this.setLocation(0, 0);
			this.setVisible(true);
			this.setResizable(false);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			repaint();
		}
	}

	// public static void main(String[] args) {
	// TorusWorld world = new TorusWorld(100, 100);
	// TorusAgent[] agents = new TorusAgent[]{new TorusAgent(world, 10, 20, 0),
	// new TorusAgent(world, 90, 50, 1)};
	// TorusWorldView view = new TorusWorldView(world, new
	// TorusAgent[][]{agents});
	// view.showGame();
	// for (;;) {
	// agents[0].move(1, 0);
	// view.repaint();
	// try {
	// Thread.sleep(1000);
	// } catch (Exception e) {
	// }
	// }
	// }
}
