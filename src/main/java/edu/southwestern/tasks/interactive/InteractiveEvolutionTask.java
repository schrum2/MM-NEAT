package edu.southwestern.tasks.interactive;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.SinglePopulationGenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.evolution.mutation.tweann.ActivationFunctionRandomReplacement;
import edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.util.BooleanUtil;
import edu.southwestern.util.CombinatoricUtilities;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Class that builds an interface designed for interactive evolution. 
 * Generates a series of images from CPPNs that can be evolved based on
 * each other and a variety of activation functions that can be turned on
 * and off. Used to generate several interactive programs, including 
 * Picbreeder, Breedesizer, SoundRemixer, PictureRemixer, AnimationBreeder,
 * 3DObjectBreeder, and 3DAnimationBreeder.
 * 
 * @author Lauren Gillespie
 * @author Isabel Tweraser
 *
 * @param <T>
 */
@SuppressWarnings("unused")
public abstract class InteractiveEvolutionTask<T> implements SinglePopulationTask<T>, ActionListener, ChangeListener, NetworkTask {
	
	//Global static final variables
	public static final int NUM_COLUMNS	= 5;
	public static final int MPG_DEFAULT = 2;// Starting number of mutations per generation (on slider)	
	// Offset for checkbox id numbers assigned to activation function checkboxes
	public static final int ACTIVATION_CHECKBOX_OFFSET = 100;

	//private static final Variables
	//includes indices of buttons for action listener
	private static final int IMAGE_BUTTON_INDEX = 0;
	private static final int EVOLVE_BUTTON_INDEX = -1;
	private static final int SAVE_BUTTON_INDEX = -2;
	private static final int RESET_BUTTON_INDEX = -3;
	//private static final int CLOSE_BUTTON_INDEX	= -4;
	//private static final int LINEAGE_BUTTON_INDEX = -5;
	private static final int NETWORK_BUTTON_INDEX = -6;
	private static final int UNDO_BUTTON_INDEX = -7;

	private static final int BORDER_THICKNESS = 4;
	private static final int MPG_MIN = 0;//minimum # of mutations per generation
	private static final int MPG_MAX = 10;//maximum # of mutations per generation

	// Activation Button Widths and Heights
	private static final int ACTION_BUTTON_WIDTH = 80;
	private static final int ACTION_BUTTON_HEIGHT = 60;	

	//Private final variables
	private static int numRows;
	protected static int picSize;
	private static int numButtonOptions;

	//Private graphic objects
	protected JFrame frame;
	private ArrayList<JPanel> panels;
	protected ArrayList<JButton> buttons;
	protected ArrayList<Score<T>> scores;
	private ArrayList<Score<T>> previousScores;

	//private helper variables
	private boolean showLineage;
	protected boolean showNetwork;
	private boolean waitingForUser;
	protected final boolean[] chosen;
	private final boolean[] activation;
	protected double[] inputMultipliers;

	// This is a weird magic number that is used to track the checkboxes
	public static final int CHECKBOX_IDENTIFIER_START = -25;

	protected T currentCPPN;

	private HashMap<Long,BufferedImage> cachedButtonImages = new HashMap<Long,BufferedImage>();

	private JPanel topper;
	protected JPanel top;

	public LinkedList<Integer> selectedItems;

	public InteractiveEvolutionTask() throws IllegalAccessException {		
		this(true); // By default, evolve CPPNs
	}
	
