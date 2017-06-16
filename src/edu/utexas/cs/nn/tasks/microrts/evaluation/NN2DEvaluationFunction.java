package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;
import micro.rts.GameState;
import micro.rts.units.Unit;

/**
 * Evaluation function compatible with NEAT and HyperNEAT that
 * uses 1 substrate containing information about every tile of
 * the game state.
 * 
 * @author alicequint
 *
 * @param <T> NN
 */
public class NN2DEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {

	public static final double BASE_WEIGHT = 4; //hard to quantify because different amount of importance at different stages of the game
	public static final double BASE_RESOURCE_WEIGHT = .25;
	public static final double BARRACKS_WEIGHT = 2.5;
	public static final double WORKER_WEIGHT = 1;
	public static final double WORKER_RESOURCE_WEIGHT = .15;
	//these subject to change because in experiments so far there have rarely been multiple non-worker units
	public static final double LIGHT_WEIGHT = 3; 
	public static final double HEAVY_WEIGHT = 3.25;
	public static final double RANGED_WEIGHT = 3.75;
	public static final double RAW_RESOURCE_WEIGHT = .01;
	
	/**
	 * constructor for FEStatePane and similar
	 * @param NNfile
	 * 				neural network .xml file 
	 */
	public NN2DEvaluationFunction(String NNfile){
		// Parameter init can/should be removed when moving to stand-alone competition entry
		Parameters.initializeParameterCollections(new String[]{"task:edu.utexas.cs.nn.tasks.microrts.MicroRTSTask","hyperNEAT:true"
				,"microRTSEnemySequence:edu.utexas.cs.nn.tasks.microrts.iterativeevolution.HardeningEnemySequence",
				"microRTSMapSequence:edu.utexas.cs.nn.tasks.microrts.iterativeevolution.GrowingMapSequence","log:microRTS-temp","saveTo:temp"});
		MMNEAT.loadClasses();
		Genotype<T> g = PopulationUtil.extractGenotype(NNfile);
		nn = g.getPhenotype();
	}

	/**
	 * Default constructor used by MMNEAT's class creation methods.
	 * Must pass in the network via the setNetwork method of parent class.
	 */
	public NN2DEvaluationFunction(){
		super();
	}
	
	/**
	 * represents all squares of the gameState in an array
	 */
	protected double[] gameStateToArray(GameState gs) {
		pgs = gs.getPhysicalGameState();
		double[] board = new double[pgs.getHeight()*pgs.getWidth()];
		int boardIndex;
		Unit currentUnit;
		for(int j = 0; j < pgs.getHeight(); j++){
			for(int i = 0; i < pgs.getWidth(); i++){
				boardIndex = i + j * pgs.getHeight();
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					switch(currentUnit.getType().name){
					case "Worker": board[boardIndex] = WORKER_WEIGHT + (WORKER_RESOURCE_WEIGHT * currentUnit.getResources()); break; 
					case "Light": board[boardIndex] = LIGHT_WEIGHT; break;
					case "Heavy": board[boardIndex] = HEAVY_WEIGHT; break;
					case "Ranged": board[boardIndex] = RANGED_WEIGHT; break;
					case "Base": board[boardIndex] = BASE_WEIGHT + (BASE_RESOURCE_WEIGHT * currentUnit.getResources()); break;
					case "Barracks": board[boardIndex] = BARRACKS_WEIGHT; break;
					case "Resource": board[boardIndex] = RAW_RESOURCE_WEIGHT; break;
					default: break;
					}
					if(currentUnit.getPlayer() == 1) board[boardIndex] *= -1; 
				}
			}//end inner loop
		}//end outer loop
		return board;
	}

	/**
	 * returns labels describing what gameStateToArray will
	 * give for the inputs to a NN  
	 */
	@Override
	public String[] sensorLabels() {
		assert pgs != null : "There must be a physical game state in order to extract height and width";
		String[]labels = new String[pgs.getHeight()*pgs.getWidth()];
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				String label = "unit at (" + i + ", " + j + ")";
				labels[i*pgs.getWidth() + j] = label;
			} 
		}
		return labels; 
	}
	
	public int getNumInputSubstrates(){
		return 1;
	}
}
