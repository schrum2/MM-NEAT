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
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.MegaManCPPNtoGANUtil;
import edu.southwestern.tasks.megaman.MegaManRenderUtil;
import edu.southwestern.tasks.megaman.MegaManTrackSegmentType;
import edu.southwestern.tasks.megaman.MegaManVGLCUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManOneGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManSevenGANGenerator;

public class MegaManCPPNtoGANLevelBreederTask extends InteractiveEvolutionTask<TWEANN>{
	public static final String[] SENSOR_LABELS = new String[] {"x-coordinate", "y-coordinate", "bias"};

	public static final int VIEW_BUTTON_INDEX = -19; 
	public static final int GANS_BUTTON_INDEX = -18; 
	public static final int PLAY_BUTTON_INDEX = -20; 
	public static final int SAVE_BUTTON_INDEX = -21; 
	
	// TODO: Will eliminate these to move into MegaManGANGenerator
	public static final int UP_PREFERENCE = 0; 
	public static final int DOWN_PREFERENCE = 1; 
	public static final int RIGHT_PREFERENCE = 2; 
	public static final int LEFT_PREFERENCE = 3; 

	// TODO: In order to support LEFT, this will need to be a method rather than a constant
	//public static final int NUM_NON_LATENT_INPUTS = 3; //the first three values in the latent vector

	private static final int LEVEL_MIN_CHUNKS = 1;
	private static final int LEVEL_MAX_CHUNKS = 10;

//	private static final int FILE_LOADER_BUTTON_INDEX = -21; 
	
	private String[] outputLabels;

	MegaManGANGenerator megaManGenerator;
	MegaManTrackSegmentType segmentCount = new MegaManTrackSegmentType();
	
	private boolean initializationComplete = false;
	
	

