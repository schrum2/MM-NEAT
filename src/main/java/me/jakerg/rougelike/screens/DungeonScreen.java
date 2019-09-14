package me.jakerg.rougelike.screens;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import asciiPanel.AsciiPanel;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.level.*;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.CreatureFactory;
import me.jakerg.rougelike.DungeonBuilder;
import me.jakerg.rougelike.Item;
import me.jakerg.rougelike.Log;
import me.jakerg.rougelike.RougelikeApp;
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
	private int oX; // offset for x axis, dont want to render in the top left
	private int oY; // offset for y axis, ""
	private MapScreen mapScreen; // This is the view of the overview of the dungeon
	private MessageScreen messageScreen; // View our latest actions
	private HelpScreen helpScreen; // Helpful information for player ingame
	private DungeonBuilder dungeonBuilder; // Keeps track of the worlds along with the current world
	private Log log;
    
	/**
	 * Screen if a dungeon is to be played
	 * @param dungeon Dungeon to play
	 */
    public DungeonScreen(Dungeon dungeon) {
    	setDropRates();
    	makeTempDungeon(dungeon);
    	int h = dungeon.getCurrentlevel().level.getLevel().size();
    	int w = dungeon.getCurrentlevel().level.getLevel().get(0).size();
        log = new Log(6);
    	screenWidth = w;
        screenHeight = h;
        oX = 80 / 2 - screenWidth / 2;
    	oY = dungeon.getLevelThere().length + 2 + 10;
        // Creature factory to create our player
        CreatureFactory cf = new CreatureFactory(world, log);
        player = cf.newDungeonPlayer(dungeon);
        player.x = 5; // Start in middle of dungeon
        player.y = 5;
        
        if(RougelikeApp.DEBUG) {
        	player.setBombs(9999);
        	player.setHP(20);
        }
        // Set dungeon builder along with current world
        dungeonBuilder = new DungeonBuilder(dungeon, player, log);
        player.setDungeonBuilder(dungeonBuilder);
        dungeonBuilder.getCurrentWorld().removeSpawn();
        // Make map screen to the left of the dungeon screen
        if(dungeon.getLevelThere() != null)
//        	mapScreen = new MapScreen(dungeon, 1, 1);
        	mapScreen = new MapScreen(dungeon, oX + screenWidth / 2 - dungeon.getLevelThere()[0].length / 2, oY - 2 - dungeon.getLevelThere().length);

        messageScreen = new MessageScreen(80 / 2 - w / 2 - 5, oY + h + 2, 6, log);
        if(Parameters.parameters.booleanParameter("zeldaHelpScreenEnabled"))
        	helpScreen = new HelpScreen(25, oY + 25);
    }

	
	private void setDropRates() {
		double hDR = 30;
		double bDR = 40;
		int playerHealth = 4;
		if(RougelikeApp.LIVES == 2) {
			hDR = 60;
			playerHealth = 6;
		} else if(RougelikeApp.LIVES == 1) {
			hDR = 90;
			bDR = 10;
			playerHealth = 8;
		} else if(RougelikeApp.LIVES == 0) {
			hDR = 90;
			playerHealth = 20;
		}
		
		Parameters.parameters.setDouble("healthDropRate", hDR);
		Parameters.parameters.setDouble("bombDropRate", bDR);
		Parameters.parameters.setInteger("zeldaMaxHealth", playerHealth);
	}


	private void makeTempDungeon(Dungeon d) {
		try {
			FileUtils.forceMkdir(new File("data/rouge/tmp"));
			Gson gson = new GsonBuilder()
					.setPrettyPrinting()
					.create();

			FileWriter writer = new FileWriter("data/rouge/tmp/dungeon.json");
			gson.toJson(d, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void displayOutput(AsciiPanel terminal) {
		// Update the current world to get any changes
		this.world = dungeonBuilder.getCurrentWorld();
		player.setWorld(this.world);
        // display stuff to terminal
		world.update(); // Move enemies (basically)
		displayTiles(terminal);
		if(mapScreen != null) mapScreen.displayOutput(terminal);
		messageScreen.displayOutput(terminal);
		if(helpScreen != null) helpScreen.displayOutput(terminal);
		player.display(terminal, oX + screenWidth + 1, oY);
        terminal.write(player.glyph(), player.x + oX, player.y + oY, player.color());
		log.clear();
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
        case KeyEvent.VK_B: player.placeBomb(); break;
		}
		
		if(player.win()) return new WinScreen();
		
		if(player.hp() <= 0) return new LoseScreen();
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
            	Item i = world.item(x, y);
            	if (c != null)
            		terminal.write(c.glyph(), c.x + oX, c.y + oY, c.color());
            	else if(i != null)
            		terminal.write(i.glyph(), i.x + oX, i.y + oY, i.color());
            	else
            		terminal.write(world.glyph(x, y), x + oX, y + oY, world.color(x, y));
            }
        }
		
	}
}
