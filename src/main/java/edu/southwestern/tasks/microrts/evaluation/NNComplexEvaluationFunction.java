package edu.southwestern.tasks.microrts.evaluation;

import java.util.ArrayList;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.microrts.evaluation.substrates.AllOfPlayerTypeOnGradientSubstrate;
import edu.southwestern.tasks.microrts.evaluation.substrates.AllOfPlayerTypeSqrt3Substrate;
import edu.southwestern.tasks.microrts.evaluation.substrates.AllOfPlayerTypeSubstrate;
import edu.southwestern.tasks.microrts.evaluation.substrates.BaseGradientSubstrate;
import edu.southwestern.tasks.microrts.evaluation.substrates.MicroRTSSubstrateInputs;
import edu.southwestern.tasks.microrts.evaluation.substrates.SimpleResourceProportionSubstrate;
import edu.southwestern.util.datastructures.Pair;
import micro.rts.GameState;

/**
 * Puts different types of units onto their own substrates, 
 * according to parameters. Now the one-and-only default
 * evaluation function for microRTSTasks! 
 * 
 * @author alicequint
 */
public class NNComplexEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {

	private int numSubstrates;
	private int substrateSize;
	// TODO: This was meant to be used to resolve GitHub issue #341
	//private int smudgeSize = Parameters.parameters.integerParameter("microRTSInputSize"); 
	private ArrayList<Integer> activeSubs;
	private ArrayList<MicroRTSSubstrateInputs> inputSubstrates;
	
