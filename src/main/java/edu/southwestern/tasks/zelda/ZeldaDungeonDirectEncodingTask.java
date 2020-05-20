package edu.southwestern.tasks.zelda;

import java.awt.Point;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.ContainerGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import me.jakerg.rougelike.Tile;

public class ZeldaDungeonDirectEncodingTask extends ZeldaDungeonTask<List<List<Integer>>[][]> {

	//private Dungeon dungeonInstance;

	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype<List<List<Integer>>[][]> individual) {
		List<List<Integer>>[][] levelAsListsGrid = individual.getPhenotype();
		return makeDungeon(levelAsListsGrid);
	}
	/**
	 * Almost identical to SimpleDungeon version, but takes in List<List<Integer>>[][] instead of ArrayList<ArrayList<Double>> (phenotypes)
	 * Makes doors that should be adjacent passable both ways.
	 * @param individual A List<List<Integer>>[][] representing a hard-coded dungeon
	 * @return dungeon the conversion from List<List<Integer>>[][] to dungeon
	 */
	public Dungeon makeDungeon(List<List<Integer>>[][] levelAsListsGrid) {
		Level[][] dungeon = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
		String[][] uuidLabels = new String[dungeon.length][dungeon[0].length];

		Dungeon dungeonInstance = new Dungeon();
		for(int y = 0; y < dungeon.length; y++) {
			for(int x = 0; x < dungeon[y].length; x++) {
				if(dungeon[y][x] != null) {
					uuidLabels[y][x] = "("+x+","+y+")";
				}	
			}
		}
		for(int y = 0; y < dungeon.length; y++) {
			for(int x = 0; x < dungeon[y].length; x++) {
				if(dungeon[y][x] != null) {
					String name = uuidLabels[y][x];

					dungeonInstance.newNode(name, dungeon[y][x]);
				}	
			}
		}
		for(int y = 0; y < dungeon.length; y++) {
			for(int x = 0; x < dungeon[y].length; x++) {
				if(dungeon[y][x] != null) {
					String name = uuidLabels[y][x];

					Node currentNode = dungeonInstance.getNode(name);
					
					Tile[][] k = dungeon[y][x].getTiles();
					if((k[ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE][ZeldaLevelUtil.BIG_DOOR_COORDINATE_START]==Tile.DOOR|| //if the tile is a door or locked door, then make the opposite adjacent
						k[ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE][ZeldaLevelUtil.BIG_DOOR_COORDINATE_START]==Tile.LOCKED_DOOR)
						&& x+1 < dungeon[y].length && dungeon[y][x+1] != null) {
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, currentNode, x+1, y, "RIGHT", ZeldaDungeon.encodedValueForDoorType(levelAsListsGrid[y][x].get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START).get(ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE)));			
						String nameRight = 	uuidLabels[y][x+1];
						Node nodeRight = dungeonInstance.getNode(nameRight);
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, nodeRight, x, y, "LEFT", ZeldaDungeon.encodedValueForDoorType(levelAsListsGrid[y][x+1].get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START).get(ZeldaLevelUtil.CLOSE_EDGE_DOOR_COORDINATE)));
					}
					if((k[ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START][ZeldaLevelUtil.FAR_SHORT_EDGE_DOOR_COORDINATE]==Tile.DOOR|| //if the tile is a door or locked door, then make the opposite adjacent
						k[ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START][ZeldaLevelUtil.FAR_SHORT_EDGE_DOOR_COORDINATE]==Tile.LOCKED_DOOR)
						&& y+1 < dungeon.length && dungeon[y+1][x] != null) {
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, currentNode, x, y+1, "DOWN", ZeldaDungeon.encodedValueForDoorType(levelAsListsGrid[y][x].get(ZeldaLevelUtil.FAR_SHORT_EDGE_DOOR_COORDINATE).get(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START)));
						String nameBelow = 	uuidLabels[y+1][x];		
						Node nodeBelow = dungeonInstance.getNode(nameBelow);
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, nodeBelow, x, y, "UP", ZeldaDungeon.encodedValueForDoorType(levelAsListsGrid[y+1][x].get(ZeldaLevelUtil.CLOSE_EDGE_DOOR_COORDINATE).get(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START)));

					}
					
				}	
			}
		}
		
		String name = uuidLabels[(uuidLabels.length - 1) / 2][(uuidLabels[0].length - 1) /2].toString();
		
		dungeonInstance.setCurrentLevel(name);
		dungeonInstance.setLevelThere(uuidLabels);
		return dungeonInstance;
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		MMNEAT mmneat = new MMNEAT("runNumber:0 randomSeed:0 watch:true zeldaCPPN2GANSparseKeys:true zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false zeldaDungeonBackTrackRoomFitness:true trials:1 mu:10 io:false netio:false cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false task:edu.southwestern.tasks.zelda.ZeldaDungeonDirectEncodingTask".split(" "));
		
		mmneat.loadClasses();
		ZeldaDungeonDirectEncodingTask task = new ZeldaDungeonDirectEncodingTask();
		
		@SuppressWarnings("unchecked")
		List<List<Integer>>[][] levelAsListsGrid = new List[2][2];
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 0, 0);
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 1, 0);
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 0, 1);
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 1, 1);
		int y=ZeldaVGLCUtil.ZELDA_ROOM_ROWS-1;
		
		levelAsListsGrid[0][0].get(4).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(5).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(6).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(4).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(5).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(6).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(y-1).set(8,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[0][0].get(y-1).set(7,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(1).set(8,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(1).set(7,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(4).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][0].get(5).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][0].get(6).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(4).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(5).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(6).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(1).set(8,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(1).set(7,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(y-1).set(8,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(y-1).set(7,Tile.DOOR.getNum());
		for(int x = 0; x<ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS;x++) {
			if(y>=0) {
			levelAsListsGrid[0][1].get(y).set(x,Tile.WALL.getNum());
			y--;
			}
		}
		levelAsListsGrid[0][1].get(2).set(6,Tile.WALL.getNum());
		levelAsListsGrid[0][1].get(5).set(3, Tile.KEY.getNum());
		
		
		// Look at dungeon structure
		Dungeon dungeon = task.makeDungeon(levelAsListsGrid);
		Point triforceRoom = new Point(0,1);
		Level[][] levelGrid = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
		levelGrid[triforceRoom.x][triforceRoom.y].placeTriforce(dungeon);
		dungeon.setGoalPoint(new Point(triforceRoom.x, triforceRoom.y));
		dungeon.setGoal("f7ecf085-4a8c-36f0-85ed-11fb6ef5a642");
		
		// Verify that fitness calculations are correct
		//ContainerGenotype
		Score<List<List<Integer>>[][]> s = task.evaluate(new ContainerGenotype<List<List<Integer>>[][]>(levelAsListsGrid));
		
		System.out.println(s);
		
	}	
}
