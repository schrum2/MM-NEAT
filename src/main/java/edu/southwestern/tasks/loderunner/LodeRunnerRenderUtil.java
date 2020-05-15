package edu.southwestern.tasks.loderunner;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class LodeRunnerRenderUtil {
	public static final int LODE_RUNNER_TILE_X = 8; // x length of an individual tile 
	public static final int LODE_RUNNER_TILE_Y = 7; // y length of an individual tile 
	public static final int LODE_RUNNER_COLUMNS = 32; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 22; // Equivalent to width in original game
	
	
	public static void main(String[] args) {
		
	}

	
	public static BufferedImage getBufferedImage(List<List<Integer>> list) {
		int width = LODE_RUNNER_TILE_X*LODE_RUNNER_COLUMNS;
		int height = LODE_RUNNER_TILE_Y*LODE_RUNNER_ROWS;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		setTilesForBufferedImage(image, list, width, height);
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JLabel label = new JLabel(new ImageIcon(image.getScaledInstance(width/2, height/2, Image.SCALE_FAST)));
		panel.add(label);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		return image;
	}


	private static BufferedImage setTilesForBufferedImage(BufferedImage image, List<List<Integer>> list, int width, int height) {
		Graphics2D load = (Graphics2D) image.getGraphics();
		load.setRenderingHint(
			    RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
		load.setRenderingHint(
			    RenderingHints.KEY_TEXT_ANTIALIASING,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		image =  load.getDeviceConfiguration().createCompatibleImage(width, height);
		for(int x = 0; x < LODE_RUNNER_TILE_X; x++) {
			for(int y = 0; y < LODE_RUNNER_TILE_Y; y++) {
				
			}
		}
		return image;
	}
	
	
	
}
