package edu.southwestern.tasks.mario.level;

import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.SpriteTemplate;
import ch.idsia.mario.engine.sprites.Enemy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * This level parser is used by the original version of Mario GAN presented in our GECCO 2018
 * paper, and is also used by CPPN level generation. However, Vanessa later made improvements
 * to the level parser and enhanced the GAN capabilities, and these changes are in the LevelParser class.
 *
 * @author Vanessa Volz
 */
public class OldLevelParser {

	public static final int BUFFER_WIDTH = 15;
	
	// Each integer corresponds to a block
	// This is an array representation to easily map integers to a character
	// OldLevelParser doesn't provide an easy way to map ints to chars
	public static final char[] BLOCK_INDEX = {
			'X', // 0: Solid block
			'S', // 1: Breakable block
			'-', // 2: Air block
			'?', // 3: Question block
			'Q', // 4: Empty Question block(?)
			'E', // 5: Goomba block
			'<', // 6: Top left pipe
			'>', // 7: Top right pipe
			'[', // 8: Left pipe
			']', // 9: Right pipe
			'o', // 10: Coin,
			'B', // 11: Top of bullet bill cannon thing
			'b', // 12: Bottom of bullet bill
	};

	
    private static final java.util.Map<Integer, Integer> prettyTiles = new HashMap<>();
    
    static {
        prettyTiles.put(0, 0); //solid
        prettyTiles.put(1, 1); //breakable
        prettyTiles.put(2, 0); //passable
        prettyTiles.put(3, 1); //question with coin
        prettyTiles.put(4, 1); //question with power up
        prettyTiles.put(5, 1); //goomba
        prettyTiles.put(6, 1); //tube
        prettyTiles.put(7, 1); //tube
        prettyTiles.put(8, 1); //pipe
        prettyTiles.put(9, 1); //pipe
        prettyTiles.put(10, 0); //coin
        prettyTiles.put(11, 1); //bullet bill
        prettyTiles.put(12, 1); //bullet bill
    }
    
    public static final java.util.Map<Integer, Double> leniencyTiles = new HashMap<>();
    
    static {
        leniencyTiles.put(0, 0.0); //solid
        leniencyTiles.put(1, 0.0); //breakable
        leniencyTiles.put(2, 0.0); //passable
        leniencyTiles.put(3, 1.0); //question with coin
        leniencyTiles.put(4, 1.0); //question with power up
        leniencyTiles.put(5, -1.0); //goomba
        leniencyTiles.put(6, -0.5); //tube
        leniencyTiles.put(7, -0.5); //tube
        leniencyTiles.put(8, -0.5); //pipe
        leniencyTiles.put(9, -0.5); //pipe
        leniencyTiles.put(10, 0.0); //coin
        leniencyTiles.put(11, -0.5); //bullet bill
        leniencyTiles.put(12, -0.5); //bullet bill
    }
    
    public static final java.util.Map<Integer, Integer> negativeSpaceTiles = new HashMap<>();
    
    static {
        negativeSpaceTiles.put(0, 1); //solid
        negativeSpaceTiles.put(1, 1); //breakable
        negativeSpaceTiles.put(2, 0); //passable
        negativeSpaceTiles.put(3, 1); //question with coin
        negativeSpaceTiles.put(4, 1); //question with power up
        negativeSpaceTiles.put(5, 0); //goomba
        negativeSpaceTiles.put(6, 1); //tube
        negativeSpaceTiles.put(7, 1); //tube
        negativeSpaceTiles.put(8, 1); //pipe
        negativeSpaceTiles.put(9, 1); //pipe
        negativeSpaceTiles.put(10, 0); //coin
        negativeSpaceTiles.put(11, 1); //bullet bill
        negativeSpaceTiles.put(12, 1); //bullet bill
    }
    	
	
	
	public static int indexOfBlock(char c) {
		// There is only one enemy in the encoding, but it is mapped to an enemy
		// of a random type. So all enemies need to map back to the one enemy tile index.
		if(c == 'R' || c == 'r' || c == 'W' || c == 'g' || c == 'G' || c == '^' || c == '&') return 5;
		
		for(int i = 0; i < BLOCK_INDEX.length; i++) {
			if(BLOCK_INDEX[i] == c) return i;
		}
		throw new IllegalArgumentException(c + ": not a valid tile.");
	}
	
