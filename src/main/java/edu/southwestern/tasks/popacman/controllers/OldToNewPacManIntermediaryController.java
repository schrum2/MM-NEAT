package edu.southwestern.tasks.popacman.controllers;

import static pacman.game.Constants.MAG;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
//TODO: this could be useful
import pacman.game.info.GameInfo;
import pacman.game.internal.Maze;
import pacman.game.Drawable;
import pacman.game.Game;
import popacman.prediction.GhostLocation;
import popacman.prediction.PillModel;
import popacman.prediction.fast.GhostPredictionsFast;

/**
 * a class that converts oldpacman controller information into popacman controller information
 * @author pricew
 *
 */
public class OldToNewPacManIntermediaryController extends pacman.controllers.PacmanController implements Drawable {

	protected final oldpacman.controllers.NewPacManController oldpacman;
	public PillModel pillModel = null;
	public Maze currentMaze;
	public GhostPredictionsFast ghostPredictions = null;
	ArrayList<Integer> eatenPills;
	ArrayList<Integer> eatenPowerPills;
	public int lastPowerPillEatenTime = -1;
	public int lastPillEatenTime = -1;
	public int[] ghostEdibleTime;
    private Color[] redAlphas;
    private Game mostRecentGame;
	public boolean usePillModel = Parameters.parameters.booleanParameter("usePillModel");
	public boolean useGhostModel = Parameters.parameters.booleanParameter("useGhostModel");
	public final double GHOST_THRESHOLD = Parameters.parameters.doubleParameter("probabilityThreshold");
	
	public OldToNewPacManIntermediaryController(oldpacman.controllers.NewPacManController oldpacman) {
		this.oldpacman = oldpacman;
		
        redAlphas = new Color[256];
        for (int i = 0; i < 256; i++) {
            redAlphas[i] = new Color(255, 0, 0, i);
        }
        
        ghostEdibleTime = new int[GHOST.values().length];
	}
	

	@Override
	/**
	 * This method returns a popacman move. 
	 * Used for popacman
	 */
	public MOVE getMove(Game game, long timeDue) {
        
		
		GameFacade informedGameFacade = updateModels(game, timeDue);

		//get the action to be made
		int action = oldpacman.getAction(informedGameFacade, timeDue);
		//converts an action to an oldpacman move to a popacman move to be returned
		return moveConverterOldToPO(GameFacade.indexToMove(action));
	}
	
	//This method clears all stored information about a game
	public void clearStoredInformation() {
		pillModel = null;
		ghostPredictions = null;
	}
	
