package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.random.RandomNumbers;

public class SimpleDungeon extends ZeldaDungeon<ArrayList<Double>>{

	public Level[][] makeLevels(ArrayList<ArrayList<Double>> phenotypes, int numRooms) {
		LinkedList<Level> levelList = new LinkedList<>();
		
		for(ArrayList<Double> phenotype : phenotypes)
			levelList.add(new Level(getLevelFromLatentVector(phenotype)));
		
		Level[][] dungeon = new Level[numRooms][numRooms];
		
		int x = numRooms / 2;
		int y = x;
		
		while(levelList.size() > 0) {
			if(levelList.size() == 1) {
				Level level = levelList.pop();
				levelList.add(level.placeTriforce(null));
			}
			
			
			if(x >= 0 && x < dungeon[0].length && y >= 0 && y < dungeon.length)
				if(dungeon[y][x] == null)
					dungeon[y][x] = levelList.pop();
			
			
			switch(RandomNumbers.randomGenerator.nextInt(4)) {
			case 0: x--; break; // left
			case 1: x++; break; // right
			case 2: y--; break; // down
			case 3: y++; break; // up
			
			}

			// Make sure the coordinates don't go too far out of bounds
			x = Math.max(Math.min(x, dungeon[0].length), 0);
			y = Math.max(Math.min(y, dungeon.length), 0);
		}
		
		return dungeon;
	}

	@Override
	public List<List<Integer>> getLevelFromLatentVector(ArrayList<Double> latentVector) {
		double[] room = ArrayUtil.doubleArrayFromList(latentVector);
		return ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(room);
	}

	@Override
	public Dungeon makeDungeon(ArrayList<ArrayList<Double>> phenotypes, int numRooms) throws Exception {
		Level[][] dungeon = makeLevels(phenotypes, numRooms);
		if (dungeon == null) return null;
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
					
					addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x + 1, y, "RIGHT");
					addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x, y - 1, "UP");
					addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x - 1, y, "LEFT");
					addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x, y + 1, "DOWN");
				}	
			}
		}
		
		String name = uuidLabels[(uuidLabels.length - 1) / 2][(uuidLabels[0].length - 1) /2].toString();
		
		dungeonInstance.setCurrentLevel(name);
		dungeonInstance.setLevelThere(uuidLabels);
		
		this.dungeonInstance = dungeonInstance;
		return dungeonInstance;
	}
	
}