	/*
	 "tiles" : {
    0    "X" : ["solid","ground"],
    1    "S" : ["solid","breakable"],
    2    "-" : ["passable","empty"],
    3    "?" : ["solid","question block", "full question block"],
    4    "Q" : ["solid","question block", "empty question block"],
    5    "E" : ["enemy","damaging","hazard","moving"], // Results in a generic ground goomba
    6    "<" : ["solid","top-left pipe","pipe"],
    7    ">" : ["solid","top-right pipe","pipe"],
    8    "[" : ["solid","left pipe","pipe"],
    9    "]" : ["solid","right pipe","pipe"],
    10   "o" : ["coin","collectable","passable"]
    11   "B" : Top of a Bullet Bill cannon, solid
    12   "b" : Body/support of a Bullet Bill cannon, solid
	 */

	/**
	 * Types added by schrum2:
	 * 
	 * W = winged goomba
	 * g = green shelled koopa
	 * r = red shelled koopa
	 * G = winged green koopa
	 * R = winged red koopa
	 * ^ = spiky shelled enemy
	 * & = winged spiky shelled enemy
	 */

	/**
	 * char based version of method below
	 * @param code character of block
	 * @return True if the code 
	 */
	public static boolean isEnemy(char code) {
		return isEnemy(code+"");
	}

	/**
	 * Based on the list of enemy types above
	 * @param code String of the tile
	 * @return True if the code corresponds to an enemy
	 */
	public static boolean isEnemy(String code) {
		return code.equals("E") || code.equals("W") || code.equals("G") || code.equals("g") || code.equals("r") || code.equals("R") || code.equals("^") || code.equals("&");
	}

	/**
	 * Generate sprites based on the enemy codes above
	 * @param code String representing a tile of a level
	 * @return SpriteTemplete based on the code
	 */
	private SpriteTemplate spriteForCode(String code) {
		switch(code) {
		case "E":
			return new SpriteTemplate(Enemy.ENEMY_GOOMBA, false);
		case "W":
			return new SpriteTemplate(Enemy.ENEMY_GOOMBA, true);
		case "g":
			return new SpriteTemplate(Enemy.ENEMY_GREEN_KOOPA, false);
		case "G":
			return new SpriteTemplate(Enemy.ENEMY_GREEN_KOOPA, true);
		case "r":
			return new SpriteTemplate(Enemy.ENEMY_RED_KOOPA, false);
		case "R":
			return new SpriteTemplate(Enemy.ENEMY_RED_KOOPA, true);
		case "^":
			return new SpriteTemplate(Enemy.ENEMY_SPIKY, false);
		case "&":
			return new SpriteTemplate(Enemy.ENEMY_SPIKY, true);
		default:
			throw new IllegalArgumentException("Invalid enemy sprite code: " + code);
		}
	}




