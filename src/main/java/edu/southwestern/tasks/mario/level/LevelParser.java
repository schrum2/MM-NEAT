package edu.southwestern.tasks.mario.level;

import java.util.HashMap;
import java.util.List;

import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.SpriteTemplate;
import ch.idsia.mario.engine.sprites.Enemy;
import java.util.ArrayList;

/**
 * This is the upgraded version of LevelParser that allows for more expressivity
 * from MarioGAN, and autofixes problems like pipe and bullet bill tiles. The
 * OldLevelParser is kept around for backwards compatibility with the old GAN,
 * and is (currently) also used by CPPNs generating levels.
 *
 * @author Vanessa Volz
 */
public class LevelParser {
	
	//////////// --- Vanessa originally had the code below in a file called Settings.java, but I moved it directly here
    public static final java.util.Map<Character, Integer> tiles = new HashMap<>();
    
    static {
        tiles.put('X', 0); //solid
        tiles.put('x', 1); //breakable
        tiles.put('-', 2); //passable
        tiles.put('q', 3); //question with coin
        tiles.put('Q', 4); //question with power up
        tiles.put('o', 5); //coin
        tiles.put('t', 6); //tube
        tiles.put('p', 7); //piranha plant tube
        tiles.put('b', 8); //bullet bill
        tiles.put('g', 9); //goomba
        tiles.put('k', 10); //green koopas + paratroopas
        tiles.put('r', 11); //red koopas + paratroopas
        tiles.put('s', 12); //spiny + winged spiny
    }
    
    private static final java.util.Map<Integer, Integer> prettyTiles = new HashMap<>();
    
    static {
        prettyTiles.put(0, 0); //solid
        prettyTiles.put(1, 1); //breakable
        prettyTiles.put(2, 0); //passable
        prettyTiles.put(3, 1); //question with coin
        prettyTiles.put(4, 1); //question with power up
        prettyTiles.put(5, 0); //coin
        prettyTiles.put(6, 1); //tube
        prettyTiles.put(7, 1); //piranha plant tube
        prettyTiles.put(8, 1); //bullet bill
        prettyTiles.put(9, 1); //goomba
        prettyTiles.put(10, 1); //green koopas + paratroopas
        prettyTiles.put(11, 1); //red koopas + paratroopas
        prettyTiles.put(12, 1); //spiny + winged spiny
    }
    
    public static final java.util.Map<Integer, Double> leniencyTiles = new HashMap<>();
    
    static {
        leniencyTiles.put(0, 0.0); //solid
        leniencyTiles.put(1, 0.0); //breakable
        leniencyTiles.put(2, 0.0); //passable
        leniencyTiles.put(3, 1.0); //question with coin
        leniencyTiles.put(4, 1.0); //question with power up
        leniencyTiles.put(5, 0.0); //coin
        leniencyTiles.put(6, -0.5); //tube
        leniencyTiles.put(7, -0.5); //piranha plant tube
        leniencyTiles.put(8, -0.5); //bullet bill
        leniencyTiles.put(9, -1.0); //goomba
        leniencyTiles.put(10, -1.0); //green koopas + paratroopas
        leniencyTiles.put(11, -1.0); //red koopas + paratroopas
        leniencyTiles.put(12, -1.0); //spiny + winged spiny
    }
    
    public static final java.util.Map<Integer, Integer> negativeSpaceTiles = new HashMap<>();
    
