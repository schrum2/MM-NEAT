package edu.utexas.cs.nn.tasks.picbreeder;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.GraphicsUtil;
import edu.utexas.cs.nn.util.MiscUtil;

public class PicbreederTask<T extends Network> implements SinglePopulationTask<T>, ActionListener {

	private static final int LABEL_INDEX = 0;
	
	private  final int NUM_ROWS;
	private final int NUM_COLUMNS;
	private final int PIC_SIZE;
	
	
	private JFrame frame;
	private ArrayList<JPanel> panels;
	private ArrayList<JButton> buttons;
	private ArrayList<Boolean> keepScore;
	private ArrayList<Score<T>> scores;
	
	
	public PicbreederTask(int rows, int columns, int size) {//TODO
		NUM_ROWS = rows + 1;
		NUM_COLUMNS = columns;
		PIC_SIZE = size;
		frame = new JFrame("Picbreeder");
		panels = new ArrayList<JPanel>(NUM_ROWS);
		keepScore = new ArrayList<Boolean>((NUM_ROWS - 1) * NUM_COLUMNS);
		buttons = new ArrayList<JButton>((NUM_ROWS - 1) * NUM_COLUMNS);
		frame.setSize(PIC_SIZE * NUM_COLUMNS, PIC_SIZE * (NUM_ROWS));
		frame.setLocation(300, 100);//magic #s 100 correspond to relocating frame to middle of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUM_ROWS, 0));
		frame.setVisible(true);
		frame.setBackground(Color.RED);
		
		
		for(int i = 0; i < NUM_ROWS; i++) {
			panels.add(new JPanel());
		}
		
		
		for(int i = 0; i < NUM_ROWS; i++) {
			if(i == LABEL_INDEX){
				panels.get(i).setSize(frame.getWidth(), PIC_SIZE);
				frame.add(panels.get(i));
			}
			panels.get(i).setSize(frame.getWidth(), PIC_SIZE);
			panels.get(i).setLayout(new GridLayout(1, NUM_COLUMNS));
			frame.add(panels.get(i));
		}
		
		JLabel label = new JLabel("Picture Evolver");
		label.setFont(new Font("Serif", Font.BOLD, 48));
		label.setForeground(Color.DARK_GRAY);
		panels.get(LABEL_INDEX).add(label);
		label.setLocation((int) frame.getAlignmentX() + 10, 5);//5 is magic # to set just below menu

		
	}

	/*
	 * shouldnt actually be used in the final code. For testing purposes
	 */
	private JButton getImageButton(BufferedImage image) {
		JButton button = new JButton(new ImageIcon(image));
		return button;
	}

	
	private JButton getImageButton(Genotype<T> genotype) {
		BufferedImage image = GraphicsUtil.imageFromCPPN(genotype.getPhenotype(), PIC_SIZE, PIC_SIZE); 
		return getImageButton(image);
	}

	public double evaluate(boolean action) {
		if(action) return 1.0;
		else return 0.0;
	}
	@Override
	public int numObjectives() {
		return 1;
	}

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
	 * 	 * this method also makes no sense in 
	 * scope of this task
	 */
	@Override
	public void finalCleanup() {
	}

	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {// TODO

		System.out.println("Sanity check");
		
		scores = new ArrayList<Score<T>>();
//		if(population.size() != (NUM_ROWS - 1) * NUM_COLUMNS) {
//			throw new IllegalArgumentException("number of genotypes doesn't match size of population!");
//		}
		// Most of what is below needs to be changes somehow
		int x = 0;
		for(int i = LABEL_INDEX + 1; i < panels.size(); i++) {
			for(int j = 0; j < NUM_COLUMNS; j++) {
				JButton image = getImageButton(population.get(j));
				image.addActionListener(this);
				panels.get(j).add(image);
				buttons.add(image);
				//keepScore.set(x++, false);
				break;
			}
			break;
		}
		
		
		//need key listener to wait
		System.out.println("another sanity check");
		//need to add action listeners
		//use actions from mouse to determine score
		return scores; // change
	}

	@Override
	public void actionPerformed(ActionEvent event) {// TODO 
		//keepScore.set(buttons.indexOf(event.getSource()), true);
		System.out.println("You pressed a button!");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		System.out.println("test is running");
		
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "netChangeActivationRate:0.4", "recurrency:false" });
		MMNEAT.loadClasses();
		
		PicbreederTask test = new PicbreederTask(3, 4, 250);


		final int NUM_MUTATIONS = 200;

		TWEANNGenotype tg1 = new TWEANNGenotype(4, 3, false, 0, 1, 0);
		for (int i = 0; i < NUM_MUTATIONS; i++) {
			tg1.mutate();
		}
		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		genotypes.add(tg1);
		test.evaluateAll(genotypes); // replace with a test population
		MiscUtil.waitForReadStringAndEnterKeyPress();
	}

	
}
