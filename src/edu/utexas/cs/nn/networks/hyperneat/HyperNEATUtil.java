package edu.utexas.cs.nn.networks.hyperneat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;

/**
 * Util class containing methods used by hyperNEAT and its tasks
 * 
 * @author Lauren Gillespie
 *
 */
public class HyperNEATUtil {

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

	//size of grid in substrate drawing. 
	public final static int LINK_WINDOW_SPACING = 5;
	public final static int SUBS_GRID_SIZE = Parameters.parameters.integerParameter("substrateGridSize");
	public final static int WEIGHT_GRID_SIZE = Parameters.parameters.integerParameter("substrateWeightSize");
	private static List<DrawingPanel> substratePanels = null;
	private static List<DrawingPanel> weightPanels = null;
	private static HyperNEATTask hyperNEATTask;
	private static TWEANNGenotype tg;
	private static List<Substrate> substrates;
	private static List<Triple<String, String, Boolean>> connections;
	private static List<TWEANNGenotype.NodeGene> nodes;

	/**
	 * Turn off all HyperNEAT visualization.
	 * Used when hybrID switches over.
	 */
	public static void clearHyperNEATVisualizations() {
		if(substratePanels != null) {
			for(DrawingPanel dp: substratePanels) {
				dp.dispose();
			}
		}
		if(weightPanels != null) {
			for(DrawingPanel dp: weightPanels) {
				dp.dispose();
			}
		}
	}
	
	/**
	 * Resets the Substrates of the HyperNEAT
	 */
	public static void resetSubstrates() {
		if(hyperNEATTask != null) {
			substrates = hyperNEATTask.getSubstrateInformation();
		}
	}

