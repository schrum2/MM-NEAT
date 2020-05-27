package icecreamyou.LodeRunner;

import javax.swing.SwingUtilities;

/**
 * The main entry point for running Lode Runner.
 */
public class Game {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LodeRunner();
			}
		});
	}

}