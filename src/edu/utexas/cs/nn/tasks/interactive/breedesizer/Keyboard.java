package edu.utexas.cs.nn.tasks.interactive.breedesizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;
import edu.utexas.cs.nn.util.sound.MiscSoundUtil;
import edu.utexas.cs.nn.util.sound.SoundAmplitudeArrayManipulator;

@SuppressWarnings("serial")
public class Keyboard extends JFrame implements MouseListener{

	public static final double C3 = 130.81;
	public static final double CSHARP3 = 138.59;
	public static final double D3 = 146.83;
	public static final double DSHARP3 = 155.56;
	public static final double E3 = 164.81;
	public static final double F3 = 174.61;
	public static final double FSHARP3 = 185.00;
	public static final double G3 = 196.00;
	public static final double GSHARP3 = 207.65;
	public static final double A3 = 220.00;
	public static final double ASHARP3 = 233.08;
	public static final double B3 = 246.94;
	public static final double C4 = 261.63;
	public static final double CSHARP4 = 277.18;
	public static final double D4 = 293.66;
	public static final double DSHARP4 = 311.13;
	public static final double E4 = 329.63;
	public static final double F4 = 349.23;
	public static final double FSHARP4 = 369.99;
	public static final double G4 = 392.00;
	public static final double GSHARP4 = 415.30;
	public static final double A4 = 440.00;
	public static final double ASHARP4 = 466.16;
	public static final double B4 = 493.88;
	public static final double C5 = 523.25;

	public static final double[] KEYBOARD = new double[]{C3, CSHARP3, D3, DSHARP3, E3, F3, FSHARP3, G3, GSHARP3, A3, ASHARP3, B3, C4, CSHARP4, D4, DSHARP4, E4, F4, FSHARP4, G4, GSHARP4, A4, ASHARP4, B4, C5};
	public static final double[] WHITE_KEYS = new double[]{C3, D3, E3, F3, G3, A3, B3, C4, D4, E4, F4, G4, A4, B4, C5};
	public static final double[] BLACK_KEYS = new double[]{CSHARP3, -1, DSHARP3, -1, -1, -1, FSHARP3, -1, GSHARP3, -1, ASHARP3, -1, -1,  -1, CSHARP4, -1, DSHARP4, -1, -1, -1, FSHARP4, -1, GSHARP4, -1, ASHARP4, -1, -1, -1};

	public static final String[] PRENOTE = {"C3","D3","E3","F3","G3","A3","B3","C4","D4","E4","F4","G4","A4","B4","C5"};
	public static final String[] PREEXTENDEDNOTE = {"C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3", "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4", "C5"};

	//variables used for drawing keyboard graphics
	public static final int KEYBOARD_WIDTH = 600;
	public static final int KEYBOARD_HEIGHT = 200;
	public static final int WHITE_KEY_WIDTH = 40;
	public static final int BLACK_KEY_WIDTH = 20;	
	public static final int BLACK_KEY_START_WIDTH = 30;
	public static final int BLACK_KEY_START_HEIGHT = 100;
	public static final int WINDOW_EDGE_WIDTH = 8; //to account for construction of graphics being 8 pixels off (width)
	public static final int WINDOW_EDGE_HEIGHT = 30; //to account for construction of graphics being 30 pixels off (height)

	public static final int NOTE_LENGTH_DEFAULT = 40000;
	
