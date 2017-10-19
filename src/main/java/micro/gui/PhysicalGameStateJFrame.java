/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.gui;

import javax.swing.JFrame;
import micro.rts.GameState;

/**
 *
 * @author santi
 */
@SuppressWarnings("serial")
public class PhysicalGameStateJFrame extends JFrame {
    PhysicalGameStatePanel panel = null;
    
    public PhysicalGameStateJFrame(String title, int dx, int dy, PhysicalGameStatePanel a_panel) {
        super(title);
        panel = a_panel;

        getContentPane().add(panel);
        pack();
        setResizable(false);
        setSize(dx,dy);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public PhysicalGameStatePanel getPanel() {
        return panel;
    }
    
    public void setStateCloning(GameState gs) {
        panel.setStateCloning(gs);
    }
            
    public void setStateDirect(GameState gs) {
        panel.setStateDirect(gs);
    }
}
