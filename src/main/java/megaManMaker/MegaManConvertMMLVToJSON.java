package megaManMaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gvgai.tools.IO;

public class MegaManConvertMMLVToJSON {
	public static void main(String[] args) {
		int i = 1;
		List<List<Integer>> level = convertMMLVtoInt(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManLevel"+i+".mmlv");

	}
	
	public static List<List<Integer>> convertMMLVtoInt(String mmlvFile) {
		String[] level = new IO().readFile(mmlvFile);
		List<List<Integer>> complete = new ArrayList<>();
		//loops through levels to get characters and convert them 
		for(int y = 0; y < level.length; y++) { 
			System.out.println(level[y]);
			List<Integer> col = new ArrayList<>();//creates new List to be a new row of the JSON
			int tileCode = convertMMLVTilesToInt(level[y]);
			col.add(tileCode);
//			for(int x = 0; x < level[y].length(); x++) { //fills that array list that got added to create the row
//				if(level[y].charAt(x) != '[' || level[y].charAt(x) != ']') {
//					int tileCode = convertMMLVTilesToInt(level[y].charAt(x)); 
//					col.add(tileCode);
//				}
//			}
			complete.add(col); //adds a new array list to the list at index i 
		}
		return complete;
		
		
		
	}

	private static int convertMMLVTilesToInt(String string) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
