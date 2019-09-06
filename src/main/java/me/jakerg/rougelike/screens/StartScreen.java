package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import me.jakerg.rougelike.TitleUtil;
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
		try {
			List<String> title = TitleUtil.loadTitleFromFile("data/rouge/titles/loz.txt");
			int y = TitleUtil.getCenterAligned(title.size(), terminal);
			for(String line : title) {
				terminal.write(line, 0, y++);
			}
			terminal.writeCenter("[enter] start", y + 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

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
