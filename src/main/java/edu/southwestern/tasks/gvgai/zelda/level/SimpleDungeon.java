package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

public class SimpleDungeon extends ZeldaDungeon<ArrayList<Double>>{

	@Override
	public Level[][] makeDungeon(ArrayList<ArrayList<Double>> phenotypes, int numRooms) {
		LinkedList<Level> levelList = new LinkedList<>();
		
		for(ArrayList<Double> phenotype : phenotypes)
			levelList.add(new Level(getLevelFromLatentVector(phenotype)));
		
		Level[][] dungeon = new Level[numRooms][numRooms];
		
		int x = numRooms / 2;
		int y = x;
		
		Random random = new Random();
		
		while(levelList.size() > 0) {
			if(x < 0 || x >= dungeon[y].length || y < 0 || y >= dungeon.length) break;
			
			if(dungeon[y][x] == null) {
				dungeon[y][x] = levelList.pop();
			}
			switch(random.nextInt(4)) {
			case 0: x--; break; // left
			case 1: x++; break; // right
			case 2: y--; break; // down
			case 3: y++; break; // up
			}
		}
		
		return dungeon;
		
	}

	@Override
	public List<List<Integer>> getLevelFromLatentVector(ArrayList<Double> latentVector) {
		double[] room = ArrayUtil.doubleArrayFromList(latentVector);
		return ZeldaGANUtil.generateRoomListRepresentationFromGAN(room);
	}
	
}
