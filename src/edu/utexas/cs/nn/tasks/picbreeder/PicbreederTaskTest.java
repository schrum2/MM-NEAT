package edu.utexas.cs.nn.tasks.picbreeder;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.GraphicsUtil;
import edu.utexas.cs.nn.util.MiscUtil;

public class PicbreederTaskTest {

	@Test
	public void test() {
		fail("This test is a mess, ignore");
	}
static ArrayList<JButton> buttons = new ArrayList<JButton>();
static ArrayList<Score> scores = new ArrayList<Score>();
	@SuppressWarnings("rawtypes")
	static
	PicbreederTask test = new PicbreederTask(4, 4, 250);//also good for testInABottle
	@SuppressWarnings({ "rawtypes"})
	public static void main(String[] args) {
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "netChangeActivationRate:0.4", "recurrency:false" });
		MMNEAT.loadClasses();

		
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
		scores = new ArrayList<Score>();
		for(int i = 0; i < buttons.size(); i++) {
			scores.add(new Score(genotypes.get(i), new double[]{0}, null));
			ImageIcon img = new ImageIcon(GraphicsUtil.imageFromCPPN((Network) genotypes.get(i).getPhenotype(), 250, 250));
			((Component) buttons.get(i)).setName("" + i);
			((AbstractButton) buttons.get(i)).setIcon(img);
			((AbstractButton) buttons.get(i)).addActionListener(test);
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
			JButton image = getImageButton(GraphicsUtil.solidColorImage(Color.BLACK, 100, 100), "dummy");
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
	private static JButton getImageButton(BufferedImage solidColorImage, String string) {
		// TODO Auto-generated method stub
		return null;
	}


}
