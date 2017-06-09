 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.tests;

import micro.gui.PhysicalGameStatePanel;
import java.io.OutputStreamWriter;
import javax.swing.JFrame;
import micro.rts.GameState;
import micro.rts.PartiallyObservableGameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.UnitTypeTable;
import micro.util.XMLWriter;

/**
 *
 * @author santi
 */
public class MapVisualizationTest {
    @SuppressWarnings("unused")
	public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8Obstacle.xml", utt);

        GameState gs = new GameState(pgs, utt);
                
        XMLWriter xml = new XMLWriter(new OutputStreamWriter(System.out));
        pgs.toxml(xml);
        xml.flush();

        OutputStreamWriter jsonwriter = new OutputStreamWriter(System.out);
        pgs.toJSON(jsonwriter);
        jsonwriter.flush();

        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640);
        JFrame w2 = PhysicalGameStatePanel.newVisualizer(new PartiallyObservableGameState(gs,0),640,640, true);
        JFrame w3 = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);
        
    }    
}
