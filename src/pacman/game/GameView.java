package pacman.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Vector;

import static pacman.game.Constants.*;

/**
 * This class is the view that displays the game. The only thing contestants
 * might need to know about are the helper methods that allow one to add colours
 * and lines to the game view. This could be useful for debugging.
 */
@SuppressWarnings("serial")
public final class GameView extends JComponent {

    private final Game game;
    private Images images;
    private MOVE lastPacManMove;
    private int time;
    private GameFrame frame;
    private Graphics bufferGraphics;
    private BufferedImage offscreen;
    //for debugging/illustration purposes only: draw colors in the maze to check whether controller is working
    //correctly or not; can draw squares and lines (see NearestPillPacManVS for demonstration).
    private static boolean isVisible = true;
    private static boolean saveImage = false;
    private static String imageFileName = "";
    public static Vector<DebugPointer> debugPointers = new Vector<DebugPointer>();
    public static Vector<DebugLine> debugLines = new Vector<DebugLine>();

    /**
     * Instantiates a new game view.
     *
     * @param game The game that is played.
     */
    public GameView(Game game) {
        this.game = game;

        images = new Images();
        lastPacManMove = game.getPacmanLastMoveMade();
        time = game.getTotalTime();
    }

    ///////////////////////////////////////////////
    ////// Visual aids for debugging: start ///////
    ///////////////////////////////////////////////
    /**
     * Adds a node to be highlighted using the color specified
     *
     * @param game Copy of the current game
     * @param color Colour to be used
     * @param nodeIndices The node indices to be highlighted by the chosen
     * colour
     */
    public synchronized static void addPoints(Game game, Color color, int... nodeIndices) {
        if (isVisible) {
            for (int i = 0; i < nodeIndices.length; i++) {
                debugPointers.add(new DebugPointer(game.getNodeXCoord(nodeIndices[i]), game.getNodeYCoord(nodeIndices[i]), color));
            }
        }
    }

    /**
     * Adds a set of lines to be drawn using the color specified
     * (fromNnodeIndices.length must be equals toNodeIndices.length)
     *
     * @param game Copy of the current game
     * @param color Colour to be used
     * @param fromNnodeIndices The node indices where the lines start
     * @param toNodeIndices The node indices where the lines end
     */
    public synchronized static void addLines(Game game, Color color, int[] fromNnodeIndices, int[] toNodeIndices) {
        if (isVisible) {
            for (int i = 0; i < fromNnodeIndices.length; i++) {
                debugLines.add(new DebugLine(game.getNodeXCoord(fromNnodeIndices[i]), game.getNodeYCoord(fromNnodeIndices[i]), game.getNodeXCoord(toNodeIndices[i]), game.getNodeYCoord(toNodeIndices[i]), color));
            }
        }
    }

    /**
     * Adds a line to be drawn using the color specified
     *
     * @param game the game
     * @param color the color
     * @param fromNnodeIndex the from nnode index
     * @param toNodeIndex the to node index
     */
    public synchronized static void addLines(Game game, Color color, int fromNnodeIndex, int toNodeIndex) {
        if (isVisible) {
            debugLines.add(new DebugLine(game.getNodeXCoord(fromNnodeIndex), game.getNodeYCoord(fromNnodeIndex), game.getNodeXCoord(toNodeIndex), game.getNodeYCoord(toNodeIndex), color));
        }
    }

    /**
     * Draw the debug info and the clear it - it is shown for a single time step
     * only.
     */
    private void drawDebugInfo() {
        for (int i = 0; i < debugPointers.size(); i++) {
            DebugPointer dp = debugPointers.get(i);
            bufferGraphics.setColor(dp.color);
            bufferGraphics.fillRect(dp.x * MAG + 1, dp.y * MAG + 5, 10, 10);
        }

        for (int i = 0; i < debugLines.size(); i++) {
            DebugLine dl = debugLines.get(i);
            bufferGraphics.setColor(dl.color);
            bufferGraphics.drawLine(dl.x1 * MAG + 5, dl.y1 * MAG + 10, dl.x2 * MAG + 5, dl.y2 * MAG + 10);
        }

        debugPointers.clear();
        debugLines.clear();
    }

    /**
     * Allows one to save the image of the current game state using the file
     * name specified.
     *
     * @param fileName Name of the image.
     */
    public synchronized static void saveImage(String fileName) {
        saveImage = true;
        imageFileName = fileName;
    }

