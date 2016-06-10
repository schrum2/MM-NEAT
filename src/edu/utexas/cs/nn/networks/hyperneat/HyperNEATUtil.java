package edu.utexas.cs.nn.networks.hyperneat;

import java.awt.Color;
import java.util.ArrayList;

import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.util.datastructures.Pair;



//need a method that takes an array list of nodes from tweann
//uses mMNEAT.task and casts it to hyperNeattask so it can get out specific substrate info about task
//then, creates a drawing panel for each substrate task uses
//fills in activations into rectangles of said substrate drawing panels using activation function from
//corresponding node in array list
//draw grid on substrate
//every time refreshed, draw new rectangles on top of old rectangles
//need a variable that scales rectangles down just a bit so it doesn't erase grid

public class HyperNEATUtil {

	public final static int SUBS_GRID_SIZE = 30;

	public static DrawingPanel drawSubstrate(Substrate s, ArrayList<Node> nodes, Color c) { 
		DrawingPanel p = new DrawingPanel(s.size.t1 * SUBS_GRID_SIZE, s.size.t2 * SUBS_GRID_SIZE, s.name);
		return drawSubstrate(p, s, nodes, c); // updates existing panel
	}

	// Call inside of TWEANN.process at end
	public static DrawingPanel drawSubstrate(DrawingPanel dp, Substrate s, ArrayList<Node> nodes, Color c) { 
		for(int i = 0; i < s.size.t1; i ++) {
			for(int j = 0; j < s.size.t2; j++) {
				drawCoord(dp, s.size, c);
				drawGrid(dp, s.size);
			}
		}
		return dp;
	}
	private static void drawGrid(DrawingPanel p, Pair<Integer, Integer> size) {
		// Loop through columns and rows to draw black lines
		p.getGraphics().setBackground(Color.white);
		p.getGraphics().setColor(Color.black);
		for(int i = 0; i <= size.t1; i++) {
			for(int j = 0; j <= size.t2; j++) {
				p.getGraphics().drawRect(i * SUBS_GRID_SIZE, j * SUBS_GRID_SIZE, (i + 1) * SUBS_GRID_SIZE, SUBS_GRID_SIZE * (j + 1));
			}
		}	
	}

	private static void drawCoord(DrawingPanel p, Pair<Integer, Integer> size, Color c) { 
		for(int i = 0; i <= size.t1; i++) {
			for(int j = 0; j <= size.t2; j++) {
				p.getGraphics().setColor(c);
				p.getGraphics().fillRect(i ,  j ,  i * SUBS_GRID_SIZE, j * SUBS_GRID_SIZE );
			}
		}

	}
}
