package edu.southwestern.tasks.loderunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ConvertVGLCtoIceCreamYou {
	
	public static final String ICE_CREAM_YOU_PATH = "src/main/java/icecreamyou/LodeRunner/";

	public static void main(String[] args) throws FileNotFoundException {
		
		for(int i = 1; i <=150; i++) {
			String file = "Level " + i + ".txt";
			String level = LodeRunnerVGLCUtil.convertLodeRunnerVGLCtoIceCreamYou(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH +file);
			PrintStream ps = new PrintStream(new File(ICE_CREAM_YOU_PATH+"LEVEL"+i+"-level.txt"));
			ps.print(level);
			ps.close();
		}
		

	}

}
