package edu.utexas.cs.nn.util.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import edu.utexas.cs.nn.util.MiscUtil;

/**
 *
 * @author Jacob Schrum
 */
public class Plot {

	public static final int BROWSE_DIM = 300;
	public static final int OFFSET = 20;
	public static final int EDGE = 16;
	public static final int TOP = 56;
	public static final int OVAL_DIM = 5;
	
	public static void linePlot(DrawingPanel panel, double min, double max, ArrayList<Double> scores, Color color) {
		Graphics g = panel.getGraphics();
		int height = panel.getFrame().getHeight() - 50; // -50 is to avoid gray panel at bottom of DrawingPanel
		int width = panel.getFrame().getWidth();		
		g.setColor(Color.black);
		// y-axis
		g.drawLine(OFFSET, OFFSET, OFFSET, height - OFFSET);	
		// x-axis
		g.drawLine(OFFSET, height - OFFSET, width - OFFSET, height - OFFSET);
		double last = scores.get(0);
		double maxRange = Math.max(max, max - min);
		double lowerMin = Math.min(0, min);
		for (int i = 1; i < scores.size(); i++) {
			g.setColor(color);
			// g.fillRect(OFFSET + scale((double) i, (double) scores.size()),
			// OFFSET + invert(scores.get(i), max), 1, 1);
			int x1 = OFFSET + scale((double) (i - 1), (double) scores.size(), 0, width);
			int y1 = OFFSET + invert(last, maxRange, lowerMin, height);
			int x2 = OFFSET + scale((double) i, (double) scores.size(), 0, width);
			int y2 = OFFSET + invert(scores.get(i), maxRange, lowerMin, height);

			//System.out.println(x1+","+ y1+","+ x2+","+ y2);
			g.drawLine(x1, y1, x2, y2);
			g.setColor(Color.black);
			last = scores.get(i);
		}
		g.drawString("" + max, OFFSET / 2, OFFSET / 2);
		g.drawString("" + lowerMin, OFFSET / 2, height - (OFFSET / 2));
	}

	public static int scale(double x, double max, double min) {
		return scale(x,max,min,BROWSE_DIM);
	}
	
	public static int scale(double x, double max, double min, int totalWidth) {
		return (int) (((x - min) / max) * (totalWidth - (2 * OFFSET)));
	}

	public static int invert(double y, double max, double min) {
		return invert(y,max,min);
	}
	
	public static int invert(double y, double max, double min, int totalHeight) {
		return (totalHeight - (2 * OFFSET)) - scale(y, max, min, totalHeight);
	}
}
