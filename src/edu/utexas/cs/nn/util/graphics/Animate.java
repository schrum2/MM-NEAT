package edu.utexas.cs.nn.util.graphics;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//TODO: Eventually completely delete this class

/**
 * Utility class found online that takes an array of images
 * and creates an animation that runs in a JApplet.
 */
public class Animate extends javax.swing.JApplet implements Runnable {
	Image scream;
	Image sunset1;
	Image sunset2;
	Image[] picture; 
	int totalPictures = 3;
	int current = 0;
	Thread runner;
	int pause = 1000;
	
	public Animate() {
		try {
			scream = ImageIO.read(new File("../data/imagematch/theScream.png"));
			sunset1 = ImageIO.read(new File("../data/imagematch/sunset1.png"));
			sunset2 = ImageIO.read(new File("../data/imagematch/sunset2.png"));
		}
		 catch (IOException e)
        {e.printStackTrace();}
		picture = new Image[]{scream, sunset1, sunset2};
	}

	public void init() {
		for (int i = 0; i < picture.length; i++) {
			String imageText = null;
			imageText = getParameter("image"+i);
			if (imageText != null) {
				totalPictures++;
				//picture[i] = getImage(getCodeBase(), imageText);
			} else
				break;
		}
		String pauseText = null;
		pauseText = getParameter("pause");
		if (pauseText != null) {
			pause = Integer.parseInt(pauseText);
		}
	}

	public void paint(Graphics screen) {
		super.paint(screen);
		Graphics2D screen2D = (Graphics2D) screen;
		if (picture[current] != null)
			screen2D.drawImage(picture[current], 0, 0, this);
	}

	public void start() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}
	
	public void run() {
		Thread thisThread = Thread.currentThread();
		while (runner == thisThread) {
			System.out.println(current);
			repaint();
			current++;
			if (current >= totalPictures)
				current = 0;
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) { }
		}
	}

	public void stop() {
		if (runner != null) {
			runner = null;
		}
	}
}
