package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.HashSet;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.PopulationUtil;
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

	private boolean[] activeSubs = new boolean[]{
			Parameters.parameters.booleanParameter("mRTSMobileUnits"),
			Parameters.parameters.booleanParameter("mRTSBuildings"),
			Parameters.parameters.booleanParameter("mRTSMyMobileUnits"),
			Parameters.parameters.booleanParameter("mRTSMyBuildings"),
			Parameters.parameters.booleanParameter("mRTSOpponentsMobileUnits"),
			Parameters.parameters.booleanParameter("mRTSOpponentsBuildings"),
			Parameters.parameters.booleanParameter("mRTSMyAll"),
			Parameters.parameters.booleanParameter("mRTSOpponentsAll"),
		//	Parameters.parameters.booleanParameter("mRTSTerrain"),
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
		for(boolean b : activeSubs){
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
				int ter = pgs.getTerrain(i, j);
				System.out.print(ter);
				boardIndex =  i + j * pgs.getHeight(); 
				current = pgs.getUnitAt(i, j);
				if(current!= null){
					inputs = populateSubstratesWith(current, inputs, substrateSize, boardIndex);
				}
				System.out.println();
			}
		}
		return inputs;
	}

	/**
	 * puts the current unit into all substrates where it should go
	 * according to parameters.
	 * 
	 * @param u
	 * 			unit to be put into substrates
	 * @param substrates
	 * 				array containing all substrates
	 * @param substrateSize
	 * 				how big each substrate is
	 * @param location
	 * 				index within an individual substrate
	 * 
	 */
	private double[] populateSubstratesWith(Unit u, double[] substrates, int substrateSize, int location){
		HashSet<Integer> appropriateSubstrates = new HashSet<>();
		int numCurrentSubs = 0;
		if(activeSubs[0]){  
			numCurrentSubs++;
			if(u.getType().canMove){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		if(activeSubs[1]){
			numCurrentSubs++;
			if(!u.getType().canMove){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		if(activeSubs[2]){
			numCurrentSubs++;
			if(u.getType().canMove && u.getPlayer() == 0){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		if(activeSubs[3]){ //includes resources
			numCurrentSubs++;
			if(!u.getType().canMove && u.getPlayer() != 1){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		if(activeSubs[4]){
			numCurrentSubs++;
			if(u.getType().canMove && u.getPlayer() == 1){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		if(activeSubs[5]){ //includes resources
			numCurrentSubs++;
			if(!u.getType().canMove && u.getPlayer() != 0){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		if(activeSubs[6]){
			numCurrentSubs++;
			if(u.getPlayer() == 0){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		if(activeSubs[7]){
			numCurrentSubs++;
			if(u.getPlayer() == 1){
				appropriateSubstrates.add(numCurrentSubs);
			}
		}
		for(int appropriateSubstrate : appropriateSubstrates){
			System.out.println("putting unit in sub: " + appropriateSubstrate);
			int indexWithinAll = substrateSize * appropriateSubstrate + location;
			substrates[indexWithinAll] = 1; //maybe change something else that represents what it is
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
					labels[i*pgs.getWidth() + j + pgs.getWidth()*pgs.getHeight()] = "Immobile unit:  (" + i + "," + j + ")";
				}
			}
		}
		return labels;
	}

	@Override
	public int getNumInputSubstrates() {
		return numSubstrates;
	}

}
