package megaManMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import gvgai.tools.IO;
@SuppressWarnings("unused")
public class MegaManVGLCUtil {
	public static final String MEGAMAN_ENEMY_LEVEL_PATH = "data/VGLC/MegaMan/EnhancedWithBossesAndEnemies/";
	public static final String MEGAMAN_LEVEL_PATH = "data/VGLC/MegaMan/Enhanced/";
	public static final String MEGAMAN_MMLV_PATH = "data/MegaManLevels_mmlv/";
	public static HashSet<Point> visited = new HashSet<>();
	public static int lowerY;
	public static int lowerX;
	public static int upperY;
	public static HashMap<Point, String> levelEnemies = new HashMap<Point, String>();
	public static HashSet<Point> activatedScreens = new HashSet<Point>();
	public static List<List<List<Integer>>> json = new ArrayList<>();

	//public static boolean executed = false;
	//public static int levelNumber;
	public static void main(String[] args) {
//		int firstLevel = 1;
//		int lastLevel = 10;
//		for(int i = firstLevel;i<=lastLevel;i++) {
//			List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_"+i+".txt");
//			//convertMegaManLevelToMMLV(level, i);
//			convertMegaManLevelToJSONHorizontalScroll(level);
//
//		}
//		for(int i=1;i<=10;i++) {
//			if(i!=7) {
//				List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_"+i+".txt");
//				convertMegaManLevelToJSONVerticalScroll(level);
//				if(i!=3) convertMegaManLevelToJSONHorizontalScroll(level);
//				
//						
//						
//						
//			}
//		}
//		List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_"+2+".txt");
//		printLevel(level);
//		convertMegaManLevelToMMLV(level, 2);
//		convertMegaManLevelToJSONVerticalScroll(level);
//		convertMegaManLevelToMMLV(level,5);
		//System.out.println(json.toString());
	}
	/**
	 * prints the level to the console
	 * @param level  
	 */
	public static void printLevel(List<List<Integer>> level) {
		for(List<Integer> k : level) {
			for(Integer m: k) {
				System.out.print(m);

			}
			System.out.println();
		}
	}
	/**
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
				if(level.get(y).get(x)!=7&&y+intYint<level.size()&&x+intXint<level.get(0).size()&&level.get(y).get(x+intXint)!=7&&!visited.contains(new Point(x,y))&&((x==0||level.get(y).get(x-1)!=7)&&level.get(y).get(x+intXint-1)!=7)) {//NORMALLY USE 17 FOR NULL!!! IS NOW 7!!!
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

				if((y-intYint>=0&&(x==0&&level.get(y).get(x)!=7||(x!=0&&level.get(y).get(x-1)==7&&level.get(y-intYint+1).get(x-1)==7))&&
						(x+intXint==level.get(0).size()||
						(x+intXint<level.get(0).size()&&level.get(y).get(x+intXint)==7&& level.get(y-intYint+1).get(x+intXint)==7))&& //could be x+intXint+1
						!visited.contains(new Point(x,y))&&
						level.get(y).get(x)!=7&&
						level.get(y).get(x+intXint-1)!=7&&level.get(y-intYint).get(x)!=7&&
						level.get(y-intYint+1).get(x+intXint-1)!=7)) { //check four corners (with xcoord+1 on each) if
							//either left point is null AND either right point is null, then save the screen
					System.out.println("  1 ");
					
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
	private static void putPointsInHashSet(List<List<Integer>> level, int intXint, int intYint, int lowerX2, int y1) {
		// TODO Auto-generated method stub
		for(int x = lowerX2;x<=lowerX2+intXint;x++) {
			visited.add(new Point(x, y1)); //add visited points to hashset
		}
	}

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
	public static File convertMegaManLevelToMMLV(List<List<Integer>> level, int levelNumber) {
		//int enemyCount = 0;

		// TODO Auto-generated method stub
		int xcoord = 0;
		int ycoord = 0;
		HashSet<Point> o = new HashSet<Point>();
		HashSet<Point> movingPlat = new HashSet<Point>();
		File levelFile = null;
		try {

		levelFile = new File(MEGAMAN_MMLV_PATH+"MegaManLevel"+levelNumber+".mmlv");
		
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
					if(x+2<=k.size()) {
					if(k.get(x+1)==5) {
						movingPlat.add(new Point(x+1,y));
						
					}else if(k.get(x+2)==5&&k.get(x+3)==5) {
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

				}else if(m==4&&!o.contains(new Point(x,y))) { //breakable
					//add surrounding points to the hashset so that you don't add multiple breakables in one spot!!
					o.add(new Point(x, y));
					o.add(new Point(x+1, y));
					o.add(new Point(x, y+1));
					o.add(new Point(x+1, y+1));
					int newx = xcoord+16;
					int newy = ycoord+16;
					p.println("o"+newx+","+newy+"=\"9999.000000\"");
					p.println("e"+newx+","+newy+"=\"45.000000\"");
					p.println("d"+newx+","+newy+"=\"6.000000\"");
					p.println("a"+newx+","+newy+"=\"1.000000\"");

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
					if(levelEnemies.isEmpty()) {

						printEnemiesToMMLV(p, m, xcoord, ycoord, level, x, y);
					}
					else {
						System.out.println(levelEnemies);

						printEnemiesToMMLVFromHashMap(p, xcoord, ycoord, level, x, y);
					}
				}
				
				if(m!=9) {
					placeActivatedScreen(xcoord,ycoord, p);
					p.println("2a"+xcoord+","+ycoord+"=\"1.000000\"");
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
		p.println("1k2=\"11.000000\"");
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

		p.println("1a=\"MegaManLevel"+levelNumber+".mmlv\"");

//				0v="1.6.3"
		p.println("0v=\"1.6.3\"");

//				0a="408382.000000"
		p.println("0a=\"408382.000000\"");
		p.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return levelFile;
	}
	private static void printEnemiesToMMLVFromHashMap(PrintWriter p, int xcoord, int ycoord,
			List<List<Integer>> level, int x, int y) {
		String enemyString = levelEnemies.get(new Point(x,y));
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
	public static List<List<Integer>> convertMegamanVGLCtoListOfLists(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<Integer>> complete = new ArrayList<>();
		//loops through levels to get characters and convert them 
		for(int i = 0; i < level.length; i++) { 
			List<Integer> col = new ArrayList<>();//creates new List to be a new row of the JSON 
			for(int j = 0; j < level[i].length(); j++) { //fills that array list that got added to create the row
				if(level[i].charAt(j) != '[' || level[i].charAt(j) != ']') {
//					int tileCode = convertMegamanTilesToInt(level[i].charAt(j)); 
					int tileCode = convertMegamanTilesToIntSimple(level[i].charAt(j)); 
					String enemyTypeString = getStringForTypeEnemy(level[i].charAt(j));
					if(enemyTypeString!=null) levelEnemies.put(new Point(j,i), enemyTypeString);
					col.add(tileCode);
				}
			}
			complete.add(col); //adds a new array list to the list at index i 
		}
		return complete;
	}
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
		default:
			return 0;
		}
	}
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
			return 7;
		case 'P': //Player
			return 8;
		case 'Z':
			return 9;
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
			return 12;
		case 'd': //picket man
			return 12;
		case 'e': //screw bomber
			return 12;
		case 'f': //big eye
			return 12;
		case 'g': //spine
			return 13;
		case 'h': //crazy razy
			return 13;
		case 'i': //watcher
			return 13;
		case 'j': //killer bullet
			return 14;
		case 'k': //killer bullet spawner
			return 14;
		case 'm': //tackle fire
			return 14;
		case 'n': //flying shell
			return 14;
		case 'o': //flying shell spawner
			return 15;
		case 'p': //footholder
			return 15;
		case 'q': //jumper
			return 15;
		case 'r': //gunner
			return 15;
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
