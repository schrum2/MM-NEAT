package edu.utexas.cs.nn.tasks.microrts;

import java.util.Arrays;

import org.python.modules.newmodule;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;
import micro.rts.units.UnitType;
import micro.rts.units.UnitTypeTable;

public class NNEvaluationFunction<T extends Network> extends EvaluationFunction{
	
	Network nn;
	//score modifier for these categories
	public static float RESOURCE_IN_BASE = 20; //UnitAction defines resource to not include unharvested resources
    public static float RESOURCE_IN_WORKER = 10;
    public static float UNIT_BONUS_MULTIPLIER = 40.0f;
    public static float BUILDING_MULTIPLIER = -1;
    static UnitTypeTable utt = new UnitTypeTable(); //static and initialized so that Main can access it for making a GameState
    
    public NNEvaluationFunction(Genotype<T> g){
    	nn = g.getPhenotype();
//    	utt = new UnitTypeTable();
    }
    
	/**
	 * @param maxplayer - player to be evaluated
	 * @param minplayer - opponent
	 * @param gs - specified state of the game
	 * @return number from -1 to 1 depending on if and how hard evaluated player is winning/losing
	 */
	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		double[] board = gameStateToArray(gs);
		float score = 0;
		for(double current : board){
			
		}
		System.out.println(Arrays.toString(board));
		return score;
	}
	
	private double[] gameStateToArray(GameState gs){
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[] board = new double[pgs.getHeight()*pgs.getWidth()];
		int boardIndex;
		Unit currentUnit;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				boardIndex = i * pgs.getWidth() + j;
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					String currentUnitName = currentUnit.getType().name;
					switch(currentUnitName){
					case "Worker": board[boardIndex] = 1; break;
					case "Light": board[boardIndex] = 2; break;
					case "Heavy": board[boardIndex] = 3; break;
					case "Ranged": board[boardIndex] = 4; break;
					case "Base": board[boardIndex] = 10; break;
					case "Barracks": board[boardIndex] = 20; break;
					case "Resource": board[boardIndex] = 100; break;
					default: break;
					}
					board[boardIndex] += .01 * currentUnit.getResources();
					if(currentUnit.getPlayer() == 1) board[boardIndex] *= -1; 
				}
			}//end inner loop
		}//end outer loop
		return board;
	}

	/**
	 * maximum possible thing returned by the evaluation function
	 */
	@Override
	public float upperBound(GameState gs) {
        return 1f;
	}
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false",
				"task:edu.utexas.cs.nn.tasks.microrts.MicroRTSTask"});
		MMNEAT.loadClasses();
		TWEANNGenotype individual = new TWEANNGenotype();
		NNEvaluationFunction<TWEANN> ef = new NNEvaluationFunction<TWEANN>(individual);
		
		PhysicalGameState testpgs = new PhysicalGameState(10, 10);
		//public Unit(int a_player, UnitType a_type, int a_x, int a_y, int a_resources)
		
		testpgs.addUnit( new Unit(0, utt.getUnitType("Worker"), 0, 1, 0));
		testpgs.addUnit( new Unit(0, utt.getUnitType("Light"), 0, 2, 0));
		testpgs.addUnit( new Unit(0, utt.getUnitType("Heavy"), 0, 3, 0));
		testpgs.addUnit( new Unit(0, utt.getUnitType("Ranged"), 0, 4, 0));
//		testpgs.addUnit( new Unit(0, utt.getUnitType("Resource"), 0, 5, 0));
//		testpgs.addUnit( new Unit(0, utt.getUnitType("Resource"), 0, 6, 0));
		
		GameState gs = new GameState(testpgs, utt);
		System.out.println(ef.evaluate(0, 1, gs));
	}
}
