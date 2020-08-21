package icecreamyou.LodeRunner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Read, write, and interpret methods for level layout files.
 */
public class Layout {
	
	public static final String FILE_PATH = "src/main/java/icecreamyou/LodeRunner/";
	
	/**
	 * Reads in a layout file and returns an array of lines representing it.
	 * @param filename The name of the layout file to parse.
	 * @return An array of String representing lines in the layout file.
	 */
	public static String[] getLayoutAsArray(String filename) {
		String fileContents = "";
		try {
			BufferedReader r = new BufferedReader(new FileReader(FILE_PATH +filename));
			if (r.ready()) {
				String line = "";
				while (line != null) {
					line = r.readLine();
					fileContents += line +"\n";
				}
			}
			r.close();
			if (fileContents.equals(""))
				fileContents = "player:0,560,1"; //The base level with virtually nothing in it.
			return fileContents.split("\n");
		} catch (IOException e) {
			System.out.println("Error: reading the level file "+ filename +" did not complete.");
		}
		String[] z = {"player:0,560,1"}; //The base level with virtually nothing in it.
		return z;
	}
	
	/**
	 * Saves the Level level as the layout file filename.
	 */
	public static void save(Level level, String filename) {
		String contents = levelToString(level);
		
		// Enforce an invariant that enemies must be the last lines in the file.
		// This is necessary to drop enemies from their original position so that they never fall.
		for (WorldNode n : level.enemies)
			contents += n.toString() +"\n";
		
		// Actually write the file.
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(FILE_PATH + filename +".txt"));
			file.write(contents);
			file.close();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static String levelToString(Level level) {
		String contents = "";
		if (level.getNextLevel() != null)
			contents += "nextLevel:"+ level.getNextLevel();
		for (WorldNode n : level.bars)
			contents += n.toString() +"\n";
		for (Gate g : level.gates)
			contents += g.toString() +"\n";
		for (WorldNode n : level.holes)
			contents += n.toString() +"\n";
		for (WorldNode n : level.ladders)
			contents += n.toString() +"\n";
		for (WorldNode n : level.pickups)
			contents += n.toString() +"\n";
		for (WorldNode n : level.solids)
			contents += n.toString() +"\n";
		for(WorldNode n : level.enemies) 
			contents += n.toString() + "\n";
		if (level.portal != null)
			contents += level.portal.toString() +"\n";
		if (level.player1 != null)
			contents += level.player1.toString() +"\n";
		if (level.player2 != null)
			contents += level.player1.toString() +"\n";
		return contents;
	}

}
