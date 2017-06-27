package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.ArrayList;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;

/**
 * Puts different types of units onto their own substrates, 
 * according to parameters. Now the one-and-only default
 * evaluation function for microRTSTasks! 
 * 
 * @author alicequint
 * 
 * unfinished, eventually different substrate for each unit-type maybe.
 */
public class NNComplexEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {

	private int numSubstrates;
	private int substrateSize;
	private int smudgeSize = Parameters.parameters.integerParameter("microRTSInputSize"); //TODO use
	private ArrayList<Integer> activeSubs;
	private ArrayList<MicroRTSSubstrateInputs> inputSubstrates;
	
	//Indexes within areSubsActive
	private final int mobile = 0;
	private final int buildings = 1;
	private final int myMobile = 2;
	private final int myBuildings = 3;
	private final int oppsMobile = 4;
	private final int oppsBuildings = 5;
	private final int myAll = 6;
	private final int oppsAll = 7;
	private final int all = 8;
	private final int neutral = 9;
	private final int terrain = 10;
	private final int path = 11;

	private boolean[] areSubsActive = new boolean[]{
			Parameters.parameters.booleanParameter("mRTSMobileUnits"),
			Parameters.parameters.booleanParameter("mRTSBuildings"),
			Parameters.parameters.booleanParameter("mRTSMyMobileUnits"),
			Parameters.parameters.booleanParameter("mRTSMyBuildings"),
			Parameters.parameters.booleanParameter("mRTSOpponentsMobileUnits"),
			Parameters.parameters.booleanParameter("mRTSOpponentsBuildings"),
			Parameters.parameters.booleanParameter("mRTSMyAll"),
			Parameters.parameters.booleanParameter("mRTSOpponentsAll"),
			Parameters.parameters.booleanParameter("mRTSAll"), //the only one that is true by default
			Parameters.parameters.booleanParameter("mRTSNeutral"), //terrain and resources
			Parameters.parameters.booleanParameter("mRTSTerrain"),
			Parameters.parameters.booleanParameter("mRTSObjectivePath"),
	};

	/**
	 * Default constructor used by MMNEAT's class creation methods.
	 * Must pass in the network via the setNetwork method of parent class.
	 */
	public NNComplexEvaluationFunction(){
		super();
		numSubstrates = 0;
		activeSubs = new ArrayList<Integer>();
		for(int i = 0; i < areSubsActive.length; i++){
			if(areSubsActive[i]){
				numSubstrates++;
				activeSubs.add(i);
			}
		}
		assert activeSubs.size() == numSubstrates : "conflicting information gathered in NNComplex constructor";

		inputSubstrates = new ArrayList<>();
		ArrayList<Pair<String,Integer>> criteria = new ArrayList<Pair<String, Integer>>();
		MicroRTSSubstrateInputs currentSubstrate = null;
		for(int i = 0; i < numSubstrates; i++){ //for each active substrate:
			switch(activeSubs.get(i)){
			case mobile: {
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("mobile",-2));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case buildings: {
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("immobile",-2));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case myMobile:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("mobile", 0));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case myBuildings:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("immobile", 0));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case oppsMobile:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("mobile", 1));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case oppsBuildings:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("immobile", 1));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case myAll:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, 0));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case oppsAll:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, 1));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case all:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, -2));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			} case neutral:{
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, -1));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria, true);
				break;
			} case terrain:{
				criteria = new ArrayList<Pair<String, Integer>>(); //empty
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria, true);
			} case path: currentSubstrate = new BaseGradientSubstrate(0); break;
			default: throw new UnsupportedOperationException("unrecognized substrate id: " + activeSubs.get(i));
			} //end switch		
			
			// Add substrate to list of substrates
			inputSubstrates.add(currentSubstrate);
		}

	}

	/**
	 * takes the gameState and separates into substrates that contain
	 * different information.
	 */
	@Override
	protected double[] gameStateToArray(GameState gs) {
		ArrayList<Pair<String,Integer>> criteria = new ArrayList<Pair<String, Integer>>();
		pgs = gs.getPhysicalGameState();
		substrateSize = pgs.getHeight()*pgs.getWidth();
		double[] inputs = new double[substrateSize*numSubstrates];
		for(int i = 0; i < numSubstrates; i++){ //for each active substrate:
			double[][] twoDimensionalSubArray = inputSubstrates.get(i).getInputs(gs);
			assert twoDimensionalSubArray.length > 0 : "length < 0";
//			System.out.println(Arrays.deepToString(twoDimensionalSubArray));
			int width = pgs.getWidth();
			for(int j = 0; j < substrateSize; j++){
				inputs[(i*substrateSize)+j] = twoDimensionalSubArray[j%width][j/width];
			}
		}
		return inputs;
	}

	/**
	 * returns labels describing what gameStateToArray will
	 * give for the inputs to a NN
	 */
	@Override
	public String[] sensorLabels() {
		assert pgs != null : "There must be a physical game state in order to extract height and width";
		String[] labels = new String[pgs.getWidth()*pgs.getHeight() * numSubstrates];
		for(int h = 0; h < numSubstrates; h++ ){
			for(int i = 0; i < pgs.getHeight(); i++){
				for(int j = 0; j < pgs.getWidth(); j++){
					labels[substrateSize*h + pgs.getWidth()*i + j] = "substrate: " + h + " row: " + i + " column: " + j;
				}
			}
		}
		return labels;
	}

	@Override
	public int getNumInputSubstrates() {
		return numSubstrates;
	}

	/**
	 * constructor for FEStatePane and similar
	 * @param NNfile
	 * 				neural network .xml file 
	 */
	//	public NNComplexEvaluationFunction(String NNfile){
	//		// Parameter init can/should be removed when moving to stand-alone competition entry
	//		Parameters.initializeParameterCollections(new String[]{"task:edu.utexas.cs.nn.tasks.microrts.MicroRTSTask","hyperNEAT:true"
	//				,"microRTSEnemySequence:edu.utexas.cs.nn.tasks.microrts.iterativeevolution.HardeningEnemySequence",
	//				"microRTSMapSequence:edu.utexas.cs.nn.tasks.microrts.iterativeevolution.GrowingMapSequence","log:microRTS-temp","saveTo:temp"});
	//		MMNEAT.loadClasses();
	//		Genotype<T> g = PopulationUtil.extractGenotype(NNfile);
	//		nn = g.getPhenotype();
	//	}

}
