package edu.utexas.cs.nn.tasks.testmatch.imagematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.*;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.testmatch.MatchDataTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *Image match training task for a CPPN
 * 
 * @author gillespl
 */
public class ImageMatchTask<T extends Network> extends MatchDataTask<T> {

	public static final String IMAGE_MATCH_PATH = "data\\imagematch";
	private static final int IMAGE_PLACEMENT = 200;
	private static final int X_COORDINATE_INDEX = 0;
	private static final int Y_COORDINATE_INDEX = 1;
	private static final int DISTANCE_COORDINATE_IND = 2;
	private static final int hIndex = 0;
	private static final int sIndex = 1;
	private static final int bIndex = 2;
	private static final double BIAS = 1.0;//a common input used in CPPNs
	private static final double SQRT2 = Math.sqrt(2);
	private BufferedImage img = null;
	public int imageHeight, imageWidth;

	/**
	 * Default task constructor
	 */
	public ImageMatchTask() {
		this(Parameters.parameters.stringParameter("matchImageFile"));
		MatchDataTask.pauseForEachCase = false;
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
			System.out.println("Could not load image: " + filename);
			System.exit(1);
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
	}

	/**
	 * Evaluate method. If watch is true, puts the image and network into a JFrame with
	 * option to save both as a bmp. Else, the super evaluate method is called.
	 */
	public Score<T> evaluate(Genotype<T> individual) {
		if(CommonConstants.watch) {
			Network n = individual.getPhenotype();
			double[] hsb = null;
			BufferedImage child = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			for(int x = 0; x < imageWidth; x++) {//scans across whole image
				for(int y = 0; y < imageHeight; y++) {
					double[] input = {x, y, 0, BIAS};
					ImageMatchTask.scale(input, imageWidth, imageHeight);//properly scales the coordinates
					ILocated2D distance = new Tuple2D(input[X_COORDINATE_INDEX], input[Y_COORDINATE_INDEX]);
					input[DISTANCE_COORDINATE_IND] = distance.distance(new Tuple2D(0,0)) * SQRT2;//computes euclidian distance
					hsb = n.process(input);
					//network outputs computed on hsb, not rgb scale because creates better images
					Color childColor = Color.getHSBColor((float) hsb[hIndex], (float) Math.max(0,Math.min(hsb[sIndex],1)), (float) Math.abs(hsb[bIndex]));
					child.setRGB(x, y, childColor.getRGB());//set back to RGB to draw picture to JFrame
				}
			}

			//draws picture and network to JFrame
			DrawingPanel parentPanel = new DrawingPanel(imageWidth, imageHeight, "target");
			DrawingPanel childPanel = new DrawingPanel(imageWidth, imageHeight, "output");
			childPanel.setLocation(imageWidth + IMAGE_PLACEMENT, 0);
			Graphics2D parentGraphics = parentPanel.getGraphics();
			Graphics2D childGraphics = childPanel.getGraphics();

			parentGraphics.drawRenderedImage(img, null);
			childGraphics.drawRenderedImage(child, null);
			Scanner scan = new Scanner(System.in);//Scanner allows picture and network to be saved as bmp
			System.out.println("Save image? y/n");
			if(scan.next().equals("y")) {
				System.out.println("enter filename");
				String filename = scan.next();
				childPanel.save(filename + ".bmp");
			}
			scan.close();
			parentPanel.dispose();
			childPanel.dispose();
		}
		return super.evaluate(individual);//if watch=false
	}

