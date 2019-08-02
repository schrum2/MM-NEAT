package me.jakerg.rougelike;

import asciiPanel.AsciiPanel;

/**
 * Creature AI controls the creature given, other types of creatures will extend from this 
 * @author gutierr8
 *
 */
public class CreatureAi {
    protected Creature creature;

    /**
     * Make a new creatureAi based on creature
     * @param creature
     */
    public CreatureAi(Creature creature){
        this.creature = creature;
        this.creature.setCreatureAi(this);
    }

    /**
     * Basic AI controls
     * @param x X point to go to
     * @param y Y point to go to
     * @param tile Tile the character is going to
     */
    public void onEnter(int x, int y, Tile tile) {
    	if (!(creature.getWorld().item(x, y) instanceof MovableBlock) && tile.isGround()){
            creature.x = x;
            creature.y = y;
       }
    }
    
    /**
     * Basic wander function to tell creature to wander in a random direction
     */
    public void wander(){
        int mx = (int)(Math.random() * 3) - 1;
        int my = (int)(Math.random() * 3) - 1;
        Creature other = creature.creature(creature.x + mx, creature.y + my); // Get creature there
        
        // If theres a creature and it's of the same type don't move
        if (other != null && other.glyph() == creature.glyph())
            return;
        else // Otherwise tell the creature to move there
        	creature.moveBy(mx, my);
    }

    /**
     * Basic function to let Ai take care of displaying necessary information
     * @param terminal display to output to
     * @param oX offset on x
     * @param oY offset on y
     */
	public void display(AsciiPanel terminal, int oX, int oY) {
	}

	/**
	 * Function to let AI take care of what happens when the main player moves
	 */
	public void onUpdate() {}

}
