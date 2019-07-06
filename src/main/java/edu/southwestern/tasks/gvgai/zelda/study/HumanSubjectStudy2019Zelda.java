package edu.southwestern.tasks.gvgai.zelda.study;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;
import edu.southwestern.tasks.gvgai.zelda.level.LevelLoader;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGraphGrammar;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Graph;
import me.jakerg.rougelike.RougelikeApp;

public class HumanSubjectStudy2019Zelda {
	public enum Type {TUTORIAL, ORIGINAL, GENERATED_DUNGEON}; // Use original dungeon or generated dungeon?
	
	public static void runTrial(Type type) {
		Dungeon dungeonToPlay = null;
		
		if(type.equals(Type.ORIGINAL)) {
			String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip"};
			int seed = Parameters.parameters.integerParameter("randomSeed");
			String dungeonName = names[seed % names.length];
			try {
				dungeonToPlay = LoadOriginalDungeon.loadOriginalDungeon(dungeonName);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else if(type.equals(Type.GENERATED_DUNGEON)) {
			List<ZeldaGrammar> initialList = new LinkedList<>();
			initialList.add(ZeldaGrammar.START_S);
			initialList.add(ZeldaGrammar.ENEMY_S);
			initialList.add(ZeldaGrammar.KEY_S);
			initialList.add(ZeldaGrammar.LOCK_S);
			initialList.add(ZeldaGrammar.ENEMY_S);
			initialList.add(ZeldaGrammar.KEY_S);
			initialList.add(ZeldaGrammar.LOCK_S);
			initialList.add(ZeldaGrammar.ENEMY_S);
			initialList.add(ZeldaGrammar.TREASURE);
			Graph<ZeldaGrammar> graph = new Graph<>(initialList);
			
			ZeldaGraphGrammar grammar = new ZeldaGraphGrammar();
			try {
				grammar.applyRules(graph);
				dungeonToPlay = DungeonUtil.recursiveGenerateDungeon(graph, (LevelLoader) ClassCreation.createObject("zeldaLevelLoader"));
				DungeonUtil.makeDungeonPlayable(dungeonToPlay);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			
		} else if(type.equals(Type.TUTORIAL)) {
			System.out.println("\n\n\nTutorial not supported yet.");
			System.exit(1);
		} else {
			throw new IllegalArgumentException("Type : " + type + " unrecognized");
		}
		
		System.out.println("Play dungeon");
		try {
			RougelikeApp.startDungeon(dungeonToPlay, true, false);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// TODO Auto-generated method stub
		
		// zeldaType: original/generated/tutorial
		// zeldaLevelLoader: edu.southwestern.tasks.gvgai.zelda.level.GANLoader
		//                   edu.southwestern.tasks.gvgai.zelda.level.OriginalLoader
		
		
		MMNEAT.main("zeldaType:generated randomSeed:0 zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.GANLoader".split(" "));
	}

}
