package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.PopulationUtil;
import micro.rts.GameState;
import micro.rts.units.Unit;

/**
 * Evaluation Function for MicroRTS that puts different unit-classes
 * onto their own substrates, according to parameters (TODO it doesn't do that yet)
 * 
 * @author alicequint
 * 
 * unfinished, eventually different substrate for each unit-type maybe.
 */
public class NNComplexEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {
	
	private final int numSubstrates = 2; //TODO generalize so that everything can go well
	
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
				boardIndex =  i + j * pgs.getHeight(); 
				current = pgs.getUnitAt(i, j);
				if(current!= null){
					inputs = populateSubstratesWith(current, inputs, substrateSize, boardIndex);
				}
			}
		}
		return inputs;
	}
	
	/**
	 * puts the current unit into all substrates where it should go
	 * according to parameters.
	 * 
	 * @param substrates
	 * 				array containing all substrates
	 * @param index
	 * 				place within the substrate where
	 */
	private double[] populateSubstratesWith(Unit u, double[] substrates, int substrateSize, int indexWithinSingleSubstrate){
		int appropriateSubstrate = -1; //decide this based on qualities that the unit has & parameters, TODO
		int indexWithinAll = substrateSize * appropriateSubstrate+ indexWithinSingleSubstrate;
		substrates[indexWithinAll] = u.getHitPoints(); //or something else that represents what it is
		return substrates;
	}

	/**
	 * returns labels describing what gameStateToArray will
	 * give for the inputs to a NN
	 */
	@Override
	public String[] sensorLabels() {
		assert pgs != null : "There must be a physical game state in order to extract height and width";
		String[] labels = new String[pgs.getWidth()*pgs.getHeight() * 2];
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				labels[i*pgs.getWidth() + j ] = "Mobile unit:  (" + i + ", " + j + ")";
				labels[i*pgs.getWidth() + j + pgs.getWidth()*pgs.getHeight()] = "Immobile unit:  (" + i + "," + j + ")";
			}
		}
		return labels;
	}

}