    static {
        negativeSpaceTiles.put(0, 1); //solid
        negativeSpaceTiles.put(1, 1); //breakable
        negativeSpaceTiles.put(2, 0); //passable
        negativeSpaceTiles.put(3, 1); //question with coin
        negativeSpaceTiles.put(4, 1); //question with power up
        negativeSpaceTiles.put(5, 0); //coin
        negativeSpaceTiles.put(6, 1); //tube
        negativeSpaceTiles.put(7, 1); //piranha plant tube
        negativeSpaceTiles.put(8, 1); //bullet bill
        negativeSpaceTiles.put(9, 0); //goomba
        negativeSpaceTiles.put(10, 0); //green koopas + paratroopas
        negativeSpaceTiles.put(11, 0); //red koopas + paratroopas
        negativeSpaceTiles.put(12, 0); //spiny + winged spiny
    }
    
    
    public static final java.util.Map<Integer, Integer> tilesMario = new HashMap<>();
    //encoding can be found in LevelScene ZMap    
    static {
        tilesMario.put(0, 9); //solid
        tilesMario.put(1, 16); //breakable
        tilesMario.put(2, 0); //passable
        tilesMario.put(3, 21); //question with coin
        tilesMario.put(4, 22); //question with power up
        tilesMario.put(5, 34); //coin
        // Special tile meant to indicate a conflict in visualization ... not for playable levels, just visualization
        tilesMario.put(-100, -81); 
    }
    
    public static final java.util.Map<String, Integer> tilesAdv = new HashMap<>();
    //numbers from the picture files "mapsheet.png"
    static{
        tilesAdv.put("bb", 14+0*16); //bullet bill shooter
        tilesAdv.put("bbt", 14+1*16); //bullet bill top
        tilesAdv.put("bbb", 14+2*16); //bullet bill bottom
        tilesAdv.put("ttl", 10+0+0*16); //tube top left
        tilesAdv.put("ttr", 10+1+0*16); //tube top right
        tilesAdv.put("tbl", 10+0+1*16); //tube bottom left
        tilesAdv.put("tbr", 10+1+1*16); //tube bottom right
    }
    //////////////////////// -- End of code from Settings.java
	
    
    public static final int BUFFER_WIDTH = 15; // This is the extra space added at the start and ends of levels
    public LevelParser(){
        
    }    
    
     
    // Jacob: I commented this out so I would not need to include the MarioReader class, but may add it back later.
    //For Testing purposes to create straight from example files
//    public static Level createLevelASCII(String filename) throws Exception
//    {
//    	int[][] level = MarioReader.readLevel(new Scanner(new FileInputStream(filename)));
//        return createLevel(level);        
//    }
    
