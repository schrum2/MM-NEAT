package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.World;
import me.jakerg.rougelike.WorldBuilder;

/**
 * Default screen from starter code, still here to test out
 * @author gutierr8
 *
 */
public class PlayScreen implements Screen {
	private World world;
    private Creature player;
    private int screenWidth;
    private int screenHeight;
    
    public PlayScreen(){
        screenWidth = 80;
        screenHeight = 21;
        createWorld();
//        CreatureFactory cf = new CreatureFactory(world);
//        player = cf.newPlayer();
    }

	private void createWorld() {
    	world = new WorldBuilder(80, 21)
    			.makeCaves()
    			.build();
    }

	public void displayOutput(AsciiPanel terminal) {
		
        displayTiles(terminal);
        terminal.write(player.glyph(), player.x, player.y, player.color());
    }

    private void displayTiles(AsciiPanel terminal) {
    	for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
            	System.out.println("x : " + x + " | y : " + y);
                terminal.write(world.glyph(x, y), x, y, world.color(x, y));
            }
        }
    	
		
	}

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
    
    public int getScrollX() {
        return Math.max(0, Math.min(player.x - screenWidth / 2, world.getWidth() - screenWidth));
    }
    
    public int getScrollY() {
        return Math.max(0, Math.min(player.y - screenHeight / 2, world.getHeight() - screenHeight));
    }
    
    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;

                terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
            }
        }
    }

}
