package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import me.jakerg.rougelike.Log;
import me.jakerg.rougelike.Message;

/**
 * A message panel to display messages
 * @author gutierr8
 *
 */
public class MessageScreen implements Screen {

	private Log log;
	
	private int oX;
	private int oY;
	private int max;
	
	public MessageScreen(int x, int y, int max, Log log) {
		oX = x;
		oY = y;
		this.max = log.max();
		this.log = log;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		int i = 0;
		terminal.write("Log:", oX, oY  + i++, AsciiPanel.brightGreen);
		for(Message m : log.messages())
			terminal.write(m.message(), oX, oY + max - i++ + 1);
	}

	/**
	 * Not using
	 */
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		// TODO Auto-generated method stub
		return null;
	}

}
