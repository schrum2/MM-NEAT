 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.tests;

import micro.ai.core.AI;
import micro.ai.*;
import micro.ai.core.ContinuingAI;
import micro.ai.evaluation.SimpleEvaluationFunction;
import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.gui.MouseController;
import micro.gui.PhysicalGameStateMouseJFrame;
import micro.gui.PhysicalGameStatePanel;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.PlayerAction;
import micro.rts.units.UnitTypeTable;

/**
 *
 * @author santi
 */
public class PlayGameWithMouseTest {
    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml", utt);

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 10000;
        int PERIOD = 100;
        boolean gameover = false;
                
        PhysicalGameStatePanel pgsp = new PhysicalGameStatePanel(gs);
        PhysicalGameStateMouseJFrame w = new PhysicalGameStateMouseJFrame("Game State Visuakizer (Mouse)",640,640,pgsp);
//        PhysicalGameStateMouseJFrame w = new PhysicalGameStateMouseJFrame("Game State Visuakizer (Mouse)",400,400,pgsp);

        AI ai1 = new MouseController(w);
//        AI ai2 = new PassiveAI();
//        AI ai2 = new RandomBiasedAI();
//        AI ai2 = new LightRush(utt, new AStarPathFinding());
        AI ai2 = new ContinuingAI(new NaiveMCTS(PERIOD, -1, 100, 20, 0.33f, 0.0f, 0.75f, new RandomBiasedAI(), new SimpleEvaluationFunction(), true));

        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do{
            if (System.currentTimeMillis()>=nextTimeToUpdate) {
                PlayerAction pa1 = ai1.getAction(0, gs);
                PlayerAction pa2 = ai2.getAction(1, gs);
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

                // simulate:
                gameover = gs.cycle();
                w.repaint();
                nextTimeToUpdate+=PERIOD;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }while(!gameover && gs.getTime()<MAXCYCLES);
        
        System.out.println("Game Over");
    }    
}
