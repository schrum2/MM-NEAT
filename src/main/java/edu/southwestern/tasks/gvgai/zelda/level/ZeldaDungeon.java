package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import gvgai.core.game.BasicGame;
import gvgai.tracks.singlePlayer.tools.human.Agent;


public abstract class ZeldaDungeon<T> {
	
	private static final int ZELDA_HEIGHT = (176/11)*16;//Parameters.parameters.integerParameter("zeldaImageHeight");
	private static final int ZELDA_WIDTH = 176;//Parameters.parameters.integerParameter("zeldaImageWidth");
	
	public abstract Level[][] makeDungeon(ArrayList<T> phenotypes, int numRooms);
	
	public abstract List<List<Integer>> getLevelFromLatentVector(T phenotype);
	
	public void showDungeon(ArrayList<T> phenotypes, int numRooms) {
		Level[][] dungeon = makeDungeon(phenotypes, numRooms);
		
		JFrame frame = new JFrame("Dungeon Viewer");
		frame.setSize(1000, 1000);
		
		JPanel dungeonGrid = new JPanel();
		dungeonGrid.setLayout(new GridLayout(numRooms, numRooms));
		
		for(int i = 0; i < dungeon.length; i++) {
			for(int j = 0; j < dungeon[i].length; j++) {
				if(dungeon[i][j] != null) {
					BufferedImage level = getButtonImage(dungeon[i][j], ZELDA_WIDTH, ZELDA_HEIGHT); //creates image rep. of level)
					ImageIcon img = new ImageIcon(level.getScaledInstance(ZELDA_WIDTH, ZELDA_HEIGHT, Image.SCALE_DEFAULT)); //creates image of level
					JLabel imageLabel = new JLabel(img); // places level on label
					dungeonGrid.add(imageLabel); //add label to panel
				} else {
					JLabel blankText = new JLabel("");
					blankText.setForeground(Color.WHITE);
					JPanel blankBack = new JPanel();
					blankBack.setBackground(Color.BLACK);
					blankBack.add(blankText);
					dungeonGrid.add(blankBack);
				}
			}
		}
		
		frame.add(dungeonGrid);
		frame.setVisible(true);
	}
	
	private BufferedImage getButtonImage(Level level, int width, int height) {
		GameBundle bundle = ZeldaGANLevelBreederTask.setUpGameWithLevelFromList(level.getLevel());
		return GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, width, height, bundle.randomSeed);
	}
	
	public static class Level{
		List<List<Integer>> intLevel;
		
		public Level(List<List<Integer>> intLevel) {
			this.intLevel = intLevel;
		}
		
		public List<List<Integer>> getLevel(){
			return this.intLevel;
		}
	}
	
}
