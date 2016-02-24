package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public final class HumanPlayer extends KeyAdapter implements AgentController {

    private double turn = 0;
    private double force = 0;

    public Breve2DAction getAction(Breve2DGame game) {
        return new Breve2DAction(turn, force);
    }

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

    public void reset() {
    }
}
