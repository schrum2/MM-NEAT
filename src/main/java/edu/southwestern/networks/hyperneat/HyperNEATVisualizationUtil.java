package edu.southwestern.networks.hyperneat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.FullLinkGene;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.TWEANN.Node;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.CombinatoricUtilities;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.graphics.DrawingPanel;

public class HyperNEATVisualizationUtil {
	//private List<DrawingPanel> substratePanels;
	//private List<DrawingPanel> weightPanels;
	public static final int SUBS_GRID_SIZE = Parameters.parameters.integerParameter("substrateGridSize");
	public static final int WEIGHT_GRID_SIZE = Parameters.parameters.integerParameter("substrateWeightSize");
	public final static int LINK_WINDOW_SPACING = 5;

	//may change later to just use collections.sort with a special comparator instead of creating new data type
	/**
	 * Public inner class that creates a new VisualNode
	 * 
	 * @author Lauren Gillespie
	 *
	 */
	public static class VisualNode implements Comparable<VisualNode> {
		public double activation;
		public int xCoord, yCoord;
		public Color c;
		public boolean dead;

		/**
		 * Default Constructor for a new VisualNode
		 * 
		 * @param activation Current activation to be stored in the VisualNode
		 * @param xCoord X-Coordinate of the new VisualNode
		 * @param yCoord Y-Coordinate of the new VisualNode
		 */
		public VisualNode(double activation, int xCoord, int yCoord) {
			this(activation, xCoord, yCoord, Color.white);
		}

		/**
		 * Constructor for a new VisualNode;
		 * different from the Default Constructor
		 * in that the Color is set by the user
		 * 
		 * @param activation Current activation to be stored in the VisualNode
		 * @param xCoord X-Coordinate of the new VisualNode
		 * @param yCoord Y-Coordinate of the new VisualNode
		 * @param c Color of the new Visual Node
		 */
		public VisualNode(double activation, int xCoord, int yCoord, Color c) {
			this(activation, xCoord, yCoord, c, false);
		}

		/**
		 * Constructor for a new VisualNode;
		 * different from the Default Constructor
		 * in that the Color is set by the user
		 * and that the user sets whether or not the VisualNode is dead
		 * 
		 * @param activation Current activation to be stored in the VisualNode
		 * @param xCoord X-Coordinate of the new VisualNode
		 * @param yCoord Y-Coordinate of the new VisualNode
		 * @param c Color of the new Visual Node
		 * @param dead Boolean that sets the VisualNode to be dead if true, not dead if false
		 */
		public VisualNode(double activation, int xCoord, int yCoord, Color c, boolean dead) {
			this.activation = activation;
			this.xCoord = xCoord*SUBS_GRID_SIZE;
			this.yCoord = yCoord*SUBS_GRID_SIZE;
			this.c = c;
			this.dead = dead;
		}

		/**
		 * Sets the Color of the VisualNode
		 * 
		 * @param c New Color of the VisualNode
		 */
		public void setColor(Color c) {
			this.c = c;
		}

		/**
		 * Compares this VisualNode to a specified other VisualNode
		 * and returns an Integer value that describes the relationship between
		 * the two.
		 * 
		 * @param o Another VisualNode to be compared against
		 * @return 0 if the VisualNodes are alike,
		 * 		1 if the other VisualNode is dead and this one isn't,
		 * 		-1 if this VisualNode is dead and the other one isn't,
		 * 		or the signum of this VisualNode's activation minus the other VisualNode's activation.
		 */
		@Override
		public int compareTo(VisualNode o) {
			if(o.dead && this.dead) return 0; // dead neurons are alike
			else if(o.dead) return 1; // this is not dead
			else if(this.dead) return -1; // o is not dead
			else return (int) Math.signum(this.activation - o.activation);
		} 

		/**
		 * Draws this VisualNode on a Drawing Panel at the VisualNode's
		 * X-Coordinate and Y-Coordinate with its set Color
		 * 
		 * @param p DrawingPanel where the VisualNode should be drawn
		 */
		public void drawNode(DrawingPanel p) {
			p.getGraphics().setColor(c);
			p.getGraphics().fillRect(xCoord, yCoord, SUBS_GRID_SIZE, SUBS_GRID_SIZE);
		}

