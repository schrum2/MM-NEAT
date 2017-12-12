package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.ToolsConfigurator;
import competition.cig.robinbaumgarten.AStarAgent;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) {
        Agent controller = new HumanKeyboardAgent();
        if (args.length > 0) {
            controller = AgentsPool.load (args[0]);
            AgentsPool.addAgent(controller);
        }
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        
        LevelScene.twoPlayers = true; // TODO: find better way to set this
        if(LevelScene.twoPlayers) {
            // Swap: A* must be player 1, and human is player 2.
        	// Variable juggling required because variable controller sent to evaluate call below.
        	Agent astar = new AStarAgent();
        	Agent human = controller;
        	controller = astar;
        	options.setAgent(controller);
            options.setAgent2(human);
        } else {
            options.setAgent(controller);
        }
        
        Task task = new ProgressTask(options);
        options.setMaxFPS(false);
        options.setVisualization(true);
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        options.setLevelRandSeed((int) (Math.random () * Integer.MAX_VALUE));
        options.setLevelDifficulty(3);
        task.setOptions(options);

        System.out.println ("Score: " + task.evaluate(controller)[0]);
        
        ToolsConfigurator.DestroyMarioComponentFrame();
    }
}
