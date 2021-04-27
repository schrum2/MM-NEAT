package edu.southwestern.tasks.megaman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import gvgai.tools.IO;
@SuppressWarnings("unused")
/**
 * This class is a utility class to deal with most things relating to VGLC data converting to json and mmlv formats
 * @author Benjamin Capps
 *
 */
public class MegaManVGLCUtil {
	public static final String MEGAMAN_ENEMY_LEVEL_PATH = "data/VGLC/MegaMan/EnhancedWithBossesAndEnemies/";
	public static final String MEGAMAN_LEVEL_PATH = "data/VGLC/MegaMan/Enhanced/";
	public static final String MEGAMAN_MMLV_PATH = "data/MegaManLevels_mmlv/";
	/**
	 * THE BELOW FINAL CONSTANTS (UNIQUE_) ARE FOR AN OLD NUMBERING SCHEME!!!
	 */
	public static final int UNIQUE_AIR = 0;
	public static final int UNIQUE_SOLID = 1;
	public static final int UNIQUE_LADDER = 2;
	public static final int UNIQUE_HAZARD = 3;
	public static final int UNIQUE_BREAKABLE = 4;
	public static final int UNIQUE_MOVING_PLATFORM = 5;
	public static final int UNIQUE_CANNON = 6;
	public static final int UNIQUE_ORB = 7;
	public static final int UNIQUE_PLAYER = 8;
	public static final int UNIQUE_NULL = 9;
	public static final int UNIQUE_WATER = 10;
	public static final int UNIQUE_MET_ENEMY = 11;
	public static final int UNIQUE_FLY_BOY_ENEMY = 12;
	public static final int UNIQUE_OCTOPUS_BATTERY_LEFTRIGHT_ENEMY = 13;
	public static final int UNIQUE_OCTUPUS_BATTERY_UPDOWN_ENEMY = 14;
	public static final int UNIQUE_BEAK_ENEMY = 15;
	public static final int UNIQUE_PICKET_MAN_ENEMY = 16;
	public static final int UNIQUE_SCREW_BOMBER_ENEMY = 17;
	public static final int UNIQUE_BIG_EYE_ENEMY = 18;
	public static final int UNIQUE_SPINE_ENEMY = 19;
	public static final int UNIQUE_CRAZY_RAZY_ENEMY = 20;
	public static final int UNIQUE_WATCHER_ENEMY = 21;
	public static final int UNIQUE_KILLER_BULLET_ENEMY = 22;
	public static final int UNIQUE_KILLER_BULLET_SPAWNER_ENEMY = 23;
	public static final int UNIQUE_TACKLE_FIRE_ENEMY = 24;
	public static final int UNIQUE_FLYING_SHELL_ENEMY = 25;
	public static final int UNIQUE_FLYING_SHELL_SPAWNER = 26;
	public static final int UNIQUE_FOOTHOLDER_ENEMY = 27;
	public static final int UNIQUE_JUMPER_ENEMY = 28;
	public static final int UNIQUE_GUNNER_ENEMY = 29;
	public static final int UNIQUE_ENEMY_THRESH_HOLD = 10;
	
	
	
	public static final int ONE_ENEMY_AIR = 0;
	public static final int ONE_ENEMY_SOLID = 1;
	public static final int ONE_ENEMY_LADDER = 2;
	public static final int ONE_ENEMY_HAZARD = 3;
	public static final int ONE_ENEMY_BREAKABLE = 4;
	public static final int ONE_ENEMY_MOVING_PLATFORM = 5;
	public static final int ONE_ENEMY_CANNON = 6;
	public static final int ONE_ENEMY_ORB = 7;
	public static final int ONE_ENEMY_PLAYER = 8;
	public static final int ONE_ENEMY_NULL = 9;
	public static final int ONE_ENEMY_WATER = 10;
	public static final int ONE_ENEMY_GROUND_ENEMY = 11;
	public static final int ONE_ENEMY_WALL_ENEMY = 12;
	public static final int ONE_ENEMY_FLYING_ENEMY = 13;
	public static HashSet<Point> visited = new HashSet<>();
	public static int lowerY;
	public static int lowerX;
	public static int upperY;
	public static Direction start;
	public static HashMap<Point, String> levelEnemies = new HashMap<Point, String>();
	public static HashSet<Point> activatedScreens = new HashSet<Point>();
	public static List<List<List<Integer>>> json = new ArrayList<>();
	public static List<List<List<Integer>>> jsonUp = new ArrayList<>();
	public static List<List<List<Integer>>> jsonDown = new ArrayList<>();
	public static List<List<List<Integer>>> jsonLL = new ArrayList<>();
	public static List<List<List<Integer>>> jsonUL = new ArrayList<>();
	public static List<List<List<Integer>>> jsonLR = new ArrayList<>();
	public static List<List<List<Integer>>> jsonUR = new ArrayList<>();

	public static List<List<List<Integer>>> conditionalJson = new ArrayList<>();
	public static List<List<Integer>> conditionalJsonID = new ArrayList<>();
	public static HashSet<Point> placed = new HashSet<>();

	
	
	
	
