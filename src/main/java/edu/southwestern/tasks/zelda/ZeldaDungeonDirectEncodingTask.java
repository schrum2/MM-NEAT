package edu.southwestern.tasks.zelda;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.ContainerGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.util.random.RandomNumbers;
import me.jakerg.rougelike.Tile;

public class ZeldaDungeonDirectEncodingTask extends ZeldaDungeonTask<List<List<Integer>>[][]> {

	//private Dungeon dungeonInstance;

	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype<List<List<Integer>>[][]> individual) {
		List<List<Integer>>[][] levelAsListsGrid = individual.getPhenotype();
		return makeDungeon(levelAsListsGrid);
	}
	/**
	 * Almost identical to SimpleDungeon version, but takes in Genotype<List<List<Integer>>[][]> instead of ArrayList<ArrayList<Double>> (phenotype)
	 * @param individual A List<List<Integer>>[][] representing a hard-coded dungeon
	 * @return dungeon the conversion from List<List<Integer>>[][] to dungeon
	 */
	private Dungeon makeDungeon(List<List<Integer>>[][] levelAsListsGrid) {
		Level[][] dungeon = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
		
		Dungeon dungeonInstance = new Dungeon();

		String[][] uuidLabels = new String[dungeon.length][dungeon[0].length];
		
		for(int y = 0; y < dungeon.length; y++) {
			for(int x = 0; x < dungeon[y].length; x++) {
				if(dungeon[y][x] != null) {
					if(uuidLabels[y][x] == null) {
						// Random ID generation inspired by https://stackoverflow.com/questions/17729753/generating-reproducible-ids-with-uuid
						uuidLabels[y][x] = UUID.nameUUIDFromBytes(RandomNumbers.randomByteArray(16)).toString();
					}
					String name = uuidLabels[y][x];
					dungeonInstance.newNode(name, dungeon[y][x]);
//					if(y==1&&x==0) {
//						System.out.println();
//						System.out.println("this is what I need: "+name);
//						System.out.println();
//
//					}
					//System.out.println(name);
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
		MMNEAT mmneat = new MMNEAT("runNumber:0 randomSeed:0 zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false zeldaDungeonBackTrackRoomFitness:true watch:true trials:1 mu:10 io:false netio:false cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false task:edu.southwestern.tasks.zelda.ZeldaDungeonDirectEncodingTask".split(" "));
		
		mmneat.loadClasses();
		ZeldaDungeonDirectEncodingTask task = new ZeldaDungeonDirectEncodingTask();
		
		@SuppressWarnings("unchecked")
		List<List<Integer>>[][] levelAsListsGrid = new List[2][2];
		makeEmptyRoom(levelAsListsGrid, 0, 0);
		makeEmptyRoom(levelAsListsGrid, 1, 0);
		makeEmptyRoom(levelAsListsGrid, 0, 1);
		makeEmptyRoom(levelAsListsGrid, 1, 1);
		int y=ZeldaVGLCUtil.ZELDA_ROOM_ROWS-1;
		
		levelAsListsGrid[0][0].get(4).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(5).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(6).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(4).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(5).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(6).set(1,Tile.DOOR.getNum());
		
		
		levelAsListsGrid[0][0].get(y-1).set(8,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[0][0].get(y-1).set(7,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[0][0].get(y-1).set(9,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(1).set(8,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(1).set(7,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(1).set(9,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(4).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][0].get(5).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][0].get(6).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(4).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(5).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(6).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(1).set(8,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(1).set(7,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(1).set(9,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(y-1).set(8,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(y-1).set(7,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(y-1).set(9,Tile.DOOR.getNum());
		for(int x = 0; x<ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS;x++) {
			if(y>=0) {
			levelAsListsGrid[0][1].get(y).set(x,Tile.WALL.getNum());
			y--;
			}
		}
		levelAsListsGrid[0][1].get(5).set(3, Tile.KEY.getNum());
		//levelAsListsGrid[0][1].get(6).set(8, Tile.TRIFORCE.getNum());
		
		
		// Look at dungeon structure
		Dungeon dungeon = task.makeDungeon(levelAsListsGrid);
		Point triforceRoom = new Point(0,1);
		//Node k = dungeon.getNodeAt(0, 1);
		//dungeon.setGoal("triforce room");
//		dungeon.setGoalPoint(new Point(24, 6));
//		dungeon.setGoal(dungeon.getNodeAt(0, 1).toString());
		Level[][] levelGrid = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
		levelGrid[triforceRoom.x][triforceRoom.y].placeTriforce(dungeon);

		//levelGrid[triforceRoom.y][triforceRoom.x] = levelGrid[triforceRoom.y][triforceRoom.x].placeTriforce(dungeon);
		dungeon.setGoalPoint(new Point(triforceRoom.x, triforceRoom.y));
		dungeon.setGoal("f7ecf085-4a8c-36f0-85ed-11fb6ef5a642");
		//dungeon.newNode("85072496-2ea8-3064-9a82-0f614e38d9bd", levelGrid[0][1]);

		System.out.println();
		System.out.println("the goal is: "+dungeon.getGoal());
		Point goalPoint = dungeon.getCoords(dungeon.getGoal());
		System.out.println(goalPoint+" is the goal point");
		System.out.println();

		//Point g = dungeon.getGoalPoint();
		//System.out.println("maybe this? "+g);
		dungeon = task.makeDungeon(levelAsListsGrid);
		System.out.println();
		System.out.println("the goal is: "+dungeon.getGoal());
		Point goalPoin1t = dungeon.getCoords(dungeon.getGoal());
		System.out.println(goalPoin1t+" is the goal point");
		System.out.println();
		DungeonUtil.viewDungeon(dungeon);
		
		// Verify that fitness calculations are correct
		//ContainerGenotype
		//Score<Dungeon> sm = task.evaluate(new ContainerGenotype<Dungeon>(dungeon));

		Score<List<List<Integer>>[][]> s = task.evaluate(new ContainerGenotype<List<List<Integer>>[][]>(levelAsListsGrid));
		
		System.out.println(s);
		
	}
	private static void makeEmptyRoom(List<List<Integer>>[][] levelAsListsGrid, int x1, int y1) {
		levelAsListsGrid[x1][y1] = new ArrayList<List<Integer>>();
		// Make totally empty room
		for(int y = 0; y < ZeldaVGLCUtil.ZELDA_ROOM_ROWS; y++) {
			ArrayList<Integer> row = new ArrayList<>();
			for(int x = 0; x < ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS; x++) {
				row.add(Tile.FLOOR.getNum());
			}
			levelAsListsGrid[x1][y1].add(row);
		}
		// Set left/right walls
		for(int y = 0; y < ZeldaVGLCUtil.ZELDA_ROOM_ROWS; y++) {
			for(int x = 0; x <= 1; x++) {
				levelAsListsGrid[x1][y1].get(y).set(x,Tile.WALL.getNum());
				levelAsListsGrid[x1][y1].get(y).set(levelAsListsGrid[x1][y1].get(y).size() - 1 - x,Tile.WALL.getNum());
			}
		}
		// Set top/bottom walls
		for(int y = 0; y <= 1; y++) {
			for(int x = 0; x < ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS; x++) {
				levelAsListsGrid[x1][y1].get(y).set(x,Tile.WALL.getNum());
				levelAsListsGrid[x1][y1].get(ZeldaVGLCUtil.ZELDA_ROOM_ROWS-y-1).set(levelAsListsGrid[x1][y1].get(y).size() - 1 - x,Tile.WALL.getNum());
			}
		}
	}
}
