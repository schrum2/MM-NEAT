/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.data;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.CombinatoricUtilities;
import java.awt.Color;
import java.util.HashMap;

/**
 *
 * @author Jacob
 */
public class ScentPath {

	public static final double SCENT_DROP = 0.5;
	public static ScentPath scents = new ScentPath();
	public static ScentPath[] modeScents = null;
	private final HashMap<Integer, Double> scent;
	private final double scentDecay;
	private final boolean personalScent;
	private float[] colors;

	public static void resetAll() {
		if (modeScents != null)
			resetAll(modeScents.length);
	}

	public static void resetAll(int numModes) {
		modeScents = new ScentPath[numModes];
		for (int i = 0; i < ScentPath.modeScents.length; i++) {
			modeScents[i] = new ScentPath(0.99, true, CombinatoricUtilities.mapTuple(i + 1));
		}
	}

	public ScentPath() {
		this(Parameters.parameters.doubleParameter("scentDecay"),
				Parameters.parameters.booleanParameter("personalScent"), new float[] { 0, 1, 0 });
	}

	public ScentPath(double scentDecay, boolean personalScent, float[] colors) {
		scent = new HashMap<Integer, Double>();
		this.scentDecay = scentDecay;
		this.personalScent = personalScent;
		this.colors = colors;
	}

	public void visit(GameFacade g, int node) {
		visit(g, node, SCENT_DROP);
	}

	public void visit(GameFacade g, int node, double amount) {
		if (personalScent) {
			double newAmount = amount + getScent(node);
			// System.out.println(node +":" + Arrays.toString(colors) + ":"+
			// newAmount);
			scent.put(node, newAmount);
			fade();
			if (CommonConstants.watch) {
				draw(g);
			}
		}
	}

	public void fade() {
		for (Integer node : scent.keySet()) {
			double strength = scent.get(node) * scentDecay;
			scent.put(node, strength);
		}
	}

	public void draw(GameFacade g) {
		for (Integer node : scent.keySet()) {
			double strength = scent.get(node);
			g.addPoints(new Color((int) (colors[0] * Math.min(strength, 1) * 255),
					(int) (colors[1] * Math.min(strength, 1) * 255), (int) (colors[2] * Math.min(strength, 1) * 255)),
					new int[] { node });
		}
	}

	public void reset() {
		scent.clear();
	}

	public double getScent(int node) {
		if (!scent.containsKey(node)) {
			return 0;
		} else {
			return scent.get(node);
		}
	}
}
