package edu.southwestern.tasks.gvgai.zelda.study;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.file.NullPrintStream;
import edu.southwestern.util.stats.StatisticsUtilities;
import me.jakerg.rougelike.Tile;

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
	 * @param rooms List of rooms to compare with. Could be List of Lists or Dungeon.Nodes
	 * @param focus Index in rooms representing the room to compare the other rooms with
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double roomNovelty(List<List<List<Integer>>> rooms, int focus) {
		double novelty = 0;
		
		for(int i = 0; i < rooms.size(); i++) { // For each other room
			if(i != focus) { // don't compare with self
				novelty += roomDistance(rooms.get(focus), rooms.get(i));
			}
		}
		
		return novelty / rooms.size(); // Novelty is average distance from other rooms
	}
	
	
	/**
	 * Version of the same method that takes an Integer List representation of the levels
	 * @param room1 List of Lists representing a level
	 * @param room2 List of Lists representing a level
	 * @return Real number between 0 and 1, 0 being identical and 1 being completely different
	 */
	public static double roomDistance(List<List<Integer>> room1, List<List<Integer>> room2) {
//		for(int x = START.x; x < START.x+ROWS; x++) {
//			System.out.println(room1.get(x) + "\t" + room2.get(x));
//		}
		
		double distance = 0;
		for(int x = START.x; x < START.x+ROWS; x++) {
			for(int y = START.y; y < START.y+COLUMNS; y++) {
				int compare1 = room1.get(x).get(y); 
				int compare2 = room2.get(x).get(y); 
				if(compare1 != compare2) // If the blocks at the same position are not the same, increment novelty
					distance++;
			}
//			System.out.println();
		}
		
//		System.out.println("dist = "+distance);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		return distance / (ROWS * COLUMNS);
	}
	
	/**
	 * Cleans room for novelty comparison by removing enemies, keys, and triforce.
	 * Also turns walls and movable blocks into plain blocks.
	 * @param room Represented at list of list of Integers
	 */
	public static void cleanRoom(List<List<Integer>> room) {
		
		for(int x = 0; x < room.size(); x++) {
			for(int y = 0; y < room.get(x).size(); y++) {
				
				int compare1 = room.get(x).get(y); 
				if(compare1 == Tile.TRIFORCE.getNum()|| // Triforce is item, not tile
						compare1 == Tile.KEY.getNum()||
						compare1 == -6 || // I think this is the raft/ladder item
						compare1 == 2) // I think this represents an enemy
					room.get(x).set(y,Tile.FLOOR.getNum());
				else if(compare1 == Tile.WALL.getNum()|| // These all look like a block 
						compare1 == Tile.MOVABLE_BLOCK_UP.getNum()||
						compare1 == Tile.MOVABLE_BLOCK_DOWN.getNum()||
						compare1 == Tile.MOVABLE_BLOCK_LEFT.getNum()||
						compare1 == Tile.MOVABLE_BLOCK_RIGHT.getNum()) 
					room.get(x).set(y,Tile.WATER.getNum());
				else if(compare1 < 0 || // Many door types seem to be negative
						compare1 == Tile.DOOR.getNum()||
						compare1 == Tile.SOFT_LOCK_DOOR.getNum()) 
					room.get(x).set(y,Tile.WATER.getNum());
						
			}
		}
	}
	
	/**
	 * Get the novelty of a dungeon by computing average novelty of all rooms in the dungeon
	 * with respect to each other.
	 * 
	 * @param dungeon Dungeon to check the novelty of
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double averageDungeonNovelty(Dungeon dungeon) {
		List<List<List<Integer>>> rooms = new LinkedList<>();
		for(Node x : dungeon.getLevels().values()) {
			rooms.add(x.level.getLevel());
		}
		return averageRoomNovelty(rooms);
	}
	
	/**
	 * Average novelty across a list of rooms.
	 * 
	 * @param rooms List of rooms
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double averageRoomNovelty(List<List<List<Integer>>> rooms) {
		return StatisticsUtilities.average(roomNovelties(rooms));
	}

	/**
	 * Get array of novelties of all rooms with respect to each other.
	 * 
	 * @param rooms List of rooms
	 * @return double array where each index is the novelty of the same index in the rooms list.
	 */
	public static double[] roomNovelties(List<List<List<Integer>>> rooms) {
		// Repalce all rooms with deep copies of the room since the novelty calculation
		// modifies the rooms
		ListIterator<List<List<Integer>>> itr = rooms.listIterator();
		while(itr.hasNext()) {
			List<List<Integer>> level = itr.next();
			itr.set(ListUtil.deepCopyListOfLists(level));
		}
		//System.out.println("roomNovelties!");
		// Clean all rooms
		for(List<List<Integer>> room : rooms) {
			cleanRoom(room);
		}
		
		// The order of the rooms can be different each time the rooms are loaded. Put into a consistent order.
		Collections.sort(rooms, new Comparator<List<List<Integer>>>() {
			@Override
			public int compare(List<List<Integer>> o1, List<List<Integer>> o2) {
				String level1 = o1.toString();
				String level2 = o2.toString();
				return level1.compareTo(level2);
			}
		});

//		for(List<List<Integer>> full : rooms) {
//			for(List<Integer> row : full) {
//				System.out.println(row);
//			}
//			MiscUtil.waitForReadStringAndEnterKeyPress();
//		}
		
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
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public static void main(String[] args) throws FileNotFoundException {
		final String basePath = "G:\\My Drive\\Research\\SCOPE Artifacts\\Zelda Human Subject Data\\Experiments-2020-CEC-ZeldaGAN\\Subject-";
		
		// To suppress output from file loading
		PrintStream original = System.out;
		//System.setOut(new NullPrintStream());
		
		Parameters.initializeParameterCollections(args);
		String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", "tloz4_1_flip", "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip", "tloz9_1_flip",
									   "tloz1_2_flip", "tloz2_2_flip", "tloz3_2_flip", "tloz4_2_flip", "tloz5_2_flip", "tloz6_2_flip", "tloz7_2_flip", "tloz8_2_flip", "tloz9_2_flip"};

		List<List<List<Integer>>> allOriginalRooms = new ArrayList<>();
		List<List<List<Integer>>> allPureGrammarRooms = new ArrayList<>();
		List<List<List<Integer>>> allGANRooms = new ArrayList<>();
		
		HashMap<String,Double> originalNovelties = new HashMap<String,Double>();
		for(String name: names) {			
			String file = name+".txt";
			List<List<List<Integer>>> roomList = ZeldaVGLCUtil.convertZeldaLevelFileVGLCtoListOfRooms(ZeldaVGLCUtil.ZELDA_LEVEL_PATH+file);
			allOriginalRooms.addAll(roomList); // Collect all rooms for final comparison at the end
			originalNovelties.put(name, averageRoomNovelty(roomList));		
		}
		
		// Resume outputting text
		System.setOut(original);
		
		System.out.println("Novelty of Original Dungeons");
		PrintStream originalStream = new PrintStream(new File("Zelda-Original.csv"));
		double originalDungeonAverage = 0;
		for(String name: names) {
			double novelty = originalNovelties.get(name);
			System.out.println(novelty);
			originalStream.println(novelty);
			originalDungeonAverage += novelty;
		}
		originalStream.close();
		// Average novelty of dungeons from original game
		originalDungeonAverage /= names.length; 

		
		// Mute output again
		System.setOut(new NullPrintStream());

		HashMap<String,Double> graphNovelties = new HashMap<String,Double>();
		for(int i = 0; i < 30; i++) {
			String path = basePath + i + "\\";
			Dungeon originalDungeon = Dungeon.loadFromJson(path + "OriginalLoader_dungeon.json");
			graphNovelties.put("graphSubject"+i, averageDungeonNovelty(originalDungeon));
			
			List<List<List<Integer>>> rooms = new LinkedList<>();
			for(Node x : originalDungeon.getLevels().values()) {
				rooms.add(x.level.getLevel());
			}
			allPureGrammarRooms.addAll(rooms);
		}
		
		// Resume outputting text
		System.setOut(original);

		System.out.println("Novelty of Graph Grammar Dungeons");
		PrintStream graphGrammarStream = new PrintStream(new File("Zelda-GraphGrammar.csv"));
		double graphGrammarAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = graphNovelties.get("graphSubject"+i);
			System.out.println(novelty);
			graphGrammarStream.println(novelty);
			graphGrammarAverage += novelty;
		}
		graphGrammarStream.close();
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
			
			List<List<List<Integer>>> rooms = new LinkedList<>();
			for(Node x : ganDungeon.getLevels().values()) {
				rooms.add(x.level.getLevel());
			}
			allGANRooms.addAll(rooms);

		}
		
		// Resume outputting text
		System.setOut(original);

		System.out.println("Novelty of Graph GAN Dungeons");
		PrintStream graphGANStream = new PrintStream(new File("Zelda-GraphGAN.csv"));
		double graphGANAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = graphGANNovelties.get("graphGANSubject"+i);
			System.out.println(novelty);
			graphGANStream.println(novelty);
			graphGANAverage += novelty;
		}
		graphGANStream.close();
		// Average novelty of Graph GAN dungeons from study
		graphGANAverage /= 30;
	
		System.out.println();
		System.out.println("Original Average: "+originalDungeonAverage);
		System.out.println("Grammar  Average: "+graphGrammarAverage);
		System.out.println("GraphGAN Average: "+graphGANAverage);
		
		
		
		HashSet<List<List<Integer>>> noDuplicatesSet = new HashSet<>(allOriginalRooms);
		List<List<List<Integer>>> noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);
		
		double[] originalRoomsNoveltySet = roomNovelties(noDuplicatesList);
		PrintStream originalPS = new PrintStream(new File("OriginalRoomsSet.csv"));
		for(Double d : originalRoomsNoveltySet) {
			originalPS.println(d);
		}
		originalPS.close();

		double[] originalRoomsNoveltyAll = roomNovelties(allOriginalRooms);
		originalPS = new PrintStream(new File("OriginalRoomsAll.csv"));
		for(Double d : originalRoomsNoveltyAll) {
			originalPS.println(d);
		}
		originalPS.close();

		
		System.out.println(noDuplicatesList.size());
		System.out.println("Average Set of Original Rooms: " + DungeonNovelty.averageRoomNovelty(noDuplicatesList));
		System.out.println(allOriginalRooms.size());
		System.out.println("Average All Original Rooms: " + DungeonNovelty.averageRoomNovelty(allOriginalRooms));
		
		noDuplicatesSet = new HashSet<>(allPureGrammarRooms);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] graphRoomsNoveltySet = roomNovelties(noDuplicatesList);
		PrintStream graphPS = new PrintStream(new File("GraphRoomsSet.csv"));
		for(Double d : graphRoomsNoveltySet) {
			graphPS.println(d);
		}
		graphPS.close();

		double[] graphRoomsNoveltyAll = roomNovelties(allPureGrammarRooms);
		graphPS = new PrintStream(new File("GraphRoomsAll.csv"));
		for(Double d : graphRoomsNoveltyAll) {
			graphPS.println(d);
		}
		graphPS.close();

		
		System.out.println(noDuplicatesList.size());
		System.out.println("Average Set of Grammar Rooms: " + DungeonNovelty.averageRoomNovelty(noDuplicatesList));
		System.out.println(allPureGrammarRooms.size());
		System.out.println("Average All Grammar Rooms: " + DungeonNovelty.averageRoomNovelty(allPureGrammarRooms));

		noDuplicatesSet = new HashSet<>(allGANRooms);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] ganRoomsNoveltySet = roomNovelties(noDuplicatesList);
		PrintStream ganPS = new PrintStream(new File("GANRoomsSet.csv"));
		for(Double d : ganRoomsNoveltySet) {
			ganPS.println(d);
		}
		ganPS.close();

		double[] ganRoomsNoveltyAll = roomNovelties(allGANRooms);
		ganPS = new PrintStream(new File("GANRoomsAll.csv"));
		for(Double d : ganRoomsNoveltyAll) {
			ganPS.println(d);
		}
		ganPS.close();

		
		System.out.println(noDuplicatesList.size());
		System.out.println("Average Set of GAN Rooms: " + DungeonNovelty.averageRoomNovelty(noDuplicatesList));
		System.out.println(allGANRooms.size());
		System.out.println("Average All GAN Rooms: " + DungeonNovelty.averageRoomNovelty(allGANRooms));
		
	}
	
}