	/**
	 * Takes an oldpacman move and returns the equivalent popacman move
	 * @param move
	 * @return
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
				System.out.println("ERROR in moveConverterOldPO, GAmeFacade.java");
				return null;
		}
	}
	
	//credit to piers InformationSetMCTSPacMan.java
	//draws the red blocks representing a predicted ghost location
    public void draw(Graphics2D graphics) {
    	   	
    	//Draw Pill Model based on parameter
    	if(Parameters.parameters.booleanParameter("drawPillModel")) {
    		
    		
    		for (int i = 0; i < mostRecentGame.getNumberOfNodes(); i++) {
    			boolean isPillAvailable = pillModel.getPills().get(mostRecentGame.getCurrentMaze().graph[i].nodeIndex);
		    		if(isPillAvailable) {
		    			if(!eatenPills.contains(mostRecentGame.getCurrentMaze().graph[i].nodeIndex)) {
			    			graphics.setColor(new Color(255, 0, 0, 255));
			    			graphics.fillRect(
			    					mostRecentGame.getNodeXCood(i) * MAG + 5,
			    					mostRecentGame.getNodeYCood(i) * MAG + 8,
			    					2, 2
			    					);	
		    			}
		    		}
    		}
    		
    		
    		for (int i = 0; i < mostRecentGame.getNumberOfNodes(); i++) {
    			boolean isPillAvailable = pillModel.getPowerPills().get(mostRecentGame.getCurrentMaze().graph[i].nodeIndex);
		    		if(isPillAvailable) {
		    			if(!eatenPowerPills.contains(mostRecentGame.getCurrentMaze().graph[i].nodeIndex)) {
			    			graphics.setColor(new Color(255, 0, 0, 255));
			    			graphics.fillRect(
			    					mostRecentGame.getNodeXCood(i) * MAG + 5,
			    					mostRecentGame.getNodeYCood(i) * MAG + 8,
			    					4, 4
			    					);	
		    			}
		    		}
    		}
    		
    		//DRAWS PILLS WE CAN SEE
	        for(int pill : mostRecentGame.getActivePillsIndices()) {
	        	assert pillModel != null : "why is this null";
	        	assert pillModel.getPills() != null : "why is this null";
	    		boolean isPillAvailable = pillModel.getPills().get(pill);
	    		if(isPillAvailable) {
	    			graphics.setColor(new Color(0, 0, 255, 255));
	    			graphics.fillRect(
	    					mostRecentGame.getNodeXCood(pill) * MAG + 4,
	    					mostRecentGame.getNodeYCood(pill) * MAG + 12,
	    					2, 2
	    					);
	    		}
	        
	        }
    	}
 
    		
    	//Draw predicted Ghost Locations based on parameter
    	if(Parameters.parameters.booleanParameter("drawGhostPredictions")) {
    		if(ghostPredictions != null) {
	        	for (int i = 0; i < mostRecentGame.getNumberOfNodes(); i++) {      	
	                double probability = ghostPredictions.calculate(i);
	                if (probability >= GHOST_THRESHOLD) {
	                	graphics.setColor(redAlphas[(int) Math.min(255 * probability, 255)]);
	                    graphics.fillRect(
	                            mostRecentGame.getNodeXCood(i) * MAG - 1,
	                            mostRecentGame.getNodeYCood(i) * MAG + 3,
	                            14, 14
	                    );
	                }
	            }
    		}
       	}
    }

    //determines whether or not to use this classes draw method
	public boolean enabled() {
		if(Parameters.parameters.booleanParameter("drawGhostPredictions") || Parameters.parameters.booleanParameter("drawPillModel")) {
			return true;
		}
		return false;
	}
	
	
	public GameFacade updateModels(Game game, long timeDue) {
        //If we switched mazes, we need new models
		if(currentMaze != game.getCurrentMaze()){
            currentMaze = game.getCurrentMaze();
            ghostPredictions = null;
            pillModel = null;
            lastPillEatenTime = -1;
            lastPowerPillEatenTime = -1;
            Arrays.fill(ghostEdibleTime, -1);
        }
		
		if(game.wasPacManEaten()) {
            lastPillEatenTime = -1;
            lastPowerPillEatenTime = -1;
		}
		      
        //We need to pass the model of the game to the new gameFacade
		GameFacade informedGameFacade = new GameFacade(game);
		//we need to keep track of the mostRecenGame to pass to the draw method
		mostRecentGame = game;
		
		
		
		//PILL MODEL CODE
		if(usePillModel) {
			if(pillModel == null) {
				
				//Piers' Code
				pillModel = new PillModel(game.getNumberOfPills());
	            int[] pillIndices = informedGameFacade.poG.getCurrentMaze().pillIndices;
	            for (int index : pillIndices) {
	                pillModel.observePill(index, true);
	            }
	            //My code
	            int[] powerPillIndices = informedGameFacade.poG.getCurrentMaze().powerPillIndices;
	            for (int index : powerPillIndices){
	            	pillModel.observePowerPill(index, true);
	            }
	                    
	            eatenPills = new ArrayList<Integer>();
	            eatenPowerPills = new ArrayList<Integer>();
	            
	            informedGameFacade.setPillModel(pillModel);
	            informedGameFacade.setTimeOfLastPowerPillEaten(lastPowerPillEatenTime);
	            informedGameFacade.setTimeOfLastPillEaten(lastPillEatenTime);
	        
			} 

			assert pillModel != null : "there is an if that checks it above. A null pillModel would break this code";
			
			//tell the game what the pill model looks like
	        int pillIndex = informedGameFacade.poG.getPillIndex(informedGameFacade.poG.getPacmanCurrentNodeIndex());				        
			if (pillIndex != -1) {
	            Boolean pillState = informedGameFacade.poG.isPillStillAvailable(pillIndex);
	            if (pillState != null && !pillState) {
	            	pillModel.update(informedGameFacade.poG.getPacmanCurrentNodeIndex());
	            	eatenPills.add(informedGameFacade.poG.getPacmanCurrentNodeIndex());
	            	lastPillEatenTime = informedGameFacade.getCurrentLevelTime();
	            	informedGameFacade.setTimeOfLastPillEaten(informedGameFacade.getCurrentLevelTime());
	            }
	        }
		    
			int powerPillIndex = informedGameFacade.poG.getPowerPillIndex(informedGameFacade.poG.getPacmanCurrentNodeIndex());				        
			if (powerPillIndex != -1) {
		        Boolean powerPillState = informedGameFacade.poG.isPowerPillStillAvailable(powerPillIndex);
		        if (powerPillState != null && !powerPillState) {
		        	pillModel.update(informedGameFacade.poG.getPacmanCurrentNodeIndex());
		        	eatenPowerPills.add(informedGameFacade.poG.getPacmanCurrentNodeIndex());
		        	lastPowerPillEatenTime = informedGameFacade.getCurrentLevelTime();
		        	informedGameFacade.setTimeOfLastPowerPillEaten(informedGameFacade.getCurrentLevelTime());
		        }
			}
	        
			informedGameFacade.setTimeOfLastPillEaten(lastPillEatenTime);
			informedGameFacade.setTimeOfLastPowerPillEaten(lastPowerPillEatenTime);
	        informedGameFacade.setPillModel(pillModel);
		}
		
		
		//GHOST MODEL CODE
		if(useGhostModel) {
			//if pacman was eaten, ghosts are in the lair
			if (informedGameFacade.poG.wasPacManEaten()) {
	            ghostPredictions = null;
	        }
			
			//init the ghost predictions
			if(ghostPredictions == null){				
				//create a new ghostPredictionFast object, initialize it, and pass it to informedGameFacade
				ghostPredictions = new GhostPredictionsFast(informedGameFacade.poG.getCurrentMaze());
	            ghostPredictions.preallocate();
	            informedGameFacade.setGhostPredictions(this.ghostPredictions);
			} 
	        
			assert ghostPredictions != null : "it should be set by now";
			
			// Get observations of ghosts and pass them in to the predictor
			//Credit to piers InformationSetMCTSPacmMan.java, cited 6/4/18
	        for (GHOST ghost : GHOST.values()) {
	            if (ghostEdibleTime[ghost.ordinal()] != -1) {
	                ghostEdibleTime[ghost.ordinal()]--;
	            }

	            int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
	            if (ghostIndex != -1) {
	            	try {
	            		ghostPredictions.observe(ghost, ghostIndex, informedGameFacade.poG.getGhostLastMoveMade(ghost));
	            	} catch (java.lang.ArrayIndexOutOfBoundsException e) {
	            		//System.out.println(e.toString() + " in OldToNewPacManIntermediaryController.updateModels()");
	            		break;
	            	}
	                ghostEdibleTime[ghost.ordinal()] = game.getGhostEdibleTime(ghost);
	            } else {
	                List<GhostLocation> locations = ghostPredictions.getGhostLocations(ghost);
	                locations.stream().filter(location -> informedGameFacade.poG.isNodeObservable(location.getIndex())).forEach(location -> {
	                	ghostPredictions.observeNotPresent(ghost, location.getIndex());
	                });
	            }
	        }
			
	        ghostPredictions.update();
	        informedGameFacade.setGhostPredictions(ghostPredictions);
		}
		
		return informedGameFacade;
	}


}
