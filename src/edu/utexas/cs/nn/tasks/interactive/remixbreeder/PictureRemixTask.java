package edu.utexas.cs.nn.tasks.interactive.remixbreeder;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.interactive.picbreeder.PicbreederTask;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;

/**
 * Takes in an input image and remixes it by extending the
 * Picbreeder interface.
 * 
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public class PictureRemixTask<T extends Network> extends PicbreederTask<T> {

	public static final int CPPN_NUM_INPUTS = 7;
	//public static final int CPPN_NUM_INPUTS = 4 + Parameters.parameters.integerParameter("remixSamplesPerDimension") * Parameters.parameters.integerParameter("remixSamplesPerDimension") * GraphicsUtil.NUM_HSB;

	public static final int CPPN_NUM_OUTPUTS = 5;

	private static final int FILE_LOADER_CHECKBOX_INDEX = CHECKBOX_IDENTIFIER_START - CPPN_NUM_INPUTS;

	public String inputImage;
	public int imageHeight;
	public int imageWidth;

	private BufferedImage img = null;

	protected JSlider windowSize;

	public PictureRemixTask() throws IllegalAccessException {
		this(Parameters.parameters.stringParameter("matchImageFile"));	
		windowSize = new JSlider(JSlider.HORIZONTAL, Parameters.parameters.integerParameter("minRemixImageWindow"), Parameters.parameters.integerParameter("maxRemixImageWindow"), Parameters.parameters.integerParameter("remixImageWindow"));
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		windowSize.setMinorTickSpacing(10);
		windowSize.setPaintTicks(true);
		labels.put(Parameters.parameters.integerParameter("minRemixImageWindow"), new JLabel("Sharp lines"));
		labels.put(Parameters.parameters.integerParameter("maxRemixImageWindow"), new JLabel("Blurred lines"));
		windowSize.setLabelTable(labels);
		windowSize.setPaintLabels(true);
		windowSize.setPreferredSize(new Dimension(200, 40));

		/**
		 * Implements ChangeListener to adjust clip length of generated sounds. When clip length is specified, 
		 * input length is used to reset and redraw buttons. 
		 */
		windowSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					int newLength = (int) source.getValue();
					Parameters.parameters.setInteger("remixImageWindow", newLength);
					// reset buttons
					resetButtons(true);
				}
			}
		});

		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("ChooseNewImage");
		fileLoadButton.setName("" + FILE_LOADER_CHECKBOX_INDEX);
		fileLoadButton.addActionListener(this);

		top.add(windowSize);
		top.add(fileLoadButton);
	}

	public PictureRemixTask(String filename) throws IllegalAccessException{
		try {// throws and exception if filename is not valid
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Could not load image: " + filename);
			System.exit(1);
		}
	}

	@Override
	public String[] sensorLabels() {
		return new String[]{"X-coordinate", "Y-coordinate", "distance from center", "Picture H", "Picture S", "Picture V", "bias"};
	}

	@Override
	protected String getWindowTitle() {
		return "PictureRemix";
	}

	@Override
	protected BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers) {
		// Rescale image based on width and height?
		return GraphicsUtil.remixedImageFromCPPN(phenotype, img, inputMultipliers, Parameters.parameters.integerParameter("remixImageWindow"));
	}

	@Override
	protected void respondToClick(int itemID) {
		super.respondToClick(itemID);
		if(itemID == FILE_LOADER_CHECKBOX_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("BMP Images", "bmp");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				Parameters.parameters.setString("matchImageFile", chooser.getCurrentDirectory() + "\\" + chooser.getSelectedFile().getName());
				String filename = Parameters.parameters.stringParameter("matchImageFile");
				try {// throws and exception if filename is not valid
					img = ImageIO.read(new File(filename));
				} catch (IOException e) {
					System.out.println("Could not load image: " + filename);
					System.exit(1);
				}
				// reset necessary?
				resetButtons(true);
			}
		}
	}

	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	@Override
	public int numCPPNOutputs() {
		return CPPN_NUM_OUTPUTS;
	}

	protected void save(int i) {
		// Use of imageHeight and imageWidth allows saving a higher quality image than is on the button
		BufferedImage toSave = GraphicsUtil.remixedImageFromCPPN((Network)scores.get(i).individual.getPhenotype(), img, inputMultipliers, Parameters.parameters.integerParameter("remixImageWindow"));
		DrawingPanel p = GraphicsUtil.drawImage(toSave, "" + i, toSave.getWidth(), toSave.getHeight());
		if(Parameters.parameters.booleanParameter("saveinteractiveSelections")) {	
			//TODO
			p.save(FileUtilities.getSaveDirectory() + "/selectedFromGen" +  MMNEAT.ea.currentGeneration() + "//" +"item" + MMNEAT.ea.currentGeneration() + "_" + i + "_" + scores.get(i).individual.getId());
		} else {
			String saveName = getSaveName("BMP Images", "bmp");
			p.save(saveName);
			System.out.println("image " + saveName + " was saved successfully");
			p.setVisibility(false);
		}
	}

	/**
	 * For quick testing
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:0","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.utexas.cs.nn.tasks.interactive.remixbreeder.PictureRemixTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
