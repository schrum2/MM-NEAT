package edu.utexas.cs.nn.tasks.picbreeder;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.BooleanUtil;
import edu.utexas.cs.nn.util.GraphicsUtil;

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
	private static final int BORDER_THICKNESS = 4;
	
	//Private final variables
	private final int NUM_ROWS;
	private final int PIC_SIZE;
	private final int NUM_BUTTONS;

	//Private graphic objects
	private JFrame frame;
	private ArrayList<JPanel> panels;
	private ArrayList<JButton> buttons;
	private ArrayList<Score<T>> scores;

	//private helper variables
	private boolean showNetwork;
	private boolean waitingForUser;
	private boolean[] chosen;
	
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
		showNetwork = false;
		waitingForUser = false;
		
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
		JPanel top = new JPanel();
		JButton resetButton = new JButton(new ImageIcon("data\\picbreeder\\reset.png"));
		JButton saveButton = new JButton(new ImageIcon("data\\picbreeder\\save.png"));
		JButton evolveButton = new JButton(new ImageIcon("data\\picbreeder\\arrow.png"));
		JButton closeButton = new JButton(new ImageIcon("data\\picbreeder\\quit.png"));
		JButton lineageButton = new JButton(new ImageIcon("data\\picbreeder\\lineage.png"));
		JButton networkButton = new JButton(new ImageIcon("data\\picbreeder\\network.png"));
		JButton undoButton = new JButton( new ImageIcon("data\\picbreeder\\undo.png"));

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

		//add action listeners to buttons
		resetButton.addActionListener(this);
		saveButton.addActionListener(this);
		evolveButton.addActionListener(this);
		closeButton.addActionListener(this);
		lineageButton.addActionListener(this);
		networkButton.addActionListener(this);
		undoButton.addActionListener(this);

		//add graphics to title panel
		top.add(lineageButton);
		top.add(networkButton);
		top.add(resetButton);
		top.add(evolveButton);
		top.add(saveButton);
		top.add(undoButton);
		top.add(closeButton);
		panels.add(top);

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
		//need to reset the ea log??
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
		if(scoreIndex == CLOSE_BUTTON_INDEX) {//If close button clicked
			System.exit(1);
		} else if(scoreIndex == RESET_BUTTON_INDEX) {//If reset button clicked
			reset();
		} else if(scoreIndex == SAVE_BUTTON_INDEX && BooleanUtil.any(chosen)) { //If save button clicked
			saveAll();
		} else if(scoreIndex == LINEAGE_BUTTON_INDEX) {//If lineage button clicked
			setLineage();
		} else if(scoreIndex == NETWORK_BUTTON_INDEX) {//If network button clicked
			setNetwork();
		} else if(scoreIndex == UNDO_BUTTON_INDEX) {//If undo button clicked
			setUndo();
		}else if(scoreIndex == EVOLVE_BUTTON_INDEX && BooleanUtil.any(chosen)) {//If evolve button clicked
			waitingForUser = false;//tells evaluateAll method to finish
		} else if(scoreIndex >= IMAGE_BUTTON_INDEX) {//If an image button clicked
			assert (scores.size() == buttons.size()) : 
				"size mismatch! score array is " + scores.size() + " in length and buttons array is " + buttons.size() + " long";
			buttonPressed(scoreIndex);
		}
	}

	private void setLineage() {
		// TODO Auto-generated method stub
		
	}

	private void setUndo() {
		// TODO Auto-generated method stub
		
	}
}
