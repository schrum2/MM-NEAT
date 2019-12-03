package edu.southwestern.tasks.gvgai.zelda.study;

import java.awt.Point;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;
import edu.southwestern.util.file.NullPrintStream;
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
			//System.out.println(i+":" + novelties[i]);
		}
		return novelties;
	}

	
	/**
	 * Perform an analysis of the novelty of of various dungeons from the original game and
	 * from the human subject study conducted in 2019. Note that this command assumes the 
	 * availability of saved dungeon data from the study, stored in the location specified
	 * by the basePath variable.
	 * 
	 * @param args Empty array ... just use default parameters
	 * @throws Exception
	 */
	public static void main(String[] args) {
		final String basePath = "G:\\My Drive\\Research\\SCOPE Artifacts\\Zelda Human Subject Data\\Experiments-2019-ZeldaGAN\\Subject-";
		
		// To suppress output from file loading
		PrintStream original = System.out;
		System.setOut(new NullPrintStream());
		
		Parameters.initializeParameterCollections(args);
		String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", "tloz4_1_flip", "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip", "tloz9_1_flip"};
		HashMap<String,Double> originalNovelties = new HashMap<String,Double>();
		for(String name: names) {
			Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon(name);
			originalNovelties.put(name, averageDungeonNovelty(dungeon));			
		}
		
		// Resume outputting text
		System.setOut(original);
		
		System.out.println("Novelty of Original Dungeons");
		double originalDungeonAverage = 0;
		for(String name: names) {
			double novelty = originalNovelties.get(name);
			System.out.println(novelty);
			originalDungeonAverage += novelty;
		}
		// Average novelty of dungeons from original game
		originalDungeonAverage /= names.length; 

		
		// Mute output again
		System.setOut(new NullPrintStream());

		HashMap<String,Double> graphNovelties = new HashMap<String,Double>();
		for(int i = 0; i < 30; i++) {
			String path = basePath + i + "\\";
			Dungeon originalDungeon = Dungeon.loadFromJson(path + "OriginalLoader_dungeon.json");
			graphNovelties.put("graphSubject"+i, averageDungeonNovelty(originalDungeon));
		}
		
		// Resume outputting text
		System.setOut(original);

		System.out.println("Novelty of Graph Grammar Dungeons");
		double graphGrammarAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = graphNovelties.get("graphSubject"+i);
			System.out.println(novelty);
			graphGrammarAverage += novelty;
		}
		// Average novelty of Graph Grammar dungeons from study
		graphGrammarAverage /= 30;
		
		// Mute output again
		System.setOut(new NullPrintStream());

		HashMap<String,Double> graphGANNovelties = new HashMap<String,Double>();
		for(int i = 0; i < 30; i++) {
			String path = basePath + i + "\\";
			Dungeon ganDungeon = Dungeon.loadFromJson(path + "GANLoader_dungeon.json");
			//Dungeon originalDungeon = Dungeon.loadFromJson(path + "OriginalLoader_dungeon.json");
			graphGANNovelties.put("graphGANSubject"+i, averageDungeonNovelty(ganDungeon));
		}
		
		// Resume outputting text
		System.setOut(original);

		System.out.println("Novelty of Graph GAN Dungeons");
		double graphGANAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = graphGANNovelties.get("graphGANSubject"+i);
			System.out.println(novelty);
			graphGANAverage += novelty;
		}
		// Average novelty of Graph GAN dungeons from study
		graphGANAverage /= 30;
	
		System.out.println();
		System.out.println("Original Average: "+originalDungeonAverage);
		System.out.println("Grammar  Average: "+graphGrammarAverage);
		System.out.println("GraphGAN Average: "+graphGANAverage);
	}
	
}
