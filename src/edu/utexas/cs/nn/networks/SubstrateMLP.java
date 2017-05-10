package edu.utexas.cs.nn.networks;

import java.util.ArrayList;
import java.util.List;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 * Multi-Layer Perceptron class that has a generalized
 * number of in, hidden, and out layers that can be 
 * used as an alternative to TWEANNs, also will
 * hopefully speed up hyperNEAT considerably
 * 
 * @author Lauren Gillespie
 *
 */
public class SubstrateMLP implements Network {

	/**
	 * MLP layer object. Stores node information, name of layer
	 * and type of layer
	 * @author Lauren Gillespie
	 *
	 */
	public class MLPLayer {

		//Layer types
		public final static int INPUT_LAYER = 0;
		public final static int PROCCESS_LAYER = 1;
		public final static int OUTPUT_LAYER = 2;

		//information stored by MLPLayer class
		public double[][] nodes;
		public String name;
		public int ltype;

		/**
		 * Constructor for MLPLayer
		 * @param nodes nodes in layer
		 * @param name name of layer
		 * @param ltype type of layer
		 */
		public MLPLayer(double[][] nodes, String name, int ltype) {
			this.nodes = nodes;
			this.name = name;
			this.ltype = ltype;
		}
	}

	/**
	 * MLP connection object. Stores information on connections between
	 * MLP layers and connection weights
	 * @author gillespl
	 *
	 */
	public class MLPConnection {

		//information stored by MLPConnection class
		// (x,y) of source followed by (x,y) of target
		public double[][][][] connection;
		public MLPLayer source;
		public MLPLayer target;

		/**
		 * Constructor for mlpConnection
		 * @param connection connection between layers
		 * @param connects the layers connected by MLPConnection
		 */
		public MLPConnection(double[][][][] connection, MLPLayer source, MLPLayer target) { 
			this.connection = connection;
			this.source = source;
			this.target = target;
		}
	}


	//private instance variables
	protected List<MLPLayer> layers;
	protected List<MLPConnection> connections;
	private int numInputs = 0;
	private int numOutputs = 0;
	private int ftype;

	public SubstrateMLP(List<Substrate> subs,  List<Pair<String, String>> connections, Network network) {
		this(subs, connections, network, CommonConstants.ftype);
	}	