	/**
	 * Default Constructor
	 * @throws IllegalAccessException 
	 */
	public InteractiveEvolutionTask(boolean evolveCPPNs) throws IllegalAccessException {		
		if(evolveCPPNs) inputMultipliers = new double[numCPPNInputs()];

		selectedItems = new LinkedList<Integer>(); //keeps track of selected CPPNs for MIDI playback with multiple CPPNS in Breedesizer

		MMNEAT.registerFitnessFunction("User Preference");
		//sets mu to a divisible number
		if(Parameters.parameters.integerParameter("mu") % InteractiveEvolutionTask.NUM_COLUMNS != 0) { 
			Parameters.parameters.setInteger("mu", InteractiveEvolutionTask.NUM_COLUMNS * ((Parameters.parameters.integerParameter("mu") / InteractiveEvolutionTask.NUM_COLUMNS) + 1));
			System.out.println("Changing population size to: " + Parameters.parameters.integerParameter("mu"));
		}

		//Global variable instantiations
		numButtonOptions	= Parameters.parameters.integerParameter("mu");
		numRows = numButtonOptions / NUM_COLUMNS;
		picSize = Parameters.parameters.integerParameter("imageSize");
		chosen = new boolean[numButtonOptions];
		//showLineage = false;
		showNetwork = false;
		waitingForUser = false;
		
		activation = new boolean[ActivationFunctions.MAX_POSSIBLE_ACTIVATION_FUNCTIONS]; // Leaves many gaps in array
		Arrays.fill(activation, true);
		
		if(MMNEAT.browseLineage) {
			// Do not setup the JFrame if browsing the lineage
			return;
		}            

		//Graphics instantiations
		frame = new JFrame(getWindowTitle());
		panels = new ArrayList<JPanel>();
		buttons = new ArrayList<JButton>();

		//sets up JFrame


		//frame.setSize(PIC_SIZE * NUM_COLUMNS + 200, PIC_SIZE * NUM_ROWS + 700);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		picSize = Math.min(picSize, frame.getWidth() / NUM_COLUMNS);
		frame.setLocation(300, 100);//magic #s 100 correspond to relocating frame to middle of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(numRows + 1, 0));// the + 1 includes room for the title panel
		frame.setVisible(true);

		//instantiates helper buttons
		topper = new JPanel();
		top = new JPanel();
		
		JPanel bottom = new JPanel();
		bottom.setPreferredSize(new Dimension(frame.getWidth(), 200)); // 200 magic number: height of checkbox area
		bottom.setLayout(new FlowLayout());

		// Gets the Button Images from the Picbreeder data Folder and re-scales them for use on the smaller Action Buttons
		ImageIcon reset = new ImageIcon("data"+File.separator+"picbreeder"+File.separator+"reset.png");
		Image reset2 = reset.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		ImageIcon save = new ImageIcon("data"+File.separator+"picbreeder"+File.separator+"save.png");
		Image save2 = save.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		ImageIcon evolve = new ImageIcon("data"+File.separator+"picbreeder"+File.separator+"arrow.png");
		Image evolve2 = evolve.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		//ImageIcon close = new ImageIcon("data"+File.separator+"picbreeder"+File.separator+"quit.png");
		//Image close2 = close.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		//ImageIcon lineage = new ImageIcon("data"+File.separator+"picbreeder"+File.separator+"lineage.png");
		//Image lineage2 = lineage.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		ImageIcon network = evolveCPPNs ? new ImageIcon("data"+File.separator+"picbreeder"+File.separator+"network.png") : null;
		Image network2 = evolveCPPNs ? network.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1) : null;

		ImageIcon undo = new ImageIcon("data"+File.separator+"picbreeder"+File.separator+"undo.png");
		Image undo2 = undo.getImage().getScaledInstance(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 1);

		JButton resetButton = new JButton(new ImageIcon(reset2));
		JButton saveButton = new JButton(new ImageIcon(save2));
		JButton evolveButton = new JButton(new ImageIcon(evolve2));
		//JButton closeButton = new JButton(new ImageIcon(close2));
		//JButton lineageButton = new JButton(new ImageIcon(lineage2));
		JButton networkButton = evolveCPPNs ? new JButton(new ImageIcon(network2)) : null;
		JButton undoButton = new JButton( new ImageIcon(undo2));

		//to make it work on my mac
		resetButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		saveButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		evolveButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		//lineageButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		if(evolveCPPNs) networkButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		undoButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));
		//closeButton.setPreferredSize(new Dimension(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT));

		resetButton.setText("Reset");
		saveButton.setText("Save");
		evolveButton.setText("Evolve!");
		//lineageButton.setText("Lineage");
		if(evolveCPPNs) networkButton.setText("Network");
		undoButton.setText("Undo");
		//closeButton.setText("Close");

		//adds slider for mutation rate change
		JSlider mutationsPerGeneration = new JSlider(JSlider.HORIZONTAL, MPG_MIN, MPG_MAX, MPG_DEFAULT);

		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		//set graphic names and toolTip titles
		evolveButton.setName("" + EVOLVE_BUTTON_INDEX);
		evolveButton.setToolTipText("Evolve button");
		saveButton.setName("" + SAVE_BUTTON_INDEX);
		saveButton.setToolTipText("Save button");
		resetButton.setName("" + RESET_BUTTON_INDEX);
		resetButton.setToolTipText("Reset button");
		//closeButton.setName("" + CLOSE_BUTTON_INDEX);
		//closeButton.setToolTipText("Close button");
		//lineageButton.setName("" + LINEAGE_BUTTON_INDEX);
		//lineageButton.setToolTipText("Lineage button");
		if(evolveCPPNs) {
			networkButton.setName("" + NETWORK_BUTTON_INDEX);
			networkButton.setToolTipText("Network button");
		}
		undoButton.setName("" + UNDO_BUTTON_INDEX);
		undoButton.setToolTipText("Undo button");

