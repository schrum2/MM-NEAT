package edu.utexas.cs.nn.tasks.picbreeder;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.GraphicsUtil;

public class PicbreederTask<T> implements SinglePopulationTask<T>, ActionListener {

	private static final int NUM_ROWS = 4;
	private static final int NUM_COLUMNS = 3;
	private static final int PIC_SIZE = 250;
	private static final int BORDER = 5;
	private ArrayList<Genotype<T>> genotypes;
	private static JFrame frame;
	private Network network;
	public PicbreederTask<T> task;
	
	public PicbreederTask() {//TODO
		frame = new JFrame("Picbreeder");
		genotypes = loadGenotypes();
		JPanel panel = new JPanel();
		network = (Network) new HyperNEATCPPNGenotype();
		//magic #s 4 & 5 correspond to padding edges
		frame.setSize(PIC_SIZE * NUM_ROWS + BORDER * 5, (int) (PIC_SIZE * NUM_COLUMNS + BORDER * 4 + PIC_SIZE * (2.0/3.0)));
		frame.setLocation(300, 100);//magic #s 100 correspond to relocating frame to middle of screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        JLabel label = new JLabel("Picture Evolver");
        label.setFont(new Font("Serif", Font.BOLD, 48));
        label.setForeground(Color.DARK_GRAY);
        panel.add(label);
        frame.add(panel);
        label.setLocation(frame.getWidth()/2, 5);//5 is magic # to set just below menu
        
        
        JButton image1 = getImage(genotypes.get(1));
        panel.add(image1);
        image1.setLocation(BORDER, (int) (NUM_ROWS * 2.0/3.0 + BORDER)); 
        JButton image2 = getImage(genotypes.get(2));
        panel.add(image2);
        image2.setLocation(BORDER * 2 + PIC_SIZE, (int) (NUM_ROWS * 2.0/3.0 + BORDER));
        JButton image3 = getImage(genotypes.get(3));
        panel.add(image3);
        image3.setLocation(BORDER * 3 + PIC_SIZE * 2, (int) (NUM_ROWS * 2.0/3.0 + BORDER));
        JButton image4 = getImage(genotypes.get(4));
        panel.add(image4);
        image4.setLocation(BORDER * 4 + PIC_SIZE * 3, (int) (NUM_ROWS * 2.0/3.0 + BORDER));
        JButton image5 = getImage(genotypes.get(5));
        panel.add(image5);
        image5.setLocation(BORDER, (int) (NUM_ROWS * 2.0/3.0) + BORDER * 2 + PIC_SIZE);
        JButton image6 = getImage(genotypes.get(6));
        panel.add(image6);
        image6.setLocation(BORDER * 2 + PIC_SIZE, (int) (NUM_ROWS * 2.0/3.0 + BORDER * 2 + PIC_SIZE));
        JButton image7 = getImage(genotypes.get(7));
        panel.add(image7);
        image7.setLocation(BORDER * 3 + PIC_SIZE * 2, (int) (NUM_ROWS * 2.0/3.0 + BORDER * 2 + PIC_SIZE));
        JButton image8 = getImage(genotypes.get(8));
        panel.add(image8);
        image8.setLocation(BORDER * 4 + PIC_SIZE * 3, (int) (NUM_ROWS * 2.0/3.0 + BORDER * 2 + PIC_SIZE));
        JButton image9 = getImage(genotypes.get(9));
        panel.add(image9);
        image9.setLocation(BORDER, (int) (NUM_ROWS * 2.0/3.0 + BORDER * 3 + PIC_SIZE * 2));
        JButton image10 = getImage(genotypes.get(10));
        panel.add(image10);
        image10.setLocation(BORDER * 2 + PIC_SIZE, (int) (NUM_ROWS * 2.0/3.0 + BORDER * 3 + PIC_SIZE * 2));
        JButton image11 = getImage(genotypes.get(11));
        panel.add(image11);
        image11.setLocation(BORDER * 3 + PIC_SIZE * 2, (int) (NUM_ROWS * 2.0/3.0 + BORDER * 3 + PIC_SIZE * 2));
        JButton image12 = getImage(genotypes.get(12));
        panel.add(image12);
        image12.setLocation(BORDER * 4 + PIC_SIZE * 3, (int) (NUM_ROWS * 2.0/3.0 + BORDER * 3 + PIC_SIZE * 2));
        
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Genotype<T>> loadGenotypes(){
		ArrayList<Genotype<T>> genos = new ArrayList<Genotype<T>>(NUM_ROWS*NUM_COLUMNS);
		for(int i = 0; i < genos.size(); i++){
			genos.add((Genotype<T>) new HyperNEATCPPNGenotype());
		}
		
		return genos;
	}
	private JButton getImage(Genotype<T> genotype) {
		BufferedImage image = GraphicsUtil.imageFromCPPN(network, PIC_SIZE, PIC_SIZE); 
		JButton button = new JButton((Icon) image);
		return button;
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
		return new double[] {0};
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
	 * Not a necessary method
	 */
	@Override
	public void finalCleanup() {
	}

	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static void main(String[] args) {
		System.out.println("test is running");
		PicbreederTask test = new PicbreederTask();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
