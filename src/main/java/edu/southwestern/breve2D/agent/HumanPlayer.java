package edu.southwestern.breve2D.agent;

import edu.southwestern.breve2D.Breve2DGame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public final class HumanPlayer extends KeyAdapter implements AgentController {

	private double turn = 0;
	private double force = 0;

	/**
	 * Returns the actions that the Player Agent will take based on the human player's input
	 * 
	 * @param game A specific instance a Breve2DGame
	 * @return Breve2DAction representing the movement the Player Agent will make
	 */
	public Breve2DAction getAction(Breve2DGame game) {
		return new Breve2DAction(turn, force);
	}

	/**
	 * Moves the Player Agent based on the keys pressed
	 * 
	 * @param e KeyEvent used to control the Player Agent
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_UP) {
			force = 1;
		}

		if (key == KeyEvent.VK_RIGHT) {
			turn = -1;
		}

		if (key == KeyEvent.VK_DOWN) {
			force = -1;
		}

		if (key == KeyEvent.VK_LEFT) {
			turn = 1;
		}
	}

	/**
	 * Stops the Player Agent's movement based on the key released
	 * 
	 * @param e KeyEvent used to control the Player Agent
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if (force == 1 && key == KeyEvent.VK_UP) {
			force = 0;
		}

		if (turn < 0 && key == KeyEvent.VK_RIGHT) {
			turn = 0;
		}

		if (force == -1 && key == KeyEvent.VK_DOWN) {
			force = 0;
		}

		if (turn > 0 && key == KeyEvent.VK_LEFT) {
			turn = 0;
		}
	}

	/**
	 * Would reset the Player Agent's actions, but does not do anything as of now
	 */
	public void reset() {
	}
}
