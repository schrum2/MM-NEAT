package me.jakerg.csv;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;

public class ParticipantData {
	
	static Point[] pointsToCheck = new Point[] {
			new Point(7, 1), // UP
			new Point(14, 5), // RIGHT
			new Point(7, 9), // DOWN
			new Point(1, 5) // LEFT
	};
	
	public void storeDungeonData(Dungeon dungeon) {
		HashMap<String, Dungeon.Node> levels = dungeon.getLevels();
		numberOfRooms = levels.size();
		int[] roomsWithBomb = new int[4];
		int[] roomsWithExit = new int[4];
		
		for(Dungeon.Node node : levels.values()) {
			List<List<Integer>> ints = node.level.intLevel;
			int numDoors = 0;
			int numBombs = 0;
			for(Point p : pointsToCheck) {
				Tile t = Tile.findNum(ints.get(p.y).get(p.x));
				if(t.isDoor()) {
					numDoors++;
				}
				if(t.equals(Tile.HIDDEN)) {
					numBombs++;
				}
			}
			
			numDoors -= numBombs;
			if(numDoors > 0)
				roomsWithExit[numDoors - 1]++;
			if(numBombs > 0)
				roomsWithBomb[numBombs - 1]++;
				
		}
		
		roomsWithOneExit = roomsWithExit[0];
		roomsWithTwoExits = roomsWithExit[1];
		roomsWithThreeExits = roomsWithExit[2];
		roomsWithFourExits = roomsWithExit[3];
		
		roomsWithOneBomb = roomsWithBomb[0];
		roomsWithTwoBombs = roomsWithBomb[1];
		roomsWithThreeBombs = roomsWithBomb[2];
		roomsWithFourBombs = roomsWithBomb[3];
		
	}
	
	@CSVField
	public int participantID = Parameters.parameters.integerParameter("randomSeed");
	
	@CSVField
	public int actionsPerformed = 0;
	
	@CSVField
	public int keysCollected = 0;
	
	@CSVField
	public int enemiesKilled = 0;
	
	@CSVField
	public int damageReceived = 0;
	
	@CSVField
	public int heartsCollected = 0;
	
	@CSVField
	public int bombsCollected = 0;
	
	@CSVField
	public int bombsUsed = 0;
	
	@CSVField
	public int deaths = RougelikeApp.TRIES;
	
	@CSVField
	public int distinctRoomsVisited = 0;
	
	@CSVField
	public int numberOfRooms = 0;
	
	@CSVField
	public int roomsWithOneExit = 0;
	
	@CSVField
	public int roomsWithTwoExits = 0;
	
	@CSVField
	public int roomsWithThreeExits = 0;
	
	@CSVField
	public int roomsWithFourExits = 0;
	
	@CSVField
	public int roomsWithOneBomb = 0;
	
	@CSVField
	public int roomsWithTwoBombs = 0;
	
	@CSVField
	public int roomsWithThreeBombs = 0;
	
	@CSVField
	public int roomsWithFourBombs = 0;
}