    public static ArrayList<double[]> getLevelStats(ArrayList<List<Integer>> oneLevel, int segmentWidth){
        if (oneLevel.get(0).size()%segmentWidth!=0){
            System.out.println("getLevelStats: Level not multiple of segment width");
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
                    if(code==2 && i==height-1){
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
    

    /**
     * Create level based on 2D array of inputs
     * @param input 2D array of integers representing the level
     * @return Level instance
     */
    public static Level createLevel(int[][] input){
        int width = input[0].length;
    	int height = input.length;
    	int extraStones = BUFFER_WIDTH;
    	Level level = new Level(width+2*extraStones,height);
        //Set Level Exit
        //Extend level by that
        level.xExit = width+extraStones+1; // Push exit point over by 1 so that goal post does not overlap with other level sprites
        level.yExit = height-1;
        
        for(int i=0; i<extraStones; i++){
            level.setBlock(i, height-1, (byte) 9);
        }
        for(int i=0; i<extraStones; i++){
            level.setBlock(width+i+extraStones, height-1, (byte) 9);
        }
        
       
        //set Level map
        //revert order of iterating rows bottom -> top (so that below tiles can be checked for building tubes etc)
        for(int i=height-1; i>=0; i--){
            for(int j=width-1; j>=0; j--){
                int code = input[i][j];
                if(code>=9){
                    //set Enemy
                    int code_below=0;
                    if(i+1<height){//just in case we are in bottom row
                       code_below = input[i+1][j]; 
                    }
                    level.setSpriteTemplate(j+extraStones, i, getEnemySprite(code,code_below==2));
                }else if(code==8){//bullet bill
                    level.setBlock(j + extraStones, i, tilesAdv.get("bb").byteValue());//bullet bill shooter
                    if(i+1<height && input[i+1][j]==2){
                        level.setBlock(j + extraStones, i+1, tilesAdv.get("bbt").byteValue());//bullet bill top 
                        for(int k=i+2; k<height; k++){
                            if(input[k][j]==2){
                                level.setBlock(j+extraStones, k, tilesAdv.get("bbb").byteValue());
                            }else{
                                break;
                            }
                        }
                    }
                }else if(code==6 || code==7){//tubes + plants
                    level.setBlock(j + extraStones, i, tilesAdv.get("ttl").byteValue());
                    level.setBlock(j + extraStones +1, i, tilesAdv.get("ttr").byteValue());
                    for(int k=i+1; k<height; k++){
                        if(input[k][j]==2 || (j<width-1 && input[k][j+1]==2)){
                            level.setBlock(j+extraStones, k, tilesAdv.get("tbl").byteValue());
                            level.setBlock(j+extraStones+1, k, tilesAdv.get("tbr").byteValue());
                        }else{
                            break;
                        }
                    }
                    if(code==7){
                        level.setSpriteTemplate(j + extraStones, i, new SpriteTemplate(Enemy.ENEMY_FLOWER, false));
                    }
                }else if(code==-100) { // Special weird value that should not be in played levels. Just for visualizations. Indicated problem tile
                    level.setBlock(j+extraStones, i, tilesMario.get(code).byteValue());
                }else if(code!=2) { // 2 is "nothing" so anything else is a tile that gets placed
                    level.setBlock(j+extraStones, i, tilesMario.get(code).byteValue());
                }
            }
        }                
        return level;
    }

    /**
     * Convert list of int to array
     * @param list List of integers
     * @return Array of integers
     */
    private static int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }

    /**
     * Create level based on 2D list of integers
     * @param input 2D list of ints representing the level
     * @return Level mario level instance
     */
    public static Level createLevelJson(List<List<Integer>> input)
    {
        int[][] output = new int[input.size()][];
        int i = 0;
        for (List<Integer> nestedList : input) {
            output[i++] = toIntArray(nestedList);
        }
        return createLevel(output);
    }
  
    /**
     * Get type of enemy based on code and whether they're flying or not
     * @param code Int code of enemey
     * @param flying Boolean whether they're flying or not
     * @return SpriteTemplate representing the enemy
     */
    public static SpriteTemplate getEnemySprite(int code, boolean flying){
        int type = 0;
        switch(code){
            case 9:
                type=Enemy.ENEMY_GOOMBA;
                break;
            case 10:
                type=Enemy.ENEMY_GREEN_KOOPA;
                break;
            case 11:
                type=Enemy.ENEMY_RED_KOOPA;
                break;
            case 12:
                type=Enemy.ENEMY_SPIKY;
                break;
        }
        SpriteTemplate enemy = new SpriteTemplate(type, flying);
        return enemy;
    }
    
    
       public Level test(){
        Level level = new Level(202,14);
        level.setBlock(1, 13, (byte) 9);
        level.setBlock(2, 13, (byte) 9);
        level.setBlock(3, 13, (byte) 9);
        level.setBlock(4, 13, (byte) 9);
        level.setBlock(5, 13, (byte) 9);
        level.setBlock(6, 13, (byte) 9);
        level.setBlock(7, 13, (byte) 9);
        level.setBlock(4, 10, (byte) 9);
        //level.setSpriteTemplate(3,10, new SpriteTemplate(100, false));
        //level.setBlock(6,10,(byte)(14));
        //level.setBlock(6,11,(byte)(14+16));
        //level.setBlock(6,12,(byte)(14+2*16));
        level.setBlock(3, 10, (byte) 24);
        level.setBlock(6, 10, (byte) 25);
        level.setBlock(7, 10, (byte) 18);
        level.setBlock(5, 10, (byte) 23);
        
        return level;
    }
}
