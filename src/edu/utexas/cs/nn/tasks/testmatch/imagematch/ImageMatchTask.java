package edu.utexas.cs.nn.tasks.testmatch.imagematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.*;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.testmatch.MatchDataTask;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 * Image match training task for a CPPN
 *
 * @author gillespl
 * @param <T> Phenotype must be a Network (Should be a CPPN)
 */
public class ImageMatchTask<T extends Network> extends MatchDataTask<T> {

	public static final String IMAGE_MATCH_PATH = "data\\imagematch";
	private static final int IMAGE_PLACEMENT = 200;
	private static final int HUE_INDEX = 0;
	private static final int SATURATION_INDEX = 1;
	private static final int BRIGHTNESS_INDEX = 2;
	private static final double BIAS = 1.0;//a common input used in neural networks
	private static final double SQRT2 = Math.sqrt(2); //Used for scaling distance from center

	private Network individual;
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
	 * Evaluate method. If watch is true, puts the image and network into a
	 * JFrame with option to save both as a bmp. Else, the super evaluate method
	 * is called.
	 *
	 * @return MSE comparison between image and CPPN outputs
	 */
	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		if (CommonConstants.watch) {
			Network n = individual.getPhenotype();
			BufferedImage child;
                        int drawWidth = imageWidth;
                        int drawHeight = imageHeight;
			if(Parameters.parameters.booleanParameter("overrideImageSize")) {
                            drawWidth = Parameters.parameters.integerParameter("imageWidth");
                            drawHeight = Parameters.parameters.integerParameter("imageHeight");
			} 
			child = imageFromCPPN(n, drawWidth, drawHeight);
			//draws picture and network to JFrame
			DrawingPanel parentPanel = drawImage(img, "target");
			DrawingPanel childPanel = drawImage(child, "output", drawWidth, drawHeight);
			childPanel.setLocation(img.getWidth() + IMAGE_PLACEMENT, 0);
			considerSavingImage(childPanel);
			parentPanel.dispose();
			childPanel.dispose();
		}
		// Too many outputs to print to console. Don't want to watch.
		boolean temp = CommonConstants.watch;
		CommonConstants.watch = false; // Prevent watching of console showing error energy
		Score<T> result = super.evaluate(individual);//if watch=false
		CommonConstants.watch = temp;
		this.individual = individual.getPhenotype();
		result.behaviorVector = getBehaviorVector();
		return result;
	}

	/**
	 * Allows image and network to be saved as a bmp if user chooses to do so
	 *
	 * @param panel the drawing panel containing the image to be saved
	 */
	public static void considerSavingImage(DrawingPanel panel) {
		System.out.println("Save image? y/n");
		String input = MiscUtil.CONSOLE.next();
		if (input.equals("y")) {
			System.out.println("enter filename");
			String filename = MiscUtil.CONSOLE.next();
			panel.save(filename + ".bmp");//allows user to choose a unique name for file and to not worry about adding '.bmp'
		}
	}

	/**
	 * Draws the image created by the CPPN to a BufferedImage
	 *
	 * @param n the network used to process the imag
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @return buffered image containing image drawn by network
	 */
	public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight) {
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {//scans across whole image
			for (int y = 0; y < imageHeight; y++) {
				float[] hsb = getHSBFromCPPN(n, x, y, imageWidth, imageHeight);
				//network outputs computed on hsb, not rgb scale because creates better images
				Color childColor = Color.getHSBColor(hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX]);
				image.setRGB(x, y, childColor.getRGB());//set back to RGB to draw picture to JFrame
			}
		}
		return image;
	}

	/**
	 * Given the direct HSB values from the CPPN (a double array),
	 * convert to a float array (required by Color methods) and do
	 * range restriction on certain values.
	 * 
	 * These range restrictions were stolen from Picbreeder code 
	 * on GitHub (though not the original code), but 2 in 13 randomly
	 * mutated networks still produce boring black screens.
	 * Is there a way to fix this?
	 * 
	 * @param hsb array of HSB color information from CPPN
	 * @return scaled HSB information in float array
	 */
	public static float[] rangeRestrictHSB(double[] hsb) {
		return new float[]{
				(float) hsb[HUE_INDEX], 
				//(float) hsb[SATURATION_INDEX], 
				(float) ActivationFunctions.halfLinear(hsb[SATURATION_INDEX]), 
				//(float) hsb[BRIGHTNESS_INDEX]};
				(float) Math.abs(hsb[BRIGHTNESS_INDEX])};
	}

	/**
	 * Gets HSB outputs from the CPPN in question
	 *
	 * @param n the CPPN
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 *
	 * @return double containing the HSB values
	 */
	public static float[] getHSBFromCPPN(Network n, int x, int y, int imageWidth, int imageHeight) {
		double[] input = getCPPNInputs(x, y, imageWidth, imageHeight);
		((TWEANN) n).flush();
		return rangeRestrictHSB(n.process(input));
	}

	/**
	 * Gets scaled inputs to send to CPPN
	 *
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 *
	 * @return array containing inputs for CPPN
	 */
	public static double[] getCPPNInputs(int x, int y, int imageWidth, int imageHeight) {
		ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), imageWidth, imageHeight);
		return new double[]{scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * SQRT2, BIAS};
	}

	/**
	 * method for drawing an image onto a drawing panel
	 *
	 * @param image image to draw
	 * @param label name of image
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 *
	 * @return the drawing panel with the image
	 */
	public static DrawingPanel drawImage(BufferedImage image, String label, int imageWidth, int imageHeight) {
		DrawingPanel parentPanel = new DrawingPanel(imageWidth, imageHeight, label);
		Graphics2D parentGraphics = parentPanel.getGraphics();
		parentGraphics.drawRenderedImage(image, null);
		return parentPanel;
	}

	/**
	 * public method for drawing an image onto a drawing panel
	 *
	 * @param image image to draw
	 * @param label label name of image
	 *
	 * @return drawing panel with image
	 */
	public DrawingPanel drawImage(BufferedImage image, String label) {
		return drawImage(image, label, imageWidth, imageHeight);
	}

	/**
	 * Returns labels for input
	 *
	 * @return List of CPPN outputs
	 */
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-coordinate", "Y-coordinate", "distance from center", "bias"};
	}

	/**
	 * Returns labels for output
	 *
	 * @return list of CPPN outputs
	 */
	@Override
	public String[] outputLabels() {
		return new String[]{"hue-value", "saturation-value", "brightness-value"};
	}

	/**
	 * Returns number of inputs to network
	 *
	 * @return Number of inputs: X, Y, distance from center, Bias
	 */
	@Override
	public int numInputs() {
		return 4;
	}

	/**
	 * Returns number of outputs from network
	 *
	 * @return Number of outputs: Hue, Saturation, and Brightness
	 */
	@Override
	public int numOutputs() {
		return 3;
	}

	/**
	 * Gets the expected input and output from picture for network to compare
	 * against
	 *
	 * @return an ArrayList of all the pairs of expected inputs and outputs
	 */
	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
		ArrayList<Pair<double[], double[]>> pairs = new ArrayList<Pair<double[], double[]>>();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				Color color = new Color(img.getRGB(x, y));
				float[] hsb = new float[numOutputs()];
				Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
				pairs.add(new Pair<double[], double[]>(
						getCPPNInputs(x, y, imageWidth, imageHeight),
						new double[]{hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX]}));
			}
		}
		return pairs;
	}
	
	/**
	 * 
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		ArrayList<Double> results = new ArrayList<Double>(img.getHeight()*img.getWidth());
		BufferedImage child = imageFromCPPN(individual, img.getWidth(), img.getHeight());
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j =0; j < img.getHeight(); j++) {
				Color color = new Color(child.getRGB(i, j));
				float[] hsb = new float[numOutputs()];
				Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
				for(int k = 0; k < hsb.length; k++) {
					results.add((double) hsb[k]); 
				}
			}
		}
		
        return results;
    } 
	/**
	 * main method used to create a random CPPN image.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
                MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "allowMultipleFunctions:true", "netChangeActivationRate:0.4", "recurrency:false"});
		MMNEAT.loadClasses();
            
		//draw8RandomImages();
		testImageConsistency();
	}

	public static void draw8RandomImages() {
		randomCPPNimage(true, 400);
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 3; j++) {
				randomCPPNimage(false, i*300, j*300, 100);
			}
		}
	}

	public static DrawingPanel randomCPPNimage(boolean offerToSave, int size) {
		return randomCPPNimage(offerToSave, 0, 0, size);
	}

	public static void testImageConsistency() {
                final int NUM_MUTATIONS = 200;

		TWEANNGenotype toDraw = new TWEANNGenotype(4, 3, false, 0, 1, 0);
		for (int i = 0; i < NUM_MUTATIONS; i++) {
			toDraw.mutate();
		}
		TWEANN n = toDraw.getPhenotype();
                int SMALL = 100;
		BufferedImage small = imageFromCPPN(n, SMALL, SMALL);
		DrawingPanel smallPanel = drawImage(small, "small", SMALL, SMALL);
				smallPanel.setLocation(0,0);
                int MEDIUM = 300;
		BufferedImage medium = imageFromCPPN(n, MEDIUM, MEDIUM);
		DrawingPanel mediumPanel = drawImage(medium, "medium", MEDIUM, MEDIUM);
                mediumPanel.setLocation(SMALL, 0);	

                int LARGE = 600;
		BufferedImage large = imageFromCPPN(n, LARGE, LARGE);
		DrawingPanel largePanel = drawImage(large, "large", LARGE, LARGE);
                largePanel.setLocation(SMALL + MEDIUM, 0);	
        }
	/**
	 * Creates a random image of given size and numMutations and puts it in
	 * JFrame with option to save image and network as a bmp
	 *
	 * @param offerToSave Whether to pause and ask to save image
	 * @param x x-coordinate to place image window
	 * @param y y-coordinate to place image window
	 * @return panel on which image was drawn
	 */
	public static DrawingPanel randomCPPNimage(boolean offerToSave, int x, int y, int size) {

		final int NUM_MUTATIONS = 200;

		TWEANNGenotype toDraw = new TWEANNGenotype(4, 3, false, 0, 1, 0);
		for (int i = 0; i < NUM_MUTATIONS; i++) {
			toDraw.mutate();
		}
		TWEANN n = toDraw.getPhenotype();
		BufferedImage child = imageFromCPPN(n, size, size);

		System.out.println(n.toString());

		DrawingPanel childPanel = drawImage(child, "output", size, size);
		childPanel.setLocation(x, y);

		if (offerToSave) {
			DrawingPanel network = new DrawingPanel(size, size, "network");
			n.draw(network);
			network.setLocation(300, 0);
			Scanner scan = MiscUtil.CONSOLE;
			System.out.println("would you like to save this image? y/n");
			if (scan.next().equals("y")) {
				System.out.println("enter filename");
				String filename = scan.next();
				childPanel.save(filename + ".bmp");
				System.out.println("save network? y/n");
				if (scan.next().equals("y")) {
					network.save(filename + "network.bmp");
				}
			}
			network.dispose();
			childPanel.dispose();
			System.exit(0);
		}
		return childPanel;
	}
}