	/**
	 * Create level from text file with 2D arrangement of
	 * level content.
	 * 
	 * @param filename string of the filename of the level
	 * @return Level based on an ASCII representation
	 */
	public Level createLevelASCII(String filename) {
		//Read in level representation
		ArrayList<String> lines = new ArrayList<String>();
		try {
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Create level line by line from file
		return createLevelASCII(lines);
	}        

	/**
	 * Create level from list of Strings corresponding to the 2D
	 * layout of the level.
	 * 
	 * @param lines List of strings with each string being a row of the level
	 * @return Level based on the rows of the ASCII representation
	 */
	public Level createLevelASCII(ArrayList<String> lines) {
		int width = lines.get(0).length(); // Get width of level
		int height = lines.size(); // Get height of level
		int actualWidth = width+2*BUFFER_WIDTH; // Actual width corresponds to the width of the level instance
		Level level = new Level(actualWidth,height);

		//Set Level Exit
		//Extend level by that
		level.xExit = width+BUFFER_WIDTH+1;
		level.yExit = height-1;

		// Set the left side of the level to solid blocks
		for(int i=0; i<BUFFER_WIDTH; i++){
			level.setBlock(i, height-1, (byte) 9);
		}
		// Set the right side of the level to solid blocks
		for(int i=0; i<BUFFER_WIDTH; i++){
			level.setBlock(width+i+BUFFER_WIDTH, height-1, (byte) 9);
		}

		//set Level map
		for(int i=0; i<height; i++){
			for(int j=0; j<lines.get(i).length(); j++){
				String code = String.valueOf(lines.get(i).charAt(j)); // Get code by row and then position in string
				if(isEnemy(code)){
					//set Enemy
					//new SpriteTemplate(type, boolean winged)
					level.setSpriteTemplate(j+BUFFER_WIDTH, i, spriteForCode(code));
					//System.out.println("j: "+j+" i:"+i);
					//set passable tile: everything not set is passable
				}else{
					int encoded = codeParserASCII(code);
					if(encoded !=0){
						level.setBlock(j+BUFFER_WIDTH, i, (byte) encoded);
						//System.out.println("j: "+j+" i:"+i+" encoded: "+encoded);
					}
				}
			}
		}

		return level;
	}

	/**
	 * Generate a level based on the JSON representation, 2D ints
	 * @param input 2D array of integers
	 * @return Level mario level based on input
	 */
	public static Level createLevelJson(List<List<Integer>> input)
	{
		int width = input.get(0).size();
		int height = input.size();
		Level level = new Level(width+2*BUFFER_WIDTH,height);

		//Set Level Exit
		//Extend level by that
		level.xExit = width+BUFFER_WIDTH;
		level.yExit = height-1;

		// Set left side of level
		for(int i=0; i<BUFFER_WIDTH; i++){
			level.setBlock(i, height-1, (byte) 9);
		}
		
		// Set right side of level
		for(int i=0; i<BUFFER_WIDTH; i++){
			level.setBlock(width+i+BUFFER_WIDTH, height-1, (byte) 9);
		}

		//set Level map
		for(int i=0; i<height; i++){
			for(int j=0; j<width; j++){
				int code = input.get(i).get(j);
				if(5==code){
					//set Enemy
					//new SpriteTemplate(type, boolean winged)
					level.setSpriteTemplate(j+BUFFER_WIDTH, i, new SpriteTemplate(Enemy.ENEMY_GOOMBA, false));
					//System.out.println("j: "+j+" i:"+i);
					//set passable tile: everything not set is passable
				}else{
					int encoded = codeParser( code ); 
					if(encoded !=0){
						level.setBlock(j+BUFFER_WIDTH, i, (byte) encoded);
						//System.out.println("j: "+j+" i:"+i+" encoded: "+encoded);
					}
				}
			}
		}

		return level;
	}


	/**
	 * If given ints as the level input, use this parser to	code it to something Level can understand
	 * @param code int from input
	 * @return int representing a block in a level
	 */
	public static int codeParser(int code){
		int output = 0;
		switch(code){
		case 0: output = 9; break; //rocks
		case 1: output = 16; break; //"S" : ["solid","breakable"]
		case 3: output = 21; break; //"?" : ["solid","question block", "full question block"]
		case 6: output = 10; break; //"<" : ["solid","top-left pipe","pipe"]
		case 7: output = 11; break; //">" : ["solid","top-right pipe","pipe"]
		case 8: output = 26; break; //"[" : ["solid","left pipe","pipe"]
		case 9: output = 27; break; //"]" : ["solid","right pipe","pipe"]
		case 10: output = 34; break; //"o" : ["coin","collectable","passable"]
		// Bullet Bill cannons not described in VDLC json, but were present in the data
		case 11: output = 14; break; //"B" : Top of a Bullet Bill cannon, solid
		// There may be a problem here: VGLC uses "b" to represent what is either sprite 30 or 46 in Infinite Mario
		case 12: output = 46; break; //"b" : Body/support of a Bullet Bill cannon, solid
		default: output=0; break; //"-" : ["passable","empty"],  "Q" : ["solid","question block", "empty question block"],  "E" : ["enemy","damaging","hazard","moving"],
		}
		return output;
	}

	/**
	 * If given a strings as level input, use this parser to code it something Level can understand
	 * @param code String representation of a tile
	 * @return int representing a block in the level
	 */
	public static int codeParserASCII(String code){
		int output = 0;
		switch(code){
		case "X": output = 9; break; //rocks
		case "S": output = 16; break; //"S" : ["solid","breakable"]
		case "?": output = 21; break; //"?" : ["solid","question block", "full question block"]
		case "<": output = 10; break; //"<" : ["solid","top-left pipe","pipe"]
		case ">": output = 11; break; //">" : ["solid","top-right pipe","pipe"]
		case "[": output = 26; break; //"[" : ["solid","left pipe","pipe"]
		case "]": output = 27; break; //"]" : ["solid","right pipe","pipe"]
		case "o": output = 34; break; //"o" : ["coin","collectable","passable"]
		// Bullet Bill cannons not described in VDLC json, but were present in the data
		case "B": output = 14; break; //"B" : Top of a Bullet Bill cannon, solid
		// There may be a problem here: VGLC uses "b" to represent what is either sprite 30 or 46 in Infinite Mario
		case "b": output = 46; break; //"b" : Body/support of a Bullet Bill cannon, solid
		default: output=0; break; //"-" : ["passable","empty"],  "Q" : ["solid","question block", "empty question block"],  "E" : ["enemy","damaging","hazard","moving"],
		}
		return output;
	}

	public static final boolean DEBUG = false;
	
    public static int[][] readLevel(Scanner scanner) throws Exception { // read level into 2D int array (from: https://github.com/TheHedgeify/DagstuhlGAN/blob/master/marioaiDagstuhl/src/reader/MarioReader.java)
        String line;
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            width = line.length();
            lines.add(line);
            // System.out.println(line);
        }

        HashMap<Character,Integer> map = new HashMap<>();
        for(int i = 0; i < BLOCK_INDEX.length; i++) {
        	map.put(BLOCK_INDEX[i], i);
        }
        
        int[][] a = new int[lines.size()][width];
        if(DEBUG) System.out.println("Arrays length: " + a.length);
        for (int y = 0; y < lines.size(); y++) {
        	if(DEBUG) System.out.println("Processing line: " + lines.get(y));
            for (int x = 0; x < width; x++) {
            	try { // Added error checking to deal with unrecognized tile types
            		//a[y][x] = codeParserASCII(""+lines.get(y).charAt(x));
            		a[y][x] = indexOfBlock(lines.get(y).charAt(x));
                } catch(Exception e) {
            		System.out.println("Problem on ");
            		System.out.println("\ty = " + y);
            		System.out.println("\tx = " + x);
            		System.out.println("\tlines.get(y).charAt(x) = " + lines.get(y).charAt(x));
            		System.exit(1);
            	}
            }
        }

        return a;
    }

