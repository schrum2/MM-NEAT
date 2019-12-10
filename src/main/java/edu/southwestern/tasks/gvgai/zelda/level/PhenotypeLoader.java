package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import me.jakerg.rougelike.Tile;

public class PhenotypeLoader implements LevelLoader{

	private List<List<List<Integer>>> levels;
	
	public PhenotypeLoader(ArrayList<ArrayList<Double>> phenotypes) {
		levels = new ArrayList<>();
		for(ArrayList<Double> phenotype : phenotypes) {
			double[] room = ArrayUtil.doubleArrayFromList(phenotype);
			List<List<Integer>> level =  ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(room);
			levels.add(remove(level));
		}

			
	}
	
	private List<List<Integer>> remove(List<List<Integer>> level2) {
		List<List<Integer>> level = new ArrayList<>(level2);
		for(int y = 0; y < level.size(); y++) {
			for(int x = 0; x < level.get(y).size(); x++) {
				int num = level.get(y).get(x);
				Tile tile = Tile.findNum(num);
				switch(tile) {
				case DOOR:
				case LOCKED_DOOR:
					num = Tile.WALL.getNum();
					break;
				case KEY:
				case TRIFORCE:
					num = Tile.FLOOR.getNum();
					break;
				default:
					break;
				}
				level.get(y).set(x, num);
					
			}
		}
		
		return level;
	}

	@Override
	public List<List<List<Integer>>> getLevels() {
		return 	(levels);
	}

}
