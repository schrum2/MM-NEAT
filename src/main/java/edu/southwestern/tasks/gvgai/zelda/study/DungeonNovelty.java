package edu.southwestern.tasks.gvgai.zelda.study;

import java.awt.Point;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;
import edu.southwestern.util.stats.StatisticsUtilities;
import me.jakerg.rougelike.Tile;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;

public class DungeonNovelty {

	// These variables provide the area to determine the novelty value
	// Should look through the playable area of a given room
	static final int ROWS = 7; // Number of rows to look through
	static final int COLUMNS = 12; // Number of columns to look through
	static final Point START = new Point(2, 2); // Starting point
	
	/**
	 * Find the novelty of a room (given by focus) with respect to all the other rooms of the list.
	 * This is essentially the average distance of the room from all other rooms.
	 * 
	 * @param rooms List of rooms to compare with
	 * @param focus Index in rooms representing the room to compare the other rooms with
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double roomNovelty(List<Dungeon.Node> rooms, int focus) {
		double novelty = 0;
		
		for(int i = 0; i < rooms.size(); i++) { // For each other room
			if(i != focus) { // don't compare with self
				novelty += roomDistance(rooms.get(focus), rooms.get(i)); 
			}
		}
		
		return novelty / rooms.size(); // Novelty is average distance from other rooms
	}

	/**
	 * Measure the distance between two rooms, which is like a Hamming distance
	 * according to different tiles at each coordinate position. The distance is
	 * then normalized based on the number of tiles.
	 * 
	 * @param node "Room" of a dungeon
	 * @param node2 Another "room" of a dungeon
	 * @return Real number between 0 and 1, 0 being identical and 1 being completely different
	 */
	public static double roomDistance(Node node, Node node2) {
		double distance = 0;
		// Convert to levels to tiles to remove enemies, etc
		Tile[][] room1 = node.level.getTiles();
		Tile[][] room2 = node2.level.getTiles();
		
		for(int y = START.y; y < COLUMNS; y++) {
			for(int x = START.x; x < ROWS; x++) {
				if(!room1[y][x].equals(room2[y][x])) // If the blocks at the same position are not the same, increment novelty
					distance++;
			}
		}
		return distance / (ROWS * COLUMNS);
	}
	
	/**
	 * Get the novelty of a dungeon by computing average novelty of all rooms in the dungeon
	 * with respect to each other.
	 * 
	 * @param dungeon Dungeon to check the novelty of
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double averageDungeonNovelty(Dungeon dungeon) {
		List<Node> rooms = dungeon.getNodes();
		return averageRoomNovelty(rooms);
	}
	
	/**
	 * Average novelty across a list of rooms.
	 * 
	 * @param rooms List of rooms
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double averageRoomNovelty(List<Node> rooms) {
		return StatisticsUtilities.average(roomNovelties(rooms));
	}

	/**
	 * Get array of novelties of all rooms with respect to each other.
	 * 
	 * @param rooms List of rooms
	 * @return double array where each index is the novelty of the same index in the rooms list.
	 */
	public static double[] roomNovelties(List<Node> rooms) {
		double[] novelties = new double[rooms.size()];
		for(int i = 0; i < rooms.size(); i++) { // For each room in the list
			novelties[i] = roomNovelty(rooms, i); // Calculate novelty of room 
			System.out.println(i+":" + novelties[i]);
		}
		return novelties;
	}

	
	
	public static void main(String[] args) throws Exception {
		Parameters.initializeParameterCollections(args);
		Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon("tloz4_1_flip");
		
		System.out.println(averageDungeonNovelty(dungeon));
	}
	
}