		/**
		 * Returns a String with the VisualNode's activation,
		 * X-Coordinate, Y-Coordinate, and Color.
		 * 
		 * @return s String with the VisualNode's data
		 */
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


	/**
	 * Turn off all HyperNEAT visualization.
	 * Used when hybrID switches over.
	 */
	//	public static void clearHyperNEATVisualizations() {
	//		if(substratePanels != null) {
	//			for(DrawingPanel dp: substratePanels) {
	//				dp.dispose();
	//			}
	//		}
	//		if(weightPanels != null) {
	//			for(DrawingPanel dp: weightPanels) {
	//				dp.dispose();
	//			}
	//		}
	//	}

	/**
	 * Draws the Substrates of the HyperNEAT
	 * 
	 * @param nodes ArrayList<Nodes> of Nodes to be drawn
	 * @param substrate substrate information for a network
	 * @return List<DrawingPanel> of DrawingPanels used to draw the Substrates
	 */
	public static void drawSubstrates(ArrayList<Node> nodes, List<Substrate> substrates) {
		if(TWEANN.subsPanel == null) {
			TWEANN.subsPanel = new ArrayList<DrawingPanel>();
			int nodeIndexStart = 0;
			int substratePlacing = 0;
			for(int i = 0; i < substrates.size(); i++) {
				Substrate substrate = substrates.get(i);
				TWEANN.subsPanel.add(drawSubstrate(substrate, nodes, nodeIndexStart));
				TWEANN.subsPanel.get(i).setLocation(substratePlacing, 0);
				nodeIndexStart += substrate.getSize().t1 * substrate.getSize().t2;
				substratePlacing += TWEANN.subsPanel.get(i).getFrame().getWidth();
			}
		} else {
			int nodeIndexStart = 0;
			for(int i = 0; i < substrates.size(); i++) {
				Substrate s = substrates.get(i);
				drawSubstrate(TWEANN.subsPanel.get(i), s, nodes, nodeIndexStart);
				nodeIndexStart += s.getSize().t1 * s.getSize().t2;
			}
		}
	}

