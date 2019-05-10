package distance.view;

import distance.util.MarioReader;
import utilities.JEasyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by simonmarklucas on 04/08/2016.
 */

public class LevelView extends JPanel {

    public static void main(String[] args) {
        int[][] randRect = randomRect(40, 25);
        showMaze(randRect, "Random Test of LevelView");
    }

    int width, height;
    int cellSize = 20;
    int[][] tiles;

    static Color[] colors = {
            Color.white, Color.black, Color.red,
    };

    HashMap<Integer,Color> colorMap;
    HashMap<Integer,BufferedImage> iconMap;

    public LevelView setColorMap(HashMap<Integer, Color> colorMap) {
        this.colorMap = colorMap;
        return this;
    }

    public LevelView setIconMap(HashMap<Integer, BufferedImage> iconMap) {
        this.iconMap = iconMap;
        return this;
    }

    public LevelView setCellSize(int cellSize) {
        this.cellSize = cellSize;
        return this;
    }

    static Random random = new Random();

    static int[][] randomRect(int w, int h) {
        int[][] tiles = new int[w][h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                tiles[i][j] = random.nextInt(colors.length);
            }
        }
        return tiles;
    }

    public static int[][] toRect(int[] x, int w, int h) {
        int[][] tiles = new int[w][h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                tiles[i][j] = x[i + j * w];
            }
        }
        return tiles;
    }

    public LevelView(int[][] tiles) {
        this.tiles = tiles;
        width = tiles.length;
        height = tiles[0].length;
        iconMap = MarioReader.icons;

    }

    public LevelView setTiles(int[][] tiles) {
        this.tiles = tiles;
        repaint();
        return this;
    }

    public LevelView setTiles(int[] tiles) {
        this.tiles = toRect(tiles, width, height);
        repaint();
        return this;
    }


    // setting the icons to Mario is just a quick hack for now
    public static void showMaze(int[][] tiles, String title) {
        LevelView levelView = new LevelView(tiles).setColorMap(MarioReader.tileColors);
        levelView.iconMap = MarioReader.icons;
        new JEasyFrame(levelView, title);
    }

    public static void showMaze(int[] tiles, int w, int h, String title) {
        LevelView levelView = new LevelView(toRect(tiles, w, h));
        levelView.iconMap = MarioReader.icons;
        new JEasyFrame(levelView, title);
    }

    public static void showMaze(int[] tiles, int w, int h, String title, HashMap<Integer,Color> tileMap) {
        LevelView levelView = new LevelView(toRect(tiles, w, h)).setColorMap(tileMap);
        levelView.iconMap = MarioReader.icons;
        new JEasyFrame(levelView, title);
    }

    public void paintComponent(Graphics go) {
        Graphics2D g = (Graphics2D) go;
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                boolean foundIcon = false;
                if (colorMap != null) {
                    try {
                        BufferedImage img = null;
                        if (iconMap!= null)
                            img = iconMap.get(tiles[x][y]);
                        if (img !=null) {
                            g.drawImage(img, x*cellSize, y*cellSize, this);
                            foundIcon = true;
                        } else {
                            g.setColor(colorMap.get(tiles[x][y]));
                        }
                    } catch (Exception e) {
                        g.setColor(Color.white);
                    }
                } else {
                    // System.out.println(tiles[x][y]);
                    g.setColor(colors[tiles[x][y]]);
                }
                if (!foundIcon) {
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(width * cellSize, height * cellSize);
    }

}
