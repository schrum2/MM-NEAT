 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.tests;

import micro.ai.core.AI;
import micro.ai.*;
import micro.ai.abstraction.WorkerRush;
import micro.ai.abstraction.pathfinding.BFSPathFinding;
//import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.gui.PhysicalGameStatePanel;
//import java.io.OutputStreamWriter;
import javax.swing.JFrame;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.PlayerAction;
import micro.rts.units.UnitTypeTable;
//import micro.util.XMLWriter;

/**
 *
 * @author santi
 */
public class GameVisualSimulationTest {
    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml", utt);
//        PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 5000;
        int PERIOD = 20;
        boolean gameover = false;
        
        AI ai1 = new WorkerRush(utt, new BFSPathFinding());        
        AI ai2 = new RandomBiasedAI();

        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }while(!gameover && gs.getTime()<MAXCYCLES);
        
        System.out.println("Game Over");
    }    
}
