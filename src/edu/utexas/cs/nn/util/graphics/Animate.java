package edu.utexas.cs.nn.util.graphics;
import java.awt.*;

public class Animate extends javax.swing.JApplet implements Runnable {
	Image[] picture = new Image[6];
	int totalPictures = 0;
	int current = 0;
	Thread runner;
	int pause = 500;

	public void init() {
		for (int i = 0; i < 6; i++) {
			String imageText = null;
			imageText = getParameter("image"+i);
			if (imageText != null) {
				totalPictures++;
				picture[i] = getImage(getCodeBase(), imageText);
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
