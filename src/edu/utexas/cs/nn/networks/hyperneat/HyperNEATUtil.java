package edu.utexas.cs.nn.networks.hyperneat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 * Util class containing methods used by hyperNEAT and its tasks
 * 
 * @author Lauren Gillespie
 *
 */
public class HyperNEATUtil {

	//may change later to just use collections.sort with a special comparator instead of creating new data type
	@SuppressWarnings("rawtypes")
	public static class VisualNode implements Comparable{
		public double activation;
		public int xCoord, yCoord;
		public Color c;
		public boolean dead;

		public VisualNode(double activation, int xCoord, int yCoord) {
			this(activation, xCoord, yCoord, Color.white);
		}

		public VisualNode(double activation, int xCoord, int yCoord, Color c) {
			this(activation, xCoord, yCoord, c, false);
		}

		public VisualNode(double activation, int xCoord, int yCoord, Color c, boolean dead) {
			this.activation = activation;
			this.xCoord = xCoord*SUBS_GRID_SIZE;
			this.yCoord = yCoord*SUBS_GRID_SIZE;
			this.c = c;
			this.dead = dead;
		}

		public void setColor(Color c) {
			this.c = c;
		}
		@Override
		public int compareTo(Object o) {
			if(o instanceof VisualNode) {
				if(((VisualNode) o).activation > this.activation) return 1;
				else if(((VisualNode) o).activation == this.activation) return 0;
				else return -1;
			}
			throw new IllegalArgumentException("o is not of type VisualNode, can't compare!");
		} 
		public void drawNode(DrawingPanel p) {
			p.getGraphics().setColor(c);
			p.getGraphics().fillRect(xCoord, yCoord, SUBS_GRID_SIZE, SUBS_GRID_SIZE);
		}

		@Override
		public String toString() {
			String s = "Visual Node: (";
			s += "activation: " + activation;
			s+= ", X-coord: " + xCoord;
			s += " Y-coord: " + yCoord;
			s += ", Color: " + c;
			s += ") + \n";
			return s;
		}
	}

	//size of grid in substrate drawing. Can be changed/turned into a param if need be
	public final static int SUBS_GRID_SIZE = Parameters.parameters.integerParameter("substrateGridSize");

	private static List<DrawingPanel> substratePanels = null;

	private static HyperNEATTask hyperNEATTask;

	private static List<Substrate> substrates;

	public static void resetSubstrates() {
		if(hyperNEATTask != null) {
			substrates = hyperNEATTask.getSubstrateInformation();
		}
	}

	public static List<DrawingPanel> drawSubstrates(ArrayList<Node> nodes) {
		if(substratePanels == null) {
			hyperNEATTask = (HyperNEATTask) MMNEAT.task;
			substrates = hyperNEATTask.getSubstrateInformation();
			substratePanels = new ArrayList<DrawingPanel>();
			int nodeIndexStart = 0;
			int substratePlacing = 0;
			for(int i = 0; i < substrates.size(); i++) {
				Substrate s = substrates.get(i);
				substratePanels.add(drawSubstrate(s, nodes, nodeIndexStart));
				substratePanels.get(i).setLocation(substratePlacing, 0);
				nodeIndexStart += s.size.t1 * s.size.t2;
				substratePlacing += substratePanels.get(i).getFrame().getWidth();
			}
		} else {
			int nodeIndexStart = 0;
			for(int i = 0; i < substrates.size(); i++) {
				Substrate s = substrates.get(i);
				drawSubstrate(substratePanels.get(i), s, nodes, nodeIndexStart);
				nodeIndexStart += s.size.t1 * s.size.t2;
			}
		}
		return substratePanels;
	}

	/**
	 * Draws substrate as a grid with squares representing each substrate coordinate
	 * @param s substrate
	 * @param nodes list of substrate nodes
	 * @param nodeIndexStart where the relevant nodes start
	 * @return drawing panel containing substrate drawing
	 */
	public static DrawingPanel drawSubstrate(Substrate s, ArrayList<Node> nodes, int nodeIndexStart) { 
		DrawingPanel p = new DrawingPanel(s.size.t1 * SUBS_GRID_SIZE, s.size.t2 * SUBS_GRID_SIZE, s.name);
		return drawSubstrate(p, s, nodes, nodeIndexStart); // updates existing panel
	}

