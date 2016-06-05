package edu.utexas.cs.nn.tasks.picbreeder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.*;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.BooleanUtil;
import edu.utexas.cs.nn.util.GraphicsUtil;
import edu.utexas.cs.nn.util.MiscUtil;

/**
 * 
 * @author gillespl
 *
 * @param <T>
 */
public class PicbreederTask<T extends Network> implements SinglePopulationTask<T>, ActionListener, NetworkTask {

	//Global static final variables
	public static final int CPPN_NUM_INPUTS	= 4;
	public static final int CPPN_NUM_OUTPUTS = 3;
	public static final int NUM_COLUMNS	= 4;

	private static final int IMAGE_BUTTON_INDEX = 0;
	private static final int EVOLVE_BUTTON_INDEX = -1;
	private static final int SAVE_BUTTON_INDEX = -2;
	private static final int RESET_BUTTON_INDEX = -3;
	private static final int CLOSE_BUTTON_INDEX	= -4;
	private static final int LINEAGE_BUTTON_INDEX = -5;
	private static final int NETWORK_BUTTON_INDEX = -6;
	private static final int UNDO_BUTTON_INDEX = -7;
	private static final int BORDER_THICKNESS = 4;
	private static final int TOP_PANEL_INDEX = 0;
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
	private int saveIndex;

	/**
	 * Default constructor
	 */
	public PicbreederTask() {
		this(NUM_COLUMNS, MiscUtil.multiplicationFactor(NUM_COLUMNS, Parameters.parameters.integerParameter("mu")), Parameters.parameters.integerParameter("imageSize"));
	}

	/**
	 * Constructor
	 * @param rows number of rows of images
	 * @param columns number of columns of images
	 * @param size size of each image
	 */
	public PicbreederTask(int rows, int columns, int size) {

		Parameters.parameters.setInteger("mu", Parameters.parameters.integerParameter("mu") * NUM_COLUMNS);
		//Global variable instantiations
		NUM_ROWS = rows;
		PIC_SIZE = size;
		NUM_BUTTONS	= Parameters.parameters.integerParameter("mu");
		chosen = new boolean[NUM_BUTTONS];
		showNetwork = false;
		waitingForUser = false;
		saveIndex = 0;
		//Graphics instantiations
		frame = new JFrame("Picbreeder");
		panels = new ArrayList<JPanel>();
		buttons = new ArrayList<JButton>();
		//sets up JFrame
		frame.setSize(PIC_SIZE * NUM_COLUMNS, PIC_SIZE * (NUM_ROWS));
		frame.setLocation(300, 100);//magic #s 100 correspond to relocating frame to middle of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUM_ROWS + 1, 0));// the + 1 includes room for the title panel
		frame.setVisible(true);

		//sets up title graphics

		//instantiate graphics
		JPanel top = new JPanel();
		JButton resetButton = new JButton(new ImageIcon("data\\picbreeder\\reset.png"));
		JButton saveButton = new JButton(new ImageIcon("data\\picbreeder\\save.png"));
		JButton evolveButton = new JButton(new ImageIcon("data\\picbreeder\\arrow.png"));
		JButton closeButton = new JButton(new ImageIcon("data\\picbreeder\\quit.png"));
		JButton lineageButton = new JButton(new ImageIcon("data\\picbreeder\\lineage.png"));
		JButton networkButton = new JButton(new ImageIcon("data\\picbreeder\\network.png"));
		JButton undoButton = new JButton( new ImageIcon("data\\picbreeder\\undo.png"));
		//JLabel label = new JLabel("Picture Evolver");

		//set graphic names
		evolveButton.setName("" + EVOLVE_BUTTON_INDEX);
		saveButton.setName("" + SAVE_BUTTON_INDEX);
		resetButton.setName("" + RESET_BUTTON_INDEX);
		closeButton.setName("" + CLOSE_BUTTON_INDEX);
		lineageButton.setName("" + LINEAGE_BUTTON_INDEX);
		networkButton.setName("" + NETWORK_BUTTON_INDEX);
		undoButton.setName("" + UNDO_BUTTON_INDEX);

		//label.setFont(new Font("Serif", Font.BOLD, 48));
		//label.setForeground(Color.DARK_GRAY);

		//add action listeners
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
		for(int i = 1; i <= NUM_ROWS; i++) {
			JPanel row = new JPanel();
			row.setSize(frame.getWidth(), PIC_SIZE);
			row.setSize(frame.getWidth(), PIC_SIZE);
			row.setLayout(new GridLayout(1, NUM_COLUMNS));
			panels.add(row);
		}
		//adds buttons to button panels
		int x = 0;//used to keep track of index of button panel
		for(JPanel panel: panels) frame.add(panel);
		for(int i = 1; i <= NUM_ROWS; i++) {
			for(int j = 0; j < NUM_COLUMNS; j++) {
				if(x < NUM_BUTTONS) {
					JButton image = getImageButton(GraphicsUtil.solidColorImage(Color.BLACK, PIC_SIZE, PIC_SIZE), "dummy");
					panels.get(i).add(image);
					buttons.add(image);
					buttons.get(x++).addActionListener(this);
				}
			}
		}
	}

