package edu.southwestern.tasks.interactive.remixbreeder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.PythonNeuralStyleTransfer;

/**
 * Remixes an input image using the Neural Style Transfer Algorithm.
 * The style images are CPPN generated/evolved images.
 * 
 * @author Jacob
 *
 * @param <T>
 */
public class PictureStyleBreederTask<T extends Network> extends PicbreederTask<T> {

	private static final int FILE_LOADER_CHECKBOX_INDEX = CHECKBOX_IDENTIFIER_START - CPPN_NUM_INPUTS;

	public PictureStyleBreederTask() throws IllegalAccessException {
		String contentImagePath = Parameters.parameters.stringParameter("matchImageFile");	
		// Boot up the Python program for Neural Style transfer
		PythonNeuralStyleTransfer.initiateNeuralStyleTransferProcess(contentImagePath);
		
		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("ChooseNewImage");
		fileLoadButton.setName("" + FILE_LOADER_CHECKBOX_INDEX);
		fileLoadButton.addActionListener(this);

		top.add(fileLoadButton);
	}

	@Override
	protected String getWindowTitle() {
		return "PictureStyleBreeder";
	}

	@Override
	protected BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers) {
		// Standard CPPN image will be the style for the Neural Style Transfer Algorithm
		BufferedImage styleImage = super.getButtonImage(phenotype, width, height, inputMultipliers);
		// Content image with new style from CPPN image
		BufferedImage comboImage = PythonNeuralStyleTransfer.sendStyleImage(styleImage);
		return comboImage;
	}

	@Override
	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
		
		if(itemID == FILE_LOADER_CHECKBOX_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			// Should restrict to images, but want all images
			//FileNameExtensionFilter filter = new FileNameExtensionFilter("BMP Images", "bmp");
			//chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				// End the process using the old content image
				PythonNeuralStyleTransfer.terminatePythonProcess();
				// Re-launch process using new content image
				Parameters.parameters.setString("matchImageFile", chooser.getCurrentDirectory() + File.separator + chooser.getSelectedFile().getName());
				String contentImagePath = Parameters.parameters.stringParameter("matchImageFile");
				PythonNeuralStyleTransfer.initiateNeuralStyleTransferProcess(contentImagePath);
				
				// reset necessary?
				resetButtons(true);
			}
		}
		return false; //default: all is fine
	}

	@Override
	protected void save(String filename, int i) {
		// Retrieve image without generating it again
		BufferedImage toSave = (BufferedImage) ((ImageIcon) this.buttons.get(i).getIcon()).getImage();
		filename += ".bmp";
		GraphicsUtil.saveImage(toSave, filename);
		System.out.println("image " + filename + " was saved successfully");
	}

	/**
	 * For quick testing
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:0","trials:1","mu:20","maxGens:500","io:false","netio:false","mating:true", "fs:false", "task:edu.southwestern.tasks.interactive.remixbreeder.PictureStyleBreederTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
