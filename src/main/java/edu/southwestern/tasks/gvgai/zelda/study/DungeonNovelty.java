package edu.southwestern.tasks.gvgai.zelda.study;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;
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
	private static double roomDistance(Node node, Node node2) {
		double distance = 0;
		ArrayList<ArrayList<Integer>> room1 = node.level.intLevel;
		ArrayList<ArrayList<Integer>> room2 = node2.level.intLevel;
		
		for(int y = START.y; y < ROWS; y++) {
			for(int x = START.x; x < COLUMNS; x++) {
				if(room1.get(y).get(x) != room2.get(y).get(x)) // If the blocks at the same position are not the same, increment novelty
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
		double novelty = 0;
		for(int i = 0; i < rooms.size(); i++) { // For each node in the dungeon
			novelty += roomNovelty(rooms, i); // Calculate novelty of room and each of its neighbors
		}
		return novelty / rooms.size(); // Get average novelty of each room
	}

	
	public static void main(String[] args) throws Exception {
		Parameters.initializeParameterCollections(args);
		Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon("tloz4_1_flip");
		
		System.out.println(averageDungeonNovelty(dungeon));
	}
	
}
