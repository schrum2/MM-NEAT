package edu.southwestern.tasks.rlglue.mountaincar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.rlcommunity.environments.mountaincar.MountainCarState;
import org.rlcommunity.environments.mountaincar.messages.MCGoalRequest;
import org.rlcommunity.environments.mountaincar.messages.MCGoalResponse;
import org.rlcommunity.environments.mountaincar.messages.MCHeightRequest;
import org.rlcommunity.environments.mountaincar.messages.MCHeightResponse;

import edu.southwestern.networks.TWEANN;
import edu.southwestern.util.graphics.DrawingPanel;
import rlVizLib.messaging.environment.EnvRangeRequest;
import rlVizLib.messaging.environment.EnvRangeResponse;
import rlVizLib.utilities.UtilityShop;

public final class MountainCarViewer {

	public static MountainCarViewer current = null;
	public static final int HEIGHT = 500;
	public static final int WIDTH = 500;
	public static final double CEILING_SPACE = 50;
	public static final double SCALE_FACTOR = WIDTH - CEILING_SPACE;
	public static final String TITLE = "Mountain_Car";
	public DrawingPanel panel;

	private double theGoalPosition = 0.0d;
	private double theGoalHeight = 0.0d;
	boolean everDrawn = false;
	
	private Image carImageNeutral = null;
	private Image carImageLeft = null;
	private Image carImageRight = null;
	
	/**
	 * Sets up the viewer for MountainCar
	 */
	public MountainCarViewer() {
		panel = new DrawingPanel(WIDTH, HEIGHT, TITLE);
		
		try {
			carImageNeutral = ImageIO.read(new File("data/MountainCar/auto.png"));
			carImageNeutral = carImageNeutral.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			carImageLeft = ImageIO.read(new File("data/MountainCar/auto_left.png"));
			carImageLeft = carImageLeft.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			carImageRight = ImageIO.read(new File("data/MountainCar/auto_right.png"));
			carImageRight = carImageRight.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		} catch (IOException ex) {
			System.err.println("ERROR: Problem getting car image.");
			System.exit(1);
		}

		
		panel.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
		reset();
		current = this;
	}
	
	// Default: no last action or state
	public void reset() { reset(0, null); }
	
	/**
	 * Resets the graphics for the view
	 */
	public void reset(int lastAction, MountainCarState theState) {
		Graphics2D g = panel.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		renderBackground(g);
		if(theState != null)
			renderCar(g,lastAction, theState);
	}
	
	public double getCurrentStateInDimension(int whichDimension, MountainCarState theState) {
		if (whichDimension == 0) {
			return theState.getPosition();
		} else {
			return theState.getVelocity();
		}
	}
	
	public void renderCar(Graphics2D g, int lastAction, MountainCarState theState) {
		g.setColor(Color.RED);
//		AffineTransform saveAT = g.getTransform();
//		g.scale(.005, .005);

		// to bring things back into the window
		double minPosition = getMinValueForDim(0);
		double maxPosition = getMaxValueForDim(0);

		// From parameter instead
		//int lastAction = mcv.getLastAction();

		double transX = UtilityShop.normalizeValue(getCurrentStateInDimension(0,theState), minPosition, maxPosition);

		// need to get he actual height ranges
		double transY = UtilityShop.normalizeValue(theState.getHeightAtPosition(theState.getPosition()), getMinHeight(), getMaxHeight());
		transY = (1.0 - transY - .05);

		transX *= SCALE_FACTOR; // 200.0d;
		transY *= SCALE_FACTOR; // 200.0d;

		transY += CEILING_SPACE;
		
		double theta = -theState.getSlope(theState.getPosition()) * 1.25;

		Image whichImageToDraw = carImageNeutral;
		if (lastAction == 0) {
			whichImageToDraw = carImageLeft;
		}
		if (lastAction == 2) {
			whichImageToDraw = carImageRight;
		}
		AffineTransform theTransform = AffineTransform.getTranslateInstance(
				transX - whichImageToDraw.getWidth(null) / 2.0d, transY - whichImageToDraw.getHeight(null) / 2.0d);
		theTransform.concatenate(AffineTransform.getRotateInstance(theta, whichImageToDraw.getWidth(null) / 2,
				whichImageToDraw.getHeight(null) / 2));
		g.drawImage(whichImageToDraw, theTransform, null);

//		g.setTransform(saveAT);

	}
	
	public void renderBackground(Graphics2D g) {
		if (!everDrawn) {
			theGoalPosition = getGoalPosition();
			Vector<Double> tempVec = new Vector<Double>();
			tempVec.add(theGoalPosition);

			Vector<Double> returnVector = getHeightsForPositions(tempVec);
			theGoalHeight = returnVector.get(0);
			everDrawn = true;

		}

		//AffineTransform theScaleTransform = new AffineTransform();

		// Schrum: Was this just clearing the screen? Not necessary
		// Draw a rectangle
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, 1, 1);

//		theScaleTransform.scale(1.0d / scaleFactor, 1.0d / scaleFactor);
//		AffineTransform origTransform = g.getTransform();
//
//		AffineTransform x = g.getTransform();
//		x.concatenate(theScaleTransform);
//		g.setTransform(x);

		theHeights = getSampleHeights();

		double sizeEachComponent = 1.0 / (double) theHeights.size();

		g.setColor(Color.BLACK);

		double lastX = 0.0d;
		double lastY = 1.0 - UtilityShop.normalizeValue(1, getMinHeight(), getMaxHeight());
		for (int i = 1; i < theHeights.size(); i++) {
			double thisX = lastX + sizeEachComponent;
			double thisY = 1.0 - UtilityShop.normalizeValue(theHeights.get(i), getMinHeight(), getMaxHeight());

			
			Line2D thisLine = new Line2D.Double(SCALE_FACTOR * lastX, CEILING_SPACE + SCALE_FACTOR * lastY, 
					 						    SCALE_FACTOR * thisX, CEILING_SPACE + SCALE_FACTOR * thisY);

			lastX = thisX;
			lastY = thisY;

			g.draw(thisLine);
		}

//		g.setTransform(origTransform);

		g.setColor(Color.GREEN);

		// to bring things back into the window
		double minPosition = getMinValueForDim(0);
		double maxPosition = getMaxValueForDim(0);

		double transX = UtilityShop.normalizeValue(theGoalPosition, minPosition, maxPosition);
		// need to get the actual height ranges
		double transY = UtilityShop.normalizeValue(theGoalHeight, getMinHeight(), getMaxHeight());
		transY = (1.0 - transY);


		transX *= WIDTH;
		transY *= HEIGHT;
		
		// Schrum: made these magic numbers up through trial and error
		double rectWidth = 10; //.05 / 4;
		double rectHeight = 10; //0.1;
		Rectangle2D fillRect = new Rectangle2D.Double(transX - rectWidth / 2.0d, CEILING_SPACE + transY - rectHeight / 2.0d, 
													  rectWidth, rectHeight);
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
