package edu.southwestern.tasks.popacman.ghosts.controllers;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.EnumMap;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.agentcontroller.ghosts.SharedNNGhostsController;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import oldpacman.controllers.NewGhostController;
import pacman.controllers.Controller;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Drawable;
import pacman.game.Game;
import pacman.game.internal.Maze;
import popacman.prediction.PillModel;
import popacman.prediction.fast.GhostPredictionsFast;

/**
 * a class that converts oldpacman controller information into popacman controller information
 * @author pricew
 *
 */
public class OldToNewGhostIntermediaryController extends Controller<EnumMap<GHOST, MOVE>> implements Drawable {

	public final oldpacman.controllers.NewGhostController Network;
	//public final EnumMap<GHOST, IndividualGhostController> controllers;
	public final MASController MASController;
	
	public PillModel pillModel = null;
	public Maze currentMaze;
	public GhostPredictionsFast ghostPredictions = null;
	ArrayList<Integer> eatenPills;
	ArrayList<Integer> eatenPowerPills;
	public int lastPowerPillEatenTime = -1;
	public int lastPillEatenTime = -1;
	public int[] ghostEdibleTime;
	public boolean usePillModel = Parameters.parameters.booleanParameter("usePillModel");
	public boolean useGhostModel = Parameters.parameters.booleanParameter("useGhostModel");
	public final double GHOST_THRESHOLD = Parameters.parameters.doubleParameter("probabilityThreshold");
	
	private final static GHOST BLINKY = pacman.game.Constants.GHOST.BLINKY;
	private final static GHOST PINKY = pacman.game.Constants.GHOST.PINKY;
	private final static GHOST INKY = pacman.game.Constants.GHOST.INKY;
	private final static GHOST SUE = pacman.game.Constants.GHOST.SUE;
	
