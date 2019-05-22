package me.jakerg.rougelike;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;
import me.jakerg.rougelike.screens.*;

/**
 * Hello world!
 *
 */
public class RougelikeApp extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1060623638149583738L;
	
	private AsciiPanel terminal;
	private Screen screen;
	
	public RougelikeApp() {
		super();
		terminal = new AsciiPanel();
		terminal.setAsciiFont(AsciiFont.TALRYTH_15_15);
		add(terminal);
		pack();
		screen = new StartScreen();
		addKeyListener(this);
		repaint();
	}
	
	public RougelikeApp(Dungeon dungeon) {
		super();
		terminal = new AsciiPanel();
		terminal.setAsciiFont(AsciiFont.TALRYTH_15_15);
		add(terminal);
		pack();
		screen = new StartScreen(dungeon);
		addKeyListener(this);
		repaint();
	}
	
	public void repaint() {
		terminal.clear();
		screen.displayOutput(terminal);
		super.repaint();
	}

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {
		screen = screen.respondToUserInput(e);
		repaint();
	}

	public void keyReleased(KeyEvent e) {}
	
	public static void main(String[] args) {
		RougelikeApp app = new RougelikeApp();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
	}
	
	public static void startDungeon(Dungeon dungeon) {
		RougelikeApp app = new RougelikeApp(dungeon);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
	}

}
