package edu.utexas.cs.nn.breve2D;

import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.breve2D.dynamics.RammingDynamics;
import edu.utexas.cs.nn.breve2D.sensor.RaySensor;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class Breve2DGameView extends JComponent {

    //private static String pathImages = "images" + System.getProperty("file.separator") + "breve2D" + System.getProperty("file.separator");
    private final Breve2DGame game;
    //for debugging/illustration purposes only: draw colors in the maze to check whether controller is working
    //correctly or not; can draw squares and lines (see NearestPillPacManVS for demonstration).
    private GameFrame frame;
    private Graphics bufferGraphics;
    private Image offscreen;
    private final int maxHealth;
    private final boolean rams;

    public Breve2DGameView(Breve2DGame game) {
        this.game = game;
        this.maxHealth = Parameters.parameters.integerParameter("breve2DAgentHealth");
        this.rams = game.dynamics instanceof RammingDynamics;
    }

    ////////////////////////////////////////
    ////// Visual aids for debugging ///////
    ////////////////////////////////////////
    @Override
    public void paintComponent(Graphics g) {
        if (offscreen == null) {
            offscreen = createImage(this.getPreferredSize().width, this.getPreferredSize().height);
            bufferGraphics = offscreen.getGraphics();
        }
        bufferGraphics.setColor(Color.WHITE);
        bufferGraphics.fillRect(0, 0, Breve2DGame.SIZE_X, Breve2DGame.SIZE_Y);

        drawPlayer();
        drawMonsters();
        if (rams) {
            RammingDynamics rammingDynamics = (RammingDynamics) game.dynamics;
            if (rammingDynamics.monstersHaveRams()) {
                drawMonsterRams(rammingDynamics.getRamOffset());
            }
            if (rammingDynamics.playerHasRam()) {
                drawPlayerRam(rammingDynamics.getRamOffset());
            }
        }
        drawGameInfo();

        Iterator<Triple<ILocated2D, ILocated2D, Color>> itr = game.lines.iterator();
        while (itr.hasNext()) {
            Triple<ILocated2D, ILocated2D, Color> t = itr.next();
            bufferGraphics.setColor(t.t3);
            bufferGraphics.drawLine(x(t.t1.getX()), y(t.t1.getY()), x(t.t2.getX()), y(t.t2.getY()));
            itr.remove();
        }

        if (game.gameOver()) {
            drawGameOver();
        }

        g.drawImage(offscreen, 0, 0, this);
    }

    private static int[][] trianglePoints(double x, double y, double rad) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        xPoints[0] = (int) (x + Breve2DGame.AGENT_MAGNITUDE * Math.cos(rad));
        yPoints[0] = (int) (Breve2DGame.SIZE_Y - (y + Breve2DGame.AGENT_MAGNITUDE * Math.sin(rad)));

        xPoints[1] = (int) (x + Breve2DGame.AGENT_MAGNITUDE * Math.cos(rad + (Math.PI * (5.0 / 6.0))));
        yPoints[1] = (int) (Breve2DGame.SIZE_Y - (y + Breve2DGame.AGENT_MAGNITUDE * Math.sin(rad + (Math.PI * (5.0 / 6.0)))));

        xPoints[2] = (int) (x + Breve2DGame.AGENT_MAGNITUDE * Math.cos(rad - (Math.PI * (5.0 / 6.0))));
        yPoints[2] = (int) (Breve2DGame.SIZE_Y - (y + Breve2DGame.AGENT_MAGNITUDE * Math.sin(rad - (Math.PI * (5.0 / 6.0)))));

        return new int[][]{xPoints, yPoints};
    }

    private int x(double x) {
        return (int) x;
    }

    private int y(double y) {
        return (int) (Breve2DGame.SIZE_Y - y);
    }

    private void drawAgent(Agent a, Color c) {
        bufferGraphics.setColor(Color.DARK_GRAY);
        bufferGraphics.fillOval((int) (a.getX() - Breve2DGame.AGENT_MAGNITUDE), (int) (Breve2DGame.SIZE_Y - a.getY() - Breve2DGame.AGENT_MAGNITUDE), 2 * Breve2DGame.AGENT_MAGNITUDE, 2 * Breve2DGame.AGENT_MAGNITUDE);
        bufferGraphics.setColor(c);
        int[][] points = trianglePoints(a.getX(), a.getY(), a.getHeading());
        bufferGraphics.fillPolygon(points[0], points[1], 3);
    }

    private void drawPlayer() {
        drawAgent(game.player, Color.GREEN);
    }

    private void drawMonsterRams(Tuple2D offset) {
        for (int i = 0; i < game.numMonsters; i++) {
            if (!game.monsters[i].isDead()) {
                drawRam(game.monsters[i], Color.PINK, offset);
            }
        }
    }

    private void drawPlayerRam(Tuple2D ramOffset) {
        drawRam(game.player, Color.CYAN, ramOffset);
    }

    private void drawRam(Agent a, Color c, Tuple2D offset) {
        Tuple2D pos = a.getPosition().add(offset.rotate(a.getHeading()));
        bufferGraphics.setColor(c);
        bufferGraphics.fillOval((int) (pos.getX() - Breve2DGame.RAM_MAGNITUDE), (int) (Breve2DGame.SIZE_Y - pos.getY() - Breve2DGame.RAM_MAGNITUDE), 2 * Breve2DGame.RAM_MAGNITUDE, 2 * Breve2DGame.RAM_MAGNITUDE);
    }

    private void drawMonsters() {
        for (int i = 0; i < game.numMonsters; i++) {
            if (!game.monsters[i].isDead()) {
                bufferGraphics.setColor(Color.BLACK);
                for (int j = 0; j < game.numMonsterRays; j++) {
                    RaySensor ray = game.getRaySensor(i, j);
                    Tuple2D end = ray.getEndpoint();
                    bufferGraphics.drawLine(x(game.monsters[i].getX()), y(game.monsters[i].getY()), x(end.getX()), y(end.getY()));
                }
                drawAgent(game.monsters[i], Color.RED);
            }
        }
    }

    private void drawGameInfo() {
        bufferGraphics.setColor(Color.BLACK);
        ArrayList<Integer> monsterHealths = new ArrayList<Integer>(game.monsters.length);
        ArrayList<Integer> distances = new ArrayList<Integer>(game.monsters.length);
        for (int i = 0; i < game.monsters.length; i++) {
            monsterHealths.add(game.monsters[i].getHealth());
            distances.add((int) game.getMonster(i).distance(game.getPlayer()));
        }
        bufferGraphics.drawString("Time: " + game.totalTime + "/" + game.timeLimit + " | Player: " + game.getPlayer().getHealth() + "/" + maxHealth + " | Monsters: " + monsterHealths + " | Dis: " + distances, 4, Breve2DGame.SIZE_Y - 10);
    }

    private void drawGameOver() {
        bufferGraphics.setColor(Color.WHITE);
        bufferGraphics.drawString("Game Over", 80, 150);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Breve2DGame.SIZE_X, Breve2DGame.SIZE_Y);
    }

    public Breve2DGameView showGame() {
        this.frame = new GameFrame(this);

        //just wait for a bit for player to be ready
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        return this;
    }

    public GameFrame getFrame() {
        return frame;
    }

    public class GameFrame extends JFrame {

        public GameFrame(JComponent comp) {
            getContentPane().add(BorderLayout.CENTER, comp);
            pack();
            this.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
            this.setVisible(true);
            this.setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            repaint();
        }
    }
}
