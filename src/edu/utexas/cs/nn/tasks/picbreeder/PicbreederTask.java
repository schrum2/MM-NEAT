package edu.utexas.cs.nn.tasks.picbreeder;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.BooleanUtil;
import edu.utexas.cs.nn.util.GraphicsUtil;
import edu.utexas.cs.nn.util.PopulationUtil;
import java.util.HashSet;

/**
 * Implementation of picbreeder that uses Java Swing components for graphical interface
 * 
 * @author Lauren Gillespie
 *
 * @param <T>
 */
public class PicbreederTask<T extends Network> implements SinglePopulationTask<T>, ActionListener, NetworkTask {

	//Global static final variables
	public static final int CPPN_NUM_INPUTS	= 4;
	public static final int CPPN_NUM_OUTPUTS = 3;
	public static final int NUM_COLUMNS	= 4;

	//private static final Variables
	//includes indices of buttons for action listener
	private static final int IMAGE_BUTTON_INDEX = 0;
	private static final int EVOLVE_BUTTON_INDEX = -1;
	private static final int SAVE_BUTTON_INDEX = -2;
	private static final int RESET_BUTTON_INDEX = -3;
	private static final int CLOSE_BUTTON_INDEX	= -4;
	private static final int LINEAGE_BUTTON_INDEX = -5;
	private static final int NETWORK_BUTTON_INDEX = -6;
	private static final int UNDO_BUTTON_INDEX = -7;
	private static final int SIGMOID_CHECKBOX_INDEX = -8;
	private static final int GAUSSIAN_CHECKBOX_INDEX = -9;
	private static final int SINE_CHECKBOX_INDEX = -10;
	private static final int SAWTOOTH_CHECKBOX_INDEX = -11;
	private static final int ABSVAL_CHECKBOX_INDEX = -12;
	private static final int HALF_LINEAR_CHECKBOX_INDEX = -13;

	private static final int BORDER_THICKNESS = 4;
	
	//Private final variables
	private static int NUM_ROWS;
	private static int PIC_SIZE;
	private static int NUM_BUTTONS;

	//Private graphic objects
	private JFrame frame;
	private ArrayList<JPanel> panels;
	private ArrayList<JButton> buttons;
	private ArrayList<Score<T>> scores;
	private ArrayList<Score<T>> previousScores;

	//private helper variables
	private boolean showLineage;
	private boolean showNetwork;
	private boolean waitingForUser;
	private boolean[] chosen;
	private boolean[] activation;
	
