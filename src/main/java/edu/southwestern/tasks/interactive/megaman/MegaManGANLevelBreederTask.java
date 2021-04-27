package edu.southwestern.tasks.interactive.megaman;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;


import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.MegaManRenderUtil;
import edu.southwestern.tasks.megaman.MegaManTrackSegmentType;
import edu.southwestern.tasks.megaman.MegaManVGLCUtil;
import edu.southwestern.tasks.megaman.astar.MegaManState;
import edu.southwestern.tasks.megaman.astar.MegaManState.MegaManAction;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManOneGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManSevenGANGenerator;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

public class MegaManGANLevelBreederTask extends InteractiveGANLevelEvolutionTask{
	public static final int LEVEL_MIN_CHUNKS = 1;
	public static final int LEVEL_MAX_CHUNKS = 10;
	public static final int SAVE_BUTTON_INDEX = -19; 
	public static final int VIEW_BUTTON_INDEX = -19; 
	public static final int GANS_BUTTON_INDEX = -18; 
	MegaManGANGenerator megaManGenerator;
	MegaManTrackSegmentType segmentCount = new MegaManTrackSegmentType();

	
	//public static GANProcess ganProcessDown = null;
	private boolean initializationComplete = false;
	protected JSlider levelChunksSlider;
	public MegaManGANLevelBreederTask() throws IllegalAccessException {
		super(false);
		//save button
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

		JButton launchMegaManMaker = new JButton("MegaManMaker");
		launchMegaManMaker.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Name is first available numeric label after the input disablers
		launchMegaManMaker.setName("MegaManMaker" + PLAY_BUTTON_INDEX);
		launchMegaManMaker.setToolTipText("Launch MegaManMaker");
		launchMegaManMaker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playLevel(null);
			}
		});
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			launchMegaManMaker.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		
		bottom.add(launchMegaManMaker);
		
		JButton save = new JButton("SaveMMLV");
		save.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Name is first available numeric label after the input disablers
		save.setName("" + SAVE_BUTTON_INDEX);
		save.setToolTipText("Save a selected level.");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLevel();
			}
		});
		
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			save.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		
		bottom.add(save);
		
		
		//frame.add(bottom);
		//topper.add(bottom);
		JButton view = new JButton("View");
		view.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Name is first available numeric label after the input disablers
		view.setName("view" + VIEW_BUTTON_INDEX);
		view.setToolTipText("Launch MegaManMaker");
		view.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewLevel();
			}
		});
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			view.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		
		bottom.add(view);
		top.add(bottom);

		//horizontal slider for level chunks
		levelChunksSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		levelChunksSlider.setToolTipText("Determines the number of distinct latent vectors that are sent to the GAN to create level chunks which are patched together into a single level.");
		levelChunksSlider.setMinorTickSpacing(1);
		levelChunksSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		JLabel shorter = new JLabel("Shorter Level");
		JLabel longer = new JLabel("Longer Level");
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			shorter.setFont(new Font("Arial", Font.PLAIN, 23));
			longer.setFont(new Font("Arial", Font.PLAIN, 23));
		}
		labels.put(LEVEL_MIN_CHUNKS, shorter);
		labels.put(LEVEL_MAX_CHUNKS, longer);
		levelChunksSlider.setLabelTable(labels);
		levelChunksSlider.setPaintLabels(true);
		levelChunksSlider.setPreferredSize(new Dimension((int)(200 * (Parameters.parameters.booleanParameter("bigInteractiveButtons") ? 1.4 : 1)), 40 * (Parameters.parameters.booleanParameter("bigInteractiveButtons") ? 2 : 1)));

		/**
		 * Changed level width picture previews
		 */
		levelChunksSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!initializationComplete) return;
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					
					int oldValue = Parameters.parameters.integerParameter("megaManGANLevelChunks");
					int newValue = (int) source.getValue();
					Parameters.parameters.setInteger("megaManGANLevelChunks", newValue);
					

					if(oldValue != newValue) {
						int oldLength = oldValue * (GANProcess.latentVectorLength()+MegaManGANGenerator.numberOfAuxiliaryVariables());
						int newLength = newValue * (GANProcess.latentVectorLength()+MegaManGANGenerator.numberOfAuxiliaryVariables());
				
						resizeGenotypeVectors(oldLength, newLength);
						resetButtons(true);

						// reset buttons
					}
				}
			}
		});

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(levelChunksSlider);	
		}

		initializationComplete = true;
		
		
		//adds the ability to show the solution path
		
		JPanel effectsCheckboxes = new JPanel();
		
		JPanel aSTAR = new JPanel();
		aSTAR.setLayout(new BoxLayout(aSTAR, BoxLayout.Y_AXIS));
		JCheckBox showSolutionPath = new JCheckBox("ShowSolutionPath", Parameters.parameters.booleanParameter("interactiveMegaManAStarPaths"));
		showSolutionPath.setAlignmentX(Component.CENTER_ALIGNMENT);
		showSolutionPath.setName("interactiveMegaManAStarPaths");
		showSolutionPath.getAccessibleContext();
		showSolutionPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Parameters.parameters.changeBoolean("interactiveMegaManAStarPaths");
				resetButtons(true);
			}
		});
		aSTAR.add(showSolutionPath);
		
		//JTextField aStLb = new JTextField();
		
		JPanel AStarBudget = new JPanel();
		AStarBudget.setLayout(new BoxLayout(AStarBudget, BoxLayout.Y_AXIS));

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
						return;
					}
					int value = Integer.parseInt(budget);
					Parameters.parameters.setInteger("aStarSearchBudget", value);
					resetButtons(true);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		AStarBudget.add(AStarLabel);
		AStarBudget.add(updateAStarBudget);
		//top.add(AStarBudget);
		aSTAR.add(AStarBudget);
		top.add(aSTAR);
		
		
		JPanel platformAndBreak = new JPanel();
		platformAndBreak.setLayout(new BoxLayout(platformAndBreak, BoxLayout.Y_AXIS));
		JCheckBox allowLeftGeneration = new JCheckBox("AllowLeftGeneration", Parameters.parameters.booleanParameter("megaManAllowsLeftSegments"));
		allowLeftGeneration.setName("allowLeftGeneration");
		allowLeftGeneration.getAccessibleContext();
		allowLeftGeneration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int continueOption = JOptionPane.showConfirmDialog(null,"Warning! Changing this setting will reset the evolved population. Continue anyway?");
				if(continueOption == 0) { //0 means user pressed yes
					int oldLength = Parameters.parameters.integerParameter("megaManGANLevelChunks") * (GANProcess.latentVectorLength()+MegaManGANGenerator.numberOfAuxiliaryVariables());

					Parameters.parameters.changeBoolean("megaManAllowsLeftSegments");
					
					int newLength = Parameters.parameters.integerParameter("megaManGANLevelChunks") * (GANProcess.latentVectorLength()+MegaManGANGenerator.numberOfAuxiliaryVariables());
				
					resizeGenotypeVectors(oldLength, newLength);
					resetButtons(true);

				}else { //user pressed something other than yes
					boolean changeTo = true;
					if(allowLeftGeneration.isSelected()) changeTo=false;
					allowLeftGeneration.setSelected(changeTo);
				}
				
			}
		});
		platformAndBreak.add(allowLeftGeneration);
		
		
		
		JCheckBox allowPlatformGun = new JCheckBox("AllowPlatformGun", Parameters.parameters.booleanParameter("megaManAllowsPlatformGun"));
		allowPlatformGun.setName("allowPlatformGun");
		allowPlatformGun.getAccessibleContext();
		allowPlatformGun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Parameters.parameters.changeBoolean("megaManAllowsPlatformGun");
				resetButtons(true);
			}
		});
		platformAndBreak.add(allowPlatformGun);

		
		
		JCheckBox allowBlockBreaker = new JCheckBox("AllowBlockBreaker", Parameters.parameters.booleanParameter("megaManAllowsBlockBreaker"));
		allowBlockBreaker.setName("allowBlockBreaker");
		allowBlockBreaker.getAccessibleContext();
		allowBlockBreaker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Parameters.parameters.changeBoolean("megaManAllowsBlockBreaker");
				resetButtons(true);
			}
		});
		platformAndBreak.add(allowBlockBreaker);
		top.add(platformAndBreak);
		
		
		
		JPanel MultipleGANs = new JPanel();
		MultipleGANs.setLayout(new BoxLayout(MultipleGANs, BoxLayout.Y_AXIS));
		JButton fileLoadButton = new JButton();
		fileLoadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		fileLoadButton.setText("SetGANModelHorizontal");
		fileLoadButton.setName("GANModelHorizontal"+GANS_BUTTON_INDEX);
		fileLoadButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String modelName = "MegaManGANHorizontalModel";
				openGANModelPanel(modelName);
			}

			
			
		});
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			fileLoadButton.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		MultipleGANs.add(fileLoadButton);
		
		JButton fileLoadButton1 = new JButton();
		fileLoadButton1.setAlignmentX(Component.CENTER_ALIGNMENT);
		fileLoadButton1.setText("SetGANModelUp");
		fileLoadButton1.setName("GANModelUp");
		fileLoadButton1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String modelName = "MegaManGANUpModel";
				openGANModelPanel(modelName);

			}
			
		});
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			fileLoadButton1.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		MultipleGANs.add(fileLoadButton1);
		JButton fileLoadButton2 = new JButton();
		fileLoadButton2.setAlignmentX(Component.CENTER_ALIGNMENT);
		fileLoadButton2.setText("SetGANModelDown");
		fileLoadButton2.setName("GANModelDown");
		fileLoadButton2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String modelName = "MegaManGANDownModel";
				openGANModelPanel(modelName);

			}
			
		});
		
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			fileLoadButton2.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		MultipleGANs.add(fileLoadButton2);
		MultipleGANs.setVisible(false);
