package edu.utexas.cs.nn.tasks.microrts;

import java.util.Arrays;

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

	public NNEvaluationFunction(Genotype<T> g){
		nn = g.getPhenotype();
	}

	/**
	 * @param maxplayer - player to be evaluated
	 * @param minplayer - opponent
	 * @param gs - specified state of the game
	 * @return number from -1 to 1 depending on if and how hard evaluated player is winning/losing
	 */
	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		double[] rawInputs = gameStateToArray(gs);
		double[] outputs = nn.process(rawInputs);
		float score = (float) outputs[0];
		System.out.println(Arrays.toString(rawInputs));
		return score;
	}

	/**
	 * counts the number of each unit belonging to each player
	 * @param gs current game state
	 * @return	array containing{#workers, #lights, #heavies, #ranged-units, #bases, #barracks, #enemy-workers, 
	 * 							#enemy-lights, #enemy-heavies, #enemy-ranged-units, #enemy-bases #enemy-barracks}  
	 */
	private double[] gameStateToArray(GameState gs){
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[] unitsOnBoard = new double[14];
		Unit currentUnit;
		int playerAdjustment;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : 6; //shift enemy units +6 in the array
					switch(currentUnit.getType().name){
					case "Worker": unitsOnBoard[0 + playerAdjustment]++; break;
					case "Light": unitsOnBoard[1 + playerAdjustment]++; break;
					case "Heavy": unitsOnBoard[2 + playerAdjustment]++; break;
					case "Ranged": unitsOnBoard[3 + playerAdjustment]++; break;
					case "Base": unitsOnBoard[4 + playerAdjustment]++; break;
					case "Barracks": unitsOnBoard[5 + playerAdjustment]++; break;
					default: break;
					}
				}
			}
		}
		return unitsOnBoard;
	}
	
	/**
	 * converts GameState into Array containing information about the contents of every tile
	 * 			to the detail of what type of unit occupies it and amount of
	 * 			resources that unit has. To be used in more advanced versions of this code
	 * @param gs gamestate to be turned into array
	 * @return Array of coded info
	 */
	public double[] deepGameStateToArray(GameState gs){
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
					case "Base": board[boardIndex] = 11; break;
					case "Barracks": board[boardIndex] = 20; break;
					case "Resource": board[boardIndex] = 10; break;
					default: break;
					}
					board[boardIndex] += .01 * currentUnit.getResources();
					if(currentUnit.getPlayer() == 1) board[boardIndex] *= -1; 
				}
			}
		}
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
		UnitTypeTable utt = new UnitTypeTable();
		PhysicalGameState testpgs = new PhysicalGameState(10, 10);
		
		//public Unit(int a_player, UnitType a_type, int a_x, int a_y, int a_resources)
		testpgs.addUnit( new Unit(0, utt.getUnitType("Worker"), 0, 1, 1));
		testpgs.addUnit( new Unit(1, utt.getUnitType("Light"), 0, 2, 0));
		testpgs.addUnit( new Unit(1, utt.getUnitType("Light"), 1, 2, 0));
		testpgs.addUnit( new Unit(1, utt.getUnitType("Light"), 2, 2, 0));
		testpgs.addUnit( new Unit(0, utt.getUnitType("Heavy"), 0, 3, 0));
		testpgs.addUnit( new Unit(1, utt.getUnitType("Ranged"), 0, 4, 0));
		testpgs.addUnit( new Unit(0, utt.getUnitType("Barracks"), 3, 4, 0));
		testpgs.addUnit( new Unit(0, utt.getUnitType("Base"), 4, 4, 0));
		testpgs.addUnit( new Unit(-1, utt.getUnitType("Resource"), 0, 5, 0));
		testpgs.addUnit( new Unit(-1, utt.getUnitType("Resource"), 0, 6, 26));

		GameState gs = new GameState(testpgs, utt);
		ef.evaluate(0, 1, gs);
		ef.evaluate(0, 1, gs);
	}
}