	/**
	 * Default Constructor
	 */
	public PicbreederTask() {		
		//sets mu to a divisible number
		if(Parameters.parameters.integerParameter("mu") % PicbreederTask.NUM_COLUMNS != 0) { 
			Parameters.parameters.setInteger("mu", PicbreederTask.NUM_COLUMNS * ((Parameters.parameters.integerParameter("mu") / PicbreederTask.NUM_COLUMNS) + 1));
			System.out.println("Changing population size to: " + Parameters.parameters.integerParameter("mu"));
		}
		
		//Global variable instantiations
		NUM_BUTTONS	= Parameters.parameters.integerParameter("mu");
		NUM_ROWS = NUM_BUTTONS / NUM_COLUMNS;
		PIC_SIZE = Parameters.parameters.integerParameter("imageSize");
		chosen = new boolean[NUM_BUTTONS];
		showLineage = false;
		showNetwork = false;
		waitingForUser = false;
		activation = new boolean[Math.abs(HALF_LINEAR_CHECKBOX_INDEX) + 1];
		Arrays.fill(activation, true);
                if(MMNEAT.browseLineage) {
                    // Do not setup the JFrame if browsing the lineage
                    return;
                }            

                //Graphics instantiations
		frame = new JFrame("Picbreeder");
		panels = new ArrayList<JPanel>();
		buttons = new ArrayList<JButton>();
		
		//sets up JFrame
		frame.setSize(PIC_SIZE * NUM_COLUMNS, PIC_SIZE * NUM_ROWS);
		frame.setLocation(300, 100);//magic #s 100 correspond to relocating frame to middle of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUM_ROWS + 1, 0));// the + 1 includes room for the title panel
		frame.setVisible(true);

		//instantiate graphics
		JPanel topper = new JPanel();
		//topper.setLayout(new GridLayout(2, 0));
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		JButton resetButton = new JButton(new ImageIcon("data\\picbreeder\\reset.png"));
		JButton saveButton = new JButton(new ImageIcon("data\\picbreeder\\save.png"));
		JButton evolveButton = new JButton(new ImageIcon("data\\picbreeder\\arrow.png"));
		JButton closeButton = new JButton(new ImageIcon("data\\picbreeder\\quit.png"));
		JButton lineageButton = new JButton(new ImageIcon("data\\picbreeder\\lineage.png"));
		JButton networkButton = new JButton(new ImageIcon("data\\picbreeder\\network.png"));
		JButton undoButton = new JButton( new ImageIcon("data\\picbreeder\\undo.png"));

		JCheckBox sigmoid = new JCheckBox("sigmoid_function", true);
		JCheckBox gaussian = new JCheckBox("gaussian_function", true);
		JCheckBox sine = new JCheckBox("sine_function", true);
		JCheckBox sawtooth = new JCheckBox("sawtooth_function", true);
		JCheckBox absVal = new JCheckBox("absolute_value_function", true);
		JCheckBox halfLinear = new JCheckBox("half_linear_function", true);
		
		//set graphic names and toolTip titles
		evolveButton.setName("" + EVOLVE_BUTTON_INDEX);
		evolveButton.setToolTipText("Evolve button");
		saveButton.setName("" + SAVE_BUTTON_INDEX);
		saveButton.setToolTipText("Save button");
		resetButton.setName("" + RESET_BUTTON_INDEX);
		resetButton.setToolTipText("Reset button");
		closeButton.setName("" + CLOSE_BUTTON_INDEX);
		closeButton.setToolTipText("Close button");
		lineageButton.setName("" + LINEAGE_BUTTON_INDEX);
		lineageButton.setToolTipText("Lineage button");
		networkButton.setName("" + NETWORK_BUTTON_INDEX);
		networkButton.setToolTipText("Network button");
		undoButton.setName("" + UNDO_BUTTON_INDEX);
		undoButton.setToolTipText("Undo button");
		sigmoid.setName("" + SIGMOID_CHECKBOX_INDEX);

		absVal.setName("" + ABSVAL_CHECKBOX_INDEX);

		gaussian.setName("" + GAUSSIAN_CHECKBOX_INDEX);

		sine.setName("" + SINE_CHECKBOX_INDEX);

		sawtooth.setName("" + SAWTOOTH_CHECKBOX_INDEX);

		halfLinear.setName("" + HALF_LINEAR_CHECKBOX_INDEX);
System.out.println("half linear checkbox: " + halfLinear.toString());
		//add action listeners to buttons
		resetButton.addActionListener(this);
		saveButton.addActionListener(this);
		evolveButton.addActionListener(this);
		closeButton.addActionListener(this);
		lineageButton.addActionListener(this);
		networkButton.addActionListener(this);
		undoButton.addActionListener(this);
		sigmoid.addActionListener(this);
		gaussian.addActionListener(this);
		sine.addActionListener(this);
		sawtooth.addActionListener(this);
		absVal.addActionListener(this);
		halfLinear.addActionListener(this);
		

		//add graphics to title panel
		top.add(lineageButton);
		top.add(resetButton);
		top.add(networkButton);
		top.add(evolveButton);
		top.add(saveButton);
		top.add(undoButton);
		top.add(closeButton);
		topper.add(top);
		bottom.add(halfLinear);
		bottom.add(absVal);
		bottom.add(sawtooth);
		bottom.add(sine);
		bottom.add(gaussian);
		bottom.add(sigmoid);
		topper.add(bottom);
		panels.add(topper);

		//adds button panels
		addButtonPanels();
		
		//adds panels to frame
		for(JPanel panel: panels) frame.add(panel);
		
		//adds buttons to button panels
		int x = 0;//used to keep track of index of button panel
		addButtonsToPanel(x++);
	}

