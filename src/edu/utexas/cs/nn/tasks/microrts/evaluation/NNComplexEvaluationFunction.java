package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.ArrayList;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.microrts.evaluation.substrates.AllOfPlayerTypeOnGradientSubstrate;
import edu.utexas.cs.nn.tasks.microrts.evaluation.substrates.AllOfPlayerTypeSqrt3Substrate;
import edu.utexas.cs.nn.tasks.microrts.evaluation.substrates.AllOfPlayerTypeSubstrate;
import edu.utexas.cs.nn.tasks.microrts.evaluation.substrates.BaseGradientSubstrate;
import edu.utexas.cs.nn.tasks.microrts.evaluation.substrates.MicroRTSSubstrateInputs;
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
			default: 
				throw new UnsupportedOperationException("unrecognized substrate id: " + activeSubs.get(i));
			} //end switch		
			System.out.println("Add " + currentSubstrate + " substrate");
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
		double[] inputs = new double[substrateSize*numSubstrates];
		for(int i = 0; i < numSubstrates; i++){ //for each active substrate:
			double[][] twoDimensionalSubArray = inputSubstrates.get(i).getInputs(gs,playerToEvaluate);
			assert twoDimensionalSubArray.length > 0 : "length < 0";
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