    public static ArrayList<double[]> getLevelStats(List<List<Integer>> oneLevel, int segmentWidth){
        if (oneLevel.get(0).size()%segmentWidth!=0){
        	System.out.println("getLevelStats: Level not multiple of segment width!");
            return null;
        }      
        ArrayList<double[]> statList = new ArrayList<>();
        int height = oneLevel.size();
        
        // Loop through each segment
        int numSegments = oneLevel.get(0).size()/segmentWidth;
        for(int l=0; l<numSegments; l++){
            double[] vals = {0,0,0};
            int gapCount = 0;
            for(int i=0; i<height-1;i++){ // Loop from top to bottom
            	// Loop from left to right through the tiles in this particular segment
                for(int j=l*segmentWidth;j<(l+1)*segmentWidth;j++){
                    int code = oneLevel.get(i).get(j); // Get number code for tile
                    vals[0] +=prettyTiles.get(code);
                    vals[1] +=leniencyTiles.get(code);
                    vals[2] +=negativeSpaceTiles.get(code);
                    if(code==2 && i==height-1){ // Magic numbers?
                        gapCount++;
                    }
                }
            }
            vals[0]/= segmentWidth*height;
            vals[2]/= segmentWidth*height;

            vals[1]+=gapCount*-0.5;
            vals[1]/=segmentWidth*height;
            statList.add(vals);
        }
                
        return statList;
    }



}
