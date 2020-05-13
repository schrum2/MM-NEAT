package edu.southwestern.tasks.gvgai.zelda.study;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.level.GANLoader;
import edu.southwestern.tasks.gvgai.zelda.level.LevelLoader;
import edu.southwestern.tasks.gvgai.zelda.level.OriginalLoader;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaHumanSubjectStudy2019GraphGrammar;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.random.RandomNumbers;
import me.jakerg.csv.SimpleCSV;

/**
 * Quickly thrown together to count the number of room alterations caused
 * by A* in response to the WCCI paper reviews.
 * 
 * @author Jake Gutierrez
 */
public class DungeonComparison {
	
	public static ChangedDungeonData cdData = new ChangedDungeonData();
	
	
	public static void main(String[] args) {
		
		Parameters.initializeParameterCollections(new String[] {});
		
		LevelLoader loader = new GANLoader();
		
		SimpleCSV<ChangedDungeonData> data;
		
		for(int i = 0; i < 30; i++) {
			if(i == 7 && (loader instanceof OriginalLoader))
				RandomNumbers.reset(30);
			else
				RandomNumbers.reset(i);
			
			List<ZeldaGrammar> initialList = new LinkedList<>();
			initialList.add(ZeldaGrammar.START_S);
			initialList.add(ZeldaGrammar.ENEMY_S);
			initialList.add(ZeldaGrammar.KEY_S);
			initialList.add(ZeldaGrammar.LOCK_S);
			initialList.add(ZeldaGrammar.ENEMY_S);
			initialList.add(ZeldaGrammar.KEY_S);
			initialList.add(ZeldaGrammar.PUZZLE_S);
			initialList.add(ZeldaGrammar.LOCK_S);
			initialList.add(ZeldaGrammar.ENEMY_S);
			initialList.add(ZeldaGrammar.TREASURE);
			Graph<ZeldaGrammar> graph = new Graph<>(initialList);
			
			ZeldaHumanSubjectStudy2019GraphGrammar grammar = new ZeldaHumanSubjectStudy2019GraphGrammar();
			
			cdData = new ChangedDungeonData();
			
			cdData.loaderType = loader.getClass().getSimpleName();
			cdData.seed = i;
			
			try {
				grammar.applyRules(graph);
				Dungeon beforeDungeon = DungeonUtil.recursiveGenerateDungeon(graph, loader);
				DungeonUtil.makeDungeonPlayable(beforeDungeon);
				
				cdData.setTotalRooms(beforeDungeon);
				data = new SimpleCSV<>(cdData);
				data.saveToCSV(true, new File("ZeldaStudy2019/ChangedDungeonData.csv"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(i == 29 && (loader instanceof GANLoader)) {
				i = -1;
				loader = new OriginalLoader();
			}
		}
	}

}
