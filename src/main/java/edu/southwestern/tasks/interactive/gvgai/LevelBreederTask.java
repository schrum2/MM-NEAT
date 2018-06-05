package edu.southwestern.tasks.interactive.gvgai;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import gvgai.core.game.BasicGame;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tracks.singlePlayer.tools.human.Agent;

public class LevelBreederTask<T extends Network> extends InteractiveEvolutionTask<T> {
	// Should exceed any of the CPPN inputs or other interface buttons
	public static final int PLAY_BUTTON_INDEX = -20; 
	
	// TODO: Make these two settings into command line parameters
	public static final int GAME_GRID_WIDTH = 30;
	public static final int GAME_GRID_HEIGHT = 20;
	
	// TODO: This next setting should be evolved
	public static final int NUMBER_RANDOM_ITEMS = 10; 
	
	// TODO: Need to generalize this to use the game description instead of having specific rules for each game
	public static final HashMap<String, char[][]> SPECIFIC_GAME_LEVEL_CHARS = new HashMap<String, char[][]>();
	public static final char DEFAULT_FLOOR = '.'; // Probably not true in all games
	public static final char DEFAULT_WALL = 'w'; // Probably not true in all games
	
	// TODO: Specific generalization for each game should not be necessary, but that is what is currently done
	public static final int FIXED_ITEMS_INDEX = 0;
	public static final int UNIQUE_ITEMS_INDEX = 1;
	public static final int RANDOM_ITEMS_INDEX = 2;
	static {
		SPECIFIC_GAME_LEVEL_CHARS.put("zelda", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','+','A'}, // There is one gate, one key, and one avatar
			new char[]{'1','2','3'}}); // There are random monsters dignified by 1, 2, 3
		SPECIFIC_GAME_LEVEL_CHARS.put("blacksmoke", new char[][] {
			new char[]{'w','b','c'}, // There are fixed walls, destructible blocks, and black death squares
			new char[]{'l','k','e','A'}, // There is one locked door, one key, one escape gate, and one avatar
			new char[]{'d'}}); // There are a random number of death smoke blobs
		SPECIFIC_GAME_LEVEL_CHARS.put("chipschallenge", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'-','r','g','b','y','1','2','3','4','e','A'}, // There is one locked gate, four keys, four doors, one escape floor and one avatar
			new char[]{'~','m','x','f','i','d'}}); // There are random hazards dignified by ~(water), m(mud), x(fire) and random perks dignified by f (flippers), i(fireboots), d(crate)
		//TODO: fix aliens: everything spawns yet some levels are broken, force the spawn of the avatar and the aliens
		SPECIFIC_GAME_LEVEL_CHARS.put("aliens", new char[][] {
			new char[]{'.'}, // floor
			new char[]{'1','2','A'}, // There is one slow portal, one fast portal, and one avatar
			new char[]{'0'}}); // There are base blocks dignified by 0 
		SPECIFIC_GAME_LEVEL_CHARS.put("pacman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','2','3','4','A'}, // There are four ghosts and one avatar
			new char[]{'.','f','0','+'}}); // There are random pellets, fruits. and power pellets
		SPECIFIC_GAME_LEVEL_CHARS.put("angelsdemons", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','a','d','d','i','o','A'}, // There are two angels, two demons, an input and output, and an Avatar
			new char[]{'t','x'}}); // There are random sky boxes and a sky trunk
		SPECIFIC_GAME_LEVEL_CHARS.put("assemblyline", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'l','r','u','d','g','p','v','A'}, // Spawns a lcleft, lcright,lcup, lcdown, goal, portal, vortex, and an Avatar
			new char[]{'1','2','3','4','5','6','7','8','9'}}); // Spawns random assemblies
		SPECIFIC_GAME_LEVEL_CHARS.put("avoidgeorge", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns George and an Avatar
			new char[]{'c'}}); // Spawns random number of quiet
		SPECIFIC_GAME_LEVEL_CHARS.put("bait", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','k','A'}, // Spawns a key, goal, and an Avatar
			new char[]{'m','0'}}); //Spawns a random number of mushrooms and holes
		SPECIFIC_GAME_LEVEL_CHARS.put("beltmanager", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','2','3','4','p','s','A'}, // 
			new char[]{'b','d','j'}}); //
		SPECIFIC_GAME_LEVEL_CHARS.put("boloadventures", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns a goal and an Avatar
			new char[]{'b','c','l','r','u','d','o'}}); // Spawns a random number of boxes, boulders, lcleft, lcright,lcup, lcdown, and holes
		SPECIFIC_GAME_LEVEL_CHARS.put("bomber", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns a goal and an Avatar
			new char[]{'1','2','3','4','e','b','~'}}); // Spawns a random number of lcleft, lcright,lcup, lcdown, boxes, bombs, and water
		//TODO: fix this
		SPECIFIC_GAME_LEVEL_CHARS.put("bomberman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns the goal and an Avatar
			new char[]{'b','s','c','q'}}); // Spawns a random number of bats, spiders, scorpions, and breakable walls
		SPECIFIC_GAME_LEVEL_CHARS.put("boulderchase", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // Spawns and exit and an Avatar
			new char[]{'o','x','c','b'}}); // Spawns a random number of boulders, diamonds, crabs, butterflies
		SPECIFIC_GAME_LEVEL_CHARS.put("boulderdash", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // Spawns and exit and an Avatar
			new char[]{'o','x','c','b'}}); //Spawns a random number of boulders, diamonds, crabs, butterflies
		SPECIFIC_GAME_LEVEL_CHARS.put("brainman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'k','d','e','A'}, // Spawns a key, door, exit and an Avatar
			new char[]{'r','g','b','o'}}); //Spawns a random number of red, green, and blue gems, and blouders
		SPECIFIC_GAME_LEVEL_CHARS.put("butterflies", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Spawns an Avatar
			new char[]{'1','0'}}); // Spawns a random number of butterflies and cocoons
		SPECIFIC_GAME_LEVEL_CHARS.put("cakybaky", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','2','3','4','5','6','A'}, // Spawns all the objectives and an Avatar
			new char[]{'t','c'}}); // Spawns a random number of tables and the chefs
		SPECIFIC_GAME_LEVEL_CHARS.put("camelRace", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'r','h','n','t','f','m','s','g','g','g','g','g','g','g'}, // Spawns different camels and a goal for each camel
			new char[]{'A','B'}}); // Spawns a right and left
		//TODO: fix water spawns
		SPECIFIC_GAME_LEVEL_CHARS.put("catapults", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A',}, // Spawns a goal and an Avatar
			new char[]{'0','1','2','3','_'}}); // Spawns random launch pads, and water
		SPECIFIC_GAME_LEVEL_CHARS.put("chainreaction", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'m','c','A'}, // Spawns the master boulder and an Avatar
			new char[]{'g','0','c','b'}}); // Spawns a random number of goals, holes, boulders and boxes
		SPECIFIC_GAME_LEVEL_CHARS.put("chase", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Spawns an Avatar
			new char[]{'0'}}); // Spawns a Crow?
		//TODO: fix this, game type to sophisticated
		SPECIFIC_GAME_LEVEL_CHARS.put("chopper", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{' '}, // 
			new char[]{' '}}); //
		SPECIFIC_GAME_LEVEL_CHARS.put("portals", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'i','o','2','3','g','A'}, // Spawns portals and an Avatar
			new char[]{'i','o','2','3'}}); // Randomly spawns portals
		SPECIFIC_GAME_LEVEL_CHARS.put("clusters", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','b','c','A'}, // Spawns a solid red, green, and blue block and also an Avatar
			new char[]{'1','2','3','h'}}); //Spawns a random number of holes and red, green, blue movable blocks
		SPECIFIC_GAME_LEVEL_CHARS.put("colourescape", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','b','c','d','x','A'}, // Spawns a normal, red, green, and blue switch and also an Avatar
			new char[]{'1','2','3','4','h'}}); //Spawns a random number of and normal, red, green, blue blocks, and holes
		SPECIFIC_GAME_LEVEL_CHARS.put("cookmepasta", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'b','p','o','t','k','l','A'}, // Spawns all the ingredients and an Avatar
			new char[]{'.'}}); //null
		SPECIFIC_GAME_LEVEL_CHARS.put("cops", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'0','1','d','b','A'}, // Spawns a jail, depot, key, and an Avatar
			new char[]{'g','y','r','d'}}); // spawns different levels of criminals
		SPECIFIC_GAME_LEVEL_CHARS.put("crossfire", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns a goal and an Avatar
			new char[]{'t'}}); // Spawns a random number of turrets
		SPECIFIC_GAME_LEVEL_CHARS.put("defem", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a'}, // Spawns an Avatar
			new char[]{'r','c','z','x','f','v'}}); // Spawns random number of enemies
		SPECIFIC_GAME_LEVEL_CHARS.put("defender", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Spawns avatar
			new char[]{'0','1','3',}}); // spawn portals for aliens
		SPECIFIC_GAME_LEVEL_CHARS.put("digdug", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // spawn Avatar
			new char[]{'0','1','m','e'}}); //spawns gems gold monsters and an entrance
		SPECIFIC_GAME_LEVEL_CHARS.put("dungeon", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','k','m','A'}, // Spawns exit, key, lock, avatar
			new char[]{'g','f','1','2','t','l','r','u','d'}}); // Spwans gold, firehole, boulders, lasers
		SPECIFIC_GAME_LEVEL_CHARS.put("eggomania", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'s','c','A'}, // Avatar and chickens
			new char[]{' '}}); // N/A
		SPECIFIC_GAME_LEVEL_CHARS.put("eighthpassenger", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','e','A'}, // alien, avatar and exit
			new char[]{'t','x','n','m','d','s'}}); //tunnels and door
		SPECIFIC_GAME_LEVEL_CHARS.put("enemycitadel", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','e','0','1','g','A'}, // enemy, holes, goal, avatar
			new char[]{'b','c'}}); //boulder, crate
		SPECIFIC_GAME_LEVEL_CHARS.put("escape", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','A'}, // exit and avatar
			new char[]{'h','b'}}); // holes and boxes
		SPECIFIC_GAME_LEVEL_CHARS.put("factorymanager", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'l','r','u','d','p','b','h','s','t'}}); //lasers, portal, boc, hgighway, street, and a trap
		SPECIFIC_GAME_LEVEL_CHARS.put("firecaster", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // goal, avatar
			new char[]{'b','.'}}); //box, mana
		SPECIFIC_GAME_LEVEL_CHARS.put("fireman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // extinguisher, avatar
			new char[]{'b','f'}}); // box, fire
		SPECIFIC_GAME_LEVEL_CHARS.put("firestorms", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','0','A'}, // exit, seed, avatar
			new char[]{'h'}}); // water
		SPECIFIC_GAME_LEVEL_CHARS.put("freeway", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'S','i'}, // 
			new char[]{'+','x','t','-','_','l'}}); //
