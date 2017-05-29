package edu.utexas.cs.nn.tasks.rlglue.acrobot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import org.rlcommunity.environments.acrobot.AcrobotState;

import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;

public class AcrobotViewer {

	public static AcrobotViewer current = null;
	public static final int HEIGHT = 100;
	public static final int WIDTH = 100;
	public static final String TITLE = "Acrobot";
	public DrawingPanel panel;
	
	//variables for render
	private static final int joint1X = 50;
	private static final int joint1Y = 30;
	private static final int leg1length = 25;
	private static final int leg2length = leg1length;
	private static final int circleSize1 = 6;
	private static final int circleSize2 = 4;
	private static final int circleSize3 = 2;
	
	//Constructor
	public AcrobotViewer() {
		panel = new DrawingPanel(WIDTH, HEIGHT, TITLE);
		panel.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
		reset(null);
		current = this;
	}

	/**
	 * Resets the graphics for the view
	 */
	public void reset(AcrobotState state) {
		Graphics2D g = panel.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		if(state != null) render(g, state);
	}

	/**
	 * draws acrobot
	 * @param g
	 * 			Graphics2D instance to draw in
	 */
	public void render(Graphics2D g, AcrobotState state) {
		//AffineTransform saveAT = g.getTransform();
//		g.setColor(Color.WHITE);
//		g.fill(new Rectangle(1, 1));
//		g.scale(.01, .01);

		g.setColor(Color.green);
		int goalY = joint1Y - leg1length;
		g.drawLine(0, goalY, 100, goalY);
		g.setColor(Color.BLACK);

		int joint2X = (int) (leg1length * Math.sin(state.getTheta1()) + joint1X); //null pointer exception TODO
		int joint2Y = (int) (leg1length * Math.cos(state.getTheta1()) + joint1Y);
		g.drawLine(joint1X, joint1Y, joint2X, joint2Y);
		//Draw the first joint circle
		g.setColor(Color.BLUE);
		g.fill(new Ellipse2D.Float((float) joint1X - circleSize1 / 2, (float) joint1Y - circleSize1 / 2, circleSize1, circleSize1));

		int joint3X = (int) (leg2length * Math.cos(Math.PI / 2 - state.getTheta2() - state.getTheta1()) + joint2X);
		int joint3Y = (int) (leg2length * Math.sin(Math.PI / 2 - state.getTheta1() - state.getTheta2()) + joint2Y);
		g.setColor(Color.BLACK);
		g.drawLine(joint2X, joint2Y, joint3X, joint3Y);
		//Second circle
		g.setColor(Color.BLUE);
		g.fill(new Ellipse2D.Float((float) joint2X - circleSize2 / 2, (float) joint2Y - circleSize2 / 2, circleSize2, circleSize2));

		//                System.out.printf("(%d %d) --> (%d %d)\n",joint2X,joint2Y,joint3X,joint3Y);

		//Feet
		g.setColor(Color.CYAN);
		g.fill(new Ellipse2D.Float((float) joint3X - circleSize3 / 2, (float) joint3Y - circleSize3 / 2, circleSize3, circleSize3));
		//g.setTransform(saveAT);
	}

}
