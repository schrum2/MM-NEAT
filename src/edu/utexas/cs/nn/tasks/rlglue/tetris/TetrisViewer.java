/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.rlglue.tetris;

import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rlcommunity.environments.tetris.TetrisState;

/**
 *
 * @author Jacob Schrum
 */
public final class TetrisViewer {

    public static TetrisViewer current = null;
    public static final int HEIGHT = 500;
    public static final int WIDTH = 300;
    public static final String TITLE = "Tetris";
    public static final int BLOCK_SIZE = 20; //10;
    public static final int BUFFER = 50;
    public static final int WATCH_DELAY = 100; //1000;
    public DrawingPanel panel;

    public TetrisViewer() {
        panel = new DrawingPanel(WIDTH, HEIGHT, TITLE);
        panel.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
        panel.getGraphics().translate(BUFFER, BUFFER);
        current = this;
    }

    public void update(TetrisState ts) {
        Graphics2D g = panel.getGraphics();
        //System.out.println("Update Tetris");

        Rectangle2D agentRect;
        int numCols = ts.getWidth();
        int numRows = ts.getHeight();
        int[] tempWorld = new int[ts.worldState.length];
        System.arraycopy(ts.worldState, 0, tempWorld, 0, tempWorld.length);
        ts.writeCurrentBlock(tempWorld);

        //Desired abstract block size
        int DABS = BLOCK_SIZE;
        //int scaleFactorX = numCols * DABS;
        //int scaleFactorY = numRows * DABS;

        int w = DABS;
        int h = DABS;
        int x = 0;
        int y = 0;
        g.setColor(Color.GRAY);

        //System.out.println(numRows + "," + numCols);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                x = j * DABS;
                y = i * DABS;
                int thisBlockColor = tempWorld[i * numCols + j];
                if (thisBlockColor != 0) {
                    //System.out.println("Colored block: " + x +","+ y +","+ w +","+ h);
                    switch (thisBlockColor) {
                        case 1:
                            g.setColor(Color.PINK);
                            break;
                        case 2:
                            g.setColor(Color.RED);
                            break;
                        case 3:
                            g.setColor(Color.GREEN);
                            break;
                        case 4:
                            g.setColor(Color.YELLOW);
                            break;
                        case 5:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 6:
                            g.setColor(Color.ORANGE);
                            break;
                        case 7:
                            g.setColor(Color.MAGENTA);
                            break;

                    }
                    g.fill3DRect(x, y, w, h, true);
                } else {
                    //System.out.println("Empty block: " + x +","+ y +","+ w +","+ h);
                    g.setColor(Color.WHITE);
                    agentRect = new Rectangle2D.Double(x, y, w, h);
                    if (true) { //tetVis.printGrid()) {
                        g.fill3DRect(x, y, w, h, true);
                    } else {
                        g.fill(agentRect);
                    }
                }
            }
        }
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, DABS * numCols, DABS * numRows);

        try {
            Thread.sleep(WATCH_DELAY);
        } catch (InterruptedException ex) {
            Logger.getLogger(TetrisViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
