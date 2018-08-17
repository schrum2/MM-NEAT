package edu.southwestern.tasks.interactive.picbreeder;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveNetworkEvolutionTask;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * Implementation of picbreeder that extends InteractiveNetworkEvolutionTask
 * and uses Java Swing components for graphical interface
 * 
 * Original Picbreeder paper: 
 * Jimmy Secretan, Nicholas Beato, David B. D'Ambrosio, Adelein Rodriguez, Adam Campbell, 
 * Jeremiah T. Folsom-Kovarik and Kenneth O. Stanley. Picbreeder: A Case Study in Collaborative 
 * Evolutionary Exploration of Design Space. Evolutionary Computation 19, 3 (2011), 373-403. 
 * DOI: http://dx.doi.org/10.1162/evco_a_00030
 * 
 * @author Lauren Gillespie
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public class PicbreederTask<T extends Network> extends InteractiveNetworkEvolutionTask<T> {

	public static final int CPPN_NUM_INPUTS	= 4;
	public static final int CPPN_NUM_OUTPUTS = 3;

	/** 
	 * Default constructor
	 * 
	 * @throws IllegalAccessException
	 */
	public PicbreederTask() throws IllegalAccessException {
		super();
	}

	/**
	 * X and Y input labels, distance from center is useful for radial distance,
	 * bias is required for all neural networks.
	 */
	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias" };
	}

	/**
	 * Hue, saturation, and brightness values being output by CPPN
	 */
	@Override
	public String[] outputLabels() {
		return new String[] { "hue-value", "saturation-value", "brightness-value" };
	}

	/**
	 * Window title
	 */
	@Override
	protected String getWindowTitle() {
		return "Picbreeder";
	}

	/**
	 * Create BufferedImage from CPPN
	 */
	@Override
	protected BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers) {
		return GraphicsUtil.imageFromCPPN(phenotype, width, height, inputMultipliers);
	}

	/**
	 * No additional behavior of click other than initial response is used
	 */
	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		// Do nothing
	}

	/**
	 * Save generated images from CPPN
	 */
	@Override
	protected void save(String filename, int i) {
		// Use of imageHeight and imageWidth allows saving a higher quality image than is on the button
		BufferedImage toSave = GraphicsUtil.imageFromCPPN((Network)scores.get(i).individual.getPhenotype(), Parameters.parameters.integerParameter("imageWidth"), Parameters.parameters.integerParameter("imageHeight"), inputMultipliers);
		filename += ".bmp";
		GraphicsUtil.saveImage(toSave, filename);
		System.out.println("image " + filename + " was saved successfully");
	}

    /**
     * Returns the number of inputs used in the interactive evolution task
     */
	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	/**
	 * Returns the number of outputs used in the interactive evolution task
	 */
	@Override
	public int numCPPNOutputs() {
		return CPPN_NUM_OUTPUTS;
	}

	/**
	 * For quick testing
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.picbreeder.PicbreederTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:false","includeSquareWaveFunction:false","includeFullSawtoothFunction:false","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns type of file being saved (BMP)
	 */
	@Override
	protected String getFileType() {
		return "BMP Images";
	}

	/**
	 * Returns extension of saved images (.bmp)
	 */
	@Override
	protected String getFileExtension() {
		return "bmp";
	}
}