	public MegaManCPPNtoGANLevelBreederTask() throws IllegalAccessException {
		super();
//		JButton fileLoadButton5 = new JButton();
//		fileLoadButton5.setText("SelectGANModel");
//		fileLoadButton5.setName("" + FILE_LOADER_BUTTON_INDEX);
//		fileLoadButton5.addActionListener(this);
//		top.add(fileLoadButton5);
		if(Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")) megaManGenerator = new MegaManSevenGANGenerator();
		else  megaManGenerator = new MegaManOneGANGenerator();

		
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
				playLevel();
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
				save(null, 0);
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
				viewLevel(scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype());
			}

			
		});
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			view.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		
		bottom.add(view);
		top.add(bottom);

		//horizontal slider for level chunks
		JSlider levelChunksSlider;
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
					//Parameters.parameters.setInteger("GANInputSize", 5*newValue); // Default latent vector size

					if(oldValue != newValue) {
//						int oldLength = oldValue * GANProcess.latentVectorLength();
//						int newLength = newValue * GANProcess.latentVectorLength();

						resetLatentVectorAndOutputs();
						reset();

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
					Parameters.parameters.changeBoolean("megaManAllowsLeftSegments");
					MMNEAT.setNNInputParameters(SENSOR_LABELS.length, Parameters.parameters.integerParameter("GANInputSize")+MegaManGANGenerator.numberOfAuxiliaryVariables());

					resetLatentVectorAndOutputs();
					reset();
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
		
		
		
		JPanel threeGANs = new JPanel();
		threeGANs.setLayout(new BoxLayout(threeGANs, BoxLayout.Y_AXIS));
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
		threeGANs.add(fileLoadButton);
		
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
		threeGANs.add(fileLoadButton1);
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
		threeGANs.add(fileLoadButton2);
//		top.add(threeGANs);
		
		
		JCheckBox useMultipleGANs = new JCheckBox("UseMultipleGANs", Parameters.parameters.booleanParameter("useMultipleGANsMegaMan"));
		useMultipleGANs.setName("useMultipleGANsMegaMan");
		useMultipleGANs.getAccessibleContext();
		useMultipleGANs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Parameters.parameters.changeBoolean("useMultipleGANsMegaMan");
				Parameters.parameters.changeBoolean("showInteractiveGANModelLoader");
				top.getComponent(3).setVisible(false);
				
				segmentCount = new MegaManTrackSegmentType();
				if(Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")) {
					
					GANProcess.terminateGANProcess();
				
					megaManGenerator = new MegaManSevenGANGenerator();
				
//					MultipleGANs.setVisible(true);

				}else {
					megaManGenerator = new MegaManOneGANGenerator();

//					MultipleGANs.setVisible(false);

				}
				
				
				resetButtons(true);
			}
		});
		
		effectsCheckboxes.add(useMultipleGANs);
		top.add(effectsCheckboxes);
		resetLatentVectorAndOutputs();

		initializationComplete = true;
	}
	private void openGANModelPanel(String modelName) {
		
	}

	
	
	private void viewLevel(TWEANN phenotype) {
		// TODO Auto-generated method stub
		List<List<Integer>> level = MegaManCPPNtoGANUtil.cppnToMegaManLevel(megaManGenerator, phenotype, Parameters.parameters.integerParameter("megaManGANLevelChunks"), inputMultipliers, segmentCount);

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
	
	private void saveLevel() {
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

			TWEANN phenotype = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
			List<List<Integer>> level = MegaManCPPNtoGANUtil.cppnToMegaManLevel(megaManGenerator, phenotype, Parameters.parameters.integerParameter("megaManGANLevelChunks"), inputMultipliers, segmentCount);

//			double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
//			List<List<Integer>> level = levelListRepresentation(doubleArray);
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
	
	private void playLevel() {
		MegaManGANLevelBreederTask.launchMegaManStatic(frame);
	}
	@Override
	public String[] sensorLabels() {
		// TODO Auto-generated method stub
		return SENSOR_LABELS;
	}

	@Override
	public String[] outputLabels() {
		// TODO Auto-generated method stub
		return outputLabels;
	}

	@Override
	protected String getWindowTitle() {
		// TODO Auto-generated method stub
		return "Mega Man CPPN to GAN Breeder";
	}

	@Override
	protected void save(String file, int i) {
		saveLevel();
	}

	@Override
	protected BufferedImage getButtonImage(TWEANN phenotype, int width, int height, double[] inputMultipliers) {
		// TODO Auto-generated method stub
		List<List<Integer>> level = MegaManCPPNtoGANUtil.cppnToMegaManLevel(megaManGenerator, phenotype, Parameters.parameters.integerParameter("megaManGANLevelChunks"), inputMultipliers, segmentCount);
		return MegaManGANLevelBreederTask.getStaticButtonImage(null, width, height, level);

	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<TWEANN> individual) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getFileType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFileExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	
	private void resetLatentVectorAndOutputs() {
		int latentVectorLength = Parameters.parameters.integerParameter("GANInputSize");
		outputLabels = new String[latentVectorLength + MegaManGANGenerator.numberOfAuxiliaryVariables()];
		outputLabels[UP_PREFERENCE] = "Up Presence";
		outputLabels[DOWN_PREFERENCE] = "Down Preference";
		outputLabels[RIGHT_PREFERENCE] = "Right Preference";
		if(Parameters.parameters.booleanParameter("megaManAllowsLeftSegments"))	{
			System.out.println("Left is active");
			
			outputLabels[LEFT_PREFERENCE] = "Left Preference";
			System.out.println(outputLabels.length);
		}

		for(int i = MegaManGANGenerator.numberOfAuxiliaryVariables(); i < outputLabels.length; i++) {
			outputLabels[i] = "LV"+(i-MegaManGANGenerator.numberOfAuxiliaryVariables());
		}
	}

	@Override
	public int numCPPNInputs() {
		// TODO Auto-generated method stub
		return this.sensorLabels().length;
	}
	
	public static int staticNumCPPNInputs() {
		return SENSOR_LABELS.length;
	}
	@Override
	public int numCPPNOutputs() {
		return this.outputLabels().length;
	}
	public static int staticNumCPPNOutputs() {
		return MegaManGANGenerator.numberOfAuxiliaryVariables()+Parameters.parameters.integerParameter("GANInputSize");
	}
	
	
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"MegaManGAN";
	}
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","useMultipleGANsMegaMan:false","showKLOptions:false","trials:1","mu:16", "base:megaManGAN",
					"maxGens:500","io:false","netio:false","GANInputSize:5","mating:true","fs:false","megaManGANLevelChunks:10",
					"task:edu.southwestern.tasks.interactive.megaman.MegaManCPPNtoGANLevelBreederTask","cleanOldNetworks:false", 
					"allowMultipleFunctions:true","ftype:0","watch:true","netChangeActivationRate:0.3","cleanFrequency:-1",
					"simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false",
					"ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000",
					"imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true",
					"includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true",
					"includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		
//		stringOptions.add("MegaManGANHorizontalModel", "HORIZONTALONLYUniqueEnemiesMegaManAllLevelsBut7With30TileTypes_5_Epoch5000.pth", "File name of Horizontal GAN model to use for MegaMan GAN level evolution");
//		stringOptions.add("MegaManGANVerticalModel", "VERTICALONLYMegaManAllLevelsWith7Tiles_5_Epoch5000.pth", "File name of Vertical GAN model to use for MegaMan GAN level evolution");
//		stringOptions.add("MegaManGANUpModel", "VERTICALONLYUPUniqueEnemiesMegaManAllLevelsBut7With30TileTypes_5_Epoch5000.pth", "File name of Vertical GAN model to use for MegaMan GAN level evolution");
//		stringOptions.add("MegaManGANDownModel", "VERTICALONLYDOWNUniqueEnemiesMegaManAllLevelsBut7With30TileTypes_5_Epoch5000.pth", "File name of Vertical GAN model to use for MegaMan GAN level evolution");

	}
}