	/**
	 * Inner class that constructs the graphical piano keyboard interface using JPanel and Graphics.
	 * 
	 * @author Isabel Tweraser
	 *
	 */
	class DrawPane extends JPanel{
		public void paintComponent(Graphics g){
			//draw on g here e.g.
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, KEYBOARD_WIDTH, KEYBOARD_HEIGHT); //white background
			g.setColor(Color.BLACK);
			for(int i = 0; i < KEYBOARD_WIDTH; i += WHITE_KEY_WIDTH) {
				g.drawLine(i, 0, i, KEYBOARD_HEIGHT); //line divisions for white keys
			}
			boolean[] blackKeyPresent = new boolean[]{true,true,false,true,true,true,false}; //follows 2-3 pattern of black keys on standard piano
			for(int i = 0; BLACK_KEY_START_WIDTH+i*WHITE_KEY_WIDTH < KEYBOARD_WIDTH; i++) {
				if(blackKeyPresent[i % blackKeyPresent.length])	
					g.fillRect(BLACK_KEY_START_WIDTH + i*WHITE_KEY_WIDTH, 0, BLACK_KEY_WIDTH, BLACK_KEY_START_HEIGHT); //black keys
			}
		}
	}
	
	/**
	 * Instance of CPPN used to replicate CPPN generated at different frequencies on computer
	 */
	private Network currentCPPN;
	
	/**
	 * Keyboard constructor set to desired height and width of window
	 */
	public Keyboard() {
		this(KEYBOARD_WIDTH, KEYBOARD_HEIGHT,"Keyboard");

	}
	
	/**
	 * Constructor that sets the size and enables click responses by adding a mouse listener
	 * 
	 * @param width of keyboard
	 * @param height of keyboard
	 * @param title Desired title of window being constructed
	 */
	public Keyboard(int width, int height, String title) {
		super("Keyboard");
		setContentPane(new DrawPane());
		
		this.setSize(width,height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.addMouseListener(this);

	}
	
	/**
	 * Used to access current CPPN generated. Called from BreedesizerTask so that current phenotype 
	 * can be accessed and set to private variable within Keyboard
	 * 
	 * @param phenotype input CPPN played in Breedesizer
	 */
	public void setCPPN(Network phenotype) {
		currentCPPN = phenotype;
	}
	
	/**
	 * Details actions that occur (or notes that play) when mouse is clicked. Determined based on screen location
	 * where mouse is clicked, which is divided by the width of the individual keys. The quotient corresponds to 
	 * the index of the correct frequency in the array of frequencies that corresponds with either white keys or black keys.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
		//System.out.println(e);
		
		int indexClicked;
		double freq;
		if(currentCPPN == null) return;
		
		boolean played = false; //used for top half of keyboard (determines whether to treat areas in top half of keyboard as black or white keys)
		
		if(e.getPoint().getY() < BLACK_KEY_START_HEIGHT + WINDOW_EDGE_HEIGHT && e.getPoint().getX() >= (BLACK_KEY_START_WIDTH + WINDOW_EDGE_WIDTH)) { //top half of keyboard
			indexClicked = (int) (e.getPoint().getX()-WINDOW_EDGE_WIDTH-BLACK_KEY_START_WIDTH) / BLACK_KEY_WIDTH;
			freq = BLACK_KEYS[indexClicked];
			if(freq != -1) { //if there is a black key in the region
				double[] amplitude = SoundAmplitudeArrayManipulator.amplitudeGenerator(currentCPPN, NOTE_LENGTH_DEFAULT, freq, InteractiveEvolutionTask.getInputMultipliers());
				MiscSoundUtil.playDoubleArray(amplitude);
				played = true; //black key, so should not be set to white key index
			}
		}

		if(!played) {
			// white key
			indexClicked = (int) (e.getPoint().getX()-WINDOW_EDGE_WIDTH) / WHITE_KEY_WIDTH;
			freq = WHITE_KEYS[indexClicked];
			double[] amplitude = SoundAmplitudeArrayManipulator.amplitudeGenerator(currentCPPN, NOTE_LENGTH_DEFAULT, freq, InteractiveEvolutionTask.getInputMultipliers());
			MiscSoundUtil.playDoubleArray(amplitude);
		}
		
		System.out.println(e.getPoint());
		//System.out.println(indexClicked);

	}

	// Not used
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public static void main(String[] args) {
		Keyboard gui = new Keyboard();	  
		System.out.println(PRENOTE.length);
		System.out.println(PREEXTENDEDNOTE.length);
	}


}
