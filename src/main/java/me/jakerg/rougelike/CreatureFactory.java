package me.jakerg.rougelike;

import asciiPanel.AsciiPanel;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;

/**
 * Factory class to take care of creating new enemies or a player
 * @author gutierr8
 *
 */
public class CreatureFactory {
    private World world;
    private Log log;

    public CreatureFactory(World world, Log log){
        this.world = world;
        this.log = log;
    }
    
    /**
     * Create a new player with Player ai, no dungeon
     * @return Creature with player AI
     */
    public Creature newPlayer(){
        Creature player = new Creature(world, '@', AsciiPanel.brightWhite);
        world.addAtEmptyLocation(player);
        new PlayerAi(player);
        return player;
    }
    
    /**
     * Create an enemy at given coordinates
     * @param x x point to place enemy
     * @param y y point to place enemy
     * @param player 
     * @return Creature with enemy ai
     */
    public Creature newEnemy(int x, int y, Creature player) {
    	int maxHP = Parameters.parameters.integerParameter("rougeEnemyHealth");
    	Creature enemy = new Creature(world, 'e', AsciiPanel.brightRed, maxHP, 1, 0, log);
    	world.addCreatureAt(x, y, enemy);
    	new EnemyAi(enemy, player);
    	return enemy;
    }
    
    /**
     * Create a creature with a player ai and a dungeon
     * @param dungeon Dungeon for the player to interact with
     * @return Creature with player ai
     */
    public Creature newDungeonPlayer(Dungeon dungeon) {
    	int hp = Parameters.parameters.integerParameter("zeldaMaxHealth");
    	Creature player = new Creature(world, '@', AsciiPanel.brightWhite, hp, 5, 0, dungeon, log);
        new DungeonAi(player);
        return player;
    }
}
