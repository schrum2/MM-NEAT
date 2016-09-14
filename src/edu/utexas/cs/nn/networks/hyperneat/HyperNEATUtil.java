package edu.utexas.cs.nn.networks.hyperneat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
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
	public static class VisualNode implements Comparable<VisualNode> {
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
		public int compareTo(VisualNode o) {
			if(o.dead && this.dead) return 0; // dead neurons are alike
			else if(o.dead) return 1; // this is not dead
			else if(this.dead) return -1; // o is not dead
			else return (int) Math.signum(this.activation - o.activation);
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

	//size of grid in substrate drawing. 
	public final static int SUBS_GRID_SIZE = Parameters.parameters.integerParameter("substrateGridSize");
	
	private static int weightGridXSize;
	private static int weightGridYSize;
	private static List<DrawingPanel> substratePanels = null;
	private static ArrayList<DrawingPanel> weightPanels = null;//TODO is it a problem that I had to make this explicitly an array list instead of a list?
	private static HyperNEATTask hyperNEATTask;
	private static TWEANNGenotype tg;
	private static List<Substrate> substrates;
	private static List<Pair<String, String>> connections;
	private static List<TWEANNGenotype.NodeGene> nodes;

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
	private static void drawCoord(DrawingPanel p, Substrate s, ArrayList<Node> nodes, int nodeIndex) {
		p.getGraphics().setBackground(Color.gray);
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
			for(int i = activations.size() - 1; i >= 0 && !activations.get(i).dead; i--) {
				VisualNode vNode = activations.get(i);
				if(!vNode.dead) {
					vNode.setColor(new Color(0, i / scale, 0));
				}
			}
		}
		if(biggest) {//only shows biggest neuron 
			Collections.sort(activations);
			int currentIndex = activations.size() - 1;
			while(activations.get(activations.size() - 1).activation == activations.get(currentIndex).activation) {
				activations.get(currentIndex).setColor(Color.MAGENTA);
				currentIndex--;
			}
		}  




		for(VisualNode vn : activations) {
			vn.drawNode(p);
		}
	}

	public static Color regularVisualization(double activation) { 
		activation = Math.max(-1, Math.min(activation, 1.0));// For unusual activation functions that go outside of the [-1,1] range
		return new Color(activation > 0 ? (int)(activation*255) : 0, 0, activation < 0 ? (int)(-activation*255) : 0);

	}


	public static ArrayList<DrawingPanel> drawWeight(TWEANNGenotype genotype) {
		tg = (TWEANNGenotype)genotype.copy();
		connections = hyperNEATTask.getSubstrateConnectivity();
		nodes = tg.nodes;
		weightPanels = new ArrayList<DrawingPanel>();
		int weightPlacing = 0;
		for(int i = 0; i < connections.size(); i++) {
			String sub1 = connections.get(i).t1;
			String sub2 = connections.get(i).t2;
			Substrate s1 = getSubstrate(sub1);
			Substrate s2 = getSubstrate(sub2);
			assert s1 != null && s2 != null;
			int s1StartingIndex = getSubstrateNodeStartingIndex(s1);
			int s2StartingIndex = getSubstrateNodeStartingIndex(s2);
			weightPanels.add(drawWeight(s1, s2, s1StartingIndex, s2StartingIndex));
			weightPanels.get(i).setLocation(weightPlacing, 0);
			weightPlacing += weightPanels.get(i).getFrame().getWidth();
		}
		return weightPanels;

	}
	//find a neuron from this position in substrate and another substrate and get link between them

	private static int getSubstrateNodeStartingIndex(Substrate sub) {//could there be a more useful place to put this?
		int nodeIndex = 0;
		for(int i = 0; i < substrates.size(); i++) {
			if(substrates.get(i).getName().equals(sub.getName())) break;
			nodeIndex += substrates.get(i).size.t1 * substrates.get(i).size.t2;
		}
		return nodeIndex;
	}

	private static Substrate getSubstrate(String name) {
		Substrate s = null;
		for(int i = 0; i < substrates.size(); i++) {
			if(substrates.get(i).name.equals(name)) { 
				s =  substrates.get(i);
			}
		}
		return s;
	}

	private static DrawingPanel drawWeight(Substrate s1, Substrate s2, int s1Index, int s2Index) {
		//create new panel here
		int xCoord = 0;
		int yCoord = 0;
		weightGridXSize = SUBS_GRID_SIZE / s2.size.t1;//TODO not sure how to set up this grid scaling
		weightGridYSize = SUBS_GRID_SIZE / s2.size.t2;//TODO ""
		DrawingPanel wPanel = new DrawingPanel(SUBS_GRID_SIZE * s1.getSize().t1, SUBS_GRID_SIZE * s1.getSize().t2, s1.getName() + " and " + s2.getName() + " connection weights");
		wPanel.getGraphics().setBackground(Color.gray);
		for(int i = s1Index; i < (s1Index + s1.size.t1 + s1.size.t2); i++) {

			drawNodeWeight(wPanel, nodes.get(i), xCoord, yCoord, s2Index, s2Index + s2.size.t1 + s2.size.t2);
			xCoord += SUBS_GRID_SIZE;
			yCoord += SUBS_GRID_SIZE;
		}
		return wPanel;
	}


	private  static void  drawNodeWeight(DrawingPanel dPanel, TWEANNGenotype.NodeGene startingNode, int xCoord, int yCoord, int startingNodeIndex, int endingNodeIndex) {
		//get all connections of node to next substrate nodes and get all those links and then link weights
		//and then paint color to drawing panel corresponding to link weight
		//use same color scheme as substrate visualizer except dead links are gray
		for(int j = startingNodeIndex; j < endingNodeIndex; j++) {
			Color c = Color.gray;
			TWEANNGenotype.LinkGene link = tg.getLinkBetween(startingNode.innovation, nodes.get(j).innovation);
			double weight = link.weight;
			if(! (weight < 0.000000001)) {
				//Color = .setColor(new Color(0, i / scale, 0) //TODO I dont know how to do color computation..?
			}
			dPanel.getGraphics().setColor(c);
			dPanel.getGraphics().fillRect(xCoord, yCoord, weightGridXSize, weightGridYSize);
		}
	}

}
