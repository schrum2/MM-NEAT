package edu.southwestern.util.datastructures;

import java.awt.Point;

import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;

public class GraphUtil {
	
	/**
	 * Set the adjacencies, the exit and starting points
	 * @param fromNode Node where the ajancencie originates
	 * @param from exit Point
	 * @param to starting Point
	 * @param whereTo Name of the room the starting point is going to
	 * @param tile Tile to place the at exit point as a number
	 * @throws Exception
	 */
	public static void setAdjacencies(Dungeon.Node fromNode, Point from,
			Point to, String whereTo, int tile) throws Exception {
		String direction = DungeonUtil.getDirection(from, to);
		
		if(direction == null) return;

		switch(direction) {
		case "UP":
			ZeldaLevelUtil.addUpAdjacencies(fromNode, whereTo);
			break;
		case "DOWN":
			ZeldaLevelUtil.addDownAdjacencies(fromNode, whereTo);
			break;
		case "LEFT":
			ZeldaLevelUtil.addLeftAdjacencies(fromNode, whereTo);
			break;
		case "RIGHT":
			ZeldaLevelUtil.addRightAdjacencies(fromNode, whereTo);
			break;
		default:
			throw new Exception ("DIRECTION AINT HEREE");
		}
		
		ZeldaLevelUtil.setDoors(direction, fromNode.level.intLevel, tile);
	}
	
}
