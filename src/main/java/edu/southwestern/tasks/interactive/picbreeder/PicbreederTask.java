package edu.southwestern.tasks.interactive.picbreeder;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.aqwis.SimpleTiledZentangle;
import com.aqwis.models.SimpleTiledZentangleWFCModel;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.EnhancedCPPNPictureGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkPlusParameters;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.util.BooleanUtil;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Implementation of picbreeder that extends InteractiveEvolutionTask and uses
 * Java Swing components for graphical interface
 * 
 * Original Picbreeder paper: Jimmy Secretan, Nicholas Beato, David B.
 * D'Ambrosio, Adelein Rodriguez, Adam Campbell, Jeremiah T. Folsom-Kovarik and
 * Kenneth O. Stanley. Picbreeder: A Case Study in Collaborative Evolutionary
 * Exploration of Design Space. Evolutionary Computation 19, 3 (2011), 373-403.
 * DOI: http://dx.doi.org/10.1162/evco_a_00030
 * 
 * @author Lauren Gillespie
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public class PicbreederTask<T extends Network> extends InteractiveEvolutionTask<T> implements BoundedTask {

	// These upper and lower bounds are for enhanced CPPN genotypes only (encoding scale, rotation, and translation)
	private static double[] lower;
	private static double[] upper;
	
	private static final int MAX_SELECTABLE_BEFORE_MIXING = 6;

	private static final int MAX_TILE_SIZE_POWER = 5;

	public static int runNumber = 0;

	public static final int CPPN_NUM_INPUTS = 4;
	public static final int CPPN_NUM_OUTPUTS = 3;

	private static final int ZENTANGLE_BUTTON_INDEX = -8;

	private static final int MAX_POSSIBLE_PATTERNS = 6; // Will this always be the max for Zentangles?

	/**
	 * Default constructor
	 * 
	 * @throws IllegalAccessException
	 */
	public PicbreederTask() throws IllegalAccessException {
		super();

		// A check box that switches the output between colorful and black/white
		JCheckBox blackAndWhite = new JCheckBox("black&white",
				Parameters.parameters.booleanParameter("blackAndWhitePicbreeder"));
		blackAndWhite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Flip Black/White");
				// Switch to opposite of current setting
				Parameters.parameters.changeBoolean("blackAndWhitePicbreeder");
				// Need to change all images and re-load
				resetButtons(true);
			}
		});
		
		// A check box that switches the HSB filter method
		JCheckBox standardHSB = new JCheckBox("standardHSB",
				Parameters.parameters.booleanParameter("standardPicBreederHSBRestriction"));
		standardHSB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Change HSB Restrictions");
				// Switch to opposite of current setting
				Parameters.parameters.changeBoolean("standardPicBreederHSBRestriction");
				// Need to change all images and re-load
				resetButtons(true);
			}
		});

		// A check box that switches the output between two brightness levels and
		// continuous
		JCheckBox stark = new JCheckBox("stark", Parameters.parameters.booleanParameter("starkPicbreeder"));
		stark.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Flip Stark Brightness");
				// Switch to opposite of current setting
				Parameters.parameters.changeBoolean("starkPicbreeder");
				// Need to change all images and re-load
				resetButtons(true);
			}
		});

		JPanel imageTweaks = new JPanel();
		imageTweaks.setLayout(new GridLayout(2, 1, 2, 2));
		imageTweaks.add(blackAndWhite);
		imageTweaks.add(stark);
		imageTweaks.add(standardHSB);
		top.add(imageTweaks);

		// Add the Zentangle button
		ImageIcon zentangle = new ImageIcon("data\\picbreeder\\zentangle.png");
		Image zentangle2 = zentangle.getImage().getScaledInstance(getActionButtonWidth(), getActionButtonHeight(), 1);
		JButton zentangleButton = new JButton(new ImageIcon(zentangle2));
		zentangleButton.setPreferredSize(new Dimension(getActionButtonWidth(), getActionButtonHeight()));
		zentangleButton.setText("Zentangle");
		zentangleButton.setName("" + ZENTANGLE_BUTTON_INDEX);
		zentangleButton.setToolTipText("Zentangle button");
		zentangleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				zentangle();
			}

		});

		if (!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(zentangleButton);
		}
	}

	/**
	 * Creates a buffered image from the CPPN.
	 * 
	 * @param <T>
	 * @param phenotype Phenotype of the CPPN
	 * @param imageWidth Width of the image
	 * @param imageHeight Height of the image
	 * @param inputMultiples array of multiples indicating whether to turn activation functions on or off
	 * @return the newly created buffered image
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Network> BufferedImage imageFromCPPN(T phenotype, int imageWidth, int imageHeight, double[] inputMultiples) {
		
		if(phenotype instanceof NetworkPlusParameters) { // CPPN with extra scale and rotation parameters
			NetworkPlusParameters<TWEANN,ArrayList<Double>> npp = (NetworkPlusParameters<TWEANN,ArrayList<Double>>) phenotype;
			ArrayList<Double> scaleRotationTranslation = npp.t2;
			System.out.println("Enhanced:"+scaleRotationTranslation);
			//System.out.println("Scale, Rotation, and Translation (x,y): " + scaleRotationTranslation);
			return GraphicsUtil.imageFromCPPN(phenotype, imageWidth, imageHeight, inputMultiples, -1, scaleRotationTranslation.get(EnhancedCPPNPictureGenotype.INDEX_SCALE), scaleRotationTranslation.get(EnhancedCPPNPictureGenotype.INDEX_ROTATION), scaleRotationTranslation.get(EnhancedCPPNPictureGenotype.INDEX_DELTA_X), scaleRotationTranslation.get(EnhancedCPPNPictureGenotype.INDEX_DELTA_Y));
		} else { // Plain CPPN/TWEANGenotype
			return GraphicsUtil.imageFromCPPN((Network) phenotype, imageHeight, imageWidth, inputMultiples, -1, Parameters.parameters.doubleParameter("picbreederImageScale"), Parameters.parameters.doubleParameter("picbreederImageRotation"), Parameters.parameters.doubleParameter("picbreederImageTranslationX"), Parameters.parameters.doubleParameter("picbreederImageTranslationY"));
		}
	}
	
	/**
	 * Save a single hi-res version of a particular image. This is used by the
	 * zentangle method below, though I'm not sure it belongs in this class, or even
	 * deserves its own method.
	 * 
	 * @param filename name of file to be saved
	 * @param dim dimensions of the image
	 * @param phenotype phenotype of the CPPN
	 * @param inputMultipliers array of multiples indicating whether to turn activation functions on or off
	 */

	public static <T> BufferedImage saveSingle(String filename, int dim, T phenotype, double[] inputMultipliers, boolean isBackground) {
		// Use of imageHeight and imageWidth allows saving a higher quality image than
		// is on the button
		// Set starkPicbreeder to true
		boolean originalValue = Parameters.parameters.booleanParameter("starkPicbreeder");
		if(isBackground == true) Parameters.parameters.setBoolean("starkPicbreeder", true);
		BufferedImage toSave1 = imageFromCPPN((Network) phenotype, dim, dim, inputMultipliers);
		// Set starkPicbreeder back to false
		Parameters.parameters.setBoolean("starkPicbreeder", originalValue);
		String filename1 = filename + "1.bmp";
		GraphicsUtil.saveImage(toSave1, filename1);

		System.out.println("image " + filename1 + " was saved successfully. Size: "+toSave1.getWidth()+" by "+toSave1.getHeight());
		return toSave1; // return the image that was saved
	}

	/**
	 * Take scores and selected items and return a list of phenotypes. This is used
	 * by the zentangle method below.
	 * 
	 * @param <T>
	 * @param scores 
	 * @param chosen 
	 * @param selectedItems 
	 * @return list of phenotypes
	 */
	public static <T> ArrayList<T> getPhenotypes(ArrayList<Score<T>> scores, boolean[] chosen,
			LinkedList<Integer> selectedItems) {
		int numSelected = selectedItems.size();
		ArrayList<T> phenotypes = new ArrayList<T>();
		if (!BooleanUtil.any(chosen) || numSelected <= 1) {
			return null;
		} else {
			for (int j = 0; j < scores.size(); j++) {
				if (chosen[j]) {
					phenotypes.add(scores.get(j).individual.getPhenotype());
				}
			}
			return phenotypes;
		}
	}

	/**
	 * Code from Sarah Friday, Anna Krolikowski, and Alice Quintanilla from their
	 * final Spring 2019 AI project.
	 * 
	 * Saves several selected images from picbreeder to disk and then runs the Wave
	 * Function Collapse code to make a Zentangle mosaic out of the results.
	 * 
	 * TODO: Need to clean this code up a bit
	 */
	public void zentangle() {
		ArrayList<T> chosenTiles = getPhenotypes(scores, this.chosen, this.selectedItems);
		zentangle(SimpleTiledZentangle.getSaveDirectory(), chosenTiles, inputMultipliers);
	}

	/**
	 * Creates a zentangle image for different cases 
	 * depending on the number of images selected.
	 * 
	 * @param <T>
	 * @param directory directory to save the images to
	 * @param chosenTiles Tiles chosen to make the zentangle image from
	 * @param inputMultipliers array of multiples indicating whether to turn activation functions on or off
	 */
	public static <T> void zentangle(String directory, ArrayList<T> chosenTiles, double[] inputMultipliers) {
		// Make sure zentangle directory exists
		File d = new File(directory);
		if (!d.exists()) {
			d.mkdirs(); // Make all recursive directories
		}

		if (chosenTiles == null) {
			System.out.println("Insufficient number of tiles chosen to zentangle.");
			JOptionPane.showMessageDialog(null,
					"Insufficient number of tiles chosen to zentangle. Select at least two.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			final int numSelected = chosenTiles.size();
			runNumber++;
			String waveFunctionSaveLocation = directory + "/";
			File dir = new File(waveFunctionSaveLocation);
			if (!dir.exists()) { // Create save directory if it does not exist
				dir.mkdir();
			}

			String[] tileNames = new String[chosenTiles.size()];
			int numTileImagesSaved = 0;
			int numStored = 0;
			int tileSize = Parameters.parameters.integerParameter("zentangleTileDim");
			int backgroundSize = tileSize * Parameters.parameters.integerParameter("zentanglePatternDim");

			// Pick two random distinct indices to determine which images make up background
			// patterns
			int[] bgIndices = RandomNumbers.randomDistinct(2, numSelected);
			int bgIndex1 = bgIndices[0];
			int bgIndex2 = bgIndices[1];
			//int bgIndex3 = bgIndices[2];

			BufferedImage bgImage1 = null;
			BufferedImage bgImage2 = null;
			
			int[] tempTileSizeList = new int[numSelected];
			
			for (int i = 0; i < numSelected; i++) {
				// If too many images are selected, then they will be combined into mixed tile sets, but this requires all tiles to be the same size (multiplier of 1)
				int tileSizeMultiplier = numSelected > MAX_SELECTABLE_BEFORE_MIXING ? 1 : (int) Math.pow(2, RandomNumbers.randomGenerator.nextInt(MAX_TILE_SIZE_POWER));
				
				if (i == bgIndex1) {
					// Represents a template pattern
					bgImage1 = saveSingle(waveFunctionSaveLocation + "background", backgroundSize, chosenTiles.get(i),inputMultipliers, true);
					System.out.println("one template pattern");
					if (numSelected < 3) { // If there are only two images, one serves as a background pattern AND a
											// tile pattern
						String fullName = "tile" + numTileImagesSaved + "_";
						tempTileSizeList[numStored] = tileSizeMultiplier;
						System.out.println("tileSizeMultiplier = "+tileSizeMultiplier+" -> pixels = "+tileSizeMultiplier*tileSize);
						tileNames[numStored++] = fullName + "1";
						saveSingle(waveFunctionSaveLocation + fullName, tileSizeMultiplier*tileSize, chosenTiles.get(i), inputMultipliers, false);
						numTileImagesSaved++;
						System.out.println("fewer than 3 selected");
					}
				} else if (i == bgIndex2) {
					// A possible second template pattern
					bgImage2 = saveSingle(waveFunctionSaveLocation + "background2", backgroundSize, chosenTiles.get(i),inputMultipliers, true);
					System.out.println("second template pattern");
					
					if(numSelected != 5 && numSelected != 6) {
						String fullName = "tile" + numTileImagesSaved + "_";
						tempTileSizeList[numStored] = tileSizeMultiplier;
						System.out.println("tileSizeMultiplier = "+tileSizeMultiplier+" -> pixels = "+tileSizeMultiplier*tileSize);
						tileNames[numStored++] = fullName + "1";
						saveSingle(waveFunctionSaveLocation + fullName, tileSizeMultiplier*tileSize, chosenTiles.get(i), inputMultipliers, false);
						numTileImagesSaved++;
					}
//				} else if (i == bgIndex3) {
//					saveSingle(waveFunctionSaveLocation + "background3", backgroundSize, chosenTiles.get(i), inputMultipliers, true);
//					String fullName = "tile" + numSaved + "_";
//					tileNames[numStored++] = fullName + "1";
//					saveSingle(waveFunctionSaveLocation + fullName, tileSize, chosenTiles.get(i), inputMultipliers, false);
//					numSaved++;
//					System.out.println("third template pattern");
				} else {
					// All other images used to create background tiles with WFC
					String fullName = "tile" + numTileImagesSaved + "_";
					tempTileSizeList[numStored] = tileSizeMultiplier;
					System.out.println("tileSizeMultiplier = "+tileSizeMultiplier+" -> pixels = "+tileSizeMultiplier*tileSize);
					tileNames[numStored++] = fullName + "1";
					saveSingle(waveFunctionSaveLocation + fullName, tileSizeMultiplier*tileSize, chosenTiles.get(i), inputMultipliers, false);
					numTileImagesSaved++;
					System.out.println("All other tiles selected are background tiles made with WFC");
				}
			}

			// At this point, tileNames only stores tile images that will be used with WFC,
			// though the array has
			// some empty slots at the end which are null.

			// use wfc to create final zentangle image, save it as zentangle.bmp
			BufferedImage[] patterns = new BufferedImage[MAX_POSSIBLE_PATTERNS]; 
			int numPartitions = 2;
			int standardSize = numStored / numPartitions;
			ArrayList<String> tilesToProcess = new ArrayList<>();
			int zentangleNumber = 0;
			for (int i = 0; i < numStored; i++) {
				tilesToProcess.add(tileNames[i]);
				// The partition is full, create a zentangle with WFC
				if (chosenTiles.size() <= MAX_SELECTABLE_BEFORE_MIXING || (i + 1) % standardSize == 0) {
					// Writes data.xml
					SimpleTiledZentangleWFCModel.writeAdjacencyRules(directory, tilesToProcess.toArray(new String[tilesToProcess.size()]), tempTileSizeList[zentangleNumber]*tileSize);
					// data.xml gets read in this next method
					try {
						patterns[zentangleNumber] = SimpleTiledZentangle.simpleTiledZentangle(directory, zentangleNumber, Parameters.parameters.integerParameter("zentanglePatternDim") / tempTileSizeList[zentangleNumber]);
						patterns[zentangleNumber] = GraphicsUtil.extractCenterOfDoubledRotatedImage(patterns[zentangleNumber], RandomNumbers.randomGenerator.nextDouble() * 360);
						zentangleNumber++;
					} catch (Exception e) {
						e.printStackTrace();
					}
					tilesToProcess.clear(); // Empty out partition
				}
			}

			// Different cases for how many images are selected:
			// 1, 2, 3, 4, 5, 6, 7+ images are individual cases
			BufferedImage zentangle = null;
			switch(numSelected) {
			// 2 or 3 images selected: one will be a background image,
			// if 2 images, the background image will also be a pattern image
			// if 3 images, one background image and the other two will be pattern images.
			case 2:
			case 3:
				zentangle = GraphicsUtil.zentangleImages(bgImage1, patterns[0], patterns[1]);				
				break;	
			// 4 or 5 images selected: two will be background images
			// if 4 images, two background images, one background image will double as one of three pattern images
			// if 5 images, two background images, three completely different pattern images
			case 4:
			case 5:
				zentangle = GraphicsUtil.zentangleImages(bgImage1, bgImage2, patterns[0], patterns[1], patterns[2]);								
				break;
			// if 6 images, two background images, four completely different pattern images
			case 6:
				zentangle = GraphicsUtil.zentangleImages(bgImage1, bgImage2, patterns[0], patterns[1], patterns[2], patterns[3]);												
				break;
			// if 7+ images, one background image, two pattern images with tiles from the excess images.
			// essentially, half of the chosen images (not including the background image) will be used by 
			// WFC to create the first pattern and the other half of the selected images will be used to create
			// the second pattern.
			default:
				zentangle = GraphicsUtil.zentangleImages(bgImage1, patterns[0], patterns[1]);								
				break;
			}
			
			File outputfile = new File(waveFunctionSaveLocation + "/zentangle.png");
			try {
				ImageIO.write(zentangle, "png", outputfile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("image was saved successfully");

			if (CommonConstants.watch) {
				try {
					System.out.println("Opening " + outputfile);
					Desktop.getDesktop().open(outputfile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
		return imageFromCPPN(phenotype, width, height, inputMultipliers);
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
		// Use of imageHeight and imageWidth allows saving a higher quality image than
		// is on the button
		BufferedImage toSave = imageFromCPPN((Network) scores.get(i).individual.getPhenotype(),
				Parameters.parameters.integerParameter("imageWidth"),
				Parameters.parameters.integerParameter("imageHeight"), inputMultipliers);
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
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// args[0] is the random seed
		int seed = 0;
		if (args.length == 1) {
			seed = Integer.parseInt(args[0]);
		}
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:16", "maxGens:500",
					"zentangleTileDim:100", 
					"genotype:edu.southwestern.evolution.genotypes.EnhancedCPPNPictureGenotype",
					"io:false", "netio:false", "mating:true", "fs:false", "starkPicbreeder:false",
					//"imageCenterTranslationRange:0.0", // Uncomment to turn off evolution of translation 
					//"minScale:1.0", "maxScale:1.0", // Uncomment to turn off evolution of scale
					//"enhancedCPPNCanRotate:false", // Uncomment to turn off evolution of rotation
					"task:edu.southwestern.tasks.interactive.picbreeder.PicbreederTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:true", "netChangeActivationRate:0.3", "cleanFrequency:-1",
					"simplifiedInteractiveInterface:false", "recurrency:false", "saveAllChampions:true",
					"cleanOldNetworks:false", "ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA",
					"imageWidth:2000", "imageHeight:2000", "imageSize:200", "includeFullSigmoidFunction:true",
					"includeFullGaussFunction:true", "includeCosineFunction:true", "includeGaussFunction:false",
					"includeIdFunction:true", "includeTriangleWaveFunction:false", "includeSquareWaveFunction:false",
					"includeFullSawtoothFunction:false", "includeSigmoidFunction:false", "includeAbsValFunction:false",
					"includeSawtoothFunction:false", "allowInteractiveSave:true", 
					//"picbreederImageScale:10.0", "picbreederImageRotation:0.0", // <- Not relevant when EnhancedCPPNPictureGenotype is used
					"picbreederImageTranslationX:0.0", "picbreederImageTranslationY:0.0"});  // <- Not relevant when EnhancedCPPNPictureGenotype is used
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

	public static double[] getStaticUpperBounds() {
		if(upper == null) upper = new double[] {Parameters.parameters.doubleParameter("maxScale"), Parameters.parameters.booleanParameter("enhancedCPPNCanRotate") ? 2*Math.PI : 0.0, Parameters.parameters.doubleParameter("imageCenterTranslationRange"), Parameters.parameters.doubleParameter("imageCenterTranslationRange")};		
		return upper;
	}

	public static double[] getStaticLowerBounds() {
		if(lower == null) lower = new double[] {Parameters.parameters.doubleParameter("minScale"), 0, -Parameters.parameters.doubleParameter("imageCenterTranslationRange"), -Parameters.parameters.doubleParameter("imageCenterTranslationRange")};
		return lower;
	}

	@Override
	public double[] getUpperBounds() {
		return getStaticUpperBounds();
	}

	public double[] getLowerBounds() {
		return getStaticLowerBounds();
	}
}