//		top.add(MultipleGANs);
		//whether or not to use both GANs **NOTE** need to change initialization for when there are more tile types
		JCheckBox useMultipleGANs = new JCheckBox("UseMultipleGANs", Parameters.parameters.booleanParameter("useMultipleGANsMegaMan"));
		useMultipleGANs.setName("useMultipleGANsMegaMan");
		useMultipleGANs.getAccessibleContext();
		useMultipleGANs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Parameters.parameters.changeBoolean("useMultipleGANsMegaMan");
				Parameters.parameters.changeBoolean("showInteractiveGANModelLoader");
				top.getComponent(3).setVisible(Parameters.parameters.booleanParameter("showInteractiveGANModelLoader"));
				if(Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")) {
					
					GANProcess.terminateGANProcess();
				
					megaManGenerator = new MegaManSevenGANGenerator();
				
					MultipleGANs.setVisible(true);

				}else {
					megaManGenerator = new MegaManOneGANGenerator();

					MultipleGANs.setVisible(false);

				}
				
				
				resetButtons(true);
			}
		});
		
		effectsCheckboxes.add(useMultipleGANs);
		top.add(effectsCheckboxes);
		
		
		//if(!Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")){
			
			
			
		}
		
		//Ability to change A* budget
		
	
	
	//}

	@Override
	/**
	 * configures the GAN to MegaMan type
	 */
	public void configureGAN() { //sets GAN to megaman
		GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN;
		megaManGenerator = new MegaManOneGANGenerator();

		
	}
	private void openGANModelPanel(String modelName) {
		JFileChooser chooser = new JFileChooser();//used to get new file
		chooser.setApproveButtonText("Open");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("GAN Model", "pth");
		chooser.setFileFilter(filter);
		// This is where all the GANs are stored (only allowable spot)
		chooser.setCurrentDirectory(new File(getGANModelDirectory()));
		int returnVal = chooser.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
			String model = chooser.getSelectedFile().getName();
			Parameters.parameters.setString(modelName, model);
			Pair<Integer, Integer> lengths = resetAndReLaunchGAN(model);
			resizeGenotypeVectors(lengths.t1, lengths.t2);
		}
		resetButtons(true);
	}
	@Override
	/**
	 * returns the command line parameter associated with the MegaMan GAN model
	 */
	public String getGANModelParameterName() { 
		// TODO Auto-generated method stub
		return "MegaManGANModel";
	}

	@Override
	/**
	 * resets and relaunches GAN
	 * @param model the GAN model used
	 */
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		return staticResetAndReLaunchGAN(model);
	}
	/**
	 * resets and relaunches GAN
	 * @param model the GAN model used
	 */
	public static Pair<Integer, Integer> staticResetAndReLaunchGAN(String model) {
		int megaManGANLevelChunks = Parameters.parameters.integerParameter("megaManGANLevelChunks");
		int oldLength = megaManGANLevelChunks * GANProcess.latentVectorLength();
		Parameters.parameters.setInteger("GANInputSize", 5); // Default latent vector size
		
		GANProcess.terminateGANProcess();
		// Because Python process was terminated, latentVectorLength will reinitialize with the new params
		int newLength = megaManGANLevelChunks * GANProcess.latentVectorLength(); // new model
		return new Pair<>(oldLength,newLength);
	}
	
	
	@Override
	/**
	 * gets the GAN model directory
	 */
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"MegaManGAN";
	}
	public void viewLevel() {
		ArrayList<Double> phenotype = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = levelListRepresentation(doubleArray);
		 //Initializes the array that hold the tile images 
		if(selectedItems.size() != 1) {
			JOptionPane.showMessageDialog(null, "Select exactly one level to view.");
			return; // Nothing to explore
		}
		try {
			if(selectedItems.size() != 1) {
				JOptionPane.showMessageDialog(null, "Select exactly one level to save.");
				return; // Nothing to explore
			}
			//List<List<List<Integer>>> levelInList = MegaManGANUtil.getLevelListRepresentationFromGAN(GANProcess.getGANProcess(), doubleArray);
//			int width1 = MegaManRenderUtil.renderedImageWidth(level.get(0).size());
//			int height1 = MegaManRenderUtil.renderedImageHeight(level.size());
			BufferedImage[] images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH);
			MegaManRenderUtil.getBufferedImageWithRelativeRendering(level, images);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //puts the final rendered level into a buffered image
		//int levelNumber = 2020;
		//mmlvFile = MegaManVGLCUtil.convertMegaManLevelToMMLV(level, mmlvFileName);
	}
	/**
	 * saves the level in the directory of MegaManMaker levels.
	 * Basically it reads the MegaManMakerLevelPath and saves the
	 * selected level into an mmlv file named by the user
	 */
	public void saveLevel() {
		File mmlvFilePath = new File("MegaManMakerLevelPath.txt"); //file containing the path

		
		Scanner scan;
		//When the button is pushed, ask for the name input
		try {
			scan = new Scanner(mmlvFilePath);
			//scan.next();
			String mmlvPath = scan.nextLine();
			String mmlvFileName = JOptionPane.showInputDialog(null, "What do you want to name your level?");
			//File mmlvFileFromEvolution = new File(mmlvPath+mmlvFileName+".mmlv"); //creates file inside user's MegaManLevelPath
			@SuppressWarnings("unused")
			File mmlvFile; //creates file inside MMNEAT
			scan.close();
			if(selectedItems.size() != 1) {
				JOptionPane.showMessageDialog(null, "Select exactly one level to save.");
				return; // Nothing to explore
			}

			ArrayList<Double> phenotype = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
			double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
			List<List<Integer>> level = levelListRepresentation(doubleArray);
			//int levelNumber = 2020;
			mmlvFile = MegaManVGLCUtil.convertMegaManLevelToMMLV(level, mmlvFileName, mmlvPath);
			//Files.copy(mmlvFile, mmlvFileFromEvolution); //copies over
			//mmlvFile.delete(); //deletes MMNEAT file
			JOptionPane.showMessageDialog(frame, "Level saved to: "+mmlvPath);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			String errorMessage = "You need to create a local text file in the MMNEAT directory called \n MegaManMakerLevelPath.txt which contains the path to where MegaManMaker stores levels on your device. \n It will likely look like this: C:\\Users\\[Insert User Name]\\AppData\\Local\\MegaMaker\\Levels\\";
			JOptionPane.showMessageDialog(frame, errorMessage);
		}
		
		
	}
	@Override
	//Will eventually launch megamanmaker
	public void playLevel(ArrayList<Double> phenotype) {
		launchMegaManStatic(frame);
	}

	public static void launchMegaManStatic(JFrame frame) {
		File mmlvFilePath = new File("MegaManMakerPath.txt"); //file containing the path
//		if(selectedItems.size() != 1) {
//			JOptionPane.showMessageDialog(null, "Save exactly one level to play.");
//			return; // Nothing to explore
//		}
		
		Scanner scan;
		//When the button is pushed, ask for the name input
		try {
			scan = new Scanner(mmlvFilePath);
			
			
			String mmlvPath = scan.nextLine();
			scan.close();
			
			Runtime runTime = Runtime.getRuntime();
			@SuppressWarnings("unused")
			Process process = runTime.exec(mmlvPath);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			String errorMessage = "You need to create a local text file in the MMNEAT directory called \n MegaManMakerPath.txt which contains the path to where MegaManMaker.exe is stored on your device";
			JOptionPane.showMessageDialog(frame, errorMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getWindowTitle() {
		// TODO Auto-generated method stub
		return "MegaManGANLevelBreeder";
	}

	@Override
	/**
	 * Determines whether or not to allow vertical or horizontal stretching based on the GAN model
	 */
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height,
			double[] inputMultipliers) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);

		List<List<Integer>> level = levelListRepresentation(doubleArray);

		return getStaticButtonImage(phenotype, width, height, level);
	}

	public static BufferedImage getStaticButtonImage(ArrayList<Double> phenotype, int width, int height, List<List<Integer>> level) {
		//MegaManVGLCUtil.printLevel(level);
		BufferedImage[] images;
		//sets the height and width for the rendered level to be placed on the button 
		int width1 = MegaManRenderUtil.renderedImageWidth(level.get(0).size());
		int height1 = MegaManRenderUtil.renderedImageHeight(level.size());
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		if(Parameters.parameters.booleanParameter("interactiveMegaManAStarPaths")) {
			MegaManState start = new MegaManState(level);
			Search<MegaManAction,MegaManState> search = new AStarSearch<>(MegaManState.manhattanToOrb);
			HashSet<MegaManState> mostRecentVisited = null;
			ArrayList<MegaManAction> actionSequence = null;
			try {
				//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
				//represented by red x's in the visualization 
				actionSequence = ((AStarSearch<MegaManAction, MegaManState>) search).search(start, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
			} catch(Exception e) {
				System.out.println("failed search");
				e.printStackTrace();
			}
			//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
			mostRecentVisited = ((AStarSearch<MegaManAction, MegaManState>) search).getVisited();
			try {
				image = MegaManState.vizualizePath(level,mostRecentVisited,actionSequence,start);
			}catch(IOException e) {
				e.printStackTrace();
	
			}
		}else {
		try {
			//

			images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH); //7 different tiles to display 
			image = MegaManRenderUtil.createBufferedImage(level,width1,height1, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		return image;
	}
	
	
	@Override
	/**
	 * takes in a double array from the GAN and uses it to generate a level based on which GAN model is being used
	 * @param doubleArray the vector from the GAN
	 * @return level the level generated from the GAN
	 */
	public List<List<Integer>> levelListRepresentation(double[] doubleArray) {
		List<List<Integer>> level;
		System.out.println(doubleArray.length);
		if (Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")){
			level = MegaManGANUtil.longVectorToMegaManLevel(megaManGenerator, doubleArray, Parameters.parameters.integerParameter("megaManGANLevelChunks"), segmentCount);
		}
		else if(Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLY")) { //if horiontal GAN model
			level = MegaManGANUtil.generateOneLevelListRepresentationFromGANHorizontal(doubleArray);
			placeSpawnAndLevelOrbHorizontal(level);
		}else if(Parameters.parameters.stringParameter("MegaManGANModel").startsWith("VERTICALONLY")){ //if vertical GAN model
			level = MegaManGANUtil.generateOneLevelListRepresentationFromGANVertical(doubleArray);
			placeSpawnAndLevelOrbVertical(level);
		}else {
			level = MegaManGANUtil.longVectorToMegaManLevel(megaManGenerator, doubleArray, Parameters.parameters.integerParameter("megaManGANLevelChunks"), segmentCount);
			//placeSpawnAndLevelOrbHorizontal(level);			
		}
		if(!Parameters.parameters.booleanParameter("megaManUsesUniqueEnemies")) {
			MegaManGANUtil.postProcessingPlaceProperEnemies(level);
		}
		return level;
	}

	/**
	 * places the spawn point and the orb based on a vertical level
	 * @param level the level
	 */
	private void placeSpawnAndLevelOrbVertical(List<List<Integer>> level) {
		boolean placed = false;
		for(int x = 0;x<level.get(0).size();x++) {
			for(int y = level.size()-1;y>=0;y--) {
				if(y-2>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0&&level.get(y-2).get(x)==0) {
					level.get(y-1).set(x, 8);
					placed = true;
					break;
				}
			}
			if(placed) {
				break;
			}
			
		
		}
		for(int i = 0; i<level.get(0).size();i++) {
			if(!placed) {
				level.get(level.size()-1).set(0, 1);
				level.get(level.size()-2).set(0, 8);
				placed = true;
			}
		}
		placed = false;
		for(int y = 0; y<level.size();y++) {
			for(int x = level.get(0).size()-1;x>=0; x--) {
				if(y-1>=0&&level.get(y).get(x)==2&&level.get(y-1).get(x)==0) {
					level.get(y-1).set(x, 7);
					placed=true;
					break;
					
				}else if(y-1>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0) {
					level.get(y-1).set(x, 7);
					placed=true;
					break;
				}
			}
			if(placed) break;
		}
	}
	/**
	 * places the spawn point and the orb based on a horizontal level
	 * @param level the level
	 */
	private void placeSpawnAndLevelOrbHorizontal(List<List<Integer>> level) { //7 orb 8 spawn
		//int prevY = 0;
		boolean rtrn = false;
		for(int x = 0;x<level.get(0).size();x++) {
			for(int y = 0;y<level.size();y++) {
				if(y-2>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0&&level.get(y-2).get(x)==0) {
					level.get(y-1).set(x, 8);
					rtrn  = true;
					break;
				}
			}
			if(rtrn) {
				rtrn = false;
				break;
			}
		}
		
		
		for(int x = level.get(0).size()-1;x>=0; x--) {
			for(int y = 0; y<level.size();y++) {
				if(y-1>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0) {
					level.get(y-1).set(x, 7);
					rtrn = true;
					break;
				}
			}
			if(rtrn) break;
		}
	}
	@Override
	public void finalCleanup() {
		GANProcess.terminateGANProcess();
	}
	/**
	 * Launches the level breeder, sets GAN input size to 5
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","megaManGANLevelChunks:10","bigInteractiveButtons:false","megaManUsesUniqueEnemies:false","GANInputSize:5","useMultipleGANsMegaMan:true","showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.megaman.MegaManGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
