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
	public static final int GAME_GRID_WIDTH = 20;
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
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","trials:1","mu:16","maxGens:500","gvgaiGame:chipschallenge","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.LevelBreederTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
