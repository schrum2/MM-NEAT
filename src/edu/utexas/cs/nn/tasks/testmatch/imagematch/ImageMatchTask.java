package edu.utexas.cs.nn.tasks.testmatch.imagematch;


import java.awt.Color;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.testmatch.MatchDataTask;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *Image match training task for a CPPN
 * 
 * @author gillespl
 */
public class ImageMatchTask<T extends Network> extends MatchDataTask<T> {

	public static final String IMAGE_MATCH_PATH = "data\\imagematch";
	
	//this variable needed to scale RGB values to a 0-1 range
	private final double MAX_COLOR_INTENSITY = 255.0;
	private BufferedImage img = null;
	int imageHeight, imageWidth;

	/**
	 * Default task constructor
	 */
	public ImageMatchTask() {
		this(Parameters.parameters.stringParameter("matchImageFile"));
	}
	/**
	 * Constructor for ImageMatchTask
	 * 
	 * @param filename name of file pathway for image
	 */
	public ImageMatchTask(String filename) {
		try {//throws and exception if filename is not valid
		    img = ImageIO.read(new File(IMAGE_MATCH_PATH + "\\" + filename));
		} catch (IOException e) {
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
	}
	
	/**
	 * Returns labels for input
	 */
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-coordinate", "Y-coordinate"};
	}

	/**
	 * Returns labels for output
	 */
	@Override
	public String[] outputLabels() {
        return new String[]{"r-value", "g-vaule", "b-value"};
	}

	@Override
	public int numInputs() {
		return 2;
	}

	@Override
	public int numOutputs() {
		return 3;
	}

	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
        ArrayList<Pair<double[], double[]>> pairs = new ArrayList<Pair<double[], double[]>>();
		for(int i = 0; i < imageWidth; i++) {
			for(int j = 0; j < imageHeight; j++) {
				Color color = new Color(img.getRGB(i, j));
				pairs.add(new Pair<double[], double[]>(new double[]{i, j}, new double[]
				{color.getRed()/MAX_COLOR_INTENSITY, color.getGreen()/MAX_COLOR_INTENSITY, color.getBlue()/MAX_COLOR_INTENSITY}));
			}
		}
		return pairs;
	}

}