//		SPECIFIC_GAME_LEVEL_CHARS.put("", new char[][] {
	//		new char[]{'w'}, // Walls are fixed
	//		new char[]{'a','a','d','d','i','o','A'}, // 
	//		new char[]{'t','x'}}); //
//		SPECIFIC_GAME_LEVEL_CHARS.put("", new char[][] {
	//		new char[]{'w'}, // Walls are fixed
	//		new char[]{'a','a','d','d','i','o','A'}, // 
	//		new char[]{'t','x'}}); //
//		SPECIFIC_GAME_LEVEL_CHARS.put("", new char[][] {
	//		new char[]{'w'}, // Walls are fixed
	//		new char[]{'a','a','d','d','i','o','A'}, // 
	//		new char[]{'t','x'}}); //
//		SPECIFIC_GAME_LEVEL_CHARS.put("", new char[][] {
	//		new char[]{'w'}, // Walls are fixed
	//		new char[]{'a','a','d','d','i','o','A'}, // 
	//		new char[]{'t','x'}}); //
//		SPECIFIC_GAME_LEVEL_CHARS.put("", new char[][] {
	//		new char[]{'w'}, // Walls are fixed
	//		new char[]{'a','a','d','d','i','o','A'}, // 
	//		new char[]{'t','x'}}); //

	}
	
	public static final String GAMES_PATH = "data/gvgai/examples/gridphysics/";
	private String fullGameFile;
	private String gameFile;
	private char[][] gameCharData;
	
	public LevelBreederTask() throws IllegalAccessException {
		super();

		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();
		
		gameFile = Parameters.parameters.stringParameter("gvgaiGame");
		fullGameFile = GAMES_PATH + gameFile + ".txt";
		gameCharData = SPECIFIC_GAME_LEVEL_CHARS.get(gameFile);
		
		//Construction of button that lets user plays the level
		JButton play = new JButton("Play");
		// Name is first available numeric label after the input disablers
		play.setName("" + PLAY_BUTTON_INDEX);
		play.addActionListener(this);
		top.add(play);

	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias" };
	}

	@Override
	public String[] outputLabels() {
		ArrayList<String> outputs = new ArrayList<String>(10);
		outputs.add("FixedPresence");
		
		for(Character c : gameCharData[FIXED_ITEMS_INDEX]) {
			outputs.add("Fixed-"+c);
		}
		for(Character c : gameCharData[UNIQUE_ITEMS_INDEX]) {
			outputs.add("Unique-"+c);
		}		
		outputs.add("Random");
		
		// Convert the ArrayList<String> to a String[] and return
		String str = outputs.toString();
		String[] result = str.substring(1,str.length() - 1).split(", ");
		return result;
	}

	@Override
	protected String getWindowTitle() {
		return "Level Breeder";
	}

	@Override
	protected void save(String file, int i) {
		String[] level = GVGAIUtil.generateLevelFromCPPN((Network)scores.get(i).individual.getPhenotype(), inputMultipliers, GAME_GRID_WIDTH, GAME_GRID_HEIGHT, DEFAULT_FLOOR, DEFAULT_WALL, 
				gameCharData[FIXED_ITEMS_INDEX], gameCharData[UNIQUE_ITEMS_INDEX], gameCharData[RANDOM_ITEMS_INDEX], NUMBER_RANDOM_ITEMS);
		// Prepare text file
		try {
			PrintStream ps = new PrintStream(new File(file));
			// Write String array to text file 
			for(String line : level) {
				ps.println(line);
			}
			ps.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not save file: " + file);
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Use a CPPN to create a level and wrap in a game bundle with a new game.
	 * @param phenotype CPPN
	 * @return Bundle of information for running a game
	 */
	public GameBundle setUpGameWithLevelFromCPPN(Network phenotype) {
		String[] level = GVGAIUtil.generateLevelFromCPPN(phenotype, inputMultipliers, GAME_GRID_WIDTH, GAME_GRID_HEIGHT, DEFAULT_FLOOR, DEFAULT_WALL, 
				gameCharData[FIXED_ITEMS_INDEX], gameCharData[UNIQUE_ITEMS_INDEX], gameCharData[RANDOM_ITEMS_INDEX], NUMBER_RANDOM_ITEMS);
		int seed = 0; // TODO: Use parameter?
		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 
		Game game = new VGDLParser().parseGame(fullGameFile); // Initialize the game	

		return new GameBundle(game, level, agent, seed, 0);
	}
	
	@Override
	protected BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers) {
		GameBundle bundle = setUpGameWithLevelFromCPPN(phenotype);
		BufferedImage levelImage = GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, width, height, bundle.randomSeed);
		return levelImage;
	}
		
	/**
	 * Responds to a button to actually play a selected level
	 */
	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
		// Human plays level
		if(itemID == PLAY_BUTTON_INDEX && selectedCPPNs.size() > 0) {
			Network cppn = scores.get(selectedCPPNs.get(selectedCPPNs.size() - 1)).individual.getPhenotype();
			GameBundle bundle = setUpGameWithLevelFromCPPN(cppn);
			// Must launch game in own thread, or won't animate or listen for events
			new Thread() {
				public void run() {
					// True is to watch the game being played
					GVGAIUtil.runOneGame(bundle, true);
				}
			}.start();
			System.out.println("Launched");
		}
		return false; // no undo: every thing is fine
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getFileType() {
		return "Text File";
	}

	@Override
	protected String getFileExtension() {
		return "txt";
	}

	@Override
	public int numCPPNInputs() {
		return sensorLabels().length;
	}

	@Override
	public int numCPPNOutputs() {
		return outputLabels().length;
	}

	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","trials:1","mu:16","maxGens:500","gvgaiGame:freeway","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.LevelBreederTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