	/**
	 * Constructor
	 * @param subs list of substrates provided by task
	 * @param connections connections of substrates provided by task
	 * @param network cppn used to process coordinates to produce weight of links
	 * @param ftype An activation function type from ActivationFunctions
	 */
	public SubstrateMLP(List<Substrate> subs,  List<Pair<String, String>> connections, Network network, int ftype) {
		assert network.numInputs() == HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS:"Number of inputs to network = " + network.numInputs() + " not " + HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
		this.ftype = ftype;
		this.connections = new ArrayList<MLPConnection>();
		layers = new ArrayList<MLPLayer>();
		int connectionsIndex = 0;
		addLayers(subs, layers);
		for(Pair<String, String> connection : connections) {
			Substrate sourceSub = null;
			Substrate targetSub = null;
			for(int z = 0; z < subs.size(); z++) {
				if(subs.get(z).getName().equals(connection.t1)) { 
					sourceSub = subs.get(z);
				} else if(subs.get(z).getName().equals(connection.t2)) {
					targetSub = subs.get(z);
				}
			} 
			assert(sourceSub != null && targetSub != null):"either source or target substrate is not in subs list!";
			double[][][][] connect = new double[sourceSub.getSize().t1][sourceSub.getSize().t2][targetSub.getSize().t1][targetSub.getSize().t2];
			for(int X1 = 0; X1 < sourceSub.getSize().t1; X1++) {
				for(int Y1 = 0; Y1 < sourceSub.getSize().t2; Y1++) {
					for(int X2 = 0; X2 < targetSub.getSize().t1; X2++) {
						for(int Y2 = 0; Y2 < targetSub.getSize().t2; Y2++) {
							// CPPN inputs need to be centered and scaled
							ILocated2D scaledSourceCoordinates = MMNEAT.substrateMapping.transformCoordinates(new Tuple2D(X1, Y1), sourceSub.getSize().t1, sourceSub.getSize().t2);
							ILocated2D scaledTargetCoordinates = MMNEAT.substrateMapping.transformCoordinates(new Tuple2D(X2, Y2), targetSub.getSize().t1, targetSub.getSize().t2);
							// inputs to CPPN 
							double[] inputs = { scaledSourceCoordinates.getX(), scaledSourceCoordinates.getY(), scaledTargetCoordinates.getX(), scaledTargetCoordinates.getY(), HyperNEATCPPNGenotype.BIAS}; 
							double[] outputs = network.process(inputs);
							boolean expressLink = Math.abs(outputs[connectionsIndex]) > CommonConstants.linkExpressionThreshold;
							//whether or not to place a link in location
							if (expressLink) {
								connect[X1][Y1][X2][Y2] = NetworkUtil.calculateWeight(outputs[connectionsIndex]);
							} else {//if not, make weight 0, synonymous to no link in first place
								connect[X1][Y1][X2][Y2] = 0;
							}
						}
					}
				}
			}
			MLPLayer sourceLayer = null;
			MLPLayer toLayer = null;
			for(MLPLayer layer : layers) {
			if(layer.name.equals(sourceSub.getName())) {
				sourceLayer = layer;
				} else if(layer.name.equals(targetSub.getName())) { 
				toLayer = layer;
					}
		}
			assert(sourceLayer != null && toLayer != null):"can't find layers corresponding to substrate!";
			MLPConnection conn = new MLPConnection(connect, sourceLayer, toLayer);
			this.connections.add(conn);
			connectionsIndex++;			
		}
	}

	/**
	 * adds nodes from substrates to layers list
	 * @param subs substrate list
	 * @param layers node array to add to 
	 */
	private final void addLayers(List<Substrate> subs, List<MLPLayer> layers) { 
		for(Substrate sub: subs) {
			MLPLayer layer = new MLPLayer(new double[sub.getSize().t1][sub.getSize().t2], sub.getName(), sub.getStype());
			layers.add(layer);
			if(sub.getStype() == Substrate.INPUT_SUBSTRATE){ numInputs += sub.getSize().t1 * sub.getSize().t2;
			}else if(sub.getStype() == Substrate.OUTPUT_SUBSTRATE){ numOutputs += sub.getSize().t1 * sub.getSize().t2;}
		}
	}

	/**
	 * Fills layers with correct inputs.
	 * Assumes input layers are at start of layer
	 * list in order that they will be filled.
	 * @param layers list of layers
	 * @param inputs inputs 
	 */
	private void fillLayers(List<MLPLayer> layers, double[] inputs) { 
		int x = 0;
		for(MLPLayer mlplayer : layers) {
			assert (mlplayer.ltype == Substrate.INPUT_SUBSTRATE) : "Input layers must be at front of list and have room for inputs";
			if(mlplayer.ltype == Substrate.INPUT_SUBSTRATE) {
				for(int j = 0; j< mlplayer.nodes[0].length; j++) {
					for(int i = 0; i < mlplayer.nodes.length; i++) {
						mlplayer.nodes[i][j] = inputs[x++];
					}
				}
			}
			// To replicate TWEANN behavior, all input neurons also
			// transmit through an activation function. This is not
			// normal MLP behavior, but I don't think there are any
			// problems with this. 
			NetworkUtil.activateLayer(mlplayer.nodes, ftype);
			// After input layers have been passed
			if(x == inputs.length) break;
		}
	}
	/**
	 * Returns number of inputs
	 */
	@Override
	public int numInputs() {
		return numInputs;
	}

	/**
	 * Returns number of outputs
	 */
	@Override
	public int numOutputs() {
		return numOutputs;
	}

