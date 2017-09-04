package edu.southwestern.log;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Jacob
 */
public class DeathLocationsLog extends MMNEATLog {

        public static final int MAX_COLOR = 255;
    
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> intensities = null;
	private HashMap<Integer, ArrayList<Integer>> sortedKeys = null;

	public DeathLocationsLog() {
		super("DeathLocations", false, true);
	}

	/**
	 * Returns (maze, (location, death count)) hash
	 *
	 * @return
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> deathCount() {
		HashMap<Integer, HashMap<Integer, Integer>> result = new HashMap<Integer, HashMap<Integer, Integer>>();
		Scanner read;
		try {
			read = new Scanner(getFile());
		} catch (FileNotFoundException ex) {
			return result; // New empty grid
		}
		while (read.hasNextLine()) {
			String line = read.nextLine();
			// System.out.println("Read: " + line);
			Scanner pair = new Scanner(line);
			pair.useDelimiter(":");
			if (!pair.hasNext()) {
				System.out.println("Death Location Log read error on line: " + line);
				continue;
			}
			int maze = pair.nextInt();
			// System.out.print("maze:" + maze);
			if (!pair.hasNext()) {
				System.out.println("Death Location Log read error on line: " + line);
				continue;
			}
			int location = pair.nextInt();
			// System.out.println(":location:" + location);
			if (!result.containsKey(maze)) {
				result.put(maze, new HashMap<Integer, Integer>());
			}
			HashMap<Integer, Integer> mazeHash = result.get(maze);
			if (!mazeHash.containsKey(location)) {
				mazeHash.put(location, 0);
			}
			mazeHash.put(location, mazeHash.get(location) + 1);
			pair.close();
		}
		read.close();
		// System.out.println("Return death count");
		return result;
	}

	/**
	 * Given (location, death count) hash for single maze, return (intensity,
	 * locations) hash for each possible intensity
	 *
	 * @param deaths
	 *            (location, death count) hash
	 * @return
	 */
	public HashMap<Integer, ArrayList<Integer>> intensityGroups(HashMap<Integer, Integer> deaths) {
		HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
		for (Integer location : deaths.keySet()) {
			int count = deaths.get(location);
			if (!result.containsKey(count)) {
				result.put(count, new ArrayList<Integer>());
			}
			result.get(count).add(location);
		}
		return result;
	}

	/**
	 * Locations where more deaths have occurred will be a stronger red
	 *
	 * @param g
	 */
	public void heatMap(GameFacade g) {
		if (intensities == null) {
			HashMap<Integer, HashMap<Integer, Integer>> deaths = deathCount();
			intensities = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
			sortedKeys = new HashMap<Integer, ArrayList<Integer>>();
			for (Integer maze : deaths.keySet()) {
				// System.out.print("Maze: " + maze);
				HashMap<Integer, ArrayList<Integer>> iGroup = intensityGroups(deaths.get(maze));
				intensities.put(maze, iGroup);
				ArrayList<Integer> keys = Collections.list(Collections.enumeration(iGroup.keySet()));
				Collections.sort(keys);
				sortedKeys.put(maze, keys);
			}
		}
		HashMap<Integer, ArrayList<Integer>> mazeIntensities = intensities.get(g.getMazeIndex());
		ArrayList<Integer> keys = sortedKeys.get(g.getMazeIndex());
		if (keys != null) {
			for (Integer intensity : keys) {
				// System.out.println("Intensity: " + intensity);
				ArrayList<Integer> locations = mazeIntensities.get(intensity);
				g.addPoints(new Color(Math.min(MAX_COLOR, intensity), Math.min(MAX_COLOR, Math.max(intensity - MAX_COLOR, 0)),
						Math.min(MAX_COLOR, Math.max(intensity - (2 * MAX_COLOR), 0))), locations);
			}
		}
	}
}
