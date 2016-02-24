package edu.utexas.cs.nn.graphics;

/*
 Stuart Reges and Marty Stepp
 07/01/2005

 The DrawingPanel class provides a simple interface for drawing persistent
 images using a Graphics object.  An internal BufferedImage object is used
 to keep track of what has been drawn.  A client of the class simply
 constructs a DrawingPanel of a particular size and then draws on it with
 the Graphics object, setting the background color if they so choose.

 To ensure that the image is always displayed, a timer calls repaint at
 regular intervals.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;

public class DrawingPanel implements ActionListener {

    public static final int DELAY = 100;  // delay between repaints in millis
    private static final String DUMP_IMAGE_PROPERTY_NAME = "drawingpanel.save";
    private static String TARGET_IMAGE_FILE_NAME = null;
    private static final boolean PRETTY = true;  // true to anti-alias
    private static boolean DUMP_IMAGE = true;  // true to write DrawingPanel to file
    private int width, height;    // dimensions of window frame
    private JFrame frame;         // overall window frame
    private JPanel panel;         // overall drawing surface
    private BufferedImage image;  // remembers drawing commands
    private Graphics2D g2;        // graphics context for painting
    private JLabel statusBar;     // status bar showing mouse position
    private long createTime;

    static {
        TARGET_IMAGE_FILE_NAME = System.getProperty(DUMP_IMAGE_PROPERTY_NAME);
        DUMP_IMAGE = (TARGET_IMAGE_FILE_NAME != null);
    }

    // construct a drawing panel of given width and height enclosed in a window
    public DrawingPanel(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        this.statusBar = new JLabel(" ");
        this.statusBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.panel.setBackground(Color.WHITE);
        this.panel.setPreferredSize(new Dimension(width, height));
        this.panel.add(new JLabel(new ImageIcon(image)));

        // listen to mouse movement
        MouseInputAdapter listener = new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                DrawingPanel.this.statusBar.setText("(" + e.getX() + ", " + e.getY() + ")");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                DrawingPanel.this.statusBar.setText(" ");
            }
        };
        this.panel.addMouseListener(listener);
        this.panel.addMouseMotionListener(listener);

        this.g2 = (Graphics2D) image.getGraphics();
        this.g2.setColor(Color.BLACK);
        if (PRETTY) {
            this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            this.g2.setStroke(new BasicStroke(1.1f));
        }

        this.frame = new JFrame(title);
        this.frame.setResizable(true);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (DUMP_IMAGE) {
                    DrawingPanel.this.save(TARGET_IMAGE_FILE_NAME);
                }
                System.exit(0);
            }
        });
        this.frame.getContentPane().add(panel);
        this.frame.getContentPane().add(statusBar, "South");
        this.frame.pack();
        this.frame.setVisible(true);
        if (DUMP_IMAGE) {
            createTime = System.currentTimeMillis();
            this.frame.toBack();
        } else {
            this.toFront();
        }

        // repaint timer so that the screen will update
        new Timer(DELAY, this).start();
    }

    // used for an internal timer that keeps repainting
    public void actionPerformed(ActionEvent e) {
        this.panel.repaint();
        if (DUMP_IMAGE && System.currentTimeMillis() > createTime + 4 * DELAY) {
            this.frame.setVisible(false);
            this.frame.dispose();
            this.save(TARGET_IMAGE_FILE_NAME);
            System.exit(0);
        }
    }

    // obtain the Graphics object to draw on the panel
    public Graphics2D getGraphics() {
        return this.g2;
    }

    // set the background color of the drawing panel
    public void setBackground(Color c) {
        this.panel.setBackground(c);
    }

    // show or hide the drawing panel on the screen
    public void setVisible(boolean visible) {
        this.frame.setVisible(visible);
    }

    // makes the program pause for the given amount of time,
    // allowing for animation
    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    // take the current contents of the panel and write them to a file
    public void save(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);

        // create second image so we get the background color
        BufferedImage image2 = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image2.getGraphics();
        g.setColor(panel.getBackground());
        g.fillRect(0, 0, this.width, this.height);
        g.drawImage(this.image, 0, 0, panel);

        // write file
        try {
            ImageIO.write(image2, extension, new java.io.File(filename));
        } catch (java.io.IOException e) {
            System.err.println("Unable to save image:\n" + e);
        }
    }

    // makes drawing panel become the frontmost window on the screen
    public void toFront() {
        this.frame.toFront();
    }

    public void dispose() {
        this.getGraphics().dispose();
        frame.dispose();
    }

    public void setLocation(int x, int y) {
        this.frame.setLocation(x, y);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setTitle(String title) {
        getFrame().setTitle(title);
    }

    public void clear() {
        getGraphics().setBackground(Color.WHITE);
        getGraphics().clearRect(0, 0, width, height);
    }
}
