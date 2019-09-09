package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import me.jakerg.csv.ParticipantData;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.TitleUtil;

public class LoseScreen implements Screen {

	Dungeon d;
	
	public LoseScreen() {
		RougelikeApp.LIVES--;
		try {
			RougelikeApp.saveParticipantData();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		RougelikeApp.TRIES++;
		d = Dungeon.loadFromJson("data/rouge/tmp/dungeon.json");
		try {
			FileUtils.deleteDirectory(new File("data/rouge/tmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		RougelikeApp.PD = new ParticipantData();
		RougelikeApp.PD.storeDungeonData(d);
	}
	
    public void displayOutput(AsciiPanel terminal) {
        try {
			List<String> title = TitleUtil.loadTitleFromFile("data/rouge/titles/lose.txt");
	        int y = TitleUtil.getCenterAligned(title.size(), terminal);
	        int x = 5;
			for(String line : title)
				terminal.write(line, x, y++, AsciiPanel.brightRed);
			
			if(RougelikeApp.LIVES <= -1)
				terminal.writeCenter("You have used up all your lives.", y + 4);
			else
				terminal.writeCenter("You have " + (RougelikeApp.LIVES + 1) + " tries remaining...", y + 4);
			
			String action = RougelikeApp.LIVES <= -1 ? "quit" : "retry";
			terminal.writeCenter("Press [enter] to " + action , y + 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Screen respondToUserInput(KeyEvent key) {
    	if(key.getKeyCode() == KeyEvent.VK_ENTER) {
    		if(RougelikeApp.LIVES <= -1)
    			RougelikeApp.app.dispatchEvent(new WindowEvent(RougelikeApp.app, WindowEvent.WINDOW_CLOSING));
    		
    		return new DungeonScreen(d);
    	}
    		
   
        return this;
    }
}