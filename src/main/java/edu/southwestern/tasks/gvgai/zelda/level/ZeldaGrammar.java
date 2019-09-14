package edu.southwestern.tasks.gvgai.zelda.level;

public enum ZeldaGrammar implements Grammar{
	DUNGEON_S("Dungeon", "", true),
	OBSTACLE_S("Obstacle", "", true),
	KEY("Key", "k", false),
	KEY_S("KEY", "K", true),
	LOCK("Lock", "l", false),
	BOMB("Bomb", "b", false),
	SOFT_LOCK("SoftLock", "sl", false),
	SOFT_LOCK_S("SOFTLOCK", "S", true),
	MONSTER("Monster", "e", false),
	ROOM("Room", "", false),
	TREASURE("Treasure", "t", false),
	START_S("START", "S", true),
	START("Start", "s", false),
	FORK_S("FORK", "F", true),
	ENEMY_S("ENEMY", "E", true),
	ENEMY("Enemy", "e", false), 
	LOCK_S("LOCK", "L", true),
	NOTHING("Nothing", "n", false), 
	BOMB_S("BOMB", "B", true),
	PUZZLE("Puzzle", "p", false), 
	PUZZLE_S("PUZZEL", "P", true);
	
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

	public static ZeldaGrammar getByType(String type) throws Exception {
		for(ZeldaGrammar g : ZeldaGrammar.values()) {
			if(g.getLevelType().equals(type))
				return g;
		}

		
		throw new Exception("Didn't find Grammar for type : " + type);
	}
	
	public boolean isCyclable() {
		return this == NOTHING || this == ENEMY;
	}

}
