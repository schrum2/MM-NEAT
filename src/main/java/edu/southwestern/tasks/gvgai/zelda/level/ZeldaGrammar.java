package edu.southwestern.tasks.gvgai.zelda.level;

public enum ZeldaGrammar implements Grammar{
	DUNGEON("Dungeon", "", true),
	OBSTACLE("Obstacle", "", true),
	KEY("Key", "k", false),
	LOCK("Lock", "l", false),
	MONSTER("Monster", "e", false),
	ROOM("Room", "", false),
	TREASURE("Treasure", "t", false),
	START("Start", "s", false),
	FORK("FORK", "F", true);
	
	private final String labelName;
	private final String levelType;
	private final boolean isSymbol;
	
	ZeldaGrammar(String label, String level, boolean isSymbol) {
		this.labelName = label;
		this.levelType = level;
		this.isSymbol = isSymbol;
	}
	
	@Override
	public String getLabelName() {
		return this.labelName;
	}

	@Override
	public String getLevelType() {
		return this.levelType;
	}

	@Override
	public boolean isSymbol() {
		return this.isSymbol;
	}

}
