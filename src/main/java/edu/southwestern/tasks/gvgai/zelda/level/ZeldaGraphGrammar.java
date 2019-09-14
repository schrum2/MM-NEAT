package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.GraphUtil;
import edu.southwestern.util.random.RandomNumbers;
import me.jakerg.rougelike.RougelikeApp;

public class ZeldaGraphGrammar extends GraphRuleManager<ZeldaGrammar> {
	public ZeldaGraphGrammar() {
		super();
		
		GraphRule<ZeldaGrammar> rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.BOMB_S);
		rule.grammar().addNodeBetween(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeBetween(ZeldaGrammar.BOMB_S);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.BOMB_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
//		rule.grammar().setStart(ZeldaGrammar.START);
//		rule.grammar().setEnd(ZeldaGrammar.KEY);
//		rule.grammar().setNodeBetween(ZeldaGrammar.PUZZLE);
//		graphRules.add(rule);
		
		// Checking for graph errors, this rule seems to be a problem
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
//		rule.grammar().setStart(ZeldaGrammar.KEY);
//		rule.grammar().setEnd(ZeldaGrammar.LOCK);
//		rule.grammar().setNodeBetween(ZeldaGrammar.PUZZLE);
//		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		rule.grammar().addNodeBetween(ZeldaGrammar.KEY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
//		rule.grammar().setStart(ZeldaGrammar.KEY);
//		rule.grammar().setEnd(ZeldaGrammar.LOCK);
//		rule.grammar().setNodeBetween(ZeldaGrammar.PUZZLE);
//		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeToStart(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.SOFT_LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.SOFT_LOCK);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.BOMB_S);
		rule.grammar().setStart(ZeldaGrammar.BOMB);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.PUZZLE_S);
		rule.grammar().setStart(ZeldaGrammar.PUZZLE);
		graphRules.add(rule);
	}
	
	public ZeldaGraphGrammar(File directory) {
		super(directory);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("About to reset random number generator based on time. Press enter.");
		MiscUtil.waitForReadStringAndEnterKeyPress();
		RandomNumbers.reset((int) System.currentTimeMillis());
		
		List<ZeldaGrammar> initialList = new LinkedList<>();
		initialList.add(ZeldaGrammar.START_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
//		initialList.add(ZeldaGrammar.BOMB_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.PUZZLE_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.TREASURE);
		
		Graph<ZeldaGrammar> graph = new Graph<>(initialList);
		GraphUtil.saveGrammarGraph(graph, "data/VGLC/Zelda/GraphDOTs/start.dot");
		
		System.out.println("\n-----------------------------\n");
		
		ZeldaGraphGrammar grammar = new ZeldaGraphGrammar();
//		ZeldaGraphGrammar grammar = new ZeldaGraphGrammar(new File("data/VGLC/Zelda/rules/1"));
		try {
			grammar.applyRules(graph);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(0);
		}

		System.out.println("After size " + graph.size());
		
		try {
			GraphUtil.saveGrammarGraph(graph, "data/VGLC/graph.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parameters.initializeParameterCollections(new String[] {"zeldaGANUsesOriginalEncoding:false", "zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.GANLoader"});
		
		
		Dungeon d = null;
		try {
			d = DungeonUtil.recursiveGenerateDungeon(graph, (LevelLoader) ClassCreation.createObject("zeldaLevelLoader"));
			DungeonUtil.makeDungeonPlayable(d);
			BufferedImage image = DungeonUtil.imageOfDungeon(d);
			File file = new File("data/VGLC/Zelda/dungeon.png");
			ImageIO.write(image, "png", file);
			
			Desktop desk = Desktop.getDesktop();
			desk.open(file);
			
			RougelikeApp.startDungeon(d);
		} catch (Exception e) {
			e.printStackTrace();
			DungeonUtil.viewDungeon(d);
			// TODO Auto-generated catch block
			
		}
		
		try {
			grammar.saveRules(new File("data/VGLC/Zelda/rules/1"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
