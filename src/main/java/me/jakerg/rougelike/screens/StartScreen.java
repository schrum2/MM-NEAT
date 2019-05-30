package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;
/**
 * Screen that is first seen on the app
 * @author gutierr8
 *
 */
public class StartScreen implements Screen {
	
	private Dungeon dungeon;
	
	public StartScreen() {
		this.dungeon = null;
	}

	public StartScreen(Dungeon dungeon) {
		this.dungeon = dungeon;
	}

	public void displayOutput(AsciiPanel terminal) {
		terminal.writeCenter("Rouge1 tutorial", 7);
		terminal.writeCenter("[enter] start", 10);

	}

	public Screen respondToUserInput(KeyEvent key) {
		System.out.println("Responding");
		if(key.getKeyCode() == KeyEvent.VK_ENTER) { // If the input is enter, switch to screen based on whether or not theres a dungon
			return dungeon == null ? new PlayScreen() : new DungeonScreen(dungeon);
		}
		// else return the current screen
		return this;
	}

}
