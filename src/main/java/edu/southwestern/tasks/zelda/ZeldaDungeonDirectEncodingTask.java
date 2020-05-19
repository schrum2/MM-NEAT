package edu.southwestern.tasks.zelda;

import java.util.List;
import java.util.UUID;


import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.util.random.RandomNumbers;

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
	


}
