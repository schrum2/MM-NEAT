package edu.southwestern.tasks.interactive.gvgai;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
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

public class LevelBreederTask extends InteractiveEvolutionTask<TWEANN> {
	// Should exceed any of the CPPN inputs or other interface buttons
	public static final int PLAY_BUTTON_INDEX = -20; 
	
	// TODO: Make these two settings into command line parameters
	public static final int GAME_GRID_WIDTH = 30;
	public static final int GAME_GRID_HEIGHT = 20;
	
	// TODO: This next setting should be evolved
	public static final int NUMBER_RANDOM_ITEMS = 10; 
	
	// TODO: Need to generalize this to use the game description instead of having specific rules for each game
	public static final HashMap<String, char[][]> SPECIFIC_GAME_LEVEL_CHARS = new HashMap<String, char[][]>();
	
	// TODO: Specific generalization for each game should not be necessary, but that is what is currently done
	public static final int FIXED_ITEMS_INDEX = 0;
	public static final int UNIQUE_ITEMS_INDEX = 1;
	public static final int RANDOM_ITEMS_INDEX = 2;
	public static final int FLOOR_INDEX = 3; // Will always only have one character
	public static final int WALL_INDEX = 3; // Will always only have one character
	public static final int BOTTOM_ITEMS_INDEX = 4; // Items that prefer to be at the bottom of the screen
	static {
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("zelda", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','+','A'}, // There is one gate, one key, and one avatar
			new char[]{'1','2','3'}, // There are random monsters signified by 1, 2, 3
			new char[]{'.'},
			new char[]{'w'},
			new char[0]});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("blacksmoke", new char[][] {
			new char[]{'w','b','c'}, // There are fixed walls, destructible blocks, and black death squares
			new char[]{'l','k','e','A'}, // There is one locked door, one key, one escape gate, and one avatar
			new char[]{'d'}, // There are a random number of death smoke blobs
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("chipschallenge", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'-','r','g','b','y','1','2','3','4','e','A'}, // There is one locked gate, four keys, four doors, one escape floor and one avatar
			new char[]{'~','m','x','f','i','d'}, // There are random hazards dignified by ~(water), m(mud), x(fire) and random perks dignified by f (flippers), i(fireboots), d(crate)
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		//TODO: fix aliens: everything spawns yet some levels are broken, force the spawn of the avatar and the aliens
		SPECIFIC_GAME_LEVEL_CHARS.put("aliens", new char[][] {
			new char[]{'.'}, // floor
			new char[]{'1','2'}, // There is one slow portal, one fast portal, and one avatar
			new char[]{'0'}, // There are base blocks dignified by 0 
			new char[]{'.'},
			new char[]{'w'},
			new char[]{'A'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("pacman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','2','3','4','A'}, // There are four ghosts and one avatar
			new char[]{'.','f','0','+'}, // There are random pellets, fruits. and power pellets
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("angelsdemons", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','a','d','d','i','o','A'}, // There are two angels, two demons, an input and output, and an Avatar
			new char[]{'t','x'}, // There are random sky boxes and a sky trunk
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("assemblyline", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'l','r','u','d','g','p','v','A'}, // Spawns a lcleft, lcright,lcup, lcdown, goal, portal, vortex, and an Avatar
			new char[]{'1','2','3','4','5','6','7','8','9'}, // Spawns random assemblies
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("avoidgeorge", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns George and an Avatar
			new char[]{'c'}, // Spawns random number of quiet
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("bait", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','k','A'}, // Spawns a key, goal, and an Avatar
			new char[]{'m','0'}, //Spawns a random number of mushrooms and holes
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("beltmanager", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','2','3','4','p','s','A'}, // 
			new char[]{'b','d','j'}, //
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("boloadventures", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns a goal and an Avatar
			new char[]{'b','c','l','r','u','d','o'}, // Spawns a random number of boxes, boulders, lcleft, lcright,lcup, lcdown, and holes
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("bomber", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns a goal and an Avatar
			new char[]{'1','2','3','4','e','b','.'}, // Spawns a random number of lcleft, lcright,lcup, lcdown, boxes, bombs, and water
			new char[]{','},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		//TODO: fix this
		SPECIFIC_GAME_LEVEL_CHARS.put("bomberman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns the goal and an Avatar
			new char[]{'b','s','c','q'}, // Spawns a random number of bats, spiders, scorpions, and breakable walls
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("boulderchase", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // Spawns and exit and an Avatar
			new char[]{'o','x','c','b'}, // Spawns a random number of boulders, diamonds, crabs, butterflies
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("boulderdash", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // Spawns and exit and an Avatar
			new char[]{'o','x','c','b'}, //Spawns a random number of boulders, diamonds, crabs, butterflies
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("brainman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'k','d','e','A'}, // Spawns a key, door, exit and an Avatar
			new char[]{'r','g','b','o'}, //Spawns a random number of red, green, and blue gems, and blouders
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("butterflies", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Spawns an Avatar
			new char[]{'1','0'}, // Spawns a random number of butterflies and cocoons
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("cakybaky", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','2','3','4','5','6','A'}, // Spawns all the objectives and an Avatar
			new char[]{'t','c'}, // Spawns a random number of tables and the chefs
			new char[]{'.'},
			new char[]{'w'}});
		//DOES NOT WORK
		SPECIFIC_GAME_LEVEL_CHARS.put("camelRace", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'r','h','n','t','f','m','s','g','g','g','g','g','g','g'}, // Spawns different camels and a goal for each camel
			new char[]{'A','B'}, // Spawns a right and left
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		//TODO: fix water spawns
		SPECIFIC_GAME_LEVEL_CHARS.put("catapults", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A',}, // Spawns a goal and an Avatar
			new char[]{'0','1','2','3','_'}, // Spawns random launch pads, and water
			new char[]{'.'},
			new char[]{'w'}});
		//DOES NOT WORK
		SPECIFIC_GAME_LEVEL_CHARS.put("chainreaction", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'m','c','A'}, // Spawns the master boulder and an Avatar
			new char[]{'g','0','c','b'}, // Spawns a random number of goals, holes, boulders and boxes
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("chase", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Spawns an Avatar
			new char[]{'0'}, // Spawns a Crow?
			new char[]{'.'},
			new char[]{'w'}});
		//DOES NOT WORK
		//TODO: fix this, game type, to0 sophisticated
		SPECIFIC_GAME_LEVEL_CHARS.put("chopper", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{' '}, // 
			new char[]{' '}, //
			new char[]{'.'},
			new char[]{'w'}});
		//Broken
		SPECIFIC_GAME_LEVEL_CHARS.put("portals", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'i','o','2','3','g','A'}, // Spawns portals and an Avatar
			new char[]{'i','o','2','3'}, // Randomly spawns portals
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("clusters", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','b','c','A'}, // Spawns a solid red, green, and blue block and also an Avatar
			new char[]{'1','2','3','h'}, //Spawns a random number of holes and red, green, blue movable blocks
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("colourescape", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','b','c','d','x','A'}, // Spawns a normal, red, green, and blue switch and also an Avatar
			new char[]{'1','2','3','4','h'}, //Spawns a random number of and normal, red, green, blue blocks, and holes
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("cookmepasta", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'b','p','o','t','k','l','A'}, // Spawns all the ingredients and an Avatar
			new char[]{'.'}, //null
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("cops", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'0','1','d','b','A'}, // Spawns a jail, depot, key, and an Avatar
			new char[]{'g','y','r','d'}, // spawns different levels of criminals
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("crossfire", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // Spawns a goal and an Avatar
			new char[]{'t'}, // Spawns a random number of turrets
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("defem", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a'}, // Spawns an Avatar
			new char[]{'r','c','z','x','f','v'}, // Spawns random number of enemies
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("defender", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Spawns avatar
			new char[]{'0','1','3',}, // spawn portals for aliens
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("digdug", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // spawn Avatar
			new char[]{'0','1','m','e'}, //spawns gems gold monsters and an entrance
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("dungeon", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','k','m','A'}, // Spawns exit, key, lock, avatar
			new char[]{'g','f','1','2','t','l','r','u','d'}, // Spwans gold, firehole, boulders, lasers
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("eggomania", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'s','c','A'}, // Avatar and chickens
			new char[]{' '}, // N/A
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("eighthpassenger", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','e','A'}, // alien, avatar and exit
			new char[]{'t','x','n','m','d','s'}, //tunnels and door
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("enemycitadel", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','e','0','1','g','A'}, // enemy, holes, goal, avatar
			new char[]{'b','c'}, //boulder, crate
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("escape", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','A'}, // exit and avatar
			new char[]{'h','b'}, // holes and boxes
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("factorymanager", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'l','r','u','d','p','b','h','s','t'}, //lasers, portal, boc, hgighway, street, and a trap
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("firecaster", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // goal, avatar
			new char[]{'b','.'}, //box, mana
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("fireman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // extinguisher, avatar
			new char[]{'b','f'}, // box, fire
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("firestorms", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','0','A'}, // exit, seed, avatar
			new char[]{'h'}, // water
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("freeway", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'S','i'}, // 
			new char[]{'+','x','t','-','_','l'}, //
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		//TODO: fix this, complicated game play
		SPECIFIC_GAME_LEVEL_CHARS.put("frogs", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','0','A'}, //goal tree avatar
			new char[]{'-','x','_','l','='}, // obstacles?
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("garbagecollector", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'g'}, // garbage
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		//TODO: fix this, complicated game play
		SPECIFIC_GAME_LEVEL_CHARS.put("gymkhana", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'.','-',',','0','1','2','3','r','l','g'}, //trees
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("hungrybirds", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // 
			new char[]{'f'}, //
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("iceandfire", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // 
			new char[]{'t','c','i','f','_','x'}, //
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		//TODO:same issue with aliens
		SPECIFIC_GAME_LEVEL_CHARS.put("ikaruga", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'q','w','e','r','z','w','A'}, // portals, color changers, avatar
			new char[]{' '}, //
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("infection", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'1','0','e','A'}, // 
			new char[]{'1','0','x'}, //
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("intersection", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'s','i'}, // avatar and input
			new char[]{'t','x','-','_','l'}, //tree and cars
			new char[]{'='},
			new char[]{'w'}});
		//BROKEN
		//TODO: game play a little wonky
		SPECIFIC_GAME_LEVEL_CHARS.put("islands", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // 
			new char[]{'b','x','p','t'}, //
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("jaws", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'3','A'}, // shark, avatar
			new char[]{'1','2'}, // pitanhahole, whalehole
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		//TODO: fix bat collision
		SPECIFIC_GAME_LEVEL_CHARS.put("killBillVol1", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // exit and avatar
			new char[]{'u','d','h','x','y','z'}, // "doors" and enemies
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("labyrinth", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','A'}, // exit, avatar
			new char[]{'t'}, // random traps
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("labyrinthdual", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','A'}, // exit, avatar
			new char[]{'t','r','b','1','2'}, // random traps, walls, and cloaks
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("lasers", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'l','r','u','d','g','b','A'}, // lasers and avatar
			new char[]{'l','r','u','d','b'}, //lasers
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("lasers2", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'m','s','t','e','u','d','r','l','g','b','A'}, // lasers and avatar with other obstacles
			new char[]{'l','r','u','d','b','m','s','t','e'}, // lasers and other obstacles
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("lemmings", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','e','A'}, // exit. entry, and avatar
			new char[]{'h'}, // holes
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("missilecommand", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A','c'}, // avatar and city
			new char[]{'m','f','c'}, //different speeds of missiles andrandom number of cities
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("modality", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'.','+','g','b','*','@','t','u','A'}, // 
			new char[]{' '}, //
			new char[]{'.'},
			new char[]{'w'}});
		//DOES NOT WORK
		//TODO: GAME FILE DOESNT EXSIST
		SPECIFIC_GAME_LEVEL_CHARS.put("myAliens", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'0','1','2'}, // 
			new char[]{' '}, //
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("overload", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // goal and avatar
			new char[]{'0','s','1'}, //gold, random, weapon
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("painter", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'x'}, // paint
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		//TODO: lacks foramt like aliens
		SPECIFIC_GAME_LEVEL_CHARS.put("plants", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','h','f','z','x','t','A'}, // goal, zombies, avatar
			new char[]{'g','h','f','z','x','t'}, // goals and zombies
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("plaqueattack", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'h','d','b','v','n','m','A'}, //food and avatar 
			new char[]{'h','d','b','v','n','m'}, //random food
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("pokemon", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'0','1','2','A'}, // pokemon and avatar
			new char[]{'d','a','t'}, // trainers pokemon
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("witnessprotection", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','l','r','.',',','1','2','3','4','n','A'}, // all directions of ally and enemies also spawns avatar
			new char[]{' '}, //
			new char[]{'_'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("wrapsokoban", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Avatar
			new char[]{'o','*'}, // hole and box
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("zenpuzzle", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'r','g'}, //rock, ground
			new char[]{'.'},
			new char[]{'w'}});
		//DOES NOT WORK
		SPECIFIC_GAME_LEVEL_CHARS.put("racebet", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'.','1','2','3','4','a','b','c','d','g','g','g','g','A'}, // arena, camels pit, camels, goals, normal arena
			new char[]{' '}, //
			new char[]{'+'},
			new char[]{'w'}});
		//DOES NOT WORK
		SPECIFIC_GAME_LEVEL_CHARS.put("racebet2", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'.','1','2','3','4','a','b','c','d','g','g','g','g','A'}, // arena, camels pit, camels, goals, normal arena
			new char[]{'t','_'}, // traps and barriers
			new char[]{'+'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("realportals", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','t','p','k','o','A'}, // goal, items, key, boulder, avatar
			new char[]{'.','x'}, //water
			new char[]{'+'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("realsokoban", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Avatar
			new char[]{'o','*'}, // hole and box
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("rivers", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'h','n','r'}, // house, water, rock
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("roadfighter", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','A'}, // exit, avatar
			new char[]{'f','t','s','c'}, // trees, fast and slow car
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("roguelike", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'x','k','l','m','A'}, // exit, key, lock, market, avatar
			new char[]{'s','g','r','p','h'}, // weapon, gold, spider, phantom, health
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("run", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','k','l','A'}, // exit, key, lock, avatar
			new char[]{'c','d'}, //cliff, damaged cliff
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("seaquest", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'a','A','3','4'}, // sky and avatar, normal, oft
			new char[]{'1','2'}, // shark, whale
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("sheriff", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'0','u','d','r','1','2','3','4','A'}, // 
			new char[]{' '}, //
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("shipwreck", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'1','2','3','w','s','w'}, // gold, gems, diamonds, whirlpool, shipwreck, land
			new char[]{'.'},
			new char[]{'x'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("sokoban", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // Avatar
			new char[]{'o','1'}, // hole and box
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("solarfox", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'1','2','.','b','p'}, // enemy spawn, blips
			new char[]{'+'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("superman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'j','A'}, // jail, avatar
			new char[]{'b','s','l','r','c','p','q'}, // building, civilian, portals, sky
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("surround", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'1'}, //mud
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("survivezombies", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'0','1','2','.','_'}, // flower, zombie portals, honey, zombie
			new char[]{'+'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("tercio", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{' '}, // 
			new char[]{'.','+','-','g','B','A','L','*','@','$','t','u','r'}, // not quite sure what is spawning
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("thecitadel", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'g','A'}, // goal and avatar
			new char[]{'0','1','b','c'}, // roundhole, squarehole, boulder, crate
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("thesnowman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'s','b','c','h','k','l','A'}, // base, body, chest, head, key, lock
			new char[]{' '}, //
			new char[]{'.'},
			new char[]{'w'}});
		//DOES NOT WORK
		SPECIFIC_GAME_LEVEL_CHARS.put("waitforbreakfast", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'e','A'}, // exit, avatar
			new char[]{'t','b','f','l','r','0','1','2','3'}, // different floors
			new char[]{'k'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("watergame", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'c','x','o'}, // box, water, door
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("waves", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'1','2','s'}, // portalsSlow, rockPortal, asteroid
			new char[]{'.'},
			new char[]{'w'}});
		//SEMI-PLAYABLE
		SPECIFIC_GAME_LEVEL_CHARS.put("whackamole", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'0','1','2'}, // wide wall, tight wall, cat wall
			new char[]{'.'},
			new char[]{'w'}});
		//BROKEN
		SPECIFIC_GAME_LEVEL_CHARS.put("wildgunman", new char[][] {
			new char[]{'w'}, // Walls are fixed
			new char[]{'A'}, // avatar
			new char[]{'c','m','f','n'}, // city floor, slow portal, fast portal, nice guy portal
			new char[]{'.'},
			new char[]{'w'}});
		

	}
	
	public static final String GAMES_PATH = "data/gvgai/examples/gridphysics/";
	private String fullGameFile;
	private String gameFile;
	private char[][] gameCharData;
	
	protected JComboBox<String> gameChoice;
	
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
		
		
		//creates a string of all game names
		Set<String> gameNames = SPECIFIC_GAME_LEVEL_CHARS.keySet();
		//converts the string to an array
		String[] choices = gameNames.toArray(new String[gameNames.size()]);
		//sorts list alphabetically
		Arrays.sort(choices);
		
		JLabel Loading = new JLabel ("Loading...", JLabel.RIGHT);
		top.setLayout(new FlowLayout());
		
		gameChoice = new JComboBox<String>(choices);
		gameChoice.setSelectedIndex(choices.length - 1); 
		gameChoice.setSize(90, 40);
		gameChoice.setSelectedItem(gameFile);
		gameChoice.addItemListener(new ItemListener() {
			@SuppressWarnings({ "unchecked"})
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				top.add(Loading);
				JComboBox<String> source = (JComboBox<String>)e.getSource();
				gameFile = (String) source.getSelectedItem();
				Parameters.parameters.setString("gvgaiGame", gameFile);
				fullGameFile = GAMES_PATH + gameFile + ".txt";
				System.out.println("fullGameFile");
				gameCharData = SPECIFIC_GAME_LEVEL_CHARS.get(gameFile);
				System.out.println("gameCharData");
				
				VGDLFactory.GetInstance().init();
				VGDLRegistry.GetInstance().init();
				
				// New to reset all network population configurations because different games require different numbers of outputs
				MMNEAT.setNNInputParameters(numCPPNInputs(), numCPPNOutputs());
				// Make new TWEANNGenotype for archetype index 0
				TWEANNGenotype newStart = new TWEANNGenotype(numCPPNInputs(), numCPPNOutputs(), 0);				
				EvolutionaryHistory.initArchetype(0, null, newStart);
				scores.get(0).individual = newStart; // Is used in the call to reset() in a moment
				// Resets the population using the first agent as a prototype
				reset();
				// Replace images on buttons
				resetButtons(true);
				top.remove(Loading);
			}

		});
		
		top.add(gameChoice);

	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias" };
	}

	@Override
	public String[] outputLabels() {
		ArrayList<String> outputs = new ArrayList<String>(10);
		outputs.add("");
		
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
		String[] level = GVGAIUtil.generateLevelFromCPPN((Network)scores.get(i).individual.getPhenotype(), inputMultipliers, GAME_GRID_WIDTH, GAME_GRID_HEIGHT, gameCharData[FLOOR_INDEX][0], gameCharData[WALL_INDEX][0], 
				gameCharData[FIXED_ITEMS_INDEX], gameCharData[UNIQUE_ITEMS_INDEX], gameCharData[RANDOM_ITEMS_INDEX], NUMBER_RANDOM_ITEMS, gameCharData[BOTTOM_ITEMS_INDEX]);
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
		String[] level = GVGAIUtil.generateLevelFromCPPN(phenotype, inputMultipliers, GAME_GRID_WIDTH, GAME_GRID_HEIGHT, gameCharData[FLOOR_INDEX][0], gameCharData[WALL_INDEX][0], 
				gameCharData[FIXED_ITEMS_INDEX], gameCharData[UNIQUE_ITEMS_INDEX], gameCharData[RANDOM_ITEMS_INDEX], NUMBER_RANDOM_ITEMS, gameCharData[BOTTOM_ITEMS_INDEX]);
		int seed = 0; // TODO: Use parameter?
		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 
		Game game = new VGDLParser().parseGame(fullGameFile); // Initialize the game	

		return new GameBundle(game, level, agent, seed, 0);
	}
	
	@Override
	protected BufferedImage getButtonImage(TWEANN phenotype, int width, int height, double[] inputMultipliers) {
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
		if(itemID == PLAY_BUTTON_INDEX && selectedItems.size() > 0) {
			Network cppn = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
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
	protected void additionalButtonClickAction(int scoreIndex, Genotype<TWEANN> individual) {
		// Not used
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
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","trials:1","mu:16","maxGens:500","gvgaiGame:zelda","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.LevelBreederTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