	//Indexes within areSubsActive
	private final int MOBILE = 0;
	private final int BUILDINGS = 1;
	private final int MY_MOBILE = 2;
	private final int MY_BUILDINGS = 3;
	private final int ENEMY_MOBILE = 4;
	private final int ENEMY_BUILDINGS = 5;
	private final int MY_ALL = 6;
	private final int ENEMY_ALL = 7;
	private final int ALL = 8;
	private final int RESOURCES = 9;
	private final int TERRAIN = 10;
	private final int ENEMY_BUILDING_GRADIENT = 11;
	private final int SQRT3_MOBILE = 12;
	private final int MY_GRADIENT_MOBILE = 13;
	private final int MY_RESOURCE_PROPORTION = 14;

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
			Parameters.parameters.booleanParameter("mRTSResources"), //resources
			Parameters.parameters.booleanParameter("mRTSTerrain"),
			Parameters.parameters.booleanParameter("mRTSObjectivePath"),
			Parameters.parameters.booleanParameter("mRTSAllSqrt3MobileUnits"),
			Parameters.parameters.booleanParameter("mRTSMyBuildingGradientMobileUnits"),
			Parameters.parameters.booleanParameter("mRTSResourceProportion")
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
			case MOBILE: 
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Worker",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				criteria.add(new Pair<String, Integer>("Light",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				criteria.add(new Pair<String, Integer>("Heavy",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				criteria.add(new Pair<String, Integer>("Ranged",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case BUILDINGS: 
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Base", AllOfPlayerTypeSubstrate.ANY_PLAYER));
				criteria.add(new Pair<String, Integer>("Barracks", AllOfPlayerTypeSubstrate.ANY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case MY_MOBILE:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Worker",AllOfPlayerTypeSubstrate.MY_PLAYER));
				criteria.add(new Pair<String, Integer>("Light",AllOfPlayerTypeSubstrate.MY_PLAYER));
				criteria.add(new Pair<String, Integer>("Heavy",AllOfPlayerTypeSubstrate.MY_PLAYER));
				criteria.add(new Pair<String, Integer>("Ranged",AllOfPlayerTypeSubstrate.MY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case MY_BUILDINGS:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Base", AllOfPlayerTypeSubstrate.MY_PLAYER));
				criteria.add(new Pair<String, Integer>("Barracks", AllOfPlayerTypeSubstrate.MY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case ENEMY_MOBILE:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Worker",AllOfPlayerTypeSubstrate.ENEMY_PLAYER));
				criteria.add(new Pair<String, Integer>("Light",AllOfPlayerTypeSubstrate.ENEMY_PLAYER));
				criteria.add(new Pair<String, Integer>("Heavy",AllOfPlayerTypeSubstrate.ENEMY_PLAYER));
				criteria.add(new Pair<String, Integer>("Ranged",AllOfPlayerTypeSubstrate.ENEMY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case ENEMY_BUILDINGS:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Base", AllOfPlayerTypeSubstrate.ENEMY_PLAYER));
				criteria.add(new Pair<String, Integer>("Barracks", AllOfPlayerTypeSubstrate.ENEMY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case MY_ALL:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, AllOfPlayerTypeSubstrate.MY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case ENEMY_ALL:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, AllOfPlayerTypeSubstrate.ENEMY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case ALL:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, AllOfPlayerTypeSubstrate.ANY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case RESOURCES:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>(null, AllOfPlayerTypeSubstrate.RESOURCE));
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria);
				break;
			case TERRAIN:
				criteria = new ArrayList<Pair<String, Integer>>(); //empty
				currentSubstrate = new AllOfPlayerTypeSubstrate(criteria, true);
				break;
			case ENEMY_BUILDING_GRADIENT: 
				currentSubstrate = new BaseGradientSubstrate(true); // gradient to enemy base
				break;
			case SQRT3_MOBILE:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Worker",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				criteria.add(new Pair<String, Integer>("Light",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				criteria.add(new Pair<String, Integer>("Heavy",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				criteria.add(new Pair<String, Integer>("Ranged",AllOfPlayerTypeSubstrate.ANY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeSqrt3Substrate(criteria);
				break;
			case MY_GRADIENT_MOBILE:
				criteria = new ArrayList<Pair<String, Integer>>();
				criteria.add(new Pair<String, Integer>("Worker",AllOfPlayerTypeSubstrate.MY_PLAYER));
				criteria.add(new Pair<String, Integer>("Light",AllOfPlayerTypeSubstrate.MY_PLAYER));
				criteria.add(new Pair<String, Integer>("Heavy",AllOfPlayerTypeSubstrate.MY_PLAYER));
				criteria.add(new Pair<String, Integer>("Ranged",AllOfPlayerTypeSubstrate.MY_PLAYER));
				currentSubstrate = new AllOfPlayerTypeOnGradientSubstrate(criteria, true);
				break;
			case MY_RESOURCE_PROPORTION:
				currentSubstrate = new SimpleResourceProportionSubstrate(true);
				break;
			default: 
				throw new UnsupportedOperationException("unrecognized substrate id: " + activeSubs.get(i));
			} //end switch		
			System.out.println("Add " + currentSubstrate + " substrate"); //dont delete
			// Add substrate to list of substrates
			inputSubstrates.add(currentSubstrate);
		}

	}

	/**
	 * takes the gameState and separates into substrates that contain
	 * different information.
	 */
	@Override
	protected double[] gameStateToArray(GameState gs, int playerToEvaluate) {
		pgs = gs.getPhysicalGameState();
		substrateSize = pgs.getHeight()*pgs.getWidth();
		int numInputs = substrateSize*numSubstrates;
		// The SimpleResourceProportionSubstrate is unusual because its size is just 1 by 1
		if(areSubsActive[MY_RESOURCE_PROPORTION]) {
			numInputs = numInputs - substrateSize + 1;
		}
		double[] inputs = new double[numInputs];
		for(int i = 0; i < numSubstrates; i++){ //for each active substrate:
			double[][] twoDimensionalSubArray = inputSubstrates.get(i).getInputs(gs,playerToEvaluate);
			assert twoDimensionalSubArray.length > 0 : "length < 0";
			int thisSubstrateSize = twoDimensionalSubArray.length * twoDimensionalSubArray[0].length;
			int thisWidth = twoDimensionalSubArray.length;
			for(int j = 0; j < thisSubstrateSize; j++){
				// The only reason using substrateSize in the calculation below is compatible with 
				// SimpleResourceProportionSubstrate is that SimpleResourceProportionSubstrate must be the
				// final substrate. Otherwise, problems would emerge.
				inputs[(i*substrateSize)+j] = twoDimensionalSubArray[j%thisWidth][j/thisWidth];
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
		int numInputs = pgs.getWidth()*pgs.getHeight() * numSubstrates;
		// The SimpleResourceProportionSubstrate is unusual because its size is just 1 by 1
		if(areSubsActive[MY_RESOURCE_PROPORTION]) {
			numInputs = numInputs - (pgs.getWidth()*pgs.getHeight()) + 1;
		}
		String[] labels = new String[numInputs];
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
	//		Parameters.initializeParameterCollections(new String[]{"task:edu.southwestern.tasks.microrts.MicroRTSTask","hyperNEAT:true"
	//				,"microRTSEnemySequence:edu.southwestern.tasks.microrts.iterativeevolution.HardeningEnemySequence",
	//				"microRTSMapSequence:edu.southwestern.tasks.microrts.iterativeevolution.GrowingMapSequence","log:microRTS-temp","saveTo:temp"});
	//		MMNEAT.loadClasses();
	//		Genotype<T> g = PopulationUtil.extractGenotype(NNfile);
	//		nn = g.getPhenotype();
	//	}

}