	/**
	 * Returns labels for input
	 */
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-coordinate", "Y-coordinate", "distance from center", "bias"};
	}

	/**
	 * Returns labels for output
	 */
	@Override
	public String[] outputLabels() {
		return new String[]{"hue-value", "saturation-value", "brightness-value"};
	}

	/**
	 * Returns number of inputs to network
	 */
	@Override
	public int numInputs() {
		return 4;
	}

	/**
	 * Returns number of outputs from network
	 */
	@Override
	public int numOutputs() {
		return 3;
	}

	/**
	 * Gets the expected input and output from picture for network to compare against
	 * 
	 * @return an ArrayList of all the pairs of expected inputs and outputs
	 */
	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
		ArrayList<Pair<double[], double[]>> pairs = new ArrayList<Pair<double[], double[]>>();
		for(int i = 0; i < imageWidth; i++) {
			for(int j = 0; j < imageHeight; j++) {
				Color color = new Color(img.getRGB(i, j));
				float[] hsb = new float[numOutputs()];
				ILocated2D distance = new Tuple2D(i, j);
				Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
				pairs.add(new Pair<double[], double[]>(new double[]{ImageMatchTask.scale(i, imageWidth), 
						ImageMatchTask.scale(j, imageHeight), distance.distance(new Tuple2D(0,0)) * Math.sqrt(2), BIAS},
						new double[] {hsb[hIndex], hsb[sIndex], hsb[bIndex]}));
			}
		}
		return pairs;
	}

	/**
	 * Scales X-Y coordinates to where origin is at center of picture, not top left corner
	 * 
	 * @param toScale double array of x and y coordinate, with x-coordinate at[0] and y at [1]
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 */
	public static void scale(double[] toScale, int imageWidth, int imageHeight) {
		if(toScale[X_COORDINATE_INDEX] == 0 && toScale[Y_COORDINATE_INDEX] == 0) {
			toScale[X_COORDINATE_INDEX] = -1;
			toScale[Y_COORDINATE_INDEX] = -1;
		} else if(toScale[X_COORDINATE_INDEX] == 0 && toScale[Y_COORDINATE_INDEX] != 0){
			toScale[X_COORDINATE_INDEX] = -1;
			toScale[Y_COORDINATE_INDEX] = (toScale[Y_COORDINATE_INDEX]/imageHeight)*2 - 1;
		} else if(toScale[Y_COORDINATE_INDEX] == 0 && toScale[X_COORDINATE_INDEX] != 0){
			toScale[Y_COORDINATE_INDEX]= -1;
			toScale[X_COORDINATE_INDEX] = (toScale[X_COORDINATE_INDEX]/imageWidth)*2 - 1;
		} else {
		toScale[X_COORDINATE_INDEX] = (toScale[X_COORDINATE_INDEX]/imageWidth)*2 - 1;
		toScale[Y_COORDINATE_INDEX] = (toScale[Y_COORDINATE_INDEX]/imageHeight)*2 - 1;
		}
	}
	/**
	 * Scales either x or y coordinate to where origin is at center of picture, not top left corner
	 * @param toScale coordinate to be scaled
	 * @param imageSize either imageHeight or imageWidth, depending on whether toScale is x or y coordinate
	 * 
	 * @return scaled coordinate
	 */
	public static int scale(int toScale, int imageSize) {
		return (toScale/imageSize)*2 - 1;
	}
	
	/**
	 * main method used to create a random CPPN image.
	 */
	public static void main(String[] args) {
		randomCPPNimage();	
	}
	
	/**
	 * Creates a random image of given size and numMutations and puts it in JFrame
	 *  with option to save image and network as a bmp
	 */
	public static void randomCPPNimage() {

		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false", "includeHalfLinearPiecewiseFunction:true", "includeSawtoothFunction:true"});
		MMNEAT.loadClasses();

		final int NUM_MUTATIONS = 200;
		final int SIZE = 500;
		int hIndex = 0;
		int sIndex = 1;
		int bIndex = 2;
		int color = BufferedImage.TYPE_INT_RGB;

		TWEANNGenotype toDraw = new TWEANNGenotype(4, 3, false, 0, 1, 0);
		for(int i = 0; i < NUM_MUTATIONS; i++) {	
			toDraw.mutate();
		}
		TWEANN n = toDraw.getPhenotype();
		BufferedImage child = new BufferedImage(SIZE, SIZE, color);
		double[] hsb = null;
		for(int x = 0; x < SIZE ; x++) {
			for(int y = 0; y < SIZE; y++) {
				double[] input = {x,y,0,1};
				scale(input, SIZE, SIZE);
				ILocated2D distance = new Tuple2D(input[0], input[1]);
				input[2] = distance.distance(new Tuple2D(0,0)) * Math.sqrt(2);
				hsb = n.process(input);
				if(hsb[hIndex] < 0 && hsb[hIndex] > 1|| hsb[sIndex] < 0 && hsb[sIndex] > 1 || hsb[bIndex] < 0 && hsb[bIndex] > 1){
					System.out.println("failed check");
					break;
				}else{
					Color childColor = Color.getHSBColor((float) hsb[hIndex], (float) Math.max(0,Math.min(hsb[sIndex],1)), (float) Math.abs(hsb[bIndex]));
					child.setRGB(x, y, childColor.getRGB());
				}
			}
		}
		System.out.println(Arrays.toString(hsb));
		System.out.println(n.toString());

		DrawingPanel network = new DrawingPanel(SIZE, SIZE, "network");
		n.draw(network);
		DrawingPanel childPanel = new DrawingPanel(SIZE, SIZE, "output");
		Graphics2D childGraphics = childPanel.getGraphics();
		childGraphics.drawRenderedImage(child, null);

		Scanner scan = new Scanner(System.in);
		System.out.println("would you like to save this image? y/n");
		if(scan.next().equals("y")) {
			System.out.println("enter filename");
			String filename = scan.next();
			childPanel.save(filename + ".bmp");
			System.out.println("save network? y/n");
			if(scan.next().equals("y")) {
				network.save(filename + "network.bmp");
			}
		}
		scan.close();
	}

}

