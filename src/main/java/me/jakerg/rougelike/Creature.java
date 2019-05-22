package me.jakerg.rougelike;

import java.awt.Color;

import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;

public class Creature {
    private World world;
    public World getWorld() { return world; }
    public void setWorld(World w) { world = w; }
    
    private Dungeon dungeon;
    public Dungeon getDungeon() { return dungeon; }
    public void setDungeon(Dungeon d) { dungeon = d; }

    public int x;
    public int y;

    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }
    
    private CreatureAi ai;
    public void setCreatureAi(CreatureAi ai) { this.ai = ai; }
    
    public void moveBy(int mx, int my){
        ai.onEnter(x+mx, y+my, world.tile(x+mx, y+my));
    }
    
    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }

    public Creature(World world, char glyph, Color color){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
    }
    
    public Creature(World world, char glyph, Color color, Dungeon dungeon){
    	this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.dungeon = dungeon;
    }
}