		mutationsPerGeneration.setMinorTickSpacing(1);
		mutationsPerGeneration.setPaintTicks(true);
		labels.put(0, new JLabel("Fewer Mutations"));
		labels.put(10, new JLabel("More Mutations"));
		mutationsPerGeneration.setLabelTable(labels);
		mutationsPerGeneration.setPaintLabels(true);
		mutationsPerGeneration.setPreferredSize(new Dimension(200, 40));

		//add action listeners to buttons
		resetButton.addActionListener(this);
		saveButton.addActionListener(this);
		evolveButton.addActionListener(this);
		//closeButton.addActionListener(this);
		//lineageButton.addActionListener(this);
		if(evolveCPPNs) networkButton.addActionListener(this);
		undoButton.addActionListener(this);

		mutationsPerGeneration.addChangeListener(this);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			//add additional action buttons
			//top.add(lineageButton);
			top.add(resetButton);
		}

		//add graphics to title panel
		top.add(evolveButton);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(saveButton);
			if(evolveCPPNs) top.add(networkButton);
			top.add(undoButton);
		}

		//top.add(closeButton);
		top.add(mutationsPerGeneration);	

		if(evolveCPPNs) {
			//instantiates activation function checkboxes
			for(Integer ftype : ActivationFunctions.allPossibleActivationFunctions()) {
				boolean checked = ActivationFunctions.availableActivationFunctions.contains(ftype);
				JCheckBox functionCheckbox = new JCheckBox(ActivationFunctions.activationName(ftype).replaceAll(" ", "_"), checked);
				int id = Math.abs(ftype); // leaves many gaps in array 
				activation[id] = checked;			
				// IDs are negative to they do not conflict with item selection.
				// They are offset by -100 so they do not conflict with other buttons like save, network, etc.
				functionCheckbox.setName("" + (-ACTIVATION_CHECKBOX_OFFSET - id)); 
				functionCheckbox.addActionListener(this);
				//set checkbox colors to match activation function color
				functionCheckbox.setForeground(CombinatoricUtilities.colorFromInt(ftype));
				if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
					//add activation function checkboxes to interface
					bottom.add(functionCheckbox);
				}
			}		
		}

		topper.add(top);
		topper.add(bottom);
		panels.add(topper);
		//adds button panels
		addButtonPanels();	

		//adds panels to frame
		for(JPanel panel: panels) frame.add(panel);

		//adds buttons to button panels
		addButtonsToPanel(0);
		//add input checkboxes
		if(evolveCPPNs) inputCheckBoxes();
	}

	/**
	 * Adds checkboxes for disabling certain input values
	 */
	public void inputCheckBoxes() {		
		JPanel effectsCheckboxes = new JPanel();
		effectsCheckboxes.setPreferredSize(new Dimension(300, 90));
		effectsCheckboxes.setLayout(new FlowLayout());
		String[] inputLabels = this.sensorLabels();
		inputMultipliers = new double[inputLabels.length];
		for(int i = 0; i < inputLabels.length; i++) {
			// Remove spaces because the buttons are parsed based on whitespace
			String label = inputLabels[i].replaceAll(" ", "_");
			JCheckBox inputEffect = new JCheckBox(label, true);
			inputMultipliers[i] = 1.0;
			inputEffect.setName("" + (CHECKBOX_IDENTIFIER_START - i));
			inputEffect.addActionListener(this);
			inputEffect.setForeground(new Color(0,0,0));
			if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
				effectsCheckboxes.add(inputEffect);
			}		
		}
		top.add(effectsCheckboxes);
	}

	/**
	 * Allows for static access to the input multipliers
	 * @return
	 */
	public static double[] getInputMultipliers() {
		@SuppressWarnings("rawtypes")
		InteractiveEvolutionTask task = (InteractiveEvolutionTask) MMNEAT.task;
		double[] inputMultipliersCopy = Arrays.copyOf(task.inputMultipliers, task.numCPPNInputs());
		return inputMultipliersCopy;
	}

	/**
	 * Accesses title of window
	 * @return string representing title of window
	 */
	protected abstract String getWindowTitle();

	/**
	 * adds buttons to a JPanel
	 * @param x size of button array
	 */
	private void addButtonsToPanel(int x) {
		for(int i = 1; i <= numRows; i++) {
			for(int j = 0; j < NUM_COLUMNS; j++) {
				if(x < numButtonOptions) {
					JButton image = getImageButton(GraphicsUtil.solidColorImage(Color.BLACK, picSize,( frame.getHeight() - topper.getHeight())/numRows), "x");
					image.setName("" + x);
					image.addActionListener(this);
					panels.get(i).add(image);
					buttons.add(image);

				}
			}
		}
	}

	/**
	 * Adds all necessary button panels 
	 */
	private void addButtonPanels() { 
		for(int i = 1; i <= numRows; i++) {
			JPanel row = new JPanel();
			row.setSize(frame.getWidth(), picSize);
			row.setSize(frame.getWidth(), picSize);
			row.setLayout(new GridLayout(1, NUM_COLUMNS));
			panels.add(row);
		}
	}

	/**
	 * Gets JButton from given image
	 * @param image image to put on button
	 * @param s title of button
	 * @return JButton
	 */
	protected JButton getImageButton(BufferedImage image, String s) {
		JButton button = new JButton(new ImageIcon(image));
		button.setName(s);
		return button;
	}

	/**
	 * Score for an evaluated individual
	 * @return array of scores
	 */
	public double[] evaluate() {
		return new double[]{1.0};
	}

	/**
	 * Number of objectives for task
	 * @return number of objectives
	 */
	@Override
	public int numObjectives() {
		return 1;
	}

	/**
	 * minimum score for an individual
	 * @return 0
	 */
	@Override
	public double[] minScores() {
		return new double[]{0};
	}

	/**
	 * this method makes no sense in 
	 * scope of this task
	 */
	@Override
	public double getTimeStamp() {
		return 0.0;
	}

	/**
	 * this method also makes no sense in 
	 * scope of this task
	 */
	@Override
	public void finalCleanup() {
	}

	/**
	 * Resets image on button
	 * @param gmi replacing image
	 * @param buttonIndex index of button 
	 */
	protected void setButtonImage(BufferedImage gmi, int buttonIndex){ 
		ImageIcon img = new ImageIcon(gmi.getScaledInstance(picSize,picSize,Image.SCALE_DEFAULT));
		buttons.get(buttonIndex).setName("" + buttonIndex);
		buttons.get(buttonIndex).setIcon(img);

	}
	
	/**
	 * If user is saving file to a specified location, this method obtains
	 * the directory in which the file is saved and the desired name of the 
	 * file.
	 * 
	 * @param type Type of file being saved
	 * @param extension file extension
	 * @return
	 */
	protected String getDialogFileName(String type, String extension) {
		JFileChooser chooser = new JFileChooser();//used to get save name 
		chooser.setCurrentDirectory(new File("."));
		chooser.setApproveButtonText("Save");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(type, extension);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the file
			System.out.println("You chose to call the file: " + chooser.getSelectedFile().getName());
			return chooser.getCurrentDirectory() + File.separator + chooser.getSelectedFile().getName(); 
		} else { //else image dumped
			System.out.println("file not saved");
			return null;
		}
	}
	
	/**
	 * Generalized version of save method that accounts for user pressing 
	 * "cancel" because this needs to be handled in all extensions of
	 * the abstract save method.
	 * 
	 * @param i index of item being saved
	 */
	protected void save(int i) {
		String file = getDialogFileName(getFileType(), getFileExtension());
		if(file != null) {
			save(file, i);
		} else {
			System.out.println("Saving cancelled");
		}
	}
	
	/**
	 * All interactive evolution interfaces must implement this
	 * class to save generated files. 
	 * @param file Desired file name
	 * @param i Index of item being saved
	 */
	protected abstract void save(String file, int i);

	/**
	 * used to reset image on button using given genotype
	 * @param individual genotype used to replace button image
	 * @param x index of button in question
	 */
	protected void resetButton(Genotype<T> individual, int x) { 
		scores.add(new Score<T>(individual, new double[]{0}, null));
		setButtonImage(showNetwork ? getNetwork(individual) : getButtonImage(true, individual.getPhenotype(),  picSize, picSize, inputMultipliers), x);
		chosen[x] = false;
		buttons.get(x).setBorder(BorderFactory.createLineBorder(Color.lightGray, BORDER_THICKNESS));
	}
	
	/**
	 * Creates BufferedImage representation of item to be displayed on 
	 * the buttons of the interface.
	 * 
	 * @param phenotype CPPN input
	 * @param width width of image
	 * @param height height of input
	 * @param inputMultipliers determines whether CPPN inputs are turned on or off
	 * @return BufferedImage representation of created item
	 */
	protected abstract BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers);

	/**
	 * Get button images by checking cache first if checkCache is true. 
	 * Otherwise, generate as normal.
	 * 
	 * @param checkCache
	 * @param phenotype Must be a TWEANN
	 * @param width
	 * @param height
	 * @param inputMultipliers
	 * @return Image for button
	 */
	protected BufferedImage getButtonImage(boolean checkCache, T phenotype, int width, int height, double[] inputMultipliers) {
		// See if image is already in hash map to be retrieved
		if(checkCache) {
			// Will this interface ever be used with items that are not TWEANNs?
			long id = ((TWEANN) phenotype).getId();
			if(cachedButtonImages.containsKey(id)) {
				// Return pre-computed image instead of watsing time
				return cachedButtonImages.get(id);
			} 
		}
		// If fails, or if not allowing cache checks, do the default call to getButtonImage
		BufferedImage image = getButtonImage(phenotype, width, height, inputMultipliers);
		if(checkCache) {
			// Use of checkCache avoids the cast to TWEANN for non-TWEANN phenotypes
			long id = ((TWEANN) phenotype).getId();
			cachedButtonImages.put(id, image);
		}
		return image;
	}
	
	/**
	 * Used to get the image of a network using a drawing panel
	 * @param tg genotype of network
	 * @return
	 */
	private BufferedImage getNetwork(Genotype<T> tg) {
		T pheno = tg.getPhenotype();
		return ((TWEANN) pheno).getNetworkImage(picSize, (frame.getHeight() - topper.getHeight())/numRows, false, false);
	}

	/**
	 * evaluates all genotypes in a population
	 * @param population of starting population
	 * @return score of each member of population
	 */
	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		waitingForUser = true;
		scores = new ArrayList<Score<T>>();
		if(population.size() != numButtonOptions) {
			throw new IllegalArgumentException("number of genotypes doesn't match size of population! Size of genotypes: " + population.size() + " Num buttons: " + numButtonOptions);
		}	
		// Because image loading may take a while, blank all images first so that it is clear
		// when the images have loaded.
		BufferedImage blank = new BufferedImage(picSize, picSize, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < buttons.size(); i++) {
			setButtonImage(blank, i);
		}	
		// Put appropriate content on buttons
		for(int x = 0; x < buttons.size(); x++) {
			resetButton(population.get(x), x);
		}
		while(waitingForUser){
			try {//waits for user to click buttons before evaluating
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Clear unselected items from cache
		for(Score<T> s : scores) {
			if(s.scores[0] == 0) { // This item was not selected by the user
				// Remove from image cache
				long id = s.individual.getId();
				cachedButtonImages.remove(id);
				System.out.println("Removed image " + id);
			}
		}
		System.out.println("Size of cache: " + cachedButtonImages.size());
		return scores;
	}

	/**
	 * sets all relevant features if button at index is pressed  
	 * @param scoreIndex index in arrays
	 */
	private void buttonPressed(int scoreIndex) {
		if(chosen[scoreIndex]) {//if image has already been clicked, reset
			selectedItems.remove(new Integer(scoreIndex)); //remove CPPN from list of currently selected CPPNs
			chosen[scoreIndex] = false;
			buttons.get(scoreIndex).setBorder(BorderFactory.createLineBorder(Color.lightGray, BORDER_THICKNESS));
			scores.get(scoreIndex).replaceScores(new double[]{0});
		} else {//if image has not been clicked, set it
			selectedItems.add(scoreIndex); //add CPPN to list of currently selected CPPNs
			chosen[scoreIndex] = true;
			buttons.get(scoreIndex).setBorder(BorderFactory.createLineBorder(Color.BLUE, BORDER_THICKNESS));
			scores.get(scoreIndex).replaceScores(new double[]{1.0});
		}
		additionalButtonClickAction(scoreIndex,scores.get(scoreIndex).individual);
		currentCPPN = scores.get(scoreIndex).individual.getPhenotype();
	}
	
	/**
	 * If the buttons should do something in the interface other than the initial response
	 * to a click, the associated code should be written in this method.
	 * 
	 * @param scoreIndex index of button
	 * @param individual genotype input
	 */
	protected abstract void additionalButtonClickAction(int scoreIndex, Genotype<T> individual);

	/**
	 * Resets to a new random population
	 */
	@SuppressWarnings("unchecked")
	protected void reset() { 
		// Select one of the available activation functions as default
		CommonConstants.ftype = RandomNumbers.randomElement(ActivationFunctions.availableActivationFunctions);
		Parameters.parameters.setInteger("ftype", CommonConstants.ftype);
		ArrayList<Genotype<T>> newPop = ((SinglePopulationGenerationalEA<T>) MMNEAT.ea).initialPopulation(scores.get(0).individual);
		scores = new ArrayList<Score<T>>();
		ActivationFunctionRandomReplacement frr = new ActivationFunctionRandomReplacement();
		for(int i = 0; i < newPop.size(); i++) {
			if(newPop.get(i) instanceof TWEANNGenotype) frr.mutate((Genotype<TWEANN>) newPop.get(i));
			resetButton(newPop.get(i), i);
		}	
	}

	/**
	 * Saves all currently clicked images
	 */
	private void saveAll() { 
		for(int i = 0; i < chosen.length; i++) {
			boolean choose = chosen[i];
			if(choose) {//loops through and any image  clicked automatically saved
				save(i);
			}
		}
	}
	
	/**
	 * Returns type of file being saved (for FileExtensionFilter for save method)
	 * 
	 * @return type of file being saved
	 */
	protected abstract String getFileType();
	
	/**
	 * Returns extension of file being saved (for FileExtensionFilter for save method)
	 * 
	 * @return extension of file being saved	
	 */
	protected abstract String getFileExtension();

	/**
	 * Shows network on button if network button pressed
	 * replaces images on buttons otherwise
	 */
	private void setNetwork() { 
		if(showNetwork) {//puts images back on buttons
			showNetwork = false;
			for(int i = 0; i < scores.size(); i++) {
				setButtonImage(getButtonImage(scores.get(i).individual.getPhenotype(), picSize, picSize, inputMultipliers), i);
			}
		} else {//puts networks on buttons
			showNetwork = true;
			for(int i = 0; i < buttons.size(); i++) {
				BufferedImage network = getNetwork(scores.get(i).individual);
				setButtonImage(network, i);
			}
		}
	}

	/**
	 * Sets the activation functions as true or false based on whether or
	 * not they were pressed
	 * @param act whether or not function is active
	 * @param ftype index of function in boolean array
	 */
	private void setActivationFunctionCheckBox(boolean act, int ftype) { 
		if(act) { 
			activation[ftype] = false;
			ActivationFunctions.availableActivationFunctions.remove(new Integer(ftype));
			// Parameter value not actually changed
		} else {
			activation[ftype] = true;
			ActivationFunctions.availableActivationFunctions.add(new Integer(ftype));
			// Parameter value not actually changed
		}
	}

	/**
	 * Handles the changing of the input multipliers when an Effect Checkbox is clicked
	 * 
	 * @param index Index of the effect being changed
	 */
	protected void setEffectCheckBox(int index){

		// Generalize depending on number of inputs

		if(inputMultipliers[index] == 1.0){ // Effect is currently ON
			inputMultipliers[index] = 0.0;
		}else{ // Effect is currently OFF
			inputMultipliers[index] = 1.0;
		}
		resetButtons(true);
	}

	/**
	 * Used to reset the buttons when an Effect CheckBox is clicked
	 */
	public void resetButtons(boolean hardReset){
		for(int i = 0; i < scores.size(); i++) {
			// If not doing hard reset, there is a chance to load from cache
			setButtonImage(getButtonImage(!hardReset, scores.get(i).individual.getPhenotype(),  picSize, picSize, inputMultipliers), i);
		}		
	}

	/**
	 * Contains actions to be performed based
	 * on specific events
	 * @param event that occurred
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		//open scanner to read which button was pressed
		Scanner s = new Scanner(event.toString());
		s.next(); //parsing action event, no spaces allowed 
		s.next(); //parsing the word "on"
		int itemID = s.nextInt();
		s.close();
		boolean undo = respondToClick(itemID);
		// Special case: do not allow unchecking of last activation function checkbox
		if(undo) {
			Object source = event.getSource();
			if(source instanceof JCheckBox) {
				((JCheckBox) source).setSelected(true);
			}
		}
	}

	/**
	 * Takes unique identifier for clicked element and performs appropriate action.
	 * Returns whether or not the action should be undone, which is only true if the
	 * user attempts to disable the last activation function. 
	 * @param itemID Unique identifier stored in the name of each clickable object in a convoluted way
	 * @return Whether to undo the click
	 */
	protected boolean respondToClick(int itemID) {
		// Must be checkbox for activation function
		if(itemID <= -ACTIVATION_CHECKBOX_OFFSET) {			
			int ftype = Math.abs(itemID + ACTIVATION_CHECKBOX_OFFSET); // remove offset and make positive
			setActivationFunctionCheckBox(activation[ftype], ftype);			
			// Don't deselect all activation functions!
			if(ActivationFunctions.availableActivationFunctions.isEmpty()) {
				// Re-select the activation function
				setActivationFunctionCheckBox(activation[ftype], ftype);
				System.out.println("Cannot deselect ALL activation functions. There must be at least one");
				return true; // Undo the click
			} else {
				System.out.println("Param " + ActivationFunctions.activationName(ftype) + " now set to: " + activation[ftype]);
			}
		} else if(itemID == RESET_BUTTON_INDEX) {//If reset button clicked
			reset();
		} else if(itemID == SAVE_BUTTON_INDEX && BooleanUtil.any(chosen)) { //If save button clicked
			saveAll();
		//} else if(itemID == LINEAGE_BUTTON_INDEX) {//If lineage button clicked
		//	setLineage();
		} else if(itemID == NETWORK_BUTTON_INDEX) {//If network button clicked
			setNetwork();
		} else if(itemID == UNDO_BUTTON_INDEX) {//If undo button clicked
			// Not implemented yet
			setUndo();
		} else if(itemID == EVOLVE_BUTTON_INDEX && BooleanUtil.any(chosen)) {//If evolve button clicked
			if(Parameters.parameters.booleanParameter("saveInteractiveSelections")) {
				String dir = FileUtilities.getSaveDirectory() + "/selectedFromGen" +  ((GenerationalEA) MMNEAT.ea).currentGeneration();
				new File(dir).mkdir(); // Make the save directory
				for(int i = 0; i < scores.size(); i++) {
					if(chosen[i]) {
						String fullName = dir + "/itemGen" + ((GenerationalEA) MMNEAT.ea).currentGeneration() + "_Index" + i + "_ID" + scores.get(i).individual.getId();
						save(fullName,i);
					}
				}
			}
			evolve();
		} else if(itemID >= IMAGE_BUTTON_INDEX) {//If an image button clicked
			assert (scores.size() == buttons.size()) : 
				"size mismatch! score array is " + scores.size() + " in length and buttons array is " + buttons.size() + " long";
			buttonPressed(itemID);
		} 

		// Handle all input disabling checkboxes
		for(int i = 0; i < sensorLabels().length; i++) {			
			if(itemID == CHECKBOX_IDENTIFIER_START - i){
				setEffectCheckBox(i);
			}
		}		
		// Do not undo the action: default
		return false; 
	}

	protected void evolve() {
		previousScores = new ArrayList<Score<T>>();
		previousScores.addAll(scores);
		waitingForUser = false;//tells evaluateAll method to finish	
	}

	//used for lineage and undo button
	private static HashSet<Long> drawnOffspring = null;
	private static HashMap<Integer, Integer> savedLineage = null;
	private static ArrayList<DrawingPanel> dPanels = null;

	/**
	 * resets lineage drawer if button pressed multiple
	 * times.
	 * 
	 * TODO: There are many issues with the lineage drawer, so this button is disabled
	 */
	private static void resetLineageDrawer() { 
		if(dPanels != null) {
			for(int i = 0; i < dPanels.size(); i++) {
				dPanels.get(i).setVisibility(false);
			}
		}
		dPanels = null;
		drawnOffspring = null;
	}


	/**
	 * gets lineage from offspring object
	 * 
	 * TODO: Currently not used because it doesn't fully work properly.
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private void setLineage() {
		if(!showLineage) {
			showLineage = true;
			resetLineageDrawer();
			String base = Parameters.parameters.stringParameter("base");
			String log =  Parameters.parameters.stringParameter("log");
			int runNumber = Parameters.parameters.integerParameter("runNumber");
			String saveTo = Parameters.parameters.stringParameter("saveTo");
			String prefix = base + "/" + saveTo + runNumber + "/" + log + runNumber + "_";
			String originalPrefix = base + "/" + saveTo + runNumber + "/" + log + runNumber + "_";

			drawnOffspring = new HashSet<Long>();
			savedLineage = new HashMap<Integer, Integer>();
			dPanels = new ArrayList<DrawingPanel>();

			try {
				Offspring.reset();
				Offspring.lineage = new ArrayList<Offspring>();
				PopulationUtil.loadLineage();
				System.out.println("Lineage loaded from file");
				// Also adds networks
				Offspring.addAllScores(prefix, "parents_gen", ((SinglePopulationGenerationalEA) MMNEAT.ea).currentGeneration(), true, originalPrefix);
				System.out.println("Scores added");
				for(int i = 0; i < chosen.length; i++) {
					boolean choose = chosen[i];
					if(choose) {//loops through and any image  clicked automatically saved
						Score<T> s = scores.get(i);
						Genotype<T> network = s.individual;
						long id = network.getId();
						for(Offspring o : SelectiveBreedingEA.offspring) {
							if(o.offspringId == id) {
								// Magic number here: 600 is start y-coord for drawing lineage
								drawLineage(o, id, 0, 600);						
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("Lineage browser failed");
				e.printStackTrace();
			}
		} else {
			resetLineageDrawer();
			showLineage = false;
		}
	}

	/**
	 * Draws lineage of image recursively
	 * @param o offspring object (used to retrieve lineage)
	 * @param id id of image
	 * @param x x-coord of image
	 * @param y y-coord of image
	 */
	private void drawLineage(Offspring o, long id, int x, int y) { 
		int depth = 0;
		if(o.parentId1 > -1) {
			drawLineage(o.parentId1, id, x, y - picSize/4, depth++);
		}
		if(o.parentId2 > -1) {
			drawLineage(o.parentId2, id, x, y + picSize/4, depth++);
		}	
	}

	/**
	 * draws lineage of an image
	 * @param <T> phenotype of network
	 * @param id id of image
	 * @param childId id of child image
	 * @param x x-coord
	 * @param y y-coord
	 * @param depth depth of the recursive call and this the
	 *              distance in generations from the child.
	 */
	@SuppressWarnings("unchecked")
	public void drawLineage(long id, long childId, int x, int y, int depth) {
		Offspring o = Offspring.lineage.get((int) id);
		if(o != null && !drawnOffspring.contains(id)) { // Don't draw if already drawn
			Genotype<T> g = (Genotype<T>) Offspring.getGenotype(o.xmlNetwork);
			BufferedImage bi = getButtonImage(g.getPhenotype(), picSize/2, picSize/2, inputMultipliers);
			DrawingPanel p = GraphicsUtil.drawImage(bi, id + " -> " + childId, picSize/2, picSize/2);
			p.setLocation(x, y);
			savedLineage.put(depth, savedLineage.get(depth) == null ? 0 : savedLineage.get(depth) + 1);
			drawLineage(o, id, x + picSize/2, y);
			p.setTitle(id + "ancestor" + depth + savedLineage.get(depth));
			p.save(p.getFrame().getTitle());
			dPanels.add(p);
		}
		drawnOffspring.add(id); // don't draw again
	}

	/**
	 * undoes previous evolution call
	 * NOT COMPLETE
	 */
	protected void setUndo() {
		scores = new ArrayList<Score<T>>();
		for(int i = 0; i < previousScores.size(); i++) {
			//System.out.println("score size " + scores.size() + " previousScores size " + previousScores.size() + " buttons size " + buttons.size() + " i " + i);
			resetButton(previousScores.get(i).individual, i);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		SelectiveBreedingEA.MUTATION_RATE = source.getValue();

	}
	/**
	 * Specifies the number of CPPN inputs used in the interactive evolution task.
	 * 
	 * @return number of CPPN inputs
	 */
	public abstract int numCPPNInputs();
	
	/**
	 * Specifies the number of CPPN outputs used in the interactive evolution task.
	 * 
	 * @return number of CPPN outputs
	 */
	public abstract int numCPPNOutputs();
}
