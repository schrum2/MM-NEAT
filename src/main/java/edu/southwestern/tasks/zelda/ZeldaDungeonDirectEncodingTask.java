package edu.southwestern.tasks.zelda;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
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
					Node newNode = dungeonInstance.newNode(name, dungeon[y][x]);
					
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x + 1, y, "RIGHT");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x, y - 1, "UP");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x - 1, y, "LEFT");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x, y + 1, "DOWN");
				}	
			}
		}
		
		String name = uuidLabels[(uuidLabels.length - 1) / 2][(uuidLabels[0].length - 1) /2].toString();
		
		dungeonInstance.setCurrentLevel(name);
		dungeonInstance.setLevelThere(uuidLabels);
		
		return dungeonInstance;
	}
	
	public static void main(String[] args) {
		MMNEAT mmneat = new MMNEAT("runNumber:0 randomSeed:0 zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false zeldaDungeonBackTrackRoomFitness:true watch:true trials:1 mu:10 io:false netio:false cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false".split(" "));
		mmneat.loadClasses();
		
		ZeldaDungeonDirectEncodingTask task = new ZeldaDungeonDirectEncodingTask();
		
		@SuppressWarnings("unchecked")
		List<List<Integer>>[][] levelAsListsGrid = new List[2][2];
		
		levelAsListsGrid[0][0] = new ArrayList<List<Integer>>();
		// Make totally empty room
		for(int y = 0; y < ZeldaVGLCUtil.ZELDA_ROOM_ROWS; y++) {
			ArrayList<Integer> row = new ArrayList<>();
			for(int x = 0; x < ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS; x++) {
				row.add(Tile.FLOOR.getNum());
			}
			levelAsListsGrid[0][0].add(row);
		}
		// Set left/right walls
		for(int y = 0; y < ZeldaVGLCUtil.ZELDA_ROOM_ROWS; y++) {
			for(int x = 0; x <= 1; x++) {
				levelAsListsGrid[0][0].get(y).set(x,Tile.WALL.getNum());
				levelAsListsGrid[0][0].get(y).set(levelAsListsGrid[0][0].get(y).size() - 1 - x,Tile.WALL.getNum());
			}
		}
		// Set top/bottom walls
		for(int y = 0; y <= 1; y++) {
			for(int x = 0; x < ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS; x++) {
				levelAsListsGrid[0][0].get(y).set(x,Tile.WALL.getNum());
				levelAsListsGrid[0][0].get(y).set(levelAsListsGrid[0][0].get(y).size() - 1 - x,Tile.WALL.getNum());
			}
		}
		
		// Look at dungeon structure
		Dungeon dungeon = task.makeDungeon(levelAsListsGrid);
		DungeonUtil.viewDungeon(dungeon);
		
		// Verify that fitness calculations are correct
		Score<List<List<Integer>>[][]> s = task.evaluate(new Genotype<List<List<Integer>>[][]>() {

			@Override
			public void addParent(long id) {
			}

			@Override
			public List getParentIDs() {
				return null;
			}

			@Override
			public Genotype copy() {
				return null;
			}

			@Override
			public void mutate() {
			}

			@Override
			public Genotype<List<List<Integer>>[][]> crossover(Genotype<List<List<Integer>>[][]> g) {
				return null;
			}

			@Override
			public List<List<Integer>>[][] getPhenotype() {
				return levelAsListsGrid;
			}

			@Override
			public Genotype<List<List<Integer>>[][]> newInstance() {
				return null;
			}

			@Override
			public long getId() {
				return 0;
			}
		});
		
		System.out.println(s);
	}
}
