package megaManMaker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import java.awt.Point;
import java.io.File;
//import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import gvgai.tools.IO;

public class MegaManVGLCUtil {
	public static final String MEGAMAN_LEVEL_PATH = "data/VGLC/MegaMan/Enhanced/";

	
	public static void main(String[] args) {
		List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_6.txt");
		for(List<Integer> k : level) {
			for(Integer m: k) {
				System.out.print(m);

			}
			System.out.println();
		}
		convertMegaManLevelToMMLV(level);
		convertMegaManLevelToJSON(level);
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
	private static void convertMegaManLevelToJSON(List<List<Integer>> level) { //take upper left x,y, take width height param returns screen
		List<List<List<Integer>>> json = new ArrayList<>();
		List<List<Integer>> screen = new ArrayList<>();

		//scroller for the screen
		int intXint = 0;
		int lowerY = 0;
		for(int x = 0;x<level.get(0).size();x++) {
			for(int y = 0;y<level.size();y++) {
				if(level.get(y).get(x)!=17) {
					intXint++;
					lowerY = y;
				}
			}
			if(intXint!=0) {
				break;
			}
		}
		int upperY = lowerY-intXint+1;
		//System.out.println("this is it: "+intXint);
		for(int y = 0;y<intXint;y++) {
			List<Integer> okay = new ArrayList<>();
			for (int x = 0;x<intXint;x++) {
				//screen.get(y).add(okay.get(x));// = level.get(y).get(x);
				okay.add(level.get(upperY).get(x));
			}
			screen.add(okay);
			upperY++;
		}
		
		
		
		
		
		
		
		
		
		
		
		//System.out.println("this is a screen");
		for(List<Integer> k : screen) {
			for(Integer m: k) {
				System.out.print(m);

			}
			System.out.println();
		}
	}
	private static void convertMegaManLevelToMMLV(List<List<Integer>> level) {
		// TODO Auto-generated method stub
		int xcoord = 0;
		int ycoord = 0;
		int levelNumber = 6;
		HashSet<Point> o = new HashSet<Point>();
		HashSet<Point> movingPlat = new HashSet<Point>();
		try {
		File levelFile = new File("MegaManLevel"+levelNumber+".mmlv");
		
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
				//if play online, does it download to mmlv file???
				if(m==1||m==12||m==6) { //solid ground TODO make case for cannon shooter (not just blocks) TODO make case for appear/dis blocks
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
				else if (m==11) {
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

				}else if (m==19) { //water
					p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
					p.println("e"+xcoord+","+ycoord+"=\"177.000000\"");
					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
					
				}
//				else if (m==6) { //TODO make proper sequencing
//					Random rand = new Random();
//					p.println("o"+xcoord+","+ycoord+"=\"9999.000000\"");
//					p.println("h"+xcoord+","+ycoord+"=\""+rand.nextInt(6)+"\"");
//					p.println("e"+xcoord+","+ycoord+"=\"5.000000\"");
//					p.println("d"+xcoord+","+ycoord+"=\"6.000000\"");
//					p.println("a"+xcoord+","+ycoord+"=\"1.000000\"");
//				}

				if(xcoord%256==0/*&&m!=17*/) {
					//add 2a clause
					p.println("2a"+xcoord+","+ycoord+"=\"1.000000\"");
					p.println("2c"+xcoord+","+ycoord+"=\"1.000000\"");

				}
				xcoord+=16;
			}
			xcoord = 0;
			ycoord+=16;
			if(ycoord%256==0/*&&l!=17*/) {
				//add 2a clause
				p.println("2a"+xcoord+","+ycoord+"=\"1.000000\"");
				p.println("2c"+xcoord+","+ycoord+"=\"1.000000\"");

			}
		}
		//NEED 2a for enabling squares
		p.println("2b"+0+","+896+"=\"0.000000\"");
		p.println("2b"+0+","+896+"=\"0.000000\"");
		p.println("2b"+0+","+672+"=\"0.000000\"");
		p.println("2b"+0+","+448+"=\"0.000000\"");
		p.println("2b"+0+","+224+"=\"0.000000\"");
		p.println("2b"+0+","+0+"=\"0.000000\"");
		p.println("2a"+0+","+0+"=\"1.000000\"");
		p.println("1s=\"3000.000000\"");
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

		
	}
	private static List<List<Integer>> convertMegamanVGLCtoListOfLists(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<Integer>> complete = new ArrayList<>();
		//loops through levels to get characters and convert them 
		for(int i = 0; i < level.length; i++) { 
			List<Integer> col = new ArrayList<>();//creates new List to be a new row of the JSON 
			for(int j = 0; j < level[i].length(); j++) { //fills that array list that got added to create the row
				if(level[i].charAt(j) != '[' || level[i].charAt(j) != ']') {
					int tileCode = convertMegamanTilesToInt(level[i].charAt(j)); 
					col.add(tileCode);
				}
			}
			complete.add(col); //adds a new array list to the list at index i 
		}
		return complete;
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
			return 6;
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
			return 12; 
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
		case 'O': //hovering platform (shooter)
			return 20;
		default:
			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);

		}
	}
}
