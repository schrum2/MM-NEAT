package megaManMaker;

import java.util.ArrayList;
import java.util.List;

import gvgai.tools.IO;

public class MegaManVGLCUtil {
	public static final String MEGAMAN_LEVEL_PATH = "data/VGLC/MegaMan/Processed/";
	
	public static void main(String[] args) {
		List<List<Integer>> level = convertMegamanVGLCtoListOfLists(MEGAMAN_LEVEL_PATH+"megaman_1_1.txt");
		for(List<Integer> k : level) {
			for(Integer m: k) {
				System.out.print(m);

			}
			System.out.println();
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
			return 5;
		case 'l': //small health pack
			return 6; 
		case 'W': //large ammo pack 
			return 7; 
		case 'w': //small ammo pack
			return 8;
		case '+': //extra life
			return 9;
		case 'M': //Moving platform
			return 10;
		case 'P': //Moving platform
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
