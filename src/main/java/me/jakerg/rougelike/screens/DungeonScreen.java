package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.level.*;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.CreatureFactory;
import me.jakerg.rougelike.DungeonBuilder;
import me.jakerg.rougelike.World;

/**
 * This is the main screen if given a dungeon
 * @author gutierr8
 *
 */
public class DungeonScreen implements Screen {
	
	private World world;
    private Creature player;
    private int screenWidth;
    private int screenHeight;
    private Dungeon dungeon;
	private int oX; // offset for x axis, dont want to render in the top left
	private int oY; // offset for y axis, ""
	private MapScreen mapScreen; // This is the view of the overview of the dungeon
	private DungeonBuilder dungeonBuilder; // Keeps track of the worlds along with the current world
    
	/**
	 * Screen if a dungeon is to be played
	 * @param dungeon Dungeon to play
	 */
    public DungeonScreen(Dungeon dungeon) {
    	// Set offsets
    	int h = dungeon.getCurrentlevel().level.getLevel().size();
    	int w = dungeon.getCurrentlevel().level.getLevel().get(0).size();
    	screenWidth = w;
        screenHeight = h;
        oX = 80 / 2 - screenWidth / 2;
    	oY = 26 / 2 - screenHeight / 2;
        this.dungeon = dungeon;
        // Set dungeon builder along with current world
        dungeonBuilder = new DungeonBuilder(dungeon);
        this.world = dungeonBuilder.getCurrentWorld();
        // Creature factory to create our player
        CreatureFactory cf = new CreatureFactory(world);
        player = cf.newDungeonPlayer(dungeon);
        player.x = 5; // Start in middle of dungeon
        player.y = 5;
        // Make map screen to the left of the dungeon screen
        mapScreen = new MapScreen(dungeon, oX + w + 1, oY + h / 2 - dungeon.getLevelThere().length / 2 - 1);
    }

	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		// Update the current world to get any changes
		this.world = dungeonBuilder.getCurrentWorld();
		player.setWorld(this.world);
        // display stuff to terminal
		displayTiles(terminal);
		mapScreen.displayOutput(terminal);
		player.display(terminal, oX + screenWidth + 1, oY);
        terminal.write(player.glyph(), player.x + oX, player.y + oY, player.color());
        world.update(); // Move enemies (basically)
	}

	/**
	 * Basic input(Arrow keys to move and vim controls from starting code)
	 */
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_H: player.moveBy(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_L: player.moveBy( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_K: player.moveBy( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_J: player.moveBy( 0, 1); break;
      
		}
	  return this;
	}

	/**
	 * Helper method to display the tiles along with creatures
	 * @param terminal output to display to
	 */
    private void displayTiles(AsciiPanel terminal) {
    	for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
            	
            	// If there's a creature at that position display it
            	Creature c = world.creature(x, y);
            	if (c != null)
            		terminal.write(c.glyph(), c.x + oX, c.y + oY, c.color());
            	else
            		terminal.write(world.glyph(x, y), x + oX, y + oY, world.color(x, y));
            }
        }
		
	}
}