	private final ZombieGhost zombieBlinky;
	private final ZombieGhost zombiePinky;
	private final ZombieGhost zombieInky;
	private final ZombieGhost zombieSue;
	
	
    public OldToNewGhostIntermediaryController(SharedNNGhostsController controller) {
    	//Instanitate the EnumMap for the MASController
    	EnumMap<GHOST, IndividualGhostController> map = new EnumMap<GHOST, IndividualGhostController>(pacman.game.Constants.GHOST.class);
    	
    	//Instantiate the zombie controllers
    	this.zombieBlinky = new ZombieGhost(BLINKY);
    	this.zombiePinky = new ZombieGhost(PINKY);
    	this.zombieInky = new ZombieGhost(INKY);
    	this.zombieSue = new ZombieGhost(SUE);
    	
    	//Put the zombie controllers in the map
    	map.put(BLINKY, zombieBlinky);
    	map.put(PINKY, zombiePinky);
    	map.put(INKY, zombieInky);
    	map.put(SUE, zombieSue);
    	
    	MASController = new MASController(map);
    	this.Network = (NewGhostController) controller;
	}

    
	private EnumMap<GHOST, MOVE> convertMoveMapOldToPO(EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> move) {
		EnumMap<pacman.game.Constants.GHOST, pacman.game.Constants.MOVE> result = new EnumMap<pacman.game.Constants.GHOST, pacman.game.Constants.MOVE>(pacman.game.Constants.GHOST.class);
    	for(oldpacman.game.Constants.GHOST g : move.keySet()) {
    		result.put(ghostConverterOldToPO(g), moveConverterOldToPO(move.get(g)));
    	}
		return result;
	}
    
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue){
    	EnumMap<GHOST, MOVE> map = convertMoveMapOldToPO(this.Network.getMove());
    	for(GHOST g : map.keySet()) {
    		switch(g) {
    			case BLINKY:
    				zombieBlinky.setMove(map.get(g));
    				break;
    			case PINKY:
    				zombiePinky.setMove(map.get(g));
    				break;
    			case INKY:
    				zombieInky.setMove(map.get(g));
    				break;
    			case SUE:
    				zombieSue.setMove(map.get(g));
    				break;
    			default:
    				throw new UnsupportedOperationException("invalid ghost");    				
    		}	
    	}
    	
    	return convertMoveMapOldToPO(this.Network.getMove());
    }

	
	/**
	 * Takes an oldpacman move and returns the equivalent popacman move
	 * @param move a move of the older pacman move enumeration
	 * @return the equivalent move of the po pacman move enumeration
	 * @throws NoSuchFieldException
	 * @author pricew
	 */
	public static pacman.game.Constants.MOVE moveConverterOldToPO(oldpacman.game.Constants.MOVE move){
		switch(move) {
			case NEUTRAL:
				return pacman.game.Constants.MOVE.NEUTRAL;
			case UP:
				return pacman.game.Constants.MOVE.UP;
			case LEFT:
				return pacman.game.Constants.MOVE.LEFT;
			case DOWN:
				return pacman.game.Constants.MOVE.DOWN;
			case RIGHT:
				return pacman.game.Constants.MOVE.RIGHT;
			default:
				throw new UnsupportedOperationException("invalid parameter move");
		}
	}
	
	/**
	 * 
	 * @param ghost a ghost of the older pacman code ghost enumeration
	 * @return the equvalent ghost of the po pacman ghost enumeration
	 * @author pricew
	 */
	public static pacman.game.Constants.GHOST ghostConverterOldToPO(oldpacman.game.Constants.GHOST ghost){
		switch(ghost) {
		case BLINKY:
			return pacman.game.Constants.GHOST.BLINKY;
		case PINKY:
			return pacman.game.Constants.GHOST.PINKY;
		case INKY:
			return pacman.game.Constants.GHOST.INKY;
		case SUE:
			return pacman.game.Constants.GHOST.SUE;
		default:
			throw new UnsupportedOperationException("invalid parameter ghost");
		}
	}
	
    public void draw(Graphics2D graphics) {

    }

    //determines whether or not to use this classes draw method
	public boolean enabled() {
		return false;
	}
	
	
	public GameFacade updateModels(Game game, long timeDue) {
        //If we switched mazes, we need new models
		if(currentMaze != game.getCurrentMaze()){
//            currentMaze = game.getCurrentMaze();
//            ghostPredictions = null;
//            pillModel = null;
//            lastPillEatenTime = -1;
//            lastPowerPillEatenTime = -1;
//            Arrays.fill(ghostEdibleTime, -1);
        }
		
		if(game.wasPacManEaten()) {
//            lastPillEatenTime = -1;
//            lastPowerPillEatenTime = -1;
		}
		      
        //We need to pass the model of the game to the new gameFacade
		GameFacade informedGameFacade = new GameFacade(game);
		//we need to keep track of the mostRecenGame to pass to the draw method
		//mostRecentGame = game;
		
		
		
//		//PILL MODEL CODE
//		if(usePillModel) {
//			if(pillModel == null) {
//				
//				//Piers' Code
//				pillModel = new PillModel(game.getNumberOfPills());
//	            int[] pillIndices = informedGameFacade.poG.getCurrentMaze().pillIndices;
//	            for (int index : pillIndices) {
//	                pillModel.observePill(index, true);
//	            }
//	            //My code
//	            int[] powerPillIndices = informedGameFacade.poG.getCurrentMaze().powerPillIndices;
//	            for (int index : powerPillIndices){
//	            	pillModel.observePowerPill(index, true);
//	            }
//	                    
//	            eatenPills = new ArrayList<Integer>();
//	            eatenPowerPills = new ArrayList<Integer>();
//	            
//	            informedGameFacade.setPillModel(pillModel);
//	            informedGameFacade.setTimeOfLastPowerPillEaten(lastPowerPillEatenTime);
//	            informedGameFacade.setTimeOfLastPillEaten(lastPillEatenTime);
//	        
//			} 
//
//			assert pillModel != null : "there is an if that checks it above. A null pillModel would break this code";
//			
//			//tell the game what the pill model looks like
//	        int pillIndex = informedGameFacade.poG.getPillIndex(informedGameFacade.poG.getPacmanCurrentNodeIndex());				        
//			if (pillIndex != -1) {
//	            Boolean pillState = informedGameFacade.poG.isPillStillAvailable(pillIndex);
//	            if (pillState != null && !pillState) {
//	            	pillModel.update(informedGameFacade.poG.getPacmanCurrentNodeIndex());
//	            	eatenPills.add(informedGameFacade.poG.getPacmanCurrentNodeIndex());
//	            	lastPillEatenTime = informedGameFacade.getCurrentLevelTime();
//	            	informedGameFacade.setTimeOfLastPillEaten(informedGameFacade.getCurrentLevelTime());
//	            }
//	        }
//		    
//			int powerPillIndex = informedGameFacade.poG.getPowerPillIndex(informedGameFacade.poG.getPacmanCurrentNodeIndex());				        
//			if (powerPillIndex != -1) {
//		        Boolean powerPillState = informedGameFacade.poG.isPowerPillStillAvailable(powerPillIndex);
//		        if (powerPillState != null && !powerPillState) {
//		        	pillModel.update(informedGameFacade.poG.getPacmanCurrentNodeIndex());
//		        	eatenPowerPills.add(informedGameFacade.poG.getPacmanCurrentNodeIndex());
//		        	lastPowerPillEatenTime = informedGameFacade.getCurrentLevelTime();
//		        	informedGameFacade.setTimeOfLastPowerPillEaten(informedGameFacade.getCurrentLevelTime());
//		        }
//			}
//	        
//			informedGameFacade.setTimeOfLastPillEaten(lastPillEatenTime);
//			informedGameFacade.setTimeOfLastPowerPillEaten(lastPowerPillEatenTime);
//	        informedGameFacade.setPillModel(pillModel);
//		}
//		
//		
//		//GHOST MODEL CODE
//		if(useGhostModel) {
//			//if pacman was eaten, ghosts are in the lair
//			if (informedGameFacade.poG.wasPacManEaten()) {
//	            ghostPredictions = null;
//	        }
//			
//			//init the ghost predictions
//			if(ghostPredictions == null){				
//				//create a new ghostPredictionFast object, initialize it, and pass it to informedGameFacade
//				ghostPredictions = new GhostPredictionsFast(informedGameFacade.poG.getCurrentMaze());
//	            ghostPredictions.preallocate();
//	            informedGameFacade.setGhostPredictions(this.ghostPredictions);
//			} 
//	        
//			assert ghostPredictions != null : "it should be set by now";
//			
//			// Get observations of ghosts and pass them in to the predictor
//			//Credit to piers InformationSetMCTSPacmMan.java, cited 6/4/18
//	        for (GHOST ghost : GHOST.values()) {
//	            if (ghostEdibleTime[ghost.ordinal()] != -1) {
//	                ghostEdibleTime[ghost.ordinal()]--;
//	            }
//
//	            int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
//	            if (ghostIndex != -1) {
//	            	try {
//	            		ghostPredictions.observe(ghost, ghostIndex, informedGameFacade.poG.getGhostLastMoveMade(ghost), informedGameFacade);
//	            	} catch (java.lang.ArrayIndexOutOfBoundsException e) {
//	            		//System.out.println(e.toString() + " in OldToNewPacManIntermediaryController.updateModels()");
//	            		break;
//	            	}
//	                ghostEdibleTime[ghost.ordinal()] = game.getGhostEdibleTime(ghost);
//	            } else {
//	                List<GhostLocation> locations = ghostPredictions.getGhostLocations(ghost);
//	                locations.stream().filter(location -> informedGameFacade.poG.isNodeObservable(location.getIndex())).forEach(location -> {
//	                	ghostPredictions.observeNotPresent(ghost, location.getIndex(), informedGameFacade);
//	                });
//	            }
//	        }
//			
//	        ghostPredictions.update();
//	        informedGameFacade.setGhostPredictions(ghostPredictions);
//		}
		
		return informedGameFacade;
	}

}
