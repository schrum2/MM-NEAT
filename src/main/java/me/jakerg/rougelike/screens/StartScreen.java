package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;

public class StartScreen implements Screen {
	
	private Dungeon dungeon;
	
	public StartScreen() {
		this.dungeon = null;
	}

	public StartScreen(Dungeon dungeon) {
		this.dungeon = dungeon;
	}

	public void displayOutput(AsciiPanel terminal) {
		terminal.write("Rouge1 tutorial", 1, 1);
		terminal.writeCenter("[enter] start", 10);

	}

	public Screen respondToUserInput(KeyEvent key) {
		System.out.println("Responding");
		if(key.getKeyCode() == KeyEvent.VK_ENTER) {
			return dungeon == null ? new PlayScreen() : new DungeonScreen(dungeon);
		}
		return this;
	}

}
