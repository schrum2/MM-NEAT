package ch.idsia.tools;

import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.ai.agents.AgentsPool;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 9:05:20 AM
 * Package: .Tools
 */
public class CmdLineOptions extends EvaluationOptions
{
    // TODO: SK Move default options to xml, properties, beans, whatever..
//    private SmartBool gui = new SmartBool();
//    private SmartBool toolsConfigurator = new SmartBool();
//    private SmartBool gameViewer = new SmartBool();
//    private SmartBool gameViewerContinuousUpdates = new SmartBool();
//    private SmartBool timer = new SmartBool();
//    private SmartInt attemptsNumber = new SmartInt();
//    private SmartBool echo = new SmartBool();
//    private SmartBool maxFPS = new SmartBool();
//    private SmartType<String> agentName = new SmartType<String>();
//    private SmartInt serverAgentPort = new SmartInt();
//    private SmartBool serverAgentEnabled = new SmartBool(false);
//    private SmartType<Point> viewLocation = new SmartType<Point>(new Point(0,0));
//    private SmartInt viewLocationX = new SmartInt(0);
//    private SmartInt viewLocationY = new SmartInt(0);
//
//    private SmartBool viewAlwaysOnTop = new SmartBool(false);

    public CmdLineOptions(String[] args)
    {
        super();
        // -agent wox name, like evolvable in simplerace
        // -ll digit  range [5:15], increase if succeeds.
        //TODO Load From File.

//        argsHashMap.put("-ag", agentName.setValue(GlobalOptions.defaults.getAgentName()));
//        argsHashMap.put("-agentName", agentName);
//        argsHashMap.put("-port", serverAgentPort.setValue(GlobalOptions.defaults.getServerAgentPort()));
//        argsHashMap.put("-visual", visualization.setValue(GlobalOptions.VisualizationOn));
//        argsHashMap.put("-vis", visualization);
//        argsHashMap.put("-viewAlwaysOnTop", viewAlwaysOnTop);
//        argsHashMap.put("-vaot", viewAlwaysOnTop);
//        argsHashMap.put("-gui", gui.setValue(GlobalOptions.defaults.isGui()));
//        argsHashMap.put("-levelDifficulty", levelDifficulty.setValue(GlobalOptions.defaults.getLevelDifficulty()));
//        argsHashMap.put("-ld", levelDifficulty);
//        argsHashMap.put("-levelLength", levelLength.setValue(GlobalOptions.defaults.getLevelLength()));
//        argsHashMap.put("-ll", levelLength);
//        argsHashMap.put("-levelType", levelType.setValue(GlobalOptions.defaults.getLevelType()));
//        argsHashMap.put("-lt", levelType);
//        argsHashMap.put("-levelRandSeed", levelRandSeed.setValue(GlobalOptions.defaults.getLevelRandSeed()));
//        argsHashMap.put("-ls", levelRandSeed);
//        argsHashMap.put("-toolsConfigurator", toolsConfigurator.setValue(GlobalOptions.defaults.isToolsConfigurator()) );
//        argsHashMap.put("-tc", toolsConfigurator);
//        argsHashMap.put("-gameViewer", gameViewer.setValue(GlobalOptions.defaults.isGameViewer()));
//        argsHashMap.put("-gv", gameViewer);
//        argsHashMap.put("-gameViewerContinuousUpdates", gameViewerContinuousUpdates.setValue(GlobalOptions.defaults.isGameViewerContinuousUpdates()));
//        argsHashMap.put("-gvc", gameViewerContinuousUpdates);
//        argsHashMap.put("-timer", timer.setValue(GlobalOptions.defaults.isTimer()));
//        argsHashMap.put("-t", timer);
////        argsHashMap.put("-verbose", GlobalOptions.defaults.getVerbose());
//        argsHashMap.put("-attemptsNumber", attemptsNumber.setValue(GlobalOptions.defaults.getAttemptsNumber()));
//        argsHashMap.put("-an", attemptsNumber);
//        argsHashMap.put("-echo", echo.setValue(GlobalOptions.defaults.isEcho()));
//        argsHashMap.put("-e", echo);
//        argsHashMap.put("-maxFPS", maxFPS.setValue(GlobalOptions.defaults.isMaxFPS()));
//        argsHashMap.put("-pw", pauseWorld.setValue(GlobalOptions.defaults.isPauseWorld()));
//        argsHashMap.put("-pauseWorld", pauseWorld);
//        argsHashMap.put("-powerRestoration", powerRestoration.setValue(GlobalOptions.defaults.isPowerRestoration()));
//        argsHashMap.put("-pr", powerRestoration);
//        argsHashMap.put("-stopSimulationIfWin", stopSimulationIfWin.setValue(GlobalOptions.defaults.isStopSimulationIfWin()));
//        argsHashMap.put("-ssiw", stopSimulationIfWin);
//        argsHashMap.put("-exitWhenFinished", exitProgramWhenFinished.setValue(GlobalOptions.defaults.isExitProgramWhenFinished()));
//        argsHashMap.put("-ewf", exitProgramWhenFinished);
//        argsHashMap.put("-viewLocationX", viewLocationX);
//        argsHashMap.put("-viewLocationY", viewLocationY);
//        argsHashMap.put("-vlx", viewLocationX);
//        argsHashMap.put("-vly", viewLocationY);
//        argsHashMap.put("-m", matlabFileName);

        this.setUpOptions(args);
//        System.out.println("args = " + args.length);
        if (args.length == 1)
//        {
            AgentsPool.addAgent(args[0]);
//            System.out.println("length 1");
//        }


        if (isEcho())
        {
            System.out.println("\nOptions have been set to:");
            for (Map.Entry<String,String> el : optionsHashMap.entrySet())
                System.out.println(el.getKey() + ": " + el.getValue());
        }
        GlobalOptions.GameVeiwerContinuousUpdatesOn = isGameViewerContinuousUpdates();        
    }

    public Boolean isToolsConfigurator() {
        return b(getParameterValue("-tc"));      }

    public Boolean isGameViewer() {
        return b(getParameterValue("-gv"));      }

    public Boolean isGameViewerContinuousUpdates() {
        return b(getParameterValue("-gvc"));      }

    public Boolean isEcho() {
        return b(getParameterValue("-echo"));      }


}
