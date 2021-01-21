package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.stats.StatisticsUtilities;
import me.jakerg.rougelike.Tile;

public class LevelNovelty {

	// These variables provide the area to determine the novelty value
	// Should look through the playable area of a given room
	protected enum GAME {MARIO, ZELDA, LODE_RUNNER, MEGA_MAN};
	
	protected static GAME game = null;
	protected static int getRows() {
		// If Mario, return Mario dimension, if Mega Man, get Mega Man dimension, etc.
		switch(game) {
		case MARIO:
			throw new UnsupportedOperationException("Still need to provide details for Mario");
		case ZELDA:
			return 7;		
		case LODE_RUNNER:
			throw new UnsupportedOperationException("Still need to provide details for Lode Runner");
		case MEGA_MAN:
			return 14; //default Mega Man
		}
		return -1;
	}
	protected static int getColumns() {
		// If Mario, return Mario dimension, if Mega Man, get Mega Man dimension, etc.
		switch(game) {
		case MARIO:
			throw new UnsupportedOperationException("Still need to provide details for Mario");
		case ZELDA:
			return 12;		
		case LODE_RUNNER:
			throw new UnsupportedOperationException("Still need to provide details for Lode Runner");
		case MEGA_MAN:
			return 16; //default Mega Man
		}
		return -1;
	}
	protected static Point getStart() {
		Point start = new Point(0,0);
		switch(game) {
		case MARIO:			
			break;
		case ZELDA:
			start = new Point(2,2);
			break;
		case LODE_RUNNER:
			break;
		case MEGA_MAN:
			break;
		}
		//no need for starting at point other than 0,0 for Mega Man
		return start;
	}
	public static double getAverageSolutionPathPercent(List<List<List<Integer>>> listOfSegments) {
		double result = 0.00;
		for(List<List<Integer>> segment:listOfSegments) {
			for(int i = 0;i<segment.size();i++) {
				for(int j = 0;j<segment.get(0).size();j++) {
					if(segment.get(i).get(j)==-1) result+=1;
				}
			}
		}
		result/=listOfSegments.size()*listOfSegments.get(0).size()*listOfSegments.get(0).get(0).size();
		return result;
	}
	/**
	 * Find the novelty of a segment (given by focus) with respect to all the other segments of the list.
	 * This is essentially the average distance of the segment from all other segments.
	 * 
	 * @param segments List of rooms to compare with. Could be List of Lists or Dungeon.Nodes
	 * @param focus Index in rooms representing the room to compare the other rooms with
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double segmentNovelty(List<List<List<Integer>>> segments, int focus) {
		double novelty = 0;
		
		for(int i = 0; i < segments.size(); i++) { // For each other segment
			if(i != focus) { // don't compare with self
				novelty += segmentDistance(segments.get(focus), segments.get(i));
			}
		}
		
		return novelty / segments.size(); // Novelty is average distance from other segments
	}
	
	
	/**
	 * Version of the same method that takes an Integer List representation of the levels
	 * @param segment1 List of Lists representing a level
	 * @param segment2 List of Lists representing a level
	 * @return Real number between 0 and 1, 0 being identical and 1 being completely different
	 */
	public static double segmentDistance(List<List<Integer>> segment1, List<List<Integer>> segment2) {
//		for(int x = START.x; x < START.x+ROWS; x++) {
//			System.out.println(room1.get(x) + "\t" + room2.get(x));
//		}
		int rows = getRows();
		int colums = getColumns();
		Point start = getStart();
		double distance = 0;
		for(int x = start.x; x < start.x+rows; x++) {
			for(int y = start.y; y < start.y+colums; y++) {
				int compare1 = segment1.get(x).get(y); 
				int compare2 = segment2.get(x).get(y); 
				if(compare1 != compare2) // If the blocks at the same position are not the same, increment novelty
					distance++;
			}
//			System.out.println();
		}
		
//		System.out.println("dist = "+distance);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		return distance / (rows * colums);
	}
	
