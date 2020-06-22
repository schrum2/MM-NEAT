package edu.southwestern.tasks.megaman;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

import java.util.Hashtable;


import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;

public class MegaManCPPNtoGANLevelBreederTask extends InteractiveEvolutionTask<TWEANN>{
	public static final String[] SENSOR_LABELS = new String[] {"x-coordinate", "y-coordinate", "radius", "bias"};

	
	
	
	public static final int VIEW_BUTTON_INDEX = -19; 
	public static final int GANS_BUTTON_INDEX = -18; 
	public static final int PLAY_BUTTON_INDEX = -20; 

	
	public static final int UP_PREFERENCE = 0; 
	public static final int DOWN_PREFERENCE = 1; 
	public static final int HORIZONTAL_PREFERENCE = 2; 
	public static final int NUM_NON_LATENT_INPUTS = 3; //the first six values in the latent vector

	
	private static final int LEVEL_MIN_CHUNKS = 1;
	private static final int LEVEL_MAX_CHUNKS = 10; 
	private String[] outputLabels;
	public static GANProcess ganProcessDown = null;
	public static GANProcess ganProcessHorizontal = null;
	public static GANProcess ganProcessUp = null;
	private boolean initializationComplete = false;
	
	

	public MegaManCPPNtoGANLevelBreederTask() throws IllegalAccessException {
		super();
		// TODO Auto-generated constructor stub
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

						resizeGenotypeVectors();
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
		
		top.add(effectsCheckboxes);
		
		initializationComplete = true;
	}
	private void openGANModelPanel(String modelName) {
		// TODO Auto-generated method stub
		
	}
	
	private void resizeGenotypeVectors() {
		// TODO Auto-generated method stub
		
	}
	
	
	private void viewLevel() {
		// TODO Auto-generated method stub
		
	}
	
	private void saveLevel() {
//		File mmlvFilePath = new File("MegaManMakerLevelPath.txt"); //file containing the path

//		
//		Scanner scan;
//		//When the button is pushed, ask for the name input
//		try {
//			scan = new Scanner(mmlvFilePath);
//			//scan.next();
//			String mmlvPath = scan.nextLine();
//			String mmlvFileName = JOptionPane.showInputDialog(null, "What do you want to name your level?");
//			File mmlvFileFromEvolution = new File(mmlvPath+mmlvFileName+".mmlv"); //creates file inside user's MegaManLevelPath
//			File mmlvFile; //creates file inside MMNEAT
//			scan.close();
//			if(selectedItems.size() != 1) {
//				JOptionPane.showMessageDialog(null, "Select exactly one level to save.");
//				return; // Nothing to explore
//			}
//
//			ArrayList<Double> phenotype = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
//			double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
//			List<List<Integer>> level = levelListRepresentation(doubleArray);
//			//int levelNumber = 2020;
//			mmlvFile = MegaManVGLCUtil.convertMegaManLevelToMMLV(level, mmlvFileName);
//			try {
//				Files.copy(mmlvFile, mmlvFileFromEvolution); //copies over
//				mmlvFile.delete(); //deletes MMNEAT file
//				JOptionPane.showMessageDialog(frame, "Level saved");
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//System.out.println(mmlvPath);
//			
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			String errorMessage = "You need to create a local text file in the MMNEAT directory called \n MegaManMakerLevelPath.txt which contains the path to where MegaManMaker stores levels on your device. \n It will likely look like this: C:\\Users\\[Insert User Name]\\AppData\\Local\\MegaMaker\\Levels\\";
//			JOptionPane.showMessageDialog(frame, errorMessage);
//		}		
	}
	
	private void playLevel() {
		// TODO Auto-generated method stub
		
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
//		List<List<Integer>> level = cppnToMegaManLevel(phenotype, width, height, inputMultipliers);
		return null;
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

	@Override
	public int numCPPNInputs() {
		// TODO Auto-generated method stub
		return this.sensorLabels().length;
	}
	private void resetLatentVectorAndOutputs() {
		int latentVectorLength = GANProcess.latentVectorLength();
		outputLabels = new String[latentVectorLength + numberOfNonLatentVariables()];
		outputLabels[UP_PREFERENCE] = "Up Presence";
		outputLabels[DOWN_PREFERENCE] = "Down Preference";
		outputLabels[HORIZONTAL_PREFERENCE] = "Horizontal Preference";
		for(int i = numberOfNonLatentVariables(); i < outputLabels.length; i++) {
			outputLabels[i] = "LV"+(i-numberOfNonLatentVariables());
		}
	}
	private int numberOfNonLatentVariables() {
		return NUM_NON_LATENT_INPUTS;
	}
	@Override
	public int numCPPNOutputs() {
		// TODO Auto-generated method stub
		return this.outputLabels().length;
	}
	
	
	
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"MegaManGAN";
	}

}
