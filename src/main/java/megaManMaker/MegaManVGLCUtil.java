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
	public static final String MEGAMAN_LEVEL_PATH = "data/VGLC/MegaMan/Processed/";

	
	public static void main(String[] args) {
		List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_2.txt");
		for(List<Integer> k : level) {
			for(Integer m: k) {
				System.out.print(m);

			}
			System.out.println();
		}
		convertMegaManLevelToMMLV(level);
		//convertMegaManLevelToJSON(level);
	}
//	private static void convertMegaManLevelToJSON(List<List<Integer>> level) {
//		List<List<List<Integer>>> json = new ArrayList<>();
//		//scroller for the screen
//		for(int i = 0;i<level.size()-16;i++) {
//			
//		}
//		List<List<Integer>> screen = new ArrayList<>();
//	}
	private static void convertMegaManLevelToMMLV(List<List<Integer>> level) {
		// TODO Auto-generated method stub
		int xcoord = 0;
		int ycoord = 0;
		int levelNumber = 1;
		HashSet<Point> o = new HashSet<Point>();
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
				if(m==1) { //solid ground
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
				}else if (m == 5) { //moving platform

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

				}
//				else if (m==17) {
//					l=17;
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
//				2b0,448="0.000000"
		p.println("2b"+0+","+448+"=\"0.000000\"");

//				2b0,224="0.000000"
		p.println("2b"+0+","+224+"=\"0.000000\"");

//				2b0,0="0.000000"
		p.println("2b"+0+","+0+"=\"0.000000\"");

//				2a0,0="1.000000"
		p.println("2a"+0+","+0+"=\"1.000000\"");

//				1s="240.000000"
		p.println("1s=\"4000.000000\"");

//				1r="0.000000"
		p.println("1r=\"0.000000\"");

//				1q="272.000000"
		p.println("1q=\""+12800+"\""); //CHANGE TO POS INFINITY

//				1p="0.000000"
		p.println("1p=\"0.000000\"");

//				1m="9.000000"
		p.println("1m=\"9.000000\"");

//				1l="11.000000"
		p.println("1l=\"11.000000\"");

//				1k0="0.000000"
		p.println("1k0=\"0.000000\"");

//				1bc="0.000000"
		p.println("1bc=\"0.000000\"");

//				1f="-1.000000"
		p.println("1f=\"-1.000000\"");

//				1e="29.000000"
		p.println("1e=\"29.000000\"");

//				1d="6.000000"
		p.println("1d=\"6.000000\"");

//				1bb="0.000000"
		p.println("1bb=\"0.000000\"");

//				1ca="0.000000"
		p.println("1ca=\"0.000000\"");

//				1ba="0.000000"
		p.println("1ba=\"0.000000\"");

//				1c="1.000000"
		p.println("1c=\"1.000000\"");

//				1b="1.000000"
		p.println("1b=\"1.000000\"");

//				4b="64.000000"
		p.println("4b=\"64.000000\"");

//				4a="dakuchen"
		p.println("4a=\"dakuchen\""); //your user name

//				1a="Super Simple"
		//"MegaManLevel"+levelNumber+".txt"
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
		case 'L': //large health pack
			return 10;
		case 'l': //small health pack
			return 6; 
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
		default:
			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);

		}
	}
}