	/**
	 * Gets JButton from given image
	 * @param image image to put on button
	 * @param s title of button
	 * @return JButton
	 */
	private JButton getImageButton(BufferedImage image, String s) {
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
	 * 
	 * @param gmi
	 * @param buttonIndex
	 */
	private void resetButton(BufferedImage gmi, int buttonIndex){ 
		ImageIcon img = new ImageIcon(gmi);
		buttons.get(buttonIndex).setName("" + buttonIndex);
		buttons.get(buttonIndex).setIcon(img);
		chosen[buttonIndex] = false;
	}

	/**
	 * 
	 * @param i
	 * @param x
	 * @param button
	 */
	private void save(int i, int x, JButton button) {
		//		JTextField saveName = new JTextField("image " + x, 30);
		//		saveName.addActionListener(this);
		//		button.add(saveName);
		//		MiscUtil.waitForReadStringAndEnterKeyPress();
		BufferedImage toSave = (BufferedImage) ((ImageIcon) button.getIcon()).getImage();
		DrawingPanel p = GraphicsUtil.drawImage(toSave, "" + i, toSave.getWidth(), toSave.getHeight());
		//panels.get(TOP_PANEL_INDEX);
		p.setLocation(x, 0);
		p.save((showNetwork ? "network" : "image") + i + ".bmp");
		System.out.println((showNetwork ? "network number " : "image number ") + i++ + " was saved successfully");
	}

	/**
	 * 
	 * @param tg
	 * @return
	 */
	private BufferedImage getNetwork(Genotype<T> tg) {
		T pheno = tg.getPhenotype();
		DrawingPanel network = new DrawingPanel(PIC_SIZE, PIC_SIZE, "network");
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
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {//TODO 
		waitingForUser = true;
		scores = new ArrayList<Score<T>>();
		if(population.size() != NUM_BUTTONS) {
			throw new IllegalArgumentException("number of genotypes doesn't match size of population! Size of genotypes: " + population.size() + " Num buttons: " + NUM_BUTTONS);
		}	
		for(int x = 0; x < buttons.size(); x++) {
			scores.add(new Score<T>(population.get(x), new double[]{0}, null));
			resetButton(showNetwork ? getNetwork(population.get(x)) : GraphicsUtil.imageFromCPPN((Network)population.get(x).getPhenotype(), PIC_SIZE, PIC_SIZE), x);
			buttons.get(x).setBorder(BorderFactory.createLineBorder(Color.lightGray, BORDER_THICKNESS));
		}
		while(waitingForUser){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return scores;
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
		if(scoreIndex == CLOSE_BUTTON_INDEX) {
			System.exit(1);
		} else if(scoreIndex == RESET_BUTTON_INDEX) {//If reset button clicked
			//not sure what to do here
		} else if(scoreIndex == SAVE_BUTTON_INDEX && BooleanUtil.any(chosen)) { //If save button clicked
			int x = 0;
			for(int i = 0; i < chosen.length; i++) {
				boolean choose = chosen[i];
				if(choose) {//loops through and any image  clicked automatically saved
					save(i ,x, buttons.get(i));
					x += buttons.get(i).getWidth();
				}
			}
		} else if(scoreIndex == LINEAGE_BUTTON_INDEX) {

		} else if(scoreIndex == NETWORK_BUTTON_INDEX) {
			if(showNetwork) {
				showNetwork = false;
				for(int i = 0; i < scores.size(); i++) {
					resetButton(GraphicsUtil.imageFromCPPN((Network)scores.get(i).individual.getPhenotype(), PIC_SIZE, PIC_SIZE), i);
				}
			} else {
				showNetwork = true;
				for(int i = 0; i < buttons.size(); i++) {
					BufferedImage network = getNetwork(scores.get(i).individual);
					resetButton(network, i);
				}

			}
		} else if(scoreIndex == UNDO_BUTTON_INDEX) {

		}else if(scoreIndex == EVOLVE_BUTTON_INDEX && BooleanUtil.any(chosen)) {//If evolve button clicked
			System.out.println("congratulations you pressed the evolve button");
			System.out.println("scores: " + scores);	
			System.out.println("boolean values: " + Arrays.toString(chosen));
			waitingForUser = false;//tells evaluateAll method to finish

		} else if(scoreIndex >= IMAGE_BUTTON_INDEX) {//If an image button clicked
			assert (scores.size() == buttons.size()) : 
				"size mismatch! score array is " + scores.size() + " in length and buttons array is " + buttons.size() + " long";
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
	}


	@SuppressWarnings({ "rawtypes"})
	public static void main(String[] args) {
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "netChangeActivationRate:0.4", "recurrency:false" });
		MMNEAT.loadClasses();

		PicbreederTask test = new PicbreederTask(4, 4, 250);//also good for testInABottle
		//PicbreederTask test = new PicbreederTask(0, 0, 0);//test for button reset
		//testInABottle(test);
		moreBullshitTests(test);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void testInABottle(PicbreederTask test) {

		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		for(int i = 0; i < 16; i++) {
			final int NUM_MUTATIONS = 200;
			TWEANNGenotype tg1 = new TWEANNGenotype(4, 3, false, 0, 1, 0);
			for (int j = 0; j < NUM_MUTATIONS; j++) {
				tg1.mutate();
			}
			genotypes.add(tg1);
		}
		test.scores = new ArrayList<Score>();
		for(int i = 0; i < test.buttons.size(); i++) {
			test.scores.add(new Score(genotypes.get(i), new double[]{0}, null));
			ImageIcon img = new ImageIcon(GraphicsUtil.imageFromCPPN((Network) genotypes.get(i).getPhenotype(), 250, 250));
			((Component) test.buttons.get(i)).setName("" + i);
			((AbstractButton) test.buttons.get(i)).setIcon(img);
			((AbstractButton) test.buttons.get(i)).addActionListener(test);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void moreBullshitTests(PicbreederTask test) { 
		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		for(int i = 0; i < 16; i++) {
			final int NUM_MUTATIONS = 200;
			TWEANNGenotype tg1 = new TWEANNGenotype(4, 3, false, 0, 1, 0);
			for (int j = 0; j < NUM_MUTATIONS; j++) {
				tg1.mutate();
			}
			genotypes.add(tg1);
		}
		while(true) {
			ArrayList<Score<TWEANN>> gen0 = test.evaluateAll(genotypes); // replace with a test population
			System.out.println("is this what I'm looking for?");
			System.out.println(gen0);
			for(int i = 0; i < gen0.size(); i++) {
				genotypes.set(i, gen0.get(i).individual);
			}
		}
	}


	@SuppressWarnings("rawtypes")
	public static void testButtonReset(PicbreederTask test) {
		System.out.println("test is running");
		JFrame holder  = new JFrame();
		holder.setSize(500, 500);
		holder.setVisible(true);
		JPanel graphics = new JPanel();
		graphics.setSize(500, 500);
		graphics.setLayout(new GridLayout(3, 3));
		holder.add(graphics);
		ArrayList<JButton> buttons = new ArrayList<JButton>();
		for(int i = 0; i < 9; i++) {
			JButton image = test.getImageButton(GraphicsUtil.solidColorImage(Color.BLACK, 100, 100), "dummy");
			buttons.add(image);
			graphics.add(image);
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
		System.out.print("continue");
		for(int i = 0; i < 5 ; i++){
			ImageIcon img = new ImageIcon(GraphicsUtil.solidColorImage(Color.PINK, 100, 100), "");
			buttons.get(i).setIcon(img);
			graphics.repaint();
			holder.invalidate();
			holder.validate();
			holder.repaint();
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
		System.out.print("continue");

		for(int i = 5; i < 9; i++) {
			ImageIcon img = new ImageIcon(GraphicsUtil.solidColorImage(Color.GREEN, 100, 100), "");
			buttons.get(i).setIcon(img);
			graphics.repaint();
			holder.invalidate();
			holder.validate();
			holder.repaint();

		}
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

}