    /*
     * Saves the actual image.
     */
    private void saveImage() {
        try {
            ImageIO.write(offscreen, "png", new File("myData/" + imageFileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveImage = false;
    }

    /////////////////////////////////////////////
    ////// Visual aids for debugging: end ///////
    /////////////////////////////////////////////
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g) {
        time = game.getTotalTime();

        if (offscreen == null) {
            offscreen = (BufferedImage) createImage(this.getPreferredSize().width, this.getPreferredSize().height);
            bufferGraphics = offscreen.getGraphics();
        }

        drawMaze();
        drawDebugInfo();	//this will be used during testing only and will be disabled in the competition itself        
        drawPills();
        drawPowerPills();
        drawPacMan();
        drawGhosts();
        drawLives();
        drawGameInfo();

        if (game.gameOver()) {
            drawGameOver();
        }

        g.drawImage(offscreen, 0, 0, this);

        if (saveImage) {
            saveImage();
        }
    }

    /**
     * Draw maze.
     */
    private void drawMaze() {
        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.fillRect(0, 0, GV_WIDTH * MAG, GV_HEIGHT * MAG + 20);

        bufferGraphics.drawImage(images.getMaze(game.getMazeIndex()), 2, 6, null);
    }

    /**
     * Draw pills.
     */
    private void drawPills() {
        int[] pillIndices = game.getPillIndices();

        bufferGraphics.setColor(Color.white);

        for (int i = 0; i < pillIndices.length; i++) {
            if (game.isPillStillAvailable(i)) {
                bufferGraphics.fillOval(game.getNodeXCoord(pillIndices[i]) * MAG + 4, game.getNodeYCoord(pillIndices[i]) * MAG + 8, 3, 3);
            }
        }
    }

    /**
     * Draw power pills.
     */
    private void drawPowerPills() {
        int[] powerPillIndices = game.getPowerPillIndices();

        bufferGraphics.setColor(Color.white);

        for (int i = 0; i < powerPillIndices.length; i++) {
            if (game.isPowerPillStillAvailable(i)) {
                bufferGraphics.fillOval(game.getNodeXCoord(powerPillIndices[i]) * MAG + 1, game.getNodeYCoord(powerPillIndices[i]) * MAG + 5, 8, 8);
            }
        }
    }

    /**
     * Draw pac man.
     */
    private void drawPacMan() {
        int pacLoc = game.getPacmanCurrentNodeIndex();

        MOVE tmpLastPacManMove = game.getPacmanLastMoveMade();

        if (tmpLastPacManMove != MOVE.NEUTRAL) {
            lastPacManMove = tmpLastPacManMove;
        }

        bufferGraphics.drawImage(images.getPacMan(lastPacManMove, time), game.getNodeXCoord(pacLoc) * MAG - 1, game.getNodeYCoord(pacLoc) * MAG + 3, null);
    }

    /**
     * Draw ghosts.
     */
    private void drawGhosts() {
        for (GHOST ghostType : GHOST.values()) {
            int currentNodeIndex = game.getGhostCurrentNodeIndex(ghostType);
            int nodeXCood = game.getNodeXCoord(currentNodeIndex);
            int nodeYCood = game.getNodeYCoord(currentNodeIndex);

            if (game.getGhostEdibleTime(ghostType) > 0) {
                //what is the second clause for????
                if (game.getGhostEdibleTime(ghostType) < EDIBLE_ALERT && ((time % 6) / 3) == 0) {
                    bufferGraphics.drawImage(images.getEdibleGhost(true, time), nodeXCood * MAG - 1, nodeYCood * MAG + 3, null);
                } else {
                    bufferGraphics.drawImage(images.getEdibleGhost(false, time), nodeXCood * MAG - 1, nodeYCood * MAG + 3, null);
                }
            } else {
                int index = ghostType.ordinal();

                if (game.getGhostLairTime(ghostType) > 0) {
                    bufferGraphics.drawImage(images.getGhost(ghostType, game.getGhostLastMoveMade(ghostType), time), nodeXCood * MAG - 1 + (index * 5), nodeYCood * MAG + 3, null);
                } else {
                    bufferGraphics.drawImage(images.getGhost(ghostType, game.getGhostLastMoveMade(ghostType), time), nodeXCood * MAG - 1, nodeYCood * MAG + 3, null);
                }
            }
        }
    }

    /**
     * Draw lives.
     */
    private void drawLives() {
        for (int i = 0; i < game.getPacmanNumberOfLivesRemaining() - 1; i++) //-1 as lives remaining includes the current life
        {
            bufferGraphics.drawImage(images.getPacManForExtraLives(), 210 - (30 * i) / 2, 260, null);
        }
    }

    /**
     * Draw game info.
     */
    private void drawGameInfo() {
        bufferGraphics.setColor(Color.WHITE);
        bufferGraphics.drawString("S: ", 4, 271);
        bufferGraphics.drawString("" + game.getScore(), 16, 271);
        bufferGraphics.drawString("L: ", 78, 271);
        bufferGraphics.drawString("" + (game.getCurrentLevel() + 1), 90, 271);
        bufferGraphics.drawString("T: ", 116, 271);
        bufferGraphics.drawString("" + game.getCurrentLevelTime(), 129, 271);
    }

    /**
     * Draw game over.
     */
    private void drawGameOver() {
        bufferGraphics.setColor(Color.WHITE);
        bufferGraphics.drawString("Game Over", 80, 150);
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return new Dimension(GV_WIDTH * MAG, GV_HEIGHT * MAG + 20);
    }

    /**
     * Show game.
     *
     * @return the game view
     */
    public GameView showGame() {
        this.frame = new GameFrame(this);

        //just wait for a bit for player to be ready
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }

        return this;
    }

    /**
     * Gets the frame.
     *
     * @return the frame
     */
    public GameFrame getFrame() {
        return frame;
    }

    /**
     * The Class GameFrame.
     */
    public class GameFrame extends JFrame {

        /**
         * Instantiates a new game frame.
         *
         * @param comp the comp
         */
        public GameFrame(JComponent comp) {
            getContentPane().add(BorderLayout.CENTER, comp);
            pack();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation((int) (screen.getWidth() * 3 / 8), (int) (screen.getHeight() * 3 / 8));
            this.setVisible(true);
            this.setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            repaint();
        }
    }

    private static class DebugPointer {

        public int x, y;
        public Color color;

        public DebugPointer(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    private static class DebugLine {

        public int x1, y1, x2, y2;
        public Color color;

        public DebugLine(int x1, int y1, int x2, int y2, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

    public class Images {

        private EnumMap<MOVE, BufferedImage[]> pacman;
        private EnumMap<GHOST, EnumMap<MOVE, BufferedImage[]>> ghosts;
        private BufferedImage[] edibleGhosts, edibleBlinkingGhosts, mazes;

        public Images() {
            pacman = new EnumMap<MOVE, BufferedImage[]>(MOVE.class);

            pacman.put(MOVE.UP, new BufferedImage[]{_loadImage("mspacman-up-normal.png"),
                        _loadImage("mspacman-up-open.png"),
                        _loadImage("mspacman-up-closed.png")});

            pacman.put(MOVE.RIGHT, new BufferedImage[]{_loadImage("mspacman-right-normal.png"),
                        _loadImage("mspacman-right-open.png"),
                        _loadImage("mspacman-right-closed.png")});

            pacman.put(MOVE.DOWN, new BufferedImage[]{_loadImage("mspacman-down-normal.png"),
                        _loadImage("mspacman-down-open.png"),
                        _loadImage("mspacman-down-closed.png")});

            pacman.put(MOVE.LEFT, new BufferedImage[]{_loadImage("mspacman-left-normal.png"),
                        _loadImage("mspacman-left-open.png"),
                        _loadImage("mspacman-left-closed.png")});

            ghosts = new EnumMap<GHOST, EnumMap<MOVE, BufferedImage[]>>(GHOST.class);

            ghosts.put(GHOST.BLINKY, new EnumMap<MOVE, BufferedImage[]>(MOVE.class));
            ghosts.get(GHOST.BLINKY).put(MOVE.UP, new BufferedImage[]{_loadImage("blinky-up-1.png"), _loadImage("blinky-up-2.png")});
            ghosts.get(GHOST.BLINKY).put(MOVE.RIGHT, new BufferedImage[]{_loadImage("blinky-right-1.png"), _loadImage("blinky-right-2.png")});
            ghosts.get(GHOST.BLINKY).put(MOVE.DOWN, new BufferedImage[]{_loadImage("blinky-down-1.png"), _loadImage("blinky-down-2.png")});
            ghosts.get(GHOST.BLINKY).put(MOVE.LEFT, new BufferedImage[]{_loadImage("blinky-left-1.png"), _loadImage("blinky-left-2.png")});

            ghosts.put(GHOST.PINKY, new EnumMap<MOVE, BufferedImage[]>(MOVE.class));
            ghosts.get(GHOST.PINKY).put(MOVE.UP, new BufferedImage[]{_loadImage("pinky-up-1.png"), _loadImage("pinky-up-2.png")});
            ghosts.get(GHOST.PINKY).put(MOVE.RIGHT, new BufferedImage[]{_loadImage("pinky-right-1.png"), _loadImage("pinky-right-2.png")});
            ghosts.get(GHOST.PINKY).put(MOVE.DOWN, new BufferedImage[]{_loadImage("pinky-down-1.png"), _loadImage("pinky-down-2.png")});
            ghosts.get(GHOST.PINKY).put(MOVE.LEFT, new BufferedImage[]{_loadImage("pinky-left-1.png"), _loadImage("pinky-left-2.png")});

            ghosts.put(GHOST.INKY, new EnumMap<MOVE, BufferedImage[]>(MOVE.class));
            ghosts.get(GHOST.INKY).put(MOVE.UP, new BufferedImage[]{_loadImage("inky-up-1.png"), _loadImage("inky-up-2.png")});
            ghosts.get(GHOST.INKY).put(MOVE.RIGHT, new BufferedImage[]{_loadImage("inky-right-1.png"), _loadImage("inky-right-2.png")});
            ghosts.get(GHOST.INKY).put(MOVE.DOWN, new BufferedImage[]{_loadImage("inky-down-1.png"), _loadImage("inky-down-2.png")});
            ghosts.get(GHOST.INKY).put(MOVE.LEFT, new BufferedImage[]{_loadImage("inky-left-1.png"), _loadImage("inky-left-2.png")});

            ghosts.put(GHOST.SUE, new EnumMap<MOVE, BufferedImage[]>(MOVE.class));
            ghosts.get(GHOST.SUE).put(MOVE.UP, new BufferedImage[]{_loadImage("sue-up-1.png"), _loadImage("sue-up-2.png")});
            ghosts.get(GHOST.SUE).put(MOVE.RIGHT, new BufferedImage[]{_loadImage("sue-right-1.png"), _loadImage("sue-right-2.png")});
            ghosts.get(GHOST.SUE).put(MOVE.DOWN, new BufferedImage[]{_loadImage("sue-down-1.png"), _loadImage("sue-down-2.png")});
            ghosts.get(GHOST.SUE).put(MOVE.LEFT, new BufferedImage[]{_loadImage("sue-left-1.png"), _loadImage("sue-left-2.png")});

            edibleGhosts = new BufferedImage[2];
            edibleGhosts[0] = _loadImage("edible-ghost-1.png");
            edibleGhosts[1] = _loadImage("edible-ghost-2.png");

            edibleBlinkingGhosts = new BufferedImage[2];
            edibleBlinkingGhosts[0] = _loadImage("edible-ghost-blink-1.png");
            edibleBlinkingGhosts[1] = _loadImage("edible-ghost-blink-2.png");

            mazes = new BufferedImage[4];
            for (int i = 0; i < mazes.length; i++) {
                mazes[i] = _loadImage(mazeNames[i]);
            }
        }

        public BufferedImage getPacMan(MOVE move, int time) {
            return pacman.get(move)[(time % 6) / 2];
        }

        public BufferedImage getPacManForExtraLives() {
            return pacman.get(MOVE.RIGHT)[0];
        }

        public BufferedImage getGhost(GHOST ghost, MOVE move, int time) {
            if (move == MOVE.NEUTRAL) {
                return ghosts.get(ghost).get(MOVE.UP)[(time % 6) / 3];
            } else {
                return ghosts.get(ghost).get(move)[(time % 6) / 3];
            }
        }

        public BufferedImage getEdibleGhost(boolean blinking, int time) {
            if (!blinking) {
                return edibleGhosts[(time % 6) / 3];
            } else {
                return edibleBlinkingGhosts[(time % 6) / 3];
            }
        }

        public BufferedImage getMaze(int mazeIndex) {
            return mazes[mazeIndex];
        }

        private BufferedImage _loadImage(String fileName) {
            BufferedImage image = null;

            try {
                image = ImageIO.read(new File(pathImages + System.getProperty("file.separator") + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return image;
        }
    }
}