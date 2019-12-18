package edu.southwestern.tasks.interactive.gvgai;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;

public class ZeldaCPPNtoGANLevelBreederTask extends InteractiveEvolutionTask<TWEANN> {

	public static final int PLAY_BUTTON_INDEX = -20;
	private static final int LEVEL_MIN_CHUNKS = 1;
	private static final int LEVEL_MAX_CHUNKS = 10; 
	private String[] outputLabels;


	public ZeldaCPPNtoGANLevelBreederTask() throws IllegalAccessException {
		super();
		configureGAN();

		//Construction of button that lets user plays the level
		JButton play = new JButton("Play");
		// Name is first available numeric label after the input disablers
		play.setName("" + PLAY_BUTTON_INDEX);
		play.addActionListener(this);
		top.add(play);

		JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"));
		widthSlider.setMinorTickSpacing(1);
		widthSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> widthLabels = new Hashtable<>();
		widthLabels.put(LEVEL_MIN_CHUNKS, new JLabel("Narrower"));
		widthLabels.put(LEVEL_MAX_CHUNKS, new JLabel("Wider"));
		widthSlider.setLabelTable(widthLabels);
		widthSlider.setPaintLabels(true);
		widthSlider.setPreferredSize(new Dimension(200, 40));

		JSlider heightSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"));
		heightSlider.setMinorTickSpacing(1);
		heightSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> heightLabels = new Hashtable<>();
		heightLabels.put(LEVEL_MIN_CHUNKS, new JLabel("Shorter"));
		heightLabels.put(LEVEL_MAX_CHUNKS, new JLabel("Taller"));
		heightSlider.setLabelTable(heightLabels);
		heightSlider.setPaintLabels(true);
		heightSlider.setPreferredSize(new Dimension(200, 40));

		JPanel size = new JPanel();
		size.add(widthSlider);
		size.add(heightSlider);
		
		top.add(size);

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
		return new String[] {"x-coordinate", "y-coordinate", "radius", "bias"};
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
		Dungeon dungeonInstance = new Dungeon();
		
		
		// TODO: Build up, addd rooms. SimpleDungeon.makeDungeon might have some helpful code
		
		BufferedImage image = DungeonUtil.imageOfDungeon(dungeonInstance);
		return image;
	}

	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
		// Human plays level
		if(itemID == PLAY_BUTTON_INDEX && selectedItems.size() > 0) {
			Network cppn = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
			
			// TODO: Generate and launch dungeon

		}
		return false; // no undo: every thing is fine
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<TWEANN> individual) {
		// Not used
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

	public static Dungeon cppnToDungeon(Network cppn, int width, int height, double[] inputMultipliers) {
		double[][][] latentVectorGrid = latentVectorGridFromCPPN(cppn, width, height, inputMultipliers);
		List<List<Integer>>[][] levelAsListsGrid = levelGridFromLatentVectorGrid(latentVectorGrid);
		Level[][] levelGrid = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
		Dungeon dungeon = dungeonFromLevelGrid(levelGrid);
		return dungeon;
	}
	
	/**
	 * Make a playable Rogue-like dungeon from a 2D Level grid. Copied some code
	 * from SimpleDungeon. Might need to refactor at some point
	 * @param levelGrid 2D Level grid for dungeon (each cell is a room)
	 * @return Complete Dungeon representing the given Level grid
	 */
	public static Dungeon dungeonFromLevelGrid(Level[][] levelGrid) {
		Dungeon dungeonInstance = new Dungeon();

		String[][] uuidLabels = new String[levelGrid.length][levelGrid[0].length];
		
		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					if(uuidLabels[y][x] == null) {
						uuidLabels[y][x] = "("+x+","+y+")";
					}
					String name = uuidLabels[y][x];
					Node newNode = dungeonInstance.newNode(name, levelGrid[y][x]);
					
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x + 1, y, "RIGHT");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x, y - 1, "UP");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x - 1, y, "LEFT");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x, y + 1, "DOWN");
				}	
			}
		}
		
		// Put start in middle
		String name = uuidLabels[(uuidLabels.length - 1) / 2][(uuidLabels[0].length - 1) /2].toString();
		
		dungeonInstance.setCurrentLevel(name);
		dungeonInstance.setLevelThere(uuidLabels);
		
		return dungeonInstance;

	}
	
	/**
	 * CPPN is queried at each point in a 2D grid and generates a latent vector for the GAN to store at that location in an array.
	 * @param cppn Neural network that creates latent vectors
	 * @param width Width of Dungeon grid (second dimension of array)
	 * @param height Height of Dungeon grid (first dimension of array)
	 * @param inputMultipliers Multipliers for CPPN inputs which has potential to disable them
	 * @return 3D array that is a 2D grid of latent vectors
	 */
	public static double[][][] latentVectorGridFromCPPN(Network cppn, int width, int height, double[] inputMultipliers) {
		double[][][] latentVectorGrid = new double[height][width][];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), width, height);
				double[] remixedInputs = { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * GraphicsUtil.SQRT2, GraphicsUtil.BIAS };
				// Might turn some inputs on/off
				for(int i = 0; i < remixedInputs.length; i++) {
					remixedInputs[i] *= inputMultipliers[i];
				}
				double[] vector = cppn.process(remixedInputs);
				latentVectorGrid[y][x] = vector;
			}
		}
		return latentVectorGrid;
	}
	
	/**
	 * Given 2D grid of latent vectors, send each to the GAN to get a 2D grid of List representations of the rooms.
	 * @param latentVectorGrid 3D array that is 2D grid of latent vectors
	 * @return Grid of corresponding Lists of Lists of Integers, which each such list is the room for a latent vector
	 */
	public static List<List<Integer>>[][] levelGridFromLatentVectorGrid(double[][][] latentVectorGrid) {
		@SuppressWarnings("unchecked")
		List<List<Integer>>[][] levelAsListsGrid = (List<List<Integer>>[][]) new List[latentVectorGrid.length][latentVectorGrid[0].length];
		for(int y = 0; y < levelAsListsGrid.length; y++) {
			for(int x = 0; x < levelAsListsGrid[0].length; x++) {
				levelAsListsGrid[y][x] = ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(latentVectorGrid[y][x]);
			}
		}
		return levelAsListsGrid;
	}
	
	
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","showKLOptions:false","trials:1","mu:16","zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask","cleanOldNetworks:false", "zeldaGANUsesOriginalEncoding:false","allowMultipleFunctions:true","ftype:0","watch:true","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
