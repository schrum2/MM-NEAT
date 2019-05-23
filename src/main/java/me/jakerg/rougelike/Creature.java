package me.jakerg.rougelike;

import java.awt.Color;

import asciiPanel.AsciiPanel;
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
    
    private int maxHp;
    public int maxHp() { return maxHp; }

    private int hp;
    public int hp() { return hp; }

    private int attackValue;
    public int attackValue() { return attackValue; }

    private int defenseValue;
    public int defenseValue() { return defenseValue; }
    
    /**
     * If a creature is told to display, let the ai control take care of it
     * @param terminal output
     * @param oX offsetX
     * @param oY offsetY
     */
    public void display(AsciiPanel terminal, int oX, int oY) {
    	ai.display(terminal, oX, oY);
    }
    
    /**
     * Get the creature at wx, wy
     * @param wx World x
     * @param wy World y
     * @return Creature if there's one present at coords, null if not
     */
    public Creature creature(int wx, int wy) {
        return world.creature(wx, wy);
    }
    
    /**
     * Creature constructor for a basic creature
     * @param world World for the creature to be on
     * @param glyph Character representation of creature
     * @param color Color representation of creature
     */
    public Creature(World world, char glyph, Color color){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
    }
    
    /**
     * Creature constructor for a basic creature
     * @param world World for the creature to be on
     * @param glyph Character representation of creature
     * @param color Color representation of creature
     * @param Dungeon dungeon for the creature to be on
     */
    public Creature(World world, char glyph, Color color, Dungeon dungeon){
    	this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.dungeon = dungeon;
    }
    
    /**
     * In-depth creature constructor w/o dungeon
     * @param world World for the creature to be on
     * @param glyph Character representation of creature
     * @param color Color representation of creature
     * @param maxHp Maximum health of creature
     * @param attack How much damage the creature can do
     * @param defense How much can it defend from attacks
     */
    public Creature(World world, char glyph, Color color, int maxHp, int attack, int defense){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
    }
    
    /**
     * In-depth creature constructor
     * @param world World for the creature to be on
     * @param glyph Character representation of creature
     * @param color Color representation of creature
     * @param maxHp Maximum health of creature
     * @param attack How much damage the creature can do
     * @param defense How much can it defend from attacks
     * @param Dungeon dungeon for the creature to be on
     */
    public Creature(World world, char glyph, Color color, int maxHp, int attack, int defense, Dungeon dungeon){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.dungeon = dungeon;
    }
    
    /**
     * Function to help the creature move, either let the ai take care of it or attack another creature if its there
     * @param mx distance to move on x
     * @param my distance to move on y
     */
    public void moveBy(int mx, int my){
    	if(mx == 0 && my == 0) return; // If the the character is staying still, it may kill itself so return
    	Creature other = world.creature(x+mx, y+my); // Get the creature that is where the creature is moving

    	
    	if (other == null) // If there's no creature let the ai take care of it
    		ai.onEnter(x+mx, y+my, world.tile(x+mx, y+my));
    	else // Otherwise attack the creature
    		attack(other);
    		
    }
    
    /**
     * Attack another creature
     * @param other the other creature
     */
	public void attack(Creature other){
		System.out.println(this.glyph + " attacking " + other.glyph());
        int amount = Math.max(0, attackValue() - other.defenseValue()); // Get whatever is higher: 0 or the total attack value, dont want negative attack
    
        amount = (int)(Math.random() * amount) + 1; // Add randomness to ammount
    
        other.modifyHp(-amount); // Modify hp of the the other creature
    }

	/**
	 * Modify HP of creature by amount
	 * @param amount Amount to modify HP
	 */
    public void modifyHp(int amount) {
        hp += amount; // Add amount
    
        if (hp < 1) // If the health is less than 1 then remove from world
         world.remove(this);
    }
    
    /**
     * let the creature dig at wx, wy
     * @param wx World x
     * @param wy World y
     */
    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }

    /**
     * Update to let ai update
     */
	public void update() {
		ai.onUpdate();
	}
	

}
