package me.jakerg.rougelike;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.util.random.RandomNumbers;

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
    
    private Color previous = null;
    
    private CreatureAi ai;
    public void setCreatureAi(CreatureAi ai) { this.ai = ai; }
    
    private int maxHp;
    public int maxHp() { return maxHp; }
	public void setMaxHp(int hp) { maxHp = hp; }

    private int hp;
    public int hp() { return hp; }
    public void setHP(int n) { hp = n; }
    public void addHP() { if(hp < maxHp) hp++; }

    private int attackValue;
    public int attackValue() { return attackValue; }

    private int defenseValue;
    public int defenseValue() { return defenseValue; }
    
	private Move lastDirection = Move.NONE;
	public Move getLastDirection() { return lastDirection; }
	public void setDirection(Move m) { this.lastDirection = m; }
	
	private int numBombs = 0;
	public int bombs() { return numBombs; }
	public void setBombs(int b) { numBombs = b; }
	public void addBomb() { numBombs++; }
	
	public int numKeys = 0;
	public int keys() { return numKeys; }
	public void addKey() { ++numKeys; }
	
	private boolean win = false;
	public boolean win() {return this.win; }
	
	public List<Item> items = new LinkedList<>();
	public List<Item> getItems() { return this.items; }
	
	private Log log;
	public Log log() { return log; };
	
	private DungeonBuilder dungeonBuilder;	
	public DungeonBuilder getDungeonBuilder() { return this.dungeonBuilder; }
    
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
     * @param Log list of messages
     */
    public Creature(World world, char glyph, Color color, int maxHp, int attack, int defense, Log log){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.log = log;
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
     * @param Log list of messages
     */
    public Creature(World world, char glyph, Color color, int maxHp, int attack, int defense, Dungeon dungeon, Log log){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.dungeon = dungeon;
        this.log = log;
    }
    
    /**
     * Function to help the creature move, either let the ai take care of it or attack another creature if its there
     * @param mx distance to move on x
     * @param my distance to move on y
     */
    public void moveBy(int mx, int my){
    	if(mx == 0 && my == 0) return; // If the the character is staying still, it may kill itself so return
    	
    	setLastDirection(mx, my); // Set the last direction of the creature
    	Creature other = world.creature(x+mx, y+my); // Get the creature that is where the creature is moving

    	
    	if (other == null) // If there's no creature let the ai take care of it
    		ai.onEnter(x+mx, y+my, world.tile(x+mx, y+my));
    	else // Otherwise attack the creature
    		attack(other);
    	

    	if(isPlayer())
    		RougelikeApp.PD.actionsPerformed++;
    }
    
    /**
     * Set the direction of the creature based on the distance moving
     * @param dX Direction creature is moving on x (-1,0,1)
     * @param dY Direction creature is moving on y (-1,0,1)
     */
    public void setLastDirection(int dX, int dY) {
    	if(dX == 0 && dY == 1)
			setDirection(Move.DOWN);
		else if(dX == 0 && dY == -1)
			setDirection(Move.UP);
		else if(dX == 1 && dY == 0)
			setDirection(Move.RIGHT);
		else if(dX == -1 && dY == 0)
			setDirection(Move.LEFT);
		else
			setDirection(Move.NONE);
	}
	/**
     * Attack another creature
     * @param other the other creature
     */
	public void attack(Creature other){
		if(this.glyph == other.glyph) return;
		
		if(RandomNumbers.coinFlip() || isPlayer()){			
	        int amount = Math.max(0, attackValue() - other.defenseValue()); // Get whatever is higher: 0 or the total attack value, dont want negative attack
	    
	        amount = RandomNumbers.randomGenerator.nextInt(amount) + 1; // Add randomness to ammount
	    
	        doAction(glyph + " did " + amount + " damage to " + other.glyph);
	        other.modifyHp(-amount); // Modify hp of the the other creature
		} else {
			doAction(glyph + " missed");
		}

    }

	/**
	 * Modify HP of creature by amount
	 * @param amount Amount to modify HP
	 */
    public void modifyHp(int amount) {
        hp += amount; // Add amount
    
        if(isPlayer()){
        	RougelikeApp.PD.damageReceived += -amount;
        }
        if(previous == null) {
        	previous = this.color;
        	this.color = AsciiPanel.brightYellow;
        } 
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
		if(RougelikeApp.DEBUG)
			System.out.println("Creature : " + glyph + " at (" + x + ", " + y + ")");
		
		if(previous != null) {
			this.color = previous;
			previous = null;
		}
		
    	if (hp < 1) {
            doAction(glyph + " died.");
            if(!isPlayer()) {
            	RougelikeApp.PD.enemiesKilled++;
            	EnemyDrops drops = new EnemyDrops(this);
        		Item itemToDrop = drops.getItem();
        		if(itemToDrop != null) {
        			if(itemToDrop instanceof Health && this.world.getPlayer().isMaxed())
        				itemToDrop = new Bomb(getWorld(), 'b', AsciiPanel.white, x, y, 4, 5, true);
        				
        			this.world.addItem(itemToDrop);
        		}
        			
            }
            return;
        }
		ai.onUpdate();
	}
	
	public boolean isMaxed() {
		return maxHp() == hp();
	}
	
	/**
	 * To let us know if the character is a player by checking the glyph
	 * @return True if the character is a player
	 */
	public boolean isPlayer() {
		return glyph == '@';
	}
	
	/**
	 * Let the creature place a bomb at coords based on the last direction
	 */
	public void placeBomb() {
		if(!isPlayer()) return; // Let only the player place bombs
		Move direction = getLastDirection();
		if(direction.equals(Move.NONE) || numBombs == 0) return; // Don't place a bomb if they haven't moved 
		int wx = x;
		int wy = y;
		
		if(direction.equals(Move.RIGHT))
			wx++;
		else if (direction.equals(Move.LEFT))
			wx--;
		
		if(direction.equals(Move.UP))
			wy--;
		else if(direction.equals(Move.DOWN))
			wy++;
		
		if(world.placeBomb(wx, wy))
			numBombs--;
		
		RougelikeApp.PD.actionsPerformed++;
	}
	
	/**
	 * Tell the log what the creature is doing
	 * @param action What the action is
	 */
	public void doAction(String action) {
		log.addMessage(action);
	}
	
	public void setDungeonBuilder(DungeonBuilder dungeonBuilder) {
		this.dungeonBuilder = dungeonBuilder;
	}
	
	/**
	 * Set win condition
	 * @param b True if won the game, false if not
	 */
	public void setWin(boolean b) {
		this.win = b;
	}
	
	public boolean hasItem(char glyph) {
		for(Item i : items)
			if(i.glyph == glyph)
				return true;
		
		return false;
	}
	
	public boolean hasItem(Item item) {
		return hasItem(item.glyph);
	}
	
	
	public void addItem(Item item) {
		if(hasItem(item)) return;
		items.add(item);
		doAction("You picked up " + item.glyph);
	}

}