	private void addButtonsToPanel(int x) {
		for(int i = 1; i <= NUM_ROWS; i++) {
			for(int j = 0; j < NUM_COLUMNS; j++) {
				if(x < NUM_BUTTONS) {
					JButton image = getImageButton(GraphicsUtil.solidColorImage(Color.BLACK, PIC_SIZE, PIC_SIZE), "x");
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
		for(int i = 1; i <= NUM_ROWS; i++) {
			JPanel row = new JPanel();
			row.setSize(frame.getWidth(), PIC_SIZE);
			row.setSize(frame.getWidth(), PIC_SIZE);
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
	 * Returns labels for input
	 *
	 * @return List of CPPN outputs
	 */
	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias" };
	}
	
	/**
	 * Returns labels for output
	 *
	 * @return list of CPPN outputs
	 */
	@Override
	public String[] outputLabels() {
		return new String[] { "hue-value", "saturation-value", "brightness-value" };
	}
	
	/**
	 * Resets image on button
	 * @param gmi replacing image
 	 * @param buttonIndex index of button 
	 */
	private void setButtonImage(BufferedImage gmi, int buttonIndex){ 
		ImageIcon img = new ImageIcon(gmi);
		buttons.get(buttonIndex).setName("" + buttonIndex);
		buttons.get(buttonIndex).setIcon(img);

	}

	/**
	 * Saves image from button utilizing drawingPanel save functionality
	 * @param i index of button
	 * @param button button
	 */
	private void save(int i, JButton button) {
		BufferedImage toSave = (BufferedImage) ((ImageIcon) button.getIcon()).getImage();
		DrawingPanel p = GraphicsUtil.drawImage(toSave, "" + i, toSave.getWidth(), toSave.getHeight());
		JFileChooser chooser = new JFileChooser();//used to get save name 
		chooser.setApproveButtonText("Save");
		chooser.setCurrentDirectory(new File("\\" + "Users" + "\\" + "gillespl" + "\\" + "SCOPE" + "\\" + "MM-NEATv2" + "\\" + "evolvedPicbreederImages"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BMP Images", "bmp");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
			System.out.println("You chose to call the image: " + chooser.getSelectedFile().getName());
			p.save(chooser.getCurrentDirectory() + "\\" + chooser.getSelectedFile().getName() + (showNetwork ? "network" : "image") + ".bmp");
			System.out.println("image " + chooser.getSelectedFile().getName() + " was saved successfully");
			p.setVisibility(false);
		} else { //else image dumped
			p.setVisibility(false);
			System.out.println("image not saved");
		}
	}

	/**
	 * used to reset image on button using given genotype
	 * @param individual genotype used to replace button image
	 * @param x index of button in question
	 */
	private void resetButton(Genotype<T> individual, int x) { 
		scores.add(new Score<T>(individual, new double[]{0}, null));
		setButtonImage(showNetwork ? getNetwork(individual) : GraphicsUtil.imageFromCPPN((Network)individual.getPhenotype(), PIC_SIZE, PIC_SIZE), x);
		chosen[x] = false;
		buttons.get(x).setBorder(BorderFactory.createLineBorder(Color.lightGray, BORDER_THICKNESS));
	}
	
	/**
	 * Used to get the image of a network using a drawing panel
	 * @param tg genotype of network
	 * @return
	 */
	private BufferedImage getNetwork(Genotype<T> tg) {
		T pheno = tg.getPhenotype();
		DrawingPanel network = new DrawingPanel(PIC_SIZE, PIC_SIZE - 75, "network");
		((TWEANN) pheno).draw(network);
		network.setVisibility(false);
		return network.image;

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
		if(population.size() != NUM_BUTTONS) {
			throw new IllegalArgumentException("number of genotypes doesn't match size of population! Size of genotypes: " + population.size() + " Num buttons: " + NUM_BUTTONS);
		}	
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
		return scores;
	}

	/**
	 * sets all relevant features if button at index is pressed  
	 * @param scoreIndex index in arrays
	 */
	private void buttonPressed(int scoreIndex) {
		if(chosen[scoreIndex]) {//if image has already been clicked, reset
			chosen[scoreIndex] = false;
			buttons.get(scoreIndex).setBorder(BorderFactory.createLineBorder(Color.lightGray, BORDER_THICKNESS));
			scores.get(scoreIndex).replaceScores(new double[]{0});
		} else {//if image has not been clicked, set it
			chosen[scoreIndex] = true;
			buttons.get(scoreIndex).setBorder(BorderFactory.createLineBorder(Color.BLUE, BORDER_THICKNESS));
			scores.get(scoreIndex).replaceScores(new double[]{1.0});
		}
	}
	
	/**
	 * Resets to a new random population
	 */
	@SuppressWarnings("unchecked")
	private void reset() { 
		ArrayList<Genotype<T>> newPop = ((SinglePopulationGenerationalEA<T>) MMNEAT.ea).initialPopulation(scores.get(0).individual);
		scores = new ArrayList<Score<T>>();
		for(int i = 0; i < newPop.size(); i++) {
			resetButton(newPop.get(i), i);
		}
		// Attempted to completely clear all old log info, but realy complicated
//		String base = Parameters.parameters.stringParameter("base");
//		int runNumber = Parameters.parameters.integerParameter("runNumber");
//		String saveTo = Parameters.parameters.stringParameter("saveTo");
//		String prefix = base + "/" + saveTo + runNumber;
//		// Null pointer issue?
//		((SinglePopulationGenerationalEA<T>) MMNEAT.ea).close(null);
//		MMNEAT.closeLogs(); // close logs
//		// delete all records
//		FileUtilities.deleteDirectoryContents(new File(prefix));
//		// Reset some parameters to defaults
//		Parameters.parameters.setInteger("lastSavedGeneration", 0);
//		Parameters.parameters.setLong("lastInnovation", 0l);
//		Parameters.parameters.setLong("lastGenotypeId", 0l);
//		Parameters.parameters.setString("lastSavedDirectory", "");
//		
//		completeReset  = true;
//		MMNEAT.mmneat.run();		
	}
	
	/**
	 * Saves all currently clicked images
	 */
	private void saveAll() { 
		for(int i = 0; i < chosen.length; i++) {
			boolean choose = chosen[i];
			if(choose) {//loops through and any image  clicked automatically saved
				save(i , buttons.get(i));
			}
		}
	}
	
	private void setNetwork() { 
		if(showNetwork) {
			showNetwork = false;
			for(int i = 0; i < scores.size(); i++) {
				setButtonImage(GraphicsUtil.imageFromCPPN((Network)scores.get(i).individual.getPhenotype(), PIC_SIZE, PIC_SIZE), i);
			}
		} else {
			showNetwork = true;
			for(int i = 0; i < buttons.size(); i++) {
				BufferedImage network = getNetwork(scores.get(i).individual);
				setButtonImage(network, i);
			}
		}
	}
	
	private void setCheckBox(boolean act, int index, String title) { 
		if(act) { 
			activation[Math.abs(index)] = false;
			Parameters.parameters.setBoolean(title, false);
		} else {
			activation[Math.abs(index)] = true;
			Parameters.parameters.setBoolean(title, true);
		}
                ActivationFunctions.resetFunctionSet();
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
		s.next();
		s.next();
		int scoreIndex = s.nextInt();
		s.close();
		if(scoreIndex == SIGMOID_CHECKBOX_INDEX) {
			setCheckBox(activation[Math.abs(SIGMOID_CHECKBOX_INDEX)], SIGMOID_CHECKBOX_INDEX, "includeSigmoidFunction");
			System.out.println("param sigmoid now set to: " + Parameters.parameters.booleanParameter("includeSigmoidFunction"));
		} else if(scoreIndex ==GAUSSIAN_CHECKBOX_INDEX) {
			setCheckBox(activation[Math.abs(GAUSSIAN_CHECKBOX_INDEX)], GAUSSIAN_CHECKBOX_INDEX, "includeGaussFunction");
			System.out.println("param Gauss now set to: " + Parameters.parameters.booleanParameter("includeGaussFunction"));
		} else if(scoreIndex == SINE_CHECKBOX_INDEX) {
			setCheckBox(activation[Math.abs(SINE_CHECKBOX_INDEX)], SINE_CHECKBOX_INDEX, "includeSineFunction");
			System.out.println("param Sine now set to: " + Parameters.parameters.booleanParameter("includeSineFunction"));
		}else if(scoreIndex == SAWTOOTH_CHECKBOX_INDEX) {
			setCheckBox(activation[Math.abs(SAWTOOTH_CHECKBOX_INDEX)], SAWTOOTH_CHECKBOX_INDEX, "includeSawtoothFunction");
			System.out.println("param sawtooth now set to: " + Parameters.parameters.booleanParameter("includeSawtoothFunction"));
		}else if(scoreIndex == ABSVAL_CHECKBOX_INDEX) {
			setCheckBox(activation[Math.abs(ABSVAL_CHECKBOX_INDEX)], ABSVAL_CHECKBOX_INDEX, "includeAbsValFunction");
			System.out.println("param abs val now set to: " + Parameters.parameters.booleanParameter("includeAbsValFunction"));
		}else if(scoreIndex == HALF_LINEAR_CHECKBOX_INDEX) {
			setCheckBox(activation[Math.abs(HALF_LINEAR_CHECKBOX_INDEX)], HALF_LINEAR_CHECKBOX_INDEX, "includeHalfLinearPiecewiseFunction");
			System.out.println("param half linear now set to: " + Parameters.parameters.booleanParameter("includeHalfLinearPiecewiseFunction"));
		}else if(scoreIndex == CLOSE_BUTTON_INDEX) {//If close button clicked
			System.exit(0);
		} else if(scoreIndex == RESET_BUTTON_INDEX) {//If reset button clicked
			reset();
		} else if(scoreIndex == SAVE_BUTTON_INDEX && BooleanUtil.any(chosen)) { //If save button clicked
			saveAll();
		} else if(scoreIndex == LINEAGE_BUTTON_INDEX) {//If lineage button clicked
			setLineage();
		} else if(scoreIndex == NETWORK_BUTTON_INDEX) {//If network button clicked
			setNetwork();
		} else if(scoreIndex == UNDO_BUTTON_INDEX) {//If undo button clicked
			// Not implemented yet
			setUndo();
		}else if(scoreIndex == EVOLVE_BUTTON_INDEX && BooleanUtil.any(chosen)) {//If evolve button clicked
			previousScores = new ArrayList<Score<T>>();
			previousScores.addAll(scores);
			waitingForUser = false;//tells evaluateAll method to finish
		} else if(scoreIndex >= IMAGE_BUTTON_INDEX) {//If an image button clicked
			assert (scores.size() == buttons.size()) : 
				"size mismatch! score array is " + scores.size() + " in length and buttons array is " + buttons.size() + " long";
			buttonPressed(scoreIndex);
		}
	}

        private static HashSet<Long> drawnOffspring = null;
        private static ArrayList<DrawingPanel> dPanels = null;
        
	private static void drawLineage(Offspring o, long id, int x, int y) { 
		if(o.parentId1 > -1) {
			drawLineage(o.parentId1, id, x, y - PIC_SIZE/4);
		}
		if(o.parentId2 > -1) {
			drawLineage(o.parentId2, id, x, y + PIC_SIZE/4);
		}	
	}
	private static void resetLineageDrawer() { 
		if(dPanels != null) {
		for(int i = 0; i < dPanels.size(); i++) {
			dPanels.get(i).setVisibility(false);
		}
		}
		dPanels = null;
		drawnOffspring = null;
	}
	@SuppressWarnings("rawtypes")
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
                dPanels = new ArrayList<DrawingPanel>();
                
		try {
			Offspring.reset();
			Offspring.lineage = new ArrayList<Offspring>();
			PopulationUtil.loadLineage();
			System.out.println("Lineage loaded from file");
			// Also adds networks
			Offspring.addAllScores(prefix, "parents_gen", ((SinglePopulationGenerationalEA) MMNEAT.ea).currentGeneration(), true, originalPrefix);
			
			for(int i = 0; i < chosen.length; i++) {
				boolean choose = chosen[i];
				if(choose) {//loops through and any image  clicked automatically saved
					Score<T> s = scores.get(i);
					Genotype<T> network = s.individual;
					long id = network.getId();
					for(Offspring o : SelectiveBreedingEA.offspring) {
						if(o.offspringId == id) {
							drawLineage(o, id, 0, 500);						
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
	
	@SuppressWarnings("unchecked")
	public static <T extends Network> void drawLineage(long id, long childId, int x, int y) {
                Offspring o = Offspring.lineage.get((int) id);
		if(o != null && !drawnOffspring.contains(id)) { // Don't draw if already drawn
			Genotype<T> g = (Genotype<T>) Offspring.getGenotype(o.xmlNetwork);
			BufferedImage bi = GraphicsUtil.imageFromCPPN(g.getPhenotype(), PIC_SIZE/2, PIC_SIZE/2);
			DrawingPanel p = GraphicsUtil.drawImage(bi, id + " -> " + childId, PIC_SIZE/2, PIC_SIZE/2);
			p.setLocation(x, y);
			drawLineage(o, id, x + PIC_SIZE/2, y);
			dPanels.add(p);
		}
                drawnOffspring.add(id); // don't draw again
	}
	
	// NOT COMPLETE
//	@SuppressWarnings("unchecked")
	private void setUndo() {
		scores = new ArrayList<Score<T>>();
		for(int i = 0; i < previousScores.size(); i++) {
			scores.add(previousScores.get(i));
		}
		//scores.addAll(previousScores);
		//assert scores.size() == previousScores.size() && buttons.size()	== scores.size(): "either scores and button arrays don't match or scores and previous scores don't match";
		for(int i = 0; i < scores.size(); i++) {
			System.out.println("score size " + scores.size() + " previousScores size " + previousScores.size() + " buttons size " + buttons.size() + " i " + i);
			resetButton(scores.get(i).individual, i);
		}
//		int lastGen = Parameters.parameters.integerParameter("lastSavedGeneration");
//		System.out.println("before decrementing generation: " + lastGen);
//		Parameters.parameters.setInteger("lastSavedGeneration", lastGen--);
//		System.out.println("after decrementing generation: " + Parameters.parameters.integerParameter("lastSavedGeneration"));
//		System.out.println("offspring to string prints out: " + "offspring.toString()");
//
//		System.out.println("This button is not yet implemented");
	}
}
