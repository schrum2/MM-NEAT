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
	 * Find the novelty of a room (given by focus) in a dungeon by comparing them with the other rooms (for numNeighbors) of the dungeon
	 * @param rooms List of rooms to compare with
	 * @param focus Number representing the room to compare the other rooms with
	 * @param numNeighbors The amount of rooms to compare the focus room with
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double roomNovelty (List<Dungeon.Node> rooms, int focus, int numNeighbors) {
		double novelty = 0;
		
		for(int i = 0; i < numNeighbors; i++) { // For each neighbor we have
			int neighbor = Math.floorMod(focus + calcNeighbor(i), rooms.size()); // Calculate distance from focus
			novelty += oneRoomNovelty(rooms.get(focus), rooms.get(neighbor)); // Get the novelty of focus room and calculated neighbor room
		}
		
		return novelty / numNeighbors; // Average novelty for each room and neighbors
	}

	/**
	 * Get the novelty of the a room (node) and another node
	 * @param node "Room" of a dungeon
	 * @param node2 Another "room" of a dungeon
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	private static double oneRoomNovelty(Node node, Node node2) {
		double novelty = 0;
		ArrayList<ArrayList<Integer>> room1 = node.level.intLevel;
		ArrayList<ArrayList<Integer>> room2 = node2.level.intLevel;
		
		for(int y = START.y; y < ROWS; y++) {
			for(int x = START.x; x < COLUMNS; x++) {
				if(room1.get(y).get(x) != room2.get(y).get(x)) // If the blocks at the same position are not the same, increment novelty
					novelty++;
			}
		}
		return novelty / (ROWS * COLUMNS);
	}
	
	/**
	 * Get the novelty of a dungeon by looking through each room and comparing it with its' neighbors
	 * @param dungeon Dungeon to check the novelty of
	 * @param numNeighbors The amount of rooms to compare each room
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double dungeonNovelty(Dungeon dungeon, int numNeighbors) {
		double novelty = 0;
		List<Node> rooms = dungeon.getNodes();
		for(int i = 0; i < rooms.size(); i++) { // For each node in the dungeon
			novelty += roomNovelty(rooms, i, numNeighbors); // Calculate novelty of room and each of its neighbors
		}
		
		return novelty / rooms.size(); // Get average novelty of each room
	}

	/**
	 * Function to get the distance of a neighbor based off of i
	 * 0  1  2  3  4
	 * 0  -1 1  -2 2
	 * @param i Neighbor to check
	 * @return Distance from focus
	 */
	private static int calcNeighbor(int i) {
		int n = (i + 2) / 2;
		
		n = (i % 2 == 0) ? n : -n;
		
		return n;
	}
	
	public static void main(String[] args) throws Exception {
		int neighbors = 5;
		Parameters.initializeParameterCollections(args);
		Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon("tloz4_1_flip");
		
		System.out.println(dungeonNovelty(dungeon, neighbors));
	}
	
}
