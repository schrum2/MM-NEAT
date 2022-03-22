package edu.southwestern.evolution.mapelites.generalmappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.util2D.ILocated2D;

public class LevelTraversalPathBinLabels extends BaseBinLabels {
	
	List<String> labels = null;
	private int verticalCoarseness;
	private int horizontalCoarseness;
	private int sizeX;
	private int sizeY;

	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			switch(GANProcess.type) {
				case MARIO:
					throw new UnsupportedOperationException("Mario does not work with LevelTraversalPathBinLabels.");
				case ZELDA:
					this.verticalCoarseness = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks");
					this.horizontalCoarseness = Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
					this.sizeX = 16; // Magic numbers for the size of a zelda room
					this.sizeY = 11; 
					break;
				case MEGA_MAN:
					throw new UnsupportedOperationException("MegaMan does not work with LevelTraversalPathBinLabels.");
				case LODE_RUNNER:
					throw new UnsupportedOperationException("Lode Runner does not work with LevelTraversalPathBinLabels.");
				default:
					throw new UnsupportedOperationException("Pick a game");
			}
			labels = new ArrayList<String>((int)Math.pow(2, verticalCoarseness*horizontalCoarseness));
			generateBinLabels(verticalCoarseness*horizontalCoarseness, "");
		}
		return labels;
	}
	
	private void generateBinLabels(int maxSize, String previous) {
		for (int i = 0; i < Math.pow(2, maxSize); i++) {
			String binString = padStringTo(maxSize, Integer.toBinaryString(i));
			labels.add(binString);
		}
	}
	
	private String padStringTo(int length, String s) {
		while (s.length() < length) {
			s = "0"+s;
		}
		return s;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		return multi[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		// Create the path from the Level Path that is reported
		HashSet<ILocated2D> path = new HashSet<>();
		if (keys.containsKey("Level Path")) {
			path = (HashSet<ILocated2D>) keys.get("Level Path");
		}
		// Map each room to an index in the visited tiles
		boolean[][] visitedTiles = new boolean[verticalCoarseness][horizontalCoarseness];
		int tileX;
		int tileY;
		// Traverse path and mark bins if visited
		for (ILocated2D location : path) {
			tileX = (int) location.getX() / sizeX;
			tileY = (int) location.getY() / sizeY;
			visitedTiles[tileX][tileY] = true;
		}
		// based on what bins were visited, assemble a bit string
		int locationSum = 0;
		for (int y = 0; y < verticalCoarseness; y++) {
			for (int x = 0; x < horizontalCoarseness; x++) {
				if (visitedTiles[verticalCoarseness-1-x][horizontalCoarseness-1-y]) {
					locationSum += Math.pow(2d, x+verticalCoarseness*y);
				}
			}
		}
		// then return the bitstring as the the single bin for this scheme
		return new int[] {locationSum};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Level Path"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {horizontalCoarseness * verticalCoarseness};
	}
	
//	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
//		//MMNEAT.main("".split(" "));
//		GANProcess.type = GAN_TYPE.ZELDA;
//		Parameters.initializeParameterCollections(args);
//		Parameters.parameters.setInteger("zeldaGANLevelWidthChunks", 4);
//		Parameters.parameters.setInteger("zeldaGANLevelHeightChunks", 4);
//		LevelTraversalPathBinLabels lablers = new LevelTraversalPathBinLabels();
//		lablers.binLabels();
//	}

}
