package ch.idsia.mario.simulation;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.tools.EvaluationInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 7, 2009
 * Time: 2:27:48 PM
 * Package: .Simulation
 */

public class BasicSimulator implements Simulation
{
    SimulationOptions simulationOptions = null;
    private MarioComponent marioComponent;

    public BasicSimulator(SimulationOptions simulationOptions)
    {
        GlobalOptions.VisualizationOn = simulationOptions.isVisualization();
        this.marioComponent = GlobalOptions.getMarioComponent();
        this.setSimulationOptions(simulationOptions);
    }

    private MarioComponent prepareMarioComponent()
    {
        Agent agent = simulationOptions.getAgent();
        agent.reset();
        marioComponent.setAgent(agent);
        if(LevelScene.twoPlayers) {
        	Agent agent2 = simulationOptions.getAgent2();
        	agent2.reset();
        	marioComponent.setAgent2(agent2);        	
        }
        return marioComponent;
    }

    public void setSimulationOptions(SimulationOptions simulationOptions)
    {
        this.simulationOptions = simulationOptions;
    }

    public EvaluationInfo simulateOneLevel()
    {
        Mario.setDefaultStart(simulationOptions.getMarioMode());        
        prepareMarioComponent();
        marioComponent.setZLevelScene(simulationOptions.getZLevelMap());
        marioComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
        // This first option is the default
        if(simulationOptions.level == null) {
	        marioComponent.startLevel(simulationOptions.getLevelRandSeed(), simulationOptions.getLevelDifficulty()
	                                 , simulationOptions.getLevelType(), simulationOptions.getLevelLength(),
	                                  simulationOptions.getTimeLimit());
        } else { // Provided alternate way to specify level in advance
	        marioComponent.startLevel(simulationOptions.level, 
	        		simulationOptions.getLevelRandSeed(), // Might still need for picking enemies
	        		simulationOptions.getLevelType(),
                    simulationOptions.getTimeLimit());
        }
        marioComponent.setPaused(simulationOptions.isPauseWorld());
        marioComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
        marioComponent.setZLevelScene(simulationOptions.getZLevelMap());
        marioComponent.setMarioInvulnerable(simulationOptions.isMarioInvulnerable());
        return marioComponent.run1(SimulationOptions.currentTrial++,
                simulationOptions.getNumberOfTrials()
        );
    }
}
