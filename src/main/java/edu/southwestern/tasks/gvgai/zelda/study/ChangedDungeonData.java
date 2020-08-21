package edu.southwestern.tasks.gvgai.zelda.study;

import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import me.jakerg.csv.CSVField;

public class ChangedDungeonData {
	
	@CSVField
	public int seed;
	
	@CSVField
	public String loaderType;
	
	@CSVField
	public int roomsChanged;
	
	@CSVField
	public int alterations;
	
	@CSVField
	public int totalRooms;
	
	public ChangedDungeonData() {
		seed = 0;
		roomsChanged = 0;
		totalRooms = 0;
		alterations = 0;
	}
	
	public void setTotalRooms(Dungeon dungeon) {
		totalRooms = dungeon.getLevels().size();
	}
	
}
