package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.level.*;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.CreatureFactory;
import me.jakerg.rougelike.DungeonBuilder;
import me.jakerg.rougelike.World;

public class DungeonScreen implements Screen {
	private World world;
    private Creature player;
    private int screenWidth;
    private int screenHeight;
    private Dungeon dungeon;
	private int oX;
	private int oY;
    
    public DungeonScreen(Dungeon dungeon) {
    	int h = dungeon.getCurrentlevel().level.getLevel().size();
    	int w = dungeon.getCurrentlevel().level.getLevel().get(0).size();
    	screenWidth = w;
        screenHeight = h;
        oX = 80 / 2 - screenWidth / 2;
    	oY = 26 / 2 - screenHeight / 2;
        this.dungeon = dungeon;
        createDungeon();
        CreatureFactory cf = new CreatureFactory(world);
        player = cf.newDungeonPlayer(dungeon);
        player.x = 5;
        player.y = 5;
    }
    
    private void createDungeon() {
    	this.world = new DungeonBuilder(dungeon)
    			.getLevel()
    			.build();
    }
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		displayTiles(terminal);
        terminal.write(player.glyph(), player.x + oX, player.y + oY, player.color());
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_ESCAPE: return new LoseScreen();
        case KeyEvent.VK_ENTER: return new WinScreen();
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_H: player.moveBy(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_L: player.moveBy( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_K: player.moveBy( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_J: player.moveBy( 0, 1); break;
        case KeyEvent.VK_Y: player.moveBy(-1,-1); break;
        case KeyEvent.VK_U: player.moveBy( 1,-1); break;
        case KeyEvent.VK_B: player.moveBy(-1, 1); break;
        case KeyEvent.VK_N: player.moveBy( 1, 1); break;
      
		}
	  return this;
	}

		
    private void displayTiles(AsciiPanel terminal) {
    	System.out.println("wdith : " + screenWidth + " | h : " + screenHeight );

    	for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){

                terminal.write(world.glyph(x, y), x + oX, y + oY, world.color(x, y));
            }
        }
		
	}
}
