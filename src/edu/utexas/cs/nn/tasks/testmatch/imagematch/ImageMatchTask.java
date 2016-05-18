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
            BufferedImage child = imageFromCPPN(n, imageWidth, imageHeight);

            //draws picture and network to JFrame
            DrawingPanel parentPanel = drawImage(img, "target");
            DrawingPanel childPanel = drawImage(child, "output");
            childPanel.setLocation(imageWidth + IMAGE_PLACEMENT, 0);

            considerSavingImage(childPanel);
            //MiscUtil.waitForReadStringAndEnterKeyPress();

            parentPanel.dispose();
            childPanel.dispose();
        }
        return super.evaluate(individual);//if watch=false
    }

    public static void considerSavingImage(DrawingPanel panel) {
        Scanner scan = new Scanner(System.in);//Scanner allows picture and network to be saved as bmp
        System.out.println("Save image? y/n");
        if (scan.next().equals("y")) {
            System.out.println("enter filename");
            String filename = scan.next();
            panel.save(filename + ".bmp");
        }
        scan.close();
    }

    public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight) {
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < imageWidth; x++) {//scans across whole image
            for (int y = 0; y < imageHeight; y++) {
                double[] hsb = getHSBFromCPPN(n, x, y, imageWidth, imageHeight);
                //network outputs computed on hsb, not rgb scale because creates better images
                Color childColor = Color.getHSBColor((float) hsb[HUE_INDEX], (float) Math.max(0, Math.min(hsb[SATURATION_INDEX], 1)), (float) Math.abs(hsb[BRIGHTNESS_INDEX]));
                image.setRGB(x, y, childColor.getRGB());//set back to RGB to draw picture to JFrame
            }
        }
        return image;
    }

    public static double[] getHSBFromCPPN(Network n, int x, int y, int imageWidth, int imageHeight) {
        double[] input = getCPPNInputs(x, y, imageWidth, imageHeight);
        return n.process(input);
    }

    public static double[] getCPPNInputs(int x, int y, int imageWidth, int imageHeight) {
        ILocated2D scaled = scale(new Tuple2D(x, y), imageWidth, imageHeight);
        return new double[]{scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * SQRT2, BIAS};
    }

    public static DrawingPanel drawImage(BufferedImage image, String label, int imageWidth, int imageHeight) {
        DrawingPanel parentPanel = new DrawingPanel(imageWidth, imageHeight, label);
        Graphics2D parentGraphics = parentPanel.getGraphics();
        parentGraphics.drawRenderedImage(image, null);
        return parentPanel;
    }

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
     * Scales X-Y coordinates to where origin is at center of picture, not top
     * left corner. Also scales to range [-1,1] in each dimension.
     *
     * @param toScale (x,y) coordinates as a tuple
     * @param imageWidth width of image
     * @param imageHeight height of image
     * @return new tuple with scaled coordinates
     */
    public static Tuple2D scale(Tuple2D toScale, int imageWidth, int imageHeight) {
        double newX = scale(toScale.x, imageWidth);
        double newY = scale(toScale.y, imageHeight);
        return new Tuple2D(newX, newY);
    }

    /**
     * Scales either x or y coordinate to where origin is at center of picture,
     * not top left corner
     *
     * @param toScale coordinate to be scaled
     * @param imageSize either imageHeight or imageWidth, depending on whether
     * toScale is x or y coordinate
     *
     * @return scaled coordinate
     */
    public static double scale(double toScale, int imageSize) {
        return ((toScale / (imageSize - 1)) * 2) - 1;
    }

    /**
     * main method used to create a random CPPN image.
     * @param args
     */
    public static void main(String[] args) {
        randomCPPNimage();
    }

    /**
     * Creates a random image of given size and numMutations and puts it in
     * JFrame with option to save image and network as a bmp
     */
    public static void randomCPPNimage() {

        MMNEAT.clearClasses();
        EvolutionaryHistory.setInnovation(0);
        EvolutionaryHistory.setHighestGenotypeId(0);
        Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false", "includeHalfLinearPiecewiseFunction:true", "includeSawtoothFunction:true"});
        MMNEAT.loadClasses();

        final int NUM_MUTATIONS = 200;
        final int SIZE = 500;

        TWEANNGenotype toDraw = new TWEANNGenotype(4, 3, false, 0, 1, 0);
        for (int i = 0; i < NUM_MUTATIONS; i++) {
            toDraw.mutate();
        }
        TWEANN n = toDraw.getPhenotype();
        BufferedImage child = imageFromCPPN(n, SIZE, SIZE);

        System.out.println(n.toString());

        DrawingPanel network = new DrawingPanel(SIZE, SIZE, "network");
        n.draw(network);
        DrawingPanel childPanel = drawImage(child, "output", SIZE, SIZE);

        Scanner scan = new Scanner(System.in);
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
        scan.close();
        network.dispose();
        childPanel.dispose();
        System.exit(0);
    }

}
