package me.jakerg.rougelike.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import asciiPanel.AsciiPanel;
import me.jakerg.rougelike.Tile;

public class HelpScreen implements Screen {
	
	private int oX, oY;
	private List<HelpInfo> helpInfo;

	public HelpScreen(int oX, int oY) {
		this.oX = oX;
		this.oY = oY;
		setupHelpInfo();
	}
	
	private void setupHelpInfo() {
		this.helpInfo = new LinkedList<>();
		helpInfo.add(new HelpInfo('@', Color.WHITE, "You. Move with arrows."));
		helpInfo.add(new HelpInfo('e', AsciiPanel.brightRed, "Enemy. Move toward to attack."));
		helpInfo.add(HelpInfo.getFromTile(Tile.DOOR, "Use to reach new rooms."));
		helpInfo.add(HelpInfo.getFromTile(Tile.SOFT_LOCK_DOOR, "Kill all enemies to open."));
		helpInfo.add(HelpInfo.getFromTile(Tile.LOCKED_DOOR, "Find a key to open."));
		helpInfo.add(HelpInfo.getFromTile(Tile.PUZZLE_LOCKED, "Push a block to open"));
		helpInfo.add(HelpInfo.getFromTile(Tile.KEY, "Keys, dropped by enemies"));
		helpInfo.add(new HelpInfo('b', AsciiPanel.white, "Bomb. b to use. Find secret doors."));
		helpInfo.add(HelpInfo.getFromTile(Tile.BLOCK, "Water. Enemies can cross, but you can't."));
		helpInfo.add(new HelpInfo('#', AsciiPanel.brightCyan, "Cross water from land."));
		helpInfo.add(HelpInfo.getFromTile(Tile.TRIFORCE, "Find this to win!"));
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		int iY = oY;
		for(HelpInfo info : helpInfo) {
			info.displayInfo(terminal, oX, iY++);
		}
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return this;
	}
	
	public static class HelpInfo {
		char item;
		Color color;
		String description;
		
		public HelpInfo(char item, Color color, String description) {
			this.item = item;
			this.color = color;
			this.description = description;
		}
		
		public static HelpInfo getFromTile(Tile tile, String description) {
			return new HelpInfo(tile.getGlyph(), tile.getColor(), description);
		}

		public void displayInfo(AsciiPanel terminal, int x, int y) {
			terminal.write(item, x++, y, color);
			terminal.write(": " + description, x, y);
		}
	}

}
