package edu.southwestern.tasks.interactive.remixbreeder;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.PythonNeuralStyleTransfer;

/**
 * Remixes an input image using the Neural Style Transfer Algorithm.
 * The style images are CPPN generated/evolved images.
 * 
 * This doesn't really work any more. There was a Java version of Neural Style Transfer that
 * relied on DL4J, but I removed that since it bloated the codebase. The Python version can be
 * used instead, but it relies on obsolete versions of tensor-flow and other libraries, so
 * it has been hard to maintain.
 * 
 * @author Jacob
 *
 * @param <T>
 */
public class PictureStyleBreederTask<T extends Network> extends PicbreederTask<T> {

	private static final int FILE_LOADER_CHECKBOX_INDEX = CHECKBOX_IDENTIFIER_START - CPPN_NUM_INPUTS;

	private static final int MIN_STYLE_ITERATIONS = 1;
	private static final int MAX_STYLE_ITERATIONS = 100;
	private static final int MIN_STYLE_WEIGHT = 1;
	private static final int MAX_STYLE_WEIGHT = 99;
	
	protected JSlider styleIterations;	
	protected JSlider styleWeight;	
		
	public PictureStyleBreederTask() throws IllegalAccessException {
		String contentImagePath = Parameters.parameters.stringParameter("matchImageFile");	
		System.out.println("Load content file: "+contentImagePath);
		// Boot up the Python program for Neural Style transfer
		PythonNeuralStyleTransfer.initiateNeuralStyleTransferProcess(contentImagePath);
		
		styleIterations = new JSlider(JSlider.HORIZONTAL, MIN_STYLE_ITERATIONS, MAX_STYLE_ITERATIONS, Parameters.parameters.integerParameter("neuralStyleIterations"));
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		styleIterations.setMinorTickSpacing(10);
		styleIterations.setPaintTicks(true);
		labels.put((MIN_STYLE_ITERATIONS+MAX_STYLE_ITERATIONS)/2, new JLabel("Iterations"));
		styleIterations.setLabelTable(labels);
		styleIterations.setPaintLabels(true);
		styleIterations.setPreferredSize(new Dimension(200, 40));

		styleIterations.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					int newValue = (int) source.getValue();
					Parameters.parameters.setInteger("neuralStyleIterations", newValue);
					// reset buttons
					resetButtons(true);
				}
			}
		});

		top.add(styleIterations);		

		// Style weight is from [0.0,1,0] but slider requires an integer, so multiply by 100
		styleWeight = new JSlider(JSlider.HORIZONTAL, MIN_STYLE_WEIGHT, MAX_STYLE_WEIGHT, (int)(Parameters.parameters.doubleParameter("neuralStyleStyleWeight")*100));
		Hashtable<Integer,JLabel> styleLabels = new Hashtable<>();
		styleWeight.setMinorTickSpacing(10);
		styleWeight.setPaintTicks(true);
		styleLabels.put((MIN_STYLE_WEIGHT+MAX_STYLE_WEIGHT)/2, new JLabel("Style Weight"));
		styleWeight.setLabelTable(styleLabels);
		styleWeight.setPaintLabels(true);
		styleWeight.setPreferredSize(new Dimension(200, 40));

		styleWeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					int newValue = (int) source.getValue();
					// JSlider value is integer, so divide by 100.0 to get actual weight
					double weight = newValue/100.0;
					Parameters.parameters.setDouble("neuralStyleStyleWeight", weight);
					// reset buttons
					resetButtons(true);
				}
			}
		});

		top.add(styleWeight);		
		
		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("ChooseNewImage");
		fileLoadButton.setName("" + FILE_LOADER_CHECKBOX_INDEX);
		fileLoadButton.addActionListener(this);

		top.add(fileLoadButton);
	}

	/**
	 * A hard reset kills the Python process and initializes it again with potentially new command line parameters
	 */
	public void resetButtons(boolean hardReset) {
		if(hardReset) {
			String contentImagePath = Parameters.parameters.stringParameter("matchImageFile");
			// End the process
			PythonNeuralStyleTransfer.terminatePythonProcess();
			// Re-launch process using content image (some parameters probably changed if hard reset is being called)
			PythonNeuralStyleTransfer.initiateNeuralStyleTransferProcess(contentImagePath);
		}
		super.resetButtons(hardReset);
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
			String[] pictureSuffixes = ArrayUtil.filterString(ImageIO.getReaderFileSuffixes(), "");
			FileFilter imageFilter = new FileNameExtensionFilter("Image files", pictureSuffixes);
			chooser.setFileFilter(imageFilter);
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				Parameters.parameters.setString("matchImageFile", chooser.getCurrentDirectory() + File.separator + chooser.getSelectedFile().getName());				
				// reset necessary to kill and restart process with new image
				resetButtons(true);
			}
		}
		return false; //default: all is fine
	}

	@Override
	protected void save(String filename, int i) {
		// Retrieve image without generating it again
		BufferedImage toSave = GraphicsUtil.toBufferedImage(((ImageIcon) this.buttons.get(i).getIcon()).getImage());
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
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:0","trials:1","simplifiedInteractiveInterface:false","mu:20","maxGens:500","neuralStyleIterations:20","io:false","netio:false","mating:true", "fs:false", "task:edu.southwestern.tasks.interactive.remixbreeder.PictureStyleBreederTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
