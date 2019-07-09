package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;

public class SimpleLoader implements LevelLoader{

	@Override
	public ArrayList<ArrayList<ArrayList<Integer>>> getLevels() {
		Scanner scanner;
		ArrayList<ArrayList<ArrayList<Integer>>> levels = new ArrayList<>();
		try {
			scanner = new Scanner(new File("data/VGLC/Zelda/n.txt"));
			String[] levelString = new String[11];
			int i = 0;
			while(scanner.hasNextLine())
				levelString[i++] = scanner.nextLine();
				
			List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
			levels.add(ZeldaLevelUtil.listToArrayList(levelInt));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return levels;
	}

}
