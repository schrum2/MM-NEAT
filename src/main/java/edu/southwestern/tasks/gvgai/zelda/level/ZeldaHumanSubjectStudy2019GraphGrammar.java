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
/**
 * This class sets various rules for the creation of new rooms in a dungeon
 * given a backbone.
 * 
 * Original ZeldaGraphGrammar paper: Jake Gutierrez and Jacob Schrum. Generative Adversarial 
 * Network Rooms in Generative Graph Grammar Dungeons for The Legend of Zelda. 
 * In IEEE Congress on Evolutionary Computation. 2020
 * https://people.southwestern.edu/~schrum2/SCOPE/gutierrez.cec2020.pdf
 * @author Jake Gutierrez
 *
 */
public class ZeldaHumanSubjectStudy2019GraphGrammar extends GraphRuleManager<ZeldaGrammar> {
	public ZeldaHumanSubjectStudy2019GraphGrammar() {
		super();
		
		//if the start room and an enemy room are adjacent, 
		//then add an enemy room after start
		//then add a bomb room and a soft lock room, with an enemy room at the end.
		GraphRule<ZeldaGrammar> rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.BOMB_S);
		rule.grammar().addNodeBetween(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		//if the start room and an enemy room are adjacent, then add a soft lock
		//START is adjacent to ENEMY which is adjacent to ENEMY. 
		//START off-shoots a SOFT_LOCK_S (which leads to the ENEMY room at the end)
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		//if an enemy room and a key room are adjacent
		//add an off-shoot from ENEMY to BOMB_S (which leads to KEY)
		//and maintain adjacency to KEY
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeBetween(ZeldaGrammar.BOMB_S);
		graphRules.add(rule);
		
		//If there's a bomb room with no adjacency,
		//replace it with ENEMY
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.BOMB_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		//if the start room and a key room are adjacent,
		//START is adjacent to ENEMY, which leads to ENEMY, which leads to KEY.
		//START off-shoots to an ENEMY room that is adjacent to the KEY room
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		//if the start room and a key room are adjacent,
		//START leads to and ENEMY room, which leads to a KEY room
		//START off-shoots to an ENEMY room that leads to the KEY room
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
		
		//if a KEY room leads to a LOCK room
		//ENEMY room leads to LOCK room
		//adds KEY room to ENEMY that leads to the LOCK
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		rule.grammar().addNodeBetween(ZeldaGrammar.KEY);
		graphRules.add(rule);
		
		//If a room has a KEY and a LOCK adjacent,
		//The KEY room leads to an ENEMY room, which leads to the LOCKed room
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
		
		//If a KEY room and an ENEMY room are adjacent,
		//the KEY room leads to an ENEMY room, which leads to the end ENEMY room.
		//the KEY room also leads to another ENEMY room that leads to the end ENEMY room.
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		//if an ENEMY room and a TREASURE room are adjacent
		//ENEMY room leads to ENEMY room, which leads to the TREASURE room
		//the first ENEMY room has another ENEMY room which also leads to the TREASURE room
		//(Two paths of enemy rooms to the same treasure room)
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		//if an ENEMY room and a TREASURE room are adjacent
		//ENEMY room leads to TREASURE room
		//ENEMY room also leads to another ENEMY room which leads to the TREASURE room
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		//if an ENEMY room and a TREASURE room are adjacent
		//ENEMY room leads to a SOFT_LOCK room which leads to the TREASURE room
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeToStart(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		//if there is a LOCK room by itself
		//then sets the room as a LOCK room by itself
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		
		//if there is a KEY room by itself
		//then KEY room leads to a SOFT-LOCK room
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.SOFT_LOCK_S);
		graphRules.add(rule);
		
		//if there is a SOFT_LOCK room by itself
		//SOFT_LOCK room leads to an ENEMY room
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.SOFT_LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.SOFT_LOCK);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		//if there is a bombable door by itself
		//then there is just a bombable door
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.BOMB_S);
		rule.grammar().setStart(ZeldaGrammar.BOMB);
		graphRules.add(rule);
		
		//if there is a PUZZLE room by itself
		//then there is just a PUZZLE room by itself
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.PUZZLE_S);
		rule.grammar().setStart(ZeldaGrammar.PUZZLE);
		graphRules.add(rule);
		
		
	}

	public ZeldaHumanSubjectStudy2019GraphGrammar(File directory) {
		super(directory);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("About to reset random number generator based on time. Press enter.");
		MiscUtil.waitForReadStringAndEnterKeyPress();
		RandomNumbers.reset((int) System.currentTimeMillis());
		//add the types of rooms to the list for reset
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

		ZeldaHumanSubjectStudy2019GraphGrammar grammar = new ZeldaHumanSubjectStudy2019GraphGrammar();
//		ZeldaGraphGrammar grammar = new ZeldaGraphGrammar(new File("data/VGLC/Zelda/rules/1"));
		try {
			grammar.applyRules(graph); //try applying the rules
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(0);
		}

		System.out.println("After size " + graph.size());

		try {
			GraphUtil.saveGrammarGraph(graph, "data/VGLC/graph.dot"); //try saving
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); //see what goes wrong
		}
		//set the parameters
		Parameters.initializeParameterCollections(new String[] {"zeldaGANUsesOriginalEncoding:false", "zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.GANLoader"});
		
		
		Dungeon d = null;
		try {//create, save, and run dungeon
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
		
		try { //save the rules
			grammar.saveRules(new File("data/VGLC/Zelda/rules/1"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
