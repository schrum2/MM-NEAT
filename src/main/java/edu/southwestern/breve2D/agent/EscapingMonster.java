package edu.southwestern.breve2D.agent;

public final class EscapingMonster extends AttractRepelMonster {

	/**
	 * Constructor for an EscapingMonster; calls the Constructor for a backwards-moving AttractRepelMonster
	 * 
	 * @param index Integer setting the index of this specific EscapingMonster
	 */
	public EscapingMonster(int index) {
		super(index, false);
	}
}
