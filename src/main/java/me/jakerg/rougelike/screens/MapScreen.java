package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import me.jakerg.rougelike.Tile;

public class MapScreen implements Screen {

	private int oX;
	private int oY;
	private Dungeon dungeon;
	
	public MapScreen(Dungeon dungeon, int oX, int oY) {
		this.dungeon = dungeon;
		this.oX = oX;
		this.oY = oY;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		String[][] levels = dungeon.getLevelThere();
		String current = dungeon.getCurrentlevel().name;
		for(int y = 0; y < levels.length; y++) {
			for(int x = 0; x < levels[y].length; x++) {
				Tile t;
				if(levels[y][x] == null)
					t = Tile.BOUNDS;
				else {
					if(levels[y][x].equals(current))
						t = Tile.CURRENT;
					else
						t = Tile.WALL;
				}
					
				
				terminal.write(t.getGlyph(), x + oX, y + oY, t.getColor());
			}
		}

	}


	/**
	 * Dont respond to user input
	 */
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		
		return null;
	}

}
