package edu.southwestern.tasks.interactive.loderunner;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
//import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.loderunner.LodeRunnerGANUtil;
import edu.southwestern.tasks.loderunner.LodeRunnerRenderUtil;
import edu.southwestern.tasks.loderunner.LodeRunnerTSPUtil;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import icecreamyou.LodeRunner.LodeRunner;

/**
 * Interactively evolves Lode Runner levels from the latent space of a GAN network.
 * @author kdste
 *
 */
public class LodeRunnerGANLevelBreederTask extends InteractiveGANLevelEvolutionTask{

	private static final int PATH_TYPE_ASTAR = 0;
	private static final int PATH_TYPE_TSP = 1;

	/**
	 * Constructor for the Level Breeder for interactive evolving 
	 * @throws IllegalAccessException
	 */
	public LodeRunnerGANLevelBreederTask() throws IllegalAccessException {
		super();
		//adds a check box to show solution path or not, starts with them not showing 
		JPanel solutionPathPanel = new JPanel();
		solutionPathPanel.setLayout(new BoxLayout(solutionPathPanel, BoxLayout.Y_AXIS));
		JLabel AStarLabel = new JLabel("UpdateAStarBudget");
		AStarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JTextField updateAStarBudget = new JTextField(10);
		updateAStarBudget.setText(String.valueOf(Parameters.parameters.integerParameter("aStarSearchBudget")));
		updateAStarBudget.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					String budget = updateAStarBudget.getText();
					if(!budget.matches("\\d+")) {
						System.out.println("Match failure! \"" + budget + "\"");
						return;
					}
					int value = Integer.parseInt(budget);
					Parameters.parameters.setInteger("aStarSearchBudget", value);
					System.out.println("Reset budget: "+value);
					resetButtons(true);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		JLabel TSPBudget = new JLabel("UpdateTSPBudget");
		TSPBudget.setAlignmentX(Component.CENTER_ALIGNMENT);
		TSPBudget.setVisible(false);
		JTextField updateTSPBudget = new JTextField(10);
		updateTSPBudget.setText(String.valueOf(Parameters.parameters.integerParameter("lodeRunnerTSPBudget")));
		updateTSPBudget.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					String budget = updateTSPBudget.getText();
					if(!budget.matches("\\d+")) {
						System.out.println("Match failure! \"" + budget + "\"");
						return;
					}
					int value = Integer.parseInt(budget);
					Parameters.parameters.setInteger("lodeRunnerTSPBudget", value);
					System.out.println("Reset budget: "+value);
					resetButtons(true);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {	
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		updateTSPBudget.setVisible(false);
		JLabel choosePath = new JLabel("Choose Path Type");
		choosePath.setAlignmentX(Component.CENTER_ALIGNMENT);
		String[] options = {"None","Pure A*", "TSP + A*"};
		JComboBox<String> showSolutionPath = new JComboBox<String>(options);
		showSolutionPath.setSelectedIndex(0);
		showSolutionPath.setSize(40, 40);
		showSolutionPath.addItemListener(new ItemListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void itemStateChanged(ItemEvent e) {
				Parameters.parameters.setBoolean("showInteractiveLodeRunnerSolutionPaths", true);
				JComboBox<String> source = (JComboBox<String>)e.getSource();
				int index = source.getSelectedIndex();
				if(index == 1) {//pure A*
					Parameters.parameters.setInteger("interactiveLodeRunnerPathType", PATH_TYPE_ASTAR);
					updateTSPBudget.setVisible(false);
					updateTSPBudget.setVisible(false);
					AStarLabel.setVisible(true);
					updateAStarBudget.setVisible(true);
				}
				else if(index == 2) { //tsp + A*
					Parameters.parameters.setInteger("interactiveLodeRunnerPathType", PATH_TYPE_TSP);
					TSPBudget.setVisible(true);
					updateTSPBudget.setVisible(true);
					AStarLabel.setVisible(false);
					updateAStarBudget.setVisible(false);
				}
				else {
					Parameters.parameters.setBoolean("showInteractiveLodeRunnerSolutionPaths", false);//if neither path is selected it displays the default render
				}
				resetButtons(true);
			}

		});
		solutionPathPanel.add(choosePath);
		solutionPathPanel.add(showSolutionPath);
		solutionPathPanel.add(AStarLabel);
		solutionPathPanel.add(updateAStarBudget);
		solutionPathPanel.add(TSPBudget);
		solutionPathPanel.add(updateTSPBudget);
		top.add(solutionPathPanel);
		//adds a checkbox to display the level in IceCreamYou format
		JPanel effectsCheckboxes = new JPanel();
		JCheckBox iceCreamYou = new JCheckBox("PlayFormat", Parameters.parameters.booleanParameter("showInteractiveLodeRunnerIceCreamYouVisualization"));
		iceCreamYou.setName("showInteractiveLodeRunnerIceCreamYouVisualization");
		iceCreamYou.getAccessibleContext();
		iceCreamYou.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Parameters.parameters.changeBoolean("showInteractiveLodeRunnerIceCreamYouVisualization");
				resetButtons(true);
			}

		});
		effectsCheckboxes.add(iceCreamYou);
		top.add(effectsCheckboxes);
	}

	/**
	 * Sets the GAN to the Lode Runner type 
	 */
	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.LODE_RUNNER;
	}

	/**
	 * The label for the window to specify that they are levels from the Lode Runner GAN Model 
	 * @return The label for the window 
	 */
	@Override
	public String getGANModelParameterName() {
		return "LodeRunnerGANModel";
	}

	/**
	 * Gets a level from a the random latent vector 
	 * @return A single level 
	 */
	@Override
	public List<List<Integer>> levelListRepresentation(double[] latentVector) {
		return LodeRunnerGANUtil.generateOneLevelListRepresentationFromGAN(latentVector);
	}

	/**
	 * This method is the kick-off method that calls the static method below 
	 * @return
	 */
	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		return staticResetAndReLaunchGAN(model);
	}

	/**
	 * This method allows users to pick which model that they want to use in the Level breeder 
	 * @param model The name of the file holding the model 
	 * @return 
	 */
	public static Pair<Integer, Integer> staticResetAndReLaunchGAN(String model) {
		int standardSize = GANProcess.latentVectorLength(); //gets the length of the current GANProcess 
		int updatedSize; //to hold the variable if the size of the latent vector changes
		//if we are using the 6 tile mapping, it sets it to the default model, otherwise it updates the size of the latent vector of the new model 
		if(!(Parameters.parameters.booleanParameter("lodeRunnerDistinguishesSolidAndDiggableGround")) && model.equals("LodeRunnerEpochFirstFiveOneGround10000_20_6.pth")) {
			Parameters.parameters.setInteger("GANInputSize", standardSize); // Default latent vector size
			Parameters.parameters.setBoolean("lodeRunnerDistinguishesSolidAndDiggableGround", false);
		}
		else{
			String latentVectorSize = model.substring(model.indexOf("_")+1, model.lastIndexOf("_"));
			updatedSize = Integer.parseInt(latentVectorSize);
			Parameters.parameters.setInteger("GANInputSize", updatedSize); //updates latent vector size for the GANProcess if it has changed 
			Parameters.parameters.setBoolean("lodeRunnerDistinguishesSolidAndDiggableGround", true);
		}
		GANProcess.terminateGANProcess();
		updatedSize = GANProcess.latentVectorLength(); // new model latent vector length 
		return new Pair<>(standardSize, updatedSize);
	}

	/**
	 * Get the directory that holds the GAN models for Lode Runner 
	 * @return File path as a string 
	 */
	@Override
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"LodeRunnerGAN";
	}

	/**
	 * Allows users to play the levels in the level breeder with the IceCreamYou code to play lode runner  
	 */
	@Override
	public void playLevel(ArrayList<Double> phenotype) { 
		//probably need a few helper methods, one to save to the right place, maybe we need to add a class/method that defaults to the level we pick from the 
		//level breeder instead of the first level of the campaign that IceCreamYou has by default.  
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype); 
		List<List<Integer>> level = levelListRepresentation(doubleArray); 
		//fills this list with all of the empty points in the level, one of these will become the spawn point
		List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level); 
		Random rand = new Random(Double.doubleToLongBits(doubleArray[0]));
		LodeRunnerGANUtil.setSpawn(level, emptySpaces, rand); //sets a consistent and random spawn point 
		//opens play window to play a level from the LevelBreeder 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LodeRunner(level);
			}
		});
	}

	/**
	 * Gets the title of the window 
	 * @return Title of the window as a string
	 */
	@Override
	protected String getWindowTitle() {
		return "LodeRunnerGANLevelBreeder";
	}

	/**
	 * Generates new levels to be put on the buttons in the level breeder 
	 * @return BufferedImage of a generated level 
	 */
	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height,
			double[] inputMultipliers) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = levelListRepresentation(doubleArray);
		//sets the height and width for the rendered level to be placed on the button 
		int width1 = LodeRunnerRenderUtil.RENDERED_IMAGE_WIDTH;
		int height1 = LodeRunnerRenderUtil.RENDERED_IMAGE_HEIGHT;
		BufferedImage image = null;
		try {
			//if we are using the mapping with 7 tiles, other wise use 6 tiles 
			// ACTUALLY: We can have extra unused tiles in the image array. Easier to have one method that keeps them all around
			//			if(Parameters.parameters.booleanParameter("lodeRunnerDistinguishesSolidAndDiggableGround")){
			List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level);
			Random rand = new Random(Double.doubleToLongBits(doubleArray[0]));
			LodeRunnerGANUtil.setSpawn(level, emptySpaces, rand);
			if(Parameters.parameters.booleanParameter("showInteractiveLodeRunnerSolutionPaths")) {
				List<List<Integer>> originalLevel = ListUtil.deepCopyListOfLists(level);
				LodeRunnerState start = new LodeRunnerState(level);
				//				System.out.println(level);
				Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
				HashSet<LodeRunnerState> mostRecentVisited = null;
				ArrayList<LodeRunnerAction> actionSequence = null;
				try {
					//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
					//represented by red x's in the visualization 
					if(Parameters.parameters.integerParameter("interactiveLodeRunnerPathType") == PATH_TYPE_ASTAR) {
						//						System.out.println(level);
						actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
					} else if(Parameters.parameters.integerParameter("interactiveLodeRunnerPathType") == PATH_TYPE_TSP){
						Pair<ArrayList<LodeRunnerAction>, HashSet<LodeRunnerState>> tspInfo = LodeRunnerTSPUtil.getFullActionSequenceAndVisitedStatesTSPGreedySolution(originalLevel);
						actionSequence = tspInfo.t1;
						mostRecentVisited = tspInfo.t2;
						//System.out.println("actionSequence: "+ actionSequence);
						//System.out.println("mostRecentVisited: "+mostRecentVisited);
					} 
					else throw new IllegalArgumentException("Parameter is not either 1 or 0");
				} catch(IllegalStateException e) {
					System.out.println("search exceeded computation budget");
					//e.printStackTrace();
				} catch(OutOfMemoryError e) {
					System.out.println("search ran out of memory");
					//e.printStackTrace();
				} finally {
					// Even if search fails, still try to get visited states.
					// Need this here because A* fails with Exception
					if(Parameters.parameters.integerParameter("interactiveLodeRunnerPathType") == PATH_TYPE_ASTAR) {
						//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
						mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
					}
				}
				try {
					//visualizes the points visited with red and whit x's
					image = LodeRunnerState.vizualizePath(level,mostRecentVisited,actionSequence,start);

				} catch (IOException e) {
					System.out.println("Image could not be displayed");
					//e.printStackTrace();
				}
			}
			else if(Parameters.parameters.booleanParameter("showInteractiveLodeRunnerIceCreamYouVisualization")) {
				BufferedImage[] iceCreamYouImages = LodeRunnerRenderUtil.loadIceCreamYouTiles(LodeRunnerRenderUtil.ICE_CREAM_YOU_TILE_PATH);
				image = LodeRunnerRenderUtil.createIceCreamYouImage(level, LodeRunnerRenderUtil.ICE_CREAM_YOU_IMAGE_WIDTH, LodeRunnerRenderUtil.ICE_CREAM_YOU_IMAGE_HEIGHT, iceCreamYouImages);
			}
			else {
				BufferedImage[] images = LodeRunnerRenderUtil.loadImagesNoSpawnTwoGround(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH); //all tiles 
				image = LodeRunnerRenderUtil.createBufferedImage(level,width1,height1, images);
			}
		} catch (IOException e) {
			System.out.println("Image could not be displayed");
		}
		return image;
	}

	/**
	 * Launches the level breeder, sets GAN input size to 20
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:false","LodeRunnerGANModel:LodeRunnerAllGround20LevelsEpoch20000_10_7.pth","lodeRunnerDistinguishesSolidAndDiggableGround:false","GANInputSize:10","showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.loderunner.LodeRunnerGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
