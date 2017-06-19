package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.HashSet;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.units.Unit;

/**
 * Evaluation Function for MicroRTS that puts different unit-classes
 * onto their own substrates, according to parameters
 * 
 * @author alicequint
 * 
 * unfinished, eventually different substrate for each unit-type maybe.
 */
public class NNComplexEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {
	
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
		//	Parameters.parameters.booleanParameter("mRTSTerrain"),
			//resources separate from units, seperate from bases
			//mobile , bases , terrain + resources. : first thing to try 
	};
	private int numSubstrates;

	/**
	 * constructor for FEStatePane and similar
	 * @param NNfile
	 * 				neural network .xml file 
	 */
	public NNComplexEvaluationFunction(String NNfile){
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
	public NNComplexEvaluationFunction(){
		super();
		numSubstrates = 0;
		for(boolean b : areSubsActive){
			if(b) numSubstrates++;
		}
	}

	/**
	 * takes the gameState and separates into substrates that contain
	 * different information.
	 */
	@Override
	protected double[] gameStateToArray(GameState gs) {
		pgs = gs.getPhysicalGameState();
		int substrateSize = pgs.getHeight()*pgs.getWidth();
		double[] inputs = new double[substrateSize * numSubstrates];
		Unit current = null;
		int boardIndex;
		for(int j = 0; j < pgs.getHeight(); j++){
			for(int i = 0; i < pgs.getWidth(); i++){
//				int isTerrain = pgs.getTerrain(i, j);
//				System.out.print(isTerrain); // 0 = no, (1 = yes (maybe))
				boardIndex =  i + j * pgs.getHeight(); 
				current = pgs.getUnitAt(i, j);
				if(current!= null){
					inputs = populateSubstratesWith(current, inputs, substrateSize, boardIndex);
				}
			}//end i : width
		}//end j : height
		System.out.println("SUB 0 -----------------------");
		printSubstrateConfig(0, inputs);
		System.out.println("SUB 1 -----------------------");
		printSubstrateConfig(0, inputs);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		return inputs;
	}

	/**
	 * puts the current unit into all substrates where it should go
	 * according to parameters.
	 * 
	 * @param u
	 * 			unit to be put into substrates
	 * @param substrates
	 * 				array containing all substrates. this array is modified and then returned
	 * @param substrateSize
	 * 				how big each substrate is
	 * @param location
	 * 				index within an individual substrate
	 * @return double[] input as substrates, but with the unit added at location for every appropriate substrate
	 * 
	 */
	private double[] populateSubstratesWith(Unit u, double[] substrates, int substrateSize, int location){
		HashSet<Integer> appropriateSubstrates = new HashSet<>();
		int numCurrentSubs = 0;
		//for current unit, find which substrates it belongs to
		
		System.out.println("unit: " + u.getX() + " " + u.getY()+ " ==> " + location);
		
		if(areSubsActive[0]){ //all mobile units   
			if(u.getType().canMove){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[1]){ //all buildings
			if(!u.getType().canMove){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[2]){
			if(u.getType().canMove && u.getPlayer() == 0){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[3]){ //includes resources
			if(!u.getType().canMove && u.getPlayer() != 1){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[4]){
			if(u.getType().canMove && u.getPlayer() == 1){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[5]){ //includes resources
			if(!u.getType().canMove && u.getPlayer() != 0){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[6]){
			if(u.getPlayer() == 0){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[7]){
			if(u.getPlayer() == 1){
				appropriateSubstrates.add(numCurrentSubs);
			}
			numCurrentSubs++;
		}
		if(areSubsActive[8]){ //all
			appropriateSubstrates.add(numCurrentSubs);
			numCurrentSubs++;
		}
		for(int appropriateSubstrate : appropriateSubstrates){
			System.out.println("putting unit in sub: " + appropriateSubstrate + " at sublocation " + location + " out of " + substrateSize);
			int indexWithinAll = (substrateSize * appropriateSubstrate) + location;
			System.out.println("index within all: " + indexWithinAll + " = " + substrateSize  + " * " + appropriateSubstrate + " + " + location);
			substrates[indexWithinAll] = 1; //TODO depends on which sub, etc.
		} 
		return substrates;
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
			for(int i = 0; i < pgs.getWidth(); i++){
				for(int j = 0; j < pgs.getHeight(); j++){
					labels[i*pgs.getWidth() + j ] = "Mobile unit:  (" + i + ", " + j + ")";
					labels[i*pgs.getWidth() + j + (pgs.getWidth()*pgs.getHeight()*h)] = "Immobile unit: (" + i + "," + j + ")";
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
	 * for debugging & ghostbusting
	 * @param whichSub
	 * 				
	 * @param substrates
	 */
	private void printSubstrateConfig(int whichSub, double[] substrates){
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				System.out.print(substrates[whichSub*pgs.getHeight()*pgs.getWidth()+(i*pgs.getWidth() + j)] + " ");
			}
			System.out.println();
		}
	}

}
