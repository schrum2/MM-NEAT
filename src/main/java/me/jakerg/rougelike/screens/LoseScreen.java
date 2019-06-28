package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import asciiPanel.AsciiPanel;
import me.jakerg.rougelike.TitleUtil;

public class LoseScreen implements Screen {

    public void displayOutput(AsciiPanel terminal, boolean update) {
        int y = 10;
        int x = 5;
        try {
			List<String> title = TitleUtil.loadTitleFromFile("data/rouge/titles/lose.txt");
			for(String line : title)
				terminal.write(line, x, y++, AsciiPanel.brightRed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }
}