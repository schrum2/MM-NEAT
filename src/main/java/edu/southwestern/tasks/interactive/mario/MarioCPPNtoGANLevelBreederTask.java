package edu.southwestern.tasks.interactive.mario;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.idsia.mario.engine.level.Level;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.MarioGANUtil;

/**
 * Interactively evolve Mario levels. Users selectively
 * breed levels based on appearance, but can also choose
 * to play any of the evolved levels. CPPNs are evolved,
 * but they send inputs to a GAN which is responsible for final
 * level creation.
 * 
 * @author Jacob
 *
 * @param <T>
 */
public class MarioCPPNtoGANLevelBreederTask<T extends Network> extends MarioLevelBreederTask<T> {

	private static final int FILE_LOADER_BUTTON_INDEX = -21;

	// Labels for CPPN
	public static final String[] UPDATED_INPUTS = new String[] {"x-coordinate","bias"};
	private String[] outputLabels;
	
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;
	}

	// In this class, the interpretation of these methods is overridden to relate to the number of latent vector chunks
	public int shortestLevelLength() { return 1; }
	public int longestLevelLength() { return 10; }
	
	public MarioCPPNtoGANLevelBreederTask() throws IllegalAccessException {
		super();
		configureGAN();

		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("SelectGANModel");
		fileLoadButton.setName("" + FILE_LOADER_BUTTON_INDEX);
		fileLoadButton.addActionListener(this);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(fileLoadButton);
		}
		
		
		resetLatentVectorAndOutputs();
	}

	/**
	 * Whenever the GAN model changes, the latent vector length is
	 * different, and the output labels need to reset. The CPPN population
	 * also has to reset, but that is done elsewhere.
	 */
	private void resetLatentVectorAndOutputs() {
		int latentVectorLength = GANProcess.latentVectorLength();
		outputLabels = new String[latentVectorLength];
		for(int i = 0; i < latentVectorLength; i++) {
			outputLabels[i] = "LV"+i;
		}
	}

	protected String[] generateLevelLayoutFromCPPN(Network cppn, double[] inputMultipliers, int marioLevelLength) {
		double[] doubleArray = createLatentVectorFromCPPN(cppn, inputMultipliers, marioLevelLength);
		ArrayList<List<Integer>> levelList = MarioGANUtil.generateLevelListRepresentationFromGAN(doubleArray);
		String[] level = MarioGANUtil.generateTextLevel(levelList);	
		return level;
	}
	
	protected Level generateLevelFromCPPN(Network phenotype, double[] inputMultipliers, int marioLevelLength) {
		double[] doubleArray = createLatentVectorFromCPPN(phenotype, inputMultipliers, marioLevelLength);
		Level level = MarioGANUtil.generateLevelFromGAN(doubleArray);
		return level;
	}

	public static double[] createLatentVectorFromCPPN(Network cppn, double[] inputMultipliers, int marioLevelLength) {
		int latentVectorLength = GANProcess.latentVectorLength();
		// One GAN vector for each segment
		double[] doubleArray = new double[marioLevelLength*latentVectorLength];
		for(int i = 0; i < marioLevelLength; i++) {
			// x-coordinate and bias
			double[] segment = cppn.process(new double[] {inputMultipliers[0] * i/marioLevelLength, inputMultipliers[1] * 1.0});
			System.arraycopy(segment, 0, doubleArray, i*latentVectorLength, latentVectorLength);
		}
		return doubleArray;
	}
	
	public String getGANModelDirectory() {
		return "python"+File.separator+"GAN"+File.separator+"MarioGAN";
	}
	
	public String getGANModelParameterName() {
		return "marioGANModel";
	}
	
	/**
	 * Responds to a button to actually play a selected level
	 * @param itemID Unique integer stored in each button to determine which one was pressed
	 * @returns boolean True if we need to undo the click
	 */
	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest

		if(itemID == FILE_LOADER_BUTTON_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("GAN Model", "pth");
			chooser.setFileFilter(filter);
			// This is where all the GANs are stored (only allowable spot)
			chooser.setCurrentDirectory(new File(getGANModelDirectory()));
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				String model = chooser.getSelectedFile().getName();
				Parameters.parameters.setString(getGANModelParameterName(), model);
				MarioGANLevelBreederTask.staticResetAndReLaunchGAN(model);
				reset(); // Reset the whole population, since the CPPNs need to have a different number of output neurons
				resetLatentVectorAndOutputs();
			}
			resetButtons(true);
		}

		return false; // no undo: every thing is fine
	}
	
	
	@Override
	public String[] sensorLabels() {
		return UPDATED_INPUTS;
	}

	@Override
	public String[] outputLabels() {
		return outputLabels;
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","marioLevelLength:4","randomSeed:1","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask","allowMultipleFunctions:true","ftype:0","watch:true","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