	/**
	 * Processes inputs through network
	 */
	@Override
	public double[] process(double[] inputs) {
		assert numInputs == inputs.length: "number of inputs " + numInputs + " does not match size of inputs given: " + inputs.length;
		fillLayers(layers, inputs);
		for(MLPConnection connect: connections) { //process through rest of network
			propagateOneStep(connect);
		}
		// TODO Outputs may actually come from multiple output substrates
		double[] outputs = ArrayUtil.doubleArrayFrom2DdoubleArrayRowMajor(layers.get(layers.size() - 1).nodes);
		// Therefore, this size() - 1 trick may not always work.
//		double[] outputs =  ArrayUtil.doubleArrayFrom2DdoubleArrayRowMajor(layers.get(0).nodes);
//		for(int i = 1; i < layers.size(); i++) {
//			if(layers.get(i).ltype == MLPLayer.OUTPUT_LAYER) { 
//				MLPLayer layer = layers.get(i);
//				double[] newOutputs = ArrayUtil.doubleArrayFrom2DdoubleArrayRowMajor(layer.nodes);
//				outputs = ArrayUtil.combineArrays(outputs, newOutputs);
//			}
//		}
		return outputs;
	}

	/**
	 * Propagates one step through network
	 * @param inputs inputs to layer
	 * @return outputs from layer
	 */
	private void propagateOneStep(MLPConnection connection) {
		MLPLayer fromLayer = connection.source;
		MLPLayer toLayer = connection.target;
		assert(fromLayer != null && toLayer != null):"either from or to layer was not properly initialized!";
		// Modifies toLayer.nodes
		NetworkUtil.propagateOneStep(fromLayer.nodes, toLayer.nodes, connection.connection);
		NetworkUtil.activateLayer(toLayer.nodes, ftype);
	}

	/**
	 * Clears out all previous activations in nodes
	 */
	@Override
	public void flush() {
		for(MLPLayer toClear : layers) {
			clear(toClear.nodes);
		}

	}
	@Override
	public String toString() { 
		//System.out.println("connections size: " + connections.size());
		String result = "";
		result += numInputs + " Inputs\n";
		result += numOutputs + " Outputs\n";
		result += ActivationFunctions.activationName(ftype) + " activation\n";
		for(MLPConnection connection : connections) {
			for(int X1 = 0; X1 < connection.connection.length; X1++) {
				for(int Y1 = 0; Y1 < connection.connection[0].length; Y1++) {
					for(int X2 = 0; X2 < connection.connection[0][0].length; X2++) {
						for(int Y2 = 0; Y2 < connection.connection[0][0][0].length; Y2++) {
							result += connection.source.name + ": [" + X1 + ", " + Y1 + "]" + " : " + connection.connection[X1][Y1][X2][Y2] + " : " + connection.target.name +  ": [" + X2 + ", " + Y2 + "]" +"\n";
						}
					}
				}
			}
		}
		return result;
	}
	/**
	 * Clears given double array
	 * @param toClear array to clear
	 */
	private void clear(double[][] toClear) {
		for(int x = 0; x < toClear.length; x++) {
			for(int y = 0; y < toClear[0].length; y++) {
				toClear[x][y] = 0;
			}
		}
	}

	public double[][][][] getConnections(int index) { 
		return connections.get(index).connection;
	}

	@Override
	/**
	 * Will always return false
	 * unless multiobjective
	 * behavior developted for
	 * substrateMLP
	 */
	public boolean isMultitask() {
		return false;
	}

	@Override
	/**
	 * not supported yet
	 */
	public int effectiveNumOutputs() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	/**
	 * not supported yet
	 */
	public void chooseMode(int mode) {
		throw new UnsupportedOperationException("Not supported yet.");

	}

	@Override
	/**
	 * not supported yet
	 */
	public int lastModule() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	/**
	 * not supported yet
	 */
	public double[] moduleOutput(int mode) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	/**
	 * not supported yet
	 */
	public int numModules() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	/**
	 * not supported yet
	 */
	public int[] getModuleUsage() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