	public static final List<Integer> UPID = new ArrayList<>(Arrays.asList(1,0,0,0,0,0,0));
	public static final List<Integer> DOWNID = new ArrayList<>(Arrays.asList(0,1,0,0,0,0,0));
	public static final List<Integer> HORIZONTALID = new ArrayList<>(Arrays.asList(0,0,1,0,0,0,0));
	public static final List<Integer> BOTTOMRIGHTID = new ArrayList<>(Arrays.asList(0,0,0,1,0,0,0));
	public static final List<Integer> BOTTOMLEFTID = new ArrayList<>(Arrays.asList(0,0,0,0,1,0,0));
	public static final List<Integer> UPPERRIGHTID = new ArrayList<>(Arrays.asList(0,0,0,0,0,1,0));
	public static final List<Integer> UPPERLEFTID = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,1));
	//public static boolean executed = false;
	//public static int levelNumber;
	public static List<List<List<Integer>>> getLL(){
		return jsonLL;
	}
	public static List<List<List<Integer>>> getLR(){
		return jsonLR;
	}
	public static List<List<List<Integer>>> getUL(){
		return jsonUL;
	}
	public static List<List<List<Integer>>> getUR(){
		return jsonUR;
	}
	public static void main(String[] args) {
		int firstLevel = 1;
		int lastLevel = 10;
		int prevSize = 0;
		int max = 0;
		int min = 0;
		String basePath = "megamanlevels/textLevels/AStarDistAndConnectivity";;
		for(int i = 0; i < 30; i++) { //test 1-3
			String path = basePath + "SevenGAN" + i + ".txt";
			//System.out.println(path);
			File file = new File(path);
			List<List<Integer>> sevenGANlevel = convertLevelFromIntText(file);
			//printLevel(sevenGANlevel);
			upAndDownTrainingData(sevenGANlevel);
		}
		System.out.println(getLL().size()+" "+jsonLL.size());
		System.out.println(jsonUL.size());
		System.out.println(jsonUR.size());
		System.out.println(jsonLR.size());

		System.out.println("DONE");
		MiscUtil.waitForReadStringAndEnterKeyPress();
		for(int i = firstLevel;i<=lastLevel;i++) {
			if(i==7) {
				prevSize = json.size();
				List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_"+i+".txt");
				//convertMegaManLevelToMMLV(level, i);
				upAndDownTrainingData(level);
				int newSize = json.size()-prevSize;
				if(newSize>max) max = newSize;
				if(newSize<min||min==0)min = newSize;
				//System.out.println("Level "+i+" number of segements: "+newSize);
			
			}
		}
		System.out.println("Max number of segments: "+max);
		System.out.println("Min number of segments: "+min);

//		System.out.println(conditionalJson.size());
//		
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(conditionalJson.toString());
//		
//		
//		
//		
//		
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(conditionalJsonID.size());
//		
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(conditionalJsonID.toString());
		
		
		
		
		
		
//		for(int i=11;i<=29;i++) {
//			placed.clear();
//			activatedScreens.clear();
//			visited.clear();
//			levelEnemies.clear();
//			
//			MegaManConvertMMLVToJSON.maxX=0;
//			MegaManConvertMMLVToJSON.maxY=0;
//			MegaManConvertMMLVToJSON.visited.clear();
//			MegaManConvertMMLVToJSON.enemyNumber=-1;
//			MegaManConvertMMLVToJSON.enemyString = null;
//			MegaManConvertMMLVToJSON.bossString = null;
//			//if(i!=7) {
//				List<List<Integer>> level = MegaManConvertMMLVToJSON.convertMMLVtoInt(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManLevel"+i+".mmlv");
//				printLevel(level);
//				upAndDownTrainingData(level);
//				//System.out.println(i);
//				//MiscUtil.waitForReadStringAndEnterKeyPress();
////				System.out.println("level" +i);
////				MiscUtil.waitForReadStringAndEnterKeyPress();
//
//				//if(i!=3) convertMegaManLevelToJSONHorizontalScroll(level);
//				
//						
//						
//						
//			//}
//		}
//		//System.out.println(conditionalJson.size()+" "+conditionalJsonID.size());
//		//MiscUtil.waitForReadStringAndEnterKeyPress();
//
//		System.out.println(json);
//
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(jsonUp);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(jsonDown);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(jsonLL);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(jsonUL);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(jsonLR);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(jsonUR);
		//MiscUtil.waitForReadStringAndEnterKeyPress();
		//System.out.println(jsonUp);
		
		
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		System.out.println(jsonUp);

//		List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_"+1+".txt");
//		printLevel(level);
//		upAndDownTrainingData(level);
//		convertMegaManLevelToMMLV(level, 2);
//		convertMegaManLevelToJSONVerticalScroll(level);
//		convertMegaManLevelToMMLV(level,5);
//		System.out.println(json.toString());
		
	}
	public static List<List<Integer>> convertLevelFromIntText(File intText){
		List<List<Integer>> level;
		try {
			Scanner s = new Scanner(intText);
			ArrayList<String> lines = new ArrayList<String>();

			while(s.hasNextLine()) {
				lines.add(s.nextLine());
			}
			int count = lines.size();
			level = new ArrayList<List<Integer>>(count);
			s.close();
			
			for(int i = 0; i<lines.size();i++) {
				Scanner s1 = new Scanner(lines.get(i));
				List<Integer> line = new ArrayList<Integer>();
				while(s1.hasNextInt()) {
					int intToAdd = s1.nextInt();
					line.add(intToAdd);
				}
				level.add(line);
				s1.close();
			}
			return level;
		}catch(Exception e) {
			System.out.println("Exception!!");
			return null;
		}		
		
	}
	public enum Direction {UP, RIGHT, DOWN, LEFT};
	/**
	 * Scans through the level gathering information on up, down, right, left
	 * 
	 * @param level <List<List<Integer>> level representation
	 */
	public static void upAndDownTrainingData(List<List<Integer>> level) {
		List<Point> corners = new ArrayList<Point>();
		corners = findSpawnScreen(level);
		
		List<List<Integer>> screen = new ArrayList<List<Integer>>();
		for(int i = 0;i<14;i++) {
			List<Integer> k = new ArrayList<Integer>();
			for(int j = 0;j<16;j++) {
				k.add(9);
			}
			screen.add(k);
		}
		for(int y = (int) corners.get(0).getY();y< corners.get(2).getY();y++) {
			for(int x = (int) corners.get(0).getX();x< (int) corners.get(1).getX();x++) {
//				System.out.print(y+": ");
//				int a = (y-(int) corners.get(0).getY());
//				System.out.println(a);
//				System.out.print(x+": ");
//				a = x-(int) corners.get(0).getX();
//				System.out.println(a);
				//printLevel(screen);
				screen.get(y-(int) corners.get(0).getY()).set(x-(int) corners.get(0).getX(), level.get(y).get(x));
			}
			
		}
		//printLevel(screen);
		int x1 = (int) corners.get(0).getX();
		int x2 = (int) corners.get(1).getX();
		int y1 = (int) corners.get(0).getY();
		int y2 = (int) corners.get(2).getY();
		int rightScreenSide = x2;
		x2=15;

		Direction d = start;
		start = null;
	//	int iteratio= 0;
		boolean done = false;
		int upDownTimer = -1;
		int rightLeftTimer = -1;
		if(d == Direction.DOWN)upDownTimer=-1; 
		boolean firstRound = true;
		while(!done) {
			
//			System.out.println(d);
			if(d==null) { //d==null
				done = true;
//				System.out.println("DONE");
			}else if(d.equals(Direction.RIGHT)) {
				//System.out.println(rightLeftTimer);
				rightLeftTimer--;
				
				if(canGoRight(level,rightScreenSide,y1)||rightLeftTimer>=0) {
					
					if(rightLeftTimer<0)rightLeftTimer = 15;
					
					screen = copyScreen(level, 16, 14, rightScreenSide-x2+1, y1, false);
					//if(rightScreenSide+1<level.get(0).size()&&level.get(y1).get(rightScreenSide+1)!=9)
						rightScreenSide++;
					//System.out.println("Horizontal");
					//printLevel(screen);
					//MiscUtil.waitForReadStringAndEnterKeyPress();
						
						json.add(screen);
//						conditionalJsonID.add(HORIZONTALID);
//						conditionalJson.add(screen);
						
//						System.out.println(conditionalJson);
//						MiscUtil.waitForReadStringAndEnterKeyPress();

				}else { //find new direction
//					x1--;
					Direction previous = Direction.RIGHT;
					d = findNewDirection(level, rightScreenSide, y1, previous); //using upper right corner
					if(previous!=d) {
						rightLeftTimer = 16;
						if(!firstRound) {
						if(d == Direction.DOWN) {
							jsonUR.add(screen);
//							System.out.println("UPPER RIGHT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}else if(d == Direction.UP) {
							jsonLR.add(screen);
//							System.out.println("LOWER RIGHT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}
						}
					}
				}
				
			}
			else if (d.equals(Direction.UP)){

//				MiscUtil.waitForReadStringAndEnterKeyPress();
//				System.out.println(level.get(y1-1).get(x2+x1-1));
//				MiscUtil.waitForReadStringAndEnterKeyPress();
				if(y1-1>=0&&level.get(y1-1).get(rightScreenSide)!=9&&canGoUp(level,rightScreenSide,y1)) {
					y1--;
					if(rightScreenSide-x2>=0)
					screen = copyScreen(level, 16, 14, rightScreenSide-x2, y1, false);
					else screen = copyScreen(level, 16, 14, rightScreenSide-x2+1, y1, false);
					json.add(screen);

//					jsonUp.add(screen);
//					conditionalJsonID.add(UPID);
//					conditionalJson.add(screen);
					//System.out.println("UP");
					//printLevel(screen);
					//MiscUtil.waitForReadStringAndEnterKeyPress();

				}else { //find new direction
					Direction previous = Direction.UP;

					d = findNewDirection(level, rightScreenSide, y1, previous); //using upper right corner
					if(previous!=d) {
						upDownTimer = 14;
						if(!firstRound) {
						if(d == Direction.LEFT) {
							jsonUR.add(screen);
//							System.out.println("UPPER RIGHT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}else if(d == Direction.RIGHT) {
							jsonUL.add(screen);
//							System.out.println("UPPER LEFT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}
						}
					}

				}
			}
			else if(d.equals(Direction.DOWN)){
				//System.out.println(upDownTimer);
				upDownTimer--;
				if(y1+14<level.size()&&level.get(y1+14).get(rightScreenSide)!=9&&canGoDown(level,rightScreenSide,y1)||upDownTimer>=0) {
					y1++;
					if(upDownTimer<0)upDownTimer = 13;
					if(rightScreenSide-x2>=0)
						screen = copyScreen(level, 16, 14, rightScreenSide-x2, y1, false);
					else screen = copyScreen(level, 16, 14, rightScreenSide-x2+1, y1, false);
					json.add(screen);

//					jsonDown.add(screen);
//					conditionalJsonID.add(DOWNID);
//					conditionalJson.add(screen);
					//System.out.println("DOWN");
					//printLevel(screen);
					//MiscUtil.waitForReadStringAndEnterKeyPress();
				}else { //find new direction
					Direction previous = Direction.DOWN;
					d = findNewDirection(level, rightScreenSide, y1, previous); //using upper right corner
					if(previous!=d) {
						upDownTimer = 14;
						if(!firstRound) {
						if(d == Direction.LEFT) {
							jsonLR.add(screen);
//							System.out.println("LOWER RIGHT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}else if(d == Direction.RIGHT) {
							jsonLL.add(screen);
//							System.out.println("LOWER LEFT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}
						}
					}

				}
			}else if(d.equals(Direction.LEFT)) {
				if(canGoLeft(level,rightScreenSide,y1)) {
						
					screen = copyScreen(level, 16, 14, rightScreenSide-x2-1, y1, false);
					//if(rightScreenSide+1<level.get(0).size()&&level.get(y1).get(rightScreenSide+1)!=9)
						rightScreenSide--;
//					System.out.println("Horizontal");
//					printLevel(screen);
//					MiscUtil.waitForReadStringAndEnterKeyPress();
						

						json.add(screen);
//						conditionalJsonID.add(HORIZONTALID);
//						conditionalJson.add(screen);
						
						//System.out.println("LEFT");
						//printLevel(screen);
						//MiscUtil.waitForReadStringAndEnterKeyPress();
//						System.out.println(conditionalJson);
//						MiscUtil.waitForReadStringAndEnterKeyPress();

				}else { //find new direction
//					x1--;
					Direction previous = Direction.LEFT;
					d = findNewDirection(level, rightScreenSide, y1, previous); //using upper right corner
					if(previous!=d) {
						rightLeftTimer = 16;
						if(!firstRound) {
							
						
						if(d == Direction.UP) {
							jsonLL.add(screen);
//							System.out.println("LOWER LEFT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}else if(d == Direction.DOWN) {
							jsonUL.add(screen);
//							System.out.println("UPPER LEFT");
//							printLevel(screen);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
						}
						}
					}
				}
			}
			//System.out.println(iteratio);
		
			firstRound=false;
			/*
			 * upDownTimer = -1;
		int rightLeftTimer
			 */
			//findCorners(level, rightScreenSide, y1, x2, upDownTimer, rightLeftTimer);
			

//			iteratio++;
			
		}
	}
	/**
	 *  if can go up or left then lower left corner, etc.
	 * @param level
	 * @param rightScreenSide
	 * @param y1
	 */
	private static void findCorners(List<List<Integer>> level, int rightScreenSide, int y1, int x2, int upDownTimer, int rightLeftTimer) {
		boolean left = canGoLeft(level,rightScreenSide,y1);
		boolean right = canGoRight(level,rightScreenSide,y1)||rightLeftTimer>=0;
		boolean down = canGoDown(level,rightScreenSide,y1)||upDownTimer>=0;
		boolean up = canGoUp(level,rightScreenSide,y1);
//		System.out.println(new Point(rightScreenSide-x2,y1));
		Point point = new Point(rightScreenSide-x2,y1);
		List<List<Integer>> screen;
		if(rightScreenSide-x2>=0) {
			screen = copyScreen(level, 16, 14, rightScreenSide-x2, y1, false);
		}
		else {
			screen = copyScreen(level, 16, 14, rightScreenSide-x2+1, y1, false);
			placed.add(new Point(rightScreenSide-x2+1, y1));

		}
		if(up&&left&&!right&&!down&&!placed.contains(point)) { //lower right
			if(jsonLR.size()<7) {
			placed.add(point);
			jsonLR.add(screen);
			conditionalJsonID.add(BOTTOMRIGHTID);
			//System.out.println("LOWER RIGHT");
			//printLevel(screen);
			//MiscUtil.waitForReadStringAndEnterKeyPress();
			conditionalJson.add(screen);
			}
			
		}else if(up&&right&&!left&&!down&&!placed.contains(point)) { //lower left
			
			if(jsonLL.size()<7) {

			placed.add(point);
			jsonLL.add(screen);
			conditionalJsonID.add(BOTTOMLEFTID);
			//System.out.println("LOWER LEFT");
			//printLevel(screen);
			//MiscUtil.waitForReadStringAndEnterKeyPress();
			conditionalJson.add(screen);
			
			}
		}else if(down&&right&&!up&&!left&&!placed.contains(point)) { //upper left
			if(jsonUL.size()<7) {

			placed.add(point);
			jsonUL.add(screen);
			conditionalJsonID.add(UPPERLEFTID);
//			System.out.println("UPPER LEFT");
//			printLevel(screen);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
			conditionalJson.add(screen);
			}
		}else if(down&&left&&!right&&!up&&!placed.contains(point)) { //upper right
			if(jsonUR.size()<7) {

			placed.add(point);
			jsonUR.add(screen);
			conditionalJsonID.add(UPPERRIGHTID);
//			System.out.println("UPPER RIGHT");
//			printLevel(screen);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
			conditionalJson.add(screen);
			}
		}

		
	}
/**
 * determines if a screen can go left or not
 * @param level the level	
 * @param rightScreenSide the right side of the screen
 * @param y1 the y-coordinate (upper)
 * @return true if the screen can move to the left, false otherwise
 */
	public static boolean canGoLeft(List<List<Integer>> level, int rightScreenSide, int y1) {
		//check all of the tiles from the leftmost side of this segment and the rightmost side of the left segment and see if it's compatible.
		//System.out.println(rightScreenSide-16+" "+level.get(0).size());
		if(rightScreenSide-16>=0&&level.get(y1).get(rightScreenSide-16)!=9) {
			if(rightScreenSide-20>0&&level.get(y1).get(rightScreenSide-20)==9||rightScreenSide-18<0) return true; //if there's left space and null space behind that, go ahead and move
			boolean canMove = false;
			for(int i = y1;i<y1+14;i++) {
				int j = rightScreenSide-16;
				//level.get(i).get(j) is passable
				int tile1 = level.get(i).get(j);
				int tile2 = level.get(i).get(j+1);
				if(passable(tile1)&&passable(tile2)) {
					canMove = true;
				}
				
			}
			return canMove;
		}
		
		return false;
	}
	
	/*
	 * 
	 * ONE_ENEMY_AIR = 0;
	public static final int ONE_ENEMY_SOLID = 1;
	public static final int ONE_ENEMY_LADDER = 2;
	public static final int ONE_ENEMY_HAZARD = 3;
	public static final int ONE_ENEMY_BREAKABLE = 4;
	public static final int ONE_ENEMY_MOVING_PLATFORM = 5;
	public static final int ONE_ENEMY_CANNON = 6;
	public static final int ONE_ENEMY_ORB = 7;
	public static final int ONE_ENEMY_PLAYER = 8;
	public static final int ONE_ENEMY_NULL = 9;
	public static final int ONE_ENEMY_WATER = 10;
	public static final int ONE_ENEMY_GROUND_ENEMY = 11;
	public static final int ONE_ENEMY_WALL_ENEMY = 12;
	public static final int ONE_ENEMY_FLYING_ENEMY = 13;
	 */
	private static boolean passable(int tile) {
		if((	tile==ONE_ENEMY_AIR ||tile==ONE_ENEMY_LADDER||tile==ONE_ENEMY_ORB||tile==ONE_ENEMY_BREAKABLE||tile==ONE_ENEMY_WATER||tile>10)) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * determines if a screen can go right or not
	 * @param level the level	
	 * @param rightScreenSide the right side of the screen
	 * @param y1 the y-coordinate (upper)
	 * @return true if the screen can move to the right, false otherwise
	 */
	public static boolean canGoRight(List<List<Integer>> level, int rightScreenSide, int y1) {
		if(rightScreenSide+1<level.get(0).size()&&level.get(y1).get(rightScreenSide+1)!=9) {
			if(rightScreenSide+2<level.get(0).size()&&level.get(y1).get(rightScreenSide+2)==9||(rightScreenSide+10<level.get(0).size()&&level.get(y1).get(rightScreenSide+10)==9)) return true;
			boolean canMove = false;
			for(int i = y1;i<y1+14;i++) {
				int j = rightScreenSide;
				//level.get(i).get(j) is passable
				int tile1 = level.get(i).get(j);
				int tile2 = level.get(i).get(j+1);
				//System.out.print(tile1+""+tile2+" ");
				if(passable(tile1)&&passable(tile2)) {
					canMove = true;
					//System.out.print(canMove);
				}
				//System.out.println();
				
			}
			//System.out.println(canMove);
			return canMove;
		}
		
		return false;
	}
	/**
	 * determines if a screen can go down or not
	 * @param level the level	
	 * @param rightScreenSide the right side of the screen
	 * @param y1 the y-coordinate (upper)
	 * @return true if the screen can move down, false otherwise
	 */
	public static boolean canGoDown(List<List<Integer>> level, int rightScreenSide, int y1) {
		//System.out.println(level.size()+" "+y1);
		if(y1+14<level.size()&&level.get(y1+14).get(rightScreenSide)!=9) {
			if(y1+20<level.size()&&level.get(y1+20).get(rightScreenSide)==9||y1+16>=level.size()||y1+25>=level.size()) return true;
			boolean canMove = false;
			for(int i = rightScreenSide-15;i<=rightScreenSide;i++) {
				int j = y1+14;
				//level.get(i).get(j) is passable
				int tile1 = level.get(j).get(i);
				int tile2 = level.get(j-1).get(i);
				if(passable(tile1)&&passable(tile2)) {
					canMove = true;
				}
				
			}
			return canMove;
		}
		
		return false;
	}
	/**
	 * determines if a screen can go up or not
	 * @param level the level	
	 * @param rightScreenSide the right side of the screen
	 * @param y1 the y-coordinate (upper)
	 * @return true if the screen can move up, false otherwise
	 */
	public static boolean canGoUp(List<List<Integer>> level, int rightScreenSide, int y1) {
		 if(y1-1>=0&&level.get(y1-1).get(rightScreenSide-14)!=9&&level.get(y1-1).get(rightScreenSide)!=9) {
			 if(y1-2>=0&&level.get(y1-2).get(rightScreenSide-14)==9) return true;
			 boolean canMove = false;
				for(int i = rightScreenSide-14;i<=rightScreenSide;i++) {
					int j = y1;
					//level.get(i).get(j) is passable
					int tile1 = level.get(j).get(i);
					int tile2 = level.get(j-1).get(i);
					//System.out.println(tile1+""+tile2);
					if(passable(tile1)&&passable(tile2)) {
						canMove = true;
						//System.out.println(canMove);
					}
					
				}
				return canMove;
		 }
		
		return false;
	}
	/**
	 * Finds the new direction for the screen to go given the upper right-hand point
	 * @param level the level
	 * @param xcoord x coordinate of the upper right hand point
	 * @param ycoord y coordinate of the upper right hand point
	 * @param previous the previous direction
	 * @return Direction d the new direction to go
	 */
	private static Direction findNewDirection(List<List<Integer>> level, int xcoord, int ycoord, Direction previous) { 
		Direction d = null;
//		System.out.println(level.get(ycoord-1).get(xcoord-1));
//		System.out.println(previous);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		//System.out.println("previous is "+previous);
		if(canGoRight(level,xcoord,ycoord)&&!previous.equals(Direction.LEFT)) {
			d = Direction.RIGHT;
			//System.out.println("oopsie");
		}
		
		else if(canGoUp(level,xcoord,ycoord)&&!previous.equals(Direction.DOWN)) { //prioritize going up (for level 9)
			d = Direction.UP;
			//System.out.println("UP");
		}
		
		else if(canGoDown(level,xcoord,ycoord)&&!previous.equals(Direction.UP)) {
//			System.out.println("previous:"+previous);
			d = Direction.DOWN;
//			System.out.println("DOWN");

		}else if(canGoLeft(level,xcoord,ycoord)&&!previous.equals(Direction.RIGHT)) {
			d = Direction.LEFT;
		}
		//System.out.println(d);
		return d;
		
	}
/**
 * takes in a level and finds its spawn screen
 * @param level level
 * @return a 16x14 screen containing MegaMan
 */
	public static List<Point> findSpawnScreen(List<List<Integer>> level) {
		Point spawn = new Point();
		for(int y = 0;y<level.size();y++) {
			for(int x = 0;x<level.get(0).size();x++) {
				if(level.get(y).get(x)==8) {
					spawn = new Point(x,y);
				}
			}
		}
		int spawnX = (int) spawn.getX();
		int spawnY = (int) spawn.getY();
		int distanceFromLeft = 0;
		for(int x = spawnX-1;x>=0;x--) {
			if(x<0||level.get(spawnY).get(x)!=9) {
				distanceFromLeft++;
				if(distanceFromLeft>16) {
					
					start = Direction.LEFT;
				}
			}else {
				break;
			}
		}
		
		int distanceFromRight = 0;
		for(int x = spawnX+1;x<level.get(0).size();x++) {
			if(level.get(spawnY).get(x)!=9) {
				distanceFromRight++;
				if(distanceFromRight>16) {
					
					start = Direction.RIGHT;
				}
			}else{
				break;
			}
		}

		int distanceFromTop = 0;
		for(int y = spawnY-1;y>=0;y--) {
			if(level.get(y).get(spawnX)!=9) {
				distanceFromTop++;
				if(distanceFromTop>14) {
					start = Direction.UP;
				}
			}else{
				break;
			}
		}
		int distanceFromBottom = 0;
		for(int y = spawnY;y<level.size();y++) {
			if(level.get(y).get(spawnX)!=9) {
				distanceFromBottom++;
				if(distanceFromBottom>14) {
					start = Direction.DOWN;
				}
			}else{
				break;
			}
		}
		if(distanceFromBottom>14) {
			distanceFromBottom = 14-distanceFromTop;
		}if(distanceFromTop>14) {
			distanceFromTop = 14- distanceFromBottom;
		}if(distanceFromRight>16) {
			distanceFromRight = 16 - distanceFromLeft-1;
		}if(distanceFromLeft>16) {
			distanceFromLeft = 16 - distanceFromRight-1;
		}
//		System.out.println(distanceFromRight);

		
//		System.out.println(distanceFromRight);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		//System.out.println()
		
		int x1 = spawnX - distanceFromLeft;
		int y1 = spawnY - distanceFromTop;
		int x2 = spawnX + distanceFromRight;
		int y2 = spawnY + distanceFromBottom;
		Point x1y1 = new Point (x1,y1);
		Point x2y1 = new Point (x2,y1);
		Point x1y2 = new Point (x1,y2);
		Point x2y2 = new Point (x2,y2);
		

		List<Point> k = new ArrayList<Point>();
		k.add(x1y1);
		k.add(x2y1);
		k.add(x1y2);
		k.add(x2y2);
		return k;
	}


	/**
	 * prints the level to the console
	 * @param level  
	 */
	public static void printLevel(List<List<Integer>> level) {
		for(List<Integer> k : level) {
			for(Integer m: k) {
				//if(m!=9) {
					System.out.print(/*f("%3d",*/m+" ");

				//}

			}
			System.out.println();
		}
	}
	/**
	 * 
	 * NOT USED, REPLACED BY upAndDownTrainingData
	 * start at 0,0
	 * scan down (y++) until you find a number that is not 17 (null)
	 * then count how many below it are not 17 (null)
	 * (use the count to tell you 15x15 or 16x16)
	 * then scan right until the rightmost number is 17 (null)
	 * save each iteration into a List<List<List<Integer>>>
	 * @param level the 2d array of ints
	 */
	private static void convertMegaManLevelToJSONHorizontalScroll(List<List<Integer>> level) {
		visited.clear();
		//List<List<List<Integer>>> json = new ArrayList<>();
		boolean vertical = false;
		//scroller for the screen
		//int intXint = 0;
		lowerY = 0;
		lowerX = 0;
		int intYint = 14;
		int intXint = intYint+2;
//		System.out.println(intXint+", "+intYint);

		for(int y = 0; y<level.size();y++) {
			for(int x = 0;x<level.get(0).size();x++) {
				List<List<Integer>> screen = new ArrayList<>();			
				if(level.get(y).get(x)!=9&&y+intYint<level.size()&&x+intXint<level.get(0).size()&&level.get(y).get(x+intXint)!=9&&!visited.contains(new Point(x,y))&&((x==0||level.get(y).get(x-1)!=9)&&level.get(y).get(x+intXint-1)!=9)) {//NORMALLY USE 17 FOR NULL!!! IS NOW 9!!!
					upperY = y;
					lowerX = x;				
					screen = copyScreen(level, intXint, intYint, lowerX, upperY, vertical);
					//System.out.println("this is a screen");
					//printLevel(screen);
					json.add(screen);
				}
			}
		}
		//return json;
	}
	/**
	 *  NOT USED, REPLACED BY upAndDownTrainingData
	 * @param level
	 */
	private static void convertMegaManLevelToJSONVerticalScroll(List<List<Integer>> level) { 
		visited.clear();
		
		//List<List<List<Integer>>> json = new ArrayList<>();
		boolean vertical = true;
		lowerY = 0;
		lowerX = 0;
		int intYint = 14;
		int intXint = intYint+2;

		//scans from  bottom up
		for(int x = 0; x<level.get(0).size();x++) {
			for(int y = level.size()-1;y>=0;y--) {
				List<List<Integer>> screen = new ArrayList<>();			

				if((y-intYint>=0&&(x==0&&level.get(y).get(x)!=9||(x!=0&&level.get(y).get(x-1)==9&&level.get(y-intYint+1).get(x-1)==9))&&
						(x+intXint==level.get(0).size()||
						(x+intXint<level.get(0).size()&&level.get(y).get(x+intXint)==9&& level.get(y-intYint+1).get(x+intXint)==9))&& //could be x+intXint+1
						!visited.contains(new Point(x,y))&&
						level.get(y).get(x)!=9&&
						level.get(y).get(x+intXint-1)!=9&&level.get(y-intYint).get(x)!=9&&
						level.get(y-intYint+1).get(x+intXint-1)!=9)) { //check four corners (with xcoord+1 on each) if
							//either left point is null AND either right point is null, then save the screen
					//System.out.println("  1 ");
					
					upperY = y-intYint+1;
					lowerX = x;	
					putPointsInHashSet(level, intXint, intYint,lowerX, y);
					screen = copyScreen(level, intXint, intYint, lowerX, upperY, vertical);
					System.out.println("this is a screen");
					printLevel(screen);
					json.add(screen);
				}
			}
		}
	}
	/**
	 * puts visited points in a hashset
	 * @param level
	 * @param intXint
	 * @param intYint
	 * @param lowerX2
	 * @param y1
	 */
	private static void putPointsInHashSet(List<List<Integer>> level, int intXint, int intYint, int lowerX2, int y1) {
		// TODO Auto-generated method stub
		for(int x = lowerX2;x<=lowerX2+intXint;x++) {
			visited.add(new Point(x, y1)); //add visited points to hashset
		}
	}
 /**
  * copies a screen from a whole level
  * @param level level
  * @param intXint width
  * @param intYint height
  * @param lowerX lower x coord
  * @param upperY upper y coord
  * @param vertical whether or not this is vertical
  * @return
  */
	private static List<List<Integer>> copyScreen(List<List<Integer>> level, int intXint, int intYint, int lowerX,
			int upperY, boolean vertical) {
		List<List<Integer>> screen = new ArrayList<>();
		for(int y = 0;y<intYint;y++) {
			List<Integer> okay = new ArrayList<>();
			for (int x = 0;x<intXint;x++) {
				if(lowerX+x<level.get(0).size()) {
					okay.add(level.get(upperY).get(lowerX+x));
				}
			}
			if(!vertical) visited.add(new Point(lowerX, upperY)); //add visited points to hashset
			screen.add(okay);
			upperY++;
		}

		return screen;
	}
	
	/**
	 * converts the MegaMan List<List<Integer>> into mmlv format
	 * @param level the level
	 * @param levelName the name desired
	 * @param path where to store
	 * @return File mmlvFile
	 */
	public static File convertMegaManLevelToMMLV(List<List<Integer>> level, String levelName, String path) {
		//int enemyCount = 0;

		// TODO Auto-generated method stub
		int xcoord = 0;
		int ycoord = 0;
		HashSet<Point> o = new HashSet<Point>();
		HashSet<Point> movingPlat = new HashSet<Point>();
		File levelFile = null;
		try {

		levelFile = new File(path+levelName+".mmlv");
		
		if(!levelFile.exists()) {
			levelFile.createNewFile();
			
		}
		PrintWriter p = new PrintWriter(levelFile);
		p.println("[Level]");
		for(int y = 0;y<level.size();y++) {
			List<Integer> k = level.get(y);
			//int l=0;
			for(int x = 0;x<level.get(0).size();x++) { //TODO convert mmlv to json
				Integer m = k.get(x);
				//l=m;
				//if play online, does it download to mmlv file???
				if(m==1||m==6/*||m==12||m==6*/) { //solid ground TODO make case for cannon shooter (not just blocks) TODO make case for appear/dis blocks
					p.println("k"+xcoord+","+ycoord+"=\"71.000000\"");
					p.println("j"+xcoord+","+ycoord+"=\"71.000000\"");
					p.println("i"+xcoord+","+ycoord+"=\"1.000000\"");
					p.println("e"+xcoord+","+ycoord+"=\"3.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
				//	l=1;
				}else if(m==2) { //ladders
					/*
					 * i0,0="3.000000"e0,0="98.000000"a0,0="1.000000"
					 */
					p.println("i"+xcoord+","+ycoord+"=\"3.000000\"");
					p.println("e"+xcoord+","+ycoord+"=\"98.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
				//	l=2;

				}else if(m==3) { //spikes
					p.println("l"+xcoord+","+ycoord+"=\"4.000000\"");
					p.println("i"+xcoord+","+ycoord+"=\"2.000000\"");
					p.println("e"+xcoord+","+ycoord+"=\"7.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
				//	l=3;
				}
				else if (m==8) { //player
					p.println("1t=\"0.000000\"");
					p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
					p.println("d"+xcoord+","+ycoord+"=\"4.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
					//l=11;
				}else if (m == 5||m==20) { //moving platform
					if(x+2<k.size()) {
					if(k.get(x+1)==5) {
						movingPlat.add(new Point(x+1,y));
						
					}else if(x+3<k.size()&&k.get(x+2)==5&&k.get(x+3)==5) {
						movingPlat.add(new Point(x+2,y));

					}
					if(k.get(x+1)!=5&&k.get(x+2)==5) {
						int nx = xcoord+16;
						p.println("o"+nx+","+ycoord+"=\"9999.000000\"");
						p.println("e"+nx+","+ycoord+"=\"31.000000\"");
						p.println("d"+nx+","+ycoord+"=\"6.000000\"");
						p.println("a"+nx+","+ycoord+"=\"1.000000\"");
					}
					}
					//print the platform track with platform
					p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
					if(!movingPlat.contains(new Point(x,y))) {
						p.println("h"+xcoord+","+ycoord+"=\"2.000000\"");
					}
					p.println("e"+xcoord+","+ycoord+"=\"31.000000\"");
					p.println("d"+xcoord+","+ycoord+"=\"6.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");

				}else if(m==4) { //breakable				
					p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
					p.println("e"+xcoord+","+ycoord+"=\"9.000000\"");
					p.println("d"+xcoord+","+ycoord+"=\"6.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");

				}else if (m==10) { //water
					p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
					p.println("e"+xcoord+","+ycoord+"=\"177.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
					
				}else if(m==7) { //z orb
					p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
					p.println("e"+xcoord+","+ycoord+"=\"15.000000\"");
					p.println("d"+xcoord+","+ycoord+"=\"8.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");

				}else if(m>10) { //is an enemy
					if(levelEnemies.isEmpty()||levelEnemies!=null||levelEnemies.get(new Point(x,y)).contentEquals(null)) {
						//System.out.println("pringint improper enemy types helas");
						if(Parameters.parameters.booleanParameter("megaManUsesUniqueEnemies")) printEnemiesToMMLVFromUniqueEnemy(p, xcoord, ycoord, level, x, y, m);
						else printEnemiesToMMLVFromOneEnemyType(p, xcoord, ycoord, level, x, y, m);
					}
					else {
						System.out.println(levelEnemies);

						printEnemiesToMMLVFromHashMap(p, xcoord, ycoord, level, x, y);
					}
				}
				
				if(m!=9) {
					placeActivatedScreen(xcoord,ycoord, p);
					p.println("2a"+xcoord+","+ycoord+"=\"1.000000\"");
//					if(Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")) {
						p.println("2c"+xcoord+","+ycoord+"=\"1.000000\"");
//					}
				}


				xcoord+=16;
			}
			xcoord = 0;
			ycoord+=16;

		}
		//System.out.println("not actually happening");
		//NEED 2a for enabling squares
		p.println("2b"+0+","+896+"=\"0.000000\"");
		p.println("2b"+0+","+896+"=\"0.000000\"");
		p.println("2b"+0+","+672+"=\"0.000000\"");
		p.println("2b"+0+","+448+"=\"0.000000\"");
		p.println("2b"+0+","+224+"=\"0.000000\"");
		p.println("2b"+0+","+0+"=\"0.000000\"");
		//p.println("2a"+0+","+0+"=\"1.000000\"");
		p.println("1s=\"4480.000000\"");
		p.println("1r=\"0.000000\"");
		p.println("1q=\""+12800+"\""); //CHANGE TO POS INFINITY
		p.println("1p=\"0.000000\"");
		p.println("1m=\"9.000000\"");
		p.println("1l=\"11.000000\"");
//		1k2="11.000000"
//				1k1="51.000000"
		if(Parameters.parameters.booleanParameter("megaManAllowsPlatformGun"))
		p.println("1k2=\"11.000000\"");
		if(Parameters.parameters.booleanParameter("megaManAllowsBlockBreaker"))
		p.println("1k1=\"51.000000\"");

		p.println("1k0=\"0.000000\"");
		p.println("1bc=\"0.000000\"");
		p.println("1f=\"-1.000000\"");
		p.println("1e=\"29.000000\"");
		p.println("1d=\"6.000000\"");
		p.println("1bb=\"0.000000\"");
		p.println("1ca=\"0.000000\"");
		p.println("1ba=\"0.000000\"");
		p.println("1c=\"1.000000\"");
		p.println("1b=\"1.000000\"");
		p.println("4b=\"64.000000\"");
		p.println("4a=\"dakuchen\""); //your user name

		p.println("1a=\""+levelName+".mmlv\"");

//				0v="1.6.3"
		p.println("0v=\"1.6.3\"");

//				0a="408500.000000"
		p.println("0a=\"408382.000000\"");
		p.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return levelFile;
	}
	/**
	 * Prints three enemy types from one (wall, floor, flying)
	 * @param p printWriter for the file
	 * @param xcoord xcoord (mmlv)
	 * @param ycoord ycoord (mmlv)
	 * @param level List<List<Integer>> level
	 * @param x xcoord (List<List<Integer>>)
	 * @param y ycoord (List<List<Integer>>)
	 * @param m mmlv ID
	 */
	private static void printEnemiesToMMLVFromOneEnemyType(PrintWriter p, int xcoord, int ycoord, List<List<Integer>> level, int x, int y,
			int m) {
		//if(level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_GROUND_ENEMY) {

		if(m==ONE_ENEMY_WALL_ENEMY) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"2.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			if(x>0&&level.get(y).get(x-1)!=1) p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}else if(m==ONE_ENEMY_GROUND_ENEMY) {
			if(x%2==0&&y>0&&level.get(y-1).get(x)!=ONE_ENEMY_SOLID){
				p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
				p.println("e"+xcoord+","+ycoord+"=\"0.000000\"");
				p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
				p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
				p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
			}else{
				p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
				p.println("e"+xcoord+","+ycoord+"=\"4.000000\"");
				p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
				if(y>0&&level.get(y-1).get(x)==ONE_ENEMY_SOLID)p.println("c"+xcoord+","+ycoord+"=\"-1.000000\"");
				p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
			}
		}else if(m==ONE_ENEMY_FLYING_ENEMY){
			if(x%2==0){
				p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
				p.println("e"+xcoord+","+ycoord+"=\"20.000000\"");
				p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
				p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
				p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
			}else{
				p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
				p.println("e"+xcoord+","+ycoord+"=\"117.000000\"");
				p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
				p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
			}
		}
	//	}
	}
	/**
	 * 
	 * NOT USED ?
	 * Prints for all unique MegaMan enemy types (from Mega Man 1)
	 * @param p
	 * @param xcoord
	 * @param ycoord
	 * @param level
	 * @param x
	 * @param y
	 * @param m
	 */
	private static void printEnemiesToMMLVFromUniqueEnemy(PrintWriter p, int xcoord, int ycoord, List<List<Integer>> level, int x, int y,
			int m) {
		//String enemyString = levelEnemies.get(new Point(x,y));
		//System.out.println(enemyString);
		//MiscUtil.waitForReadStringAndEnterKeyPress();
		if(m==11) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"0.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		} 
		else if(m==12) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"63.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==13) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"1.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==14) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("g"+xcoord+","+ycoord+"=\"270.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"1.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		
		else if(m==15) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"2.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			if(x>0&&level.get(y).get(x-1)!=1) p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==16) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"3.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==17) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"4.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			if(y>0&&level.get(y-1).get(x)==1)p.println("c"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==18) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==19) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"48.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==20) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"49.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==21) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"52.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==22) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"56.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==23) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"57.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==24) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("h"+xcoord+","+ycoord+"=\"3.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"58.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==25) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"59.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==26) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"60.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==27) {
			int toX = xcoord+4*16;
			int toY = ycoord+16;
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("n"+xcoord+","+ycoord+"=\""+toY+"\"");
			p.println("m"+xcoord+","+ycoord+"=\""+toX+"\"");
			p.println("e"+xcoord+","+ycoord+"=\"45.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==28) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"159.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(m==29) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"7.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		
	}
	
	/**
	 * Prints the mlmv files from the original VLGC
	 * @param p printWriter for the file
	 * @param xcoord xcoord (mmlv)
	 * @param ycoord ycoord (mmlv)
	 * @param level List<List<Integer>> level
	 * @param x xcoord (List<List<Integer>>)
	 * @param y ycoord (List<List<Integer>>)
	 */
	private static void printEnemiesToMMLVFromHashMap(PrintWriter p, int xcoord, int ycoord,
			List<List<Integer>> level, int x, int y) {
		String enemyString = levelEnemies.get(new Point(x,y));
		System.out.println(enemyString);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		if(enemyString.equals("a")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"0.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		} 
		else if(enemyString.equals("b")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"63.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("<")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"1.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("^")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("g"+xcoord+","+ycoord+"=\"270.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"1.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		
		else if(enemyString.equals("c")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"2.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			if(x>0&&level.get(y).get(x-1)!=1) p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("d")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"3.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("e")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"4.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			if(y>0&&level.get(y-1).get(x)==1)p.println("c"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("f")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("g")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"48.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("h")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"49.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("i")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"52.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("j")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"56.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("k")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"57.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("m")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("h"+xcoord+","+ycoord+"=\"3.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"58.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("n")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"59.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("o")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"60.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("p")) {
			int toX = xcoord+4*16;
			int toY = ycoord+16;
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("n"+xcoord+","+ycoord+"=\""+toY+"\"");
			p.println("m"+xcoord+","+ycoord+"=\""+toX+"\"");
			p.println("e"+xcoord+","+ycoord+"=\"45.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("q")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"159.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		else if(enemyString.equals("r")) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"7.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		
	}
	/**
	 * 
	 * NOT USED
	 * Prints 5 types of enemies
	 * @param p printWriter for the file
	 * @param xcoord xcoord (mmlv)
	 * @param ycoord ycoord (mmlv)
	 * @param level List<List<Integer>> level
	 * @param x xcoord (List<List<Integer>>)
	 * @param y ycoord (List<List<Integer>>)
	 */
	private static void printEnemiesToMMLV(PrintWriter p, int m, int xcoord,  int  ycoord, List<List<Integer>> level, int x, int y) {
		if(m==11) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"0.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}else if (m==12) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"4.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			if(y>0&&level.get(y-1).get(x)==1)p.println("c"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}else if (m==13) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"48.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		} else if(m==14) {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"56.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}else {
			p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
			p.println("e"+xcoord+","+ycoord+"=\"159.000000\"");
			p.println("d"+xcoord+","+ycoord+"=\"5.000000\"");
			p.println("b"+xcoord+","+ycoord+"=\"-1.000000\"");
			p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
		}
		
		
		//return enemyCount;
	}
	/**
	 * Activates a screen at the coordinates (for mmlv)
	 * @param xcoord of the mmlv level
	 * @param ycoord of the mmlv level
	 * @param p printwriter to the mmlv file
	 */
	private static void placeActivatedScreen(int xcoord, int ycoord, PrintWriter p) {
		// TODO Auto-generated method stub
		int howManySquaresX = xcoord/256;
		int howManySquaresY = ycoord/224;
		int screenX = howManySquaresX*256;
		int screenY = howManySquaresY*224;
		if(!activatedScreens.contains(new Point(screenX, screenY))){
			p.println("2a"+screenX+","+screenY+"=\"1.000000\"");
			activatedScreens.add(new Point(screenX,screenY));

		}
		
	}
	
	
	/**
	 * Converts the VGLC data to List<List<Integer>> format
	 * @param fileName of the VGLC data
	 * @return List<List<Integer>> level - the level
	 */
	public static List<List<Integer>> convertMegamanVGLCtoListOfLists(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<Integer>> complete = new ArrayList<>();
		//loops through levels to get characters and convert them 
		for(int i = 0; i < level.length; i++) { 
			List<Integer> col = new ArrayList<>();//creates new List to be a new row of the JSON 
			for(int j = 0; j < level[i].length(); j++) { //fills that array list that got added to create the row
				if(level[i].charAt(j) != '[' || level[i].charAt(j) != ']') {
//					int tileCode = convertMegamanTilesToInt(level[i].charAt(j)); 
					int tileCode = convertMegamanTilesToIntEnemies(level[i].charAt(j)); 
					String enemyTypeString = getStringForTypeEnemy(level[i].charAt(j));
					if(enemyTypeString!=null) levelEnemies.put(new Point(j,i), enemyTypeString);
					col.add(tileCode);
				}
			}
			complete.add(col); //adds a new array list to the list at index i 
		}
		return complete;
	} 
	/**
	 * Converts enemy chars into Strings (probably unnecessary)
	 * @param charAt
	 * @return
	 */
	private static String getStringForTypeEnemy(char charAt) {
		switch(charAt) {
		case 'a':
			return "a"; 
		case 'b': //fly boy enemy
			return "b";
		case '<': //octopus battery going left/right
			return "<";
		case '^': //octopus battery going up/down
			return "^";
		case 'c': //beak
			return "c";
		case 'd': //picket man
			return "d";
		case 'e': //screw bomber
			return "e";
		case 'f': //big eye
			return "f";
		case 'g': //spine
			return "g";
		case 'h': //crazy razy
			return "h";
		case 'i': //watcher
			return "i";
		case 'j': //killer bullet
			return "j";
		case 'k': //killer bullet spawner
			return "k";
		case 'm': //tackle fire
			return "m";
		case 'n': //flying shell
			return "n";
		case 'o': //flying shell spawner
			return "o";
		case 'p': //footholder
			return "p";
		case 'q': //jumper
			return "q";
		case 'r': //gunner
			return "r";
		default:
			return null;
		}
		//return null;
	}
	
	/**
	 * Simple A* Model NOT USED
	 * @param tile
	 * @return
	 */
	private static int convertMegaManTilesToIntForASTAR(char tile) {
//		public static final int MEGA_MAN_TILE_GROUND = 1;
//		public static final int MEGA_MAN_TILE_LADDER = 2;
//		public static final int MEGA_MAN_TILE_HAZARD = 3;
//		public static final int MEGA_MAN_TILE_BREAKABLE = 4;
//		public static final int MEGA_MAN_TILE_MOVING_PLATFORM = 5;
//		public static final int MEGA_MAN_TILE_CANNON = 6;
//		public static final int MEGA_MAN_TILE_ORB = 7;
//		public static final int MEGA_MAN_TILE_SPAWN = 8;
		
		switch(tile) {
		case '-': //empty, passable
			return 0;
		case '@': //null
			return 9;	
		case '#': //solid
			return 1; 
		case '|': //ladder
			return 2; 
		case 'H': //Hazard 
			return 3; 
		case 'B': //breakable
			return 4;
		case 'M': //Moving platform
			return 5;
		case 'C': //Cannon/shooter
			return 6;
		case 'Z':
			return 7;
		case 'P':
			return 8;
		case 'A': //appearing/disappearing block
			return 1;
		case 'p': //hovering platform (shooter)
			return 5;
		case '~':
			return 10;
		default:
			return 0;
		}
		
	}
/**
 * used for simple version (no enemies, no water, etc)
 * @param tile
 * @return
 */
	private static int convertMegamanTilesToIntSimple(char tile) {
		switch(tile) {
		case '-': //empty, passable
			return 0;
		case '@': //null
			return 7;	
		case '#': //solid
			return 1; 
		case '|': //ladder
			return 2; 
		case 'H': //Hazard 
			return 3; 
		case 'B': //breakable
			return 4;
		case 'M': //Moving platform
			return 5;
		case 'C': //Cannon/shooter
			return 6;
		case 'P':
			return 8;
		default:
			return 0;
		}
	}
	/**
	 * NOT USED
	 * @param tile
	 * @return
	 */
	private static int convertMegamanTilesToInt(char tile) {
		switch(tile) {
		case '-': //empty, passable
			return 0;
		case '@': //null
			return 17;	
		case '#': //solid
			return 1; 
		case '|': //ladder
			return 2; 
		case 'H': //Hazard 
			return 3; 
		case 'B': //breakable
			return 4;
		case 'A': //appearing/disappearing block
			return 12;
		case 'L': //large health pack
			return 10;
		case 'l': //small health pack
			return 18; 
		case 'W': //large ammo pack 
			return 7; 
		case 'w': //small ammo pack
			return 8;
		case '+': //extra life
			return 9;
		case 'M': //Moving platform
			return 5;
		case 'P': //Player
			return 11;
		case 'C': //Cannon/shooter
			return 6; 
		case 'D': //Door 
			return 13; 
		case 'U': //Transport beam upgrade
			return 14;
		case 't': //"solids" that you can pass through
			return 15;
		case '*': //Special item that falls and fills health and ammo
			return 16;
		case '~': //water
			return 19;
//		case 'p': //hovering platform (shooter)
//			return 20;
		case 'a': //met enemy
			return 50; 
		case 'b': //fly boy enemy
			return 51;
		case '<': //octopus battery going left/right
			return 52;
		case '^': //octopus battery going up/down
			return 53;
		case 'c': //beak
			return 54;
		case 'd': //picket man
			return 55;
		case 'e': //screw bomber
			return 56;
		case 'f': //big eye
			return 57;
		case 'g': //spine
			return 58;
		case 'h': //crazy razy
			return 59;
		case 'i': //watcher
			return 60;
		case 'j': //killer bullet
			return 61;
		case 'k': //killer bullet spawner
			return 62;
		case 'm': //tackle fire
			return 63;
		case 'n': //flying shell
			return 64;
		case 'o': //flying shell spawner
			return 65;
		case 'p': //footholder
			return 66;
		case 'Z':
			return 21;
			
		default:
			throw new IllegalArgumentException("Invalid Mega Man tile from VGLV: " + tile);

		}
	}
	/**
	 * Used for one enemy type for any data trained on 12 tile types
	 * @param tile the char tile from VGLC
	 * @return int corresponding to the type
	 */
	private static int convertMegamanTilesToIntEnemies(char tile) {
		switch(tile) {
		case '-': //empty, passable
			return 0;
		case '#': //solid
			return 1; 
		case '|': //ladder
			return 2; 
		case 'H': //Hazard 
			return 3; 
		case 'B': //breakable
			return 4;
		case 'M': //Moving platform
			return 5;
		case 'C': //Cannon/shooter
			return 6; 
		case '@': //null
			return 9;
		case 'P': //Player
			return 8;
		case 'Z':
			return 7;
		case '~': //water
			return 10;
		case 'a': //met enemy
			return 11; 
		case 'b': //fly boy enemy
			return 11;
		case '<': //octopus battery going left/right
			return 11;
		case '^': //octopus battery going up/down
			return 11;
		case 'c': //beak
			return 11;
		case 'd': //picket man
			return 11;
		case 'e': //screw bomber
			return 11;
		case 'f': //big eye
			return 11;
		case 'g': //spine
			return 11;
		case 'h': //crazy razy
			return 11;
		case 'i': //watcher
			return 11;
		case 'j': //killer bullet
			return 11;
		case 'k': //killer bullet spawner
			return 11;
		case 'm': //tackle fire
			return 11;
		case 'n': //flying shell
			return 11;
		case 'o': //flying shell spawner
			return 11;
		case 'p': //footholder
			return 11;
		case 'q': //jumper
			return 11;
		case 'r': //gunner
			return 11;
		case 'A': //appearing/disappearing block
			return 1;
		case 'L': //large health pack
			return 0;
		case 'l': //small health pack
			return 0; 
		case 'W': //large ammo pack 
			return 0; 
		case 'w': //small ammo pack
			return 0;
		case '+': //extra life
			return 0;
		case 'D': //Door 
			return 0; 
		case 'U': //Transport beam upgrade
			return 0;
		case 't': //"solids" that you can pass through
			return 0;
		case '*': //Special item that falls and fills health and ammo
			return 0;
		
//		case 'p': //hovering platform (shooter)
//			return 20;
					
		default:
			throw new IllegalArgumentException("Invalid Mega Man tile from VGLV: " + tile);

		}
		
		
	}
	/**
	 * NOT USED
	 * for each unique enemy in VGLC/MegaMan 1
	 * @param tile
	 * @return
	 */
	private static int convertMegamanTilesToIntUniqueEnemies(char tile) {
		switch(tile) {
		case '-': //empty, passable
			return 0;
		case '#': //solid
			return 1; 
		case '|': //ladder
			return 2; 
		case 'H': //Hazard 
			return 3; 
		case 'B': //breakable
			return 4;
		case 'M': //Moving platform
			return 5;
		case 'C': //Cannon/shooter
			return 6; 
		case '@': //null
			return 9;
		case 'P': //Player
			return 8;
		case 'Z':
			return 7;
		case '~': //water
			return 10;
		case 'a': //met enemy
			return 11; 
		case 'b': //fly boy enemy
			return 12;
		case '<': //octopus battery going left/right
			return 13;
		case '^': //octopus battery going up/down
			return 14;
		case 'c': //beak
			return 15;
		case 'd': //picket man
			return 16;
		case 'e': //screw bomber
			return 17;
		case 'f': //big eye
			return 18;
		case 'g': //spine
			return 19;
		case 'h': //crazy razy
			return 20;
		case 'i': //watcher
			return 21;
		case 'j': //killer bullet
			return 22;
		case 'k': //killer bullet spawner
			return 23;
		case 'm': //tackle fire
			return 24;
		case 'n': //flying shell
			return 25;
		case 'o': //flying shell spawner
			return 26;
		case 'p': //footholder
			return 27;
		case 'q': //jumper
			return 28;
		case 'r': //gunner
			return 29;
		case 'A': //appearing/disappearing block
			return 1;
		case 'L': //large health pack
			return 0;
		case 'l': //small health pack
			return 0; 
		case 'W': //large ammo pack 
			return 0; 
		case 'w': //small ammo pack
			return 0;
		case '+': //extra life
			return 0;
		case 'D': //Door 
			return 0; 
		case 'U': //Transport beam upgrade
			return 0;
		case 't': //"solids" that you can pass through
			return 0;
		case '*': //Special item that falls and fills health and ammo
			return 0;
		
//		case 'p': //hovering platform (shooter)
//			return 20;
					
		default:
			throw new IllegalArgumentException("Invalid Mega Man tile from VGLV: " + tile);

		}
	}

}
