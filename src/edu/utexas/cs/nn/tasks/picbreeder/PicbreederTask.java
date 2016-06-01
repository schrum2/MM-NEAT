package edu.utexas.cs.nn.tasks.picbreeder;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.GraphicsUtil;

/**
 * 
 * @author gillespl
 *
 * @param <T>
 */
public class PicbreederTask<T extends Network> implements SinglePopulationTask<T>, ActionListener {

	private final int NUM_ROWS;
	private final int NUM_COLUMNS;
	private final int PIC_SIZE;


	private JFrame frame;
	private ArrayList<JPanel> panels;
	private ArrayList<JButton> buttons;
	private ArrayList<Score<T>> scores;
	private ArrayList<Boolean> chosen;
	
	private boolean waitingForUser = false;

	public PicbreederTask(int rows, int columns, int size) {
		NUM_ROWS = rows;
		NUM_COLUMNS = columns;
		PIC_SIZE = size;
		frame = new JFrame("Picbreeder");
		panels = new ArrayList<JPanel>(NUM_ROWS);
		buttons = new ArrayList<JButton>((NUM_ROWS - 1) * NUM_COLUMNS);
		chosen = new ArrayList<Boolean>();
		frame.setSize(PIC_SIZE * NUM_COLUMNS, PIC_SIZE * (NUM_ROWS));
		frame.setLocation(300, 100);//magic #s 100 correspond to relocating frame to middle of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(NUM_ROWS + 1, 0));
		frame.setVisible(true);
		frame.setBackground(Color.RED);

		JPanel top = new JPanel();
		JLabel label = new JLabel("Picture Evolver");
		label.setFont(new Font("Serif", Font.BOLD, 48));
		label.setForeground(Color.DARK_GRAY);
		top.add(label);
		label.setLocation((int) frame.getAlignmentX(), 5);//5 is magic # to set just below menu
		JButton evolveButton = new JButton(new ImageIcon("data\\picbreeder\\arrow.png"));
		evolveButton.setName("" + -1);
		evolveButton.addActionListener(this);
		evolveButton.setLocation((int)(frame.getWidth() * (3.0/4)), (int) top.getAlignmentY());
		top.add(evolveButton);
		panels.add(top);

		for(int i = 1; i <= NUM_ROWS; i++) {
			JPanel row = new JPanel();
			row.setSize(frame.getWidth(), PIC_SIZE);
			row.setSize(frame.getWidth(), PIC_SIZE);
			row.setLayout(new GridLayout(1, NUM_COLUMNS));
			panels.add(row);
		}

		for(JPanel panel: panels) {
			frame.add(panel);
			for(int j = 0; j < NUM_COLUMNS; j++) {
				JButton image = getImageButton(GraphicsUtil.solidColorImage(Color.BLACK, PIC_SIZE, PIC_SIZE), "dummy");
				panel.add(image);
				buttons.add(image);
				chosen.add(false);
			}
		}
	}


	private JButton getImageButton(BufferedImage image, String s) {
		JButton button = new JButton(new ImageIcon(image));
		button.setName(s);
		return button;
	}


	private JButton getImageButton(Genotype<T> genotype, String s) {
		BufferedImage image = GraphicsUtil.imageFromCPPN(genotype.getPhenotype(), PIC_SIZE, PIC_SIZE); 
		return getImageButton(image, s);
	}

	public double[] evaluate() {
		return new double[]{1.0};
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
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {//TODO paint icon method is being a butt
		waitingForUser = true;
		scores = new ArrayList<Score<T>>();
		if(population.size() != NUM_ROWS * NUM_COLUMNS) {
			throw new IllegalArgumentException("number of genotypes doesn't match size of population!");
		}
		int x = 0;
		for(int i = 1; i < panels.size(); i++) {
			for(int j = 0; j < NUM_COLUMNS; j++) {
				scores.add(new Score<T>(population.get(x), new double[]{0}, null));
				buttons.get(x).getIcon().paintIcon(buttons.get(x), getImageButton(population.get(x), ""+ x).getGraphics(), PIC_SIZE, PIC_SIZE);
				buttons.get(x).addActionListener(this);
				panels.get(i).add(buttons.get(x));
				buttons.add(buttons.get(x++));
				chosen.add(false);
			}
		}
		
		while(waitingForUser){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return scores;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Scanner s = new Scanner(event.toString());
		s.next();
		s.next();
		int scoreIndex = s.nextInt();
		if(scoreIndex == -1) {// TODO 
			//need to add code here that mutates chosen individuals and updates jpanel
			System.out.println("congratulations you pressed the evolve button");
			waitingForUser = false;
		} else {
			if(scores.size() != buttons.size()) {
				s.close();
				throw new IllegalArgumentException("size mismatch! score array is " + scores.size()
				+ " in length and buttons array is " + buttons.size() + " long");
			}
			scores.get(scoreIndex).replaceScores(evaluate());
			int buttonIndex = buttons.indexOf(event.getSource());
			if(chosen.get(buttonIndex)) {
				chosen.set(buttonIndex, false);
				buttons.get(buttonIndex).setBorder(BorderFactory.createLineBorder(Color.lightGray));
				scores.get(scoreIndex).replaceScores(new double[]{0});
			} else {
				chosen.set(buttonIndex, true);
				buttons.get(buttonIndex).setBorder(BorderFactory.createLineBorder(Color.YELLOW));
			}
		}
		s.close();
	}














	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		System.out.println("test is running");

		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "netChangeActivationRate:0.4", "recurrency:false" });
		MMNEAT.loadClasses();

		PicbreederTask test = new PicbreederTask(4, 3, 250);

		ArrayList<Genotype> genotypes = new ArrayList<Genotype>();
		for(int i = 0; i < 12; i++) {
			final int NUM_MUTATIONS = 200;
			TWEANNGenotype tg1 = new TWEANNGenotype(4, 3, false, 0, 1, 0);
			for (int j = 0; j < NUM_MUTATIONS; j++) {
				tg1.mutate();
			}
			genotypes.add(tg1);
		}
		ArrayList<Score<TWEANN>> gen0 = test.evaluateAll(genotypes); // replace with a test population
		System.out.println(gen0);
		
		genotypes = new ArrayList<Genotype>();
		for(int i = 0; i < 12; i++) {
			final int NUM_MUTATIONS = 200;
			TWEANNGenotype tg1 = new TWEANNGenotype(4, 3, false, 0, 1, 0);
			for (int j = 0; j < NUM_MUTATIONS; j++) {
				tg1.mutate();
			}
			genotypes.add(tg1);
		}
		
		ArrayList<Score<TWEANN>> gen1 = test.evaluateAll(genotypes); // replace with a test population
		System.out.println(gen1);
	}


}
