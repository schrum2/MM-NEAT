package edu.utexas.cs.nn.tasks.ut2004;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.ut2004.controller.BotController;
import edu.utexas.cs.nn.tasks.ut2004.fitness.*;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 *
 * @author Jacob Schrum
 */
public class UT2004OneVsGBBotsDeathMatchTask<T extends Network> extends UT2004Task<T> {

    public UT2004OneVsGBBotsDeathMatchTask() {
        this(Parameters.parameters.stringParameter("utMap"),
                getOpponents(Parameters.parameters.integerParameter("utNumOpponents")),
                Parameters.parameters.integerParameter("utEvalMinutes"),
                Parameters.parameters.integerParameter("utEvolvingBotSkill"));
    }

    public static BotController[] getOpponents(int num) {
        BotController[] result = new BotController[num];
        try {
            for (int i = 0; i < num; i++) {
                result[i] = (BotController) ClassCreation.createObject("utGameBotsOpponent");
            }
        } catch (NoSuchMethodException ex) {
            System.out.println("Could not load bot opponents");
            ex.printStackTrace();
            System.exit(1);
        }
        return result;
    }

    public UT2004OneVsGBBotsDeathMatchTask(String map, BotController[] opponents, int evalMinutes, int desiredSkill) {
        super(map, new int[0], evalMinutes, desiredSkill, opponents);
        // Fitness objectives
        addObjective(new DamageDealtFitness<T>(), fitness, true);
        addObjective(new DamageReceivedFitness<T>(), fitness, true);
        // Other stats to track
        addObjective(new FragFitness<T>(), others, false);
        addObjective(new DeathsFitness<T>(), others, false);
        addObjective(new ScoreFitness<T>(), others, false);
        addObjective(new HighestEnemyScoreFitness<T>(), others, false);
        addObjective(new StreakFitness<T>(), others, false);

        System.out.println("Fitness:" + fitness);
        System.out.println("Other Scores:" + others);
    }
}