	/**
	 * Overloaded. Draws substrate as a grid with squares representing each substrate coordinate
	 *  
	 * @param dp drawing panel which substrate is going to be drawn onto
	 * @param s substrate in question
	 * @param nodes list of substrate nodes 
	 * @param nodeIndexStart where the relevant nodes start
	 * @return drawing panel containing drawing of substrate
	 */
	public static DrawingPanel drawSubstrate(DrawingPanel dp, Substrate s, ArrayList<Node> nodes, int nodeIndexStart) { 
		drawCoord(dp, s, nodes, nodeIndexStart);
		drawGrid(dp, s.size);
		return dp;
	}

	/**
	 * Draws grid around squares so they are more easily distinguishable
	 * 'Not used currently b/c slows down computation time
	 * @param p drawingPanel
	 * @param size size of substrate
	 */
	private static void drawGrid(DrawingPanel p, Pair<Integer, Integer> size) {
		// Loop through columns and rows to draw black lines
		p.getGraphics().setBackground(Color.gray);
		p.getGraphics().setColor(Color.black);
		for(int i = 0; i <= size.t1; i++) {
			for(int j = 0; j <= size.t2; j++) {
				p.getGraphics().drawRect(i * SUBS_GRID_SIZE, j * SUBS_GRID_SIZE, (i + 1) * SUBS_GRID_SIZE, SUBS_GRID_SIZE * (j + 1));
			}
		}	
	}

	/**
	 * Draws squares representing a coordinate location from substrate
	 * @param p drawing panel
	 * @param size size of substrate
	 * @param c color of square
	 */
	@SuppressWarnings("unchecked")
	private static void drawCoord(DrawingPanel p, Substrate s, ArrayList<Node> nodes, int nodeIndex) {
		p.getGraphics().setBackground(Color.gray);
		//		System.out.println("-----------------Substrate " + s.name + "---------------------");
		boolean sort = Parameters.parameters.booleanParameter("sortOutputActivations") && s.stype == Substrate.OUTPUT_SUBSTRATE;
		boolean biggest = Parameters.parameters.booleanParameter("showHighestActivatedOutput") && s.stype == Substrate.OUTPUT_SUBSTRATE;
		ArrayList<VisualNode> activations = new ArrayList<VisualNode>(); 
		for(int j = 0; j < s.size.t2; j++) {
			for(int i = 0; i < s.size.t1; i++) {
				Node node = nodes.get(nodeIndex++);
				Color c = Color.gray;
				double activation = node.output();
				if(node.ntype == TWEANN.Node.NTYPE_OUTPUT || !node.outputs.isEmpty()) {
					if(!s.isNeuronDead(i, j)) c = HyperNEATUtil.regularVisualization(activation);

				}
				activations.add(new VisualNode(activation, i, j, c, s.isNeuronDead(i, j)));//dead neurons
			}
		}
		if(sort) {
			Collections.sort(activations);
			float scale = activations.size();
			for(int i = activations.size() - 1; i >= 0; i--) {
				VisualNode vNode = activations.get(i);
				if(!vNode.dead) {
					vNode.setColor(new Color(0, i / scale, 0));
				}
			}
		} else if(biggest) {//only shows biggest neuron 
			Collections.sort(activations);
			VisualNode biggestAct = activations.get(activations.size() - 1);
			biggestAct.setColor(Color.green);

		}  




		for(VisualNode vn : activations) {
			vn.drawNode(p);
		}
	}

	public static Color regularVisualization(double activation) { 
		activation = Math.max(-1, Math.min(activation, 1.0));// For unusual activation functions that go outside of the [-1,1] range
		return new Color(activation > 0 ? (int)(activation*255) : 0, 0, activation < 0 ? (int)(-activation*255) : 0);

	}
}
