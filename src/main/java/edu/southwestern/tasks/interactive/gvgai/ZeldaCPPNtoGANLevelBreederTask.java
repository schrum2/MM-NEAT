package edu.southwestern.tasks.interactive.gvgai;

import java.awt.image.BufferedImage;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;

public class ZeldaCPPNtoGANLevelBreederTask extends InteractiveEvolutionTask<TWEANN> {

	private String[] outputLabels;

	
	public ZeldaCPPNtoGANLevelBreederTask() throws IllegalAccessException {
		super();
		configureGAN();

		
		
	
		resetLatentVectorAndOutputs();
	}
	
	/**
	 * Set the GAN Process to type ZELDA
	 */
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
	}

	/**
	 * Function to get the file name of the Zelda GAN Model
	 * @returns String the file name of the GAN Model
	 */
	public String getGANModelParameterName() {
		return "zeldaGANModel";
	}
	
	private void resetLatentVectorAndOutputs() {
		int latentVectorLength = GANProcess.latentVectorLength();
		outputLabels = new String[latentVectorLength];
		for(int i = 0; i < latentVectorLength; i++) {
			outputLabels[i] = "LV"+i;
		}
	}


	@Override
	public String[] sensorLabels() {
		return new String[] {"x-coordinate", "y-coordinate", "bias"};
	}

	@Override
	public String[] outputLabels() {
		return outputLabels;
	}

	@Override
	protected String getWindowTitle() {
		return "Zelda CPPN To GAN Dungeon Breeder";
	}

	@Override
	protected void save(String file, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected BufferedImage getButtonImage(TWEANN phenotype, int width, int height, double[] inputMultipliers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<TWEANN> individual) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Override the type of file we want to generate
	 * @return String of file type
	 */
	@Override
	protected String getFileType() {
		return "Text File";
	}

	/**
	 * The extenstion of the file type
	 * @return String file extension
	 */
	@Override
	protected String getFileExtension() {
		return "txt";
	}

	@Override
	public int numCPPNInputs() {
		return this.sensorLabels().length;
	}

	@Override
	public int numCPPNOutputs() {
		return this.outputLabels().length;
	}

}
