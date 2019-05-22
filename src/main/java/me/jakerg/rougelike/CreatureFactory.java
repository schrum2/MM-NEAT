package me.jakerg.rougelike;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;

public class CreatureFactory {
    private World world;

    public CreatureFactory(World world){
        this.world = world;
    }
    
    public Creature newPlayer(){
        Creature player = new Creature(world, '@', AsciiPanel.brightWhite);
        world.addAtEmptyLocation(player);
        new PlayerAi(player);
        return player;
    }
    
    public Creature newDungeonPlayer(Dungeon dungeon) {
    	Creature player = new Creature(world, '@', AsciiPanel.brightWhite, dungeon);
        world.addAtEmptyLocation(player);
        new DungeonAi(player);
        return player;
    }
}
