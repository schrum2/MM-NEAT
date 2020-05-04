package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;

public class SimpleLoader implements LevelLoader{

	@Override
	public List<List<List<Integer>>> getLevels() {
		Scanner scanner;
		List<List<List<Integer>>> levels = new ArrayList<>();
		try {
			scanner = new Scanner(new File("data/VGLC/Zelda/n.txt"));
			String[] levelString = new String[11];
			int i = 0;
			while(scanner.hasNextLine())
				levelString[i++] = scanner.nextLine();
				
			List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
			levels.add(levelInt);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return levels;
	}

}
