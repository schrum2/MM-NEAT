package edu.utexas.cs.nn.tasks.rlglue.mountaincar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.rlcommunity.environments.mountaincar.MountainCarState;
import org.rlcommunity.environments.mountaincar.messages.MCGoalRequest;
import org.rlcommunity.environments.mountaincar.messages.MCGoalResponse;
import org.rlcommunity.environments.mountaincar.messages.MCHeightRequest;
import org.rlcommunity.environments.mountaincar.messages.MCHeightResponse;
import org.rlcommunity.environments.mountaincar.visualizer.MountainCarVisualizer;

import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import rlVizLib.messaging.environment.EnvRangeRequest;
import rlVizLib.messaging.environment.EnvRangeResponse;
import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.VizComponentChangeListener;

public final class MountainCarViewer {

	public static MountainCarViewer current = null;
	public static final int HEIGHT = 500;
	public static final int WIDTH = 500;
	public static final String TITLE = "Mountain_Car";
	public DrawingPanel panel;

	private double theGoalPosition = 0.0d;
	private double theGoalHeight = 0.0d;
	boolean everDrawn = false;
	
	/**
	 * Sets up the viewer for MountainCar
	 */
	public MountainCarViewer() {
		panel = new DrawingPanel(WIDTH, HEIGHT, TITLE);
		panel.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
		reset();
		current = this;
	}
	
	/**
	 * Resets the graphics for the view
	 */
	public void reset() {
		Graphics2D g = panel.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		render(g);
	}
	
	public void render(Graphics2D g) {
		if (!everDrawn) {
			theGoalPosition = getGoalPosition();
			Vector<Double> tempVec = new Vector<Double>();
			tempVec.add(theGoalPosition);

//			Vector<Double> returnVector = theVizualizer.getHeightsForPositions(tempVec);
			Vector<Double> returnVector = getHeightsForPositions(tempVec);
			theGoalHeight = returnVector.get(0);
			everDrawn = true;

		}

		AffineTransform theScaleTransform = new AffineTransform();

		// Draw a rectangle
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1, 1);

		double scaleFactor = 1000.0d;
		theScaleTransform.scale(1.0d / scaleFactor, 1.0d / scaleFactor);
		AffineTransform origTransform = g.getTransform();

		AffineTransform x = g.getTransform();
		x.concatenate(theScaleTransform);
		g.setTransform(x);

//		theHeights = theVizualizer.getSampleHeights();
		theHeights = getSampleHeights();

		double sizeEachComponent = 1.0 / (double) theHeights.size();

		g.setColor(Color.BLACK);

		double lastX = 0.0d;
		double lastY = 1.0 - UtilityShop.normalizeValue(1, getMinHeight(), getMaxHeight());
		for (int i = 1; i < theHeights.size(); i++) {
			double thisX = lastX + sizeEachComponent;
			double thisY = 1.0 - UtilityShop.normalizeValue(theHeights.get(i), getMinHeight(), getMaxHeight());

			
			Line2D thisLine = new Line2D.Double(scaleFactor * lastX, scaleFactor * lastY, scaleFactor * thisX,
					scaleFactor * thisY);

			lastX = thisX;
			lastY = thisY;

			g.draw(thisLine);
		}

		g.setTransform(origTransform);

		g.setColor(Color.GREEN);

		// to bring things back into the window
		double minPosition = getMinValueForDim(0);
		double maxPosition = getMaxValueForDim(0);

		double transX = UtilityShop.normalizeValue(theGoalPosition, minPosition, maxPosition);
		// need to get the actual height ranges
		double transY = UtilityShop.normalizeValue(theGoalHeight, getMinHeight(), getMaxHeight());
		transY = (1.0 - transY);

		double rectWidth = .05 / 4;
		double rectHeight = 0.1;
		Rectangle2D fillRect = new Rectangle2D.Double(transX - rectWidth / 2.0d, transY - rectHeight / 2.0d, rectWidth, rectHeight);
		//Rectangle2D fillRect = new Rectangle2D.Double(1, 2, 10, 10);
		g.fill(fillRect);

	}

	////////////////////////////////////////////////////////////////
	// Code below this line taken from MountainCarVisualizer
	////////////////////////////////////////////////////////////////
	
	private Vector<Double> mins = null;
	private Vector<Double> maxs = null;
	double minHeight = Double.MIN_VALUE;
	double maxHeight = Double.MAX_VALUE;
	private Vector<Double> theQueryPositions = null;
	private Vector<Double> theHeights = null;

	public double getMaxHeight() {
		if (theQueryPositions == null) {
			initializeHeights();
		}
		return minHeight;
	}

	public double getMinHeight() {
		if (theQueryPositions == null) {
			initializeHeights();
		}
		return maxHeight;
	}
	
	public Vector<Double> getSampleHeights() {
		if (theHeights == null) {
			initializeHeights();
		}
		return theHeights;
	}

	public double getGoalPosition() { 
		MCGoalResponse goalResponse = MCGoalRequest.Execute();
		return goalResponse.getGoalPosition();
	}

	public void initializeHeights() {
		// Because we can change the shape of the curve we have no guarantees
		// what
		// the max and min heights of the mountains may turn out to be...
		// this takes a quick sample based approach to find out what is a good
		// approximation
		// for the min and the max.
		double minPosition = getMinValueForDim(0);
		double maxPosition = getMaxValueForDim(0);

		int pointsToDraw = 500;
		double theRangeSize = maxPosition - minPosition;
		double pointIncrement = theRangeSize / (double) pointsToDraw;

		theQueryPositions = new Vector<Double>();
		for (double i = minPosition; i < maxPosition; i += pointIncrement) {
			theQueryPositions.add(i);
		}
		theHeights = this.getHeightsForPositions(theQueryPositions);

		maxHeight = Double.MIN_VALUE;
		minHeight = Double.MAX_VALUE;
		for (Double thisHeight : theHeights) {
			if (thisHeight > maxHeight) {
				maxHeight = thisHeight;
			}
			if (thisHeight < minHeight) {
				minHeight = thisHeight;
			}
		}
	}
	
	public Vector<Double> getHeightsForPositions(Vector<Double> theQueryPositions) {
		MCHeightResponse heightResponse = MCHeightRequest.Execute(theQueryPositions);
		return heightResponse.getHeights();
	}

	public double getMaxValueForDim(int whichDimension) {
		if (maxs == null) {
			updateEnvironmentVariableRanges();
		}
		return maxs.get(whichDimension);
	}

	public double getMinValueForDim(int whichDimension) {
		if (mins == null) {
			updateEnvironmentVariableRanges();
		}
		return mins.get(whichDimension);
	}
	
	public void updateEnvironmentVariableRanges() {
		// Get the Ranges (internalize this)
		EnvRangeResponse theERResponse = EnvRangeRequest.Execute();

		if (theERResponse == null) {
			System.err.println("Asked an Environment for Variable Ranges and didn't get back a parseable message.");
			Thread.dumpStack();
			System.exit(1);
		}

		mins = theERResponse.getMins();
		maxs = theERResponse.getMaxs();
	}

}