	/**
	 * Draws substrate as a grid with squares representing each substrate coordinate
	 * @param s substrate
	 * @param nodes list of substrate nodes
	 * @param nodeIndexStart where the relevant nodes start
	 * @return drawing panel containing substrate drawing
	 */
	public static DrawingPanel drawSubstrate(Substrate s, ArrayList<Node> nodes, int nodeIndexStart) { 
		DrawingPanel p = new DrawingPanel(s.getSize().t1 * SUBS_GRID_SIZE, s.getSize().t2 * SUBS_GRID_SIZE, s.getName());
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
		drawGrid(dp, s.getSize());
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
	private static void drawCoord(DrawingPanel p, Substrate substrate, ArrayList<Node> nodes, int nodeIndex) {
		p.getGraphics().setBackground(Color.gray);
		boolean sort = Parameters.parameters.booleanParameter("sortOutputActivations") && substrate.getStype() == Substrate.OUTPUT_SUBSTRATE;
		boolean biggest = Parameters.parameters.booleanParameter("showHighestActivatedOutput") && substrate.getStype() == Substrate.OUTPUT_SUBSTRATE;
		ArrayList<VisualNode> activations = new ArrayList<VisualNode>(); 
		for(int j = 0; j < substrate.getSize().t2; j++) {
			for(int i = 0; i < substrate.getSize().t1; i++) {
				Node node = nodes.get(nodeIndex++);
				Color c = Color.gray;
				double activation = node.output();
				if(node.ntype == TWEANN.Node.NTYPE_OUTPUT || !node.outputs.isEmpty()) {
					if(!substrate.isNeuronDead(i, j)) c = regularVisualization(activation);
				}
				activations.add(new VisualNode(activation, i, j, c, substrate.isNeuronDead(i, j)));//dead neurons
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
			while(activations.get(activations.size() - 1).activation == activations.get(currentIndex).activation && currentIndex > 0) {
				activations.get(currentIndex).setColor(Color.MAGENTA);
				currentIndex--;
			}
			//			activations.get(currentIndex).setColor(Color.MAGENTA);
		}  

		for(VisualNode vn : activations) {
			vn.drawNode(p);
		}
	}

	/**
	 * draws the weights of the connections between s1 and s2	
	 * @param s1 substrate 1
	 * @param s2 substrate 2
	 * @param s1Index starting index of nodes in s1
	 * @param s2Index starting index of nodes in s2
	 * @return drawingPanel with drawn weights
	 */
	static DrawingPanel drawWeight(Substrate s1, Substrate s2, int s1Index, int s2Index, boolean showMultipleModules, List<TWEANNGenotype.NodeGene> nodes, TWEANNGenotype substrateGenotype) {
		//create new panel here
		int xCoord = 0;
		int yCoord = 0;
		int nodeVisWidth = WEIGHT_GRID_SIZE* s1.getSize().t1;
		int nodeVisHeight = WEIGHT_GRID_SIZE * s1.getSize().t2;
		int panelWidth = s2.getSize().t1 * nodeVisWidth  + s2.getSize().t1 - 1;
		int panelHeight  = s2.getSize().t2 * nodeVisHeight  + s2.getSize().t2 - 1;

		//instantiates panel
		DrawingPanel wPanel = new DrawingPanel(panelWidth, panelHeight, (showMultipleModules ? "M" : "W") + ":" + s1.getName() + "->" + s2.getName());
		wPanel.getGraphics().setBackground(Color.white);
		//for every node in s1, draws all links from it to s2
		for(int i = s2Index; i < (s2Index + (s2.getSize().t1 * s2.getSize().t2)); i++) {//goes through every node in target substrate
			//drawBorder(wPanel, xCoord, yCoord, nodeVisWidth + 2, nodeVisHeight + 2);
			drawNodeWeight(wPanel, nodes, substrateGenotype, nodes.get(i), xCoord , yCoord , s1Index, s1Index + (s1.getSize().t1 * s1.getSize().t2), nodeVisWidth, nodeVisHeight, showMultipleModules);
			xCoord += nodeVisWidth + 1;
			if(xCoord >= panelWidth) {
				xCoord = 0;
				yCoord += nodeVisHeight + 1;
			}
		}
		return wPanel;
	}

	/**
	 * get all connections of node to next substrate nodes and get all those links and then link weights
		and then paint color to drawing panel corresponding to link weight
		use same color scheme as substrate visualizer except dead links are gray

	 * @param dPanel drawing panel
	 * @param targetNode node to draw links from
	 * @param xCoord x coordinate to start from in drawing panel
	 * @param yCoord y coordinate to start from in drawing panel
	 * @param startingNodeIndex index of first node in second substrate
	 * @param endingNodeIndex ending index of first node in second substrate
	 * @param nodeWidth width of node 1
	 * @param nodeHeight height of node 1
	 * @param if true visualization will show module source. if false visualization will show default
	 */
	private  static void  drawNodeWeight(DrawingPanel dPanel, List<TWEANNGenotype.NodeGene> nodes, TWEANNGenotype substrateGenotype, TWEANNGenotype.NodeGene targetNode, int xCoord, int yCoord, int startingNodeIndex, int endingNodeIndex, int nodeWidth, int nodeHeight, boolean showModuleSource) {
		int xLeftEdge = xCoord;
		for(int j = startingNodeIndex; j < endingNodeIndex; j++) {//goes through every node in second substrate
			Color c = Color.gray;
			TWEANNGenotype.NodeGene node = nodes.get(j);
			TWEANNGenotype.FullLinkGene link = (FullLinkGene) substrateGenotype.getLinkBetween(node.innovation, targetNode.innovation);
			if(link != null) {
				if(showModuleSource) {
					c = CombinatoricUtilities.colorFromInt(link.getModuleSource());
				} else {					
					c = regularVisualization(ActivationFunctions.activation(ActivationFunctions.FTYPE_TANH, link.weight));
				}
			}
			dPanel.getGraphics().setColor(c);
			dPanel.getGraphics().fillRect(xCoord, yCoord, WEIGHT_GRID_SIZE, WEIGHT_GRID_SIZE);
			xCoord += WEIGHT_GRID_SIZE;
			if(xCoord >= xLeftEdge + nodeWidth ) {
				xCoord = xLeftEdge;
				yCoord += WEIGHT_GRID_SIZE;
			}
		}
	}

	/**
	 * Compresses values from -1 to 1 and then returns a color where -1 is blue, 0 is black and 1 is red.
	 * 
	 * @param activation value to convert
	 * @return color of value
	 */
	public static Color regularVisualization(double activation) { 
		// Temporary debugging: Fiddle with activation range to allow visualization outside range [-1,1]
		// activation /= 10;
		activation = Math.max(-1, Math.min(activation, 1.0));// For unusual activation functions that go outside of the [-1,1] range
		return new Color(activation > 0 ? (int)(activation*255) : 0, 0, activation < 0 ? (int)(-activation*255) : 0);
	}

	/**
	 * Draws the weights of the links between nodes in substrate layers to a series of drawing panels, with
	 * one panel per connection of substrates
	 * @param hngt genotype of network to be drawn
	 * @param hnt hyperNEAT task
	 * @return the weight panels
	 */
	public static List<DrawingPanel> drawWeight(HyperNEATCPPNGenotype hngt, HyperNEATTask hnt, int numCPPNModules) {
		//gets all relevant information needed to draw link weights
		TWEANNGenotype substrateGenotype = hngt.getSubstrateGenotype(hnt);
		List<TWEANNGenotype.NodeGene> nodes = substrateGenotype.nodes;
		List<Substrate> substrates = hngt.getSubstrateInformation(hnt);
		List<SubstrateConnectivity> connections = hngt.getSubstrateConnectivity(hnt);

		//disposes of weight panels if already instantiated to clean up old panels
//		if(weightPanels != null) {
//			for(int i =0; i < weightPanels.size(); i++) {
//				weightPanels.get(i).dispose();
//			}
//		}
		//instantiates panel array
		ArrayList<DrawingPanel> weightPanels = new ArrayList<DrawingPanel>();

		//used to instantiate drawing panels not on top of one another
		int weightPanelsWidth = 0;
		int weightPanelsHeight = 0;

		//creates each a panel for each connection between substrates
		for(int i = 0; i < connections.size(); i++) {
			String sub1 = connections.get(i).sourceSubstrateName;
			String sub2 = connections.get(i).targetSubstrateName;
			Substrate s1 = getSubstrate(sub1, substrates);
			Substrate s2 = getSubstrate(sub2, substrates);
			//this block of code gets the substrates and their nodes and indices
			assert s1 != null && s2 != null;
			int s1StartingIndex = getSubstrateNodeStartingIndex(s1, substrates);
			int s2StartingIndex = getSubstrateNodeStartingIndex(s2, substrates);
			//actually creates panel with weights
			weightPanels.add(drawWeight(s1, s2, s1StartingIndex, s2StartingIndex, false, nodes, substrateGenotype));
			if(numCPPNModules > 1) {
				weightPanels.add(drawWeight(s1, s2, s1StartingIndex, s2StartingIndex, true, nodes, substrateGenotype));
			}
			//sets locations of panels so they're not right on top of one another
			weightPanels.get(i).setLocation(weightPanelsWidth, weightPanelsHeight);
			weightPanelsWidth += weightPanels.get(i).getFrame().getWidth() + LINK_WINDOW_SPACING;
		}
		return weightPanels;

	}

	/**
	 * Gets the index of the first node in substrate
	 * @param sub substrate 
	 * @return index of first node in substrate
	 */
	private static int getSubstrateNodeStartingIndex(Substrate sub, List<Substrate> substrates) {
		int nodeIndex = 0;
		for(int i = 0; i < substrates.size(); i++) {
			if(substrates.get(i).getName().equals(sub.getName())){ break;}
			nodeIndex += substrates.get(i).getSize().t1 * substrates.get(i).getSize().t2;
		}
		return nodeIndex;
	}

	/**
	 * gets the substrate from the list of substrates using the name
	 * @param name name of given substrate
	 * @return substrate with given name
	 */
	private static Substrate getSubstrate(String name, List<Substrate> substrates) {
		Substrate s = null;
		for(int i = 0; i < substrates.size(); i++) {
			if(substrates.get(i).getName().equals(name)) { 
				s =  substrates.get(i);
			}
		}
		return s;
	}
}
