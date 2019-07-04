package edu.southwestern.tasks.gvgai.zelda.level;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.GraphUtil;
import edu.southwestern.util.random.RandomNumbers;

public class ZeldaGraphGrammarTest {

	List<ZeldaGrammar> initialList;
	ZeldaGraphGrammar grammar;
	LevelLoader loader;
	
	@Before
	public void setUp() {
		Parameters.initializeParameterCollections(new String[] {"zeldaGANUsesOriginalEncoding:false"});
		
		initialList = new LinkedList<>();
		initialList.add(ZeldaGrammar.START_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.TREASURE);
		
		grammar = new ZeldaGraphGrammar();
		
		loader = new GANLoader();
		
		RandomNumbers.reset();
	}
	
	@Test
	public void test() {
		for(int i = 0; i <= 100; i++) {
			RandomNumbers.reset(i);
			Graph<ZeldaGrammar> graph = new Graph<>(initialList);
			
			Dungeon d = null;
			try {
				grammar.applyRules(graph);
				d = DungeonUtil.recursiveGenerateDungeon(graph, loader);
				System.out.println("Starting dungeon playable for dungeon: " + i);
				GraphUtil.saveGrammarGraph(graph, "data/VGLC/" + i +"_test.dot");
				DungeonUtil.makeDungeonPlayable(d);				
				BufferedImage image = DungeonUtil.imageOfDungeon(d);
				File file = new File("data/VGLC/Zelda/dungeon_"+ i +"_test.png");
				ImageIO.write(image, "png", file);
			} catch (Exception e) {
				e.printStackTrace();
				DungeonUtil.viewDungeon(d);
				try {
					GraphUtil.saveGrammarGraph(graph, "data/VGLC/" + i +"_test.dot");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				MiscUtil.waitForReadStringAndEnterKeyPress();
				fail("Test number : " + i + " failed");
			}
		    System.out.print("\033[H\033[2J");  
		    System.out.flush();
			RandomNumbers.reset();
		}
	}

}
