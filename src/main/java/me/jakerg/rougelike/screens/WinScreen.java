package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import asciiPanel.AsciiPanel;
import me.jakerg.rougelike.TitleUtil;

public class WinScreen implements Screen {

	 public void displayOutput(AsciiPanel terminal, boolean update) {
        try {
        	int y = 10;
			List<String> title = TitleUtil.loadTitleFromFile("data/rouge/titles/win.txt");
			for(String line : title) {
				System.out.println(line);
				terminal.write(line, 27, y++);
			}
			terminal.writeCenter("You got the triforce!", y + 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }

}