	/**
	 * Draws the Substrates of the HyperNEAT
	 * 
	 * @param nodes ArrayList<Nodes> of Nodes to be drawn
	 * @return List<DrawingPanel> of DrawingPanels used to draw the Substrates
	 */
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
				nodeIndexStart += s.getSize().t1 * s.getSize().t2;
				substratePlacing += substratePanels.get(i).getFrame().getWidth();
			}
		} else {
			int nodeIndexStart = 0;
			for(int i = 0; i < substrates.size(); i++) {
				Substrate s = substrates.get(i);
				drawSubstrate(substratePanels.get(i), s, nodes, nodeIndexStart);
				nodeIndexStart += s.getSize().t1 * s.getSize().t2;
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
	private static void drawCoord(DrawingPanel p, Substrate s, ArrayList<Node> nodes, int nodeIndex) {
		p.getGraphics().setBackground(Color.gray);
		boolean sort = Parameters.parameters.booleanParameter("sortOutputActivations") && s.getStype() == Substrate.OUTPUT_SUBSTRATE;
		boolean biggest = Parameters.parameters.booleanParameter("showHighestActivatedOutput") && s.getStype() == Substrate.OUTPUT_SUBSTRATE;
		ArrayList<VisualNode> activations = new ArrayList<VisualNode>(); 
		for(int j = 0; j < s.getSize().t2; j++) {
			for(int i = 0; i < s.getSize().t1; i++) {
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
	 * Converts defined MMNEAT task into a HyperNEATTask and returns it
	 * 
	 * @return HyperNEATTask
	 */
	public static HyperNEATTask getHyperNEATTask() {
		HyperNEATTask hnt = (HyperNEATTask) MMNEAT.task;
		return hnt;
	}

	/**
	 * Compresses values from -1 to 1 and then returns a color where -1 is blue, 0 is black and 1 is red.
	 * 
	 * @param activation value to convert
	 * @return color of value
	 */
	public static Color regularVisualization(double activation) { 
		activation = Math.max(-1, Math.min(activation, 1.0));// For unusual activation functions that go outside of the [-1,1] range
		return new Color(activation > 0 ? (int)(activation*255) : 0, 0, activation < 0 ? (int)(-activation*255) : 0);

	}


	/**
	 * Draws the weights of the links between nodes in substrate layers to a series of drawing panels, with
	 * one panel per connection of substrates
	 * @param genotype genotype of network to be drawn
	 * @param hnt hyperNEAT task
	 * @return the weight panels
	 */
	public static List<DrawingPanel> drawWeight(TWEANNGenotype genotype, HyperNEATTask hnt) {

		//gets all relevant information needed to draw link weights
		tg = (TWEANNGenotype)genotype.copy();
		connections = hnt.getSubstrateConnectivity();
		nodes = tg.nodes;
		//disposes of weight panels if already instantiated to clean up old panels
		if(weightPanels != null) {
			for(int i =0; i < weightPanels.size(); i++) {
				weightPanels.get(i).dispose();
			}
		}
		//instantiates panel array
		weightPanels = new ArrayList<DrawingPanel>();
		substrates = hnt.getSubstrateInformation();

		//used to instantiate drawing panels not on top of one another
		int weightPanelsWidth = 0;
		int weightPanelsHeight = 0;

		//creates each a panel for each connection between substrates
		for(int i = 0; i < connections.size(); i++) {
			String sub1 = connections.get(i).t1;
			String sub2 = connections.get(i).t2;
			Substrate s1 = getSubstrate(sub1);
			Substrate s2 = getSubstrate(sub2);
			//this block of code gets the substrates and their nodes and indices
			assert s1 != null && s2 != null;
			int s1StartingIndex = getSubstrateNodeStartingIndex(s1);
			int s2StartingIndex = getSubstrateNodeStartingIndex(s2);
			//actually creates panel with weights
			weightPanels.add(drawWeight(s1, s2, s1StartingIndex, s2StartingIndex));
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
	private static int getSubstrateNodeStartingIndex(Substrate sub) {
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
	private static Substrate getSubstrate(String name) {
		Substrate s = null;
		for(int i = 0; i < substrates.size(); i++) {
			if(substrates.get(i).getName().equals(name)) { 
				s =  substrates.get(i);
			}
		}
		return s;
	}

	/**
	 * draws the weights of the connections between s1 and s2	
	 * @param s1 substrate 1
	 * @param s2 substrate 2
	 * @param s1Index starting index of nodes in s1
	 * @param s2Index starting index of nodes in s2
	 * @return drawingPanel with drawn weights
	 */
	static DrawingPanel drawWeight(Substrate s1, Substrate s2, int s1Index, int s2Index) {
		//create new panel here
		int xCoord = 0;
		int yCoord = 0;
		int nodeVisWidth = WEIGHT_GRID_SIZE* s1.getSize().t1;
		int nodeVisHeight = WEIGHT_GRID_SIZE * s1.getSize().t2;
		int panelWidth = s2.getSize().t1 * nodeVisWidth  + s2.getSize().t1 - 1;
		int panelHeight  = s2.getSize().t2 * nodeVisHeight  + s2.getSize().t2 - 1;

		//instantiates panel
		DrawingPanel wPanel = new DrawingPanel(panelWidth, panelHeight, s1.getName() + "->" + s2.getName());
		wPanel.getGraphics().setBackground(Color.white);
		//for every node in s1, draws all links from it to s2
		for(int i = s2Index; i < (s2Index + (s2.getSize().t1 * s2.getSize().t2)); i++) {//goes through every node in target substrate
			//drawBorder(wPanel, xCoord, yCoord, nodeVisWidth + 2, nodeVisHeight + 2);
			drawNodeWeight(wPanel, nodes.get(i), xCoord , yCoord , s1Index, s1Index + (s1.getSize().t1 * s1.getSize().t2), nodeVisWidth, nodeVisHeight);
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
	 */
	private  static void  drawNodeWeight(DrawingPanel dPanel, TWEANNGenotype.NodeGene targetNode, int xCoord, int yCoord, int startingNodeIndex, int endingNodeIndex, int nodeWidth, int nodeHeight) {
		int xLeftEdge = xCoord;
		for(int j = startingNodeIndex; j < endingNodeIndex; j++) {//goes through every node in second substrate
			Color c = Color.gray;
			TWEANNGenotype.NodeGene node = nodes.get(j);
			TWEANNGenotype.LinkGene link = tg.getLinkBetween(node.innovation, targetNode.innovation);
			if(link != null) {
				double weight = link.weight;
				c = regularVisualization(ActivationFunctions.activation(ActivationFunctions.FTYPE_TANH, weight));
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
	 * If HyperNEAT neuron bias values are evolved, then this method determines
	 * how many CPPN outputs are needed to specify them: 1 per non-input substrate layer.
	 * @param hnt HyperNEATTask that specifies substrate connectivity
	 * @return number of bias outputs needed by CPPN
	 */
	public static int numBiasOutputsNeeded(HyperNEATTask hnt) {
		List<Substrate> subs = hnt.getSubstrateInformation();
		int count = 0;
		for(Substrate s : subs) {
			if(s.getStype() != Substrate.INPUT_SUBSTRATE) count++;
		}
		return count;
	}
	
	/**
	 * If bias outputs are used in CPPN, they will appear after all others.
	 * There should be one output group per layer pairing, so the number of
	 * layer pairings is multiplied by the neurons per output group to determine
	 * the index of the first bias output.
	 * @param hnt HyperNEAT task
	 * @return index where first bias output is located, if it exists
	 */
	public static int indexFirstBiasOutput(HyperNEATTask hnt) {
		return hnt.getSubstrateConnectivity().size() * HyperNEATCPPNGenotype.numCPPNOutputsPerLayerPair;
	}

	/**
	 * Number of links that a fully connected substrate network would possess for the
	 * given HyperNEAT task (which defines potential substrate connectivity)
	 * @param hnt HyperNEATTask
	 * @return Total possible links
	 */
	public static int totalPossibleLinks(HyperNEATTask hnt) {
		// extract substrate information from domain
		List<Substrate> subs = hnt.getSubstrateInformation();
		List<Triple<String, String, Boolean>> connections = hnt.getSubstrateConnectivity();
		// Will map substrate names to index in subs List
		HashMap<String, Integer> substrateIndexMapping = new HashMap<String, Integer>();
		for (int i = 0; i < subs.size(); i++) {
			substrateIndexMapping.put(subs.get(i).getName(), i);
		}

		int count = 0;
		for(int i = 0; i < connections.size(); i++) {
			String source = connections.get(i).t1;
			String target = connections.get(i).t2;
			Substrate subSource = subs.get(substrateIndexMapping.get(source));
			Substrate subTarget = subs.get(substrateIndexMapping.get(target));
			count += subSource.getSize().t1 * subSource.getSize().t2 * subTarget.getSize().t1 * subTarget.getSize().t2; 
		}
		
		return count;
	}
	
	/**
	 * Generalizes the retrieval of Substrate Information
	 * 
	 * @param inputWidth Width of each Input and Processing Board
	 * @param inputHeight Height of each Input and Processing Board
	 * @param numInputSubstrates Number of Input Boards
	 * @param output List<Triple<String, Integer, Integer>> that defines the name of the substrates, followed by their sizes
	 * 
	 * @return Substrate Information
	 */	
	public static List<Substrate> getSubstrateInformation(int inputWidth, int inputHeight, int numInputSubstrates, List<Triple<String, Integer, Integer>> output){
		int processWidth = Parameters.parameters.integerParameter("HNProcessWidth");
		int processDepth = Parameters.parameters.integerParameter("HNProcessDepth");
		return getSubstrateInformation(inputWidth, inputHeight, numInputSubstrates, processWidth, processDepth, output);
	}
	
	/**
	 * Generalizes the retrieval of Substrate Information
	 * 
	 * @param inputWidth Width of each Input and Processing Board
	 * @param inputHeight Height of each Input and Processing Board
	 * @param numInputSubstrates Number of Input Boards
	 * @param processWidth Number of Processing Boards per Processing Layer
	 * @param processDepth Number of Processing Layers
	 * @param output List<Triple<String, Integer, Integer>> that defines the name of the substrates, followed by their sizes
	 * 
	 * @return Substrate Information
	 */
	public static List<Substrate> getSubstrateInformation(int inputWidth, int inputHeight, int numInputSubstrates, int processWidth, int processDepth, List<Triple<String, Integer, Integer>> output) {		
		List<Substrate> substrateInformation = new LinkedList<Substrate>();

		// Convolutional network layer sizes depend on the size of the preceding layer,
		// along with the receptive field size, unless zero-padding is used
		boolean zeroPadding = Parameters.parameters.booleanParameter("zeroPadding");
		int receptiveFieldSize = Parameters.parameters.integerParameter("receptiveFieldSize");
		assert receptiveFieldSize % 2 == 1 : "Receptive field size needs to be odd to be centered: " + receptiveFieldSize;
		int edgeOffset = zeroPadding ? 0 : receptiveFieldSize / 2;
		
		// Different extractors correspond to different substrate configurations
		Pair<Integer, Integer> substrateDimension = new Pair<Integer, Integer>(inputWidth, inputHeight);
				
		for(int i = 0; i < numInputSubstrates; i++){
			Substrate inputSub = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, 
					new Triple<Integer, Integer, Integer>(i, 0, 0), // i is the x-coordinate: all are at the bottom level: y = 0, z = 0 
					"Input(" + i + ")");
			substrateInformation.add(inputSub);
		}
		if(!CommonConstants.hyperNEAT){
			Substrate biasSub = new Substrate(new Pair<>(1,1), Substrate.INPUT_SUBSTRATE, 
					new Triple<Integer, Integer, Integer>(numInputSubstrates, 0, 0), // to the right of all other input substrates 
					"bias");
			substrateInformation.add(biasSub);
		}				
		for(int i = 0; i < processDepth; i++) { // Add 2D hidden/processing layer(s)
			if(CommonConstants.convolution) {
				// Subsequent convolutional layers sometimes need to be smaller than preceding ones
				substrateDimension = new Pair<Integer, Integer>(substrateDimension.t1 - 2*edgeOffset, substrateDimension.t2 - 2*edgeOffset);
			}
			for(int k = 0; k < processWidth; k++) {
				// x coord = k, y = 1 + i because the height is the depth plus 1 (for the input layer)
				Triple<Integer, Integer, Integer> processSubCoord = new Triple<Integer, Integer, Integer>(k, 1 + i, 0);
				Substrate processSub = new Substrate(substrateDimension, Substrate.PROCCESS_SUBSTRATE, processSubCoord,
													"process(" + k + "," + i + ")", 
													CommonConstants.convolution ? ActivationFunctions.FTYPE_RE_LU : Substrate.DEFAULT_ACTIVATION_FUNCTION);
				substrateInformation.add(processSub);
			}
		}
		
		for(int i = 0; i < output.size(); i++){
			Substrate outputSub = new Substrate(new Pair<Integer, Integer>(output.get(i).t2, output.get(i).t3), Substrate.OUTPUT_SUBSTRATE,
					new Triple<Integer, Integer, Integer>(i, (processDepth+1), 0), // i is the x-coordinate, y = one above the top processing layer, z = 0 
					output.get(i).t1);
			substrateInformation.add(outputSub);
		}
		
		return substrateInformation;
	}
	
	/**
	 * Generalizes the creation of HyperNEAT Substrates
	 * 
	 * (Output layers are domain-specific)
	 * 
	 * @param numInputSubstrates Number of individual Input Boards being processed
	 * @param outputNames Names of the Output Layers (Domain-Specific)
	 * 
	 * @return Substrate connectivity
	 */
	public static List<Triple<String, String,Boolean>> getSubstrateConnectivity(int numInputSubstrates, List<String> outputNames){
		int processWidth = Parameters.parameters.integerParameter("HNProcessWidth");
		int processDepth = Parameters.parameters.integerParameter("HNProcessDepth");
		return getSubstrateConnectivity(numInputSubstrates, processWidth, processDepth, outputNames, Parameters.parameters.booleanParameter("extraHNLinks"));
	}

	/**
	 * Generalizes the creation of HyperNEAT Substrates
	 * 
	 * (Output layers are domain-specific)
	 * 
	 * @param numInputSubstrates Number of individual Input Boards being processed
	 * @param outputNames Names of the Output Layers (Domain-Specific)
	 * @param connectInputsToOutputs Should the Input Layer be directly connected to the Output Layer?
	 * 
	 * @return Substrate connectivity
	 */
	public static List<Triple<String, String,Boolean>> getSubstrateConnectivity(int numInputSubstrates, List<String> outputNames, boolean connectInputsToOutputs){
		int processWidth = Parameters.parameters.integerParameter("HNProcessWidth");
		int processDepth = Parameters.parameters.integerParameter("HNProcessDepth");
		return getSubstrateConnectivity(numInputSubstrates, processWidth, processDepth, outputNames, connectInputsToOutputs);
	}
	
	/**
	 * Generalizes the creation of HyperNEAT Substrates
	 * 
	 * (Output layers are domain-specific)
	 * 
	 * @param numInputSubstrates Number of individual Input Boards being processed
	 * @param processWidth Number of Processing Boards per Processing Layer
	 * @param processDepth Number of Processing Layers
	 * @param outputNames Names of the Output Layers (Domain-Specific)
	 * 
	 * @return Substrate connectivity
	 */
	public static List<Triple<String, String,Boolean>> getSubstrateConnectivity(int numInputSubstrates, int processWidth, int processDepth, List<String> outputNames){
		return getSubstrateConnectivity(numInputSubstrates, processWidth, processDepth, outputNames, Parameters.parameters.booleanParameter("extraHNLinks"));
	}
	
	/**
	 * Generalizes the creation of HyperNEAT Substrates
	 * 
	 * (Output layers are domain-specific)
	 * 
	 * @param numInputSubstrates Number of individual Input Boards being processed
	 * @param processWidth Number of Processing Boards per Processing Layer
	 * @param processDepth Number of Processing Layers
	 * @param outputNames Names of the Output Layers (Domain-Specific)
	 * @param connectInputsToOutputs Should the Input Layer be directly connected to the Output Layer?
	 * 
	 * @return Substrate connectivity
	 */
	public static List<Triple<String, String,Boolean>> getSubstrateConnectivity(int numInputSubstrates, int processWidth, int processDepth, List<String> outputNames, boolean connectInputsToOutputs){
		
		List<Triple<String, String, Boolean>> substrateConnectivity = null;
		
		substrateConnectivity = new LinkedList<Triple<String, String, Boolean>>();
		// Different extractors correspond to different substrate configurations
		if(processDepth > 0) {
			for(int k = 0; k < processWidth; k++) {
				// Link the input layer to the processing layer: allows convolution
				for(int i = 0; i < numInputSubstrates; i++){
					substrateConnectivity.add(new Triple<String, String, Boolean>("Input(" + i + ")", "process(" + k + ",0)", Boolean.TRUE));
				}
				
				if(!CommonConstants.hyperNEAT){
					// connect bias to the bottom layer of processing substrates
					substrateConnectivity.add(new Triple<String, String, Boolean>("bias", "process(" + k + ",0)", Boolean.TRUE));
				}
			}
		}
			
		// hidden layer connectivity is the same, regardless of input configuration
		for(int i = 0; i < (processDepth - 1); i++) {
			for(int k = 0; k < processWidth; k++) {
				for(int q = 0; q < processWidth; q++) {
					// Each processing substrate at one depth connected to processing subsrates at next depth
					substrateConnectivity.add(new Triple<String, String, Boolean>("process("+k+","+i+")", "process("+q+","+(i + 1)+")", Boolean.TRUE));
				}

				if(!CommonConstants.hyperNEAT){
					// connect bias to each remaining processing substrate
					substrateConnectivity.add(new Triple<String, String, Boolean>("bias", "process("+k+","+(i + 1)+")", Boolean.TRUE));
				}

			}
		}
		
		if(processDepth > 0) {
			for(int k = 0; k < processWidth; k++) {
				// Link the final processing layer to the output layer
				for(String name : outputNames){
					substrateConnectivity.add(new Triple<String, String, Boolean>("process(" + k + "," + (processDepth-1) + ")", name, Boolean.FALSE));
				}
			}
		}
		
		if(connectInputsToOutputs) { // Connect each input substrate directly to the output neuron
			// Link the input layer to the output layer
			for(int i = 0; i < numInputSubstrates; i++){
				for(String name : outputNames) {
					substrateConnectivity.add(new Triple<String, String, Boolean>("Input(" + i + ")", name, Boolean.FALSE));
				}
			}

			// For HyperNEAT seeded tasks
			if(!CommonConstants.hyperNEAT){
				for(String name : outputNames){
					// Each output substrate has a bias connection
					substrateConnectivity.add(new Triple<String, String, Boolean>("bias", name, Boolean.FALSE));
				}
			}
		}

		return substrateConnectivity;
	}
	
}