	/**
	 * Cleans room for novelty comparison by removing enemies, keys, and triforce.
	 * Also turns walls and movable blocks into plain blocks.
	 * @param segment Represented at list of list of Integers
	 */
	public static void cleanSegment(List<List<Integer>> segment) {
		
		for(int x = 0; x < segment.size(); x++) {
			for(int y = 0; y < segment.get(x).size(); y++) {
				
				int compare1 = segment.get(x).get(y); 
				
				switch(game) {
				case MARIO:
					
					break;
				case ZELDA:
					if(compare1 == Tile.TRIFORCE.getNum()|| // Triforce is item, not tile
					compare1 == Tile.KEY.getNum()||
					compare1 == -6 || // I think this is the raft/ladder item
					compare1 == 2) // I think this represents an enemy
				segment.get(x).set(y,Tile.FLOOR.getNum());
			else if(compare1 == Tile.WALL.getNum()|| // These all look like a block 
					compare1 == Tile.MOVABLE_BLOCK_UP.getNum()||
					compare1 == Tile.MOVABLE_BLOCK_DOWN.getNum()||
					compare1 == Tile.MOVABLE_BLOCK_LEFT.getNum()||
					compare1 == Tile.MOVABLE_BLOCK_RIGHT.getNum()) 
				segment.get(x).set(y,Tile.WATER.getNum());
			else if(compare1 < 0 || // Many door types seem to be negative
					compare1 == Tile.DOOR.getNum()||
					compare1 == Tile.SOFT_LOCK_DOOR.getNum()) 
				segment.get(x).set(y,Tile.WATER.getNum());
					break;
				
				case LODE_RUNNER:
					
					break;
					
				case MEGA_MAN:
					if(compare1 >= MegaManVGLCUtil.ONE_ENEMY_WATER) {
						segment.get(x).set(y, MegaManVGLCUtil.ONE_ENEMY_AIR);
					}
					break;
				}
				
						
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
		return averageSegmentNovelty(rooms);
	}
	
	
	
	/**
	 * Average novelty across a list of rooms.
	 * 
	 * @param rooms List of rooms
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double averageSegmentNovelty(List<List<List<Integer>>> rooms) {
		return StatisticsUtilities.average(segmentNovelties(rooms));
	}

	/**
	 * Get array of novelties of all rooms with respect to each other.
	 * 
	 * @param segments List of rooms
	 * @return double array where each index is the novelty of the same index in the rooms list.
	 */
	public static double[] segmentNovelties(List<List<List<Integer>>> segments) {
		// Repalce all rooms with deep copies of the room since the novelty calculation
		// modifies the rooms
		ListIterator<List<List<Integer>>> itr = segments.listIterator();
		while(itr.hasNext()) {
			List<List<Integer>> level = itr.next();
			itr.set(ListUtil.deepCopyListOfLists(level));
		}
		//System.out.println("roomNovelties!");
		// Clean all rooms
		for(List<List<Integer>> segment : segments) {
			cleanSegment(segment);
		}
		
		// The order of the rooms can be different each time the rooms are loaded. Put into a consistent order.
		Collections.sort(segments, new Comparator<List<List<Integer>>>() {
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
		
		double[] novelties = new double[segments.size()];
		for(int i = 0; i < segments.size(); i++) { // For each room in the list
			novelties[i] = segmentNovelty(segments, i); // Calculate novelty of room 
			//System.out.println(i+":" + novelties[i]);
		}
		return novelties;
	}
	/**
	 * Based on level dimensions, break up the level into rowsXcolumn segments. Store into a List<List<List<Integer>>>
	 * @param level - the entire level to be partitioned
	 * @param rows - the number of rows per segment
	 * @param columns - the number of columns per segment.
	 * @return segments - the list representation of each segment
	 */
	public static List<List<List<Integer>>> partitionSegments(List<List<Integer>> level, int rows, int columns) {
		int startX = 0;
		int startY = 0;
		List<List<List<Integer>>> segments = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> copySegment = new ArrayList<List<Integer>>();
		for(int i = startY; i < level.size();i+=rows) {
			for(int j = startX; j < level.get(0).size(); j+=columns) {
//				System.out.println("startX: " + startX);
//				System.out.println("startY: " + startY);
//				System.out.println("rows: " + rows);
//				System.out.println("columns: " + columns);
				copySegment = getSegmentFromCoordinates(level, j, i, rows, columns);
//				MegaManVGLCUtil.printLevel(copySegment);
				if(game == GAME.MEGA_MAN&&copySegment.get(0).contains(MegaManVGLCUtil.ONE_ENEMY_NULL)) {
					//do nothing
				}else {
					segments.add(copySegment); //otherwise, add the segment to the list of segments.
				}
			}
		}
		
		return segments;
	}
	/**
	 * gets the segment in specified zone for copying
	 * @param level - the level to be copied from
	 * @param startX - the start x-coord
	 * @param startY - the start y-coord
	 * @param rows - the height of the segment
	 * @param columns - the width of the segment
	 * @return copySegment - the segment to be copied (the rowsXcolumn segment)
	 */
	private static List<List<Integer>> getSegmentFromCoordinates(List<List<Integer>> level, int startX, int startY, int rows, int columns) {
		List<List<Integer>> copySegment = new ArrayList<List<Integer>>();
//		System.out.println("startX: " + startX/16);
//		System.out.println("startY: " + startY/14);
//		System.out.println("rows: " + rows);
//		System.out.println("columns: " + columns);
		for(int j = startY; j<startY+rows;j++) {			
			List<Integer> row = new ArrayList<Integer>();
			for(int i = startX; i<startX+columns;i++) {
//				System.out.println("Column: "+i);
//				System.out.println("row: "+j);
				row.add(level.get(j).get(i));
			}
			copySegment.add(row);
		}
		return copySegment;
	}
	
}